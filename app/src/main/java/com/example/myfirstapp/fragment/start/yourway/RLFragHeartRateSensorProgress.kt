package com.example.myfirstapp.fragment.start.yourway

import android.content.pm.ActivityInfo
import android.Manifest
import android.animation.ObjectAnimator
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
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.utils.RLPrefManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.myfirstapp.databinding.RlFragHeartrateSensorProgressBinding
import com.example.myfirstapp.services.RLBLEService
import com.example.myfirstapp.services.RLLocationViewModel
import com.example.myfirstapp.utils.RLTimerManager
import com.example.myfirstapp.utils.RLTools
import java.lang.Math.round

class RLFragHeartRateSensorProgress : RLBaseFragment(){
    val TAG: String = RLFragHeartRateSensorProgress::class.java.simpleName
    lateinit var fragBinding: RlFragHeartrateSensorProgressBinding
    private val timerManager = RLTimerManager()
    private var rlbleService: RLBLEService? = null
    private var isServiceBound = false
    private lateinit var handler: Handler
    private lateinit var bluetoothAdapter: BluetoothAdapter
    var  yourWayType:String=""
    companion object {
        private val REQUEST_CODE_BLE_PERMISSIONS = 1
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_PERMISSIONS = 2
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragHeartRateSensorProgress()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragHeartrateSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_heartrate_sensor_progress, container) as RlFragHeartrateSensorProgressBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString("YourWayType").toString().trim()
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        //fragBinding.txtMaintitle.setText(yourWayType)

        fragBinding.relaytiveMain.setBackgroundResource(RLTools.RLgetImage1(yourWayType.toLowerCase()))
        fragBinding.inlayTop.ivTitle.setText(yourWayType)
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setTextColor(resources.getColor(R.color.AppWhiteColor))


