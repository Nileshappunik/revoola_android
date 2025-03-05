package com.revoola.fragment.start.body

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragBodyClassesSpeedVideoStartBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.fragment.start.classes.RLFragClassWorkoutComplete

import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.google.gson.Gson
import com.revoola.ble.BLERepository
import com.revoola.ble.BLEViewModel
import com.revoola.ble.RLBLEViewModelFactory
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.fragment.start.yourway.RLSessionDataTransferModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class RLFragBodyClassesSpeedVideoStart : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesSpeedVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragBodyClassesSpeedVideoStartBinding
    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true
    var ride:Boolean=false

    private var totalTime:String =""
    private val timerManager = RLTimerManager()

    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit=""
    private var displayImage = ""
    private var displayName = ""
    private var  joiningDate: Long = 0
    private var emailId=""
    private var isBasicDataAdded=true


    private var distanceNumber:Double=0.0
    private var climbedNumber:Int=0
    private var activeCaloriesNumber:Double=0.0
    private var speedNumber:Double=0.0
    private var cadenceData:Double=0.0
    private var CumDistance =0.0
    private var CumSpeed =0.0

    private val handlerprogress = Handler(Looper.getMainLooper())
    private lateinit var gestureDetectorleft: GestureDetectorCompat
    private var speedList:MutableList<Double> = mutableListOf()

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

    private val bleRepository by lazy {
        BLERepository(requireContext())
    }

    private val viewModel: BLEViewModel by activityViewModels {
        RLBLEViewModelFactory(bleRepository)
    }

    private val binding by lazy {
        RlFragBodyClassesSpeedVideoStartBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesSpeedVideoStart()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_body_classes_speed_video_start, container) as RlFragBodyClassesSpeedVideoStartBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragBodyClassesSpeedVideoStart" )
        RLuisetup()
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        // Initialize the GestureDetector
        gestureDetectorleft = GestureDetectorCompat(requireContext(), SwipeGestureListenerLeft())

        // Set touch listener to the  view
        fragBinding.leftsideview.setOnTouchListener { _, event ->
            gestureDetectorleft.onTouchEvent(event)
            true
        }

        return fragBinding.root
    }
    private fun RLuisetup() {
        RLUserDataGet()
        RLstartCountdown()

        val data=  requireArguments().getString(RLExtraValueKey.videoData,"")
         ride=  requireArguments().getBoolean(RLExtraValueKey.isRide)
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)

        fragBinding.inlayCadence.imgIcon.setImageResource(R.drawable.ic_cadence)
        fragBinding.inlayCadence.txtName.setText(R.string.cadence)
        fragBinding.inlayCadence.txtNumber.setText("--")

        fragBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.inlayTime.txtName.setText(R.string.time)
        fragBinding.inlayTime.progressView2.visibility=View.GONE

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
                        val AvgSPEED = dataGet?.avgSpeed?:"0"
                        val CALORIES = dataGet?.calories?:"0"

                        val speedSetValue=RLYourWayCalvulation.RlGetValueInt(SPEED.toString())?:0
                        val cadenceSetValue=RLYourWayCalvulation.RlGetValueInt(CADENCE!!.toString())?:0
                        if (cadenceSetValue>0){
                            fragBinding.inlayCadence.txtNumber.setText(CADENCE.toString())
                        }

                        distanceNumber=RLYourWayCalvulation.RlGetValueDouble(DISTANCE.toString())?:0.0
                        climbedNumber=RLYourWayCalvulation.RlGetValueInt(CADENCE.toString())?:0
                        speedNumber=RLYourWayCalvulation.RlGetValueDouble(SPEED.toString())?:0.0
                        activeCaloriesNumber=RLYourWayCalvulation.RlGetValueDouble(CALORIES.toString())?:0.0
                        cadenceData=RLYourWayCalvulation.RlGetValueDouble(CADENCE.toString())?:0.0
                        if (!arrCadence.isNullOrEmpty()){
                            maxCadence=RLYourWayCalvulation.RLmax(maxCadence,cadenceData.toInt())
                            avgCadence=arrCadence.average()?:0.00
                        }
                        speedList.add(speedSetValue.toDouble())
                        if (!speedList.isNullOrEmpty()){
                            avgSpeed=speedList.average()?:0.00
                            maxSpeed=RLYourWayCalvulation.RLmax(maxSpeed,speedNumber.roundToInt())
                        }
                    }
                }

            }
        }


    }
    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        val videoUri = Uri.parse(VideoCardData.videoLinkiPhonex)
        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)
        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
           // RLAdjustAspectRatio( fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            mediaPlayer.start()
            fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(fragBinding.videoView.duration))
            handlerprogress.post(RLupdateSeekBarRunnable)
        }
        // Handle errors
        fragBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }
        fragBinding.inlayPlayStop.btnStop.setOnClickListener {
            val videoID=  requireArguments().getString(RLExtraValueKey.videoId,"")
            fragBinding.videoView.stopPlayback()
           viewModel.stopNotifications()
            RLCompleteSessionFragmentOpen(data,videoID,VideoCardData)
        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
                viewModel.pauseNotifications()
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
               viewModel.resumeNotifications()
            }
        }
        fragBinding.relayVideoplay.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.VISIBLE
            }
        }
    }
    private val RLupdateSeekBarRunnable = object : Runnable {
        override fun run() {
            handlerprogress.postDelayed(this, 1000)
            val timeminus=fragBinding.videoView.duration - fragBinding.videoView.currentPosition
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(timeminus))
        }
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
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }

    private fun  RlDataFillAllArray(){
        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distanceNumber
        arrCumDistance.add(RLYourWayCalvulation.noNanValueDouble(CumDistance?:0.00))
        arrCumSpeed.add(RLYourWayCalvulation.noNanValueDouble(CumSpeed?:0.00))
        arrAvgRevPercentage.add(0)
        arrPower.add(0)
        arrPowerFromDevice.add(0)
        arrHr.add(0)
        arrBurntCalories.add(activeCaloriesNumber)
        arrCadence.add(cadenceData)
        arrDistance.add(distanceNumber)
        arrSpeed.add(speedNumber)
        arrRevPercentage.add(0.0)
        arrRevSecond.add(0.0)
        arrMaxRevPercentage.add(0.0)
        burntCalories=burntCalories+activeCaloriesNumber
        distance=CumDistance
        if (!arrBurntCalories.isNullOrEmpty()){
            maxBurntCalories=RLYourWayCalvulation.RLmax(maxBurntCalories,burntCalories.roundToInt())
            avgBurntCalories=arrBurntCalories.average()?:0.00
        }

    }

    private inner class SwipeGestureListenerLeft : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            return if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                    return   true
                } else {
                    return  false
                }
            } else {
                return  false
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

    }
    private fun onSwipeRight() {
        toggleVisibilityleft(true)
    }
    private fun onSwipeLeft() {
        toggleVisibilityleft(false)
    }
    private fun toggleVisibilityleft(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                fragBinding.linearHeart.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        fragBinding.linearHeart.startAnimation(anim)
    }

    //when all data set and new open then this function call
    private fun RLCompleteSessionFragmentOpen(data: String, videoID: String,VideoCardData: RLFulllVideoModel) {
        val bundle: Bundle = Bundle()
        val cardData = RLSessionDataTransferModel()
        val assumedREV=VideoCardData.assumedREV?:"0"
        totalRev=assumedREV.toDouble()

        cardData.VIDEODATA=data
        cardData.classType = RLConstants.BODY
        cardData.SENSOR = RLConstants.SPEED_SENSOR
        cardData.videoID=videoID

        cardData.totalTime=totalTime?:"0"
        cardData.avgRevPercentage=RLYourWayCalvulation.noNanValueDouble(avgRevPercentage?:0.00)
        cardData.burntCalories=RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00)
        cardData.distance=RLYourWayCalvulation.noNanValueDouble(distance?:0.00)
        cardData.maxRevPercentage=RLYourWayCalvulation.noNanValueDouble(maxRevPercentage?:0.00)
        cardData.minRevPercentage=RLYourWayCalvulation.noNanValueDouble(minRevPercentage?:0.00)
        cardData.revPercentage=RLYourWayCalvulation.noNanValueDouble(revPercentage?:0.00)
        cardData.totalRev=RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00)
        cardData.maxSpeed=maxSpeed?:0
        cardData.maxHeartRate=maxHeartrate?:0
        cardData.maxCadence=maxCadence?:0
        cardData.maxBurntCalories=maxBurntCalories?:0
        cardData.minHeartRate=minHeartrate?:0

        cardData.avgBurntCalories=avgBurntCalories?:0.0
        cardData.avgCadence=avgCadence?:0.0
        cardData.avgHr=avgHr?:0
        cardData.avgSpeed=avgSpeed?:0.0

        cardData.maxSpeedForOneKm=RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00)
        cardData.maxSpeedForOneMile=RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00)
        cardData.avgSpeedForOneKm=RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00)
        cardData.avgSpeedForOneMile=RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00)

        cardData.arrBurntCalories=arrBurntCalories
        cardData.arrCadence=arrCadence
        cardData.arrDistance=arrDistance
        cardData.arrHr=arrHr
        cardData.arrPower=arrPower
        cardData.arrPowerFromDevice=arrPowerFromDevice

        cardData.arrRevPercentage=arrRevPercentage
        cardData.arrRevSecond=arrRevSecond
        cardData.arrSpeed=arrSpeed
        cardData.arrCumDistance=arrCumDistance
        cardData.arrCumSpeed=arrCumSpeed

        cardData.arrAvgRevPercentage=arrAvgRevPercentage
        cardData.arrMaxRevPercentage=arrMaxRevPercentage

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

        bundle.putSerializable("cardData",cardData)

