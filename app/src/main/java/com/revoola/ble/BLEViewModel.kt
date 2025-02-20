package com.revoola.ble

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BLEViewModel(
    private val bleRepository: BLERepository
) : ViewModel() {
    private val _deviceList = MutableStateFlow<List<BLEResult.DeviceFound>>(emptyList())
    val deviceList: StateFlow<List<BLEResult.DeviceFound>> = _deviceList.asStateFlow()

    private val _sensorData = MutableStateFlow<BLEResult.SensorData?>(null)
    val sensorData: StateFlow<BLEResult.SensorData?> = _sensorData.asStateFlow()

    private val _connectionState = MutableStateFlow<BLEResult.ConnectionState?>(null)
    val connectionState: StateFlow<BLEResult.ConnectionState?> = _connectionState.asStateFlow()

    private val _bluetoothState = MutableStateFlow<BluetoothState>(BluetoothState.Unknown)
    val bluetoothState: StateFlow<BluetoothState> = _bluetoothState.asStateFlow()


    init {
        viewModelScope.launch {
            bleRepository.bleFlow.collect { result ->
                when (result) {
                    is BLEResult.DeviceFound -> updateDeviceList(result)
                    is BLEResult.ConnectionState -> _connectionState.value = result
                    is BLEResult.SensorData -> _sensorData.value = result
                    is BLEResult.Error -> handleError(result)
                    null -> {} // No action needed
                }
            }
        }
    }

    private fun updateDeviceList(device: BLEResult.DeviceFound) {
        _deviceList.update { currentList ->
            currentList.filter { it.deviceAddress != device.deviceAddress } + device
        }
    }

    private fun handleError(error: BLEResult.Error) {
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