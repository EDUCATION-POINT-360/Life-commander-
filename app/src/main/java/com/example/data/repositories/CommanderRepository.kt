package com.example.data.repositories

import com.example.data.database.LifeCommanderDatabase
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min
import kotlin.math.roundToInt

class CommanderRepository(private val db: LifeCommanderDatabase) {

    private val routineDao = db.routineDao()
    private val taskDao = db.taskDao()
    private val sessionsDao = db.sessionsDao()
    private val prayerDao = db.prayerDao()
    private val habitDao = db.habitDao()
    private val dailyReportDao = db.dailyReportDao()

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getYesterdayDateString(): String {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(cal.time)
        }
    }

    // --- Routine Activities ---
    fun getRoutineActivitiesFlow(): Flow<List<RoutineActivity>> = routineDao.getAllActivitiesFlow()

    suspend fun getAllRoutineActivities(): List<RoutineActivity> = routineDao.getAllActivities()

    suspend fun insertOrUpdateActivity(activity: RoutineActivity): Long {
        return if (activity.id == 0L) {
            routineDao.insertActivity(activity)
        } else {
            routineDao.updateActivity(activity)
            activity.id
        }
    }

    suspend fun deleteActivity(activity: RoutineActivity) = routineDao.deleteActivity(activity)

    suspend fun markActivityCompleted(activity: RoutineActivity, completed: Boolean) {
        val today = getTodayDateString()
        routineDao.updateActivity(
            activity.copy(
                isCompletedToday = completed,
                isSkippedToday = false,
                lastCompletedDate = if (completed) today else ""
            )
        )
    }

    suspend fun skipActivity(activity: RoutineActivity, reason: String) {
        routineDao.updateActivity(
            activity.copy(
                isCompletedToday = false,
                isSkippedToday = true,
                skipReason = reason
            )
        )
    }

    suspend fun rescheduleActivity(activity: RoutineActivity, newStartMinutes: Int, newEndMinutes: Int) {
        routineDao.updateActivity(
            activity.copy(
                startTimeMinutes = newStartMinutes,
                endTimeMinutes = newEndMinutes,
                isSkippedToday = false
            )
        )
    }

    suspend fun seedDefaultRoutineIfEmpty() {
        val current = routineDao.getAllActivities()
        if (current.isEmpty()) {
            val defaultList = listOf(
                RoutineActivity(
                    title = "Wake up & Fajr Prayer",
                    titleUrdu = "بیداری اور نماز فجر",
                    startTimeMinutes = 360, // 06:00 AM
                    endTimeMinutes = 375,   // 06:15 AM
                    category = ActivityCategory.PRAYER,
                    priority = PriorityLevel.HIGH,
                    notes = "Wake up before sunrise, offer Fajr prayer with devotion."
                ),
                RoutineActivity(
                    title = "Morning Exercise",
                    titleUrdu = "صبح کی ورزش",
                    startTimeMinutes = 375, // 06:15 AM
                    endTimeMinutes = 405,   // 06:45 AM
                    category = ActivityCategory.EXERCISE,
                    priority = PriorityLevel.HIGH,
                    notes = "30 minutes stretching, cardio, or bodyweight training."
                ),
                RoutineActivity(
                    title = "Freshen Up & Preparation",
                    titleUrdu = "تازہ دم ہونا اور تیاری",
                    startTimeMinutes = 405, // 06:45 AM
                    endTimeMinutes = 420,   // 07:00 AM
                    category = ActivityCategory.PERSONAL,
                    priority = PriorityLevel.MEDIUM,
                    notes = "Shower, hygiene, and setting up study desk."
                ),
                RoutineActivity(
                    title = "Study Session 1 (Morning Deep Work)",
                    titleUrdu = "مطالعہ سیشن 1 (صبح کا گہرا مطالعہ)",
                    startTimeMinutes = 420, // 07:00 AM
                    endTimeMinutes = 540,   // 09:00 AM
                    category = ActivityCategory.STUDY,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 2 hours intensive study without distractions."
                ),
                RoutineActivity(
                    title = "Wife Quality Time (Morning)",
                    titleUrdu = "اہلیہ کے ساتھ معیاری وقت (صبح)",
                    startTimeMinutes = 540, // 09:00 AM
                    endTimeMinutes = 720,   // 12:00 PM
                    category = ActivityCategory.WIFE_TIME,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 3 hours quality conversation, breakfast, and shared activities."
                ),
                RoutineActivity(
                    title = "Zuhr Prayer, Lunch & Personal Needs",
                    titleUrdu = "نماز ظہر، دوپہر کا کھانا اور ضروریات",
                    startTimeMinutes = 720, // 12:00 PM
                    endTimeMinutes = 780,   // 01:00 PM
                    category = ActivityCategory.PRAYER,
                    priority = PriorityLevel.HIGH,
                    notes = "Zuhr prayer and nutritious lunch."
                ),
                RoutineActivity(
                    title = "Study Session 2 (Afternoon Focus)",
                    titleUrdu = "مطالعہ سیشن 2 (دوپہر کا مطالعہ)",
                    startTimeMinutes = 780, // 01:00 PM
                    endTimeMinutes = 900,   // 03:00 PM
                    category = ActivityCategory.STUDY,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 2 hours focused revision and problem solving."
                ),
                RoutineActivity(
                    title = "Wife Quality Time (Afternoon)",
                    titleUrdu = "اہلیہ کے ساتھ معیاری وقت (دوپہر)",
                    startTimeMinutes = 900,  // 03:00 PM
                    endTimeMinutes = 1050,  // 05:30 PM
                    category = ActivityCategory.WIFE_TIME,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 2.5 hours tea time, companionship, and household collaboration."
                ),
                RoutineActivity(
                    title = "Evening Exercise",
                    titleUrdu = "شام کی ورزش",
                    startTimeMinutes = 1050, // 05:30 PM
                    endTimeMinutes = 1080, // 06:00 PM
                    category = ActivityCategory.EXERCISE,
                    priority = PriorityLevel.HIGH,
                    notes = "30 minutes workout, outdoor walk, or gym."
                ),
                RoutineActivity(
                    title = "Dinner, Maghrib Prayer & Family",
                    titleUrdu = "رات کا کھانا، نماز مغرب اور ضروریات",
                    startTimeMinutes = 1080, // 06:00 PM
                    endTimeMinutes = 1140, // 07:00 PM
                    category = ActivityCategory.PRAYER,
                    priority = PriorityLevel.HIGH,
                    notes = "Maghrib prayer and family dinner."
                ),
                RoutineActivity(
                    title = "Wife Quality Time (Evening)",
                    titleUrdu = "اہلیہ کے ساتھ معیاری وقت (شام)",
                    startTimeMinutes = 1140, // 07:00 PM
                    endTimeMinutes = 1230, // 08:30 PM
                    category = ActivityCategory.WIFE_TIME,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 1.5 hours evening relaxation together."
                ),
                RoutineActivity(
                    title = "Isha Prayer & Personal Reflection",
                    titleUrdu = "نماز عشاء اور ذاتی وقت",
                    startTimeMinutes = 1230, // 08:30 PM
                    endTimeMinutes = 1260, // 09:00 PM
                    category = ActivityCategory.PRAYER,
                    priority = PriorityLevel.HIGH,
                    notes = "Isha prayer and spiritual connection."
                ),
                RoutineActivity(
                    title = "Night Study Session 3 (Final Lap)",
                    titleUrdu = "رات کا مطالعہ سیشن 3",
                    startTimeMinutes = 1260, // 09:00 PM
                    endTimeMinutes = 1380, // 11:00 PM
                    category = ActivityCategory.STUDY,
                    priority = PriorityLevel.HIGH,
                    notes = "Target: 2 hours night study to achieve total 6 hours."
                ),
                RoutineActivity(
                    title = "Night Sleep (7 Hours Rest)",
                    titleUrdu = "رات کی پرسکون نیند (7 گھنٹے)",
                    startTimeMinutes = 1380, // 11:00 PM
                    endTimeMinutes = 360,   // 06:00 AM (Next morning)
                    category = ActivityCategory.SLEEP,
                    priority = PriorityLevel.HIGH,
                    notes = "Complete restful sleep for rejuvenation."
                )
            )
            routineDao.insertAll(defaultList)
        }
    }

    suspend fun resetRoutineToDefault() {
        routineDao.clearAll()
        seedDefaultRoutineIfEmpty()
    }

    // --- Tasks ---
    fun getTasksForDateFlow(dateString: String = getTodayDateString()): Flow<List<DailyTask>> =
        taskDao.getTasksForDateFlow(dateString)

    suspend fun insertTask(task: DailyTask): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: DailyTask) = taskDao.updateTask(task)
    suspend fun deleteTask(task: DailyTask) = taskDao.deleteTask(task)

    // --- Sessions ---
    fun getStudySessionsFlow(dateString: String = getTodayDateString()): Flow<List<StudySession>> =
        sessionsDao.getStudySessionsForDateFlow(dateString)

    fun getTotalStudyMinutesFlow(dateString: String = getTodayDateString()): Flow<Int?> =
        sessionsDao.getTotalStudyMinutesFlow(dateString)

    suspend fun addStudySession(durationMinutes: Int, subject: String, sessionType: String, notes: String = ""): Long {
        return sessionsDao.insertStudySession(
            StudySession(
                dateString = getTodayDateString(),
                durationMinutes = durationMinutes,
                subject = subject,
                sessionType = sessionType,
                notes = notes
            )
        )
    }

    fun getWifeTimeSessionsFlow(dateString: String = getTodayDateString()): Flow<List<WifeTimeSession>> =
        sessionsDao.getWifeTimeSessionsForDateFlow(dateString)

    fun getTotalWifeTimeMinutesFlow(dateString: String = getTodayDateString()): Flow<Int?> =
        sessionsDao.getTotalWifeTimeMinutesFlow(dateString)

    suspend fun addWifeTimeSession(durationMinutes: Int, notes: String = ""): Long {
        return sessionsDao.insertWifeTimeSession(
            WifeTimeSession(
                dateString = getTodayDateString(),
                durationMinutes = durationMinutes,
                notes = notes
            )
        )
    }

    fun getExerciseSessionsFlow(dateString: String = getTodayDateString()): Flow<List<ExerciseSession>> =
        sessionsDao.getExerciseSessionsForDateFlow(dateString)

    fun getTotalExerciseMinutesFlow(dateString: String = getTodayDateString()): Flow<Int?> =
        sessionsDao.getTotalExerciseMinutesFlow(dateString)

    suspend fun addExerciseSession(durationMinutes: Int, type: String, notes: String = ""): Long {
        return sessionsDao.insertExerciseSession(
            ExerciseSession(
                dateString = getTodayDateString(),
                durationMinutes = durationMinutes,
                exerciseType = type,
                notes = notes
            )
        )
    }

    fun getSleepRecordFlow(dateString: String = getTodayDateString()): Flow<SleepRecord?> =
        sessionsDao.getSleepRecordForDateFlow(dateString)

    suspend fun saveSleepRecord(durationMinutes: Int, bedtimeMinutes: Int, wakeMinutes: Int, notes: String = "") {
        sessionsDao.insertOrUpdateSleep(
            SleepRecord(
                dateString = getTodayDateString(),
                bedtimeMinutes = bedtimeMinutes,
                wakeMinutes = wakeMinutes,
                durationMinutes = durationMinutes,
                notes = notes
            )
        )
    }

    // --- Prayers ---
    fun getPrayerRecordFlow(dateString: String = getTodayDateString()): Flow<PrayerRecord?> =
        prayerDao.getPrayerRecordForDateFlow(dateString)

    suspend fun getOrCreatePrayerRecord(dateString: String = getTodayDateString()): PrayerRecord {
        val existing = prayerDao.getPrayerRecordForDate(dateString)
        if (existing != null) return existing
        val newRecord = PrayerRecord(dateString = dateString)
        prayerDao.insertOrUpdatePrayer(newRecord)
        return newRecord
    }

    suspend fun updatePrayerRecord(record: PrayerRecord) {
        prayerDao.insertOrUpdatePrayer(record)
    }

    suspend fun togglePrayer(prayerName: String, completed: Boolean) {
        val record = getOrCreatePrayerRecord(getTodayDateString())
        val updated = when (prayerName.lowercase(Locale.ROOT)) {
            "fajr" -> record.copy(fajrCompleted = completed)
            "zuhr" -> record.copy(zuhrCompleted = completed)
            "asr" -> record.copy(asrCompleted = completed)
            "maghrib" -> record.copy(maghribCompleted = completed)
            "isha" -> record.copy(ishaCompleted = completed)
            else -> record
        }
        prayerDao.insertOrUpdatePrayer(updated)
    }

    // --- Habits ---
    fun getAllHabitsFlow(): Flow<List<Habit>> = habitDao.getAllHabitsFlow()

    suspend fun seedDefaultHabitsIfEmpty() {
        val list = habitDao.getAllHabits()
        if (list.isEmpty()) {
            val defaults = listOf(
                Habit(title = "Study 6 Hours Daily", titleUrdu = "روزانہ 6 گھنٹے مطالعہ", category = ActivityCategory.STUDY),
                Habit(title = "Spend 7 Hours with Wife", titleUrdu = "اہلیہ کے ساتھ 7 گھنٹے وقت", category = ActivityCategory.WIFE_TIME),
                Habit(title = "Sleep 7 Hours", titleUrdu = "روزانہ 7 گھنٹے پرسکون نیند", category = ActivityCategory.SLEEP),
                Habit(title = "Daily Exercise (60m)", titleUrdu = "روزانہ ورزش (صبح و شام)", category = ActivityCategory.EXERCISE),
                Habit(title = "5 Daily Prayers", titleUrdu = "پانچ وقت کی نمازیں", category = ActivityCategory.PRAYER),
                Habit(title = "Zero Social Media in Study", titleUrdu = "مطالعہ کے دوران فون سے پرہیز", category = ActivityCategory.PERSONAL)
            )
            habitDao.insertAll(defaults)
        }
    }

    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)

    suspend fun toggleHabitCompletedToday(habit: Habit) {
        val today = getTodayDateString()
        if (habit.lastCompletedDate == today) {
            // Already completed today, unmark
            habitDao.updateHabit(
                habit.copy(
                    lastCompletedDate = "",
                    currentStreak = maxOf(0, habit.currentStreak - 1)
                )
            )
        } else {
            val newStreak = habit.currentStreak + 1
            habitDao.updateHabit(
                habit.copy(
                    lastCompletedDate = today,
                    currentStreak = newStreak,
                    bestStreak = maxOf(habit.bestStreak, newStreak)
                )
            )
        }
    }

    // --- Daily Report & Discipline Score Calculation ---
    fun getDailyReportFlow(dateString: String = getTodayDateString()): Flow<DailyReport?> =
        dailyReportDao.getDailyReportFlow(dateString)

    suspend fun calculateDisciplineScore(dateString: String = getTodayDateString()): Int {
        // Study: 25 points max (target: 360 min)
        val studyMin = sessionsDao.getTotalStudyMinutes(dateString) ?: 0
        val studyScore = min(25.0, (studyMin.toDouble() / 360.0) * 25.0)

        // Sleep: 20 points max (target: 420 min)
        val sleepRecord = sessionsDao.getSleepRecordForDate(dateString)
        val sleepMin = sleepRecord?.durationMinutes ?: 420 // default considered 7h if completed
        val sleepScore = min(20.0, (sleepMin.toDouble() / 420.0) * 20.0)

        // Prayers: 20 points max (5 prayers = 4 points each)
        val prayerRecord = prayerDao.getPrayerRecordForDate(dateString)
        val prayerCount = prayerRecord?.completedCount ?: 0
        val prayerScore = (prayerCount / 5.0) * 20.0

        // Exercise: 10 points max (target: 60 min)
        val exerciseMin = sessionsDao.getTotalExerciseMinutes(dateString) ?: 0
        val exerciseScore = min(10.0, (exerciseMin.toDouble() / 60.0) * 10.0)

        // Wife Time: 15 points max (target: 420 min)
        val wifeMin = sessionsDao.getTotalWifeTimeMinutes(dateString) ?: 0
        val wifeScore = min(15.0, (wifeMin.toDouble() / 420.0) * 15.0)

        // Tasks: 10 points max
        val tasks = taskDao.getTasksForDate(dateString)
        val tasksScore = if (tasks.isEmpty()) {
            10.0
        } else {
            val completed = tasks.count { it.isCompleted }
            (completed.toDouble() / tasks.size.toDouble()) * 10.0
        }

        val total = (studyScore + sleepScore + prayerScore + exerciseScore + wifeScore + tasksScore).roundToInt()
        return total.coerceIn(0, 100)
    }

    suspend fun updateDailyReport(
        dateString: String = getTodayDateString(),
        screenTimeMinutes: Int = 0,
        isFinished: Boolean = false,
        feedback: String = ""
    ) {
        val score = calculateDisciplineScore(dateString)
        val studyMin = sessionsDao.getTotalStudyMinutes(dateString) ?: 0
        val wifeMin = sessionsDao.getTotalWifeTimeMinutes(dateString) ?: 0
        val sleepRecord = sessionsDao.getSleepRecordForDate(dateString)
        val sleepMin = sleepRecord?.durationMinutes ?: 420
        val exerciseMin = sessionsDao.getTotalExerciseMinutes(dateString) ?: 0
        val prayerRecord = prayerDao.getPrayerRecordForDate(dateString)
        val prayersCompleted = prayerRecord?.completedCount ?: 0
        val tasks = taskDao.getTasksForDate(dateString)
        val tasksCompleted = tasks.count { it.isCompleted }

        val report = DailyReport(
            dateString = dateString,
            disciplineScore = score,
            studyMinutes = studyMin,
            wifeTimeMinutes = wifeMin,
            sleepMinutes = sleepMin,
            exerciseMinutes = exerciseMin,
            prayersCompleted = prayersCompleted,
            tasksCompleted = tasksCompleted,
            tasksTotal = tasks.size,
            screenTimeMinutes = screenTimeMinutes,
            coachFeedback = feedback,
            isDayFinished = isFinished
        )
        dailyReportDao.insertOrUpdateReport(report)
    }

    fun getRecentReportsFlow(): Flow<List<DailyReport>> = dailyReportDao.getRecentReportsFlow()
}
