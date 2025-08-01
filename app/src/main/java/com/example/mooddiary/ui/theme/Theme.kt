package com.example.mooddiary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentYellow,
    secondary = TextGray,
    tertiary = MoodHappy,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = Black,
    onBackground = White,
    onSurface = White,
    surfaceVariant = MediumGray,
    onSurfaceVariant = TextGray
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlue,
    secondary = MediumGray,
    tertiary = CalmingPurple,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Color(0xFF1A1A1A), // Темно-серый для лучшей читаемости
    onSurface = Color(0xFF2A2A2A), // Темно-серый для текста на поверхности
    surfaceVariant = LightLavender,
    onSurfaceVariant = Color(0xFF4A4A4A) // Серый для вторичного текста
)

@Composable
fun MoodDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Отключаем динамические цвета для стабильности темы
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}