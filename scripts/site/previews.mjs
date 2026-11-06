/**
 * Illustrative UI previews.
 *
 * These are vector mock-ups rebuilt from the app's Compose screens (HomeScreen,
 * CalculatorScreen, QrGeneratorScreen, PdfScreens, UnitConverterScreen,
 * SettingsScreen) and its palette. They are NOT device screenshots and every
 * caption on the site says so. Replacing them with real captures is a matter of
 * dropping PNG files in and changing the <img src> list in site previews data.
 */

const W = 360;
const H = 740;

const light = {
  bg: "#F4F6F8",
  surface: "#FFFFFF",
  surface2: "#EEF1F5",
  border: "#E1E6EC",
  text: "#202938",
  muted: "#788394",
  orange: "#F47B20",
  navy: "#172235",
  navy2: "#263B57",
  onNavy: "#FFFFFF",
};

const dark = {
  bg: "#131D2C",
  surface: "#263B57",
  surface2: "#1C2A3E",
  border: "#38507A",
  text: "#EEF2F7",
  muted: "#9AABC2",
  orange: "#FF9445",
  navy: "#0E1622",
  navy2: "#1B283C",
  onNavy: "#FFFFFF",
};

const font =
  "system-ui, -apple-system, 'Segoe UI', Roboto, 'Noto Naskh Arabic', 'Droid Sans', Tahoma, Arial, sans-serif";

const esc = (s) =>
  String(s).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");

function rect(x, y, w, h, { rx = 0, fill = "none", stroke = "none", sw = 1, opacity = 1 } = {}) {
  return `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="${rx}" fill="${fill}"${
    stroke !== "none" ? ` stroke="${stroke}" stroke-width="${sw}"` : ""
  }${opacity !== 1 ? ` opacity="${opacity}"` : ""}/>`;
}

function text(x, y, str, { size = 13, fill = "#202938", weight = 400, anchor = "start", opacity = 1, dir } = {}) {
  return `<text x="${x}" y="${y}" font-family="${font}" font-size="${size}" font-weight="${weight}" fill="${fill}" text-anchor="${anchor}"${
    opacity !== 1 ? ` opacity="${opacity}"` : ""
  }${dir ? ` direction="${dir}"` : ""}>${esc(str)}</text>`;
}

function circle(cx, cy, r, { fill = "none", stroke = "none", sw = 1.5 } = {}) {
  return `<circle cx="${cx}" cy="${cy}" r="${r}" fill="${fill}"${
    stroke !== "none" ? ` stroke="${stroke}" stroke-width="${sw}"` : ""
  }/>`;
}

function line(x1, y1, x2, y2, { stroke = "#788394", sw = 1.5, cap = "round" } = {}) {
  return `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="${stroke}" stroke-width="${sw}" stroke-linecap="${cap}"/>`;
}

function path(d, { stroke = "none", fill = "none", sw = 1.5 } = {}) {
  return `<path d="${d}" fill="${fill}"${stroke !== "none" ? ` stroke="${stroke}" stroke-width="${sw}" stroke-linecap="round" stroke-linejoin="round"` : ""}/>`;
}

function pill(x, y, w, h, label, { active = false, c = light, size = 11, anchorMiddle = true } = {}) {
  return [
    rect(x, y, w, h, {
      rx: h / 2,
      fill: active ? c.orange : c.surface,
      stroke: active ? c.orange : c.border,
    }),
    text(anchorMiddle ? x + w / 2 : x + 12, y + h / 2 + 4, label, {
      size,
      weight: active ? 700 : 500,
      fill: active ? "#FFFFFF" : c.muted,
      anchor: anchorMiddle ? "middle" : "start",
    }),
  ].join("");
}

