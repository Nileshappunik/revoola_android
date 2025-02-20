package com.revoola.ble

data class BLEEvent(
    val deviceName: String = "",
    val deviceAddress: String = "",
    val deviceType: String = "",
    val isConnected: Boolean = false,
    val heartRate: String = "",
    val speed: String = "",
    val avgSpeed: String = "",
    val distance: String = "",
    val cadence: String = "",
    val calories: String = ""
)