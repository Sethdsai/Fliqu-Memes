package com.fliqu.memes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = TealAccent,
    onPrimary = Color.White,
    primaryContainer = TealAccentDark,
    onPrimaryContainer = TealAccentLight,
    secondary = BlueAccent,
    onSecondary = Color.White,
    tertiary = PurpleAccent,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = RedAccent,
    onError = Color.White
)

@Composable
fun FliquTheme(content: @Composable () -> Unit) {
    val colorScheme = DarkColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? android.app.Activity
            activity?.let {
                it.window.statusBarColor = DarkBg.toArgb()
                WindowCompat.getInsetsController(it.window, view).isAppearanceLightStatusBars = false
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
