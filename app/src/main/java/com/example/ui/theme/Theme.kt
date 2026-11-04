package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LuxeDarkTextPrimary,
    onPrimary = LuxeEspresso,
    primaryContainer = LuxeDarkSurfaceVariant,
    onPrimaryContainer = LuxeDarkTextPrimary,
    secondary = LuxeTerracotta,
    onSecondary = Color.White,
    secondaryContainer = LuxeTerracotta.copy(alpha = 0.2f),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = LuxeOliveLight,
    onTertiary = LuxeOlive,
    background = LuxeDarkBg,
    onBackground = LuxeDarkTextPrimary,
    surface = LuxeDarkSurface,
    onSurface = LuxeDarkTextPrimary,
    surfaceVariant = LuxeDarkSurfaceVariant,
    onSurfaceVariant = LuxeDarkTextSecondary,
    outline = LuxeDarkBorder,
    outlineVariant = Color(0xFF332D29)
)

private val LightColorScheme = lightColorScheme(
    primary = LuxeEspresso,
    onPrimary = Color.White,
    primaryContainer = LuxeOat,
    onPrimaryContainer = LuxeEspresso,
    secondary = LuxeTerracotta,
    onSecondary = Color.White,
    secondaryContainer = LuxeTerracottaLight,
    onSecondaryContainer = Color(0xFF5E2410),
    tertiary = LuxeOlive,
    onTertiary = Color.White,
    tertiaryContainer = LuxeOliveLight,
    onTertiaryContainer = Color(0xFF1E2816),
    background = LuxeWarmCream,
    onBackground = LuxeEspresso,
    surface = Color.White,
    onSurface = LuxeEspresso,
    surfaceVariant = LuxeLinen,
    onSurfaceVariant = LuxeTaupe,
    outline = LuxeBorder,
    outlineVariant = LuxeSand
)

@Composable
fun LuxeLivingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Preserve custom warm minimalism across both themes
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LuxeTypography,
        content = content
    )
}
