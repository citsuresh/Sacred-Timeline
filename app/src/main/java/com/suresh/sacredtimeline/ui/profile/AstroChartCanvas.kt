package com.suresh.sacredtimeline.ui.profile

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.ChartStyle

@Composable
fun AstroChartCanvas(
    planetaryPositions: Map<String, Int>, // Planet name -> Rasi index (1..12, 1=Mesha, etc.)
    chartStyle: ChartStyle,
    modifier: Modifier = Modifier,
    lagnaHouse: Int = 1, // Default 1 (Mesha)
    rasiHouse: Int = 4,  // Default 4 (Cancer / Moon sign)
    title: String = "Rasi Chart (D1)"
) {
    val containerColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor, MaterialTheme.shapes.medium)
            .border(1.dp, outlineColor, MaterialTheme.shapes.medium)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = onSurfaceColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (chartStyle == ChartStyle.SOUTH_INDIAN) {
            SouthIndianChartGrid(
                planetaryPositions = planetaryPositions,
                lagnaHouse = lagnaHouse,
                rasiHouse = rasiHouse,
                outlineColor = outlineColor,
                onSurfaceColor = onSurfaceColor,
                title = title
            )
        } else {
            NorthIndianDiamondChartGrid(
                planetaryPositions = planetaryPositions,
                lagnaHouse = lagnaHouse,
                rasiHouse = rasiHouse,
                outlineColor = outlineColor,
                onSurfaceColor = onSurfaceColor,
                title = title
            )
        }
    }
}

@Composable
fun SouthIndianChartGrid(
    planetaryPositions: Map<String, Int>,
    lagnaHouse: Int,
    rasiHouse: Int,
    outlineColor: Color,
    onSurfaceColor: Color,
    title: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .border(2.dp, outlineColor)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                listOf(12, 1, 2, 3).forEach { rasiIndex ->
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        RasiCell(rasiIndex, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().weight(2f)) {
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) { RasiCell(11, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor) }
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) { RasiCell(10, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor) }
                }
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .border(1.dp, outlineColor)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor.copy(alpha = 0.8f))
                    )
                }
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) { RasiCell(4, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor) }
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) { RasiCell(5, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                listOf(9, 8, 7, 6).forEach { rasiIndex ->
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        RasiCell(rasiIndex, planetaryPositions, lagnaHouse, rasiHouse, outlineColor, onSurfaceColor)
                    }
                }
            }
        }
    }
}

@Composable
fun NorthIndianDiamondChartGrid(
    planetaryPositions: Map<String, Int>,
    lagnaHouse: Int,
    rasiHouse: Int,
    outlineColor: Color,
    onSurfaceColor: Color,
    title: String
) {
    val textMeasurer = rememberTextMeasurer()
    val context = LocalContext.current
    val lagText = stringResource(R.string.badge_lag)
    val rasiText = stringResource(R.string.badge_rasi)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(2.dp, outlineColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawNorthIndianDiamondChart(planetaryPositions, lagnaHouse, rasiHouse, textMeasurer, outlineColor, onSurfaceColor, title, context, lagText, rasiText)
        }
    }
}

