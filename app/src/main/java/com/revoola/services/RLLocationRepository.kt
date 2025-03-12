package com.revoola.services

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revoola.utils.RLConstants
import com.google.android.gms.location.*
import com.revoola.commonobject.RLTools

class RLLocationRepository(val application: Application) : SensorEventListener  {

    private val sensorManager: SensorManager = application.getSystemService(Application.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val _locationData = MutableLiveData<Location>()
    val locationData: LiveData<Location> = _locationData

    private val _speedData = MutableLiveData<Float>()
    val speedData: LiveData<Float> = _speedData

    private val _stepCountData = MutableLiveData<Int>()
    val stepCountData: LiveData<Int> = _stepCountData

    private val _distanceData = MutableLiveData<Double>()
    val distanceData: LiveData<Double> = _distanceData

    private val _cadenceData = MutableLiveData<Double>()
    val cadenceData: LiveData<Double> = _cadenceData

    private val _paceData = MutableLiveData<Double>()
    val paceData: LiveData<Double> = _paceData


    private val _elevationMeter = MutableLiveData<Double>()
    val elevationMeter: LiveData<Double> = _elevationMeter


    private var initialStepCount: Int = -1
    private var startTime: Long = 0L

   // private val _caloriesBurnedData = MutableLiveData<Double>()
 //   val caloriesBurnedData: LiveData<Double> = _caloriesBurnedData

    private var stepCount=0
    private val weightInKg = RLConstants.weightInKg//  //you can change it dynamically
    private var currentStepCount = 0
    private var totalCaloriesBurned = 0.0
    private var totalDistance = 0.0 // Variable to store cumulative distance
    private var lastLocation: Location? = null

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
    }

    fun RLstartLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

         locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?: return
                for (location in locationResult.locations) {
                   // lastLocation = location
                    startTime = System.currentTimeMillis()
                    val newLocation = locationResult.locations.last()
                    val speedKmh = location.speed * 3.6
                    _speedData.postValue(speedKmh.toFloat())
                    _paceData.postValue(calculatePace(location.speed).toDouble())
                    _elevationMeter.postValue(location.altitude)
                    // Calculate distance between lastLocation and newLocation
                    if (lastLocation != null) {
                        val distanceInMeters = lastLocation!!.distanceTo(newLocation)
                        totalDistance += distanceInMeters // Accumulate distance
                    }
                    // Update lastLocation
                    lastLocation = newLocation
                    // Convert distance to kilometers
                    val distanceInKm = totalDistance / 1000

                    // Post distance data
                    _distanceData.postValue(distanceInKm.toDouble())
                    _locationData.postValue(location)

                    val distance = location.distanceTo(lastLocation!!)
                    if (distance > 1) { // assuming a step is at least 1 meter
                        stepCount++
                    }
                    //Cadence
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - startTime >= 60000) { // 1 minute
                        val cadence = stepCount / ((currentTime - startTime) / 60000.0)
                        _cadenceData.postValue(cadence.toDouble())
                      //  RLTools.RlLogDPrint("RLFragSensorProgress", "Cadence: $cadence steps/minute")
                        stepCount = 0
                        startTime = currentTime
                    }

                }
            }
        }
        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount < 0) {
                initialStepCount = event.values[0].toInt()
                currentStepCount = event.values[0].toInt()- initialStepCount
            }
            _stepCountData.postValue(event.values[0].toInt() - initialStepCount)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun RLstopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
    }

    private fun calculatePace(speed: Float): Int {
        // Cadence is typically measured in revolutions per minute (RPM)
        // We can estimate cadence based on speed, assuming a typical stride length
        // of 2.5 meters per revolution
        val strideLength = 2.5f
        val cadence = (speed / strideLength) * 60
        return cadence.toInt()
    }


}
