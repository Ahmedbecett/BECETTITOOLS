# BECETTI TOOLS — Everything you need. One smart toolbox.

Native Android & Amazon Fire OS / Softonic release utility app (Kotlin + Jetpack Compose + Material 3,
MVVM + Clean Architecture), fully localized in **Arabic**, **French** and English.

## Package & Release Info
- **Application ID**: `com.becetti.tools` (unchanged across releases)
- **Version**: `1.1.0` (`versionCode = 2`), `compileSdk 35`, `targetSdk 34`
- **SDK Support**: `minSdk 24` (Android 7.0+ / Fire OS 7 & Fire OS 8)
- **Supported languages**: Arabic (`ar`), French (`fr`), English (`en` fallback) — `resourceConfigurations`
  and complete `values-ar` / `values-fr` string sets
- **Signed release binaries** (same release certificate as v1.0.0, APK Signature Scheme v1 + v2 + v3):
  - `BECETTI-TOOLS-v1.1.0-softonic.apk` — Softonic submission
    (SHA-256 `80417b761cdafa9c11e765cf9df8e5d53ae2ef041917b7bc528e6041413c0c2c`)
  - `BECETTI-TOOLS-v1.1.0-release.apk` — CI build
    (SHA-256 `b16282dbe460cb9892da990595b5eeec03250d28e1b83796e721bbfdfc581eda`)
- **Signer certificate SHA-256**: `c40c4ba81a8c28465a126b11fd8fad84cebcf2be163d30ae5c51bbeff75b03b6`
  (`CN=Becetti Tools, O=Becetti, L=Chlef, ST=Chlef, C=DZ`), release keystore in `keystore/becetti-release.jks`

## Release verification
```bash
pip install cryptography asn1crypto
python3 tools/verify_apk_signature.py BECETTI-TOOLS-v1.1.0-softonic.apk   # v1 + v2 + v3, no Android tooling
python3 tools/check_store_metadata.py                                     # AR/FR listing limits and language
python3 tools/check_localization.py                                       # AR/FR resources complete

./gradlew verifyReleaseApk    # assembles the release APK and runs apksigner verify
```
The release build **fails** if `keystore/becetti-release.jks` is missing; it never falls back to
the debug keystore.

## Features (20 tools, all functional)
**Calculator & Converters**
- Scientific Calculator (add/sub/mul/div, %, sqrt, pow, parentheses `(` `)`, trig sin/cos/tan/log/ln, history via Room)
- Unit Converter (Length, Weight, Temperature, Area, Volume, Speed, Data size)
- Currency Converter (Frankfurter.app/ECB + built-in offline reference rates for 16 currencies incl. DZD/SAR/AED/EGP/MAD/TND/QAR/KWD, cached via DataStore)
- Percentage Calculator (% of number, increase, decrease, discount, ratio)
- Age Calculator (years/months/days, next birthday, date difference, desugared `java.time` for API 24+)

**QR & Security**
- QR Generator (Text, URL, Wi-Fi `WIFI:T:WPA;S:...`, Contact vCard, custom colors, export PNG to Pictures/Becetti)
- QR Scanner (CameraX + ML Kit Barcode, permission only on demand, open links only after confirmation)
- Password Generator (SecureRandom, length 4–64, upper/lower/digits/symbols, copy)
- Password Strength Checker (local analysis, 0–5 score, never stored/uploaded)

**Documents & Files**
- Image to PDF (multiple images via SAF, ordering = selection order, A4/A3/Letter, PdfDocument, preserves originals)
- PDF Merger (multiple PDFs via SAF, PDFBox-Android, merge & save on background thread)
- Image Compressor (quality slider 10–100, preview, accurate KB size comparison, save JPEG to Pictures/Becetti)
- OCR Text Extractor (ML Kit Text Recognition, on-device processing, copy)
- Wi-Fi File Transfer (QR-paired, same local Wi-Fi, authenticated AES-GCM, SAF-selected files, user-controlled export/delete, 512 MiB per-file cap; received APKs are not installed automatically)

**Text & Productivity**
- Text & Code Toolkit (word/char count, remove duplicate spaces, upper/lower, remove empty lines, reverse, Base64)
- Notes Manager (Room, create/edit/delete/search, local only)

**Health, Time & Device**
- BMI & Health Calculator (BMI, ideal weight range, calories, water intake)
- Stopwatch & Timer (millisecond lap stopwatch, countdown timer)
- Device & System Inspector (RAM, storage, CPU ABI, battery, display, OS)

## Store metadata (Arabic / French)
- `docs/store/softonic-v1.1.0-metadata.json` — machine-checked titles, captions and descriptions
- `docs/STORE_METADATA_AR_FR.md` — same texts formatted for pasting into the store form
- `docs/SOFTONIC_RESUBMISSION.md` — signature + language fix report and evidence table

## Amazon Appstore Compliance (`docs/AMAZON_APPSTORE_COMPLIANCE.md`)
- **Binary Size < 50 MB**: R8 code minification + resource shrinking (`isMinifyEnabled = true`, `isShrinkResources = true`), custom vector `BecettiIcons` (eliminating 41.5 MB `material-icons-extended` DEX), ARM 32-bit/64-bit native ABI filtering (`armeabi-v7a`, `arm64-v8a`), compressed JNI packaging, and exclusion of unused BouncyCastle PQC tables.
- **Fire OS 7 & 8 Ready**: Signed with APK Signature Scheme v1 + v2 + v3; optional hardware features (`android.hardware.camera`, `autofocus`, `touchscreen` all marked `android:required="false"`); `<supports-screens>` for all phone and Fire HD tablet sizes; API 24–28 MediaStore fallback.
- **Advertising Integration**: Google Mobile Ads with UMP consent flow; Settings exposes UMP privacy choices only when required. See `docs/ADS_INTEGRATION.md` and `docs/PRIVACY_POLICY.md`; integration alone is not a legal-compliance claim.
- **Store Assets**: 512×512 & 114×114 PNG icons in `docs/amazon/`.

## Build
```bash
./gradlew verifyReleaseApk     # build + apksigner verification
# or
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`. CI stages the v1.1.0 APK, publishes it on
the GitHub release and commits it with a SHA-256 checksum.

## Docs
- `docs/SOFTONIC_RESUBMISSION.md`
- `docs/STORE_METADATA_AR_FR.md`
- `docs/AMAZON_APPSTORE_COMPLIANCE.md`
- `docs/ERROR_AUDIT_REPORT.md`
- `docs/ARCHITECTURE.md`
- `docs/PRIVACY_POLICY.md`
- `docs/ADS_INTEGRATION.md`
- `docs/FILES_AND_WIFI.md`
- `docs/TESTING.md`
