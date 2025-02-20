package com.revoola.fragment.start.body

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragBodyClassesHeartVideoStartBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.fragment.start.classes.RLFragClassWorkoutComplete
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.google.gson.Gson
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.services.RLBLEManagerHeartRate
import com.revoola.services.RLSwipeGestureDetector
import com.revoola.utils.RLPrefManager
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class RLFragBodyClassesHeartVideoStart : RLBaseFragment() {
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

    private val binding by lazy {
        RlFragBodyClassesHeartVideoStartBinding.inflate(layoutInflater)
    }
    private val bleManager by lazy { RLBLEManagerHeartRate(requireContext()) }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesHeartVideoStart()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_body_classes_heart_video_start, container) as RlFragBodyClassesHeartVideoStartBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
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
            fragBinding.videoView.stopPlayback()
            bleManager.lrstopgetData()
            RLCompleteSessionFragmentOpen(data,videoID)
        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
                bleManager.rlpausegetData()
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
                bleManager.rlresumegetData()
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
        bundle.putString("VIDEODATA",data)
        bundle.putString(RLConstants.CLASS_TYPE, RLConstants.BODY)
        bundle.putString(RLConstants.HEART_SENSOR, RLConstants.HEART_SENSOR)
        bundle.putString("videoID",videoID)

        bundle.putString("totalTime",(totalTime?:"0"))
        bundle.putDouble("avgRevPercentage",RLYourWayCalvulation.noNanValueDouble(avgRevPercentage?:0.00))
        bundle.putDouble("burntCalories",RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00))
        bundle.putDouble("distance",RLYourWayCalvulation.noNanValueDouble(distance?:0.00))
        bundle.putDouble("maxRevPercentage",RLYourWayCalvulation.noNanValueDouble(maxRevPercentage?:0.00))
        bundle.putDouble("minRevPercentage",RLYourWayCalvulation.noNanValueDouble(minRevPercentage?:0.00))
        bundle.putDouble("revPercentage",RLYourWayCalvulation.noNanValueDouble(revPercentage?:0.00))
        bundle.putDouble("totalRev",RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00))
        bundle.putInt("maxSpeed",maxSpeed?:0)
        bundle.putInt("maxHeartRate",maxHeartrate?:0)
        bundle.putInt("maxCadence",maxCadence?:0)
        bundle.putInt("maxBurntCalories",maxBurntCalories?:0)
        bundle.putInt("minHeartrate",minHeartrate?:0)

        bundle.putDouble("avgBurntCalories",avgBurntCalories?:0.0)
        bundle.putDouble("avgCadence",avgCadence?:0.0)
        bundle.putInt("avgHr",avgHr?:0)
        bundle.putDouble("avgSpeed",avgSpeed?:0.0)

        bundle.putDouble("maxSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00))
        bundle.putDouble("maxSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00))
        bundle.putDouble("avgSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00))
        bundle.putDouble("avgSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00))



        bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
        bundle.putIntegerArrayList(RLYourWayArrayType.arrHr.toString(),ArrayList(arrHr))
        bundle.putIntegerArrayList(RLYourWayArrayType.arrPower.toString(),ArrayList(arrPower))
        bundle.putIntegerArrayList(RLYourWayArrayType.arrPowerFromDevice.toString(),ArrayList(arrPowerFromDevice))



        bundle.putDoubleArray(RLYourWayArrayType.arrRevPercentage.toString(),arrRevPercentage.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrRevSecond.toString(),arrRevSecond.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
        bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())

        bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgRevPercentage.toString(),ArrayList(arrAvgRevPercentage))
        bundle.putDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString(),arrMaxRevPercentage.toDoubleArray())

        (context as RLMainActivityRL).RLloadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, true, null, false)
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
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                totalTime=(elapsedTime/1000).toString()
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
            leftFragment?.RLUpdateHRPersentage(REVPer.roundToInt())
        }


        arrRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(REVPer))
        avgRevPercentage = RLYourWayCalvulation.avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(RLYourWayCalvulation.noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        maxRevPercentage=RLYourWayCalvulation.RLmax(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLYourWayCalvulation.RLmin(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        if (rightFragment!=null){
            rightFragment?.RLRankingByRevSec(totalTime.toInt(),REVSec.roundToInt(),totalRev.roundToInt(),REVPer.roundToInt(),maxRevPercentage.roundToInt(),avgRevPercentage.roundToInt(),heartRateNumber)
        }

        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLYourWayCalvulation.RLmax(maxBurntCalories,burntCalories.toInt())

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

    }

    override fun onStart() {
        super.onStart()
        // timerManager.resume()
        if (bleManager.checkAndRequestPermissions(requireActivity())) {
            bleManager.setupBluetooth {
                bleManager.startBLEService()
            }
        }
        bleManager.setCallback(object : RLBLEManagerHeartRate.BLECallback {
            override fun onHeartRateDataReceived(data: String) {
                Log.d("BLE", "Heart Rate: $heartRate")
                heartRateNumber=RLYourWayCalvulation.RlGetValueInt(data.toString())
                val heartRateSetValue=RLYourWayCalvulation.RlGetValueInt(data.toString())
                if (heartRateSetValue > 0){
                    if (leftFragment!=null){
                        leftFragment?.RLUpdateHRTime(data.toString(),burntCalories.roundToInt().toString()?:"0",totalRev?:0.00)
                    }
                    maxHeartrate=RLYourWayCalvulation.RLmax(maxHeartrate,heartRateNumber)
                    minHeartrate=RLYourWayCalvulation.RLmin(minHeartrate,heartRateNumber)
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

    override fun onDestroy() {
        super.onDestroy()
        bleManager.cleanup()
        fragBinding.videoView.stopPlayback()
        try {
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