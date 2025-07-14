package com.revoola.services

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import android.location.Location
class RLLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = RLLocationRepository(application)

    val locationData: LiveData<Location> = locationRepository.locationData
    val speedData: LiveData<Float> = locationRepository.speedData
    val stepCountData: LiveData<Int> = locationRepository.stepCountData
    val distanceData: LiveData<Double> = locationRepository.distanceData
    val cadenceData: LiveData<Double> = locationRepository.cadenceData
    val paceData: LiveData<Double> = locationRepository.paceData
   // val caloriesBurnedData: LiveData<Double> = RLLocationRepository.caloriesBurnedData
    val elevationMeter: LiveData<Double> = locationRepository.elevationMeter

    fun rl_startLocationUpdates() {
        locationRepository.rl_startLocationUpdates()
    }

    fun rl_stopLocationUpdates() {
        locationRepository.rl_stopLocationUpdates()
    }
}