//        bundle.putString("VIDEODATA",data)
//        bundle.putString(RLConstants.CLASS_TYPE, RLConstants.BODY)
//        bundle.putString(RLConstants.HEART_SENSOR, RLConstants.SPEED_SENSOR)
//        bundle.putString("videoID",videoID)
//
//        bundle.putString("totalTime",(totalTime?:"0"))
//        bundle.putDouble("avgRevPercentage",RLYourWayCalvulation.noNanValueDouble(avgRevPercentage?:0.00))
//        bundle.putDouble("burntCalories",RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00))
//        bundle.putDouble("distance",RLYourWayCalvulation.noNanValueDouble(distance?:0.00))
//        bundle.putDouble("maxRevPercentage",RLYourWayCalvulation.noNanValueDouble(maxRevPercentage?:0.00))
//        bundle.putDouble("minRevPercentage",RLYourWayCalvulation.noNanValueDouble(minRevPercentage?:0.00))
//        bundle.putDouble("revPercentage",RLYourWayCalvulation.noNanValueDouble(revPercentage?:0.00))
//        bundle.putDouble("totalRev",RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00))
//        bundle.putInt("maxSpeed",maxSpeed?:0)
//        bundle.putInt("maxHeartRate",maxHeartrate?:0)
//        bundle.putInt("maxCadence",maxCadence?:0)
//        bundle.putInt("maxBurntCalories",maxBurntCalories?:0)
//        bundle.putInt("minHeartrate",minHeartrate?:0)
//
//        bundle.putDouble("avgBurntCalories",avgBurntCalories?:0.0)
//        bundle.putDouble("avgCadence",avgCadence?:0.0)
//        bundle.putInt("avgHr",avgHr?:0)
//        bundle.putDouble("avgSpeed",avgSpeed?:0.0)
//
//        bundle.putDouble("maxSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00))
//        bundle.putDouble("maxSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00))
//        bundle.putDouble("avgSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00))
//        bundle.putDouble("avgSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00))
//
//
//
//        bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
//        bundle.putIntegerArrayList(RLYourWayArrayType.arrHr.toString(),ArrayList(arrHr))
//        bundle.putIntegerArrayList(RLYourWayArrayType.arrPower.toString(),ArrayList(arrPower))
//        bundle.putIntegerArrayList(RLYourWayArrayType.arrPowerFromDevice.toString(),ArrayList(arrPowerFromDevice))
//
//
//
//        bundle.putDoubleArray(RLYourWayArrayType.arrRevPercentage.toString(),arrRevPercentage.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrRevSecond.toString(),arrRevSecond.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
//        bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())
//
//        bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgRevPercentage.toString(),ArrayList(arrAvgRevPercentage))
//        bundle.putDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString(),arrMaxRevPercentage.toDoubleArray())

        (context as RLMainActivityRL).RLloadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, true, null, false)
    }

    private fun RLUserDataGet() {
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                appUnit=userData.appUnit
                displayImage=userData.displayImage
                displayName=userData.displayName
                joiningDate=userData.joiningDate
                emailId=userData.emailId
                isBasicDataAdded=userData.isBasicDataAdded
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragBinding.videoView.stopPlayback()
        try {
            viewModel.stopNotifications()
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
        }
    }
}