package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.*
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.Duration

object PanchangamCalculator {

    enum class GowriCategory {
        AMRIDHA, UTHI, LABAM, DHANAM, SUGAM, SORAM, VISHAM, ROGAM;

        fun toAuspiciousness(): Auspiciousness = when (this) {
            AMRIDHA -> Auspiciousness.GREEN
            UTHI, LABAM, DHANAM, SUGAM -> Auspiciousness.BLUE
            VISHAM, ROGAM -> Auspiciousness.AMBER
            SORAM -> Auspiciousness.RED
        }
    }

    private val GOWRI_DAY_SEQUENCE = mapOf(
        DayOfWeek.SUNDAY to listOf(GowriCategory.UTHI, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.VISHAM),
        DayOfWeek.MONDAY to listOf(GowriCategory.AMRIDHA, GowriCategory.VISHAM, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI),
        DayOfWeek.TUESDAY to listOf(GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA),
        DayOfWeek.WEDNESDAY to listOf(GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA, GowriCategory.ROGAM),
        DayOfWeek.THURSDAY to listOf(GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM),
        DayOfWeek.FRIDAY to listOf(GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM),
        DayOfWeek.SATURDAY to listOf(GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM)
    )

    private val GOWRI_NIGHT_SEQUENCE = mapOf(
        DayOfWeek.SUNDAY to listOf(GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.AMRIDHA, GowriCategory.VISHAM, GowriCategory.ROGAM, GowriCategory.LABAM),
        DayOfWeek.MONDAY to listOf(GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM),
        DayOfWeek.TUESDAY to listOf(GowriCategory.SORAM, GowriCategory.VISHAM, GowriCategory.UTHI, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM),
        DayOfWeek.WEDNESDAY to listOf(GowriCategory.UTHI, GowriCategory.AMRIDHA, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.VISHAM),
        DayOfWeek.THURSDAY to listOf(GowriCategory.AMRIDHA, GowriCategory.VISHAM, GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI),
        DayOfWeek.FRIDAY to listOf(GowriCategory.ROGAM, GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.UTHI, GowriCategory.VISHAM, GowriCategory.AMRIDHA),
        DayOfWeek.SATURDAY to listOf(GowriCategory.LABAM, GowriCategory.DHANAM, GowriCategory.SUGAM, GowriCategory.SORAM, GowriCategory.VISHAM, GowriCategory.UTHI, GowriCategory.AMRIDHA, GowriCategory.ROGAM)
    )

