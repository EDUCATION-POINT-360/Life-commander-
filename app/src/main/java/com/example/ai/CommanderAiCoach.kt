package com.example.ai

import com.example.localization.AppLanguage

data class DailyCoachMetrics(
    val studyMinutes: Int,
    val wifeTimeMinutes: Int,
    val sleepMinutes: Int,
    val exerciseMinutes: Int,
    val prayersCompleted: Int,
    val tasksCompleted: Int,
    val tasksTotal: Int,
    val screenTimeMinutes: Int,
    val disciplineScore: Int
)

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "AI" or "USER"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

object CommanderAiCoach {

    fun generateAnalysis(metrics: DailyCoachMetrics, language: AppLanguage): String {
        val isUrdu = language == AppLanguage.URDU

        val studyTarget = 360 // 6h
        val wifeTarget = 420  // 7h
        val sleepTarget = 420 // 7h
        val exerciseTarget = 60 // 1h
        val prayersTarget = 5

        val studyRemaining = maxOf(0, studyTarget - metrics.studyMinutes)
        val wifeRemaining = maxOf(0, wifeTarget - metrics.wifeTimeMinutes)
        val exerciseRemaining = maxOf(0, exerciseTarget - metrics.exerciseMinutes)
        val prayersRemaining = maxOf(0, prayersTarget - metrics.prayersCompleted)

        val studyHours = metrics.studyMinutes / 60
        val studyMins = metrics.studyMinutes % 60
        val wifeHours = metrics.wifeTimeMinutes / 60
        val wifeMins = metrics.wifeTimeMinutes % 60

        return if (isUrdu) {
            buildString {
                appendLine("🎯 **کمانڈر اے آئی روزانہ تجزیہ رپورٹ**")
                appendLine("آپ کا مجموعی ڈسپلن اسکور: ${metrics.disciplineScore}/100")
                appendLine()

                // Study
                if (metrics.studyMinutes >= studyTarget) {
                    appendLine("✅ **مطالعہ:** شاندار! آپ نے 6 گھنٹے کا ہدف پورا کر لیا ہے۔ ($studyHours گھنٹے $studyMins منٹ)")
                } else {
                    val remHours = studyRemaining / 60
                    val remMins = studyRemaining % 60
                    appendLine("⚠️ **مطالعہ:** آپ نے $studyHours گھنٹے $studyMins منٹ مطالعہ کیا۔ ہدف کے لیے ابھی $remHours گھنٹے $remMins منٹ درکار ہیں۔ اگلا سیشن فوری شروع کریں۔")
                }

                // Wife Time
                if (metrics.wifeTimeMinutes >= wifeTarget) {
                    appendLine("✅ **اہلیہ کے ساتھ وقت:** بہترین! آپ نے 7 گھنٹے کا خاندانی وقت مکمل کیا ہے۔")
                } else {
                    val remHours = wifeRemaining / 60
                    val remMins = wifeRemaining % 60
                    appendLine("❤️ **اہلیہ کے ساتھ وقت:** اب تک $wifeHours گھنٹے مکمل، ہدف مکمل کرنے کے لیے $remHours گھنٹے $remMins منٹ کی ضرورت ہے۔")
                }

                // Prayers
                if (metrics.prayersCompleted == 5) {
                    appendLine("🕌 **نمازیں:** ماشاءاللہ! تمام پانچ وقت کی نمازیں ادا کر لی گئی ہیں۔")
                } else {
                    appendLine("🕌 **نمازیں:** آپ نے ${metrics.prayersCompleted}/5 نمازیں ادا کی ہیں۔ $prayersRemaining نمازیں باقی ہیں۔")
                }

                // Exercise
                if (metrics.exerciseMinutes >= exerciseTarget) {
                    appendLine("💪 **ورزش:** 60 منٹ کا روزانہ ہدف مکمل! جسمانی توانائی برقرار ہے۔")
                } else {
                    appendLine("🏃 **ورزش:** ${metrics.exerciseMinutes}/60 منٹ۔ شام کو 30 منٹ کی واک یا ورزش لازمی شامل کریں۔")
                }

                // Screen time
                if (metrics.screenTimeMinutes > 180) {
                    appendLine("📱 **اسکرین وقت:** آج فون کا استعمال ${metrics.screenTimeMinutes / 60} گھنٹے ہے۔ مطالعہ کے دوران فوکس موڈ کا استعمال بڑھائیں۔")
                }

                appendLine()
                appendLine("💡 **کمانڈر مشورہ:** زندگی نظم و ضبط اور تسلسل سے بنتی ہے۔ ہر لمحہ قیمتی ہے، اپنے اہداف پر قائم رہیں۔")
            }
        } else {
            buildString {
                appendLine("🎯 **COMMANDER AI DAILY TACTICAL ANALYSIS**")
                appendLine("Current Discipline Score: ${metrics.disciplineScore} / 100")
                appendLine()

                // Study
                if (metrics.studyMinutes >= studyTarget) {
                    appendLine("✅ **Study Session:** Outstanding work! You reached the 6-hour target ($studyHours hrs $studyMins mins).")
                } else {
                    val remH = studyRemaining / 60
                    val remM = studyRemaining % 60
                    appendLine("⚠️ **Study Session:** Completed $studyHours hrs $studyMins mins. You need $remH hrs $remM mins to reach your 6-hour target.")
                }

                // Wife Time
                if (metrics.wifeTimeMinutes >= wifeTarget) {
                    appendLine("❤️ **Wife Quality Time:** Target achieved! 7 hours of meaningful family companionship fulfilled.")
                } else {
                    val remH = wifeRemaining / 60
                    val remM = wifeRemaining % 60
                    appendLine("❤️ **Wife Quality Time:** Completed $wifeHours hrs $wifeMins mins. Allocate $remH hrs $remM mins more today.")
                }

                // Prayers
                if (metrics.prayersCompleted == 5) {
                    appendLine("🕌 **Prayers:** All 5 daily prayers completed. Spiritual discipline is at 100%.")
                } else {
                    appendLine("🕌 **Prayers:** Completed ${metrics.prayersCompleted}/5 prayers. $prayersRemaining remaining.")
                }

                // Exercise
                if (metrics.exerciseMinutes >= exerciseTarget) {
                    appendLine("💪 **Exercise:** 60-minute physical fitness target accomplished.")
                } else {
                    appendLine("🏃 **Exercise:** ${metrics.exerciseMinutes}/60 minutes recorded. Complete your evening workout to stay sharp.")
                }

                // Screen time
                if (metrics.screenTimeMinutes > 180) {
                    val scrHours = metrics.screenTimeMinutes / 60
                    appendLine("📱 **Screen Time Warning:** Phone usage has reached $scrHours hours. Activate Focus Mode to curb digital distractions.")
                }

                appendLine()
                appendLine("💡 **Commander Tactical Directive:** Discipline is the bridge between goals and accomplishment. Win the next scheduled block!")
            }
        }
    }

