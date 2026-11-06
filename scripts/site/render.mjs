/**
 * Static HTML renderer for the BECETTI TOOLS landing page and privacy policy.
 * No framework, no client-side rendering: every page is complete HTML on its own,
 * which is what search engines and link previews actually read.
 */

import { repository, app, apk, contact, ads } from "./site.config.mjs";
import { categories, tools } from "./i18n/tools.mjs";
import { pages, locales } from "./i18n/pages.mjs";
import { privacy } from "./i18n/privacy.mjs";

const BUILD_DATE = process.env.SITE_BUILD_DATE || "2026-11-06";

/** Small stroke icon set (24×24, currentColor) — no icon font, no external request. */
const ICONS = {
  calculator: '<rect x="4" y="3" width="16" height="18" rx="3"/><path d="M8 7h8M8 12h.01M12 12h.01M16 12h.01M8 16h.01M12 16h.01M16 16h.01"/>',
  ruler: '<path d="M3 15.5 8.5 21 21 8.5 15.5 3 3 15.5Z"/><path d="M7 11.5l2 2M10 8.5l2 2M13 5.5l2 2"/>',
  exchange: '<path d="M4 8h13l-3-3M20 16H7l3 3"/>',
  percent: '<path d="M19 5 5 19"/><circle cx="7.5" cy="7.5" r="2.5"/><circle cx="16.5" cy="16.5" r="2.5"/>',
  calendar: '<rect x="3" y="5" width="18" height="16" rx="3"/><path d="M8 3v4M16 3v4M3 11h18"/>',
  qr: '<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><path d="M14 14h3v3h-3zM19 19h2M19 14h2v2"/>',
  scan: '<path d="M4 9V6a2 2 0 0 1 2-2h3M15 4h3a2 2 0 0 1 2 2v3M20 15v3a2 2 0 0 1-2 2h-3M9 20H6a2 2 0 0 1-2-2v-3M4 12h16"/>',
  key: '<circle cx="8" cy="8" r="4"/><path d="M11 11l9 9M17 17l2-2M14.5 14.5l2-2"/>',
  gauge: '<path d="M4 17a8 8 0 1 1 16 0"/><path d="M12 13l4-3"/><circle cx="12" cy="17" r="1.6"/>',
  imagePdf: '<rect x="3" y="4" width="12" height="12" rx="2.5"/><path d="M3 12l3.5-3.5L13 15"/><path d="M17 8h3.5v12.5H8V17"/>',
  pdf: '<path d="M6 3h8l4 4v14H6z"/><path d="M14 3v4h4"/><path d="M9 13h6M9 17h4"/>',
  ocr: '<circle cx="11" cy="11" r="6"/><path d="M15.5 15.5 21 21M8.5 9h5M8.5 12h3"/>',
  compress: '<path d="M4 9V5h4M20 9V5h-4M4 15v4h4M20 15v4h-4"/><path d="M9 12h6"/>',
  text: '<path d="M4 6h16M4 11h10M4 16h13M4 21h7"/>',
  note: '<path d="M6 3h9l4 4v14H6z"/><path d="M15 3v4h4"/><path d="M9 12h7M9 16h5"/>',
  image: '<rect x="3" y="4" width="18" height="16" rx="3"/><circle cx="8.5" cy="9.5" r="1.6"/><path d="M4 17l5-4 4 3 3-2 4 3"/>',
  shield: '<path d="M12 3 5 6v6c0 4.2 3 7.4 7 9 4-1.6 7-4.8 7-9V6l-7-3Z"/><path d="m9 12 2 2 4-4"/>',
  document: '<path d="M7 3h7l4 4v14H7z"/><path d="M14 3v4h4M10 12h6M10 16h6"/>',
  tools: '<path d="M4 20 9.5 14.5M14 4.5l5.5 5.5M4 4l6 6M14 20l6-6"/><circle cx="17" cy="7" r="2.2"/><circle cx="7" cy="17" r="2.2"/>',
  github: '<path d="M9 19c-4 1.2-4-2-5.5-2.5M15 21v-3.6c0-1 .1-1.4-.5-2 2.3-.3 4.5-1.2 4.5-5.2A4 4 0 0 0 17.7 7c.1-.9.1-1.9-.2-2.7 0 0-1.2-.3-3.4 1.3a11 11 0 0 0-5.4 0C6.5 4 5.3 4.3 5.3 4.3c-.3.8-.3 1.8-.2 2.7A4 4 0 0 0 4 10.2c0 4 2.2 4.9 4.5 5.2-.5.5-.6 1.1-.5 2V21"/>',
  mail: '<rect x="3" y="5" width="18" height="14" rx="3"/><path d="m4 7 8 6 8-6"/>',
  download: '<path d="M12 3v12"/><path d="m7 11 5 5 5-5"/><path d="M4 20h16"/>',
  check: '<path d="m4 12 5 5L20 6"/>',
  arrow: '<path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.5 2.6 3.8 5.7 3.8 9S14.5 18.4 12 21c-2.5-2.6-3.8-5.7-3.8-9S9.5 5.6 12 3Z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v5M12 8h.01"/>',
  copy: '<rect x="9" y="9" width="11" height="11" rx="2.5"/><path d="M15 5H6a2 2 0 0 0-2 2v9"/>',
  sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M2 12h2M20 12h2M5 5l1.5 1.5M17.5 17.5 19 19M19 5l-1.5 1.5M6.5 17.5 5 19"/>',
  moon: '<path d="M20 14.5A8.5 8.5 0 0 1 9.5 4a8.5 8.5 0 1 0 10.5 10.5Z"/>',
  lock: '<rect x="4" y="10" width="16" height="11" rx="3"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/>',
};

function icon(name, extraClass = "") {
  const body = ICONS[name] || ICONS.tools;
  return `<svg class="icon${extraClass ? " " + extraClass : ""}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false">${body}</svg>`;
}

const esc = (s) =>
  String(s)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");

/** Depth of a page relative to the site root, used for relative asset/link URLs. */
function prefixFor(pagePath) {
  const depth = pagePath.split("/").filter(Boolean).length;
  return "../".repeat(depth);
}

export function createUrls({ base }) {
  const abs = (rel = "") => base + "/" + rel.replace(/^\/+/, "");
  const landing = (loc) => pages[loc].route.landing;
  const privacyPath = (loc) => pages[loc].route.privacy;
  return {
    base,
    abs,
    landingPath: landing,
    privacyPath,
    pathFor: (kind, loc) => (kind === "privacy" ? privacyPath(loc) : landing(loc)),
  };
}

