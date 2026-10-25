package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScoreHeatmapColumn(
    hourHeight: Dp,
    modifier: Modifier = Modifier
) {
    val totalHeight = hourHeight * 24

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE53935), // Red
            Color(0xFFFF9800), // Orange
            Color(0xFF4CAF50), // Green
            Color(0xFFFF9800),
            Color(0xFFE53935)
        )
    )

    Box(
        modifier = modifier
            .width(16.dp)
            .height(totalHeight)
            .background(brush = gradientBrush, shape = RoundedCornerShape(4.dp))
    )
}
