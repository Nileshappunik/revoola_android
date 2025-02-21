package com.revoola.ble

sealed class RLBLEResult {

    data class RLDeviceFound(
        val deviceName: String,
        val deviceAddress: String,
        val deviceType: RLDeviceType,
        val isWatch: Boolean = false
    ) : RLBLEResult()

    data class RLConnectionState(
        val deviceName: String,
        val isConnected: Boolean,
        val isPaused: Boolean = false
    ) : RLBLEResult()

    data class RLSensorData(
        val heartRate: String = "",
        val speed: String = "",
        val avgSpeed: String = "",
        val distance: String = "",
        val cadence: String = "",
        val calories: String = ""
    ) : RLBLEResult()

    data class RLError(val message: String) : RLBLEResult()
}

