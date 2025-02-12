import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.getSdkStatus
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RLHealthConnectManager(private val context: Context) {

    val TAG: String = RLMainActivityRL::class.java.simpleName

    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    val requiredPermissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class))

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

    suspend fun arePermissionsGranted(): Boolean {
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

    // Add function to check if Health Connect app is installed
    fun isHealthConnectInstalled(): Boolean {
        val intent = Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
        return intent.resolveActivity(context.packageManager) != null
    }
}