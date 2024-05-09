package com.example.myfirstapp.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    val TAG: String = SplashActivity::class.java.simpleName
    lateinit var activityBinding: ActivitySplashBinding

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
    }
}