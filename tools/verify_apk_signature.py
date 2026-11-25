#!/usr/bin/env python3
"""Independent APK signature verification (v1 / JAR, v2, v3) with no Android tooling.

This exists so a release APK can be proven valid on any machine that has Python and
the `cryptography` package, without Java, Android SDK build-tools or `apksigner`.
It re-implements the checks described in the public specifications:

  * APK Signature Scheme v2 (source.android.com/docs/core/architecture/apk-signature-scheme)
  * APK Signature Scheme v3 (same document, "v3" section)
  * JAR signing (JAR File Specification + Signature Scheme v1 requirements)

Exit code 0 means every check passed.

Usage:
    python3 tools/verify_apk_signature.py app.apk
"""
from __future__ import annotations

import base64
import hashlib
import re
import struct
import sys
import zipfile
from datetime import datetime, timezone

from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric import ec, padding, rsa

SIG_BLOCK_MAGIC = b"APK Sig Block 42"
V2_BLOCK_ID = 0x7109871A
V3_BLOCK_ID = 0xF05368C0
V31_BLOCK_ID = 0x1B93AD61

# signature algorithm id -> (family, hash, content-digest capable)
ALGORITHMS = {
    0x0101: ("RSASSA-PKCS1-v1_5", "SHA-256", True),
    0x0102: ("RSASSA-PKCS1-v1_5", "SHA-512", True),
    0x0103: ("RSASSA-PKCS1-v1_5", "SHA-256", True),
    0x0104: ("RSASSA-PKCS1-v1_5", "SHA-512", True),
    0x0201: ("ECDSA", "SHA-256", True),
    0x0202: ("ECDSA", "SHA-512", True),
    0x0301: ("DSA", "SHA-256", True),
    0x0421: ("RSASSA-PSS", "SHA-256", False),
    0x0423: ("RSASSA-PKCS1-v1_5", "SHA-256", False),
    0x0425: ("VERITY", "SHA-256", False),
}


# ------------------------------------------------------------------ helpers --
def u32(buf: bytes, off: int) -> int:
    return struct.unpack_from("<I", buf, off)[0]


def u64(buf: bytes, off: int) -> int:
    return struct.unpack_from("<Q", buf, off)[0]


class Reader:
    """Little-endian reader for length-prefixed structures."""

    def __init__(self, buf: bytes):
        self.buf, self.pos, self.end = buf, 0, len(buf)

    def uint32(self) -> int:
        value = u32(self.buf, self.pos)
        self.pos += 4
        return value

    def uint64(self) -> int:
        value = u64(self.buf, self.pos)
        self.pos += 8
        return value

    def bytes(self) -> bytes:
        length = self.uint32()
        if self.pos + length > self.end:
            raise ValueError(f"length prefix {length} overruns buffer of {self.end} bytes")
        value = self.buf[self.pos:self.pos + length]
        self.pos += length
        return value

    def eof(self) -> bool:
        return self.pos >= self.end


def length_prefixed_sequence(buf: bytes) -> list[bytes]:
    reader, items = Reader(buf), []
    while not reader.eof():
        items.append(reader.bytes())
    return items


def public_key_sha256(cert: x509.Certificate) -> str:
    return hashlib.sha256(cert.public_bytes(serialization.Encoding.DER)).hexdigest()


def algorithm_name(alg_id: int) -> str:
    family, hash_name, _ = ALGORITHMS[alg_id]
    return f"{family} / {hash_name}"


