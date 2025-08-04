package com.example.mooddiary.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import kotlinx.datetime.LocalDate
import kotlin.math.*

@Composable
fun MoodLineChart(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "chart_animation"
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Тренд настроения",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (moodEntries.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
            ) {
                drawMoodLineChart(moodEntries, animationProgress)
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Недостаточно данных",
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun MoodBarChart(
    moodStats: Map<Mood, Int>,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "bar_animation"
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Распределение эмоций",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(horizontal = 16.dp)
            ) {
                drawMoodBarChart(moodStats, animationProgress)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Mood.entries.forEach { mood ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(getMoodDrawable(mood)),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${moodStats[mood] ?: 0}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YearlyMoodGrid(
    moodEntries: List<MoodEntry>,
    year: Int = 2024,
    modifier: Modifier = Modifier
) {
    val monthNames = listOf(
        "Я", "Ф", "М", "А", "М", "И",
        "И", "А", "С", "О", "Н", "Д"
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Настроения в $year году",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthNames.forEach { month ->
                    Text(
                        text = month,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            for (week in 1..5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (month in 1..12) {
                        val dayInMonth = (week - 1) * 6 + (month % 6) + 1
                        if (dayInMonth <= 31) {
                            YearMoodCell(
                                mood = getMoodForDate(moodEntries, year, month, dayInMonth),
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoodLegendItem(color = MoodVerySad, label = "Очень грустно")
            MoodLegendItem(color = MoodNeutral, label = "Нейтрально")
            MoodLegendItem(color = MoodVeryHappy, label = "Очень радостно")
        }
    }
}

@Composable
private fun YearMoodCell(
    mood: Mood?,
    modifier: Modifier = Modifier
) {
    val animationDelay = remember { (0..500).random() }
    val scale by animateFloatAsState(
        targetValue = if (mood != null) 1f else 0.3f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "cell_scale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(
                when (mood) {
                    Mood.VERY_SAD -> MoodVerySad
                    Mood.SAD -> MoodSad
                    Mood.SLIGHTLY_SAD -> MoodSlightlySad
                    Mood.NEUTRAL -> MoodNeutral
                    Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
                    Mood.HAPPY -> MoodHappy
                    Mood.VERY_HAPPY -> MoodVeryHappy
                    null -> Color.Gray.copy(alpha = 0.2f)
                }.copy(alpha = 0.8f)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun MoodLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

private fun DrawScope.drawMoodLineChart(
    moodEntries: List<MoodEntry>,
    animationProgress: Float
) {
    if (moodEntries.isEmpty()) return

    val sortedEntries = moodEntries.sortedBy { it.dateTime }
    val maxValue = 7f
    val minValue = 1f

    val stepX = size.width / (sortedEntries.size - 1).coerceAtLeast(1)
    val stepY = size.height / (maxValue - minValue)

    val path = Path()
    val gradientPath = Path()

    sortedEntries.forEachIndexed { index, entry ->
        val x = index * stepX
        val y = size.height - (entry.mood.value - minValue) * stepY

        val animatedX = x * animationProgress
        val animatedY = if (animationProgress > index.toFloat() / sortedEntries.size) y else size.height

        if (index == 0) {
            path.moveTo(animatedX, animatedY)
            gradientPath.moveTo(animatedX, size.height)
            gradientPath.lineTo(animatedX, animatedY)
        } else {
            path.lineTo(animatedX, animatedY)
            gradientPath.lineTo(animatedX, animatedY)
        }
    }

    gradientPath.lineTo(size.width * animationProgress, size.height)
    gradientPath.close()

    drawPath(
        path = gradientPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                MoodVeryHappy.copy(alpha = 0.3f),
                Color.Transparent
            )
        )
    )

    drawPath(
        path = path,
        color = MoodVeryHappy,
        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
    )

    sortedEntries.forEachIndexed { index, entry ->
        if (animationProgress > index.toFloat() / sortedEntries.size) {
            val x = index * stepX * animationProgress
            val y = size.height - (entry.mood.value - minValue) * stepY

            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
            drawCircle(
                color = getMoodColor(entry.mood),
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

private fun DrawScope.drawMoodBarChart(
    moodStats: Map<Mood, Int>,
    animationProgress: Float
) {
    val maxCount = moodStats.values.maxOrNull() ?: 1
    val barWidth = size.width / 7
    val maxHeight = size.height * 0.8f

    Mood.entries.forEachIndexed { index, mood ->
        val count = moodStats[mood] ?: 0
        val barHeight = (count.toFloat() / maxCount) * maxHeight * animationProgress
        val x = index * barWidth + barWidth * 0.1f
        val barActualWidth = barWidth * 0.8f

        drawRoundRect(
            color = getMoodColor(mood),
            topLeft = androidx.compose.ui.geometry.Offset(x, size.height - barHeight),
            size = androidx.compose.ui.geometry.Size(barActualWidth, barHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
        )
        if (count > 0 && animationProgress > 0.8f) {
            // TODO: добавить отрисовку текста с количеством
        }
    }
}

private fun getMoodColor(mood: Mood): Color = when (mood) {
    Mood.VERY_SAD -> MoodVerySad
    Mood.SAD -> MoodSad
    Mood.SLIGHTLY_SAD -> MoodSlightlySad
    Mood.NEUTRAL -> MoodNeutral
    Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
    Mood.HAPPY -> MoodHappy
    Mood.VERY_HAPPY -> MoodVeryHappy
}

private fun getMoodForDate(
    moodEntries: List<MoodEntry>,
    year: Int,
    month: Int,
    day: Int
): Mood? {
    return try {
        val targetDate = LocalDate(year, month, day)
        moodEntries.find { it.dateTime.date == targetDate }?.mood
    } catch (e: Exception) {
        null
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
