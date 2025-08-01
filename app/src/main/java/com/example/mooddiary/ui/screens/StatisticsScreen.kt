package com.example.mooddiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import com.example.mooddiary.ui.components.GlassCard
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

@Composable
fun StatisticsScreen(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val thirtyDaysAgo = today.minus(30, DateTimeUnit.DAY)
    val recentEntries = moodEntries.filter { entry ->
        entry.dateTime.date >= thirtyDaysAgo
    }

    val moodStats = calculateMoodStatistics(recentEntries)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Заголовок
        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp
        )

        // Общая статистика в виде карточек
        ModernStatsOverview(
            totalEntries = recentEntries.size,
            averageMood = calculateAverageMood(recentEntries),
            moodStats = moodStats
        )

        // Визуальное распределение настроений
        MoodDistributionVisual(moodStats = moodStats)

        // Тренды и инсайты
        MoodInsights(recentEntries = recentEntries, moodStats = moodStats)
    }
}

@Composable
fun ModernStatsOverview(
    totalEntries: Int,
    averageMood: Float,
    moodStats: Map<Mood, Int>
) {
    val positiveCount = (moodStats[Mood.HAPPY] ?: 0) + (moodStats[Mood.VERY_HAPPY] ?: 0) + (moodStats[Mood.SLIGHTLY_HAPPY] ?: 0)
    val negativeCount = (moodStats[Mood.SAD] ?: 0) + (moodStats[Mood.VERY_SAD] ?: 0) + (moodStats[Mood.SLIGHTLY_SAD] ?: 0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Карточка общих записей
        ModernStatCard(
            title = "Записей",
            value = totalEntries.toString(),
            subtitle = "за 30 дней",
            color = AccentPurple,
            modifier = Modifier.weight(1f)
        )

        // Карточка среднего настроения
        ModernStatCard(
            title = "Средний балл",
            value = String.format("%.1f", averageMood),
            subtitle = "из 7",
            color = SoftBlue,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Позитивные дни
        ModernStatCard(
            title = "Хорошие дни",
            value = positiveCount.toString(),
            subtitle = "${if (totalEntries > 0) (positiveCount * 100 / totalEntries) else 0}%",
            color = MoodHappy,
            modifier = Modifier.weight(1f)
        )

        // Негативные дни
        ModernStatCard(
            title = "Сложные дни",
            value = negativeCount.toString(),
            subtitle = "${if (totalEntries > 0) (negativeCount * 100 / totalEntries) else 0}%",
            color = MoodSad,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ModernStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 36.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MoodDistributionVisual(moodStats: Map<Mood, Int>) {
    val totalEntries = moodStats.values.sum()

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Распределение настроений",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (totalEntries > 0) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Mood.entries.forEach { mood ->
                    val count = moodStats[mood] ?: 0
                    val percentage = if (totalEntries > 0) (count.toFloat() / totalEntries) else 0f

                    MoodVisualBar(
                        mood = mood,
                        count = count,
                        percentage = percentage
                    )
                }
            }
        } else {
            Text(
                text = "Нет данных для отображения",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MoodVisualBar(
    mood: Mood,
    count: Int,
    percentage: Float
) {
    val moodColor = getMoodColor(mood)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Эмодзи и название
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(120.dp)
        ) {
            Text(
                text = mood.emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(mood.labelRes),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontSize = 12.sp
            )
        }

        // Визуальная полоса
        Box(
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(moodColor)
            )
        }

        // Число и процент
        Text(
            text = "$count (${(percentage * 100).toInt()}%)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.width(60.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun MoodInsights(
    recentEntries: List<MoodEntry>,
    moodStats: Map<Mood, Int>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Инсайты",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Самое частое настроение
            val mostFrequentMood = moodStats.maxByOrNull { it.value }
            if (mostFrequentMood != null && mostFrequentMood.value > 0) {
                InsightItem(
                    emoji = mostFrequentMood.key.emoji,
                    title = "Самое частое настроение",
                    description = "${stringResource(mostFrequentMood.key.labelRes)} (${mostFrequentMood.value} раз)"
                )
            }

            // Общий тренд
            val positiveCount = (moodStats[Mood.HAPPY] ?: 0) + (moodStats[Mood.VERY_HAPPY] ?: 0) + (moodStats[Mood.SLIGHTLY_HAPPY] ?: 0)
            val totalCount = moodStats.values.sum()
            val positiveRatio = if (totalCount > 0) positiveCount.toFloat() / totalCount else 0f

            val trendEmoji = when {
                positiveRatio > 0.6f -> "📈"
                positiveRatio > 0.4f -> "📊"
                else -> "📉"
            }

            val trendText = when {
                positiveRatio > 0.6f -> "Отличный период! Много позитивных дней"
                positiveRatio > 0.4f -> "Сбалансированный период"
                else -> "Стоит обратить внимание на самочувствие"
            }

            InsightItem(
                emoji = trendEmoji,
                title = "Общий тренд",
                description = trendText
            )

            // Активность
            if (totalCount > 0) {
                InsightItem(
                    emoji = "✍️",
                    title = "Активность ведения дневника",
                    description = "Записей в среднем ${totalCount / 30} в день"
                )
            }
        }
    }
}

@Composable
fun InsightItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color.White.copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

private fun calculateMoodStatistics(entries: List<MoodEntry>): Map<Mood, Int> {
    return entries.groupBy { it.mood }
        .mapValues { it.value.size }
}

private fun calculateAverageMood(entries: List<MoodEntry>): Float {
    if (entries.isEmpty()) return 0f
    return entries.map { it.mood.value }.average().toFloat()
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
