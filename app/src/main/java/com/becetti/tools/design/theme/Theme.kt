package com.becetti.tools.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Extra semantic surfaces that Material 3's ColorScheme does not model (card layers, borders,
 * semantic status colours). Screens read these through [BecettiTheme] so both themes stay in sync.
 */
@Immutable
data class BecettiPalette(
    val isDark: Boolean,
    val card: Color,
    val cardElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val error: Color,
    val navSurface: Color,
    val chipSurface: Color
)

val LocalBecettiPalette = staticCompositionLocalOf {
    BecettiPalette(
        isDark = true,
        card = CardDark,
        cardElevated = CardDarkElevated,
        border = BorderDark,
        textPrimary = TextPrimary,
        textSecondary = TextSecondary,
        success = ColorSuccess,
        warning = ColorWarning,
        info = ColorInfo,
        error = ColorError,
        navSurface = SurfaceDark,
        chipSurface = CardDark
    )
}

object BecettiTheme {
    val palette: BecettiPalette
        @Composable get() = LocalBecettiPalette.current
}

private val DarkScheme = darkColorScheme(
    primary = AccentPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2A2660),
    onPrimaryContainer = Color(0xFFDCDAFF),
    inversePrimary = Color(0xFF6257E8),
    secondary = AccentSecondary,
    onSecondary = Color(0xFF04241F),
    secondaryContainer = Color(0xFF0B3A34),
    onSecondaryContainer = Color(0xFFA6F5E9),
    tertiary = AccentHighlight,
    onTertiary = Color(0xFF04212E),
    tertiaryContainer = Color(0xFF0E3446),
    onTertiaryContainer = Color(0xFFBEE8FA),
    background = BgDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    outlineVariant = Color(0xFF202C42),
    error = ColorError,
    onError = Color.White,
    errorContainer = Color(0xFF3B1520),
    onErrorContainer = Color(0xFFFFD9DE),
    inverseSurface = Color(0xFFEDF1F8),
    inverseOnSurface = Color(0xFF16203A),
    scrim = Color.Black
)

private val LightScheme = lightColorScheme(
    primary = AccentPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDEBFF),
    onPrimaryContainer = Color(0xFF241C7A),
    inversePrimary = AccentPrimary,
    secondary = AccentSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD9F5F0),
    onSecondaryContainer = Color(0xFF043F37),
    tertiary = Color(0xFF0B86A8),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD8F1FB),
    onTertiaryContainer = Color(0xFF05303F),
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEEF2F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFEDF1F7),
    error = ColorError,
    onError = Color.White,
    errorContainer = Color(0xFFFFE4E6),
    onErrorContainer = Color(0xFF7A1020),
    inverseSurface = Color(0xFF172033),
    inverseOnSurface = Color(0xFFF5F7FC),
    scrim = Color.Black
)

private val DarkPalette = BecettiPalette(
    isDark = true,
    card = CardDark,
    cardElevated = CardDarkElevated,
    border = BorderDark,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    success = ColorSuccess,
    warning = ColorWarning,
    info = ColorInfo,
    error = ColorError,
    navSurface = SurfaceDark,
    chipSurface = CardDark
)

private val LightPalette = BecettiPalette(
    isDark = false,
    card = CardLight,
    cardElevated = CardLightElevated,
    border = BorderLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    success = ColorSuccess,
    warning = ColorWarning,
    info = Color(0xFF0B86A8),
    error = ColorError,
    navSurface = SurfaceLight,
    chipSurface = Color(0xFFEEF2F9)
)

/** Consistent corner radii used by every card, sheet, field and button. */
val BecettiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun BecettiToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkScheme
        else -> LightScheme
    }
    val palette = if (darkTheme) DarkPalette else LightPalette
    val systemReduced = rememberSystemReduceMotion()

    CompositionLocalProvider(
        LocalBecettiPalette provides palette,
        LocalReduceMotion provides (reduceMotion || systemReduced)
    ) {
        MaterialTheme(
            colorScheme = scheme,
            typography = BecettiTypography,
            shapes = BecettiShapes,
            content = content
        )
    }
}
