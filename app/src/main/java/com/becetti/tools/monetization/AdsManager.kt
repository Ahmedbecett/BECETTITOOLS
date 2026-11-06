package com.becetti.tools.monetization

import android.content.Context

class AdsManager(ctx: Context) {
    val monetag = MonetagManager(ctx)
    val adsterra = AdsterraManager(ctx)
    val consent = ConsentManager(ctx)

    private var lastInterstitial = 0L
    private var interstitialShown = 0

    fun initialize() {
        monetag.initialize()
    }

    fun canShowAd(route: String): Boolean = AdPolicy.canShowInterstitial(lastInterstitial, interstitialShown, route)

    fun tryShowInterstitial(route: String, onResult: () -> Unit) {
        if (!canShowAd(route)) { onResult(); return }
        if (monetag.isAvailable()) {
            lastInterstitial = System.currentTimeMillis()
            interstitialShown++
            monetag.showInterstitial(onResult)
        } else {
            onResult()
        }
    }
}
