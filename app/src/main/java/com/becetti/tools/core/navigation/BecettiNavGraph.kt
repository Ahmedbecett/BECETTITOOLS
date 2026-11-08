package com.becetti.tools.core.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.localization.groupName
import com.becetti.tools.core.localization.toolName
import com.becetti.tools.design.components.BecettiGlyphs
import com.becetti.tools.design.components.BecettiIcons
import com.becetti.tools.design.components.pressScale
import com.becetti.tools.design.theme.BecettiTheme
import com.becetti.tools.design.theme.Motion
import com.becetti.tools.design.theme.motionDuration
import com.becetti.tools.domain.models.ToolGroup
import com.becetti.tools.domain.models.ToolRegistry
import com.becetti.tools.features.category.CategoryScreen
import com.becetti.tools.features.favorites.FavoritesScreen
import com.becetti.tools.features.history.HistoryScreen
import com.becetti.tools.features.home.HomeDashboardScreen
import com.becetti.tools.features.profile.ProfileScreen
import com.becetti.tools.features.projects.ProjectDetailScreen
import com.becetti.tools.features.projects.ProjectsScreen
import com.becetti.tools.features.settings.SettingsScreen
import com.becetti.tools.features.settings.SettingsViewModel
import com.becetti.tools.features.tools.ToolsHubScreen
import kotlinx.coroutines.launch

data class BottomDestination(val route: String, val label: String, val icon: ImageVector)

@Composable
fun BecettiNavGraph(app: BecettiToolsApp, settingsViewModel: SettingsViewModel) {
    val nav = rememberNavController()
    val s = LocalAppStrings.current
    val palette = BecettiTheme.palette
    val scope = rememberCoroutineScope()

    val tabs = remember(s) {
        listOf(
            BottomDestination(Route.Home.route, s.navHomeTab, Icons.Filled.Home),
            BottomDestination(Route.Developers.route, s.navDevelopers, BecettiGlyphs.CodeBrackets),
            BottomDestination(Route.Projects.route, s.navProjects, BecettiGlyphs.Kanban),
            BottomDestination(Route.ToolsTab.route, s.navTools, BecettiGlyphs.Grid4),
            BottomDestination(Route.Profile.route, s.navProfile, Icons.Filled.Person)
        )
    }
    val tabRoutes = remember(tabs) { tabs.map { it.route }.toSet() }

    val currentEntry: NavBackStackEntry? by nav.currentBackStackEntryFlow
        .collectAsState(initial = nav.currentBackStackEntry)
    val currentRoute = currentEntry?.destination?.route
    val showChrome = currentRoute == null || currentRoute in tabRoutes

    val activeSpec = remember(currentRoute) { ToolRegistry.byRoute(currentRoute) }
    val accent = activeSpec?.group?.accent ?: MaterialTheme.colorScheme.primary
    val title = when {
        activeSpec != null -> s.toolName(activeSpec.id)
        currentRoute?.startsWith("category/") == true -> {
            val id = currentRoute.substringAfter("category/").substringBefore("/")
            s.groupName(id)
        }
        currentRoute?.startsWith("project/") == true -> s.toolName("projects")
        currentRoute == Route.Settings.route -> s.navSettings
        currentRoute == Route.Favorites.route -> s.navFavorites
        currentRoute == Route.History.route -> s.navHistory
        else -> s.appName
    }

    val favorites by app.toolRepository.favorites().collectAsState(initial = emptyList())
    val favoriteIds = remember(favorites) { favorites.map { it.toolId }.toSet() }

    val enter = fadeIn(tween(motionDuration(Motion.Fast))) +
        slideInHorizontally(tween(motionDuration(Motion.Base))) { it / 14 }
    val exit = fadeOut(tween(motionDuration(120)))
    val popEnter = fadeIn(tween(motionDuration(Motion.Fast)))
    val popExit = fadeOut(tween(motionDuration(120))) +
        slideOutHorizontally(tween(motionDuration(Motion.Base))) { it / 14 }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!showChrome) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .statusBarsPaddingIfNeeded()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (!nav.popBackStack()) {
                            nav.navigate(Route.Home.route) { launchSingleTop = true }
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = s.back, tint = accent)
                    }
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (activeSpec != null) {
                        val isFav = activeSpec.id in favoriteIds
                        IconButton(onClick = {
                            scope.launch { app.toolRepository.toggleFavorite(activeSpec.id, isFav) }
                        }) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = if (isFav) s.gFavoriteRemove else s.gFavoriteAdd,
                                tint = if (isFav) accent else palette.textSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (showChrome) {
                BecettiBottomBar(
                    tabs = tabs,
                    currentRoute = currentRoute,
                    onSelect = { route ->
                        nav.navigate(route) {
                            popUpTo(Route.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(pad),
            enterTransition = { enter },
            exitTransition = { exit },
            popEnterTransition = { popEnter },
            popExitTransition = { popExit }
        ) {
            composable(Route.Home.route) { HomeDashboardScreen(app, nav, settingsViewModel) }
            composable(Route.Developers.route) { CategoryScreen(ToolGroup.DEVELOPER, app, nav) }
            composable(Route.Projects.route) { ProjectsScreen(app, nav) }
            composable(Route.ToolsTab.route) { ToolsHubScreen(app, nav) }
            composable(Route.Profile.route) { ProfileScreen(app, nav, settingsViewModel) }

            composable(
                route = Route.Category.route,
                arguments = listOf(navArgument("groupId") { type = NavType.StringType })
            ) { entry ->
                val gid = entry.arguments?.getString("groupId").orEmpty()
                val group = ToolGroup.values().firstOrNull { it.id == gid } ?: ToolGroup.EVERYDAY
                CategoryScreen(group, app, nav)
            }
            composable(
                route = Route.ProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { entry ->
                val projectId = entry.arguments?.getString("projectId").orEmpty()
                ProjectDetailScreen(app, nav, projectId)
            }

            composable(Route.Settings.route) { SettingsScreen(settingsViewModel) }
            composable(Route.Favorites.route) { FavoritesScreen(app, nav) }
            composable(Route.History.route) { HistoryScreen(app, nav) }
            composable(Route.Notes.route) { com.becetti.tools.features.notes.NotesScreen(app, nav) }

            // Every registered tool gets exactly one destination, dispatched by ToolHost.
            ToolRegistry.all.forEach { spec ->
                composable(spec.route) { ToolHost(spec = spec, app = app, nav = nav) }
            }
        }
    }
}

/** Custom bottom bar: animated pill indicator, colour + scale transitions, RTL safe. */
@Composable
private fun BecettiBottomBar(
    tabs: List<BottomDestination>,
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    val palette = BecettiTheme.palette
    Surface(
        color = palette.navSurface,
        border = BorderStroke(1.dp, palette.border.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val selected = currentRoute == tab.route
                val accent = MaterialTheme.colorScheme.primary
                val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1f else 0.94f,
                    animationSpec = tween(motionDuration(Motion.Fast)),
                    label = "tabScale"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .pressScale(interaction, 0.94f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(interactionSource = interaction, indication = null) { onSelect(tab.route) }
                        .padding(vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .width(40.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selected) accent.copy(alpha = 0.16f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (selected) accent else palette.textSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer { scaleX = scale; scaleY = scale }
                        )
                    }
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) accent else palette.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** MainActivity already applies system bar padding to the root surface. */
private fun Modifier.statusBarsPaddingIfNeeded(): Modifier = this
