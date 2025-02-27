package com.revoola.fragment.start.mind

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
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
import android.widget.VideoView
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
import com.revoola.databinding.RlFragMindClassesHeartVideoStartBinding
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
import com.revoola.databasefirebase.RLZoneDataDetails
import com.revoola.databasefirebase.RLZoneDataSummery
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.fragment.start.yourway.RLSessionDataTransferModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class RLFragMindClassesHeartVideoStart : RLBaseFragment() ,DataClient.OnDataChangedListener{
    val TAG: String = RLFragMindClassesHeartVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesHeartVideoStartBinding

    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var gestureDetector: GestureDetectorCompat
    private var totalTime:String =""
    private val timerManager = RLTimerManager()

    var arrHr:MutableList<Int> = mutableListOf()
    var heartRateNumber:Int=0

    var maxHeartrate=0
    var minHeartrate=0
    var avgHr=0

    private var burntCalories =0.0
    private var totalRev  =0.0
    private var revPercentage=0.0

    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit=""



    private val binding by lazy {
        RlFragMindClassesHeartVideoStartBinding.inflate(layoutInflater)
    }

    private val bleRepository by lazy {
        BLERepository(requireContext())
    }

    private val viewModel: BLEViewModel by activityViewModels {
        RLBLEViewModelFactory(bleRepository)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClassesHeartVideoStart()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_heart_video_start, container) as RlFragMindClassesHeartVideoStartBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragMindClassesHeartVideoStart" )
        RLUserDataGet()
        RLuisetup()
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        // Initialize the GestureDetector
        gestureDetector = GestureDetectorCompat(requireContext(), SwipeGestureListener())
        // Set touch listener to the root view
        fragBinding.leftsideview.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        return fragBinding.root
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
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }
    }

    private fun RLuisetup() {
        RLstartCountdown()
        val data=  requireArguments().getString(RLExtraValueKey.videoData,"")
        val audioVideoType=  requireArguments().getString(RLExtraValueKey.audioVideoType,"")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)

        fragBinding.inlayRelaxation.imgIcon.setImageResource(R.drawable.ic_mind_read)
        fragBinding.inlayRelaxation.txtName.setText("RELAXATION")
        fragBinding.inlayRelaxation.txtNumber.setText("0")

        fragBinding.inlayRelaxed.imgIcon.setImageResource(R.drawable.ic_mind_read)
        fragBinding.inlayRelaxed.txtName.setText("RELAXED")
        fragBinding.inlayRelaxed.txtNumber.setText("0%")

        fragBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.inlayTime.txtName.setText(R.string.time)

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
                            heartRateNumber=RLYourWayCalvulation.RlGetValueInt(data.toString())
                            var heartRateSetValue=RLYourWayCalvulation.RlGetValueInt(data.toString())
                            if (heartRateSetValue > 0){
                                maxHeartrate=RLYourWayCalvulation.RLmax(maxHeartrate,heartRateNumber)
                                minHeartrate=RLYourWayCalvulation.RLmin(minHeartrate,heartRateNumber)
                            }

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
            //RLAdjustAspectRatio(fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            fragBinding.seekbarVideo.max=fragBinding.videoView.duration
            mediaPlayer.start()
            fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(fragBinding.videoView.duration))
            handler.post(RLupdateSeekBarRunnable)
        }

        // Handle errors
        fragBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }

        fragBinding.inlayPlayStop.btnStop.setOnClickListener {
            RLSendDataToWearOS(context = requireContext(),
                formattedTime = RLYourWayCalvulation.RLformatElapsedTime(((totalTime.toInt())*1000).toLong()),
                calories = burntCalories,
                total_Rev = totalRev,
                REVPer = revPercentage,
                buttonType = "STOP")
            fragBinding.videoView.stopPlayback()
            viewModel.stopNotifications()
            timerManager.RLstop()
            Wearable.getDataClient(requireContext()).removeListener(this)
            val videoID=  requireArguments().getString(RLExtraValueKey.videoId,"")
            val bundle = Bundle()

            val cardData = RLSessionDataTransferModel()
            cardData.VIDEODATA = data
            cardData.classType = RLConstants.MIND
            cardData.SENSOR = RLConstants.HEART_SENSOR
            cardData.arrHr = arrHr
            cardData.totalTime = totalTime
            cardData.videoID = videoID
            cardData.avgHr = avgHr
            cardData.maxHeartRate = maxHeartrate
            cardData.minHeartRate = minHeartrate
            cardData.rms = 0.0

            cardData.wsWeight = wsWeight
            cardData.wsHeight=wsHeight
            cardData.wsAge=wsAge
            cardData.gender=gender
            cardData.RFMHR=RFMHR
            cardData.RestingHR=RestingHR
            cardData.appUnit=appUnit

            bundle.putSerializable("cardData",cardData)

            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASS_TYPE,RLConstants.MIND)
            bundle.putString(RLConstants.HEART_SENSOR, RLConstants.HEART_SENSOR)

            bundle.putIntegerArrayList(RLYourWayArrayType.arrHr.toString(),ArrayList(arrHr))
            bundle.putString("totalTime",totalTime)
            bundle.putString("videoID",videoID)
            bundle.putInt("avgHr",avgHr)
            bundle.putInt("maxHr",maxHeartrate)
            bundle.putInt("minHr",minHeartrate)
            bundle.putDouble("rms",0.0)
            (context as RLMainActivityRL).RLloadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, true, null, false)

        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.RLformatElapsedTime(((totalTime.toInt())*1000).toLong()),
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = "PAUSE")
                viewModel.pauseNotifications()
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)

            }else{
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.RLformatElapsedTime(((totalTime.toInt())*1000).toLong()),
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = "RESUME")
                viewModel.resumeNotifications()
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)

            }
        }

        fragBinding.relayVideoplay.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.relayProgress.visibility=View.GONE
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.relayProgress.visibility=View.VISIBLE
                fragBinding.inlayPlayStop.layPlayStop.visibility=View.VISIBLE
            }
        }
    }

    private val RLupdateSeekBarRunnable = object : Runnable {
        override fun run() {
            fragBinding.seekbarVideo.progress = fragBinding.videoView.currentPosition
            handler.postDelayed(this, 1000)
            //fragBinding.txtVideoTime.setText(RLformatTime(fragBinding.videoView.currentPosition))
            val timeminus=fragBinding.videoView.duration - fragBinding.videoView.currentPosition
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(timeminus))
            fragBinding.txtVideoTime.setText(RLformatTime(timeminus))
        }
    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.inlayCountdown.txtTitle.setText(VideoData.rideTitle)
        fragBinding.inlayCountdown.txtNamewith.setText(VideoData.instructor)
        fragBinding.inlayCountdown.txtMinutesMind.setText(VideoData.duration)
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
                RLSendDataToWearOS(context = requireContext(),
                    formattedTime = RLYourWayCalvulation.RLformatElapsedTime(elapsedTime) ,
                    calories = burntCalories,
                    total_Rev = totalRev,
                    REVPer = revPercentage,
                    buttonType = "START")
                RlDataFillAllArray()

            }
        }
    }

    private fun  RlDataFillAllArray(){
        arrHr.add(heartRateNumber)
        avgHr = arrHr.average().roundToInt()?:0
        val currentCalories=RLYourWayCalvulation.calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble(),RestingHR,RFMHR)
        burntCalories=burntCalories+currentCalories

        val REVPer=RLYourWayCalvulation.calculateREVPer(heartRateNumber,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender,RestingHR,RFMHR) //only REV
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        totalRev = totalRev+ REVSec
        revPercentage=REVPer
    }
    //SWIPE LEFT RIGHT WITH ANIMATION SET
    private fun RLAdjustAspectRatio(videoView: VideoView, videoWidth: Int, videoHeight: Int) {
        val layoutParams = videoView.layoutParams
        val viewWidth = videoView.width.toFloat()
        val viewHeight = videoView.height.toFloat()
        val aspectRatio = videoWidth.toFloat() / videoHeight

        if (viewWidth / viewHeight > aspectRatio) {
            // Adjust height to fit the width
            layoutParams.height = (viewWidth / aspectRatio).toInt()
            layoutParams.width = viewWidth.toInt()
        } else {
            // Adjust width to fit the height
            layoutParams.width = (viewHeight * aspectRatio).toInt()
            layoutParams.height = viewHeight.toInt()
        }

        videoView.layoutParams = layoutParams
    }
    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
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
        /*fragBinding.inlayTime.relaySensorProgress.visibility=View.VISIBLE
        fragBinding.inlayRelaxed.relaySensorProgress.visibility=View.VISIBLE
        fragBinding.inlayRelaxation.relaySensorProgress.visibility=View.VISIBLE*/
        toggleVisibility(true)
    }
    private fun onSwipeLeft() {
        /*fragBinding.inlayTime.relaySensorProgress.visibility=View.GONE
        fragBinding.inlayRelaxed.relaySensorProgress.visibility=View.GONE
        fragBinding.inlayRelaxation.relaySensorProgress.visibility=View.GONE*/
        toggleVisibility(false)
    }
    private fun toggleVisibility(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                fragBinding.animatedview.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        fragBinding.animatedview.startAnimation(anim)
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            timerManager.RLstop()
            fragBinding.videoView.stopPlayback()
            Wearable.getDataClient(requireContext()).removeListener(this)
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
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
                                   REVPer: Double, buttonType: String) {
        // Create a PutDataMapRequest with a unique path
        val putDataMapRequest = PutDataMapRequest.create("/mobile_to_wear")
        val dataMap = putDataMapRequest.dataMap

        // Put your data into the DataMap using distinct keys
        dataMap.putString("formattedTime", formattedTime)
        dataMap.putDouble("calories", calories)
        dataMap.putDouble("total_Rev", total_Rev)
        dataMap.putDouble("REVPer", REVPer)
        dataMap.putString("buttonType", buttonType)
        dataMap.putString("SessionName", "Mind")

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
                    heartRateNumber=RLYourWayCalvulation.RlGetValueInt(data.toString())
                    var heartRateSetValue=RLYourWayCalvulation.RlGetValueInt(data.toString())
                    if (heartRateSetValue > 0){
                        maxHeartrate=RLYourWayCalvulation.RLmax(maxHeartrate,heartRateNumber)
                        minHeartrate=RLYourWayCalvulation.RLmin(minHeartrate,heartRateNumber)
                    }
                }
            }
        }
    }



}