package com.example.myfirstapp.fragment.start.mind

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragMindClassesHeartVideoStartBinding
import com.example.myfirstapp.enumclass.RLYourWayArrayType
import com.example.myfirstapp.fragment.start.adapter.RLStartClassAttendListAdapter
import com.example.myfirstapp.fragment.start.body.RLFragBodyClassesHeartVideoStart
import com.example.myfirstapp.fragment.start.body.RLFragBodyClassesHeartVideoStart.Companion
import com.example.myfirstapp.fragment.start.classes.RLFragClassWorkoutComplete
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTimerManager
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class RLFragMindClassesHeartVideoStart : RLBaseFragment() {
    val TAG: String = RLFragMindClassesHeartVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesHeartVideoStartBinding

    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var bluetoothAdapter: BluetoothAdapter

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

    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }

    private val binding by lazy {
        RlFragMindClassesHeartVideoStartBinding.inflate(layoutInflater)
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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClassesHeartVideoStart" )
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
        val data=  requireArguments().getString("VIDEODATA","")
        val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
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
            fragBinding.videoView.stopPlayback()
            if (isServiceBound) {
                rlbleService!!.RLstopNotifications()
            }
            val videoID=  requireArguments().getString("videoID","")
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,RLConstants.MIND)
            bundle.putString(RLConstants.HEARTSENSOR, RLConstants.HEARTSENSOR)

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
                RlDataFillAllArray()
            }
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
    private fun  RlDataFillAllArray(){
        arrHr.add(heartRateNumber)
        avgHr = arrHr.average().roundToInt()?:0
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

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
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
                startActivityForResult(enableBtIntent,
                    REQUEST_ENABLE_BT
                )
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
            Log.d(TAG,"onServiceConnected")
            val binder = service as RLBLEService.RLLocalBinder
            rlbleService = binder.getService()
            // Check if devices are not connected then scan
            isServiceBound = true
            val lastConnectDeviceAddress = RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect, "")
            RLhandleDeviceFound(lastConnectDeviceAddress)

        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            Log.d(TAG,"onServiceDisconnected")
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
                    var heartRateSetValue=RlGetValueInt(data.toString())
                    if (heartRateSetValue > 0){
                        maxHeartrate=RLmax(maxHeartrate,heartRateNumber)
                        minHeartrate=RLmin(minHeartrate,heartRateNumber)
                    }
                }
            }
        }
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
            Log.e(TAG,"Exception:- "+e.message)
        }
    }




}