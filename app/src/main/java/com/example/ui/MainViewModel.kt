package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.LifeCommanderApp
import com.example.ai.AiMessage
import com.example.ai.CommanderAiCoach
import com.example.ai.DailyCoachMetrics
import com.example.data.models.*
import com.example.data.repositories.CommanderRepository
import com.example.localization.AppLanguage
import com.example.localization.StringsProvider
import com.example.utils.AppUsageInfo
import com.example.utils.UsageStatsHelper
import com.example.voice.CommanderVoiceManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class Screen {
    DASHBOARD,
    TIMETABLE,
    TASKS,
    FOCUS,
    PROFILE,
    PRAYERS,
    AI_COACH,
    REPORTS,
    HABITS
}

enum class FocusSessionType {
    STUDY_POMODORO_25,
    STUDY_DEEP_50,
    STUDY_CUSTOM,
    WIFE_TIME,
    EXERCISE_MORNING,
    EXERCISE_EVENING
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LifeCommanderApp
    private val repository = app.repository

    // Current Screen & Backstack
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            return true
        }
        return false
    }

    // Language & Preferences
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.URDU else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun getString(key: String): String = StringsProvider.get(key, _language.value)

    // Data Flows
    val routineActivities: StateFlow<List<RoutineActivity>> = repository.getRoutineActivitiesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTasks: StateFlow<List<DailyTask>> = repository.getTasksForDateFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStudyMinutes: StateFlow<Int> = repository.getTotalStudyMinutesFlow()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWifeMinutes: StateFlow<Int> = repository.getTotalWifeTimeMinutesFlow()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalExerciseMinutes: StateFlow<Int> = repository.getTotalExerciseMinutesFlow()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val sleepRecord: StateFlow<SleepRecord?> = repository.getSleepRecordFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val prayerRecord: StateFlow<PrayerRecord?> = repository.getPrayerRecordFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val habits: StateFlow<List<Habit>> = repository.getAllHabitsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyReport: StateFlow<DailyReport?> = repository.getDailyReportFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentReports: StateFlow<List<DailyReport>> = repository.getRecentReportsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Screen Time & App Usage
    private val _screenTimeMinutes = MutableStateFlow(0L)
    val screenTimeMinutes: StateFlow<Long> = _screenTimeMinutes.asStateFlow()

    private val _topApps = MutableStateFlow<List<AppUsageInfo>>(emptyList())
    val topApps: StateFlow<List<AppUsageInfo>> = _topApps.asStateFlow()

    private val _hasUsagePermission = MutableStateFlow(false)
    val hasUsagePermission: StateFlow<Boolean> = _hasUsagePermission.asStateFlow()

    fun refreshUsageStats() {
        val hasPerm = UsageStatsHelper.hasUsageStatsPermission(getApplication())
        _hasUsagePermission.value = hasPerm
        if (hasPerm) {
            val (total, apps) = UsageStatsHelper.getTodayUsageStats(getApplication())
            _screenTimeMinutes.value = total
            _topApps.value = apps
        }
    }

    // Live Discipline Score
    val disciplineScore: StateFlow<Int> = combine(
        totalStudyMinutes,
        totalWifeMinutes,
        totalExerciseMinutes,
        prayerRecord,
        dailyTasks
    ) { studyMin, wifeMin, exerciseMin, prayers, tasks ->
        val studyScore = kotlin.math.min(25.0, (studyMin.toDouble() / 360.0) * 25.0)
        val sleepScore = 20.0 // 7 hours scheduled
        val prayerCount = prayers?.completedCount ?: 0
        val prayerScore = (prayerCount / 5.0) * 20.0
        val exerciseScore = kotlin.math.min(10.0, (exerciseMin.toDouble() / 60.0) * 10.0)
        val wifeScore = kotlin.math.min(15.0, (wifeMin.toDouble() / 420.0) * 15.0)
        val tasksScore = if (tasks.isEmpty()) 10.0 else {
            (tasks.count { it.isCompleted }.toDouble() / tasks.size.toDouble()) * 10.0
        }
        (studyScore + sleepScore + prayerScore + exerciseScore + wifeScore + tasksScore).toInt().coerceIn(0, 100)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Day Started / Finished
    private val _isDayStarted = MutableStateFlow(false)
    val isDayStarted: StateFlow<Boolean> = _isDayStarted.asStateFlow()

    fun startMyDay() {
        _isDayStarted.value = true
        voiceManager?.speak(
            if (_language.value == AppLanguage.URDU) "کمانڈر، آپ کے دن کا آغاز ہو چکا ہے۔ وقت کی قدر کریں۔"
            else "Commander, your day is active. Execute your timetable with precision.",
            _language.value
        )
    }

    fun finishMyDay() {
        viewModelScope.launch {
            repository.updateDailyReport(
                screenTimeMinutes = _screenTimeMinutes.value.toInt(),
                isFinished = true,
                feedback = CommanderAiCoach.generateAnalysis(getCurrentMetrics(), _language.value)
            )
            voiceManager?.speak(
                if (_language.value == AppLanguage.URDU) "دن مکمل ہوا! شاندار ڈسپلن۔"
                else "Day concluded. Outstanding discipline, Commander.",
                _language.value
            )
        }
    }

    // Focus & Timers State
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _activeSessionType = MutableStateFlow(FocusSessionType.STUDY_POMODORO_25)
    val activeSessionType: StateFlow<FocusSessionType> = _activeSessionType.asStateFlow()

    private val _currentSubject = MutableStateFlow("General Study")
    val currentSubject: StateFlow<String> = _currentSubject.asStateFlow()

    fun setSubject(subject: String) {
        _currentSubject.value = subject
    }

    private var timerJob: Job? = null

    fun startFocusTimer(type: FocusSessionType) {
        _activeSessionType.value = type
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _timerSeconds.value += 1
            }
        }
    }

    fun pauseFocusTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun stopAndSaveFocusTimer(notes: String = "") {
        _isTimerRunning.value = false
        timerJob?.cancel()
        val durationMins = maxOf(1, _timerSeconds.value / 60)
        viewModelScope.launch {
            when (_activeSessionType.value) {
                FocusSessionType.STUDY_POMODORO_25,
                FocusSessionType.STUDY_DEEP_50,
                FocusSessionType.STUDY_CUSTOM -> {
                    repository.addStudySession(durationMins, _currentSubject.value, _activeSessionType.value.name, notes)
                }
                FocusSessionType.WIFE_TIME -> {
                    repository.addWifeTimeSession(durationMins, notes)
                }
                FocusSessionType.EXERCISE_MORNING -> {
                    repository.addExerciseSession(durationMins, "MORNING", notes)
                }
                FocusSessionType.EXERCISE_EVENING -> {
                    repository.addExerciseSession(durationMins, "EVENING", notes)
                }
            }
            _timerSeconds.value = 0
            refreshUsageStats()
        }
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        _timerSeconds.value = 0
    }

    // Timetable Actions
    fun markActivityComplete(activity: RoutineActivity, completed: Boolean) {
        viewModelScope.launch {
            repository.markActivityCompleted(activity, completed)
        }
    }

    fun skipActivity(activity: RoutineActivity, reason: String) {
        viewModelScope.launch {
            repository.skipActivity(activity, reason)
        }
    }

    fun rescheduleActivity(activity: RoutineActivity, newStart: Int, newEnd: Int) {
        viewModelScope.launch {
            repository.rescheduleActivity(activity, newStart, newEnd)
        }
    }

    fun saveActivity(activity: RoutineActivity) {
        viewModelScope.launch {
            repository.insertOrUpdateActivity(activity)
        }
    }

    fun deleteActivity(activity: RoutineActivity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }

    fun resetToDefaultRoutine() {
        viewModelScope.launch {
            repository.resetRoutineToDefault()
        }
    }

    // Task Actions
    fun addTask(title: String, description: String, priority: PriorityLevel, deadlineMin: Int) {
        viewModelScope.launch {
            repository.insertTask(
                DailyTask(
                    title = title,
                    description = description,
                    priority = priority,
                    deadlineMinutes = deadlineMin,
                    dateString = CommanderRepository.getTodayDateString()
                )
            )
        }
    }

    fun toggleTaskComplete(task: DailyTask) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: DailyTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Prayer Actions
    fun togglePrayer(prayerName: String, completed: Boolean) {
        viewModelScope.launch {
            repository.togglePrayer(prayerName, completed)
        }
    }

    // Habit Actions
    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            repository.toggleHabitCompletedToday(habit)
        }
    }

    fun addHabit(title: String, titleUrdu: String, category: ActivityCategory) {
        viewModelScope.launch {
            repository.insertHabit(
                Habit(
                    title = title,
                    titleUrdu = titleUrdu,
                    category = category
                )
            )
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // AI Coach Chat
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                sender = "AI",
                content = "Greetings, Commander. I am your personal discipline and routine coach. Ask me to evaluate your day, optimize your 6-hour study timetable, or analyze today's habits."
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    fun askAiCoach(prompt: String) {
        val userMsg = AiMessage(sender = "USER", content = prompt)
        _aiMessages.value = _aiMessages.value + userMsg

        val metrics = getCurrentMetrics()
        val answer = CommanderAiCoach.answerQuery(prompt, metrics, _language.value)

        viewModelScope.launch {
            delay(400) // smooth response feel
            val aiMsg = AiMessage(sender = "AI", content = answer)
            _aiMessages.value = _aiMessages.value + aiMsg
            voiceManager?.speak(answer, _language.value)
        }
    }

    fun requestDailyEvaluation() {
        val metrics = getCurrentMetrics()
        val analysis = CommanderAiCoach.generateAnalysis(metrics, _language.value)
        _aiMessages.value = _aiMessages.value + AiMessage(sender = "AI", content = analysis)
        voiceManager?.speak(analysis, _language.value)
    }

    private fun getCurrentMetrics(): DailyCoachMetrics {
        val tasks = dailyTasks.value
        val prayers = prayerRecord.value
        return DailyCoachMetrics(
            studyMinutes = totalStudyMinutes.value,
            wifeTimeMinutes = totalWifeMinutes.value,
            sleepMinutes = sleepRecord.value?.durationMinutes ?: 420,
            exerciseMinutes = totalExerciseMinutes.value,
            prayersCompleted = prayers?.completedCount ?: 0,
            tasksCompleted = tasks.count { it.isCompleted },
            tasksTotal = tasks.size,
            screenTimeMinutes = _screenTimeMinutes.value.toInt(),
            disciplineScore = disciplineScore.value
        )
    }

    // Voice Manager Integration
    var voiceManager: CommanderVoiceManager? = null

    fun initializeVoiceManager(manager: CommanderVoiceManager) {
        this.voiceManager = manager
    }

    fun handleVoiceCommand(spokenText: String) {
        val text = spokenText.lowercase()
        when {
            text.contains("timetable") || text.contains("شیڈول") || text.contains("ٹائم ٹیبل") -> {
                navigateTo(Screen.TIMETABLE)
                voiceManager?.speak(
                    if (_language.value == AppLanguage.URDU) "آپ کا ٹائم ٹیبل پیش خدمت ہے۔"
                    else "Displaying your 24-hour timetable, Commander.",
                    _language.value
                )
            }
            text.contains("study") || text.contains("مطالعہ") -> {
                navigateTo(Screen.FOCUS)
                startFocusTimer(FocusSessionType.STUDY_POMODORO_25)
                voiceManager?.speak(
                    if (_language.value == AppLanguage.URDU) "مطالعہ ٹائمر شروع ہو گیا ہے۔"
                    else "Study session initiated. Focus mode active.",
                    _language.value
                )
            }
            text.contains("prayer") || text.contains("نماز") -> {
                navigateTo(Screen.PRAYERS)
                val count = prayerRecord.value?.completedCount ?: 0
                voiceManager?.speak(
                    if (_language.value == AppLanguage.URDU) "آپ نے آج $count نمازیں ادا کی ہیں۔"
                    else "You have completed $count out of 5 daily prayers.",
                    _language.value
                )
            }
            text.contains("progress") || text.contains("score") || text.contains("ڈسپلن") -> {
                val score = disciplineScore.value
                voiceManager?.speak(
                    if (_language.value == AppLanguage.URDU) "آج آپ کا ڈسپلن اسکور $score ہے۔"
                    else "Your current discipline score is $score out of 100.",
                    _language.value
                )
            }
            else -> {
                askAiCoach(spokenText)
            }
        }
    }

    init {
        refreshUsageStats()
    }
}
