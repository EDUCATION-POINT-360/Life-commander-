package com.example.data.database.daos

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_activities ORDER BY startTimeMinutes ASC")
    fun getAllActivitiesFlow(): Flow<List<RoutineActivity>>

    @Query("SELECT * FROM routine_activities ORDER BY startTimeMinutes ASC")
    suspend fun getAllActivities(): List<RoutineActivity>

    @Query("SELECT * FROM routine_activities WHERE id = :id LIMIT 1")
    suspend fun getActivityById(id: Long): RoutineActivity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: RoutineActivity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<RoutineActivity>)

    @Update
    suspend fun updateActivity(activity: RoutineActivity)

    @Delete
    suspend fun deleteActivity(activity: RoutineActivity)

    @Query("DELETE FROM routine_activities")
    suspend fun clearAll()
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM daily_tasks WHERE dateString = :dateString ORDER BY priority DESC, id ASC")
    fun getTasksForDateFlow(dateString: String): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks WHERE dateString = :dateString")
    suspend fun getTasksForDate(dateString: String): List<DailyTask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DailyTask): Long

    @Update
    suspend fun updateTask(task: DailyTask)

    @Delete
    suspend fun deleteTask(task: DailyTask)
}

@Dao
interface SessionsDao {
    // Study
    @Query("SELECT * FROM study_sessions WHERE dateString = :dateString ORDER BY timestampMs DESC")
    fun getStudySessionsForDateFlow(dateString: String): Flow<List<StudySession>>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE dateString = :dateString")
    fun getTotalStudyMinutesFlow(dateString: String): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE dateString = :dateString")
    suspend fun getTotalStudyMinutes(dateString: String): Int?

    @Query("SELECT * FROM study_sessions ORDER BY timestampMs DESC LIMIT 50")
    fun getAllRecentStudySessionsFlow(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySession): Long

    // Wife Time
    @Query("SELECT * FROM wife_time_sessions WHERE dateString = :dateString ORDER BY timestampMs DESC")
    fun getWifeTimeSessionsForDateFlow(dateString: String): Flow<List<WifeTimeSession>>

    @Query("SELECT SUM(durationMinutes) FROM wife_time_sessions WHERE dateString = :dateString")
    fun getTotalWifeTimeMinutesFlow(dateString: String): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM wife_time_sessions WHERE dateString = :dateString")
    suspend fun getTotalWifeTimeMinutes(dateString: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWifeTimeSession(session: WifeTimeSession): Long

    // Exercise
    @Query("SELECT * FROM exercise_sessions WHERE dateString = :dateString ORDER BY timestampMs DESC")
    fun getExerciseSessionsForDateFlow(dateString: String): Flow<List<ExerciseSession>>

    @Query("SELECT SUM(durationMinutes) FROM exercise_sessions WHERE dateString = :dateString")
    fun getTotalExerciseMinutesFlow(dateString: String): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM exercise_sessions WHERE dateString = :dateString")
    suspend fun getTotalExerciseMinutes(dateString: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseSession(session: ExerciseSession): Long

    // Sleep
    @Query("SELECT * FROM sleep_records WHERE dateString = :dateString LIMIT 1")
    fun getSleepRecordForDateFlow(dateString: String): Flow<SleepRecord?>

    @Query("SELECT * FROM sleep_records WHERE dateString = :dateString LIMIT 1")
    suspend fun getSleepRecordForDate(dateString: String): SleepRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSleep(sleep: SleepRecord)
}

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayer_records WHERE dateString = :dateString LIMIT 1")
    fun getPrayerRecordForDateFlow(dateString: String): Flow<PrayerRecord?>

    @Query("SELECT * FROM prayer_records WHERE dateString = :dateString LIMIT 1")
    suspend fun getPrayerRecordForDate(dateString: String): PrayerRecord?

    @Query("SELECT * FROM prayer_records ORDER BY dateString DESC LIMIT 30")
    fun getRecentPrayerRecordsFlow(): Flow<List<PrayerRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayer(prayer: PrayerRecord)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY currentStreak DESC, id ASC")
    fun getAllHabitsFlow(): Flow<List<Habit>>

    @Query("SELECT * FROM habits")
    suspend fun getAllHabits(): List<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(habits: List<Habit>)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)
}

@Dao
interface DailyReportDao {
    @Query("SELECT * FROM daily_reports WHERE dateString = :dateString LIMIT 1")
    fun getDailyReportFlow(dateString: String): Flow<DailyReport?>

    @Query("SELECT * FROM daily_reports WHERE dateString = :dateString LIMIT 1")
    suspend fun getDailyReport(dateString: String): DailyReport?

    @Query("SELECT * FROM daily_reports ORDER BY dateString DESC LIMIT 30")
    fun getRecentReportsFlow(): Flow<List<DailyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateReport(report: DailyReport)
}
