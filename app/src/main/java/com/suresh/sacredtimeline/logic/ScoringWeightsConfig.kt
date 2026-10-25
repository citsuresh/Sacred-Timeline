package com.suresh.sacredtimeline.logic

data class ScoringWeightsConfig(
    val tharaBalaWeight: Float = 0.20f,
    val transitWeight: Float = 0.20f,
    val dasaWeight: Float = 0.20f,
    val horaWeight: Float = 0.15f,
    val yogaWeight: Float = 0.15f,
    val tithiWeight: Float = 0.10f
) {
    init {
        val total = tharaBalaWeight + transitWeight + dasaWeight + horaWeight + yogaWeight + tithiWeight
        require(kotlin.math.abs(total - 1.0f) < 0.001f) { "Weights must sum to 1.0 (current sum: $total)" }
    }
}
