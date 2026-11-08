package com.becetti.tools.features.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.becetti.tools.design.theme.GlowAmber
import com.becetti.tools.design.theme.TextGray
import com.becetti.tools.domain.models.ToolCatalog
import kotlinx.coroutines.launch

@Composable
fun FavoritesScreen(app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val repo = remember {
        ToolRepository(
            app.database.favoriteDao(),
            app.database.historyDao(),
            app.database.recentToolDao(),
            app.database.noteDao()
        )
    }
    val favs by repo.favorites().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "★ ${s.navFavorites}",
                style = MaterialTheme.typography.titleLarge,
                color = GlowAmber,
                fontWeight = FontWeight.Bold
            )
            if (favs.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(s.favEmpty, style = MaterialTheme.typography.bodyMedium)
            }
        }
        items(favs, key = { "fav_${it.toolId}" }) { f ->
            val tool = ToolCatalog.all.find { it.route == f.toolId }
            val accent = BecettiIcons.colorForTool(tool?.id ?: f.toolId)
            val icon = BecettiIcons.iconForTool(tool?.id ?: f.toolId)
            GlowingCard(
                accentColor = accent,
                onClick = { tool?.let { nav.navigate(it.route) } }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlowingToolIconBadge(icon = icon, accentColor = accent, size = 46.dp, iconSize = 23.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            tool?.localizedName(s) ?: f.toolId,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(tool?.localizedDescription(s) ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { scope.launch { repo.toggleFavorite(f.toolId, true) } }) {
                        Text("★", color = GlowAmber, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Text(
                s.favToggleAllTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(ToolCatalog.all, key = { "all_${it.id}" }) { tool ->
            val isFav = favs.any { it.toolId == tool.route }
            val accent = BecettiIcons.colorForTool(tool.id)
            val icon = BecettiIcons.iconForTool(tool.id)
            GlowingCard(
                accentColor = accent,
                onClick = { nav.navigate(tool.route) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlowingToolIconBadge(icon = icon, accentColor = accent, size = 44.dp, iconSize = 22.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            tool.localizedName(s),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(tool.localizedDescription(s), style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { scope.launch { repo.toggleFavorite(tool.route, isFav) } }) {
                        Text(
                            if (isFav) "★" else "☆",
                            color = if (isFav) GlowAmber else TextGray,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