        fragBinding.circularProgressBar.RLsetProgress(100)
        fragBinding.circularProgressBar.RLsetMaxProgress(100)
        fragBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppOrangeColor))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.RLsetStrokeWidth(15f)

        fragBinding.inlayCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtProgressTime.setText(R.string.activecalories)
        fragBinding.inlayCalories.txtProgressTimeNumber.setText("0")

        fragBinding.inlayHeartrate.imgTime.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtProgressTime.setText(R.string.heartratebpm)
        fragBinding.inlayHeartrate.layAvg.visibility=View.VISIBLE
        fragBinding.inlayHeartrate.layMax.visibility=View.VISIBLE

        RLwayTypeDesignSet(yourWayType)

        fragBinding.layPause.setOnClickListener {
           try {
            timerManager.RLpause()
            fragBinding.layPause.visibility=View.GONE
            fragBinding.layResumestop.visibility=View.VISIBLE
            if (isServiceBound) {
                rlbleService!!.RLpauseNotifications()
            }
           }catch (e:Exception){
               Log.e(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layResume.setOnClickListener {
           try{
            timerManager.RLresume()
            fragBinding.layPause.visibility=View.VISIBLE
            fragBinding.layResumestop.visibility=View.GONE
            if (isServiceBound) {
                rlbleService!!.RLresumeNotifications()
            }
           }catch (e:Exception){
               Log.e(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
           try{
            timerManager.RLstop()
            if (isServiceBound) {
                rlbleService!!.RLstopNotifications()
            }
           }catch (e:Exception){
               Log.e(TAG,"Exception:- "+e.message)
           }
            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            (context as RLMainActivityRL).RLloadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

        }
    }
    fun  RLwayTypeDesignSet(yourWayType:String){
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
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(R.string.speedkmh)
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE

            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(R.string.paceperkm)
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
            fragBinding.inlayDistance.txtProgressTime.setText(R.string.distancekm)

            fragBinding.inlayClimbed.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.inlayClimbed.txtProgressTime.setText(R.string.climbedft)
            fragBinding.inlayClimbed.txtProgressTimeNumber.setText("0")

            fragBinding.inlaySpeed.imgTime.setImageResource(R.drawable.ic_speeed)
            fragBinding.inlaySpeed.txtProgressTime.setText(R.string.speedkmh)
            fragBinding.inlaySpeed.layAvg.visibility=View.VISIBLE
            fragBinding.inlaySpeed.layMax.visibility=View.VISIBLE

            fragBinding.inlayPace.imgTime.setImageResource(R.drawable.ic_pace)
            fragBinding.inlayPace.txtProgressTime.setText(R.string.paceperkm)
            fragBinding.inlayPace.txtProgressTimeNumber.setText("0")
            fragBinding.inlayPace.layAvg.visibility=View.VISIBLE
            fragBinding.inlayPace.layMax.visibility=View.VISIBLE
            fragBinding.inlayPace.txtMaxNumber.setText("0")
            fragBinding.inlayPace.txtAvgNumber.setText("0")

        }
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
            ActivityCompat.requestPermissions(requireActivity(),permissions.toTypedArray(), REQUEST_CODE_BLE_PERMISSIONS
            )
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN),
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
            if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                RLstepGetToGPS()
            }
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
                    fragBinding.txtEffortNumber.setText(data)
                    fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(data)
                }
            }
        }
    }
    //BLE DEVICE CODE CLOSE
    private fun RLstartCountdown1() {
        var count = 5
        var countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) { // Countdown from 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                fragBinding.countdownText.text = "$count" // Display current count
                count--
            }
            override fun onFinish() {
                fragBinding.countdownText.visibility=View.GONE
                RLtimerMain()
            }
        }.start()
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

    fun RLtimerMain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLformatElapsedTime(elapsedTime))
            }
        }
    }
    private fun RLformatElapsedTime(elapsedTime: Long): String {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = (elapsedTime / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    private fun RLstepGetToGPS() {
        val RLLocationViewModel: RLLocationViewModel =RLLocationViewModel(requireActivity().application)
        RLLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
            speed?.let {
                Log.d(TAG,"Speed: ${it} m/s")
                fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
            }
        })

        RLLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
            stepCount?.let {
                Log.d(TAG,"Steps: $stepCount")
                if (yourWayType.equals("Run")||yourWayType.equals("Walk")){
                    fragBinding.inlayCadence.txtProgressTimeNumber.setText(stepCount.toString())
                }
            }
        })
        RLLocationViewModel.distanceData.observe(viewLifecycleOwner, Observer { distance ->
            distance?.let {
                val totalDistance=it //round(it * 100) / 100
                Log.d(TAG,"Distance: $totalDistance km")
                fragBinding.inlayDistance.txtProgressTimeNumber.setText(totalDistance.toString())
            }
        })

        RLLocationViewModel.averageSpeedData.observe(viewLifecycleOwner, Observer { averageSpeed ->
            averageSpeed?.let {
                val AvgSpeed=round(it * 100) / 100
                Log.d(TAG,"Avg Speed: $AvgSpeed m/s")
                fragBinding.inlaySpeed.txtAvgNumber.setText(AvgSpeed.toString())
            }
        })

        RLLocationViewModel.maxSpeedData.observe(viewLifecycleOwner, Observer { maxSpeed ->
            maxSpeed?.let {
                val maxsSpeed=round(it * 100) / 100
                Log.d(TAG,"Max Speed: $maxsSpeed m/s")
                fragBinding.inlaySpeed.txtMaxNumber.setText(maxsSpeed.toString())
            }
        })

        RLLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
            pace?.let {
                val totalspace=round(it * 100) / 100
                Log.d(TAG, "Pace: $totalspace min/km")
                fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
            }
        })

        RLLocationViewModel.averagePaceData.observe(viewLifecycleOwner, Observer { averagePace ->
            averagePace?.let {
                val avgspace=round(it * 100) / 100
                Log.d(TAG, "Avg Pace: $avgspace min/km")
                fragBinding.inlayPace.txtAvgNumber.setText(avgspace.toString())
            }
        })

        RLLocationViewModel.maxPaceData.observe(viewLifecycleOwner, Observer { maxPace ->
            maxPace?.let {
                val maxxpace=round(it * 100) / 100
                Log.d(TAG, "Max Pace: $maxxpace min/km")
                fragBinding.inlayPace.txtMaxNumber.setText(maxxpace.toString())
            }
        })

        RLLocationViewModel.RLstartLocationUpdates()
    }
    override fun onStart() {
        super.onStart()
       // timerManager.resume()
        RLcheckAndRequestPermissions()
    }
    override fun onResume() {
        super.onResume()
        //timerManager.resume()
    }
    override fun onPause() {
        super.onPause()
        //timerManager.pause()
    }
    override fun onDestroy() {
        super.onDestroy()
        try {
            if (isServiceBound) {
                requireActivity().unbindService(RLserviceConnection)
                isServiceBound = false
            }
            requireActivity().unregisterReceiver(RLbleBroadcastReceiver)
        }catch (e:Exception){
            Log.e(TAG,"Exception:- "+e.message)
        }
    }
}