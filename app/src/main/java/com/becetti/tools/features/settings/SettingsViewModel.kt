package com.becetti.tools.features.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.becetti.tools.BecettiToolsApp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val app: BecettiToolsApp, private val ds: DataStore<Preferences>) : ViewModel() {
    private val LANG = stringPreferencesKey("language")
    private val THEME = stringPreferencesKey("theme")
    private val ADS_CONSENT = booleanPreferencesKey("ads_consent_granted")

    val language: StateFlow<String> = ds.data
        .map { it[LANG] ?: "en" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    val theme: StateFlow<String> = ds.data
        .map { it[THEME] ?: "system" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "system")

    val adsConsent: StateFlow<Boolean> = ds.data
        .map { it[ADS_CONSENT] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setLanguage(l: String) {
        viewModelScope.launch { ds.edit { it[LANG] = l } }
    }

    fun setTheme(t: String) {
        viewModelScope.launch { ds.edit { it[THEME] = t } }
    }

    fun setAdsConsent(granted: Boolean) {
        viewModelScope.launch { ds.edit { it[ADS_CONSENT] = granted } }
    }

    fun clearHistory() {
        viewModelScope.launch { app.database.historyDao().clear() }
    }

    class Factory(private val app: BecettiToolsApp, private val ds: DataStore<Preferences>) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(c: Class<T>): T = SettingsViewModel(app, ds) as T
    }
}
