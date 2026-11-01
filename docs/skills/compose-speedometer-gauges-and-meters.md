---
name: compose-speedometer-gauges-and-meters
description: Provides guidelines, math formulas, and Jetpack Compose Canvas implementations for building custom semi-circular speedometer gauges, multi-color gradient arc painting, needle pointer trigonometry, gauge sizing parity, and interactive power meter timeline widgets.
---

# Jetpack Compose Speedometer Gauges & Power Meters Skill

## Overview
This skill covers building professional custom Canvas speedometer gauges and interactive power meters in Jetpack Compose. It provides exact radius math for semi-circular arcs, needle rotation trigonometry, and badge sizing parity for timeline dashboards.

---

## 1. Custom Semicircular Canvas (`SpeedometerCanvas.kt`)

### Radius & Coordinate Math
For a semi-circular meter (180° sweep from 180° to 360°), derive the radius from half the canvas width (`size.width / 2`):

```kotlin
@Composable
fun SpeedometerCanvas(
    score: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp,
    backgroundColor: Color = Color.Transparent
) {
    Canvas(modifier = modifier) {
        val strokePx = strokeWidth.toPx()
        if (backgroundColor != Color.Transparent) {
            drawRect(color = backgroundColor)
        }
        
        val colors = listOf(Color(0xFFD32F2F), Color(0xFFFF9800), Color(0xFFFFEB3B), Color(0xFF8BC34A), Color(0xFF4CAF50))
        val sweepAngle = 180f / colors.size
        
        val radius = ((size.width / 2) - strokePx / 2).coerceAtLeast(2f)
        val centerOffset = Offset(size.width / 2, size.height - strokePx / 2)
        
        colors.forEachIndexed { index, color ->
            drawArc(
                color = color,
                startAngle = 180f + (index * sweepAngle),
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        
        // Needle pointer math
        val scoreFraction = (score / 100f).coerceIn(0f, 1f)
        val angle = 180f + (scoreFraction * 180f)
        val radians = Math.toRadians(angle.toDouble())
        val needleEnd = Offset(
            centerOffset.x + (radius * 0.85f * kotlin.math.cos(radians)).toFloat(),
            centerOffset.y + (radius * 0.85f * kotlin.math.sin(radians)).toFloat()
        )
        
        drawLine(
            color = Color.Black,
            start = centerOffset,
            end = needleEnd,
            strokeWidth = (strokePx * 0.35f).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )
        drawCircle(color = Color.Black, radius = (strokePx * 0.4f).coerceAtLeast(2f), center = centerOffset)
    }
}
```

---

## 2. Interactive Components

### Power Meter Row (`PowerMeterRow.kt`)
- Combines summary score badges with quick-action icon buttons (e.g., Find Best Window, Preview, Heatmap).

### Score Breakdown Sheets (`ScoreBreakdownSheet.kt`)
- Center-aligned enlarged speedometer canvas (`220x110.dp`, `strokeWidth = 14.dp`), score percentage, qualitative band, and itemized factor contributor list.

### Timeline Gauge Badges (`SunIndicators.kt`)
- Circular badge surfaces (`64.dp`) with embedded speedometer canvases (`56x28.dp`, `strokeWidth = 11.dp`) supporting tap-to-inspect and drag interactions.