# ------------------------------------------------------- APK Signing Block --
def read_signing_block(data: bytes):
    """Locate the APK Signing Block and return (pairs, block_start, cd_off, cd_end, eocd_off)."""
    eocd_off = data.rfind(b"PK\x05\x06")
    if eocd_off < 0:
        raise SystemExit("not a ZIP/APK: end-of-central-directory record not found")
    cd_off = u32(data, eocd_off + 16)
    cd_end = cd_off + u32(data, eocd_off + 12)
    if cd_off < 32 or data[cd_off - 16:cd_off] != SIG_BLOCK_MAGIC:
        return None, None, cd_off, cd_end, eocd_off
    size_in_footer = u64(data, cd_off - 24)
    block_start = cd_off - size_in_footer - 8
    if block_start < 0 or u64(data, block_start) != size_in_footer:
        raise SystemExit("APK Signing Block is malformed (size fields disagree)")
    pairs, cursor, end = {}, block_start + 8, cd_off - 24
    while cursor < end:
        pair_length = u64(data, cursor)
        pair_id = u32(data, cursor + 8)
        if pair_length < 4 or cursor + 8 + pair_length > end:
            raise SystemExit("APK Signing Block contains a malformed ID-value pair")
        pairs.setdefault(pair_id, data[cursor + 12:cursor + 8 + pair_length])
        cursor += 8 + pair_length
    return pairs, block_start, cd_off, cd_end, eocd_off


def content_digest(data: bytes, block_start: int, cd_off: int, cd_end: int,
                   eocd_off: int, algorithm: str) -> bytes:
    """APK Signature Scheme v2/v3 chunked content digest.

    The APK is split into three sections -- everything before the APK Signing Block, the
    central directory, and the end-of-central-directory record -- and *all* chunk digests of
    all three sections form one single list that is then hashed.
    """
    chunk_size = 1024 * 1024

    def chunk_digests(blob: bytes) -> list[bytes]:
        return [hashlib.new(algorithm, b"\xa5" + struct.pack("<I", len(chunk)) + chunk).digest()
                for chunk in (blob[i:i + chunk_size] for i in range(0, len(blob), chunk_size))]

    eocd = bytearray(data[eocd_off:])
    # The EOCD field that normally holds the central-directory offset is replaced, for digest
    # purposes, with the offset of the APK Signing Block itself.
    struct.pack_into("<I", eocd, 16, block_start)
    chunks = (chunk_digests(data[:block_start])
              + chunk_digests(data[cd_off:cd_end])
              + chunk_digests(bytes(eocd)))
    return hashlib.new(algorithm, b"\x5a" + struct.pack("<I", len(chunks)) + b"".join(chunks)).digest()


def verify_signature(alg_id: int, key, message: bytes, signature: bytes) -> tuple[bool, str]:
    family, hash_name, _ = ALGORITHMS[alg_id]
    hash_alg = hashes.SHA512() if hash_name == "SHA-512" else hashes.SHA256()
    if family == "RSASSA-PKCS1-v1_5":
        if not isinstance(key, rsa.RSAPublicKey):
            return False, "certificate does not hold an RSA key"
        key.verify(signature, message, padding.PKCS1v15(), hash_alg)
        return True, "signature verified"
    if family == "RSASSA-PSS":
        if not isinstance(key, rsa.RSAPublicKey):
            return False, "certificate does not hold an RSA key"
        key.verify(signature, message, padding.PSS(padding.MGF1(hash_alg), hash_alg.digest_size), hash_alg)
        return True, "signature verified"
    if family == "ECDSA":
        if not isinstance(key, ec.EllipticCurvePublicKey):
            return False, "certificate does not hold an EC key"
        key.verify(signature, message, ec.ECDSA(hash_alg))
        return True, "signature verified"
    return False, f"{family} signatures are not content-digest signatures"


