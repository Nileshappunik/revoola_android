package com.revoola.ble

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BLEViewModel(private val bleRepository: BLERepository) : ViewModel() {
    private val _deviceList = MutableStateFlow<List<RLBLEResult.RLDeviceFound>>(emptyList())
    val deviceList: StateFlow<List<RLBLEResult.RLDeviceFound>> = _deviceList.asStateFlow()

    private val _sensorData = MutableStateFlow<RLBLEResult.RLSensorData?>(null)
    val sensorData: StateFlow<RLBLEResult.RLSensorData?> = _sensorData.asStateFlow()

    private val _connectionState = MutableStateFlow<RLBLEResult.RLConnectionState?>(null)
    val connectionState: StateFlow<RLBLEResult.RLConnectionState?> = _connectionState.asStateFlow()

    private val _bluetoothState = MutableStateFlow<BluetoothState>(BluetoothState.Unknown)
    val bluetoothState: StateFlow<BluetoothState> = _bluetoothState.asStateFlow()


    init {
        viewModelScope.launch {
            bleRepository.bleFlow.collect { result ->
                when (result) {
                    is RLBLEResult.RLDeviceFound -> updateDeviceList(result)
                    is RLBLEResult.RLConnectionState -> _connectionState.value = result
                    is RLBLEResult.RLSensorData -> _sensorData.value = result
                    is RLBLEResult.RLError -> handleError(result)
                    null -> {} // No action needed
                }
            }
        }
    }

    private fun updateDeviceList(device: RLBLEResult.RLDeviceFound) {
        _deviceList.update { currentList ->
            currentList.filter { it.deviceAddress != device.deviceAddress } + device
        }
    }

    private fun handleError(error: RLBLEResult.RLError) {
        // Handle error appropriately
    }

    fun stopScanning() {
        bleRepository.stopScan()
    }

    fun startScanning() {
        if (bleRepository.isBluetoothEnabled()) {
            bleRepository.startScan()
            _bluetoothState.value = BluetoothState.Enabled
        } else {
            _bluetoothState.value = BluetoothState.Disabled
        }
    }


    fun connectToDevice(deviceAddress: String) {
        if (bleRepository.isBluetoothEnabled()) {
            bleRepository.connectToDevice(deviceAddress)
            _bluetoothState.value = BluetoothState.Enabled
        } else {
            _bluetoothState.value = BluetoothState.Disabled
        }
    }

    fun disConnectDevice(deviceAddress : String) {
        if (bleRepository.isBluetoothEnabled()) {
            bleRepository.disconnectDevice(deviceAddress)
            _bluetoothState.value = BluetoothState.Enabled
        } else {
            _bluetoothState.value = BluetoothState.Disabled
        }
    }

    fun requestBluetoothEnable(activity: Activity) {
        bleRepository.requestBluetoothEnable(activity)
    }

    fun pauseNotifications() {
        bleRepository.pauseNotifications()
    }
    fun resumeNotifications() {
        bleRepository.resumeNotifications()
    }
    fun stopNotifications() {
        bleRepository.stopNotifications()
    }

    // Make sure to stop scanning when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        bleRepository.stopScan()
    }
}

sealed class BluetoothState {
    object Unknown : BluetoothState()
    object Enabled : BluetoothState()
    object Disabled : BluetoothState()
}