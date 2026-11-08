package com.becetti.tools.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.*
import com.becetti.tools.core.navigation.Route
import com.becetti.tools.design.components.*
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.ToolAccents
import com.becetti.tools.domain.models.ToolGroup
import com.becetti.tools.domain.models.ToolRegistry
import com.becetti.tools.features.settings.SettingsViewModel
import java.util.Calendar

/**
 * Premium home dashboard (spec §2): brand header, personalised greeting, search, featured tools,
 * category grid, quick actions, recents, favorites and live project activity.
 */
@Composable
fun HomeDashboardScreen(
    app: BecettiToolsApp,
    nav: NavController,
    settingsViewModel: SettingsViewModel
) {
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val lang by settingsViewModel.language.collectAsState()
    val theme by settingsViewModel.theme.collectAsState()

    val favorites by app.toolRepository.favorites().collectAsState(initial = emptyList())
    val recents by app.toolRepository.recentTools().collectAsState(initial = emptyList())
    val notes by app.toolRepository.notes().collectAsState(initial = emptyList())
    val projects by app.projectStore.projects.collectAsState()

    val favoriteIds = remember(favorites) { favorites.map { it.toolId }.toSet() }
    val recentSpecs = remember(recents) {
        recents.mapNotNull { ToolRegistry.byId(it.toolId) }.take(10)
    }
    val favoriteSpecs = remember(favoriteIds) {
        ToolRegistry.all.filter { it.id in favoriteIds }.take(12)
    }
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = when (hour) {
        in 5..11 -> s.homeGreetingMorning
        in 12..17 -> s.homeGreetingAfternoon
        else -> s.homeGreetingEvening
    }
    val openProjects = projects.filter { !it.archived }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /* ------------------------------- brand header ------------------------------- */
        item {
            Row(
                modifier = Modifier.fillMaxWidth().entrance(0),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, ToolAccents.Projects)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(BecettiGlyphs.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    Row {
                        Text(
                            "BECETTI",
                            style = MaterialTheme.typography.titleMedium,
                            color = palette.textPrimary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "TOOLS",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.4.sp
                        )
                    }
                }
                LanguagePill(lang = lang, onSelect = { settingsViewModel.setLanguage(it) })
                Spacer(Modifier.width(4.dp))
                IconButton(
                    onClick = {
                        settingsViewModel.setTheme(
                            when (theme) {
                                "dark" -> "light"
                                "light" -> "system"
                                else -> "dark"
                            }
                        )
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (palette.isDark) BecettiGlyphs.Bolt else BecettiGlyphs.PaletteDots,
                        contentDescription = s.profileTheme,
                        tint = palette.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        /* --------------------------------- greeting -------------------------------- */
        item {
            Column(Modifier.entrance(1), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(greeting, style = MaterialTheme.typography.bodyMedium, color = palette.textSecondary)
                Text(
                    s.homeSubtitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.textPrimary
                )
            }
        }

        /* ------------------------------- search launcher ---------------------------- */
        item {
            val interaction = remember { MutableInteractionSource() }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(2)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(interactionSource = interaction, indication = null) {
                        nav.navigate(Route.ToolsTab.route) { launchSingleTop = true }
                    },
                shape = RoundedCornerShape(16.dp),
                color = palette.card,
                border = BorderStroke(1.dp, palette.border)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = palette.textSecondary, modifier = Modifier.size(19.dp))
                    Text(
                        s.searchAllTools,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.textSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${ToolRegistry.all.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        /* ---------------------------------- hero stats ------------------------------ */
        item {
            val toolsCount = ToolRegistry.all.size
            Surface(
                modifier = Modifier.fillMaxWidth().entrance(3),
                shape = RoundedCornerShape(22.dp),
                color = if (palette.isDark) palette.cardElevated else palette.card,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
            ) {
                Column(
                    Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = if (palette.isDark) 0.22f else 0.10f),
                                    ToolAccents.Projects.copy(alpha = if (palette.isDark) 0.10f else 0.06f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(s.appName, style = MaterialTheme.typography.titleMedium, color = palette.textPrimary)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ToolAccents.Business.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, ToolAccents.Business.copy(alpha = 0.4f))
                        ) {
                            Row(
                                Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(BecettiGlyphs.Bolt, contentDescription = null, tint = ToolAccents.Business, modifier = Modifier.size(11.dp))
                                Text(s.homeOffline, style = MaterialTheme.typography.labelSmall, color = ToolAccents.Business)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        HeroStat("$toolsCount", s.homeToolsCount, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                        HeroStat("${openProjects.size}", s.homeProjectsCount, ToolAccents.Projects, Modifier.weight(1f))
                        HeroStat("${notes.size}", s.homeNotesCount, ToolAccents.Media, Modifier.weight(1f))
                    }
                }
            }
        }

        /* --------------------------------- featured -------------------------------- */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(s.homeFeatured)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(ToolRegistry.featured.take(8), key = { it.id }) { spec ->
                        FeaturedToolCard(
                            spec = spec,
                            strings = s,
                            onClick = { nav.navigate(spec.route) { launchSingleTop = true } }
                        )
                    }
                }
            }
        }

        /* -------------------------------- quick actions ---------------------------- */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(s.homeQuickActions)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAction(
                        icon = BecettiGlyphs.Kanban,
                        label = s.homeQuickNewProject,
                        accent = ToolAccents.Projects,
                        modifier = Modifier.weight(1f)
                    ) { nav.navigate(Route.Projects.route) { launchSingleTop = true } }
                    QuickAction(
                        icon = BecettiGlyphs.Checklist,
                        label = s.homeQuickNewNote,
                        accent = ToolAccents.Files,
                        modifier = Modifier.weight(1f)
                    ) { nav.navigate("notes") { launchSingleTop = true } }
                    QuickAction(
                        icon = BecettiGlyphs.Barcode,
                        label = s.homeQuickQr,
                        accent = ToolAccents.Business,
                        modifier = Modifier.weight(1f)
                    ) { nav.navigate("qr_scanner") { launchSingleTop = true } }
                    QuickAction(
                        icon = BecettiGlyphs.LockKey,
                        label = s.homeQuickPassword,
                        accent = ToolAccents.Network,
                        modifier = Modifier.weight(1f)
                    ) { nav.navigate("password_gen") { launchSingleTop = true } }
                }
            }
        }

        /* -------------------------------- categories ------------------------------- */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = s.homeCategories,
                    actionLabel = s.homeSeeAll,
                    onAction = { nav.navigate(Route.ToolsTab.route) { launchSingleTop = true } }
                )
                val groups = ToolRegistry.groups()
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    groups.chunked(2).forEach { rowGroups ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowGroups.forEach { group ->
                                CategoryCard(
                                    group = group,
                                    strings = s,
                                    count = ToolRegistry.countFor(group),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    nav.navigate(Route.Category.create(group.id))
                                }
                            }
                            if (rowGroups.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        /* ------------------------------ recently used ------------------------------ */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(s.homeRecent)
                if (recentSpecs.isEmpty()) {
                    Text(s.homeNoRecent, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recentSpecs, key = { "r_${it.id}" }) { spec ->
                            ToolChip(spec, s, onClick = { nav.navigate(spec.route) { launchSingleTop = true } })
                        }
                    }
                }
            }
        }

        /* -------------------------------- favorites -------------------------------- */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(s.homeFavorites)
                if (favoriteSpecs.isEmpty()) {
                    Text(s.homeNoFavorites, style = MaterialTheme.typography.bodySmall, color = palette.textSecondary)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(favoriteSpecs, key = { "f_${it.id}" }) { spec ->
                            ToolChip(spec, s, onClick = { nav.navigate(spec.route) { launchSingleTop = true } })
                        }
                    }
                }
            }
        }

        /* ------------------------------ project activity --------------------------- */
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = s.homeActivity,
                    actionLabel = s.homeSeeAll,
                    onAction = { nav.navigate(Route.Projects.route) { launchSingleTop = true } }
                )
                Panel(accent = ToolAccents.Projects) {
                    if (openProjects.isEmpty()) {
                        EmptyState(
                            title = s.homeNoProjects,
                            message = s.homeQuickNewProject,
                            icon = BecettiGlyphs.Kanban,
                            accent = ToolAccents.Projects,
                            actionLabel = s.gAdd,
                            onAction = { nav.navigate(Route.Projects.route) { launchSingleTop = true } }
                        )
                    } else {
                        openProjects.sortedByDescending { it.createdAt }.take(3).forEach { project ->
                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        project.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = palette.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "${(project.progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = ToolAccents.Projects
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { project.progress },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                    color = ToolAccents.Projects,
                                    trackColor = ToolAccents.Projects.copy(alpha = 0.16f)
                                )
                                Row(
                                    Modifier.fillMaxWidth().clickable {
                                        nav.navigate(Route.ProjectDetail.create(project.id))
                                    },
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${project.doneCount}/${project.tasks.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.textSecondary
                                    )
                                    Text(
                                        s.gOpen,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ToolAccents.Projects
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                "${s.appName} · v2.0 · ${s.gLocalOnly}",
                style = MaterialTheme.typography.labelSmall,
                color = palette.textSecondary.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeroStat(value: String, label: String, accent: Color, modifier: Modifier = Modifier) {
    val palette = BecettiTheme.palette
    Column(
        modifier
            .clip(RoundedCornerShape(13.dp))
            .background(accent.copy(alpha = if (palette.isDark) 0.12f else 0.08f))
            .border(1.dp, accent.copy(alpha = 0.20f), RoundedCornerShape(13.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = accent)
        Text(label, style = MaterialTheme.typography.labelSmall, color = palette.textSecondary, maxLines = 1)
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val palette = BecettiTheme.palette
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = modifier
            .pressScale(interaction, 0.95f)
            .clip(RoundedCornerShape(16.dp))
            .background(palette.card)
            .border(1.dp, palette.border, RoundedCornerShape(16.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(17.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LanguagePill(lang: String, onSelect: (String) -> Unit) {
    val palette = BecettiTheme.palette
    val options = listOf("en" to "EN", "ar" to "AR", "fr" to "FR")
    Row(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(palette.chipSurface)
            .border(1.dp, palette.border, RoundedCornerShape(50))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        options.forEach { (code, label) ->
            val active = lang == code
            Surface(
                shape = RoundedCornerShape(50),
                color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f) else Color.Transparent,
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable { onSelect(code) }
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (active) MaterialTheme.colorScheme.primary else palette.textSecondary
                )
            }
        }
    }
}
