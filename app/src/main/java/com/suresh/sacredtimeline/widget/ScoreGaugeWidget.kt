package com.suresh.sacredtimeline.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.DpSize
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.Image
import androidx.glance.ImageProvider
import com.suresh.sacredtimeline.MainActivity
import com.suresh.sacredtimeline.data.BirthProfilesRepository
import com.suresh.sacredtimeline.data.CacheManager
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.logic.DayDataProvider
import com.suresh.sacredtimeline.logic.ScoringEngine
import com.suresh.sacredtimeline.model.DayData
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime

class ScoreGaugeWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(70.dp, 70.dp),
            DpSize(140.dp, 140.dp),
            DpSize(220.dp, 220.dp)
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = SettingsRepository(context)
        val cacheManager = CacheManager(context)
        val now = LocalTime.now()
        val date = LocalDate.now()

        val mode = repository.locationMode.first()
        val lat: Double
        val lng: Double
        if (mode == "AUTO") {
            lat = repository.lastKnownLatitude.first()
            lng = repository.lastKnownLongitude.first()
        } else {
            lat = repository.manualLatitude.first()
            lng = repository.manualLongitude.first()
        }

        val cache = cacheManager.loadCache(lat, lng)
        val dayData: DayData = if (cache?.containsKey(date) == true) {
            cache[date]!!
        } else {
            DayDataProvider(context).fetchDayData(date, lat, lng, usePrimaryProfile = true)
        }

        val primaryProfile = BirthProfilesRepository(context).getPrimaryProfile()
        val scoreResult = ScoringEngine.calculateScore(dayData, primaryProfile, now)

        provideContent {
            GlanceTheme {
                ScoreGaugeWidgetContent(scoreResult)
            }
        }
    }

    @Composable
    private fun ScoreGaugeWidgetContent(scoreResult: com.suresh.sacredtimeline.logic.ScoreResult) {
        val size = LocalSize.current
        val widthDp = size.width
        val heightDp = size.height

        val scoreColor = when {
            scoreResult.totalScore >= 75f -> Color(0xFF4CAF50)
            scoreResult.totalScore >= 50f -> Color(0xFFFF9800)
            else -> Color(0xFFE53935)
        }

        val bitmapWidth = (widthDp.value * 2.5f).toInt().coerceIn(160, 600)
        val bitmapHeight = (heightDp.value * 2.5f).toInt().coerceIn(80, 300)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(4.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (heightDp.value > 85) {
                    Text(
                        text = "⚡ Power",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 120) 9.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(scoreColor)
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                }

                val bitmap = SpeedometerBitmapUtils.createSpeedometerBitmap(scoreResult.totalScore, width = bitmapWidth, height = bitmapHeight)
                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                )

                if (heightDp.value > 65) {
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = if (widthDp.value < 130) "${scoreResult.totalScore.toInt()}%" else "${scoreResult.totalScore.toInt()}% • ${scoreResult.bandTitle}",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 130) 9.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color.White)
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
