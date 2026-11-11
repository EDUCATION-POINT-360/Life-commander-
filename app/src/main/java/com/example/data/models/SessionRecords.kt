package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val timestampMs: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val subject: String = "General Study",
    val sessionType: String = "FREE_STUDY", // POMODORO, DEEP_WORK, FREE_STUDY
    val notes: String = ""
)

@Entity(tableName = "wife_time_sessions")
data class WifeTimeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val timestampMs: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val notes: String = ""
)

@Entity(tableName = "exercise_sessions")
data class ExerciseSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val timestampMs: Long = System.currentTimeMillis(),
    val durationMinutes: Int,
    val exerciseType: String = "MORNING", // MORNING, EVENING, CUSTOM
    val notes: String = ""
)

@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val bedtimeMinutes: Int = 1380, // 11:00 PM
    val wakeMinutes: Int = 360,     // 06:00 AM
    val durationMinutes: Int = 420, // 7 Hours
    val consistencyScore: Int = 100, // 0 - 100
    val notes: String = ""
)
