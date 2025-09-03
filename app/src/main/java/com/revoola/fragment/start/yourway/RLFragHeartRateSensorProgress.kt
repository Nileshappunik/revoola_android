package com.revoola.fragment.start.yourway

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.revoola.ble.BLERepository
import com.revoola.ble.BLEViewModel
import com.revoola.ble.RLBLEViewModelFactory
import com.revoola.ble.RLExtraValueKey
import com.revoola.databinding.RlFragHeartrateSensorProgressBinding
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.services.RLLocationViewModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.launch
import java.lang.Math.round
import kotlin.math.roundToInt
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.databasefirebase.RevoolaKeys

class RLFragHeartRateSensorProgress : RLBaseFragment(),DataClient.OnDataChangedListener{
    val TAG: String = RLFragHeartRateSensorProgress::class.java.simpleName
    private val timerManager = RLTimerManager()

    private lateinit var rlLocationViewModel: RLLocationViewModel
    private var  yourWayType:String=""

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
    private var lastGeoElevation: Int = 0
    private var totalGeoElevation: Int = 0

    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit:String="Metric"
    private var visibilityflagforthatsession:Int=0
    private var isImperial:Boolean = false
    private var emailId=""
    private var isBasicDataAdded=true



    private var burntCalories =0.0
    private var totalRev  =0.0
    private var lastrevPercentage=0.0
    private var maxRevPercentage =0.0
    private var minRevPercentage =0.0
    private var maxEffort =0
    private var maxSpeed =0
    private var maxCadence =0
    private var maxBurntCalories =0
    private var maxHeartrate =0
    private var minHeartrate =0
    private var CumDistance =0.0
    private var CumSpeed =0.0
    private var cadenceData =0.0
    private var maxPace =0

    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0

    private var startTimeKm: Long = 0
    private var startTimeMile: Long = 0
    private var lastLocation: Location? = null
    private var totalDistance = 0.0

    private val gpxStringBuilder = StringBuilder()


    private var  zoneDataMapSummery: MutableMap<String, RLZoneDataSummery> = mutableMapOf()
    private var  zoneDataMapDetails: MutableMap<String, RLZoneDataDetails> = mutableMapOf()

    private val bleRepository by lazy {
        BLERepository(requireContext())
    }

