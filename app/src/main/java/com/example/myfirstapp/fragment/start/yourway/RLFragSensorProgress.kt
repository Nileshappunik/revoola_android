package com.example.myfirstapp.fragment.start.yourway

import android.Manifest
import android.animation.ObjectAnimator
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragSensorProgressBinding
import com.example.myfirstapp.utils.RLPrefManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.enumclass.RLYourWayArrayType
import com.example.myfirstapp.firebaseModel.RLElevationPoint
import com.example.myfirstapp.firebaseModel.RLLocationDetails
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.services.RLLocationViewModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTimerManager
import com.example.myfirstapp.utils.RLTools
import com.google.gson.Gson
import java.lang.Math.round
import kotlin.math.roundToInt

class RLFragSensorProgress : RLBaseFragment(){
    val TAG: String = RLFragSensorProgress::class.java.simpleName
    lateinit var fragBinding: RlFragSensorProgressBinding
    private lateinit var rlLocationViewModel: RLLocationViewModel
    private val timerManager = RLTimerManager()
    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var  yourWayType:String=""
    private var  isSpeedSensor:Boolean=false
    private var issGpsConnect:Boolean =false
    private var isSpeedSensorConnect:Boolean =false

    private var paceList:MutableList<Int> = mutableListOf()

    private var speedList:MutableList<Double> = mutableListOf()

    private var arrBurntCalories:MutableList<Double> = mutableListOf()
    private var arrCadence:MutableList<Double> = mutableListOf()
    private var arrDistance:MutableList<Double> = mutableListOf()
    private var arrElevation:MutableList<Double> = mutableListOf()
    private var arrSpeed:MutableList<Double> = mutableListOf()
    private var arrCumDistance:MutableList<Double> = mutableListOf()
    private var arrCumSpeed:MutableList<Double> = mutableListOf()
    private var arrCumElevation:MutableList<Double> = mutableListOf()

    private var arrAvgCadence:MutableList<Double> = mutableListOf()
    private var arrMaxCadence:MutableList<Int> = mutableListOf()

    private var arrSpeedForOneKm:MutableList<Double> = mutableListOf()
    private var arrSpeedForOneMile:MutableList<Double> = mutableListOf()

    private var avgSpeedForOneKm:Double=0.0
    private var avgSpeedForOneMile:Double=0.0

    private var maxSpeedForOneKm:Double=0.0
    private var maxSpeedForOneMile:Double=0.0

    private var startTimeKm: Long = 0
    private var startTimeMile: Long = 0
    private var lastLocation: Location? = null
    private var totalDistance = 0.0

    private var stepsNumber:Int=0
    private var distanceNumber:Double=0.0
    private var climbedNumber:Int=0

    private var activeCaloriesNumber:Double=0.0
    private var speedNumber:Double=0.0

    private var paceNumber:Int=0
    private var totalTime:String =""
    private var maxSpeed =0
    private var maxPace =0
    private var maxCadence =0
    private var maxBurntCalories =0

