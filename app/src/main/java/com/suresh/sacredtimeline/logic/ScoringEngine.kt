package com.suresh.sacredtimeline.logic

import com.suresh.sacredtimeline.model.DayData
import com.suresh.sacredtimeline.model.BirthProfile
import com.suresh.sacredtimeline.model.HoraCompatibility
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
        weights: ScoringWeightsConfig = ScoringWeightsConfig()
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

        // 4. Active Hora at currentTime (Prioritizing User-Specific Natal Compatibility if available)
        val activeHora = dayData.hora.find { it.isCurrent(currentTime) }
        val horaScore = when (activeHora?.natalCompatibility) {
            HoraCompatibility.FAVORABLE -> 1.0f
            HoraCompatibility.CONFLICTING -> 0.3f
            HoraCompatibility.NEUTRAL, null -> when (activeHora?.auspiciousness) {
                com.suresh.sacredtimeline.model.Auspiciousness.GREEN -> 0.85f
                com.suresh.sacredtimeline.model.Auspiciousness.ORANGE -> 0.6f
                com.suresh.sacredtimeline.model.Auspiciousness.RED, com.suresh.sacredtimeline.model.Auspiciousness.DARK_RED -> 0.3f
                else -> 0.7f
            }
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
}
