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
  mib: "84.8 MiB",
  sha256: "460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632",
  // GitHub has no release *asset* on v1.0.0 (uploads.github.com is unreachable from the
  // publish environment), so the preserved blob on `main` is the download of record.
  downloadUrl:
    "https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  checksumUrl:
    "https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk.sha256",
  blobUrl:
    "https://github.com/Ahmedbecett/BECETTITOOLS/blob/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk",
  releaseUrl: "https://github.com/Ahmedbecett/BECETTITOOLS/releases/tag/v1.0.0",
};

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
