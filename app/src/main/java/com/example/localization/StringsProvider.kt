package com.example.localization

enum class AppLanguage {
    ENGLISH,
    URDU
}

object StringsProvider {

    fun get(key: String, language: AppLanguage): String {
        val dict = if (language == AppLanguage.URDU) urduStrings else englishStrings
        return dict[key] ?: englishStrings[key] ?: key
    }

    private val englishStrings = mapOf(
        // General / Nav
        "app_name" to "LIFE COMMANDER",
        "app_subtitle" to "Personal Life Management System",
        "dashboard" to "Dashboard",
        "timetable" to "Timetable",
        "tasks" to "Tasks",
        "focus" to "Focus",
        "profile" to "Profile",
        "prayers" to "Prayers",
        "ai_coach" to "AI Coach",
        "habits" to "Habits",
        "reports" to "Reports",
        "voice_assistant" to "Voice Command",
        "settings" to "Settings",

        // Dashboard
        "welcome_back" to "Welcome back, Commander",
        "discipline_score" to "Discipline Score",
        "daily_target_progress" to "Daily Target Progress",
        "start_my_day" to "START MY DAY",
        "finish_my_day" to "FINISH MY DAY",
        "day_active" to "Day in Progress",
        "day_completed" to "Day Finished",
        "study_target" to "Study Target (6h)",
        "wife_time_target" to "Wife Time (7h)",
        "sleep_target" to "Sleep Target (7h)",
        "exercise_target" to "Exercise (1h)",
        "prayers_target" to "Prayers (5 Daily)",
        "screen_time" to "Screen Time",
        "next_activity" to "Next Scheduled Activity",
        "next_prayer" to "Next Prayer",
        "tasks_summary" to "Tasks Overview",
        "completed" to "Completed",
        "remaining" to "Remaining",
        "missed" to "Missed",
        "hours" to "Hours",
        "minutes" to "Minutes",

        // Timetable
        "daily_timetable" to "24-Hour Timetable",
        "add_activity" to "Add Activity",
        "edit_activity" to "Edit Activity",
        "activity_title" to "Activity Title",
        "category" to "Category",
        "start_time" to "Start Time",
        "end_time" to "End Time",
        "duration" to "Duration",
        "priority" to "Priority",
        "reminder" to "Reminder",
        "mark_complete" to "Mark Complete",
        "skip_activity" to "Skip Activity",
        "reschedule" to "Reschedule",
        "notes" to "Notes",
        "save" to "Save",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "reason" to "Reason",

        // Categories
        "cat_study" to "Study",
        "cat_wife_time" to "Wife Time",
        "cat_sleep" to "Sleep",
        "cat_exercise" to "Exercise",
        "cat_prayer" to "Prayer",
        "cat_meals" to "Meals",
        "cat_personal" to "Personal",
        "cat_work" to "Work",
        "cat_other" to "Other",

        // Tasks
        "all_tasks" to "All Tasks",
        "new_task" to "New Task",
        "task_title" to "Task Title",
        "task_desc" to "Description",
        "deadline" to "Deadline",
        "priority_low" to "Low",
        "priority_medium" to "Medium",
        "priority_high" to "High",
        "pending" to "Pending",

        // Focus & Timers
        "focus_hub" to "Focus & Timers",
        "study_timer" to "Study Timer",
        "pomodoro" to "Pomodoro (25/5m)",
        "pomodoro_deep" to "Deep Work (50/10m)",
        "custom_timer" to "Custom Timer",
        "start" to "Start",
        "pause" to "Pause",
        "resume" to "Resume",
        "stop" to "Stop",
        "reset" to "Reset",
        "wife_time_tracker" to "Wife Time Tracker",
        "exercise_tracker" to "Exercise Tracker",
        "sleep_tracker" to "Sleep Tracker",
        "current_session" to "Current Session",
        "distraction_mode" to "Discipline Focus Mode",
        "distraction_warnings" to "App Distraction Warnings",
        "usage_access_needed" to "Grant Usage Access to monitor distractions",
        "grant_permission" to "Grant Permission",

        // Prayers
        "prayer_tracker" to "Daily Prayers",
        "fajr" to "Fajr",
        "zuhr" to "Zuhr",
        "asr" to "Asr",
        "maghrib" to "Maghrib",
        "isha" to "Isha",
        "prayer_status" to "Prayer Status",

        // AI Coach
        "commander_ai" to "COMMANDER AI",
        "ai_coach_subtitle" to "Personal Discipline & Productivity Coach",
        "ask_coach" to "Ask Commander AI...",
        "send" to "Send",
        "evaluate_day" to "Evaluate Today's Progress",
        "study_advice" to "How to meet 6h study target?",
        "discipline_tips" to "Tips to reduce phone distraction",

        // Voice
        "commander_voice" to "COMMANDER VOICE",
        "listening" to "Listening for command...",
        "tap_to_speak" to "Tap to Speak",
        "voice_help" to "Try: 'Show timetable', 'Start study timer', 'Show today\\'s progress'",

        // Reports & Habits
        "analytics_reports" to "Analytics & Reports",
        "daily" to "Daily",
        "weekly" to "Weekly",
        "monthly" to "Monthly",
        "habit_tracker" to "Habit Streaks",
        "add_habit" to "Add Habit",

        // Settings
        "settings_title" to "Settings & Preferences",
        "language" to "Language",
        "notifications" to "Smart Notifications",
        "sound_alerts" to "Sound & Vibration Alerts",
        "voice_announcements" to "Voice Announcements",
        "data_management" to "Data & Backup",
        "reset_routine" to "Reset to Default Schedule",
        "export_data" to "Export Data (JSON)",
        "privacy_policy" to "Privacy & Security Info",
        "privacy_desc" to "Life Commander operates 100% offline. No private chats, calls, or personal data leave your phone."
    )

