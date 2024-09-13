package com.example.myfirstapp.activity

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RLSplashFirstActivity : AppCompatActivity() {
    val TAG: String = RLSplashFirstActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.rl_activity_splash_first)
        supportActionBar?.hide()

        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val currentModeType = uiModeManager.currentModeType

        if (currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            // The device is running in TV mode (Android TV)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
        } else {
            // The device is running in Mobile mode
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, RLSplashActivityRL::class.java)
            startActivity(intent)
            finish()
        }, 2000)

    }

}