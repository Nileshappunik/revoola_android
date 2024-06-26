package com.example.myfirstapp.fragment.start.adapter


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
import com.example.myfirstapp.fragment.start.RLStartClassesMindBody
import com.example.myfirstapp.interfaceall.RLItemClickListenerAdapter
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.UUID

class RLFragChooseYourSensorClass : RLBaseFragment() , RLItemClickListenerAdapter {
    val TAG: String = RLFragChooseYourSensorClass::class.java.simpleName
    lateinit var fragBinding: RlFragChooseYourSensorBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var handler: Handler
    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private var isHeartRateDevice = false
    var adapter : RLSensorListAdapter?=null
    var classtype:String=""
    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChooseYourSensorClass()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass, inflater, R.layout.rl_frag_choose_your_sensor, container) as RlFragChooseYourSensorBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment, "RLFragChooseYourSensor")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvSensorList.layoutManager = linearLayoutManager
        adapter = RLSensorListAdapter(activity,this)
        fragBinding.rvSensorList.adapter = adapter
       val deviceTypeLastConnect= RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect_type, "")
        fragBinding.cardGps.visibility = View.GONE
        fragBinding.tvgo.setOnClickListener {
            if (adapter!=null){
                if (adapter!!.connectedDeviceType.equals("HEARTRATESENSOR")){
                    isHeartRateDevice=true
                }else{
                    isHeartRateDevice=false
                }
                Log.e(TAG,"SIZEOFLIST:- "+adapter!!.dataList.size)
                if(adapter!!.dataList.size<=0){
                    RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect, "no")
                    RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect_type, "")
                }
            }else{
                RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect, "no")
                RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect_type, "")
                isHeartRateDevice=false
            }
            RLclickToNextScreenOpen()
        }
        fragBinding.tvskip.setOnClickListener {
            RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect, "no")
            RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.last_device_connect_type, "")
            isHeartRateDevice=false
            RLclickToNextScreenOpen()
        }
    }
    private fun RLclickToNextScreenOpen(){
        classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        val data=  requireArguments().getString("VIDEODATA","")
        val bundle = Bundle()
        bundle.putString("VIDEODATA",data)
        bundle.putString(RLConstants.CLASSTYPE,classtype)
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        (context as RLMainActivityRL).RLloadFrag(RLStartClassesMindBody().newInstance(bundle), TAG, true, null, false)
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
                    } else {
                        // Heart rate service is not available
                        isHeartRateDevice=false
                    }
                    // Close GATT connection after checking
                    gatt.close()
                }
            }
        })

    }

    override fun onItemClick(deviceAddress: String,isconnection:Boolean) {
        if (isconnection){
            Log.d(TAG, "BLE connection isconnection")
            RLhandleDeviceFound(deviceAddress)
        }else{
            Log.d(TAG, "BLE connection disconnection")
            rlbleService!!.RLdisconnectFromDevice()
        }

    }
}










