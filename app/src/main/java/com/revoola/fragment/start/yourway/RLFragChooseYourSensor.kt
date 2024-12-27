package com.revoola.fragment.start.yourway


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragChooseYourSensorBinding
import com.revoola.databinding.RlFragSetYourGoalBinding
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
import com.revoola.services.RLBLEService
import com.revoola.commonobject.RLTools
import java.util.UUID

class RLFragChooseYourSensor : RLBaseFragment(), RLItemClickListenerAdapter {
    val TAG: String = RLFragChooseYourSensor::class.java.simpleName
    lateinit var fragBinding: RlFragChooseYourSensorBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var handler: Handler
    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private var isHeartRateDevice = false
    var adapter :RLSensorHeartListAdapter?=null
    var adapterspeed :RLSensorSpeedListAdapter?=null
    var adaptercadence : RLSensorCadenceListAdapter?=null
    var yourWayType=""
    private var connecetedDeviceType = ""
    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChooseYourSensor()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass, inflater, R.layout.rl_frag_choose_your_sensor, container) as RlFragChooseYourSensorBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment, "RLFragChooseYourSensor")
        RLuisetup()

        return fragBinding.root
    }

    private fun RLuisetup() {
        //RLonBackPresAct(fragBinding.ivBack)

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

         yourWayType = requireArguments().getString("YourWayType").toString().trim()

        if (yourWayType.equals("Ride") || yourWayType.equals("Run") || yourWayType.equals("Walk")) {
            fragBinding.cardGps.visibility = View.VISIBLE
            fragBinding.cardHeartRateSensor.visibility = View.GONE
        } else {
            fragBinding.cardGps.visibility = View.GONE
            fragBinding.cardCadenceSensor.visibility = View.GONE
            fragBinding.cardHeartRateSensor.visibility = View.GONE
        }
        fragBinding.tvgo.setOnClickListener {
            var isspeedsensor:Boolean=false
            val lastdevicetype= com.revoola.utils.RLPrefManager.RLgetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect_type, "")
            if (!lastdevicetype.isNullOrEmpty()){
                connecetedDeviceType=lastdevicetype
            }
            if (connecetedDeviceType.equals("HEARTRATESENSOR")){
                isHeartRateDevice=true
                isspeedsensor=false
            }else if (connecetedDeviceType.equals("SPEEDSENSOR")){
                isHeartRateDevice=false
                isspeedsensor=true
            }else{
                isHeartRateDevice=false
                isspeedsensor=false
            }
            RLclickToNextScreenOpen(yourWayType,isspeedsensor)
        }
        fragBinding.tvskip.setOnClickListener {
            isHeartRateDevice=false
            RLclickToNextScreenOpen(yourWayType,false)
        }
    }

    private fun RLclickToNextScreenOpen(yourWayType:String,isspeedsensor:Boolean){
        val isBodyClass=requireArguments().getBoolean("isBody")
        val isMindClass=requireArguments().getBoolean("isMind")
        val isYourWayClass=requireArguments().getBoolean("isYourWay")

        if (isYourWayClass){
            //All Your Way Class Next Open
            if (rlbleService!=null){
                rlbleService!!.RLstopScan()
            }
            var bundle: Bundle = Bundle()
            bundle.putString("YourWayType", yourWayType)
            bundle.putBoolean("isspeedsensor",isspeedsensor)
            if (isHeartRateDevice){
                (context as RLMainActivityRL).RLloadFrag(RLFragHeartRateSensorProgress().newInstance(bundle), TAG, true, RLFragHeartRateSensorProgress::class.java.simpleName, false)
            }else{
                (context as RLMainActivityRL).RLloadFrag(RLFragSensorProgress().newInstance(bundle), TAG, true, RLFragSensorProgress::class.java.simpleName, false)
            }
        }else if (isBodyClass){
            //All Body Class Next Open
            val data=  requireArguments().getString("VIDEODATA","")
            val videoID=  requireArguments().getString("videoID","")
            val  ridetype=  requireArguments().getBoolean("Ride")
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString("videoID",videoID)
            bundle.putBoolean("Ride",ridetype)

            if (isHeartRateDevice){
                (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesHeartVideoStart().newInstance(bundle), TAG, true, null, false)
            }else if(isspeedsensor){
                (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesSpeedVideoStart().newInstance(bundle), TAG, true, null, false)
            }else{
                (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesNormalVideoStart().newInstance(bundle), TAG, true, null, false)
            }
        }else if (isMindClass){
            //All Mind Class Next Open
            val data=  requireArguments().getString("VIDEODATA","")
            val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
            val videoID=  requireArguments().getString("videoID","")
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            bundle.putString("videoID",videoID)
            if (isHeartRateDevice){
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesHeartVideoStart().newInstance(bundle), TAG, true, null, false)
            }else{
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesNormalVideoStart().newInstance(bundle), TAG, true, null, false)
            }
        }

    }
    private fun RLcheckAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(),permissions.toTypedArray(), REQUEST_CODE_BLE_PERMISSIONS)
        } else {
            RLsetupBlutooth()
        }
    }
    private fun RLsetupBlutooth() {
        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        handler = Handler(Looper.getMainLooper())
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN), REQUEST_PERMISSIONS)
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                RLstartBLEService()
            }
        }

    }
    private fun RLstartBLEService() {
        val intent = Intent(requireContext(), RLBLEService::class.java)
        requireActivity().bindService(intent, RLserviceConnection, Context.BIND_AUTO_CREATE)
        val filter = IntentFilter().apply {
            addAction("ACTION_DEVICE_FOUND")
            addAction("ACTION_DATA_RETRIEVED")
            addAction("ACTION_CONNECTION_STATE_CHANGED")
            if (yourWayType.equals("Ride")) {
                addAction("ACTION_DEVICE_FOUND_SPEED")
            }
        }
        requireActivity().registerReceiver(RLbleBroadcastReceiver, filter)
    }
    //BLE SERVICES
    private val RLserviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Request necessary permissions
            }
            RLTools.RlLogDPrint(TAG,"onServiceConnected")
            val binder = service as RLBLEService.RLLocalBinder
            rlbleService = binder.getService()
            // Check if devices are not connected then scan
            isServiceBound = true
            if (yourWayType.equals("Ride")) {
                rlbleService?.RLstartScan(true)
            }else{
                rlbleService?.RLstartScan(false)
            }

            // Check if devices are already connected
            val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT)
            if (connectedDevices.isNotEmpty()) {
                connectedDevices.forEach { device ->
                   RLcheckHeartRateService(device)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            RLTools.RlLogDPrint(TAG,"onServiceDisconnected")
        }
    }
    override fun onStart() {
        super.onStart()
        RLcheckAndRequestPermissions()
    }

    override fun onStop() {
        super.onStop()
        try {
            if (isServiceBound) {
                requireActivity().unbindService(RLserviceConnection)
                isServiceBound = false
            }
            requireActivity().unregisterReceiver(RLbleBroadcastReceiver)
        }catch (e:Exception){
        RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        rlbleService?.RLstopScan()
    }

    //BroadCastReceiver Data
    private val RLbleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_DEVICE_FOUND" -> {
                    fragBinding.cardHeartRateSensor.visibility = View.VISIBLE
                    val lastConnectDeviceAddress =
                        com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, "")

                    val deviceName:String = intent.getStringExtra("DEVICE_NAME").toString()
                    val deviceAddress:String = intent.getStringExtra("DEVICE_ADDRESS").toString()
                    val deviceType:String = intent.getStringExtra("DEVICE_TYPE").toString()

                    if (lastConnectDeviceAddress.equals(deviceAddress)){
                        // Last Connected device found
                        RLhandleDeviceFound(deviceAddress)
                        adapter!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,deviceType,true)))
                    }else{
                        adapter!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,deviceType,false)))
                    }
                }
                "ACTION_DEVICE_FOUND_SPEED" -> {
                    fragBinding.cardCadenceSensor.visibility = View.VISIBLE
                    val lastConnectDeviceAddress = com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, "")

                    val deviceName:String = intent.getStringExtra("DEVICE_NAME").toString()
                    val deviceAddress:String = intent.getStringExtra("DEVICE_ADDRESS").toString()
                    val deviceType:String = intent.getStringExtra("DEVICE_TYPE").toString()

                    if (lastConnectDeviceAddress.equals(deviceAddress)){
                        // Last Connected device found
                        RLhandleDeviceFound(deviceAddress)
                        adapterspeed!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,"SPEEDSENSOR",true)))
                        adaptercadence!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,"CADENCESENSOR",true)))
                    }else{
                        adapterspeed!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,"SPEEDSENSOR",false)))
                        adaptercadence!!.RLaddData(listOf(RLBleListModel(deviceName,deviceAddress,"CADENCESENSOR",false)))
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
              RLsetupBlutooth()
            } else {
                // Handle the case where permissions are not granted
            }
        }
    }

    fun RLhandleDeviceFound(deviceAddress: String) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (device != null) {
            // Handle the found device
            com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, deviceAddress)
          //  rlbleService!!.RLconnectToDevice(device)
        }
    }

    private fun RLcheckHeartRateService(device: BluetoothDevice) {
        val bluetoothGatt = device.connectGatt(requireContext(), false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permissions
                }
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // Discover services once connected
                    gatt.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Request necessary permissions
                }
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // Check if the heart rate service is available
                    val heartRateService: BluetoothGattService? = gatt.getService(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))
                    if (heartRateService != null) {
                        // Heart rate service is available
                        isHeartRateDevice=true
                        connecetedDeviceType="HEARTRATESENSOR"
                    }else {
                        // Heart rate service is not available
                        connecetedDeviceType="SPEEDSENSOR"
                        isHeartRateDevice=false
                    }
                    // Close GATT connection after checking
                    gatt.close()
                }
            }
        })

    }

    override fun onItemClick(deviceType:String,deviceAddress: String,isconnection:Boolean) {
        adaptercadence!!.notifyDataSetChanged()
        adapterspeed!!.notifyDataSetChanged()
        adapter!!.notifyDataSetChanged()
        if (isconnection){
            connecetedDeviceType=deviceType
            RLTools.RlLogDPrint(TAG, "BLE connection isconnection:-  $connecetedDeviceType")
            RLhandleDeviceFound(deviceAddress)
            com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, deviceAddress)
        }else{
            if (deviceType.equals(connecetedDeviceType)){
                connecetedDeviceType=""
            }
            RLTools.RlLogDPrint(TAG, "BLE connection disconnection:- $connecetedDeviceType")
            rlbleService!!.RLdisconnectFromDevice()
        }

    }
}










