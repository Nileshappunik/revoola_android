package com.revoola.fragment.start.yourway

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import android.util.Log
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import androidx.lifecycle.Observer
import com.revoola.ble.RLExtraValueKey
import com.revoola.databinding.RlFragHeartrateSensorProgressBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.services.RLLocationViewModel
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTimerManager
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.services.RLBLEManagerHeartRate
import com.revoola.utils.RLPrefManager
import java.lang.Math.round
import kotlin.math.roundToInt

class RLFragHeartRateSensorProgress : RLBaseFragment(){
    val TAG: String = RLFragHeartRateSensorProgress::class.java.simpleName
    lateinit var fragBinding: RlFragHeartrateSensorProgressBinding
    private val timerManager = RLTimerManager()

    private lateinit var rlLocationViewModel: RLLocationViewModel
    private var  yourWayType:String=""

    private var arrBurntCalories:MutableList<Double> = mutableListOf()
    private var arrCadence:MutableList<Double> = mutableListOf()
    private var arrDistance:MutableList<Double> = mutableListOf()
    private var arrElevation:MutableList<Double> = mutableListOf()
    private var arrHRRecordedSecond:MutableList<Int> = mutableListOf()
    private var arrHr:MutableList<Int> = mutableListOf()
    private var arrRevPercentage:MutableList<Double> = mutableListOf()
    private var arrRevSecond:MutableList<Double> = mutableListOf()
    private var arrSpeed:MutableList<Double> = mutableListOf()
    private var arrCumDistance:MutableList<Double> = mutableListOf()
    private var arrCumElevation:MutableList<Double> = mutableListOf()
    private var arrCumSpeed:MutableList<Double> = mutableListOf()
    private var arrConnection:MutableList<Boolean> = mutableListOf()

    private var arrAvgCadence:MutableList<Double> = mutableListOf()
    private var arrAvgHr:MutableList<Int> = mutableListOf()
    private var arrAvgRevPercentage:MutableList<Int> = mutableListOf()
    private var arrMaxCadence:MutableList<Int> = mutableListOf()
    private var arrMaxHr:MutableList<Int> = mutableListOf()
    private var arrMaxRevPercentage:MutableList<Double> = mutableListOf()

    private var speedList:MutableList<Double> = mutableListOf()
    private var paceList:MutableList<Int> = mutableListOf()

    private var arrSpeedForOneKm:MutableList<Double> = mutableListOf()
    private var arrSpeedForOneMile:MutableList<Double> = mutableListOf()

    private var avgSpeedForOneKm:Double=0.0
    private var avgSpeedForOneMile:Double=0.0

    private var maxSpeedForOneKm:Double=0.0
    private var maxSpeedForOneMile:Double=0.0

    private var heartRateNumber:Int=0
    private var heartRate:Int=110
    private var stepsNumber:Int=0
    private var distance:Double=0.0
    private var elevationMeter:Double=0.0
    private var speedNumber:Double=0.0
    private var paceNumber:Int=0
    private var totalTime:String ="0"
    private var avgRevPercentage=0.0
    private var revPercentage=0.0
    private var totalElevation=0.0
    private var lastGeoElevation: Int = 0
    private var totalGeoElevation: Int = 0

    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var appUnit=""
    private var burntCalories =0.0
    private var totalRev  =0.0
    private var lastrevPercentage=0.0
    private var maxRevPercentage =0.0
    private var minRevPercentage =0.0
    private var maxEffort =0
    private var maxSpeed =0
    private var maxCadence =0
    private var maxBurntCalories =0
    private var maxHeartrate =0
    private var minHeartrate =0
    private var CumDistance =0.0
    private var CumSpeed =0.0
    private var cadenceData =0.0
    private var maxPace =0

    private var arrDataLocation:MutableList<RLElevationPoint> = mutableListOf()
    private var arrLocationDetails:MutableList<RLLocationDetails> = mutableListOf()
    private var latitude:Double =0.0
    private var longitude:Double =0.0

    private var startTimeKm: Long = 0
    private var startTimeMile: Long = 0
    private var lastLocation: Location? = null
    private var totalDistance = 0.0

