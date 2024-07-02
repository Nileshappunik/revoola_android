package com.example.myfirstapp.services

import android.Manifest
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.myfirstapp.utils.RLConstants
import java.lang.StringBuilder
import java.util.*

class RLBLEService : Service() {
    val TAG: String = RLBLEService::class.java.simpleName
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val foundDevicesArray = mutableListOf<BluetoothDevice>()

   // private val deviceGattMap = mutableMapOf<BluetoothDevice, BluetoothGatt>()

    private var notificationCharacteristic: BluetoothGattCharacteristic? = null
    private var bluetoothGatt: BluetoothGatt? = null

    private var lastWheelRevolutions: Int = 0
    private var lastWheelEventTime: Int = 0
    private var lastCrankRevolutions: Int = 0
    private var lastCrankEventTime: Int = 0
    private val wheelCircumference = 2.105
    private var totalDistance = 0.0
    private var totalTime = 0.0

    companion object {
        // Replace with your own UUIDs
        val UUID_HEART_RATE_SERVICE: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val UUID_HEART_RATE_CHARACTERISTIC: UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")

        val UUID_CLIENT_CHARACTERISTIC_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        val UUID_SPEED_SERVICE: UUID = UUID.fromString("00001816-0000-1000-8000-00805f9b34fb")
        val UUID_SPEED_CHARACTERISTIC: UUID = UUID.fromString("00002A5B-0000-1000-8000-00805f9b34fb")

    }
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    private val binder = RLLocalBinder()
    inner class RLLocalBinder : Binder() {
        fun getService(): RLBLEService = this@RLBLEService
    }
    override fun onCreate() {
        super.onCreate()
        RLinitializeBluetooth()
    }
    private fun RLinitializeBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    }
    fun RLstartScan() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
            Log.d(TAG,"Request Necessary Permission")
        }
        Log.d(TAG,"START SCAN")
        bluetoothLeScanner?.startScan(RLscanCallback)
    }
    fun RLstopScan() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
        }
        bluetoothLeScanner?.stopScan(RLscanCallback)
    }
    private val RLscanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                if (!device.name.isNullOrEmpty()){
                    if (!foundDevicesArray.contains(device)){
                        foundDevicesArray.add(device)
                        RLconnectToDevicelist(device)
                        //Log.d(TAG,"Device Name"+device.name)
                    }
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                result.device?.let { device ->
                    if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // Request necessary permission
                    }
                    if (!device.name.isNullOrEmpty() ){
                        if (!foundDevicesArray.contains(device)){
                            foundDevicesArray.add(device)
                            RLconnectToDevicelist(device)
                        }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error: $errorCode")
        }
    }
    fun RLconnectToDevicelist(device: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
        }
        bluetoothGatt = device.connectGatt(this, false,RLgattCallbacklist)

    }
    private val RLgattCallbacklist = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
               // Log.d(TAG, "Connected to GATT server.")
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                gatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
               // Log.d(TAG, "Disconnected from GATT server.")
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request necessary permissions
            }
            if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                val hasHeartRateService = gatt.services.any { it.uuid == UUID_HEART_RATE_SERVICE }
                val hasSpeedService = gatt.services.any { it.uuid == UUID_SPEED_SERVICE }
                if (hasHeartRateService) {
                    Log.d(TAG,"Device Found HeartRate:- "+gatt.device.name)
                    RLbroadcastDeviceFound(gatt.device.name,gatt.device.address,"HEARTRATESENSOR")
                }else if (hasSpeedService){
                    Log.d(TAG,"Device Found Speed:- "+gatt.device.name)
                    RLbroadcastDeviceFound(gatt.device.name,gatt.device.address,"SPEEDSENSOR")
                    RLbroadcastDeviceFound(gatt.device.name,gatt.device.address,"CADENCESENSOR")
                }


            } else {
                Log.d(TAG, "GATT_SUCCESS FAIL")
            }
            gatt.close()
        }

    }
    fun RLconnectToDevice(device: BluetoothDevice) {
        if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
        }
        bluetoothGatt = device.connectGatt(this, false, RLgattCallback)

    }
    private val RLgattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected to GATT server.")
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                gatt?.discoverServices()
                RLbroadcastConnectionState(gatt?.device?.name ?: "Unknown Device", true)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Disconnected from GATT server.")
                bluetoothGatt?.close()
                bluetoothGatt = null
                RLbroadcastConnectionState(gatt?.device?.name ?: "Unknown Device", false)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request necessary permissions
            }
            if (status == BluetoothGatt.GATT_SUCCESS && gatt != null) {
                val hasHeartRateService = gatt.services.any { it.uuid == UUID_HEART_RATE_SERVICE }
                val hasSpeedService = gatt.services.any { it.uuid == UUID_SPEED_SERVICE }
                if (hasHeartRateService) {
                    RLbroadcastConnectionDeviceType(RLConstants.HEARTSENSOR, true)
                    RLheartRateServicesDiscovered(gatt)
                }else if (hasSpeedService){
                    RLbroadcastConnectionDeviceType(RLConstants.SPEEDSENSOR, true)
                    RLspeedAndCadenceServicesDiscovered(gatt)
                }
            } else {
                Log.d(TAG, "GATT_SUCCESS FAIL")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            Log.d(TAG,"Received data: Start")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic.value
                // Handle received data
                Log.d(TAG,"Received data: ${data}")
            }else{
                Log.d(TAG,"Received data: Fail")
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Descriptor write success")
            } else {
                Log.e(TAG, "Descriptor write fail, status: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic.value
            if (UUID_HEART_RATE_CHARACTERISTIC == characteristic.uuid) {
                RLheartRateGettoDevice(data,characteristic)
            }else if (UUID_SPEED_CHARACTERISTIC == characteristic.uuid){
                //speedAndCadenceGettoDevice(data,characteristic)
                RLparseSpeedCadenceData(data)
            } else {
                Log.d(TAG,"Received data: Fail")
            }
        }
    }
    private fun RLheartRateServicesDiscovered(gatt: BluetoothGatt) {
        if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permissions
        }
        val service = gatt.getService(UUID_HEART_RATE_SERVICE)
        val heartRateCharacteristic = service?.getCharacteristic(UUID_HEART_RATE_CHARACTERISTIC)
        heartRateCharacteristic?.let {
            Log.d(TAG, "Start Reading... ")
            notificationCharacteristic = heartRateCharacteristic
            gatt.setCharacteristicNotification(it,true)
            val descriptor = heartRateCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
            if (descriptor!=null){
                descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }else{
                Log.e(TAG, "Descriptor does not support")
            }

            if ((it.properties and BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                Log.d(TAG, "Characteristic supports read")
                gatt.readCharacteristic(it)
            } else {
                Log.e(TAG, "Characteristic does not support read")
            }

        }
    }
    private fun RLspeedAndCadenceServicesDiscovered(gatt: BluetoothGatt) {
        if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permissions
        }
        val service = gatt.getService(UUID_SPEED_SERVICE)
        val SppedCharacteristic = service?.getCharacteristic(UUID_SPEED_CHARACTERISTIC)
        SppedCharacteristic?.let {
            Log.d(TAG, "Start Reading... ")
            notificationCharacteristic = SppedCharacteristic
            gatt.setCharacteristicNotification(it,true)
            val descriptor = SppedCharacteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
            if (descriptor!=null){
                descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }else{
                Log.e(TAG, "Descriptor does not support")
            }

            if ((it.properties and BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                Log.d(TAG, "Characteristic supports read")
                gatt.readCharacteristic(it)
            } else {
                Log.e(TAG, "Characteristic does not support read")
            }

        }
    }
    private fun RLheartRateGettoDevice(data: ByteArray?, characteristic: BluetoothGattCharacteristic) {
        if (data != null && data.isNotEmpty()) {
            val flag = data[0].toInt()
            val format = if (flag and 0x01 != 0) {
                BluetoothGattCharacteristic.FORMAT_UINT16
            } else {
                BluetoothGattCharacteristic.FORMAT_UINT8
            }
            val heartRate = characteristic.getIntValue(format, 1).toInt()
            RLbroadcastDataRetrieved(heartRate.toString())
        } else {
            Log.d(TAG, "Characteristic value is null or empty")
        }
    }
    private fun RLbroadcastDeviceFound(deviceName: String, deviceAddress:String, sensorType:String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
        }
        val intent = Intent("ACTION_DEVICE_FOUND")
        intent.putExtra("DEVICE_NAME", deviceName)
        intent.putExtra("DEVICE_ADDRESS", deviceAddress)
        intent.putExtra("DEVICE_TYPE", sensorType)
        sendBroadcast(intent)
    }
    private fun RLbroadcastDataRetrieved(data: String) {
        Log.d(TAG, "Received heart rate: $data")
        val intent = Intent("ACTION_DATA_RETRIEVED_HEART")
        intent.putExtra("EXTRA_DATA", data)
        sendBroadcast(intent)
    }
    private fun RLbroadcastSpeedDataRetrieved(data: String, speed:String, avgspeed:String, distance:String, cadence:String) {
        Log.d(TAG,data)
        val intent = Intent("ACTION_DATA_RETRIEVED")
        intent.putExtra("EXTRA_DATA", data)
        intent.putExtra("SPEED",speed)
        intent.putExtra("AvgSPEED",avgspeed)
        intent.putExtra("DISTANCE",distance)
        intent.putExtra("CADENCE",cadence)
        sendBroadcast(intent)

    }
    private fun RLbroadcastConnectionState(deviceName: String, isConnected: Boolean) {
        val intent = Intent("ACTION_CONNECTION_STATE_CHANGED")
        intent.putExtra("device_name", deviceName)
        intent.putExtra("is_connected", isConnected)
        sendBroadcast(intent)
    }

    private fun RLbroadcastConnectionDeviceType(deviceType: String, isConnected: Boolean) {
        val intent = Intent("ACTION_CONNECTION_DEVICE_TYPE")
        intent.putExtra("device_type", deviceType)
        intent.putExtra("is_connected", isConnected)
        sendBroadcast(intent)
    }
    private fun RLparseSpeedCadenceData(data: ByteArray) {

        val flags = data[0].toInt()
        val wheelRevolutionPresent = (flags and 0x01) != 0
        val crankRevolutionPresent = (flags and 0x02) != 0
        var offset = 1
        var speedcadence=StringBuilder()
        var SPEED=""
        var AvgSPEED=""
        var DISTANCE=""
        var CADENCE=""

        if (wheelRevolutionPresent) {
            val cumulativeWheelRevolutions = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8) or
                    ((data[offset + 2].toInt() and 0xFF) shl 16) or
                    ((data[offset + 3].toInt() and 0xFF) shl 24)
            offset += 4

            val lastWheelEventTime = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)
            offset += 2

            // Calculate speed
            if (lastWheelRevolutions != 0 && lastWheelEventTime != 0) {
                val wheelRevolutionDiff = cumulativeWheelRevolutions - lastWheelRevolutions
                val wheelEventTimeDiff = (lastWheelEventTime - this.lastWheelEventTime) / 1024.0 // Convert to seconds

                if (wheelEventTimeDiff != 0.0) {
                    val speed = (wheelRevolutionDiff * wheelCircumference) / wheelEventTimeDiff
                    val speedkm=speed * 3.6
                    if (speedkm>0){
                        val formattedNumber = String.format("%.2f", speedkm)
                        // Use the speed value as needed
                         SPEED=formattedNumber+" km/h"
                        Log.d(TAG, "Speed: $formattedNumber km/h")
                        speedcadence.append("Speed: $formattedNumber km/h  ")
                    }
                    // Update total distance and time
                    val distance = wheelRevolutionDiff * wheelCircumference / 1000.0 // Convert to kilometers
                    val distancemeter = wheelRevolutionDiff * wheelCircumference
                    if (distancemeter>0){
                        val formatteddistancemeter = String.format("%.2f", distancemeter)
                        Log.d(TAG, "Distance: $formatteddistancemeter meter")
                        speedcadence.append("Distance: $formatteddistancemeter meter  ")
                         DISTANCE=formatteddistancemeter +" meter"
                    }
                    // Calculate average speed
                    totalDistance += distance
                    totalTime += wheelEventTimeDiff / 3600.0 // Convert to hours
                    val averageSpeed = totalDistance / totalTime
                    if (averageSpeed>0){
                        val formattedAvgSpeed = String.format("%.2f", averageSpeed)
                        Log.d(TAG, "AvgSpeed: $formattedAvgSpeed km/h")
                        speedcadence.append("AvgSpeed: $formattedAvgSpeed km/h  ")
                         AvgSPEED=formattedAvgSpeed+" km/h"
                    }
                }
            }
            // Update Speed last values
            lastWheelRevolutions = cumulativeWheelRevolutions
            this.lastWheelEventTime = lastWheelEventTime
        }

        if (crankRevolutionPresent) {
            val cumulativeCrankRevolutions = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)
            offset += 2

            val lastCrankEventTime = (data[offset].toInt() and 0xFF) or
                    ((data[offset + 1].toInt() and 0xFF) shl 8)

            // Calculate cadence
            if (lastCrankRevolutions != 0 && lastCrankEventTime != 0) {
                val crankRevolutionDiff = cumulativeCrankRevolutions - lastCrankRevolutions
                val crankEventTimeDiff = (lastCrankEventTime - this.lastCrankEventTime) / 1024.0 // Convert to seconds

                if (crankEventTimeDiff != 0.0) {
                    val cadence = (crankRevolutionDiff / crankEventTimeDiff) * 60 // Convert to RPM
                    val formattedNumber = String.format("%.2f", cadence)
                    // Use the cadence value as needed
                    Log.d(TAG, "Cadence: $formattedNumber RPM")
                    speedcadence.append(" Cadence: $formattedNumber RPM")
                    CADENCE=formattedNumber
                }
            }

            RLbroadcastSpeedDataRetrieved("Data:- "+speedcadence,SPEED,AvgSPEED,DISTANCE,CADENCE)
            // Update cadence last values
            lastCrankRevolutions = cumulativeCrankRevolutions
            this.lastCrankEventTime = lastCrankEventTime
        }
    }
    fun RLdisconnectFromDevice() {
        if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permissions
        }
        bluetoothGatt?.let {
            it.disconnect()
            it.close()
            bluetoothGatt = null
        }
        Log.d(TAG, "Service destroyed and BLE connection closed")
    }
    override fun onDestroy() {
        RLdisconnectFromDevice()
        super.onDestroy()
    }
    fun RLpauseNotifications() {
        bluetoothGatt?.let { gatt ->
            notificationCharacteristic?.let { characteristic ->
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                gatt.setCharacteristicNotification(characteristic, false)
            }
        }
    }
    fun RLresumeNotifications() {
        bluetoothGatt?.let { gatt ->
            notificationCharacteristic?.let { characteristic ->
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }
    }
    fun RLstopNotifications() {
        bluetoothGatt?.let { gatt ->
            notificationCharacteristic?.let { characteristic ->
                if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permission
                }
                gatt.setCharacteristicNotification(characteristic, false)
                val descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
                descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        }
    }

}
