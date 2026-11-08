package com.becetti.tools.features.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.data.repository.ToolRepository
import com.becetti.tools.design.components.BecettiIcons
import com.becetti.tools.design.components.GlowingCard
import com.becetti.tools.design.components.GlowingToolIconBadge
import com.becetti.tools.design.theme.GlowCyan
import com.becetti.tools.design.theme.GlowOrange
import com.becetti.tools.domain.models.ToolCatalog
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val repo = remember {
        ToolRepository(
            app.database.favoriteDao(),
            app.database.historyDao(),
            app.database.recentToolDao(),
            app.database.noteDao()
        )
    }
    val hist by repo.history().collectAsState(initial = emptyList())
    val recent by repo.recentTools().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    s.histCalculations,
                    style = MaterialTheme.typography.titleLarge,
                    color = GlowOrange,
                    fontWeight = FontWeight.Bold
                )
                if (hist.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { scope.launch { repo.clearHistory() } },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(s.calcClearHistory)
                    }
                }
            }
            if (hist.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(s.calcNoHistory, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(hist) { h ->
            GlowingCard(accentColor = GlowOrange) {
                Text(h.expression, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "= ${h.result}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GlowOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Text(
                s.histRecentTools,
                style = MaterialTheme.typography.titleLarge,
                color = GlowCyan,
                fontWeight = FontWeight.Bold
            )
            if (recent.isEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(s.histNoRecents, style = MaterialTheme.typography.bodySmall)
            }
        }
        items(recent) { r ->
            val tool = ToolCatalog.all.find { it.route == r.toolId }
            val accent = BecettiIcons.colorForTool(tool?.id ?: r.toolId)
            val icon = BecettiIcons.iconForTool(tool?.id ?: r.toolId)
            GlowingCard(
                accentColor = accent,
                onClick = { tool?.let { nav.navigate(it.route) } }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlowingToolIconBadge(icon = icon, accentColor = accent, size = 42.dp, iconSize = 21.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            tool?.localizedName(s) ?: r.toolId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(tool?.localizedDescription(s) ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(BecettiIcons.ChevronRight, contentDescription = null, tint = accent)
                }
            }
        }
    }
}
