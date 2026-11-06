#!/usr/bin/env bash
# Generates the raster brand assets for the landing page (ImageMagick + DejaVu Sans).
# The SVG logo and favicon are hand-written; these PNGs are derived from the same
# geometry so the browser tab, home-screen icon and link previews all match.
#
#   bash scripts/make-images.sh
#
# Output: website/assets/img/{logo-192,logo-512,favicon-32,apple-touch-icon,og-image}.png
set -euo pipefail

cd "$(dirname "$0")/.."
OUT="website/assets/img"
mkdir -p "$OUT"

BOLD=/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf
REG=/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf
TMP=$(mktemp -d)
trap 'rm -rf "$TMP"' EXIT

# --- square logo: orange tile, white frame, navy core (matches ic_launcher_foreground)
S=512
convert -size ${S}x${S} gradient:'#F47B20-#D96513' "$TMP/grad.png"
convert -size ${S}x${S} xc:none -fill white -draw "roundrectangle 0,0,$((S-1)),$((S-1)),112,112" "$TMP/mask.png"
convert "$TMP/grad.png" "$TMP/mask.png" -alpha off -compose CopyOpacity -composite "$TMP/base.png"
convert "$TMP/base.png" \
  -fill '#FFFFFF' -draw "roundrectangle 150,150,362,362,40,40" \
  -fill '#172235' -draw "roundrectangle 188,188,324,324,26,26" \
  -fill '#F47B20' -draw "rectangle 232,232,280,280" \
  "$OUT/logo-512.png"

convert "$OUT/logo-512.png" -resize 192x192 "$OUT/logo-192.png"
convert "$OUT/logo-512.png" -resize 32x32 "$OUT/favicon-32.png"
convert -size 180x180 xc:'#172235' "$TMP/touch-bg.png"
convert "$TMP/touch-bg.png" "$OUT/logo-192.png" -gravity center -geometry +0+0 -compose Over -composite \
  -resize 180x180 "$OUT/apple-touch-icon.png"

# --- Open Graph / Twitter card (1200x630)
convert -size 1200x630 gradient:'#172235-#2B4266' "$TMP/og-bg.png"
convert -size 1200x630 xc:none -fill 'rgba(244,123,32,0.16)' -draw "circle 1180,-40 1180,380" -blur 0x110 "$TMP/og-glow.png"

convert "$TMP/og-bg.png" "$TMP/og-glow.png" -compose Over -composite \
  -fill '#F47B20' -draw "rectangle 0,0 12,630" \
  "$TMP/og-stage.png"

convert "$OUT/logo-192.png" -resize 128x128 "$TMP/logo-128.png"

convert "$TMP/og-stage.png" "$TMP/logo-128.png" -geometry +64+76 -compose Over -composite \
  -font "$BOLD" -pointsize 25 -fill '#FFB27A' -annotate +216+120 'NATIVE ANDROID UTILITY TOOLBOX' \
  -font "$BOLD" -pointsize 74 -fill '#FFFFFF' -annotate +64+300 'BECETTI TOOLS' \
  -font "$REG" -pointsize 34 -fill '#E7EDF5' -annotate +64+360 'Everything you need. One smart toolbox.' \
  -font "$REG" -pointsize 25 -fill '#A9B8CC' -annotate +64+414 '15 tools · Calculator & converters · QR · PDF · OCR · Notes' \
  \
  -fill 'rgba(255,255,255,0.10)' -draw "roundrectangle 64,458,236,514,28,28" \
  -font "$BOLD" -pointsize 22 -fill '#FFFFFF' -annotate +84+494 'FREE' \
  -fill 'rgba(255,255,255,0.10)' -draw "roundrectangle 252,458,486,514,28,28" \
  -font "$BOLD" -pointsize 22 -fill '#FFFFFF' -annotate +272+494 'ANDROID 7.0+' \
  -fill 'rgba(255,255,255,0.10)' -draw "roundrectangle 502,458,744,514,28,28" \
  -font "$BOLD" -pointsize 22 -fill '#FFFFFF' -annotate +522+494 'OFFLINE-FIRST' \
  -fill 'rgba(255,255,255,0.10)' -draw "roundrectangle 760,458,1010,514,28,28" \
  -font "$BOLD" -pointsize 22 -fill '#FFFFFF' -annotate +780+494 'NO ACCOUNT' \
  \
  -font "$REG" -pointsize 21 -fill '#8FA2BA' -annotate +64+584 'v1.0.0 · MIT · github.com/Ahmedbecett/BECETTITOOLS' \
  "$OUT/og-image.png"

echo "Wrote:"
identify -format '  %f  %wx%h  %b\n' \
  "$OUT/logo-512.png" "$OUT/logo-192.png" "$OUT/favicon-32.png" "$OUT/apple-touch-icon.png" "$OUT/og-image.png"
