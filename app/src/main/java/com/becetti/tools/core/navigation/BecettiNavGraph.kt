package com.becetti.tools.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.design.components.BecettiIcons
import com.becetti.tools.design.theme.PrimaryOrange
import com.becetti.tools.domain.models.ToolCatalog
import com.becetti.tools.features.age.AgeCalculatorScreen
import com.becetti.tools.features.calculator.CalculatorScreen
import com.becetti.tools.features.converter.UnitConverterScreen
import com.becetti.tools.features.currency.CurrencyScreen
import com.becetti.tools.features.device.DeviceInfoScreen
import com.becetti.tools.features.favorites.FavoritesScreen
import com.becetti.tools.features.health.BmiCalculatorScreen
import com.becetti.tools.features.history.HistoryScreen
import com.becetti.tools.features.home.CategoriesScreen
import com.becetti.tools.features.home.HomeScreen
import com.becetti.tools.features.image.ImageCompressorScreen
import com.becetti.tools.features.notes.NotesScreen
import com.becetti.tools.features.ocr.OcrScreen
import com.becetti.tools.features.password.PasswordGenScreen
import com.becetti.tools.features.password.PasswordStrengthScreen
import com.becetti.tools.features.pdf.ImageToPdfScreen
import com.becetti.tools.features.pdf.PdfMergerScreen
import com.becetti.tools.features.percentage.PercentageScreen
import com.becetti.tools.features.qr.QrGeneratorScreen
import com.becetti.tools.features.qr.QrScannerScreen
import com.becetti.tools.features.settings.SettingsScreen
import com.becetti.tools.features.settings.SettingsViewModel
import com.becetti.tools.features.text.TextToolkitScreen
import com.becetti.tools.features.timer.StopwatchTimerScreen

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecettiNavGraph(app: BecettiToolsApp, settingsViewModel: SettingsViewModel) {
    val nav = rememberNavController()
    val s = LocalAppStrings.current

    val bottomItems = listOf(
        BottomItem(Route.Home.route, s.navHome, Icons.Filled.Home),
        BottomItem(Route.Categories.route, s.navCategories, BecettiIcons.Category),
        BottomItem(Route.Favorites.route, s.navFavorites, Icons.Filled.Star),
        BottomItem(Route.History.route, s.navHistory, BecettiIcons.History),
        BottomItem(Route.Settings.route, s.navSettings, Icons.Filled.Settings)
    )
    val bottomRoutes = remember {
        setOf(
            Route.Home.route,
            Route.Categories.route,
            Route.Favorites.route,
            Route.History.route,
            Route.Settings.route
        )
    }
    val current = nav.currentBackStackEntryFlow
        .collectAsState(initial = nav.currentBackStackEntry)
        .value?.destination?.route

    val activeTool = remember(current) { ToolCatalog.all.find { it.route == current } }

    Scaffold(
        topBar = {
            if (current != null && current !in bottomRoutes) {
                val accent = activeTool?.let { BecettiIcons.colorForTool(it.id) } ?: PrimaryOrange
                TopAppBar(
                    title = {
                        Text(
                            text = activeTool?.localizedName(s) ?: s.appName,
                            fontWeight = FontWeight.Bold,
                            color = accent
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = s.back,
                                tint = accent
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = {
                            nav.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(Route.Home.route) { inclusive = false }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryOrange,
                            selectedTextColor = PrimaryOrange
                        )
                    )
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(pad)
        ) {
            composable(Route.Home.route) { HomeScreen(app, nav, settingsViewModel) }
            composable(Route.Categories.route) { CategoriesScreen(nav) }
            composable(Route.Favorites.route) { FavoritesScreen(app, nav) }
            composable(Route.History.route) { HistoryScreen(app, nav) }
            composable(Route.Settings.route) { SettingsScreen(settingsViewModel) }
            composable(Route.ScientificCalculator.route) { CalculatorScreen(app) }
            composable(Route.UnitConverter.route) { UnitConverterScreen() }
            composable(Route.CurrencyConverter.route) { CurrencyScreen(app) }
            composable(Route.PercentageCalculator.route) { PercentageScreen() }
            composable(Route.AgeCalculator.route) { AgeCalculatorScreen() }
            composable(Route.QrGenerator.route) { QrGeneratorScreen() }
            composable(Route.QrScanner.route) { QrScannerScreen() }
            composable(Route.PasswordGen.route) { PasswordGenScreen() }
            composable(Route.PasswordStrength.route) { PasswordStrengthScreen() }
            composable(Route.ImageToPdf.route) { ImageToPdfScreen() }
            composable(Route.PdfMerger.route) { PdfMergerScreen() }
            composable(Route.ImageCompressor.route) { ImageCompressorScreen() }
            composable(Route.Ocr.route) { OcrScreen(app) }
            composable(Route.TextToolkit.route) { TextToolkitScreen() }
            composable(Route.Notes.route) { NotesScreen(app, nav) }
            composable(Route.BmiCalculator.route) { BmiCalculatorScreen() }
            composable(Route.StopwatchTimer.route) { StopwatchTimerScreen() }
            composable(Route.DeviceInfo.route) { DeviceInfoScreen() }
        }
    }
}
