package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suresh.sacredtimeline.R
import java.time.LocalTime

@Composable
fun BoxScope.NowIndicator(currentTime: LocalTime, hourHeight: Dp, color: Color = Color.Red, thickness: Dp = 2.dp) {
    val topOffset = calculateOffset(currentTime, hourHeight)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = topOffset - (thickness / 2))
            .height(thickness)
            .background(color)
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
            text = stringResource(R.string.label_now_line),
            color = if (color == Color.White) Color.Black else Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}
