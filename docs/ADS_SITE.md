# Ads on the site — Monetag tag + service worker

Status: **active on the three landing pages, loaded on every page view (`ads.requireConsent = false`).**
Two publisher artefacts are in play:

| Artefact | Zone | Host | State |
|---|---|---|---|
| Site tag (in-page sponsored messages) | `12206566` | `nap5k.com` | loaded on every page view, no gate |
| Site tag, earlier zone | `306773` | `quge5.com` | kept in the config, `enabled: false` |
| Service worker (`/sw.js`) | `12206346` | `5gvci.com` | published at the root, **not registered** by any page |

The tag is still not pasted into the HTML: each page ships a JSON config block and `site.js`
injects the real `<script>` from it. That indirection is what makes the whole thing switchable —
`ads.requireConsent = true` restores the consent bar, the stored choice and the footer control
without touching a template, and `ads.enabled = false` removes the tag entirely.

---

## 1. Assets

| Item | Value |
|---|---|
| Site tag | `https://nap5k.com/tag.min.js` · `data-zone="12206566"` · `async` · `data-cfasync="false"` |
| Config | `scripts/site/site.config.mjs` → `ads` |
| Service worker file | `website/sw.js` (source: `scripts/site/static/sw.js`) |
| SW SHA-256 | `5e7510cb4f3a9cab8dd72ea855b513784f7a2bd1b4f0e8e432b92cf23559c421` (156 bytes) |
| SW content | `self.options = {domain:"5gvci.com", zoneId:12206346}` + `importScripts('https://5gvci.com/act/files/service-worker.min.js?r=sw')` |

`build-site.mjs` copies the worker to the output root on every build, so a rebuild never loses it.

**Zones are a list, not a constant.** `ads.tags` accepts every tag the dashboard hands out; only
entries with `enabled: true` reach the page. Zone `306773` (`quge5.com`) is retained but switched
off: two live site tags on one page compete for the same slots and double-count impressions. Flip
`enabled` on that entry — and only that entry — if the older zone should come back.

---

## 2. How the tag is injected

Each landing page ships the config, never the live script:

```html
<script type="application/json" id="becetti-ads">{"tags":[{"src":"https://nap5k.com/tag.min.js","zone":"12206566"}],"auto":true}</script>
```

`assets/js/site.js` (deferred, ~1 KB) then:

1. reads the config; with `auto: true` there is no bar and no stored choice to consult;
2. `auto: true` in the config → appends one `<script data-zone="12206566" async data-cfasync="false">`
   per enabled entry in `ads.tags` immediately (each guarded by `#monetag-tag-<zone>`, so never twice);
3. if the config carries a `consentKey` instead, the older consent path is used unchanged: bar first,
   tag only on **Accept**, choice stored under that key, footer **Ad settings** to change it.

Privacy policy pages receive neither the config nor the bar, so they stay third-party free.

---

## 3. CSP (`website/vercel.json`)

| Path | Policy |
|---|---|
| `/becetti-tools/`, `/becetti-tools/(.*)`, `/ads-verify/` | `default-src 'self'; script-src 'self' 'unsafe-inline' https:; connect-src 'self' https:; img-src 'self' data: blob: https:; frame-src https:; media-src 'self' https:; worker-src 'self' blob:; object-src 'none'; form-action 'none'; base-uri 'self'; frame-ancestors 'self'` |
| everything else (`/privacy/`, `/`, `/404.html`) | unchanged strict `default-src 'self'; script-src 'self' 'unsafe-inline'` |
| `/sw.js` | `default-src 'self' https://5gvci.com; script-src 'self' https://5gvci.com; connect-src 'self' https://5gvci.com; img-src 'self' data: https://5gvci.com` + `Cache-Control: max-age=0` + `Service-Worker-Allowed: /` |

`img-src https:`, `frame-src https:` **and** `script-src https:` are scoped to the ad-bearing pages
only: the tag pulls its runtime and creatives from hosts that cannot be enumerated in advance, and
pinning one host alone silently blocked part of the ad chain. Everything else on those pages
stays locked (`object-src 'none'`, `form-action 'none'`, `base-uri 'self'`, `frame-ancestors 'self'`),
and `/`, `/privacy/`, `/404.html` and `/sw.js` keep the original strict policy — asserted by
`scripts/verify-site.mjs`.

