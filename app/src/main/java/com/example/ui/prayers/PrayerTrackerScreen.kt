package com.example.ui.prayers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.models.PrayerRecord
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.components.LinearTargetProgress
import com.example.ui.theme.*

@Composable
fun PrayerTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val isUrdu = language == AppLanguage.URDU
    val prayerRecord by viewModel.prayerRecord.collectAsState()

    var showEditTimeDialog by remember { mutableStateOf(false) }

    val completedCount = prayerRecord?.completedCount ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with back and title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("prayer_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = CommanderGold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = viewModel.getString("prayer_tracker"),
                            color = CommanderTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track spiritual discipline and daily congregation",
                            color = CommanderTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = { showEditTimeDialog = true }) {
                    Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "Edit Times", tint = CommanderGold)
                }
            }
        }

        // 2. Daily Prayer Progress Card
        item {
            CommanderCard(borderColor = CommanderInfo) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Prayer Checklist",
                            color = CommanderTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Awards 20 points towards your daily discipline score",
                            color = CommanderGold,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "$completedCount / 5",
                        color = CommanderInfo,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                LinearTargetProgress(
                    label = "Completion Ratio",
                    current = completedCount.toFloat(),
                    target = 5f,
                    unit = "Prayers",
                    color = CommanderInfo
                )
            }
        }

        // 3. Five Daily Prayers Checklist
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PrayerItemRow(
                    name = viewModel.getString("fajr"),
                    urduName = "نماز فجر",
                    time = prayerRecord?.fajrTime ?: "05:45 AM",
                    isCompleted = prayerRecord?.fajrCompleted ?: false,
                    isUrdu = isUrdu,
                    onToggle = { viewModel.togglePrayer("fajr", it) }
                )

                PrayerItemRow(
                    name = viewModel.getString("zuhr"),
                    urduName = "نماز ظہر",
                    time = prayerRecord?.zuhrTime ?: "12:30 PM",
                    isCompleted = prayerRecord?.zuhrCompleted ?: false,
                    isUrdu = isUrdu,
                    onToggle = { viewModel.togglePrayer("zuhr", it) }
                )

                PrayerItemRow(
                    name = viewModel.getString("asr"),
                    urduName = "نماز عصر",
                    time = prayerRecord?.asrTime ?: "04:15 PM",
                    isCompleted = prayerRecord?.asrCompleted ?: false,
                    isUrdu = isUrdu,
                    onToggle = { viewModel.togglePrayer("asr", it) }
                )

                PrayerItemRow(
                    name = viewModel.getString("maghrib"),
                    urduName = "نماز مغرب",
                    time = prayerRecord?.maghribTime ?: "06:10 PM",
                    isCompleted = prayerRecord?.maghribCompleted ?: false,
                    isUrdu = isUrdu,
                    onToggle = { viewModel.togglePrayer("maghrib", it) }
                )

                PrayerItemRow(
                    name = viewModel.getString("isha"),
                    urduName = "نماز عشاء",
                    time = prayerRecord?.ishaTime ?: "08:15 PM",
                    isCompleted = prayerRecord?.ishaCompleted ?: false,
                    isUrdu = isUrdu,
                    onToggle = { viewModel.togglePrayer("isha", it) }
                )
            }
        }

        // 4. Spiritual Discipline & Hadith/Motivation Card
        item {
            CommanderCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Mosque, contentDescription = null, tint = CommanderInfo, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Spiritual Discipline Insight",
                        color = CommanderInfo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isUrdu) "نماز مومن کی معراج اور دن بھر کے نظام الاوقات کی اصل روح ہے۔ وقت پر ادا کی جانے والی نماز زندگی میں برکت اور وقت کا بہترین ڈسپلن پیدا کرتی ہے۔"
                    else "Prayer forms the central axis of your daily routine. Anchoring your study, wife companionship, and sleep around the 5 prayer times brings harmony and unshakeable discipline to your day.",
                    color = CommanderTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }

    if (showEditTimeDialog) {
        EditPrayerTimesDialog(
            record = prayerRecord,
            onDismiss = { showEditTimeDialog = false },
            onSave = { updated ->
                // Handled in dialog
                showEditTimeDialog = false
            }
        )
    }
}

@Composable
private fun PrayerItemRow(
    name: String,
    urduName: String,
    time: String,
    isCompleted: Boolean,
    isUrdu: Boolean,
    onToggle: (Boolean) -> Unit
) {
    CommanderCard(
        borderColor = if (isCompleted) CommanderSuccess.copy(alpha = 0.6f) else CommanderBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggle(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = CommanderSuccess,
                        uncheckedColor = CommanderBorder,
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isUrdu) urduName else name,
                        color = if (isCompleted) CommanderTextMuted else CommanderTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scheduled at $time",
                        color = CommanderGold,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCompleted) CommanderSuccess.copy(alpha = 0.2f) else CommanderSurfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isCompleted) "Offered" else "Pending",
                    color = if (isCompleted) CommanderSuccess else CommanderTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EditPrayerTimesDialog(
    record: PrayerRecord?,
    onDismiss: () -> Unit,
    onSave: (PrayerRecord) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        title = { Text("Local Prayer Times Configuration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Prayer timetable calculation: Karachi / University of Islamic Sciences", color = CommanderTextSecondary, fontSize = 12.sp)
                Text("Fajr: 05:45 AM", color = CommanderTextPrimary, fontSize = 13.sp)
                Text("Zuhr: 12:30 PM", color = CommanderTextPrimary, fontSize = 13.sp)
                Text("Asr: 04:15 PM", color = CommanderTextPrimary, fontSize = 13.sp)
                Text("Maghrib: 06:10 PM", color = CommanderTextPrimary, fontSize = 13.sp)
                Text("Isha: 08:15 PM", color = CommanderTextPrimary, fontSize = 13.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = CommanderGold, fontWeight = FontWeight.Bold)
            }
        }
    )
}
