# BECETTI TOOLS — Everything you need. One smart toolbox.

Native Android Utility App (Kotlin + Jetpack Compose + Material 3, MVVM + Clean Architecture).

## Package
- `com.becetti.tools` — Version 1.0.0 — minSdk 24, targetSdk 34, Android 13+ supported.

## Download & landing page
- **APK (preserved v1.0.0, debug build):** [`BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`](BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk) — 88,869,105 bytes
- **SHA-256:** `460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632`
  ([checksum file](BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256))
- **Release notes:** <https://github.com/Ahmedbecett/BECETTITOOLS/releases/tag/v1.0.0>
- **Landing page (EN / FR / AR, deploy-ready):** [`website/`](website/) — see
  [`docs/LANDING_PAGE.md`](docs/LANDING_PAGE.md) to build, verify and publish it
- **Privacy policy (public, 3 languages):** [`docs/PUBLIC_PRIVACY_POLICY.md`](docs/PUBLIC_PRIVACY_POLICY.md)
- **Publishing deliverables & test report:** [`docs/DELIVERABLES.md`](docs/DELIVERABLES.md),
  [`docs/TESTING_REPORT_v1.0.0.md`](docs/TESTING_REPORT_v1.0.0.md)

The v1.0.0 APK is frozen: nothing in this repository rebuilds, re-signs or replaces it. The site is
static, dependency-free and contains no analytics.

```bash
node scripts/build-site.mjs     # regenerate website/
node scripts/verify-site.mjs    # 237 checks: links, metadata, structured data, a11y, checksum
```

## Features (15 tools, all functional)
**Calculator & Converters**
- Scientific Calculator (add/sub/mul/div, %, sqrt, pow, trig sin/cos/tan/log/ln, history via Room)
- Unit Converter (Length, Weight, Temperature, Area, Volume, Speed)
- Currency Converter (Frankfurter.app/ECB, cached via DataStore, offline indicator + last updated timestamp)
- Percentage Calculator (% of number, increase, decrease)
- Age Calculator (years/months/days, next birthday, date difference)

**QR & Security**
- QR Generator (Text, URL, Wi-Fi `WIFI:T:WPA;S:...`, Contact vCard, export PNG to Pictures/Becetti)
- QR Scanner (CameraX + ML Kit Barcode, permission only on demand, open links only after confirmation)
- Password Generator (SecureRandom, length 4–64, upper/lower/digits/symbols, copy)
- Password Strength Checker (local analysis, 0–5 score, never stored/uploaded)

**Documents & Files**
- Image to PDF (multiple images via SAF, ordering = selection order, A4/A3/Letter, PdfDocument, preserves originals)
- PDF Merger (multiple PDFs via SAF, PDFBox-Android, reordering = selection order, merge & save)
- Image Compressor (quality slider 10–100, preview, save JPEG to Pictures/Becetti)
- OCR Text Extractor (ML Kit Text Recognition, AR/FR/EN where supported, copy)

**Text Utilities**
- Text Toolkit (word/char count, remove duplicate spaces, upper/lower, remove empty lines, reverse)
- Notes Manager (Room, create/edit/delete/search, local only)

## Navigation
Home, Categories, Favorites, History, Settings — Bottom Nav + Compose Navigation.

## Monetization
- Centralized `AdsManager` → `MonetagManager` + `AdsterraManager` + `ConsentManager` + `AdPolicy`
- Monetag: SDK init placeholder `MonetagManager.initialize(appId)` — enable when official SDK + account config received; test mode during dev; `isAvailable()` gate.
- Adsterra SmartLink: optional clearly labeled **“Sponsored Offers”** section (`AdPlaceholder` + `openSponsoredOffers()`), user must tap; no auto-redirect; no web-script injection.
- Rules enforced: no ad on calculator screen, no forced pre-tool interstitial, no ad over controls, frequency cap 90s + 3/session, consent via DataStore, no ads if SDK unavailable, independent enable/disable.

## Tech Stack
- Kotlin, Jetpack Compose, Material 3, Navigation-Compose, Coroutines/StateFlow
- Room (notes/favorites/history/recents), DataStore (language/theme/consent/cache)
- CameraX, ML Kit Barcode + Text, ZXing Core (QR gen), PDFBox-Android, Retrofit/OkHttp/Gson, Coil
- SAF for files, SecureRandom for passwords, PdfDocument native for image→PDF

## Languages & Theming
- `values/values-ar/values-fr` strings; `supportsRtl=true`; language & theme persisted via DataStore; Light/Dark/System.

## Privacy & Security
- Permissions only when needed (camera on scanner, SAF for files), rationale strings, no plaintext password storage, no unnecessary data, HTTPS only, no hardcoded secrets.
- See `docs/PRIVACY_POLICY.md` and in-app Settings.

## Project Structure
See spec section 5 — modular `core/design/data/domain/features/monetization`.

## Build
- Android Studio Hedgehog+ / AGP 8.2.2 / Kotlin 1.9.22 / Compose BOM 2024.02.00
- `gradle.properties` + wrapper 8.6
- Debug APK: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `./gradlew assembleRelease` (configure signing)

## Testing Checklist
- Build APK, test 15 tools, AR RTL, FR, EN, Android 13, offline, PDF exports, QR, permissions, restarts, ad-failure, low-memory, battery, privacy disclosures — `docs/TESTING.md`.

## Docs
- `docs/ARCHITECTURE.md`, `docs/PRIVACY_POLICY.md`, `docs/ADS_INTEGRATION.md`, `docs/TESTING.md`

## Roadmap
- 1.1: extra calculators, image utils, OCR improvements; 1.2: widgets, dashboard; 2.0: optional cloud sync.
