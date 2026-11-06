#!/usr/bin/env node
/**
 * Store artwork generator — Google Play listing assets for BECETTI TOOLS.
 *
 * These screens are REBUILT from the app's own Compose layout and palette, using the same
 * strings the APK ships. They are illustrative UI previews, not device captures, and every
 * image says so in its caption strip. Nothing here reads or modifies the APK.
 *
 * Requires ImageMagick 6 (`convert`). There is no SVG rasteriser and no fontconfig in the
 * build image, so everything is drawn with primitives and the bundled DejaVu faces.
 *
 *   node scripts/make-store-assets.mjs            # writes store/play/*.png
 */

import { execFileSync } from "node:child_process";
import { mkdirSync, existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const here = path.dirname(fileURLToPath(import.meta.url));
const repoRoot = path.resolve(here, "..");
const outDir = path.join(repoRoot, "store", "play");
mkdirSync(outDir, { recursive: true });

const FONT_DIR = "/usr/share/fonts/truetype/dejavu";
const FONTS = {
  regular: path.join(FONT_DIR, "DejaVuSans.ttf"),
  bold: path.join(FONT_DIR, "DejaVuSans-Bold.ttf"),
  mono: path.join(FONT_DIR, "DejaVuSansMono.ttf"),
  monoBold: path.join(FONT_DIR, "DejaVuSansMono-Bold.ttf"),
};
for (const f of Object.values(FONTS)) {
  if (!existsSync(f)) throw new Error(`missing font: ${f}`);
}

const C = {
  orange: "#F47B20",
  orangeDark: "#D96513",
  orangeLight: "#FFF0E2",
  navy: "#172235",
  navy2: "#263B57",
  bg: "#F4F6F8",
  text: "#202938",
  muted: "#64748B",
  border: "#E4E8ED",
  white: "#FFFFFF",
  captionBg: "#0E1724",
  captionText: "#C7D2E0",
  captionDim: "#8FA3BD",
};

const W = 1080;
const H = 1920;

/* --------------------------------- drawing --------------------------------- */

class Canvas {
  constructor(w, h, bg) {
    this.w = w;
    this.h = h;
    this.args = ["-size", `${w}x${h}`, `xc:${bg}`];
  }
  rect(x1, y1, x2, y2, r, fill, stroke = null, sw = 0) {
    this.args.push("-fill", fill);
    this.args.push("-stroke", stroke || "none", "-strokewidth", String(sw));
    this.args.push("-draw", `roundrectangle ${x1},${y1} ${x2},${y2} ${r},${r}`);
    return this;
  }
  box(x1, y1, x2, y2, fill) {
    this.args.push("-fill", fill, "-stroke", "none");
    this.args.push("-draw", `rectangle ${x1},${y1} ${x2},${y2}`);
    return this;
  }
  circle(cx, cy, r, fill) {
    this.args.push("-fill", fill, "-stroke", "none");
    this.args.push("-draw", `circle ${cx},${cy} ${cx + r},${cy}`);
    return this;
  }
  measure(value, { size = 28, font = "regular" } = {}) {
    const out = execFileSync(
      "convert",
      ["-font", FONTS[font], "-pointsize", String(size), `label:${value}`, "-format", "%w", "info:"],
      { encoding: "utf8" }
    );
    return parseInt(out.trim(), 10);
  }
  /** x is the left edge for align:"start", the centre for align:"center". */
  text(x, y, value, { size = 28, color = C.text, font = "regular", align = "start" } = {}) {
    const left = align === "center" ? Math.round(x - this.measure(value, { size, font }) / 2) : x;
    this.args.push(
      "-font", FONTS[font], "-pointsize", String(size), "-fill", color, "-stroke", "none",
      "-gravity", "NorthWest", "-annotate", `+${left}+${y}`, value
    );
    return this;
  }
  textCentered(cx, y, value, opts = {}) {
    return this.text(cx, y, value, { ...opts, align: "center" });
  }
  run(out) {
    execFileSync("convert", [...this.args, "-strip", out], { stdio: "pipe" });
    return out;
  }
}

/* ------------------------------- page chrome -------------------------------- */

function chrome(screen) {
  const c = new Canvas(W, H, C.bg);
  // status bar
  c.box(0, 0, W, 72, C.navy);
  c.text(48, 22, "9:41", { size: 30, color: C.white, font: "bold" });
  c.text(846, 22, "100%", { size: 28, color: C.captionText, align: "center" });
  c.rect(902, 24, 952, 52, 7, "none", C.captionText, 2);
  c.rect(908, 30, 940, 46, 3, C.captionText);
  c.rect(955, 33, 960, 43, 2, C.captionText);

  // app bar
  c.box(0, 72, W, 250, C.navy);
  c.text(48, 112, "BECETTI TOOLS", { size: 46, color: C.white, font: "bold" });
  c.text(48, 176, screen.subtitle, { size: 28, color: "#FFB27A" });
  c.circle(996, 160, 30, C.navy2);
  c.text(996, 146, "EN", { size: 26, align: "center", color: C.white, font: "bold" });

  // caption strip — every asset carries this
  c.box(0, H - 150, W, H, C.captionBg);
  c.textCentered(W / 2, H - 118, "Illustrative UI preview — not a device screenshot", {
    size: 30,
    color: C.captionText,
    font: "bold",
  });
  c.textCentered(W / 2, H - 72, `BECETTI TOOLS v1.0.0 · com.becetti.tools · ${screen.slug}`, {
    size: 24,
    color: C.captionDim,
  });
  return c;
}

/* --------------------------------- screens ---------------------------------- */

const ROWS = [
  ["Scientific Calculator", "Arithmetic, trig, log, local history", "="],
  ["QR Scanner", "Decoded on the device by ML Kit", "▢"],
  ["Image to PDF", "A4, A3 or Letter · originals untouched", "▤"],
  ["PDF Merger", "Combine files in selection order", "▥"],
  ["Password Generator", "4 to 64 characters, SecureRandom", "#"],
  ["Notes Manager", "Local Room database, no account", "✎"],
];

function screenHome() {
  const c = chrome({ slug: "Home", subtitle: "Everything you need. One smart toolbox." });
  c.rect(48, 290, 1032, 396, 30, C.white, C.border, 2);
  c.text(88, 322, "Search tools, e.g. QR or PDF", { size: 30, color: C.muted });

  const chips = ["All 15", "Calculator", "QR & Security", "Documents"];
  let x = 48;
  for (const [i, chip] of chips.entries()) {
    const w = 34 + chip.length * 17;
    c.rect(x, 424, x + w, 484, 30, i === 0 ? C.orange : C.white, i === 0 ? null : C.border, 2);
    c.text(x + w / 2, 440, chip, { size: 26, align: "center", color: i === 0 ? C.white : C.text, font: "bold" });
    x += w + 16;
  }

  let y = 520;
  for (const [title, sub, glyph] of ROWS) {
    c.rect(48, y, 1032, y + 150, 28, C.white, C.border, 2);
    c.rect(76, y + 32, 162, y + 118, 22, C.orangeLight);
    c.text(106, y + 52, glyph, { size: 44, color: C.orange, font: "bold" });
    c.text(190, y + 40, title, { size: 36, color: C.text, font: "bold" });
    c.text(190, y + 92, sub, { size: 25, color: C.muted });
    c.text(996, y + 58, ">", { size: 36, color: C.border, font: "bold" });
    y += 166;
  }
  c.rect(48, 1560, 1032, 1660, 26, C.navy);
  c.text(540, 1592, "15 tools · 6 categories · nothing uploaded", {
    size: 28,
    align: "center",
    color: C.white,
    font: "bold",
  });
  return c;
}

function screenCalculator() {
  const c = chrome({ slug: "Scientific Calculator", subtitle: "Every result kept in local history" });
  c.rect(48, 290, 1032, 640, 32, C.white, C.border, 2);
  c.text(88, 330, "128 × 4 + √81", { size: 44, color: C.text });
  c.text(88, 430, "521", { size: 120, color: C.orange, font: "bold" });
  c.text(88, 570, "History: 3 results stored on this device", { size: 26, color: C.muted });

  const keys = [
    ["sin", "cos", "tan", "√"],
    ["7", "8", "9", "÷"],
    ["4", "5", "6", "×"],
    ["1", "2", "3", "−"],
    ["0", ".", "(", "+"],
  ];
  const bw = 232;
  const bh = 150;
  const gap = 20;
  keys.forEach((row, r) => {
    row.forEach((label, i) => {
      const x1 = 48 + i * (bw + gap);
      const y1 = 700 + r * (bh + gap);
      const isOp = i === 3;
      c.rect(x1, y1, x1 + bw, y1 + bh, 26, isOp ? C.navy : C.white, isOp ? null : C.border, 2);
      c.text(x1 + bw / 2, y1 + 54, label, {
        size: 46, align: "center",
        color: isOp ? C.white : C.text,
        font: "bold",
      });
    });
  });
  c.rect(48, 1548, 1032, 1676, 26, C.orange);
  c.text(540, 1592, "=", { size: 54, align: "center", color: C.white, font: "bold" });
  return c;
}

function screenQr() {
  const c = chrome({ slug: "QR Generator", subtitle: "Text, URL, Wi-Fi and contact payloads" });
  c.rect(48, 290, 1032, 1180, 32, C.white, C.border, 2);
  c.text(88, 330, "Wi-Fi network", { size: 34, color: C.text, font: "bold" });
  c.text(88, 386, "WIFI:T:WPA;S:Maison;P:••••••••;;", { size: 26, color: C.muted, font: "mono" });

  // deterministic QR-like module grid (decorative, not a scannable code)
  const mod = 20;
  const ox = 210;
  const oy = 440;
  const n = 33;
  let seed = 20261106;
  const rnd = () => {
    seed = (seed * 1103515245 + 12345) & 0x7fffffff;
    return seed / 0x7fffffff;
  };
  const finder = (cx, cy) => {
    c.box(cx, cy, cx + mod * 7, cy + mod * 7, C.text);
    c.box(cx + mod, cy + mod, cx + mod * 6, cy + mod * 6, C.white);
    c.box(cx + mod * 2, cy + mod * 2, cx + mod * 5, cy + mod * 5, C.text);
  };
  for (let r = 0; r < n; r += 1) {
    for (let col = 0; col < n; col += 1) {
      const inFinder =
        (r < 8 && col < 8) || (r < 8 && col > n - 9) || (r > n - 9 && col < 8);
      if (inFinder) continue;
      if (rnd() > 0.52) c.box(ox + col * mod, oy + r * mod, ox + (col + 1) * mod, oy + (r + 1) * mod, C.text);
    }
  }
  finder(ox, oy);
  finder(ox + (n - 7) * mod, oy);
  finder(ox, oy + (n - 7) * mod);

  c.rect(48, 1210, 1032, 1330, 26, C.orange);
  c.text(540, 1252, "Export PNG", { size: 40, align: "center", color: C.white, font: "bold" });
  c.text(88, 1372, "Saved to Pictures/Becetti — nothing is uploaded", { size: 26, color: C.muted });
  return c;
}

function screenPdf() {
  const c = chrome({ slug: "Image to PDF", subtitle: "Pick images, choose a page size, export" });
  c.text(48, 300, "3 images selected", { size: 34, color: C.text, font: "bold" });
  const tiles = ["1", "2", "3"];
  tiles.forEach((label, i) => {
    const x1 = 48 + i * 330;
    c.rect(x1, 360, x1 + 300, 660, 24, i === 1 ? C.orangeLight : C.white, C.border, 2);
    c.text(x1 + 150, 480, label, { size: 90, align: "center", color: C.border, font: "bold" });
    c.text(x1 + 20, 606, `Image ${label}`, { size: 24, color: C.muted });
  });

  c.text(48, 730, "Page size", { size: 32, color: C.text, font: "bold" });
  ["A4", "A3", "Letter"].forEach((label, i) => {
    const x1 = 48 + i * 190;
    c.rect(x1, 786, x1 + 170, 856, 26, i === 0 ? C.navy : C.white, i === 0 ? null : C.border, 2);
    c.text(x1 + 85, 806, label, { size: 30, align: "center", color: i === 0 ? C.white : C.text, font: "bold" });
  });

  c.rect(48, 920, 1032, 1120, 28, C.white, C.border, 2);
  c.text(88, 960, "Output", { size: 28, color: C.muted });
  c.text(88, 1016, "Documents/Becetti/scan-2026-11-06.pdf", { size: 26, color: C.text, font: "mono" });
  c.rect(48, 1170, 1032, 1300, 26, C.orange);
  c.text(540, 1216, "Create PDF", { size: 42, align: "center", color: C.white, font: "bold" });
  c.text(48, 1350, "Your original files are never modified", { size: 26, color: C.muted });
  return c;
}

function screenConverter() {
  const c = chrome({ slug: "Unit Converter", subtitle: "Six categories, two-way conversion" });
  const cats = ["Length", "Weight", "Temperature", "Area", "Volume", "Speed"];
  let x = 48;
  cats.forEach((label, i) => {
    const w = 32 + label.length * 17;
    c.rect(x, 300, x + w, 358, 28, i === 0 ? C.orange : C.white, i === 0 ? null : C.border, 2);
    c.text(x + w / 2, 315, label, { size: 25, align: "center", color: i === 0 ? C.white : C.text, font: "bold" });
    x += w + 14;
  });

  c.rect(48, 420, 1032, 700, 30, C.white, C.border, 2);
  c.text(88, 456, "From · Kilometre", { size: 26, color: C.muted });
  c.text(88, 520, "1", { size: 110, color: C.text, font: "bold" });

  c.rect(48, 740, 1032, 1020, 30, C.navy);
  c.text(88, 776, "To · Mile", { size: 26, color: "#9FB3CC" });
  c.text(88, 840, "0.621371", { size: 96, color: C.white, font: "bold" });

  c.rect(48, 1050, 1032, 1150, 24, C.orangeLight);
  c.text(88, 1078, "Live rates are not needed here — conversion is offline", { size: 25, color: C.orangeDark });

  c.rect(48, 1200, 1032, 1330, 26, C.orange);
  c.text(540, 1246, "Convert", { size: 42, align: "center", color: C.white, font: "bold" });
  return c;
}

function screenPassword() {
  const c = chrome({ slug: "Password Generator", subtitle: "SecureRandom, nothing leaves the device" });
  c.rect(48, 290, 1032, 560, 32, C.white, C.border, 2);
  c.text(88, 330, "Generated password", { size: 26, color: C.muted });
  c.text(88, 400, "Xk7#pQ2!mR9@tLzV", { size: 58, color: C.text, font: "monoBold" });
  c.text(88, 490, "Copied to clipboard", { size: 26, color: C.orangeDark });

  c.text(48, 630, "Length · 16", { size: 32, color: C.text, font: "bold" });
  c.rect(48, 700, 1032, 716, 8, C.border);
  c.rect(48, 700, 720, 716, 8, C.orange);
  c.circle(720, 708, 22, C.white);
  c.rect(700, 688, 740, 728, 20, C.white, C.orange, 4);

  const toggles = [
    ["Upper case A–Z", true],
    ["Lower case a–z", true],
    ["Digits 0–9", true],
    ["Symbols !@#$", false],
  ];
  let y = 800;
  for (const [label, on] of toggles) {
    c.rect(48, y, 1032, y + 120, 26, C.white, C.border, 2);
    c.text(88, y + 42, label, { size: 32, color: C.text });
    c.rect(900, y + 34, 984, y + 86, 26, on ? C.orange : C.border);
    c.circle(on ? 954 : 930, y + 60, 22, C.white);
    y += 134;
  }

  c.rect(48, 1350, 1032, 1480, 26, C.orange);
  c.text(540, 1396, "Generate", { size: 42, align: "center", color: C.white, font: "bold" });
  return c;
}

/* ------------------------------ feature graphic ----------------------------- */

function featureGraphic() {
  const c = new Canvas(1024, 500, C.navy);
  c.box(0, 0, 1024, 500, C.navy);
  c.rect(620, -120, 1180, 300, 0, "#1E2C42");
  c.rect(760, 250, 1200, 640, 0, "#1A2739");
  c.rect(70, 360, 200, 372, 6, C.orange);
  c.text(70, 150, "BECETTI TOOLS", { size: 64, color: C.white, font: "bold" });
  c.text(70, 236, "15 offline utilities in one app", { size: 34, color: "#FFB27A" });
  c.text(70, 296, "Calculator · QR · PDF · OCR · Notes · Images", { size: 26, color: "#9FB3CC" });
  c.text(70, 400, "Free · No account · Works offline", { size: 26, color: "#9FB3CC" });
  return c;
}

/** Composite the real app icon onto the feature graphic, right-hand side. */
function featureGraphicWithIcon(out) {
  const base = path.join(outDir, "_feature-base.png");
  featureGraphic().run(base);
  const logo = path.join(repoRoot, "website", "assets", "img", "logo-512.png");
  execFileSync("convert", [
    base,
    "(", logo, "-resize", "300x300", ")",
    "-gravity", "NorthWest", "-geometry", "+664+100", "-composite",
    "-strip", out,
  ]);
  return out;
}

/* ---------------------------------- icons ----------------------------------- */

function icon() {
  const src = path.join(repoRoot, "website", "assets", "img", "logo-512.png");
  const out = path.join(outDir, "icon-512.png");
  execFileSync("convert", [src, "-background", "none", "-resize", "512x512", "-depth", "8", `PNG32:${out}`]);
  return out;
}

/* ----------------------------------- run ------------------------------------ */

const jobs = [
  ["phone-01-home.png", screenHome],
  ["phone-02-calculator.png", screenCalculator],
  ["phone-03-qr.png", screenQr],
  ["phone-04-image-to-pdf.png", screenPdf],
  ["phone-05-converter.png", screenConverter],
  ["phone-06-password.png", screenPassword],
  ["feature-graphic-1024x500.png", featureGraphic],
];

for (const [name, fn] of jobs) {
  const out = path.join(outDir, name);
  if (name === "feature-graphic-1024x500.png") {
    featureGraphicWithIcon(out);
  } else {
    fn().run(out);
  }
  console.log("wrote", path.relative(repoRoot, out));
}
try {
  execFileSync("rm", ["-f", path.join(outDir, "_feature-base.png")]);
} catch {}
console.log("wrote", path.relative(repoRoot, icon()));
