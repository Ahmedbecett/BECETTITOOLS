package com.becetti.tools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.becetti.tools.core.navigation.BecettiNavGraph
import com.becetti.tools.design.theme.BecettiToolsTheme
import com.becetti.tools.features.settings.SettingsViewModel

val ComponentActivity.dataStore by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val app = application as BecettiToolsApp
            val settingsVm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(app, dataStore))
            val theme by settingsVm.theme.collectAsState()
            val lang by settingsVm.language.collectAsState()
            BecettiToolsTheme(darkTheme = theme == "dark", dynamicColor = false) {
                BecettiNavGraph(app = app, settingsViewModel = settingsVm)
            }
        }
    }
}
