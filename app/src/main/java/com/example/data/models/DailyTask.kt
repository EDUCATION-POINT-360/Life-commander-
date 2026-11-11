package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val deadlineMinutes: Int = -1, // e.g. 1020 for 17:00 or -1 if no specific deadline
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val category: ActivityCategory = ActivityCategory.OTHER,
    val isCompleted: Boolean = false,
    val isSkipped: Boolean = false,
    val isMissed: Boolean = false,
    val dateString: String, // "YYYY-MM-DD"
    val reminderEnabled: Boolean = false,
    val notes: String = ""
)
