# Deliverables — publishing, SEO and future enhancements (v1.0.0)

**Branch:** `arena/f0b63656-becettitools` · **Baseline preserved:** `cbec07b` · **Date:** 6 November 2026
**Rule followed throughout:** the v1.0.0 APK, package name, database schema, architecture, ad
integration rules and commit history are **unchanged**. Everything here is additive.

---

## Status at a glance

| # | Deliverable | Status | Where |
|---|---|---|---|
| 1 | Existing v1.0.0 APK preserved | ✅ Done | `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` (88,869,105 bytes, SHA-256 `460235a2…22632`) — untouched |
| 2 | GitHub Release page | ✅ Notes rewritten (artifacts untouched) | <https://github.com/Ahmedbecett/BECETTITOOLS/releases/tag/v1.0.0> · source: `docs/RELEASE_NOTES_v1.0.0.md` |
| 3 | Professional download landing page | ✅ **Live** — <https://becetti-tools.vercel.app/> | `website/` · deploy notes: `docs/LANDING_PAGE.md` §3 |
| 4 | SEO and sitemap configuration | ✅ Built + verified | `website/robots.txt`, `website/sitemap.xml`, JSON-LD in every page · checklist: `docs/SEO_CHECKLIST.md` |
| 5 | Public privacy policy | ✅ Published-ready in 3 languages | `website/privacy/` (+ `/fr/`, `/ar/`) · mirror: `docs/PUBLIC_PRIVACY_POLICY.md` |
| 6 | Screenshots and documentation | ✅ 6 illustrative previews + OG image; swap guide included | `website/assets/img/previews/` · `screenshots/README.md` |
| 7 | Testing report | ✅ Written, scope stated honestly | `docs/TESTING_REPORT_v1.0.0.md` |
| 8 | Separate future-enhancement list | ✅ Written as proposals only | `docs/FUTURE_ENHANCEMENTS.md` |

Delivered as files first, then published on the owner's explicit instruction ("انشر الموقع") on
6 November 2026: the landing page is live at <https://becetti-tools.vercel.app/>, served from the
existing Vercel project `becetti-tools` (root directory `website`, no build step). The APK, the
GitHub release, the package name and every line of Android source remain untouched — see
`docs/LANDING_PAGE.md` §3 for the exact configuration and the redeploy procedure.

---

## 1. Preserved artifact

- File: `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` — 88,869,105 bytes (**88.9 MB** decimal, as Android's file manager shows it; 84.8 MiB binary)
- SHA-256: `460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632`
  (matches `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256`, recomputed on every verification run)
- Manifest inside the APK: `com.becetti.tools.debug`, versionName `1.0.0`, versionCode `1`,
  minSdk 24, targetSdk 34, `debuggable=true`, `supportsRtl=true`
- Signature: APK Signature Scheme v2 with a debug key (labelled as such everywhere on the site)

## 2. GitHub release

The v1.0.0 release body was rewritten to: state the debug-build caveat up front, publish the
checksum and OS-specific verification commands, give installation steps and troubleshooting,
list the 15 tools accurately, describe privacy behaviour as implemented, include a changelog and
the proposals for later versions, and **remove the link to a landing page that does not exist**.
Release assets were not touched (the release has none; `uploads.github.com` is unreachable from
this environment, so the preserved file on `main` is still where the bytes live — but the site
no longer links to that raw URL: every download link on the page points at
`https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest`, i.e. the newest published release).

## 3. Landing page

Static, dependency-free, three languages, light/dark, mobile-first:

- `/becetti-tools/` (EN) · `/becetti-tools/fr/` · `/becetti-tools/ar/` (RTL)
- `/privacy/` + `/fr/` + `/ar/`
- 15 tools in the app's own six categories, compatibility table read from the APK,
  download + SHA-256 verification block, install guide and troubleshooting, privacy summary,
  FAQ, future roadmap, contact
- branding from the app's palette (`#F47B20`, `#D96513`, `#FFF0E2`, `#172235`, `#263B57`)
- no fake counters, ratings or redirects; every number traced to the artifact

Sources: `scripts/site/**` · build: `node scripts/build-site.mjs` · gate: `node scripts/verify-site.mjs`

## 4. SEO

Canonical URLs, per-locale titles/descriptions, Open Graph + Twitter cards, reciprocal hreflang,
`SoftwareApplication` + `MobileApplication`, `WebSite`/`WebPage`, `BreadcrumbList`, `FAQPage`,
`PrivacyPolicy`, 6-URL sitemap with `xhtml:link` alternates, `robots.txt`, `site.webmanifest`,
`vercel.json` security/cache headers. Search Console steps are listed in `docs/SEO_CHECKLIST.md`.

## 5. Privacy policy

Thirteen sections per language covering local storage, camera permission, Storage Access
Framework, OCR, the currency API, advertising and consent, no password collection, no unnecessary
permissions, security, user controls, children, changes and contact. Generated from one source so
the three languages cannot diverge.

## 6. Screenshots and branding

Six custom SVG previews (Home, Calculator, QR generator, Image to PDF, Unit converter, Arabic
Settings RTL), each captioned as an **illustrative UI preview, not a device capture**, plus a
1200×630 OG image, SVG/PNG logo set and favicon. `docs/LANDING_PAGE.md` §6 documents exactly how
to drop in real captures later.

## 7. Testing report

`docs/TESTING_REPORT_v1.0.0.md` — 237 automated site checks, binary APK inspection, source review,
and an explicit list of what still requires a device pass (the 15-tool functional pass, RTL,
exports, permissions, persistence, ad-failure handling, install of the published file).

## 8. Future enhancements

`docs/FUTURE_ENHANCEMENTS.md` — v1.1 / v1.2 / v2.0 proposals with rationale, files touched,
verification plan and inherited guardrails. Includes two items that were found during this work:
the OCR "Arabic/FR/EN" description vs Latin-only model, and a signed release build.

---

## What a human still has to do

1. Choose the host and canonical domain, then run the build with `--base-url` (§2 of
   `docs/LANDING_PAGE.md`) and deploy.
2. Approve the contact address printed on the site (`scripts/site/site.config.mjs`).
3. Run the device pass in `docs/TESTING_REPORT_v1.0.0.md` §5.
4. After the site is live, add its URL to the release notes and submit the sitemap in Search
   Console.
5. Decide whether `uploads.github.com` access can be arranged to attach the APK as a release
   asset. Once it is attached, switch `apk.downloadUrl` to
   `.../releases/latest/download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` for a one-click download;
   until then the site's single link opens the latest release, whose notes carry the file link.

---

## Files added by this work

```
website/                                  # 27 published files (see docs/LANDING_PAGE.md §1)
scripts/build-site.mjs
scripts/verify-site.mjs
scripts/make-images.sh
scripts/export-privacy-md.mjs
scripts/site/{site.config.mjs,render.mjs,previews.mjs}
scripts/site/i18n/{tools.mjs,pages.mjs,privacy.mjs}
scripts/site/static/{site.css,site.js,logo.svg,favicon.svg}
docs/DELIVERABLES.md                      # this file
docs/LANDING_PAGE.md
docs/SEO_CHECKLIST.md
docs/TESTING_REPORT_v1.0.0.md
docs/FUTURE_ENHANCEMENTS.md
docs/RELEASE_NOTES_v1.0.0.md
docs/PUBLIC_PRIVACY_POLICY.md
```

Modified: `README.md` (download/website section appended), `screenshots/README.md` (preview list +
capture guide). No Android source file, Gradle file, manifest, asset under `app/`, or the APK was
modified.
