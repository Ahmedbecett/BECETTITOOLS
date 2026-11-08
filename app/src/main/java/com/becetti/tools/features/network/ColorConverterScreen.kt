package com.becetti.tools.features.network

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.utils.FileExporter
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** Real colour-space conversion: HEX ⇄ RGB ⇄ HSL ⇄ HSV ⇄ CMYK, plus tints and shades. */
@Composable
fun ColorConverterScreen() {
    val s = LocalAppStrings.current
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val palette = BecettiTheme.palette
    val accent = ToolAccents.Media

    var hexInput by remember { mutableStateOf("#6C63FF") }
    var error by remember { mutableStateOf<String?>(null) }
    var selected by remember { mutableStateOf<Int>(0xFF6C63FF.toInt()) }
    var status by remember { mutableStateOf("") }

    val rgb = rgbOf(selected)
    val hsl = hslOf(rgb)
    val cmyk = cmykOf(rgb)
    val hex = hexOf(rgb)
    val foreground = if (luminance(rgb) > 0.55) Color(0xFF111827) else Color.White

    fun applyHex(raw: String) {
        val parsed = parseHex(raw)
        if (parsed == null) {
            error = s.colorInvalid
        } else {
            error = null
            selected = parsed
        }
    }

    ToolPage(
        title = s.toolName("net_color"),
        subtitle = s.toolDescription("net_color"),
        icon = BecettiGlyphs.PaletteDots,
        accent = accent,
        hint = s.colorHint
    ) {
        Panel(accent = accent) {
            BecettiField(
                value = hexInput,
                onValueChange = { hexInput = it; applyHex(it) },
                label = "HEX",
                placeholder = "#6C63FF",
                keyboardType = KeyboardType.Ascii,
                isError = error != null,
                supporting = error,
                accent = accent
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF000000.toInt() or selected))
                        .border(1.dp, palette.border, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(hex, style = MaterialTheme.typography.labelSmall, color = foreground)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeyValueRow("RGB", "rgb(${rgb.r}, ${rgb.g}, ${rgb.b})", mono = true)
                    KeyValueRow("HSL", "hsl(${hsl.h}, ${hsl.s}%, ${hsl.l}%)", mono = true)
                    KeyValueRow("CMYK", "${cmyk.c}%, ${cmyk.m}%, ${cmyk.y}%, ${cmyk.k}%", mono = true)
                }
            }
            SoftDivider()
            KeyValueRow("HSV", "hsv(${hsvOf(rgb).h}, ${hsvOf(rgb).s}%, ${hsvOf(rgb).v}%)", mono = true)
            KeyValueRow(s.colorAlpha, "#${"%02X".format((selected ushr 24) and 0xFF)}", mono = true)
            KeyValueRow(s.colorLuminance, String.format(java.util.Locale.US, "%.2f", luminance(rgb)), mono = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconAction(BecettiGlyphs.CopyStack, s.copy, {
                    clipboard.setText(AnnotatedString("$hex\nrgb(${rgb.r}, ${rgb.g}, ${rgb.b})\nhsl(${hsl.h}, ${hsl.s}%, ${hsl.l}%)"))
                    status = s.gCopiedToClipboard
                }, accent)
                IconAction(BecettiGlyphs.SwapArrows, s.share, {
                    FileExporter.shareText(ctx, "$hex · rgb(${rgb.r}, ${rgb.g}, ${rgb.b})", "Color")
                }, accent)
            }
        }

        Panel(accent = accent) {
            SectionHeader(s.colorTintsShades)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.8f, 0.6f, 0.4f, 0.2f, 0f, -0.2f, -0.4f, -0.6f).forEach { factor ->
                    val shade = if (factor >= 0) lighten(rgb, factor) else darken(rgb, -factor)
                    val colorInt = 0xFF000000.toInt() or (shade.r shl 16) or (shade.g shl 8) or shade.b
                    Box(
                        Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(Color(colorInt))
                            .border(1.dp, palette.border, RoundedCornerShape(9.dp))
                            .clickable {
                                selected = colorInt
                                hexInput = hexOf(shade)
                                error = null
                            }
                    )
                }
            }
            Text(
                s.colorTapSwatch,
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary
            )
        }
        if (status.isNotEmpty()) SuccessTick(visible = true, message = status)
    }
}

/* ------------------------------------------ the maths -------------------------------------- */