const NAV_ITEMS = ["tools", "previews", "install", "verify", "privacy", "faq", "roadmap", "contact"];
const NAV_TARGET = {
  tools: "#tools",
  previews: "#preview",
  install: "#install",
  verify: "#verify",
  privacy: "#privacy",
  faq: "#faq",
  roadmap: "#roadmap",
  contact: "#contact",
};

function head({
  t,
  urls,
  pagePath,
  canonicalPath,
  title,
  description,
  keywords = [],
  ogImage,
  jsonLd,
  robots = "index, follow, max-image-preview:large, max-snippet:-1",
  alternates = [],
  isPolicy = false,
  adsConfig = null,
}) {
  const prefix = prefixFor(pagePath);
  const canonical = urls.abs(canonicalPath);
  const altLinks = alternates
    .map(
      (a) =>
        `<link rel="alternate" hreflang="${esc(a.hreflang)}" href="${esc(urls.abs(a.path))}">`
    )
    .join("\n  ");

  return `<!doctype html>
<html lang="${esc(t.htmlLang)}" dir="${esc(t.dir)}">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${esc(title)}</title>
  <meta name="description" content="${esc(description)}">
${keywords.length ? `  <meta name="keywords" content="${esc(keywords.join(", "))}">\n` : ""}  <meta name="author" content="${esc(app.developer)}">
  <meta name="robots" content="${esc(robots)}">
  <meta name="googlebot" content="${esc(robots)}">
  <link rel="canonical" href="${esc(canonical)}">
  ${altLinks}
  <meta property="og:type" content="website">
  <meta property="og:site_name" content="${esc(app.name)}">
  <meta property="og:title" content="${esc(title)}">
  <meta property="og:description" content="${esc(description)}">
  <meta property="og:url" content="${esc(canonical)}">
  <meta property="og:locale" content="${esc(t.locale)}">
${locales
  .filter((l) => l !== t.htmlLang)
  .map((l) => `  <meta property="og:locale:alternate" content="${esc(pages[l].locale)}">`)
  .join("\n")}
  <meta property="og:image" content="${esc(urls.abs("assets/img/og-image.png"))}">
  <meta property="og:image:type" content="image/png">
  <meta property="og:image:width" content="1200">
  <meta property="og:image:height" content="630">
  <meta property="og:image:alt" content="${esc(t.meta.ogImageAlt)}">
  <meta name="twitter:card" content="summary_large_image">
  <meta name="twitter:title" content="${esc(title)}">
  <meta name="twitter:description" content="${esc(description)}">
  <meta name="twitter:image" content="${esc(urls.abs("assets/img/og-image.png"))}">
  <meta name="twitter:image:alt" content="${esc(t.meta.ogImageAlt)}">
  <meta name="theme-color" content="#172235" media="(prefers-color-scheme: dark)">
  <meta name="theme-color" content="#f7f8fa" media="(prefers-color-scheme: light)">
  <link rel="icon" href="${prefix}assets/img/favicon.svg" type="image/svg+xml">
  <link rel="icon" href="${prefix}assets/img/favicon-32.png" sizes="32x32" type="image/png">
  <link rel="apple-touch-icon" href="${prefix}assets/img/apple-touch-icon.png">
  <link rel="manifest" href="${prefix}site.webmanifest">
  <link rel="stylesheet" href="${prefix}assets/css/site.css">
  <script>${inlineThemeInit()}</script>
${adsConfig ? `  <script type="application/json" id="becetti-ads">${JSON.stringify(adsConfig)}</script>\n` : ""}  <script type="application/ld+json">
${JSON.stringify(jsonLd, null, 2)}
  </script>
</head>
<body>
  <a class="skip-link" href="#main">${esc(t.nav.skip)}</a>`;
}

function inlineThemeInit() {
  // Runs before first paint so the theme does not flash. Kept tiny and inline on purpose.
  return `(function(){try{var s=localStorage.getItem("becetti-theme")||"system";var d=window.matchMedia("(prefers-color-scheme: dark)").matches;document.documentElement.dataset.theme=s==="system"?(d?"dark":"light"):s;document.documentElement.dataset.themePref=s;}catch(e){}})();`;
}

function header({ t, urls, loc, pagePath, isPolicy }) {
  const prefix = prefixFor(pagePath);
  const landingFor = (l) => prefix + urls.landingPath(l);
  const privacyFor = (l) => prefix + urls.privacyPath(l);
  const langLinks = locales
    .map((l) => {
      const target = isPolicy ? privacyFor(l) : landingFor(l);
      const current = l === loc;
      return `<a href="${target}" hreflang="${esc(pages[l].htmlLang)}" lang="${esc(pages[l].htmlLang)}"${
        current ? ' aria-current="page"' : ""
      }>${l.toUpperCase()}</a>`;
    })
    .join("");

  return `
  <header class="site-header">
    <div class="wrap header-inner">
      <a class="brand" href="${prefix}${urls.landingPath(loc)}">
        <img src="${prefix}assets/img/logo.svg" alt="" width="34" height="34" class="brand-mark">
        <span class="brand-text">
          <span class="brand-name">${esc(app.name)}</span>
          <span class="brand-tag">${esc(t.hero.tagline)}</span>
        </span>
      </a>
      <nav class="nav" aria-label="${esc(t.nav.label)}">
        ${isPolicy
          ? `<a href="${prefix}${urls.landingPath(loc)}">${esc(t.nav.tools)}</a><a href="${prefix}${urls.landingPath(loc)}#install">${esc(t.nav.install)}</a>`
          : NAV_ITEMS.map(
              (k) =>
                `<a href="${NAV_TARGET[k]}">${esc(t.nav[k === "previews" ? "previews" : k])}</a>`
            ).join("")}
      </nav>
      <div class="header-tools">
        <div class="lang-switch" role="group" aria-label="${esc(t.nav.language)}">
          ${langLinks}
        </div>
        <button type="button" class="theme-toggle" data-theme-toggle
          aria-label="${esc(t.nav.theme)}" title="${esc(t.nav.theme)}"
          data-label-system="${esc(t.nav.themeSystem)}"
          data-label-light="${esc(t.nav.themeLight)}"
          data-label-dark="${esc(t.nav.themeDark)}">
          ${icon("sun", "theme-icon-light")}${icon("moon", "theme-icon-dark")}
          <span class="visually-hidden" data-theme-state>${esc(t.nav.themeSystem)}</span>
        </button>
        <a class="btn btn-primary btn-sm header-cta" href="${prefix}${urls.landingPath(loc)}#verify">
          ${icon("download")}<span>${esc(t.nav.download)}</span>
        </a>
      </div>
    </div>
  </header>`;
}