    fun calculateNallaNeram(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): List<NallaNeram> {
        val offset = Duration.between(LocalTime.of(6, 0), sunrise)
        
        // Typical Nalla Neram slots (Morning and Evening/Night)
        val fixedWindows = when (dayOfWeek) {
            DayOfWeek.MONDAY -> listOf(
                LocalTime.of(6, 15) to LocalTime.of(7, 15), 
                LocalTime.of(15, 15) to LocalTime.of(16, 15),
                LocalTime.of(18, 0) to LocalTime.of(19, 0),
                LocalTime.of(21, 0) to LocalTime.of(22, 0)
            )
            DayOfWeek.TUESDAY -> listOf(
                LocalTime.of(7, 45) to LocalTime.of(8, 45), 
                LocalTime.of(16, 45) to LocalTime.of(17, 45), 
                LocalTime.of(18, 15) to LocalTime.of(19, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
            DayOfWeek.WEDNESDAY -> listOf(
                LocalTime.of(9, 15) to LocalTime.of(10, 15), 
                LocalTime.of(15, 15) to LocalTime.of(16, 15),
                LocalTime.of(18, 15) to LocalTime.of(19, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
            DayOfWeek.THURSDAY -> listOf(
                LocalTime.of(10, 45) to LocalTime.of(11, 45), 
                LocalTime.of(12, 15) to LocalTime.of(13, 15), 
                LocalTime.of(18, 15) to LocalTime.of(19, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
            DayOfWeek.FRIDAY -> listOf(
                LocalTime.of(9, 15) to LocalTime.of(10, 15), 
                LocalTime.of(16, 45) to LocalTime.of(17, 45), 
                LocalTime.of(18, 15) to LocalTime.of(19, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
            DayOfWeek.SATURDAY -> listOf(
                LocalTime.of(7, 45) to LocalTime.of(8, 45), 
                LocalTime.of(16, 45) to LocalTime.of(17, 45), 
                LocalTime.of(18, 15) to LocalTime.of(19, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
            DayOfWeek.SUNDAY -> listOf(
                LocalTime.of(7, 45) to LocalTime.of(8, 45), 
                LocalTime.of(10, 45) to LocalTime.of(11, 45),
                LocalTime.of(15, 15) to LocalTime.of(16, 15),
                LocalTime.of(20, 0) to LocalTime.of(21, 0)
            )
        }

        return fixedWindows.map { (start, end) ->
            NallaNeram(start.plus(offset), end.plus(offset), Auspiciousness.GREEN)
        }
    }

    fun calculateGowriNeram(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): List<GowriNeram> {
        val dayDuration = Duration.between(sunrise, sunset)
        val daySlotDuration = dayDuration.dividedBy(8)
        val daySequence = GOWRI_DAY_SEQUENCE[dayOfWeek] ?: return emptyList()

        val daySlots = daySequence.mapIndexed { index, category ->
            val start = sunrise.plus(daySlotDuration.multipliedBy(index.toLong()))
            val end = sunrise.plus(daySlotDuration.multipliedBy((index + 1).toLong()))
            GowriNeram(category.name, start, end, category.toAuspiciousness())
        }

        // Night slots (from sunset to next sunrise)
        var nightDuration = Duration.between(sunset, sunrise)
        if (nightDuration.isNegative) {
            nightDuration = nightDuration.plus(Duration.ofDays(1))
        }
        val nightSlotDuration = nightDuration.dividedBy(8)
        val nightSequence = GOWRI_NIGHT_SEQUENCE[dayOfWeek] ?: return emptyList()

        val nightSlots = nightSequence.mapIndexed { index, category ->
            val start = sunset.plus(nightSlotDuration.multipliedBy(index.toLong()))
            val end = sunset.plus(nightSlotDuration.multipliedBy((index + 1).toLong()))
            GowriNeram(category.name, start, end, category.toAuspiciousness())
        }

        return daySlots + nightSlots
    }

    fun calculateHora(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): List<Hora> {
        val dayDuration = Duration.between(sunrise, sunset)
        val daySlotDuration = dayDuration.dividedBy(12)
        
        var nightDuration = Duration.between(sunset, sunrise)
        if (nightDuration.isNegative) {
            nightDuration = nightDuration.plus(Duration.ofDays(1))
        }
        val nightSlotDuration = nightDuration.dividedBy(12)
        
        val planets = listOf("Sun", "Venus", "Mercury", "Moon", "Saturn", "Jupiter", "Mars")
        val startIndex = when (dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 3
            DayOfWeek.TUESDAY -> 6
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 5
            DayOfWeek.FRIDAY -> 1
            DayOfWeek.SATURDAY -> 4
        }

        val dayHoras = (0 until 12).map { i ->
            val planet = planets[(startIndex + i) % 7]
            val start = sunrise.plus(daySlotDuration.multipliedBy(i.toLong()))
            val end = sunrise.plus(daySlotDuration.multipliedBy((i + 1).toLong()))
            
            val auspiciousness = when (planet) {
                "Jupiter", "Venus", "Mercury", "Moon" -> Auspiciousness.GREEN
                "Sun" -> Auspiciousness.BLUE
                "Saturn", "Mars" -> Auspiciousness.RED
                else -> Auspiciousness.AMBER
            }
            Hora(planet, start, end, auspiciousness)
        }

        val nightHoras = (0 until 12).map { i ->
            val planet = planets[(startIndex + 12 + i) % 7]
            val start = sunset.plus(nightSlotDuration.multipliedBy(i.toLong()))
            val end = sunset.plus(nightSlotDuration.multipliedBy((i + 1).toLong()))
            
            val auspiciousness = when (planet) {
                "Jupiter", "Venus", "Mercury", "Moon" -> Auspiciousness.GREEN
                "Sun" -> Auspiciousness.BLUE
                "Saturn", "Mars" -> Auspiciousness.RED
                else -> Auspiciousness.AMBER
            }
            Hora(planet, start, end, auspiciousness)
        }

        return dayHoras + nightHoras
    }

    fun calculateAllTimings(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): List<Timing> {
        return calculateNallaNeram(dayOfWeek, sunrise, sunset) +
                calculateGowriNeram(dayOfWeek, sunrise, sunset) +
                calculateHora(dayOfWeek, sunrise, sunset)
    }

}
