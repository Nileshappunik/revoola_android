package com.revoola.ble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RLBLEViewModelFactory(private val bleRepository: BLERepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BLEViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BLEViewModel(bleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}