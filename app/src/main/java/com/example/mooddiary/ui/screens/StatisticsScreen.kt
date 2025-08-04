package com.example.mooddiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import com.example.mooddiary.ui.components.*
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
        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 32.sp
        )
        ModernStatsOverview(
            totalEntries = recentEntries.size,
            averageMood = calculateAverageMood(recentEntries),
            moodStats = moodStats
        )

        MoodLineChart(
            moodEntries = recentEntries.takeLast(14) 
        )

        MoodBarChart(
            moodStats = moodStats
        )

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
        ModernStatCard(
            title = "Записей",
            value = totalEntries.toString(),
            subtitle = "за 30 дней",
            color = AccentPurple,
            modifier = Modifier.weight(1f)
        )

        ModernStatCard(
            title = "Средний балл",
            value = String.format("%.1f", averageMood),
            subtitle = "из 7",
            color = MoodHappy,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ModernStatCard(
            title = "Хорошие дни",
            value = positiveCount.toString(),
            subtitle = "${if (totalEntries > 0) (positiveCount * 100 / totalEntries) else 0}%",
            color = MoodVeryHappy,
            modifier = Modifier.weight(1f)
        )
        ModernStatCard(
            title = "Сложные дни",
            value = negativeCount.toString(),
            subtitle = "${if (totalEntries > 0) (negativeCount * 100 / totalEntries) else 0}%",
            color = MoodVerySad,
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
fun MoodInsights(
    recentEntries: List<MoodEntry>,
    moodStats: Map<Mood, Int>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Анализ и рекомендации",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val mostFrequentMood = moodStats.maxByOrNull { it.value }?.key
            if (mostFrequentMood != null) {
                InsightItem(
                    icon = "📊",
                    text = "Чаще всего у вас было ${getMoodDescription(mostFrequentMood)} настроение"
                )
            }

            if (recentEntries.size >= 7) {
                val firstWeek = recentEntries.take(7).map { it.mood.value }.average()
                val lastWeek = recentEntries.takeLast(7).map { it.mood.value }.average()
                val trend = when {
                    lastWeek > firstWeek + 0.5 -> "📈 Ваше настроение улучшается!"
                    lastWeek < firstWeek - 0.5 -> "📉 Стоит обратить внимание на самочувствие"
                    else -> "📊 Ваше настроение стабильно"
                }
                InsightItem(icon = "", text = trend)
            }

            val positivePercentage = moodStats.filter { it.key.value >= 5 }.values.sum() * 100 / recentEntries.size.coerceAtLeast(1)
            val recommendation = when {
                positivePercentage >= 70 -> "✨ Отлично! Продолжайте в том же духе"
                positivePercentage >= 50 -> "🌱 Попробуйте добавить больше позитивных активностей"
                else -> "🤗 Рассмотрите возможность поговорить с близкими или специалистом"
            }
            InsightItem(icon = "", text = recommendation)
        }
    }
}

@Composable
private fun InsightItem(
    icon: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon.isNotEmpty()) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
        }
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun getMoodDescription(mood: Mood): String = when (mood) {
    Mood.VERY_SAD -> "очень грустное"
    Mood.SAD -> "грустное"
    Mood.SLIGHTLY_SAD -> "слегка грустное"
    Mood.NEUTRAL -> "нейтральное"
    Mood.SLIGHTLY_HAPPY -> "слегка хорошее"
    Mood.HAPPY -> "хорошее"
    Mood.VERY_HAPPY -> "отличное"
}

private fun calculateMoodStatistics(entries: List<MoodEntry>): Map<Mood, Int> {
    return entries.groupBy { it.mood }
        .mapValues { it.value.size }
        .toMutableMap()
        .apply {
            Mood.entries.forEach { mood ->
                if (!containsKey(mood)) {
                    this[mood] = 0
                }
            }
        }
}

private fun calculateAverageMood(entries: List<MoodEntry>): Float {
    return if (entries.isNotEmpty()) {
        entries.map { it.mood.value }.average().toFloat()
    } else {
        4f 
    }
}
