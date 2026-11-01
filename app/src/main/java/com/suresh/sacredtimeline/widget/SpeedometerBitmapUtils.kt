package com.suresh.sacredtimeline.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

object SpeedometerBitmapUtils {
    fun getScoreSegmentColor(score: Float): androidx.compose.ui.graphics.Color {
        val seg = ((score.coerceIn(0f, 99.99f) / 10f)).toInt().coerceIn(0, 9)
        val hexColors = listOf(
            0xFFD32F2F, 0xFFE53935, 0xFFFF5722, 0xFFFF9800, 0xFFFFC107,
            0xFFFFEB3B, 0xFFCDDC39, 0xFF8BC34A, 0xFF4CAF50, 0xFF2E7D32
        )
        return androidx.compose.ui.graphics.Color(hexColors[seg])
    }

    fun createSpeedometerBitmap(score: Float, width: Int = 360, height: Int = 220): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val arcStrokeWidth = 34f
        // Generous padding so the top stroke and arc have plenty of headroom and never get clipped
        val padding = 32f
        val radius = (width / 2f) - (arcStrokeWidth / 2f) - padding
        val centerX = width / 2f
        val centerY = height.toFloat() - padding

        val rectF = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = arcStrokeWidth
            strokeCap = Paint.Cap.BUTT
        }

        val colors = intArrayOf(
            android.graphics.Color.parseColor("#D32F2F"),
            android.graphics.Color.parseColor("#E53935"),
            android.graphics.Color.parseColor("#FF5722"),
            android.graphics.Color.parseColor("#FF9800"),
            android.graphics.Color.parseColor("#FFC107"),
            android.graphics.Color.parseColor("#FFEB3B"),
            android.graphics.Color.parseColor("#CDDC39"),
            android.graphics.Color.parseColor("#8BC34A"),
            android.graphics.Color.parseColor("#4CAF50"),
            android.graphics.Color.parseColor("#2E7D32")
        )

        val segAngle = 18f
        for (i in 0 until 10) {
            paint.color = colors[i]
            val startAngle = 180f + (i * segAngle)
            canvas.drawArc(rectF, startAngle, segAngle, false, paint)
        }

        // Draw needle with white outline for visibility on dark backgrounds
        val angleDeg = 180f + (score.coerceIn(0f, 100f) / 100f) * 180f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val needleLength = radius * 0.85f
        val tipX = centerX + (needleLength * cos(angleRad)).toFloat()
        val tipY = centerY + (needleLength * sin(angleRad)).toFloat()

        // White outline stroke
        val outlinePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            strokeWidth = 12f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(centerX, centerY, tipX, tipY, outlinePaint)

        // Black core needle stroke
        val needlePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            strokeWidth = 6f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(centerX, centerY, tipX, tipY, needlePaint)

        // White pin outline
        val pinOutlinePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 10f, pinOutlinePaint)

        // Black center pin
        val pinPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 6f, pinPaint)

        return bitmap
    }
}
