package com.example.mooddiary.data.database

import androidx.room.TypeConverter
import com.example.mooddiary.data.model.Mood
import kotlinx.datetime.LocalDateTime

class Converters {

    @TypeConverter
    fun fromMood(mood: Mood): Int = mood.value

    @TypeConverter
    fun toMood(value: Int): Mood = Mood.fromValue(value)

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime): String = dateTime.toString()

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String): LocalDateTime = LocalDateTime.parse(dateTimeString)
}
