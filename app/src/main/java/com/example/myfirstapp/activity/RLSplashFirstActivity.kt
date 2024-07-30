package com.example.myfirstapp.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.myfirstapp.R
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class RLSplashFirstActivity : AppCompatActivity() {
    val TAG: String = RLSplashFirstActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rl_activity_splash_first)
        supportActionBar?.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, RLSplashActivityRL::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}