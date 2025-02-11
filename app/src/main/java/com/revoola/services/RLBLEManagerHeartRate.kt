package com.revoola.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class RLBLEManagerHeartRate(private val context: Context) {

    interface BLECallback {
        fun onHeartRateDataReceived(data: String)
        fun onDeviceConnected(device: BluetoothDevice)
        fun onDeviceDisconnected()
        fun onError(errorMessage: String)
    }

    companion object {
        const val REQUEST_CODE_BLE_PERMISSIONS = 1001
        const val ACTION_DATA_RETRIEVED_HEART = "ACTION_DATA_RETRIEVED_HEART"
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private var bleService: RLBLEService? = null
    private var isServiceBound = false
    private val permissions = mutableListOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }
    private var serviceConnection: ServiceConnection? = null
    private var broadcastReceiver: BroadcastReceiver? = null
    private var callback: BLECallback? = null

    fun setCallback(callback: BLECallback) {
        this.callback = callback
    }

    fun checkAndRequestPermissions(activity: Activity): Boolean {
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), REQUEST_CODE_BLE_PERMISSIONS)
            false
        } else {
            true
        }
    }

    @SuppressLint("MissingPermission")
    fun setupBluetooth(onReady: () -> Unit) {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivity(enableBtIntent)
        } else {
            onReady()
        }
    }

    fun startBLEService() {
        val intent = Intent(context, RLBLEService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RLBLEService.RLLocalBinder
                bleService = binder.getService()
                isServiceBound = true
                connectToLastDevice()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
                callback?.onDeviceDisconnected()
            }
        }
        context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_DATA_RETRIEVED_HEART -> {
                        val data = intent.getStringExtra("EXTRA_DATA")
                        callback?.onHeartRateDataReceived(data.toString())
                    }
                }
            }
        }

        val filter = IntentFilter(ACTION_DATA_RETRIEVED_HEART)
        context.registerReceiver(broadcastReceiver, filter)
    }

    private fun connectToLastDevice() {
        val lastDeviceAddress = com.revoola.utils.RLPrefManager.RLGetSomeStringValue(
            context, com.revoola.utils.RLPrefManager.last_device_connect, ""
        )
        if (lastDeviceAddress.isNotEmpty()) {
            val device = bluetoothAdapter.getRemoteDevice(lastDeviceAddress)
            if (device != null) {
                bleService?.RLconnectToDevice(device)
                callback?.onDeviceConnected(device)
            } else {
                callback?.onError("Device not found for address: $lastDeviceAddress")
            }
        } else {
            callback?.onError("No last device address saved")
        }
    }

    fun cleanup() {
        if (isServiceBound) {
            context.unbindService(serviceConnection!!)
            isServiceBound=false
        }
        context.unregisterReceiver(broadcastReceiver)
        serviceConnection = null
        broadcastReceiver = null
    }

    fun lrstopgetData(){
        if (isServiceBound) {
            bleService!!.RLstopNotifications()
        }
    }
    fun rlresumegetData(){
        if (isServiceBound) {
            bleService!!.RLresumeNotifications()
        }
    }
    fun rlpausegetData(){
        if (isServiceBound) {
            bleService!!.RLpauseNotifications()
        }
    }
}

