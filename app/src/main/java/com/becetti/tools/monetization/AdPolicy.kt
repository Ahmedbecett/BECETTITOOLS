package com.becetti.tools.monetization

object AdPolicy {
    const val INTERSTITIAL_MIN_INTERVAL_MS = 90_000L // 90s
    const val MAX_INTERSTITIAL_PER_SESSION = 3
    val NO_AD_ROUTES = setOf("calc_scientific") // calculator
    fun canShowInterstitial(lastShown: Long, shownCount: Int, currentRoute: String): Boolean {
        if (currentRoute in NO_AD_ROUTES) return false
        if (shownCount >= MAX_INTERSTITIAL_PER_SESSION) return false
        return System.currentTimeMillis() - lastShown > INTERSTITIAL_MIN_INTERVAL_MS
    }
}