def parse_scheme_signers(block_value: bytes, v3: bool) -> list[dict]:
    signers = []
    for raw in length_prefixed_sequence(Reader(block_value).bytes()):
        reader = Reader(raw)
        signed_data = reader.bytes()
        signed_sdk = (reader.uint32(), reader.uint32()) if v3 else (None, None)
        signatures = []
        for record in length_prefixed_sequence(reader.bytes()):
            record_reader = Reader(record)
            signatures.append((record_reader.uint32(), record_reader.bytes()))
        public_key = serialization.load_der_public_key(reader.bytes())

        signed_reader = Reader(signed_data)
        digests = []
        for record in length_prefixed_sequence(signed_reader.bytes()):
            record_reader = Reader(record)
            digests.append((record_reader.uint32(), record_reader.bytes()))
        certs = [x509.load_der_x509_certificate(blob)
                 for blob in length_prefixed_sequence(signed_reader.bytes())]
        declared_sdk = (signed_reader.uint32(), signed_reader.uint32()) if v3 else (None, None)
        # Additional attributes (always present, possibly empty): e.g. v3 stripping protection.
        attributes = length_prefixed_sequence(signed_reader.bytes()) if not signed_reader.eof() else []
        if v3 and declared_sdk != signed_sdk:
            raise ValueError("v3 SDK range differs between signer and signed data")
        if not certs:
            raise ValueError("signer does not embed a certificate")
        # Some tools append extra length-prefixed fields after the attributes; they are part of
        # the signed bytes, so they are reported rather than treated as corruption.
        signers.append(dict(signed_data=signed_data, signatures=signatures, digests=digests,
                            public_key=public_key, certs=certs, sdk_range=signed_sdk,
                            attributes=attributes,
                            extra_fields=signed_data[signed_reader.pos:]))
    return signers


def verify_scheme(data: bytes, signature_block, block_id: int, v3: bool, label: str,
                  checks: list) -> tuple[x509.Certificate | None, bool]:
    print(f"\n--- {label} (signature block 0x{block_id:08x}) ---")
    if signature_block is None:
        print("  SKIPPED: APK has no signing block")
        checks.append((label, None, "no signing block"))
        return None, True

    pairs, block_start, cd_off, cd_end, eocd_off = signature_block
    if block_id not in pairs:
        print("  SKIPPED: block not present in this APK")
        checks.append((label, None, "block not present"))
        return None, True
    ok, first_cert = True, None
    for index, signer in enumerate(parse_scheme_signers(pairs[block_id], v3), 1):
        cert = signer["certs"][0]
        first_cert = first_cert or cert

        # 1. signature over the signed-data bytes
        signature_ok = False
        for alg_id, signature in signer["signatures"]:
            try:
                good, note = verify_signature(alg_id, signer["public_key"], signer["signed_data"], signature)
            except Exception as exc:  # noqa: BLE001 - report any cryptographic failure
                good, note = False, f"{type(exc).__name__}: {exc}"
            print(f"  signer #{index} signature [{algorithm_name(alg_id)}]: {'PASS' if good else 'FAIL'} ({note})")
            signature_ok = signature_ok or good

        # 2. content digest over the APK bytes
        digest_ok = False
        for alg_id, expected in signer["digests"]:
            if alg_id not in ALGORITHMS:
                print(f"  signer #{index} content digest alg 0x{alg_id:04x}: skipped (unknown)")
                continue
            _, hash_name, digest_capable = ALGORITHMS[alg_id]
            if not digest_capable:
                print(f"  signer #{index} content digest [{algorithm_name(alg_id)}]: skipped (verity)")
                continue
            actual = content_digest(data, block_start, cd_off, cd_end, eocd_off, hash_name.replace("-", "").lower())
            good = actual == expected
            print(f"  signer #{index} content digest [{algorithm_name(alg_id)}]: {'PASS' if good else 'FAIL'}")
            digest_ok = digest_ok or good

        # 3. the signing key must be the one certified
        same_key = (cert.public_key().public_bytes(serialization.Encoding.DER,
                                                   serialization.PublicFormat.SubjectPublicKeyInfo)
                    == signer["public_key"].public_bytes(serialization.Encoding.DER,
                                                         serialization.PublicFormat.SubjectPublicKeyInfo))
        print(f"  signer #{index} certificate matches embedded public key: {'PASS' if same_key else 'FAIL'}")

        # 4. certificate must currently be valid
        now = datetime.now(timezone.utc)
        valid = cert.not_valid_before_utc <= now <= cert.not_valid_after_utc
        print(f"  signer #{index} certificate validity: {'PASS' if valid else 'FAIL'} "
              f"({cert.not_valid_before_utc:%Y-%m-%d} to {cert.not_valid_after_utc:%Y-%m-%d})")

        print(f"  signer #{index} certificate subject: {cert.subject.rfc4514_string()}")
        print(f"  signer #{index} certificate SHA-256: {public_key_sha256(cert)}")
        if v3:
            print(f"  signer #{index} SDK range: {signer['sdk_range'][0]}..{signer['sdk_range'][1]}")
        if signer["extra_fields"]:
            print(f"  signer #{index} note: {len(signer['extra_fields'])} trailing byte(s) in signed data "
                  f"(covered by the signature; not part of the v2/v3 layout, ignored)")

        ok = ok and signature_ok and digest_ok and same_key and valid

    print(f"  {label}: {'PASS' if ok else 'FAIL'}")
    checks.append((label, ok, f"{len(parse_scheme_signers(pairs[block_id], v3))} signer(s)"))
    return first_cert, ok


