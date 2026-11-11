package com.example.ui.profile

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.CommanderCard
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*
import com.example.utils.UsageStatsHelper

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val isUrdu = language == AppLanguage.URDU
    val hasUsagePerm by viewModel.hasUsagePermission.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Profile Banner
        item {
            CommanderCard(borderColor = CommanderGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CommanderDarkCharcoal)
                            .border(2.dp, CommanderGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CommanderGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "COMMANDER PROFILE",
                            color = CommanderGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Life Commander Operator",
                            color = CommanderTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Target: 6h Study • 7h Wife • 7h Sleep • 5 Prayers",
                            color = CommanderTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 2. Language Selection Card
        item {
            CommanderCard {
                Text(
                    text = viewModel.getString("language"),
                    color = CommanderGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LanguageOption(
                        title = "English (Default)",
                        isSelected = language == AppLanguage.ENGLISH,
                        onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) },
                        modifier = Modifier.weight(1f)
                    )
                    LanguageOption(
                        title = "اردو (Urdu RTL)",
                        isSelected = language == AppLanguage.URDU,
                        onClick = { viewModel.setLanguage(AppLanguage.URDU) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. System Permissions & Distraction Monitor
        item {
            CommanderCard {
                Text(
                    text = "DEVICE PERMISSIONS & USAGE STATS",
                    color = CommanderGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Usage Stats Access",
                            color = CommanderTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (hasUsagePerm) "Permission granted. Monitoring distractions." else "Required to monitor app distractions.",
                            color = if (hasUsagePerm) CommanderSuccess else CommanderWarning,
                            fontSize = 12.sp
                        )
                    }
                    if (!hasUsagePerm) {
                        LuxuryButton(
                            text = "Grant",
                            onClick = { UsageStatsHelper.openUsageStatsSettings(context) },
                            isPrimary = false
                        )
                    } else {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = CommanderSuccess)
                    }
                }
            }
        }

        // 4. Quick Navigation Hub Options
        item {
            CommanderCard {
                Text(
                    text = "COMMAND MODULES",
                    color = CommanderGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsMenuRow("AI Discipline Coach", Icons.Default.SmartToy) {
                        viewModel.navigateTo(Screen.AI_COACH)
                    }
                    SettingsMenuRow("Habit Continuity Streaks", Icons.Default.Stars) {
                        viewModel.navigateTo(Screen.HABITS)
                    }
                    SettingsMenuRow("Spiritual Prayer Tracker", Icons.Default.Mosque) {
                        viewModel.navigateTo(Screen.PRAYERS)
                    }
                    SettingsMenuRow("Discipline & Performance Reports", Icons.Default.BarChart) {
                        viewModel.navigateTo(Screen.REPORTS)
                    }
                }
            }
        }

        // 5. Data & Routine Reset
        item {
            CommanderCard {
                Text(
                    text = viewModel.getString("data_management"),
                    color = CommanderGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LuxuryButton(
                        text = viewModel.getString("reset_routine"),
                        onClick = { showResetConfirm = true },
                        icon = Icons.Default.RestartAlt,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LuxuryButton(
                        text = viewModel.getString("privacy_policy"),
                        onClick = { showPrivacyDialog = true },
                        icon = Icons.Default.Lock,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = CommanderDarkCharcoal,
            titleContentColor = CommanderGold,
            title = { Text(viewModel.getString("privacy_policy")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = viewModel.getString("privacy_desc"),
                        color = CommanderTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    Text(
                        text = "• Zero call or audio recording.\n• Zero message scanning.\n• All timetable, study sessions, and wife quality time records remain strictly stored on your local SQLite database.",
                        color = CommanderTextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Understood", color = CommanderGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = CommanderDarkCharcoal,
            titleContentColor = CommanderGold,
            title = { Text("Reset to Default Schedule?") },
            text = { Text("Restore original 24-hour routine: 6h study, 7h wife time, 7h sleep, 1h workout, 5 prayers.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetToDefaultRoutine()
                    showResetConfirm = false
                }) {
                    Text("Reset", color = CommanderRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = CommanderTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CommanderGold else CommanderCardSurface)
            .border(1.dp, if (isSelected) CommanderGold else CommanderBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) CommanderBlack else CommanderTextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CommanderGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = CommanderTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = CommanderTextMuted, modifier = Modifier.size(18.dp))
    }
}
