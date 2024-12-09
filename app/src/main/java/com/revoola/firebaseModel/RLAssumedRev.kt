package com.revoola.firebaseModel

data  class RLAssumedRev (
    val pilates: RLRevPerSecondData? = null,
    val ridein: RLRevPerMeterData? = null,
    val rideout: RLRevPerMeterData? = null,
    val run: RLRevPerMeterData? = null,
    val walk: RLRevPerMeterData? = null,
    val workout: RLRevPerSecondData? = null,
    val yoga: RLRevPerSecondData? = null
)

data class RLRevPerSecondData(
    val RevPerSecond: Double = 0.0
)

data class RLRevPerMeterData(
    val RevPerMeterClimbed: Double = 0.0,
    val RevPerMeterTravelled: Double = 0.0
)