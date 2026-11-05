package com.becetti.tools.design.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = LightOrange,
    secondary = SecondaryNavy,
    background = LightGray,
    surface = androidx.compose.ui.graphics.Color.White,
    error = Error
)
private val DarkScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = DarkOrange,
    secondary = SecondaryNavy,
    background = DeepNavy,
    surface = SecondaryNavy,
    error = Error
)

@Composable
fun BecettiToolsTheme(darkTheme: Boolean = isSystemInDarkTheme(), dynamicColor: Boolean = false, content: @Composable () -> Unit) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkScheme
        else -> LightScheme
    }
    MaterialTheme(colorScheme = scheme, typography = BecettiTypography, content = content)
}
