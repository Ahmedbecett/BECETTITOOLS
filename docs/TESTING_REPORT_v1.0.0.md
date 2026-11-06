# Testing report — BECETTI TOOLS v1.0.0 (publishing deliverables)

**Date:** 6 November 2026
**Scope:** the publishing, SEO and privacy deliverables built around the unchanged v1.0.0 APK.
**Method:** static inspection of the repository and of the preserved APK binary, plus an automated
quality gate for the site (`node scripts/verify-site.mjs`).

> **Read this first.** No Android device, emulator or Android SDK is available in the environment
> where this work was done. Everything below is either (a) machine-verified against the artifact
> and sources, or (b) explicitly marked as *not verified here* and left for a device pass. Nothing
> in this report is a claim of device testing that did not happen.

---

## 1. Artifact preservation

| Check | Result | Evidence |
|---|---|---|
| v1.0.0 APK unchanged | **PASS** | `git status` clean for `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`; file never written by this work |
| Byte size | **PASS** | 88,869,105 bytes (88.9 MB / 84.8 MiB) |
| SHA-256 matches the committed `.sha256` file | **PASS** | `460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632` |
| SHA-256 on the website matches the file | **PASS** | `verify-site.mjs` recomputes it every run |
| Package name unchanged | **PASS** | APK manifest still `com.becetti.tools` / `com.becetti.tools.debug` |
| Version unchanged | **PASS** | `versionName 1.0.0`, `versionCode 1` inside the APK |
| No APK rebuilt or re-signed | **PASS** | no Gradle build was run; no keystore exists in the repo |
| Android sources, manifest, Room schema untouched | **PASS** | every change in this work is a new file under `website/`, `scripts/`, `docs/` (see §8) |
| APK/AAB never copied into the published site | **PASS** | build aborts if a binary appears under `website/` |

---

## 2. APK inspection (binary, not documentation)

Parsed with a small AXML reader over the APK's binary `AndroidManifest.xml` and the ZIP central
directory:

```
package        : com.becetti.tools.debug
versionName    : 1.0.0        versionCode : 1
compileSdk     : 34           minSdk      : 24      targetSdk : 34
debuggable     : true         supportsRtl : true    allowBackup : true
application    : com.becetti.tools.BecettiToolsApp
launcher       : com.becetti.tools.MainActivity (MAIN/LAUNCHER)
permissions    : CAMERA, INTERNET, ACCESS_NETWORK_STATE,
                 READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED
features       : camera (required=false), camera.autofocus (required=false), …
entries        : 718 files · resources.arsc · 20 dex files · 4 ABIs
signature      : "APK Sig Block 42" present (v2/v3 scheme); no v1 META-INF/*.RSA
```

**Result: PASS** — the facts published on the landing page match the binary. The debug label on
the site and in the release notes is accurate (`debuggable=true`, debug key, `.debug` install id).

---

## 3. Source-level review (42 Kotlin files, 1,606 lines)

| Check | Result | Evidence |
|---|---|---|
| No WebView / `android.webkit` (stays native) | **PASS** | 0 files match |
| No analytics / crash / attribution SDK | **PASS** | 0 matches for firebase-analytics, crashlytics, appsflyer, adjust, facebook |
| No ad SDK in Gradle | **PASS** | 0 matches for monetag/adsterra/admob/applovin; both managers are stubs (`MonetagManager.isAvailable() == false`, `AdsterraManager.smartLinkUrl == null` by default) |
| Network endpoints in the whole app | **PASS** | `https://api.frankfurter.app/` (currency), plus `github.com` and `becetti.tools` strings used in links/defaults. Nothing else. |
| Ad policy enforced in code | **PASS** | `AdPolicy`: no ad on `calc_scientific`, 90 s interval, 3 per session |
| Consent stored as a local flag only | **PASS** | `ConsentManager` → DataStore boolean |
| Passwords never persisted | **PASS** | `SecurityUtils.secureRandomPassword` result lives in a composable state variable; no DAO writes |
| 15 tools registered | **PASS** | `ToolCatalog.all` has exactly 15 `ToolItem`s across 6 categories |
| Output locations | **PASS** | `Pictures/Becetti` (QR PNG, compressed JPEG), `Documents/Becetti` (created + merged PDFs) |

### Finding: the OCR tool is described as "Arabic/FR/EN" but only ships the Latin model

`OcrScreen.kt` uses `com.google.mlkit.vision.text.latin.TextRecognizerOptions` (dependency:
`com.google.mlkit:text-recognition:16.0.0`, the Latin script model). Arabic-script recognition
would require `com.google.mlkit:text-recognition-arabic` and a separate recognizer, which are not
in the build.

- **Reported, not changed.** Fixing this is a v1.1 feature, not a v1.0.0 patch (§2 of the brief
  forbids changing behaviour of the approved baseline).