function footer({ t, urls, loc, pagePath, consent = null }) {
  const prefix = prefixFor(pagePath);
  const landing = prefix + urls.landingPath(loc);
  const policy = prefix + urls.privacyPath(loc);
  return `
  <footer class="site-footer">
    <div class="wrap footer-grid">
      <div>
        <a class="brand brand-footer" href="${landing}">
          <img src="${prefix}assets/img/logo.svg" alt="" width="30" height="30" class="brand-mark">
          <span class="brand-name">${esc(app.name)}</span>
        </a>
        <p class="footer-note">${esc(t.footer.madeBy)} · ${esc(t.footer.license)}</p>
        <p class="footer-note">${esc(t.footer.version)}</p>
        <p class="footer-note subtle">${esc(t.footer.updated)}: <time datetime="${BUILD_DATE}">${BUILD_DATE}</time></p>
${consent ? adSettingsButton({ t }) : ""}
      </div>
      <div>
        <h2 class="footer-heading">${esc(t.footer.links)}</h2>
        <ul class="footer-list">
          <li><a href="${landing}#tools">${esc(t.nav.tools)}</a></li>
          <li><a href="${landing}#install">${esc(t.nav.install)}</a></li>
          <li><a href="${landing}#verify">${esc(t.nav.verify)}</a></li>
          <li><a href="${landing}#faq">${esc(t.nav.faq)}</a></li>
        </ul>
      </div>
      <div>
        <h2 class="footer-heading">${esc(t.footer.legal)}</h2>
        <ul class="footer-list">
          <li><a href="${policy}">${esc(t.privacy.title)}</a></li>
          <li><a href="${repository.licenseUrl}">MIT</a></li>
          <li><a href="${apk.releaseUrl}">v${esc(app.versionName)}</a></li>
        </ul>
      </div>
      <div>
        <h2 class="footer-heading">${esc(t.footer.project)}</h2>
        <ul class="footer-list">
          <li><a href="${repository.url}">GitHub</a></li>
          <li><a href="${repository.issuesUrl}">${esc(t.contact.issuesLabel)}</a></li>
          <li><a href="mailto:${esc(contact.email)}">${esc(contact.email)}</a></li>
        </ul>
      </div>
    </div>
    <div class="wrap footer-legal">
      <p>${esc(t.footer.factNote)}</p>
      <p>© ${BUILD_DATE.slice(0, 4)} ${esc(app.developer)} · ${esc(app.packageName)} · v${esc(app.versionName)}</p>
    </div>
  </footer>
${consent ? consentBanner({ t, prefix, policyHref: policy }) : ""}  <script src="${prefix}assets/js/site.js" defer></script>
</body>
</html>`;
}

/* ------------------------------ ads / consent ------------------------------ */

function consentBanner({ t, prefix, policyHref }) {
  // Server-rendered but hidden; site.js reveals it only when no choice is stored yet.
  return `  <aside class="consent" id="ads-consent" hidden aria-labelledby="ads-consent-title">
    <div class="consent-inner">
      <h2 id="ads-consent-title" class="consent-title">${esc(t.consent.title)}</h2>
      <p class="consent-body">${esc(t.consent.body)}</p>
      <div class="consent-actions">
        <button type="button" class="btn btn-primary btn-sm" data-consent="accept">${esc(t.consent.accept)}</button>
        <button type="button" class="btn btn-ghost btn-sm" data-consent="decline">${esc(t.consent.decline)}</button>
        <a class="consent-link" href="${policyHref}">${esc(t.consent.policy)}</a>
      </div>
    </div>
  </aside>
`;
}

function adSettingsButton({ t }) {
  return `      <p class="footer-note"><button type="button" class="linkish" data-consent="reset">${esc(t.consent.settings)}</button></p>\n`;
}

/* ---------------------------------- JSON-LD --------------------------------- */

function appNode(urls) {
  return {
    // One entity, two schema.org types: SoftwareApplication is the general type,
    // MobileApplication is its mobile subtype. Emitting both keeps a single @id
    // instead of duplicating the app in the graph.
    "@type": ["SoftwareApplication", "MobileApplication"],
    "@id": urls.abs("#app"),
    name: app.name,
    alternateName: ["أدوات بي سيتي", "BECETTI OUTILS", "Becetti Tools"],
    applicationCategory: "UtilitiesApplication",
    applicationSubCategory: "Utility toolbox",
    operatingSystem: "Android 7.0+",
    softwareVersion: app.versionName,
    version: app.versionName,
    fileFormat: "application/vnd.android.package-archive",
    fileSize: `${apk.sizeBoth}; ${apk.bytes} bytes`,
    downloadUrl: apk.downloadUrl,
    installUrl: apk.releaseUrl,
    license: repository.licenseUrl,
    isAccessibleForFree: true,
    inLanguage: ["en", "fr", "ar"],
    author: { "@type": "Person", name: app.developer, url: contact.github },
    publisher: { "@type": "Person", name: app.developer, url: contact.github },
    offers: { "@type": "Offer", price: "0", priceCurrency: "USD", availability: "https://schema.org/InStock" },
    releaseNotes: apk.releaseUrl,
    featureList: tools.map((tool) => tool.name.en).join(", "),
    softwareHelp: repository.url,
    identifier: app.packageName,
    // Deliberately no aggregateRating / downloadCount / interactionStatistic:
    // no verified rating or install figure exists, so none is published.
  };
}

function websiteNode(urls, t) {
  return {
    "@type": "WebSite",
    "@id": urls.abs("#website"),
    url: urls.base + "/",
    name: app.name,
    description: t.meta.description,
    inLanguage: locales.map((l) => pages[l].htmlLang),
    publisher: { "@type": "Person", name: app.developer, url: contact.github },
  };
}

