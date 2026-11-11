package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CommanderColorScheme = darkColorScheme(
    primary = CommanderRed,
    onPrimary = Color.White,
    primaryContainer = CommanderRedDark,
    onPrimaryContainer = Color.White,
    secondary = CommanderGold,
    onSecondary = CommanderBlack,
    secondaryContainer = CommanderGoldDark,
    onSecondaryContainer = Color.Black,
    tertiary = CommanderGold,
    onTertiary = Color.Black,
    background = CommanderBlack,
    onBackground = CommanderTextPrimary,
    surface = CommanderDarkCharcoal,
    onSurface = CommanderTextPrimary,
    surfaceVariant = CommanderCardSurface,
    onSurfaceVariant = CommanderTextSecondary,
    outline = CommanderBorder,
    error = CommanderRed,
    onError = Color.White
)

@Composable
fun LifeCommanderTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CommanderColorScheme,
        typography = Typography,
        content = content
    )
}
