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
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLForgotPasswordActivityRL
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.ActivityRlbodyClassesHeartVideoStartBinding
import com.example.myfirstapp.databinding.RlActivityForgotPasswordBinding
import com.example.myfirstapp.fragment.start.adapter.RLStartClassAttendListAdapter
import com.example.myfirstapp.fragment.start.classes.RLFragClassWorkoutComplete
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class RLActivityBodyClassesHeartVideoStart : RLBaseActivity() {
    val TAG: String = RLActivityBodyClassesHeartVideoStart::class.java.simpleName
    lateinit var activityBinding: ActivityRlbodyClassesHeartVideoStartBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        activityBinding = RLinflateBindLayout(this, R.layout.activity_rlbody_classes_heart_video_start) as ActivityRlbodyClassesHeartVideoStartBinding
        RLUisetup()
    }
    private fun RLUisetup() {
        RLstartCountdown()
        val data=intent.getStringExtra("VIDEODATA").toString()
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)

        activityBinding.circularProgressBar.RLsetProgress(20)
        activityBinding.circularProgressBar.RLsetMaxProgress(100)
        activityBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppZone1Color))
        activityBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        activityBinding.circularProgressBar.RLsetStrokeWidth(15f)

        activityBinding.inlayEffort.imgIcon.setImageResource(R.drawable.ic_heart)
        activityBinding.inlayEffort.txtName.setText(R.string.effort)
        activityBinding.inlayEffort.txtNumber.setText("0")

        activityBinding.inlayCalories.imgIcon.setImageResource(R.drawable.fd_calories_green)
        activityBinding.inlayCalories.txtName.setText(R.string.calories)
        activityBinding.inlayCalories.txtNumber.setText("0")

        activityBinding.inlayHeartrate.imgIcon.setImageResource(R.drawable.ic_heartrate)
        activityBinding.inlayHeartrate.txtName.setText(R.string.heartrate)
        activityBinding.inlayHeartrate.txtNumber.setText("0")

        activityBinding.inlayCadence.imgIcon.setImageResource(R.drawable.ic_cadence)
        activityBinding.inlayCadence.txtName.setText(R.string.cadence)
        activityBinding.inlayCadence.txtNumber.setText("--")

        activityBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        activityBinding.inlayTime.txtName.setText(R.string.time)

        val linearLayoutMain = LinearLayoutManager(this)
        activityBinding.recyclerList.layoutManager = linearLayoutMain
        val adapter = RLStartClassAttendListAdapter(this)
        activityBinding.recyclerList.adapter = adapter

    }

    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        val videoUri = Uri.parse(VideoCardData.streamingUrl)
        // Set the media controller for the VideoView
        /*val mediaController = MediaController(this)
        mediaController.setAnchorView(activityBinding.videoView)
        activityBinding.videoView.setMediaController(mediaController)*/
        // Set the URI for the VideoView
        activityBinding.videoView.setVideoURI(videoUri)

        // Start playing the video
        activityBinding.videoView.setOnPreparedListener { mediaPlayer ->
            RLAdjustAspectRatio( activityBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            mediaPlayer.start()
            activityBinding.layPlayStop.visibility=View.GONE
            activityBinding.inlayTime.txtNumber.setText(RLformatTime(activityBinding.videoView.duration))
        }

        // Handle errors
        activityBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }

        activityBinding.btnStop.setOnClickListener {
            activityBinding.videoView.stopPlayback()
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE, RLConstants.BODY)
        }
        activityBinding.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                activityBinding.videoView.pause()
                pauseVideo=false
                activityBinding.txtPauseResume.setText("RESUME")
                activityBinding.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
            }else{
                pauseVideo=true
                activityBinding.videoView.start()
                activityBinding.txtPauseResume.setText("PAUSE")
                activityBinding.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
            }
        }
        activityBinding.linearHeart.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                activityBinding.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                activityBinding.layPlayStop.visibility=View.VISIBLE
            }
        }
        activityBinding.reclayList.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                activityBinding.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                activityBinding.layPlayStop.visibility=View.VISIBLE
            }
        }
    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        activityBinding.inlayCountdown.txtTitle.setText(VideoData.rideTitle)
        activityBinding.inlayCountdown.txtNamewith.setText(VideoData.instructor)
        //BODY
        activityBinding.inlayCountdown.rlBodyTimenumber.visibility=View.VISIBLE
        activityBinding.inlayCountdown.rlMindTimenumber.visibility=View.GONE
        activityBinding.inlayCountdown.txtMinutesMind.setText(VideoData.duration+" Class")
        activityBinding.inlayCountdown.txtVideo.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            activityBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_easy)
            activityBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            activityBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_hard)
            activityBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            activityBinding.inlayCountdown.imgVideo.setImageResource(R.drawable.ic_medium)
            activityBinding.inlayCountdown.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
    }
    private fun RLstartCountdown() {
        var count = 5
        var countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) { // Countdown from 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                activityBinding.inlayCountdown.txtCountdown.text = "$count" // Display current count
                count--
            }
            override fun onFinish() {
                activityBinding.inlayCountdown.relayCountdown.visibility=View.GONE
            }
        }.start()
    }

    //BLE DEVICE CODE START
    private fun RLcheckAndRequestPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,permissions.toTypedArray(),
                REQUEST_CODE_BLE_PERMISSIONS
            )
        } else {
            RLsetupBlutooth()
        }
    }
    private fun RLsetupBlutooth() {
        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        handler = Handler(Looper.getMainLooper())
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
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
        val intent = Intent(this,RLBLEService::class.java)
        this.bindService(intent, RLserviceConnection, Context.BIND_AUTO_CREATE)

        val filter = IntentFilter().apply {
            addAction("ACTION_DATA_RETRIEVED_HEART")
        }
        this.registerReceiver(RLbleBroadcastReceiver, filter)

    }
    private val RLserviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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
       /* val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        if (device != null) {
            rlbleService!!.RLconnectToDevice(device)
        }*/
    }
    private val RLbleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "ACTION_DATA_RETRIEVED_HEART" -> {
                    val data = intent.getStringExtra("EXTRA_DATA")
                    activityBinding.inlayHeartrate.txtNumber.setText(data)
                    activityBinding.inlayEffort.txtNumber.setText(data)
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
        activityBinding.videoView.stopPlayback()
        try {
            if (isServiceBound) {
                this.unbindService(RLserviceConnection)
                isServiceBound = false
            }
            this.unregisterReceiver(RLbleBroadcastReceiver)
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

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