    private var elevationMeter:Double=0.0
    private var cadenceData =0.0
    private var lastGeoElevation: Int = 0
    private var totalGeoElevation: Int = 0
    private var totalElevation=0.0
    private var CumDistance =0.0
    private var CumSpeed =0.0
    private var distance:Double=0.0
    private var burntCalories:Double=0.0
    private var appUnit:String=""

    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0


    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSensorProgress()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_sensor_progress, container) as RlFragSensorProgressBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup(){
        RLstartCountdown()

        yourWayType = requireArguments().getString("YourWayType").toString().trim()
        isSpeedSensor = requireArguments().getBoolean("isspeedsensor",false)

        fragBinding.relaytiveMain.setBackgroundResource(RLTools.RLgetImage1(yourWayType.toLowerCase()))
        rlLocationViewModel =RLLocationViewModel(requireActivity().application)


        RLFirebaseToFatchUserData()

       // fragBinding.txtMaintitle.setText(yourWayType)
        fragBinding.inlayTop.ivTitle.setText(yourWayType)
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setTextColor(resources.getColor(R.color.AppWhiteColor))
        RLwayTypeDesignSet(yourWayType)

        fragBinding.layPause.setOnClickListener {
            try {
                timerManager.RLpause()
                fragBinding.layPause.visibility=View.GONE
                fragBinding.layResumestop.visibility=View.VISIBLE
                if (isServiceBound) {
                    rlbleService!!.RLpauseNotifications()
                }

                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    rlLocationViewModel.RLstopLocationUpdates()
                }

            }catch (e:Exception){
                Log.e(TAG,"Exception:- "+e.message)
            }


        }
        fragBinding.layResume.setOnClickListener {
           try{
                timerManager.RLresume()
                fragBinding.layPause.visibility=View.VISIBLE
                fragBinding.layResumestop.visibility=View.GONE
                if (isServiceBound) {
                    rlbleService!!.RLresumeNotifications()
                }
               if (issGpsConnect && (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride"))){
                   rlLocationViewModel.RLstartLocationUpdates()
               }
           }catch (e:Exception){
               Log.e(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            bundle.putString("totalTime",totalTime)

            if (yourWayType.equals("Ride") && isSpeedSensorConnect){
                bundle.putString("SENSOR", RLConstants.SPEEDSENSOR)
            }else{
                bundle.putString("SENSOR", RLConstants.NOSENSOR)
            }
            bundle.putDouble("burntCalories",noNanValueDouble(burntCalories?:0.00))
            bundle.putDouble("totalElevation",noNanValueDouble(totalElevation?:0.00))

            bundle.putDouble("maxSpeedForOneKm",noNanValueDouble(maxSpeedForOneKm?:0.00))
            bundle.putDouble("maxSpeedForOneMile",noNanValueDouble(maxSpeedForOneMile?:0.00))
            bundle.putDouble("avgSpeedForOneKm",noNanValueDouble(avgSpeedForOneKm?:0.00))
            bundle.putDouble("avgSpeedForOneMile",noNanValueDouble(avgSpeedForOneMile?:0.00))

            bundle.putInt("totalSteps",stepsNumber?:0)
            bundle.putDouble("distance",noNanValueDouble(distance?:0.00))
            bundle.putInt("maxSpeed",maxSpeed?:0)
            bundle.putInt("maxCadence",maxCadence?:0)
            bundle.putInt("maxBurntCalories",maxBurntCalories?:0)


            bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrElevation.toString(),arrElevation.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumElevation.toString(),arrCumElevation.toDoubleArray())

            if (arrSpeedForOneKm.isNullOrEmpty()){
                arrSpeedForOneKm= mutableListOf(0.00)
            }
            if (arrSpeedForOneMile.isNullOrEmpty()){
                arrSpeedForOneMile= mutableListOf(0.00)
            }
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneKm.toString(),arrSpeedForOneKm.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneMile.toString(),arrSpeedForOneMile.toDoubleArray())

            bundle.putDoubleArray(RLYourWayArrayType.arrAvgCadence.toString(),arrAvgCadence.toDoubleArray())
            bundle.putIntegerArrayList(RLYourWayArrayType.arrMaxCadence.toString(),ArrayList(arrMaxCadence))

             bundle.putParcelableArrayList(RLYourWayArrayType.arrDataLocation.toString(), ArrayList(arrDataLocation))
             bundle.putParcelableArrayList(RLYourWayArrayType.arrLocationDetails.toString(),ArrayList(arrLocationDetails))


            try {
                timerManager.RLstop()
                if (isServiceBound) {
                    rlbleService!!.RLstopNotifications()
                }
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    rlLocationViewModel.RLstopLocationUpdates()
                }
            }catch (e:Exception){
                Log.e(TAG,"Exception:- "+e.message)
            }
            (context as RLMainActivityRL).RLloadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

        }
    }
    private fun RLFirebaseToFatchUserData() {
        //Firebase To Fetch UserData
        val databaseManager: RLDatabaseManagerRead = RLDatabaseManagerRead()
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()!!.uid
        val path ="/proposedstructure/revoolaUserSettings/$userId/basicData"
        databaseManager.RlreadData(path){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                appUnit=userData.appUnit
            }
        }
    }
    fun  RLwayTypeDesignSet(yourWayType:String){
        if (yourWayType.equals("Pilates")||yourWayType.equals("Workout")||yourWayType.equals("Yoga")){
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.ic_effort_avg)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.avgeffortpersentage)
            fragBinding.inlayStep.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.relaySensorProgress.visibility=View.GONE
            fragBinding.inlayClimbed.relaySensorProgress.visibility=View.GONE
            fragBinding.inlaySpeed.relaySensorProgress.visibility=View.GONE
            fragBinding.inlayPace.relaySensorProgress.visibility=View.GONE
        }else if (yourWayType.equals("Ride")&& !isSpeedSensor){
            //below set Distance Value
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.distancekm)

            //below set climbed Value
            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.climbedft)

            //below set pace Value
            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.paceperkm)
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")
            fragBinding.inlayClimbed.layAvg.visibility=View.VISIBLE
            fragBinding.inlayClimbed.layMax.visibility=View.VISIBLE
            fragBinding.inlayClimbed.txtMaxNumber.setText("0")
            fragBinding.inlayClimbed.txtAvgNumber.setText("0")

            //below set Speed Value
            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlayPace.txtProgressTime.setText(R.string.speedkmh)
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE

            fragBinding.inlaySpeed.relaySensorProgress.visibility=View.GONE

        }else if (yourWayType.equals("Ride")&& isSpeedSensor){
            //below set Cadence  Value
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.ic_cadence)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.cadencerpm)

            //below set Distance Value
            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            //below set climbed Value
            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)

            //below set pace Value
            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(R.string.paceperkm)
            fragBinding.inlayPace.txtProgressTimeNumber.setText("0")
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE
            fragBinding.inlayPace.txtMaxNumber.setText("0")
            fragBinding.inlayPace.txtAvgNumber.setText("0")

            //below set Speed Value
            fragBinding.inlaySpeed.relaySensorProgress.visibility=View.VISIBLE
            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(R.string.speedkmh)
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE



        }else if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.fd_steps_green)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.step)
            fragBinding.inlayStep.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)

            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(R.string.speedkmh)
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE

            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(R.string.paceperkm)
            fragBinding.inlayPace.txtProgressTimeNumber.setText("0")
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE
            fragBinding.inlayPace.txtMaxNumber.setText("0")
            fragBinding.inlayPace.txtAvgNumber.setText("0")

        }
    }
    //BLE DEVICE CODE START
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
            ActivityCompat.requestPermissions(requireActivity(),permissions.toTypedArray(),
                REQUEST_CODE_BLE_PERMISSIONS
            )
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN),
                REQUEST_PERMISSIONS
            )
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent,
                    REQUEST_ENABLE_BT
                )
            } else {
                RLstartBLEService()
            }
        }

    }
    private fun RLstartBLEService() {
        val lastconnectdevicetype= RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect_type, "")

        if (lastconnectdevicetype.isNullOrEmpty()){
            if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                RLstepGetToGPS()
            }
            Log.e(TAG,"No DEVICE CONNECT SO Step Get To GPS")
        }else{
            if (yourWayType.equals("Ride")&& isSpeedSensor){
                val intent = Intent(requireContext(),RLBLEService::class.java)
                requireActivity().bindService(intent, RLserviceConnection, Context.BIND_AUTO_CREATE)

                val filter = IntentFilter().apply {
                    addAction("ACTION_DATA_RETRIEVED")
                    addAction("ACTION_CONNECTION_STATE_CHANGED")
                }
                requireActivity().registerReceiver(RLbleBroadcastReceiver, filter)
                RLstepGetToGPS()
            }else{
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    RLstepGetToGPS()
                }
                Log.e(TAG,"No DEVICE CONNECT SO Step Get To GPS")
            }

        }
    }
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
            val lastConnectDeviceAddress = RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect, "")
            RLhandleDeviceFound(lastConnectDeviceAddress)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            Log.d(TAG,"onServiceDisconnected")
            if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                RLstepGetToGPS()
            }
        }
    }
    fun RLhandleDeviceFound(deviceAddress: String) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (device != null) {
            isSpeedSensorConnect=true
            rlbleService!!.RLconnectToDevice(device)
        }
    }
    private val RLbleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_DATA_RETRIEVED" -> {
                    val data = intent.getStringExtra("EXTRA_DATA")?:"0"
                    val SPEED = intent.getStringExtra("SPEED")?:"0"
                    val AvgSPEED = intent.getStringExtra("AvgSPEED")?:"0"
                    val DISTANCE = intent.getStringExtra("DISTANCE")?:"0"
                    val CADENCE = intent.getStringExtra("CADENCE")?:"0"
                    val CALORIES = intent.getStringExtra("CALORIES")?:"0"

                    var speedSetValue=RlGetValueInt(SPEED.toString())?:0
                    var avgspeedSetValue=RlGetValueInt(AvgSPEED.toString())?:0
                    var distanceSetValue=RlGetValueInt(DISTANCE.toString())?:0
                    var cadenceSetValue=RlGetValueInt(CADENCE.toString())?:0

                    distanceNumber=RlGetValueDouble(DISTANCE.toString())?:0.0
                    climbedNumber=RlGetValueInt(CADENCE.toString())?:0
                    speedNumber=RlGetValueDouble(SPEED.toString())?:0.0
                    activeCaloriesNumber=RlGetValueDouble(CALORIES.toString())?:0.0
                    cadenceData=RlGetValueDouble(CADENCE.toString())?:0.0
                    maxCadence=RLmax(maxCadence,cadenceData.toInt())

                    val floatSpeed:Float= speedSetValue.toFloat()?:0f
                    paceNumber=calculatePace(floatSpeed)

                    speedList.add(speedSetValue.toDouble())
                    fragBinding.inlaySpeed.txtProgressTimeNumber.setText(speedSetValue.toString())
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                        maxSpeed=RLmax(maxSpeed,speedNumber.roundToInt())
                        fragBinding.inlaySpeed.txtAvgNumber.setText(averageSpeed.toString())
                        fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                    }
                    if (cadenceSetValue>0){
                        fragBinding.inlayStep.txtProgressTimeNumber.setText(CADENCE.toString())
                    }
                    if (distanceSetValue>0){
                        fragBinding.inlayDistance.txtProgressTimeNumber.setText(DISTANCE.toString())
                    }
                    if (paceNumber>0){
                        fragBinding.inlayPace.txtProgressTimeNumber.setText(paceNumber.toString())
                        paceList.add(paceNumber)
                        if (!paceList.isNullOrEmpty()){
                            val averagePace=paceList.average().roundToInt()?:0
                            //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                            maxPace=RLmax(maxPace,paceNumber.toInt())
                            fragBinding.inlayPace.txtAvgNumber.setText( averagePace.toString())
                            fragBinding.inlayPace.txtMaxNumber.setText( maxPace.toString())
                        }
                    }

                }
                "ACTION_CONNECTION_STATE_CHANGED" -> {
                    val device_name = intent.getStringExtra("device_name")
                    val is_connected = intent.getBooleanExtra("is_connected",false)
                    if (is_connected){
                        // commonToast("BLE DEVICE CONNECT")
                        Log.d(TAG,"BLE DEVICE CONNECT")
                    }else{
                        //commonToast("NO ANY BLE DEVICE CONNECT")
                        Log.d(TAG,"NO ANY BLE DEVICE CONNECT")
                        if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                            RLstepGetToGPS()
                        }
                    }
                }
            }
        }
    }
    private fun calculatePace(speed: Float): Int {
        // Cadence is typically measured in revolutions per minute (RPM)
        // We can estimate cadence based on speed, assuming a typical stride length
        // of 2.5 meters per revolution
        val strideLength = 2.5f
        val cadence = (speed / strideLength) * 60
        return cadence.toInt()
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
    private fun RlGetValueDouble(value:String):Double{
        if (value.isNullOrEmpty()){
            return 0.0
        }else if(value.toDouble() < 0) {
            return 0.0
        }else{
            return value.toDouble()
        }
    }
    //BLE DEVICE CODE CLOSE
    private fun RLstartCountdown() {
        val countdownTimeInMillis = 6000L // 5 seconds
        val intervalInMillis = 1000L // 1 second interval

        object : CountDownTimer(countdownTimeInMillis, intervalInMillis) {

            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                fragBinding.countdownText.text = secondsRemaining.toString()

                // Animate the text scale
                val scaleX = ObjectAnimator.ofFloat(fragBinding.countdownText, "scaleX", 0.5f, 1f)
                val scaleY = ObjectAnimator.ofFloat(fragBinding.countdownText, "scaleY", 0.5f, 1f)

                scaleX.duration = 500
                scaleY.duration = 500

                scaleX.start()
                scaleY.start()
            }

            override fun onFinish() {
                fragBinding.countdownText.visibility=View.GONE
                fragBinding.relayCountDown.visibility=View.GONE
                RLtimermain()
                // You can add additional animations or actions here when the countdown ends
            }
        }.start()
    }
    private fun RLtimermain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLformatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){
        burntCalories=burntCalories+activeCaloriesNumber
        maxBurntCalories=RLmax(maxBurntCalories,burntCalories.roundToInt())
        arrBurntCalories.add(noNanValueDouble(activeCaloriesNumber?:0.00))
        arrCadence.add(noNanValueDouble(cadenceData?:0.00))
        arrDistance.add(noNanValueDouble(distanceNumber?:0.00))
        arrSpeed.add(noNanValueDouble(speedNumber?:0.00))
        if (elevationMeter > 0) {
            val relativeAltitude = elevationMeter
            val roundedAltitude = relativeAltitude.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toInt()

            if (lastGeoElevation != null) {
                if (lastGeoElevation!! < roundedAltitude) {
                    totalGeoElevation += (roundedAltitude - lastGeoElevation!!)
                    totalElevation=(totalGeoElevation/10).toDouble()
                }
            }
            lastGeoElevation = roundedAltitude
            arrElevation.add(noNanValueDouble(lastGeoElevation.toDouble()?:0.00))
            arrCumElevation.add(noNanValueDouble((totalGeoElevation/10).toDouble()?:0.00))

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distanceNumber
        arrCumDistance.add(noNanValueDouble(CumDistance?:0.00))
        arrCumSpeed.add(noNanValueDouble(CumSpeed?:0.00))
        distance=CumDistance
        if (yourWayType.equals("Ride")&& !isSpeedSensor){
            fragBinding.inlayDistance.txtProgressTimeNumber.setText(getClimbData(totalElevation))
        }else{
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(getClimbData(totalElevation))
        }

        val elevationpoint=RLElevationPoint(noNanValueDouble(elevationMeter?:0.00),noNanValueDouble(latitude?:0.00), noNanValueDouble(longitude?:0.00))
        arrDataLocation.add(elevationpoint)
        val locationDetails= RLLocationDetails(noNanValueDouble(speedNumber?:0.00),noNanValueDouble(speedNumber?:0.00),noNanValueDouble(latitude?:0.00),0.0, noNanValueDouble(longitude?:0.00),noNanValueDouble(elevationMeter?:0.00))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(noNanValueDouble(arrCadence.average()?:0.00))
        arrMaxCadence.add(maxCadence)
    }

    private fun noNanValueDouble(value:Double):Double{
        if (value.isNaN()){
            return 0.00
        }else{
            return  value
        }
    }

    private fun getClimbData(elevation: Number): String {
        if (!isValidValue(elevation)) return "0"
        val convertedElevation = if (getIsImperial()) elevation.toDouble() * 3.281 else elevation.toDouble()
        val climbData=convertedElevation?:0
        return climbData.toString()
    }

    private fun getIsImperial():Boolean {
        if (appUnit.equals("Imperial")){
            return true;
        }else if (appUnit.equals("Metric")){
            return true;
        }else{
            return false;
        }
    }

    // check the valid value or not return 0
    private fun isValidValue(value: Any?): Boolean {
        return value!= null && value!= "" &&!value.toString().matches(Regex("\\d+"))
    }

    private fun RLformatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    //GPS VALU GET
    private fun RLstepGetToGPS() {
        try {
            issGpsConnect=true
            if (isSpeedSensor){
                rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
                    elevation?.let {
                        Log.d(TAG,"elevation: ${it} m")
                        val elevation=RlGetValueDouble(it.toString())
                        elevationMeter=elevation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                        Log.d(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })
            }else{
                rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
                    speed?.let {
                        Log.d(TAG,"Speed: ${it} m/s")
                        val speedSetValue=RlGetValueDouble(it.toString())
                        speedNumber=speedSetValue
                        speedList.add("%.2f".format(it).toDouble())
                        if (speedSetValue>0){
                            if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                fragBinding.inlayPace.txtProgressTimeNumber.setText("%.2f".format(it))
                            }else {
                                fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                            }
                            if (!speedList.isNullOrEmpty()){
                                val averageSpeed=speedList.average().toDouble()?:0.0
                                //val maxSpeed=speedList.maxOrNull()!!.toDouble()?:0.0
                                maxSpeed=RLmax(maxSpeed,speedNumber.roundToInt())
                                if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                    fragBinding.inlayPace.txtAvgNumber.setText("%.2f".format(averageSpeed).toString())
                                    fragBinding.inlayPace.txtMaxNumber.setText(maxSpeed.toString())
                                }else{
                                    fragBinding.inlaySpeed.txtAvgNumber.setText("%.2f".format(averageSpeed).toString())
                                    fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                                }
                            }
                        }


                    }
                })
                rlLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
                    stepCount?.let {
                        Log.d(TAG,"Steps: $stepCount")
                        val stepSetValue=RlGetValueInt(stepCount.toString())
                        stepsNumber=stepSetValue
                        //stepsList.add(stepSetValue)
                        if (stepSetValue>0){
                            if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
                                fragBinding.inlayStep.txtProgressTimeNumber.setText(stepCount.toString())
                            }
                        }
                    }
                })
                rlLocationViewModel.distanceData.observe(viewLifecycleOwner, Observer { distance ->
                    distance?.let {
                        val totalDistance=it //     round(it * 100) / 100
                        Log.d(TAG,"Distance: $totalDistance km")

                        val totalDistanceValue=RlGetValueDouble(totalDistance.toString())
                        distanceNumber=totalDistanceValue
                        // distanceList.add(totalDistanceValue)
                        if (totalDistanceValue>0){
                            if  (yourWayType.equals("Ride")&& !isSpeedSensor){
                                fragBinding.inlayStep.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                            }else{
                                fragBinding.inlayDistance.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                            }
                        }



                    }
                })
                rlLocationViewModel.caloriesBurnedData.observe(viewLifecycleOwner, Observer { calories ->
                    calories?.let {
                        val totalCaloriesBurned=it
                        Log.d(TAG,"CaloriesBurned: $totalCaloriesBurned")

                        val totalCaloriesBurnedValue=RlGetValueDouble(totalCaloriesBurned.toString())
                        activeCaloriesNumber=totalCaloriesBurnedValue
                        if (totalCaloriesBurnedValue>0){
                            //fragBinding.inlayStep.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())
                        }

                    }
                })
                rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
                    pace?.let {
                        val totalspace=round(it * 100)  / 100
                        Log.d(TAG, "Pace: $totalspace min/km")
                        val totalspaceValue=RlGetValueInt(totalspace.toString())
                        paceNumber=totalspaceValue
                        paceList.add(totalspaceValue)
                        if (totalspaceValue>0){
                            if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                fragBinding.inlayClimbed.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                            }else{
                                fragBinding.inlayPace.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                            }
                            if (!paceList.isNullOrEmpty()){
                                val averagePace=paceList.average().roundToInt()?:0
                                //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                                maxPace=RLmax(maxPace,paceNumber.toInt())
                                if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                    fragBinding.inlayClimbed.txtAvgNumber.setText(averagePace.toString())
                                    fragBinding.inlayClimbed.txtMaxNumber.setText( maxPace.toString())
                                }else{
                                    fragBinding.inlayPace.txtAvgNumber.setText( averagePace.toString())
                                    fragBinding.inlayPace.txtMaxNumber.setText( maxPace.toString())
                                }
                            }

                        }


                    }
                })
                rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
                    elevation?.let {
                        Log.d(TAG,"elevation: ${it} m")
                        val elevation=RlGetValueDouble(it.toString())
                        elevationMeter=elevation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                        Log.d(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })

                rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
                    cadence?.let {
                        Log.d(TAG,"cadenceData: ${it} ")
                        val _cadenceData=RlGetValueDouble(it.toString())
                        cadenceData=_cadenceData
                        maxCadence=RLmax(maxCadence,cadenceData.toInt())
                    }
                })
            }
            rlLocationViewModel.RLstartLocationUpdates()
        }
        catch (e:Exception){
            Log.e(TAG,"EXCEPTION GetGPS:- ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        RLcheckAndRequestPermissions()
        //timerManager.resume()
    }
    override fun onResume() {
        super.onResume()
       // timerManager.resume()
    }
    override fun onPause() {
        super.onPause()
       // timerManager.pause()
    }
    override fun onDestroy() {
        super.onDestroy()
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

    private fun RLtrackTimePerKilometer(location: Location) {
        if (lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)
            totalDistance += distance

            // Check if one kilometer is reached
            if (totalDistance >= 1000) {
                val currentTime = System.currentTimeMillis()
                val elapsedTimeKm = (currentTime - startTimeKm) / 1000.0 // in seconds
                maxSpeedForOneKm=RLmax(maxSpeedForOneKm.roundToInt(),elapsedTimeKm.roundToInt()).toDouble()
                arrSpeedForOneKm.add(RlGetValueDouble(elapsedTimeKm.toString()))
                avgSpeedForOneKm=arrSpeedForOneKm.average()
                println("Time taken for last kilometer: $elapsedTimeKm seconds")

                // Reset distance and start time for the next kilometer
                totalDistance -= 1000
                startTimeKm = currentTime
            }

            // Check if one mile is reached
            if (totalDistance >= 1609.34) {
                val currentTime = System.currentTimeMillis()
                val elapsedTimeMile = (currentTime - startTimeMile) / 1000.0 // in seconds
                maxSpeedForOneMile=RLmax(maxSpeedForOneMile.roundToInt(),elapsedTimeMile.roundToInt()).toDouble()
                println("Time taken for last mile: $elapsedTimeMile seconds")
                arrSpeedForOneMile.add(RlGetValueDouble(elapsedTimeMile.toString()))
                avgSpeedForOneMile=arrSpeedForOneMile.average()
                // Reset distance and start time for the next mile
                totalDistance -= 1609.34
                startTimeMile = currentTime
            }
        } else {
            // Initialize start times for the first kilometer and mile
            startTimeKm = System.currentTimeMillis()
            startTimeMile = System.currentTimeMillis()
        }
        lastLocation = location
    }
    //Max Value get
    fun RLmax(previous: Int, next: Int): Int {
        return when {
            previous > next -> previous
            else ->next
        }
    }

}