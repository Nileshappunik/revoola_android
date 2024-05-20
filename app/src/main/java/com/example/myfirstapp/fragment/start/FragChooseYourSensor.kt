package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.FragChooseYourSensorBinding
import com.example.myfirstapp.databinding.FragSetYourGoalBinding

import com.example.myfirstapp.utils.PrefManager


class FragChooseYourSensor : BaseFragment(){
    val TAG: String = FragChooseYourSensor::class.java.simpleName
    lateinit var fragBinding: FragChooseYourSensorBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = FragChooseYourSensor()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        FragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_choose_your_sensor, container) as FragChooseYourSensorBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragChooseYourSensor" )
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        onBackPresAct(fragBinding.toolbar.ivBack)
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        if (yourWayType.equals("Ride")||yourWayType.equals("Run")||yourWayType.equals("Walk")){
            fragBinding.cardGps.visibility=View.VISIBLE
        }else{
            fragBinding.cardGps.visibility=View.GONE
        }
        fragBinding.tvgo.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            (context as MainActivity).hidebottombarcolorwhite()
            (context as MainActivity).loadFrag(FragSensorProgress().newInstance(bundle), TAG, true, FragSensorProgress::class.java.simpleName, false)

        }
    }
}