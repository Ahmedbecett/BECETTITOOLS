# Landing page — build, verify and publish

A dependency-free static site for BECETTI TOOLS, published at `/becetti-tools` with a privacy
policy at `/privacy`. No framework, no build server needed at request time, no third-party
requests: **8 HTML documents, one CSS file, one 3 KB script, vector screenshots** — roughly
430 KiB total, or about 60 KiB for the first page view.

> **Status: live at <https://becetti-tools.vercel.app/>** (deployed 6 November 2026 on the owner's
> explicit instruction). Nothing in this repository deploys itself: git auto-deploy is disabled
> for the Vercel project, so a push never publishes. See §3 for the configuration in force.

---

## 1. What is in the box

```
website/                        ← deploy this directory (it is the site root)
├── index.html                  ← meta-refresh fallback → /becetti-tools/
├── becetti-tools/index.html    ← landing page, English (canonical)
├── becetti-tools/fr/index.html ← French
├── becetti-tools/ar/index.html ← Arabic, dir="rtl"
├── privacy/{,fr/,ar/}index.html← public privacy policy, 3 languages
├── 404.html
├── robots.txt
├── sitemap.xml                 ← 6 URLs with xhtml:link hreflang alternates
├── site.webmanifest
├── vercel.json                 ← / → /becetti-tools/ redirect, security + cache headers
└── assets/
    ├── css/site.css            ← light/dark, mobile-first, logical properties for RTL
    ├── js/site.js              ← theme toggle + checksum copy (progressive enhancement)
    └── img/
        ├── logo.svg, favicon.svg, logo-192.png, logo-512.png, favicon-32.png,
        │   apple-touch-icon.png, og-image.png (1200×630)
        └── previews/*.svg      ← illustrative UI previews (see §6)
```

The **sources** live in `scripts/`:

| File | Role |
|---|---|
| `scripts/site/site.config.mjs` | Verified facts: version, package ids, SDK levels, APK size, SHA-256, URLs, contact |
| `scripts/site/i18n/tools.mjs` | The 15 tools and 6 categories, named exactly as in the app, in EN/FR/AR |
| `scripts/site/i18n/pages.mjs` | All landing-page copy for the three locales |
| `scripts/site/i18n/privacy.mjs` | Full privacy policy text for the three locales |
| `scripts/site/render.mjs` | HTML + JSON-LD renderer |
| `scripts/site/previews.mjs` | Generates the six vector UI previews |
| `scripts/site/static/` | CSS, JS, SVG logo, favicon (copied verbatim) |
| `scripts/build-site.mjs` | Writes `website/` |
| `scripts/verify-site.mjs` | 237-check quality gate |
| `scripts/make-images.sh` | Raster brand assets via ImageMagick |

Rebuild:

```bash
node scripts/build-site.mjs      # writes website/
node scripts/verify-site.mjs     # must end with "0 failed"
bash scripts/make-images.sh      # only when the logo or OG image changes
```

Node 18+ is enough; there are **no npm dependencies** and no lockfile to maintain.

---

## 2. Canonical URL — one place to change

Every absolute URL (canonical, hreflang, Open Graph, sitemap, robots) comes from two values:

```bash
# default: https://becetti-tools.vercel.app  (the URL already advertised in the release notes)
node scripts/build-site.mjs

# custom domain
node scripts/build-site.mjs --base-url=https://becetti.tools

# GitHub Pages project site
node scripts/build-site.mjs --base-url=https://ahmedbecett.github.io --base-path=/BECETTITOOLS
```

The same values can be provided as `SITE_BASE_URL` / `SITE_BASE_PATH`.

All **internal** links and asset references are relative, so the output works unchanged at a
domain root or in a sub-directory. Only metadata and `sitemap.xml` / `robots.txt` need the
absolute base.

After changing the base, re-run `node scripts/verify-site.mjs` — it re-checks every canonical,
hreflang pair and the sitemap against the configured base.

---

## 3. Download-link policy

The site publishes exactly **one** download target:

```
https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest
```

- GitHub resolves `/releases/latest` to whichever release is marked **Latest**, so the link keeps
  pointing at the current version without rebuilding the site after each release.
- No other copy of the APK is linked from the site: no `raw.githubusercontent.com` URL, no
  `blob/main` page, no separate `.sha256` file button. The SHA-256 is shown as copyable text, with
  per-OS verification commands.
- v1.0.0 has **no uploaded release asset** (`uploads.github.com` is unreachable from the publish
  environment), so the release notes on that page contain the file link. Once the APK is attached
  to the release, set `apk.downloadUrl` in `scripts/site/site.config.mjs` to
  `https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest/download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk`
  and rebuild for a one-click download that still tracks the newest release.

