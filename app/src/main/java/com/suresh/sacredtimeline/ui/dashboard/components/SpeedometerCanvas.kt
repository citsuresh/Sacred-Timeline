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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerCanvas(
    score: Float, // 0f to 100f
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 22.dp, // Much thicker arc
    showNeedle: Boolean = true
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        // Radius calculated to fit tightly to the outer edge with zero excess white space
        val radius = (width / 2f) - (strokeWidth.toPx() / 2f)
        val center = Offset(width / 2f, height)

        val rect = Offset(center.x - radius, center.y - radius)
        val arcSize = Size(radius * 2f, radius * 2f)
        val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)

        val segAngle = 18f
        val colors = listOf(
            Color(0xFFD32F2F), // 0-10%
            Color(0xFFE53935), // 10-20%
            Color(0xFFFF5722), // 20-30%
            Color(0xFFFF9800), // 30-40%
            Color(0xFFFFC107), // 40-50%
            Color(0xFFFFEB3B), // 50-60%
            Color(0xFFCDDC39), // 60-70%
            Color(0xFF8BC34A), // 70-80%
            Color(0xFF4CAF50), // 80-90%
            Color(0xFF2E7D32)  // 90-100%
        )

        for (i in 0 until 10) {
            val startAngle = 180f + (i * segAngle)
            drawArc(colors[i], startAngle, segAngle, false, rect, arcSize, style = stroke)
        }

        if (showNeedle) {
            val angleDeg = 180f + (score.coerceIn(0f, 100f) / 100f) * 180f
            val angleRad = Math.toRadians(angleDeg.toDouble())
            
            val needleLength = radius * 0.85f
            val tipX = center.x + (needleLength * cos(angleRad)).toFloat()
            val tipY = center.y + (needleLength * sin(angleRad)).toFloat()

            drawLine(
                color = Color.Black,
                start = center,
                end = Offset(tipX, tipY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = Color.Black,
                radius = 2.5.dp.toPx(),
                center = center
            )
        }
    }
}
