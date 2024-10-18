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
import android.graphics.Color
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
import com.example.myfirstapp.utils.RLPrefManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragHeartrateSensorProgressBinding
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.services.RLLocationViewModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTimerManager
import com.example.myfirstapp.utils.RLTools
import com.google.gson.Gson
import java.lang.Math.round
import kotlin.math.roundToInt

class RLFragHeartRateSensorProgress : RLBaseFragment(){
    val TAG: String = RLFragHeartRateSensorProgress::class.java.simpleName
    lateinit var fragBinding: RlFragHeartrateSensorProgressBinding
    private val timerManager = RLTimerManager()
    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var rlLocationViewModel: RLLocationViewModel
    var  yourWayType:String=""

    private var heartRateList:MutableList<Int> = mutableListOf()
    private var stepsList:MutableList<Int> = mutableListOf()
    private var distanceList:MutableList<Double> = mutableListOf()
    private var climbedList:MutableList<Int> = mutableListOf()
    private var activeCaloriesList:MutableList<Double> = mutableListOf()

    private var paceList:MutableList<Int> = mutableListOf()
    private var avgSpaceList:MutableList<Int> = mutableListOf()
    private var maxxPaceList:MutableList<Int> = mutableListOf()
    private var arrRevPercentage:MutableList<Double> = mutableListOf()
    private var arravgRevPercentage:MutableList<Double> = mutableListOf()
    private var arrRevSecond:MutableList<Double> = mutableListOf()

    private var speedList:MutableList<Double> = mutableListOf()
    private var avgSpeedList:MutableList<Double> = mutableListOf()
    private var maxsSpeedList:MutableList<Double> = mutableListOf()

    private var heartRateNumber:Int=0
    private var stepsNumber:Int=0
    private var distanceNumber:Double=0.0
    private var climbedNumber:Int=0

    private var activeCaloriesNumber:Double=0.0
    private var speedNumber:Double=0.0
    private var avgSpeedNumber:Double=0.0
    private var maxsSpeedNumber:Double=0.0

