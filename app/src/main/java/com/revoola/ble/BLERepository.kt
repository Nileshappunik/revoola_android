package com.revoola.ble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.wearable.Wearable
import com.revoola.commonobject.RLTools
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BLERepository(private val context: Context) {

    // Add these properties to track notification characteristics
    private var heartRateCharacteristic: BluetoothGattCharacteristic? = null
    private var speedCharacteristic: BluetoothGattCharacteristic? = null
    private var watchCharacteristic: BluetoothGattCharacteristic? = null


    private val bluetoothLeScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private val _bleFlow = MutableStateFlow<RLBLEResult?>(null)
    val bleFlow: StateFlow<RLBLEResult?> = _bleFlow.asStateFlow()

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val SPEED_SERVICE_UUID = UUID.fromString("00001816-0000-1000-8000-00805f9b34fb")

        private val CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        private var INSTANCE: BLERepository? = null

        fun getInstance(context: Context): BLERepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: BLERepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        _bleFlow.value = RLBLEResult.RLConnectionState(
                            deviceName = gatt.device.name ?: "Unknown Device",
                            isConnected = true
                        )
                        gatt.discoverServices()
                    }else {
                        _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
                    }

                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _bleFlow.value = RLBLEResult.RLConnectionState(
                        deviceName = gatt.device.name ?: "Unknown Device",
                        isConnected = false
                    )
                    cleanupConnection()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setupCharacteristicNotifications(gatt)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            parseCharacteristicData(characteristic)
        }
    }

    private fun parseCharacteristicData(characteristic: BluetoothGattCharacteristic) {
        when (characteristic.service.uuid) {
            HEART_RATE_SERVICE_UUID -> parseHeartRateData(characteristic)
            SPEED_SERVICE_UUID -> parseSpeedData(characteristic)
        }
    }

    private fun parseHeartRateData(characteristic: BluetoothGattCharacteristic) {
        val flag = characteristic.value[0].toInt()
        val format = if (flag and 0x01 != 0) {
            BluetoothGattCharacteristic.FORMAT_UINT16
        } else {
            BluetoothGattCharacteristic.FORMAT_UINT8
        }
        val heartRate = characteristic.getIntValue(format, 1).toString()

        _bleFlow.value = RLBLEResult.RLSensorData(heartRate = heartRate)
    }

    private fun parseSpeedData(characteristic: BluetoothGattCharacteristic) {
        val data = characteristic.value
        val flags = data[0].toInt()
        val wheelRevolutionPresent = (flags and 0x01) != 0
        val crankRevolutionPresent = (flags and 0x02) != 0
        var offset = 1

        var speed = ""
        var avgSpeed = ""
        var distance = ""
        var cadence = ""
        var calories = ""

        if (wheelRevolutionPresent) {
            val cumulativeWheelRevolutions = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8) or
                    ((data[offset + 2].toInt() and 0xFF) shl 16) or
                    ((data[offset + 3].toInt() and 0xFF) shl 24)
            offset += 4

            val lastWheelEventTime = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)
            offset += 2

            // Calculate speed and distance
            val wheelCircumference = 2.105 // meters
            if (lastWheelRevolutions != 0 && lastWheelEventTime != 0) {
                val wheelRevolutionDiff = cumulativeWheelRevolutions - lastWheelRevolutions
                val wheelEventTimeDiff = (lastWheelEventTime - this.lastWheelEventTime) / 1024.0 // Convert to seconds

                if (wheelEventTimeDiff != 0.0) {
                    val speedMps = (wheelRevolutionDiff * wheelCircumference) / wheelEventTimeDiff
                    val speedKph = speedMps * 3.6
                    speed = String.format("%.2f", speedKph)

                    // Calculate distance
                    val distanceMeters = wheelRevolutionDiff * wheelCircumference
                    distance = String.format("%.2f", distanceMeters)

                    // Calculate average speed
                    totalDistance += distanceMeters / 1000.0 // Convert to kilometers
                    totalTime += wheelEventTimeDiff / 3600.0 // Convert to hours
                    val averageSpeed = totalDistance / totalTime
                    avgSpeed = String.format("%.2f", averageSpeed)

                    // Calculate calories (simplified calculation)
                    val caloriesPerHour = when {
                        speedKph > 25 -> 740 // High intensity
                        speedKph > 15 -> 520 // Medium intensity
                        else -> 280 // Low intensity
                    }
                    val timeInHours = wheelEventTimeDiff / 3600.0
                    calories = String.format("%.2f", caloriesPerHour * timeInHours)
                }
            }

            lastWheelRevolutions = cumulativeWheelRevolutions
            this.lastWheelEventTime = lastWheelEventTime
        }

        if (crankRevolutionPresent) {
            val cumulativeCrankRevolutions = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)
            offset += 2

            val lastCrankEventTime = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)

            if (lastCrankRevolutions != 0 && lastCrankEventTime != 0) {
                val crankRevolutionDiff = cumulativeCrankRevolutions - lastCrankRevolutions
                val crankEventTimeDiff = (lastCrankEventTime - this.lastCrankEventTime) / 1024.0

                if (crankEventTimeDiff != 0.0) {
                    val cadenceRpm = (crankRevolutionDiff / crankEventTimeDiff) * 60
                    cadence = String.format("%.2f", cadenceRpm)
                }
            }

            lastCrankRevolutions = cumulativeCrankRevolutions
            this.lastCrankEventTime = lastCrankEventTime
        }

        _bleFlow.value = RLBLEResult.RLSensorData(
            speed = speed,
            avgSpeed = avgSpeed,
            distance = distance,
            cadence = cadence,
            calories = calories
        )
    }

    // Add necessary class variables
    private var lastWheelRevolutions: Int = 0
    private var lastWheelEventTime: Int = 0
    private var lastCrankRevolutions: Int = 0
    private var lastCrankEventTime: Int = 0
    private var totalDistance: Double = 0.0
    private var totalTime: Double = 0.0

    private fun cleanupConnection() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt?.close()
            bluetoothGatt = null
        } else {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun startScan() {
        if (!hasRequiredPermissions()) {
            _bleFlow.value = RLBLEResult.RLError("Missing required permissions")
            return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            val filters = listOf(
                ScanFilter.Builder().setServiceUuid(ParcelUuid(HEART_RATE_SERVICE_UUID)).build(),
                ScanFilter.Builder().setServiceUuid(ParcelUuid(SPEED_SERVICE_UUID)).build())
            bluetoothLeScanner.startScan(filters, settings, scanCallback)
            //bluetoothLeScanner.startScan(scanCallback)

        } else {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_SCAN permission")
        }
    }


    fun stopScan() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
           if (bluetoothLeScanner!=null){
               bluetoothLeScanner.stopScan(scanCallback)
           }
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                // Determine device type based on advertised services or name
                val deviceType = when {
                    result.scanRecord?.serviceUuids?.any {
                        it.uuid == HEART_RATE_SERVICE_UUID
                    } == true -> RLDeviceType.HEART_RATE
                    result.scanRecord?.serviceUuids?.any {
                        it.uuid == SPEED_SERVICE_UUID
                    } == true -> RLDeviceType.SPEED
                    // Add more conditions for CADENCE if needed
                    else -> RLDeviceType.SPEED // Default type or determine based on your needs
                }

                _bleFlow.value = RLBLEResult.RLDeviceFound(
                    deviceName = device.name ?: "Unknown Device",
                    deviceAddress = device.address,
                    deviceType = deviceType,
                    isWatch = false
                )
            }
        }
        override fun onScanFailed(errorCode: Int) {
            _bleFlow.value = RLBLEResult.RLError("Scan failed with error code: $errorCode")
        }
    }
    
    // Add a method to check Bluetooth state
    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    // Add a method to request Bluetooth enable
    fun requestBluetoothEnable(activity: Activity) {
        if (!isBluetoothEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            }
        }
    }

    // Modify your existing setupCharacteristicNotifications to store the characteristics
    private fun setupCharacteristicNotifications(gatt: BluetoothGatt) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            return
        }

        try {
            val services = gatt.services

            for (service in services) {
                when (service.uuid) {
                    HEART_RATE_SERVICE_UUID -> {
                        val characteristic = service.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"))
                        characteristic?.let {
                            heartRateCharacteristic = it
                            enableCharacteristicNotification(gatt, it)
                        }
                    }
                    SPEED_SERVICE_UUID -> {
                        val characteristic = service.getCharacteristic(UUID.fromString("00002a5b-0000-1000-8000-00805f9b34fb"))
                        characteristic?.let {
                            speedCharacteristic = it
                            enableCharacteristicNotification(gatt, it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _bleFlow.value = RLBLEResult.RLError("Failed to setup notifications: ${e.message}")
        }
    }

    private fun enableCharacteristicNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            return
        }
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
        descriptor?.let { desc ->
            desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(desc)
        }
    }

    fun connectToDevice(deviceAddress: String) {
        if (!hasRequiredPermissions()) {
            _bleFlow.value = RLBLEResult.RLError("Missing required permissions")
            return
        }
        try {
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
                bluetoothGatt?.requestMtu(517) // Request maximum MTU size
            } else {
                _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            }
        } catch (e: Exception) {
            _bleFlow.value = RLBLEResult.RLError("Failed to connect: ${e.message}")
        }
    }
    fun disconnectDevice(deviceAddress: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Check if this is the currently connected device
                if (bluetoothGatt?.device?.address == deviceAddress) {
                    bluetoothGatt?.let { gatt ->
                        gatt.disconnect()
                        _bleFlow.value = RLBLEResult.RLConnectionState(
                            deviceName = gatt.device.name ?: "Unknown Device",
                            isConnected = false
                        )
                        cleanupConnection()
                    }
                } else {
                    _bleFlow.value = RLBLEResult.RLError("Device not connected: $deviceAddress")
                }
            } catch (e: Exception) {
                _bleFlow.value = RLBLEResult.RLError("Failed to disconnect: ${e.message}")
            }
        } else {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
        }
    }

    // Add notification control functions
    fun pauseNotifications() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            return
        }

        bluetoothGatt?.let { gatt ->
            heartRateCharacteristic?.let { characteristic ->
                gatt.setCharacteristicNotification(characteristic, false)
            }
            speedCharacteristic?.let { characteristic ->
                gatt.setCharacteristicNotification(characteristic, false)
            }
            watchCharacteristic?.let { characteristic ->
                gatt.setCharacteristicNotification(characteristic, false)
            }
            _bleFlow.value = RLBLEResult.RLConnectionState(
                deviceName = gatt.device.name ?: "Unknown Device",
                isConnected = true,
                isPaused = true
            )
        }
    }

    fun resumeNotifications() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            return
        }

        bluetoothGatt?.let { gatt ->
            heartRateCharacteristic?.let { characteristic ->
                enableCharacteristicNotification(gatt, characteristic)
            }
            speedCharacteristic?.let { characteristic ->
                enableCharacteristicNotification(gatt, characteristic)
            }
            _bleFlow.value = RLBLEResult.RLConnectionState(
                deviceName = gatt.device.name ?: "Unknown Device",
                isConnected = true,
                isPaused = false
            )
        }
    }

    fun stopNotifications() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            _bleFlow.value = RLBLEResult.RLError("Missing BLUETOOTH_CONNECT permission")
            return
        }

        bluetoothGatt?.let { gatt ->
            heartRateCharacteristic?.let { characteristic ->
                gatt.setCharacteristicNotification(characteristic, false)
                val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                descriptor?.let { desc ->
                    desc.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(desc)
                }
            }
            speedCharacteristic?.let { characteristic ->
                gatt.setCharacteristicNotification(characteristic, false)
                val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG)
                descriptor?.let { desc ->
                    desc.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(desc)
                }
            }

            gatt.disconnect()
            cleanupConnection()

            _bleFlow.value = RLBLEResult.RLConnectionState(
                deviceName = gatt.device.name ?: "Unknown Device",
                isConnected = false
            )
        }
    }


}


