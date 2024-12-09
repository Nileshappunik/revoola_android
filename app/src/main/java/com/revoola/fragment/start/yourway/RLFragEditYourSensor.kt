package com.revoola.fragment.start.yourway

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragEditYourSensorBinding
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager
import com.google.gson.JsonObject


class RLFragEditYourSensor : RLBaseFragment() {
    val TAG: String = RLFragEditYourSensor::class.java.simpleName
    lateinit var fragBinding: RlFragEditYourSensorBinding

    private val binding by lazy {
        RlFragEditYourSensorBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragEditYourSensor()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_edit_your_sensor, container) as RlFragEditYourSensorBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragEditYourSensor" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val deviceType:String = requireArguments().getString("deviceType").toString()
        val deviceAddress:String = requireArguments().getString("deviceAddress").toString()
        val devicename:String = requireArguments().getString("devicename").toString()
        fragBinding.edtSensorName.setText(devicename)
        fragBinding.txtSensorName.setText(devicename)

        if (deviceType.equals("HEARTRATESENSOR")){
            fragBinding.txtSensorType.setText(R.string.heartratemonitor)
            fragBinding.imgHeartRate.setImageResource(R.drawable.ic_heartrate)

        }else if (deviceType.equals("SPEEDSENSOR")){
            fragBinding.txtSensorType.setText(R.string.speedmonitor)
            fragBinding.imgHeartRate.setImageResource(R.drawable.ic_speeed)
        }else{
            fragBinding.txtSensorType.setText(R.string.cadencemonitor)
            fragBinding.imgHeartRate.setImageResource(R.drawable.ic_cadence)
        }

        fragBinding.tvforgetsensor.setOnClickListener {
            com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, "no")
            com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect_type, "")
            RLcloseFragment()
        }

        fragBinding.tvsave.setOnClickListener {
            val editdeviceName:String= fragBinding.edtSensorName.text.toString()
            if (editdeviceName.isNotEmpty()){
                val jsonObject = JsonObject().apply {
                    addProperty(RLConstants.DEVICENAME,editdeviceName)
                    addProperty(RLConstants.DEVICEADDRESS,deviceAddress)
                }
                com.revoola.utils.RLPrefManager.RLsetSomeJsonObjectValue(activity, com.revoola.utils.RLPrefManager.change_device_name, jsonObject)
                RLcloseFragment()
            }else{
                RLcommonToast("Device Name is Empty")
            }

        }

    }
}