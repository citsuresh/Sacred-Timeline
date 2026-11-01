---
name: kotlin-multi-factor-scoring-engine
description: Provides architectural patterns and implementation workflows for building multi-factor analytical or astrological scoring engines in Kotlin. Covers weighted factor aggregation, normalized 0–100% scoring, qualitative band classification, itemized contributors breakdown, and personalization using user profile data.
---

# Kotlin Multi-Factor Scoring Engine Skill

## Overview
This skill guides developers through implementing robust multi-factor scoring engines in Kotlin. It is ideal for applications that evaluate diverse real-time domain attributes (such as astronomical/ephemeris positions, user birth profiles, temporal factors, and transit influences) into a normalized favorability score with itemized breakdowns.

---

## Architecture & Data Models

### 1. Score Result & Contributors (`ScoreResult.kt`)
```kotlin
data class ScoreResult(
    val score: Int, // Normalized 0 to 100
    val band: String, // e.g. "Very Favorable", "Favorable", "Mixed", "Challenging", "Avoid"
    val breakdown: List<ScoreContributor>
)

data class ScoreContributor(
    val name: String,
    val points: Int,
    val maxPoints: Int,
    val description: String
)
```

### 2. Configurable Weights (`ScoringWeightsConfig.kt`)
```kotlin
data class ScoringWeightsConfig(
    val tharaBalaWeight: Float = 0.25f,
    val transitWeight: Float = 0.20f,
    val dasaWeight: Float = 0.15f,
    val horaWeight: Float = 0.15f,
    val yogaWeight: Float = 0.15f,
    val tithiWeight: Float = 0.10f
)
```

### 3. Engine Implementation (`ScoringEngine.kt`)
```kotlin
object ScoringEngine {
    fun calculateScore(
        dayData: DayData,
        profile: BirthProfile?,
        time: LocalTime,
        weights: ScoringWeightsConfig = ScoringWeightsConfig()
    ): ScoreResult {
        // Compute individual factor scores based on dayData, profile, and time
        val totalScore = ...
        val band = when {
            totalScore >= 81 -> "Very Favorable"
            totalScore >= 61 -> "Favorable"
            totalScore >= 41 -> "Mixed"
            totalScore >= 21 -> "Challenging"
            else -> "Avoid"
        }
        
        val breakdown = listOf(
            ScoreContributor("Factor 1", points, maxPoints, "Description"),
            ...
        )
        return ScoreResult(score = totalScore, band = band, breakdown = breakdown)
    }
}
```
