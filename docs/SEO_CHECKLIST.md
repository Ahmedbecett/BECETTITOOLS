# SEO & indexing checklist — BECETTI TOOLS landing page

Everything marked ✅ is implemented in `website/` and asserted by
`node scripts/verify-site.mjs` (237 checks). Everything marked ⬜ needs a human: it either
requires the deployed URL or is Google's decision, not ours.

> **Honest expectation:** being discoverable is not the same as being indexed. Indexing depends on
> Google's crawling and ranking decisions and cannot be guaranteed by any markup. What markup can
> do is remove obstacles — which is what this checklist is for.

---

## 1. Target keywords

Each locale targets the terms from the brief, in natural copy rather than stuffed meta tags.

**English**
`BECETTI TOOLS` · `Free Android Utility App` · `Android Calculator and Converter` ·
`QR Code Generator and Scanner` · `Image to PDF Android` · `PDF Merger Android` ·
`Free Android Tools`

**French**
`BECETTI TOOLS Android` · `Outils Android gratuits` · `Convertisseur PDF Android` ·
`Générateur QR Android`

**Arabic**
`أدوات أندرويد مجانية` · `تطبيق أدوات بي سيتي` · `تطبيق تحويل الصور إلى PDF` ·
`ماسح QR للأندرويد`

Where they appear: `<title>`, meta description, `h1`/`h2`, the 15 tool descriptions, the FAQ,
image alt text, and `SoftwareApplication.featureList`. No hidden text, no cloaking, no keyword
blocks repeated for robots only.

---

## 2. On-page and technical SEO

| Item | Status | Where |
|---|---|---|
| Unique title per page (25–75 chars) | ✅ | `render.mjs` → verified |
| Meta description per page (80–330 chars) | ✅ | same |
| Canonical URL on every page | ✅ | absolute, from one config value |
| `robots` meta = `index, follow, max-image-preview:large, max-snippet:-1` | ✅ | all pages |
| `robots.txt` with `Allow: /` and `Sitemap:` line | ✅ | `website/robots.txt` |
| `sitemap.xml`, 6 URLs, `lastmod`, `changefreq`, `priority` | ✅ | `website/sitemap.xml` |
| hreflang alternates `en`, `fr`, `ar`, `x-default` (reciprocal) | ✅ | `<link rel="alternate">` + `xhtml:link` in the sitemap |
| Language in the URL, not a client-side toggle | ✅ | `/becetti-tools/`, `/fr/`, `/ar/` |
| `lang` and `dir` on `<html>` (RTL for Arabic) | ✅ | per-locale documents |
| Open Graph: type, site_name, title, description, url, locale, locale:alternate, image (1200×630), image:alt | ✅ | all pages |
| Twitter Card: `summary_large_image` + title, description, image, image:alt | ✅ | all pages |
| Mobile-first responsive layout | ✅ | CSS, 360 px → 1440 px |
| Fast first paint: no web fonts, no analytics, no third-party requests | ✅ | ~60 KiB HTML+CSS |
| Accessible: skip link, landmarks, heading order, focus rings, alt text, AA contrast | ✅ | checked in `verify-site.mjs` |
| Light and dark themes, honouring the OS preference | ✅ | CSS variables + tiny inline script |
| Root `/` handled (`308` to `/becetti-tools/`) | ✅ | `vercel.json` + meta-refresh fallback |
| Custom 404 with links to all three languages | ✅ | `website/404.html` |

---

## 3. Structured data (JSON-LD)

One `@graph` per page, `@context: https://schema.org`:

- **`SoftwareApplication` + `MobileApplication`** (one node, both types — the mobile type is a
  schema.org subtype of the software type, so a single `@id` avoids duplicating the app):
  `name`, `alternateName` (AR/FR), `applicationCategory: UtilitiesApplication`,
  `operatingSystem: Android 7.0+`, `softwareVersion: 1.0.0`, `fileFormat`, `fileSize`,
  `downloadUrl`, `installUrl`, `releaseNotes`, `license`, `isAccessibleForFree`,
  `offers` (price `0`, USD), `author`/`publisher` (Person), `inLanguage: [en, fr, ar]`,
  `featureList` (the 15 tools).
- **`WebSite`** (`inLanguage` for all three locales) and **`WebPage`** (`datePublished`,
  `dateModified`, `primaryImageOfPage`, `about` → the app node).
- **`BreadcrumbList`** on every page.
- **`FAQPage`** on the three landing pages, matching the visible FAQ word for word.
- **`PrivacyPolicy`** on the policy pages (as a secondary type of `WebPage`).

**Deliberately absent:** `aggregateRating`, `review`, `ratingValue`, `downloadCount`,
`installCount`, `interactionStatistic`. There is no verified rating or install figure, and
inventing one would both mislead users and violate Google's structured-data policies. The
verifier fails the build if any of those properties appear.

Validate after deploying:
- <https://validator.schema.org/>
- <https://search.google.com/test/rich-results>

---

## 4. Google Search Console (after deployment)

The site is live at **<https://becetti-tools.vercel.app/>** (deployed 2026-11-06); the steps below
are owner-side actions and have not been performed.

1. Add a **URL-prefix property** for `https://becetti-tools.vercel.app/`.
2. Verify ownership — DNS TXT record for a custom domain, or an HTML file / `<meta>` tag for a
   `*.vercel.app` host (add the token to `scripts/site/render.mjs` → `head()` if a meta tag is
   required, then rebuild).
3. **Sitemaps → Add**: `https://<host>/sitemap.xml`, then check the status reads *Success* and
   6 URLs are discovered.
4. **URL inspection** → paste `/becetti-tools/` → *Test live URL* → confirm the canonical Google
   selects is the one in the page → *Request indexing*. Repeat for `/becetti-tools/fr/`,
   `/becetti-tools/ar/` and `/privacy/`.
5. Watch **Pages → Indexed** over the following days/weeks. A brand-new host typically takes days
   to weeks before any page is indexed, and a low-authority project site may only show up for
   brand-name queries at first.

Optional acceleration that does **not** mislead:
- link the landing page from the repository's About → Website field and from the release notes;
- share it on profiles that accept links;
- keep the page's facts current — a page whose checksum, version and download link stay in sync
  with the release is more likely to stay indexed.

---

## 5. Core Web Vitals expectations

| Metric | Why it should be good |
|---|---|
| LCP | First viewport is text + one small SVG; no web fonts, no hero bitmap |
| CLS | Every `<img>` declares `width`/`height`; no late-loading fonts |
| INP | 3 KB of JavaScript, two event listeners, no framework |
| Payload | ~60 KiB for a full first page view (CSS + HTML + one SVG) |

Re-measure after deploying with PageSpeed Insights; local numbers are not a substitute.

---

## 6. Preservation guardrails

- The site is a new top-level `website/` directory: the APK, the Android sources, the package
  name, the Room schema and the architecture are untouched.
- The APK is never copied into the site output — `build-site.mjs` aborts if an `.apk`/`.aab`
  appears under `website/`.
- No keystore, `local.properties` or `.env` is ever published — also checked.
- The published SHA-256 is recomputed from the repository file on every verification run.
