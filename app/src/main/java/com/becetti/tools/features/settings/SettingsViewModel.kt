package com.becetti.tools.features.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.becetti.tools.BecettiToolsApp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val app: BecettiToolsApp, private val ds: DataStore<Preferences>) : ViewModel() {
    private val LANG = stringPreferencesKey("language")
    private val THEME = stringPreferencesKey("theme")
    val language: StateFlow<String> = ds.data.map{ it[LANG] ?: "en" }.stateIn(viewModelScope, SharingStarted.Eagerly, "en")
    val theme: StateFlow<String> = ds.data.map{ it[THEME] ?: "system" }.stateIn(viewModelScope, SharingStarted.Eagerly, "system")

    fun setLanguage(l: String){
        viewModelScope.launch{
            ds.edit{ it[LANG]=l }
            // backup to sharedPrefs for attachBaseContext (which runs before DataStore)
            app.getSharedPreferences("becetti_prefs_backup", Context.MODE_PRIVATE).edit().putString("language_backup", l).apply()
        }
    }
    fun setTheme(t: String){ viewModelScope.launch{ ds.edit{ it[THEME]=t } } }
    fun clearHistory(){ viewModelScope.launch{ app.database.historyDao().clear() } }

    class Factory(private val app: BecettiToolsApp, private val ds: DataStore<Preferences>): ViewModelProvider.Factory{
        override fun <T: ViewModel> create(c: Class<T>): T = SettingsViewModel(app, ds) as T
    }
}
