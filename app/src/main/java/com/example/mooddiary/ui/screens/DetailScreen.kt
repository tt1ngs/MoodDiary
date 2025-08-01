package com.example.mooddiary.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    moodEntry: MoodEntry?,
    onBackClick: () -> Unit,
    onEditClick: (MoodEntry) -> Unit,
    onDeleteClick: (MoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    if (moodEntry == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SoftBlue)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Запись от ${formatDate(moodEntry.dateTime)}",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = { onEditClick(moodEntry) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать",
                        tint = SoftBlue
                    )
                }
                IconButton(onClick = { onDeleteClick(moodEntry) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MoodDisplayCard(moodEntry = moodEntry)

            Spacer(modifier = Modifier.height(16.dp))

            if (moodEntry.note.isNotBlank()) {
                NoteDisplayCard(note = moodEntry.note)
                Spacer(modifier = Modifier.height(16.dp))
            }

            moodEntry.sentimentScore?.let { score ->
                AIAnalysisCard(sentimentScore = score)
            }
        }
    }
}

@Composable
fun MoodDisplayCard(moodEntry: MoodEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) CardBackgroundDark else CardBackgroundLight
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(getMoodDrawable(moodEntry.mood)),
                contentDescription = null,
                modifier = Modifier.size(72.dp).padding(16.dp)
            )

            Text(
                text = stringResource(moodEntry.mood.labelRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatDateTime(moodEntry.dateTime),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoteDisplayCard(note: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) CardBackgroundDark else CardBackgroundLight
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Ваша заметка",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )
        }
    }
}

private fun formatDate(dateTime: LocalDateTime): String {
    val months = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    return "${dateTime.dayOfMonth} ${months[dateTime.monthNumber - 1]} ${dateTime.year}"
}

private fun formatDateTime(dateTime: LocalDateTime): String {
    return "${formatDate(dateTime)} в ${String.format("%02d:%02d", dateTime.hour, dateTime.minute)}"
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
