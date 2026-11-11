package com.example.ui.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun VoiceAssistantDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val language by viewModel.language.collectAsState()
    var isListening by remember { mutableStateOf(false) }
    var spokenResult by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("") }

    val voiceManager = remember {
        viewModel.voiceManager
    }

    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val sampleCommands = listOf(
        "Show my timetable",
        "Start study timer",
        "How much study is remaining?",
        "Show today's progress",
        "Show prayer status",
        "Start focus mode"
    )

    AlertDialog(
        onDismissRequest = {
            voiceManager?.stopListening()
            onDismiss()
        },
        containerColor = CommanderDarkCharcoal,
        titleContentColor = CommanderGold,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = CommanderRed, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COMMANDER VOICE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = CommanderTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isListening) "Listening for command..." else "Tap the microphone or speak a command",
                    color = CommanderTextSecondary,
                    fontSize = 12.sp
                )

                // Large Mic Button with animated pulse
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (isListening) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isListening) CommanderRed else CommanderCardSurface)
                        .border(2.dp, if (isListening) CommanderGold else CommanderBorder, CircleShape)
                        .clickable {
                            isListening = !isListening
                            if (isListening) {
                                voiceManager?.startListening(language)
                            } else {
                                voiceManager?.stopListening()
                            }
                        }
                        .testTag("voice_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Speak",
                        tint = if (isListening) Color.White else CommanderGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                if (spokenResult.isNotEmpty()) {
                    Text(
                        text = "Heard: \"$spokenResult\"",
                        color = CommanderGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sample commands row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(sampleCommands) { cmd ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(CommanderCardSurface)
                                .border(1.dp, CommanderBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    spokenResult = cmd
                                    viewModel.handleVoiceCommand(cmd)
                                    onDismiss()
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(cmd, color = CommanderTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                // Text alternative input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Type command...", fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CommanderGold,
                            unfocusedBorderColor = CommanderBorder,
                            focusedTextColor = CommanderTextPrimary,
                            unfocusedTextColor = CommanderTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                val cmd = textInput
                                textInput = ""
                                viewModel.handleVoiceCommand(cmd)
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CommanderRed)
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = CommanderGold)
            }
        }
    )
}
