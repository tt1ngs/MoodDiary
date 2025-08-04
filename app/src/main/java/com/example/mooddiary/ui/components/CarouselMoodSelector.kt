package com.example.mooddiary.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.ui.theme.*
import kotlin.math.*

@Composable
fun CarouselMoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    val moods = Mood.entries
    val density = LocalDensity.current
    val radiusPx = with(density) { 120.dp.toPx() }

    var isExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300) 
        isExpanded = true
    }

    val expandScale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "expand_scale"
    )

    val selectedIndex = selectedMood?.let { moods.indexOf(it) } ?: (moods.size / 2)

    val targetRotation = -selectedIndex * (360f / moods.size)

    val rotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "carousel_rotation"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "subtle_effects")
    val subtleGlow by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "subtle_glow"
    )

    val selectedPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selected_pulse"
    )

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (selectedMood != null) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        scaleX = expandScale
                        scaleY = expandScale
                        alpha = subtleGlow * 0.6f
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                getAdaptedMoodColor(selectedMood).copy(alpha = 0.15f),
                                Color.Transparent
                            ),
                            radius = 120f
                        ),
                        shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = expandScale
                    scaleY = expandScale
                },
            contentAlignment = Alignment.Center
        ) {
            moods.forEachIndexed { index, mood ->
                val angle = (index * 360f / moods.size) * (PI / 180f)
                val x = (radiusPx * cos(angle)).toFloat()
                val y = (radiusPx * sin(angle)).toFloat()

                val isSelected = mood == selectedMood
                val distance = abs(selectedIndex - index).let {
                    minOf(it, moods.size - it)
                }

                val depthScale = when (distance) {
                    0 -> if (isSelected) selectedPulse else 1f
                    1 -> 0.85f
                    2 -> 0.7f
                    else -> 0.55f
                }

                val depthAlpha = when (distance) {
                    0 -> 1f
                    1 -> 0.85f
                    2 -> 0.6f
                    else -> 0.4f
                }

                MoodItem(
                    mood = mood,
                    isSelected = isSelected,
                    isCentered = distance == 0,
                    scale = depthScale,
                    alpha = depthAlpha,
                    modifier = Modifier
                        .offset(
                            x = with(density) { x.toDp() },
                            y = with(density) { y.toDp() }
                        )
                        .graphicsLayer {
                            rotationZ = -rotation
                        }
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onMoodSelected(mood)
                        }
                )
            }
        }

        if (selectedMood == null && isExpanded) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun MoodItem(
    mood: Mood,
    isSelected: Boolean,
    isCentered: Boolean,
    scale: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "mood_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(300),
        label = "mood_alpha"
    )

    Box(
        modifier = modifier
            .size(70.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                this.alpha = animatedAlpha
            },
        contentAlignment = Alignment.Center
    ) {
        if (isCentered) {
            Box(
                modifier = Modifier
                    .size(if (isSelected) 65.dp else 60.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                getAdaptedMoodColor(mood).copy(alpha = 0.1f),
                                getAdaptedMoodColor(mood).copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        Image(
            imageVector = ImageVector.vectorResource(getMoodDrawable(mood)),
            contentDescription = null,
            modifier = Modifier.size(
                when {
                    isCentered && isSelected -> 55.dp
                    isCentered -> 50.dp
                    else -> 40.dp
                }
            )
        )

        if (isCentered && isSelected) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                getAdaptedMoodColor(mood).copy(alpha = 0.3f),
                                getAdaptedMoodColor(mood).copy(alpha = 0.1f),
                                getAdaptedMoodColor(mood).copy(alpha = 0.3f),
                                getAdaptedMoodColor(mood).copy(alpha = 0.1f),
                                getAdaptedMoodColor(mood).copy(alpha = 0.3f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .padding(1.dp)
                    .background(Color.Transparent, CircleShape)
            )
        }
    }
}

private fun getMoodDrawable(mood: Mood): Int = when (mood) {
    Mood.VERY_SAD -> R.drawable.mood_very_sad
    Mood.SAD -> R.drawable.mood_sad
    Mood.SLIGHTLY_SAD -> R.drawable.mood_slightly_sad
    Mood.NEUTRAL -> R.drawable.mood_neutral
    Mood.SLIGHTLY_HAPPY -> R.drawable.mood_slightly_happy
    Mood.HAPPY -> R.drawable.mood_happy
    Mood.VERY_HAPPY -> R.drawable.mood_very_happy
}

private fun getAdaptedMoodColor(mood: Mood): Color = when (mood) {
    Mood.VERY_SAD -> MoodVerySad
    Mood.SAD -> MoodSad
    Mood.SLIGHTLY_SAD -> MoodSlightlySad
    Mood.NEUTRAL -> MoodNeutral
    Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
    Mood.HAPPY -> MoodHappy
    Mood.VERY_HAPPY -> MoodVeryHappy
}