    private val urduStrings = mapOf(
        // General / Nav
        "app_name" to "لائف کمانڈر",
        "app_subtitle" to "ذاتی زندگی کا نظام الاوقات",
        "dashboard" to "ڈیش بورڈ",
        "timetable" to "ٹائم ٹیبل",
        "tasks" to "کام (ٹاسکس)",
        "focus" to "فوکس",
        "profile" to "پروفائل",
        "prayers" to "نمازیں",
        "ai_coach" to "اے آئی کوچ",
        "habits" to "عادات",
        "reports" to "رپورٹس",
        "voice_assistant" to "صوتی کمانڈ",
        "settings" to "سیٹنگز",

        // Dashboard
        "welcome_back" to "خوش آمدید، کمانڈر",
        "discipline_score" to "ڈسپلن اسکور",
        "daily_target_progress" to "روزانہ کے اہداف کی پیشرفت",
        "start_my_day" to "دن کا آغاز کریں",
        "finish_my_day" to "دن مکمل کریں",
        "day_active" to "دن جاری ہے",
        "day_completed" to "دن مکمل ہو چکا",
        "study_target" to "پڑھائی کا ہدف (6 گھنٹے)",
        "wife_time_target" to "اہلیہ کے ساتھ وقت (7 گھنٹے)",
        "sleep_target" to "نیند کا ہدف (7 گھنٹے)",
        "exercise_target" to "ورزش (1 گھنٹہ)",
        "prayers_target" to "نمازیں (5 وقت)",
        "screen_time" to "اسکرین کا وقت",
        "next_activity" to "اگلی سرگرمی",
        "next_prayer" to "اگلی نماز",
        "tasks_summary" to "کاموں کا جائزہ",
        "completed" to "مکمل",
        "remaining" to "باقی",
        "missed" to "چھوٹ گئے",
        "hours" to "گھنٹے",
        "minutes" to "منٹ",

        // Timetable
        "daily_timetable" to "24 گھنٹے کا ٹائم ٹیبل",
        "add_activity" to "سرگرمی شامل کریں",
        "edit_activity" to "سرگرمی تبدیل کریں",
        "activity_title" to "سرگرمی کا نام",
        "category" to "کیٹیگری",
        "start_time" to "شروع کا وقت",
        "end_time" to "ختم کا وقت",
        "duration" to "دورانیہ",
        "priority" to "ترجیح",
        "reminder" to "یاد دہانی",
        "mark_complete" to "مکمل نشان زد کریں",
        "skip_activity" to "چھوڑ دیں",
        "reschedule" to "نیا وقت طے کریں",
        "notes" to "نوٹس",
        "save" to "محفوظ کریں",
        "cancel" to "منسوخ",
        "delete" to "حذف کریں",
        "reason" to "وجہ",

        // Categories
        "cat_study" to "تعلیم / مطالعہ",
        "cat_wife_time" to "اہلیہ کے ساتھ وقت",
        "cat_sleep" to "نیند",
        "cat_exercise" to "ورزش",
        "cat_prayer" to "نماز",
        "cat_meals" to "کھانا",
        "cat_personal" to "ذاتی ضروریات",
        "cat_work" to "کام",
        "cat_other" to "دیگر",

        // Tasks
        "all_tasks" to "تمام کام",
        "new_task" to "نیا کام",
        "task_title" to "کام کا عنوان",
        "task_desc" to "تفصیل",
        "deadline" to "آخری وقت",
        "priority_low" to "کم",
        "priority_medium" to "درمیانہ",
        "priority_high" to "زیادہ",
        "pending" to "باقی",

        // Focus & Timers
        "focus_hub" to "فوکس اور ٹائمر",
        "study_timer" to "مطالعہ ٹائمر",
        "pomodoro" to "پومودورو (25/5 منٹ)",
        "pomodoro_deep" to "گہرا کام (50/10 منٹ)",
        "custom_timer" to "حسب ضرورت ٹائمر",
        "start" to "شروع کریں",
        "pause" to "روکیں",
        "resume" to "دوبارہ شروع",
        "stop" to "ختم",
        "reset" to "ری سیٹ",
        "wife_time_tracker" to "اہلیہ کا وقت ٹریکر",
        "exercise_tracker" to "ورزش ٹریکر",
        "sleep_tracker" to "نیند ٹریکر",
        "current_session" to "موجودہ سیشن",
        "distraction_mode" to "ڈسپلن فوکس موڈ",
        "distraction_warnings" to "توجہ بھٹکانے والی ایپس کی وارننگ",
        "usage_access_needed" to "ایپس کے استعمال کی اجازت دیں",
        "grant_permission" to "اجازت دیں",

        // Prayers
        "prayer_tracker" to "پانچ وقت کی نمازیں",
        "fajr" to "فجر",
        "zuhr" to "ظہر",
        "asr" to "عصر",
        "maghrib" to "مغرب",
        "isha" to "عشاء",
        "prayer_status" to "نماز کی صورتحال",

        // AI Coach
        "commander_ai" to "کمانڈر اے آئی",
        "ai_coach_subtitle" to "ذاتی ڈسپلن اور پیداواری کوچ",
        "ask_coach" to "کمانڈر اے آئی سے پوچھیں...",
        "send" to "بھیجیں",
        "evaluate_day" to "آج کی پیشرفت کا جائزہ لیں",
        "study_advice" to "6 گھنٹے مطالعہ کا ہدف کیسے حاصل کریں؟",
        "discipline_tips" to "فون کا غیر ضروری استعمال کم کرنے کے طریقے",

        // Voice
        "commander_voice" to "کمانڈر وائس",
        "listening" to "حکم سننے کے لیے تیار...",
        "tap_to_speak" to "بولنے کے لیے دبائیں",
        "voice_help" to "آزمائیں: 'ٹائم ٹیبل دکھائیں'، 'مطالعہ ٹائمر شروع کریں'",

        // Reports & Habits
        "analytics_reports" to "تجزیات اور رپورٹس",
        "daily" to "روزانہ",
        "weekly" to "ہفتہ وار",
        "monthly" to "ماہانہ",
        "habit_tracker" to "عادات کا تسلسل",
        "add_habit" to "عادت شامل کریں",

        // Settings
        "settings_title" to "ترتیبات (سیٹنگز)",
        "language" to "زبان (Language)",
        "notifications" to "سمارٹ یاد دہانی",
        "sound_alerts" to "آواز اور وائبریشن",
        "voice_announcements" to "صوتی اعلانات",
        "data_management" to "ڈیٹا اور بیک اپ",
        "reset_routine" to "ابتدائی شیڈول پر بحال کریں",
        "export_data" to "ڈیٹا ایکسپورٹ کریں",
        "privacy_policy" to "رازداری اور سیکیورٹی معلومات",
        "privacy_desc" to "لائف کمانڈر مکمل طور پر آف لائن کام کرتا ہے۔ آپ کا کوئی ذاتی ڈیٹا فون سے باہر نہیں جاتا۔"
    )
}
