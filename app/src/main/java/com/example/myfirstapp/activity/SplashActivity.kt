package com.example.myfirstapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    val TAG: String = SplashActivity::class.java.simpleName
    lateinit var activityBinding: ActivitySplashBinding

    companion object {
        const val ACTIVITY_FINE_LOCATION_PERMISSION_REQUEST_CODE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_splash) as ActivitySplashBinding
        activityBinding.txtFullrevoolaexperience.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        activityBinding.txtJusthereforaquickpeak.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        locatiobpermissioncheck()
    }

    fun locatiobpermissioncheck(){
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
        if (requestCode == MainActivity.ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE) {
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