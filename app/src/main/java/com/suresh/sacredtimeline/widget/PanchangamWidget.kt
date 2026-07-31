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
import com.suresh.sacredtimeline.logic.MockPanchangamProvider
import com.suresh.sacredtimeline.logic.SunriseSunsetProvider
import com.suresh.sacredtimeline.model.Auspiciousness
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
                    nallaLabel = timings.nallaNeram?.let { "Active" } ?: "None",
                    nallaAusp = timings.nallaNeram?.auspiciousness,
                    gowriLabel = timings.gowriNeram?.name ?: "None",
                    gowriAusp = timings.gowriNeram?.auspiciousness,
                    horaLabel = timings.hora?.name ?: "None",
                    horaAusp = timings.hora?.auspiciousness,
                    specialLabel = timings.specialPeriod?.name ?: "None",
                    specialAusp = timings.specialPeriod?.auspiciousness
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        nallaLabel: String, nallaAusp: Auspiciousness?,
        gowriLabel: String, gowriAusp: Auspiciousness?,
        horaLabel: String, horaAusp: Auspiciousness?,
        specialLabel: String, specialAusp: Auspiciousness?
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimingColumn("Nalla", nallaLabel, nallaAusp, GlanceModifier.defaultWeight())
            TimingColumn("Gowri", gowriLabel, gowriAusp, GlanceModifier.defaultWeight())
            TimingColumn("Hora", horaLabel, horaAusp, GlanceModifier.defaultWeight())
            TimingColumn("Special", specialLabel, specialAusp, GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun TimingColumn(
        title: String,
        label: String,
        auspiciousness: Auspiciousness?,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val backgroundColor = when (auspiciousness) {
            Auspiciousness.GREEN -> AuspiciousGreen
            Auspiciousness.BLUE -> AuspiciousBlue
            Auspiciousness.RED -> InauspiciousRed
            Auspiciousness.AMBER -> CautionAmber
            Auspiciousness.DARK_RED -> RahuRed
            Auspiciousness.ORANGE -> YamaOrange
            Auspiciousness.GREY -> KuligaiGrey
            null -> Color.Gray
        }

        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(4.dp)
                .cornerRadius(8.dp)
                .background(backgroundColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color.White)
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = ColorProvider(Color.White)
                )
            )
        }
    }
}
