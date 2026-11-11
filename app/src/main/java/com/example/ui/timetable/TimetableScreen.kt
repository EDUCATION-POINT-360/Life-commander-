package com.example.ui.timetable

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ActivityCategory
import com.example.data.models.PriorityLevel
import com.example.data.models.RoutineActivity
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.components.CommanderCard
import com.example.ui.components.LuxuryButton
import com.example.ui.theme.*

@Composable
fun TimetableScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val activities by viewModel.routineActivities.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf<ActivityCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var activityToEdit by remember { mutableStateOf<RoutineActivity?>(null) }
    var activityToSkip by remember { mutableStateOf<RoutineActivity?>(null) }
    var activityToReschedule by remember { mutableStateOf<RoutineActivity?>(null) }
    var showResetConfirm by remember { mutableStateOf(false) }

    val filteredActivities = remember(activities, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) activities
        else activities.filter { it.category == selectedCategoryFilter }
    }

    val totalMinutes = remember(activities) {
        activities.sumOf { it.durationMinutes }
    }
    val totalHours = totalMinutes / 60.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CommanderBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Title, Total Hours & Action Buttons Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.getString("daily_timetable"),
                        color = CommanderTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total Scheduled: ${String.format("%.1f", totalHours)} / 24.0 Hours",
                        color = if (totalHours > 24.0) CommanderRed else CommanderGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CommanderCardSurface)
                            .border(1.dp, CommanderBorder, CircleShape)
                            .testTag("reset_routine_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Routine",
                            tint = CommanderTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = CommanderRed,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("add_activity_fab"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Activity", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // 2. Category Filters
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                item {
                    CategoryFilterChip(
                        title = "All",
                        isSelected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null }
                    )
                }
                items(ActivityCategory.values()) { cat ->
                    CategoryFilterChip(
                        title = cat.name.replace("_", " "),
                        isSelected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat }
                    )
                }
            }
        }

        // 3. Timetable List
        if (filteredActivities.isEmpty()) {
            item {
                CommanderCard {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No activities in this category.",
                            color = CommanderTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredActivities, key = { it.id }) { activity ->
                TimetableActivityCard(
                    activity = activity,
                    language = language,
                    onToggleComplete = { completed ->
                        viewModel.markActivityComplete(activity, completed)
                    },
                    onSkip = { activityToSkip = activity },
                    onReschedule = { activityToReschedule = activity },
                    onEdit = { activityToEdit = activity },
                    onDelete = { viewModel.deleteActivity(activity) }
                )
            }
        }
    }

    // Dialogs
    if (showAddDialog) {
        ActivityEditDialog(
            activity = null,
            onDismiss = { showAddDialog = false },
            onSave = { newActivity ->
                viewModel.saveActivity(newActivity)
                showAddDialog = false
            }
        )
    }

    if (activityToEdit != null) {
        ActivityEditDialog(
            activity = activityToEdit,
            onDismiss = { activityToEdit = null },
            onSave = { updated ->
                viewModel.saveActivity(updated)
                activityToEdit = null
            }
        )
    }

    if (activityToSkip != null) {
        SkipActivityDialog(
            activity = activityToSkip!!,
            onDismiss = { activityToSkip = null },
            onConfirmSkip = { reason ->
                viewModel.skipActivity(activityToSkip!!, reason)
                activityToSkip = null
            }
        )
    }

    if (activityToReschedule != null) {
        RescheduleDialog(
            activity = activityToReschedule!!,
            onDismiss = { activityToReschedule = null },
            onConfirmReschedule = { start, end ->
                viewModel.rescheduleActivity(activityToReschedule!!, start, end)
                activityToReschedule = null
            }
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = CommanderCardSurface,
            titleContentColor = CommanderGold,
            textContentColor = CommanderTextPrimary,
            title = { Text("Reset to Default 24h Routine?") },
            text = { Text("This will restore the recommended master schedule (6h study, 7h wife time, 7h sleep, 1h exercise, 5 prayers).") },
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
private fun CategoryFilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) CommanderGold else CommanderCardSurface
    val text = if (isSelected) CommanderBlack else CommanderTextSecondary
    val border = if (isSelected) CommanderGold else CommanderBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun TimetableActivityCard(
    activity: RoutineActivity,
    language: AppLanguage,
    onToggleComplete: (Boolean) -> Unit,
    onSkip: () -> Unit,
    onReschedule: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isUrdu = language == AppLanguage.URDU
    val titleText = if (isUrdu && activity.titleUrdu.isNotEmpty()) activity.titleUrdu else activity.title

    val categoryColor = when (activity.category) {
        ActivityCategory.STUDY -> CommanderRed
        ActivityCategory.WIFE_TIME -> CommanderGold
        ActivityCategory.SLEEP -> CommanderPurple
        ActivityCategory.EXERCISE -> CommanderSuccess
        ActivityCategory.PRAYER -> CommanderInfo
        ActivityCategory.MEALS -> Color(0xFFFF9800)
        else -> CommanderTextMuted
    }

    CommanderCard(
        borderColor = if (activity.isCompletedToday) CommanderSuccess.copy(alpha = 0.6f)
        else if (activity.isSkippedToday) CommanderRed.copy(alpha = 0.4f)
        else CommanderBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for completion
            Checkbox(
                checked = activity.isCompletedToday,
                onCheckedChange = { onToggleComplete(it) },
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
                    // Category pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = activity.category.name.replace("_", " "),
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (activity.priority == PriorityLevel.HIGH) {
                        Text(
                            text = "HIGH",
                            color = CommanderRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = titleText,
                    color = if (activity.isCompletedToday) CommanderTextMuted else CommanderTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = activity.formattedTimeRange(),
                        color = CommanderGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "• ${activity.durationMinutes} mins",
                        color = CommanderTextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (activity.isSkippedToday && activity.skipReason.isNotEmpty()) {
                    Text(
                        text = "Skipped: ${activity.skipReason}",
                        color = CommanderRed,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Menu
            var menuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options", tint = CommanderTextSecondary)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(CommanderCardSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit Activity", color = CommanderTextPrimary) },
                        onClick = { menuExpanded = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = CommanderGold) }
                    )
                    DropdownMenuItem(
                        text = { Text("Reschedule", color = CommanderTextPrimary) },
                        onClick = { menuExpanded = false; onReschedule() },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = CommanderInfo) }
                    )
                    DropdownMenuItem(
                        text = { Text("Skip Activity", color = CommanderTextPrimary) },
                        onClick = { menuExpanded = false; onSkip() },
                        leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, tint = CommanderWarning) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = CommanderRed) },
                        onClick = { menuExpanded = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CommanderRed) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityEditDialog(
    activity: RoutineActivity?,
    onDismiss: () -> Unit,
    onSave: (RoutineActivity) -> Unit
) {
    var title by remember { mutableStateOf(activity?.title ?: "") }
    var titleUrdu by remember { mutableStateOf(activity?.titleUrdu ?: "") }
    var startMinutes by remember { mutableStateOf(activity?.startTimeMinutes ?: 420) }
    var endMinutes by remember { mutableStateOf(activity?.endTimeMinutes ?: 540) }
    var category by remember { mutableStateOf(activity?.category ?: ActivityCategory.STUDY) }
    var priority by remember { mutableStateOf(activity?.priority ?: PriorityLevel.MEDIUM) }
    var notes by remember { mutableStateOf(activity?.notes ?: "") }
    var reminderEnabled by remember { mutableStateOf(activity?.reminderEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        textContentColor = CommanderTextPrimary,
        title = { Text(if (activity == null) "Add Routine Activity" else "Edit Activity") },
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
                    label = { Text("Activity Title (English)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = titleUrdu,
                    onValueChange = { titleUrdu = it },
                    label = { Text("Activity Title (اردو - اختیاری)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                Text("Category", color = CommanderTextSecondary, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ActivityCategory.values()) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.name.replace("_", " "), fontSize = 11.sp) }
                        )
                    }
                }

                // Time Pickers (Simple hour selection)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Start: ${RoutineActivity.formatMinutesToTime(startMinutes)}", color = CommanderGold, fontSize = 12.sp)
                        Slider(
                            value = startMinutes.toFloat(),
                            onValueChange = { startMinutes = (it / 15).toInt() * 15 },
                            valueRange = 0f..1425f,
                            colors = SliderDefaults.colors(thumbColor = CommanderGold, activeTrackColor = CommanderGold)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End: ${RoutineActivity.formatMinutesToTime(endMinutes)}", color = CommanderGold, fontSize = 12.sp)
                        Slider(
                            value = endMinutes.toFloat(),
                            onValueChange = { endMinutes = (it / 15).toInt() * 15 },
                            valueRange = 0f..1425f,
                            colors = SliderDefaults.colors(thumbColor = CommanderRed, activeTrackColor = CommanderRed)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Remind me before start", color = CommanderTextPrimary, fontSize = 13.sp)
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CommanderGold, checkedTrackColor = CommanderGoldDark)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val updated = activity?.copy(
                            title = title,
                            titleUrdu = titleUrdu,
                            startTimeMinutes = startMinutes,
                            endTimeMinutes = endMinutes,
                            category = category,
                            priority = priority,
                            notes = notes,
                            reminderEnabled = reminderEnabled
                        ) ?: RoutineActivity(
                            title = title,
                            titleUrdu = titleUrdu,
                            startTimeMinutes = startMinutes,
                            endTimeMinutes = endMinutes,
                            category = category,
                            priority = priority,
                            notes = notes,
                            reminderEnabled = reminderEnabled
                        )
                        onSave(updated)
                    }
                }
            ) {
                Text("Save", color = CommanderGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommanderTextSecondary)
            }
        }
    )
}

