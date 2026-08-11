package com.suresh.sacredtimeline.logic

import android.content.Context
import com.suresh.sacredtimeline.data.SettingsRepository
import com.suresh.sacredtimeline.data.VerifiedHolidays
import com.suresh.sacredtimeline.model.*
import kotlinx.coroutines.flow.first
import java.time.LocalDate
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
            maitraMuhurtham = maitra
        )
    }
}
