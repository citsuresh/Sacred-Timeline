package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import kotlin.math.abs
import kotlin.math.cos

@Composable
fun MoonPhaseIcon(
    tithi: Int, 
    modifier: Modifier = Modifier, 
    lightColor: Color = Color.White, 
    darkColor: Color = Color.Black,
    strokeColor: Color = Color.Black
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2, size.height / 2)
        val rect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)

        // 1. Base Dark Circle
        drawCircle(color = darkColor, radius = radius, center = center)

        val t = tithi % 30
        if (t == 0) {
            drawCircle(color = strokeColor, radius = radius, center = center, style = Stroke(width = 0.5.dp.toPx()))
            return@Canvas
        }

        // 2. Draw the illuminated part
        val phi = (t * 12.0)
        val cosPhi = cos(Math.toRadians(phi)).toFloat()
        val ellipseWidth = abs(cosPhi) * radius * 2f
        val ellipseRect = Rect(center.x - ellipseWidth / 2f, center.y - radius, center.x + ellipseWidth / 2f, center.y + radius)

        val path = Path()
        if (t <= 15) {
            // Waxing (Valarpirai): Light is on the right. 
            // Edge is the right semi-circle.
            path.arcTo(rect, -90f, 180f, true) 
            if (cosPhi >= 0) {
                // Crescent: Terminator is on the right side (concave)
                path.arcTo(ellipseRect, 90f, -180f, false)
            } else {
                // Gibbous: Terminator is on the left side (convex)
                path.arcTo(ellipseRect, 90f, 180f, false)
            }
        } else {
            // Waning (Theipirai): Light is on the left.
            // Edge is the left semi-circle.
            path.arcTo(rect, 90f, 180f, true)
            if (cosPhi <= 0) {
                // Gibbous: Terminator is on the right side (convex)
                path.arcTo(ellipseRect, -90f, 180f, false)
            } else {
                // Crescent: Terminator is on the left side (concave)
                path.arcTo(ellipseRect, 270f, -180f, false)
            }
        }
        path.close()
        drawPath(path, lightColor)

        // 3. Outline
        drawCircle(color = strokeColor, radius = radius, center = center, style = Stroke(width = 0.5.dp.toPx()))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun MoonPhaseGalleryPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Valar Pirai (1-15)", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            (1..15).forEach { t ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                    MoonPhaseIcon(tithi = t, modifier = Modifier.size(64.dp))
                    Text(t.toString(), fontSize = 10.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Problematic Tithis (Testing)", fontWeight = FontWeight.Bold)
        Row {
            listOf(10, 13, 14, 17, 18).forEach { t ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                    MoonPhaseIcon(tithi = t, modifier = Modifier.size(64.dp))
                    Text(t.toString(), fontSize = 10.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Theipirai (16-30)", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            (16..30).forEach { t ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
                    MoonPhaseIcon(tithi = t, modifier = Modifier.size(64.dp))
                    Text(t.toString(), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun KalashIcon(modifier: Modifier = Modifier, color: Color = Color.Unspecified) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        
        val potPath = Path().apply {
            moveTo(w * 0.5f, h * 0.95f)
            cubicTo(w * 0.2f, h * 0.95f, w * 0.15f, h * 0.7f, w * 0.15f, h * 0.6f)
            cubicTo(w * 0.15f, h * 0.5f, w * 0.35f, h * 0.45f, w * 0.35f, h * 0.42f)
            lineTo(w * 0.65f, h * 0.42f)
            cubicTo(w * 0.65f, h * 0.45f, w * 0.85f, h * 0.5f, w * 0.85f, h * 0.6f)
            cubicTo(w * 0.85f, h * 0.7f, w * 0.80f, h * 0.95f, w * 0.5f, h * 0.95f)
            close()
        }
        
        val rimPath = Path().apply {
            addRect(Rect(w * 0.3f, h * 0.38f, w * 0.7f, h * 0.43f))
        }
        
        val coconutPath = Path().apply {
            moveTo(w * 0.4f, h * 0.38f)
            cubicTo(w * 0.4f, h * 0.2f, w * 0.5f, h * 0.05f, w * 0.5f, h * 0.05f)
            cubicTo(w * 0.5f, h * 0.05f, w * 0.6f, h * 0.2f, w * 0.6f, h * 0.38f)
            close()
        }
        
        val leavesPath = Path().apply {
            moveTo(w * 0.35f, h * 0.38f)
            lineTo(w * 0.15f, h * 0.25f)
            lineTo(w * 0.4f, h * 0.35f)
            moveTo(w * 0.65f, h * 0.38f)
            lineTo(w * 0.85f, h * 0.25f)
            lineTo(w * 0.6f, h * 0.35f)
        }

        drawPath(potPath, color)
        drawPath(rimPath, color)
        drawPath(coconutPath, color)
        drawPath(leavesPath, color, style = Stroke(width = 0.8.dp.toPx()))
    }
}

@Composable
fun NandiIcon(modifier: Modifier = Modifier, isWhite: Boolean) {
    val bgColor = if (isWhite) Color.Black else Color.White
    val iconColor = if (isWhite) Color.White else Color.Black
    
    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_nandi_black),
            contentDescription = null,
            modifier = Modifier.padding(2.dp).fillMaxSize(),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
        )
    }
}

