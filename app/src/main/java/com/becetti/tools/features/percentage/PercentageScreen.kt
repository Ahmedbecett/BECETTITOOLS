package com.becetti.tools.features.percentage

import androidx.compose.foundation.horizontalScroll
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
import java.util.Locale

@Composable
fun PercentageScreen() {
    val s = LocalAppStrings.current
    val accent = GlowAmber
    var mode by remember { mutableStateOf(0) }
    var a by remember { mutableStateOf("250") }
    var b by remember { mutableStateOf("20") }

    val av = a.toDoubleOrNull() ?: 0.0
    val bv = b.toDoubleOrNull() ?: 0.0
    val (result, diffAmount) = remember(av, bv, mode) {
        when (mode) {
            0 -> (av * bv / 100.0) to 0.0
            1 -> (av * (1.0 + bv / 100.0)) to (av * bv / 100.0)
            2 -> (av * (1.0 - bv / 100.0)) to (av * bv / 100.0)
            3 -> (if (bv != 0.0) (av / bv) * 100.0 else 0.0) to 0.0
            else -> 0.0 to 0.0
        }
    }

    val toolMeta = s.tools["percentage"]
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingToolHeader(
            toolId = "percentage",
            title = toolMeta?.name ?: "Percentage Calculator",
            subtitle = toolMeta?.description ?: ""
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text(s.pctOfNumber) })
            FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text(s.pctIncrease) })
            FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text(s.pctDecreaseDiscount) })
            FilterChip(selected = mode == 3, onClick = { mode = 3 }, label = { Text(s.pctRatioMode) })
        }

        GlowingCard(accentColor = accent) {
            OutlinedTextField(
                value = a,
                onValueChange = { a = it },
                label = { Text(s.pctNumberLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = b,
                onValueChange = { b = it },
                label = { Text(if (mode == 3) s.pctValueLabel else s.pctPercentLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        GlowingCard(accentColor = GlowEmerald) {
            Text(s.resultLabel, style = MaterialTheme.typography.labelLarge, color = GlowEmerald)
            Spacer(Modifier.height(4.dp))
            Text(
                if (mode == 3) String.format(Locale.US, "%.2f%%", result)
                else String.format(Locale.US, "%.2f", result),
                style = MaterialTheme.typography.displayLarge,
                color = GlowEmerald,
                fontWeight = FontWeight.Bold
            )
            if (mode == 1 || mode == 2) {
                Spacer(Modifier.height(6.dp))
                Text(
                    "${s.pctSavedAmount}: ${String.format(Locale.US, "%.2f", diffAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GlowAmber,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
