package com.suresh.sacredtimeline.ui.dashboard.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
    segments: List<LaneSegment>,
    onClick: () -> Unit
) {
    if (segments.isEmpty()) return

    val topOffset = calculateOffset(timing.startTime, hourHeight)
    val bottomOffset = calculateOffset(timing.endTime, hourHeight)
    val totalHeight = bottomOffset - topOffset

    val timingColor = SacredTimelineColors.getTimingColor(timing)
    val contentColor = SacredTimelineColors.getContentColor(timingColor)
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = Modifier
            .offset(y = topOffset)
            .height(totalHeight)
            .fillMaxWidth()
    ) {
        val containerWidthPx = with(density) { maxWidth.toPx() }
        val totalHeightPx = with(density) { totalHeight.toPx() }

        // 1. Draw the Stepped Shape and Sticker Borders
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path()
            val sidePadding = 1.dp.toPx()
            
            // Build RIGHT edge (top to bottom)
            val first = segments[0]
            path.moveTo(first.offsetFactor * containerWidthPx + sidePadding, 0f)
            path.lineTo((first.offsetFactor + first.widthFactor) * containerWidthPx - sidePadding, 0f)
            
            segments.forEachIndexed { index, seg ->
                val segTop = (calculateOffset(seg.startTime, hourHeight) - topOffset).toPx()
                val segBottom = (calculateOffset(seg.endTime, hourHeight) - topOffset).toPx()
                val segRight = (seg.offsetFactor + seg.widthFactor) * containerWidthPx - sidePadding
                
                // Vertical line to top of segment
                path.lineTo(segRight, segTop)
                // Vertical line to bottom of segment
                path.lineTo(segRight, segBottom)
                
                if (index < segments.size - 1) {
                    val next = segments[index + 1]
                    val nextRight = (next.offsetFactor + next.widthFactor) * containerWidthPx - sidePadding
                    // Horizontal step to next segment's right edge
                    path.lineTo(nextRight, segBottom)
                }
            }
            
            // Bottom edge
            val last = segments.last()
            path.lineTo(last.offsetFactor * containerWidthPx + sidePadding, totalHeightPx)
            
            // Build LEFT edge (bottom to top)
            segments.reversed().forEachIndexed { index, seg ->
                val segTop = (calculateOffset(seg.startTime, hourHeight) - topOffset).toPx()
                val segBottom = (calculateOffset(seg.endTime, hourHeight) - topOffset).toPx()
                val segLeft = seg.offsetFactor * containerWidthPx + sidePadding
                
                // Vertical line to bottom of segment
                path.lineTo(segLeft, segBottom)
                // Vertical line to top of segment
                path.lineTo(segLeft, segTop)
                
                if (index < segments.size - 1) {
                    val prev = segments.reversed()[index + 1]
                    val prevLeft = prev.offsetFactor * containerWidthPx + sidePadding
                    // Horizontal step to previous segment's left edge
                    path.lineTo(prevLeft, segTop)
                }
            }
            path.close()

            // Draw Background
            drawPath(path, timingColor.copy(alpha = 0.9f))
            
            // Double Border
            drawPath(path, Color.White, style = Stroke(width = with(density) { 3.dp.toPx() }))
            drawPath(path, Color.Black.copy(alpha = 0.3f), style = Stroke(width = with(density) { 0.8.dp.toPx() }))
        }

        // 2. Clickable Areas (Precise Segment-based touch targets)
        segments.forEach { seg ->
            val sTop = calculateOffset(seg.startTime, hourHeight) - topOffset
            val sHeight = calculateOffset(seg.endTime, hourHeight) - calculateOffset(seg.startTime, hourHeight)
            Box(
                modifier = Modifier
                    .offset(x = maxWidth * seg.offsetFactor, y = sTop)
                    .size(width = maxWidth * seg.widthFactor, height = sHeight)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = onClick
                    )
            )
        }

        // 3. Place Text in the "best" segment (Tallest among those with the maximum width)
        val widestSegment = segments.sortedWith(
            compareByDescending<LaneSegment> { it.widthFactor }
                .thenByDescending { java.time.Duration.between(it.startTime, it.endTime).toMinutes() }
        ).firstOrNull() ?: segments[0]
        
        val sOffset = calculateOffset(widestSegment.startTime, hourHeight) - topOffset
        val sHeight = calculateOffset(widestSegment.endTime, hourHeight) - calculateOffset(widestSegment.startTime, hourHeight)
        
        Box(
            modifier = Modifier
                .offset(x = maxWidth * widestSegment.offsetFactor, y = sOffset)
                .size(width = maxWidth * widestSegment.widthFactor, height = sHeight)
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Heading - Top Center (Only if enough height and NOT redundant)
                val isMuhurthamType = timing is Muhurtham || timing is MaitraMuhurtham
                if (widestSegment.widthFactor >= 0.28f && sHeight > 45.dp && !isMuhurthamType) {
                    Text(
                        text = stringResource(Metadata.getCategoryShortNameRes(timing)).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = 7.sp,
                        lineHeight = 8.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // 2. Main Icon (Higher threshold)
                if (sHeight > 95.dp) {
                    val iconSize = if (sHeight > 120.dp) 20.dp else 16.dp
                    if (timing is SpecialPeriod && timing.name == "Yama") {
                        Image(
                            painter = painterResource(R.drawable.ic_yama_bull),
                            contentDescription = null,
                            modifier = Modifier.size(iconSize + 4.dp)
                        )
                    } else {
                        val iconPainter = when {
                            timing is SpecialPeriod && timing.name == "Rahu" -> painterResource(R.drawable.ic_rahu)
                            timing is SpecialPeriod && (timing.name == "Kuli Dawn" || timing.name == "Kuli Dusk") -> painterResource(R.drawable.ic_saturn)
                            else -> null
                        }
                        if (iconPainter != null) {
                            Icon(iconPainter, contentDescription = null, modifier = Modifier.size(iconSize), tint = contentColor)
                        } else if (timing is NallaNeram || timing is Muhurtham || timing is MaitraMuhurtham) {
                            Icon(if (timing is NallaNeram) Icons.Default.Star else Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(iconSize), tint = contentColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // 3. Label (The primary text)
                val labelRes = when (timing) {
                    is Hora -> Metadata.getPlanetNameRes(timing.name)
                    is NallaNeram -> Metadata.getSpecialNameRes("Nalla")
                    is GowriNeram -> Metadata.getGowriNameRes(timing.name)
                    is SpecialPeriod -> Metadata.getSpecialNameRes(timing.name)
                    is Muhurtham -> Metadata.getMuhurthamNameRes(timing.name)
                    is MaitraMuhurtham -> Metadata.getSpecialNameRes("Maitra Muhurtham")
                }
                Text(
                    text = stringResource(labelRes),
                    style = if (sHeight < 60.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    textAlign = TextAlign.Center,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = if (sHeight < 60.dp) 9.sp else 11.sp,
                    fontSize = when {
                        widestSegment.widthFactor < 0.20f -> 7.sp
                        widestSegment.widthFactor < 0.25f -> 8.sp
                        sHeight < 40.dp -> 8.sp
                        else -> 10.sp
                    }
                )

                // 4. Time Range
                if (sHeight > 28.dp) {
                    val pattern = if (is24Hour) "HH:mm" else "h:mm"
                    val timeFormatter = DateTimeFormatter.ofPattern(pattern)
                    Text(
                        text = "${timing.startTime.format(timeFormatter)} - ${timing.endTime.format(timeFormatter)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = if (sHeight < 50.dp) 7.sp else 8.sp,
                        maxLines = 1
                    )
                }

                // 5. Potency Stars
                if (timing is MaitraMuhurtham && sHeight > 40.dp) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(timing.potencyStars) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(10.dp),
                                tint = contentColor
                            )
                        }
                    }
                }
            }
        }
        
        // Compatibility Icon for Hora
        if (timing is Hora && totalHeight > 30.dp) {
             Box(modifier = Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.TopEnd) {
                val (icon, tint) = when (timing.compatibility) {
                    HoraCompatibility.FAVORABLE -> Icons.Default.CheckCircle to CompatibilityFavorable
                    HoraCompatibility.CONFLICTING -> Icons.Default.Cancel to CompatibilityConflicting
                    HoraCompatibility.NEUTRAL -> Icons.Default.RadioButtonUnchecked to CompatibilityNeutral
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(if (totalHeight > 60.dp) 18.dp else 12.dp).background(Color.White, RoundedCornerShape(9.dp)),
                    tint = tint
                )
            }
        }
    }
}