function breadcrumb(urls, t, loc, isPolicy, pagePath) {
  const items = [
    {
      "@type": "ListItem",
      position: 1,
      name: app.name,
      item: urls.abs(urls.landingPath(loc)),
    },
  ];
  if (isPolicy) {
    items.push({
      "@type": "ListItem",
      position: 2,
      name: privacy[loc].heading,
      item: urls.abs(pagePath),
    });
  }
  return { "@type": "BreadcrumbList", "@id": urls.abs(pagePath) + "#breadcrumb", itemListElement: items };
}

export function landingJsonLd({ t, urls, loc, pagePath }) {
  const canonical = urls.abs(pagePath);
  return {
    "@context": "https://schema.org",
    "@graph": [
      websiteNode(urls, t),
      {
        "@type": "WebPage",
        "@id": canonical + "#webpage",
        url: canonical,
        name: t.meta.title,
        description: t.meta.description,
        isPartOf: { "@id": urls.abs("#website") },
        inLanguage: t.htmlLang,
        datePublished: BUILD_DATE,
        dateModified: BUILD_DATE,
        primaryImageOfPage: { "@type": "ImageObject", url: urls.abs("assets/img/og-image.png"), width: 1200, height: 630 },
        about: { "@id": urls.abs("#app") },
        significantLink: [apk.downloadUrl, apk.releaseUrl],
      },
      appNode(urls),
      {
        "@type": "FAQPage",
        "@id": canonical + "#faq",
        inLanguage: t.htmlLang,
        mainEntity: t.faq.items.map((item) => ({
          "@type": "Question",
          name: item.q,
          acceptedAnswer: { "@type": "Answer", text: item.a },
        })),
      },
      breadcrumb(urls, t, loc, false, pagePath),
    ],
  };
}

export function privacyJsonLd({ t, urls, loc, pagePath, p }) {
  const canonical = urls.abs(pagePath);
  return {
    "@context": "https://schema.org",
    "@graph": [
      websiteNode(urls, t),
      {
        "@type": ["WebPage", "PrivacyPolicy"],
        "@id": canonical + "#webpage",
        url: canonical,
        name: p.metaTitle,
        description: p.metaDescription,
        isPartOf: { "@id": urls.abs("#website") },
        inLanguage: t.htmlLang,
        datePublished: BUILD_DATE,
        dateModified: BUILD_DATE,
        dateModified_effective: p.effective,
        about: { "@id": urls.abs("#app") },
        publisher: { "@type": "Person", name: app.developer, url: contact.github },
      },
      breadcrumb(urls, t, loc, true, pagePath),
    ],
  };
}

/* --------------------------------- sections -------------------------------- */

function heroSection(t, prefix = "") {
  const facts = t.hero.facts
    .map((f) => `<li><strong>${esc(f.value)}</strong><span>${esc(f.label)}</span></li>`)
    .join("");
  return `
      <section class="hero" aria-labelledby="hero-title">
        <div class="wrap hero-inner">
          <div class="hero-copy">
            <p class="badge">${esc(t.hero.badge)}</p>
            <h1 id="hero-title">${esc(t.hero.title)}</h1>
            <p class="tagline">${esc(t.hero.tagline)}</p>
            <p class="lead">${esc(t.hero.lead)}</p>
            <div class="cta-row">
              <a class="btn btn-primary btn-lg" href="${apk.downloadUrl}" rel="noopener">
                ${icon("download")}<span>${esc(t.hero.ctaPrimary)}</span>
                <span class="btn-meta">${esc(t.hero.sizeMeta)} · v${esc(app.versionName)}</span>
              </a>
              <a class="btn btn-ghost btn-lg" href="#install">${icon("info")}<span>${esc(t.hero.ctaSecondary)}</span></a>
              <a class="btn btn-ghost btn-lg" href="${repository.url}" rel="noopener">${icon("github")}<span>${esc(t.hero.ctaTertiary)}</span></a>
            </div>
            <ul class="facts">${facts}</ul>
            <div class="notice notice-warn">
              <h2>${icon("lock")}<span>${esc(t.hero.buildNoticeTitle)}</span></h2>
              <p>${esc(t.hero.buildNotice)}</p>
            </div>
          </div>
          <div class="hero-media">
            <figure>
              <img src="${prefix}assets/img/previews/home.svg" width="360" height="740" alt="${esc(
                t.previews.items[0].title
              )} — ${esc(t.previews.items[0].caption)}" fetchpriority="high" decoding="async">
              <figcaption>${esc(t.previews.disclaimer)}</figcaption>
            </figure>
          </div>
        </div>
      </section>`;
}

function trustSection(t) {
  const items = t.trust.items
    .map(
      (i, idx) =>
        `<li class="trust-card"><h3>${icon(["shield", "gauge", "lock", "github"][idx] || "check")}<span>${esc(
          i.title
        )}</span></h3><p>${esc(i.text)}</p></li>`
    )
    .join("");
  return `
      <section class="section section-alt" aria-labelledby="trust-title">
        <div class="wrap">
          <h2 id="trust-title">${esc(t.trust.title)}</h2>
          <ul class="trust-grid">${items}</ul>
        </div>
      </section>`;
}

function toolsSection(t) {
  const count = tools.length;
  const groups = categories
    .map((cat) => {
      const list = tools.filter((tool) => tool.category === cat.id);
      const items = list
        .map(
          (tool) => `
              <li class="tool" id="tool-${esc(tool.id)}">
                <a class="tool-link" href="#tool-${esc(tool.id)}" aria-label="${esc(
            t.toolsSection.directLink + ": " + tool.name.en
          )}">${icon(tool.icon)}</a>
                <div>
                  <h4>${esc(tool.name[t.htmlLang])}</h4>
                  <p>${esc(tool.desc[t.htmlLang])}</p>
                  <p class="tool-meta"><code>${esc(tool.route)}</code></p>
                </div>
              </li>`
        )
        .join("");
      return `
          <section class="category" aria-labelledby="cat-${esc(cat.id)}">
            <h3 id="cat-${esc(cat.id)}">${icon(cat.icon)}<span>${esc(cat.name[t.htmlLang])}</span>
              <span class="pill">${list.length} ${esc(list.length === 1 ? t.toolsSection.countOne : t.toolsSection.countMany)}</span>
            </h3>
            <ul class="tool-list">${items}</ul>
          </section>`;
    })
    .join("");

  return `
      <section class="section" id="tools" aria-labelledby="tools-title">
        <div class="wrap">
          <h2 id="tools-title">${esc(t.toolsSection.title)} <span class="pill pill-lg">${count}</span></h2>
          <p class="section-lead">${esc(t.toolsSection.lead)}</p>
          <nav class="toc" aria-label="${esc(t.toolsSection.title)}">
            ${categories.map((c) => `<a href="#cat-${esc(c.id)}">${esc(c.name[t.htmlLang])}</a>`).join("")}
          </nav>
          ${groups}
        </div>
      </section>`;
}

