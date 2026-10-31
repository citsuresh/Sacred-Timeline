package com.suresh.sacredtimeline.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

object SpeedometerBitmapUtils {
    fun createSpeedometerBitmap(score: Float, width: Int = 300, height: Int = 160): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val arcStrokeWidth = 24f
        val radius = (width / 2f) - (arcStrokeWidth / 2f)
        val centerX = width / 2f
        val centerY = height.toFloat()

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

        // Draw needle
        val angleDeg = 180f + (score.coerceIn(0f, 100f) / 100f) * 180f
        val angleRad = Math.toRadians(angleDeg.toDouble())
        val needleLength = radius * 0.85f
        val tipX = centerX + (needleLength * cos(angleRad)).toFloat()
        val tipY = centerY + (needleLength * sin(angleRad)).toFloat()

        val needlePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            this.strokeWidth = 4f
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(centerX, centerY, tipX, tipY, needlePaint)

        val pinPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.BLACK
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 6f, pinPaint)

        return bitmap
    }
}
