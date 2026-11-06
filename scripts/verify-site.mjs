#!/usr/bin/env node
/**
 * Quality gate for the built landing page.
 *
 *   node scripts/verify-site.mjs
 *
 * Checks links, metadata, structured data, hreflang reciprocity, accessibility
 * basics, "no fake facts" rules, and re-hashes the preserved APK to confirm the
 * checksum published on the site is still the checksum of the file in the repo.
 */

import { readFile, readdir, stat } from "node:fs/promises";
import { existsSync } from "node:fs";
import { createHash } from "node:crypto";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { apk, app, repository } from "./site/site.config.mjs";
import { locales, pages } from "./site/i18n/pages.mjs";
import { tools } from "./site/i18n/tools.mjs";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..");
const siteRoot = path.join(repoRoot, "website");

let failures = 0;
let warnings = 0;
const results = [];

function check(name, ok, detail = "") {
  if (ok) results.push({ level: "pass", name, detail });
  else {
    failures++;
    results.push({ level: "fail", name, detail });
  }
}

function warn(name, ok, detail = "") {
  if (!ok) {
    warnings++;
    results.push({ level: "warn", name, detail });
  } else results.push({ level: "pass", name, detail });
}

const rel = (p) => path.relative(repoRoot, p);
const attrAll = (html, re) => [...html.matchAll(re)];
const unescape = (s) =>
  s
    .replace(/&amp;/g, "&")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'");

async function walk(dir, out = []) {
  for (const entry of await readdir(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) await walk(full, out);
    else out.push(full);
  }
  return out;
}

function resolveTarget(href, fromFile) {
  const clean = href.split("#")[0].split("?")[0];
  if (!clean) return fromFile; // pure fragment
  if (/^(https?:|mailto:|tel:|data:)/.test(clean)) return null;
  let target;
  if (clean.startsWith("/")) {
    // Site-relative path: the site root is website/ (Vercel root directory).
    target = path.join(siteRoot, clean);
  } else {
    target = path.resolve(path.dirname(fromFile), clean);
  }
  if (clean.endsWith("/") || !path.extname(target)) target = path.join(target, "index.html");
  return target;
}

