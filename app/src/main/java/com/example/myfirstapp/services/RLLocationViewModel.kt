package com.example.myfirstapp.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import android.location.Location
class RLLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val RLLocationRepository = RLLocationRepository(application)

    val locationData: LiveData<Location> = RLLocationRepository.locationData
    val speedData: LiveData<Float> = RLLocationRepository.speedData
    val stepCountData: LiveData<Int> = RLLocationRepository.stepCountData
    val distanceData: LiveData<Float> = RLLocationRepository.distanceData
    val averageSpeedData: LiveData<Float> = RLLocationRepository.averageSpeedData
    val maxSpeedData: LiveData<Float> = RLLocationRepository.maxSpeedData
    val paceData: LiveData<Float> = RLLocationRepository.paceData
    val averagePaceData: LiveData<Float> = RLLocationRepository.averagePaceData
    val maxPaceData: LiveData<Float> = RLLocationRepository.maxPaceData
    val caloriesBurnedData: LiveData<Float> = RLLocationRepository.caloriesBurnedData

    fun RLstartLocationUpdates() {
        RLLocationRepository.RLstartLocationUpdates()
    }

    fun RLstopLocationUpdates() {
        RLLocationRepository.RLstopLocationUpdates()
    }
}
