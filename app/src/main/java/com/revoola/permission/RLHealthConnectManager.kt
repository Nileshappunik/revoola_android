package com.revoola.permission

import android.app.Activity
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.getSdkStatus
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RLHealthConnectManager (private val context: Context) { // Use 'context' instead of 'this'

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }
    val requiredPermissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
    )


    // ✅ Function to check if Health Connect is available
    fun isHealthConnectAvailable(): Boolean {
        return when (getSdkStatus(context)) {
            HealthConnectClient.SDK_UNAVAILABLE,
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> false
            else -> true
        }
    }

    // ✅ Function to check if permissions are granted
    suspend fun arePermissionsGranted(): Boolean {
        return withContext(Dispatchers.IO) {
            val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
            requiredPermissions.all { it in grantedPermissions }
        }
    }



}