package com.becetti.tools.monetization

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.becetti.tools.dataStore
import kotlinx.coroutines.flow.map

class ConsentManager(private val ctx: Context) {
    private val KEY = booleanPreferencesKey("ads_consent_granted")
    val consentFlow = ctx.dataStore.data.map { it[KEY] ?: false }
    suspend fun grantConsent(v: Boolean) { ctx.dataStore.edit { it[KEY] = v } }
}
