package com.example.myfirstapp.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.DialogHelpChallengesBinding
import com.example.myfirstapp.databinding.DialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.FragChooseYourSensorBinding
import com.example.myfirstapp.databinding.FragSensorProgressBinding
import com.example.myfirstapp.databinding.FragSetYourGoalBinding

import com.example.myfirstapp.utils.PrefManager
import java.util.Calendar


class FragSensorProgress : BaseFragment(){
    val TAG: String = FragSensorProgress::class.java.simpleName
    lateinit var fragBinding: FragSensorProgressBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = FragSensorProgress()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        FragSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_sensor_progress, container) as FragSensorProgressBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragSensorProgress" )
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga" ic_pace ic_speeed
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        fragBinding.inlayCadence.imgTime.setImageResource(R.drawable.ic_cadence)
        fragBinding.inlayCadence.txtProgressTime.setText(R.string.cadencerpm)
        fragBinding.inlayCadence.txtProgressTimeNumber.setText("0")

        fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
        fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)
        fragBinding.inlayDistance.txtProgressTimeNumber.setText("0.00")

        fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
        fragBinding.inlaySpeed.txtProgressTime.setText(R.string.speedkmh)
        fragBinding.inlaySpeed.txtProgressTimeNumber.setText("0.0")
        fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
        fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE
        fragBinding.inlaySpeed.txtMaxNumber.setText("0.00")
        fragBinding.inlaySpeed.txtAvgNumber.setText("0.00")

        fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
        fragBinding.inlayPace.txtProgressTime.setText(R.string.paceperkm)
        fragBinding.inlayPace.txtProgressTimeNumber.setText("00:00")
        fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
        fragBinding.inlayPace.layMax.visibility=View.VISIBLE
        fragBinding.inlayPace.txtMaxNumber.setText("00:00")
        fragBinding.inlayPace.txtAvgNumber.setText("00:00")
        fragBinding.inlayPace.progressView1.visibility=View.GONE

        fragBinding.layPause.setOnClickListener {
            fragBinding.layPause.visibility=View.GONE
            fragBinding.layResumestop.visibility=View.VISIBLE
        }
        fragBinding.layResume.setOnClickListener {
            fragBinding.layPause.visibility=View.VISIBLE
            fragBinding.layResumestop.visibility=View.GONE
        }
        fragBinding.layStop.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            (context as MainActivity).hidebottombarcolorwhite()
            (context as MainActivity).loadFrag(FragSessionComplete().newInstance(bundle), TAG, true, FragSessionComplete::class.java.simpleName, false)

        }


    }
}