    private var paceNumber:Int=0
    private var avgSpaceNumber:Int=0
    private var maxxPaceNumber:Int=0
    var totalTime:String =""


    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    var burntCalories =0.0
    var totalRev  =0.0
    var lastrevPercentage=0.0
    var maxRevPercentage =0.0
    var minRevPercentage =0.0
    var maxSpeed =0
    var maxHeartrate =0

    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragHeartRateSensorProgress()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragHeartrateSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_heartrate_sensor_progress, container) as RlFragHeartrateSensorProgressBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString("YourWayType").toString().trim()
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        //fragBinding.txtMaintitle.setText(yourWayType)
        rlLocationViewModel =RLLocationViewModel(requireActivity().application)
        RLFirebaseToFatchUserData()

        fragBinding.relaytiveMain.setBackgroundResource(RLTools.RLgetImage1(yourWayType.toLowerCase()))
        fragBinding.inlayTop.ivTitle.setText(yourWayType)
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setTextColor(resources.getColor(R.color.AppWhiteColor))


        fragBinding.circularProgressBar.RLsetProgress(100)
        fragBinding.circularProgressBar.RLsetMaxProgress(100)
        fragBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppOrangeColor))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.RLsetStrokeWidth(15f)

        fragBinding.inlayCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtProgressTime.setText(R.string.activecalories)
        fragBinding.inlayCalories.txtProgressTimeNumber.setText("0")

        fragBinding.inlayHeartrate.imgTime.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtProgressTime.setText(R.string.heartratebpm)
        fragBinding.inlayHeartrate.layAvg.visibility=View.VISIBLE
        fragBinding.inlayHeartrate.layMax.visibility=View.VISIBLE

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
               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.RLstartLocationUpdates()
               }
           }catch (e:Exception){
               Log.e(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
           try{
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

            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            bundle.putString("totalTime",totalTime)
            bundle.putIntegerArrayList("heartRateList",ArrayList(heartRateList))
            bundle.putIntegerArrayList("stepsList",ArrayList(stepsList))
            bundle.putDoubleArray("distanceList",distanceList.toDoubleArray())
            bundle.putIntegerArrayList("climbedList",ArrayList(climbedList))
            bundle.putIntegerArrayList("paceList",ArrayList(paceList))
            bundle.putDoubleArray("speedList",speedList.toDoubleArray())
            bundle.putDoubleArray("activeCaloriesList",activeCaloriesList.toDoubleArray())
            bundle.putDoubleArray("avgSpeedList",avgSpeedList.toDoubleArray())
            bundle.putDoubleArray("maxsSpeedList",maxsSpeedList.toDoubleArray())
            bundle.putIntegerArrayList("avgSpaceList",ArrayList(avgSpaceList))
            bundle.putIntegerArrayList("maxxPaceList",ArrayList(maxxPaceList))
            bundle.putString("SENSOR",RLConstants.HEARTSENSOR)

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
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge=RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
            }
        }
    }

    private fun  RLwayTypeDesignSet(yourWayType:String){
        if (yourWayType.equals("Pilates")||yourWayType.equals("Workout")||yourWayType.equals("Yoga")){
            fragBinding.layout2.visibility=View.GONE
            fragBinding.layout3.visibility=View.GONE

            fragBinding.inlayCadence.imgTime.setImageResource(R.drawable.ic_effort_avg)
            fragBinding.inlayCadence.txtProgressTime.setText(R.string.avgeffortpersentage)
            fragBinding.inlayCadence.txtProgressTimeNumber.setText("0")

        }else if (yourWayType.equals("Ride")){

            fragBinding.inlayCadence.imgTime.setImageResource(R.drawable.ic_cadence)
            fragBinding.inlayCadence.txtProgressTime.setText(R.string.cadencerpm)
            fragBinding.inlayCadence.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

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

        }else if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
            fragBinding.inlayCadence.imgTime.setImageResource(R.drawable.fd_steps_green)
            fragBinding.inlayCadence.txtProgressTime.setText(R.string.step)
            fragBinding.inlayCadence.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

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
            ActivityCompat.requestPermissions(requireActivity(),permissions.toTypedArray(), REQUEST_CODE_BLE_PERMISSIONS
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
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                RLstartBLEService()
            }
        }

    }
    private fun RLstartBLEService() {
            if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                RLstepGetToGPS()
            }
            val intent = Intent(requireContext(),RLBLEService::class.java)
            requireActivity().bindService(intent, RLserviceConnection, Context.BIND_AUTO_CREATE)

            val filter = IntentFilter().apply {
                addAction("ACTION_DATA_RETRIEVED_HEART")
            }
            requireActivity().registerReceiver(RLbleBroadcastReceiver, filter)

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
        }
    }
   private fun RLhandleDeviceFound(deviceAddress: String) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (device != null) {
            rlbleService!!.RLconnectToDevice(device)
        }
    }
    private val RLbleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_DATA_RETRIEVED_HEART" -> {
                    val data = intent.getStringExtra("EXTRA_DATA")
                    heartRateNumber=RlGetValueInt(data.toString())
                   // heartRateList.add(RlGetValueInt(data.toString()))
                    var heartRateSetValue=RlGetValueInt(data.toString())
                    if (heartRateSetValue > 0){
                        fragBinding.txtEffortNumber.setText(data)
                        fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(data)
                        maxHeartrate=RLmax(maxHeartrate,heartRateNumber)
                        fragBinding.inlayHeartrate.txtMaxNumber.setText(maxHeartrate.toString())
                        if (!heartRateList.isNullOrEmpty()){
                            val avgHeartRate=heartRateList.average().roundToInt()?:0
                            fragBinding.inlayHeartrate.txtAvgNumber.setText(avgHeartRate.toString())
                        }

                    }
                }
            }
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
                RLtimerMain()
                // You can add additional animations or actions here when the countdown ends
            }
        }.start()
    }
    fun RLtimerMain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLformatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){

        heartRateList.add(heartRateNumber)
        stepsList.add(stepsNumber)
        distanceList.add(distanceNumber)
        climbedList.add(climbedNumber)
        paceList.add(paceNumber)
        speedList.add(speedNumber)
        activeCaloriesList.add(activeCaloriesNumber)
        avgSpeedList.add(avgSpeedNumber)
        maxsSpeedList.add(maxsSpeedNumber)
        avgSpaceList.add(avgSpaceNumber)
        maxxPaceList.add(maxxPaceNumber)

        val currentCalories=calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble())
        val REVPer=calculateREVPer(heartRateNumber,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender) //only REV
        arrRevPercentage.add(REVPer)
        arravgRevPercentage.add(avgOfArray(arrRevPercentage))
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(REVSec)
        totalRev = totalRev+ REVSec
        lastrevPercentage=REVPer
        maxRevPercentage=RLmax(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLmin(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        burntCalories=burntCalories+currentCalories


        val totalCaloriesBurnedValue=RlGetValueDouble(burntCalories.toString()).roundToInt()
        fragBinding.inlayCalories.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())

    }
    ////////////////////////////////////////////////////////////////////////////////////////////

   /* fun zoneDiff(REVPer: Int) {
        when {
            REVPer <= 30 -> {
                //Zone 1
                sessionDetails.zone1.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone1.arrHr.add(heartRate)
                sessionDetails.zone1.arrPower.add(power)
                sessionDetails.zone1.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone1.burntCalories += calory
                sessionDetails.zone1.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }
                sessionDetails.zone1.seconds++
                sessionDetails.zone1.totalRev += REVSec

                sessionSummary.zone1.avgCadence = avgOfArray(sessionDetails.zone1.arrCadence)
                sessionSummary.zone1.avgHr = avgOfArray(sessionDetails.zone1.arrHr)
                sessionSummary.zone1.avgPower = avgOfArray(sessionDetails.zone1.arrPower)
                sessionSummary.zone1.avgSpeed = avgOfArray(sessionDetails.zone1.arrSpeed)

                sessionSummary.zone1.burntCalories = sessionDetails.zone1.burntCalories
                sessionSummary.zone1.distance = sessionDetails.zone1.distance
                sessionSummary.zone1.seconds = sessionDetails.zone1.seconds
                sessionSummary.zone1.totalRev = sessionDetails.zone1.totalRev
            }
            REVPer <= 50 -> {
                //Zone 2
                sessionDetails.zone2.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone2.arrHr.add(heartRate)
                sessionDetails.zone2.arrPower.add(power)
                sessionDetails.zone2.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone2.burntCalories += calory
                sessionDetails.zone2.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }
                sessionDetails.zone2.seconds++
                sessionDetails.zone2.totalRev += REVSec

                sessionSummary.zone2.avgCadence = avgOfArray(sessionDetails.zone2.arrCadence)
                sessionSummary.zone2.avgHr = avgOfArray(sessionDetails.zone2.arrHr)
                sessionSummary.zone2.avgPower = avgOfArray(sessionDetails.zone2.arrPower)
                sessionSummary.zone2.avgSpeed = avgOfArray(sessionDetails.zone2.arrSpeed)

                sessionSummary.zone2.burntCalories = sessionDetails.zone2.burntCalories
                sessionSummary.zone2.distance = sessionDetails.zone2.distance
                sessionSummary.zone2.seconds = sessionDetails.zone2.seconds
                sessionSummary.zone2.totalRev = sessionDetails.zone2.totalRev
            }
            REVPer <= 60 -> {
                //Zone 3
                sessionDetails.zone3.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone3.arrHr.add(heartRate)
                sessionDetails.zone3.arrPower.add(power)
                sessionDetails.zone3.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone3.burntCalories += calory
                sessionDetails.zone3.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }
                sessionDetails.zone3.seconds++
                sessionDetails.zone3.totalRev += REVSec

                sessionSummary.zone3.avgCadence = avgOfArray(sessionDetails.zone3.arrCadence)
                sessionSummary.zone3.avgHr = avgOfArray(sessionDetails.zone3.arrHr)
                sessionSummary.zone3.avgPower = avgOfArray(sessionDetails.zone3.arrPower)
                sessionSummary.zone3.avgSpeed = avgOfArray(sessionDetails.zone3.arrSpeed)

                sessionSummary.zone3.burntCalories = sessionDetails.zone3.burntCalories
                sessionSummary.zone3.distance = sessionDetails.zone3.distance
                sessionSummary.zone3.seconds = sessionDetails.zone3.seconds
                sessionSummary.zone3.totalRev = sessionDetails.zone3.totalRev
            }
            REVPer <= 70 -> {
                //Zone 4
                sessionDetails.zone4.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone4.arrHr.add(heartRate)
                sessionDetails.zone4.arrPower.add(power)
                sessionDetails.zone4.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone4.burntCalories += calory
                sessionDetails.zone4.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }
                sessionDetails.zone4.seconds++
                sessionDetails.zone4.totalRev += REVSec

                sessionSummary.zone4.avgCadence = avgOfArray(sessionDetails.zone4.arrCadence)
                sessionSummary.zone4.avgHr = avgOfArray(sessionDetails.zone4.arrHr)
                sessionSummary.zone4.avgPower = avgOfArray(sessionDetails.zone4.arrPower)
                sessionSummary.zone4.avgSpeed = avgOfArray(sessionDetails.zone4.arrSpeed)

                sessionSummary.zone4.burntCalories = sessionDetails.zone4.burntCalories
                sessionSummary.zone4.distance = sessionDetails.zone4.distance
                sessionSummary.zone4.seconds = sessionDetails.zone4.seconds
                sessionSummary.zone4.totalRev = sessionDetails.zone4.totalRev
            }
            REVPer <= 80 -> {
                //Zone 5
                sessionDetails.zone5.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone5.arrHr.add(heartRate)
                sessionDetails.zone5.arrPower.add(power)
                sessionDetails.zone5.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone5.burntCalories += calory
                sessionDetails.zone5.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }

                sessionDetails.zone5.seconds++
                sessionDetails.zone5.totalRev += REVSec

                sessionSummary.zone5.avgCadence = avgOfArray(sessionDetails.zone5.arrCadence)
                sessionSummary.zone5.avgHr = avgOfArray(sessionDetails.zone5.arrHr)
                sessionSummary.zone5.avgPower = avgOfArray(sessionDetails.zone5.arrPower)
                sessionSummary.zone5.avgSpeed = avgOfArray(sessionDetails.zone5.arrSpeed)

                sessionSummary.zone5.burntCalories = sessionDetails.zone5.burntCalories
                sessionSummary.zone5.distance = sessionDetails.zone5.distance
                sessionSummary.zone5.seconds = sessionDetails.zone5.seconds
                sessionSummary.zone5.totalRev = sessionDetails.zone5.totalRev
            }
            REVPer <= 90 -> {
                //Zone 6
                sessionDetails.zone6.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone6.arrHr.add(heartRate)
                sessionDetails.zone6.arrPower.add(power)
                sessionDetails.zone6.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone6.burntCalories += calory
                sessionDetails.zone6.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }
                sessionDetails.zone6.seconds++
                sessionDetails.zone6.totalRev += REVSec

                sessionSummary.zone6.avgCadence = avgOfArray(sessionDetails.zone6.arrCadence)
                sessionSummary.zone6.avgHr = avgOfArray(sessionDetails.zone6.arrHr)
                sessionSummary.zone6.avgPower = avgOfArray(sessionDetails.zone6.arrPower)
                sessionSummary.zone6.avgSpeed = avgOfArray(sessionDetails.zone6.arrSpeed)

                sessionSummary.zone6.burntCalories = sessionDetails.zone6.burntCalories
                sessionSummary.zone6.distance = sessionDetails.zone6.distance
                sessionSummary.zone6.seconds = sessionDetails.zone6.seconds
                sessionSummary.zone6.totalRev = sessionDetails.zone6.totalRev
            }
            REVPer <= 100 -> {
                //Zone 7
                sessionDetails.zone7.arrCadence.add(CMCStatus.cadence)
                sessionDetails.zone7.arrHr.add(heartRate)
                sessionDetails.zone7.arrPower.add(power)
                sessionDetails.zone7.arrSpeed.add(
                    if (CMCStatus.speed == 0) {
                        CMCStatus.speedT ?: 0
                    } else {
                        CMCStatus.speed ?: 0
                    }
                )
                sessionDetails.zone7.burntCalories += calory
                sessionDetails.zone7.distance +=
                    if (sampleDistance == 0) {
                        CMCStatus.distanceT ?: 0
                    } else {
                        sampleDistance ?: 0
                    }

                sessionDetails.zone7.seconds++
                sessionDetails.zone7.totalRev += REVSec

                sessionSummary.zone7.avgCadence = avgOfArray(sessionDetails.zone7.arrCadence)
                sessionSummary.zone7.avgHr = avgOfArray(sessionDetails.zone7.arrHr)
                sessionSummary.zone7.avgPower = avgOfArray(sessionDetails.zone7.arrPower)
                sessionSummary.zone7.avgSpeed = avgOfArray(sessionDetails.zone7.arrSpeed)

                sessionSummary.zone7.burntCalories = sessionDetails.zone7.burntCalories
                sessionSummary.zone7.distance = sessionDetails.zone7.distance
                sessionSummary.zone7.seconds = sessionDetails.zone7.seconds
                sessionSummary.zone7.totalRev = sessionDetails.zone7.totalRev
            }
        }
    }*/

    private fun calculateREVPer(heartRate: Int, weight: Double, height: Double, age: Int, gender: String): Double {

        val currentDI = 1.0
        val RH = RestingHR.toInt()

        val BPM = heartRate
        val BMI = (weight / (height * height)) * 10000
        val BMV = when {
            BMI > 39.99 -> BMI * 0.05
            BMI > 24.99 -> (BMI - 24.99) / 3
            BMI < 18.51 -> (18.51 - BMI) / 3
            else -> 0.0
        }

        val RI = 1.0

        val TMHRM = RFMHR
        val TMHRF = RFMHR

        val RITMHRM = TMHRM * RI
        val RITMHRF = TMHRF * RI

        val REVPer = if (gender.uppercase() == "MALE") {
            val DIACTTMHRM = RITMHRM * currentDI
            val DIACTHRR = DIACTTMHRM - RH
            val per = ((BPM - RH) / DIACTHRR) * 100
            per * (1 + BMV / 100)
        } else {
            val DIACTTMHRF = RITMHRF * currentDI
            val DIACTHRR = DIACTTMHRF - RH
            val per = ((BPM - RH) / DIACTHRR) * 100
            per * (1 + BMV / 100)
        }

        return REVPer
    }

    fun sumOfArray(array: List<Int>): Int {
        return array.sum()
    }

    fun RLmax(previous: Int, next: Int): Int {
        return RLmax(previous, next)
    }

    fun RLmin(previous: Int, next: Int): Int {
        return when {
            next == 0 -> previous
            previous == 0 -> next
            else -> RLmin(previous, next)
        }
    }

    fun avgOfArray2(array: List<Double>): Double {
        return array.average()
    }

    fun avgOfArray(array: List<Double>): Double {
        val sum = array?.sumOf { if (!it.isNaN() && it.isFinite()) it else 0.0 } ?: 0.0
        return if (array?.size ?: 0 <= 1) 0.0 else sum / (array.size - 1)
    }

    fun calculateCurrentCalories(gender: String, age: Int, weight: Double, heartRate: Double): Double {
        if (heartRate != 0.0) {
            val total = when (gender.toLowerCase()) {
                "male" -> {
                    val maxHrVal = 0.6309 * RFMHR
                    val weightVal = weight * 0.1988
                    val ageVal = age * 0.2017
                    (-55.0969 + maxHrVal + weightVal + ageVal) / 4.184
                }
                else -> {
                    val maxHrVal = 0.4472 * RFMHR
                    val weightVal = weight * 0.1263
                    val ageVal = age * 0.074
                    (-20.4022 + maxHrVal + weightVal + ageVal) / 4.184
                }
            }

            val maxCaloriesHour = total * 36
            val maxCaloriesMin = maxCaloriesHour / 60
            val maxCaloriesSec = maxCaloriesMin / 60

            val rh = RestingHR.toInt()

            val hrRange = heartRate - rh
            val hrRangeMax = RFMHR - rh
            val revPer = hrRange / hrRangeMax

            return maxCaloriesSec * revPer
        }
        return 0.0
    }

    /////////////////////////////////////////////////////////////////////////////////////////////


    private fun RLformatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    //GPS VALU GET
    private fun RLstepGetToGPS() {
        //rlLocationViewModel =RLLocationViewModel(requireActivity().application)
        rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
            speed?.let {
                Log.d(TAG,"Speed: ${it} m/s")
                val speedSetValue=RlGetValueDouble(it.toString())
                speedNumber=speedSetValue
               // speedList.add(speedSetValue)
                if (speedSetValue>0){
                    fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                    maxSpeed=RLmax(maxSpeed,speedNumber.roundToInt())
                    fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                        fragBinding.inlaySpeed.txtAvgNumber.setText( "%.2f".format(averageSpeed).toString())

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
                        fragBinding.inlayCadence.txtProgressTimeNumber.setText(stepCount.toString())
                    }
                }
            }
        })
        rlLocationViewModel.distanceData.observe(viewLifecycleOwner, Observer { distance ->
            distance?.let {
                val totalDistance=it //round(it * 100) / 100
                Log.d(TAG,"Distance: $totalDistance km")
                val totalDistanceValue=RlGetValueDouble(totalDistance.toString())
                distanceNumber=totalDistanceValue
                //distanceList.add(totalDistanceValue)
                if (totalDistanceValue>0){
                    fragBinding.inlayDistance.txtProgressTimeNumber.setText(totalDistance.toString())
                }
            }
        })


        rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
            pace?.let {
                val totalspace=round(it * 100) / 100
                Log.d(TAG, "Pace: $totalspace min/km")
                val totalspaceValue=RlGetValueInt(totalspace.toString())
                paceNumber=totalspaceValue
                //paceList.add(totalspaceValue)
                if (totalspaceValue>0){
                    fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
                    if (!paceList.isNullOrEmpty()){
                        val averagePace=paceList.average().roundToInt()?:0
                        val maxPace=paceList.maxOrNull()!!.toInt()?:0
                        fragBinding.inlayPace.txtAvgNumber.setText(averagePace.toString())
                        fragBinding.inlayPace.txtMaxNumber.setText(maxPace.toString())
                    }

                }
            }
        })

     /*
      rlLocationViewModel.caloriesBurnedData.observe(viewLifecycleOwner, Observer { calories ->
            calories?.let {
                val totalCaloriesBurned=it //     round(it * 100) / 100
                Log.d(TAG,"CaloriesBurned: $totalCaloriesBurned")

                val totalCaloriesBurnedValue=RlGetValueDouble(totalCaloriesBurned.toString())
                activeCaloriesNumber=totalCaloriesBurnedValue
                // distanceList.add(totalDistanceValue)
                if (totalCaloriesBurnedValue>0){
                    //fragBinding.inlayStep.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())
                }

            }
        })

     rlLocationViewModel.averageSpeedData.observe(viewLifecycleOwner, Observer { averageSpeed ->
            averageSpeed?.let {
                val AvgSpeed=round(it * 100) / 100
                Log.d(TAG,"Avg Speed: $AvgSpeed m/s")
                val avgSpeedValue=RlGetValueDouble(AvgSpeed.toString())
                //avgSpeedList.add(avgSpeedValue)
                avgSpeedNumber=avgSpeedValue
                if (avgSpeedValue>0){
                    fragBinding.inlaySpeed.txtAvgNumber.setText(AvgSpeed.toString())
                }

            }
        })

        rlLocationViewModel.maxSpeedData.observe(viewLifecycleOwner, Observer { maxSpeed ->
            maxSpeed?.let {
                val maxsSpeed=round(it * 100) / 100
                Log.d(TAG,"Max Speed: $maxsSpeed m/s")
                val maxsSpeedValue=RlGetValueDouble(maxsSpeed.toString())
                //maxsSpeedList.add(maxsSpeedValue)
                maxsSpeedNumber=maxsSpeedValue
                if (maxsSpeedValue>0){
                    fragBinding.inlaySpeed.txtMaxNumber.setText(maxsSpeed.toString())
                }

            }
        })*/



      /*  rlLocationViewModel.averagePaceData.observe(viewLifecycleOwner, Observer { averagePace ->
            averagePace?.let {
                val avgspace=round(it * 100) / 100
                Log.d(TAG, "Avg Pace: $avgspace min/km")
                val avgspaceValue=RlGetValueInt(avgspace.toString())
                //avgSpaceList.add(avgspaceValue)
                avgSpaceNumber=avgspaceValue
                if (avgspaceValue>0){
                    fragBinding.inlayPace.txtAvgNumber.setText(avgspace.toString())
                }

            }
        })

        rlLocationViewModel.maxPaceData.observe(viewLifecycleOwner, Observer { maxPace ->
            maxPace?.let {
                val maxxpace=round(it * 100) / 100
                Log.d(TAG, "Max Pace: $maxxpace min/km")
                val maxxpaceValue=RlGetValueInt(maxxpace.toString())
               // maxxPaceList.add(maxxpaceValue)
                maxxPaceNumber=maxxpaceValue
                if (maxxpaceValue>0){
                    fragBinding.inlayPace.txtMaxNumber.setText(maxxpace.toString())
                }
            }
        })*/

        rlLocationViewModel.RLstartLocationUpdates()
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

    override fun onStart() {
        super.onStart()
       // timerManager.resume()
        RLcheckAndRequestPermissions()
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

}