package com.example.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ActivityCategory
import com.example.data.models.Habit
import com.example.data.repositories.CommanderRepository
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.theme.*

@Composable
fun HabitTrackerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val isUrdu = language == AppLanguage.URDU
    val habits by viewModel.habits.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val todayDateStr = remember { CommanderRepository.getTodayDateString() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("habits_back_button")
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = CommanderGold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = viewModel.getString("habit_tracker"),
                            color = CommanderTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daily streak continuity builds long-term discipline",
                            color = CommanderTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CommanderGold,
                    contentColor = CommanderBlack,
                    modifier = Modifier.size(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit", modifier = Modifier.size(20.dp))
                }
            }
        }

        // 2. Habits List
        items(habits, key = { it.id }) { habit ->
            val isDoneToday = habit.lastCompletedDate == todayDateStr
            val habitTitle = if (isUrdu && habit.titleUrdu.isNotEmpty()) habit.titleUrdu else habit.title

            CommanderCard(
                borderColor = if (isDoneToday) CommanderGold.copy(alpha = 0.6f) else CommanderBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (isDoneToday) CommanderGold.copy(alpha = 0.2f) else CommanderSurfaceVariant)
                                .clickable { viewModel.toggleHabit(habit) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDoneToday) Icons.Default.Check else Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = if (isDoneToday) CommanderGold else CommanderRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = habitTitle,
                                color = CommanderTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "🔥 ${habit.currentStreak} day streak",
                                    color = CommanderGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Best: ${habit.bestStreak}d",
                                    color = CommanderTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDoneToday) CommanderSuccess.copy(alpha = 0.2f) else CommanderSurfaceVariant)
                            .clickable { viewModel.toggleHabit(habit) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isDoneToday) "Completed" else "Mark Done",
                            color = if (isDoneToday) CommanderSuccess else CommanderTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, urdu, cat ->
                viewModel.addHabit(title, urdu, cat)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, ActivityCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var urdu by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ActivityCategory.STUDY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        title = { Text("Add Custom Habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title (English)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = urdu,
                    onValueChange = { urdu = it },
                    label = { Text("Habit Title (اردو - اختیاری)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) onAdd(title, urdu, category)
                }
            ) {
                Text("Save Habit", color = CommanderGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommanderTextSecondary)
            }
        }
    )
}
