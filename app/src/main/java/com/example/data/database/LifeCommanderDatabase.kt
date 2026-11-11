package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.database.daos.*
import com.example.data.models.*

@Database(
    entities = [
        RoutineActivity::class,
        DailyTask::class,
        StudySession::class,
        WifeTimeSession::class,
        ExerciseSession::class,
        SleepRecord::class,
        PrayerRecord::class,
        Habit::class,
        DailyReport::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeCommanderDatabase : RoomDatabase() {

    abstract fun routineDao(): RoutineDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionsDao(): SessionsDao
    abstract fun prayerDao(): PrayerDao
    abstract fun habitDao(): HabitDao
    abstract fun dailyReportDao(): DailyReportDao

    companion object {
        @Volatile
        private var INSTANCE: LifeCommanderDatabase? = null

        fun getDatabase(context: Context): LifeCommanderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeCommanderDatabase::class.java,
                    "life_commander.db"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
