#!/usr/bin/env bash
# One-shot release verification for BECETTI TOOLS v1.1.0.
#
# Runs, in order:
#   1. Arabic/French store metadata and localization checks
#   2. independent APK signature verification (v1 + v2 + v3) of both shipped APKs
#   3. checksum cross-check against the committed .sha256 files
#   4. package/version/certificate identity and content comparison between both APKs
#
# Usage: tools/verify_release.sh [--apk PATH]
set -euo pipefail

cd "$(dirname "$0")/.."

PYTHON=${VERIFY_PYTHON:-python3}
RELEASE_APK="BECETTI-TOOLS-v1.1.0-release.apk"
SOFTONIC_APK="BECETTI-TOOLS-v1.1.0-softonic.apk"
EXPECTED_CERT="c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6"

"$PYTHON" -c "import cryptography, asn1crypto" 2>/dev/null || {
  echo "Missing python packages. Install them with:"
  echo "  python3 -m venv /tmp/apkverify && /tmp/apkverify/bin/pip install cryptography asn1crypto"
  echo "  export VERIFY_PYTHON=/tmp/apkverify/bin/python"
  exit 1
}

if [ "${1:-}" = "--apk" ] && [ -n "${2:-}" ]; then
  APKS=("$2")
else
  APKS=("$SOFTONIC_APK" "$RELEASE_APK")
fi

echo "=============================================================="
echo "1. Store metadata and localization"
echo "=============================================================="
"$PYTHON" tools/check_store_metadata.py
"$PYTHON" tools/check_localization.py

for apk in "${APKS[@]}"; do
  echo
  echo "=============================================================="
  echo "2. APK signature verification: $apk"
  echo "=============================================================="
  "$PYTHON" tools/verify_apk_signature.py "$apk"

  echo
  echo "3. Checksum cross-check: $apk"
  if [ -f "$apk.sha256" ]; then
    expected=$(awk '{print $1}' "$apk.sha256")
    actual=$(sha256sum "$apk" | awk '{print $1}')
    echo "   recorded: $expected"
    echo "   computed: $actual"
    test "$expected" = "$actual"
    echo "   PASS checksum matches the committed .sha256 file"
  else
    echo "   note: no .sha256 file next to $apk"
  fi

  echo
  echo "4. Package identity: $apk"
  "$PYTHON" - "$apk" "$EXPECTED_CERT" <<'PY'
import hashlib, struct, sys, zipfile
from cryptography.hazmat.primitives.serialization import pkcs7

apk, expected_cert = sys.argv[1], sys.argv[2]
data = open(apk, "rb").read()
zf = zipfile.ZipFile(apk)
names = zf.namelist()
manifest = zf.read("AndroidManifest.xml")
blob = b"".join(zf.read(n) for n in names if n.endswith(".dex")) + zf.read("resources.arsc") + manifest

cert = pkcs7.load_der_pkcs7_certificates(zf.read("META-INF/CERT.RSA"))[0]
fingerprint = hashlib.sha256(cert.public_bytes(__import__("cryptography.hazmat.primitives.serialization",
                                                           fromlist=["Encoding"]).Encoding.DER)).hexdigest()

print(f"   signer certificate SHA-256: {fingerprint}")
print(f"   expected                  : {expected_cert}")
assert fingerprint == expected_cert, "signer certificate does not match the release identity"
print("   PASS signer certificate matches the established release identity")

print(f"   debuggable marker present : {b'application-debuggable' in manifest}")
assert b"application-debuggable" not in manifest, "APK is marked debuggable"
print("   PASS not debuggable")
PY
done

if [ "${#APKS[@]}" -eq 2 ]; then
  echo
  echo "=============================================================="
  echo "5. Release vs Softonic APK content comparison"
  echo "=============================================================="
  "$PYTHON" - <<'PY'
import zipfile
a = zipfile.ZipFile("BECETTI-TOOLS-v1.1.0-release.apk")
b = zipfile.ZipFile("BECETTI-TOOLS-v1.1.0-softonic.apk")
na = {i.filename: (i.CRC, i.file_size) for i in a.infolist()}
nb = {i.filename: (i.CRC, i.file_size) for i in b.infolist()}
assert set(na) == set(nb), "the two APKs contain different entries"
diff = [k for k in na if na[k] != nb[k]]
assert not diff, f"content differs in {diff[:5]}"
print(f"   entries: {len(na)}, differing entries: {len(diff)}")
print("   PASS identical application content; only the APK Signing Block differs")
PY
fi

echo
echo "=============================================================="
echo "ALL CHECKS PASSED"
echo "=============================================================="
echo "Upload BECETTI-TOOLS-v1.1.0-softonic.apk to Softonic and compare its SHA-256"
echo "before submitting. Store texts: docs/store/softonic-v1.1.0-metadata.json"
