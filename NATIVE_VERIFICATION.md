# BECETTI TOOLS — Native & Amazon Release Verification

This project is **100% Native Android** — Kotlin + Jetpack Compose + Material 3.

## No WebView
- No `WebView` import in `app/src` (verified by CI: `! grep -R WebView app/src`)
- No `android.webkit` usage
- All 15 tools are native: Room, DataStore, CameraX, ML Kit, ZXing, PDFBox-Android, Retrofit

## Release Build & Amazon Appstore Verification
- Signed **Release** APK (`BECETTI-TOOLS-v1.0.0-release.apk`)
- Package: `com.becetti.tools` (`versionCode = 1`, `versionName = "1.0.0"`, `debuggable = false`)
- Signatures: APK Signature Scheme v1 + v2 + v3 (`apksigner verify -v`)
- Size: **< 50 MB** (compliant with Amazon Appstore 50 MB binary guideline)
- ABIs: `armeabi-v7a` and `arm64-v8a` (supports all 32-bit and 64-bit Amazon Fire OS and Android devices)

## Build Locally
```bash
./gradlew assembleRelease
```
