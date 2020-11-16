package com.example.dropspot.data.model.dto

import kotlin.math.roundToInt

data class CriterionScore(
    val criterionId: Long,
    val criterionName: String,
    val description: String,
    val score: Double
) {
    fun getRatingBarScore(): Int {
        return score.roundToInt()
    }


}