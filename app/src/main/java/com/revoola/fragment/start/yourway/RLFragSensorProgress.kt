package com.revoola.fragment.start.yourway

import android.animation.ObjectAnimator
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragSensorProgressBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.revoola.ble.BLERepository
import com.revoola.ble.BLEViewModel
import com.revoola.ble.RLBLEViewModelFactory
import com.revoola.ble.RLExtraValueKey
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLAssumedRev
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.services.RLLocationViewModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.launch
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
    //private var distance:Double=0.0
    private var burntCalories:Double=0.0
    private var totalRev:Double=0.0
    //UserBasic Data Get Value
    private var appUnit:String="Metric"
    private var isImperial:Boolean = false
    private var wsHeight="167"
    private var wsWeight="70"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var emailId=""
    private var isBasicDataAdded=true
    private var visibilityflagforthatsession:Int = 0

    private var  zoneDataMapSummery: MutableMap<String, RLZoneDataSummery> = mutableMapOf()
    private var  zoneDataMapDetails: MutableMap<String, RLZoneDataDetails> = mutableMapOf()

    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0

    private var classType="workout"
    private var assumedCalories= RLAssumedCalories()
    private var assumedRev= RLAssumedRev()

    private val gpxStringBuilder = StringBuilder()

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSensorProgress()
        fragment.arguments = bundle
        return fragment
    }

    private val bleRepository by lazy {
        BLERepository(requireContext())
    }

    private val viewModel: BLEViewModel by activityViewModels {
        RLBLEViewModelFactory(bleRepository)
    }

    private val binding by lazy {
        RlFragSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_sensor_progress, container) as RlFragSensorProgressBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString(RLExtraValueKey.yourWayType).toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup(){
        initializeDefaultZonesSummery()
        RLstartCountdown()
        isSpeedSensor = requireArguments().getBoolean(RLExtraValueKey.isSpeedSensor,false)
        fragBinding.relaytiveMain.setBackgroundResource(RLTools.rl_getImage1(yourWayType.toLowerCase()))
        rlLocationViewModel = RLLocationViewModel(requireActivity().application)

        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                appUnit=userData.appUnit
                wsHeight=userData.height?:"167"
                wsWeight=userData.weightkg?:"70"
                wsAge= RLTools.rl_calculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                emailId=userData.emailId
                isBasicDataAdded=userData.isBasicDataAdded
                visibilityflagforthatsession=userData.visibilityflagforthatsession
                isImperial= RLTools.rl_getIsImperial(userData.appUnit)
                RLwayTypeDesignSet(yourWayType)
            } else {
                RLwayTypeDesignSet(yourWayType)
               RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        //Firebase AssumedCalories Data Fetch
        rl_fetchAssumedCalories{ assumedCaloriesvalue->
            if (assumedCaloriesvalue != null) {
                // Use the data as needed
                assumedCalories=assumedCaloriesvalue
            } else {
               RLTools.rl_logEPrint(TAG, "Error fetching AssumedCalories data")
            }
        }
        //Firebase AssumedRev Data Fetch
        rl_fetchAssumedRev{ assumedRevValue->
            if (assumedRevValue != null) {
                // Use the data as needed
                assumedRev=assumedRevValue
            } else {
               RLTools.rl_logEPrint(TAG, "Error fetching AssumedRev data")
            }
        }

        fragBinding.inlayTop.ivTitle.setText(yourWayType)
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setTextColor(resources.getColor(R.color.AppWhiteColor))
        //RLwayTypeDesignSet(yourWayType)

        fragBinding.layPause.setOnClickListener {
            try {
                timerManager.rl_pause()
                fragBinding.layPause.visibility=View.GONE
                fragBinding.layResumestop.visibility=View.VISIBLE
                viewModel.pauseNotifications()
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    rlLocationViewModel.rl_stopLocationUpdates()
                }

            }catch (e:Exception){
               RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
            }
        }
        fragBinding.layResume.setOnClickListener {
           try{
                timerManager.rl_resume()
                fragBinding.layPause.visibility=View.VISIBLE
                fragBinding.layResumestop.visibility=View.GONE
               viewModel.resumeNotifications()
               if (issGpsConnect && (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride"))){
                   rlLocationViewModel.rl_startLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
            val bundle: Bundle = Bundle()
            //val cardData = RLSessionDataTransferModel()
            val cardData = RLSessionDataTransferModelNew()
            cardData.yourWayType=yourWayType
            cardData.totalTime=totalTime
            cardData.gpxStringBuilder=gpxStringBuilder.toString()
            cardData.gpxTServerString=gpxStringBuilder.toString()
            cardData.gpxTServerNString=gpxStringBuilder.toString()
            if (yourWayType.toLowerCase().equals("ride") && isSpeedSensorConnect){
                cardData.SENSOR = RLConstants.SPEED_SENSOR
            }else{
                cardData.SENSOR = RLConstants.NO_SENSOR
            }
            cardData.burntCalories=RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00)
            cardData.totalElevation=RLYourWayCalvulation.noNanValueDouble(totalElevation?:0.00)
            cardData.totalRev=RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00)

            cardData.maxSpeedForOneKm=RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00)
            cardData.maxSpeedForOneMile=RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00)
            cardData.avgSpeedForOneKm=RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00)
            cardData.avgSpeedForOneMile=RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00)

            cardData.arrConnection=arrConnection
            cardData.avgSpeed = arrSpeed.average()?:0.0

            cardData.totalSteps = stepsNumber?:0
            cardData.distance = RLYourWayCalvulation.noNanValueDouble(distanceNumber?:0.00)
            cardData.maxSpeed = maxSpeed?:0
            cardData.maxCadence = maxCadence?:0
            cardData.maxBurntCalories = maxBurntCalories?:0
            cardData.hrm = 0

            cardData.arrBurntCalories=arrBurntCalories
            cardData.arrCadence=arrCadence
            cardData.arrDistance=arrDistance
            cardData.arrElevation=arrElevation
            cardData.arrSpeed=arrSpeed
            cardData.arrCumDistance=arrCumDistance
            cardData.arrCumSpeed=arrCumSpeed
            cardData.arrCumElevation=arrCumElevation

            if (arrSpeedForOneKm.isNullOrEmpty()){
                arrSpeedForOneKm= mutableListOf(0.00)
            }
            if (arrSpeedForOneMile.isNullOrEmpty()){
                arrSpeedForOneMile= mutableListOf(0.00)
            }
            cardData.speedForOneKm = arrSpeedForOneKm
            cardData.speedForOneMile = arrSpeedForOneMile

            cardData.arrAvgCadence = arrAvgCadence
            cardData.arrMaxCadence = arrMaxCadence

            cardData.arrDataLocation = arrDataLocation
            cardData.arrLocationDetails = arrLocationDetails

            cardData.zoneDataSummery = getZoneDataMapSummery()
            cardData.zoneDataDetail = getZoneDataMapDetail()

            cardData.wsWeight = wsWeight
            cardData.wsHeight = wsHeight
            cardData.wsAge = wsAge
            cardData.gender = gender
            cardData.RFMHR = RFMHR
            cardData.RestingHR = RestingHR
            cardData.appUnit = appUnit
            cardData.emailId = emailId
            cardData.isBasicDataAdded = isBasicDataAdded
            cardData.visibilityflagforthatsession = visibilityflagforthatsession

            //bundle.putSerializable("cardData",cardData)
            bundle.putParcelable("cardData",cardData)
            bundle.putBoolean("isEditFeedItem",false)
            try {
                timerManager.rl_stop()
                viewModel.stopNotifications()
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                    rlLocationViewModel.rl_stopLocationUpdates()
                }
            }catch (e:Exception){
               RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
            }
            (context as RLMainActivityRL).rl_loadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

        }

        if (isSpeedSensor){
            //Speed Sensor
            val  sensorDeviceAddress = requireArguments().getString(RLExtraValueKey.sensorDeviceAddress).toString()
            viewModel.connectToDevice(sensorDeviceAddress)
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // Add the Bluetooth state collector first
                    launch {
                        viewModel.sensorData.collect { dataGet ->
                            val SPEED = dataGet?.speed?:"0"
                            val DISTANCE = dataGet?.distance?:"0"
                            val CADENCE = dataGet?.cadence?:"0"

                            val speedSetValue=RLYourWayCalvulation.rl_getValueInt(SPEED.toString())?:0
                            val distanceSetValue=RLYourWayCalvulation.rl_getValueInt(DISTANCE.toString())?:0
                            val cadenceSetValue=RLYourWayCalvulation.rl_getValueInt(CADENCE.toString())?:0

                            distanceNumber=RLYourWayCalvulation.rl_getValueDouble(DISTANCE.toString())?:0.0
                            climbedNumber=RLYourWayCalvulation.rl_getValueInt(CADENCE.toString())?:0
                            speedNumber=RLYourWayCalvulation.rl_getValueDouble(SPEED.toString())?:0.0
                            cadenceData=RLYourWayCalvulation.rl_getValueDouble(CADENCE.toString())?:0.0
                            maxCadence=RLYourWayCalvulation.rl_max(maxCadence,cadenceData.toInt())

                            val floatSpeed:Float= speedSetValue.toFloat()?:0f
                            paceNumber=RLYourWayCalvulation.calculatePace(floatSpeed)

                            speedList.add(speedSetValue.toDouble())
                          //  fragBinding.inlaySpeed.txtProgressTimeNumber.setText(speedSetValue.toString())
                            fragBinding.inlaySpeed.txtProgressTimeNumber.setText(RLCalculateSpeed(SPEED))
                            if (!speedList.isNullOrEmpty()){
                                val averageSpeed=speedList.average().toDouble()?:0.0
                                maxSpeed=RLYourWayCalvulation.rl_max(maxSpeed,speedNumber.roundToInt())
                               // fragBinding.inlaySpeed.txtAvgNumber.setText(averageSpeed.toString())
                               // fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                                fragBinding.inlaySpeed.txtAvgNumber.setText(RLCalculateSpeed(averageSpeed))
                                fragBinding.inlaySpeed.txtMaxNumber.setText(RLCalculateSpeed(maxSpeed))
                            }
                            if (cadenceSetValue>0){
                                fragBinding.inlayStep.txtProgressTimeNumber.setText(CADENCE.toString())
                            }
                            if (distanceSetValue>0){
                                //fragBinding.inlayDistance.txtProgressTimeNumber.setText(DISTANCE.toString())
                                fragBinding.inlayDistance.txtProgressTimeNumber.setText(RLCalculateDistance(DISTANCE))
                            }
                            if (paceNumber>0){
                                fragBinding.inlayPace.txtProgressTimeNumber.setText(paceNumber.toString())
                                paceList.add(paceNumber)
                                if (!paceList.isNullOrEmpty()){
                                    val averagePace=paceList.average().roundToInt()?:0
                                    //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                                    maxPace=RLYourWayCalvulation.rl_max(maxPace,paceNumber.toInt())
                                    fragBinding.inlayPace.txtAvgNumber.setText( averagePace.toString())
                                    fragBinding.inlayPace.txtMaxNumber.setText( maxPace.toString())
                                }
                            }
                        }
                    }

                }
            }
        }
        else{
            //No Sensor
            if (yourWayType.toLowerCase().equals("run")||yourWayType.toLowerCase().equals("walk")||yourWayType.toLowerCase().equals("ride")){
                RLstepGetToGPS()
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
            fragBinding.inlayStep.txtProgressTime.setText("DISTANCE (${RLGetKmMiles("km" ,"miles")})")

            //below set climbed Value
            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayDistance.txtProgressTime.setText("CLIMBED (${RLGetKmMiles("m" ,"feet")})")

            //below set pace Value
            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayClimbed.txtProgressTime.setText(("PACE (${RLGetKmMiles("PER MILES" ,"PER FEET")})"))
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")
            fragBinding.inlayClimbed.layAvg.visibility=View.VISIBLE
            fragBinding.inlayClimbed.layMax.visibility=View.VISIBLE
            fragBinding.inlayClimbed.txtMaxNumber.setText("0")
            fragBinding.inlayClimbed.txtAvgNumber.setText("0")

            //below set Speed Value
            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlayPace.txtProgressTime.setText(("SPEED (${RLGetKmMiles("KMH" ,"MPH")})"))
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE

            fragBinding.inlaySpeed.relaySensorProgress.visibility=View.GONE

        }else if (yourWayType.equals("Ride")&& isSpeedSensor){
            //below set Cadence  Value
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.ic_cadence)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.cadencerpm)

            //below set Distance Value
            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText("DISTANCE (${RLGetKmMiles("km" ,"miles")})")

            //below set climbed Value
            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText("CLIMBED (${RLGetKmMiles("m" ,"feet")})")

            //below set pace Value
            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(("PACE (${RLGetKmMiles("PER MILES" ,"PER FEET")})"))
            fragBinding.inlayPace.txtProgressTimeNumber.setText("0")
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE
            fragBinding.inlayPace.txtMaxNumber.setText("0")
            fragBinding.inlayPace.txtAvgNumber.setText("0")

            //below set Speed Value
            fragBinding.inlaySpeed.relaySensorProgress.visibility=View.VISIBLE
            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(("SPEED (${RLGetKmMiles("KMH" ,"MPH")})"))
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE


        }else if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
            fragBinding.inlayStep.imgTime.setImageResource(R.drawable.fd_steps_green)
            fragBinding.inlayStep.txtProgressTime.setText(R.string.step)
            fragBinding.inlayStep.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText("DISTANCE (${RLGetKmMiles("km" ,"miles")})")

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText("CLIMBED (${RLGetKmMiles("m" ,"feet")})")

            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(("SPEED (${RLGetKmMiles("KMH" ,"MPH")})"))
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE

            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(("PACE (${RLGetKmMiles("PER MILES" ,"PER FEET")})"))
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
        timerManager.rl_start { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLYourWayCalvulation.rl_formatElapsedTime(elapsedTime))
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
        if (yourWayType.equals("Ride")&& !isSpeedSensor){
            fragBinding.inlayDistance.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))
        }else{
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))
        }

        val elevationpoint= RLElevationPoint(RLYourWayCalvulation.noNanValueDouble(elevationMeter?:0.00),RLYourWayCalvulation.noNanValueDouble(latitude?:0.00), RLYourWayCalvulation.noNanValueDouble(longitude?:0.00))
        arrDataLocation.add(elevationpoint)
        val locationDetails= RLLocationDetails(RLYourWayCalvulation.noNanValueDouble(speedNumber?:0.00),RLYourWayCalvulation.noNanValueDouble(speedNumber?:0.00),RLYourWayCalvulation.noNanValueDouble(latitude?:0.00),RLTools.rl_getState(0), RLYourWayCalvulation.noNanValueDouble(longitude?:0.00),RLYourWayCalvulation.noNanValueDouble(elevationMeter?:0.00))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(RLYourWayCalvulation.noNanValueDouble(arrCadence.average()?:0.00))
        arrMaxCadence.add(maxCadence)

        val distancevalue= (distanceNumber?: 0.0) * 1000
        val  totalElevation =totalElevation?: 0.0

        //Calculation AssumedRev
        val assumedRevValue = rl_generateAssumedRev(classType,assumedRev,distancevalue,totalElevation,totalTime.toDouble())
        totalRev=assumedRevValue
        //Calculation AssumedCalories
        val assumedCalories=rl_calculateAssumedCalories(RFMHR.toDouble(),wsHeight.toDouble(),wsAge.toDouble().roundToInt(),totalTime.toDouble(),assumedRevValue,assumedCalories,gender)

        burntCalories=assumedCalories
        maxBurntCalories=RLYourWayCalvulation.rl_max(maxBurntCalories,burntCalories.roundToInt())
        arrBurntCalories.add(RLYourWayCalvulation.noNanValueDouble(assumedCalories?:0.00))

        val isodate=rl_getCurrentDateTimeIsoFormatted()
        gpxStringBuilder.append(
            """
                <trkpt lat="$latitude" lon="$longitude">
                    <ele>$totalElevation</ele>
                    <time>$isodate}</time>
                </trkpt>
                
                """.trimIndent())

        arrConnection.add(true)
        updateZoneData(1)
    }

    //GPS VALUE GET
    private fun RLstepGetToGPS() {
        try {
            issGpsConnect=true
            if (isSpeedSensor){
                rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
                    elevation?.let {
                        //RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                        val elevatation=RLYourWayCalvulation.rl_getValueDouble(it.toString())
                        elevationMeter=elevatation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                       // RLTools.RlLogDPrint(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })
            }else{
                rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
                    speed?.let {
                       // RLTools.RlLogDPrint(TAG,"Speed: ${it} m/s")
                        val speedSetValue=RLYourWayCalvulation.rl_getValueDouble(it.toString())
                        speedNumber=speedSetValue
                        speedList.add("%.2f".format(it).toDouble())
                        if (speedSetValue>0){
                            if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                //fragBinding.inlayPace.txtProgressTimeNumber.setText("%.2f".format(it))
                                fragBinding.inlayPace.txtProgressTimeNumber.setText(RLCalculateSpeed(it))
                            }else {
                                //fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                                fragBinding.inlaySpeed.txtProgressTimeNumber.setText(RLCalculateSpeed(it))
                            }
                            if (!speedList.isNullOrEmpty()){
                                val averageSpeed=speedList.average().toDouble()?:0.0
                                //val maxSpeed=speedList.maxOrNull()!!.toDouble()?:0.0
                                maxSpeed=RLYourWayCalvulation.rl_max(maxSpeed,speedNumber.roundToInt())
                                if (yourWayType.equals("Ride")&& !isSpeedSensor){
                                    //fragBinding.inlayPace.txtAvgNumber.setText("%.2f".format(averageSpeed).toString())
                                    fragBinding.inlayPace.txtAvgNumber.setText(RLCalculateSpeed(averageSpeed))
                                   // fragBinding.inlayPace.txtMaxNumber.setText(maxSpeed.toString())
                                    fragBinding.inlayPace.txtMaxNumber.setText(RLCalculateSpeed(maxSpeed))
                                }else{
                                   // fragBinding.inlaySpeed.txtAvgNumber.setText("%.2f".format(averageSpeed).toString())
                                    fragBinding.inlaySpeed.txtAvgNumber.setText(RLCalculateSpeed(averageSpeed))
                                   // fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                                    fragBinding.inlaySpeed.txtMaxNumber.setText(RLCalculateSpeed(maxSpeed))
                                }
                            }
                        }


                    }
                })
                rlLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
                    stepCount?.let {
                        //RLTools.RlLogDPrint(TAG,"Steps: $stepCount")
                        val stepSetValue=RLYourWayCalvulation.rl_getValueInt(stepCount.toString())
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
                        //RLTools.RlLogDPrint(TAG,"Distance: $totalDistance km")

                        val totalDistanceValue=RLYourWayCalvulation.rl_getValueDouble(totalDistance.toString())
                        distanceNumber=totalDistanceValue
                        // distanceList.add(totalDistanceValue)
                        if (totalDistanceValue>0){
                            if  (yourWayType.equals("Ride")&& !isSpeedSensor){
                               // fragBinding.inlayStep.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                                fragBinding.inlayStep.txtProgressTimeNumber.setText(RLCalculateDistance(it))
                            }else{
                                //fragBinding.inlayDistance.txtProgressTimeNumber.setText( "%.2f".format(it).toString())
                                fragBinding.inlayDistance.txtProgressTimeNumber.setText(RLCalculateDistance(it))
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
                      //  RLTools.RlLogDPrint(TAG, "Pace: $totalspace min/km")
                        val totalspaceValue=RLYourWayCalvulation.rl_getValueInt(totalspace.toString())
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
                                maxPace=RLYourWayCalvulation.rl_max(maxPace,paceNumber.toInt())
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
                      //  RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                        val elevation=RLYourWayCalvulation.rl_getValueDouble(it.toString())
                        elevationMeter=elevation
                    }
                })

                rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
                    location?.let {
                      //  RLTools.RlLogDPrint(TAG,"location: ${it}")
                        latitude=it.latitude
                        longitude=it.longitude
                        RLtrackTimePerKilometer(it)
                    }
                })

                rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
                    cadence?.let {
                       // RLTools.RlLogDPrint(TAG,"cadenceData: ${it} ")
                        val _cadenceData=RLYourWayCalvulation.rl_getValueDouble(it.toString())
                        cadenceData=_cadenceData
                        maxCadence=RLYourWayCalvulation.rl_max(maxCadence,cadenceData.toInt())
                    }
                })
            }
            rlLocationViewModel.rl_startLocationUpdates()
        }
        catch (e:Exception){
           RLTools.rl_logEPrint(TAG,"EXCEPTION GetGPS:- ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            timerManager.rl_stop()
            viewModel.stopNotifications()
        }catch (e:Exception){
           RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
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
                maxSpeedForOneKm=RLYourWayCalvulation.rl_max(maxSpeedForOneKm.roundToInt(),elapsedTimeKm.roundToInt()).toDouble()
                arrSpeedForOneKm.add(RLYourWayCalvulation.rl_getValueDouble(elapsedTimeKm.toString()))
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
                maxSpeedForOneMile=RLYourWayCalvulation.rl_max(maxSpeedForOneMile.roundToInt(),elapsedTimeMile.roundToInt()).toDouble()
                println("Time taken for last mile: $elapsedTimeMile seconds")
                arrSpeedForOneMile.add(RLYourWayCalvulation.rl_getValueDouble(elapsedTimeMile.toString()))
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

    private fun RLGetKmMiles(km:String,miles:String):String{
        return if (isImperial) {
               miles
        } else{
               km
        }
    }
    private  fun RLCalculateSpeed(speedAny:Any?): String {
        val speed = convertToDouble(speedAny)
        return if (isImperial) {
            String.format("%.2f", speed / 1.609) //Convert to miles per hour if imperial
        }else{
            String.format("%.2f", speed)  //Return speed in km/h
        }
    }
    private  fun RLCalculateDistance(distanceAny:Any?): String {
        val distance = convertToDouble(distanceAny)
        return if (isImperial) {
            String.format("%.2f", distance  * 0.621371)
        }else{
            String.format("%.2f", distance)
        }
    }
    private fun convertToInt(value: Any?): Int {
        return when (value) {
            null -> 0 // Handle null case
            is Int -> value
            is Double -> if (value.isFinite()) value.roundToInt() else 0
            is Float -> if (value.isFinite()) value.roundToInt() else 0
            is Long -> value.toInt()
            is String -> value.toDoubleOrNull()?.takeIf { it.isFinite() }?.roundToInt() ?: 0
            else -> 0 // Handle unsupported types
        }
    }
    private fun convertToDouble(value: Any?): Double {
        return when (value) {
            null -> 0.0 // Handle null case
            is Double -> if (value.isFinite()) value else 0.0
            is Float -> if (value.isFinite()) value.toDouble() else 0.0
            is Int, is Long -> value.toString().toDouble()
            is String -> value.toDoubleOrNull()?.takeIf { it.isFinite() } ?: 0.0
            else -> 0.0 // Handle unsupported types
        }
    }

    private fun updateZoneData(zoneNumber:Int) {
        // Only update the active zone with new values
        val zoneKey = when (zoneNumber) {
            1 -> RevoolaKeys.Zone1
            2 -> RevoolaKeys.Zone2
            3 -> RevoolaKeys.Zone3
            4 -> RevoolaKeys.Zone4
            5 -> RevoolaKeys.Zone5
            6 -> RevoolaKeys.Zone6
            7 -> RevoolaKeys.Zone7
            else -> null
        }
        // If we have a valid zone, replace its data with new values
        if (zoneKey != null) {
            val newZoneDataSummery = RLZoneDataSummery(
                avgCadence = RLYourWayCalvulation.noNanValueDouble(arrCadence.average()),
                avgHr = 0,
                avgPower = 0,
                avgPowerFromDevice = 0,
                avgSpeed = arrSpeed.average()?:0.0,
                burntCalories = burntCalories?:0.0,
                distance = distanceNumber?:0.0,
                remark = "android",
                seconds = totalTime.toInt()?:0,
                totalRev = totalRev?:0.0
            )
            val newZoneDataDetail = RLZoneDataDetails(
                burntCalories = burntCalories?:0.0,
                distance = distanceNumber?:0.0,
                remark = "android",
                seconds = totalTime.toInt()?:0,
                totalRev = totalRev?:0.0
            )
            zoneDataMapSummery[zoneKey] = newZoneDataSummery
            zoneDataMapDetails[zoneKey] = newZoneDataDetail
        }
    }
    private val defaultZoneSummeryData = RLZoneDataSummery(
        avgCadence = 0.0,
        avgHr = 0,
        avgPower = 0,
        avgPowerFromDevice = 0,
        avgSpeed = 0.0,
        burntCalories = 0.0,
        distance = 0.0,
        remark = "android",
        seconds = 0,
        totalRev = 0.0 )

    private val defaultZoneDetailData = RLZoneDataDetails(
        burntCalories = 0.0,
        distance = 0.0,
        remark = "android",
        seconds = 0,
        totalRev = 0.0)

    private fun getZoneDataMapSummery(): Map<String, RLZoneDataSummery> = zoneDataMapSummery.toMap()
    private fun getZoneDataMapDetail(): Map<String, RLZoneDataDetails> = zoneDataMapDetails.toMap()
    private fun initializeDefaultZonesSummery() {
        listOf(
            RevoolaKeys.Zone1,
            RevoolaKeys.Zone2,
            RevoolaKeys.Zone3,
            RevoolaKeys.Zone4,
            RevoolaKeys.Zone5,
            RevoolaKeys.Zone6,
            RevoolaKeys.Zone7
        ).forEach { name ->
            zoneDataMapSummery[name] = defaultZoneSummeryData
            zoneDataMapDetails[name] = defaultZoneDetailData
        }
    }



}