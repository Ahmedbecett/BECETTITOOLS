# BECETTI TOOLS v1.0.0 — Native Android Utility Toolbox (Debug Build)

**Package:** `com.becetti.tools` · **Version:** 1.0.0 (versionCode 1) · **Minimum Android:** 7.0 (API 24) · **Target SDK:** 34 · **15 tools** · **Kotlin + Jetpack Compose + Material 3**

> **Preservation notice.** This release describes the **existing, unchanged** v1.0.0 APK. Nothing was rebuilt, re-signed, renamed or overwritten. Commit history, package name, database schema and architecture are untouched.

---

## ⚠️ This is a debug build

`BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` was produced by the Android Gradle plugin's **debug** variant:

- `android:debuggable="true"`
- signed with the **debug keystore** (APK Signature Scheme v2), not with a production or Play signing key
- install identifier is `com.becetti.tools.debug` (the `.debug` applicationIdSuffix is part of the preserved build)

Install it to evaluate the app. It is **not** a store-ready release. A signed release build is on the roadmap.

---

## 📦 Download

The APK is preserved byte-for-byte in the repository:

| | |
|---|---|
| **File** | `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` |
| **Size** | 88,869,105 bytes (84.8 MiB) |
| **Download** | https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk |
| **Checksum file** | https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256 |
| **Browse on GitHub** | https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk |

**Note on release assets:** this release has no uploaded asset. The publishing environment could not reach GitHub's asset upload endpoint, so the download of record is the preserved file on `main` (identical bytes to the approved baseline). No existing artifact was replaced or removed.

---

## 🔐 SHA-256

```
460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632  BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk
```

Verify **before** installing:

```bash
# Linux
sha256sum BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk
# macOS
shasum -a 256 BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk
# Windows
certutil -hashfile BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk SHA256
```

The value must match the line above exactly. The same digest is printed on the project's landing page and stored in `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256`.

---

## 🛠️ Installation

1. **Download the file** on the phone itself, so it lands in *Downloads*.
2. **Allow installing from this source.** When Android asks, enable *Install unknown apps* for your browser or file manager. This is an Android per-app permission; the app cannot and does not change it.
3. **Open the file** and review the permissions Android lists, then choose **Install**.
4. **A Play Protect prompt is expected.** An APK that did not come from Google Play can trigger a "scan for harmful apps" notice. Continue only if the checksum matched.
5. **Grant permissions as you use the features** — camera is requested only when you open the QR scanner; file access only when you pick images or PDFs.

**If installation fails**

- *"App not installed"* → an older debug build is probably present. Uninstall it first (`com.becetti.tools.debug`).
- *"There was a problem parsing the package"* → the download was incomplete. Re-download and re-check the checksum.
- The APK bundles `arm64-v8a`, `armeabi-v7a`, `x86` and `x86_64`, so it runs on phones and on most emulators.

---

## 🧰 What is inside — 15 tools

**Calculator & Converters**
1. **Scientific Calculator** — `+ − × ÷`, `%`, `^`, `√`, `sin/cos/tan` (degrees), `log`, `ln`, π, e; local history.
2. **Unit Converter** — length, weight, temperature, area, volume, speed.
3. **Currency Converter** — reference rates from `api.frankfurter.app` (ECB data) over HTTPS, cached with a last-updated time and an offline indicator.
4. **Percentage Calculator** — percentage of a number, increase, decrease.
5. **Age Calculator** — years/months/days, next birthday, date difference.

**QR & Security**

6. **QR Code Generator** — text, URL, Wi-Fi (`WIFI:T:WPA;S:…`) and contact (vCard); export PNG to *Pictures/Becetti*.
7. **QR Code Scanner** — CameraX + ML Kit on-device decoding; camera permission only on demand; links open only after confirmation.
8. **Password Generator** — `SecureRandom`, 4–64 characters, independent upper/lower/digits/symbols toggles; nothing stored or uploaded.
9. **Password Strength Checker** — local 0–5 analysis by length and character variety; the password never leaves the device.

**Documents & Files**

10. **Image to PDF** — multi-image selection through the Storage Access Framework, page size A4/A3/Letter, saved to *Documents/Becetti*; originals untouched.
11. **PDF Merger** — PDFBox-Android, merge order = selection order, saved to *Documents/Becetti*.
12. **OCR Text Extractor** — ML Kit on-device text recognition (Latin script: French, English and similar), copy the result.
13. **Image Compressor** — quality slider 10–100 with preview, JPEG saved to *Pictures/Becetti*.

**Text Utilities**

14. **Text Toolkit** — word/character counts, UPPER/lower, duplicate-space cleanup, empty-line removal, reverse.
15. **Notes Manager** — create, edit, search and delete notes in a local Room database.

**Navigation:** Home · Categories · Favorites · History · Settings. **Languages:** English, French, Arabic (full RTL). **Theme:** light / dark / system.

---

## 🛡️ Privacy

- Offline-first, **no account**, no analytics SDK, no advertising identifier.
- Notes, favourites and history live in an on-device Room database.
- Passwords, QR content and OCR text are processed on the device and never uploaded.
- Camera permission is requested only when the QR scanner opens; files are chosen through the Android system picker.
- The currency converter sends only currency codes to `api.frankfurter.app`; results are cached locally.
- No advertising SDK is initialised in v1.0.0. An optional, clearly labelled **"Sponsored Offers"** row can appear and opens an external page only when you tap it — never automatically. The calculator screen never shows ads, and the code enforces a 90-second / three-per-session interstitial cap.

Full policy: `docs/PRIVACY_POLICY.md` in the repository, and the public policy pages shipped with the landing page (`website/privacy/`, in English, French and Arabic).

---

## 🧾 Changelog

### v1.0.0 — 2026-11-05

- First public build of BECETTI TOOLS for Android (Kotlin, Jetpack Compose, Material 3, MVVM + Clean Architecture).
- 15 tools across six in-app categories, all functional (no placeholder buttons).
- Room persistence for notes, favourites, recents and calculator history; DataStore for language, theme and consent.
- Three full interface languages (English, French, Arabic) with `supportsRtl` and RTL layouts.
- Monetisation layer abstracted behind `AdsManager` / `AdPolicy` with no SDK active, consent flag and frequency caps.
- Landing page, SEO files, public privacy policy and release documentation added around the unchanged APK.

### Known limitations (documented, not defects of this build)

- Debug build, debug signature — not a store release.
- OCR covers Latin script only; Arabic-script recognition needs an additional model (planned for v1.1).
- No cloud sync or backup; uninstalling removes local data.

---

## 🗺️ Later versions (proposals only)

- **v1.1** — more scientific functions, more conversion categories, improved OCR language support (including Arabic script), extra image processing options, better PDF management.
- **v1.2** — home-screen widgets, customisable dashboard, more favourites and shortcuts, deeper personalisation.
- **v2.0** — optional encrypted cloud backup, optional cross-device sync, settings export/import, advanced document utilities.

None of these are part of v1.0.0, and nothing above changes the file published in this release.

---

## 🔗 Links

- Repository: https://github.com/Ahmedbecett/BECETTITOOLS
- Landing page source: `website/` (deploy-ready; see `docs/LANDING_PAGE.md`)
- Privacy policy: `docs/PRIVACY_POLICY.md`
- Licence: MIT
- Author: Ahmed Becetti — https://github.com/Ahmedbecett
