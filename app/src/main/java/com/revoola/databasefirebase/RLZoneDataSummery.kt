package com.revoola.databasefirebase

import java.io.Serializable

data class RLZoneDataSummery(
    val avgCadence: Double = 0.0,
    val avgHr: Any = 0,
    val avgPower: Any = 0,
    val avgPowerFromDevice: Any = 0,
    val avgSpeed: Double = 0.0,
    val burntCalories: Double = 0.0,
    val distance: Double = 0.0,
    val remark: String = "android",
    val seconds: Int = 0,
    val totalRev: Double = 0.0
): Serializable

data class RLZoneDataDetails(
    var burntCalories:Double= 0.0,
    var distance:Double = 0.0,
    var remark:String = "android",
    var seconds:Int = 0,
    var totalRev:Double = 0.0,
): Serializable
