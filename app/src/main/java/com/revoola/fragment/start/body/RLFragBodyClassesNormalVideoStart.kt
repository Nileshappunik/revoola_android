package com.revoola.fragment.start.body

import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragBodyClassesNormalVideoStartBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.fragment.start.classes.RLFragClassWorkoutComplete
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.google.gson.Gson
import com.revoola.ble.RLExtraValueKey
import java.util.concurrent.TimeUnit

class RLFragBodyClassesNormalVideoStart : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesNormalVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragBodyClassesNormalVideoStartBinding
    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var gestureDetector: GestureDetectorCompat

    private var totalTime:String =""
    private val timerManager = RLTimerManager()

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
        RlFragBodyClassesNormalVideoStartBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesNormalVideoStart()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_body_classes_normal_video_start, container) as RlFragBodyClassesNormalVideoStartBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragBodyClassesNormalVideoStart" )
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
    private fun RLuisetup() {
        RLstartCountdown()
        val data=  requireArguments().getString(RLExtraValueKey.videoData,"")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)


    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        fragBinding.inlayTime.progressView2.visibility=View.GONE
        val videoUri = Uri.parse(VideoCardData.videoLinkiPhonex)

        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)

        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
           // RLAdjustAspectRatio( fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
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
            val videoID=  requireArguments().getString(RLExtraValueKey.videoId,"")
            fragBinding.videoView.stopPlayback()
            RLCompleteSessionFragmentOpen(data,videoID,VideoCardData)

        }
        fragBinding.inlayPlayStop.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.inlayPlayStop.txtPauseResume.setText("RESUME")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.inlayPlayStop.txtPauseResume.setText("PAUSE")
                fragBinding.inlayPlayStop.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
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
            handler.postDelayed(this, 1000)
            val timeminus=fragBinding.videoView.duration - fragBinding.videoView.currentPosition
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(timeminus))
        }
    }
    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.inlayCountdown.txtTitle.setText(VideoData.rideTitle)
        fragBinding.inlayCountdown.txtNamewith.setText(VideoData.instructor)
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
        arrAvgRevPercentage.add(0)
        arrPower.add(0)
        arrPowerFromDevice.add(0)
        arrHr.add(0)
        arrBurntCalories.add(0.0)
        arrCadence.add(0.0)
        arrCumDistance.add(0.0)
        arrCumSpeed.add(0.0)
        arrDistance.add(0.0)
        arrSpeed.add(0.0)
        arrRevPercentage.add(0.0)
        arrRevSecond.add(0.0)
        arrMaxRevPercentage.add(0.0)
    }

    private fun noNanValueDouble(value:Double):Double{
        if (value.isNaN()){
            return 0.00
        }else{
            return  value
        }
    }

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
    override fun onDestroyView() {
        super.onDestroyView()
        fragBinding.videoView.stopPlayback()
        // Show the status bar and navigation bar again and set dark color
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
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
                        toggleVisibilityonSimple(true)
                    } else {
                        toggleVisibilityonSimple(false)
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

    private fun toggleVisibilityonSimple(visible: Boolean) {
        val anim: Animation = if (visible) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
        }
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                fragBinding.inlayTime.relaySensorProgress.visibility = if (visible) View.VISIBLE else View.GONE
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        fragBinding.inlayTime.relaySensorProgress.startAnimation(anim)
    }

    //when all data set and new open then this function call
    private fun RLCompleteSessionFragmentOpen(data: String, videoID: String,VideoCardData: RLFulllVideoModel) {
        val bundle: Bundle = Bundle()
        val assumedREV=VideoCardData.assumedREV?:"0"
        totalRev=assumedREV.toDouble()
        bundle.putString("VIDEODATA",data)
        bundle.putString(RLConstants.CLASS_TYPE, RLConstants.BODY)
        bundle.putString(RLConstants.HEART_SENSOR, RLConstants.NO_SENSOR)
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



}