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

class ScoreGaugeWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(60.dp, 60.dp),
            DpSize(120.dp, 120.dp),
            DpSize(200.dp, 200.dp),
            DpSize(300.dp, 300.dp)
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
        val bgColorInt = repository.scoreGaugeBgColor.first()
        val bgOpacity = repository.scoreGaugeBgOpacity.first()

        provideContent {
            GlanceTheme {
                ScoreGaugeWidgetContent(scoreResult, bgColorInt, bgOpacity)
            }
        }
    }

    @Composable
    private fun ScoreGaugeWidgetContent(
        scoreResult: com.suresh.sacredtimeline.logic.ScoreResult,
        bgColorInt: Int,
        bgOpacity: Float
    ) {
        val size = LocalSize.current
        val widthDp = size.width
        val heightDp = size.height

        val containerBg = if (bgColorInt == 0x00000000) Color.Transparent else Color(bgColorInt).copy(alpha = bgOpacity)

        val bitmap = SpeedometerBitmapUtils.createSpeedometerBitmap(scoreResult.totalScore, width = 400, height = 220)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(containerBg)
                .cornerRadius(16.dp)
                .padding(4.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (heightDp.value > 80) {
                    Text(
                        text = "⚡ Power",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 100) 8.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(SpeedometerBitmapUtils.getScoreSegmentColor(scoreResult.totalScore))
                        )
                    )
                }

                Image(
                    provider = ImageProvider(bitmap),
                    contentDescription = null,
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    contentScale = androidx.glance.layout.ContentScale.Fit
                )

                Spacer(modifier = GlanceModifier.height(1.dp))
                val badgeColor = SpeedometerBitmapUtils.getScoreSegmentColor(scoreResult.totalScore)
                val textColor = if (scoreResult.totalScore in 50f..70f) Color.Black else Color.White
                Box(
                    modifier = GlanceModifier
                        .background(badgeColor)
                        .cornerRadius(10.dp)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (widthDp.value < 110) "${scoreResult.totalScore.toInt()}%" else "${scoreResult.totalScore.toInt()}% • ${scoreResult.bandTitle}",
                        style = TextStyle(
                            fontSize = if (widthDp.value < 110) 12.sp else 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(textColor)
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