---

## 4. Publishing options

### A. Vercel project `becetti-tools` — **live** (current deployment)

Deployed 2026-11-06 as production deployment `dpl_DE6ShYcTyt3qyAziLXeZCZ2hXqrJ`
(state `READY`, promoted to production).

**Public URL: <https://becetti-tools.vercel.app/>** — `/` 308-redirects to `/becetti-tools/`.

Settings in force (project `becetti-tools`, root directory `website`):

| Setting | Value | Meaning |
|---|---|---|
| Root Directory | `website` | the built site is committed to the repo |
| Framework Preset | `hugo` (a *static* preset) | see the note below |
| Build Command | `true` | no-op: nothing is built, the files are already final |
| Install Command | `true` | no-op: the site has no dependencies |
| Output Directory | `.` | serve `website/` exactly as committed |

Why a static preset and not "Other": Vercel stores the "Other" preset as `framework: null`, and
the deployment tooling used here cannot send `null` (an explicit `null`/`""`/`"other"` is
rejected: *"The provided framework must match one of the supported options"*). A brand-new
project's *first* deployment also demands a `projectSettings` object that the same tooling
cannot send. Picking a real static preset and neutralising it with no-op commands sidesteps
both constraints without changing a single byte of the output. The `nextjs` preset the project
used to carry is gone — that preset was running `next build` in a directory with no Next.js app,
which is why both earlier deployments failed.

Aliases:

| Host | Public? |
|---|---|
| `becetti-tools.vercel.app` | **yes** — registered as a project domain, bypasses the auth gate |
| `becetti-tools-ahmed-d33b.vercel.app` | no — automatic alias, behind *Vercel Authentication* |
| `becetti-tools-git-<branch>-ahmed-d33b.vercel.app` | no — same protection |

The protection comes from `ssoProtection.deploymentType = all_except_custom_domains`: every
deployment is gated **except** on registered/project domains. Only `becetti-tools.vercel.app` is
linked in the site's canonical, Open Graph and sitemap tags, so the sitemap never points at a
gated host. To make the auto-generated aliases public too, disable *Settings → Deployment
Protection → Vercel Authentication* in the dashboard.

Publishing an update:

- Git auto-deploy is **disabled** for the connected repository (`createDeployments: "disabled"`,
  `disableRepositoryDispatchEvents: true`), so **a push does not publish anything**. This is
  deliberate: the live site only changes when someone asks for it.
- Rebuild and commit first (`node scripts/build-site.mjs && node scripts/verify-site.mjs`), then
  either press *Redeploy* on the dashboard or create a deployment from the git source with
  target `production` (the method used for this release).
- The canonical base is the `baseUrl` default in `scripts/site/site.config.mjs`
  (`https://becetti-tools.vercel.app`), which is the public host — no `--base-url` override
  needed for a redeploy.

Verified live on 2026-11-06: `/` → 308 → `/becetti-tools/` (EN landing page),
`/becetti-tools/fr/`, `/becetti-tools/ar/` (Arabic title + RTL markup), `/privacy/`,
`/sitemap.xml` (6 URLs), `/robots.txt`.

### B. New, separate Vercel project (fallback — *not* used; the existing project is live)