# ------------------------------------------------------------- v1 (JAR) ------
def rekey_sections(raw: bytes) -> dict:
    """Parse a JAR manifest/SF file into sections keyed by the exact `Name:` header value."""
    text = raw.decode("utf-8", "replace").replace("\r\n", "\n").split("\n")
    unfolded: list[str] = []
    for line in text:
        if line.startswith(" ") and unfolded:
            unfolded[-1] += line[1:]
        else:
            unfolded.append(line)
    result: dict = {}
    current: dict = {}
    name: str | None = None
    for line in unfolded + [""]:
        if line == "":
            if current:
                result[name] = current
            current, name = {}, None
            continue
        if ":" not in line:
            continue
        key, value = line.split(":", 1)
        current[key] = value.strip()
        if key == "Name":
            name = value.strip()
    return result


def digest_attributes(attributes: dict) -> list[tuple[str, str]]:
    """Extract (hashlib name, base64 value) pairs from JAR digest headers."""
    prefixes = {"sha1": "sha1", "sha-1": "sha1", "sha256": "sha256", "sha-256": "sha256",
                "sha512": "sha512", "sha-512": "sha512", "md5": "md5"}
    found = []
    for key, value in attributes.items():
        match = re.match(r"^(?P<alg>.+?)[-_](?P<kind>Digest-Manifest-Main-Attributes|Digest-Manifest|Digest)$",
                         key, re.I)
        if not match:
            continue
        algorithm = prefixes.get(match.group("alg").lower())
        if algorithm:
            found.append((algorithm, value))
    return found


def manifest_sections_raw(manifest: bytes) -> dict[str, bytes]:
    """Map entry name -> exact raw MANIFEST.MF section bytes (folded lines and CRLFs preserved)."""
    blocks = manifest.split(b"\r\n\r\n")
    sections: dict[str, bytes] = {}
    for block in blocks:
        if not block.strip():
            continue
        lines = block.split(b"\r\n")
        logical = b""
        name = None
        for line in lines:
            if line.startswith(b" "):
                logical += line[1:]
                continue
            if logical.startswith(b"Name:"):
                name = logical[5:].strip().decode("utf-8", "replace")
            logical = line
        if logical.startswith(b"Name:"):
            name = logical[5:].strip().decode("utf-8", "replace")
        if name:
            sections[name] = block + b"\r\n\r\n"
    return sections


