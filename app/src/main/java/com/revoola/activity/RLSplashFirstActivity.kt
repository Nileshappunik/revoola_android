package com.revoola.activity

import android.app.Activity
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import com.revoola.R
import com.revoola.fragment.guest.RLWelcomeDialog
import kotlinx.coroutines.launch

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

    /* private fun RLDialogShow(){
     RLWelcomeDialog().show(supportFragmentManager, "RLWelcomeDialog")}*/
}