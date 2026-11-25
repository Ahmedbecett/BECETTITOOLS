package com.becetti.tools.monetization

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.AdLoader

private const val BANNER_AD_UNIT_ID = "ca-app-pub-6858364141205978/8031316998"
private const val NATIVE_AD_UNIT_ID = "ca-app-pub-6858364141205978/5268994651"

/** Adaptive banner shown as a separate scroll-list item, never overlaying app controls. */
@Composable
fun AdMobBanner(adsManager: AdsManager, modifier: Modifier = Modifier) {
    val ready by adsManager.isGoogleAdsReady.collectAsState()
    val activity = LocalContext.current as? Activity ?: return
    if (!ready) return

    val lifecycleOwner = LocalLifecycleOwner.current
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val widthDp = constraints.maxWidth / LocalContext.current.resources.displayMetrics.density
        if (widthDp <= 0f) return@BoxWithConstraints

        val adView = remember(activity, widthDp.toInt()) {
            runCatching {
                AdView(activity).apply {
                    adUnitId = BANNER_AD_UNIT_ID
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp.toInt()))
                    adListener = object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            visibility = View.GONE
                        }
                    }
                }
            }.getOrNull()
        } ?: return@BoxWithConstraints

        LaunchedEffect(adView) {
            runCatching { adView.loadAd(AdRequest.Builder().build()) }
                .onFailure { adView.visibility = View.GONE }
        }

        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth().wrapContentHeight()
        )
        DisposableEffect(adView, lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adView.resume()
                    Lifecycle.Event.ON_PAUSE -> adView.pause()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                adView.destroy()
            }
        }
    }
}

/** One clearly attributed native ad on Home; no ad load failures affect the tools list. */
@Composable
fun AdMobNativeAd(adsManager: AdsManager, modifier: Modifier = Modifier) {
    val ready by adsManager.isGoogleAdsReady.collectAsState()
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    DisposableEffect(adsManager, context, ready) {
        var disposed = false
        var loadedAd: NativeAd? = null
        if (ready) {
            runCatching {
                AdLoader.Builder(context, NATIVE_AD_UNIT_ID)
                    .forNativeAd { ad ->
                        if (disposed) {
                            ad.destroy()
                        } else {
                            loadedAd = ad
                            nativeAd = ad
                        }
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            // No placeholder is shown when an ad is unavailable.
                        }
                    })
                    .withNativeAdOptions(NativeAdOptions.Builder().build())
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
        onDispose {
            disposed = true
            loadedAd?.destroy()
            if (nativeAd === loadedAd) nativeAd = null
        }
    }

    val ad = nativeAd ?: return
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AndroidView(
            factory = { createNativeAdView(it) },
            update = { adView ->
                if (adView.tag !== ad) {
                    populateNativeAdView(adView, ad)
                    adView.tag = ad
                }
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp).wrapContentHeight()
        )
    }
}

private fun createNativeAdView(context: android.content.Context): NativeAdView {
    val adView = NativeAdView(context)
    val root = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(context.dp(12), context.dp(10), context.dp(12), context.dp(12))
        setBackgroundColor(Color.rgb(248, 248, 248))
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val attribution = TextView(context).apply {
        text = "Ad"
        textSize = 12f
        setTextColor(Color.DKGRAY)
        gravity = Gravity.END
    }
    val headline = TextView(context).apply {
        textSize = 18f
        setTextColor(Color.BLACK)
        setTypeface(typeface, android.graphics.Typeface.BOLD)
    }
    val advertiser = TextView(context).apply {
        textSize = 12f
        setTextColor(Color.DKGRAY)
    }
    val price = TextView(context).apply { textSize = 13f; setTextColor(Color.DKGRAY) }
    val store = TextView(context).apply { textSize = 13f; setTextColor(Color.DKGRAY) }
    val starRating = TextView(context).apply { textSize = 13f; setTextColor(Color.DKGRAY) }
    val media = MediaView(context).apply {
        minimumHeight = context.dp(120)
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            context.dp(160)
        ).apply { topMargin = context.dp(8) }
    }
    val body = TextView(context).apply {
        textSize = 14f
        setTextColor(Color.DKGRAY)
        maxLines = 3
    }
    val assetRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        val icon = ImageView(context).apply {
            contentDescription = "Ad icon"
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = LinearLayout.LayoutParams(context.dp(48), context.dp(48)).apply {
                marginEnd = context.dp(10)
            }
        }
        addView(icon)
        addView(body, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
    }
    val callToAction = TextView(context).apply {
        textSize = 14f
        setTextColor(Color.WHITE)
        setTypeface(typeface, android.graphics.Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(context.dp(14), context.dp(10), context.dp(14), context.dp(10))
        setBackgroundColor(Color.rgb(230, 94, 30))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = context.dp(10) }
    }

    root.addView(attribution)
    root.addView(headline)
    root.addView(advertiser)
    root.addView(price)
    root.addView(store)
    root.addView(starRating)
    root.addView(media)
    root.addView(assetRow)
    root.addView(callToAction)
    adView.addView(root)
    adView.headlineView = headline
    adView.advertiserView = advertiser
    adView.priceView = price
    adView.storeView = store
    adView.starRatingView = starRating
    adView.mediaView = media
    adView.bodyView = body
    adView.iconView = assetRow.getChildAt(0)
    adView.callToActionView = callToAction
    return adView
}

private fun populateNativeAdView(adView: NativeAdView, ad: NativeAd) {
    (adView.headlineView as? TextView)?.text = ad.headline
    (adView.advertiserView as? TextView)?.apply {
        text = ad.advertiser.orEmpty()
        visibility = if (ad.advertiser.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    (adView.bodyView as? TextView)?.apply {
        text = ad.body.orEmpty()
        visibility = if (ad.body.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    (adView.priceView as? TextView)?.apply {
        text = ad.price.orEmpty()
        visibility = if (ad.price.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    (adView.storeView as? TextView)?.apply {
        text = ad.store.orEmpty()
        visibility = if (ad.store.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    (adView.starRatingView as? TextView)?.apply {
        text = ad.starRating?.toString().orEmpty()
        visibility = if (ad.starRating == null) View.GONE else View.VISIBLE
    }
    (adView.callToActionView as? TextView)?.apply {
        text = ad.callToAction.orEmpty()
        visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    (adView.iconView as? ImageView)?.apply {
        setImageDrawable(ad.icon?.drawable)
        visibility = if (ad.icon == null) View.GONE else View.VISIBLE
    }
    (adView.mediaView as? MediaView)?.apply {
        mediaContent = ad.mediaContent
        visibility = if (ad.mediaContent == null) View.GONE else View.VISIBLE
    }
    adView.setNativeAd(ad)
}

private fun android.content.Context.dp(value: Int): Int =
    (value * resources.displayMetrics.density).toInt()
