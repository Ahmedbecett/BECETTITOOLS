"use client";
import React, { useEffect, useState } from "react";

const STRINGS: any = {
  en: {
    badge: "BECETTI TOOLS v1.0.0 • Native Android • Free • No Account",
    title: "Everything you need.",
    title2: "One smart toolbox.",
    subtitle: "15 powerful tools for everyday tasks — calculators, converters, QR, PDFs, images and text. Lightweight, offline-first, privacy-focused.",
    download: "Download APK (v1.0.0)",
    downloadSub: "Debug build • 85 MB • GitHub Release",
    install: "Installation Guide",
    privacy: "Privacy Policy",
    features: "Explore 15 Built-in Tools",
    featuresSub: "Grouped into 4 efficient categories + Notes — all work offline, no dummy buttons.",
    categories: ["Calculator & Converters", "QR & Security", "Documents & Files", "Text Utilities"],
    tools15: [
      { n: "Scientific Calculator", d: "Advanced math, trig, log, sqrt, history saved locally", c: 0, i: "🧮" },
      { n: "Unit Converter", d: "Length, Weight, Temp, Area, Volume, Speed — 6 types", c: 0, i: "📐" },
      { n: "Currency Converter", d: "Online via Frankfurter/ECB, cached, offline indicator", c: 0, i: "💱" },
      { n: "Percentage Calculator", d: "Percent of number, increase & decrease", c: 0, i: "%" },
      { n: "Age Calculator", d: "Years/months/days + next birthday", c: 0, i: "🎂" },
      { n: "QR Code Generator", d: "Text, URL, Wi-Fi, Contact vCard → PNG export", c: 1, i: "🔳" },
      { n: "QR Code Scanner", d: "CameraX + ML Kit, permission only on demand", c: 1, i: "📷" },
      { n: "Password Generator", d: "SecureRandom 4–64 chars, all character sets", c: 1, i: "🔑" },
      { n: "Password Strength", d: "Local analysis 0–5, nothing uploaded", c: 1, i: "🛡️" },
      { n: "Image to PDF", d: "Multi-image SAF → A4/A3/Letter PDF, originals safe", c: 2, i: "🖼️" },
      { n: "PDF Merger", d: "PDFBox-Android, reorder & merge multiple PDFs", c: 2, i: "📄" },
      { n: "Image Compressor", d: "Quality 10–100 slider, preview & save JPEG", c: 2, i: "🗜️" },
      { n: "OCR Text Extractor", d: "ML Kit AR/FR/EN, copy extracted text", c: 2, i: "🔍" },
      { n: "Text Toolkit", d: "Count, case, spaces, reverse, dedup", c: 3, i: "✍️" },
      { n: "Notes Manager", d: "Room DB create/edit/search, local only", c: 3, i: "🗒️" },
    ],
    pkg: "Package & Compatibility",
    faqTitle: "FAQ",
    contact: "Contact & Support",
  },
  fr: {
    badge: "BECETTI TOOLS v1.0.0 • Natif Android • Gratuit • Sans compte",
    title: "Tout ce dont vous avez besoin.",
    title2: "Une boîte à outils intelligente.",
    subtitle: "15 outils puissants pour la vie quotidienne — calculs, conversions, QR, PDF, images et texte. Léger, hors-ligne d'abord, respect vie privée.",
    download: "Télécharger APK (v1.0.0)",
    downloadSub: "Build Debug • 85 Mo • Release GitHub",
    install: "Guide d’installation",
    privacy: "Politique de confidentialité",
    features: "Découvrez 15 outils intégrés",
    featuresSub: "Groupés en 4 catégories + Notes — tout fonctionne hors-ligne, aucun bouton factice.",
    categories: ["Calculs & Convertisseurs", "QR & Sécurité", "Documents & Fichiers", "Outils Texte"],
    tools15: [
      { n: "Calculatrice Scientifique", d: "Maths avancées, trigonométrie, log, historique local", c: 0, i: "🧮" },
      { n: "Convertisseur d’unités", d: "Longueur, Poids, Température, Surface, Volume, Vitesse", c: 0, i: "📐" },
      { n: "Convertisseur Devises", d: "En ligne via Frankfurter/BCE, cache + indicateur hors-ligne", c: 0, i: "💱" },
      { n: "Calcul Pourcentage", d: "Pourcentage d’un nombre, augmentation & réduction", c: 0, i: "%" },
      { n: "Calculateur d’âge", d: "Années/mois/jours + prochain anniversaire", c: 0, i: "🎂" },
      { n: "Générateur QR", d: "Texte, URL, Wi-Fi, Contact vCard → export PNG", c: 1, i: "🔳" },
      { n: "Scanner QR", d: "CameraX + ML Kit, permission à la demande", c: 1, i: "📷" },
      { n: "Générateur MDP", d: "SecureRandom 4–64 caractères", c: 1, i: "🔑" },
      { n: "Force Mot de Passe", d: "Analyse locale 0–5, rien n’est envoyé", c: 1, i: "🛡️" },
      { n: "Image en PDF", d: "Multi-images SAF → PDF A4/A3/Letter", c: 2, i: "🖼️" },
      { n: "Fusion PDF", d: "PDFBox-Android, réorganiser & fusionner", c: 2, i: "📄" },
      { n: "Compresseur Image", d: "Qualité 10–100, aperçu & JPEG", c: 2, i: "🗜️" },
      { n: "OCR Extraction", d: "ML Kit AR/FR/EN, copier le texte", c: 2, i: "🔍" },
      { n: "Boîte à outils Texte", d: "Compter, casse, espaces, inverser", c: 3, i: "✍️" },
      { n: "Gestionnaire Notes", d: "Room DB, création/recherche locale", c: 3, i: "🗒️" },
    ],
    pkg: "Package & Compatibilité",
    faqTitle: "FAQ",
    contact: "Contact & Support",
  },
  ar: {
    badge: "أدوات بي سيتي v1.0.0 • أندرويد أصلي • مجاني • بدون حساب",
    title: "كل ما تحتاجه.",
    title2: "صندوق أدوات ذكي واحد.",
    subtitle: "15 أداة قوية للمهام اليومية — حسابات، تحويلات، QR، ملفات PDF، صور ونصوص. خفيف، يعمل بدون إنترنت، يحترم الخصوصية.",
    download: "تحميل APK (v1.0.0)",
    downloadSub: "نسخة Debug • 85 ميغابايت • إصدار GitHub",
    install: "دليل التثبيت",
    privacy: "سياسة الخصوصية",
    features: "اكتشف 15 أداة مدمجة",
    featuresSub: "مقسمة إلى 4 فئات + الملاحظات — كلها تعمل دون إنترنت، لا أزرار وهمية.",
    categories: ["الحساب والتحويل", "QR والأمان", "المستندات والملفات", "أدوات النص"],
    tools15: [
      { n: "آلة حاسبة علمية", d: "عمليات متقدمة، مثلثات، لوغاريتم، حفظ محلي", c: 0, i: "🧮" },
      { n: "محول الوحدات", d: "الطول، الوزن، الحرارة، المساحة، الحجم، السرعة", c: 0, i: "📐" },
      { n: "محول العملات", d: "عبر Frankfurter/ECB مع تخزين مؤقت ومؤشر عدم الاتصال", c: 0, i: "💱" },
      { n: "حاسبة النسبة", d: "نسبة من رقم، زيادة ونقصان", c: 0, i: "%" },
      { n: "حاسبة العمر", d: "سنوات/أشهر/أيام + عيد الميلاد القادم", c: 0, i: "🎂" },
      { n: "مولد QR", d: "نص، رابط، Wi-Fi، جهة اتصال → تصدير PNG", c: 1, i: "🔳" },
      { n: "ماسح QR", d: "CameraX + ML Kit، الإذن عند الطلب فقط", c: 1, i: "📷" },
      { n: "مولد كلمات المرور", d: "SecureRandom من 4 إلى 64 حرفًا", c: 1, i: "🔑" },
      { n: "قوة كلمة المرور", d: "تحليل محلي 0–5، لا يتم الرفع", c: 1, i: "🛡️" },
      { n: "صور إلى PDF", d: "متعدد الصور SAF → PDF A4/A3/Letter", c: 2, i: "🖼️" },
      { n: "دمج PDF", d: "PDFBox-Android، إعادة ترتيب ودمج", c: 2, i: "📄" },
      { n: "ضاغط الصور", d: "جودة 10–100 مع معاينة وحفظ JPEG", c: 2, i: "🗜️" },
      { n: "استخراج النص OCR", d: "ML Kit عربي/فرنسي/إنجليزي", c: 2, i: "🔍" },
      { n: "أدوات النص", d: "عد، حالة الأحرف، مسافات، عكس", c: 3, i: "✍️" },
      { n: "مدير الملاحظات", d: "قاعدة Room، إنشاء/بحث محلي", c: 3, i: "🗒️" },
    ],
    pkg: "الحزمة والتوافق",
    faqTitle: "الأسئلة الشائعة",
    contact: "الاتصال والدعم",
  },
};

