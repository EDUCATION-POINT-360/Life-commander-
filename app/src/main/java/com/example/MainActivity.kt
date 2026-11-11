package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.ai.AiCoachScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.focus.FocusScreen
import com.example.ui.habits.HabitTrackerScreen
import com.example.ui.prayers.PrayerTrackerScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.reports.ReportsScreen
import com.example.ui.tasks.TasksScreen
import com.example.ui.theme.*
import com.example.ui.timetable.TimetableScreen
import com.example.ui.voice.VoiceAssistantDialog
import com.example.voice.CommanderVoiceManager

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var voiceManager: CommanderVoiceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        voiceManager = CommanderVoiceManager(
            context = this,
            onCommandRecognized = { cmd ->
                viewModel.handleVoiceCommand(cmd)
            },
            onError = { err ->
                // Handled gracefully in UI
            }
        )
        viewModel.initializeVoiceManager(voiceManager!!)

        setContent {
            val language by viewModel.language.collectAsState()
            val layoutDirection = if (language == AppLanguage.URDU) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                LifeCommanderTheme {
                    MainAppScaffold(viewModel = viewModel)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceManager?.destroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.language.collectAsState()
    var showVoiceDialog by remember { mutableStateOf(false) }

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        viewModel.navigateBack()
    }

    // Permission request launcher for notifications & audio
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.add(Manifest.permission.RECORD_AUDIO)
        permissionLauncher.launch(permissions.toTypedArray())
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CommanderBlack,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = viewModel.getString("app_name"),
                            color = CommanderGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = viewModel.getString("app_subtitle"),
                            color = CommanderTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                },
                actions = {
                    // Quick Voice Command Trigger
                    IconButton(
                        onClick = { showVoiceDialog = true },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CommanderDarkCharcoal)
                            .border(1.dp, CommanderGold.copy(alpha = 0.5f), CircleShape)
                            .testTag("top_voice_button")
                    ) {
                        Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Assistant", tint = CommanderGold, modifier = Modifier.size(20.dp))
                    }

                    // Quick AI Coach Trigger
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.AI_COACH) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CommanderDarkCharcoal)
                            .border(1.dp, CommanderRed.copy(alpha = 0.5f), CircleShape)
                            .testTag("top_ai_button")
                    ) {
                        Icon(imageVector = Icons.Default.SmartToy, contentDescription = "AI Coach", tint = CommanderRed, modifier = Modifier.size(20.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CommanderDarkCharcoal,
                    titleContentColor = CommanderGold
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CommanderDarkCharcoal,
                contentColor = CommanderTextPrimary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationItem(
                    title = viewModel.getString("dashboard"),
                    icon = Icons.Default.Dashboard,
                    isSelected = currentScreen == Screen.DASHBOARD,
                    onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                    testTag = "nav_dashboard"
                )
                NavigationItem(
                    title = viewModel.getString("timetable"),
                    icon = Icons.Default.Schedule,
                    isSelected = currentScreen == Screen.TIMETABLE,
                    onClick = { viewModel.navigateTo(Screen.TIMETABLE) },
                    testTag = "nav_timetable"
                )
                NavigationItem(
                    title = viewModel.getString("tasks"),
                    icon = Icons.Default.CheckCircle,
                    isSelected = currentScreen == Screen.TASKS,
                    onClick = { viewModel.navigateTo(Screen.TASKS) },
                    testTag = "nav_tasks"
                )
                NavigationItem(
                    title = viewModel.getString("focus"),
                    icon = Icons.Default.Timer,
                    isSelected = currentScreen == Screen.FOCUS,
                    onClick = { viewModel.navigateTo(Screen.FOCUS) },
                    testTag = "nav_focus"
                )
                NavigationItem(
                    title = viewModel.getString("profile"),
                    icon = Icons.Default.Person,
                    isSelected = currentScreen == Screen.PROFILE,
                    onClick = { viewModel.navigateTo(Screen.PROFILE) },
                    testTag = "nav_profile"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                Screen.TIMETABLE -> TimetableScreen(viewModel = viewModel)
                Screen.TASKS -> TasksScreen(viewModel = viewModel)
                Screen.FOCUS -> FocusScreen(viewModel = viewModel)
                Screen.PROFILE -> ProfileScreen(viewModel = viewModel)
                Screen.PRAYERS -> PrayerTrackerScreen(viewModel = viewModel)
                Screen.AI_COACH -> AiCoachScreen(viewModel = viewModel)
                Screen.REPORTS -> ReportsScreen(viewModel = viewModel)
                Screen.HABITS -> HabitTrackerScreen(viewModel = viewModel)
            }
        }
    }

    if (showVoiceDialog) {
        VoiceAssistantDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceDialog = false }
        )
    }
}

@Composable
private fun RowScope.NavigationItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) CommanderGold else CommanderTextSecondary
            )
        },
        label = {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CommanderGold else CommanderTextSecondary
            )
        },
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = CommanderRed.copy(alpha = 0.25f),
            selectedIconColor = CommanderGold,
            unselectedIconColor = CommanderTextSecondary
        ),
        modifier = Modifier.testTag(testTag)
    )
}
