# Ads on the site — Monetag tag + service worker

Status: **active on the three landing pages, consent-gated.** Two publisher artefacts are in play:

| Artefact | Zone | Host | State |
|---|---|---|---|
| Site tag (in-page sponsored messages) | `306773` | `quge5.com` | loaded **only after** the visitor presses "Accept ads" |
| Service worker (`/sw.js`) | `12206346` | `5gvci.com` | published at the root, **not registered** by any page |

A first visit fetches nothing third-party: the tag is stored as a JSON config block and `site.js`
injects the real `<script>` only after consent. Declining is remembered and leaves the page
byte-for-byte as it was before ads existed.

---

## 1. Assets

| Item | Value |
|---|---|
| Site tag | `https://quge5.com/88/tag.min.js` · `data-zone="306773"` · `async` · `data-cfasync="false"` |
| Config | `scripts/site/site.config.mjs` → `ads` |
| Service worker file | `website/sw.js` (source: `scripts/site/static/sw.js`) |
| SW SHA-256 | `5e7510cb4f3a9cab8dd72ea855b513784f7a2bd1b4f0e8e432b92cf23559c421` (156 bytes) |
| SW content | `self.options = {domain:"5gvci.com", zoneId:12206346}` + `importScripts('https://5gvci.com/act/files/service-worker.min.js?r=sw')` |

`build-site.mjs` copies the worker to the output root on every build, so a rebuild never loses it.

---

## 2. How the tag is injected

Each landing page ships the config, never the live script:

```html
<script type="application/json" id="becetti-ads">{"src":"https://quge5.com/88/tag.min.js","zone":"306773","consentKey":"becetti-ads-consent"}</script>
```

`assets/js/site.js` (deferred, ~1 KB) then:

1. no stored choice → reveals the consent bar (`hidden` removed), makes **no** request;
2. `accept` → stores `becetti-ads-consent=accept`, appends one `<script data-zone="306773" async data-cfasync="false">` to `<head>` (guarded by `#monetag-tag`, so never twice);
3. `decline` → stores `becetti-ads-consent=decline`, injects nothing;
4. footer **Ad settings** → clears the key and shows the bar again.

Privacy policy pages receive neither the config nor the bar, so they stay third-party free.

---

## 3. CSP (`website/vercel.json`)

| Path | Policy |
|---|---|
| `/becetti-tools/`, `/becetti-tools/(.*)` | `default-src 'self'; script-src 'self' 'unsafe-inline' https://quge5.com https://*.quge5.com; connect-src 'self' https://quge5.com https://*.quge5.com; img-src 'self' data: blob: https:; frame-src https:; media-src 'self' https:; worker-src 'self' blob:; object-src 'none'; form-action 'none'; base-uri 'self'; frame-ancestors 'self'` |
| everything else (`/privacy/`, `/`, `/404.html`) | unchanged strict `default-src 'self'; script-src 'self' 'unsafe-inline'` |
| `/sw.js` | `default-src 'self' https://5gvci.com; script-src 'self' https://5gvci.com; connect-src 'self' https://5gvci.com; img-src 'self' data: https://5gvci.com` + `Cache-Control: max-age=0` + `Service-Worker-Allowed: /` |

`img-src https:` and `frame-src https:` are scoped to the landing pages only: ad creatives are
served from rotating CDN hosts that cannot be enumerated in advance. Scripts stay limited to
`quge5.com`.

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
      `306773` and `quge5.com`, states the tag is consent-gated, records the local key
      `becetti-ads-consent`, and says the network's own policy covers its processing.
- [x] **Landing copy** — the FAQ answer "Will I see ads?" now separates the app (no SDK in v1.0.0)
      from the site (Monetag, consent-gated). The app-side trust block stays accurate because it
      describes the APK, not the page.
- [x] **Consent** — default is *declined*; the bar is server-rendered but hidden; accepting is the
      only path that loads anything; refusal never blocks or delays the download.
- [~] **Per-unit labels** — the in-page format renders its own overlay, so no "Sponsored" chip can
      be attached to an individual unit. The bar and FAQ disclose the source, and the ad never
      mimics the download button. For explicit per-unit labelling, use the network's
      banner/rectangle format inside a labelled container.
- [ ] **Rate limiting** — controlled by Monetag's dashboard frequency settings for this format, not
      by page code. The app's own `AdPolicy` (90 s / 3 per session) is untouched.
- [x] **Quality gate** — `verify-site.mjs`: 250 checks, 0 failed (7 new: config on all three
      landing pages, no hard-coded tag, privacy pages clean, one bar per page, landing CSP allows
      `quge5.com`, strict CSP untouched, injection only after accept).

---

## 6. Verifying it with Monetag

Monetag's checker loads a page and looks for its tag in the DOM. Because the tag is injected only
after consent, an automated check may report "tag not found". Options, best first:

1. Verify manually: open the page, press **Accept ads**, confirm the network starts serving.
2. Temporarily load the tag unconditionally for one deployment, let the dashboard verify, then
   restore the gate. Do **not** leave it open — the privacy policy states nothing loads before
   consent.

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
