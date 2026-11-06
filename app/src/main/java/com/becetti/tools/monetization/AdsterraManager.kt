package com.becetti.tools.monetization

import android.content.Context
import android.content.Intent
import android.net.Uri

class AdsterraManager(private val ctx: Context) {
    // SmartLink is offered as clearly labeled external offers section.
    var smartLinkUrl: String? = null // Set from remote config or publisher account

    fun openSponsoredOffers() {
        val url = smartLinkUrl ?: return
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
    }
}
