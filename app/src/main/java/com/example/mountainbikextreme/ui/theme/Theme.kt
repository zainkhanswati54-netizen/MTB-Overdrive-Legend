package com.example.mountainbikextreme.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = ForestLight,
    background = ForestDeep,
    surface = ForestMid,
    onPrimary = AccentWhite,
    onBackground = AccentWhite,
    onSurface = AccentWhite
)

@Composable
fun MountainBikeXtremeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
