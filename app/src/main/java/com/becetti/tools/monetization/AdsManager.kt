package com.becetti.tools.monetization

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

class AdsManager(ctx: Context) {
    val monetag = MonetagManager(ctx)
    val adsterra = AdsterraManager(ctx)
    val consent = ConsentManager(ctx)

    private val _isGoogleAdsReady = MutableStateFlow(false)
    val isGoogleAdsReady: StateFlow<Boolean> = _isGoogleAdsReady
    private val googleAdsInitializationStarted = AtomicBoolean(false)
    private val googleAdsSdkInitialized = AtomicBoolean(false)

    private var lastInterstitial = 0L
    private var interstitialShown = 0

    fun initialize(activity: Activity) {
        consent.requestConsent(activity) { canRequestAds ->
            if (canRequestAds) initializeGoogleMobileAds(activity.applicationContext)
        }
        monetag.initialize()
    }

    private fun initializeGoogleMobileAds(context: Context) {
        if (!googleAdsInitializationStarted.compareAndSet(false, true)) {
            if (googleAdsSdkInitialized.get() && consent.canRequestAds()) {
                _isGoogleAdsReady.value = true
            }
            return
        }
        // Google recommends initializing on a background thread to avoid blocking app startup.
        Thread({
            runCatching {
                MobileAds.initialize(context) {
                    googleAdsSdkInitialized.set(true)
                    _isGoogleAdsReady.value = consent.canRequestAds()
                }
            }.onFailure {
                // Ads are optional: SDK/Play services failures must not interrupt app usage.
                googleAdsInitializationStarted.set(false)
            }
        }, "GoogleMobileAdsInit").start()
    }

    fun showPrivacyOptions(activity: Activity) {
        consent.showPrivacyOptions(activity) { canRequestAds ->
            if (canRequestAds) {
                initializeGoogleMobileAds(activity.applicationContext)
            } else {
                // Removing the ad composables releases currently displayed ad objects/views.
                _isGoogleAdsReady.value = false
            }
        }
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
