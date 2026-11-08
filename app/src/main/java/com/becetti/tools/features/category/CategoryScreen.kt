package com.becetti.tools.features.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.domain.models.ToolGroup
import com.becetti.tools.domain.models.ToolRegistry
import com.becetti.tools.features.tools.FilterPill
import kotlinx.coroutines.launch

/**
 * Category page (spec §2): balanced title, description, in-category search, favorites shortcut,
 * recents and consistent tool cards.
 */
@Composable
fun CategoryScreen(
    group: ToolGroup,
    app: BecettiToolsApp,
    nav: NavController
) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val accent = group.accent
    var query by remember { mutableStateOf("") }
    var favoritesOnly by remember { mutableStateOf(false) }
    val favorites by app.toolRepository.favorites().collectAsState(initial = emptyList())
    val recents by app.toolRepository.recentTools().collectAsState(initial = emptyList())
    val favoriteIds = remember(favorites) { favorites.map { it.toolId }.toSet() }
    val scope = rememberCoroutineScope()

    val groupTools = remember { ToolRegistry.byGroup(group) }
    val filtered = remember(query, favoritesOnly, favoriteIds) {
        val base = if (query.isBlank()) groupTools else groupTools.filter { spec ->
            s.toolName(spec.id).lowercase().contains(query.trim().lowercase()) ||
                s.toolDescription(spec.id).lowercase().contains(query.trim().lowercase()) ||
                spec.keywords.lowercase().contains(query.trim().lowercase())
        }
        base.filter { !favoritesOnly || it.id in favoriteIds }
    }
    val recentInGroup = remember(recents) {
        recents.mapNotNull { ToolRegistry.byId(it.toolId) }.filter { it.group == group }.take(8)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(Modifier.entrance(0), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(accent.copy(alpha = 0.14f))
                            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(group.icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(s.groupName(group.id), style = MaterialTheme.typography.headlineSmall, color = palette.textPrimary)
                        Text(
                            "${groupTools.size} ${s.homeToolsCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent
                        )
                    }
                }
                Text(s.groupDescription(group.id), style = MaterialTheme.typography.bodyMedium, color = palette.textSecondary)
            }
        }

        item {
            AppSearchBar(
                value = query,
                onValueChange = { query = it },
                placeholder = s.searchInCategory,
                accent = accent,
                modifier = Modifier.entrance(1)
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterPill(label = s.gAll, selected = !favoritesOnly, accent = accent) { favoritesOnly = false }
                }
                item {
                    FilterPill(label = "★ ${s.homeFavorites}", selected = favoritesOnly, accent = accent) {
                        favoritesOnly = !favoritesOnly
                    }
                }
            }
        }

        if (query.isBlank() && !favoritesOnly && recentInGroup.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(s.homeRecent)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recentInGroup, key = { "cg_${it.id}" }) { spec ->
                            ToolChip(spec, s, onClick = { nav.navigate(spec.route) { launchSingleTop = true } })
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyState(
                    title = s.searchNoResults,
                    message = s.searchNoResultsHint,
                    icon = group.icon,
                    accent = accent
                )
            }
        } else {
            item {
                Text(s.categoryToolsIn, style = MaterialTheme.typography.titleSmall, color = palette.textSecondary)
            }
            items(filtered, key = { it.id }) { spec ->
                ToolCard(
                    spec = spec,
                    strings = s,
                    favorite = spec.id in favoriteIds,
                    onClick = { nav.navigate(spec.route) { launchSingleTop = true } },
                    onToggleFavorite = {
                        scope.launch { app.toolRepository.toggleFavorite(spec.id, spec.id in favoriteIds) }
                    },
                    modifier = Modifier.entrance(1)
                )
            }
        }
    }
}
