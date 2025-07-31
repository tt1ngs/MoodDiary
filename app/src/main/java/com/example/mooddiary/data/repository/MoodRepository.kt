package com.example.mooddiary.data.repository

import com.example.mooddiary.data.database.MoodEntryDao
import com.example.mooddiary.data.database.MoodStatistic
import com.example.mooddiary.data.model.MoodEntry
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@ViewModelScoped
class MoodRepository @Inject constructor(
    private val moodEntryDao: MoodEntryDao
) {

    fun getAllEntries(): Flow<List<MoodEntry>> = moodEntryDao.getAllEntries()

    suspend fun getEntryByDate(date: LocalDate): MoodEntry? {
        return moodEntryDao.getEntryByDate(date.toString())
    }

    fun getEntriesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<MoodEntry>> {
        return moodEntryDao.getEntriesBetweenDates(startDate.toString(), endDate.toString())
    }

    suspend fun insertEntry(entry: MoodEntry): Long {
        return moodEntryDao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: MoodEntry) {
        moodEntryDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: MoodEntry) {
        moodEntryDao.deleteEntry(entry)
    }

    suspend fun getMoodStatistics(startDate: LocalDate, endDate: LocalDate): List<MoodStatistic> {
        return moodEntryDao.getMoodStatistics(startDate.toString(), endDate.toString())
    }
}
