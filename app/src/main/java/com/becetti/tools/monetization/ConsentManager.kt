package com.becetti.tools.monetization

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.becetti.tools.core.utils.becettiDataStore
import kotlinx.coroutines.flow.map

class ConsentManager(private val ctx: Context) {
    private val KEY = booleanPreferencesKey("ads_consent_granted")
    val consentFlow = ctx.becettiDataStore.data.map { it[KEY] ?: false }
    suspend fun grantConsent(v: Boolean) { ctx.becettiDataStore.edit { it[KEY] = v } }
}
