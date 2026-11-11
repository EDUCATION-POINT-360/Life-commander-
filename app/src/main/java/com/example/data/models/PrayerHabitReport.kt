package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_records")
data class PrayerRecord(
    @PrimaryKey
    val dateString: String, // "YYYY-MM-DD"
    val fajrCompleted: Boolean = false,
    val zuhrCompleted: Boolean = false,
    val asrCompleted: Boolean = false,
    val maghribCompleted: Boolean = false,
    val ishaCompleted: Boolean = false,
    val fajrTime: String = "05:45 AM",
    val zuhrTime: String = "12:30 PM",
    val asrTime: String = "04:15 PM",
    val maghribTime: String = "06:10 PM",
    val ishaTime: String = "08:15 PM"
) {
    val completedCount: Int
        get() = (if (fajrCompleted) 1 else 0) +
                (if (zuhrCompleted) 1 else 0) +
                (if (asrCompleted) 1 else 0) +
                (if (maghribCompleted) 1 else 0) +
                (if (ishaCompleted) 1 else 0)
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val titleUrdu: String = "",
    val targetDaysPerWeek: Int = 7,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String = "",
    val category: ActivityCategory = ActivityCategory.OTHER,
    val iconName: String = "check"
)

@Entity(tableName = "daily_reports")
data class DailyReport(
    @PrimaryKey
    val dateString: String, // "YYYY-MM-DD"
    val disciplineScore: Int = 0,
    val studyMinutes: Int = 0,
    val wifeTimeMinutes: Int = 0,
    val sleepMinutes: Int = 0,
    val exerciseMinutes: Int = 0,
    val prayersCompleted: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val screenTimeMinutes: Int = 0,
    val distractionCount: Int = 0,
    val coachFeedback: String = "",
    val isDayFinished: Boolean = false
)