function previewSection(t, prefix = "") {
  const figures = t.previews.items
    .map(
      (item, idx) => `
          <figure class="shot">
            <img src="${prefix}assets/img/previews/${esc(item.id)}.svg" width="360" height="740" loading="${
        idx === 0 ? "eager" : "lazy"
      }" decoding="async" alt="${esc(item.title)} — ${esc(item.caption)}">
            <figcaption>
              <strong>${esc(item.title)}</strong>
              <span>${esc(item.caption)}</span>
            </figcaption>
          </figure>`
    )
    .join("");
  return `
      <section class="section section-alt" id="preview" aria-labelledby="preview-title">
        <div class="wrap">
          <h2 id="preview-title">${esc(t.previews.title)}</h2>
          <p class="section-lead">${esc(t.previews.lead)}</p>
          <div class="notice notice-info"><p>${icon("info")}<span>${esc(t.previews.disclaimer)}</span></p></div>
          <div class="shots">${figures}</div>
        </div>
      </section>`;
}

function compatibilitySection(t) {
  const rows = t.compatibility.rows
    .map(
      (r) =>
        `<tr><th scope="row">${esc(r.label)}</th><td>${
          r.value.includes(" · ") || r.value.includes("com.becetti")
            ? esc(r.value)
            : `<code>${esc(r.value)}</code>`
        }</td></tr>`
    )
    .join("");
  return `
      <section class="section" id="compatibility" aria-labelledby="compat-title">
        <div class="wrap">
          <h2 id="compat-title">${esc(t.compatibility.title)}</h2>
          <p class="section-lead">${esc(t.compatibility.lead)}</p>
          <div class="table-scroll">
            <table class="spec-table">
              <caption class="visually-hidden">${esc(t.compatibility.title)}</caption>
              <tbody>${rows}</tbody>
            </table>
          </div>
        </div>
      </section>`;
}

function downloadSection(t) {
  const commands = t.download.commands
    .map(
      (c) =>
        `<tr><th scope="row">${esc(c.os)}</th><td><code class="cmd">${esc(c.cmd)}</code></td></tr>`
    )
    .join("");
  return `
      <section class="section section-alt" id="verify" aria-labelledby="verify-title">
        <div class="wrap">
          <h2 id="verify-title">${esc(t.download.title)}</h2>
          <p class="section-lead">${esc(t.download.lead)}</p>
          <div class="download-grid">
            <div class="card">
              <h3>${icon("download")}<span>${esc(app.name)} v${esc(app.versionName)}</span></h3>
              <dl class="kv">
                <dt>${esc(t.download.fileLabel)}</dt><dd><code>${esc(apk.fileName)}</code></dd>
                <dt>${esc(t.download.sizeLabel)}</dt><dd>${esc(apk.mb)} <span class="subtle">(${esc(
    apk.mib
  )} · ${apk.bytes.toLocaleString("en-US")} bytes)</span></dd>
                <dt>${esc(t.download.shaLabel)}</dt>
                <dd class="sha">
                  <code id="sha256">${esc(apk.sha256)}</code>
                  <button type="button" class="btn btn-ghost btn-xs" data-copy="${esc(
                    apk.sha256
                  )}" data-copied="${esc(t.download.copied)}" aria-label="${esc(t.download.copy)}: SHA-256">${icon("copy")}<span>${esc(
    t.download.copy
  )}</span></button>
                </dd>
              </dl>
              <div class="cta-row">
                <a class="btn btn-primary" href="${apk.downloadUrl}" rel="noopener">${icon(
    "download"
  )}<span>${esc(t.hero.ctaPrimary)}</span></a>
                <a class="btn btn-ghost" href="${apk.releaseUrl}" rel="noopener">${icon("github")}<span>${esc(
    t.download.releaseLink
  )}</span></a>
              </div>
              <p class="copy-status" role="status" aria-live="polite"></p>
            </div>
            <div class="card">
              <h3>${icon("shield")}<span>${esc(t.download.verifyTitle)}</span></h3>
              <p class="subtle">${esc(t.download.verifyHint)}</p>
              <div class="table-scroll">
                <table class="cmd-table">
                  <caption class="visually-hidden">${esc(t.download.verifyTitle)}</caption>
                  <tbody>${commands}</tbody>
                </table>
              </div>
              <h4>${esc(t.download.onPhone)}</h4>
              <p class="subtle">${esc(t.download.onPhoneText)}</p>
            </div>
          </div>
        </div>
      </section>`;
}

function installSection(t) {
  const steps = t.install.steps
    .map(
      (s, i) => `
            <li class="step">
              <span class="step-num" aria-hidden="true">${i + 1}</span>
              <div><h3>${esc(s.title)}</h3><p>${esc(s.text)}</p></div>
            </li>`
    )
    .join("");
  const trouble = t.install.troubleshoot.map((x) => `<li>${esc(x)}</li>`).join("");
  return `
      <section class="section" id="install" aria-labelledby="install-title">
        <div class="wrap">
          <h2 id="install-title">${esc(t.install.title)}</h2>
          <p class="section-lead">${esc(t.install.lead)}</p>
          <ol class="steps">${steps}</ol>
          <div class="card card-flat">
            <h3>${icon("info")}<span>${esc(t.install.troubleshootTitle)}</span></h3>
            <ul class="bullets">${trouble}</ul>
            <p class="subtle">${esc(t.install.uninstallNote)}</p>
          </div>
        </div>
      </section>`;
}

