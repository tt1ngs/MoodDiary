package com.example.mooddiary.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val DarkGray = Color(0xFF121212)
val LightGray = Color(0xFF1E1E1E)
val MediumGray = Color(0xFF2A2A2A)
val TextGray = Color(0xFFB3B3B3)
val AccentPurple = Color(0xFF8B5CF6) 

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625B71)
val Pink40 = Color(0xFF7D5260)

val BackgroundDark = Color(0xFF000000)
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFF5F5F5)
val CardBackgroundDark = Color(0xFF1E1E1E)
val CardBackgroundLight = Color(0xFFFFFFFF)

val MoodVeryHappy = Color(0xFF10B981)     // Изумрудный
val MoodHappy = Color(0xFF06B6D4)         // Циан
val MoodSlightlyHappy = Color(0xFF8B5CF6) // Фиолетовый
val MoodNeutral = Color(0xFF6B7280)       // Серый
val MoodSlightlySad = Color(0xFFF59E0B)   // Янтарный
val MoodSad = Color(0xFFEF4444)           // Красный
val MoodVerySad = Color(0xFFDC2626)       // Темно-красный

val GradientStart = Color(0xFF8B5CF6)
val GradientEnd = Color(0xFF06B6D4)
val VibrancyOverlay = Color(0x40FFFFFF)

val LightLavender = Color(0xFFE0E7FF)
val SoftBlue = Color(0xFF3B82F6)
val LightBlue = Color(0xFFDDEAFE)
val CalmingPurple = Color(0xFF8B5CF6)
val WhisperPink = Color(0xFFE91E63)

val CardBackground = CardBackgroundLight
val AccentYellow = AccentPurple 

@Composable
fun cardBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) CardBackgroundDark else CardBackgroundLight
}
