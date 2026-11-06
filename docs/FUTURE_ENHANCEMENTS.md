# Future enhancements — proposals only

> **Nothing in this document is part of v1.0.0.** The approved 1.0.0 baseline stays exactly as it
> is; every item below would ship as a separate, independently tested addition, with its own
> version bump and its own release notes.

Each proposal lists what it touches, why it is worth doing, and how it would be verified, so that
work can be approved or dropped item by item instead of as a bundle.

---

## v1.1 — deepening the tools that already exist

### 1.1.1 More scientific calculator functions
- **Why:** the v1.0.0 engine covers `+ − × ÷ % ^ √ sin cos tan log ln π e`; everyday scientific
  work quickly hits the limits (`asin/acos/atan`, `exp`, `n!`, `mod` with negatives, `1/x`,
  `ANS`, radians/degrees toggle, memory keys).
- **Touches:** `features/calculator/CalculatorEngine.kt`, `CalculatorScreen.kt`.
- **Verify:** unit tests for the parser (currently untested) plus a table of reference values
  cross-checked against a desktop calculator; regression list for the expressions listed in
  `docs/TESTING.md`.
- **Risk:** low, but the parser is hand-written — it needs tests *before* new operators are added.

### 1.1.2 More unit conversion categories
- **Why:** six categories today (length, weight, temperature, area, volume, speed). Data,
  energy, pressure, angle, fuel consumption and digital storage are the common next asks.
- **Touches:** `UnitConverterScreen.kt` (the `UnitCategory` enum and factors).
- **Verify:** round-trip property tests (`A → B → A` returns the input within 1e-9), plus a spot
  check of each factor against a reference table.

### 1.1.3 OCR language support — **including Arabic**
- **Why:** this is also a **discrepancy fix**. `ToolCatalog` currently describes the OCR tool as
  "Arabic/FR/EN", but the build ships the Latin recogniser only
  (`com.google.mlkit:text-recognition`, `text.latin.TextRecognizerOptions`). Either the string or
  the model has to change.
- **Proposal:** add `com.google.mlkit:text-recognition-arabic` (and optionally Devanagari/Japanese
  /Korean), let the user pick the script on the OCR screen, and correct the catalogue description
  so it matches reality.
- **Size impact:** each script bundle adds a few MB to the APK — worth stating in the release
  notes, since size is already a top-three FAQ.
- **Verify:** photograph the same bilingual document and check FR/AR extraction; confirm no
  regression for Latin text; confirm the app still works with the model unavailable.

### 1.1.4 Smaller APK — ABI splits and model trimming (measure, don't guess)
- **Why:** the v1.0.0 APK is 88,869,105 bytes (88.9 MB decimal / 84.8 MiB binary). That is
  legitimate — the ML Kit barcode + text-recognition models and native libraries for four CPU
  architectures are bundled so the app works offline — but it is the single biggest reason people
  abandon the download on mobile data, and the most common question after "is it free".
- **Proposal (measure each step before/after, no estimates without a number):**
  1. **ABI splits** (`splits { abi { isEnable = true; isUniversalApk = true } }`) so each user
     downloads only their architecture's native libraries. Keep the universal APK as the
     fallback download link.
  2. **Per-architecture variants** built by the existing workflow
     (`build-native-apk.yml` already builds one variant per run) instead of one fat APK.
  3. **Play App Bundle** if a store release is ever pursued — Play then delivers the split
     automatically.
  4. **Model trimming:** ship only the recogniser the user chose (latin by default), or download
     the optional script models on demand instead of embedding them.
  5. **R8 + resource shrinking** for the debug build path used for public downloads, after the
     signing story is fixed (see 2.0 items) — obfuscation on a debug build is not worth it.
- **Do not** do any of this for v1.0.0: the published 88,869,105-byte file is the frozen
  baseline and its SHA-256 is quoted in the release, the site and the testing report. A smaller
  APK is a **new versioned artifact** (v1.1.0) published alongside, never a replacement.
- **Verify:** `apksigner verify`, install each split/variant on its real architecture, re-run the
  full `docs/TESTING.md` matrix on the smallest artifact, and confirm OCR/QR still work with the
  trimmed models. Record the measured sizes in the release notes.

