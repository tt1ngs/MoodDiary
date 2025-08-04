package com.example.mooddiary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import com.example.mooddiary.R

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mood: Mood,
    val note: String = "",
    val dateTime: LocalDateTime,
    val sentimentScore: Float? = null // -1.0 (негативный) до 1.0 (позитивный)
)

enum class Mood(val emoji: String, val labelRes: Int, val value: Int) {
    VERY_SAD("😢", R.string.mood_very_sad, 1),
    SAD("😔", R.string.mood_sad, 2),
    SLIGHTLY_SAD("😕", R.string.mood_slightly_sad, 3),
    NEUTRAL("😐", R.string.mood_neutral, 4),
    SLIGHTLY_HAPPY("🙂", R.string.mood_slightly_happy, 5),
    HAPPY("😊", R.string.mood_happy, 6),
    VERY_HAPPY("😄", R.string.mood_very_happy, 7);

    companion object {
        fun fromValue(value: Int): Mood = entries.find { it.value == value } ?: NEUTRAL
    }
}
