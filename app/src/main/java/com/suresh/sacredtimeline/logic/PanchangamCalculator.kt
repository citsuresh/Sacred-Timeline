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

    private val RAHU_SEQUENCE = mapOf(
        DayOfWeek.SUNDAY to 7,
        DayOfWeek.MONDAY to 1,
        DayOfWeek.TUESDAY to 6,
        DayOfWeek.WEDNESDAY to 4,
        DayOfWeek.THURSDAY to 5,
        DayOfWeek.FRIDAY to 3,
        DayOfWeek.SATURDAY to 2
    )

    private val YAMA_SEQUENCE = mapOf(
        DayOfWeek.SUNDAY to 4,
        DayOfWeek.MONDAY to 3,
        DayOfWeek.TUESDAY to 2,
        DayOfWeek.WEDNESDAY to 1,
        DayOfWeek.THURSDAY to 0,
        DayOfWeek.FRIDAY to 6,
        DayOfWeek.SATURDAY to 5
    )

    private val KULI_SEQUENCE = mapOf(
        DayOfWeek.SUNDAY to 6,
        DayOfWeek.MONDAY to 5,
        DayOfWeek.TUESDAY to 4,
        DayOfWeek.WEDNESDAY to 3,
        DayOfWeek.THURSDAY to 2,
        DayOfWeek.FRIDAY to 1,
        DayOfWeek.SATURDAY to 0
    )

    fun calculateRahuKalam(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): SpecialPeriod {
        return calculateSpecialPeriod("Rahu", dayOfWeek, sunrise, sunset, RAHU_SEQUENCE, Auspiciousness.DARK_RED)
    }

    fun calculateYamagandam(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): SpecialPeriod {
        return calculateSpecialPeriod("Yama", dayOfWeek, sunrise, sunset, YAMA_SEQUENCE, Auspiciousness.ORANGE)
    }

    fun calculateKuligai(dayOfWeek: DayOfWeek, sunrise: LocalTime, sunset: LocalTime): SpecialPeriod {
        val period = calculateSpecialPeriod("Kuli", dayOfWeek, sunrise, sunset, KULI_SEQUENCE, Auspiciousness.GREY)
        val name = if (period.startTime.isBefore(LocalTime.NOON)) "Kuli Dawn" else "Kuli Dusk"
        return period.copy(name = name)
    }

    private fun calculateSpecialPeriod(
        name: String,
        dayOfWeek: DayOfWeek,
        sunrise: LocalTime,
        sunset: LocalTime,
        sequence: Map<DayOfWeek, Int>,
        auspiciousness: Auspiciousness
    ): SpecialPeriod {
        val duration = Duration.between(sunrise, sunset)
        val slotDuration = duration.dividedBy(8)
        val slotIndex = sequence[dayOfWeek] ?: 0
        val start = sunrise.plus(slotDuration.multipliedBy(slotIndex.toLong()))
        val end = sunrise.plus(slotDuration.multipliedBy((slotIndex + 1).toLong()))
        return SpecialPeriod(name, start, end, auspiciousness)
    }

    private val NALLA_NERAM_DAY_SLOTS = mapOf(
        DayOfWeek.SUNDAY to listOf(1, 3),    // 2nd and 4th slots
        DayOfWeek.MONDAY to listOf(0, 6),    // 1st and 7th slots
        DayOfWeek.TUESDAY to listOf(1, 7),   // 2nd and 8th slots
        DayOfWeek.WEDNESDAY to listOf(2, 6), // 3rd and 7th slots
        DayOfWeek.THURSDAY to listOf(3, 4),  // 4th and 5th slots
        DayOfWeek.FRIDAY to listOf(2, 7),    // 3rd and 8th slots
        DayOfWeek.SATURDAY to listOf(1, 7)   // 2nd and 8th slots
    )

    fun calculateNallaNeram(
        dayOfWeek: DayOfWeek,
        sunrise: LocalTime,
        sunset: LocalTime,
        offsetMinutes: Long = 15,
        durationMinutes: Long = 60
    ): List<NallaNeram> {
        val gowriSlots = calculateGowriNeram(dayOfWeek, sunrise, sunset)
        val dayGowri = gowriSlots.filter { 
            !it.startTime.isBefore(sunrise) && !it.endTime.isAfter(sunset) 
        }
        
        val rahu = calculateRahuKalam(dayOfWeek, sunrise, sunset)
        val yama = calculateYamagandam(dayOfWeek, sunrise, sunset)
        val kuli = calculateKuligai(dayOfWeek, sunrise, sunset)
        
        val inauspiciousPeriods = listOf(rahu, yama, kuli)
        val primarySlots = NALLA_NERAM_DAY_SLOTS[dayOfWeek] ?: emptyList()

        val result = mutableListOf<NallaNeram>()

        dayGowri.forEachIndexed { index, gowri ->
            // Match traditional Nalla Neram slots for the day
            if (primarySlots.contains(index)) {
                // Also verify it's auspicious in Gowri (should be, by definition)
                if (gowri.auspiciousness == Auspiciousness.GREEN || gowri.auspiciousness == Auspiciousness.BLUE) {
                    var start = gowri.startTime.plusMinutes(offsetMinutes)
                    var end = start.plusMinutes(durationMinutes)
                    
                    if (end.isAfter(gowri.endTime)) {
                        end = gowri.endTime
                    }

                    // Trimming overlaps with Rahu, Yama, Kuli
                    inauspiciousPeriods.forEach { bad ->
                        if (overlaps(start, end, bad.startTime, bad.endTime)) {
                            if (start.isBefore(bad.startTime) && end.isAfter(bad.startTime)) {
                                end = bad.startTime
                            } else if (start.isBefore(bad.endTime) && end.isAfter(bad.endTime)) {
                                start = bad.endTime
                            } else if (start.isAfter(bad.startTime) && end.isBefore(bad.endTime)) {
                                start = end // Skip if fully inside
                            }
                        }
                    }

                    if (Duration.between(start, end).toMinutes() >= 30) {
                        val name = if (start.isBefore(LocalTime.NOON)) "Morning" else "Evening"
                        result.add(NallaNeram(name, start, end, Auspiciousness.GREEN))
                    }
                }
            }
        }

        return result.sortedBy { it.startTime }
    }

    private fun overlaps(s1: LocalTime, e1: LocalTime, s2: LocalTime, e2: LocalTime): Boolean {
        return s1.isBefore(e2) && s2.isBefore(e1)
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
                calculateHora(dayOfWeek, sunrise, sunset) +
                listOf(
                    calculateRahuKalam(dayOfWeek, sunrise, sunset),
                    calculateYamagandam(dayOfWeek, sunrise, sunset),
                    calculateKuligai(dayOfWeek, sunrise, sunset)
                )
    }

}
