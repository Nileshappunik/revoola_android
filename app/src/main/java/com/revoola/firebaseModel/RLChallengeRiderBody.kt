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
data class RLRanking(
    var rank: Int = 0,
    val revSec: Int,
    val displayName: String?,
    val displayImage: String?,
    val flagName: String?,
    val flagImage: String?,
    val totalRev: Int,
    val revPercentage: Int,
    val maxRevPercentage: Int,
    val avgRevPercentage: Int,
    val hr: Int,
    val userId: String,
    var status: Int = 1,
    val location: String?,
    var isGhost: Boolean
)

data class RLLeaderBoardUser(
    val key: String,
    var displayName: String? = null,
    var displayImage: String? = null,
    var flagName: String? = null,
    var flagImage: String? = null,
    val arrRevSecond: List<Any?>,
    val arrRevPercentage: List<Any?>,
    val arrMaxRevPercentage: List<Any?>,
    val arrAvgRevPercentage: List<Any?>,
    val arrHr: List<Int>?,
    val location: String?
)