@Composable
fun ShivaIcon(modifier: Modifier = Modifier, isWhite: Boolean) {
    val bgColor = if (isWhite) Color.Black else Color.White
    val iconColor = if (isWhite) Color.White else Color.Black
    
    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_shiva),
            contentDescription = null,
            modifier = Modifier.padding(2.dp).fillMaxSize(),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
        )
    }
}

@Composable
fun GaneshaIcon(modifier: Modifier = Modifier, isWhite: Boolean) {
    val bgColor = if (isWhite) Color.Black else Color.White
    val iconColor = if (isWhite) Color.White else Color.Black

    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ganesha_black),
            contentDescription = null,
            modifier = Modifier.padding(2.dp).fillMaxSize(),
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
        )
    }
}

@Composable
fun VelIcon(modifier: Modifier = Modifier, isWhite: Boolean) {
    val bgColor = if (isWhite) Color.Black else Color.White
    val iconColor = if (isWhite) Color.White else Color.Black
    
    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        VelDrawing(
            modifier = Modifier.padding(2.dp).fillMaxSize(),
            lightColor = iconColor,
            darkColor = bgColor,
            strokeColor = iconColor
        )
    }
}

@Composable
private fun VelDrawing(modifier: Modifier = Modifier, lightColor: Color, darkColor: Color, strokeColor: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        
        // 1. Shaft (Pole)
        drawLine(
            color = strokeColor, 
            start = Offset(w * 0.5f, h * 0.5f), 
            end = Offset(w * 0.5f, h * 0.98f), 
            strokeWidth = 2.2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        drawLine(
            color = lightColor, 
            start = Offset(w * 0.5f, h * 0.5f), 
            end = Offset(w * 0.5f, h * 0.98f), 
            strokeWidth = 1.0.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )
        
        // 2. Blade (Traditional leaf-shaped Vel)
        val blade = Path().apply {
            moveTo(w * 0.5f, h * 0.02f)
            cubicTo(w * 0.1f, h * 0.25f, w * 0.1f, h * 0.6f, w * 0.5f, h * 0.85f)
            cubicTo(w * 0.9f, h * 0.6f, w * 0.9f, h * 0.25f, w * 0.5f, h * 0.02f)
            close()
        }
        
        drawPath(blade, lightColor)
        drawPath(blade, strokeColor, style = Stroke(width = 1.2.dp.toPx())) 
        
        // 3. Vibhuti lines
        val vibhutiColor = if (lightColor == Color.White) Color.Black else Color.White
        val lineW = w * 0.18f
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.32f), end = Offset(w * 0.5f + lineW, h * 0.32f), strokeWidth = 0.8.dp.toPx())
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.40f), end = Offset(w * 0.5f + lineW, h * 0.40f), strokeWidth = 0.8.dp.toPx())
        drawLine(vibhutiColor, start = Offset(w * 0.5f - lineW, h * 0.48f), end = Offset(w * 0.5f + lineW, h * 0.48f), strokeWidth = 0.8.dp.toPx())
        
        // 4. Tiny Dot (Kumkum)
        drawCircle(Color.Red, radius = 0.8.dp.toPx(), center = Offset(w * 0.5f, h * 0.40f))
    }
}
