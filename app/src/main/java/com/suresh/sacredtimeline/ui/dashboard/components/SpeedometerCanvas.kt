package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SpeedometerCanvas(
    score: Float, // 0f to 100f
    modifier: Modifier = Modifier,
    arcColor: Color = Color(0xFF4CAF50),
    trackColor: Color = Color.LightGray,
    strokeWidth: Dp = 12.dp
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = (width.coerceAtMost(height * 2f)) / 2f - strokeWidth.toPx()
        val center = Offset(width / 2f, height)

        // Draw background arc (180 degrees from 180 to 360 degrees)
        drawArc(
            color = trackColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )

        // Draw progress arc
        val sweepAngle = (score.coerceIn(0f, 100f) / 100f) * 180f
        drawArc(
            color = arcColor,
            startAngle = 180f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}
