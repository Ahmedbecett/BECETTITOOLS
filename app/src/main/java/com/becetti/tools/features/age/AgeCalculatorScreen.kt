package com.becetti.tools.features.age

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

@Composable
fun AgeCalculatorScreen() {
    val s = LocalAppStrings.current
    var birth by remember { mutableStateOf("2000-01-01") }
    var target by remember { mutableStateOf(LocalDate.now().toString()) }

    val parsedData = remember(birth, target) {
        try {
            val b = LocalDate.parse(birth.trim())
            val t = LocalDate.parse(target.trim())
            val period = Period.between(b, t)
            val totalDays = ChronoUnit.DAYS.between(b, t)
            val totalWeeks = totalDays / 7
            val totalHours = totalDays * 24
            val nextBirthday = b.withYear(t.year).let { if (!it.isAfter(t)) it.plusYears(1) else it }
            val daysToNext = ChronoUnit.DAYS.between(t, nextBirthday)
            Triple(period, Triple(totalDays, totalWeeks, totalHours), nextBirthday.toString() to daysToNext)
        } catch (e: Exception) {
            null
        }
    }

    val toolMeta = s.tools["age"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "age",
            title = toolMeta?.name ?: "Age & Date Calculator",
            subtitle = toolMeta?.description ?: ""
        )

        GlowingCard(accentColor = GlowPink) {
            OutlinedTextField(
                value = birth,
                onValueChange = { birth = it },
                label = { Text(s.ageBirthDateLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = target,
                onValueChange = { target = it },
                label = { Text(s.ageTargetDateLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (parsedData != null) {
            val (period, totals, nextBday) = parsedData
            val (totalDays, totalWeeks, totalHours) = totals
            val (nextDateStr, daysRem) = nextBday

            GlowingCard(accentColor = GlowOrange) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Text("${period.years}", style = MaterialTheme.typography.displayLarge, color = GlowOrange, fontWeight = FontWeight.Bold)
                        Text(s.ageYears, style = MaterialTheme.typography.labelLarge)
                    }
                    Column {
                        Text("${period.months}", style = MaterialTheme.typography.displayLarge, color = GlowCyan, fontWeight = FontWeight.Bold)
                        Text(s.ageMonths, style = MaterialTheme.typography.labelLarge)
                    }
                    Column {
                        Text("${period.days}", style = MaterialTheme.typography.displayLarge, color = GlowEmerald, fontWeight = FontWeight.Bold)
                        Text(s.ageDays, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                GlowingCard(accentColor = GlowCyan, modifier = Modifier.weight(1f)) {
                    Text(s.ageTotalDays, style = MaterialTheme.typography.bodySmall)
                    Text("$totalDays", style = MaterialTheme.typography.titleLarge, color = GlowCyan, fontWeight = FontWeight.Bold)
                }
                GlowingCard(accentColor = GlowViolet, modifier = Modifier.weight(1f)) {
                    Text(s.ageTotalWeeks, style = MaterialTheme.typography.bodySmall)
                    Text("$totalWeeks", style = MaterialTheme.typography.titleLarge, color = GlowViolet, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                GlowingCard(accentColor = GlowEmerald, modifier = Modifier.weight(1f)) {
                    Text(s.ageTotalHours, style = MaterialTheme.typography.bodySmall)
                    Text("$totalHours", style = MaterialTheme.typography.titleLarge, color = GlowEmerald, fontWeight = FontWeight.Bold)
                }
                GlowingCard(accentColor = GlowAmber, modifier = Modifier.weight(1f)) {
                    Text("🎂 ${s.ageNextBirthday}", style = MaterialTheme.typography.bodySmall)
                    Text(nextDateStr, style = MaterialTheme.typography.titleMedium, color = GlowAmber, fontWeight = FontWeight.Bold)
                    Text("$daysRem ${s.ageDaysRemaining}", style = MaterialTheme.typography.labelSmall)
                }
            }
        } else {
            GlowingCard(accentColor = Error) {
                Text(s.ageInvalidDate, color = Error, fontWeight = FontWeight.Bold)
            }
        }
    }
}
