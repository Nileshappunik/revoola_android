package com.revoola.fragment.start.body

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragBodyClassesHeartVideoStartBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.fragment.start.classes.RLFragClassWorkoutComplete
import com.revoola.model.RLFulllVideoModel
import com.revoola.services.RLBLEService
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.google.gson.Gson
import com.revoola.utils.RLPrefManager
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class RLFragBodyClassesHeartVideoStart : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesHeartVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragBodyClassesHeartVideoStartBinding
    var leftFragment: RLFragLeftBodyWithHeartVideo? =null
    var rightFragment: RLFragRightBodyWithHeartVideo? =null

    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter

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

    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }

    private val binding by lazy {
            RlFragBodyClassesHeartVideoStartBinding.inflate(layoutInflater)
    }

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
        RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragStart" )
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
        gestureDetectorleft = GestureDetectorCompat(requireContext(), SwipeGestureListenerLeft())
        gestureDetectorright = GestureDetectorCompat(requireContext(), SwipeGestureListenerRight())

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
        val data=  requireArguments().getString("VIDEODATA","")
        val videoID=  requireArguments().getString("videoID","")

       parentFragmentManager.beginTransaction()
            .add(R.id.frame_left, RLFragLeftBodyWithHeartVideo())
            .commit()
        val bundleright=Bundle()
        bundleright.putString("videoID",videoID)
        parentFragmentManager.beginTransaction()
            .add(R.id.frame_right, RLFragRightBodyWithHeartVideo().newInstance(bundleright))
            .commit()

        RLstartCountdown()


        ride=  requireArguments().getBoolean("Ride")
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
            if (isServiceBound) {
                rlbleService!!.RLstopNotifications()
            }
            RLCompleteSessionFragmentOpen(data,videoID)
        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
                if (isServiceBound) {
                    rlbleService!!.RLpauseNotifications()
                }
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
                if (isServiceBound) {
                    rlbleService!!.RLresumeNotifications()
                }
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
        bundle.putString(RLConstants.CLASSTYPE, RLConstants.BODY)
        bundle.putString(RLConstants.HEARTSENSOR, RLConstants.HEARTSENSOR)
        bundle.putString("videoID",videoID)

        bundle.putString("totalTime",(totalTime?:"0"))
        bundle.putDouble("avgRevPercentage",noNanValueDouble(avgRevPercentage?:0.00))
        bundle.putDouble("burntCalories",noNanValueDouble(burntCalories?:0.00))
        bundle.putDouble("distance",noNanValueDouble(distance?:0.00))
        bundle.putDouble("maxRevPercentage",noNanValueDouble(maxRevPercentage?:0.00))
        bundle.putDouble("minRevPercentage",noNanValueDouble(minRevPercentage?:0.00))
        bundle.putDouble("revPercentage",noNanValueDouble(revPercentage?:0.00))
        bundle.putDouble("totalRev",noNanValueDouble(totalRev?:0.00))
        bundle.putInt("maxSpeed",maxSpeed?:0)
        bundle.putInt("maxHeartRate",maxHeartrate?:0)
        bundle.putInt("maxCadence",maxCadence?:0)
        bundle.putInt("maxBurntCalories",maxBurntCalories?:0)
        bundle.putInt("minHeartrate",minHeartrate?:0)

        bundle.putDouble("avgBurntCalories",avgBurntCalories?:0.0)
        bundle.putDouble("avgCadence",avgCadence?:0.0)
        bundle.putInt("avgHr",avgHr?:0)
        bundle.putDouble("avgSpeed",avgSpeed?:0.0)

        bundle.putDouble("maxSpeedForOneKm",noNanValueDouble(maxSpeedForOneKm?:0.00))
        bundle.putDouble("maxSpeedForOneMile",noNanValueDouble(maxSpeedForOneMile?:0.00))
        bundle.putDouble("avgSpeedForOneKm",noNanValueDouble(avgSpeedForOneKm?:0.00))
        bundle.putDouble("avgSpeedForOneMile",noNanValueDouble(avgSpeedForOneMile?:0.00))



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

    //Animation set left Right Swipe
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
                        onSwipeRight(true)
                    } else {
                        onSwipeLeft(true)
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
    private inner class SwipeGestureListenerRight : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y
            return if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight(false)
                    } else {
                        onSwipeLeft(false)
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
    private fun onSwipeRight(isLeftSideClick:Boolean) {
        if (isLeftSideClick){
            toggleVisibilityleft(true)
        }else{
            toggleVisibilityright(false)
        }
    }
    private fun onSwipeLeft(isLeftSideClick:Boolean) {
        if (isLeftSideClick){
            toggleVisibilityleft(false)
        }else{
            toggleVisibilityright(true)
        }
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
                fragBinding.frameLeft.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        fragBinding.frameLeft.startAnimation(anim)
    }
    private fun toggleVisibilityright(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                fragBinding.frameRight.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        fragBinding.frameRight.startAnimation(anim)
    }

    private fun  RlDataFillAllArray(){
        val currentCalories=calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble())
        if (heartRateNumber>0){
            heartRate=heartRateNumber
        }
        val REVPer=calculateREVPer(heartRate,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender) //only REV
        if (leftFragment!=null){
            leftFragment?.RLUpdateHRPersentage(REVPer.roundToInt())
        }


        arrRevPercentage.add(noNanValueDouble(REVPer))
        avgRevPercentage = avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        maxRevPercentage=RLmax(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLmin(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        if (rightFragment!=null){
            rightFragment?.RLRankingByRevSec(totalTime.toInt(),REVSec.roundToInt(),totalRev.roundToInt(),REVPer.roundToInt(),maxRevPercentage.roundToInt(),avgRevPercentage.roundToInt(),heartRateNumber)
        }

        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLmax(maxBurntCalories,burntCalories.toInt())

        revPercentage=REVPer

        arrBurntCalories.add( noNanValueDouble(currentCalories))
        arrCadence.add(noNanValueDouble(cadenceData))
        arrDistance.add( noNanValueDouble(distance))
        arrHr.add(heartRateNumber)
        arrSpeed.add(noNanValueDouble(speedNumber))

        avgHr = arrHr.average().roundToInt()?:0
        avgBurntCalories =  arrBurntCalories.average()?:0.0

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distance
        arrCumDistance.add(noNanValueDouble(CumDistance))
        arrCumSpeed.add(noNanValueDouble(CumSpeed))
        distance=CumDistance

        arrAvgRevPercentage.add(avgRevPercentage.roundToInt())
        arrMaxRevPercentage.add(noNanValueDouble(maxRevPercentage))
        arrPower.add(0)
        arrPowerFromDevice.add(0)

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

    private fun noNanValueDouble(value:Double):Double{
        if (value.isNaN()){
            return 0.00
        }else{
            return  value
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
        } else if(value.toDouble() < 0) {
            return 0.0
        }else{
            return value.toDouble()
        }
    }
    ///////////////////////////// calculate All Value End ////////////////////////

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
            ActivityCompat.requestPermissions(requireActivity(),permissions.toTypedArray(), REQUEST_CODE_BLE_PERMISSIONS)
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN),
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
                        if (leftFragment!=null){
                            leftFragment?.RLUpdateHRTime(data.toString(),burntCalories.roundToInt().toString()?:"0",totalRev?:0.00)
                        }
                        maxHeartrate=RLmax(maxHeartrate,heartRateNumber)
                        minHeartrate=RLmin(minHeartrate,heartRateNumber)
                    }
                }
            }
        }
    }
    //BLE DEVICE CODE CLOSE

    override fun onStart() {
        super.onStart()
        // timerManager.resume()
        RLcheckAndRequestPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragBinding.videoView.stopPlayback()
        try {
            if (isServiceBound) {
                requireActivity().unbindService(RLserviceConnection)
                isServiceBound = false
            }
            requireActivity().unregisterReceiver(RLbleBroadcastReceiver)
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