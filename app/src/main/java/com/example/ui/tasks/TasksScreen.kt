package com.example.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DailyTask
import com.example.data.models.PriorityLevel
import com.example.data.models.RoutineActivity
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.components.LinearTargetProgress
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.dailyTasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, PENDING, COMPLETED

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val completionPercentage = if (totalCount > 0) (completedCount * 100) / totalCount else 100

    val filteredTasks = remember(tasks, selectedFilter) {
        when (selectedFilter) {
            "PENDING" -> tasks.filter { !it.isCompleted }
            "COMPLETED" -> tasks.filter { it.isCompleted }
            else -> tasks
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header with Task Percentage & FAB
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.getString("tasks"),
                        color = CommanderTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily Completion: $completionPercentage%",
                        color = CommanderGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = CommanderRed,
                    contentColor = Color.White,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("add_task_fab"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(20.dp))
                }
            }
        }

        // 2. Progress Overview Card
        item {
            CommanderCard {
                LinearTargetProgress(
                    label = "Task Completion Progress",
                    current = completedCount.toFloat(),
                    target = if (totalCount > 0) totalCount.toFloat() else 1f,
                    unit = "Tasks",
                    color = CommanderGold
                )
            }
        }

        // 3. Filter tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilterTab("All (${tasks.size})", selectedFilter == "ALL") { selectedFilter = "ALL" }
                TaskFilterTab("Pending (${tasks.size - completedCount})", selectedFilter == "PENDING") { selectedFilter = "PENDING" }
                TaskFilterTab("Completed ($completedCount)", selectedFilter == "COMPLETED") { selectedFilter = "COMPLETED" }
            }
        }

        // 4. Tasks List
        if (filteredTasks.isEmpty()) {
            item {
                CommanderCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tasks.isEmpty()) "No tasks created yet. Tap '+' to create your first task." else "No tasks in this filter.",
                            color = CommanderTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                TaskItemCard(
                    task = task,
                    onToggle = { viewModel.toggleTaskComplete(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, priority, deadlineMin ->
                viewModel.addTask(title, desc, priority, deadlineMin)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TaskFilterTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CommanderGold else CommanderCardSurface)
            .border(1.dp, if (isSelected) CommanderGold else CommanderBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) CommanderBlack else CommanderTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TaskItemCard(
    task: DailyTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        PriorityLevel.HIGH -> CommanderRed
        PriorityLevel.MEDIUM -> CommanderGold
        PriorityLevel.LOW -> CommanderTextSecondary
    }

    CommanderCard(
        borderColor = if (task.isCompleted) CommanderSuccess.copy(alpha = 0.5f) else CommanderBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = CommanderSuccess,
                    uncheckedColor = CommanderBorder,
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(priorityColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.priority.name,
                            color = priorityColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (task.deadlineMinutes >= 0) {
                        Text(
                            text = "Due: ${RoutineActivity.formatMinutesToTime(task.deadlineMinutes)}",
                            color = CommanderGold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.title,
                    color = if (task.isCompleted) CommanderTextMuted else CommanderTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        color = CommanderTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = CommanderTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, PriorityLevel, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(PriorityLevel.MEDIUM) }
    var deadlineMinutes by remember { mutableStateOf(1020) } // 5:00 PM default
    var hasDeadline by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        textContentColor = CommanderTextPrimary,
        title = { Text("New Daily Task") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Priority Level", color = CommanderTextSecondary, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityLevel.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Set Deadline", color = CommanderTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = hasDeadline,
                        onCheckedChange = { hasDeadline = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CommanderGold, checkedTrackColor = CommanderGoldDark)
                    )
                }

                if (hasDeadline) {
                    Text("Deadline: ${RoutineActivity.formatMinutesToTime(deadlineMinutes)}", color = CommanderGold, fontSize = 12.sp)
                    Slider(
                        value = deadlineMinutes.toFloat(),
                        onValueChange = { deadlineMinutes = (it / 15).toInt() * 15 },
                        valueRange = 0f..1425f,
                        colors = SliderDefaults.colors(thumbColor = CommanderGold, activeTrackColor = CommanderGold)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, desc, priority, if (hasDeadline) deadlineMinutes else -1)
                    }
                }
            ) {
                Text("Add Task", color = CommanderGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommanderTextSecondary)
            }
        }
    )
}