function statusBar(c, { dark: darkBar = false } = {}) {
  const ink = darkBar ? "#FFFFFF" : c.text;
  return [
    text(20, 24, "9:41", { size: 11, weight: 700, fill: ink }),
    rect(292, 16, 16, 8, { rx: 2, fill: ink, opacity: 0.65 }),
    rect(312, 16, 4, 8, { rx: 1.5, fill: ink, opacity: 0.45 }),
    line(268, 15, 268, 24, { stroke: ink, sw: 1.6, cap: "butt" }),
    line(263, 18, 263, 24, { stroke: ink, sw: 1.6, cap: "butt" }),
    line(258, 21, 258, 24, { stroke: ink, sw: 1.6, cap: "butt" }),
    path("M228 17a11 11 0 0 1 15 0", { stroke: ink, sw: 1.6 }),
    path("M232 21a6 6 0 0 1 7 0", { stroke: ink, sw: 1.6 }),
  ].join("");
}

function appBar(c, title, { rtl = false, dark: darkBar = false } = {}) {
  const ink = darkBar ? "#FFFFFF" : c.text;
  // Back arrow sits on the logical start side, clear of the status-bar clock.
  const arrow = rtl ? `M${W - 34} 46l7 6-7 6` : "M30 46l-7 6 7 6";
  return [
    rect(0, 0, W, 92, { fill: c.bg }),
    statusBar(c, { dark: darkBar }),
    path(arrow, { stroke: ink, sw: 2 }),
    text(rtl ? W - 42 : 48, 57, title, {
      size: 16,
      weight: 700,
      fill: ink,
      anchor: rtl ? "end" : "start",
    }),
    line(0, 91, W, 91, { stroke: c.border, sw: 1 }),
  ].join("");
}

function bottomNav(c, active = 0, { rtl = false } = {}) {
  const y = 680;
  const labels = rtl
    ? ["الرئيسية", "الفئات", "المفضلة", "السجل", "الإعدادات"]
    : ["Home", "Categories", "Favorites", "History", "Settings"];
  const items = labels.map((label, i) => {
    const cx = 36 + i * ((W - 72) / 4);
    const on = i === active;
    const color = on ? c.orange : c.muted;
    const stroke = { stroke: color, sw: 1.6 };
    let glyph = "";
    if (i === 0) {
      glyph = path(`M${cx - 7} ${y + 18}l7 -7 7 7v10h-14z`, stroke);
    } else if (i === 1) {
      glyph = [
        rect(cx - 7, y + 19, 6, 6, { rx: 1.6, fill: color }),
        rect(cx + 1, y + 19, 6, 6, { rx: 1.6, fill: color }),
        rect(cx - 7, y + 27, 6, 6, { rx: 1.6, fill: color }),
        rect(cx + 1, y + 27, 6, 6, { rx: 1.6, fill: color }),
      ].join("");
    } else if (i === 2) {
      glyph = path(`M${cx} ${y + 30}l-7 -6v-9h14v9z`, stroke);
    } else if (i === 3) {
      glyph = [
        circle(cx, y + 23, 7, { stroke: color, sw: 1.6 }),
        path(`M${cx} ${y + 23}v-4M${cx} ${y + 23}h3`, { stroke: color, sw: 1.5 }),
      ].join("");
    } else {
      glyph = [
        path(`M${cx - 7} ${y + 20}h14M${cx - 7} ${y + 27}h14`, { stroke: color, sw: 1.6 }),
        circle(cx - 2, y + 20, 2.2, { fill: c.surface, stroke: color, sw: 1.4 }),
        circle(cx + 3, y + 27, 2.2, { fill: c.surface, stroke: color, sw: 1.4 }),
      ].join("");
    }
    return `${glyph}${text(cx, y + 50, label, {
      size: 9,
      fill: color,
      anchor: "middle",
      weight: on ? 700 : 500,
    })}`;
  });
  return [
    rect(0, y, W, H - y, { fill: c.surface }),
    line(0, y, W, y, { stroke: c.border, sw: 1 }),
    items.join(""),
  ].join("");
}

