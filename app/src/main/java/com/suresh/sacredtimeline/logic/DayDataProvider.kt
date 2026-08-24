package com.suresh.sacredtimeline.logic

import android.content.Context
import com.suresh.sacredtimeline.R
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.data.VerifiedHolidays
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class DayDataProvider(private val context: Context) {
    private val repository = SettingsRepository(context)
    private val provider = MockPanchangamProvider()
    private val sunProvider = SunriseSunsetProvider()

    suspend fun fetchDayData(date: LocalDate, lat: Double, lng: Double): DayData {
        val sunDef = repository.sunriseDefinition.first()
        val style = repository.specialPeriodStyle.first()
        val enabledTithisVal = repository.enabledTithis.first()
        val enabledStarsVal = repository.enabledNakshatras.first()
        val enabledChandrashtamamStars = repository.enabledChandrashtamamStars.first()

        val system = repository.lunarMonthSystem.first()

        val sunResult = sunProvider.getSunTimes(lat, lng, date, sunDef)
        val timings = provider.getTimings(date, sunResult.sunrise, sunResult.sunset, style, sunDef, lat, lng)
        
        val tamilCalendar = TamilCalendarUtils.getTamilDate(date)
        val lunarDayInfo = LunarCalendarUtils.getLunarDayInfo(date, system)
        
        val zoneId = ZoneId.systemDefault()
        val sunriseInstant = date.atTime(sunResult.sunrise).atZone(zoneId).toInstant()
        val pradoshaWindow = LunarCalendarUtils.calculatePradoshaWindow(lat, lng, date, sunDef, zoneId)
        val nishitaWindow = LunarCalendarUtils.calculateNishitaKala(lat, lng, date, sunDef, zoneId)
        
        val ritualContext = TamilCalendarUtils.RitualContext(
            tithis = lunarDayInfo.tithis,
            nakshatras = lunarDayInfo.nakshatras,
            sunrise = sunriseInstant,
            pradosham = pradoshaWindow,
            nishita = nishitaWindow,
            zoneId = zoneId
        )
        
        val festivals = TamilCalendarUtils.getSpecialEvents(tamilCalendar, ritualContext)
        val holidays = VerifiedHolidays.getHolidays(date)
        val combinedEvents = (holidays + festivals).distinct()
        
        val brahmaTimes = LunarCalendarUtils.calculateBrahmaMuhurtham(sunResult.sunrise)
        val brahma = Muhurtham(
            name = "Brahma Muhurtham",
            tamilName = "",
            startTime = brahmaTimes.first,
            endTime = brahmaTimes.second,
            auspiciousness = Auspiciousness.GREEN,
            description = ""
        )

        val abhijitTimes = LunarCalendarUtils.calculateAbhijitMuhurtham(sunResult.sunrise, sunResult.sunset)
        val abhijit = abhijitTimes?.let {
            Muhurtham(
                name = "Abhijit Muhurtham",
                tamilName = "",
                startTime = it.first,
                endTime = it.second,
                auspiciousness = Auspiciousness.GREEN,
                description = ""
            )
        }

        val maitra = PanchangamCalculator.calculateMaitraMuhurtham(
            date, lat, lng, sunResult.sunrise, zoneId
        )

        val filteredTithis = lunarDayInfo.tithis.filter { interval ->
            val normalizedValue = if (interval.value > 15) interval.value - 15 else interval.value
            enabledTithisVal.contains("TITHI_${interval.value}") || 
            (interval.value > 15 && enabledTithisVal.contains("TITHI_$normalizedValue"))
        }.map { 
            LunarInterval(it.value, it.resId, it.startTime, it.endTime)
        }

        val filteredNakshatras = lunarDayInfo.nakshatras.filter { interval ->
            enabledStarsVal.contains("STAR_${interval.value}") 
        }.map { 
            LunarInterval(it.value, it.resId, it.startTime, it.endTime)
        }

        val rasiIntervals = LunarCalendarUtils.getMoonRasiInfo(date)
        val chandrashtamamTimings = mutableListOf<ChandrashtamamTiming>()
        
        enabledChandrashtamamStars.forEach { starConfigId ->
            val birthRasi = LunarCalendarUtils.getBirthRasi(starConfigId)
            val targetRasi = ((birthRasi - 1 + 7) % 12) + 1
            
            rasiIntervals.filter { it.value == targetRasi }.forEach { interval ->
                val starResId = getStarResIdFromConfigId(starConfigId)
                val birthRasiIdx = LunarCalendarUtils.getBirthRasi(starConfigId)
                val birthRasiResId = LunarCalendarUtils.getRasiResId(birthRasiIdx)
                val transitRasiResId = LunarCalendarUtils.getRasiResId(targetRasi)
                
                // CLIP TIMES to current day bounds for vertical timeline display
                val dayStart = date.atStartOfDay(zoneId).toInstant()
                val dayEnd = date.plusDays(1).atStartOfDay(zoneId).toInstant()
                
                val displayStart = if (interval.startTime?.isBefore(dayStart) == true) LocalTime.MIN else interval.startTime?.atZone(zoneId)?.toLocalTime() ?: LocalTime.MIN
                val displayEnd = if (interval.endTime?.isAfter(dayEnd) == true) LocalTime.MAX else interval.endTime?.atZone(zoneId)?.toLocalTime() ?: LocalTime.MAX

                chandrashtamamTimings.add(
                    ChandrashtamamTiming(
                        name = "Chandrashtamam",
                        tamilName = context.getString(R.string.label_chandrashtamam),
                        startTime = displayStart,
                        endTime = displayEnd,
                        auspiciousness = Auspiciousness.RED,
                        description = "",
                        starResId = starResId,
                        birthRasiResId = birthRasiResId,
                        transitRasiResId = transitRasiResId,
                        startTimeInstant = interval.startTime ?: dayStart,
                        endTimeInstant = interval.endTime ?: dayEnd
                    )
                )
            }
        }

        return DayData(
            nallaNeram = timings.filterIsInstance<NallaNeram>(),
            gowriNeram = timings.filterIsInstance<GowriNeram>(),
            hora = timings.filterIsInstance<Hora>(),
            specialPeriods = timings.filterIsInstance<SpecialPeriod>(),
            sunrise = sunResult.sunrise,
            sunset = sunResult.sunset,
            isFallback = sunResult.isFallback,
            tamilDay = tamilCalendar.day,
            tamilMonthResId = tamilCalendar.monthResId,
            tamilYearResId = tamilCalendar.yearResId,
            pakshaResId = lunarDayInfo.pakshaResId,
            pakshaDay = lunarDayInfo.pakshaDay,
            tithis = filteredTithis,
            nakshatras = filteredNakshatras,
            specialEvents = combinedEvents,
            isSubhaMuhurtham = VerifiedHolidays.isSubhaMuhurtham(date),
            brahmaMuhurtham = brahma,
            abhijitMuhurtham = abhijit,
            maitraMuhurtham = maitra,
            chandrashtamam = chandrashtamamTimings
        )
    }

    private fun getStarResIdFromConfigId(configId: String): Int {
        val starIdx = configId.split("_")[1].toInt()
        return when (starIdx) {
            1 -> R.string.star_1
            2 -> R.string.star_2
            3 -> R.string.star_3
            4 -> R.string.star_4
            5 -> R.string.star_5
            6 -> R.string.star_6
            7 -> R.string.star_7
            8 -> R.string.star_8
            9 -> R.string.star_9
            10 -> R.string.star_10
            11 -> R.string.star_11
            12 -> R.string.star_12
            13 -> R.string.star_13
            14 -> R.string.star_14
            15 -> R.string.star_15
            16 -> R.string.star_16
            17 -> R.string.star_17
            18 -> R.string.star_18
            19 -> R.string.star_19
            20 -> R.string.star_20
            21 -> R.string.star_21
            22 -> R.string.star_22
            23 -> R.string.star_23
            24 -> R.string.star_24
            25 -> R.string.star_25
            26 -> R.string.star_26
            27 -> R.string.star_27
            else -> R.string.star_1
        }
    }
}
