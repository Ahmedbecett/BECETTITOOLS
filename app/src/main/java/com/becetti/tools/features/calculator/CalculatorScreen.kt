package com.becetti.tools.features.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.BecettiCard
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(app: BecettiToolsApp) {
    val repo = remember { ToolRepository(app.database.favoriteDao(), app.database.historyDao(), app.database.recentToolDao(), app.database.noteDao()) }
    var expr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    val history by repo.history().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BecettiCard {
            Text(expr.ifEmpty { "0" }, style = MaterialTheme.typography.displayLarge)
            Text(result, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
        val buttons = listOf(
            listOf("C","⌫","%","÷"),
            listOf("7","8","9","×"),
            listOf("4","5","6","-"),
            listOf("1","2","3","+"),
            listOf("0",".","^","="),
            listOf("sqrt(","sin(","cos(","tan("),
            listOf("log(","ln(","pi","e")
        )
        buttons.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { label ->
                    Button(onClick = {
                        when(label){
                            "C" -> { expr=""; result="" }
                            "⌫" -> if(expr.isNotEmpty()) expr=expr.dropLast(1)
                            "=" -> {
                                val res = CalculatorEngine.evaluate(expr)
                                result = if(res.isNaN()) "Error" else res.toString()
                                if(!res.isNaN()) scope.launch { repo.addHistory(expr, result) }
                            }
                            else -> expr+=label
                        }
                    }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text(label) }
                }
            }
        }
        Text("History", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            items(history.size) { i -> val h=history[i]; ListItem(headlineContent={Text(h.expression)}, supportingContent={Text(h.result)}, trailingContent={Text(h.timestamp.toString().takeLast(5))}) }
        }
        OutlinedButton(onClick={ scope.launch{ repo.clearHistory() }}){ Text("Clear history") }
        Text("No ads on this screen per policy", style = MaterialTheme.typography.labelSmall)
    }
}
