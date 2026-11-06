package com.becetti.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import com.becetti.tools.core.navigation.BecettiNavGraph
import com.becetti.tools.core.utils.becettiDataStore
import com.becetti.tools.design.theme.BecettiToolsTheme
import com.becetti.tools.features.settings.SettingsViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val app = application as BecettiToolsApp
            val settingsVm: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = SettingsViewModel.Factory(app, becettiDataStore)
            )
            val theme by settingsVm.theme.collectAsState()
            val lang by settingsVm.language.collectAsState()
            // Apply language to AppCompatDelegate for correct RTL/LTR
            val locale = Locale(lang)
            Locale.setDefault(locale)
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))

            BecettiToolsTheme(darkTheme = theme == "dark", dynamicColor = false) {
                BecettiNavGraph(app = app, settingsViewModel = settingsVm)
            }
        }
    }
}
