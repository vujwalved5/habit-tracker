package com.example.habittracker.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AmberOchre,
    onPrimary = SilverWhite,
    tertiary = AmberMid,
    background = CanvasBlack,
    surface = TileDeep,
    onBackground = SilverWhite,
    onSurface = SilverWhite
)

private val LightColorScheme = DarkColorScheme // Dark-only theme

@Composable
fun WidgetTheme(
    darkTheme: Boolean = true, // Force dark
    dynamicColor: Boolean = false, // Always false
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CanvasBlack.toArgb()
            window.navigationBarColor = CanvasBlack.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