@Composable
private fun SkipActivityDialog(
    activity: RoutineActivity,
    onDismiss: () -> Unit,
    onConfirmSkip: (String) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        textContentColor = CommanderTextPrimary,
        title = { Text("Skip Activity: ${activity.title}") },
        text = {
            Column {
                Text("State reason for skipping to log in daily accountability:", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("e.g. Urgent errand, medical need") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CommanderGold,
                        unfocusedBorderColor = CommanderBorder,
                        focusedTextColor = CommanderTextPrimary,
                        unfocusedTextColor = CommanderTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmSkip(reason) }) {
                Text("Confirm Skip", color = CommanderWarning, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommanderTextSecondary)
            }
        }
    )
}

@Composable
private fun RescheduleDialog(
    activity: RoutineActivity,
    onDismiss: () -> Unit,
    onConfirmReschedule: (Int, Int) -> Unit
) {
    var newStart by remember { mutableStateOf(activity.startTimeMinutes) }
    var newEnd by remember { mutableStateOf(activity.endTimeMinutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        textContentColor = CommanderTextPrimary,
        title = { Text("Reschedule ${activity.title}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select new start and end time:", fontSize = 13.sp)
                Text("New Start: ${RoutineActivity.formatMinutesToTime(newStart)}", color = CommanderGold, fontSize = 13.sp)
                Slider(
                    value = newStart.toFloat(),
                    onValueChange = { newStart = (it / 15).toInt() * 15 },
                    valueRange = 0f..1425f,
                    colors = SliderDefaults.colors(thumbColor = CommanderGold, activeTrackColor = CommanderGold)
                )
                Text("New End: ${RoutineActivity.formatMinutesToTime(newEnd)}", color = CommanderGold, fontSize = 13.sp)
                Slider(
                    value = newEnd.toFloat(),
                    onValueChange = { newEnd = (it / 15).toInt() * 15 },
                    valueRange = 0f..1425f,
                    colors = SliderDefaults.colors(thumbColor = CommanderRed, activeTrackColor = CommanderRed)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmReschedule(newStart, newEnd) }) {
                Text("Reschedule", color = CommanderGold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CommanderTextSecondary)
            }
        }
    )
}
