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
import androidx.glance.appwidget.cornerRadius
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

class PowerMeterWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(110.dp, 50.dp),
            DpSize(180.dp, 80.dp),
            DpSize(260.dp, 120.dp)
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
        val backgroundOpacity = repository.widgetBackgroundOpacity.first()

        provideContent {
            GlanceTheme {
                PowerMeterWidgetContent(scoreResult, backgroundOpacity)
            }
        }
    }

    @Composable
    private fun PowerMeterWidgetContent(scoreResult: com.suresh.sacredtimeline.logic.ScoreResult, backgroundOpacity: Float) {
        val size = LocalSize.current
        val widthDp = size.width
        val heightDp = size.height

        val scoreColor = when {
            scoreResult.totalScore >= 75f -> Color(0xFF4CAF50)
            scoreResult.totalScore >= 50f -> Color(0xFFFF9800)
            else -> Color(0xFFE53935)
        }
        val badgeColor = SpeedometerBitmapUtils.getScoreSegmentColor(scoreResult.totalScore)

        val imageWidth = (widthDp.value * 0.35f).coerceIn(40f, 120f).dp
        val imageHeight = (imageWidth * 0.5f)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(badgeColor.copy(alpha = backgroundOpacity)) // Configurable via Settings
                .cornerRadius(16.dp)
                .padding(8.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.Horizontal.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val bitmap = SpeedometerBitmapUtils.createSpeedometerBitmap(scoreResult.totalScore)
                    Image(
                        provider = ImageProvider(bitmap),
                        contentDescription = null,
                        modifier = GlanceModifier.size(imageWidth, imageHeight)
                    )
                    Spacer(modifier = GlanceModifier.height(1.dp))
                    Text(
                        text = "${scoreResult.totalScore.toInt()}%",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 140) 9.sp else 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(scoreColor)
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.width(8.dp))
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Power Meter",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 140) 8.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color.DarkGray)
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(1.dp))
                    Text(
                        text = scoreResult.bandTitle,
                        style = TextStyle(
                            fontSize = if (widthDp.value < 140) 10.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(Color.Black)
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
