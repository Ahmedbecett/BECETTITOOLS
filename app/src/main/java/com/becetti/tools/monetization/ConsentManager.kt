package com.becetti.tools.monetization

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

/** Owns the UMP consent flow and exposes whether the publisher privacy entry point is required. */
class ConsentManager(context: Context) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(context.applicationContext)
    private val updateStarted = AtomicBoolean(false)
    private val _privacyOptionsRequired = MutableStateFlow(false)

    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired

    fun requestConsent(activity: Activity, onComplete: (canRequestAds: Boolean) -> Unit) {
        // UMP requires a fresh consent-info update at app launch. Avoid duplicate requests if the
        // Activity is recreated while this single launch's request/form is still in progress.
        if (!updateStarted.compareAndSet(false, true)) return

        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                refreshPrivacyOptionsRequirement()
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    refreshPrivacyOptionsRequirement()
                    onComplete(consentInformation.canRequestAds())
                }
            },
            {
                refreshPrivacyOptionsRequirement()
                // UMP retains the previous session's decision after a request failure.
                onComplete(consentInformation.canRequestAds())
            }
        )
    }

    fun showPrivacyOptions(activity: Activity, onComplete: (canRequestAds: Boolean) -> Unit = {}) {
        if (!isPrivacyOptionsRequired()) return
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            refreshPrivacyOptionsRequirement()
            onComplete(consentInformation.canRequestAds())
        }
    }

    fun canRequestAds(): Boolean = consentInformation.canRequestAds()

    private fun isPrivacyOptionsRequired(): Boolean =
        consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    private fun refreshPrivacyOptionsRequirement() {
        _privacyOptionsRequired.value = isPrivacyOptionsRequired()
    }
}
