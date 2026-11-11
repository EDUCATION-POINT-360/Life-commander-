# LIFE COMMANDER AI

**Personal Life Management System**  
*Platform:* Android (API 29+) • *Language:* Kotlin • *UI Toolkit:* Jetpack Compose Material 3 • *Architecture:* MVVM + Room Database

---

## 1. App Identity & Luxury Aesthetic
- **Primary Background:** Black (`#101010`)
- **Secondary Background:** Dark Charcoal (`#1B1B1B`)
- **Card Surfaces:** Deep Charcoal (`#222222`)
- **Primary Accent:** Red (`#E53935`)
- **Secondary Accent:** Gold (`#FFC857`)
- **Custom Adaptive Icon:** Centered golden shield emblem with clock and red star on solid `#101010` background.
- **Languages:** English and Urdu (with native right-to-left RTL layout support).

---

## 2. Core Routine Targets (Exact 24-Hour Schedule)
1. **Study Target:** 6 Hours daily (Morning Deep Work 2h + Afternoon Focus 2h + Night Session 2h).
2. **Wife Quality Time Target:** 7 Hours daily (Morning 3h + Afternoon 2.5h + Evening 1.5h).
3. **Sleep Target:** 7 Hours daily (11:00 PM – 06:00 AM).
4. **Physical Fitness:** 1 Hour daily (Morning 30m + Evening 30m).
5. **Spiritual Discipline:** 5 Daily Prayers (Fajr, Zuhr, Asr, Maghrib, Isha).
6. **Task Execution:** Daily task management with Low/Medium/High priorities and deadlines.

---

## 3. Main Navigation & Modules
- **Dashboard:** Live command center with Discipline Score (/100), targets progress bars, "START MY DAY" / "FINISH MY DAY" action hero, next activity countdown, next prayer reminder, and digital screen-time overview.
- **Timetable:** 24-hour visual routine with real-time total hours validation, category filter chips, activity completion toggles, skip reasons, rescheduling, and full CRUD.
- **Tasks:** Task checklist with priority tags, deadlines, completion percentage, and deletion.
- **Focus & Timers:**
  - Pomodoro Study Timer (25 min study / 5 min break)
  - Deep Work Timer (50 min study / 10 min break)
  - Custom Study Timer with subject selector (Software Engineering, Islamic Knowledge, Mathematics, Science, General Study)
  - Wife Time Tracker (7h target timer)
  - Exercise Timers (Morning 30m, Evening 30m)
  - Medium Control System with real `UsageStatsManager` distraction detection.
- **Prayer Tracker:** 5 daily prayers checklist with Karachi/Islamic Sciences timetable, manual adjustment dialog, and 20-point discipline score weighting.
- **AI Coach ("COMMANDER AI"):** Intelligent offline rule-based tactical coach providing personalized daily audits and advice in English and Urdu, with audio read-aloud via Android TextToSpeech.
- **Voice Assistant ("COMMANDER VOICE"):** Voice command listening via `SpeechRecognizer` + fallback text commands ("Show timetable", "Start study timer", "Show today's progress", "Show prayer status").
- **Habit Tracker:** Daily continuity streaks, streak fire counters, and custom habit creation.
- **Analytics & Reports:** Daily, weekly, and monthly performance reports.
- **Profile & Settings:** English/Urdu switcher, usage permissions, 100% offline privacy policy, and routine reset.

---

## 4. Technical Stack
- **Jetpack Compose + Material 3**
- **Room SQLite Database** (`LifeCommanderDatabase` with 9 entities)
- **Android AlarmManager & BroadcastReceivers** (`AlarmReceiver`, `BootReceiver`)
- **Android UsageStatsManager** (`UsageStatsHelper`)
- **Android TextToSpeech & SpeechRecognizer** (`CommanderVoiceManager`)
- **Robolectric Local JVM Tests** (`ExampleRobolectricTest`)
