package com.becetti.tools.features.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import java.util.Locale

@Composable
fun BmiCalculatorScreen() {
    val s = LocalAppStrings.current
    val accent = GlowRose
    var heightCm by remember { mutableStateOf("175") }
    var weightKg by remember { mutableStateOf("72") }
    var ageYears by remember { mutableStateOf("28") }

    val h = (heightCm.toDoubleOrNull() ?: 175.0).coerceIn(50.0, 260.0)
    val w = (weightKg.toDoubleOrNull() ?: 72.0).coerceIn(10.0, 350.0)
    val age = (ageYears.toIntOrNull() ?: 28).coerceIn(5, 120)

    val heightM = h / 100.0
    val bmi = w / (heightM * heightM)
    val minIdeal = 18.5 * heightM * heightM
    val maxIdeal = 24.9 * heightM * heightM
    val waterLiters = w * 0.033
    val bmr = (10.0 * w) + (6.25 * h) - (5.0 * age) + 5.0
    val tdee = (bmr * 1.375).toInt()

    val (statusLabel, statusColor) = when {
        bmi < 18.5 -> s.bmiUnderweight to GlowCyan
        bmi < 25.0 -> s.bmiNormal to GlowEmerald
        bmi < 30.0 -> s.bmiOverweight to GlowAmber
        else -> s.bmiObese to Error
    }

    val toolMeta = s.tools["bmi_calculator"]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlowingToolHeader(
            toolId = "bmi_calculator",
            title = toolMeta?.name ?: "BMI & Health Calculator",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = accent) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = heightCm,
                    onValueChange = { heightCm = it },
                    label = { Text(s.bmiHeightCm) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    label = { Text(s.bmiWeightKg) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = ageYears,
                onValueChange = { ageYears = it },
                label = { Text(s.bmiAgeYears) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        GlowingCard(accentColor = statusColor) {
            Text(s.bmiScoreLabel, style = MaterialTheme.typography.labelLarge, color = statusColor)
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    String.format(Locale.US, "%.1f", bmi),
                    style = MaterialTheme.typography.displayLarge,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = statusColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        statusLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            val progress = ((bmi - 12.0) / 28.0).toFloat().coerceIn(0.05f, 1f)
            LinearProgressIndicator(
                progress = progress,
                color = statusColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GlowingCard(accentColor = GlowEmerald, modifier = Modifier.weight(1f)) {
                Text(s.bmiIdealWeight, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    String.format(Locale.US, "%.1f – %.1f kg", minIdeal, maxIdeal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GlowEmerald
                )
            }
            GlowingCard(accentColor = GlowCyan, modifier = Modifier.weight(1f)) {
                Text(s.bmiWaterIntake, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    String.format(Locale.US, "%.1f L / day", waterLiters),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GlowCyan
                )
            }
        }

        GlowingCard(accentColor = GlowAmber) {
            Text(s.bmiDailyCalories, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "$tdee kcal / day",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = GlowAmber
            )
        }
    }
}