Create a project whose root directory is `website`, framework preset *any static preset* with
no-op build/install commands and output directory `.` (the "Other" preset cannot be set through
API tooling, and a new project's first deployment requires a `projectSettings` payload). The
canonical base changes to the new domain — rebuild with `--base-url` first. The new project's
`*.vercel.app` aliases are also behind Vercel Authentication until a domain is attached.

### C. GitHub Pages

1. Settings → Pages → *Build and deployment* → **Deploy from a branch** → branch `main`,
   folder `/website`… *GitHub Pages only serves a repository root or `/docs`.* Two options:
   - move the built output to `docs/site` and publish `/docs`, or
   - copy `website/` into a `gh-pages` branch root in CI.
2. Rebuild with a sub-path base so canonical URLs stay correct:
   ```bash
   node scripts/build-site.mjs --base-url=https://ahmedbecett.github.io --base-path=/BECETTITOOLS
   ```
3. The page will live at `https://ahmedbecett.github.io/BECETTITOOLS/becetti-tools/`.

### D. Netlify / Cloudflare Pages / any static host

Publish directory `website`, no build command. Redirect `/` → `/becetti-tools/` (the
`index.html` meta refresh covers hosts without redirect rules).

---

## 5. Facts on the page are verified, not assumed

Everything numeric or technical comes from the preserved artifact and is asserted by
`scripts/verify-site.mjs`:

| Claim on the site | How it was verified |
|---|---|
| `com.becetti.tools`, `com.becetti.tools.debug` | Binary `AndroidManifest.xml` parsed out of the APK |
| versionName `1.0.0`, versionCode `1` | Same manifest parse |
| minSdk 24, target/compileSdk 34 | Same manifest parse |
| `debuggable=true`, `supportsRtl=true`, `allowBackup=true` | Same manifest parse |
| Declared permissions list | Same manifest parse |
| "APK Signature Scheme v2, debug key" | APK Signing Block (`APK Sig Block 42`) present, no v1 `META-INF/*.RSA` |
| 88.9 MB (84.8 MiB) / 88,869,105 bytes | File size, re-checked at build/verify time; both units are rendered so the page matches what the phone shows |
| SHA-256 `460235a2…22632` | Recomputed by `verify-site.mjs` on every run |
| ABIs `arm64-v8a, armeabi-v7a, x86, x86_64` | `lib/` entries in the APK |
| Tool behaviour | Read from the v1.0.0 Kotlin sources, one by one |

Deliberately **not** on the page: download counts, star ratings, user numbers, "millions of
downloads", store badges. None of those could be verified, so none are published. The verifier
fails the build if `aggregateRating`, `ratingValue`, `downloadCount` or `installCount` ever
appear in the structured data.

---

## 6. Privacy policy pages

The public policy is `website/privacy/` (EN), `/privacy/fr/`, `/privacy/ar/`. Its text lives in
`scripts/site/i18n/privacy.mjs` so the three languages cannot drift apart:

- local-only storage (Room/DataStore) and how to delete it
- camera permission and when it is requested
- Storage Access Framework file access and the output folders
- on-device OCR / barcode processing
- the currency API (`api.frankfurter.app`, HTTPS, currency codes only, local cache)
- advertising and consent, including the fact that no ad SDK is initialised in v1.0.0
- no collection or upload of generated passwords
- no unnecessary permissions, children, security, user controls, changes, contact

A Markdown mirror for the repository is generated with:

```bash
node scripts/export-privacy-md.mjs     # writes docs/PUBLIC_PRIVACY_POLICY.md
```

---

## 7. Replacing the previews with real screenshots

`assets/img/previews/*.svg` are **illustrative UI previews rebuilt from the Compose layout**, not
device captures, and each one is captioned that way on the page. To swap in real screenshots:

1. Capture from a device (Android 13 recommended): Home, Calculator, QR generator, Image to PDF,
   Unit converter, Settings in Arabic.
2. Save as `website/assets/img/previews/home.png` … `settings-rtl.png` (keep the same names,
   1080 px wide, ≤ 200 KB each, PNG or WebP).
3. In `scripts/site/i18n/pages.mjs`, `previews.items` already lists the six ids — nothing else
   changes. Simply rebuild with the `.png` files present, then update the `<img src>` extension
   in `render.mjs` (`heroSection` and `previewSection`) from `.svg` to `.png`.
4. Delete or keep the SVGs; `verify-site.mjs` only checks that referenced files exist.
5. Update the caption/disclaimer strings in `pages.mjs` (`previews.disclaimer`) once they are
   real captures.

Alternative: add the captures *in addition* to the previews as a "Screenshots" section, which is
what a future release should do once a signed build exists.

---

## 8. Accessibility and performance notes

- Semantic landmarks (`header`, `nav`, `main`, `footer`), one `h1` per page, no skipped levels.
- Skip link, visible focus rings, `prefers-reduced-motion` and `prefers-color-scheme` support.
- Theme choice is applied by a 300-byte inline script before first paint (no flash), and stored
  in `localStorage`; the page is fully usable with JavaScript disabled.
- Language is part of the URL (`/fr/`, `/ar/`) rather than a client-side switch, so every
  translation is crawlable and shareable; `dir="rtl"` is set on the Arabic documents.
- All colours meet WCAG AA contrast in both themes; text can be zoomed to 200 % without loss.
- No web fonts, no icon fonts, no analytics, no cookies, no third-party scripts.
- `vercel.json` adds `Content-Security-Policy`, `X-Content-Type-Options`, `Referrer-Policy`,
  `X-Frame-Options` and a short cache policy for `/assets/*`.

---

## 9. What still needs a human before publishing

1. Decide the host and the canonical base URL (§2, §3).
2. Confirm the contact address printed on the page (`contact.email` in `site.config.mjs`).
3. Replace the previews with real captures when a device is available (§6).
4. Deploy, then run the Search Console steps in `docs/SEO_CHECKLIST.md`.
5. Only after the site is live, add its URL to the GitHub release notes.