    private val viewModel: BLEViewModel by activityViewModels {
        RLBLEViewModelFactory(bleRepository)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragHeartRateSensorProgress()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragHeartrateSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString(RLExtraValueKey.yourWayType).toString().trim()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
            }
        })
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        initializeDefaultZonesSummery()
        RLstartCountdown()
        rlLocationViewModel = RLLocationViewModel(requireActivity().application)
        val isWatch = requireArguments().getBoolean(RLExtraValueKey.isWatch)
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.rl_calculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                appUnit=userData.appUnit
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

        if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
            RLstepGetToGPS()
        }

        fragBinding.relaytiveMain.setBackgroundResource(RLTools.rl_getImage1(yourWayType.toLowerCase()))
        fragBinding.inlayTop.ivTitle.setText(yourWayType)
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setTextColor(resources.getColor(R.color.AppWhiteColor))


        fragBinding.circularProgressBar.rl_setProgress(100)
        fragBinding.circularProgressBar.rl_setMaxProgress(100)
        fragBinding.circularProgressBar.rl_setProgressColor(resources.getColor(R.color.AppOrangeColor))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.rl_setStrokeWidth(15f)

        fragBinding.inlayCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtProgressTime.setText(R.string.activecalories)
        fragBinding.inlayCalories.txtProgressTimeNumber.setText("0")

        fragBinding.inlayHeartrate.imgTime.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtProgressTime.setText(R.string.heartratebpm)
        fragBinding.inlayHeartrate.layAvg.visibility=View.VISIBLE
        fragBinding.inlayHeartrate.layMax.visibility=View.VISIBLE

       // RLwayTypeDesignSet(yourWayType)

        fragBinding.layPause.setOnClickListener {
           try {
               RLSendDataToWearOS(context = requireContext(),
                   formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                   calories = burntCalories,
                   total_Rev = totalRev,
                   REVPer = revPercentage,
                   buttonType = 1)
               viewModel.pauseNotifications()
                timerManager.rl_pause()

                fragBinding.layPause.visibility=View.GONE
                fragBinding.layResumestop.visibility=View.VISIBLE

               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.rl_stopLocationUpdates()
               }

           }catch (e:Exception){
              RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layResume.setOnClickListener {
           try{
               RLSendDataToWearOS(context = requireContext(),
                   formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                   calories = burntCalories,
                   total_Rev = totalRev,
                   REVPer = revPercentage,
                   buttonType = 0)
                timerManager.rl_resume()

                fragBinding.layPause.visibility=View.VISIBLE
                fragBinding.layResumestop.visibility=View.GONE

               viewModel.resumeNotifications()

               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.rl_startLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
           try{
               RLSendDataToWearOS(context = requireContext(),
                   formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                   calories = burntCalories,
                   total_Rev = totalRev,
                   REVPer = revPercentage,
                   buttonType = 2)
                timerManager.rl_stop()
               viewModel.stopNotifications()
               Wearable.getDataClient(requireContext()).removeListener(this)
               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.rl_stopLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
           }

            val bundle: Bundle = Bundle()

            //val cardData = RLSessionDataTransferModel()
            val cardData = RLSessionDataTransferModelNew()

            cardData.yourWayType = yourWayType
            cardData.totalTime = totalTime?:"0"
            cardData.gpxStringBuilder = gpxStringBuilder.toString()?:""
            cardData.gpxTServerString=gpxStringBuilder.toString()?:""
            cardData.gpxTServerNString=gpxStringBuilder.toString()?:""

            cardData.SENSOR = RLConstants.HEART_SENSOR
            cardData.avgRevPercentage = RLYourWayCalvulation.noNanValueDouble(avgRevPercentage?:0.00)
            cardData.burntCalories = RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00)
            cardData.distance = RLYourWayCalvulation.noNanValueDouble(distance?:0.00)
            cardData.maxRevPercentage = RLYourWayCalvulation.noNanValueDouble(maxRevPercentage?:0.00)
            cardData.minRevPercentage = RLYourWayCalvulation.noNanValueDouble(minRevPercentage?:0.00)
            cardData.revPercentage = RLYourWayCalvulation.noNanValueDouble(revPercentage?:0.00)
            cardData.totalElevation = RLYourWayCalvulation.noNanValueDouble(totalElevation?:0.00)
            cardData.totalRev = RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00)
            cardData.totalSteps = stepsNumber?:0
            cardData.maxSpeed = maxSpeed?:0
            cardData.maxHeartRate = maxHeartrate?:0
            cardData.maxCadence = maxCadence?:0
            cardData.maxBurntCalories = maxBurntCalories?:0
            cardData.minHeartRate = minHeartrate?:0
            cardData.avgSpeed = arrSpeed.average()?:0.0
            cardData.hrm = 1

            cardData.maxSpeedForOneKm = RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00)
            cardData.maxSpeedForOneMile = RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00)
            cardData.avgSpeedForOneKm = RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00)
            cardData.avgSpeedForOneMile = RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00)

            cardData.arrConnection = arrConnection

            cardData.arrBurntCalories = arrBurntCalories
            cardData.arrCadence = arrCadence
            cardData.arrDistance = arrDistance
            cardData.arrElevation = arrElevation
            cardData.arrHRRecordedSecond = arrHRRecordedSecond
            cardData.arrHr = arrHr
            cardData.arrRevPercentage = arrRevPercentage
            cardData.arrRevSecond = arrRevSecond
            cardData.arrSpeed = arrSpeed
            cardData.arrCumDistance = arrCumDistance
            cardData.arrCumElevation = arrCumElevation
            cardData.arrCumSpeed = arrCumSpeed

            cardData.arrAvgCadence = arrAvgCadence
            cardData.arrAvgHr = arrAvgHr
            cardData.arrAvgRevPercentage = arrAvgRevPercentage
            cardData.arrMaxCadence = arrMaxCadence
            cardData.arrMaxHr = arrMaxHr
            cardData.arrMaxRevPercentage = arrMaxRevPercentage

            if (arrSpeedForOneKm.isNullOrEmpty()){
                arrSpeedForOneKm= mutableListOf(0.00)
            }
            if (arrSpeedForOneMile.isNullOrEmpty()){
                arrSpeedForOneMile= mutableListOf(0.00)
            }

            cardData.speedForOneKm = arrSpeedForOneKm
            cardData.speedForOneMile = arrSpeedForOneMile

            if (arrDataLocation.isNullOrEmpty()){
                val dataclass= RLElevationPoint(0.00,0.00,0.00)
                arrDataLocation.add(dataclass)
            }
            if (arrLocationDetails.isNullOrEmpty()){
                val dataclass= RLLocationDetails(0.00,0.00,0.00,0,0.00,0.00)
                arrLocationDetails.add(dataclass)
            }

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
            (context as RLMainActivityRL).rl_loadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

        }

        if (isWatch){
            Wearable.getDataClient(requireContext()).removeListener(this)
            Wearable.getDataClient(requireContext()).addListener(this)
        }
        else{
            val  sensorDeviceAddress = requireArguments().getString(RLExtraValueKey.sensorDeviceAddress).toString()
            viewModel.connectToDevice(sensorDeviceAddress)
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    // Add the Bluetooth state collector first
                    launch {
                        viewModel.sensorData.collect { dataGet ->
                            val data = dataGet?.heartRate?:"0"
                            heartRateNumber=convertToInt(data.toString())
                            val  heartRateSetValue=convertToInt(data.toString())
                            if (heartRateSetValue > 0){
                                arrHRRecordedSecond.add(totalTime.toInt()?:0)
                                fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(data)
                                maxHeartrate= RLYourWayCalvulation.rl_max(maxHeartrate,heartRateNumber)
                                minHeartrate=RLYourWayCalvulation.rl_min(minHeartrate,heartRateNumber)
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
            fragBinding.inlayDistance.txtProgressTime.setText("DISTANCE (${RLGetKmMiles("km" ,"miles")})")

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText("CLIMBED (${RLGetKmMiles("m" ,"feet")})")
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

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

        }else if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
            fragBinding.inlayCadence.imgTime.setImageResource(R.drawable.fd_steps_green)
            fragBinding.inlayCadence.txtProgressTime.setText(R.string.step)
            fragBinding.inlayCadence.txtProgressTimeNumber.setText("0")

            fragBinding.inlayDistance.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.inlayDistance.txtProgressTime.setText("DISTANCE (${RLGetKmMiles("km" ,"miles")})")

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText("CLIMBED (${RLGetKmMiles("m" ,"feet")})")
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

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
                RLtimerMain()
                // You can add additional animations or actions here when the countdown ends
            }
        }.start()
    }
    private fun RLtimerMain() {
        timerManager.rl_start { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLYourWayCalvulation.rl_formatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(elapsedTime),
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = 0)
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){
        val currentCalories=RLYourWayCalvulation.calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble(),RestingHR,RFMHR)
        if (heartRateNumber>0){
            heartRate = heartRateNumber
        }
        val REVPer=RLYourWayCalvulation.calculateREVPer(heartRate,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender,RestingHR,RFMHR) //only REV

        RLUpdateHRPersentage(REVPer.roundToInt())
        arrRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(REVPer))
        avgRevPercentage = RLYourWayCalvulation.avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(RLYourWayCalvulation.noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        lastrevPercentage=REVPer
        maxRevPercentage=RLYourWayCalvulation.rl_max(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLYourWayCalvulation.rl_min(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        maxEffort=RLYourWayCalvulation.rl_max(maxEffort,totalRev.roundToInt())
        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLYourWayCalvulation.rl_max(maxBurntCalories,burntCalories.toInt())

        revPercentage=REVPer

        arrBurntCalories.add( RLYourWayCalvulation.noNanValueDouble(currentCalories))
        arrCadence.add(RLYourWayCalvulation.noNanValueDouble(cadenceData))
        arrDistance.add( RLYourWayCalvulation.noNanValueDouble(distance))
        arrHr.add(heartRateNumber)
        arrSpeed.add(RLYourWayCalvulation.noNanValueDouble(speedNumber))

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distance
        arrCumDistance.add(RLYourWayCalvulation.noNanValueDouble(CumDistance))
        arrCumSpeed.add(RLYourWayCalvulation.noNanValueDouble(CumSpeed))


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
            arrElevation.add(RLYourWayCalvulation.noNanValueDouble(lastGeoElevation.toDouble()))
            arrCumElevation.add(RLYourWayCalvulation.noNanValueDouble((totalGeoElevation/10).toDouble()))

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        val totalCaloriesBurnedValue=convertToDouble(burntCalories.toString()).roundToInt()
        fragBinding.inlayCalories.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())

        fragBinding.inlayClimbed.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))


        val elevationpoint= RLElevationPoint(RLYourWayCalvulation.noNanValueDouble(elevationMeter),RLYourWayCalvulation.noNanValueDouble(latitude), RLYourWayCalvulation.noNanValueDouble(longitude))
        arrDataLocation.add((elevationpoint))
        val locationDetails= RLLocationDetails(RLYourWayCalvulation.noNanValueDouble(speedNumber),RLYourWayCalvulation.noNanValueDouble(speedNumber),RLYourWayCalvulation.noNanValueDouble(latitude),RLTools.rl_getState(revPercentage.roundToInt()), RLYourWayCalvulation.noNanValueDouble(longitude),RLYourWayCalvulation.noNanValueDouble(elevationMeter))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(RLYourWayCalvulation.noNanValueDouble(arrCadence.average()))
        arrAvgHr.add(arrHr.average().roundToInt())
        arrAvgRevPercentage.add(avgRevPercentage.roundToInt())
        arrMaxCadence.add(maxCadence)
        arrMaxHr.add(maxHeartrate)
        arrMaxRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(maxRevPercentage))

        if (!arrSpeedForOneKm.isNullOrEmpty()){

        }
        if (!arrSpeedForOneMile.isNullOrEmpty()){

        }

        val isodate=rl_getCurrentDateTimeIsoFormatted()
        gpxStringBuilder.append(
            """
                <trkpt lat="$latitude" lon="$longitude">
                    <ele>$totalElevation</ele>
                    <time>$isodate}</time>
                </trkpt>
                
                """.trimIndent())

        arrConnection.add(true)

        val REVPecentage=REVPer.roundToInt()
        updateZoneData(RLTools.rl_zoneDiff(REVPecentage))

    }

    private fun RLUpdateHRPersentage(REVPer : Int) {
        try {
        if (REVPer>=100){
            fragBinding.progresstext.setText("100%")
            fragBinding.circularProgressBar.rl_setProgress(100)
        }else if(REVPer>=0){
            fragBinding.progresstext.setText("$REVPer%")
            fragBinding.circularProgressBar.rl_setProgress(REVPer)
        }else{
            fragBinding.progresstext.setText("--%")
            fragBinding.circularProgressBar.rl_setProgress(0)
        }
        val progresscolor = RLTools.rl_calculateCircularGraph(REVPer)
        fragBinding.progresstext.setTextColor(Color.parseColor(progresscolor))
        fragBinding.circularProgressBar.rl_setProgressColor(Color.parseColor(progresscolor))
        val tintColor =Color.parseColor(progresscolor)
        fragBinding.imgEffort.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        fragBinding.txtEffortNumber.setText(totalRev.roundToInt().toString())
        fragBinding.maxEffortNumber.setText(maxEffort.toString())

        val ZoneTextData= RLTools.rl_verifyFeedZoneName(REVPer.toDouble()?:0.0)
        fragBinding.txtCurrentZone.setText(ZoneTextData.efforZoneText)
        fragBinding.txtCurrentZone.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        }catch (e:Exception){
            RLTools.rl_logEPrint(TAG,"Exception: ${e.message}")
        }
    }

    //GPS VALUE GET
    private fun RLstepGetToGPS() {
        //rlLocationViewModel =RLLocationViewModel(requireActivity().application)
        rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
            speed?.let {
                RLTools.rl_logDPrint(TAG,"Speed: ${it} m/s")
                val speedSetValue=convertToDouble(it.toString())
                speedNumber=speedSetValue
                speedList.add("%.2f".format(it).toDouble())
                if (speedSetValue>0){
                    //fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                    fragBinding.inlaySpeed.txtProgressTimeNumber.setText(RLCalculateSpeed(it))
                    maxSpeed=RLYourWayCalvulation.rl_max(maxSpeed,speedNumber.roundToInt())
                   // fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                    fragBinding.inlaySpeed.txtMaxNumber.setText(RLCalculateSpeed(maxSpeed))
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                       // fragBinding.inlaySpeed.txtAvgNumber.setText( "%.2f".format(averageSpeed).toString())
                        fragBinding.inlaySpeed.txtAvgNumber.setText(RLCalculateSpeed(averageSpeed))

                    }
                }
            }
        })

        rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
            elevation?.let {
                RLTools.rl_logDPrint(TAG,"elevation: ${it} m")
                val elevation=convertToDouble(it.toString())
                elevationMeter=elevation
            }
        })

        rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
            location?.let {
                RLtrackTimePerKilometer(it)
                RLTools.rl_logDPrint(TAG,"location: ${it}")
                latitude=it.latitude
                longitude=it.longitude
            }
        })

        rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
            cadence?.let {
                RLTools.rl_logDPrint(TAG,"cadenceData: ${it} ")
                val _cadenceData=convertToDouble(it.toString())
                cadenceData=_cadenceData
                maxCadence=RLYourWayCalvulation.rl_max(maxCadence,cadenceData.roundToInt())
            }
        })

        rlLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
            stepCount?.let {
                RLTools.rl_logDPrint(TAG,"Steps: $stepCount")
                val stepSetValue=convertToInt(stepCount.toString())
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
                RLTools.rl_logDPrint(TAG,"Distance: $totalDistance km")
                val totalDistanceValue=convertToDouble(totalDistance.toString())
                this.distance =totalDistanceValue
                //distanceList.add(totalDistanceValue)
                if (totalDistanceValue>0){
                  //  fragBinding.inlayDistance.txtProgressTimeNumber.setText("%.2f".format(it))
                    fragBinding.inlayDistance.txtProgressTimeNumber.setText(RLCalculateDistance(it))
                }
            }
        })

        rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
            pace?.let {
                val totalspace=round(it * 100) / 100
                RLTools.rl_logDPrint(TAG, "Pace: $totalspace min/km")
                val totalspaceValue=convertToInt(totalspace.toString())
                paceNumber=totalspaceValue
                paceList.add(totalspaceValue)
                if (totalspaceValue>0){
                    fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
                    if (!paceList.isNullOrEmpty()){
                        val averagePace=paceList.average().roundToInt()?:0
                        maxPace=RLYourWayCalvulation.rl_max(maxPace,paceNumber.toInt())
                        //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                        fragBinding.inlayPace.txtAvgNumber.setText(averagePace.toString())
                        fragBinding.inlayPace.txtMaxNumber.setText(maxPace.toString())
                    }

                }
            }
        })

        rlLocationViewModel.rl_startLocationUpdates()
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
                arrSpeedForOneKm.add(convertToDouble(elapsedTimeKm.toString()))
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
                arrSpeedForOneMile.add(convertToDouble(elapsedTimeMile.toString()))
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
    override fun onDestroy() {
        super.onDestroy()
        try {
            timerManager.rl_stop()
            Wearable.getDataClient(requireContext()).removeListener(this)
            viewModel.stopNotifications()
        }catch (e:Exception){
           RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
        }
    }
    //Wear os Send Data
    private fun RLSendDataToWearOS(context: Context, formattedTime: String,
                                   calories: Double, total_Rev: Double,
                                   REVPer: Double, buttonType: Int) {
        // Create a PutDataMapRequest with a unique path
        val putDataMapRequest = PutDataMapRequest.create("/mobile_to_wear")
        val dataMap = putDataMapRequest.dataMap

        // Put your data into the DataMap using distinct keys
        dataMap.putString("formattedTime", formattedTime)
        dataMap.putDouble("calories", calories)
        dataMap.putDouble("total_Rev", total_Rev)
        dataMap.putDouble("REVPer", REVPer)
        dataMap.putInt("action", buttonType)
        dataMap.putString("SessionName", "YourWay")

        //Create the PutDataRequest; marking it as urgent ensures it gets delivered quickly.
        val putDataRequest = putDataMapRequest.asPutDataRequest().setUrgent()

        // Send the DataItem to connected Wear OS devices
        Wearable.getDataClient(context).putDataItem(putDataRequest)
            .addOnSuccessListener {
                // Log.d(TAG, "Data sent successfully: formattedTime=$formattedTime, calories=$calories, total_Rev=$total_Rev, REVPer=$REVPer, buttonType=$buttonType")
            }
            .addOnFailureListener { exception ->
                // Log.e(TAG, "Failed to send data", exception)
            }
    }
    //Wear os Listener
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/wear_data/Heart_Rate") {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val heartRate = convertToInt(dataMapItem.dataMap.getInt("Heart_Rate", 0).toString())

                    // Update your UI or state with the heart rate
                    activity?.runOnUiThread {
                        // For example, update a TextView:
                        heartRateNumber=convertToInt(heartRate.toString())
                        val  heartRateSetValue=convertToInt(heartRate.toString())
                        if (heartRateSetValue > 0){
                            arrHRRecordedSecond.add(totalTime.toInt()?:0)
                            fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(heartRate.toString())
                            maxHeartrate= RLYourWayCalvulation.rl_max(maxHeartrate,heartRateNumber)
                            minHeartrate=RLYourWayCalvulation.rl_min(minHeartrate,heartRateNumber)
                            fragBinding.inlayHeartrate.txtMaxNumber.setText(maxHeartrate.toString())
                            if (!arrHr.isNullOrEmpty()){
                                val avgHeartRate=arrHr.average().roundToInt()?:0
                                fragBinding.inlayHeartrate.txtAvgNumber.setText(avgHeartRate.toString())
                            }

                        }
                    }
                    RLTools.rl_logEPrint("DataListener", "Received heart rate: $heartRate")
                }
            }
        }
    }

    private fun RLGetKmMiles(km:String,miles:String):String{
        return if (isImperial) {
            miles
        } else {
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
                avgHr = arrHr.average().roundToInt()?:0,
                avgPower = 0,
                avgPowerFromDevice = 0,
                avgSpeed = arrSpeed.average()?:0.0,
                burntCalories = burntCalories?:0.0,
                distance = distance?:0.0,
                remark = "android",
                seconds = totalTime.toInt()?:0,
                totalRev = totalRev?:0.0
            )
            val newZoneDataDetail = RLZoneDataDetails(
                burntCalories = burntCalories?:0.0,
                distance = distance?:0.0,
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