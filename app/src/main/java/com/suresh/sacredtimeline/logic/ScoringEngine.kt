package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.Hora
import com.suresh.sacredtimeline.model.HoraCompatibility
import com.suresh.sacredtimeline.model.Auspiciousness
import java.time.LocalTime

data class ScoreResult(
    val totalScore: Float, // 0 to 100
    val bandTitle: String,
    val factorScores: Map<String, Float>
)

object ScoringEngine {
    fun calculateScore(
        dayData: DayData,
        birthProfile: BirthProfile?,
        currentTime: LocalTime = LocalTime.now(),
        weights: ScoringWeightsConfig = ScoringWeightsConfig(),
        horaScoringMode: String = "NATAL"
    ): ScoreResult {
        // 1. Check Special Periods (Yamagandam, Rahu Kalam, Kuligai) active at currentTime
        val activeSpecial = dayData.specialPeriods.find { it.isCurrent(currentTime) }
        val specialName = activeSpecial?.name ?: ""
        
        if (specialName.contains("Yamagam", true) || specialName.contains("Rahu", true) || specialName.contains("Kuli", true)) {
            val penaltyName = when {
                specialName.contains("Yamagam", true) -> "Yamagandam (Inauspicious Period)"
                specialName.contains("Rahu", true) -> "Rahu Kalam (Inauspicious Period)"
                else -> "Kuligai (Inauspicious Period)"
            }
            return ScoreResult(
                totalScore = 25f,
                bandTitle = penaltyName,
                factorScores = mapOf(
                    "Special Period" to 20f,
                    "Hora" to 30f,
                    "Yogam" to 30f,
                    "Tithi" to 40f
                )
            )
        }

        // 2. Check Chandrashtamam active at currentTime
        val activeChandrashtamam = dayData.chandrashtamam.find { it.isCurrent(currentTime) }
        if (activeChandrashtamam != null) {
            return ScoreResult(
                totalScore = 20f,
                bandTitle = "Chandrashtamam (Caution)",
                factorScores = mapOf("Transit" to 10f, "Yogam" to 30f)
            )
        }

        // 3. Active Yogam at currentTime (Amirtha, Siddha vs Marana, Vadha)
        val activeYogam = dayData.yogam.find { it.isCurrent(currentTime) }
        val yogaScore = when {
            activeYogam?.name?.contains("Amirtha", true) == true -> 1.0f
            activeYogam?.name?.contains("Siddha", true) == true -> 0.95f
            activeYogam?.name?.contains("Marana", true) == true -> 0.2f
            activeYogam?.name?.contains("Vadha", true) == true -> 0.25f
            else -> 0.6f
        }

        // 4. Active Hora at currentTime (Distinguishing Natal vs General Weekday mode)
        val activeHora = dayData.hora.find { it.isCurrent(currentTime) }
        val horaScore = if (horaScoringMode == "NATAL") {
            // User-specific natal compatibility against birth ascendant/rasi
            when (activeHora?.natalCompatibility) {
                HoraCompatibility.FAVORABLE -> 1.0f
                HoraCompatibility.CONFLICTING -> 0.2f
                HoraCompatibility.NEUTRAL, null -> getGeneralHoraScore(activeHora)
            }
        } else {
            // General planetary weekday nature (independent of birth profile)
            getGeneralHoraScore(activeHora)
        }

        // 5. Thara Bala & Tithi
        val tharaScore = 0.80f
        val tithiScore = 0.80f
        val transitScore = 0.75f
        val dasaScore = 0.80f

        val weightedSum = (tharaScore * weights.tharaBalaWeight) +
                          (transitScore * weights.transitWeight) +
                          (dasaScore * weights.dasaWeight) +
                          (horaScore * weights.horaWeight) +
                          (yogaScore * weights.yogaWeight) +
                          (tithiScore * weights.tithiWeight)

        val finalScore = (weightedSum * 100f).coerceIn(0f, 100f)

        val bandTitle = when {
            finalScore >= 80f -> "Highly Favorable"
            finalScore >= 65f -> "Favorable"
            finalScore >= 50f -> "Neutral"
            finalScore >= 35f -> "Caution / Sub-Optimal"
            else -> "Inauspicious Period"
        }

        val factors = mapOf(
            "Thara Bala (Personal Star)" to (tharaScore * weights.tharaBalaWeight * 100f),
            "Transit Positions (Gochara)" to (transitScore * weights.transitWeight * 100f),
            "Dasa / Bhukti Period" to (dasaScore * weights.dasaWeight * 100f),
            "Hora Quality (${activeHora?.name ?: ""})" to (horaScore * weights.horaWeight * 100f),
            "Yoga Quality (${activeYogam?.name ?: ""})" to (yogaScore * weights.yogaWeight * 100f),
            "Tithi & Paksha Quality" to (tithiScore * weights.tithiWeight * 100f)
        )

        return ScoreResult(
            totalScore = finalScore,
            bandTitle = bandTitle,
            factorScores = factors
        )
    }

    private fun getGeneralHoraScore(hora: Hora?): Float {
        val name = hora?.name?.lowercase() ?: ""
        return when {
            name.contains("jupiter") || name.contains("sun") || name.contains("venus") -> 0.95f
            name.contains("moon") || name.contains("mercury") -> 0.75f
            name.contains("mars") || name.contains("saturn") || name.contains("rahu") -> 0.35f
            hora?.auspiciousness == Auspiciousness.GREEN -> 0.85f
            hora?.auspiciousness == Auspiciousness.ORANGE -> 0.5f
            hora?.auspiciousness == Auspiciousness.RED || hora?.auspiciousness == Auspiciousness.DARK_RED -> 0.25f
            else -> 0.6f
        }
    }
}