function privacySection(t, urls, loc, prefix) {
  const points = t.privacy.points.map((p) => `<li>${esc(p)}</li>`).join("");
  return `
      <section class="section section-alt" id="privacy" aria-labelledby="privacy-title">
        <div class="wrap">
          <h2 id="privacy-title">${esc(t.privacy.title)}</h2>
          <p class="section-lead">${esc(t.privacy.lead)}</p>
          <ul class="bullets bullets-check">${points}</ul>
          <p><a class="btn btn-ghost" href="${prefix}${urls.privacyPath(loc)}">${icon(
    "shield"
  )}<span>${esc(t.privacy.cta)}</span></a></p>
        </div>
      </section>`;
}

function faqSection(t) {
  const items = t.faq.items
    .map(
      (item, i) => `
            <details class="faq-item"${i === 0 ? " open" : ""}>
              <summary><span>${esc(item.q)}</span></summary>
              <p>${esc(item.a)}</p>
            </details>`
    )
    .join("");
  return `
      <section class="section" id="faq" aria-labelledby="faq-title">
        <div class="wrap">
          <h2 id="faq-title">${esc(t.faq.title)}</h2>
          <div class="faq">${items}</div>
        </div>
      </section>`;
}

function roadmapSection(t) {
  const groups = t.roadmap.groups
    .map(
      (g) => `
            <div class="card card-flat roadmap-card">
              <h3><span class="pill pill-lg">${esc(g.version)}</span></h3>
              <ul class="bullets">${g.items.map((i) => `<li>${esc(i)}</li>`).join("")}</ul>
            </div>`
    )
    .join("");
  return `
      <section class="section section-alt" id="roadmap" aria-labelledby="roadmap-title">
        <div class="wrap">
          <h2 id="roadmap-title">${esc(t.roadmap.title)}</h2>
          <p class="section-lead">${esc(t.roadmap.lead)}</p>
          <div class="roadmap-grid">${groups}</div>
          <p class="subtle">${esc(t.roadmap.note)}</p>
        </div>
      </section>`;
}

function contactSection(t) {
  return `
      <section class="section" id="contact" aria-labelledby="contact-title">
        <div class="wrap">
          <h2 id="contact-title">${esc(t.contact.title)}</h2>
          <p class="section-lead">${esc(t.contact.lead)}</p>
          <ul class="contact-grid">
            <li><a href="mailto:${esc(contact.email)}">${icon("mail")}<span><strong>${esc(
    t.contact.emailLabel
  )}</strong>${esc(contact.email)}</span></a></li>
            <li><a href="${contact.github}" rel="noopener">${icon("github")}<span><strong>${esc(
    t.contact.githubLabel
  )}</strong>@Ahmedbecett</span></a></li>
            <li><a href="${repository.issuesUrl}" rel="noopener">${icon("info")}<span><strong>${esc(
    t.contact.issuesLabel
  )}</strong>GitHub Issues</span></a></li>
            <li><a href="${repository.url}" rel="noopener">${icon("document")}<span><strong>${esc(
    t.contact.repoLabel
  )}</strong>Ahmedbecett/BECETTITOOLS</span></a></li>
          </ul>
          <p class="subtle">${esc(t.contact.responseNote)}</p>
        </div>
      </section>`;
}

/* ---------------------------------- pages ---------------------------------- */

export function renderLanding({ urls, loc }) {
  const t = pages[loc];
  const pagePath = urls.landingPath(loc);
  const prefix = prefixFor(pagePath);
  const alternates = [
    ...locales.map((l) => ({ hreflang: pages[l].htmlLang, path: urls.landingPath(l) })),
    { hreflang: "x-default", path: urls.landingPath("en") },
  ];
  const doc = head({
    t,
    urls,
    pagePath,
    canonicalPath: pagePath,
    title: t.meta.title,
    description: t.meta.description,
    keywords: t.meta.keywords,
    alternates,
    jsonLd: landingJsonLd({ t, urls, loc, pagePath }),
    adsConfig:
      ads.enabled && !ads.onPolicyPages
        ? { src: ads.tagSrc, zone: ads.zone, consentKey: ads.consentKey }
        : null,
  });

  return `${doc}
${header({ t, urls, loc, pagePath, isPolicy: false })}
  <main id="main">
${heroSection(t, prefix)}
${trustSection(t)}
${toolsSection(t)}
${previewSection(t, prefix)}
${compatibilitySection(t)}
${downloadSection(t)}
${installSection(t)}
${privacySection(t, urls, loc, prefix)}
${faqSection(t)}
${roadmapSection(t)}
${contactSection(t)}
  </main>
${footer({ t, urls, loc, pagePath, consent: ads.enabled })}`;
}

export function renderPrivacy({ urls, loc }) {
  const t = pages[loc];
  const p = privacy[loc];
  const pagePath = urls.privacyPath(loc);
  const prefix = prefixFor(pagePath);
  const alternates = [
    ...locales.map((l) => ({ hreflang: pages[l].htmlLang, path: urls.privacyPath(l) })),
    { hreflang: "x-default", path: urls.privacyPath("en") },
  ];
  const doc = head({
    t,
    urls,
    pagePath,
    canonicalPath: pagePath,
    title: p.metaTitle,
    description: p.metaDescription,
    alternates,
    jsonLd: privacyJsonLd({ t, urls, loc, pagePath, p }),
  });

  const related = p.relatedLinks
    .map((l) => {
      const href =
        l.href === "repo"
          ? repository.url
          : l.href === "issues"
          ? repository.issuesUrl
          : prefix + urls.landingPath(loc) + "#install";
      return `<li><a href="${href}"${l.href === "repo" || l.href === "issues" ? ' rel="noopener"' : ""}>${esc(
        l.label
      )}</a></li>`;
    })
    .join("");

  const toc = p.sections
    .map((s) => `<li><a href="#${esc(s.id)}">${esc(s.heading)}</a></li>`)
    .join("");

  const sections = p.sections
    .map((s) => {
      const paras = (s.paragraphs || []).map((x) => `<p>${esc(x)}</p>`).join("");
      const list = s.list ? `<ul class="bullets">${s.list.map((x) => `<li>${esc(x)}</li>`).join("")}</ul>` : "";
      const after = (s.after || []).map((x) => `<p>${esc(x)}</p>`).join("");
      return `
        <section class="policy-section" id="${esc(s.id)}" aria-labelledby="h-${esc(s.id)}">
          <h2 id="h-${esc(s.id)}">${esc(s.heading)}</h2>
          ${paras}${list}${after}
        </section>`;
    })
    .join("");

  return `${doc}
${header({ t, urls, loc, pagePath, isPolicy: true })}
  <main id="main">
    <section class="section policy" aria-labelledby="policy-title">
      <div class="wrap policy-wrap">
        <p class="badge">${esc(app.name)} v${esc(app.versionName)}</p>
        <h1 id="policy-title">${esc(p.heading)}</h1>
        <p class="policy-meta">${esc(p.appLine)}</p>
        <p class="policy-meta"><strong>${esc(p.effective)}</strong> · ${esc(p.updated)}</p>
        <div class="notice notice-info"><p>${icon("info")}<span>${esc(p.summaryTitle)}: ${esc(p.summary)}</span></p></div>
        <nav class="policy-toc" aria-label="${esc(p.toc)}">
          <h2>${esc(p.toc)}</h2>
          <ol>${toc}</ol>
        </nav>
        ${sections}
        <section class="policy-section" id="policy-contact" aria-labelledby="h-policy-contact">
          <h2 id="h-policy-contact">${esc(t.contact.title)}</h2>
          <ul class="bullets">
            <li>${esc(t.contact.emailLabel)}: <a href="mailto:${esc(contact.email)}">${esc(contact.email)}</a></li>
            <li>${esc(t.contact.issuesLabel)}: <a href="${repository.issuesUrl}" rel="noopener">GitHub Issues</a></li>
            <li>${esc(t.contact.repoLabel)}: <a href="${repository.url}" rel="noopener">Ahmedbecett/BECETTITOOLS</a></li>
          </ul>
          <p class="subtle">${esc(t.contact.responseNote)}</p>
        </section>
        <p class="policy-disclaimer">${esc(p.disclaimer)}</p>
        <p><a class="btn btn-primary" href="${prefix}${urls.landingPath(loc)}">${icon(
    "arrow"
  )}<span>${esc(p.backToLanding)}</span></a></p>
        <div class="policy-related">
          <h2>${esc(p.relatedTitle)}</h2>
          <ul class="bullets">${related}</ul>
        </div>
      </div>
    </section>
  </main>
${footer({ t, urls, loc, pagePath })}`;
}

