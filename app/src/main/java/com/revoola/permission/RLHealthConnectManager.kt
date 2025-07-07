package com.revoola.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.getSdkStatus
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.WeightRecord
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RLHealthConnectManager(private val context: Context) {

    val TAG: String = RLMainActivityRL::class.java.simpleName

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    // Updated permissions set to match all required data types
    val requiredPermissions = setOf(
       // Exercise permissions
         HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getWritePermission(ExerciseSessionRecord::class),

        // Heart rate permissions
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),

        // Steps permissions
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),

        // Calories permissions
      /*  HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getWritePermission(ActiveCaloriesBurnedRecord::class),*/

        // Weight permissions
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getWritePermission(WeightRecord::class)

    )

    fun isHealthConnectAvailable(): Boolean {
        val status = getSdkStatus(context)
        // Add debug logging
        when (status) {
            HealthConnectClient.SDK_AVAILABLE -> {
                RLTools.RlLogDPrint(TAG,"Health Connect SDK is available")
                return true
            }
            HealthConnectClient.SDK_UNAVAILABLE -> {
                RLTools.RlLogDPrint(TAG,"Health Connect SDK is unavailable")
                return false
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                RLTools.RlLogDPrint(TAG,"Health Connect provider update required")
                return false
            }
            else -> {
                RLTools.RlLogDPrint(TAG,"Unknown Health Connect SDK status: $status")
                return false
            }
        }
    }

    suspend fun arePermissionsGrantedOLD(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
                requiredPermissions.all { it in grantedPermissions }
            } catch (e: Exception) {
                println("Error checking permissions: ${e.message}")
                false
            }
        }
    }

    suspend fun arePermissionsGranted(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val grantedPermissions = healthConnectClient.permissionController.getGrantedPermissions()
                RLTools.RlLogDPrint(TAG, "Granted Permissions: $grantedPermissions")

                val missingPermissions = requiredPermissions.filter { it !in grantedPermissions }
                if (missingPermissions.isNotEmpty()) {
                    RLTools.RlLogEPrint(TAG, "Missing Permissions: $missingPermissions")
                }

                requiredPermissions.all { it in grantedPermissions }
            } catch (e: Exception) {
                RLTools.RlLogEPrint(TAG, "Error checking permissions: ${e.message}")
                false
            }
        }
    }

    // Add function to check if Health Connect app is installed
    fun isHealthConnectInstalled(): Boolean {
        val intent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
        return intent.resolveActivity(context.packageManager) != null
    }
    // Function to open Health Connect permissions page
    fun openHealthConnectPermissions() {
        try {
            val intent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            RLTools.RlLogDPrint(TAG, "Error opening Health Connect permissions: ${e.message}")
            // If direct opening fails, try to open through system settings
            try {
                val settingsIntent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", "com.google.android.apps.healthdata", null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(settingsIntent)
            } catch (e: Exception) {
                RLTools.RlLogDPrint(TAG, "Error opening system settings: ${e.message}")
                Toast.makeText(context, "Unable to open Health Connect settings", Toast.LENGTH_SHORT).show()
            }
        }
    }
}