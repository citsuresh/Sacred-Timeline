package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.model.*
import com.suresh.sacredtimeline.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val START_HOUR = 0

@Composable
fun TimingCard(
    timing: Timing,
    hourHeight: Dp,
    is24Hour: Boolean,
    onClick: () -> Unit
) {
    val topOffset = calculateOffset(timing.startTime, hourHeight)
    val bottomOffset = calculateOffset(timing.endTime, hourHeight)
    val height = bottomOffset - topOffset

    val timingColor = SacredTimelineColors.getTimingColor(timing)
    val contentColor = SacredTimelineColors.getContentColor(timingColor)

    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 1.dp)
            .offset(y = topOffset)
            .height(height - 2.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CardOuterBorderColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
                .background(CardBorderColor, RoundedCornerShape(7.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(timingColor, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    // ICON LOGIC
                    if (height > 80.dp) {
                        if (timing is SpecialPeriod && timing.name == "Yama") {
                            Image(
                                painter = painterResource(R.drawable.ic_yama_bull),
                                contentDescription = null,
                                modifier = Modifier.size(if (height > 110.dp) 32.dp else 24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        } else {
                            val iconPainter = when {
                                timing is SpecialPeriod && timing.name == "Rahu" -> painterResource(R.drawable.ic_rahu)
                                timing is SpecialPeriod && (timing.name == "Kuli Dawn" || timing.name == "Kuli Dusk") -> painterResource(R.drawable.ic_saturn)
                                else -> null
                            }

                            if (iconPainter != null) {
                                Icon(
                                    painter = iconPainter,
                                    contentDescription = null,
                                    modifier = Modifier.size(if (height > 110.dp) 32.dp else 24.dp),
                                    tint = contentColor.copy(alpha = 0.9f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            } else if (timing is NallaNeram) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(if (height > 110.dp) 24.dp else 16.dp),
                                    tint = contentColor.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                    }

                    // TIME RANGE
                    if (height > 70.dp) {
                        val pattern = if (is24Hour) "HH:mm" else "h:mm"
                        val timeFormatter = DateTimeFormatter.ofPattern(pattern)
                        Text(
                            text = "${timing.startTime.format(timeFormatter)} - ${timing.endTime.format(timeFormatter)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor,
                            fontSize = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // ENGLISH/LOCALIZED LABEL
                    val labelRes = when (timing) {
                        is Hora -> Metadata.getPlanetNameRes(timing.name)
                        is NallaNeram -> Metadata.getSpecialNameRes("Nalla")
                        is GowriNeram -> Metadata.getGowriNameRes(timing.name)
                        is SpecialPeriod -> Metadata.getSpecialNameRes(timing.name)
                    }
                    Text(
                        text = stringResource(labelRes),
                        style = if (height < 60.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = if (height < 40.dp) 10.sp else if (height < 60.dp) 11.sp else 12.sp
                    )

                    // SECONDARY NAME (Tamil/English based on height)
                    if (height > 140.dp) {
                        val secondaryName = when (timing) {
                            is Hora -> Metadata.getPlanetName(timing.name, androidx.compose.ui.platform.LocalContext.current)
                            is NallaNeram -> Metadata.getSpecialName("Nalla", androidx.compose.ui.platform.LocalContext.current)
                            is GowriNeram -> Metadata.getGowriName(timing.name, androidx.compose.ui.platform.LocalContext.current)
                            is SpecialPeriod -> Metadata.getSpecialName(timing.name, androidx.compose.ui.platform.LocalContext.current)
                        }
                        
                        // We only show it if it's different from the primary label (which might happen if we change logic)
                        // Or we can just show the Tamil one explicitly if current is English.
                        // But stringResource already handles localization.
                        // For height > 140dp, maybe show time in a larger font or something else?
                        // The user asked for Tamil names after translating.
                    }
                }

                // Compatibility Icon for Hora
                if (timing is Hora && height > 30.dp) {
                    Box(modifier = Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.TopEnd) {
                        val (icon, tint) = when (timing.compatibility) {
                            HoraCompatibility.FAVORABLE -> Icons.Default.CheckCircle to CompatibilityFavorable
                            HoraCompatibility.CONFLICTING -> Icons.Default.Cancel to CompatibilityConflicting
                            HoraCompatibility.NEUTRAL -> Icons.Default.RadioButtonUnchecked to CompatibilityNeutral
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(if (height > 60.dp) 20.dp else 14.dp).background(Color.White, RoundedCornerShape(10.dp)),
                            tint = tint
                        )
                    }
                }
            }
        }
    }
}
