package com.fliqu.memes.ui.theme

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = TealOnPrimary,
    primaryContainer = TealPrimaryContainer,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = TealSecondary,
    onSecondary = TealOnSecondary,
    secondaryContainer = TealSecondaryContainer,
    onSecondaryContainer = TealOnSecondaryContainer,
    tertiary = TealTertiary,
    onTertiary = TealOnTertiary,
    tertiaryContainer = TealTertiaryContainer,
    onTertiaryContainer = TealOnTertiaryContainer,
    background = TealBackground,
    onBackground = TealOnBackground,
    surface = TealSurface,
    onSurface = TealOnSurface,
    surfaceVariant = TealSurfaceVariant,
    onSurfaceVariant = TealOnSurfaceVariant,
    outline = TealOutline,
    outlineVariant = TealOutlineVariant,
    error = TealError,
    errorContainer = TealErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryContainer,
    onPrimary = TealPrimary,
    primaryContainer = TealPrimary,
    onPrimaryContainer = TealOnPrimaryContainer,
    secondary = TealSecondaryContainer,
    onSecondary = TealSecondary,
    secondaryContainer = TealSecondary,
    onSecondaryContainer = TealOnSecondaryContainer,
    tertiary = TealTertiaryContainer,
    onTertiary = TealTertiary,
    tertiaryContainer = TealTertiary,
    onTertiaryContainer = TealOnTertiaryContainer,
    background = Color(0xFF191C1B),
    onBackground = Color(0xFFE1E3E0),
    surface = Color(0xFF191C1B),
    onSurface = Color(0xFFE1E3E0),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBEC9C4),
    outline = Color(0xFF899390),
    outlineVariant = Color(0xFF3F4945)
)

@Composable
fun FliquTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.let {
                val window = it.window
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
