# Future Enhancements — Separate from v1.0.0 (Additive Only)

> Per spec: Current v1.0.0 is approved baseline and must remain unchanged. All new work must be additive and backward-compatible. Do not modify existing package name, DB structure, versioning workflow, or architecture without explicit approval.

## v1.1 Huge Edition — Already Built (Branch arena/e1ff9b3a-becettitools, commit 81d1926)
**Status: Built & Verified — 87 MB Native Debug APK, 20 dex, No WebView**

- **Splash:** DeepNavy #172235 → SecondaryNavy #263B57 gradient + PrimaryOrange #F47B20 glow 110dp B logo, 36sp Black 3sp, bottom “by ahmed becetti” 13sp 2600ms, FastOutSlowInEasing (fixed from EaseOutBackEasing).
- **Locale RTL:** AppCompatDelegate + attachBaseContext + restart dialog RTL 100% (AR/FR/EN).
- **File Manager (ZArchiver-style):** Browse Internal/Downloads/Pictures, zip/unzip via ZipOutputStream + commons-compress 1.26.1 + zip4j 2.11.5, share FileProvider, MANAGE_EXTERNAL_STORAGE flow, clickable fixed.
- **CI:** Workflow branches ["main","arena/**"], permissions contents:write + issues:write, Upload APK always, Report failure to issue via gh.
- **Docs:** README bumped to v1.1.0 Huge Edition.

**Artifacts:** `BECETTI-TOOLS-v1.1.0-NATIVE-debug.apk` 90251559b SHA256 `1d0ef2b86c8e79549b684d0d707c27340df4d73b647eef22f7945c1a36c0ca36` — raw: https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/arena/e1ff9b3a-becettitools/BECETTI-TOOLS-v1.1.0-NATIVE-debug.apk — Build run 42208891157 success.

## v1.2 Planned (Additive)
- Backup & Restore (Room export/import JSON via SAF)
- Widgets for calculators (AppWidgetProvider + Glance)
- History analytics (local aggregation, charts via Compose Canvas)
- OCR language packs improvement (downloadable ML Kit models)
- Themes marketplace (DataStore + dynamic Material You)

## v2.0 Vision (Major, Opt-In)
- Cloud sync opt-in (encrypted, user-controlled, no tracking)
- Desktop companion (Compose Multiplatform)
- Play Store release (signed AAB, release keystore, Play Integrity)
- Monetization dashboard (Monetag/Adsterra revenue insight, consent analytics)

## Guardrails
- Do not replace or rebuild existing v1.0.0 APK.
- Do not modify existing package name `com.becetti.tools`, DB structure, versioning workflow, architecture without explicit approval.
- Do not overwrite existing files on main without proposal.
