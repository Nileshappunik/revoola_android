package com.revoola.ble

sealed class BLEResult {
    data class DeviceFound(
        val deviceName: String,
        val deviceAddress: String,
        val deviceType: DeviceType,
        val isConnected: Boolean = false
    ) : BLEResult()

    data class ConnectionState(
        val deviceName: String,
        val isConnected: Boolean
    ) : BLEResult()

    data class SensorData(
        val heartRate: String = "",
        val speed: String = "",
        val avgSpeed: String = "",
        val distance: String = "",
        val cadence: String = "",
        val calories: String = ""
    ) : BLEResult()

    data class Error(val message: String) : BLEResult()
}

