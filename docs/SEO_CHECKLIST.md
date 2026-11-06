# SEO & Indexing Checklist — BECETTI TOOLS v1.0.0

## Primary Keywords (Implemented)
- EN: BECETTI TOOLS, Free Android Utility App, Android Calculator and Converter, QR Code Generator and Scanner, Image to PDF Android, PDF Merger Android, Free Android Tools
- FR: BECETTI TOOLS Android, Outils Android gratuits, Convertisseur PDF Android, Générateur QR Android
- AR: أدوات أندرويد مجانية, تطبيق أدوات بي سيتي, تطبيق تحويل الصور إلى PDF, ماسح QR للأندرويد

## On-Page SEO (in `website/app/becetti-tools/layout.tsx`)
- [x] Title: 60–65 chars, includes v1.0.0 + key tools
- [x] Meta description: 150–160 chars, native, 15 tools, offline, 85 MB, Android 7.0+
- [x] Canonical: https://becetti-tools.vercel.app/becetti-tools
- [x] Open Graph: title, desc, url, siteName, locale, alternateLocale, type website, image /assets/logo.svg
- [x] Twitter Card: summary_large_image, title, desc, image
- [x] Keywords: EN/FR/AR array
- [x] Robots: index, follow

## Structured Data (JSON-LD in layout)
- [x] SoftwareApplication: name, alternateName (AR/FR), applicationCategory UtilitiesApplication, OS Android 7.0+, offers price 0 USD, version 1.0.0, fileSize 85MB, downloadUrl (raw GitHub), installUrl, screenshot, description (native toolbox...), author Ahmed Becetti, publisher, inLanguage en/fr/ar, isAccessibleForFree true, license, releaseNotes, featureList (15 tools)
- [x] MobileApplication: name, OS Android, category, aggregateRating

## Technical SEO
- [x] Sitemap: `app/sitemap.ts` + `public/sitemap.xml` with lastmod, changefreq, priority, hreflang alternates
- [x] Robots: `app/robots.ts` + `public/robots.txt` with Allow: / and Sitemap URL
- [x] Alternate links: en/fr/ar/x-default via metadata.alternates.languages
- [x] Indexing directives: robots index/follow
- [x] Mobile-first responsive (grid3/grid4, hero-grid, nav sticky)
- [x] Light/dark themes (CSS variables, data-theme)
- [x] Fast loading: no heavy JS, Next.js static, optimized SVG logo
- [x] Accessible: semantic headings, aria-labels, keyboard nav
- [x] Multilingual RTL: dir toggle via lang state, localStorage persistence

## Verification Steps (After Deployment)
- [ ] Google Search Console: submit sitemap, request indexing for /becetti-tools
- [ ] Validate JSON-LD: https://validator.schema.org/
- [ ] Test OG/Twitter: https://cards-dev.twitter.com/validator, https://www.opengraph.xyz/
- [ ] Lighthouse SEO 100, Performance >90, Accessibility >95
- [ ] Check canonical resolves, no duplicate content

## Preservation (No Overwrite)
- Landing is additive (website/ folder) — does not modify APK, package, DB, or commit history.
- Existing release v1.0.0 preserved (tag cbec07b, SHA 460235...).
