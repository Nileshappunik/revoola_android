package com.example.myfirstapp.fragment.start.yourway


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
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragChooseYourSensorBinding
import com.example.myfirstapp.databinding.RlFragSetYourGoalBinding
import com.example.myfirstapp.fragment.start.adapter.RLBleListModel
import com.example.myfirstapp.fragment.start.adapter.RLSensorCadenceListAdapter
import com.example.myfirstapp.fragment.start.adapter.RLSensorHeartListAdapter
import com.example.myfirstapp.fragment.start.adapter.RLSensorSpeedListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListenerAdapter
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLPrefManager
import java.util.UUID

class RLFragChooseYourSensor : RLBaseFragment(),RLItemClickListenerAdapter {
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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment, "RLFragChooseYourSensor")
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
            val lastdevicetype= RLPrefManager.RLgetSomeStringValue(context, RLPrefManager.last_device_connect_type, "")
            if (!lastdevicetype.isNullOrEmpty()){
                connecetedDeviceType=lastdevicetype
            }
            if (connecetedDeviceType.equals("HEARTRATESENSOR")){
                isHeartRateDevice=true
            }else{
                isHeartRateDevice=false
            }
            RLclickToNextScreenOpen(yourWayType)
        }
        fragBinding.tvskip.setOnClickListener {
            isHeartRateDevice=false
            RLclickToNextScreenOpen(yourWayType)
        }
    }



    private fun RLclickToNextScreenOpen(yourWayType:String){
        var bundle: Bundle = Bundle()
        bundle.putString("YourWayType", yourWayType)
        if (isHeartRateDevice){
            (context as RLMainActivityRL).RLloadFrag(RLFragHeartRateSensorProgress().newInstance(bundle), TAG, true, RLFragHeartRateSensorProgress::class.java.simpleName, false)
        }else{
            (context as RLMainActivityRL).RLloadFrag(RLFragSensorProgress().newInstance(bundle), TAG, true, RLFragSensorProgress::class.java.simpleName, false)
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
        val intent = Intent(requireContext(),RLBLEService::class.java)
        requireActivity().bindService(intent, RLserviceConnection, Context.BIND_AUTO_CREATE)

        val filter = IntentFilter().apply {
            addAction("ACTION_DEVICE_FOUND")
            addAction("ACTION_DATA_RETRIEVED")
            addAction("ACTION_CONNECTION_STATE_CHANGED")
            if (yourWayType.equals("Ride") || yourWayType.equals("Run") || yourWayType.equals("Walk")) {
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
            Log.d(TAG,"onServiceConnected")
            val binder = service as RLBLEService.RLLocalBinder
            rlbleService = binder.getService()
            // Check if devices are not connected then scan
            isServiceBound = true
            rlbleService?.RLstartScan()
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
            Log.d(TAG,"onServiceDisconnected")
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
         Log.e(TAG,"Exception:- "+e.message)
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
                        RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect, "")

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
                    val lastConnectDeviceAddress = RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect, "")

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
            RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect, deviceAddress)
            rlbleService!!.RLconnectToDevice(device)
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
                    } else {
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
            Log.d(TAG, "BLE connection isconnection:-  $connecetedDeviceType")
            RLhandleDeviceFound(deviceAddress)
        }else{
            if (deviceType.equals(connecetedDeviceType)){
                connecetedDeviceType=""
            }
            Log.d(TAG, "BLE connection disconnection:- $connecetedDeviceType")
            rlbleService!!.RLdisconnectFromDevice()
        }

    }
}










