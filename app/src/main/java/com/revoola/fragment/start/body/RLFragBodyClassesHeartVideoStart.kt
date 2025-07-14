package com.revoola.fragment.start.body

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragBodyClassesHeartVideoStartBinding
import com.revoola.fragment.start.classes.RLFragClassWorkoutComplete
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.google.gson.Gson
import com.revoola.ble.BLERepository
import com.revoola.ble.BLEViewModel
import com.revoola.ble.RLBLEViewModelFactory
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.fragment.start.yourway.RLSessionDataTransferModelNew
import com.revoola.services.RLSwipeGestureDetector
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class RLFragBodyClassesHeartVideoStart : RLBaseFragment(),DataClient.OnDataChangedListener {
    val TAG: String = RLFragBodyClassesHeartVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragBodyClassesHeartVideoStartBinding
    var leftFragment: RLFragLeftBodyWithHeartVideo? =null
    var rightFragment: RLFragRightBodyWithHeartVideo? =null

    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true
    var ride:Boolean=false

    private var totalTime:String =""
    private val timerManager = RLTimerManager()
    var lastGeoElevation: Int = 0
    var totalGeoElevation: Int = 0
    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit=""
    private var displayImage = ""
    private var displayName = ""
    private var emailId=""
    private var isBasicDataAdded=true
    private var visibilityflagforthatsession:Int =0

    private var  joiningDate: Long = 0

    private var heartRateNumber:Int=0
    private var heartRate:Int=110
    private var speedNumber:Double=0.0
    private var cadenceData =0.0
    private  var CumDistance =0.0
    private var CumSpeed =0.0

    private lateinit var gestureDetectorleft: GestureDetectorCompat
    private lateinit var gestureDetectorright: GestureDetectorCompat
    private val handlerprogress = Handler(Looper.getMainLooper())

    // this all arr need to insert
    private var arrAvgRevPercentage:MutableList<Int> = mutableListOf()
    private var arrBurntCalories:MutableList<Double> = mutableListOf()
    private var arrCadence:MutableList<Double> = mutableListOf()
    private var arrCumDistance:MutableList<Double> = mutableListOf()
    private var arrCumSpeed:MutableList<Double> = mutableListOf()
    private var arrDistance:MutableList<Double> = mutableListOf()
    private var arrPower:MutableList<Int> = mutableListOf()
    private var arrPowerFromDevice:MutableList<Int> = mutableListOf()
    private var arrSpeed:MutableList<Double> = mutableListOf()
    private var arrHr:MutableList<Int> = mutableListOf()
    private var arrRevPercentage:MutableList<Double> = mutableListOf()
    private var arrRevSecond:MutableList<Double> = mutableListOf()
    private var arrMaxRevPercentage:MutableList<Double> = mutableListOf()


    private var avgRevPercentage=0.0
    var burntCalories =0.0
    private var distance:Double=0.0
    var maxRevPercentage =0.0
    var minRevPercentage =0.0
    private var revPercentage=0.0
    var totalRev  =0.0
    var maxBurntCalories =0
    var avgBurntCalories  =0.0
    var avgCadence  =0.0
    var avgHr  =0
    var avgSpeed  =0.0
    private var avgSpeedForOneKm:Double=0.0
    private var avgSpeedForOneMile:Double=0.0
    var maxCadence =0
    var maxHeartrate =0
    var maxSpeed =0
    private var maxSpeedForOneKm:Double=0.0
    private var maxSpeedForOneMile:Double=0.0
    var minHeartrate =0

    private var  zoneDataMapDetails: MutableMap<String, RLZoneDataDetails> = mutableMapOf()

    private val binding by lazy {
        RlFragBodyClassesHeartVideoStartBinding.inflate(layoutInflater)
    }

    private val bleRepository by lazy {
        BLERepository(requireContext())
    }
    private val viewModel: BLEViewModel by activityViewModels {
        RLBLEViewModelFactory(bleRepository)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesHeartVideoStart()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(true)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_body_classes_heart_video_start, container) as RlFragBodyClassesHeartVideoStartBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
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
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.rl_calculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                appUnit=userData.appUnit
                displayImage=userData.displayImage
                displayName=userData.displayName
                joiningDate=userData.joiningDate
                emailId=userData.emailId
                isBasicDataAdded=userData.isBasicDataAdded
                visibilityflagforthatsession=userData.visibilityflagforthatsession
            } else {
               RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }
        // Initialize the GestureDetector
        val swipeGestureDetector=RLSwipeGestureDetector(requireContext(),fragBinding.frameLeft,fragBinding.frameRight)
        gestureDetectorleft = GestureDetectorCompat(requireContext(), swipeGestureDetector.SwipeGestureListenerLeft())
        gestureDetectorright = GestureDetectorCompat(requireContext(), swipeGestureDetector.SwipeGestureListenerRight())

        // Set touch listener to the  view
        fragBinding.viewLeft.setOnTouchListener { _, event ->
            gestureDetectorleft.onTouchEvent(event)
            true
        }

        // Set touch listener to the  view
        fragBinding.viewRight.setOnTouchListener { _, event ->
            gestureDetectorright.onTouchEvent(event)
            true
        }


        val data=  requireArguments().getString(RLExtraValueKey.videoData,"")
        val videoID=  requireArguments().getString(RLExtraValueKey.videoId,"")

       parentFragmentManager.beginTransaction()
            .add(R.id.frame_left, RLFragLeftBodyWithHeartVideo())
            .commit()
        val bundleright=Bundle()
        bundleright.putString("videoID",videoID)
        parentFragmentManager.beginTransaction()
            .add(R.id.frame_right, RLFragRightBodyWithHeartVideo().newInstance(bundleright))
            .commit()

        RLstartCountdown()


        ride=  requireArguments().getBoolean(RLExtraValueKey.isRide)
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data,videoID)
        val isWatch = requireArguments().getBoolean(RLExtraValueKey.isWatch)
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
                            heartRateNumber=RLYourWayCalvulation.rl_getValueInt(data.toString())
                            val heartRateSetValue=RLYourWayCalvulation.rl_getValueInt(data.toString())
                            if (heartRateSetValue > 0){
                                if (leftFragment!=null){
                                    leftFragment?.RLUpdateHRTime(data.toString(),burntCalories.roundToInt().toString()?:"0",totalRev?:0.00)
                                }
                                maxHeartrate=RLYourWayCalvulation.rl_max(maxHeartrate,heartRateNumber)
                                minHeartrate=RLYourWayCalvulation.rl_min(minHeartrate,heartRateNumber)
                            }
                        }
                    }

                }
            }
        }
    }
    private fun RLVideotimeset(time:String){
       // val firstFragment = parentFragmentManager.findFragmentById(R.id.frame_left) as? RLFragLeftBodyWithHeartVideo
        // firstFragment?.RLUpdateVideoTime(time)
        leftFragment = parentFragmentManager.findFragmentById(R.id.frame_left) as? RLFragLeftBodyWithHeartVideo
        rightFragment = parentFragmentManager.findFragmentById(R.id.frame_right) as? RLFragRightBodyWithHeartVideo
        leftFragment?.RLUpdateVideoTime(time)
    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String, videoID:String){
        val videoUri = Uri.parse(VideoCardData.videoLinkiPhonex)
        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)
        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
            // RLAdjustAspectRatio( fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            mediaPlayer.start()
            fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE

            RLVideotimeset(RLformatTime(fragBinding.videoView.duration))
            handlerprogress.post(RLupdateSeekBarRunnable)
        }
        // Handle errors
        fragBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }
        fragBinding.inlayPlayStop.btnStop.setOnClickListener {
            RLSendDataToWearOS(context = requireContext(),
                formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                calories = burntCalories,
                total_Rev = totalRev,
                REVPer = revPercentage,
                buttonType = 2)
            Wearable.getDataClient(requireContext()).removeListener(this)
            timerManager.rl_stop()
            viewModel.stopNotifications()
            fragBinding.videoView.stopPlayback()
            RLCompleteSessionFragmentOpen(data,videoID)
        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                pauseVideo=false
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = 1)
                viewModel.pauseNotifications()
                fragBinding.videoView.pause()
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
            }else{
                pauseVideo=true
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(((totalTime.toInt())*1000).toLong()),
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = 0)
                viewModel.resumeNotifications()
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
            }
        }
        fragBinding.viewCenter.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.VISIBLE
            }
        }
    }

    //when all data set and new open then this function call
    private fun RLCompleteSessionFragmentOpen(data: String, videoID: String) {
        val bundle: Bundle = Bundle()
        val cardData = RLSessionDataTransferModelNew()


        cardData.VIDEODATA = data
        cardData.classType = RLConstants.BODY
        cardData.SENSOR = RLConstants.HEART_SENSOR
        cardData.videoID = videoID

        cardData.totalTime = totalTime
        cardData.avgRevPercentage = avgRevPercentage
        cardData.burntCalories = burntCalories
        cardData.distance = distance
        cardData.maxRevPercentage = maxRevPercentage
        cardData.minRevPercentage = minRevPercentage
        cardData.revPercentage = revPercentage
        cardData.totalRev = totalRev
        cardData.maxSpeed = maxSpeed
        cardData.maxHeartRate = maxHeartrate
        cardData.maxCadence = maxCadence
        cardData.maxBurntCalories = maxBurntCalories
        cardData.minHeartRate = minHeartrate

        cardData.avgBurntCalories = avgBurntCalories

        cardData.avgCadence = avgCadence
        cardData.avgHr = avgHr
        cardData.avgSpeed = avgSpeed

        cardData.maxSpeedForOneKm = maxSpeedForOneKm
        cardData.maxSpeedForOneMile = maxSpeedForOneMile
        cardData.avgSpeedForOneKm = avgSpeedForOneKm
        cardData.avgSpeedForOneMile = avgSpeedForOneMile

        cardData.arrBurntCalories = arrBurntCalories
        cardData.arrCadence = arrCadence
        cardData.arrDistance = arrDistance
        cardData.arrHr = arrHr
        cardData.arrPower = arrPower
        cardData.arrPowerFromDevice = arrPowerFromDevice


        cardData.arrRevPercentage = arrRevPercentage
        cardData.arrRevSecond = arrRevSecond
        cardData.arrSpeed = arrSpeed
        cardData.arrCumDistance = arrCumDistance
        cardData.arrCumSpeed = arrCumSpeed

        cardData.arrAvgRevPercentage = arrAvgRevPercentage
        cardData.arrMaxRevPercentage = arrMaxRevPercentage


        cardData.zoneDataDetail = getZoneDataMapDetail()

        cardData.wsWeight = wsWeight
        cardData.wsHeight=wsHeight
        cardData.wsAge=wsAge
        cardData.gender=gender
        cardData.RFMHR=RFMHR
        cardData.RestingHR=RestingHR
        cardData.appUnit=appUnit
        cardData.displayImage = displayImage
        cardData.displayName =displayName
        cardData.joiningDate= joiningDate
        cardData.emailId = emailId
        cardData.isBasicDataAdded = isBasicDataAdded
        cardData.visibilityflagforthatsession = visibilityflagforthatsession


       // bundle.putSerializable("cardData",cardData)
        bundle.putParcelable("cardData",cardData)

        (context as RLMainActivityRL).rl_loadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, false, null, false)
    }

    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.inlayCountdown.txtTitle.setText(VideoData.rideTitle)
        fragBinding.inlayCountdown.txtNamewith.setText(VideoData.instructor)
        //BODY
        fragBinding.inlayCountdown.rlBodyTimenumber.visibility=View.VISIBLE
        fragBinding.inlayCountdown.txtVideo.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_easy)
            fragBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_hard)
            fragBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_medium)
            fragBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
    }

    //When First Open Then CountDown Set
    private fun RLstartCountdown() {
        var count = 5
        var countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) { // Countdown from 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                fragBinding.inlayCountdown.txtCountdown.text = "$count" // Display current count
                count--
            }
            override fun onFinish() {
                RLtimerMain()
                fragBinding.inlayCountdown.relayCountdown.visibility=View.GONE
            }
        }.start()
    }
    fun RLtimerMain() {
        timerManager.rl_start { elapsedTime ->
            activity?.runOnUiThread {
                totalTime=(elapsedTime/1000).toString()
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.rl_formatElapsedTime(elapsedTime) ,
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = 0)
                RlDataFillAllArray()
            }
        }
    }
    private val RLupdateSeekBarRunnable = object : Runnable {
        override fun run() {
            handlerprogress.postDelayed(this, 1000)
            val timeminus=fragBinding.videoView.duration - fragBinding.videoView.currentPosition
            RLVideotimeset(RLformatTime(timeminus))
        }
    }

    private fun  RlDataFillAllArray(){
        val currentCalories=RLYourWayCalvulation.calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble(),RestingHR,RFMHR)
        if (heartRateNumber>0){
            heartRate=heartRateNumber
        }
        val REVPer=RLYourWayCalvulation.calculateREVPer(heartRate,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender,RestingHR,RFMHR) //only REV
        if (leftFragment!=null){
            leftFragment?.RLUpdateHRPersentage(REVPer.roundToInt(),heartRate,burntCalories,totalRev)
        }
        arrRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(REVPer))
        avgRevPercentage = RLYourWayCalvulation.avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(RLYourWayCalvulation.noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        maxRevPercentage=RLYourWayCalvulation.rl_max(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLYourWayCalvulation.rl_min(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        if (rightFragment!=null){
            rightFragment?.RLRankingByRevSec(totalTime.toInt(),REVSec.roundToInt(),totalRev.roundToInt(),REVPer.roundToInt(),maxRevPercentage.roundToInt(),avgRevPercentage.roundToInt(),heartRateNumber)
        }

        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLYourWayCalvulation.rl_max(maxBurntCalories,burntCalories.toInt())

        revPercentage=REVPer

        arrBurntCalories.add( RLYourWayCalvulation.noNanValueDouble(currentCalories))
        arrCadence.add(RLYourWayCalvulation.noNanValueDouble(cadenceData))
        arrDistance.add( RLYourWayCalvulation.noNanValueDouble(distance))
        arrHr.add(heartRateNumber)
        arrSpeed.add(RLYourWayCalvulation.noNanValueDouble(speedNumber))

        avgHr = arrHr.average().roundToInt()?:0
        avgBurntCalories =  arrBurntCalories.average()?:0.0

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distance
        arrCumDistance.add(RLYourWayCalvulation.noNanValueDouble(CumDistance))
        arrCumSpeed.add(RLYourWayCalvulation.noNanValueDouble(CumSpeed))
        distance=CumDistance

        arrAvgRevPercentage.add(avgRevPercentage.roundToInt())
        arrMaxRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(maxRevPercentage))
        arrPower.add(0)
        arrPowerFromDevice.add(0)

        updateZoneData(RLTools.rl_zoneDiff(REVPer.roundToInt()))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            Wearable.getDataClient(requireContext()).removeListener(this)
            timerManager.rl_stop()
            fragBinding.videoView.stopPlayback()
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }catch (e:Exception){
           RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
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
        dataMap.putString("SessionName", "Body")

        // Create the PutDataRequest; marking it as urgent ensures it gets delivered quickly.
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/wear_data/Heart_Rate") {
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val data = RlGetValueInt(dataMapItem.dataMap.getInt("Heart_Rate", 0).toString())
                    heartRateNumber=RLYourWayCalvulation.rl_getValueInt(data.toString())
                    val heartRateSetValue=RLYourWayCalvulation.rl_getValueInt(data.toString())
                    if (heartRateSetValue > 0){
                        if (leftFragment!=null){
                            leftFragment?.RLUpdateHRTime(data.toString(),burntCalories.roundToInt().toString()?:"0",totalRev?:0.00)
                        }
                        maxHeartrate=RLYourWayCalvulation.rl_max(maxHeartrate,heartRateNumber)
                        minHeartrate=RLYourWayCalvulation.rl_min(minHeartrate,heartRateNumber)
                    }
                }
            }
        }
    }


    private val defaultZoneDetailData = RLZoneDataDetails(
        burntCalories = 0.0,
        distance = 0.0,
        remark = "android",
        seconds = 0,
        totalRev = 0.0)

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
            zoneDataMapDetails[name] = defaultZoneDetailData
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
            val newZoneDataDetail = RLZoneDataDetails(
                burntCalories = burntCalories?:0.0,
                distance = distance?:0.0,
                remark = "android",
                seconds = totalTime.toInt()?:0,
                totalRev = totalRev?:0.0
            )
            zoneDataMapDetails[zoneKey] = newZoneDataDetail
        }
    }

}