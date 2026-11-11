package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.CommanderCard
import com.example.ui.components.DisciplineScoreBadge
import com.example.ui.components.LinearTargetProgress
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val isDayStarted by viewModel.isDayStarted.collectAsState()
    val disciplineScore by viewModel.disciplineScore.collectAsState()
    val totalStudyMin by viewModel.totalStudyMinutes.collectAsState()
    val totalWifeMin by viewModel.totalWifeMinutes.collectAsState()
    val totalExerciseMin by viewModel.totalExerciseMinutes.collectAsState()
    val prayerRecord by viewModel.prayerRecord.collectAsState()
    val dailyTasks by viewModel.dailyTasks.collectAsState()
    val activities by viewModel.routineActivities.collectAsState()
    val screenTimeMin by viewModel.screenTimeMinutes.collectAsState()

    // Find next activity
    val nowCalendar = Calendar.getInstance()
    val currentMinutes = nowCalendar.get(Calendar.HOUR_OF_DAY) * 60 + nowCalendar.get(Calendar.MINUTE)
    val nextActivity = activities.firstOrNull { it.startTimeMinutes > currentMinutes && !it.isCompletedToday }
        ?: activities.firstOrNull { !it.isCompletedToday }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    val completedTasksCount = dailyTasks.count { it.isCompleted }
    val remainingTasksCount = dailyTasks.size - completedTasksCount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with greeting and language toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.getString("welcome_back"),
                        color = CommanderTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDateStr,
                        color = CommanderTextSecondary,
                        fontSize = 13.sp
                    )
                }

                // Quick Language Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CommanderCardSurface)
                        .border(1.dp, CommanderGold, RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleLanguage() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("language_toggle_button")
                ) {
                    Text(
                        text = if (language == AppLanguage.ENGLISH) "اردو" else "EN",
                        color = CommanderGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. "START MY DAY" / "FINISH MY DAY" Hero Action Banner
        item {
            CommanderCard(
                borderColor = if (isDayStarted) CommanderGold else CommanderRed,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isDayStarted) viewModel.getString("day_active") else "LIFE COMMANDER",
                            color = CommanderGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isDayStarted) "Execute today's 24-hour targets with high discipline." else "Initialize today's routine & activate timers.",
                            color = CommanderTextPrimary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (!isDayStarted) {
                        LuxuryButton(
                            text = viewModel.getString("start_my_day"),
                            onClick = { viewModel.startMyDay() },
                            icon = Icons.Default.PlayArrow,
                            isPrimary = true,
                            modifier = Modifier.testTag("start_day_button")
                        )
                    } else {
                        LuxuryButton(
                            text = viewModel.getString("finish_my_day"),
                            onClick = { viewModel.finishMyDay() },
                            icon = Icons.Default.Check,
                            isPrimary = false,
                            modifier = Modifier.testTag("finish_day_button")
                        )
                    }
                }
            }
        }

        // 3. Discipline Score Card
        item {
            CommanderCard(borderColor = CommanderGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.getString("discipline_score"),
                            color = CommanderTextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily Discipline Level",
                            color = CommanderTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DisciplineScoreBadge(score = disciplineScore)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Score breakdown points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreCategoryItem("Study", "25 pts", totalStudyMin >= 360)
                    ScoreCategoryItem("Sleep", "20 pts", true)
                    ScoreCategoryItem("Prayers", "20 pts", (prayerRecord?.completedCount ?: 0) == 5)
                    ScoreCategoryItem("Wife", "15 pts", totalWifeMin >= 420)
                    ScoreCategoryItem("Exercise", "10 pts", totalExerciseMin >= 60)
                    ScoreCategoryItem("Tasks", "10 pts", dailyTasks.isNotEmpty() && completedTasksCount == dailyTasks.size)
                }
            }
        }

        // 4. Core Targets Progress Section
        item {
            CommanderCard {
                Text(
                    text = viewModel.getString("daily_target_progress"),
                    color = CommanderGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // 1. Study (6 Hours = 360 mins)
                LinearTargetProgress(
                    label = viewModel.getString("study_target"),
                    current = (totalStudyMin / 60f),
                    target = 6f,
                    unit = viewModel.getString("hours"),
                    color = CommanderRed
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 2. Wife Time (7 Hours = 420 mins)
                LinearTargetProgress(
                    label = viewModel.getString("wife_time_target"),
                    current = (totalWifeMin / 60f),
                    target = 7f,
                    unit = viewModel.getString("hours"),
                    color = CommanderGold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 3. Sleep (7 Hours = 420 mins)
                LinearTargetProgress(
                    label = viewModel.getString("sleep_target"),
                    current = 7f, // 7h default routine
                    target = 7f,
                    unit = viewModel.getString("hours"),
                    color = CommanderPurple
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 4. Exercise (60 Minutes)
                LinearTargetProgress(
                    label = viewModel.getString("exercise_target"),
                    current = totalExerciseMin.toFloat(),
                    target = 60f,
                    unit = viewModel.getString("minutes"),
                    color = CommanderSuccess
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 5. Prayers (5 Daily)
                LinearTargetProgress(
                    label = viewModel.getString("prayers_target"),
                    current = (prayerRecord?.completedCount ?: 0).toFloat(),
                    target = 5f,
                    unit = "Prayers",
                    color = CommanderInfo
                )
            }
        }

        // 5. Next Activity & Next Prayer Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Next Activity
                CommanderCard(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = CommanderGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.getString("next_activity"),
                            color = CommanderTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = nextActivity?.title ?: "No more activities",
                        color = CommanderTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = nextActivity?.formattedTimeRange() ?: "All completed",
                        color = CommanderGold,
                        fontSize = 12.sp
                    )
                }

                // Next Prayer
                CommanderCard(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = CommanderInfo,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.getString("next_prayer"),
                            color = CommanderTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val p = prayerRecord
                    val nextPrayerText = when {
                        p == null || !p.fajrCompleted -> "Fajr (05:45 AM)"
                        !p.zuhrCompleted -> "Zuhr (12:30 PM)"
                        !p.asrCompleted -> "Asr (04:15 PM)"
                        !p.maghribCompleted -> "Maghrib (06:10 PM)"
                        !p.ishaCompleted -> "Isha (08:15 PM)"
                        else -> "All 5 Completed!"
                    }
                    Text(
                        text = nextPrayerText,
                        color = CommanderTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "${p?.completedCount ?: 0} / 5 Done",
                        color = CommanderInfo,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 6. Tasks Summary & Screen Time Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tasks Summary
                CommanderCard(modifier = Modifier.weight(1f)) {
                    Text(
                        text = viewModel.getString("tasks_summary"),
                        color = CommanderTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$completedTasksCount / ${dailyTasks.size} ${viewModel.getString("completed")}",
                        color = CommanderTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$remainingTasksCount ${viewModel.getString("remaining")}",
                        color = if (remainingTasksCount > 0) CommanderWarning else CommanderSuccess,
                        fontSize = 12.sp
                    )
                }

                // Screen Time Summary
                CommanderCard(modifier = Modifier.weight(1f)) {
                    Text(
                        text = viewModel.getString("screen_time"),
                        color = CommanderTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val hours = screenTimeMin / 60
                    val mins = screenTimeMin % 60
                    Text(
                        text = "$hours hrs $mins mins",
                        color = if (screenTimeMin > 180) CommanderRed else CommanderTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (screenTimeMin > 180) "High Distraction" else "Optimal Level",
                        color = if (screenTimeMin > 180) CommanderRed else CommanderSuccess,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 7. Quick Shortcuts to Hubs (AI Coach, Voice, Prayers, Reports, Habits)
        item {
            Text(
                text = "COMMAND CENTER QUICK ACCESS",
                color = CommanderGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HubQuickCard(
                    title = viewModel.getString("ai_coach"),
                    icon = Icons.Default.SmartToy,
                    color = CommanderRed,
                    onClick = { viewModel.navigateTo(Screen.AI_COACH) },
                    modifier = Modifier.weight(1f)
                )
                HubQuickCard(
                    title = viewModel.getString("prayers"),
                    icon = Icons.Default.Mosque,
                    color = CommanderInfo,
                    onClick = { viewModel.navigateTo(Screen.PRAYERS) },
                    modifier = Modifier.weight(1f)
                )
                HubQuickCard(
                    title = viewModel.getString("habits"),
                    icon = Icons.Default.Stars,
                    color = CommanderGold,
                    onClick = { viewModel.navigateTo(Screen.HABITS) },
                    modifier = Modifier.weight(1f)
                )
                HubQuickCard(
                    title = viewModel.getString("reports"),
                    icon = Icons.Default.BarChart,
                    color = CommanderSuccess,
                    onClick = { viewModel.navigateTo(Screen.REPORTS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScoreCategoryItem(label: String, points: String, achieved: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (achieved) CommanderGold else CommanderBorder)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (achieved) CommanderTextPrimary else CommanderTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = points,
            color = if (achieved) CommanderGold else CommanderTextMuted,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun HubQuickCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .defaultMinSize(minHeight = 72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CommanderCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CommanderBorder)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = CommanderTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
