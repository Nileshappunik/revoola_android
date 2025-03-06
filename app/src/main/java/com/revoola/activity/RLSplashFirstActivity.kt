package com.revoola.activity

import android.app.Activity
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.moengage.core.internal.logger.LOG_LEVEL_TO_TYPE_MAPPING
import com.revoola.R
import com.revoola.base.RLBaseActivity
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlActivityLoginBinding
import com.revoola.databinding.RlActivitySplashBinding
import com.revoola.databinding.RlActivitySplashFirstBinding
import com.revoola.fragment.guest.RLWelcomeDialog
import com.revoola.healthconnect.domain.AppConstants
import com.revoola.healthconnect.domain.AppConstants.currentDate
import com.revoola.healthconnect.domain.AppConstants.minimumDate
import com.revoola.healthconnect.domain.HealthMainViewModel
import com.revoola.healthconnect.domain.HealthViewModelFactory
import com.revoola.healthconnect.domain.StepAdapter
import com.revoola.healthconnect.domain.showToast
import com.samsung.android.sdk.health.data.data.AggregatedData
import com.samsung.android.sdk.health.data.helper.SdkVersion
import com.samsung.android.sdk.health.data.permission.AccessType
import com.samsung.android.sdk.health.data.permission.Permission
import com.samsung.android.sdk.health.data.request.DataTypes
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


class RLSplashFirstActivity : RLBaseActivity() {
    val TAG: String = RLSplashFirstActivity::class.java.simpleName
    lateinit var activityBinding:RlActivitySplashFirstBinding
    private fun RLDialogShow(){ RLWelcomeDialog().show(supportFragmentManager, "RLWelcomeDialog") }

    private lateinit var healthMainViewModel: HealthMainViewModel
    private var startDate = currentDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_splash_first) as RlActivitySplashFirstBinding
        //setContentView(R.layout.rl_activity_splash_first)
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

       // RLSamsungHealth()
    }

    private fun RLSamsungHealth() {
        healthMainViewModel = ViewModelProvider(this, HealthViewModelFactory(this))[HealthMainViewModel::class.java]
        /** Show toast on exception occurrence **/
        healthMainViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }
        collectResponse()
        healthMainViewModel.connectToSamsungHealth(this)
    }

    private fun collectResponse() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                /**  Handle response of permission request */
                launch {
                    healthMainViewModel.permissionResponse.collect { result ->
                        if (result.first == AppConstants.SUCCESS) {
                           RlgetStep()
                        } else if (result.first != AppConstants.WAITING) {
                            showToast(this@RLSplashFirstActivity, result.first)
                        }
                        healthMainViewModel.resetPermissionResponse()
                    }
                }
            }
        }
    }

    private fun RlgetStep() {
        healthMainViewModel.readStepData(startDate)
        setStepDataObservers()
    }
    private fun setStepDataObservers() {
        val stepAdapter = StepAdapter()
        activityBinding.stepsList.layoutManager = LinearLayoutManager(this)
        activityBinding.stepsList.adapter = stepAdapter
        /**  Update steps UI */
        healthMainViewModel.totalStepCountData.observe(this) {
           stepAdapter.updateList(it)
        }

        healthMainViewModel.dailyHeartRate.observe(this) {
           // heartRateAdapter.updateList(it)
            //val heartRate = heartRateList[position]
//            holder.binding.run {
//                this.heartRateTime.text = heartRateTime
//                this.heartRateValue.text = formatString(heartRate.avg)
//            }
//            if (heartRate.max != 0f) {
//                val maxHeartRate =
//                    heartRate.max.toInt().toString() + context.getString(R.string.heart_rate_unit)
//                holder.binding.maxHeartRateValue.text = maxHeartRate
//            } else {
//                holder.binding.maxHeartRateValue.text = context.getString(R.string.no_data)
//            }
//            if (heartRate.min != 1000f) {
//                val minHeartRate =
//                    heartRate.min.toInt().toString() + context.getString(R.string.heart_rate_unit)
//                holder.binding.minHeartRateValue.text = minHeartRate
//            } else {
//                holder.binding.minHeartRateValue.text = context.getString(R.string.no_data)
//            }
        }

        /** Show toast on exception occurrence **/
        healthMainViewModel.exceptionResponse.observe(this) { message ->
            showToast(this, message)
        }


    }

}

