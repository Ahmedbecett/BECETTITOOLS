package com.becetti.tools.core.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

// Single source of truth for DataStore - used everywhere
val Context.becettiDataStore by preferencesDataStore(name = "becetti_settings")

object PrefKeys {
    val LANGUAGE = stringPreferencesKey("language")
    val THEME = stringPreferencesKey("theme")
    val ADS_CONSENT = booleanPreferencesKey("ads_consent")
    val CACHED_DATE = stringPreferencesKey("cached_date")
    fun cachedRatesKey(base: String) = stringPreferencesKey("cached_rates_${base.lowercase()}")
}