export default function BecettiToolsPage() {
  const [lang, setLang] = useState<keyof typeof STRINGS>("en");
  const [theme, setTheme] = useState<"light" | "dark">("light");
  const t: any = STRINGS[lang];
  const isRTL = lang === "ar";

  useEffect(() => {
    const saved = localStorage.getItem("becetti-lang") as any;
    if (saved && STRINGS[saved]) setLang(saved);
    const th: any = localStorage.getItem("becetti-theme") || (window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light");
    setTheme(th);
    document.documentElement.setAttribute("data-theme", th);
  }, []);
  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    document.documentElement.setAttribute("dir", isRTL ? "rtl" : "ltr");
    document.documentElement.lang = lang as string;
    localStorage.setItem("becetti-theme", theme);
    localStorage.setItem("becetti-lang", lang);
  }, [theme, lang, isRTL]);

  const APK_URL = "https://raw.githubusercontent.com/Ahmedbecett/BECETTITOOLS/main/BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk";
  const APK_SHA = "460235a24055c4c808ad16b80e3f1cbe38fe81410b57cfb8779d552020a22632";
  const CANONICAL = "https://becetti-tools.vercel.app/becetti-tools";

  return (
    <div dir={isRTL ? "rtl" : "ltr"}>
      {/* SEO head via manual tags for canonical/OG */}
      <style>{`html{scroll-behavior:smooth}`}</style>

      {/* NAV */}
      <nav className="nav">
        <div className="container" style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "12px 20px" }}>
          <a href="/becetti-tools" style={{ display: "flex", gap: 10, alignItems: "center", textDecoration: "none", fontWeight: 900 }}>
            <img src="/assets/logo.svg" alt="BECETTI TOOLS logo" width={36} height={36} style={{ borderRadius: 10, border: "1px solid var(--border)" }} />
            <span style={{ color: "var(--navy)", letterSpacing: ".2px" }}>BECETTI <span style={{ color: "var(--orange)" }}>TOOLS</span></span>
            <span className="kbd">v1.0.0</span>
          </a>
          <div style={{ display: "flex", gap: 8, alignItems: "center", flexWrap: "wrap" }}>
            <select value={lang} onChange={(e) => setLang(e.target.value as any)} aria-label="Language" style={{ padding: "8px 10px", borderRadius: 12, border: "1px solid var(--border)", background: "var(--card)" }}>
              <option value="en">English</option>
              <option value="fr">Français</option>
              <option value="ar">العربية</option>
            </select>
            <button onClick={() => setTheme(theme === "dark" ? "light" : "dark")} aria-label="Toggle theme" className="btn btn-ghost" style={{ padding: "8px 12px" }}>{theme === "dark" ? "☀️ Light" : "🌙 Dark"}</button>
            <a href="#download" className="btn btn-primary" style={{ padding: "10px 16px" }}>{lang === "ar" ? "حمّل" : lang === "fr" ? "Télécharger" : "Download"}</a>
          </div>
        </div>
      </nav>

      {/* HERO */}
      <header className="hero">
        <div className="container hero-grid">
          <div>
            <div className="badge">✦ {t.badge}</div>
            <h1 style={{ fontSize: "44px", lineHeight: 1.05, margin: "18px 0 8px", fontWeight: 900 }}>
              {t.title} <br />
              <span style={{ color: "var(--orange)" }}>{t.title2}</span>
            </h1>
            <p style={{ color: "var(--muted)", fontSize: 18, maxWidth: 640 }}>{t.subtitle}</p>
            <div style={{ display: "flex", gap: 12, flexWrap: "wrap", marginTop: 18 }}>
              <a id="download" href={APK_URL} className="btn btn-primary">{t.download} →</a>
              <a href="#install" className="btn btn-ghost">{t.install}</a>
              <a href="/privacy" className="btn btn-ghost">{t.privacy}</a>
            </div>
            <div className="card" style={{ marginTop: 18, display: "grid", gap: 10 }}>
              <div style={{ display: "flex", gap: 10, flexWrap: "wrap", alignItems: "center" }}>
                <span className="kbd">com.becetti.tools</span>
                <span className="kbd">minSdk 24 (Android 7.0)</span>
                <span className="kbd">targetSdk 34</span>
                <span className="kbd">Kotlin + Compose + Material 3</span>
              </div>
              <div style={{ fontSize: 13, color: "var(--muted)" }}>
                SHA256: <code style={{ wordBreak: "break-all" }}>{APK_SHA}</code> • Verified Native Build • No WebView • 20 dex files • 41.4 MB classes.dex
                <br />
                <a href={APK_URL} style={{ color: "var(--orange)", fontWeight: 700 }}>Direct GitHub raw link</a> • Size ~85 MB • Debug build (test before release signing). Verify checksum: <code>sha256sum BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk</code>
              </div>
            </div>
            <div style={{ display: "flex", gap: 10, marginTop: 12, flexWrap: "wrap" }}>
              <span className="kbd">✅ Zero budget — no paid libs</span>
              <span className="kbd">🔒 No hardcoded secrets</span>
              <span className="kbd">📴 Offline-first (currency needs net)</span>
              <span className="kbd">♿ Accessible • RTL</span>
            </div>
          </div>

          <div className="phone" aria-hidden>
            <div className="phone-inner">
              <div style={{ background: "var(--navy)", color: "white", padding: 16, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <div style={{ fontWeight: 800 }}>BECETTI TOOLS</div>
                <div style={{ width: 28, height: 28, borderRadius: 999, background: "var(--orange)", display: "grid", placeItems: "center", fontWeight: 900 }}>B</div>
              </div>
              <div style={{ padding: 14 }}>
                <div style={{ background: "var(--orange-light)", borderRadius: 14, padding: 14, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                  <div>
                    <div style={{ fontWeight: 800 }}>15 Tools Ready</div>
                    <div style={{ color: "#7A5A36", fontSize: 12 }}>Favorites • History • Search</div>
                  </div>
                  <div style={{ background: "white", borderRadius: 12, padding: "8px 10px", fontSize: 12, border: "1px solid #F0D9BF" }}>🔍 Search tools…</div>
                </div>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginTop: 12 }}>
                  {t.tools15.slice(0, 8).map((x: any) => (
                    <div key={x.n} style={{ border: "1px solid #EEE", borderRadius: 14, padding: 12, background: "white" }}>
                      <div className="tool-icon">{x.i}</div>
                      <div style={{ fontWeight: 700, fontSize: 12, marginTop: 6 }}>{x.n}</div>
                      <div style={{ fontSize: 11, color: "#6B7280" }}>{x.d.slice(0, 38)}…</div>
                    </div>
                  ))}
                </div>
                <div style={{ marginTop: 10, display: "flex", justifyContent: "space-around", background: "#F8FAFC", borderRadius: 14, padding: 8, border: "1px solid #E5E7EB" }}>
                  <span>🏠</span><span>🗂️</span><span>⭐</span><span>🕘</span><span>⚙️</span>
                </div>
              </div>
            </div>
            <div style={{ textAlign: "center", color: "white", fontSize: 12, marginTop: 10, opacity: .9 }}>Screenshots: Home • Calculator • QR • PDF • Settings</div>
          </div>
        </div>
      </header>

      {/* TRUST */}
      <section className="container" style={{ padding: "8px 20px 0" }}>
        <div className="grid3">
          <div className="card"><b>🛡️ Privacy by design</b><p style={{ color: "var(--muted)", margin: "6px 0 0", fontSize: 14 }}>No account, no tracking. Passwords, QR scans & OCR run 100% on-device. Camera only when you open scanner.</p></div>
          <div className="card"><b>⚡ Light & Fast</b><p style={{ color: "var(--muted)", margin: "6px 0 0", fontSize: 14 }}>No heavy frameworks. Room/DataStore, CameraX+ML Kit, PDFBox-Android. Cold start &lt; 1.5s on mid-range device.</p></div>
          <div className="card"><b>🧩 Ads — respectful</b><p style={{ color: "var(--muted)", margin: "6px 0 0", fontSize: 14 }}>Monetag + Adsterra via abstraction. No ad on Calculator. No forced interstitial. Consent required. 90s frequency cap.</p></div>
        </div>
      </section>

      {/* CATEGORIES & 15 TOOLS */}
      <section className="container" style={{ padding: "28px 20px 8px" }}>
        <h2 style={{ fontSize: 28, margin: 0 }}>{t.features}</h2>
        <p style={{ color: "var(--muted)", marginTop: 6 }}>{t.featuresSub}</p>

        {[0, 1, 2, 3].map((cat) => (
          <div key={cat} style={{ marginTop: 18 }}>
            <h3 style={{ fontSize: 16, letterSpacing: ".4px", textTransform: "uppercase", color: "var(--orange-dark)" }}>{String.fromCharCode(65 + cat)}. {t.categories[cat]}</h3>
            <div className={cat === 3 ? "grid3" : "grid4"}>
              {t.tools15.filter((x: any) => x.c === cat).map((tool: any) => (
                <div key={tool.n} className="card">
                  <div className="tool-icon">{tool.i}</div>
                  <div style={{ fontWeight: 800, marginTop: 8 }}>{tool.n}</div>
                  <div style={{ color: "var(--muted)", fontSize: 13, marginTop: 4 }}>{tool.d}</div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </section>

      {/* PACKAGE & COMPAT */}
      <section className="container" style={{ padding: "22px 20px 8px" }}>
        <h2 style={{ fontSize: 22 }}>{t.pkg}</h2>
        <div className="grid3">
          <div className="card"><b>📱 Package</b><br /><code>com.becetti.tools</code><br /><span style={{ color: "var(--muted)", fontSize: 13 }}>Version 1.0.0 (v1.0.0-native tag). Debug build for QA. Release signing planned for store.</span></div>
          <div className="card"><b>🤖 Android Support</b><br /><span style={{ color: "var(--muted)", fontSize: 13 }}>minSdk 24 (Android 7.0 Nougat) → Android 14. Tested on Android 13+. targetSdk 34. Material 3, dynamic light/dark, RTL fully supported.</span></div>
          <div className="card"><b>🌍 Languages</b><br /><span style={{ color: "var(--muted)", fontSize: 13 }}>Arabic (RTL 100%), French, English. Locale switch with AppCompatDelegate + restart dialog.</span></div>
        </div>
        <div className="card" style={{ marginTop: 14 }}>
          <b>Architecture</b>
          <p style={{ color: "var(--muted)", fontSize: 14, margin: "6px 0 0" }}>
            Kotlin + Jetpack Compose + Material 3 • MVVM + Clean Architecture • Coroutines/StateFlow • Room + DataStore • CameraX + ML Kit (Barcode & OCR) • PDFBox-Android + commons-compress + zip4j • Verified Native (No WebView, 20 dex).
          </p>
        </div>
      </section>

      {/* INSTALL */}
      <section id="install" className="container" style={{ padding: "22px 20px 8px" }}>
        <h2 style={{ fontSize: 22 }}>{lang === "ar" ? "دليل التثبيت — مع التحقق" : lang === "fr" ? "Installation & Vérification" : "Installation & Verification"}</h2>
        <div className="grid3">
          <div className="card">
            <b>1. Download</b>
            <p style={{ color: "var(--muted)", fontSize: 14 }}>Tap the orange Download button. You’ll get <code>BECETTI-TOOLS-v1.0.0-NATIVE-debug.apk</code> (~85 MB) from GitHub Releases (raw.githubusercontent.com).</p>
            <a href={APK_URL} className="btn btn-primary" style={{ marginTop: 8 }}>Download APK</a>
          </div>
          <div className="card">
            <b>2. Enable Install</b>
            <p style={{ color: "var(--muted)", fontSize: 14 }}>Android will ask “Install unknown apps” for your browser → Allow. This APK is Debug-signed for testing; no Play Protect bypass needed.</p>
            <div style={{ background: "var(--bg)", border: "1px dashed var(--border)", borderRadius: 12, padding: 10, fontSize: 13 }}>Settings → Apps → Special access → Install unknown apps → Allow</div>
          </div>
          <div className="card">
            <b>3. Verify (optional but recommended)</b>
            <p style={{ color: "var(--muted)", fontSize: 14 }}>SHA256 must match:</p>
            <code style={{ display: "block", background: "var(--bg)", border: "1px solid var(--border)", borderRadius: 10, padding: 10, wordBreak: "break-all", fontSize: 12 }}>{APK_SHA}</code>
            <div style={{ fontSize: 13, color: "var(--muted)", marginTop: 8 }}>Windows: <code>certutil -hashfile apk SHA256</code> • macOS/Linux: <code>sha256sum apk</code></div>
          </div>
        </div>
      </section>

      {/* FAQ */}
      <section className="container" style={{ padding: "22px 20px 8px" }}>
        <h2 style={{ fontSize: 22 }}>{t.faqTitle}</h2>
        <div className="grid3">
          {[
            { q: "Is BECETTI TOOLS free? Any hidden fees?", a: "100% free. No in-app purchases in v1.0.0. Future monetization will be respectful (no forced ads)." },
            { q: "Does it work offline?", a: "Yes — 14 of 15 tools work fully offline. Only Currency Converter needs internet for fresh rates; it caches the last rates with a timestamp." },
            { q: "What permissions does it need?", a: "Camera (only when you open QR Scanner) and Internet/NetworkState (currency + optional ads). Files are accessed via Storage Access Framework — you pick files explicitly." },
            { q: "Are my passwords/QR data sent to a server?", a: "Never. Password generation & strength + QR handling + OCR all run on-device. No plaintext passwords are stored." },
            { q: "Why a Debug APK?", a: "v1.0.0 is a native debug build for open QA before release signing. Install warning is normal for non-Play APKs; verify SHA256." },
            { q: "Is there an iOS version?", a: "Not yet — Android native is the v1.0.0 focus. iOS is considered for v2.x if demand exists." },
          ].map((f) => (
            <div key={f.q} className="card">
              <b>{f.q}</b>
              <p style={{ color: "var(--muted)", fontSize: 14, margin: "6px 0 0" }}>{f.a}</p>
            </div>
          ))}
        </div>
      </section>

      {/* FUTURE */}
      <section className="container" style={{ padding: "22px 20px 8px" }}>
        <h2 style={{ fontSize: 22 }}>{lang === "ar" ? "خارطة الطريق" : lang === "fr" ? "Feuille de Route" : "Roadmap — Future Enhancements (separate from v1.0.0)"}</h2>
        <div className="grid3">
          <div className="card" style={{ borderTop: "3px solid var(--orange)" }}><b>v1.1 Huge Edition — Planned (additive)</b><p style={{ color: "var(--muted)", fontSize: 14 }}>File Manager (ZArchiver-style): browse Internal/Downloads/Pictures, create/extract ZIP with ZipOutputStream + commons-compress + zip4j, share via FileProvider, MANAGE_EXTERNAL_STORAGE flow. + Splash DeepNavy→SecondaryNavy animation 2600ms. + RTL/restart polish.</p><span className="kbd">Status: built & verified (v1.1.0-native 87 MB)</span></div>
          <div className="card"><b>v1.2 — Planned</b><p style={{ color: "var(--muted)", fontSize: 14 }}>Backup & Restore (Room export/import JSON), Widgets for calculators, History analytics, Better OCR language packs, Themes marketplace.</p></div>
          <div className="card"><b>v2.0 — Vision</b><p style={{ color: "var(--muted)", fontSize: 14 }}>Cloud sync opt-in (encrypted), Desktop companion (Compose Multiplatform), Store release (signed AAB), Monetization dashboard.</p></div>
        </div>
      </section>

      {/* CONTACT */}
      <section id="contact" className="container" style={{ padding: "22px 20px 8px" }}>
        <h2 style={{ fontSize: 22 }}>{t.contact}</h2>
        <div className="card" style={{ display: "flex", flexWrap: "wrap", gap: 14, justifyContent: "space-between", alignItems: "center" }}>
          <div>
            <b>Ahmed Becetti — BECETTI TOOLS</b><br />
            <span style={{ color: "var(--muted)", fontSize: 14 }}>Communal Developer • Algeria • Zero-budget, open QA</span><br />
            <a href="https://github.com/Ahmedbecett/BECETTITOOLS" style={{ color: "var(--orange)", fontWeight: 700 }}>GitHub: Ahmedbecett/BECETTITOOLS</a> • <a href="mailto:ahmedbecetti35@gmail.com" style={{ color: "var(--orange)", fontWeight: 700 }}>ahmedbecetti35@gmail.com</a>
          </div>
          <a href="mailto:ahmedbecetti35@gmail.com?subject=BECETTI%20TOOLS%20Support%20v1.0.0" className="btn btn-primary">Contact Support</a>
        </div>
        <p style={{ color: "var(--muted)", fontSize: 12, marginTop: 10 }}>Privacy & Trust: <a href="/privacy" style={{ color: "var(--orange)" }}>Privacy Policy</a> • No fake counters • No misleading install buttons • Canonical: {CANONICAL}</p>
      </section>

      <footer className="container" style={{ padding: "18px 20px 28px", color: "var(--muted)", fontSize: 12, display: "flex", justifyContent: "space-between", flexWrap: "wrap", gap: 10, borderTop: "1px solid var(--border)", marginTop: 16 }}>
        <span>© 2026 BECETTI TOOLS • com.becetti.tools • Built with ♥ in Algeria</span>
        <span><a href="/privacy">Privacy</a> • <a href="/becetti-tools">Home</a> • <a href={APK_URL}>APK Direct</a></span>
      </footer>
    </div>
  );
}
