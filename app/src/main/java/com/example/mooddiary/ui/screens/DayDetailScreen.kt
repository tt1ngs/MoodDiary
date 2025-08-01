package com.example.mooddiary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.components.GlassCard
import com.example.mooddiary.ui.theme.*
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    date: LocalDate,
    moodEntry: MoodEntry?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Заголовок с кнопкой назад
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formatDate(date),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        if (moodEntry != null) {
            // Карточка настроения
            MoodCard(moodEntry = moodEntry)

            // Карточка с заметкой
            if (moodEntry.note.isNotBlank()) {
                NoteCard(note = moodEntry.note)
            }

            // AI анализ
            moodEntry.sentimentScore?.let { score ->
                AIAnalysisCard(sentimentScore = score)
            }
        } else {
            // Пустое состояние
            EmptyDayCard()
        }
    }
}

@Composable
fun MoodCard(moodEntry: MoodEntry) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Настроение дня",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        getMoodColor(moodEntry.mood).copy(alpha = 0.9f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(getMoodDrawable(moodEntry.mood)),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(moodEntry.mood.labelRes),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = formatTime(moodEntry.dateTime),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoteCard(note: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Заметка",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = note,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            lineHeight = 24.sp
        )
    }
}

@Composable
fun AIAnalysisCard(sentimentScore: Float) {
    val (emoji, description, color) = when {
        sentimentScore > 0.3f -> Triple("😊", "Позитивная тональность", MoodHappy)
        sentimentScore < -0.3f -> Triple("😔", "Негативная тональность", MoodSad)
        else -> Triple("😐", "Нейтральная тональность", MoodNeutral)
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Анализ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = "Оценка: ${String.format("%.2f", sentimentScore)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun EmptyDayCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📝",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет записей",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "В этот день вы не делали записей о настроении",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatDate(date: LocalDate): String {
    val monthNames = arrayOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )
    return "${date.dayOfMonth} ${monthNames[date.monthNumber - 1]} ${date.year}"
}

private fun formatTime(dateTime: kotlinx.datetime.LocalDateTime): String {
    return String.format("%02d:%02d", dateTime.hour, dateTime.minute)
}

private fun getMoodColor(mood: com.example.mooddiary.data.model.Mood): Color {
    return when (mood) {
        com.example.mooddiary.data.model.Mood.VERY_HAPPY -> MoodVeryHappy
        com.example.mooddiary.data.model.Mood.HAPPY -> MoodHappy
        com.example.mooddiary.data.model.Mood.SLIGHTLY_HAPPY -> MoodSlightlyHappy
        com.example.mooddiary.data.model.Mood.NEUTRAL -> MoodNeutral
        com.example.mooddiary.data.model.Mood.SLIGHTLY_SAD -> MoodSlightlySad
        com.example.mooddiary.data.model.Mood.SAD -> MoodSad
        com.example.mooddiary.data.model.Mood.VERY_SAD -> MoodVerySad
    }
}

private fun getMoodDrawable(mood: com.example.mooddiary.data.model.Mood): Int = when (mood) {
    com.example.mooddiary.data.model.Mood.VERY_SAD -> R.drawable.mood_very_sad
    com.example.mooddiary.data.model.Mood.SAD -> R.drawable.mood_sad
    com.example.mooddiary.data.model.Mood.SLIGHTLY_SAD -> R.drawable.mood_slightly_sad
    com.example.mooddiary.data.model.Mood.NEUTRAL -> R.drawable.mood_neutral
    com.example.mooddiary.data.model.Mood.SLIGHTLY_HAPPY -> R.drawable.mood_slightly_happy
    com.example.mooddiary.data.model.Mood.HAPPY -> R.drawable.mood_happy
    com.example.mooddiary.data.model.Mood.VERY_HAPPY -> R.drawable.mood_very_happy
}
