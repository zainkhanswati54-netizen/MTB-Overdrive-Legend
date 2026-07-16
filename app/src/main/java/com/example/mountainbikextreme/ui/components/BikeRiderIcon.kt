package com.example.mountainbikextreme.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Minimal downhill-bike-rider silhouette drawn with primitive shapes
 * (two wheels + frame + rider), matching the simple flat icon style
 * used on the continue screen.
 */
@Composable
fun BikeRiderIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val wheelRadius = w * 0.16f
        val rearWheel = Offset(w * 0.22f, h * 0.78f)
        val frontWheel = Offset(w * 0.82f, h * 0.78f)

        val stroke = Stroke(width = w * 0.045f)

        // Wheels
        drawCircle(color = color, radius = wheelRadius, center = rearWheel, style = stroke)
        drawCircle(color = color, radius = wheelRadius, center = frontWheel, style = stroke)

        // Frame (simplified triangle chassis)
        val seat = Offset(w * 0.40f, h * 0.48f)
        val pedals = Offset(w * 0.50f, h * 0.68f)
        val handle = Offset(w * 0.70f, h * 0.42f)

        drawLine(color, rearWheel, seat, strokeWidth = w * 0.045f)
        drawLine(color, seat, pedals, strokeWidth = w * 0.045f)
        drawLine(color, pedals, rearWheel, strokeWidth = w * 0.045f)
        drawLine(color, pedals, frontWheel, strokeWidth = w * 0.045f)
        drawLine(color, pedals, handle, strokeWidth = w * 0.045f)
        drawLine(color, handle, frontWheel, strokeWidth = w * 0.045f)

        // Rider: head + crouched torso
        val headCenter = Offset(w * 0.60f, h * 0.18f)
        drawCircle(color = color, radius = w * 0.075f, center = headCenter)
        drawLine(color, Offset(headCenter.x - w * 0.02f, headCenter.y + w * 0.07f), seat, strokeWidth = w * 0.045f)
        drawLine(color, seat, handle, strokeWidth = w * 0.03f)
    }
}
