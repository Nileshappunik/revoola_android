package com.revoola.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.revoola.R
import com.revoola.base.RLBaseActivity
import com.revoola.databinding.RlActivitySplashBinding
import com.revoola.utils.RLPrefManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.revoola.RLBaseProgress
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.utils.RLConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RLSplashActivityRL : RLBaseActivity() {
    val TAG: String = RLSplashActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySplashBinding

    companion object {
        const val ACTIVITY_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_splash) as RlActivitySplashBinding
        RLlocatiobpermissioncheck()
        RLRemoteConfig()
        val userId= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.current_user,"")
        if (userId.isNullOrEmpty()){
            activityBinding.btnFullExperience.setOnClickListener {
                startActivity(Intent(this, RLLoginActivityRL::class.java))
                finish()
            }
            activityBinding.btnGuestUser.setOnClickListener {
                RLGuestUser()
//                startActivity(Intent(this, RLMainActivityRL::class.java))
//                finish()
            }
        }else{
            RLTools.RlLogEPrint(TAG,"USer: $userId")
            startActivity(Intent(this, RLMainActivityRL::class.java))
            finish()
        }
    }

    private fun RLGuestUser(){
        val databaseManager = RLDatabaseManagerWrite()
        val authManager = RLAuthManager()
        authManager.RLRegisterGuestUser(){ user, error ->
            if (user != null) {
                val userId = user.uid
                val email = "$userId@guestuser.com"
                val userModel = RLUser(displayImage = user.photoUrl?.toString() ?: "",emailId = email,name = "Guest",userId = userId)
                databaseManager.REVOOLAUSERFORSEARCHWrite(userId,userModel) { success, error ->
                    if (success) {
                        RLRevoolaUserSettingFirebaseEntry(userId,email)
                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                    }
                }
            }else {
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG,"Registration failed: ${error?.message}")
            }
        }
    }

    private fun RLRemoteConfig() {

        val remoteConfig = FirebaseRemoteConfig.getInstance()

        // Set default values
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600) // 1 hour
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.rlremote_config_defaults)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this,OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val start_top = remoteConfig.getString("start")
                    val friends_top = remoteConfig.getString("friends_top")
                    val challenge_selectFor = remoteConfig.getString("challenge_selectFor")
                    val challenge_selectTarget = remoteConfig.getString("challenge_selectTarget")
                    val challenge_selectName = remoteConfig.getString("challenge_selectName")
                    RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.start_help_content,start_top.toString())
                    RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.friends_help_content,friends_top.toString())
                    RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.challenge_selectFor,challenge_selectFor.toString())
                    RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.challenge_selectTarget,challenge_selectTarget.toString())
                    RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.challenge_selectName,challenge_selectName.toString())
                } else {
                   RLTools.RlLogEPrint(TAG, "Fetch failed")
                }
            })
    }

    private fun RLlocatiobpermissioncheck(){
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted
            //onActivityRecognitionPermissionGranted()
        }
    }

    //Handle the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RLMainActivityRL.ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                //onActivityRecognitionPermissionGranted()
            } else {
                // Permission denied
                Toast.makeText(this, "Activity Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    data class RLUser(
        val displayImage: String,
        val emailId: String,
        val name: String,
        val userId: String){
        //Convert User object to a Map for Firebase Realtime Database
        fun toMap(): Map<String, Any> {
            return mapOf(
                "displayImage" to displayImage,
                "emailId" to emailId,
                "name" to name,
                "firstName" to name,
                "remark" to "Android",
                "userId" to userId)
        }
    }

    private fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String) {
        val versionName: String = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        val firebaseManager = RLFirebaseManager()
        firebaseManager.RLRevoolaUserSettingFirebaseEntry(userId, emailId, versionName) { success ->
            if (success) {
                RLBaseProgress.RLhideProgressDialog()
                RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user, userId)
                RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user_email, emailId)
                startActivity(Intent(this, RLMainActivityRL::class.java))
                finish()
            } else {
                RLBaseProgress.RLhideProgressDialog()
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun RLInsertUserServer(userId:String,emailId:String) {
        val userGuest = mapOf(
            "displayImage" to RLConstants.GuestImg,
            "emailId" to emailId,
            "firstName" to "Guest",
            "lastName" to "Guest",
            "name" to "Guest",
            "userId" to userId)
    }
    private fun setMemberProfileAnonymousUser(userId:String,emailId:String) {
        val dob = "01/01/1970"
        val age = 29 //getAge(dob)
        val TMHR = 220 - age
        val AMHR = TMHR
        val RFMHR = TMHR

        val userData = mapOf(
            "height" to 170,
            "heightUnit" to "Metric",
            "weightkg" to 50,
            "weightUnit" to "Metric",
            "gender" to "Male",
            "dob" to dob,
            "TMHR" to TMHR,
            "AMHR" to AMHR,
            "RFMHR" to RFMHR
        )
        /*val setUpMyProfile = mapOf(
            "email" to emailId,
            "gender" to "Male",
            "date_of_birth" to "01/01/1970",
            "device_type" to "Android",
            "insightlyId" to 0,
            "paidortrial" to userService.subscriptionName,
            "next_payment_date" to userService.subscriptionTime,
            "subscription_end_date" to userService.subscriptionTime)
        val updateUserInsightlyMoe = mapOf(
            "email" to emailId,
            "uid" to userId,
            "gender" to "Male",
            "date_of_birth" to "01/01/1970",
            "device_type" to "Android",
            "next_payment_date" to userService.subscriptionTime,
            "subscription_end_date" to userService.subscriptionTime,
            "referrer" to //linkService.usercode ?: "peak",
            "Is_basic_data_added" to false,
            "paidortrial" to "peak")*/

    }
}