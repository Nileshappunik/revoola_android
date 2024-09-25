package com.example.myfirstapp.fragment.start.body

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlFragLeftBodyHeartVideoBinding
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTimerManager


class RLFragLeftBodyWithHeartVideo : RLBaseFragment() {
    val TAG: String = RLFragLeftBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragLeftBodyHeartVideoBinding
    var heartRateList:MutableList<Int> = mutableListOf()
    private var heartRateNumber:Int=0
    private val timerManager = RLTimerManager()

    private val binding by lazy {
        RlFragLeftBodyHeartVideoBinding.inflate(layoutInflater)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragLeftBodyWithHeartVideo()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_left_body_heart_video, container) as RlFragLeftBodyHeartVideoBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragLeftBodyWithHeartVideo" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.circularProgressBar.RLsetProgress(77)
        fragBinding.circularProgressBar.RLsetMaxProgress(100)
        fragBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppZone5Color))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.RLsetStrokeWidth(15f)

        fragBinding.inlayEffort.imgIcon.setImageResource(R.drawable.ic_heart)
        fragBinding.inlayEffort.txtName.setText(R.string.effort)
        fragBinding.inlayEffort.txtNumber.setText("0")

        fragBinding.inlayCalories.imgIcon.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtName.setText(R.string.calories)
        fragBinding.inlayCalories.txtNumber.setText("0")

        fragBinding.inlayHeartrate.imgIcon.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtName.setText(R.string.heartrate)
        fragBinding.inlayHeartrate.txtNumber.setText("0")

        fragBinding.inlayCadence.imgIcon.setImageResource(R.drawable.ic_cadence)
        fragBinding.inlayCadence.txtName.setText(R.string.cadence)
        fragBinding.inlayCadence.txtNumber.setText("--")

        fragBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.inlayTime.txtName.setText(R.string.time)
        fragBinding.inlayTime.progressView2.visibility=View.GONE
        RLtimerMain()
    }
    fun RLUpdateVideoTime(rLformatTime: String) {
         fragBinding.inlayTime.txtNumber.setText(rLformatTime)
    }
    private fun RLHeartRategetdata(){
        val filter = IntentFilter().apply {
            addAction("ACTION_DATA_RETRIEVED_HEART")
        }
        requireActivity().registerReceiver(RLbleBroadcastReceiver, filter)

    }
    private val RLbleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_DATA_RETRIEVED_HEART" -> {
                    val data = intent.getStringExtra("EXTRA_DATA")
                    fragBinding.inlayHeartrate.txtNumber.setText(data)
                    fragBinding.inlayEffort.txtNumber.setText(data)
                    heartRateNumber=RlGetValueInt(data.toString())
                }
            }
        }
    }
    private fun RlGetValueInt(value:String):Int{
        if (value.isNullOrEmpty()){
            return 0
        }else if(value.toDouble() < 0) {
            return 0
        }else{
            return value.toDouble().toInt()
        }
    }
    fun RLtimerMain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                heartRateList.add(heartRateNumber)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        RLHeartRategetdata()

    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            requireActivity().unregisterReceiver(RLbleBroadcastReceiver)
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }catch (e:Exception){
            Log.e(TAG,"Exception:- "+e.message)
        }
    }



}