async function main() {
  const files = await walk(siteRoot);
  const htmlFiles = files.filter((f) => f.endsWith(".html"));
  check("Built site exists", htmlFiles.length >= 7, `${htmlFiles.length} HTML documents`);

  const expectedPages = [
    "becetti-tools/index.html",
    "becetti-tools/fr/index.html",
    "becetti-tools/ar/index.html",
    "privacy/index.html",
    "privacy/fr/index.html",
    "privacy/ar/index.html",
    "index.html",
    "404.html",
  ];
  for (const p of expectedPages) {
    check(`Page present: ${p}`, existsSync(path.join(siteRoot, p)));
  }

  const docs = new Map();
  for (const f of htmlFiles) docs.set(f, await readFile(f, "utf8"));

  /* ------------------------------ 1. Links ------------------------------ */
  let brokenLinks = [];
  let externalRequests = [];
  for (const [file, html] of docs) {
    for (const m of attrAll(html, /\s(?:href|src)="([^"]+)"/g)) {
      const raw = unescape(m[1]);
      const target = resolveTarget(raw, file);
      if (!target) {
        if (/^http/.test(raw) && /\.(css|js|png|jpe?g|svg|woff2?|ttf)$/i.test(raw)) {
          externalRequests.push(`${rel(file)} → ${raw}`);
        }
        continue;
      }
      if (!existsSync(target)) brokenLinks.push(`${rel(file)} → ${raw}`);
    }
  }
  check("No broken internal links", brokenLinks.length === 0, brokenLinks.slice(0, 6).join("; "));
  check("No third-party asset requests", externalRequests.length === 0, externalRequests.slice(0, 4).join("; "));

  /* --------------------------- 2. Metadata per page --------------------------- */
  const titles = new Map();
  for (const loc of locales) {
    const landing = path.join(siteRoot, pages[loc].route.landing, "index.html");
    const policy = path.join(siteRoot, pages[loc].route.privacy, "index.html");
    for (const [kind, file] of [
      ["landing", landing],
      ["privacy", policy],
    ]) {
      const html = docs.get(file);
      const t = pages[loc];
      const label = `${loc}/${kind}`;
      if (!html) {
        check(`${label}: document present`, false);
        continue;
      }
      const title = /<title>([^<]+)<\/title>/.exec(html)?.[1] || "";
      const desc = /<meta name="description" content="([^"]*)"/.exec(html)?.[1] || "";
      const canonical = /<link rel="canonical" href="([^"]+)"/.exec(html)?.[1] || "";
      check(`${label}: title 25-75 chars`, title.length >= 25 && title.length <= 75, `${title.length}`);
      check(`${label}: description 80-330 chars`, desc.length >= 80 && desc.length <= 330, `${desc.length}`);
      check(`${label}: unique title`, !titles.has(title), titles.get(title) || "");
      titles.set(title, label);
      check(`${label}: canonical is https`, /^https:\/\//.test(canonical), canonical);
      check(`${label}: canonical ends with expected path`, canonical.endsWith(pages[loc].route[kind === "landing" ? "landing" : "privacy"]), canonical);
      check(`${label}: robots index,follow`, /name="robots" content="index, follow/.test(html));
      check(`${label}: og:title/description/image/url`, ["og:title", "og:description", "og:image", "og:url"].every((k) => html.includes(`property="${k}"`)));
      check(`${label}: twitter card`, html.includes('name="twitter:card"') && html.includes('name="twitter:image"'));
      check(`${label}: html lang+dir`, html.includes(`<html lang="${t.htmlLang}" dir="${t.dir}">`));
      check(`${label}: skip link`, html.includes('class="skip-link"'));
      check(`${label}: viewport`, html.includes('name="viewport"'));

      // hreflang reciprocity
      const alts = attrAll(html, /<link rel="alternate" hreflang="([^"]+)" href="([^"]+)">/g).map((m) => m[1]);
      check(
        `${label}: hreflang en/fr/ar/x-default`,
        ["en", "fr", "ar", "x-default"].every((h) => alts.includes(h)),
        alts.join(",")
      );

      // JSON-LD
      const jsonLd = [...html.matchAll(/<script type="application\/ld\+json">([\s\S]*?)<\/script>/g)].map((m) => m[1]);
      check(`${label}: exactly one JSON-LD block`, jsonLd.length === 1, `${jsonLd.length}`);
      let parsed = null;
      try {
        parsed = JSON.parse(jsonLd[0]);
        check(`${label}: JSON-LD parses`, true);
      } catch (e) {
        check(`${label}: JSON-LD parses`, false, String(e).slice(0, 80));
      }
      if (parsed) {
        const graph = parsed["@graph"] || [];
        const types = graph.flatMap((n) => (Array.isArray(n["@type"]) ? n["@type"] : [n["@type"]]));
        check(`${label}: @context schema.org`, parsed["@context"] === "https://schema.org");
        check(`${label}: WebSite node`, types.includes("WebSite"));
        check(`${label}: WebPage node`, types.includes("WebPage"));
        check(`${label}: BreadcrumbList node`, types.includes("BreadcrumbList"));
        if (kind === "landing") {
          check(`${label}: SoftwareApplication node`, types.includes("SoftwareApplication"));
          check(`${label}: MobileApplication type`, types.includes("MobileApplication"));
          check(`${label}: FAQPage node`, types.includes("FAQPage"));
          const appNode = graph.find((n) => Array.isArray(n["@type"]) && n["@type"].includes("SoftwareApplication"));
          check(`${label}: app downloadUrl set`, appNode?.downloadUrl === apk.downloadUrl, appNode?.downloadUrl);
          check(`${label}: app softwareVersion`, appNode?.softwareVersion === app.versionName);
          check(`${label}: app offers price 0`, appNode?.offers?.price === "0");
          check(`${label}: app operatingSystem Android`, /Android/.test(appNode?.operatingSystem || ""));
        }
      }

      // No unverifiable claims
      const banned = [/aggregateRating/i, /ratingValue/i, /downloadCount/i, /installCount/i, /"reviewCount"/i];
      check(`${label}: no fake ratings/counters in JSON-LD`, !banned.some((r) => r.test(jsonLd[0])));
      check(`${label}: no fake ratings/counters in copy`, !/(million|من المستخدمين)/i.test(html));
    }
  }

  /* --------------------------- 3. Tool completeness --------------------------- */
  for (const loc of locales) {
    const html = docs.get(path.join(siteRoot, pages[loc].route.landing, "index.html"));
    const missing = tools.filter((tool) => !html.includes(escapeHtml(tool.name[loc])));
    check(`${loc}: all 15 tool names present`, missing.length === 0, missing.map((m) => m.id).join(","));
    const anchors = tools.filter((tool) => !html.includes(`id="tool-${tool.id}"`));
    check(`${loc}: all 15 tool anchors present`, anchors.length === 0, anchors.map((a) => a.id).join(","));
  }

  /* ----------------------------- 4. Accessibility ----------------------------- */
  for (const [file, html] of docs) {
    const imgs = attrAll(html, /<img\b[^>]*>/g).map((m) => m[0]);
    const noAlt = imgs.filter((tag) => !/\salt=/.test(tag));
    check(`${rel(file)}: all images have alt`, noAlt.length === 0, noAlt.slice(0, 2).join(" "));
    const noDim = imgs.filter((tag) => !(/\swidth=/.test(tag) && /\sheight=/.test(tag)));
    warn(`${rel(file)}: images declare width/height`, noDim.length === 0, `${noDim.length} without dimensions`);
    const buttons = attrAll(html, /<button\b[^>]*>([\s\S]*?)<\/button>/g).map((m) => ({ tag: m[0], text: m[1] }));
    const unnamed = buttons.filter(
      (b) => !/(aria-label=|title=)/.test(b.tag) && !b.text.replace(/<[^>]+>/g, "").trim()
    );
    check(`${rel(file)}: buttons have accessible names`, unnamed.length === 0, unnamed.slice(0, 2).map((b) => b.tag).join(" "));
    check(`${rel(file)}: single h1`, (html.match(/<h1[\s>]/g) || []).length <= 1);
    check(`${rel(file)}: headings not skipped`, headingOrderOk(html), "");
    const tables = attrAll(html, /<table\b[^>]*>/g).length;
    const captions = attrAll(html, /<caption\b/g).length;
    check(`${rel(file)}: tables have captions`, tables === captions, `${captions}/${tables}`);
    check(`${rel(file)}: external links rel=noopener`, externalLinksOk(html));
    check(`${rel(file)}: no inline event handlers`, !/\son(click|load|error|mouseover)=/i.test(html));
  }

  /* ------------------------------- 5. SEO files ------------------------------- */
  const robots = await readFile(path.join(siteRoot, "robots.txt"), "utf8");
  check("robots.txt allows crawling", /User-agent: \*\s*\nAllow: \//.test(robots));
  check("robots.txt references sitemap.xml", /Sitemap: https:\/\/\S+\/sitemap\.xml/.test(robots));

  const sitemap = await readFile(path.join(siteRoot, "sitemap.xml"), "utf8");
  const locs = [...sitemap.matchAll(/<loc>([^<]+)<\/loc>/g)].map((m) => m[1]);
  check("sitemap has 6 URLs (3 landing + 3 policy)", locs.length === 6, `${locs.length}`);
  check("sitemap uses xhtml hreflang", sitemap.includes('xmlns:xhtml="http://www.w3.org/1999/xhtml"') && sitemap.includes("<xhtml:link"));
  check("sitemap includes all locales", ["/becetti-tools/", "/becetti-tools/fr/", "/becetti-tools/ar/", "/privacy/"].every((p) => locs.some((l) => l.endsWith(p))));
  check("sitemap declares lastmod", sitemap.includes("<lastmod>"));

  const manifest = JSON.parse(await readFile(path.join(siteRoot, "site.webmanifest"), "utf8"));
  check("manifest name/short_name", manifest.name?.includes(app.name) && manifest.short_name === app.name);
  check("manifest icons exist", manifest.icons.every((i) => existsSync(path.join(siteRoot, i.src))));

  const vercel = JSON.parse(await readFile(path.join(siteRoot, "vercel.json"), "utf8"));
  check("vercel.json redirects / to the landing page", vercel.redirects?.[0]?.destination === "/becetti-tools/");
  check("vercel.json sets security headers", vercel.headers?.some((h) => h.headers.some((x) => x.key === "Content-Security-Policy")));

  /* --------------------------- 6. Artifact integrity --------------------------- */
  const apkPath = path.join(repoRoot, apk.fileName);
  if (existsSync(apkPath)) {
    const buf = await readFile(apkPath);
    const hash = createHash("sha256").update(buf).digest("hex");
    check("APK sha256 on site matches the repository file", hash === apk.sha256, hash);
    check("APK byte size on site matches the repository file", buf.length === apk.bytes, `${buf.length}`);
    const landingHtml = docs.get(path.join(siteRoot, "becetti-tools/index.html"));
    check("APK sha256 appears verbatim on the landing page", landingHtml.includes(apk.sha256));
  } else {
    warn("APK present in repository", false, "file not found — checksum could not be re-verified");
  }

  // The site must never ship the APK itself.
  const binaries = files.filter((f) => /\.(apk|aab|zip)$/i.test(f));
  check("No APK/AAB bundled in the site output", binaries.length === 0, binaries.join(","));

  // Guard against accidentally publishing a build output or secret.
  const secrets = files.filter((f) => /\.(jks|keystore|env)$/i.test(f) || /local\.properties$/.test(f));
  check("No keystore/secret files in the site output", secrets.length === 0, secrets.join(","));

  /* ---------------------------------- report ---------------------------------- */
  const pass = results.filter((r) => r.level === "pass").length;
  const fail = results.filter((r) => r.level === "fail");
  const warns = results.filter((r) => r.level === "warn");

  for (const r of fail) console.log(`FAIL  ${r.name}${r.detail ? ` — ${r.detail}` : ""}`);
  for (const r of warns) console.log(`WARN  ${r.name}${r.detail ? ` — ${r.detail}` : ""}`);

  console.log(
    `\n${pass} checks passed · ${fail.length} failed · ${warns.length} warnings · ` +
      `${htmlFiles.length} pages, ${(files.length / 1).toFixed(0)} files total`
  );
  if (fail.length) process.exit(1);
}

function escapeHtml(s) {
  return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;");
}

function headingOrderOk(html) {
  const levels = [...html.matchAll(/<h([1-6])[\s>]/g)].map((m) => Number(m[1]));
  let prev = 0;
  for (const level of levels) {
    if (prev && level > prev + 1) return false;
    prev = level;
  }
  return true;
}

function externalLinksOk(html) {
  const links = [...html.matchAll(/<a\b[^>]*href="(https?:\/\/[^"]+)"[^>]*>/g)];
  return links
    .filter((m) => !/github\.com|githubusercontent\.com/.test(m[1]) === false || true)
    .every((m) => /rel="[^"]*noopener/.test(m[0]) || !/_blank/.test(m[0]));
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});

/* keep the import used, so a future refactor does not silently drop repository checks */
void repository;