- **Consequence for the site:** the landing page says *Latin script (French, English and
  similar)*, states in the FAQ that Arabic OCR is not part of v1.0.0, and lists it under the
  v1.1 proposals. The site therefore does not repeat the optimistic in-app description.
- **Recommendation for v1.1:** add the Arabic model *or* correct the `ToolCatalog` description
  string; either way the two should agree.

---

## 4. Website quality gate

`node scripts/verify-site.mjs` → **237 checks passed, 0 failed, 0 warnings** (8 pages, 27 files).

What it asserts:

- every internal `href`/`src` resolves (no 404s inside the site);
- no third-party asset requests, no inline event handlers;
- per page: title length + uniqueness, description length, canonical presence and path,
  `robots` directives, OG/Twitter tags, `lang`/`dir`, viewport, skip link;
- reciprocal hreflang for `en`/`fr`/`ar`/`x-default`;
- exactly one JSON-LD block per page, valid JSON, required nodes present
  (`WebSite`, `WebPage`, `BreadcrumbList`; plus `SoftwareApplication`+`MobileApplication` and
  `FAQPage` on landings), `downloadUrl`, `softwareVersion`, `offers.price = 0`,
  `operatingSystem` containing Android;
- no `aggregateRating`, `ratingValue`, `downloadCount`, `installCount` anywhere in structured data;
- all 15 tool names and all 15 tool anchors present in each of the three languages;
- accessibility basics: `alt` on every image, image dimensions, accessible button names,
  one `h1`, no skipped heading levels, `<caption>` on every table;
- `robots.txt` allows crawling and points at the sitemap; sitemap has exactly 6 URLs with
  `xhtml:link` alternates and `lastmod`;
- manifest JSON valid and every declared icon exists; `vercel.json` redirect + security headers;
- APK SHA-256 and byte size on the site match the repository file;
- no `.apk`, `.aab`, keystore or `local.properties` in the published output.

Manual checks on top of the automated gate:

- the six UI previews were rasterised and inspected individually (icon shapes, label alignment,
  no clipped text in the Arabic RTL preview, nothing overflowing the 360×740 frame);
- the OG image (1200×630) was rendered and inspected for legibility and correct layout;
- both the light and the dark palette were reviewed for contrast by eye against AA
  (orange `#F47B20` on navy `#172235`, and navy text `#202938` on `#F8F9FB`).

---

## 5. Not verified in this environment (needs a device pass)

These are the items from §8 of the brief that require hardware or an emulator. **They are not
claimed as passing.** Each has a concrete script in `docs/TESTING.md` (existing) and is repeated
here with the extra cases this publishing round introduces:

| Item | How to verify |
|---|---|
| All 15 tools functional | Install the APK on Android 13; run the smoke tests in `docs/TESTING.md` (`2+2*3=8`, `sqrt(9)=3`, `sin(30)=0.5`, 1 km→1000 m, 20 % of 150 = 30, QR generate → PNG in `Pictures/Becetti`, merge two PDFs, compress, OCR a printed page, note CRUD) |
| Arabic RTL | Switch the app and the site to Arabic; check mirrored layout, no clipped labels, correct numerals |
| French and English | Both app languages and both site locales |
| Android 13 compatibility | Runtime media permissions, SAF pickers, camera |
| Offline behaviour | Airplane mode: everything but the live currency refresh; cached rates + offline indicator shown |
| PDF / image exports | Files visible in `Documents/Becetti` and `Pictures/Becetti`; originals intact |
| Camera permission | Deny → no crash → grant later from the scanner screen |
| Local persistence | Kill and relaunch: notes, favourites, history, language, theme survive |
| Ad failure handling | No crash when `MonetagManager.isAvailable()` is false / `smartLinkUrl` is null |
| Install of the published file | Download from the release link on a phone, verify SHA-256 on-device, install over the previous build, confirm `com.becetti.tools.debug` |
| Site on a real device | Tap targets, sticky header, theme toggle, copy-checksum button, language links, `prefers-color-scheme` |
| Lighthouse / PageSpeed | Run after deployment; local numbers are not a substitute |

---

## 6. Reproducibility

```bash
node scripts/build-site.mjs        # deterministic output from committed sources
node scripts/verify-site.mjs       # 237 checks
bash scripts/make-images.sh        # raster assets (needs ImageMagick + DejaVu fonts)
```

The build has no network access, no npm dependencies and no timestamp drift apart from
`SITE_BUILD_DATE` (default `2026-11-06`).

---

## 7. Summary

- The **v1.0.0 APK is untouched** and byte-identical to the approved baseline; its checksum is
  published and re-verified automatically.
- All publishable facts on the new landing page and release notes were taken from the APK binary
  and the sources, not from earlier prose.
- The site passes a 237-check gate covering links, metadata, structured data, hreflang,
  accessibility basics and "no unverifiable claims" rules.
- One substantive discrepancy was found and **reported rather than silently fixed**: the in-app
  OCR description promises Arabic, the bundled model is Latin-only.
- Device-level functional testing of the 15 tools remains open and is listed in §5.
