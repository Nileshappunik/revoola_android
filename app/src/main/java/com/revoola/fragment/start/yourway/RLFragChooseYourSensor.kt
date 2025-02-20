package com.revoola.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.R
import com.revoola.RLBaseFragment
import com.revoola.activity.RLMainActivityRL
import com.revoola.ble.BLERepository
import com.revoola.ble.BLEResult
import com.revoola.ble.BLEViewModel
import com.revoola.ble.BLEViewModelFactory
import com.revoola.ble.BluetoothState
import com.revoola.ble.DeviceType
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragChooseYourSensorBinding
import com.revoola.enumclass.RLYourWayName
import com.revoola.fragment.start.adapter.RLBleListModel
import com.revoola.fragment.start.adapter.RLSensorCadenceListAdapter
import com.revoola.fragment.start.adapter.RLSensorHeartListAdapter
import com.revoola.fragment.start.adapter.RLSensorSpeedListAdapter
import com.revoola.fragment.start.body.RLFragBodyClassesHeartVideoStart
import com.revoola.fragment.start.body.RLFragBodyClassesNormalVideoStart
import com.revoola.fragment.start.body.RLFragBodyClassesSpeedVideoStart
import com.revoola.fragment.start.mind.RLFragMindClassesHeartVideoStart
import com.revoola.fragment.start.mind.RLFragMindClassesNormalVideoStart
import com.revoola.interfaceall.RLItemClickListenerAdapter
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.launch

class RLFragChooseYourSensor : RLBaseFragment(), RLItemClickListenerAdapter {
    val TAG: String = RLFragChooseYourSensor::class.java.simpleName
    lateinit var fragBinding: RlFragChooseYourSensorBinding

    lateinit var adapter :RLSensorHeartListAdapter
    lateinit var adapterspeed :RLSensorSpeedListAdapter
    lateinit var adaptercadence : RLSensorCadenceListAdapter

    private var sensorDeviceType = ""

    private val binding by lazy {
        RlFragChooseYourSensorBinding.inflate(layoutInflater)
    }
    private val bleRepository by lazy {
        BLERepository(requireContext())
    }

    private val viewModel: BLEViewModel by activityViewModels {
        BLEViewModelFactory(bleRepository)
    }

