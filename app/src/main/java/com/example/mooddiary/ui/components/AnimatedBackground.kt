package com.example.mooddiary.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.ui.theme.*

@Composable
fun AnimatedBackground(
    selectedMood: Mood?,
    modifier: Modifier = Modifier
) {
    val moodColor = selectedMood?.let { getMoodColor(it) } ?: MoodNeutral

    val animatedColor by animateColorAsState(
        targetValue = moodColor,
        animationSpec = tween(1000),
        label = "background_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "background_animation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedColor.copy(alpha = 0.3f),
                        animatedColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    radius = 800f
                )
            )
            .graphicsLayer {
                rotationZ = rotation
            }
    )
}

private fun getMoodColor(mood: Mood): Color {
    return when (mood) {
        Mood.VERY_HAPPY -> MoodVeryHappy
        Mood.HAPPY -> MoodHappy
        Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
        Mood.NEUTRAL -> MoodNeutral
        Mood.SLIGHTLY_SAD -> MoodSlightlySad
        Mood.SAD -> MoodSad
        Mood.VERY_SAD -> MoodVerySad
    }
}
