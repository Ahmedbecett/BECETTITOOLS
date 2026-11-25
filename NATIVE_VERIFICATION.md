# BECETTI TOOLS — Native & release verification

The app is a native Android application: Kotlin + Jetpack Compose + Material 3.
No `WebView`, no `android.webkit` usage (CI fails the build if either appears under `app/src`).

## Published baseline
- Amazon Appstore release: v1.0.0 (`versionCode = 1`), binary `BECETTI-TOOLS-v1.0.0-release.apk`.
- Package `com.becetti.tools`, `minSdk 24`, `targetSdk 34`, ABIs `armeabi-v7a` + `arm64-v8a`.

## v1.1.0 (Softonic resubmission)
- Version: `versionName 1.1.0`, `versionCode 2`.
- Binaries:
  - `BECETTI-TOOLS-v1.1.0-softonic.apk` — SHA-256 `96a8f30452378e23e386d1d49dcc2ded45cbe5dd8a8ca37e04a824698b60f349`
  - `BECETTI-TOOLS-v1.1.0-release.apk` — SHA-256 `6d8f3d4c23bbd001f43a89b48df4b0770f1114abb3f0dd486829871c95e2e224`
- Both are signed with the established release certificate
  (`c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6`, identical to v1.0.0),
  APK Signature Scheme v1 + v2 + v3, `debuggable = false`, ~18.8 MiB (well under the 50 MB limit).
- Languages: Arabic (`ar`), French (`fr`), English (`en` fallback).

### What is verified here
- Signatures: `apksigner verify --verbose --print-certs` in CI **and** the independent
  from-scratch verifier `tools/verify_apk_signature.py` (JAR/v1 per-entry digests, PKCS#7
  signature, v2/v3 chunked content digests and RSA signatures, single signing identity).
- Keystore identity: the committed `keystore/becetti-release.jks` holds the certificate that
  signed both v1.0.0 and v1.1.0, so in-place updates keep working.
- Package name, version code/name, non-debuggable flag, sizes, WebView absence.
- Arabic/French resources and store texts: `tools/check_localization.py`,
  `tools/check_store_metadata.py`.
- Tamper detection: truncated, bit-flipped and appended-byte copies of the APK are rejected by
  the verifier.

### What is not verified here
- Physical-device testing (Android 7.0 through 14, Fire OS 7/8) and two-device Wi-Fi transfer
  were not executed in this workspace. Google Mobile Ads availability on devices without Google
  Play services must be checked on a Fire OS device; an ad failure must not block core tools.
- Received files in Wi-Fi transfer are stored as files; the app does not install them.

## Build and verify locally
```bash
./gradlew verifyReleaseApk    # assembleRelease + apksigner verification

pip install cryptography asn1crypto
python3 tools/verify_apk_signature.py BECETTI-TOOLS-v1.1.0-softonic.apk
python3 tools/check_store_metadata.py
python3 tools/check_localization.py
```
The release build fails if `keystore/becetti-release.jks` is missing; it never falls back to the
debug keystore.
