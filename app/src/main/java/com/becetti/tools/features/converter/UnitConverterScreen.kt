package com.becetti.tools.features.converter

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import java.util.Locale

enum class UnitCategory(val units: List<String>) {
    LENGTH(listOf("mm", "cm", "m", "km", "inch", "ft", "yard", "mile")),
    WEIGHT(listOf("mg", "g", "kg", "ton", "oz", "lb")),
    TEMPERATURE(listOf("C", "F", "K")),
    AREA(listOf("mm2", "cm2", "m2", "km2", "ft2", "acre", "hectare")),
    VOLUME(listOf("ml", "l", "m3", "cup", "gallon", "fl oz")),
    SPEED(listOf("m/s", "km/h", "mph", "knot")),
    DATA(listOf("B", "KB", "MB", "GB", "TB")),
    TIME(listOf("sec", "min", "hr", "day", "week"))
}

object UnitConverter {
    fun convert(value: Double, from: String, to: String, cat: UnitCategory): Double {
        if (from == to) return value
        return when (cat) {
            UnitCategory.LENGTH -> toMeter(value, from) / toMeter(1.0, to)
            UnitCategory.WEIGHT -> toKg(value, from) / toKg(1.0, to)
            UnitCategory.TEMPERATURE -> convertTemp(value, from, to)
            UnitCategory.AREA -> toM2(value, from) / toM2(1.0, to)
            UnitCategory.VOLUME -> toLiter(value, from) / toLiter(1.0, to)
            UnitCategory.SPEED -> toMs(value, from) / toMs(1.0, to)
            UnitCategory.DATA -> toBytes(value, from) / toBytes(1.0, to)
            UnitCategory.TIME -> toSeconds(value, from) / toSeconds(1.0, to)
        }
    }

    fun formatValue(res: Double): String {
        if (res.isNaN() || res.isInfinite()) return "0"
        return if (res == res.toLong().toDouble()) {
            res.toLong().toString()
        } else {
            String.format(Locale.US, "%.6f", res).trimEnd('0').trimEnd('.')
        }
    }

    private fun toMeter(v: Double, u: String) = when (u) {
        "mm" -> v / 1000.0; "cm" -> v / 100.0; "m" -> v; "km" -> v * 1000.0
        "inch" -> v * 0.0254; "ft" -> v * 0.3048; "yard" -> v * 0.9144; "mile" -> v * 1609.344
        else -> v
    }

    private fun toKg(v: Double, u: String) = when (u) {
        "mg" -> v / 1e6; "g" -> v / 1000.0; "kg" -> v; "ton" -> v * 1000.0
        "oz" -> v * 0.0283495; "lb" -> v * 0.45359237
        else -> v
    }

    private fun convertTemp(v: Double, from: String, to: String): Double {
        val c = when (from) { "C" -> v; "F" -> (v - 32.0) * 5.0 / 9.0; "K" -> v - 273.15; else -> v }
        return when (to) { "C" -> c; "F" -> c * 9.0 / 5.0 + 32.0; "K" -> c + 273.15; else -> c }
    }

    private fun toM2(v: Double, u: String) = when (u) {
        "mm2" -> v / 1e6; "cm2" -> v / 1e4; "m2" -> v; "km2" -> v * 1e6
        "ft2" -> v * 0.092903; "acre" -> v * 4046.856; "hectare" -> v * 10000.0
        else -> v
    }

    private fun toLiter(v: Double, u: String) = when (u) {
        "ml" -> v / 1000.0; "l" -> v; "m3" -> v * 1000.0
        "cup" -> v * 0.236588; "gallon" -> v * 3.78541; "fl oz" -> v * 0.0295735
        else -> v
    }

    private fun toMs(v: Double, u: String) = when (u) {
        "m/s" -> v; "km/h" -> v / 3.6; "mph" -> v * 0.44704; "knot" -> v * 0.514444
        else -> v
    }

    private fun toBytes(v: Double, u: String) = when (u) {
        "B" -> v; "KB" -> v * 1024.0; "MB" -> v * 1048576.0
        "GB" -> v * 1073741824.0; "TB" -> v * 1099511627776.0
        else -> v
    }

    private fun toSeconds(v: Double, u: String) = when (u) {
        "sec" -> v; "min" -> v * 60.0; "hr" -> v * 3600.0
        "day" -> v * 86400.0; "week" -> v * 604800.0
        else -> v
    }
}

@Composable
fun UnitConverterScreen() {
    val s = LocalAppStrings.current
    val clipboard = LocalClipboardManager.current
    val accent = GlowCyan
    var category by remember { mutableStateOf(UnitCategory.LENGTH) }
    var from by remember { mutableStateOf(category.units.first()) }
    var to by remember { mutableStateOf(category.units.last()) }
    var input by remember { mutableStateOf("1") }
    var copied by remember { mutableStateOf(false) }

    val numericVal = input.toDoubleOrNull() ?: 0.0
    val output = remember(input, from, to, category) {
        try {
            UnitConverter.formatValue(UnitConverter.convert(numericVal, from, to, category))
        } catch (e: Exception) {
            "0"
        }
    }

    val toolMeta = s.tools["unit_converter"]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "unit_converter",
            title = toolMeta?.name ?: "Unit Converter",
            subtitle = toolMeta?.description ?: ""
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            UnitCategory.values().forEach { c ->
                FilterChip(
                    selected = c == category,
                    onClick = {
                        category = c
                        from = c.units.first()
                        to = c.units.last()
                    },
                    label = { Text(c.name) }
                )
            }
        }

        GlowingCard(accentColor = accent) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it; copied = false },
                label = { Text(s.unitValueLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Dropdown(category.units, from, { from = it }, Modifier.weight(1f), s.unitFromLabel)
                FilledTonalButton(
                    onClick = {
                        val tmp = from
                        from = to
                        to = tmp
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("⇄")
                }
                Dropdown(category.units, to, { to = it }, Modifier.weight(1f), s.unitToLabel)
            }
        }

        GlowingCard(accentColor = GlowEmerald) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(s.resultLabel, style = MaterialTheme.typography.labelLarge, color = GlowEmerald)
                    Text(
                        "$output $to",
                        style = MaterialTheme.typography.displayLarge,
                        color = GlowEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
                AssistChip(
                    onClick = {
                        clipboard.setText(AnnotatedString("$output $to"))
                        copied = true
                    },
                    label = { Text(if (copied) s.copied else s.copy) }
                )
            }
        }

        // All Units Comparison Table
        GlowingCard(accentColor = GlowOrange) {
            Text(
                s.unitAllConversions,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GlowOrange
            )
            Spacer(Modifier.height(8.dp))
            category.units.forEach { u ->
                val conv = UnitConverter.formatValue(UnitConverter.convert(numericVal, from, u, category))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(u, fontWeight = FontWeight.SemiBold)
                    Text("$conv $u", color = GlowCyan, fontWeight = FontWeight.Bold)
                }
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Dropdown(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = { onSelect(item); expanded = false }
                )
            }
        }
    }
}
