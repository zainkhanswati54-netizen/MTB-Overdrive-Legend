package com.example.mountainbikextreme.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mountainbikextreme.ui.components.BikeRiderIcon
import com.example.mountainbikextreme.ui.theme.AccentWhite
import com.example.mountainbikextreme.ui.theme.ForestDeep
import com.example.mountainbikextreme.ui.theme.HillsDeep

@Composable
fun ContinueScreen(
    onContinue: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit
) {
    // Gentle pulsing "tap to continue" text, like the reference screenshot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(HillsDeep, ForestDeep)
                )
            )
            .clickable { onContinue() }
    ) {
        // Top-left: Exit
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .clickable { onExit() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Exit",
                tint = AccentWhite.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp)
            )
        }

        // Top-right: Settings
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .clickable { onSettings() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = AccentWhite.copy(alpha = 0.85f),
                modifier = Modifier.size(28.dp)
            )
        }

        // Center content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BikeRiderIcon(
                modifier = Modifier.size(90.dp),
                color = AccentWhite
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "MOUNTAIN BIKE XTREME",
                color = AccentWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Tap to continue",
                color = AccentWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.alpha(pulseAlpha)
            )
        }
    }
}