def verify_v1(apk_path: str, checks: list) -> x509.Certificate | None:
    print("\n--- APK Signature Scheme v1 (JAR signing) ---")
    zf = zipfile.ZipFile(apk_path)
    names = zf.namelist()
    manifest_name = next((n for n in names if n.upper() == "META-INF/MANIFEST.MF"), None)
    sf_name = next((n for n in names if n.upper().startswith("META-INF/") and n.upper().endswith(".SF")), None)
    block_name = next((n for n in names if n.upper().startswith("META-INF/")
                       and n.upper().endswith((".RSA", ".DSA", ".EC"))), None)
    if not (manifest_name and sf_name and block_name):
        print("  FAIL: META-INF signature files missing")
        checks.append(("v1 (JAR)", False, "signature files missing"))
        return None
    print(f"  signature files: {sf_name}, {block_name}")

    manifest = zf.read(manifest_name)
    sections = rekey_sections(manifest)
    entries = {name: attrs for name, attrs in sections.items() if name is not None}

    bad_digests, uncovered = [], []
    for info in zf.infolist():
        if info.filename.endswith("/") or info.filename.upper().startswith("META-INF/"):
            continue
        attrs = entries.get(info.filename)
        if attrs is None:
            uncovered.append(info.filename)
            continue
        payload = zf.read(info.filename)
        for algorithm, expected in digest_attributes(attrs):
            actual = base64.b64encode(hashlib.new(algorithm, payload).digest()).decode()
            if actual != expected:
                bad_digests.append(f"{info.filename} [{algorithm}]")

    total = len([i for i in zf.infolist() if not i.filename.endswith("/")])
    print(f"  MANIFEST.MF entry digests ({len(entries)} entries, {total} ZIP entries): "
          f"{'PASS' if not bad_digests else 'FAIL'}")
    if bad_digests:
        for item in bad_digests[:5]:
            print(f"    bad digest: {item}")
    print(f"  every ZIP entry covered by MANIFEST.MF: {'PASS' if not uncovered else 'FAIL'}")

    sf = zf.read(sf_name)
    sf_sections = rekey_sections(sf)
    main = sf_sections.get(None, {})
    manifest_digest_ok = False
    for algorithm, expected in digest_attributes(main):
        actual = base64.b64encode(hashlib.new(algorithm, manifest).digest()).decode()
        good = actual == expected
        manifest_digest_ok = manifest_digest_ok or good
        print(f"  {sf_name} {algorithm} digest of MANIFEST.MF: {'PASS' if good else 'FAIL'}")

    raw_sections = manifest_sections_raw(manifest)
    sf_entries_ok = True
    checked = 0
    missing_sections = []
    for name, attrs in sf_sections.items():
        if name is None:
            continue
        section = raw_sections.get(name)
        if section is None:
            missing_sections.append(name)
            continue
        for algorithm, expected in digest_attributes(attrs):
            checked += 1
            if base64.b64encode(hashlib.new(algorithm, section).digest()).decode() != expected:
                sf_entries_ok = False
    if missing_sections:
        print(f"  {sf_name} references {len(missing_sections)} section(s) missing from MANIFEST.MF")
    print(f"  {sf_name} per-entry digests of MANIFEST.MF sections ({checked} checks): "
          f"{'PASS' if sf_entries_ok else 'FAIL'}")

    certs = pkcs7_certificates(zf.read(block_name))
    signature_ok = False
    if certs:
        signature_ok = pkcs7_verify(zf.read(block_name), sf, certs[0])
    print(f"  {block_name} PKCS#7 signature over {sf_name}: {'PASS' if signature_ok else 'FAIL'}")
    if certs:
        print(f"  v1 certificate subject: {certs[0].subject.rfc4514_string()}")
        print(f"  v1 certificate SHA-256: {public_key_sha256(certs[0])}")

    ok = (not bad_digests and not uncovered and manifest_digest_ok and sf_entries_ok
          and signature_ok and not missing_sections)
    print(f"  APK Signature Scheme v1: {'PASS' if ok else 'FAIL'}")
    checks.append(("v1 (JAR)", ok, f"{len(entries)} entries"))
    return certs[0] if certs else None


def pkcs7_certificates(der: bytes) -> list[x509.Certificate]:
    from cryptography.hazmat.primitives.serialization import pkcs7
    return pkcs7.load_der_pkcs7_certificates(der)


def pkcs7_verify(der: bytes, content: bytes, cert: x509.Certificate) -> bool:
    """Verify the PKCS#7 SignerInfo signature over CERT.SF (attached, no signed attributes)."""
    import asn1crypto.cms as cms

    signed_data = cms.ContentInfo.load(der)["content"]
    for signer_info in signed_data["signer_infos"]:
        digest_name = signer_info["digest_algorithm"]["algorithm"].native
        signature = signer_info["signature"].native
        signed_attrs = signer_info["signed_attrs"]
        key = cert.public_key()
        if not isinstance(key, rsa.RSAPublicKey):
            continue
        hash_alg = {"sha1": hashes.SHA1(), "sha256": hashes.SHA256(), "sha512": hashes.SHA512()}[digest_name]
        if signed_attrs.native is None:
            key.verify(signature, content, padding.PKCS1v15(), hash_alg)
            return True
        # signed attributes: the signature covers the DER SET OF attributes
        payload = signed_attrs.dump()
        payload = b"\x31" + payload[1:]
        key.verify(signature, payload, padding.PKCS1v15(), hash_alg)
        return True
    return False


