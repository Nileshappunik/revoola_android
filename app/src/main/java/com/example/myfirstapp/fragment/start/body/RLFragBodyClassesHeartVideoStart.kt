package com.example.myfirstapp.fragment.start.body

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlStartClassesMindBodyWithSensorBinding
import com.example.myfirstapp.fragment.start.adapter.RLStartClassAttendListAdapter
import com.example.myfirstapp.fragment.start.classes.RLFragClassWorkoutComplete
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class RLFragBodyClassesHeartVideoStart : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesHeartVideoStart::class.java.simpleName
    lateinit var fragBinding: RlStartClassesMindBodyWithSensorBinding
    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true

    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter

    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    
    private val binding by lazy {
        RlStartClassesMindBodyWithSensorBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesHeartVideoStart()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_start_classes_mind_body_with_sensor, container) as RlStartClassesMindBodyWithSensorBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClassesHeartVideoStart" )
        RLuisetup()
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)

        fragBinding.circularProgressBar.RLsetProgress(20)
        fragBinding.circularProgressBar.RLsetMaxProgress(100)
        fragBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppZone1Color))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.RLsetStrokeWidth(15f)

        fragBinding.inlayEffort.imgIcon.setImageResource(R.drawable.ic_heart)
        fragBinding.inlayEffort.txtName.setText(R.string.effort)
        fragBinding.inlayEffort.txtNumber.setText("0")

        fragBinding.inlayCalories.imgIcon.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtName.setText(R.string.calories)
        fragBinding.inlayCalories.txtNumber.setText("0")

        fragBinding.inlayHeartrate.imgIcon.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtName.setText(R.string.heartrate)
        fragBinding.inlayHeartrate.txtNumber.setText("0")

        fragBinding.inlayCadence.imgIcon.setImageResource(R.drawable.ic_cadence)
        fragBinding.inlayCadence.txtName.setText(R.string.cadence)
        fragBinding.inlayCadence.txtNumber.setText("--")

        fragBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.inlayTime.txtName.setText(R.string.time)

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutMain
        val adapter = RLStartClassAttendListAdapter(activity)
        fragBinding.recyclerList.adapter = adapter

    }

    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        val videoUri = Uri.parse(VideoCardData.streamingUrl)
        // Set the media controller for the VideoView
        /*val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(fragBinding.videoView)
        fragBinding.videoView.setMediaController(mediaController)*/
        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)

        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
            RLAdjustAspectRatio( fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            mediaPlayer.start()
            fragBinding.layPlayStop.visibility=View.GONE
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(fragBinding.videoView.duration))
        }

        // Handle errors
        fragBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }

        fragBinding.btnStop.setOnClickListener {
            fragBinding.videoView.stopPlayback()
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,RLConstants.BODY)
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, true, null, false)

        }
        fragBinding.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.txtPauseResume.setText("RESUME")
                fragBinding.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.txtPauseResume.setText("PAUSE")
                fragBinding.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
            }
        }
        fragBinding.linearHeart.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.layPlayStop.visibility=View.VISIBLE
            }
        }
        fragBinding.reclayList.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.layPlayStop.visibility=View.VISIBLE
            }
        }
    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        //BODY
        fragBinding.rlBodyTimenumber.visibility=View.VISIBLE
        fragBinding.rlMindTimenumber.visibility=View.GONE
        fragBinding.txtMinutesMind.setText(VideoData.duration+" Class")
        fragBinding.txtVideo.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_easy)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_hard)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.imgVideo.setImageResource(R.drawable.ic_medium)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
    }
    private fun RLstartCountdown() {
        var count = 5
        var countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) { // Countdown from 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                fragBinding.txtCountdown.text = "$count" // Display current count
                count--
            }
            override fun onFinish() {
                fragBinding.relayCountdown.visibility=View.GONE
            }
        }.start()
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
                startActivityForResult(enableBtIntent,
                    REQUEST_ENABLE_BT
                )
            } else {
                RLstartBLEService()
            }
        }

    }
    private fun RLstartBLEService() {
        val intent = Intent(requireContext(),RLBLEService::class.java)
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
            val lastConnectDeviceAddress =
                RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.last_device_connect, "")
            RLhandleDeviceFound(lastConnectDeviceAddress)

        }
        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            Log.d(TAG,"onServiceDisconnected")
        }
    }
    fun RLhandleDeviceFound(deviceAddress: String) {
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
                    fragBinding.inlayHeartrate.txtNumber.setText(data)
                    fragBinding.inlayEffort.txtNumber.setText(data)
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
            Log.e(TAG,"Exception:- "+e.message)
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

}