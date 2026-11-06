package com.becetti.tools.features.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.data.repository.ToolRepository
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(app: BecettiToolsApp){
    val repo = remember{ ToolRepository(app.database.favoriteDao(), app.database.historyDao(), app.database.recentToolDao(), app.database.noteDao()) }
    val hist by repo.history().collectAsState(initial= emptyList())
    val recent by repo.recentTools().collectAsState(initial= emptyList())
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)){
        Text("History", style=MaterialTheme.typography.titleLarge)
        Text("Calculations", style=MaterialTheme.typography.titleMedium)
        if(hist.isEmpty()) Text("No calculations yet")
        LazyColumn(modifier=Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(4.dp)){
            items(hist){ h-> ListItem(headlineContent={Text(h.expression)}, supportingContent={Text(h.result)}) }
        }
        Button(onClick={ scope.launch{ repo.clearHistory() } }){ Text("Clear History") }
        Divider()
        Text("Recent Tools", style=MaterialTheme.typography.titleMedium)
        recent.forEach{ Text(it.toolId) }
        Text("You can clear local history anytime in Settings.", style=MaterialTheme.typography.bodySmall)
    }
}