    private val gpxStringBuilder = StringBuilder()

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

    private val bleManager by lazy { RLBLEManagerHeartRate(requireContext()) }

    private val binding by lazy {
        RlFragHeartrateSensorProgressBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_heartrate_sensor_progress, container) as RlFragHeartrateSensorProgressBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSensorProgress" )
        yourWayType = requireArguments().getString(RLExtraValueKey.yourWayType).toString().trim()
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLstartCountdown()
        rlLocationViewModel = RLLocationViewModel(requireActivity().application)

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

        if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
            RLstepGetToGPS()
        }

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

               bleManager.rlpausegetData()
               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.RLstopLocationUpdates()
               }

           }catch (e:Exception){
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layResume.setOnClickListener {
           try{
                timerManager.RLresume()

                fragBinding.layPause.visibility=View.VISIBLE
                fragBinding.layResumestop.visibility=View.GONE

               bleManager.rlresumegetData()
               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.RLstartLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
           }
        }
        fragBinding.layStop.setOnClickListener {
           try{
                timerManager.RLstop()
               bleManager.lrstopgetData()
               if (yourWayType.equals("Run")||yourWayType.equals("Walk")||yourWayType.equals("Ride")){
                   rlLocationViewModel.RLstopLocationUpdates()
               }
           }catch (e:Exception){
              RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
           }

            val bundle: Bundle = Bundle()
            bundle.putString("YourWayType",yourWayType)
            bundle.putString("totalTime",(totalTime?:"0"))
            bundle.putString("gpxStringBuilder",gpxStringBuilder.toString()?:"")


            bundle.putString("SENSOR",RLConstants.HEART_SENSOR)
            bundle.putDouble("avgRevPercentage",RLYourWayCalvulation.noNanValueDouble(avgRevPercentage?:0.00))
            bundle.putDouble("burntCalories",RLYourWayCalvulation.noNanValueDouble(burntCalories?:0.00))
            bundle.putDouble("distance",RLYourWayCalvulation.noNanValueDouble(distance?:0.00))
            bundle.putDouble("maxRevPercentage",RLYourWayCalvulation.noNanValueDouble(maxRevPercentage?:0.00))
            bundle.putDouble("minRevPercentage",RLYourWayCalvulation.noNanValueDouble(minRevPercentage?:0.00))
            bundle.putDouble("revPercentage",RLYourWayCalvulation.noNanValueDouble(revPercentage?:0.00))
            bundle.putDouble("totalElevation",RLYourWayCalvulation.noNanValueDouble(totalElevation?:0.00))
            bundle.putDouble("totalRev",RLYourWayCalvulation.noNanValueDouble(totalRev?:0.00))
            bundle.putInt("totalSteps",stepsNumber?:0)
            bundle.putInt("maxSpeed",maxSpeed?:0)
            bundle.putInt("maxHeartRate",maxHeartrate?:0)
            bundle.putInt("maxCadence",maxCadence?:0)
            bundle.putInt("maxBurntCalories",maxBurntCalories?:0)
            bundle.putInt("minHeartrate",minHeartrate?:0)

            bundle.putDouble("maxSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneKm?:0.00))
            bundle.putDouble("maxSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(maxSpeedForOneMile?:0.00))
            bundle.putDouble("avgSpeedForOneKm",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneKm?:0.00))
            bundle.putDouble("avgSpeedForOneMile",RLYourWayCalvulation.noNanValueDouble(avgSpeedForOneMile?:0.00))

            bundle.putBooleanArray("arrConnection",arrConnection.toBooleanArray())

            bundle.putDoubleArray(RLYourWayArrayType.arrBurntCalories.toString(),arrBurntCalories.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCadence.toString(),arrCadence.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrDistance.toString(),arrDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrElevation.toString(),arrElevation.toDoubleArray())
            bundle.putIntegerArrayList(RLYourWayArrayType.arrHRRecordedSecond.toString(),ArrayList(arrHRRecordedSecond))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrHr.toString(),ArrayList(arrHr))
            bundle.putDoubleArray(RLYourWayArrayType.arrRevPercentage.toString(),arrRevPercentage.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrRevSecond.toString(),arrRevSecond.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrSpeed.toString(),arrSpeed.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumDistance.toString(),arrCumDistance.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumElevation.toString(),arrCumElevation.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.arrCumSpeed.toString(),arrCumSpeed.toDoubleArray())

            bundle.putDoubleArray(RLYourWayArrayType.arrAvgCadence.toString(),arrAvgCadence.toDoubleArray())
            bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgHr.toString(),ArrayList(arrAvgHr))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrAvgRevPercentage.toString(),ArrayList(arrAvgRevPercentage))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrMaxCadence.toString(),ArrayList(arrMaxCadence))
            bundle.putIntegerArrayList(RLYourWayArrayType.arrMaxHr.toString(),ArrayList(arrMaxHr))
            bundle.putDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString(),arrMaxRevPercentage.toDoubleArray())

            if (arrSpeedForOneKm.isNullOrEmpty()){
                arrSpeedForOneKm= mutableListOf(0.00)
            }
            if (arrSpeedForOneMile.isNullOrEmpty()){
                arrSpeedForOneMile= mutableListOf(0.00)
            }
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneKm.toString(),arrSpeedForOneKm.toDoubleArray())
            bundle.putDoubleArray(RLYourWayArrayType.speedForOneMile.toString(),arrSpeedForOneMile.toDoubleArray())

            bundle.putParcelableArrayList(RLYourWayArrayType.arrDataLocation.toString(), ArrayList(arrDataLocation))
            bundle.putParcelableArrayList(RLYourWayArrayType.arrLocationDetails.toString(),ArrayList(arrLocationDetails))


            (context as RLMainActivityRL).RLloadFrag(RLFragSessionComplete().newInstance(bundle), TAG, false, null, false)

        }
    }

    private fun  RLwayTypeDesignSet(yourWayType:String){
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
    private fun RLtimerMain() {
        timerManager.RLstart { elapsedTime ->
            activity?.runOnUiThread {
                fragBinding.inlayTime.txtProgressTimeNumber.setText(RLYourWayCalvulation.RLformatElapsedTime(elapsedTime))
                totalTime=(elapsedTime/1000).toString()
                RlDataFillAllArray()
            }
        }
    }
    private fun  RlDataFillAllArray(){
        val currentCalories=RLYourWayCalvulation.calculateCurrentCalories(gender,wsAge,wsWeight.toDouble(),heartRateNumber.toDouble(),RestingHR,RFMHR)
        if (heartRateNumber>0){
            heartRate = heartRateNumber
        }
        val REVPer=RLYourWayCalvulation.calculateREVPer(heartRate,wsWeight.toDouble(),wsHeight.toDouble(),wsAge,gender,RestingHR,RFMHR) //only REV
        RLUpdateHRPersentage(REVPer.roundToInt())
        arrRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(REVPer))
        avgRevPercentage = RLYourWayCalvulation.avgOfArray(arrRevPercentage)
        val REVSec = REVPer / 360//each second REV PERSENTAGE
        arrRevSecond.add(RLYourWayCalvulation.noNanValueDouble(REVSec))
        totalRev = totalRev+ REVSec
        lastrevPercentage=REVPer
        maxRevPercentage=RLYourWayCalvulation.RLmax(maxRevPercentage.toInt(),REVPer.toInt()).toDouble()
        minRevPercentage=RLYourWayCalvulation.RLmin(minRevPercentage.toInt(),REVPer.toInt()).toDouble()
        maxEffort=RLYourWayCalvulation.RLmax(maxEffort,totalRev.roundToInt())
        burntCalories=burntCalories+currentCalories

        maxBurntCalories=RLYourWayCalvulation.RLmax(maxBurntCalories,burntCalories.toInt())

        revPercentage=REVPer

        arrBurntCalories.add( RLYourWayCalvulation.noNanValueDouble(currentCalories))
        arrCadence.add(RLYourWayCalvulation.noNanValueDouble(cadenceData))
        arrDistance.add( RLYourWayCalvulation.noNanValueDouble(distance))
        arrHr.add(heartRateNumber)
        arrSpeed.add(RLYourWayCalvulation.noNanValueDouble(speedNumber))

        CumSpeed=CumSpeed+speedNumber
        CumDistance=CumDistance+distance
        arrCumDistance.add(RLYourWayCalvulation.noNanValueDouble(CumDistance))
        arrCumSpeed.add(RLYourWayCalvulation.noNanValueDouble(CumSpeed))
        distance=CumDistance

        if (elevationMeter > 0) {
            val relativeAltitude = elevationMeter
            val roundedAltitude = relativeAltitude.toBigDecimal().setScale(1, java.math.RoundingMode.HALF_UP).toInt()

            if (lastGeoElevation != null) {
                if (lastGeoElevation!! < roundedAltitude) {
                    totalGeoElevation += (roundedAltitude - lastGeoElevation!!)
                    totalElevation=(totalGeoElevation/10).toDouble()
                }
            }
            lastGeoElevation = roundedAltitude
            arrElevation.add(RLYourWayCalvulation.noNanValueDouble(lastGeoElevation.toDouble()))
            arrCumElevation.add(RLYourWayCalvulation.noNanValueDouble((totalGeoElevation/10).toDouble()))

        }else{
            arrElevation.add(0.0)
            arrCumElevation.add(0.0)
        }

        val totalCaloriesBurnedValue=RlGetValueDouble(burntCalories.toString()).roundToInt()
        fragBinding.inlayCalories.txtProgressTimeNumber.setText(totalCaloriesBurnedValue.toString())

        fragBinding.inlayClimbed.txtProgressTimeNumber.setText(RLYourWayCalvulation.getClimbData(totalElevation,appUnit))

        val elevationpoint= RLElevationPoint(RLYourWayCalvulation.noNanValueDouble(elevationMeter),RLYourWayCalvulation.noNanValueDouble(latitude), RLYourWayCalvulation.noNanValueDouble(longitude))
        arrDataLocation.add((elevationpoint))
        val locationDetails= RLLocationDetails(RLYourWayCalvulation.noNanValueDouble(speedNumber),RLYourWayCalvulation.noNanValueDouble(speedNumber),RLYourWayCalvulation.noNanValueDouble(latitude),0.0, RLYourWayCalvulation.noNanValueDouble(longitude),RLYourWayCalvulation.noNanValueDouble(elevationMeter))
        arrLocationDetails.add(locationDetails)

        arrAvgCadence.add(RLYourWayCalvulation.noNanValueDouble(arrCadence.average()))
        arrAvgHr.add(arrHr.average().roundToInt())
        arrAvgRevPercentage.add(avgRevPercentage.roundToInt())
        arrMaxCadence.add(maxCadence)
        arrMaxHr.add(maxHeartrate)
        arrMaxRevPercentage.add(RLYourWayCalvulation.noNanValueDouble(maxRevPercentage))

        if (!arrSpeedForOneKm.isNullOrEmpty()){

        }
        if (!arrSpeedForOneMile.isNullOrEmpty()){

        }

        val isodate=RLgetCurrentDateTimeIsoFormatted()
        gpxStringBuilder.append(
            """
                <trkpt lat="$latitude" lon="$longitude">
                    <ele>$totalElevation</ele>
                    <time>$isodate}</time>
                </trkpt>
                
                """.trimIndent())

        arrConnection.add(true)
    }

    private fun RLUpdateHRPersentage(REVPer : Int) {
        try {
        if (REVPer>=100){
            fragBinding.progresstext.setText("100%")
            fragBinding.circularProgressBar.RLsetProgress(100)
        }else if(REVPer>=0){
            fragBinding.progresstext.setText("$REVPer%")
            fragBinding.circularProgressBar.RLsetProgress(REVPer)
        }else{
            fragBinding.progresstext.setText("--%")
            fragBinding.circularProgressBar.RLsetProgress(0)
        }
        val progresscolor = RLTools.RLCalculateCircularGraph(REVPer)
        fragBinding.progresstext.setTextColor(Color.parseColor(progresscolor))
        fragBinding.circularProgressBar.RLsetProgressColor(Color.parseColor(progresscolor))
        val tintColor =Color.parseColor(progresscolor)
        fragBinding.imgEffort.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        fragBinding.txtEffortNumber.setText(totalRev.roundToInt().toString())
        fragBinding.txtEffortNumber.setText(totalRev.roundToInt().toString())
        fragBinding.maxEffortNumber.setText(maxEffort.toString())

        val ZoneTextData= RLTools.RlVerifyFeedZoneName(REVPer.toDouble()?:0.0)
        fragBinding.txtCurrentZone.setText(ZoneTextData.efforZoneText)
        fragBinding.txtCurrentZone.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        }catch (e:Exception){
            RLTools.RlLogEPrint(TAG,"Exception: ${e.message}")
        }
    }

    //GPS VALUE GET
    private fun RLstepGetToGPS() {
        //rlLocationViewModel =RLLocationViewModel(requireActivity().application)
        rlLocationViewModel.speedData.observe(requireActivity(), Observer { speed ->
            speed?.let {
                RLTools.RlLogDPrint(TAG,"Speed: ${it} m/s")
                val speedSetValue=RlGetValueDouble(it.toString())
                speedNumber=speedSetValue
                speedList.add("%.2f".format(it).toDouble())
                if (speedSetValue>0){
                    fragBinding.inlaySpeed.txtProgressTimeNumber.setText("%.2f".format(it))
                    maxSpeed=RLYourWayCalvulation.RLmax(maxSpeed,speedNumber.roundToInt())
                    fragBinding.inlaySpeed.txtMaxNumber.setText( maxSpeed.toString())
                    if (!speedList.isNullOrEmpty()){
                        val averageSpeed=speedList.average().toDouble()?:0.0
                        fragBinding.inlaySpeed.txtAvgNumber.setText( "%.2f".format(averageSpeed).toString())

                    }
                }
            }
        })

        rlLocationViewModel.elevationMeter.observe(requireActivity(), Observer { elevation ->
            elevation?.let {
                RLTools.RlLogDPrint(TAG,"elevation: ${it} m")
                val elevation=RlGetValueDouble(it.toString())
                elevationMeter=elevation
            }
        })

        rlLocationViewModel.locationData.observe(requireActivity(), Observer { location ->
            location?.let {
                RLtrackTimePerKilometer(it)
                RLTools.RlLogDPrint(TAG,"location: ${it}")
                latitude=it.latitude
                longitude=it.longitude
            }
        })

        rlLocationViewModel.cadenceData.observe(requireActivity(), Observer { cadence ->
            cadence?.let {
                RLTools.RlLogDPrint(TAG,"cadenceData: ${it} ")
                val _cadenceData=RlGetValueDouble(it.toString())
                cadenceData=_cadenceData
                maxCadence=RLYourWayCalvulation.RLmax(maxCadence,cadenceData.roundToInt())
            }
        })

        rlLocationViewModel.stepCountData.observe(requireActivity(), Observer { stepCount ->
            stepCount?.let {
                RLTools.RlLogDPrint(TAG,"Steps: $stepCount")
                val stepSetValue=RlGetValueInt(stepCount.toString())
                stepsNumber=stepSetValue
                if (stepSetValue>0){
                    if (yourWayType.toLowerCase().equals("run")||yourWayType.toLowerCase().equals("walk")){
                        fragBinding.inlayCadence.txtProgressTimeNumber.setText(stepSetValue.toString())
                    }
                }
            }
        })

        rlLocationViewModel.distanceData.observe(viewLifecycleOwner, Observer { distance ->
            distance?.let {
                val totalDistance=it //round(it * 100) / 100
                RLTools.RlLogDPrint(TAG,"Distance: $totalDistance km")
                val totalDistanceValue=RlGetValueDouble(totalDistance.toString())
                this.distance =totalDistanceValue
                //distanceList.add(totalDistanceValue)
                if (totalDistanceValue>0){
                    fragBinding.inlayDistance.txtProgressTimeNumber.setText("%.2f".format(it))
                }
            }
        })

        rlLocationViewModel.paceData.observe(viewLifecycleOwner, Observer { pace ->
            pace?.let {
                val totalspace=round(it * 100) / 100
                RLTools.RlLogDPrint(TAG, "Pace: $totalspace min/km")
                val totalspaceValue=RlGetValueInt(totalspace.toString())
                paceNumber=totalspaceValue
                paceList.add(totalspaceValue)
                if (totalspaceValue>0){
                    fragBinding.inlayPace.txtProgressTimeNumber.setText(totalspace.toString())
                    if (!paceList.isNullOrEmpty()){
                        val averagePace=paceList.average().roundToInt()?:0
                        maxPace=RLYourWayCalvulation.RLmax(maxPace,paceNumber.toInt())
                        //val maxPace=paceList.maxOrNull()!!.toInt()?:0
                        fragBinding.inlayPace.txtAvgNumber.setText(averagePace.toString())
                        fragBinding.inlayPace.txtMaxNumber.setText(maxPace.toString())
                    }

                }
            }
        })

        rlLocationViewModel.RLstartLocationUpdates()
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

    private fun RLtrackTimePerKilometer(location: Location) {
        if (lastLocation != null) {
            val distance = lastLocation!!.distanceTo(location)
            totalDistance += distance

            // Check if one kilometer is reached
            if (totalDistance >= 1000) {
                val currentTime = System.currentTimeMillis()
                val elapsedTimeKm = (currentTime - startTimeKm) / 1000.0 // in seconds
                maxSpeedForOneKm=RLYourWayCalvulation.RLmax(maxSpeedForOneKm.roundToInt(),elapsedTimeKm.roundToInt()).toDouble()
                arrSpeedForOneKm.add(RlGetValueDouble(elapsedTimeKm.toString()))
                avgSpeedForOneKm=arrSpeedForOneKm.average()
                println("Time taken for last kilometer: $elapsedTimeKm seconds")

                // Reset distance and start time for the next kilometer
                totalDistance -= 1000
                startTimeKm = currentTime
            }

            // Check if one mile is reached
            if (totalDistance >= 1609.34) {
                val currentTime = System.currentTimeMillis()
                val elapsedTimeMile = (currentTime - startTimeMile) / 1000.0 // in seconds
                maxSpeedForOneMile=RLYourWayCalvulation.RLmax(maxSpeedForOneMile.roundToInt(),elapsedTimeMile.roundToInt()).toDouble()
                println("Time taken for last mile: $elapsedTimeMile seconds")
                arrSpeedForOneMile.add(RlGetValueDouble(elapsedTimeMile.toString()))
                avgSpeedForOneMile=arrSpeedForOneMile.average()
                // Reset distance and start time for the next mile
                totalDistance -= 1609.34
                startTimeMile = currentTime
            }
        } else {
            // Initialize start times for the first kilometer and mile
            startTimeKm = System.currentTimeMillis()
            startTimeMile = System.currentTimeMillis()
        }
        lastLocation = location
    }

    override fun onStart() {
        super.onStart()
        if (bleManager.checkAndRequestPermissions(requireActivity())) {
            bleManager.setupBluetooth {
                bleManager.startBLEService()
            }
        }
        bleManager.setCallback(object : RLBLEManagerHeartRate.BLECallback {
            override fun onHeartRateDataReceived(data: String) {
                Log.d("BLE", "Heart Rate: $heartRate")
                heartRateNumber=RlGetValueInt(data.toString())
                var heartRateSetValue=RlGetValueInt(data.toString())
                if (heartRateSetValue > 0){
                    arrHRRecordedSecond.add(totalTime.toInt()?:0)
                    fragBinding.inlayHeartrate.txtProgressTimeNumber.setText(data)
                    maxHeartrate= RLYourWayCalvulation.RLmax(maxHeartrate,heartRateNumber)
                    minHeartrate=RLYourWayCalvulation.RLmin(minHeartrate,heartRateNumber)
                    fragBinding.inlayHeartrate.txtMaxNumber.setText(maxHeartrate.toString())
                    if (!arrHr.isNullOrEmpty()){
                        val avgHeartRate=arrHr.average().roundToInt()?:0
                        fragBinding.inlayHeartrate.txtAvgNumber.setText(avgHeartRate.toString())
                    }

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
        try {
            bleManager.cleanup()
        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
        }
    }
}