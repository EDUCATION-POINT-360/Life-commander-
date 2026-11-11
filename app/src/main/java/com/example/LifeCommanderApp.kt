package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.database.LifeCommanderDatabase
import com.example.data.repositories.CommanderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LifeCommanderApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { LifeCommanderDatabase.getDatabase(this) }
    val repository by lazy { CommanderRepository(database) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        applicationScope.launch {
            repository.seedDefaultRoutineIfEmpty()
            repository.seedDefaultHabitsIfEmpty()
            repository.getOrCreatePrayerRecord()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val routineChannel = NotificationChannel(
                CHANNEL_ROUTINE_REMINDERS,
                "Life Commander Timetable Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled activities, study sessions, and wife time"
                enableVibration(true)
            }

            val prayerChannel = NotificationChannel(
                CHANNEL_PRAYER_REMINDERS,
                "Daily Prayer Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for 5 daily prayer times"
                enableVibration(true)
            }

            val focusChannel = NotificationChannel(
                CHANNEL_FOCUS_MODE,
                "Focus & Distraction Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Discipline warnings and focus timer alerts"
            }

            notificationManager.createNotificationChannel(routineChannel)
            notificationManager.createNotificationChannel(prayerChannel)
            notificationManager.createNotificationChannel(focusChannel)
        }
    }

    companion object {
        const val CHANNEL_ROUTINE_REMINDERS = "channel_routine_reminders"
        const val CHANNEL_PRAYER_REMINDERS = "channel_prayer_reminders"
        const val CHANNEL_FOCUS_MODE = "channel_focus_mode"
    }
}