/** Root document: a real Vercel redirect exists, this is the static fallback. */
/**
 * Ad-network verification page. Not linked from the site, excluded from the sitemap and
 * marked noindex: its only job is to let the publisher's ad-network crawler find the tag
 * without a click. The real pages stay consent-gated.
 */
export function renderAdsVerify({ urls }) {
  const pagePath = "/ads-verify/";
  const prefix = prefixFor(pagePath);
  const cfg = { src: ads.tagSrc, zone: ads.zone, consentKey: ads.consentKey, unconditional: true };
  return `<!doctype html>
<html lang="en" dir="ltr">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Ad tag verification — BECETTI TOOLS</title>
  <meta name="description" content="Internal page used to verify the advertising tag for becetti-tools.vercel.app.">
  <meta name="robots" content="noindex, nofollow, noarchive">
  <meta name="googlebot" content="noindex, nofollow, noarchive">
  <link rel="canonical" href="${esc(urls.abs(pagePath))}">
  <meta name="theme-color" content="#172235">
  <link rel="icon" href="${prefix}assets/img/favicon.svg" type="image/svg+xml">
  <link rel="stylesheet" href="${prefix}assets/css/site.css">
  <script type="application/json" id="becetti-ads">${JSON.stringify(cfg)}</script>
</head>
<body>
  <main id="main" class="wrap" style="padding:48px 24px;max-width:720px">
    <h1>Ad tag verification</h1>
    <p>This page exists so an advertising network's verification crawler can find the
      Monetag site tag for zone ${esc(ads.zone)} without pressing a button. It is not linked
      from the site, is excluded from the sitemap and is marked <code>noindex</code>.</p>
    <p>On the real pages the tag is injected only after the visitor accepts it, so a first
      visit downloads no third-party code at all.</p>
    <p id="ads-verify-status">Tag status: <strong id="ads-state-pending">not injected yet</strong><strong id="ads-state-ok" hidden>injected — the Monetag script is in this page</strong></p>
    <p><a href="${prefix}becetti-tools/">Go to the download page</a></p>
  </main>
  <script src="${prefix}assets/js/site.js" defer></script>
  <style>
    html[data-ads="loaded"] #ads-state-pending { display: none; }
    html[data-ads="loaded"] #ads-state-ok { display: inline; }
  </style>
</body>
</html>`;
}

export function renderRootRedirect({ urls }) {
  const target = urls.landingPath("en");
  return `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>BECETTI TOOLS — ${esc(app.taglineEn)}</title>
  <meta name="robots" content="noindex, follow">
  <link rel="canonical" href="${esc(urls.abs(target))}">
  <meta http-equiv="refresh" content="0; url=${esc(target)}">
  <meta name="theme-color" content="#172235">
  <link rel="icon" href="assets/img/favicon.svg" type="image/svg+xml">
  <style>body{font-family:system-ui,-apple-system,"Segoe UI",Roboto,sans-serif;background:#172235;color:#fff;display:grid;place-items:center;min-height:100vh;margin:0;padding:24px;text-align:center}a{color:#F47B20}</style>
</head>
<body>
  <main>
    <h1>BECETTI TOOLS</h1>
    <p>${esc(app.taglineEn)}</p>
    <p><a href="${esc(target)}">Continue to the download page →</a></p>
  </main>
  <script>location.replace(${JSON.stringify(target)});</script>
</body>
</html>`;
}

export function render404({ urls, t }) {
  return `<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Page not found — BECETTI TOOLS</title>
  <meta name="robots" content="noindex, follow">
  <link rel="canonical" href="${esc(urls.abs(urls.landingPath("en")))}">
  <meta name="theme-color" content="#172235">
  <link rel="icon" href="/assets/img/favicon.svg" type="image/svg+xml">
  <style>body{font-family:system-ui,-apple-system,"Segoe UI",Roboto,sans-serif;background:#172235;color:#fff;display:grid;place-items:center;min-height:100vh;margin:0;padding:24px;text-align:center}a{color:#F47B20}</style>
</head>
<body>
  <main>
    <h1>404</h1>
    <p>This page does not exist. / هذه الصفحة غير موجودة. / Cette page n’existe pas.</p>
    <ul style="list-style:none;padding:0">
      <li><a href="/becetti-tools/">BECETTI TOOLS — English</a></li>
      <li><a href="/becetti-tools/fr/">BECETTI TOOLS — Français</a></li>
      <li><a href="/becetti-tools/ar/">أدوات بي سيتي — العربية</a></li>
      <li><a href="/privacy/">${esc(t.privacy.title)}</a></li>
    </ul>
  </main>
</body>
</html>`;
}