---

## 4. Why the worker sits at the root

A service worker can only control pages under its own directory. Served from `/sw.js` its scope is
`/`, which is what the network requires; served from `/assets/js/sw.js` it would only control
`/assets/js/` and would be useless.

It is **not registered** by the site. The site tag is what registers a worker once a visitor
consents; the `/sw.js` file simply has to exist at the root when it does.

---

## 5. Disclosure shipped with the change

- [x] **Privacy policy** — new §14 "Advertising on this website" in EN/FR/AR: names Monetag, zone
      `12206566` and `nap5k.com`, states plainly that the tag loads on every page view, explains
      what Monetag may store, and tells the visitor to block third-party scripts to opt out.
- [x] **Landing copy** — the FAQ answer "Will I see ads?" now separates the app (no SDK in v1.0.0)
      from the site (Monetag, on every page load). The app-side trust block stays accurate because
      it describes the APK, not the page.
- [x] **Opt-out** — ad blockers and `nap5k.com` domain blocking are stated as the way out; the page
      and the APK download stay fully usable without the tag, and no wall ever covers the download.
- [~] **Per-unit labels** — the in-page format renders its own overlay, so no "Sponsored" chip can
      be attached to an individual unit. The bar and FAQ disclose the source, and the ad never
      mimics the download button. For explicit per-unit labelling, use the network's
      banner/rectangle format inside a labelled container.
- [ ] **Rate limiting** — controlled by Monetag's dashboard frequency settings for this format, not
      by page code. The app's own `AdPolicy` (90 s / 3 per session) is untouched.
- [x] **Quality gate** — `verify-site.mjs`: 250 checks, 0 failed (7 new: config on all three
      landing pages, no hard-coded tag, privacy pages clean, one bar per page, landing CSP allows
      `nap5k.com`, strict CSP elsewhere untouched, injection only after accept).

---

## 6. Verifying it with Monetag

Monetag's checker loads a page and looks for its tag in the DOM. The landing pages inject it from
`site.js` on every visit, which a JavaScript-capable checker sees but a plain HTML fetch does not — so
the dedicated page below remains the reliable target.

**Use the verification page:** `https://becetti-tools.vercel.app/ads-verify/`

- unlinked, `noindex, nofollow, noarchive`, excluded from `sitemap.xml`, not in `robots.txt`;
- loads the tag immediately (no consent step, no localStorage read), so any crawler or browser sees
  `<script src="https://nap5k.com/tag.min.js" data-zone="12206566">` in the DOM;
- shows a one-line status: "injected — the Monetag script is in this page".

Paste that URL into the "add site / verify" field in the Monetag dashboard. If the dashboard accepts
it, the zone is verified and the gated pages keep working exactly as designed.

Same page as a support tool: if ads appear on `/ads-verify/` but not on `/becetti-tools/`, the tag is
fine and the visitor simply has not pressed **Accept ads** (or has an ad blocker). If ads appear
nowhere, the zone is still in moderation or has no demand for that traffic — nothing on the site can
fix that; wait for approval in the dashboard.

Console check on a landing page after pressing **Accept ads**:

```js
document.getElementById("monetag-tag-12206566")   // <script src="https://nap5k.com/tag.min.js">
document.documentElement.dataset.ads              // "loaded"
```

---

## 7. Rollback

```bash
# ads off, keep the worker
#   scripts/site/site.config.mjs → ads.enabled = false   (then rebuild)
# everything off
rm scripts/site/static/sw.js website/sw.js
node scripts/build-site.mjs
```

Visitors who already accepted keep the stored key until they press **Ad settings**; a page without
the config block simply never injects anything. A registered worker survives until it is
unregistered — ship a `/sw.js` that only calls `self.registration.unregister()` for one cycle if a
clean removal is required.

---

## 8. Security note

A registered service worker can intercept every request on the origin, and the site tag executes
third-party code on the landing pages. Guardrails kept in place:

- the tag is only ever injected by our own script, after an explicit click;
- the strict CSP still governs the privacy pages, `/`, 404 and `/sw.js`;
- `object-src 'none'`, `form-action 'none'` and `base-uri 'self'` remain everywhere;
- the tag never replaces, hides or delays the download button — and the download link still points
  at the latest GitHub release, not at an ad host.
