package com.example.mooddiary.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mooddiary.ui.theme.*

@Composable
fun PulsingButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glass_button_effects")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_scale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient_offset"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 60.dp)
                .scale(if (enabled) scale else 1f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AccentPurple.copy(alpha = if (enabled) glowAlpha * 0.4f else 0.1f),
                            Color.Transparent
                        ),
                        radius = 150f
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
        )

        Box(
            modifier = Modifier
                .size(width = 220.dp, height = 56.dp)
                .scale(if (enabled) scale else 1f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) {
                            listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.1f),
                                AccentPurple.copy(alpha = 0.3f),
                                AccentPurple.copy(alpha = 0.2f)
                            )
                        } else {
                            listOf(
                                Color.Gray.copy(alpha = 0.2f),
                                Color.Gray.copy(alpha = 0.1f)
                            )
                        },
                        start = androidx.compose.ui.geometry.Offset(gradientOffset * 100f, 0f),
                        end = androidx.compose.ui.geometry.Offset((gradientOffset + 1f) * 100f, 100f)
                    )
                )
                .clickable(enabled = enabled) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                content()
            }

            if (enabled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                start = androidx.compose.ui.geometry.Offset(
                                    (gradientOffset - 0.5f) * 240f,
                                    0f
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    gradientOffset * 240f,
                                    56f
                                )
                            )
                        )
                )
            }
        }
        if (enabled) {
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 36.dp)
                    .offset(x = -85.dp)
                    .background(
                        Color.White.copy(alpha = glowAlpha * 0.6f),
                        RoundedCornerShape(1.dp)
                    )
            )

            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 36.dp)
                    .offset(x = 85.dp)
                    .background(
                        Color.White.copy(alpha = glowAlpha * 0.6f),
                        RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}
