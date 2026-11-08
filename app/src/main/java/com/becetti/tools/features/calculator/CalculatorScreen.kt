package com.becetti.tools.features.calculator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolHeader
import com.becetti.tools.design.theme.*
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(app: BecettiToolsApp) {
    val s = LocalAppStrings.current
    val clipboard = LocalClipboardManager.current
    val repo = remember {
        ToolRepository(
            app.database.favoriteDao(),
            app.database.historyDao(),
            app.database.recentToolDao(),
            app.database.noteDao()
        )
    }
    var expr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var copied by remember { mutableStateOf(false) }
    val history by repo.history().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    val buttons = listOf(
        listOf("C", "⌫", "(", ")"),
        listOf("sqrt(", "sin(", "cos(", "tan("),
        listOf("log(", "ln(", "pi", "e"),
        listOf("7", "8", "9", "÷"),
        listOf("4", "5", "6", "×"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "%", "+"),
        listOf("^", "x²", "=")
    )

    val toolMeta = s.tools["calc_scientific"]

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            GlowingToolHeader(
                toolId = "calc_scientific",
                title = toolMeta?.name ?: "Scientific Calculator",
                subtitle = toolMeta?.description ?: ""
            )
        }
        item {
            GlowingCard(accentColor = GlowOrange) {
                Text(
                    expr.ifEmpty { "0" },
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                if (result.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "= $result",
                            style = MaterialTheme.typography.titleLarge,
                            color = GlowOrange,
                            fontWeight = FontWeight.Bold
                        )
                        AssistChip(
                            onClick = {
                                clipboard.setText(AnnotatedString(result))
                                copied = true
                            },
                            label = { Text(if (copied) s.copied else s.copy) }
                        )
                    }
                }
            }
        }
        items(buttons.size) { rowIdx ->
            val row = buttons[rowIdx]
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { label ->
                    val isEqual = label == "="
                    val isClear = label == "C" || label == "⌫"
                    Button(
                        onClick = {
                            copied = false
                            when (label) {
                                "C" -> { expr = ""; result = "" }
                                "⌫" -> if (expr.isNotEmpty()) expr = expr.dropLast(1)
                                "x²" -> expr += "^2"
                                "=" -> {
                                    val res = CalculatorEngine.evaluate(expr)
                                    val formatted = CalculatorEngine.formatResult(res)
                                    result = formatted
                                    if (!res.isNaN() && expr.isNotBlank()) {
                                        scope.launch { repo.addHistory(expr, formatted) }
                                    }
                                }
                                else -> expr += label
                            }
                        },
                        modifier = Modifier.weight(if (isEqual) 2f else 1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isEqual -> GlowOrange
                                isClear -> GlowRose
                                rowIdx <= 2 -> SecondaryNavy
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(label, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    s.calcHistoryTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (history.isNotEmpty()) {
                    OutlinedButton(onClick = { scope.launch { repo.clearHistory() } }) {
                        Text(s.calcClearHistory)
                    }
                }
            }
        }
        items(history.size) { i ->
            val h = history[i]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expr = h.result
                        result = h.result
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                ListItem(
                    headlineContent = { Text(h.expression) },
                    supportingContent = { Text("= ${h.result}", color = GlowOrange, fontWeight = FontWeight.Bold) }
                )
            }
        }
        item {
            Text(s.calcNoAdsNote, style = MaterialTheme.typography.labelSmall)
        }
    }
}
