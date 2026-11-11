package com.example.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.components.DisciplineScoreBadge
import com.example.ui.components.LinearTargetProgress
import com.example.ui.theme.*

@Composable
fun ReportsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("DAILY") } // DAILY, WEEKLY, MONTHLY
    val disciplineScore by viewModel.disciplineScore.collectAsState()
    val totalStudyMin by viewModel.totalStudyMinutes.collectAsState()
    val totalWifeMin by viewModel.totalWifeMinutes.collectAsState()
    val totalExerciseMin by viewModel.totalExerciseMinutes.collectAsState()
    val prayerRecord by viewModel.prayerRecord.collectAsState()
    val dailyTasks by viewModel.dailyTasks.collectAsState()
    val screenTimeMin by viewModel.screenTimeMinutes.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("reports_back_button")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = CommanderGold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = viewModel.getString("analytics_reports"),
                        color = CommanderTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Comprehensive performance analytics and score trends",
                        color = CommanderTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 2. Tab Bar (Daily, Weekly, Monthly)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReportTab(viewModel.getString("daily"), selectedTab == "DAILY") { selectedTab = "DAILY" }
                ReportTab(viewModel.getString("weekly"), selectedTab == "WEEKLY") { selectedTab = "WEEKLY" }
                ReportTab(viewModel.getString("monthly"), selectedTab == "MONTHLY") { selectedTab = "MONTHLY" }
            }
        }

        // 3. Main Discipline Performance Card
        item {
            CommanderCard(borderColor = CommanderGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedTab == "DAILY") "Today's Discipline Rating" else if (selectedTab == "WEEKLY") "7-Day Discipline Average" else "30-Day Discipline Index",
                            color = CommanderTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (disciplineScore >= 80) "Elite Command Level" else if (disciplineScore >= 50) "Disciplined Operator" else "Needs Realignment",
                            color = CommanderTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DisciplineScoreBadge(score = disciplineScore)
                }
            }
        }

        // 4. Detailed Target Ratios
        item {
            CommanderCard {
                Text(
                    text = "TARGET EFFICIENCY METRICS",
                    color = CommanderGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                LinearTargetProgress(
                    label = "Study (6h Target)",
                    current = (totalStudyMin / 60f),
                    target = 6f,
                    unit = "Hours",
                    color = CommanderRed
                )
                Spacer(modifier = Modifier.height(12.dp))

                LinearTargetProgress(
                    label = "Wife Quality Time (7h Target)",
                    current = (totalWifeMin / 60f),
                    target = 7f,
                    unit = "Hours",
                    color = CommanderGold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LinearTargetProgress(
                    label = "Physical Fitness (60m Target)",
                    current = totalExerciseMin.toFloat(),
                    target = 60f,
                    unit = "Mins",
                    color = CommanderSuccess
                )
                Spacer(modifier = Modifier.height(12.dp))

                LinearTargetProgress(
                    label = "Five Daily Prayers (5 Target)",
                    current = (prayerRecord?.completedCount ?: 0).toFloat(),
                    target = 5f,
                    unit = "Offered",
                    color = CommanderInfo
                )
            }
        }

        // 5. Digital Health & Screen Time Analysis
        item {
            CommanderCard {
                Text(
                    text = "DIGITAL HEALTH & SCREEN TIME",
                    color = CommanderGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Recorded Phone Usage", color = CommanderTextSecondary, fontSize = 11.sp)
                        Text("${screenTimeMin / 60}h ${screenTimeMin % 60}m", color = CommanderTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Study Distraction Risk", color = CommanderTextSecondary, fontSize = 11.sp)
                        Text(if (screenTimeMin > 180) "Elevated" else "Controlled", color = if (screenTimeMin > 180) CommanderRed else CommanderSuccess, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ReportTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CommanderGold else CommanderCardSurface)
            .border(1.dp, if (isSelected) CommanderGold else CommanderBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) CommanderBlack else CommanderTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
