package com.example.myfirstapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.R
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivitySplashBinding
import com.example.myfirstapp.utils.RLPrefManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

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
        // Initialize Firebase
//        FirebaseApp.initializeApp(this)
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        RLRemoteConfig()
        RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,"w2p8SQCvE3emjEEDo66f02eF6fG2")
       val userId= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.current_user,"")
        if (userId.isNullOrEmpty()){
            activityBinding.txtFullrevoolaexperience.setOnClickListener {
                startActivity(Intent(this, RLLoginActivityRL::class.java))
                finish()
            }
            activityBinding.txtJusthereforaquickpeak.setOnClickListener {
                startActivity(Intent(this, RLMainActivityRL::class.java))
                finish()
            }
        }else{
            startActivity(Intent(this, RLMainActivityRL::class.java))
            finish()
           // startActivity(Intent(this, RLLoginActivityRL::class.java))
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
                    Log.e(TAG, "Fetch failed")
                }
            })
    }

    fun RLlocatiobpermissioncheck(){
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_FINE_LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission is already granted
            //onActivityRecognitionPermissionGranted()
        }
    }

    // Handle the permission result
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

}