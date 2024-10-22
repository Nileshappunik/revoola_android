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
    val distanceData: LiveData<Double> = RLLocationRepository.distanceData
    val cadenceData: LiveData<Double> = RLLocationRepository.cadenceData
    val paceData: LiveData<Double> = RLLocationRepository.paceData
    val caloriesBurnedData: LiveData<Double> = RLLocationRepository.caloriesBurnedData
    val elevationMeter: LiveData<Double> = RLLocationRepository.elevationMeter

    fun RLstartLocationUpdates() {
        RLLocationRepository.RLstartLocationUpdates()
    }

    fun RLstopLocationUpdates() {
        RLLocationRepository.RLstopLocationUpdates()
    }
}