    fun answerQuery(query: String, metrics: DailyCoachMetrics, language: AppLanguage): String {
        val q = query.lowercase()
        val isUrdu = language == AppLanguage.URDU

        if (q.contains("study") || q.contains("پڑھائی") || q.contains("مطالعہ")) {
            val rem = maxOf(0, 360 - metrics.studyMinutes)
            val remH = rem / 60
            val remM = rem % 60
            return if (isUrdu) {
                "آپ کا روزانہ مطالعہ کا ہدف 6 گھنٹے ہے۔ اب تک آپ نے ${metrics.studyMinutes / 60} گھنٹے مکمل کیے ہیں۔ باقی $remH گھنٹے $remM منٹ ہیں۔ تجویز: پومودورو موڈ (50 منٹ پڑھائی + 10 منٹ وقفہ) استعمال کریں۔"
            } else {
                "Your daily study target is 6 hours. You have completed ${metrics.studyMinutes / 60}h ${metrics.studyMinutes % 60}m. Remaining: $remH hours $remM minutes. Recommendation: Engage a 50/10 min Deep Work Pomodoro session now."
            }
        }

        if (q.contains("wife") || q.contains("اہلیہ") || q.contains("بیوی")) {
            val rem = maxOf(0, 420 - metrics.wifeTimeMinutes)
            return if (isUrdu) {
                "اہلیہ کے ساتھ روزانہ 7 گھنٹے معیاری وقت کا ہدف ہے۔ اب تک ${metrics.wifeTimeMinutes / 60} گھنٹے مکمل ہو چکے ہیں۔ باقی ${rem / 60} گھنٹے ${rem % 60} منٹ ہیں۔ موبائل ایک طرف رکھ کر پوری توجہ دیں۔"
            } else {
                "Your daily wife companionship target is 7 hours. You have spent ${metrics.wifeTimeMinutes / 60}h ${metrics.wifeTimeMinutes % 60}m together today. Remaining: ${rem / 60}h ${rem % 60}m. Keep phones aside and engage actively."
            }
        }

        if (q.contains("prayer") || q.contains("نماز") || q.contains("namaz")) {
            return if (isUrdu) {
                "نماز کی صورتحال: آپ نے آج ${metrics.prayersCompleted} میں سے 5 نمازیں ادا کی ہیں۔ نماز وقت پر ادا کرنا آپ کے ڈسپلن اسکور کو 20 پوائنٹس دیتا ہے۔"
            } else {
                "Prayer Status: You have completed ${metrics.prayersCompleted} out of 5 daily prayers. Timely prayers award a full 20 points toward your discipline score."
            }
        }

        if (q.contains("distraction") || q.contains("phone") || q.contains("توجہ") || q.contains("موبائل")) {
            return if (isUrdu) {
                "توجہ بھٹکنے سے بچنے کے لیے: 1. پڑھائی کے وقت فون کو دوسرے کمرے میں رکھیں۔ 2. لائف کمانڈر میں فوکس موڈ آن کریں۔ 3. سوشل میڈیا ایپس کے نوٹیفکیشن بند کریں۔"
            } else {
                "Distraction control protocol: 1. Place phone screen-down or in another room during study blocks. 2. Enable Life Commander Focus Mode. 3. Disable non-critical app push notifications."
            }
        }

        return generateAnalysis(metrics, language)
    }
}
