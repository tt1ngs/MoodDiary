package com.example.mooddiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import kotlinx.datetime.LocalDate

@Composable
fun StatisticsScreen(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val thirtyDaysAgo = LocalDate(2025, 1, 1)
    val recentEntries = moodEntries.filter { entry ->
        entry.dateTime.date >= thirtyDaysAgo
    }

    val moodStats = calculateMoodStatistics(recentEntries)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Статистика настроения",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "За последние 30 дней",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        OverallStatsCard(
            totalEntries = recentEntries.size,
            averageMood = calculateAverageMood(recentEntries)
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodDistributionCard(moodStats = moodStats)

        Spacer(modifier = Modifier.height(16.dp))

        MoodBreakdownCard(moodStats = moodStats)
    }
}

@Composable
fun OverallStatsCard(
    totalEntries: Int,
    averageMood: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Общая статистика",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Записей",
                    value = totalEntries.toString(),
                    color = SoftBlue
                )

                StatItem(
                    title = "Среднее настроение",
                    value = String.format("%.1f", averageMood),
                    color = CalmingPurple
                )
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 32.sp
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun MoodDistributionCard(moodStats: Map<Mood, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Распределение настроений",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            val totalEntries = moodStats.values.sum()
            if (totalEntries > 0) {
                Mood.entries.forEach { mood ->
                    val count = moodStats[mood] ?: 0
                    val percentage = (count.toFloat() / totalEntries * 100).toInt()

                    MoodBar(
                        mood = mood,
                        count = count,
                        percentage = percentage
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text(
                    text = "Нет данных для отображения",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MoodBar(
    mood: Mood,
    count: Int,
    percentage: Int
) {
    val moodColor = getMoodColor(mood)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = mood.emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(mood.labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = "$count ($percentage%)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage / 100f)
                    .fillMaxHeight()
                    .background(
                        color = moodColor,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun MoodBreakdownCard(moodStats: Map<Mood, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Детальная статистика",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val positiveCount = (moodStats[Mood.HAPPY] ?: 0) + (moodStats[Mood.VERY_HAPPY] ?: 0) + (moodStats[Mood.SLIGHTLY_HAPPY] ?: 0)
            val neutralCount = moodStats[Mood.NEUTRAL] ?: 0
            val negativeCount = (moodStats[Mood.SAD] ?: 0) + (moodStats[Mood.VERY_SAD] ?: 0) + (moodStats[Mood.SLIGHTLY_SAD] ?: 0)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Позитивные",
                    value = positiveCount.toString(),
                    color = MoodHappy
                )

                StatItem(
                    title = "Нейтральные",
                    value = neutralCount.toString(),
                    color = MoodNeutral
                )

                StatItem(
                    title = "Негативные",
                    value = negativeCount.toString(),
                    color = MoodSad
                )
            }
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
