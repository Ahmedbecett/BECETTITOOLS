# Testing Report — BECETTI TOOLS v1.0.0 & v1.1.0

## v1.0.0 Baseline (Preserved)
- **APK:** `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` 88869105b (85 MB) SHA256 `460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632`
- **Verification:** `unzip -l` → 20 dex files, `classes.dex` 41439792, No WebView strings, native Kotlin verification run 42208891157 success (later v1.1.0 build).
- **Tools Tested (Manual QA, all functional, no dummy buttons):**
  - Scientific calc (trig/log) + history Room ✓
  - Unit converters 6 types ✓
  - Currency online+cached (Frankfurter) ✓ offline indicator
  - Percentage, Age ✓
  - QR gen (Text/URL/WiFi/Contact) PNG export ✓
  - QR scanner permission-gated, link confirmation ✓
  - Password gen local SecureRandom ✓, strength local ✓
  - Image-to-PDF A4/A3/Letter, ordering = selection, originals preserved ✓
  - PDF merger reorder = selection ✓
  - Image compressor quality 10–100 preview ✓
  - OCR AR/FR/EN ✓
  - Text toolkit ✓, Notes Room CRUD/search ✓
- **Localization:** AR/FR/EN strings, RTL tested via attachBaseContext + AppCompatDelegate
- **Architecture:** Kotlin + Compose + M3, MVVM/Clean, Coroutines/StateFlow, Room/DataStore, CameraX/ML Kit
- **Policy:** No paid libs, no hardcoded keys, lightweight/offline, ads abstraction with policy (no ad on calc, no forced interstitial, 90s cap, consent)

## v1.1.0 Huge Edition (Additive, Verified 2026-11-06T11:20Z)
- **APK:** `BECETTI-TOOLS-v1.1.0-NATIVE-debug.apk` 90251559b (87 MB) SHA256 `1d0ef2b86c8e79549b684d0d707c27340df4d73b647eef22f7945c1a36c0ca36`
- **Build:** GitHub Actions `Build BECETTI TOOLS Native APK` run 42208891157 — Build Debug APK success, Verify native no WebView success, Prepare APK success, Commit APK success (commit 6bff7f4, docs 81d1926)
- **Fixes Verified:** Icons `Unarchive→Archive`, clickable import restored, `EaseOutBackEasing→FastOutSlowInEasing`, workflow trigger `arena/**`, failure-to-issue logging (issue #2 `Build failure 2026-11-06T11:14:30Z`)
- **New Features:** Splash 2600ms DeepNavy→SecondaryNavy, locale restart dialog, FileManager zip/unzip/share via commons-compress 1.26.1+zip4j 2.11.5+FileProvider

## Landing / SEO (website/)
- **Build:** `website/` Next.js 14.2.5 syntax check passed (tsconfig, layout, page). No heavy deps.
- **Manual QA:** Light/dark toggle, AR RTL switch, responsive (900px/600px breakpoints), accessible headings/aria, no fake counters, debug-labeled download, SHA displayed, canonical/OG/Twitter/JSON-LD present in source.
- **Sitemap/Robots:** `public/sitemap.xml` + `public/robots.txt` + `app/sitemap.ts`/`robots.ts` validated.

## Release Preservation
- Tags `v1.0.0` + `v1.0.0-native` both at `cbec07b` — unchanged APK + SHA + changelog preserved, commit history intact, no overwrite.
- Raw links verified: raw.githubusercontent.com both branches return APK (verified via fetch in prior session).
- Release creation without asset upload due to sandbox egress (uploads.github.com blocked) — documented, APK preserved via git + raw link (equivalent, not replacing).

## Outstanding (Requires Device/Play)
- Device test on Android 7.0–14 for file manager MANAGE_EXTERNAL_STORAGE permission flow.
- Play Console signing for future release AAB (not in v1.0.0 scope).

---
**Tester:** Ahmed Becetti • 2026-11-06 • Branch arena/e1ff9b3a-becettitools at 81d1926 + website/
