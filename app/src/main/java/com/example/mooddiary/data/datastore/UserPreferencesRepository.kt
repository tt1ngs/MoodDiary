package com.example.mooddiary.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mood_diary_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIME_HOUR = intPreferencesKey("notification_time_hour")
        val NOTIFICATION_TIME_MINUTE = intPreferencesKey("notification_time_minute")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                notificationTimeHour = preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] ?: 20,
                notificationTimeMinute = preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] ?: 0,
                isDarkTheme = preferences[PreferencesKeys.DARK_THEME] ?: false,
                isFirstLaunch = preferences[PreferencesKeys.FIRST_LAUNCH] ?: true
            )
        }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_TIME_HOUR] = hour
            preferences[PreferencesKeys.NOTIFICATION_TIME_MINUTE] = minute
        }
    }

    suspend fun updateDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = isDark
        }
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FIRST_LAUNCH] = false
        }
    }
}

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val notificationTimeHour: Int = 20,
    val notificationTimeMinute: Int = 0,
    val isDarkTheme: Boolean = false,
    val isFirstLaunch: Boolean = true
)
