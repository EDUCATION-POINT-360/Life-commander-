package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.LifeCommanderApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val app = context.applicationContext as? LifeCommanderApp ?: return
            CoroutineScope(Dispatchers.IO).launch {
                val activities = app.repository.getAllRoutineActivities()
                for (activity in activities) {
                    if (activity.reminderEnabled) {
                        val hour = activity.startTimeMinutes / 60
                        val minute = activity.startTimeMinutes % 60
                        AlarmScheduler.scheduleActivityAlarm(
                            context = context,
                            id = activity.id.toInt(),
                            title = "Life Commander: ${activity.title}",
                            message = "Upcoming: ${activity.formattedTimeRange()} (${activity.category.name})",
                            hourOfDay = hour,
                            minute = minute,
                            minutesBefore = activity.reminderMinutesBefore
                        )
                    }
                }
            }
        }
    }
}
