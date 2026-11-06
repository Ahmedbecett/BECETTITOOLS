package com.becetti.tools.monetization

import android.content.Context
import android.util.Log

class MonetagManager(private val ctx: Context) {
    private var initialized = false
    fun initialize(appId: String? = null) {
        if (initialized) return
        // TODO: Replace with official Monetag SDK init when account provided.
        // Example: MonetagSdk.init(ctx, appId)
        Log.i("Monetag", "MonetagManager initialized in test mode (no SDK). Provide SDK to enable.")
        initialized = true
    }
    fun isAvailable(): Boolean = false // becomes true when SDK added
    fun showInterstitial(onDone: () -> Unit) { onDone() }
}
