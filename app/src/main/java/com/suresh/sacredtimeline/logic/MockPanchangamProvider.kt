package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class MockPanchangamProvider {
    fun getTimings(date: LocalDate, sunrise: LocalTime, sunset: LocalTime): List<Timing> {
        // Use yesterday's cycle to fill 00:00 to Today's Sunrise
        val yesterdayCycle = PanchangamCalculator.calculateAllTimings(date.minusDays(1).dayOfWeek, sunrise, sunset)
        // Use today's cycle to fill Today's Sunrise to 23:59
        val todayCycle = PanchangamCalculator.calculateAllTimings(date.dayOfWeek, sunrise, sunset)
        
        val result = mutableListOf<Timing>()
        
        // 1. Fill 00:00 to Sunrise (from Yesterday's Night slots)
        yesterdayCycle.forEach { timing ->
            if (timing.endTime.isBefore(timing.startTime)) {
                // Crosses midnight! (e.g. 23:30 - 00:45)
                // The part after midnight belongs to TODAY
                result.add(createTiming(timing, LocalTime.MIN, timing.endTime))
            } else if (timing.startTime.isBefore(sunrise) && !timing.endTime.isAfter(sunrise)) {
                // Entirely between midnight and sunrise
                // But we must be sure it's not a day slot from yesterday!
                // Since yesterday's day slots are 06:00-18:15, anything < 06:00 is from the night cycle.
                result.add(timing)
            } else if (timing.startTime.isBefore(sunrise) && timing.endTime.isAfter(sunrise)) {
                // Starts before sunrise, ends after (e.g. 05:45 - 07:15)
                // Clip it at sunrise
                result.add(createTiming(timing, timing.startTime, sunrise))
            }
        }
        
        // 2. Fill Sunrise to 23:59 (from Today's slots)
        todayCycle.forEach { timing ->
            if (timing.endTime.isBefore(timing.startTime)) {
                // Crosses midnight! (e.g. 23:30 - 00:45)
                // The part before midnight belongs to TODAY
                result.add(createTiming(timing, timing.startTime, LocalTime.MAX))
            } else if (!timing.startTime.isBefore(sunrise)) {
                // Starts at or after sunrise
                result.add(timing)
            }
        }
        
        return result.distinctBy { it.startTime.toString() + it.endTime.toString() + it.auspiciousness.name }
    }

    private fun createTiming(original: Timing, start: LocalTime, end: LocalTime): Timing {
        return when (original) {
            is NallaNeram -> NallaNeram(original.name, start, end, original.auspiciousness)
            is GowriNeram -> GowriNeram(original.name, start, end, original.auspiciousness)
            is Hora -> Hora(original.name, start, end, original.auspiciousness)
            is SpecialPeriod -> SpecialPeriod(original.name, start, end, original.auspiciousness)
        }
    }

    fun getCurrentTimings(date: LocalDate, time: LocalTime, sunrise: LocalTime, sunset: LocalTime): SpecialTimings {
        val timings = getTimings(date, sunrise, sunset)
        return SpecialTimings(
            nallaNeram = timings.filterIsInstance<NallaNeram>().find { it.isCurrent(time) },
            gowriNeram = timings.filterIsInstance<GowriNeram>().find { it.isCurrent(time) },
            hora = timings.filterIsInstance<Hora>().find { it.isCurrent(time) },
            specialPeriod = timings.filterIsInstance<SpecialPeriod>().find { it.isCurrent(time) }
        )
    }

    data class SpecialTimings(
        val nallaNeram: NallaNeram?,
        val gowriNeram: GowriNeram?,
        val hora: Hora?,
        val specialPeriod: SpecialPeriod?
    )
}
