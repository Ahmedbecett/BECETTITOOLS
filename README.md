# BECETTI TOOLS — Everything you need. One smart toolbox.

Native Android & Amazon Fire OS Utility App (Kotlin + Jetpack Compose + Material 3, MVVM + Clean Architecture).

## Package & Release Info
- **Application ID**: `com.becetti.tools`
- **Version**: `1.0.0` (`versionCode = 1`)
- **SDK Support**: `minSdk 24` (Android 7.0+ / Fire OS 7 & Fire OS 8), `targetSdk 34` (Android 14)
- **Release Binary**: `BECETTI-TOOLS-v1.0.0-release.apk` (Signed Release APK, v1 + v2 + v3 signatures, R8 minified & resource-shrunk, **< 50 MB** for Amazon Appstore & direct installation)

## Features (15 tools, all functional)
**Calculator & Converters**
- Scientific Calculator (add/sub/mul/div, %, sqrt, pow, parentheses `(` `)`, trig sin/cos/tan/log/ln, history via Room)
- Unit Converter (Length, Weight, Temperature, Area, Volume, Speed)
- Currency Converter (Frankfurter.app/ECB + built-in offline reference rates for 14 currencies including DZD/SAR/AED/EGP/MAD/TND, cached via DataStore)
- Percentage Calculator (% of number, increase, decrease)
- Age Calculator (years/months/days, next birthday, date difference, desugared `java.time` for API 24+)

**QR & Security**
- QR Generator (Text, URL, Wi-Fi `WIFI:T:WPA;S:...`, Contact vCard, export PNG to Pictures/Becetti)
- QR Scanner (CameraX + ML Kit Barcode, permission only on demand, open links only after confirmation)
- Password Generator (SecureRandom, length 4–64, upper/lower/digits/symbols, copy)
- Password Strength Checker (local analysis, 0–5 score, never stored/uploaded)

**Documents & Files**
- Image to PDF (multiple images via SAF, ordering = selection order, A4/A3/Letter, PdfDocument, preserves originals)
- PDF Merger (multiple PDFs via SAF, PDFBox-Android, merge & save on background thread)
- Image Compressor (quality slider 10–100, preview, accurate KB size comparison, save JPEG to Pictures/Becetti)
- OCR Text Extractor (ML Kit Text Recognition, on-device processing, copy)

**Text Utilities**
- Text Toolkit (word/char count, remove duplicate spaces, upper/lower, remove empty lines, reverse)
- Notes Manager (Room, create/edit/delete/search, local only)

## Amazon Appstore Compliance (`docs/AMAZON_APPSTORE_COMPLIANCE.md`)
- **Binary Size < 50 MB**: R8 code minification + resource shrinking (`isMinifyEnabled = true`, `isShrinkResources = true`), custom vector `BecettiIcons` (eliminating 41.5 MB `material-icons-extended` DEX), ARM 32-bit/64-bit native ABI filtering (`armeabi-v7a`, `arm64-v8a`), compressed JNI packaging, and exclusion of unused BouncyCastle PQC tables.
- **Fire OS 7 & 8 Ready**: Signed with APK Signature Scheme v1 + v2 + v3; optional hardware features (`android.hardware.camera`, `autofocus`, `touchscreen` all marked `android:required="false"`); `<supports-screens>` for all phone and Fire HD tablet sizes; API 24–28 MediaStore fallback.
- **Privacy & Advertising Policies**: No dead placeholder banners; in-app offline Privacy Policy dialog + user consent toggle (`Personalized Ads Consent`) in Settings; HTTPS-only network traffic (`usesCleartextTraffic="false"`).
- **Store Assets**: 512×512 & 114×114 PNG icons in `docs/amazon/`.

## Build
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk` → copied to `BECETTI-TOOLS-v1.0.0-release.apk`.

## Docs
- `docs/AMAZON_APPSTORE_COMPLIANCE.md`
- `docs/ERROR_AUDIT_REPORT.md`
- `docs/ARCHITECTURE.md`
- `docs/PRIVACY_POLICY.md`
- `docs/ADS_INTEGRATION.md`
- `docs/TESTING.md`