private data class Rgb(val r: Int, val g: Int, val b: Int)
private data class Hsl(val h: Int, val s: Int, val l: Int)
private data class Hsv(val h: Int, val s: Int, val v: Int)
private data class Cmyk(val c: Int, val m: Int, val y: Int, val k: Int)

private fun parseHex(raw: String): Int? {
    val clean = raw.trim().removePrefix("#").replace(" ", "")
    return runCatching {
        when (clean.length) {
            3 -> {
                val r = clean[0].toString().repeat(2).toInt(16)
                val g = clean[1].toString().repeat(2).toInt(16)
                val b = clean[2].toString().repeat(2).toInt(16)
                0xFF000000.toInt() or (r shl 16) or (g shl 8) or b
            }
            6 -> 0xFF000000.toInt() or clean.toLong(16).toInt()
            8 -> clean.toLong(16).toInt()
            else -> null
        }
    }.getOrNull()
}

private fun rgbOf(argb: Int): Rgb = Rgb((argb ushr 16) and 0xFF, (argb ushr 8) and 0xFF, argb and 0xFF)

private fun hexOf(rgb: Rgb): String = "#%02X%02X%02X".format(rgb.r, rgb.g, rgb.b)

private fun hslOf(rgb: Rgb): Hsl {
    val r = rgb.r / 255f
    val g = rgb.g / 255f
    val b = rgb.b / 255f
    val maxV = max(r, max(g, b))
    val minV = min(r, min(g, b))
    val l = (maxV + minV) / 2f
    var h = 0f
    var s = 0f
    val delta = maxV - minV
    if (delta > 0.0001f) {
        s = delta / (1f - abs(2f * l - 1f))
        h = when (maxV) {
            r -> 60f * (((g - b) / delta) % 6f)
            g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }
        if (h < 0) h += 360f
    }
    return Hsl(h.roundToInt() % 360, (s * 100).roundToInt(), (l * 100).roundToInt())
}

private fun hsvOf(rgb: Rgb): Hsv {
    val r = rgb.r / 255f
    val g = rgb.g / 255f
    val b = rgb.b / 255f
    val maxV = max(r, max(g, b))
    val minV = min(r, min(g, b))
    val delta = maxV - minV
    var h = 0f
    if (delta > 0.0001f) {
        h = when (maxV) {
            r -> 60f * (((g - b) / delta) % 6f)
            g -> 60f * (((b - r) / delta) + 2f)
            else -> 60f * (((r - g) / delta) + 4f)
        }
        if (h < 0) h += 360f
    }
    val sat = if (maxV == 0f) 0f else delta / maxV
    return Hsv(h.roundToInt() % 360, (sat * 100).roundToInt(), (maxV * 100).roundToInt())
}

private fun cmykOf(rgb: Rgb): Cmyk {
    val r = rgb.r / 255f
    val g = rgb.g / 255f
    val b = rgb.b / 255f
    val k = 1f - max(r, max(g, b))
    if (k >= 0.9999f) return Cmyk(0, 0, 0, 100)
    val c = (1f - r - k) / (1f - k)
    val m = (1f - g - k) / (1f - k)
    val y = (1f - b - k) / (1f - k)
    return Cmyk((c * 100).roundToInt(), (m * 100).roundToInt(), (y * 100).roundToInt(), (k * 100).roundToInt())
}

private fun luminance(rgb: Rgb): Double {
    fun channel(value: Int): Double {
        val c = value / 255.0
        return if (c <= 0.03928) c / 12.92 else Math.pow((c + 0.055) / 1.055, 2.4)
    }
    return 0.2126 * channel(rgb.r) + 0.7152 * channel(rgb.g) + 0.0722 * channel(rgb.b)
}

private fun lighten(rgb: Rgb, amount: Float): Rgb = Rgb(
    (rgb.r + (255 - rgb.r) * amount).roundToInt().coerceIn(0, 255),
    (rgb.g + (255 - rgb.g) * amount).roundToInt().coerceIn(0, 255),
    (rgb.b + (255 - rgb.b) * amount).roundToInt().coerceIn(0, 255)
)

private fun darken(rgb: Rgb, amount: Float): Rgb = Rgb(
    (rgb.r * (1 - amount)).roundToInt().coerceIn(0, 255),
    (rgb.g * (1 - amount)).roundToInt().coerceIn(0, 255),
    (rgb.b * (1 - amount)).roundToInt().coerceIn(0, 255)
)
