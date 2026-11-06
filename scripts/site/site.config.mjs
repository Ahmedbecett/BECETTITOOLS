/**
 * BECETTI TOOLS — single source of truth for the public landing page.
 *
 * Everything factual here was verified against the preserved artifact
 * `BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk` (parsed AndroidManifest.xml,
 * APK Signing Block, sha256sum). Do not edit these numbers without
 * re-verifying the APK — the site must never advertise unverified facts.
 *
 * Canonical URL: change `baseUrl` / `basePath` (CLI or env) and re-run
 *   node scripts/build-site.mjs
 */

export const repository = {
  owner: "Ahmedbecett",
  name: "BECETTITOOLS",
  url: "https://github.com/Ahmedbecett/BECETTITOOLS",
  issuesUrl: "https://github.com/Ahmedbecett/BECETTITOOLS/issues",
  licenseUrl: "https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/LICENSE",
};

export const app = {
  name: "BECETTI TOOLS",
  taglineEn: "Everything you need. One smart toolbox.",
  developer: "Ahmed Becetti",
  packageName: "com.becetti.tools",
  // Debug-variant install id (applicationIdSuffix ".debug" is part of v1.0.0 and is preserved).
  debugPackageName: "com.becetti.tools.debug",
  versionName: "1.0.0",
  versionCode: 1,
  minSdk: 24,
  targetSdk: 34,
  compileSdk: 34,
  minAndroidLabel: "Android 7.0",
  targetAndroidLabel: "Android 14 (API 34)",
  toolCount: 15,
  languages: ["en", "fr", "ar"],
  license: "MIT",
  releaseDate: "2026-11-05",
  buildType: "debug",
  signature: "APK Signature Scheme v2 (debug key — not a production/Play signing key)",
};

export const apk = {
  fileName: "BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  bytes: 88869105,
  // Decimal MB is what Android's file manager and folder listings show; MiB is the
  // binary figure used by `ls -h`, GitHub and this repo's own tooling. Both are the
  // same file: 88,869,105 bytes = 88.9 MB = 84.8 MiB. Show both, never one that
  // contradicts what the user sees on their phone.
  mb: "88.9 MB",
  mib: "84.8 MiB",
  sizeBoth: "88.9 MB (84.8 MiB)",
  sha256: "460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632",
  // ── One download target: the newest *published* release on GitHub ────────────
  // GitHub resolves /releases/latest to whichever release is marked "Latest", so this single
  // link keeps pointing at the current version without rebuilding the site. No other copy of
  // the APK is linked from the site (no raw-branch link, no blob page, no separate .sha256
  // file) — the SHA-256 below is displayed and copyable as text instead.
  //
  // v1.0.0 carries no uploaded *asset* (uploads.github.com is unreachable from the publish
  // environment), so the release notes on that page hold the file link. Once the APK is
  // attached to the release, switch this to the one-click asset URL:
  //   https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest/download/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk
  downloadUrl: "https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest",
  releaseUrl: "https://github.com/Ahmedbecett/BECETTITOOLS/releases/latest",
};

// ── Monetag site tags (in-page sponsored messages) ────────────────────────────────
// Every tag the publisher dashboard hands out is listed here. None of them is hard-coded into
// the HTML: the page ships this config as JSON and site.js injects the real <script> only after
// the visitor presses "Accept" in the consent bar. Default is declined, and the choice is stored
// locally under `becetti-ads-consent`.
//
// Only entries with `enabled: true` reach a page. Two live site tags on the same page compete for
// the same ad slots and double-count impressions, so exactly one is on; the others stay here,
// documented and one word away from being switched back on.
export const ads = {
  enabled: true,
  provider: "Monetag",
  consentKey: "becetti-ads-consent",
  // Landing pages only. The privacy policy pages stay ad-free.
  onPolicyPages: false,
  tags: [
    {
      provider: "Monetag",
      src: "https://nap5k.com/tag.min.js",
      zone: "12206566",
      enabled: true,
    },
    {
      provider: "Monetag",
      src: "https://quge5.com/88/tag.min.js",
      zone: "306773",
      enabled: false,
    },
  ],
};

/** Tags that are allowed to reach a page, in load order. */
export const activeAdTags = ads.tags.filter((t) => t.enabled);

export const contact = {
  // Already published by the project owner in the v1.0.0 release notes and earlier docs.
  email: "ahmedbecetti35@gmail.com",
  github: "https://github.com/Ahmedbecett",
};

/** Defaults can be overridden with --base-url / --base-path or SITE_BASE_URL / SITE_BASE_PATH. */
export const defaults = {
  // The URL already advertised in the GitHub release notes.
  baseUrl: "https://becetti-tools.vercel.app",
  // Empty = site served from the domain root (Vercel `website/` root directory).
  // Use "/BECETTITOOLS" for GitHub Pages project sites.
  basePath: "",
};

export function normaliseBase({ baseUrl, basePath }) {
  const url = String(baseUrl || defaults.baseUrl).replace(/\/+$/, "");
  let path = String(basePath ?? defaults.basePath).trim();
  if (path === "/" || path === "") path = "";
  if (path && !path.startsWith("/")) path = "/" + path;
  path = path.replace(/\/+$/, "");
  return { origin: url, basePath: path, base: url + path };
}
