package com.becetti.tools.core.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {
    fun applyLanguage(context: Context, langCode: String): Context {
        val locale = when(langCode) {
            "ar" -> Locale("ar")
            "fr" -> Locale("fr")
            else -> Locale("en")
        }
        Locale.setDefault(locale)
        // For AndroidX - use AppCompatDelegate for per-app language (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use platform LocaleManager via AppCompat
            val list = LocaleListCompat.create(locale)
            AppCompatDelegate.setApplicationLocales(list)
        } else {
            // Legacy: update configuration manually
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(locale))
            }
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
        // Also create context with new config
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            return context.createConfigurationContext(config)
        }
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return context
    }

    fun isRtl(langCode: String) = langCode == "ar"

    fun getCurrentLanguage(context: Context): String {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return when(locale.language) {
            "ar" -> "ar"
            "fr" -> "fr"
            else -> "en"
        }
    }
}
