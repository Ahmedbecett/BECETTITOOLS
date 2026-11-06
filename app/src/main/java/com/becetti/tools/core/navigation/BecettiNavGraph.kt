package com.becetti.tools.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.becetti.tools.BecettiToolsApp
import com.becetti.tools.features.age.AgeCalculatorScreen
import com.becetti.tools.features.calculator.CalculatorScreen
import com.becetti.tools.features.converter.UnitConverterScreen
import com.becetti.tools.features.currency.CurrencyScreen
import com.becetti.tools.features.favorites.FavoritesScreen
import com.becetti.tools.features.files.FileManagerScreen
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
import com.becetti.tools.features.splash.SplashScreen
import com.becetti.tools.features.text.TextToolkitScreen

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun BecettiNavGraph(app: BecettiToolsApp, settingsViewModel: SettingsViewModel) {
    val nav = rememberNavController()
    val bottomItems = listOf(
        BottomItem(Route.Home.route, "Home", Icons.Filled.Home),
        BottomItem(Route.Categories.route, "Categories", Icons.Filled.Category),
        BottomItem(Route.Favorites.route, "Favorites", Icons.Filled.Star),
        BottomItem(Route.History.route, "History", Icons.Filled.History),
        BottomItem(Route.Settings.route, "Settings", Icons.Filled.Settings)
    )
    // Show bottom bar only after splash
    var showBottomBar by remember { mutableStateOf(false) }
    Scaffold(bottomBar = {
        if (showBottomBar) {
            NavigationBar {
                val current = nav.currentBackStackEntryFlow.collectAsState(initial = nav.currentBackStackEntry).value?.destination?.route
                bottomItems.forEach { item ->
                    NavigationBarItem(selected = current == item.route, onClick = {
                        nav.navigate(item.route) { launchSingleTop = true; popUpTo(Route.Home.route) }
                    }, icon = { Icon(item.icon, contentDescription = item.label) }, label = { Text(item.label) })
                }
            }
        }
    }) { pad ->
        NavHost(navController = nav, startDestination = Route.Splash.route, modifier = Modifier.padding(pad)) {
            composable(Route.Splash.route) {
                showBottomBar = false
                SplashScreen(onFinished = {
                    showBottomBar = true
                    nav.navigate(Route.Home.route) { popUpTo(Route.Splash.route) { inclusive = true } }
                })
            }
            composable(Route.Home.route) { showBottomBar = true; HomeScreen(app, nav) }
            composable(Route.Categories.route) { showBottomBar = true; CategoriesScreen(nav) }
            composable(Route.Favorites.route) { showBottomBar = true; FavoritesScreen(app, nav) }
            composable(Route.History.route) { showBottomBar = true; HistoryScreen(app) }
            composable(Route.Settings.route) { showBottomBar = true; SettingsScreen(settingsViewModel) }
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
            composable(Route.Ocr.route) { OcrScreen() }
            composable(Route.TextToolkit.route) { TextToolkitScreen() }
            composable(Route.Notes.route) { NotesScreen(app, nav) }
            composable(Route.FileManager.route) { FileManagerScreen() }
        }
    }
}
