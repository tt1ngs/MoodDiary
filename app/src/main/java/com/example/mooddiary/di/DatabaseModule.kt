package com.example.mooddiary.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.example.mooddiary.data.database.MoodDiaryDatabase
import com.example.mooddiary.data.database.MoodEntryDao
import com.example.mooddiary.data.datastore.UserPreferencesRepository
import com.example.mooddiary.data.notifications.NotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoodDiaryDatabase {
        return Room.databaseBuilder(
            context,
            MoodDiaryDatabase::class.java,
            "mood_diary_database"
        ).build()
    }

    @Provides
    fun provideMoodEntryDao(database: MoodDiaryDatabase): MoodEntryDao {
        return database.moodEntryDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context,
        userPreferencesRepository: UserPreferencesRepository
    ): NotificationManager {
        return NotificationManager(context, userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
