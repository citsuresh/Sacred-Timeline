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
import androidx.compose.material.icons.filled.AutoAwesome
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

@Composable
fun TimingCard(
    timing: Timing,
    hourHeight: Dp,
    is24Hour: Boolean,
    widthFactor: Float = 1.0f,
    horizontalOffsetFactor: Float = 0.0f,
    onClick: () -> Unit
) {
    val topOffset = calculateOffset(timing.startTime, hourHeight)
    val bottomOffset = calculateOffset(timing.endTime, hourHeight)
    val height = bottomOffset - topOffset

    val timingColor = SacredTimelineColors.getTimingColor(timing)
    val contentColor = SacredTimelineColors.getContentColor(timingColor)

    BoxWithConstraints(
        modifier = Modifier
            .offset(y = topOffset)
            .height(height - 2.dp)
            .fillMaxWidth()
    ) {
        val availableWidth = maxWidth
        val cardWidth = availableWidth * widthFactor
        val xOffset = availableWidth * horizontalOffsetFactor

        Card(
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 1.dp)
                .width(cardWidth - 4.dp)
                .offset(x = xOffset)
                .fillMaxHeight()
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = CardOuterBorderColor.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(CardBorderColor.copy(alpha = 0.9f), RoundedCornerShape(7.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .background(timingColor.copy(alpha = 0.9f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // CATEGORY HEADING (for combined views) - Positioned at Top Center
                    if (widthFactor < 0.9f && height > 25.dp) {
                        Text(
                            text = stringResource(Metadata.getCategoryShortNameRes(timing)).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 1.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = if (widthFactor < 0.9f) 8.dp else 0.dp)
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
                                } else if (timing is Muhurtham) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
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
                            is Muhurtham -> Metadata.getMuhurthamNameRes(timing.name)
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
}
