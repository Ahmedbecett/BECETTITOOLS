package com.becetti.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.becetti.tools.core.localization.LocalAppStrings
import com.becetti.tools.core.localization.appStringsFor
import com.becetti.tools.core.navigation.BecettiNavGraph
import com.becetti.tools.core.utils.becettiDataStore
import com.becetti.tools.design.theme.BecettiToolsTheme
import com.becetti.tools.features.settings.SettingsViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as BecettiToolsApp).adsManager.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            val app = application as BecettiToolsApp
            val settingsVm: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = SettingsViewModel.Factory(app, becettiDataStore)
            )
            val theme by settingsVm.theme.collectAsState()
            val lang by settingsVm.language.collectAsState()
            val strings = remember(lang) { appStringsFor(lang) }

            LaunchedEffect(lang) {
                runCatching {
                    val locale = Locale(lang)
                    Locale.setDefault(locale)
                    val config = resources.configuration
                    config.setLocale(locale)
                    config.setLayoutDirection(locale)
                    @Suppress("DEPRECATION")
                    resources.updateConfiguration(config, resources.displayMetrics)
                }
            }

            val isDark = when (theme) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }
            val layoutDirection = if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalLayoutDirection provides layoutDirection,
                LocalAppStrings provides strings
            ) {
                BecettiToolsTheme(darkTheme = isDark, dynamicColor = false) {
                    Surface(
                        modifier = Modifier.fillMaxSize().systemBarsPadding(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BecettiNavGraph(app = app, settingsViewModel = settingsVm)
                    }
                }
            }
        }
    }
}