function shell(c, content, { nav = true, rtl = false, active = 0 } = {}) {
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${W} ${H}" width="${W}" height="${H}" role="img">
  <rect width="${W}" height="${H}" fill="${c.bg}"/>
  ${content}
  ${nav ? bottomNav(c, active, { rtl }) : ""}
</svg>
`;
}

/* --------------------------------- screens --------------------------------- */

function homeScreen() {
  const c = light;
  const parts = [];
  parts.push(rect(0, 0, W, 96, { fill: c.bg }));
  parts.push(statusBar(c));
  parts.push(text(20, 62, "BECETTI TOOLS", { size: 21, weight: 800, fill: c.orange }));
  parts.push(text(20, 82, "Everything you need. One smart toolbox.", { size: 10.5, fill: c.muted }));

  // search field
  parts.push(rect(20, 98, 320, 38, { rx: 10, fill: c.surface, stroke: c.border }));
  parts.push(circle(40, 117, 5.2, { stroke: c.muted, sw: 1.5 }));
  parts.push(line(44, 121, 47, 124, { stroke: c.muted, sw: 1.5 }));
  parts.push(text(56, 121, "Search tools…", { size: 12, fill: c.muted }));

  // favorites and recents
  parts.push(text(20, 162, "Favorites & Recents", { size: 12.5, weight: 700, fill: c.text }));
  parts.push(pill(20, 172, 88, 26, "unit_converter", { c, size: 9.5 }));
  parts.push(pill(114, 172, 74, 26, "calc_scientific", { c, size: 9.5 }));
  parts.push(pill(194, 172, 62, 26, "qr_generator", { c, size: 9.5 }));

  // categories
  parts.push(text(20, 224, "Categories", { size: 12.5, weight: 700, fill: c.text }));
  const cats = [
    ["Calculator & Converters", "QR & Security"],
    ["Documents & Files", "Text Utilities"],
    ["Image Utilities", "Date & Time"],
  ];
  cats.forEach((row, r) => {
    row.forEach((label, i) => {
      const x = 20 + i * 165;
      const y = 234 + r * 56;
      parts.push(rect(x, y, 155, 46, { rx: 10, fill: c.surface, stroke: c.border }));
      const words = label.split(" ");
      if (words.length > 2) {
        parts.push(text(x + 12, y + 20, words.slice(0, 2).join(" "), { size: 10, weight: 600, fill: c.text }));
        parts.push(text(x + 12, y + 32, words.slice(2).join(" "), { size: 10, weight: 600, fill: c.text }));
      } else {
        parts.push(text(x + 12, y + 27, label, { size: 10, weight: 600, fill: c.text }));
      }
    });
  });

  // all tools
  parts.push(text(20, 442, "All Tools", { size: 12.5, weight: 700, fill: c.text }));
  const tools = [
    ["Scientific Calculator", "Trig, log, sqrt, history"],
    ["Unit Converter", "Length · Weight · Temp · Area"],
    ["Currency Converter", "ECB reference rates, cached"],
  ];
  tools.forEach(([name, desc], i) => {
    const y = 452 + i * 60;
    parts.push(rect(20, y, 320, 52, { rx: 10, fill: c.surface, stroke: c.border }));
    parts.push(text(32, y + 21, name, { size: 11.5, weight: 650, fill: c.text }));
    parts.push(text(32, y + 37, desc, { size: 9.5, fill: c.muted }));
    parts.push(path(`M320 ${y + 20}l5 6-5 6`, { stroke: c.muted, sw: 1.6 }));
  });

  // sponsored row (labelled, as in the app)
  parts.push(rect(20, 636, 320, 34, { rx: 8, fill: "#FFF0E2" }));
  parts.push(text(180, 657, "Sponsored Offers — Tap to explore", { size: 9, fill: c.orange, weight: 600, anchor: "middle" }));

  return shell(c, parts.join("\n  "), { active: 0 });
}

function calculatorScreen() {
  const c = dark;
  const parts = [];
  parts.push(rect(0, 0, W, H, { fill: c.bg }));
  parts.push(appBar(c, "Calculator", { dark: true }));

  // display
  parts.push(text(336, 140, "2 + 2 × 3", { size: 26, weight: 700, fill: c.text, anchor: "end" }));
  parts.push(text(336, 184, "= 8", { size: 34, weight: 800, fill: c.orange, anchor: "end" }));
  parts.push(text(336, 208, "History · 5 entries", { size: 10, fill: c.muted, anchor: "end" }));

  // function row
  const rows = [
    ["sin", "cos", "tan", "√"],
    ["log", "ln", "%", "C"],
    ["7", "8", "9", "÷"],
    ["4", "5", "6", "×"],
    ["1", "2", "3", "−"],
    ["0", ".", "=", "+"],
  ];
  rows.forEach((row, r) => {
    row.forEach((key, i) => {
      const x = 20 + i * 81;
      const y = 236 + r * 66;
      const isOp = i === 3 || key === "=";
      const isFn = r < 2 && !isOp;
      parts.push(
        rect(x, y, 72, 56, {
          rx: 14,
          fill: key === "=" ? c.orange : isOp ? c.navy2 : isFn ? c.surface2 : c.surface,
          stroke: key === "=" || isOp || isFn ? "none" : c.border,
        })
      );
      parts.push(
        text(x + 36, y + 36, key, {
          size: key === "=" ? 22 : 18,
          weight: 700,
          fill: key === "=" ? "#FFFFFF" : isFn ? c.muted : c.text,
          anchor: "middle",
        })
      );
    });
  });

  return shell(c, parts.join("\n  "), { nav: false });
}

function qrScreen() {
  const c = light;
  const parts = [];
  parts.push(appBar(c, "QR Generator"));
  parts.push(pill(20, 106, 62, 30, "Text", { active: true, c }));
  parts.push(pill(88, 106, 54, 30, "URL", { c }));
  parts.push(pill(148, 106, 62, 30, "Wi-Fi", { c }));
  parts.push(pill(216, 106, 74, 30, "Contact", { c }));

  parts.push(rect(20, 150, 320, 40, { rx: 10, fill: c.surface, stroke: c.border }));
  parts.push(text(34, 175, "https://becetti.tools", { size: 12, fill: c.text }));

  // QR modules
  const ox = 68;
  const oy = 210;
  const mod = 5.2;
  const n = 43;
  const qr = [];
  qr.push(rect(ox - 12, oy - 12, n * mod + 24, n * mod + 24, { rx: 12, fill: "#FFFFFF", stroke: c.border }));
  const finder = (gx, gy) => {
    qr.push(rect(ox + gx * mod, oy + gy * mod, 7 * mod, 7 * mod, { fill: c.navy }));
    qr.push(rect(ox + (gx + 1) * mod, oy + (gy + 1) * mod, 5 * mod, 5 * mod, { fill: "#FFFFFF" }));
    qr.push(rect(ox + (gx + 2) * mod, oy + (gy + 2) * mod, 3 * mod, 3 * mod, { fill: c.navy }));
  };
  // deterministic pseudo-random modules
  let seed = 7;
  const rnd = () => {
    seed = (seed * 1103515245 + 12345) % 2147483648;
    return seed / 2147483648;
  };
  for (let gy = 0; gy < n; gy++) {
    for (let gx = 0; gx < n; gx++) {
      const inFinder =
        (gx < 9 && gy < 9) || (gx > n - 10 && gy < 9) || (gx < 9 && gy > n - 10);
      if (inFinder) continue;
      if (rnd() > 0.52) {
        qr.push(
          rect(ox + gx * mod, oy + gy * mod, mod - 0.35, mod - 0.35, { rx: 0.8, fill: c.navy })
        );
      }
    }
  }
  finder(0, 0);
  finder(n - 7, 0);
  finder(0, n - 7);
  parts.push(qr.join(""));

  parts.push(rect(20, 486, 320, 44, { rx: 12, fill: c.orange }));
  parts.push(text(180, 513, "Export PNG to Pictures/Becetti", { size: 12, weight: 700, fill: "#FFFFFF", anchor: "middle" }));
  parts.push(
    text(180, 556, "Saved locally · the QR content never leaves the device", {
      size: 9.5,
      fill: c.muted,
      anchor: "middle",
    })
  );

  return shell(c, parts.join("\n  "), { active: 1 });
}

function pdfScreen() {
  const c = light;
  const parts = [];
  parts.push(appBar(c, "Image to PDF"));
  parts.push(rect(20, 106, 320, 42, { rx: 12, fill: c.surface, stroke: c.border }));
  parts.push(text(36, 132, "Choose images (Storage Access Framework)", { size: 10.5, fill: c.muted }));

  const thumbs = [
    ["1", "#D9E4F2"],
    ["2", "#F2E2D2"],
    ["3", "#DCEFE4"],
  ];
  thumbs.forEach(([n, tint], i) => {
    const x = 24 + i * 104;
    parts.push(rect(x, 166, 92, 96, { rx: 10, fill: tint, stroke: c.border }));
    parts.push(circle(x + 46, 202, 12, { fill: "#FFFFFF", stroke: c.muted }));
    parts.push(path(`M${x + 20} 246l18-20 14 15 10-11 12 16z`, { fill: "#FFFFFF", stroke: c.muted, sw: 1 }));
    parts.push(circle(x + 14, 180, 8, { fill: c.navy }));
    parts.push(text(x + 14, 183, n, { size: 9, fill: "#FFFFFF", weight: 700, anchor: "middle" }));
  });

  parts.push(text(20, 292, "Page size", { size: 12.5, weight: 700, fill: c.text }));
  parts.push(pill(20, 302, 58, 30, "A4", { active: true, c }));
  parts.push(pill(84, 302, 58, 30, "A3", { c }));
  parts.push(pill(148, 302, 76, 30, "Letter", { c }));

  parts.push(text(20, 368, "Order = selection order", { size: 10, fill: c.muted }));
  for (let i = 0; i < 3; i++) {
    const y = 384 + i * 52;
    parts.push(rect(20, y, 320, 44, { rx: 10, fill: c.surface, stroke: c.border }));
    parts.push(rect(32, y + 10, 24, 24, { rx: 6, fill: "#FFF0E2" }));
    parts.push(text(44, y + 27, String(i + 1), { size: 11, weight: 700, fill: c.orange, anchor: "middle" }));
    parts.push(text(66, y + 27, `IMG_2026${i + 1}.jpg`, { size: 10.5, fill: c.text }));
    parts.push(path(`M312 ${y + 15}l10 0M312 ${y + 22}l10 0M312 ${y + 29}l10 0`, { stroke: c.muted, sw: 1.6 }));
  }

  parts.push(rect(20, 552, 320, 46, { rx: 12, fill: c.orange }));
  parts.push(text(180, 580, "Create PDF", { size: 13, weight: 700, fill: "#FFFFFF", anchor: "middle" }));
  parts.push(rect(20, 610, 320, 46, { rx: 10, fill: "#EAF6EF", stroke: "#C8E6D6" }));
  parts.push(text(36, 638, "Saved to Documents/Becetti · originals untouched", { size: 9.5, fill: "#1E7A4E" }));

  return shell(c, parts.join("\n  "), { active: 1 });
}

function converterScreen() {
  const c = dark;
  const parts = [];
  parts.push(rect(0, 0, W, H, { fill: c.bg }));
  parts.push(appBar(c, "Unit Converter", { dark: true }));

  const chipRows = [
    ["Length", "Weight", "Temp"],
    ["Area", "Volume", "Speed"],
  ];
  chipRows.forEach((row, r) => {
    let cx = 20;
    row.forEach((cat) => {
      const w = cat.length * 6.6 + 30;
      parts.push(pill(cx, 104 + r * 36, w, 28, cat, { active: cat === "Length", c, size: 10.5 }));
      cx += w + 8;
    });
  });

  const field = (y, label, value, unit) => [
    rect(20, y, 320, 74, { rx: 12, fill: c.surface, stroke: c.border }),
    text(34, y + 24, label, { size: 10, fill: c.muted }),
    text(34, y + 56, value, { size: 26, weight: 750, fill: c.text }),
    rect(214, y + 30, 112, 30, { rx: 8, fill: c.surface2, stroke: c.border }),
    text(270, y + 50, unit, { size: 10.5, fill: c.text, anchor: "middle" }),
  ];
  parts.push(field(168, "From", "1", "Kilometre").join(""));
  parts.push(circle(180, 274, 18, { fill: c.orange }));
  parts.push(
    path("M175 266v16M175 266l-3.5 4M175 266l3.5 4M185 282v-16M185 282l-3.5-4M185 282l3.5-4", {
      stroke: "#FFFFFF",
      sw: 1.7,
    })
  );
  parts.push(field(306, "To", "1000", "Metre").join(""));

  parts.push(rect(20, 404, 320, 82, { rx: 12, fill: c.surface2, stroke: c.border }));
  parts.push(text(36, 432, "Result", { size: 10, fill: c.muted }));
  parts.push(text(36, 466, "1 km = 1000 m", { size: 18, weight: 700, fill: c.orange }));
  parts.push(text(180, 520, "Works offline · no network required", { size: 9.5, fill: c.muted, anchor: "middle" }));

  return shell(c, parts.join("\n  "), { active: 1 });
}

function settingsRtlScreen() {
  const c = light;
  const parts = [];
  parts.push(rect(0, 0, W, H, { fill: c.bg }));
  parts.push(appBar(c, "الإعدادات", { rtl: true }));

  const right = 324; // right padding inside a card that spans 20..340
  const rows = [
    { label: "اللغة", value: "العربية", kind: "chevron" },
    { label: "المظهر", value: "داكن", kind: "toggle" },
    { label: "الموافقة على الإعلانات", value: "مفعّلة", kind: "toggleOn" },
    { label: "سياسة الخصوصية", value: "", kind: "chevron" },
    { label: "عن التطبيق", value: "الإصدار 1.0.0", kind: "none" },
  ];
  rows.forEach((row, i) => {
    const y = 108 + i * 64;
    parts.push(rect(20, y, 320, 56, { rx: 12, fill: c.surface, stroke: c.border }));
    parts.push(
      text(right, y + 35, row.label, {
        size: 12.5,
        weight: 650,
        fill: c.text,
        anchor: "end",
      })
    );
    if (row.value && row.kind !== "toggle" && row.kind !== "toggleOn") {
      parts.push(text(70, y + 35, row.value, { size: 10.5, fill: c.muted, anchor: "start" }));
    }
    if (row.kind === "chevron") {
      parts.push(path(`M54 ${y + 20}l-8 8 8 8`, { stroke: c.muted, sw: 1.7 }));
    }
    if (row.kind === "toggle" || row.kind === "toggleOn") {
      const on = row.kind === "toggleOn";
      parts.push(rect(36, y + 16, 46, 24, { rx: 12, fill: on ? c.orange : c.surface2, stroke: on ? "none" : c.border }));
      parts.push(circle(on ? 70 : 48, y + 28, 9, { fill: "#FFFFFF" }));
    }
  });

  // Local-only storage note, right-aligned for RTL
  parts.push(rect(20, 444, 320, 118, { rx: 12, fill: c.surface2, stroke: c.border }));
  const notes = [
    "الملاحظات تُحفظ على الجهاز فقط",
    "لا يُرسل أي محتوى إلى خادم",
    "الكاميرا تُطلب عند فتح الماسح فقط",
    "كلمات المرور لا تُخزَّن ولا تُرسَل",
  ];
  notes.forEach((n, i) => {
    parts.push(
      text(right - 4, 476 + i * 24, n, { size: 11, fill: c.muted, anchor: "start", dir: "rtl" })
    );
  });

  parts.push(text(180, 600, "أدوات بي سيتي", { size: 17, weight: 800, fill: c.orange, anchor: "middle" }));
  parts.push(text(180, 624, "كل ما تحتاجه. صندوق أدوات ذكي واحد.", { size: 10.5, fill: c.muted, anchor: "middle" }));

  return shell(c, parts.join("\n  "), { active: 4, rtl: true });
}

export const previews = {
  home: homeScreen,
  calculator: calculatorScreen,
  qr: qrScreen,
  pdf: pdfScreen,
  converter: converterScreen,
  "settings-rtl": settingsRtlScreen,
};

export function renderPreview(id) {
  const fn = previews[id];
  if (!fn) throw new Error(`Unknown preview: ${id}`);
  return fn();
}
