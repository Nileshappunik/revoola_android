package com.revoola.healthconnect.domain

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ElevationGainedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.sdk.health.data.HealthDataStore
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.data.HealthDataPoint
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataType
import com.samsung.android.sdk.health.data.request.DataTypes
import com.samsung.android.sdk.health.data.request.LocalTimeFilter
import com.samsung.android.sdk.health.data.request.LocalTimeGroup
import com.samsung.android.sdk.health.data.request.LocalTimeGroupUnit
import com.samsung.android.sdk.health.data.request.Ordering
import com.samsung.android.sdk.health.data.response.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class HealthMainViewModel(private val healthDataStore: HealthDataStore, activity: Activity):ViewModel() {

    companion object {
        private const val TAG = "[HTK]HealthDiaryViewModel"
    }

    private val _permissionResponse = MutableStateFlow(Pair(AppConstants.WAITING, -1))
    private val _exceptionResponse: MutableLiveData<String> = MutableLiveData<String>()
    private val exceptionHandler = getExceptionHandler(activity, _exceptionResponse)
    val permissionResponse: StateFlow<Pair<String, Int>> = _permissionResponse
    val exceptionResponse: LiveData<String> = _exceptionResponse
    val dayStartTimeAsText = ObservableField<String>()
    //HeartRate
    private val hrResultList: MutableList<HeartRate> = mutableListOf()
    private val _dailyHeartRate = MutableLiveData<List<HeartRate>>()
    val dailyHeartRate: LiveData<List<HeartRate>> = _dailyHeartRate

    //Step
    private val _totalStepCountData = MutableLiveData<List<AggregatedData<Long>>>()
    val totalStepCountData: LiveData<List<AggregatedData<Long>>> = _totalStepCountData
    val totalStepCount = ObservableField<String>()


    fun checkForPermission(context: Context, permSet: MutableSet<Permission>, activityId: Int, ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val grantedPermissions = healthDataStore.getGrantedPermissions(permSet)
            if (grantedPermissions.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                requestForPermission(context, permSet, activityId)
            }
        }
    }

    private fun requestForPermission(context: Context, permSet: MutableSet<Permission>, activityId: Int, ) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val activity = context as Activity
            val result = healthDataStore.requestPermissions(permSet, activity)
            Log.i(TAG, "requestPermissions: Success ${result.size}")

            if (result.containsAll(permSet)) {
                _permissionResponse.emit(Pair(AppConstants.SUCCESS, activityId))
            } else {
                withContext(Dispatchers.Main) {
                    _permissionResponse.emit(Pair(AppConstants.NO_PERMISSION, -1))
                    Log.i(TAG, "requestPermissions: NO_PERMISSION")
                }
            }
        }
    }

    // Permissions for all data types accessed in this application
    fun connectToSamsungHealth(context: Context) {
        val permSet = mutableSetOf(
            Permission.of(DataTypes.NUTRITION, AccessType.READ),
            Permission.of(DataTypes.STEPS, AccessType.READ),
            Permission.of(DataTypes.HEART_RATE, AccessType.READ),
            Permission.of(DataTypes.SLEEP, AccessType.READ),
            Permission.of(DataTypes.BLOOD_OXYGEN, AccessType.READ),
            Permission.of(DataTypes.SKIN_TEMPERATURE, AccessType.READ),
            Permission.of(DataTypes.EXERCISE, AccessType.READ)
        )

        checkForPermission(context, permSet, AppConstants.SLEEP_ACTIVITY)
    }

    fun resetPermissionResponse() {
        viewModelScope.launch {
            _permissionResponse.emit(Pair(AppConstants.WAITING, -1))
        }
    }

    //Common Read
    fun readStepData(dateTime: LocalDateTime) {
        dayStartTimeAsText.set(dateTime.format(dateFormat))

        val localtimeFilter = LocalTimeFilter.of(dateTime, dateTime.plusDays(1))
        val localTimeGroup = LocalTimeGroup.of(LocalTimeGroupUnit.HOURLY, 1)
        //Step
        val readStepRequest = DataType.StepsType.TOTAL.requestBuilder
            .setLocalTimeFilterWithGroup(localtimeFilter, localTimeGroup)
            .setOrdering(Ordering.ASC)
            .build()
        //HeartRate
        val readHeartRateRequest = DataTypes.HEART_RATE.readDataRequestBuilder
            .setLocalTimeFilter(localtimeFilter)
            .setOrdering(Ordering.DESC)
            .build()

        //  Make SDK call to read step data
        viewModelScope.launch(AppConstants.SCOPE_IO_DISPATCHERS + exceptionHandler) {
            //Step
            val result = healthDataStore.aggregateData(readStepRequest)
            processAggregateStepDataResponse(result)
            //HeartRate
            val heartRateList = healthDataStore.readData(readHeartRateRequest).dataList
            processAggregateHeartRateDataResponse(heartRateList)
        }
    }
    //Step
    private fun processAggregateStepDataResponse(readStepRequest: DataResponse<AggregatedData<Long>>) {
        val stepCount = ArrayList<AggregatedData<Long>>()
        var totalSteps: Long = 0

        readStepRequest.dataList.forEach { stepData ->
            val hourlySteps = stepData.value as Long
            totalSteps += hourlySteps
            stepCount.add(stepData)
        }
        totalStepCount.set(totalSteps.toString())
        _totalStepCountData.postValue(stepCount)
    }
    //HeartRate
    private fun processAggregateHeartRateDataResponse(heartRateList: List<HealthDataPoint>) {
        hrResultList.clear()
        val hrOfFirstQuarter = HeartRate(1000f, 0f, 0f, "00:00", "06:00", 0)
        val hrOfSecondQuarter = HeartRate(1000f, 0f, 0f, "06:00", "12:00", 0)
        val hrOfThirdQuarter = HeartRate(1000f, 0f, 0f, "12:00", "18:00", 0)
        val hrOfFourthQuarter = HeartRate(1000f, 0f, 0f, "18:00", "24:00", 0)

        heartRateList.forEach { heartRateData ->
            val time = LocalDateTime.ofInstant(heartRateData.startTime, heartRateData.zoneOffset)
            when {
                time.isBetween(0, 5) -> processHeartRateData(heartRateData, hrOfFirstQuarter)
                time.isBetween(6, 11) -> processHeartRateData(heartRateData, hrOfSecondQuarter)
                time.isBetween(12, 17) -> processHeartRateData(heartRateData, hrOfThirdQuarter)
                time.isBetween(18, 23) -> processHeartRateData(heartRateData, hrOfFourthQuarter)
            }
        }

        processAvgData(hrOfFirstQuarter)
        processAvgData(hrOfSecondQuarter)
        processAvgData(hrOfThirdQuarter)
        processAvgData(hrOfFourthQuarter)

        _dailyHeartRate.postValue(hrResultList)
    }
    private fun LocalDateTime.isBetween(fromHour: Int, toHour: Int) =
        this >= this.withHour(fromHour).withMinute(0).withSecond(0) &&
                this <= this.withHour(toHour).withMinute(59).withSecond(59)
    private fun processAvgData(hrQuarter: HeartRate) {
        hrQuarter.apply {
            if (hrQuarter.count != 0) {
                hrQuarter.avg /= hrQuarter.count
                hrResultList.add(hrQuarter)
            }
        }
    }
    private fun processHeartRateData(heartRateData: HealthDataPoint, hrQuarter: HeartRate) {
        hrQuarter.apply {
            heartRateData.getValue(DataType.HeartRateType.HEART_RATE)?.let {
                avg += it
                count++
            }
            heartRateData.getValue(DataType.HeartRateType.MAX_HEART_RATE)?.let {
                max = maxOf(max, it)
            }
            heartRateData.getValue(DataType.HeartRateType.MIN_HEART_RATE)?.let {
                if (min != 0f) {
                    min = minOf(min, it)
                }
            }
        }
    }
    data class HeartRate(var min: Float, var max: Float,var avg: Float, var startTime: String, var endTime: String, var count: Int)

}
