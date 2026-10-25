package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun BoxScope.PreviewIndicator(
    previewTime: LocalTime,
    hourHeight: Dp,
    color: Color = Color(0xFF9C27B0), // Purple preview line
    thickness: Dp = 2.dp,
    onDrag: (Float) -> Unit = {}
) {
    val topOffset = calculateOffset(previewTime, hourHeight)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - (thickness / 2))
            .height(thickness)
            .background(color)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    }
                )
            }
    )

    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .offset(y = topOffset - 10.dp)
            .padding(start = 8.dp)
            .align(Alignment.TopStart)
    ) {
        Text(
            text = "Preview",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
