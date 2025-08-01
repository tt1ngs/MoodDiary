package com.example.mooddiary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import kotlinx.datetime.plus
import kotlinx.datetime.minus

@Composable
fun CalendarScreen(
    moodEntries: List<MoodEntry>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var currentMonth by remember { mutableStateOf(today.let { LocalDate(it.year, it.monthNumber, 1) }) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = {
                    currentMonth = currentMonth.minus(1, DateTimeUnit.MONTH)
                },
                onNextMonth = {
                    currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeekDaysHeader()

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                currentMonth = currentMonth,
                moodEntries = moodEntries,
                onDateSelected = onDateSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            MoodLegend()
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Предыдущий месяц",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = getMonthYearText(currentMonth),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Следующий месяц",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun WeekDaysHeader() {
    val weekDays = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDays.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: LocalDate,
    moodEntries: List<MoodEntry>,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    // Вычисляем первый день месяца и его день недели
    val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.ordinal + 1) % 7 // Понедельник = 0

    // Количество дней в месяце
    val daysInMonth = when (currentMonth.monthNumber) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (currentMonth.year % 4 == 0 && (currentMonth.year % 100 != 0 || currentMonth.year % 400 == 0)) 29 else 28
        else -> 30
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Пустые ячейки для выравнивания первого дня месяца
        items(firstDayOfWeek) {
            Box(modifier = Modifier.size(40.dp))
        }

        // Дни месяца
        items(daysInMonth) { dayIndex ->
            val day = dayIndex + 1
            val date = LocalDate(currentMonth.year, currentMonth.monthNumber, day)
            val moodEntry = moodEntries.find { it.dateTime.date == date }
            val isToday = date == today

            CalendarDay(
                day = day,
                moodEntry = moodEntry,
                isToday = isToday,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    moodEntry: MoodEntry?,
    isToday: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = moodEntry?.let { getMoodColor(it.mood) } ?: Color.Transparent

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor.copy(alpha = if (moodEntry != null) 0.7f else 0.1f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (moodEntry != null) {
            Text(
                text = moodEntry.mood.emoji,
                fontSize = 16.sp
            )
        } else {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }

        // Подсветка текущего дня
        if (isToday) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
fun MoodLegend() {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Легенда настроений",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Mood.entries.chunked(2).forEach { moodPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    moodPair.forEach { mood ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = getMoodColor(mood),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mood.emoji,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(mood.labelRes),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getMonthYearText(date: LocalDate): String {
    val monthNames = arrayOf(
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )
    return "${monthNames[date.monthNumber - 1]} ${date.year}"
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
