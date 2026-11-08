package com.becetti.tools.features.tools

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.domain.models.ToolGroup
import com.becetti.tools.domain.models.ToolRegistry
import com.becetti.tools.domain.models.ToolSpec
import kotlinx.coroutines.launch

/**
 * Tools hub (bottom-nav "Tools"): every tool, live search, group filters, favorites and recents.
 */
@Composable
fun ToolsHubScreen(app: BecettiToolsApp, nav: NavController) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    var query by remember { mutableStateOf("") }
    var groupFilter by remember { mutableStateOf<String?>(null) }
    var favoritesOnly by remember { mutableStateOf(false) }
    val favorites by app.toolRepository.favorites().collectAsState(initial = emptyList())
    val recents by app.toolRepository.recentTools().collectAsState(initial = emptyList())
    val favoriteIds = remember(favorites) { favorites.map { it.toolId }.toSet() }
    val scope = rememberCoroutineScope()

    val results: List<ToolSpec> = remember(query, groupFilter, favoritesOnly, favoriteIds) {
        val base = if (query.isBlank()) ToolRegistry.all else ToolRegistry.search(s, query)
        base.filter { spec ->
            (groupFilter == null || spec.group.id == groupFilter) && (!favoritesOnly || spec.id in favoriteIds)
        }
    }
    val recentSpecs = remember(recents) { recents.mapNotNull { ToolRegistry.byId(it.toolId) }.take(8) }

    fun toggleFavorite(spec: ToolSpec) {
        scope.launch { app.toolRepository.toggleFavorite(spec.id, spec.id in favoriteIds) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(Modifier.entrance(0), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(s.navTools, style = MaterialTheme.typography.headlineSmall, color = palette.textPrimary)
                Text(
                    "${ToolRegistry.all.size} ${s.homeToolsCount} · ${s.homeOffline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.textSecondary
                )
            }
        }

        item {
            AppSearchBar(
                value = query,
                onValueChange = { query = it },
                placeholder = s.searchAllTools,
                modifier = Modifier.entrance(1)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterPill(
                            label = s.gAll,
                            selected = groupFilter == null && !favoritesOnly,
                            accent = MaterialTheme.colorScheme.primary
                        ) {
                            groupFilter = null
                            favoritesOnly = false
                        }
                    }
                    item {
                        FilterPill(
                            label = "★ ${s.homeFavorites}",
                            selected = favoritesOnly,
                            accent = MaterialTheme.colorScheme.secondary
                        ) {
                            favoritesOnly = !favoritesOnly
                            groupFilter = null
                        }
                    }
                    items(ToolRegistry.groups(), key = { it.id }) { group ->
                        FilterPill(
                            label = s.groupName(group.id),
                            selected = groupFilter == group.id,
                            accent = group.accent
                        ) {
                            groupFilter = if (groupFilter == group.id) null else group.id
                            favoritesOnly = false
                        }
                    }
                }
            }
        }

        if (query.isBlank() && groupFilter == null && !favoritesOnly && recentSpecs.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(s.homeRecent)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recentSpecs, key = { "rec_${it.id}" }) { spec ->
                            ToolChip(spec, s, onClick = { nav.navigate(spec.route) { launchSingleTop = true } })
                        }
                    }
                }
            }
        }

        if (results.isEmpty()) {
            item {
                EmptyState(
                    title = s.searchNoResults,
                    message = s.searchNoResultsHint,
                    icon = BecettiGlyphs.Grid4,
                    accent = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            groupedResults(results).forEach { (group, specs) ->
                item(key = "header_${group.id}") {
                    Row(
                        Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.groupName(group.id), style = MaterialTheme.typography.titleSmall, color = group.accent)
                        Text("${specs.size}", style = MaterialTheme.typography.labelSmall, color = palette.textSecondary)
                    }
                }
                items(specs, key = { it.id }) { spec ->
                    ToolCard(
                        spec = spec,
                        strings = s,
                        favorite = spec.id in favoriteIds,
                        onClick = { nav.navigate(spec.route) { launchSingleTop = true } },
                        onToggleFavorite = { toggleFavorite(spec) },
                        modifier = Modifier.entrance(1)
                    )
                }
            }
        }
    }
}

/** Keeps result ordering stable: groups in registry order, tools in registry order. */
private fun groupedResults(results: List<ToolSpec>): List<Pair<ToolGroup, List<ToolSpec>>> {
    val order = ToolRegistry.groups()
    return results.groupBy { it.group }
        .toList()
        .sortedBy { order.indexOf(it.first) }
}

@Composable
fun FilterPill(
    label: String,
    selected: Boolean,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val palette = BecettiTheme.palette
    androidx.compose.material3.Surface(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = if (selected) accent.copy(alpha = 0.16f) else palette.chipSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) accent.copy(alpha = 0.5f) else palette.border
        )
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) accent else palette.textSecondary,
            maxLines = 1
        )
    }
}