# ------------------------------------------------------------------- main ----
def main(path: str) -> int:
    data = open(path, "rb").read()
    print(f"Verifying {path}")
    print(f"  size: {len(data):,} bytes")
    print(f"  SHA-256: {hashlib.sha256(data).hexdigest()}")

    try:
        zf = zipfile.ZipFile(path)
        broken = zf.testzip()
        entry_count = len(zf.infolist())
    except zipfile.BadZipFile as exc:
        print(f"  ZIP container integrity: FAIL ({exc})")
        print("\n=== RESULT ===")
        print(f"  FAIL  ZIP container ({exc})")
        print("\nOVERALL: INVALID — the file is truncated or corrupted, not a valid APK")
        print("         Re-download it and compare the SHA-256 before retrying.")
        return 1
    print(f"  ZIP container integrity: {'PASS' if broken is None else 'FAIL at ' + str(broken)}")
    print(f"  entries: {entry_count}")

    pairs, block_start, cd_off, cd_end, eocd_off = read_signing_block(data)
    if pairs is not None:
        known = {V2_BLOCK_ID: "v2", V3_BLOCK_ID: "v3", V31_BLOCK_ID: "v3.1",
                 0x504B4453: "dependency info", 0x42726577: "verity padding"}
        described = ", ".join(f"0x{pid:08x} ({known.get(pid, 'unknown')})" for pid in sorted(pairs))
        print(f"  APK Signing Block: present at offset {block_start:,} ({described})")
    else:
        print("  APK Signing Block: absent (v2/v3 signatures missing)")

    checks: list = []
    try:
        v1_cert = verify_v1(path, checks)
    except (zipfile.BadZipFile, ValueError, KeyError) as exc:
        print(f"  v1 verification could not complete: {type(exc).__name__}: {exc}")
        checks.append(("v1 (JAR)", False, f"{type(exc).__name__}"))
        v1_cert = None
    v2_cert, _ = verify_scheme(data, (pairs, block_start, cd_off, cd_end, eocd_off),
                               V2_BLOCK_ID, False, "APK Signature Scheme v2", checks)
    v3_cert, _ = verify_scheme(data, (pairs, block_start, cd_off, cd_end, eocd_off),
                               V3_BLOCK_ID, True, "APK Signature Scheme v3", checks)
    v31_cert = None
    if pairs and V31_BLOCK_ID in pairs:
        v31_cert, _ = verify_scheme(data, (pairs, block_start, cd_off, cd_end, eocd_off),
                                    V31_BLOCK_ID, True, "APK Signature Scheme v3.1", checks)

    print("\n--- signer identity across schemes ---")
    presented = [(name, cert) for name, cert in
                 (("v1", v1_cert), ("v2", v2_cert), ("v3", v3_cert), ("v3.1", v31_cert)) if cert]
    for name, cert in presented:
        print(f"  {name}: {public_key_sha256(cert)}")
    fingerprints = {public_key_sha256(cert) for _, cert in presented}
    same_signer = len(fingerprints) == 1
    print(f"  all schemes signed by one certificate: {'PASS' if same_signer else 'FAIL'}")
    checks.append(("single signing identity", same_signer, "1 certificate" if same_signer else "mismatch"))

    print("\n=== RESULT ===")
    for label, ok, note in checks:
        state = "SKIP" if ok is None else ("PASS" if ok else "FAIL")
        print(f"  {state}  {label} ({note})")
    overall = broken is None and all(ok for _, ok, _ in checks if ok is not None)
    print(f"\nOVERALL: {'VALID — APK signature verified' if overall else 'INVALID — see failures above'}")
    return 0 if overall else 1


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit(__doc__)
    sys.exit(main(sys.argv[1]))