private fun DrawScope.drawNorthIndianDiamondChart(
    positions: Map<String, Int>,
    lagnaHouse: Int,
    rasiHouse: Int,
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    outlineColor: Color,
    textColor: Color,
    title: String,
    context: Context,
    lagText: String,
    rasiText: String
) {
    val w = size.width
    val h = size.height

    drawRect(outlineColor, topLeft = Offset.Zero, size = size, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))

    drawLine(outlineColor, Offset(0f, 0f), Offset(w, h), 2f)
    drawLine(outlineColor, Offset(w, 0f), Offset(0f, h), 2f)

    val topMid = Offset(w / 2f, 0f)
    val rightMid = Offset(w, h / 2f)
    val bottomMid = Offset(w / 2f, h)
    val leftMid = Offset(0f, h / 2f)

    drawLine(outlineColor, topMid, rightMid, 2f)
    drawLine(outlineColor, rightMid, bottomMid, 2f)
    drawLine(outlineColor, bottomMid, leftMid, 2f)
    drawLine(outlineColor, leftMid, topMid, 2f)

    val titleLayout = textMeasurer.measure(title, style = TextStyle(fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Bold))
    drawText(titleLayout, topLeft = Offset(w / 2f - titleLayout.size.width / 2f, h / 2f - titleLayout.size.height / 2f))

    val houseCenters = mapOf(
        1 to Offset(w / 2f, h * 0.18f),
        2 to Offset(w * 0.75f, h * 0.25f),
        3 to Offset(w * 0.82f, h * 0.5f),
        4 to Offset(w * 0.75f, h * 0.75f),
        5 to Offset(w / 2f, h * 0.82f),
        6 to Offset(w * 0.25f, h * 0.75f),
        7 to Offset(w * 0.18f, h * 0.5f),
        8 to Offset(w * 0.25f, h * 0.25f),
        9 to Offset(w * 0.38f, h * 0.38f),
        10 to Offset(w * 0.62f, h * 0.38f),
        11 to Offset(w * 0.62f, h * 0.62f),
        12 to Offset(w * 0.38f, h * 0.62f)
    )

    houseCenters.forEach { (houseNum, center) ->
        val isLag = houseNum == lagnaHouse
        val isRasi = houseNum == rasiHouse
        val houseText = "$houseNum${if (isLag) " [$lagText]" else ""}${if (isRasi) " [$rasiText]" else ""}"
        val houseLayout = textMeasurer.measure(houseText, style = TextStyle(fontSize = 11.sp, color = if (isLag) Color(0xFF1976D2) else textColor, fontWeight = FontWeight.Bold))
        drawText(houseLayout, topLeft = Offset(center.x - houseLayout.size.width / 2f, center.y - 18f))

        val planetsInHouse = positions.filter { it.value == houseNum }.keys.map { getLocalizedPlanetAbbr(context, it) }
        if (planetsInHouse.isNotEmpty()) {
            val planetText = planetsInHouse.joinToString(",")
            val planetLayout = textMeasurer.measure(planetText, style = TextStyle(fontSize = 10.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold))
            drawText(planetLayout, topLeft = Offset(center.x - planetLayout.size.width / 2f, center.y + 4f))
        }
    }
}

@Composable
fun RasiCell(
    rasiIndex: Int,
    planetaryPositions: Map<String, Int>,
    lagnaHouse: Int,
    rasiHouse: Int,
    outlineColor: Color,
    onSurfaceColor: Color
) {
    val context = LocalContext.current
    val rasiName = getLocalizedRasiAbbr(context, rasiIndex)
    val lagText = stringResource(R.string.badge_lag)
    val rasiText = stringResource(R.string.badge_rasi)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, outlineColor)
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = rasiName,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = onSurfaceColor)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (rasiIndex == lagnaHouse) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = lagText,
                                style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary),
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                        }
                    }
                    if (rasiIndex == rasiHouse) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = rasiText,
                                style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondary),
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val planetsInRasi = planetaryPositions.filter { it.value == rasiIndex }.keys.map { getLocalizedPlanetAbbr(context, it) }

            if (planetsInRasi.isNotEmpty()) {
                Text(
                    text = planetsInRasi.joinToString(", "),
                    style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}

private fun getLocalizedRasiAbbr(context: Context, rasiIndex: Int): String {
    val resId = when (rasiIndex) {
        1 -> R.string.rasi_mes
        2 -> R.string.rasi_rish
        3 -> R.string.rasi_mit
        4 -> R.string.rasi_kar
        5 -> R.string.rasi_sim
        6 -> R.string.rasi_kan
        7 -> R.string.rasi_tul
        8 -> R.string.rasi_vri
        9 -> R.string.rasi_dha
        10 -> R.string.rasi_mak
        11 -> R.string.rasi_kum
        12 -> R.string.rasi_mee
        else -> 0
    }
    return if (resId != 0) context.getString(resId) else "$rasiIndex"
}

private fun getLocalizedPlanetAbbr(context: Context, planet: String): String {
    val resId = when (planet) {
        "Sun" -> R.string.planet_sun_abbr
        "Moon" -> R.string.planet_mon_abbr
        "Mars" -> R.string.planet_mar_abbr
        "Mercury" -> R.string.planet_mer_abbr
        "Jupiter" -> R.string.planet_jup_abbr
        "Venus" -> R.string.planet_ven_abbr
        "Saturn" -> R.string.planet_sat_abbr
        "Rahu" -> R.string.planet_rah_abbr
        "Ketu" -> R.string.planet_ket_abbr
        else -> 0
    }
    return if (resId != 0) context.getString(resId) else planet.take(3)
}
