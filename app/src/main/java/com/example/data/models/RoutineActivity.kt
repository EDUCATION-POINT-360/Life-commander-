package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ActivityCategory {
    STUDY,
    WIFE_TIME,
    SLEEP,
    EXERCISE,
    PRAYER,
    MEALS,
    PERSONAL,
    WORK,
    OTHER
}

enum class PriorityLevel {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "routine_activities")
data class RoutineActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val titleUrdu: String = "",
    val startTimeMinutes: Int, // e.g. 360 = 06:00 AM (minutes from midnight 0..1439)
    val endTimeMinutes: Int,   // e.g. 390 = 06:30 AM
    val category: ActivityCategory,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 10,
    val notes: String = "",
    val isCompletedToday: Boolean = false,
    val isSkippedToday: Boolean = false,
    val skipReason: String = "",
    val lastCompletedDate: String = "" // "YYYY-MM-DD"
) {
    val durationMinutes: Int
        get() {
            return if (endTimeMinutes >= startTimeMinutes) {
                endTimeMinutes - startTimeMinutes
            } else {
                // Crosses midnight (e.g., 23:00 to 06:00 = 1380 to 360 -> 7 hours = 420 mins)
                (1440 - startTimeMinutes) + endTimeMinutes
            }
        }

    fun formattedTimeRange(): String {
        return "${formatMinutesToTime(startTimeMinutes)} – ${formatMinutesToTime(endTimeMinutes)}"
    }

    companion object {
        fun formatMinutesToTime(minutes: Int): String {
            val normalized = (minutes % 1440 + 1440) % 1440
            val h24 = normalized / 60
            val m = normalized % 60
            val amPm = if (h24 < 12) "AM" else "PM"
            val h12 = when (val mod = h24 % 12) {
                0 -> 12
                else -> mod
            }
            return String.format("%02d:%02d %s", h12, m, amPm)
        }
    }
}
