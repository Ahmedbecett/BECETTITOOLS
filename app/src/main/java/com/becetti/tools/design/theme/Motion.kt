package com.becetti.tools.design.theme

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Motion system (spec §3).
 *
 * Everything is short, restrained and consistent: 150–300ms, one shared easing family, springs only
 * where a physical "settle" reads naturally (selection indicators, success ticks).
 *
 * Reduced motion: honour Android's animator-duration-scale (Developer options / accessibility
 * "Remove animations") and an explicit in-app preference. When enabled, every duration collapses to
 * 0ms so navigation and feedback are instant while still functionally complete.
 */
object Motion {
    const val Instant = 0
    const val Fast = 150
    const val Base = 200
    const val Slow = 260
    const val Slack = 300

    /** Standard easing for entering elements. */
    val Standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Decelerating easing for elements that appear from nothing. */
    val Decelerate: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)

    /** Accelerating easing for elements that leave. */
    val Accelerate: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)

    /** Stagger step for entrance animations in lists/grids. */
    const val Stagger = 28
}

val LocalReduceMotion = compositionLocalOf { false }

/**
 * Reads the platform "remove animations" preference once per composition root.
 */
@Composable
fun rememberSystemReduceMotion(): Boolean {
    val ctx = LocalContext.current
    return remember(ctx) {
        val scale = runCatching {
            Settings.Global.getFloat(ctx.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        }.getOrDefault(1f)
        scale == 0f
    }
}

/** Resolves a duration honouring reduced motion. */
@Composable
fun motionDuration(ms: Int): Int = if (LocalReduceMotion.current) 0 else ms

/** Resolves a stagger delay honouring reduced motion. */
@Composable
fun motionDelay(ms: Int): Int = if (LocalReduceMotion.current) 0 else ms
