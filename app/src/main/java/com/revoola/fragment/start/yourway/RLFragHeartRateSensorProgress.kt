package com.revoola.fragment.start.yourway

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
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.revoola.databinding.RlFragHeartrateSensorProgressBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.services.RLBLEService
import com.revoola.services.RLLocationViewModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
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

    private var arrBurntCalories:MutableList<Double> = mutableListOf()
    private var arrCadence:MutableList<Double> = mutableListOf()
    private var arrDistance:MutableList<Double> = mutableListOf()
    private var arrElevation:MutableList<Double> = mutableListOf()
    private var arrHRRecordedSecond:MutableList<Int> = mutableListOf()
    private var arrHr:MutableList<Int> = mutableListOf()
    private var arrRevPercentage:MutableList<Double> = mutableListOf()
    private var arrRevSecond:MutableList<Double> = mutableListOf()
    private var arrSpeed:MutableList<Double> = mutableListOf()
    private var arrCumDistance:MutableList<Double> = mutableListOf()
    private var arrCumElevation:MutableList<Double> = mutableListOf()
    private var arrCumSpeed:MutableList<Double> = mutableListOf()
    private var arrConnection:MutableList<Boolean> = mutableListOf()

    private var arrAvgCadence:MutableList<Double> = mutableListOf()
    private var arrAvgHr:MutableList<Int> = mutableListOf()
    private var arrAvgRevPercentage:MutableList<Int> = mutableListOf()
    private var arrMaxCadence:MutableList<Int> = mutableListOf()
    private var arrMaxHr:MutableList<Int> = mutableListOf()
    private var arrMaxRevPercentage:MutableList<Double> = mutableListOf()

    private var speedList:MutableList<Double> = mutableListOf()
    private var paceList:MutableList<Int> = mutableListOf()


    private var arrSpeedForOneKm:MutableList<Double> = mutableListOf()
    private var arrSpeedForOneMile:MutableList<Double> = mutableListOf()

    private var avgSpeedForOneKm:Double=0.0
    private var avgSpeedForOneMile:Double=0.0

    private var maxSpeedForOneKm:Double=0.0
    private var maxSpeedForOneMile:Double=0.0

    private var heartRateNumber:Int=0
    private var heartRate:Int=110
    private var stepsNumber:Int=0
    private var distance:Double=0.0
    private var elevationMeter:Double=0.0
    private var speedNumber:Double=0.0
    private var paceNumber:Int=0
    private var totalTime:String ="0"
    private var avgRevPercentage=0.0
    private var revPercentage=0.0
    private var totalElevation=0.0
    var lastGeoElevation: Int = 0
    var totalGeoElevation: Int = 0

    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit=""
    var burntCalories =0.0
    var totalRev  =0.0
    var lastrevPercentage=0.0
    var maxRevPercentage =0.0
    var minRevPercentage =0.0
    var maxSpeed =0
    var maxCadence =0
    var maxBurntCalories =0
    var maxHeartrate =0
    var minHeartrate =0
    var CumDistance =0.0
    var CumSpeed =0.0
    var cadenceData =0.0
    private var maxPace =0

    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0

    private var startTimeKm: Long = 0
    private var startTimeMile: Long = 0
    private var lastLocation: Location? = null
    private var totalDistance = 0.0

    val gpxStringBuilder = StringBuilder()

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
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString("YourWayType").toString().trim()
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        //fragBinding.txtMaintitle.setText(yourWayType)
        rlLocationViewModel = RLLocationViewModel(requireActivity().application)

        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                appUnit=userData.appUnit
            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
            RLstepGetToGPS()
        }

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
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
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
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
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
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
           }

            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            bundle.putString("totalTime",(totalTime?:"0"))
            bundle.putString("gpxStringBuilder",gpxStringBuilder.toString()?:"")


            bundle.putString("SENSOR",RLConstants.HEARTSENSOR)
            bundle.putDouble("avgRevPercentage",noNanValueDouble(avgRevPercentage?:0.00))
            bundle.putDouble("burntCalories",noNanValueDouble(burntCalories?:0.00))
            bundle.putDouble("distance",noNanValueDouble(distance?:0.00))
            bundle.putDouble("maxRevPercentage",noNanValueDouble(maxRevPercentage?:0.00))
            bundle.putDouble("minRevPercentage",noNanValueDouble(minRevPercentage?:0.00))
            bundle.putDouble("revPercentage",noNanValueDouble(revPercentage?:0.00))
            bundle.putDouble("totalElevation",noNanValueDouble(totalElevation?:0.00))
            bundle.putDouble("totalRev",noNanValueDouble(totalRev?:0.00))
            bundle.putInt("totalSteps",stepsNumber?:0)
            bundle.putInt("maxSpeed",maxSpeed?:0)
            bundle.putInt("maxHeartRate",maxHeartrate?:0)
            bundle.putInt("maxCadence",maxCadence?:0)
            bundle.putInt("maxBurntCalories",maxBurntCalories?:0)
            bundle.putInt("minHeartrate",minHeartrate?:0)

            bundle.putDouble("maxSpeedForOneKm",noNanValueDouble(maxSpeedForOneKm?:0.00))
            bundle.putDouble("maxSpeedForOneMile",noNanValueDouble(maxSpeedForOneMile?:0.00))
            bundle.putDouble("avgSpeedForOneKm",noNanValueDouble(avgSpeedForOneKm?:0.00))
            bundle.putDouble("avgSpeedForOneMile",noNanValueDouble(avgSpeedForOneMile?:0.00))

            bundle.putBooleanArray("arrConnection",arrConnection.toBooleanArray())

            bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrElevation.toString(),arrElevation.toDoubleArray())
            bundle.putIntegerArrayList(RLYourWayArrayType.arrHRRecordedSecond.toString(),ArrayList(arrHRRecordedSecond))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrHr.toString(),ArrayList(arrHr))
            bundle.putDoubleArray(RLYourWayArrayType.arrRevPercentage.toString(),arrRevPercentage.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrRevSecond.toString(),arrRevSecond.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumElevation.toString(),arrCumElevation.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())

            bundle.putDoubleArray(RLYourWayArrayType.arrAvgCadence.toString(),arrAvgCadence.toDoubleArray())
            bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgHr.toString(),ArrayList(arrAvgHr))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgRevPercentage.toString(),ArrayList(arrAvgRevPercentage))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrMaxCadence.toString(),ArrayList(arrMaxCadence))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrMaxHr.toString(),ArrayList(arrMaxHr))
            bundle.putDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString(),arrMaxRevPercentage.toDoubleArray())

            if (arrSpeedForOneKm.isNullOrEmpty()){
                arrSpeedForOneKm= mutableListOf(0.00)
            }
            if (arrSpeedForOneMile.isNullOrEmpty()){
                arrSpeedForOneMile= mutableListOf(0.00)
            }
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneKm.toString(),arrSpeedForOneKm.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneMile.toString(),arrSpeedForOneMile.toDoubleArray())

            bundle.putParcelableArrayList(RLYourWayArrayType.arrDataLocation.toString(), ArrayList(arrDataLocation))
            bundle.putParcelableArrayList(RLYourWayArrayType.arrLocationDetails.toString(),ArrayList(arrLocationDetails))


            (context as RLMainActivityRL).RLloadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

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

            val intent = Intent(requireContext(), RLBLEService::class.java)
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
            RLTools.RlLogDPrint(TAG,"onServiceConnected")
            val binder = service as RLBLEService.RLLocalBinder
            rlbleService = binder.getService()
            // Check if devices are not connected then scan
            isServiceBound = true
            val lastConnectDeviceAddress = com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.last_device_connect, "")
            RLhandleDeviceFound(lastConnectDeviceAddress)

        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            RLTools.RlLogDPrint(TAG,"onServiceDisconnected")
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
                        arrHRRecordedSecond.add(totalTime.toInt()?:0)
                        fragBinding.txtEffortNumber.setText(data)
                        fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(data)
                        maxHeartrate=RLmax(maxHeartrate,heartRateNumber)
                        minHeartrate=RLmin(minHeartrate,heartRateNumber)
                        fragBinding.inlayHeartrate.txtMaxNumber.setText(maxHeartrate.toString())
                        if (!arrHr.isNullOrEmpty()){
                            val avgHeartRate=arrHr.average().roundToInt()?:0
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
    private fun RLtimerMain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLformatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){
        val currentCalories=calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble())
        if (heartRateNumber>0){
            heartRate=heartRateNumber
        }
        val REVPer=calculateREVPer(heartRate,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender) //only REV
        arrRevPercentage.add(noNanValueDouble(REVPer))
        avgRevPercentage = avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        lastrevPercentage=REVPer
        maxRevPercentage=RLmax(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLmin(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLmax(maxBurntCalories,burntCalories.toInt())

        revPercentage=REVPer


        arrBurntCalories.add( noNanValueDouble(currentCalories))
        arrCadence.add(noNanValueDouble(cadenceData))
        arrDistance.add( noNanValueDouble(distance))
        arrHr.add(heartRateNumber)
        arrSpeed.add(noNanValueDouble(speedNumber))

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distance
        arrCumDistance.add(noNanValueDouble(CumDistance))
        arrCumSpeed.add(noNanValueDouble(CumSpeed))
        distance=CumDistance

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
            arrElevation.add(noNanValueDouble(lastGeoElevation.toDouble()))
            arrCumElevation.add(noNanValueDouble((totalGeoElevation/10).toDouble()))

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        val totalCaloriesBurnedValue=RlGetValueDouble(burntCalories.toString()).roundToInt()
        fragBinding.inlayCalories.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())

        fragBinding.inlayClimbed.txtProgressTimeNumber.setText(getClimbData(totalElevation))

        val elevationpoint= RLElevationPoint(noNanValueDouble(elevationMeter),noNanValueDouble(latitude), noNanValueDouble(longitude))
        arrDataLocation.add((elevationpoint))
        val locationDetails= RLLocationDetails(noNanValueDouble(speedNumber),noNanValueDouble(speedNumber),noNanValueDouble(latitude),0.0, noNanValueDouble(longitude),noNanValueDouble(elevationMeter))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(noNanValueDouble(arrCadence.average()))
        arrAvgHr.add(arrHr.average().roundToInt())
        arrAvgRevPercentage.add(avgRevPercentage.roundToInt())
        arrMaxCadence.add(maxCadence)
        arrMaxHr.add(maxHeartrate)
        arrMaxRevPercentage.add(noNanValueDouble(maxRevPercentage))

        if (!arrSpeedForOneKm.isNullOrEmpty()){

        }
        if (!arrSpeedForOneMile.isNullOrEmpty()){

        }

        val isodate=RLgetCurrentDateTimeIsoFormatted()
        gpxStringBuilder.append(
            """
                <trkpt lat="$latitude" lon="$longitude">
                    <ele>$totalElevation</ele>
                    <time>$isodate}</time>
                </trkpt>
                
                """.trimIndent())

        arrConnection.add(true)
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

    //////////////////////// calculate All Value Start //////////////////////////

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

    private fun RLmax(previous: Int, next: Int): Int {
        return when {
            previous > next -> previous
            else ->next
        }
    }

    private fun RLmin(previous: Int, next: Int): Int {
        return when {
            next == 0 -> previous
            previous == 0 -> next
            else -> next
        }
    }

    private fun avgOfArray(array: List<Double>): Double {
        val sum = array?.sumOf { if (!it.isNaN() && it.isFinite()) it else 0.0 } ?: 0.0
        return if (array?.size ?: 0 <= 1) 0.0 else sum / (array.size - 1)
    }

    private fun calculateCurrentCalories(gender: String, age: Int, weight: Double, heartRate: Double): Double {
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

    ///////////////////////////// calculate All Value End ////////////////////////

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
                RLTools.RlLogDPrint(TAG,"Speed: ${it} m/s")
                val speedSetValue=RlGetValueDouble(it.toString())
                speedNumber=speedSetValue
                speedList.add("%.2f".format(it).toDouble())
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

        rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
            elevation?.let {
                RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                val elevation=RlGetValueDouble(it.toString())
                elevationMeter=elevation
            }
        })

        rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
            location?.let {
                RLtrackTimePerKilometer(it)
                RLTools.RlLogDPrint(TAG,"location: ${it}")
                latitude=it.latitude
                longitude=it.longitude
            }
        })

        rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
            cadence?.let {
                RLTools.RlLogDPrint(TAG,"cadenceData: ${it} ")
                val _cadenceData=RlGetValueDouble(it.toString())
                cadenceData=_cadenceData
                maxCadence=RLmax(maxCadence,cadenceData.roundToInt())
            }
        })

        rlLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
            stepCount?.let {
                RLTools.RlLogDPrint(TAG,"Steps: $stepCount")
                val stepSetValue=RlGetValueInt(stepCount.toString())
                stepsNumber=stepSetValue
                if (stepSetValue>0){
                    if (yourWayType.toLowerCase().equals("run")||yourWayType.toLowerCase().equals("walk")){
                        fragBinding.inlayCadence.txtProgressTimeNumber.setText(stepSetValue.toString())
                    }
                }
            }
        })

        rlLocationViewModel.distanceData.observe(viewLifecycleOwner, Observer { distance ->
            distance?.let {
                val totalDistance=it //round(it * 100) / 100
                RLTools.RlLogDPrint(TAG,"Distance: $totalDistance km")
                val totalDistanceValue=RlGetValueDouble(totalDistance.toString())
                this.distance =totalDistanceValue
                //distanceList.add(totalDistanceValue)
                if (totalDistanceValue>0){
                    fragBinding.inlayDistance.txtProgressTimeNumber.setText("%.2f".format(it))
                }
            }
        })

        rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
            pace?.let {
                val totalspace=round(it * 100) / 100
                RLTools.RlLogDPrint(TAG, "Pace: $totalspace min/km")
                val totalspaceValue=RlGetValueInt(totalspace.toString())
                paceNumber=totalspaceValue
                paceList.add(totalspaceValue)
                if (totalspaceValue>0){
                    fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
                    if (!paceList.isNullOrEmpty()){
                        val averagePace=paceList.average().roundToInt()?:0
                        maxPace=RLmax(maxPace,paceNumber.toInt())
                        //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                        fragBinding.inlayPace.txtAvgNumber.setText(averagePace.toString())
                        fragBinding.inlayPace.txtMaxNumber.setText(maxPace.toString())
                    }

                }
            }
        })

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
        } else if(value.toDouble() < 0) {
            return 0.0
        }else{
            return value.toDouble()
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
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
        }
    }

}