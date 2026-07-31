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
import com.suresh.sacredtimeline.model.Auspiciousness
import com.suresh.sacredtimeline.ui.theme.AuspiciousBlue
import com.suresh.sacredtimeline.ui.theme.AuspiciousGreen
import com.suresh.sacredtimeline.ui.theme.CautionAmber
import com.suresh.sacredtimeline.ui.theme.InauspiciousRed
import java.time.LocalDate
import java.time.LocalTime

class PanchangamWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val provider = MockPanchangamProvider()
        val now = LocalTime.now()
        val date = LocalDate.now()
        val (nalla, gowri, hora) = provider.getCurrentTimings(date, now)

        provideContent {
            GlanceTheme {
                WidgetContent(
                    nallaLabel = nalla?.let { "Active" } ?: "None",
                    nallaAusp = nalla?.auspiciousness,
                    gowriLabel = gowri?.name ?: "None",
                    gowriAusp = gowri?.auspiciousness,
                    horaLabel = hora?.name ?: "None",
                    horaAusp = hora?.auspiciousness
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        nallaLabel: String, nallaAusp: Auspiciousness?,
        gowriLabel: String, gowriAusp: Auspiciousness?,
        horaLabel: String, horaAusp: Auspiciousness?
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
