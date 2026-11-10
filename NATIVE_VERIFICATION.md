# BECETTI TOOLS — Native & Amazon Release Verification

This project is **100% Native Android** — Kotlin + Jetpack Compose + Material 3.

## No WebView
- No `WebView` import in `app/src` (verified by CI: `! grep -R WebView app/src`)
- No `android.webkit` usage
- All 15 tools are native: Room, DataStore, CameraX, ML Kit, ZXing, PDFBox-Android, Retrofit

## Release Build & Amazon Appstore Verification
- Signed **Release** APK + AAB (`BECETTI-TOOLS-v1.1.0-release.apk`, `BECETTI-TOOLS-v1.1.0-release.aab`)
- Package: `com.becetti.tools` (`versionCode = 2`, `versionName = "1.1.0"`, `debuggable = false`)
- Signed with the SAME release certificate as the published v1.0.0 binary (upgrade-safe; cert SHA-256 continuity is asserted in CI — see `RELEASE-VERIFICATION-v1.1.0.txt`)
- Signatures: APK Signature Scheme v1 + v2 + v3 (`apksigner verify -v`)
- Size: **< 50 MB** (compliant with Amazon Appstore 50 MB binary guideline)
- ABIs: `armeabi-v7a` and `arm64-v8a` (supports all 32-bit and 64-bit Amazon Fire OS and Android devices)

## Build Locally
```bash
./gradlew assembleRelease
```
