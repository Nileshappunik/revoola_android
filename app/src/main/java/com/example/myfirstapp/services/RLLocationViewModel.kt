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
    val cadenceData: LiveData<Float> = RLLocationRepository.cadenceData
    val paceData: LiveData<Float> = RLLocationRepository.paceData
    val caloriesBurnedData: LiveData<Float> = RLLocationRepository.caloriesBurnedData
    val elevationMeter: LiveData<Double> = RLLocationRepository.elevationMeter

    fun RLstartLocationUpdates() {
        RLLocationRepository.RLstartLocationUpdates()
    }

    fun RLstopLocationUpdates() {
        RLLocationRepository.RLstopLocationUpdates()
    }
}
