package com.example.myfirstapp.services

import android.annotation.SuppressLint
import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myfirstapp.utils.RLConstants
import com.google.android.gms.location.*

class RLLocationRepository(application: Application) : SensorEventListener {

    private val sensorManager: SensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _locationData = MutableLiveData<Location>()
    val locationData: LiveData<Location> = _locationData

    private val _speedData = MutableLiveData<Float>()
    val speedData: LiveData<Float> = _speedData

    private val _stepCountData = MutableLiveData<Int>()
    val stepCountData: LiveData<Int> = _stepCountData

    private val _distanceData = MutableLiveData<Float>()
    val distanceData: LiveData<Float> = _distanceData

    private val _averageSpeedData = MutableLiveData<Float>()
    val averageSpeedData: LiveData<Float> = _averageSpeedData

    private val _maxSpeedData = MutableLiveData<Float>()
    val maxSpeedData: LiveData<Float> = _maxSpeedData

    private val _paceData = MutableLiveData<Float>()
    val paceData: LiveData<Float> = _paceData

    private val _averagePaceData = MutableLiveData<Float>()
    val averagePaceData: LiveData<Float> = _averagePaceData

    private val _maxPaceData = MutableLiveData<Float>()
    val maxPaceData: LiveData<Float> = _maxPaceData

    private var initialStepCount: Int = -1
    private var startTime: Long = 0L

    private val _caloriesBurnedData = MutableLiveData<Float>()
    val caloriesBurnedData: LiveData<Float> = _caloriesBurnedData

    private var maximumSpeed = 0.0
    private var totalSpeed = 0.0
    private var speedReadings = 0
    private var lastUpdateTime: Long = 0
    private var totalDistance = 0.0
    private var totalCaloriesBurned = 0.0
    private val weightInKg = RLConstants.weightInKg//  //you can change it dynamically


    private var lastLocation: Location? = null

    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 5000 // 5 seconds
        fastestInterval = 2000 // 2 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locations = locationResult.locations
            val currentTime = System.currentTimeMillis()
            if (locations.isNotEmpty()) {
                val newLocation = locations.last()
                // Check if we have a last known location
                lastLocation?.let { lastLoc ->
                 /*  val distanceInMeters = lastLoc.distanceTo(newLocation)
                    // Convert to kilometers
                    val distanceInKm = distanceInMeters / 1000
                    _distanceData.postValue(distanceInKm)*/

                    val speed = newLocation.speed * 3.6 // Convert to km/h
                    if (speed > maximumSpeed) {
                        maximumSpeed = speed
                        _maxSpeedData.postValue(maximumSpeed.toFloat())
                    }
                    totalSpeed += speed
                    speedReadings++

                    // Call the calories calculation function
                    val durationInMinutes = (currentTime - startTime) / 60000.0 // Convert ms to minutes
                    val caloriesBurned = ScxCalculateCalories(speed, weightInKg, durationInMinutes)
                    totalCaloriesBurned += caloriesBurned
                    _caloriesBurnedData.postValue(totalCaloriesBurned.toFloat())

                    // Calculate distance in meters
                    val timeInterval = (currentTime - lastUpdateTime) / 1000.0
                    val distanceInKm = speed * (timeInterval / 3600.0)
                    totalDistance += distanceInKm
                    _distanceData.postValue(totalDistance.toFloat())

                    _speedData.postValue(speed.toFloat())
                    _averageSpeedData.postValue(RLgetAverageSpeed().toFloat())

                    _paceData.postValue(RLgetPace(speed).toFloat())
                    _averagePaceData.postValue(RLgetAveragePace().toFloat())
                    _maxPaceData.postValue(RLgetMaxPace().toFloat())
                    val ElevationMeter=lastLoc.altitude
                }
                // Update the last known location
                lastLocation = newLocation
                lastUpdateTime = currentTime
            }
            val location:Location = locationResult.lastLocation!!
            startTime = System.currentTimeMillis()
            _locationData.postValue(location)
        }
    }

    @SuppressLint("MissingPermission")
    fun RLstartLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        startTime = System.currentTimeMillis()
    }

    fun RLstopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
    }

    fun RLGetLocationElevation(location: Location): Double {
        return location.altitude  // Returns elevation in meters
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                initialStepCount = event.values[0].toInt()
            }
            _stepCountData.postValue(event.values[0].toInt() - initialStepCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun ScxCalculateCalories(speed: Double, weightInKg: Double, durationInMinutes: Double): Double {
        val metValue = when {
            speed > 8 -> 7.5 // Running (high speed)
            speed > 4 -> 5.0 // Jogging (moderate speed)
            else -> 3.8 // Walking
        }
        return metValue * weightInKg * (durationInMinutes / 60)
    }

    private fun RLgetAverageSpeed(): Double {
        return if (speedReadings > 0) totalSpeed / speedReadings else 0.0
    }
    private fun RLgetPace(speed: Double): Double {
        return if (speed > 0) 60 / speed else 0.0
    }
    private fun RLgetAveragePace(): Double {
        return RLgetPace(RLgetAverageSpeed())
    }
    private fun RLgetMaxPace(): Double {
        return RLgetPace(maximumSpeed)
    }
}
