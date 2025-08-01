package com.example.mooddiary.data.notifications

import android.content.Context
import androidx.work.*
import com.example.mooddiary.data.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationManager @Inject constructor(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    suspend fun scheduleNotifications() {
        val preferences = userPreferencesRepository.userPreferencesFlow.first()

        if (preferences.notificationsEnabled) {
            val workManager = WorkManager.getInstance(context)

            // Отменяем существующие уведомления
            workManager.cancelUniqueWork(MoodReminderWorker.WORK_NAME)

            // Вычисляем время до первого уведомления
            val delay = calculateInitialDelay(preferences.notificationTimeHour, preferences.notificationTimeMinute)

            // Создаем периодическую работу
            val reminderRequest = PeriodicWorkRequestBuilder<MoodReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .addTag("mood_reminder")
                .build()

            // Запускаем работу
            workManager.enqueueUniquePeriodicWork(
                MoodReminderWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderRequest
            )
        }
    }

    suspend fun cancelNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(MoodReminderWorker.WORK_NAME)
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Устанавливаем время уведомления
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Если время уже прошло сегодня, планируем на завтра
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendar.timeInMillis - now
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        userPreferencesRepository.updateNotificationTime(hour, minute)
        // Перепланируем уведомления с новым временем
        scheduleNotifications()
    }

    suspend fun toggleNotifications(enabled: Boolean) {
        userPreferencesRepository.updateNotificationsEnabled(enabled)
        if (enabled) {
            scheduleNotifications()
        } else {
            cancelNotifications()
        }
    }
}
