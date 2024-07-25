package com.example.myfirstapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.R
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivitySplashBinding
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RLSplashActivityRL : RLBaseActivity() {
    val TAG: String = RLSplashActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySplashBinding

    companion object {
        const val ACTIVITY_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_splash) as RlActivitySplashBinding
        RLlocatiobpermissioncheck()
        // Initialize Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        FirebaseApp.initializeApp(this)
       val userId= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.current_user,"")
      //  RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,"w2p8SQCvE3emjEEDo66f02eF6fG2")
        if (userId.isNullOrEmpty()){
            activityBinding.txtFullrevoolaexperience.setOnClickListener {
                startActivity(Intent(this, RLLoginActivityRL::class.java))
            }
            activityBinding.txtJusthereforaquickpeak.setOnClickListener {
                startActivity(Intent(this, RLMainActivityRL::class.java))
            }
        }else{
            startActivity(Intent(this, RLMainActivityRL::class.java))
        }
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