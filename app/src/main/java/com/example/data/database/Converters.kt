package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.models.ActivityCategory
import com.example.data.models.PriorityLevel

class Converters {
    @TypeConverter
    fun fromCategory(category: ActivityCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): ActivityCategory = try {
        ActivityCategory.valueOf(value)
    } catch (e: Exception) {
        ActivityCategory.OTHER
    }

    @TypeConverter
    fun fromPriority(priority: PriorityLevel): String = priority.name

    @TypeConverter
    fun toPriority(value: String): PriorityLevel = try {
        PriorityLevel.valueOf(value)
    } catch (e: Exception) {
        PriorityLevel.MEDIUM
    }
}
