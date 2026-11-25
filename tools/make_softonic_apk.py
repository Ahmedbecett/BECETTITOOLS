#!/usr/bin/env python3
"""Produce the Softonic submission APK from the signed CI release APK.

Some third-party signature checkers are confused by the *optional* ID-value pairs that AGP
writes into the APK Signing Block next to the real v2/v3 signature blocks
(`0x504b4453` dependency-info and `0x42726577` verity padding).

This tool removes only those two optional pairs, keeps the v2 (`0x7109871a`) and v3
(`0xf05368c0`) signature blocks byte-identical, and adjusts the signing-block size fields and
the ZIP end-of-central-directory offset accordingly. The APK signatures stay valid, because
they cover the APK content (sections + central directory + EOCD), not the signing block itself.

The result is verified with tools/verify_apk_signature.py before the file is written out.

Usage:
    python3 tools/make_softonic_apk.py INPUT.apk OUTPUT.apk
"""
from __future__ import annotations

import hashlib
import os
import struct
import subprocess
import sys
import struct as _struct
from pathlib import Path

MAGIC = b"APK Sig Block 42"
DROP_IDS = {
    0x504B4453: "dependency-info",
    0x42726577: "verity padding",
}
KEEP_IDS = {0x7109871A: "v2", 0xF05368C0: "v3", 0x1B93AD61: "v3.1"}


def u32(buf: bytes, off: int) -> int:
    return _struct.unpack_from("<I", buf, off)[0]


def u64(buf: bytes, off: int) -> int:
    return _struct.unpack_from("<Q", buf, off)[0]


def parse_pairs(data: bytes):
    eocd = data.rfind(b"PK\x05\x06")
    if eocd < 0:
        raise SystemExit("not a ZIP file")
    cd_off = u32(data, eocd + 16)
    if data[cd_off - 16:cd_off] != MAGIC:
        raise SystemExit("no APK Signing Block; is the APK actually signed?")
    footer_size = u64(data, cd_off - 24)
    block_start = cd_off - footer_size - 8
    if u64(data, block_start) != footer_size:
        raise SystemExit("APK Signing Block size fields disagree; refusing to rewrite")
    pairs, cursor = [], block_start + 8
    while cursor < cd_off - 24:
        length = u64(data, cursor)
        pair_id = u32(data, cursor + 8)
        pairs.append((pair_id, data[cursor + 8:cursor + 8 + length]))
        cursor += 8 + length
    return eocd, cd_off, block_start, pairs


def build_block(kept) -> bytes:
    # `value` is the raw ID-value pair content, i.e. it already starts with the 4-byte ID,
    # so the length prefix is simply its size.
    body = b"".join(struct.pack("<Q", len(value)) + value for _, value in kept)
    # Block layout: [u64 size][pairs][u64 size][magic] where size covers pairs + 8 + 16.
    size = len(body) + 8 + 16
    return struct.pack("<Q", size) + body + struct.pack("<Q", size) + MAGIC


def main(src: Path, dst: Path) -> int:
    data = src.read_bytes()
    eocd, cd_off, block_start, pairs = parse_pairs(data)
    print(f"input: {src} ({len(data):,} bytes)")
    print("APK Signing Block pairs:")
    for pair_id, value in pairs:
        label = KEEP_IDS.get(pair_id) or DROP_IDS.get(pair_id) or "unknown"
        print(f"  0x{pair_id:08x} {label:<16} {len(value):>7,} bytes")

    kept = [(i, v) for i, v in pairs if i not in DROP_IDS]
    dropped = [(i, v) for i, v in pairs if i in DROP_IDS]
    if not dropped:
        print("nothing to drop: the APK Signing Block already contains only signature blocks")
        output = data
    else:
        for pair_id, value in dropped:
            print(f"dropping 0x{pair_id:08x} ({DROP_IDS[pair_id]}, {len(value):,} bytes)")
        block = build_block(kept)
        new_cd_off = block_start + len(block)
        delta = new_cd_off - cd_off
        output = bytearray(data[:block_start] + block + data[cd_off:])
        new_eocd = eocd + delta
        struct.pack_into("<I", output, new_eocd + 16, new_cd_off)
        output = bytes(output)
        print(f"signing block rebuilt, central directory moved by {delta:+,} bytes")

    dst.write_bytes(output)
    print(f"output: {dst} ({len(output):,} bytes)")
    print(f"SHA-256: {hashlib.sha256(output).hexdigest()}")

    # Independent verification of the produced file.
    verifier = Path(__file__).resolve().parent / "verify_apk_signature.py"
    # Run the verifier with the same interpreter that has `cryptography` available.
    interpreter = os.getenv("VERIFY_PYTHON", sys.executable)
    result = subprocess.run([interpreter, str(verifier), str(dst)])
    if result.returncode != 0:
        dst.unlink(missing_ok=True)
        raise SystemExit("verification of the produced APK failed; output removed")
    print("verification passed for the produced APK")
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit(__doc__)
    sys.exit(main(Path(sys.argv[1]), Path(sys.argv[2])))