    companion object{
        private const val REQUEST_ENABLE_BT = 1
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChooseYourSensor()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass, inflater, R.layout.rl_frag_choose_your_sensor, container) as RlFragChooseYourSensorBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment, "BLEFragment")
        RLuisetup()

        return fragBinding.root
    }

    private fun RLuisetup() {

        fragBinding.ivBack.setOnClickListener {
            RLBottomHideShowSet(true)
            RLcloseFragment()
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvHeartrateSensorList.layoutManager = linearLayoutManager
        adapter = RLSensorHeartListAdapter(activity,this)
        fragBinding.rvHeartrateSensorList.adapter = adapter

        val linearLayoutManagerSpeed = LinearLayoutManager(activity)
        fragBinding.rvSpeedSensorList.layoutManager = linearLayoutManagerSpeed
        adapterspeed = RLSensorSpeedListAdapter(activity,this)
        fragBinding.rvSpeedSensorList.adapter = adapterspeed

        val linearLayoutManagerCadence = LinearLayoutManager(activity)
        fragBinding.rvCadenceSensorList.layoutManager = linearLayoutManagerCadence
        adaptercadence = RLSensorCadenceListAdapter(activity,this)
        fragBinding.rvCadenceSensorList.adapter = adaptercadence

        val yourWayType = requireArguments().getString(RLExtraValueKey.yourWayType).toString()

        if (yourWayType.equals(RLYourWayName.Ride.toString()) || yourWayType.equals(RLYourWayName.Run.toString()) || yourWayType.equals(RLYourWayName.Walk.toString())) {
            fragBinding.cardGps.visibility = View.VISIBLE
            fragBinding.cardHeartRateSensor.visibility = View.GONE
        } else {
            fragBinding.cardGps.visibility = View.GONE
            fragBinding.cardCadenceSensor.visibility = View.GONE
            fragBinding.cardHeartRateSensor.visibility = View.GONE
        }

        fragBinding.tvskip.setOnClickListener {
            sensorDeviceType = DeviceType.NO_DEVICE.toString()
            RLNextScreenOpen(yourWayType)
        }
        fragBinding.tvgo.setOnClickListener {
            if (sensorDeviceType.isEmpty()){
                sensorDeviceType = DeviceType.NO_DEVICE.toString()
            }
            RLNextScreenOpen(yourWayType)
        }

        // Start scanning when the fragment is created
        viewModel.startScanning()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Add the Bluetooth state collector first
                launch {
                    viewModel.bluetoothState.collect { state ->
                        when (state) {
                            is BluetoothState.Enabled -> {
                                viewModel.startScanning()
                            }
                            is BluetoothState.Disabled -> {
                                viewModel.requestBluetoothEnable(requireActivity())
                            }
                            else -> {} // Handle unknown state
                        }
                    }
                }
                // Your existing collectors
                launch {
                    viewModel.deviceList.collect { devices ->
                        updateDeviceList(devices)
                    }
                }
                launch {
                    viewModel.sensorData.collect { data ->
                        updateSensorDisplay(data)
                    }
                }
                launch {
                    viewModel.connectionState.collect { state ->
                       //do some with state
                    }
                }
            }
        }
    }

    private fun updateDeviceList(devicesList: List<BLEResult.DeviceFound>) {
        val lastConnectDeviceAddress =RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.last_device_connect, "")
        devicesList.forEach { deviceData ->
           val lastConnect = lastConnectDeviceAddress.equals(deviceData.deviceAddress)
            if (lastConnect){
                sensorDeviceType=deviceData.deviceType.toString()
            }
            if (deviceData.deviceType.equals(DeviceType.HEART_RATE)){
                fragBinding.cardHeartRateSensor.visibility = View.VISIBLE
                adapter.addUniqueItem(RLBleListModel(deviceData.deviceName,deviceData.deviceAddress,
                    DeviceType.HEART_RATE.toString(),lastConnect))
            }else{
                fragBinding.cardCadenceSensor.visibility = View.VISIBLE
                adapterspeed.addUniqueItem(RLBleListModel(deviceData.deviceName,deviceData.deviceAddress,
                    DeviceType.SPEED.toString(),lastConnect))
                adaptercadence.addUniqueItem(RLBleListModel(deviceData.deviceName,deviceData.deviceAddress,
                    DeviceType.CADENCE.toString(),lastConnect))
            }
        }
    }

    private fun updateSensorDisplay(data: BLEResult.SensorData?) {
        // Update UI with sensor data
        RLTools.RlLogEPrint(TAG1,"data: $data")
    }

    private fun RLNextScreenOpen(yourWayType:String){
        viewModel.stopScanning()

        val isBody = requireArguments().getBoolean(RLExtraValueKey.isBody)
        val isMind = requireArguments().getBoolean(RLExtraValueKey.isMind)
        val isYourWay = requireArguments().getBoolean(RLExtraValueKey.isYourWay)

        if (isYourWay){
            val  bundle: Bundle = Bundle()
            bundle.putString(RLExtraValueKey.yourWayType, yourWayType)
            when(sensorDeviceType){
                DeviceType.NO_DEVICE.toString() ->{
                    bundle.putBoolean(RLExtraValueKey.isSpeedSensor,false)
                    (context as RLMainActivityRL).RLloadFrag(RLFragSensorProgress().newInstance(bundle), TAG, true, null, false)
                }
                DeviceType.HEART_RATE.toString() ->{
                    bundle.putBoolean(RLExtraValueKey.isSpeedSensor,false)
                    (context as RLMainActivityRL).RLloadFrag(RLFragHeartRateSensorProgress().newInstance(bundle), TAG, true, null, false)
                }
                else ->{ //SPEED AND CADENCE
                    bundle.putBoolean(RLExtraValueKey.isSpeedSensor,true)
                    (context as RLMainActivityRL).RLloadFrag(RLFragSensorProgress().newInstance(bundle), TAG, true, null, false)
                }
            }

        }else if (isBody){
            val VideoData=  requireArguments().getString(RLExtraValueKey.videoData,"")
            val videoID=  requireArguments().getString(RLExtraValueKey.videoId,"")
            val  isRide=  requireArguments().getBoolean(RLExtraValueKey.isRide)
            val bundle = Bundle()
            bundle.putString(RLExtraValueKey.videoData,VideoData)
            bundle.putString(RLExtraValueKey.videoId,videoID)
            bundle.putBoolean(RLExtraValueKey.isRide,isRide)
            when(sensorDeviceType){
                DeviceType.NO_DEVICE.toString() ->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesNormalVideoStart().newInstance(bundle), TAG, true, null, false)
                }
                DeviceType.HEART_RATE.toString() ->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesHeartVideoStart().newInstance(bundle), TAG, true, null, false)
                }
                else ->{ //SPEED AND CADENCE
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesSpeedVideoStart().newInstance(bundle), TAG, true, null, false)
                }
            }

        }else if (isMind){
            val videoData=  requireArguments().getString(RLExtraValueKey.videoData,"")
            val audioVideoType=  requireArguments().getString(RLExtraValueKey.audioVideoType,"")
            val videoId=  requireArguments().getString(RLExtraValueKey.videoId,"")
            val bundle = Bundle()
            bundle.putString(RLExtraValueKey.videoData,videoData)
            bundle.putString(RLExtraValueKey.audioVideoType,audioVideoType)
            bundle.putString(RLExtraValueKey.videoId,videoId)
            when(sensorDeviceType){
                DeviceType.NO_DEVICE.toString() ->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesNormalVideoStart().newInstance(bundle), TAG, true, null, false)
                }
                DeviceType.HEART_RATE.toString() ->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesHeartVideoStart().newInstance(bundle), TAG, true, null, false)
                }
                else ->{ //SPEED AND CADENCE
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesNormalVideoStart().newInstance(bundle), TAG, true, null, false)
                }
            }

        }
    }

    override fun onItemClick(deviceType: String, deviceAddress: String, isconnection: Boolean) {
        adaptercadence.notifyDataSetChanged()
        adapterspeed.notifyDataSetChanged()
        adapter.notifyDataSetChanged()
        if (isconnection){
            sensorDeviceType=deviceType
            RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.last_device_connect, deviceAddress)
        }else{
            if (deviceType.equals(sensorDeviceType)){
                sensorDeviceType = DeviceType.NO_DEVICE.toString()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            viewModel.startScanning()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopScanning()
    }
}