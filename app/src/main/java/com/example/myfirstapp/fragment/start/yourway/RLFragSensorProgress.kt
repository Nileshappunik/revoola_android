package com.example.myfirstapp.fragment.start.yourway

import android.content.pm.ActivityInfo
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
import com.example.myfirstapp.enumclass.RLYourWayArrayType
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.services.RLLocationViewModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTimerManager
import com.example.myfirstapp.utils.RLTools
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


    private var stepsNumber:Int=0
    private var distanceNumber:Double=0.0
    private var climbedNumber:Int=0

    private var activeCaloriesNumber:Double=0.0
    private var speedNumber:Double=0.0

    private var paceNumber:Int=0
    private var totalTime:String =""
    private var maxSpeed =0
    private var maxPace =0

    private var elevationMeter:Double=0.0
    private var cadenceData =0.0
    private var lastGeoElevation: Int = 0
    private var totalGeoElevation: Int = 0
    private var totalElevation=0.0
    private var CumDistance =0.0
    private var CumSpeed =0.0
    private var distance:Double=0.0
    private var burntCalories:Double=0.0


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
            bundle.putDouble("burntCalories",burntCalories)
            bundle.putDouble("totalElevation",totalElevation)
            bundle.putInt("totalSteps",stepsNumber)
            bundle.putDouble("distance",distance)


            bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrElevation.toString(),arrElevation.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumElevation.toString(),arrCumElevation.toDoubleArray())


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
                    val data = intent.getStringExtra("EXTRA_DATA")
                    val SPEED = intent.getStringExtra("SPEED")
                    val AvgSPEED = intent.getStringExtra("AvgSPEED")
                    val DISTANCE = intent.getStringExtra("DISTANCE")
                    val CADENCE = intent.getStringExtra("CADENCE")
                    val CALORIES = intent.getStringExtra("CALORIES")

                    var speedSetValue=RlGetValueInt(SPEED!!.toString())
                    var avgspeedSetValue=RlGetValueInt(AvgSPEED!!.toString())
                    var distanceSetValue=RlGetValueInt(DISTANCE!!.toString())
                    var cadenceSetValue=RlGetValueInt(CADENCE!!.toString())

                   // speedList.add(RlGetValueInt(SPEED!!.toString()))
                  // climbedList.add(RlGetValueInt(CADENCE!!.toString()))
                  //  distanceList.add(RlGetValueInt(DISTANCE!!.toString()))
                    distanceNumber=RlGetValueDouble(DISTANCE!!.toString())
                    climbedNumber=RlGetValueInt(CADENCE!!.toString())
                    speedNumber=RlGetValueDouble(SPEED!!.toString())
                    activeCaloriesNumber=RlGetValueDouble(CALORIES!!.toString())
                    cadenceData=RlGetValueDouble(CADENCE!!.toString())

                    if (yourWayType.toLowerCase().equals("ride")){
                        if (speedSetValue>0){
                            fragBinding.inlaySpeed.txtProgressTimeNumber.setText(SPEED.toString()+" km/h")
                            if (!speedList.isNullOrEmpty()){
                                fragBinding.inlaySpeed.txtAvgNumber.setText(speedList.average().toString())
                                fragBinding.inlaySpeed.txtMaxNumber.setText(speedList.maxOrNull().toString())
                            }
                        }
                       /* if (avgspeedSetValue>0){
                            fragBinding.inlayPace.txtAvgNumber.setText(AvgSPEED.toString()+" km/h")
                        }*/
                        if (distanceSetValue>0){
                            fragBinding.inlayDistance.txtProgressTimeNumber.setText(DISTANCE.toString()+" meter")
                        }
                        if (cadenceSetValue>0){
                            fragBinding.inlayStep.txtProgressTimeNumber.setText(CADENCE.toString())
                            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(CADENCE.toString())
                        }

                    }else{
                        if (speedSetValue>0){
                            fragBinding.inlaySpeed.txtProgressTimeNumber.setText(SPEED.toString()+" km/h")
                        }
                        if (avgspeedSetValue>0){
                            fragBinding.inlaySpeed.txtAvgNumber.setText(AvgSPEED.toString()+" km/h")
                        }
                        if (distanceSetValue>0){
                            fragBinding.inlayDistance.txtProgressTimeNumber.setText(DISTANCE.toString()+" meter")
                        }
                        if (cadenceSetValue>0){
                            fragBinding.inlayStep.txtProgressTimeNumber.setText(CADENCE.toString())
                            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(CADENCE.toString())
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
        arrBurntCalories.add(activeCaloriesNumber)
        arrCadence.add(cadenceData)
        arrDistance.add(distanceNumber)
        arrSpeed.add(speedNumber)
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
            arrElevation.add(lastGeoElevation.toDouble())
            arrCumElevation.add((totalGeoElevation/10).toDouble())

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distanceNumber
        arrCumDistance.add(CumDistance)
        arrCumSpeed.add(CumSpeed)
        distance=CumDistance
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
            rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
            speed?.let {
                Log.d(TAG,"Speed: ${it} m/s")
                val speedSetValue=RlGetValueDouble(it.toString())
                speedNumber=speedSetValue
                //speedList.add(speedSetValue)
                if (speedSetValue>0){
                    if (yourWayType.toLowerCase().equals("ride")){
                        fragBinding.inlayPace.txtProgressTimeNumber.setText("%.2f".format(it))
                    }else {
                        fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                    }
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                        //val maxSpeed=speedList.maxOrNull()!!.toDouble()?:0.0
                         maxSpeed=RLmax(maxSpeed,speedNumber.roundToInt())
                        if (yourWayType.toLowerCase().equals("ride")){
                            fragBinding.inlayPace.txtAvgNumber.setText( "%.2f".format(averageSpeed).toString())
                            fragBinding.inlayPace.txtMaxNumber.setText( "%.2f".format(maxSpeed).toString())
                        }else{
                            fragBinding.inlaySpeed.txtAvgNumber.setText( "%.2f".format(averageSpeed).toString())
                            fragBinding.inlaySpeed.txtMaxNumber.setText( "%.2f".format(maxSpeed).toString())
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
                        fragBinding.inlayClimbed.txtProgressTimeNumber.setText(stepCount.toString())

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
                    if (yourWayType.toLowerCase().equals("ride")){
                        fragBinding.inlayStep.txtProgressTimeNumber.setText(totalDistance.toString())
                    }else{
                        fragBinding.inlayDistance.txtProgressTimeNumber.setText(totalDistance.toString())
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
                    // paceList.add(totalspaceValue)
                    if (totalspaceValue>0){
                        if (yourWayType.toLowerCase().equals("ride")){
                            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(totalspace.toString())
                        }else{
                            fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
                        }
                        if (!paceList.isNullOrEmpty()){
                            val averagePace=paceList.average().roundToInt()?:0
                            //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                             maxPace=RLmax(maxPace,paceNumber.toInt())
                            if (yourWayType.toLowerCase().equals("ride")){
                                fragBinding.inlayClimbed.txtAvgNumber.setText(averagePace.toString())
                                fragBinding.inlayClimbed.txtMaxNumber.setText(maxPace.toString())
                            }else{
                                fragBinding.inlayPace.txtAvgNumber.setText(averagePace.toString())
                                fragBinding.inlayPace.txtMaxNumber.setText(maxPace.toString())
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

            rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
                cadence?.let {
                    Log.d(TAG,"cadenceData: ${it} ")
                    val _cadenceData=RlGetValueDouble(it.toString())
                    cadenceData=_cadenceData
                }
            })

            rlLocationViewModel.RLstartLocationUpdates()
        }catch (e:Exception){
            Log.e(TAG,"EXCEPTION GETGPS:- ${e.message}")
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

    //Max Value get
    fun RLmax(previous: Int, next: Int): Int {
        return when {
            previous > next -> previous
            else ->next
        }
    }

}