export function renderSitemap({ urls }) {
  const entries = [];
  for (const loc of locales) {
    entries.push({
      loc: urls.abs(urls.landingPath(loc)),
      alternates: locales.map((l) => ({ hreflang: pages[l].htmlLang, href: urls.abs(urls.landingPath(l)) })),
      priority: loc === "en" ? "1.0" : "0.9",
    });
  }
  for (const loc of locales) {
    entries.push({
      loc: urls.abs(urls.privacyPath(loc)),
      alternates: locales.map((l) => ({ hreflang: pages[l].htmlLang, href: urls.abs(urls.privacyPath(l)) })),
      priority: "0.5",
    });
  }
  const body = entries
    .map(
      (e) => `  <url>
    <loc>${esc(e.loc)}</loc>
    <lastmod>${BUILD_DATE}</lastmod>
    <changefreq>monthly</changefreq>
    <priority>${e.priority}</priority>
${e.alternates
  .map((a) => `    <xhtml:link rel="alternate" hreflang="${esc(a.hreflang)}" href="${esc(a.href)}"/>`)
  .join("\n")}
  </url>`
    )
    .join("\n");
  return `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
        xmlns:xhtml="http://www.w3.org/1999/xhtml">
${body}
</urlset>
`;
}

export function renderRobots({ urls }) {
  return `# robots.txt — BECETTI TOOLS
User-agent: *
Allow: /

# No crawl-delay is set: the site is a handful of static pages.

Sitemap: ${urls.abs("sitemap.xml")}
`;
}

export function renderManifest({ urls }) {
  return `${JSON.stringify(
    {
      name: `${app.name} — ${app.taglineEn}`,
      short_name: "BECETTI TOOLS",
      description: pages.en.meta.description,
      start_url: "becetti-tools/",
      scope: "./",
      display: "standalone",
      background_color: "#172235",
      theme_color: "#172235",
      lang: "en",
      dir: "ltr",
      categories: ["utilities", "productivity"],
      icons: [
        { src: "assets/img/logo-192.png", sizes: "192x192", type: "image/png", purpose: "any" },
        { src: "assets/img/logo-512.png", sizes: "512x512", type: "image/png", purpose: "any" },
        { src: "assets/img/logo.svg", sizes: "any", type: "image/svg+xml" },
      ],
    },
    null,
    2
  )}\n`;
}

const LANDING_CSP = [
  "default-src 'self'",
  // Ad tags pull their runtime from rotating hosts that the publisher cannot pin, so the
  // landing pages allow https scripts/connections. Everything else stays locked, and the
  // strict policy below still covers /, /privacy/, 404 and /sw.js.
  "script-src 'self' 'unsafe-inline' https:",
  "style-src 'self' 'unsafe-inline'",
  "img-src 'self' data: blob: https:",
  "font-src 'self' data:",
  "connect-src 'self' https:",
  "frame-src https:",
  "media-src 'self' https:",
  "worker-src 'self' blob:",
  "object-src 'none'",
  "form-action 'none'",
  "base-uri 'self'",
  "frame-ancestors 'self'",
].join("; ");

export function renderVercelConfig() {
  return `${JSON.stringify(
    {
      $schema: "https://openapi.vercel.sh/vercel.json",
      trailingSlash: true,
      cleanUrls: false,
      redirects: [{ source: "/", destination: "/becetti-tools/", permanent: true }],
      headers: [
        {
          source: "/(.*)",
          headers: [
            { key: "X-Content-Type-Options", value: "nosniff" },
            { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
            { key: "X-Frame-Options", value: "SAMEORIGIN" },
            {
              key: "Content-Security-Policy",
              value:
                "default-src 'self'; img-src 'self' data:; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline'; form-action 'none'; base-uri 'self'; frame-ancestors 'self'",
            },
          ],
        },
        {
          source: "/assets/(.*)",
          headers: [{ key: "Cache-Control", value: "public, max-age=3600, must-revalidate" }],
        },
        {
          // Search Console is picky about MIME types; state them explicitly instead of
          // relying on the edge's guess.
          source: "/sitemap.xml",
          headers: [
            { key: "Content-Type", value: "application/xml; charset=utf-8" },
            { key: "Cache-Control", value: "public, max-age=3600, must-revalidate" },
          ],
        },
        {
          source: "/robots.txt",
          headers: [
            { key: "Content-Type", value: "text/plain; charset=utf-8" },
            { key: "Cache-Control", value: "public, max-age=3600, must-revalidate" },
          ],
        },
        {
          // Landing pages carry the consent-gated Monetag tag, so their CSP has to allow it.
          // Privacy pages, 404 and the root redirect keep the strict policy below.
          source: "/becetti-tools/",
          headers: [{ key: "Content-Security-Policy", value: LANDING_CSP }],
        },
        {
          source: "/becetti-tools/(.*)",
          headers: [{ key: "Content-Security-Policy", value: LANDING_CSP }],
        },
        {
          // Publisher-verification page: the ad network's crawler has no way to press the
          // consent button, so this one unlinked page loads the tag immediately.
          source: "/ads-verify/",
          headers: [{ key: "Content-Security-Policy", value: LANDING_CSP }],
        },
        {
          source: "/ads-verify/(.*)",
          headers: [{ key: "Content-Security-Policy", value: LANDING_CSP }],
        },
        {
          // The ad network's service worker imports its own runtime from 5gvci.com, which the
          // page-level CSP (default-src 'self') would block. This relaxes CSP for /sw.js only —
          // no HTML page is allowed to load a third-party script.
          source: "/sw.js",
          headers: [
            {
              key: "Content-Security-Policy",
              value:
                "default-src 'self' https://5gvci.com; script-src 'self' https://5gvci.com; connect-src 'self' https://5gvci.com; img-src 'self' data: https://5gvci.com",
            },
            { key: "Cache-Control", value: "public, max-age=0, must-revalidate" },
            { key: "Service-Worker-Allowed", value: "/" },
          ],
        },
      ],
    },
    null,
    2
  )}\n`;
}

export { BUILD_DATE, prefixFor, esc };
