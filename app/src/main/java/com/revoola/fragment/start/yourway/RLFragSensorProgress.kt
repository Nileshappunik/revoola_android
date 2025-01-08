package com.revoola.fragment.start.yourway

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import com.revoola.RLBaseFragment
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragSensorProgressBinding
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLAssumedRev
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.services.RLBLEService
import com.revoola.services.RLLocationViewModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.services.RLBLEManagerSpeed
import java.lang.Math.round
import kotlin.math.roundToInt

class RLFragSensorProgress : RLBaseFragment(){
    val TAG: String = RLFragSensorProgress::class.java.simpleName
    lateinit var fragBinding: RlFragSensorProgressBinding
    private lateinit var rlLocationViewModel: RLLocationViewModel
    private val timerManager = RLTimerManager()

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
    private var arrConnection:MutableList<Boolean> = mutableListOf()

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

    //private var activeCaloriesNumber:Double=0.0
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
    private var totalRev:Double=0.0
    //UserBasic Data Get Value
    private var appUnit:String=""
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191


    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0

    private var classType="workout"
    var assumedCalories= RLAssumedCalories()
    var assumedRev= RLAssumedRev()

    val gpxStringBuilder = StringBuilder()

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSensorProgress()
        fragment.arguments = bundle
        return fragment
    }
    private val bleManager by lazy { RLBLEManagerSpeed(requireContext()) }
    private val binding by lazy {
        RlFragSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_sensor_progress, container) as RlFragSensorProgressBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragSensorProgress" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup(){
        RLstartCountdown()

        yourWayType = requireArguments().getString("YourWayType").toString().trim()
        isSpeedSensor = requireArguments().getBoolean("isspeedsensor",false)

        fragBinding.relaytiveMain.setBackgroundResource(RLTools.RLgetImage1(yourWayType.toLowerCase()))
        rlLocationViewModel = RLLocationViewModel(requireActivity().application)

        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                appUnit=userData.appUnit
                wsHeight=userData.height?:"167"
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        //Firebase AssumedCalories Data Fetch
        RLfetchAssumedCalories{assumedCaloriesvalue->
            if (assumedCaloriesvalue != null) {
                // Use the data as needed
                assumedCalories=assumedCaloriesvalue
            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching AssumedCalories data")
            }
        }
        //Firebase AssumedRev Data Fetch
        RLfetchAssumedRev{assumedRevValue->
            if (assumedRevValue != null) {
                // Use the data as needed
                assumedRev=assumedRevValue
            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching AssumedRev data")
            }
        }

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
                bleManager.rlpausegetData()
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
               bleManager.rlresumegetData()
               if (issGpsConnect && (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride"))){
                   rlLocationViewModel.RLstartLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            bundle.putString("totalTime",totalTime)
            bundle.putString("gpxStringBuilder",gpxStringBuilder.toString()?:"")
            if (yourWayType.equals("Ride") && isSpeedSensorConnect){
                bundle.putString("SENSOR", RLConstants.SPEEDSENSOR)
            }else{
                bundle.putString("SENSOR", RLConstants.NOSENSOR)
            }
            bundle.putDouble("burntCalories",RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00))
            bundle.putDouble("totalElevation",RLYourWayCalvulation.noNanValueDouble(totalElevation?:0.00))
            bundle.putDouble("totalRev",RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00))

            bundle.putDouble("maxSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00))
            bundle.putDouble("maxSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00))
            bundle.putDouble("avgSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00))
            bundle.putDouble("avgSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00))

            bundle.putBooleanArray("arrConnection",arrConnection.toBooleanArray())

            bundle.putInt("totalSteps",stepsNumber?:0)
            bundle.putDouble("distance",RLYourWayCalvulation.noNanValueDouble(distance?:0.00))
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
                bleManager.lrstopgetData()
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    rlLocationViewModel.RLstopLocationUpdates()
                }
            }catch (e:Exception){
               RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
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
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLYourWayCalvulation.RLformatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){
        arrCadence.add(RLYourWayCalvulation.noNanValueDouble(cadenceData?:0.00))
        arrDistance.add(RLYourWayCalvulation.noNanValueDouble(distanceNumber?:0.00))
        arrSpeed.add(RLYourWayCalvulation.noNanValueDouble(speedNumber?:0.00))
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
            arrElevation.add(RLYourWayCalvulation.noNanValueDouble(lastGeoElevation.toDouble()?:0.00))
            arrCumElevation.add(RLYourWayCalvulation.noNanValueDouble((totalGeoElevation/10).toDouble()?:0.00))

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distanceNumber
        arrCumDistance.add(RLYourWayCalvulation.noNanValueDouble(CumDistance?:0.00))
        arrCumSpeed.add(RLYourWayCalvulation.noNanValueDouble(CumSpeed?:0.00))
        distance=CumDistance
        if (yourWayType.equals("Ride")&& !isSpeedSensor){
            fragBinding.inlayDistance.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))
        }else{
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))
        }

        val elevationpoint= RLElevationPoint(RLYourWayCalvulation.noNanValueDouble(elevationMeter?:0.00),RLYourWayCalvulation.noNanValueDouble(latitude?:0.00), RLYourWayCalvulation.noNanValueDouble(longitude?:0.00))
        arrDataLocation.add(elevationpoint)
        val locationDetails= RLLocationDetails(RLYourWayCalvulation.noNanValueDouble(speedNumber?:0.00),RLYourWayCalvulation.noNanValueDouble(speedNumber?:0.00),RLYourWayCalvulation.noNanValueDouble(latitude?:0.00),0.0, RLYourWayCalvulation.noNanValueDouble(longitude?:0.00),RLYourWayCalvulation.noNanValueDouble(elevationMeter?:0.00))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(RLYourWayCalvulation.noNanValueDouble(arrCadence.average()?:0.00))
        arrMaxCadence.add(maxCadence)

        val distancevalue= (distance?: 0.0) * 1000
        val  totalElevation =totalElevation?: 0.0

        //Calculation AssumedRev
        val assumedRevValue = RLGenerateAssumedRev(classType,assumedRev,distancevalue,totalElevation,totalTime.toDouble())
        totalRev=assumedRevValue
        //Calculation AssumedCalories
        val assumedCalories=RLCalculateAssumedCalories(RFMHR.toDouble(),wsHeight.toDouble(),wsAge.toDouble().roundToInt(),totalTime.toDouble(),assumedRevValue,assumedCalories,gender)

        burntCalories=assumedCalories
        maxBurntCalories=RLYourWayCalvulation.RLmax(maxBurntCalories,burntCalories.roundToInt())
        arrBurntCalories.add(RLYourWayCalvulation.noNanValueDouble(assumedCalories?:0.00))

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

    //GPS VALU GET
    private fun RLstepGetToGPS() {
        try {
            issGpsConnect=true
            if (isSpeedSensor){
                rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
                    elevation?.let {
                        RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                        val elevation=RLYourWayCalvulation.RlGetValueDouble(it.toString())
                        elevationMeter=elevation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                        RLTools.RlLogDPrint(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })
            }else{
                rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
                    speed?.let {
                        RLTools.RlLogDPrint(TAG,"Speed: ${it} m/s")
                        val speedSetValue=RLYourWayCalvulation.RlGetValueDouble(it.toString())
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
                                maxSpeed=RLYourWayCalvulation.RLmax(maxSpeed,speedNumber.roundToInt())
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
                        RLTools.RlLogDPrint(TAG,"Steps: $stepCount")
                        val stepSetValue=RLYourWayCalvulation.RlGetValueInt(stepCount.toString())
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
                        RLTools.RlLogDPrint(TAG,"Distance: $totalDistance km")

                        val totalDistanceValue=RLYourWayCalvulation.RlGetValueDouble(totalDistance.toString())
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
                /*rlLocationViewModel.caloriesBurnedData.observe(viewLifecycleOwner, Observer { calories ->
                    calories?.let {
                        val totalCaloriesBurned=it
                        RLTools.RlLogDPrint(TAG,"CaloriesBurned: $totalCaloriesBurned")

                        val totalCaloriesBurnedValue=RLYourWayCalvulation.RlGetValueDouble(totalCaloriesBurned.toString())
                        activeCaloriesNumber=totalCaloriesBurnedValue
                        if (totalCaloriesBurnedValue>0){
                            //fragBinding.inlayStep.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())
                        }

                    }
                })*/
                rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
                    pace?.let {
                        val totalspace=round(it * 100)  / 100
                        RLTools.RlLogDPrint(TAG, "Pace: $totalspace min/km")
                        val totalspaceValue=RLYourWayCalvulation.RlGetValueInt(totalspace.toString())
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
                                maxPace=RLYourWayCalvulation.RLmax(maxPace,paceNumber.toInt())
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
                        RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                        val elevation=RLYourWayCalvulation.RlGetValueDouble(it.toString())
                        elevationMeter=elevation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                        RLTools.RlLogDPrint(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })

                rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
                    cadence?.let {
                        RLTools.RlLogDPrint(TAG,"cadenceData: ${it} ")
                        val _cadenceData=RLYourWayCalvulation.RlGetValueDouble(it.toString())
                        cadenceData=_cadenceData
                        maxCadence=RLYourWayCalvulation.RLmax(maxCadence,cadenceData.toInt())
                    }
                })
            }
            rlLocationViewModel.RLstartLocationUpdates()
        }
        catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"EXCEPTION GetGPS:- ${e.message}")
        }
    }

    override fun onStart() {
        super.onStart()
        if (isSpeedSensor){
            if (bleManager.checkAndRequestPermissions(requireActivity())) {
                bleManager.setupBluetooth {
                    bleManager.startBLEService()
                }
            }
            bleManager.setCallback(object : RLBLEManagerSpeed.BLECallback {
                override fun onHeartRateDataReceived(SPEED:String, AvgSPEED:String, DISTANCE:String, CADENCE:String, CALORIES:String) {
                    var speedSetValue=RLYourWayCalvulation.RlGetValueInt(SPEED.toString())?:0
                    var avgspeedSetValue=RLYourWayCalvulation.RlGetValueInt(AvgSPEED.toString())?:0
                    var distanceSetValue=RLYourWayCalvulation.RlGetValueInt(DISTANCE.toString())?:0
                    var cadenceSetValue=RLYourWayCalvulation.RlGetValueInt(CADENCE.toString())?:0

                    distanceNumber=RLYourWayCalvulation.RlGetValueDouble(DISTANCE.toString())?:0.0
                    climbedNumber=RLYourWayCalvulation.RlGetValueInt(CADENCE.toString())?:0
                    speedNumber=RLYourWayCalvulation.RlGetValueDouble(SPEED.toString())?:0.0
                    //activeCaloriesNumber=RLYourWayCalvulation.RlGetValueDouble(CALORIES.toString())?:0.0
                    cadenceData=RLYourWayCalvulation.RlGetValueDouble(CADENCE.toString())?:0.0
                    maxCadence=RLYourWayCalvulation.RLmax(maxCadence,cadenceData.toInt())

                    val floatSpeed:Float= speedSetValue.toFloat()?:0f
                    paceNumber=RLYourWayCalvulation.calculatePace(floatSpeed)

                    speedList.add(speedSetValue.toDouble())
                    fragBinding.inlaySpeed.txtProgressTimeNumber.setText(speedSetValue.toString())
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                        maxSpeed=RLYourWayCalvulation.RLmax(maxSpeed,speedNumber.roundToInt())
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
                            maxPace=RLYourWayCalvulation.RLmax(maxPace,paceNumber.toInt())
                            fragBinding.inlayPace.txtAvgNumber.setText( averagePace.toString())
                            fragBinding.inlayPace.txtMaxNumber.setText( maxPace.toString())
                        }
                    }
                }

                override fun onConnectionStateChange(is_connected: Boolean) {
                    if (is_connected){
                        // commonToast("BLE DEVICE CONNECT")
                        RLTools.RlLogDPrint(TAG,"BLE DEVICE CONNECT")
                    }else{
                        //commonToast("NO ANY BLE DEVICE CONNECT")
                        RLTools.RlLogDPrint(TAG,"NO ANY BLE DEVICE CONNECT")
                        if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                            RLstepGetToGPS()
                        }
                    }
                }
                @SuppressLint("MissingPermission")
                override fun onDeviceConnected(device: BluetoothDevice) {
                    Log.d("BLE", "Connected to device: ${device.name}")
                }

                override fun onDeviceDisconnected() {
                    Log.d("BLE", "Device disconnected")
                }

                override fun onError(errorMessage: String) {
                    Log.e("BLE", "Error: $errorMessage")
                }
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            bleManager.cleanup()
        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
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
                maxSpeedForOneKm=RLYourWayCalvulation.RLmax(maxSpeedForOneKm.roundToInt(),elapsedTimeKm.roundToInt()).toDouble()
                arrSpeedForOneKm.add(RLYourWayCalvulation.RlGetValueDouble(elapsedTimeKm.toString()))
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
                maxSpeedForOneMile=RLYourWayCalvulation.RLmax(maxSpeedForOneMile.roundToInt(),elapsedTimeMile.roundToInt()).toDouble()
                println("Time taken for last mile: $elapsedTimeMile seconds")
                arrSpeedForOneMile.add(RLYourWayCalvulation.RlGetValueDouble(elapsedTimeMile.toString()))
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

}