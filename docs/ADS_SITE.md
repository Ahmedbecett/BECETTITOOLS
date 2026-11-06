# Ads on the landing page — Monetag service worker

Status: **hosted, not active.** The file is published at `/sw.js` but nothing registers it and no
page loads a third-party script, so the site's behaviour and its "no tracking" claims are still
accurate. This document records what was added, what it implies, and exactly what has to change
before it is switched on.

---

## 1. What was added

| Item | Value |
|---|---|
| File | `website/sw.js` (source of truth: `scripts/site/static/sw.js`) |
| Origin copy | the file supplied in the publisher dashboard, **byte-identical** |
| SHA-256 | `5e7510cb4f3a9cab8dd72ea855b513784f7a2bd1b4f0e8e432b92cf23559c421` |
| Size | 156 bytes |
| Network / host | Monetag — `5gvci.com` |
| Zone ID | `12206346` |
| Content | `self.options = {domain, zoneId}` + `importScripts('https://5gvci.com/act/files/service-worker.min.js?r=sw')` |

Contents (verbatim):

```js
self.options = {
    "domain": "5gvci.com",
    "zoneId": 12206346
}
self.lary = ""
importScripts('https://5gvci.com/act/files/service-worker.min.js?r=sw')
```

`scripts/build-site.mjs` copies it to the site root on every build, so it is never lost on a
rebuild. `scripts/verify-site.mjs` checks that it is present, identical to the vendored copy,
targets the right zone, and that its headers are scoped correctly (5 new checks).

---

## 2. Why it sits at the root

A service worker can only control pages under its own directory. Served from `/sw.js` its scope is
`/`, which is what the network requires; served from `/assets/js/sw.js` it would only control
`/assets/js/` and would be useless.

## 3. Headers (`website/vercel.json`)

A dedicated rule for `/sw.js` overrides the site-wide defaults:

| Header | Value | Reason |
|---|---|---|
| `Content-Security-Policy` | `default-src 'self' https://5gvci.com; script-src 'self' https://5gvci.com; connect-src 'self' https://5gvci.com; img-src 'self' data: https://5gvci.com` | the worker's own `importScripts` would otherwise be blocked by the page CSP |
| `Cache-Control` | `public, max-age=0, must-revalidate` | a cached worker would pin an old ad runtime |
| `Service-Worker-Allowed` | `/` | explicit origin-wide scope |

**The page CSP is unchanged**: `default-src 'self'; script-src 'self' 'unsafe-inline'; …`. No HTML
page is allowed to load a third-party script, and `verify-site.mjs` now fails the build if that
changes.

---

## 4. What it does *not* do yet

Hosting the file is only step one of a Monetag push/interstitial zone. Without the following, the
service worker is dead weight:

1. **Nothing registers it.** No page calls `navigator.serviceWorker.register('/sw.js')`, which is
   what the network's own page snippet (or SDK tag) does.
2. **No permission flow.** Push-style formats need the visitor to accept notifications; the site
   has no prompt, no consent screen and no opt-out — deliberately, until the wording is approved.
3. **No ad slot in the layout.** The pages have no container, no "Sponsored" label and no
   frequency cap.

## 5. Mandatory before activation

Shipping ads without these would make the site contradict itself — the privacy policy currently
states there is no analytics SDK, no third-party request and no advertising identifier.

- [ ] **Privacy policy** (`scripts/site/i18n/policy.mjs`, all three languages): add an
      "Advertising" section naming Monetag and `5gvci.com`, what is stored (push subscription,
      consent flag, technical identifiers), retention and the opt-out route.
- [ ] **Landing page copy**: the trust block ("No account, no tracking") and the FAQ answer
      "Will I see ads?" must describe the real behaviour, per locale.
- [ ] **Consent**: gate notification requests and personalised ads on an explicit accept;
      default must stay *declined*; refusal must not block the download.
- [ ] **Labels**: every ad unit visibly marked (e.g. "Sponsored"), never styled as a download
      button, never auto-redirecting, no fake counters or fake ratings.
- [ ] **Rate limiting**: keep it consistent with the app's `AdPolicy` (90 s minimum interval,
      3 per session, none on the calculator screen).
- [ ] **Re-run** `node scripts/build-site.mjs && node scripts/verify-site.mjs` and confirm
      243 checks still pass.

## 6. Rollback

```bash
rm scripts/site/static/sw.js website/sw.js      # or revert the commit
node scripts/build-site.mjs                     # /sw.js is gone from the output
```

Existing visitors keep a registered worker until it is unregistered; if a clean removal is needed,
ship a tiny `/sw.js` whose only job is `self.registration.unregister()`, wait a release cycle,
then delete it.

## 7. Security note

A registered service worker can intercept every request on the origin. That power belongs to the
network's script, not to this repository. Two consequences worth stating plainly:

- Keep the page CSP strict (`script-src 'self'`) so the worker's runtime cannot also be injected
  into pages.
- Treat `5gvci.com` as a trusted dependency of the site: if that host serves unexpected code, the
  browser executes it in an origin-wide scope. Review it before activation and consider
  self-hosting a pinned copy if the network permits.
