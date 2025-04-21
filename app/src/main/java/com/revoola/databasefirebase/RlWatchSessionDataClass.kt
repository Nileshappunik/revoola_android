package com.revoola.databasefirebase


data  class RlWatchSessionDataClass (
    val cumArrayData: RLCumArrayData = RLCumArrayData(),
    val dataForDetails: RLDataForDetails = RLDataForDetails(),
    val normalArrayData: RLNormalArrayData = RLNormalArrayData(),
    val singleValueData: RLSingleValueData = RLSingleValueData(),
    val zoneData: Map<String, RLZoneDataDetails> = mapOf())

data class RLCumArrayData(
    val arrCumDistance: MutableList<Double> = mutableListOf(0.0),
    val arrCumElevation: MutableList<Double> = mutableListOf(0.0),
    val arrCumSpeed: MutableList<Double> = mutableListOf(0.0))


data class RLDataForDetails(
    val latLongDic: MutableList<HashMap<String, Any>> = mutableListOf(),
    val speedDic: List<Int> = listOf(0))

data class RLNormalArrayData(
    val arrBurntCalories: MutableList<Double> = mutableListOf(0.0),
    val arrCadence: MutableList<Int> = mutableListOf(0),
    val arrDistance: MutableList<Double> = mutableListOf(0.0),
    val arrElevation: MutableList<Double> = mutableListOf(0.0),
    val arrHr: MutableList<Int> = mutableListOf(),
    val arrPower: MutableList<Int> = mutableListOf(),
    val arrPowerFromDevice: MutableList<Int> = mutableListOf(0),
    val arrRevPercentage: MutableList<Double> = mutableListOf(0.0),
    val arrRevSecond: MutableList<Double> = mutableListOf(0.0),
    val arrSpeed: MutableList<Int> = mutableListOf(0))

data class RLSingleValueData(
    val burntCalories: Double = 0.0,
    val classType: String = "",
    val distance: Double = 0.0,
    val goal: String = "",
    val isClass: Boolean = false,
    val isMindClass: Boolean = false,
    val isPowerDeviceConnected: Boolean = false,
    val remark: String = "",
    val timestamp: Long = 0L,
    val totalElevation: Double = 0.0,
    val totalRev: Double = 0.0,
    val totalSteps: Int = 0,
    val totalTime: String = "",
    val typeOfGoal: String = "",
    val videoKey: String = "",
    val totalPower: Int = 0,
    val speedForOneMile: MutableList<Double> = mutableListOf(0.0),
    val speedForOneKm: MutableList<Double> =mutableListOf(0.0))




