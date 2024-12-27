package com.revoola.firebaseModel

data class RLChallengeRiderBody(
    val arrAvgPower: List<Int> = emptyList(),
    val arrAvgRevPercentage: List<Any?> = emptyList(), // Changed to Any? for flexibility
    val arrHr: List<Int> = emptyList(),
    val arrMaxRevPercentage: List<Any?> = emptyList(), // Changed to Any?
    val arrPower: List<Int> = emptyList(),
    val arrPowerFromDevice: List<Int> = emptyList(),
    val arrRevPercentage: List<Any?> = emptyList(), // Changed to Any?
    val arrRevSecond: List<Any?> = emptyList(), // Changed to Any?
    val classDate: Any? = null,
    val displayImage: String = "",
    val displayName: String = "",
    val flagImage: String = "",
    val flagName: String = "",
    val isPowerDeviceConnected: Boolean = false,
    val location: String = "",
    val totalRev: Any? = null, // Changed to Any?
    val visibilityflagforthatsession: Int = 0,
    val remark: String? = null // Optional field
)
