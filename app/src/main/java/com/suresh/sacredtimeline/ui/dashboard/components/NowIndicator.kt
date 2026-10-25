package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.DayData
import kotlinx.coroutines.delay
import java.time.LocalTime

@Composable
fun BoxScope.NowIndicator(
    hourHeight: Dp,
    dayData: DayData? = null,
    visibleCols: List<String> = emptyList(),
    score: Float = 89f,
    color: Color = Color.Red,
    thickness: Dp = 2.dp,
    onGaugeClick: () -> Unit = {}
) {
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(60000 - (System.currentTimeMillis() % 60000))
        }
    }

    val topOffset = calculateOffset(currentTime, hourHeight)
    val density = LocalDensity.current
    
    var dragOffsetPx by remember { mutableStateOf(0f) }

    LaunchedEffect(currentTime.minute) {
        dragOffsetPx = 0f
    }
    
    val emptyColIndex = if (dayData != null && visibleCols.isNotEmpty()) {
        visibleCols.indexOfFirst { colId ->
            val timings = when (colId) {
                "GOWRI" -> dayData.gowriNeram
                "CHANDRASHTAMAM" -> dayData.chandrashtamam
                "NERAM_MUHURTHAM" -> (dayData.nallaNeram + dayData.specialPeriods + dayData.maitraMuhurtham + listOfNotNull(dayData.brahmaMuhurtham) + listOfNotNull(dayData.abhijitMuhurtham))
                "HORA" -> dayData.hora
                "YOGAM" -> dayData.yogam
                "THARA_BALAM" -> dayData.tharaBalam
                else -> emptyList()
            }
            !timings.any { it.isCurrent(currentTime) }
        }
    } else -1

    val badgeAlignment = if (emptyColIndex >= 0 && visibleCols.isNotEmpty()) {
        val bias = -0.7f + (1.4f * emptyColIndex / (visibleCols.size - 1).coerceAtLeast(1))
        BiasAlignment(bias, -1f)
    } else {
        Alignment.TopCenter
    }

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

    Surface(
        onClick = { onGaugeClick() },
        color = Color.White,
        shape = CircleShape,
        border = BorderStroke(2.5.dp, color),
        shadowElevation = 6.dp,
        modifier = Modifier
            .offset(
                x = with(density) { dragOffsetPx.toDp() },
                y = topOffset - 32.dp
            )
            .size(64.dp)
            .align(badgeAlignment)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    dragOffsetPx += dragAmount
                }
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(2.dp)
            ) {
                SpeedometerCanvas(
                    score = score,
                    modifier = Modifier.size(42.dp, 21.dp),
                    strokeWidth = 6.dp,
                    showNeedle = true
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "${score.toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}
