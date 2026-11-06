package com.becetti.tools

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.room.Room
import com.becetti.tools.core.utils.becettiDataStore
import com.becetti.tools.data.local.database.BecettiDatabase
import com.becetti.tools.monetization.AdsManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class BecettiToolsApp : Application() {
    lateinit var database: BecettiDatabase
        private set
    lateinit var adsManager: AdsManager
        private set

    override fun onCreate() {
        super.onCreate()
        // Apply saved language before anything
        applySavedLanguage()
        database = Room.databaseBuilder(this, BecettiDatabase::class.java, "becetti.db")
            .fallbackToDestructiveMigration()
            .build()
        adsManager = AdsManager(this)
        adsManager.initialize()
    }

    private fun applySavedLanguage() {
        try {
            val prefs = runBlocking { becettiDataStore.data.first() }
            val lang = prefs[androidx.datastore.preferences.core.stringPreferencesKey("language")] ?: "en"
            val locale = Locale(lang)
            Locale.setDefault(locale)
            val list = LocaleListCompat.create(locale)
            AppCompatDelegate.setApplicationLocales(list)
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun attachBaseContext(base: Context) {
        // Apply locale on attach
        val lang = try {
            runBlocking { 
                // Cannot use datastore here easily (blocking), fallback to prefs via sharedPreferences
                val sp = base.getSharedPreferences("becetti_prefs_backup", Context.MODE_PRIVATE)
                sp.getString("language_backup", "en") ?: "en"
            }
        } catch (e: Exception) { "en" }
        val locale = Locale(lang)
        val config = base.resources.configuration
        config.setLocale(locale)
        val ctx = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val localeList = android.os.LocaleList(locale)
            android.os.LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            base.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            base.resources.updateConfiguration(config, base.resources.displayMetrics)
            base
        }
        super.attachBaseContext(ctx)
    }
}
