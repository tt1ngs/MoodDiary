package com.example.mooddiary.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ViewComfy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mooddiary.R
import com.example.mooddiary.data.model.Mood
import com.example.mooddiary.data.model.MoodEntry
import com.example.mooddiary.ui.theme.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

@Composable
fun FullYearCalendar(
    moodEntries: List<MoodEntry>,
    year: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Календарь настроений $year",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(12),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val monthNames = listOf("Я", "Ф", "М", "А", "М", "И", "И", "А", "С", "О", "Н", "Д")
            items(monthNames) { month ->
                Text(
                    text = month,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(2.dp)
                )
            }

            items(53 * 12) { index ->
                val month = (index % 12) + 1
                val weekInYear = index / 12
                val dayInMonth = weekInYear + 1

                if (dayInMonth <= getDaysInMonth(year, month)) {
                    val date = try {
                        LocalDate(year, month, dayInMonth)
                    } catch (e: Exception) {
                        null
                    }

                    val moodEntry = date?.let { d ->
                        moodEntries.find { it.dateTime.date == d }
                    }

                    YearCalendarCell(
                        mood = moodEntry?.mood,
                        modifier = Modifier.size(8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalendarLegendItem(color = Color.Gray.copy(alpha = 0.3f), label = "Нет записи")
            CalendarLegendItem(color = MoodVerySad, label = "Грустно")
            CalendarLegendItem(color = MoodNeutral, label = "Нейтрально")
            CalendarLegendItem(color = MoodVeryHappy, label = "Радостно")
        }
    }
}

@Composable
private fun YearCalendarCell(
    mood: Mood?,
    modifier: Modifier = Modifier
) {
    val animationDelay = remember { Random.nextInt(0, 1000) }
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "cell_animation"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(1.dp))
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
private fun CalendarLegendItem(
    color: Color,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalendarViewToggle(
    isYearView: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(25.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ToggleButton(
            text = "Месяц",
            icon = Icons.Default.DateRange,
            isSelected = !isYearView,
            onClick = { onToggle(false) }
        )

        ToggleButton(
            text = "Год",
            icon = Icons.Default.ViewComfy,
            isSelected = isYearView,
            onClick = { onToggle(true) }
        )
    }
}

@Composable
private fun ToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AccentPurple else Color.Transparent,
        animationSpec = tween(300),
        label = "button_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "content_color"
    )

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = contentColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> 30
    }
}

private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}
