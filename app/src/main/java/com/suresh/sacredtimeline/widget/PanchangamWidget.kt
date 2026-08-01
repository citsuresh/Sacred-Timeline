package com.suresh.sacredtimeline.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
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
import com.suresh.sacredtimeline.MainActivity
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.theme.*
import java.time.LocalDate
import java.time.LocalTime

class PanchangamWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val provider = MockPanchangamProvider()
        val sunProvider = SunriseSunsetProvider()
        val now = LocalTime.now()
        val date = LocalDate.now()

        // Use Coimbatore coords for widget default
        val sunTimes = sunProvider.getSunTimes(11.0168, 76.9558, date)
        val timings = provider.getCurrentTimings(date, now, sunTimes.sunrise, sunTimes.sunset)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    nallaNeram = timings.nallaNeram,
                    gowriNeram = timings.gowriNeram,
                    hora = timings.hora,
                    specialPeriod = timings.specialPeriod
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        nallaNeram: NallaNeram?,
        gowriNeram: GowriNeram?,
        hora: Hora?,
        specialPeriod: SpecialPeriod?
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable(actionStartActivity<MainActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First Column: Neram (Nalla or Special)
            val neramTiming = specialPeriod ?: nallaNeram
            val neramLabel = when {
                specialPeriod != null -> specialPeriod.name
                nallaNeram != null -> "Nalla"
                else -> "None"
            }

            TimingColumn("Neram", neramLabel, neramTiming, GlanceModifier.defaultWeight())
            TimingColumn(
                "Gowri",
                gowriNeram?.name ?: "None",
                gowriNeram,
                GlanceModifier.defaultWeight()
            )
            TimingColumn("Hora", hora?.name ?: "None", hora, GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun TimingColumn(
        title: String,
        label: String,
        timing: Timing?,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val backgroundColor = timing?.let { SacredTimelineColors.getTimingColor(it) } ?: Color.Gray
        val contentColor = SacredTimelineColors.getContentColor(backgroundColor)

        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(4.dp)
                .cornerRadius(8.dp)
                .background(CardOuterBorderColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .cornerRadius(7.dp)
                    .background(CardBorderColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .cornerRadius(6.dp)
                        .background(backgroundColor),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(contentColor.copy(alpha = 0.8f))
                        )
                    )
                    Text(
                        text = label,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(contentColor)
                        )
                    )
                    timing?.tamilName?.let { tamil ->
                        Text(
                            text = tamil,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = ColorProvider(contentColor)
                            )
                        )
                    }
                }

                // Compatibility Icon for Hora
                if (timing is Hora) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        val (symbol, color) = when (timing.compatibility) {
                            HoraCompatibility.FAVORABLE -> "✓" to CompatibilityFavorable
                            HoraCompatibility.CONFLICTING -> "✕" to CompatibilityConflicting
                            HoraCompatibility.NEUTRAL -> "○" to CompatibilityNeutral
                        }
                        Box(
                            modifier = GlanceModifier
                                .size(28.dp)
                                .cornerRadius(14.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = symbol,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorProvider(color)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}