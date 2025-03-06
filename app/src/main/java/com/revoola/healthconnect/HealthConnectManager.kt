package com.revoola.healthconnect

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.request.ReadRecordsRequest
import java.time.Instant

class HealthConnectManager(private val context: Context) {

    companion object {
        private val TAG = "HealthConnectManager"
         val PERMISSIONS = setOf(
            "android.permission.health.READ_STEPS",
            "android.permission.health.WRITE_STEPS",
            "android.permission.health.READ_HEART_RATE",
            "android.permission.health.WRITE_HEART_RATE"
        )
        private val PROVIDERS = listOf(
          //  "com.google.android.apps.healthdata",
           // "com.samsung.android.health.platform",
          //  "com.samsung.android.health.platform",
            "com.sec.android.app.shealth",
           // "com.google.android.apps.fitness"
        )
    }

    fun getInstalledProvider(): String? {
        for (provider in PROVIDERS) {
            if (isPackageInstalled(provider)) {
                Log.d(TAG, "Found provider: $provider")
                return provider
            }
        }
        Log.e(TAG, "No Health provider installed")
        return null
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            Log.d(TAG, "Package installed: $packageName")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            openInPlayStore(packageName)
            Log.e(TAG, "Package not installed: $packageName")
            false
        }
    }

    fun openInPlayStore(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$packageName")
            setPackage("com.android.vending")
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Browser fallback if Play Store app is missing
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            }
            context.startActivity(webIntent)
        }
    }

    fun getHealthConnectClient(): HealthConnectClient? {
        val provider = getInstalledProvider()
        if (provider != null) {
            return HealthConnectClient.getOrCreate(context)
        }
        return null
    }

    suspend fun hasAllPermissions(client: HealthConnectClient): Boolean {
        val grantedPermissions = client.permissionController.getGrantedPermissions()
        return grantedPermissions.containsAll(PERMISSIONS)
    }


    fun requestPermissionsLauncher() = PermissionController.createRequestPermissionResultContract()

    suspend fun readStepsData(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): List<StepsRecord> {
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)))
            response.records
        } catch (e: Exception) {
            Log.e(TAG, "Error reading steps: $e")
            emptyList()
        }
    }
}
