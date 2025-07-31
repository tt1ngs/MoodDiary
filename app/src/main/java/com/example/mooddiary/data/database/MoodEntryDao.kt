package com.example.mooddiary.data.database

import androidx.room.*
import com.example.mooddiary.data.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface MoodEntryDao {

    @Query("SELECT * FROM mood_entries ORDER BY dateTime DESC")
    fun getAllEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE date(dateTime) = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): MoodEntry?

    @Query("SELECT * FROM mood_entries WHERE dateTime BETWEEN :startDate AND :endDate ORDER BY dateTime DESC")
    fun getEntriesBetweenDates(startDate: String, endDate: String): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): MoodEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: MoodEntry): Long

    @Update
    suspend fun updateEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteEntry(entry: MoodEntry)

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllEntries()

    @Query("SELECT mood, COUNT(*) as count FROM mood_entries WHERE dateTime BETWEEN :startDate AND :endDate GROUP BY mood")
    suspend fun getMoodStatistics(startDate: String, endDate: String): List<MoodStatistic>
}

data class MoodStatistic(
    val mood: Int,
    val count: Int
)
