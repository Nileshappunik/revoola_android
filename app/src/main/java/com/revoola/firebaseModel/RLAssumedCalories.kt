package com.revoola.firebaseModel

data class RLAssumedCalories(val Female: RLAssumedCalorieDetails? = null,
                             val Male: RLAssumedCalorieDetails? = null)

data class RLAssumedCalorieDetails(
    val assumedcaloriesAge: Double = 0.0,
    val assumedcaloriesAgeCo: Double = 0.0,
    val assumedcaloriesFactor: Double = 0.0,
    val assumedcaloriesFormula: String = "",
    val assumedcaloriesX: Double = 0.0,
    val assumedcaloriesY: Double = 0.0,
    val assumedcaloriesZ: Double = 0.0
)
