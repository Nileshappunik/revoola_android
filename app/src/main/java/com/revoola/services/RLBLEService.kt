package com.revoola.services

import android.Manifest
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import com.revoola.utils.RLConstants
import java.lang.StringBuilder
import java.util.*

class RLBLEService : Service() {
    val TAG: String = RLBLEService::class.java.simpleName
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private val foundDevicesArray = mutableListOf<BluetoothDevice>()
    val heartRateDevices = mutableListOf<BluetoothDevice>()
    val speedDevices = mutableListOf<BluetoothDevice>()

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
    fun RLstartScan(isRideWay:Boolean) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
            Log.d(TAG,"Request Necessary Permission")
        }
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        val filtersSpeedHeart = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(
            UUID_HEART_RATE_SERVICE
        )).build(), ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID_SPEED_SERVICE)).build())
        val filtersHeart = listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(
            UUID_HEART_RATE_SERVICE
        )).build())
        if (isRideWay){
            bluetoothLeScanner?.startScan(filtersSpeedHeart, settings,RLscanCallback)
        }else{
            bluetoothLeScanner?.startScan(filtersHeart, settings,RLscanCallback)
        }
    }
    fun RLstopScan() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // Request necessary permission
        }
       // bluetoothLeScanner?.stopScan(RLscanCallback)
        bluetoothLeScanner?.stopScan(RLscanCallback)
    }
    private val RLscanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val scanRecord = result.scanRecord
            if (ContextCompat.checkSelfPermission(this@RLBLEService, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request necessary permission
            }
            scanRecord?.serviceUuids?.forEach { serviceUuid ->
                when (serviceUuid.uuid) {
                    UUID_HEART_RATE_SERVICE -> {
                        if (!heartRateDevices.contains(device)) {
                            heartRateDevices.add(device)
                            Log.d(TAG, "Heart Rate Device: ${device.name}, ${device.address}")
                            RLbroadcastDeviceFoundHeart(device.name,device.address,"HEARTRATESENSOR")
                        }
                    }
                    UUID_SPEED_SERVICE -> {
                        if (!speedDevices.contains(device)) {
                            speedDevices.add(device)
                            Log.d(TAG, "Speed Device: ${device.name}, ${device.address}")
                            RLbroadcastDeviceFoundSpeed(device.name,device.address,"SPEEDSENSOR")
                        }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan failed with error: $errorCode")
        }
    }



   /* private val RLscanCallback = object : ScanCallback() {
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
                    RLbroadcastDeviceFoundHeart(gatt.device.name,gatt.device.address,"HEARTRATESENSOR")
                }else if (hasSpeedService){
                    Log.d(TAG,"Device Found Speed:- "+gatt.device.name)
                    RLbroadcastDeviceFoundSpeed(gatt.device.name,gatt.device.address,"SPEEDSENSOR")
                  //  RLbroadcastDeviceFoundSpeed(gatt.device.name,gatt.device.address,"CADENCESENSOR")

                }
            } else {
                Log.d(TAG, "GATT_SUCCESS FAIL")
            }
            gatt.close()
        }

    }*/


    //Device Connect Request
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

    //Broadcast Device List
    private fun RLbroadcastDeviceFoundSpeed(deviceName: String, deviceAddress:String, sensorType:String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
        }
        val intent = Intent("ACTION_DEVICE_FOUND_SPEED")
        intent.putExtra("DEVICE_NAME", deviceName)
        intent.putExtra("DEVICE_ADDRESS", deviceAddress)
        intent.putExtra("DEVICE_TYPE", sensorType)
        sendBroadcast(intent)
    }
    private fun RLbroadcastDeviceFoundHeart(deviceName: String, deviceAddress:String, sensorType:String) {
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

    //Broadcast Data From Ble Device
    private fun RLbroadcastDataRetrieved(data: String) {
        Log.d(TAG, "Received heart rate: $data")
        val intent = Intent("ACTION_DATA_RETRIEVED_HEART")
        intent.putExtra("EXTRA_DATA", data)
        sendBroadcast(intent)
    }
    private fun RLbroadcastSpeedDataRetrieved(data: String, speed:String, avgspeed:String, distance:String, cadence:String,calories:String) {
        Log.d(TAG,data)
        val intent = Intent("ACTION_DATA_RETRIEVED")
        intent.putExtra("EXTRA_DATA", data)
        intent.putExtra("SPEED",speed)
        intent.putExtra("AvgSPEED",avgspeed)
        intent.putExtra("DISTANCE",distance)
        intent.putExtra("CADENCE",cadence)
        intent.putExtra("CALORIES",calories)
        sendBroadcast(intent)

    }

    // Broadcast Last Connect  Ble Device
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

    //Convert Speed Cadence Data
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
        var CALORIES=""

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
                         SPEED=formattedNumber
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
                         DISTANCE=formatteddistancemeter// +" meter"
                    }
                    // Calculate average speed
                    totalDistance += distance
                    totalTime += wheelEventTimeDiff / 3600.0 // Convert to hours
                    val averageSpeed = totalDistance / totalTime
                    if (averageSpeed>0){
                        val formattedAvgSpeed = String.format("%.2f", averageSpeed)
                        Log.d(TAG, "AvgSpeed: $formattedAvgSpeed km/h")
                        speedcadence.append("AvgSpeed: $formattedAvgSpeed km/h  ")
                         AvgSPEED=formattedAvgSpeed//+" km/h"
                    }
                    val  startTime=distance / speedkm
                   val calories=  RlcalculateCaloriesBasedOnSpeed(speedkm.toFloat(),startTime)
                    if (calories>0){
                        val formattedcalories = String.format("%.2f", calories)
                        Log.d(TAG, "CaloriesBurned: $formattedcalories")
                        CALORIES=formattedcalories
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

            RLbroadcastSpeedDataRetrieved("Data:- "+speedcadence,SPEED,AvgSPEED,DISTANCE,CADENCE,CALORIES)
            // Update cadence last values
            lastCrankRevolutions = cumulativeCrankRevolutions
            this.lastCrankEventTime = lastCrankEventTime
        }
    }

    //When Service Destroy Then Close BluetoothGatt
    fun RLdisconnectFromDevice(){
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
        super.onDestroy()
        RLdisconnectFromDevice()
    }

    //When User Click Pause Stop And Resume Button Event Get Notification
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

    //Calories Calculated
    private fun RlcalculateCaloriesBasedOnSpeed(speed: Float, startTime: Double):Double {
        // Assuming a static weight for now; this can be dynamic based on user input
        val weightInKg = RLConstants.weightInKg// User's weight in kilograms

        val caloriesBurned = calculateCalories(speed.toDouble(), weightInKg, startTime)

        return caloriesBurned
    }
    private fun calculateCalories(speed: Double, weightInKg: Double, durationInMinutes: Double): Double {
        val metValue = when {
            speed > 8 -> 7.5 // Running
            speed > 4 -> 5.0 // Jogging
            else -> 3.8 // Walking
        }
        return metValue * weightInKg * (durationInMinutes / 60)
    }

}