### 1.1.5 Additional image processing options
- Batch compression with a size target, resize to fixed dimensions, lossless WebP output,
  rotation and crop before export, and a share sheet instead of only "save to Pictures/Becetti".

### 1.1.6 Improved PDF management
- Rotate/delete/reorder pages *after* selection, split a PDF, password-protect the output,
  name the output file before saving, and show a progress indicator for large merges.
- **Verify:** 1-page, 100-page and malformed PDFs; cancel mid-operation; check that the originals
  are never modified.

### 1.1.7 Signed release build (not a feature, a prerequisite for real distribution)
- Generate a release keystore (kept out of the repository), enable `minifyEnabled` with the
  existing ProGuard rules, build an AAB for Play, and publish the APK under a `-release` name
  **without** replacing the preserved v1.0.0 debug artifact.
- **Why now:** every FAQ on the current landing page has to explain that the download is a debug
  build. A signed release removes that caveat.

---

## v1.2 — personalisation and reach

### 1.2.1 Home-screen widgets
- Glance widgets for the calculator (tap to open with a cleared state), the QR generator
  (last payload) and notes (most recent note), plus a "quick tools" 2×1 widget.

### 1.2.2 Customisable dashboard
- Reorder or hide tools on Home, pin up to six favourites to the top, optional compact list mode.
- Persist the layout in DataStore (additive keys only — the existing keys stay untouched so an
  upgrade never loses a preference).

### 1.2.3 Additional favourites and shortcuts
- Long-press shortcut menu (static + dynamic shortcuts), per-tool "favourite" from the tool
  screen, and search that matches tool descriptions as well as names.

### 1.2.4 Deeper personalisation
- Accent colour choice within the approved palette, adjustable text size, "always open last tool"
  option, and an explicit "clear all local data" action in Settings with a confirmation dialog.

---

## v2.0 — bigger, opt-in, and still local-first

### 2.0.1 Optional encrypted cloud backup
- Opt-in only, off by default. Client-side encryption (a user passphrase, Argon2/PBKDF2-derived
  key) before anything leaves the device; the server would store ciphertext only.
- **Must be reflected in the privacy policy before release**, with a plain-language explanation of
  what is encrypted, what the provider can see, and how to delete it.

### 2.0.2 Optional cross-device synchronisation
- Manual "sync now" rather than background sync; conflict handling documented; a visible switch
  to disable it and delete remote copies.

### 2.0.3 Settings export and import
- A single JSON file with language, theme, favourites and (optionally) notes, written through SAF,
  with a version field so future releases can migrate it.

### 2.0.4 Advanced document utilities
- Text search inside PDFs, watermarking, page-number stamping, image→PDF with margins and
  orientation control, and a document-size report before saving.

---

## Candidates that need an owner decision first

| Candidate | Question to settle |
|---|---|
| Play Store listing | Requires a signed AAB, a privacy policy URL that will not change, and a data-safety declaration. Updating the store build is a different distribution channel from the GitHub APK. |
| Enabling the Monetag SDK | Today the adapter is a stub and the app shows no ads. Turning it on is a privacy-policy change *and* a user-experience change; it should be a deliberate, announced release, not a silent one. |
| PWA/web version | Would duplicate the tool logic outside Kotlin; the site is currently a marketing page only. |
| iOS version | Not achievable with the current Kotlin/Compose codebase without a Compose Multiplatform or rewrite decision. |

---

## Guardrails every proposal inherits

1. Never replace, rebuild or re-sign `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`.
2. Never change `com.becetti.tools` or the existing Room schema without a migration plan and
   explicit approval.
3. New DataStore keys and new database columns must be additive and default-safe, so an upgrade
   from 1.0.0 keeps every note, favourite and history entry.
4. Every item ships with: updated copy in the three languages, a device test pass (see
   `docs/TESTING.md`), and a matching update to the privacy policy if data handling changes.
5. The landing page is regenerated from `scripts/site/` only after the new version's facts are
   verified — no number on the site may run ahead of the artifact.
