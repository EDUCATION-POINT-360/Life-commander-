package com.example.ui.focus

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FocusSessionType
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*
import com.example.utils.UsageStatsHelper

@Composable
fun FocusScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val timerSeconds by viewModel.timerSeconds.collectAsState()
    val activeType by viewModel.activeSessionType.collectAsState()
    val currentSubject by viewModel.currentSubject.collectAsState()
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsState()
    val screenTimeMin by viewModel.screenTimeMinutes.collectAsState()
    val topApps by viewModel.topApps.collectAsState()

    var showSubjectDialog by remember { mutableStateOf(false) }

    val subjects = listOf("Software Engineering", "Islamic Knowledge", "Mathematics", "Science", "General Study")

    val hours = timerSeconds / 3600
    val minutes = (timerSeconds % 3600) / 60
    val seconds = timerSeconds % 60
    val timeFormatted = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Title
        item {
            Text(
                text = viewModel.getString("focus_hub"),
                color = CommanderTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Execute study sessions, wife companionship, and workout timers",
                color = CommanderTextSecondary,
                fontSize = 12.sp
            )
        }

        // 2. Mode Selector Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(
                    listOf(
                        Pair(FocusSessionType.STUDY_POMODORO_25, "Pomodoro (25/5m)"),
                        Pair(FocusSessionType.STUDY_DEEP_50, "Deep Work (50/10m)"),
                        Pair(FocusSessionType.STUDY_CUSTOM, "Custom Study"),
                        Pair(FocusSessionType.WIFE_TIME, "Wife Time (7h Target)"),
                        Pair(FocusSessionType.EXERCISE_MORNING, "Morning Workout (30m)"),
                        Pair(FocusSessionType.EXERCISE_EVENING, "Evening Workout (30m)")
                    )
                ) { (type, label) ->
                    val isSelected = activeType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CommanderRed else CommanderCardSurface)
                            .border(1.dp, if (isSelected) CommanderRed else CommanderBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                if (!isTimerRunning) {
                                    viewModel.startFocusTimer(type)
                                    viewModel.pauseFocusTimer()
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else CommanderTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Central Timer Card
        item {
            CommanderCard(
                borderColor = if (isTimerRunning) CommanderGold else CommanderBorder,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Session Label
                    val sessionTitle = when (activeType) {
                        FocusSessionType.STUDY_POMODORO_25 -> "Pomodoro Study Session"
                        FocusSessionType.STUDY_DEEP_50 -> "Deep Work Session"
                        FocusSessionType.STUDY_CUSTOM -> "Custom Study Session"
                        FocusSessionType.WIFE_TIME -> "Wife Quality Time Session"
                        FocusSessionType.EXERCISE_MORNING -> "Morning Exercise Session"
                        FocusSessionType.EXERCISE_EVENING -> "Evening Exercise Session"
                    }

                    Text(
                        text = sessionTitle.uppercase(),
                        color = CommanderGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    if (activeType == FocusSessionType.STUDY_POMODORO_25 ||
                        activeType == FocusSessionType.STUDY_DEEP_50 ||
                        activeType == FocusSessionType.STUDY_CUSTOM
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CommanderSurfaceVariant)
                                .clickable { showSubjectDialog = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Subject: $currentSubject",
                                color = CommanderTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = CommanderGold, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Large Digital Clock
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .clip(CircleShape)
                            .background(CommanderDarkCharcoal)
                            .border(3.dp, if (isTimerRunning) CommanderGold else CommanderBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                color = if (isTimerRunning) CommanderTextPrimary else CommanderTextSecondary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isTimerRunning) "ACTIVE" else "READY",
                                color = if (isTimerRunning) CommanderSuccess else CommanderTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Timer Control Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isTimerRunning) {
                            LuxuryButton(
                                text = if (timerSeconds > 0) "Resume" else "Start",
                                onClick = { viewModel.startFocusTimer(activeType) },
                                icon = Icons.Default.PlayArrow,
                                isPrimary = true,
                                modifier = Modifier.testTag("start_timer_button")
                            )
                        } else {
                            LuxuryButton(
                                text = "Pause",
                                onClick = { viewModel.pauseFocusTimer() },
                                icon = Icons.Default.Pause,
                                isPrimary = false,
                                modifier = Modifier.testTag("pause_timer_button")
                            )
                        }

                        if (timerSeconds > 0) {
                            LuxuryButton(
                                text = "Save Session",
                                onClick = { viewModel.stopAndSaveFocusTimer() },
                                icon = Icons.Default.Check,
                                isPrimary = true,
                                modifier = Modifier.testTag("save_session_button")
                            )

                            IconButton(
                                onClick = { viewModel.resetFocusTimer() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CommanderSurfaceVariant)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = CommanderTextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 4. Medium Control & Distraction Monitor Card
        item {
            CommanderCard(borderColor = if (screenTimeMin > 180) CommanderRed else CommanderBorder) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = viewModel.getString("distraction_mode"),
                            color = CommanderGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Medium control system to safeguard focus sessions",
                            color = CommanderTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isTimerRunning) CommanderGold else CommanderTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!hasUsagePerm) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CommanderSurfaceVariant)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = viewModel.getString("usage_access_needed"),
                                color = CommanderTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Allows Life Commander to detect distracting apps like YouTube, TikTok, and Instagram during your 6-hour study routine.",
                                color = CommanderTextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LuxuryButton(
                                text = viewModel.getString("grant_permission"),
                                onClick = { UsageStatsHelper.openUsageStatsSettings(context) },
                                icon = Icons.Default.Security,
                                isPrimary = false
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Phone Usage Today", color = CommanderTextSecondary, fontSize = 11.sp)
                            Text("${screenTimeMin / 60}h ${screenTimeMin % 60}m", color = CommanderTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Distraction Apps", color = CommanderTextSecondary, fontSize = 11.sp)
                            val distractionCount = topApps.count { it.isDistraction }
                            Text("$distractionCount detected", color = if (distractionCount > 0) CommanderWarning else CommanderSuccess, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (topApps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Top Used Apps Today:", color = CommanderTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        topApps.take(4).forEach { appInfo ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = appInfo.appLabel,
                                    color = if (appInfo.isDistraction) CommanderRed else CommanderTextPrimary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${appInfo.totalTimeInForegroundMinutes} mins",
                                    color = CommanderTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showSubjectDialog = false },
            containerColor = CommanderDarkCharcoal,
            titleContentColor = CommanderGold,
            title = { Text("Select Study Subject") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    subjects.forEach { s ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentSubject == s) CommanderSurfaceVariant else Color.Transparent)
                            .clickable {
                                viewModel.setSubject(s)
                                showSubjectDialog = false
                            }
                            .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentSubject == s,
                                onClick = {
                                    viewModel.setSubject(s)
                                    showSubjectDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = CommanderGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s, color = CommanderTextPrimary, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSubjectDialog = false }) {
                    Text("Close", color = CommanderGold)
                }
            }
        )
    }
}
