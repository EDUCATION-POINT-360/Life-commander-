package com.example.utils

import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import java.util.Calendar

data class AppUsageInfo(
    val packageName: String,
    val appLabel: String,
    val totalTimeInForegroundMinutes: Long,
    val isDistraction: Boolean = false
)

object UsageStatsHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageStatsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private val knownDistractionPackages = setOf(
        "com.google.android.youtube",
        "com.instagram.android",
        "com.facebook.katana",
        "com.twitter.android",
        "com.zhiliaoapp.musically", // TikTok
        "com.snapchat.android",
        "com.netflix.mediaclient",
        "com.reddit.frontpage",
        "com.pinterest"
    )

    fun getTodayUsageStats(context: Context): Pair<Long, List<AppUsageInfo>> {
        if (!hasUsageStatsPermission(context)) {
            return Pair(0L, emptyList())
        }

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        ) ?: return Pair(0L, emptyList())

        val pm = context.packageManager
        val list = mutableListOf<AppUsageInfo>()
        var totalForegroundMs = 0L

        for (u in stats) {
            if (u.totalTimeInForeground > 60_000) { // at least 1 minute
                val label = try {
                    val appInfo = pm.getApplicationInfo(u.packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    u.packageName.substringAfterLast('.')
                }

                val minutes = u.totalTimeInForeground / 60_000
                val isDistraction = knownDistractionPackages.contains(u.packageName) ||
                        label.contains("Facebook", true) ||
                        label.contains("Instagram", true) ||
                        label.contains("TikTok", true) ||
                        label.contains("YouTube", true)

                totalForegroundMs += u.totalTimeInForeground
                list.add(
                    AppUsageInfo(
                        packageName = u.packageName,
                        appLabel = label,
                        totalTimeInForegroundMinutes = minutes,
                        isDistraction = isDistraction
                    )
                )
            }
        }

        val sorted = list.sortedByDescending { it.totalTimeInForegroundMinutes }
        return Pair(totalForegroundMs / 60_000, sorted)
    }
}
