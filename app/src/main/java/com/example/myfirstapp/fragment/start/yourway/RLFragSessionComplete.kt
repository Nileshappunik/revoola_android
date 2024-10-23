package com.example.myfirstapp.fragment.start.yourway

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.telecom.Call.Details
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.BuildConfig
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerWrite
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.enumclass.RLYourWayArrayType
import com.example.myfirstapp.firebaseModel.RLAllWaySessionSummaryGraphDataHRModel
import com.example.myfirstapp.firebaseModel.RLAllWaySessionSummaryGraphDataModel
import com.example.myfirstapp.firebaseModel.RLElevationPoint
import com.example.myfirstapp.firebaseModel.RLGhostDataModel
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.firebaseModel.RLHeartRateSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.firebaseModel.RLLocationDetails
import com.example.myfirstapp.firebaseModel.RLWorkoutSessionDetailsModel
import com.example.myfirstapp.firebaseModel.RLNoSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.firebaseModel.RLSpeedSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.firebaseModel.RLWorkoutSessionSummaryModel
import com.example.myfirstapp.utils.RLConstants

import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker
import java.util.UUID
import kotlin.math.roundToInt

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""
    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga"
        RLFirebaseToFatchUserData()

        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        val totalTime = requireArguments().getString("totalTime").toString().trim()
        val SensorType: String = requireArguments().getString("SENSOR").toString()


        fragBinding.edtSessionName.setText("$yourWayType Session")
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titleTxt:String=fragBinding.tvShareTitle.text.toString().toUpperCase()
            when(titleTxt){
                "FRIENDS"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                    fragBinding.tvShareTitle.setText(R.string.privatetx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                    RLShareMapHide(false)
                }
                "EVERYONE"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                    fragBinding.tvShareTitle.setText(R.string.friendstx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                    RLShareMapHide(true)
                }
                "PRIVATE"->{
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                    fragBinding.tvShareTitle.setText(R.string.everyone)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
                    RLShareMapHide(true)
                }
            }
        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
        }
        fragBinding.imgCancle.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.inlayButton.commonButton.setText(R.string.save)
        fragBinding.inlayButton.commonButton.setOnClickListener {
           /* if (imgUriList.size>0){
                RLuploadImagesToFirebase(imgUriList)
            }*/
            when (SensorType){
                RLConstants.HEARTSENSOR->{
                    RlHeartRateDataEntryToFirebase(yourWayType,totalTime)
                }
                RLConstants.SPEEDSENSOR->{
                    RlSpeedSensorDataEntryToFirebase(yourWayType,totalTime)
                }
                RLConstants.NOSENSOR->{
                    RlNoSensorDataEntryToFirebase(yourWayType,totalTime)
                }
            }

        }

    }

    //Ready Data To Firebase Entry
    private fun RlHeartRateDataEntryToFirebase(yourWayType: String, totalTime: String) {

        val avgRevPercentage=requireArguments().getDouble("avgRevPercentage")
        val burntCalories=requireArguments().getDouble("burntCalories")
        val distance=requireArguments().getDouble("distance")
        val maxRevPercentage=requireArguments().getDouble("maxRevPercentage")
        val minRevPercentage=requireArguments().getDouble("minRevPercentage")
        val revPercentage=requireArguments().getDouble("revPercentage")
        val totalElevation=requireArguments().getDouble("totalElevation")
        val totalRev=requireArguments().getDouble("totalRev")
        val totalSteps=requireArguments().getInt("totalSteps")
        val maxSpeed=requireArguments().getInt("maxSpeed")
        val maxHeartrate=requireArguments().getInt("maxHeartrate")
        val maxCadence=requireArguments().getInt("maxCadence")
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")
        val minHeartrate=requireArguments().getInt("minHeartrate")


        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())
        val arrHRRecordedSecond: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrHRRecordedSecond.toString())
        val arrHr: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrHr.toString())
        val arrRevPercentageList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevPercentage.toString())
        val arrRevSecondList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevSecond.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumDistance.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())
        val arrCumSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumSpeed.toString())

        val arrDataLocation = arguments?.getParcelableArrayList<RLElevationPoint>(RLYourWayArrayType.arrDataLocation.toString())
        val arrLocationDetails = arguments?.getParcelableArrayList<RLLocationDetails>(RLYourWayArrayType.arrLocationDetails.toString())


        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf()
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf()
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf()
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf()
        val arrRevPercentage: MutableList<Double> = arrRevPercentageList?.toMutableList() ?: mutableListOf()
        val arrRevSecond: MutableList<Double> = arrRevSecondList?.toMutableList() ?: mutableListOf()
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf()
        val arrCumDistance: MutableList<Double> = arrCumDistanceList?.toMutableList() ?: mutableListOf()
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf()
        val arrCumSpeed: MutableList<Double> = arrCumSpeedList?.toMutableList() ?: mutableListOf()

        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLHeartRateSensorWorkoutSessionDetailsModel() //Session Details Data
        val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()//summery Data Entry


        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories= arrBurntCalories
        entryWorkoutSessionDetails.arrCadence= arrCadence
        entryWorkoutSessionDetails.arrCumDistance=arrCumDistance
        entryWorkoutSessionDetails.arrCumElevation=arrCumElevation
        entryWorkoutSessionDetails.arrCumSpeed=arrCumSpeed
        entryWorkoutSessionDetails.arrDistance= arrDistance
        entryWorkoutSessionDetails.arrElevation=arrElevation
        entryWorkoutSessionDetails.arrHRRecordedSecond= arrHRRecordedSecond!!
        entryWorkoutSessionDetails.arrHr= arrHr!!
        entryWorkoutSessionDetails.arrRevPercentage=arrRevPercentage //ready value
        entryWorkoutSessionDetails.arrRevSecond=arrRevSecond //ready value
        entryWorkoutSessionDetails.arrSpeed= arrSpeed


        //Normal Entry Value
        entryWorkoutSessionDetails.MaxHrUsedForCalculation=RFMHR
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=RFMHR
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=RestingHR.toInt()
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=RestingHR.toInt()
        entryWorkoutSessionDetails.avgRevPercentage=avgRevPercentage
        entryWorkoutSessionDetails.burntCalories=burntCalories
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.classDescription=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.demsElevation=-1
        entryWorkoutSessionDetails.distance=distance
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage=maxRevPercentage
        entryWorkoutSessionDetails.minRevPercentage=minRevPercentage
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage=revPercentage
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalElevation=totalElevation
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=totalRev
        entryWorkoutSessionDetails.totalSteps=totalSteps
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.typeOfGoal=yourWayType
        //Zone Entry Value
        val REVPer=revPercentage.roundToInt()
        when {
            REVPer <= 30 -> {
                //Zone 1
                entryWorkoutSessionDetails.zone1.remark= "android"
                entryWorkoutSessionDetails.zone1.distance=distance
                entryWorkoutSessionDetails.zone1.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone1.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone1.totalRev=totalRev

                //Zone 1 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone1.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone1.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone1.distance=distance
                entryWorkoutSessionSummary.zone1.remark="android"
                entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone1.totalRev=totalRev
            }
            REVPer <= 50 -> {
                //Zone 2
                entryWorkoutSessionDetails.zone2.remark= "android"
                entryWorkoutSessionDetails.zone2.distance=distance
                entryWorkoutSessionDetails.zone2.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone2.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone2.totalRev=totalRev

                //Zone 2 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone2.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone2.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone2.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone2.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone2.distance=distance
                entryWorkoutSessionSummary.zone2.remark="android"
                entryWorkoutSessionSummary.zone2.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone2.totalRev=totalRev
            }
            REVPer <= 60 -> {
                //Zone 3
                entryWorkoutSessionDetails.zone3.remark= "android"
                entryWorkoutSessionDetails.zone3.distance=distance
                entryWorkoutSessionDetails.zone3.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone3.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone3.totalRev=totalRev

                //Zone 3 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone3.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone3.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone3.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone3.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone3.distance=distance
                entryWorkoutSessionSummary.zone3.remark="android"
                entryWorkoutSessionSummary.zone3.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone3.totalRev=totalRev
            }
            REVPer <= 70 -> {
                //Zone 4
                entryWorkoutSessionDetails.zone4.remark= "android"
                entryWorkoutSessionDetails.zone4.distance=distance
                entryWorkoutSessionDetails.zone4.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone4.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone4.totalRev=totalRev

                //Zone 4 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone4.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone4.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone4.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone4.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone4.distance=distance
                entryWorkoutSessionSummary.zone4.remark="android"
                entryWorkoutSessionSummary.zone4.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone4.totalRev=totalRev
            }
            REVPer <= 80 -> {
                //Zone 5
                entryWorkoutSessionDetails.zone5.remark= "android"
                entryWorkoutSessionDetails.zone5.distance=distance
                entryWorkoutSessionDetails.zone5.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone5.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone5.totalRev=totalRev

                //Zone 5 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone5.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone5.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone5.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone5.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone5.distance=distance
                entryWorkoutSessionSummary.zone5.remark="android"
                entryWorkoutSessionSummary.zone5.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone5.totalRev=totalRev
            }
            REVPer <= 90 -> {
                //Zone 6
                entryWorkoutSessionDetails.zone6.remark= "android"
                entryWorkoutSessionDetails.zone6.distance=distance
                entryWorkoutSessionDetails.zone6.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone6.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone6.totalRev=totalRev

                //Zone 6 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone6.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone6.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone6.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone6.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone6.distance=distance
                entryWorkoutSessionSummary.zone6.remark="android"
                entryWorkoutSessionSummary.zone6.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone6.totalRev=totalRev
            }
            REVPer <= 100 -> {
                //Zone 7
                entryWorkoutSessionDetails.zone7.remark= "android"
                entryWorkoutSessionDetails.zone7.distance=distance
                entryWorkoutSessionDetails.zone7.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone7.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone7.totalRev=totalRev

                //Zone 7 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone7.avgCadence= arrCadence.average()
                entryWorkoutSessionSummary.zone7.avgHr= arrHr.average().roundToInt()
                entryWorkoutSessionSummary.zone7.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone7.burntCalories=burntCalories
                entryWorkoutSessionSummary.zone7.distance=distance
                entryWorkoutSessionSummary.zone7.remark="android"
                entryWorkoutSessionSummary.zone7.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone7.totalRev=totalRev
            }
        }

        //AllWaySessionSummaryGraphData Entry
        val entrySessionSummaryGraphData = RLAllWaySessionSummaryGraphDataHRModel()
        entrySessionSummaryGraphData.arrHr=arrHr
        entrySessionSummaryGraphData.arrCadence=arrCadence
        entrySessionSummaryGraphData.arrSpeed=arrSpeed
        entrySessionSummaryGraphData.arrRevPercentage=arrRevPercentage

        //WorkoutSessionSummary Data Entery

        entryWorkoutSessionSummary.avgBurntCalories=arrBurntCalories.average()
        entryWorkoutSessionSummary.avgCadence=arrCadence.average()
        entryWorkoutSessionSummary.avgHr= arrHr.average().roundToInt()
        entryWorkoutSessionSummary.avgRevPercentage= arrRevPercentage.average()
        entryWorkoutSessionSummary.avgSpeed=arrSpeed.average()
        //entryWorkoutSessionSummary.avgSpeedForOneKm
        //entryWorkoutSessionSummary.avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories
        entryWorkoutSessionSummary.classDate=currentTimestamp
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.distance= distance
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories
        entryWorkoutSessionSummary.maxCadence=maxCadence
        entryWorkoutSessionSummary.maxHr=maxHeartrate
        entryWorkoutSessionSummary.maxRevPercentage=maxRevPercentage
        entryWorkoutSessionSummary.maxSpeed=maxSpeed
        //entryWorkoutSessionSummary.maxSpeedForOneKm=
        //entryWorkoutSessionSummary.maxSpeedForOneMile=
        entryWorkoutSessionSummary.minHr=minHeartrate
        entryWorkoutSessionSummary.minRevPercentage=minRevPercentage
        entryWorkoutSessionSummary.revPercentage=revPercentage
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionSummary.totalElevation=totalElevation
        entryWorkoutSessionSummary.totalRev=totalRev
        entryWorkoutSessionSummary.totalSteps=totalSteps
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType
        //entryWorkoutSessionSummary.visibilityflagforthatsession=

        //dataForTesting Entry
        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)

        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation,//ARRAY
            "status" to true)

        val locationDataMap = hashMapOf(
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to yourWayType,
            "elevationDic" to arrElevation,//ARRAY
            "locationDic" to arrLocationDetails,//ARRAY
            "speedDic" to arrSpeed,//ARRAY
            "totalRev" to totalRev)

        //GhostData Entry
        val ghostDataEntry= RLGhostDataModel()

        RLHeartRateSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap)

    }
    private fun RlNoSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {

        val burntCalories=requireArguments().getDouble("burntCalories")
        val totalElevation=requireArguments().getDouble("totalElevation")
        val totalSteps=requireArguments().getInt("totalSteps")
        val distance=requireArguments().getDouble("distance")
        val maxSpeed=requireArguments().getInt("maxSpeed")
        val maxCadence=requireArguments().getInt("maxCadence")
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")

        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumDistance.toString())
        val arrCumSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumSpeed.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())


        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf()
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf()
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf()
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf()
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf()
        val arrCumDistance: MutableList<Double> = arrCumDistanceList?.toMutableList() ?: mutableListOf()
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf()
        val arrCumSpeed: MutableList<Double> = arrCumSpeedList?.toMutableList() ?: mutableListOf()

         val arrDataLocation = arguments?.getParcelableArrayList<RLElevationPoint>(RLYourWayArrayType.arrDataLocation.toString())
         val arrLocationDetails = arguments?.getParcelableArrayList<RLLocationDetails>(RLYourWayArrayType.arrLocationDetails.toString())

        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLNoSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories= arrBurntCalories
        entryWorkoutSessionDetails.arrCadence= arrCadence
        entryWorkoutSessionDetails.arrCumDistance= arrCumDistance
        entryWorkoutSessionDetails.arrCumSpeed= arrCumSpeed
        entryWorkoutSessionDetails.arrDistance= arrDistance
        entryWorkoutSessionDetails.arrElevation=arrElevation
        entryWorkoutSessionDetails.arrSpeed= arrSpeed
        entryWorkoutSessionDetails.arrCumElevation=arrCumElevation


        //Normal Entry Value
        entryWorkoutSessionDetails.MaxHrUsedForCalculation=RFMHR
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=RFMHR
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=RestingHR.toInt()
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=RestingHR.toInt()

        entryWorkoutSessionDetails.avgRevPercentage= 0//value count
        entryWorkoutSessionDetails.burntCalories=burntCalories
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.classDescription=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.demsElevation=-1
        entryWorkoutSessionDetails.distance = distance
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage= 0 //calculation of REV persentage
        entryWorkoutSessionDetails.minRevPercentage= 0 //calculation of REV persentage
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage= 0 //last value of arrRevPercentage
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionDetails.totalElevation= totalElevation
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev= 0.0 // sum of revpersentage
        entryWorkoutSessionDetails.totalSteps=totalSteps
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.typeOfGoal=yourWayType


        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance=distance
        entryWorkoutSessionDetails.zone1.burntCalories=burntCalories
        entryWorkoutSessionDetails.zone1.seconds=totalTime.toInt()?:0
        entryWorkoutSessionDetails.zone1.totalRev=0

        //AllWaySessionSummaryGraphData Entry
        val entrySessionSummaryGraphData = RLAllWaySessionSummaryGraphDataModel()
        entrySessionSummaryGraphData.arrCadence=arrCadence
        entrySessionSummaryGraphData.arrSpeed=arrSpeed


        //WorkoutSessionSummary Data Entery
        val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()

        entryWorkoutSessionSummary.avgBurntCalories=arrBurntCalories.average()
        entryWorkoutSessionSummary.avgCadence=arrCadence.average()
        entryWorkoutSessionSummary.avgSpeed=arrSpeed.average()
        //entryWorkoutSessionSummary.avgSpeedForOneKm
        //entryWorkoutSessionSummary.avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories
        entryWorkoutSessionSummary.classDate=currentTimestamp
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.distance= distance
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories
        entryWorkoutSessionSummary.maxCadence=maxCadence
        entryWorkoutSessionSummary.maxSpeed=maxSpeed
        //entryWorkoutSessionSummary.maxSpeedForOneKm=
        //entryWorkoutSessionSummary.maxSpeedForOneMile=
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionSummary.totalElevation=totalElevation
        entryWorkoutSessionSummary.totalSteps=totalSteps
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType
        //entryWorkoutSessionSummary.visibilityflagforthatsession=


        //Zone 1 WorkoutSessionSummary Data Entery
        entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()
        entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()
        entryWorkoutSessionSummary.zone1.burntCalories=burntCalories
        entryWorkoutSessionSummary.zone1.distance=distance
        entryWorkoutSessionSummary.zone1.remark="android"
        entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0

        //dataForTesting Entry
        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)

        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation,//ARRAY
            "status" to true)

        val locationDataMap = hashMapOf(
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to yourWayType,
            "elevationDic" to arrElevation,//ARRAY
            "locationDic" to arrLocationDetails,//ARRAY
            "speedDic" to arrSpeed,//ARRAY
            "totalRev" to 0)

        //GhostData Entry
        val ghostDataEntry= RLGhostDataModel()

        RLNoSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap)
    }
    private fun RlSpeedSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {

        val burntCalories=requireArguments().getDouble("burntCalories")
        val totalElevation=requireArguments().getDouble("totalElevation")
        val totalSteps=requireArguments().getInt("totalSteps")
        val distance=requireArguments().getDouble("distance")
        val maxSpeed=requireArguments().getInt("maxSpeed")
        val maxCadence=requireArguments().getInt("maxCadence")
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")

        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())


        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf()
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf()
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf()
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf()
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf()
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf()

        val arrDataLocation = arguments?.getParcelableArrayList<RLElevationPoint>(RLYourWayArrayType.arrDataLocation.toString())
        val arrLocationDetails = arguments?.getParcelableArrayList<RLLocationDetails>(RLYourWayArrayType.arrLocationDetails.toString())


        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLSpeedSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories= arrBurntCalories
        entryWorkoutSessionDetails.arrCadence=arrCadence
        entryWorkoutSessionDetails.arrDistance= arrDistance
        entryWorkoutSessionDetails.arrElevation=arrElevation
        entryWorkoutSessionDetails.arrSpeed= arrSpeed
        entryWorkoutSessionDetails.arrCumElevation=arrCumElevation

        //Normal Entry Value

        entryWorkoutSessionDetails.MaxHrUsedForCalculation=RFMHR
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=RFMHR
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=RestingHR.toInt()
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=RestingHR.toInt()

        entryWorkoutSessionDetails.avgRevPercentage=0
        entryWorkoutSessionDetails.burntCalories=burntCalories
        entryWorkoutSessionDetails.classDate=currentTimestamp
        entryWorkoutSessionDetails.classDescription=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionDetails.classType= yourWayType
        entryWorkoutSessionDetails.demsElevation=-1
        entryWorkoutSessionDetails.distance=distance
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.isClass=false
        entryWorkoutSessionDetails.isPowerDeviceConnected=false
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.minRevPercentage=0
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage=0
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalElevation=totalElevation
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=0.0
        entryWorkoutSessionDetails.totalSteps=totalSteps
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.typeOfGoal=yourWayType



        //Zone Entry Value
        entryWorkoutSessionDetails.zone1.remark= "android"
        entryWorkoutSessionDetails.zone1.distance=distance
        entryWorkoutSessionDetails.zone1.burntCalories=burntCalories
        entryWorkoutSessionDetails.zone1.seconds=totalTime.toInt()?:0
        entryWorkoutSessionDetails.zone1.totalRev=0


        //AllWaySessionSummaryGraphData Entry
        val entrySessionSummaryGraphData = RLAllWaySessionSummaryGraphDataModel()
        entrySessionSummaryGraphData.arrCadence=arrCadence
        entrySessionSummaryGraphData.arrSpeed=arrSpeed

        //WorkoutSessionSummary Data Entery
        val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()

        entryWorkoutSessionSummary.avgBurntCalories=arrBurntCalories.average()
        entryWorkoutSessionSummary.avgCadence=arrCadence.average()
        entryWorkoutSessionSummary.avgSpeed=arrSpeed.average()
        //entryWorkoutSessionSummary.avgSpeedForOneKm
        //entryWorkoutSessionSummary.avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories
        entryWorkoutSessionSummary.classDate=currentTimestamp
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.distance= distance
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories
        entryWorkoutSessionSummary.maxCadence=maxCadence
        entryWorkoutSessionSummary.maxSpeed=maxSpeed
        //entryWorkoutSessionSummary.maxSpeedForOneKm=
        //entryWorkoutSessionSummary.maxSpeedForOneMile=
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionSummary.totalElevation=totalElevation
        entryWorkoutSessionSummary.totalSteps=totalSteps
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType
        //entryWorkoutSessionSummary.visibilityflagforthatsession=


        //Zone 1 WorkoutSessionSummary Data Entery
        entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()
        entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()
        entryWorkoutSessionSummary.zone1.burntCalories=burntCalories
        entryWorkoutSessionSummary.zone1.distance=distance
        entryWorkoutSessionSummary.zone1.remark="android"
        entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0

        //dataForTesting Entry
        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)

        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation,//ARRAY
            "status" to true)

        val locationDataMap = hashMapOf(
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to yourWayType,
            "elevationDic" to arrElevation,//ARRAY
            "locationDic" to arrLocationDetails,//ARRAY
            "speedDic" to arrSpeed,//ARRAY
            "totalRev" to 0)

        //GhostData Entry
        val ghostDataEntry= RLGhostDataModel()

        RLSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap)


    }
    //Entry to Firebase
    private fun RLNoSensorUserSessionDetailData(entry: RLNoSensorWorkoutSessionDetailsModel,entryGraph:RLAllWaySessionSummaryGraphDataModel,entrysummery:RLWorkoutSessionSummaryModel,
                                                deviceRecordedData:Any,elevationData:Any,locationData:Any) {
        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"

         databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
             if (success) {
                Log.d(TAG,"Successful connectivity Entry")
             }else {
                 Log.e(TAG,"Error connectivity Entry:- $error")
             }
         }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful elevation Entry")
            }else {
                Log.e(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful  location Entry")
            }else {
                Log.e(TAG,"Error  location Entry:- $error")
            }
        }


        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")

                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }

        //Entry Graph Data
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryGraphData/$currentUser")
        val entryIdGraph = (System.currentTimeMillis() / 1000).toString()
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(entryGraph)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }


        //Entry Session Detail Data
        // val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val databaseRef = FirebaseDatabase.getInstance().getReference("/${RLConstants.PROPOSEDSTRUCTURE}/${RLConstants.REVOOLAUSERSESSIONDETAILDATA}/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLHeartRateSensorUserSessionDetailData(entry: RLHeartRateSensorWorkoutSessionDetailsModel,entryGraph: RLAllWaySessionSummaryGraphDataHRModel,entrysummery:RLWorkoutSessionSummaryModel,
                                                       deviceRecordedData:Any,elevationData:Any,locationData:Any) {

        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"

        databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful connectivity Entry")
            }else {
                Log.e(TAG,"Error connectivity Entry:- $error")
            }
        }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful elevation Entry")
            }else {
                Log.e(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful  location Entry")
            }else {
                Log.e(TAG,"Error  location Entry:- $error")
            }
        }


        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")

                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }

        //entry Graph Data
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryGraphData/$currentUser")
        val entryIdGraph = (System.currentTimeMillis() / 1000).toString()
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(entryGraph)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }

        //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLSpeedSensorUserSessionDetailData(entry: RLSpeedSensorWorkoutSessionDetailsModel,entryGraph:RLAllWaySessionSummaryGraphDataModel,entrysummery:RLWorkoutSessionSummaryModel,
                                                   deviceRecordedData:Any,elevationData:Any,locationData:Any) {

        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"

        databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful connectivity Entry")
            }else {
                Log.e(TAG,"Error connectivity Entry:- $error")
            }
        }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful elevation Entry")
            }else {
                Log.e(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                Log.d(TAG,"Successful  location Entry")
            }else {
                Log.e(TAG,"Error  location Entry:- $error")
            }
        }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")

                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }

        //entry Graph Data
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryGraphData/$currentUser")
        val entryIdGraph = (System.currentTimeMillis() / 1000).toString()
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(entryGraph)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }

       //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }


    private fun RLShareMapHide(isVisible:Boolean){
        if (isVisible){
            fragBinding.txtShareMap.visibility=View.VISIBLE
            fragBinding.switchCompat.visibility=View.VISIBLE
        }else{
            fragBinding.txtShareMap.visibility=View.GONE
            fragBinding.switchCompat.visibility=View.GONE
        }
    }
    private fun RLchooseFromGallery() {
        TedImagePicker.with(requireContext())
            .max(5, "maximum limit to 5 images") // Set the maximum limit to 5 images
            .showCameraTile(false)
            .startMultiImage { uriList ->
                // Handle the selected images here

                for (uri in uriList) {
                    imgUriList.add(uri)
                }
                if (imgUriList.size>0){
                    RLimageListVisible(true)
                }else{
                    RLimageListVisible(false)
                }
                fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
               val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
                   imgUriList.remove(uri)
                   if (imgUriList.size>0){
                       RLimageListVisible(true)
                   }else{
                       RLimageListVisible(false)
                   }
                }
                fragBinding.rvSelectedImages.adapter = selectedImagesAdapter
            }
    }
    private fun RLimageListVisible(isVisible: Boolean){
        if (isVisible){
            fragBinding.rvSelectedImages.visibility=View.VISIBLE
            fragBinding.txtAddPhoto.visibility=View.GONE
        }else{
            fragBinding.rvSelectedImages.visibility=View.GONE
            fragBinding.txtAddPhoto.visibility=View.VISIBLE
        }
    }
    //SELECTED IMAGE SENT TO SERVER
    private fun RLuploadImagesToFirebase(imageUris: List<Uri>) {
        val storageReference = FirebaseStorage.getInstance().reference
        val databaseReference = FirebaseDatabase.getInstance().reference.child("live")

        for (uri in imageUris) {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val fileReference = storageReference.child("uploads/$fileName")

            fileReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        RLsaveImageUrlToDatabase(downloadUri.toString(), databaseReference)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    RLcommonToast( "Upload failed: ${exception.message}")
                }
        }
    }
    private fun RLsaveImageUrlToDatabase(downloadUrl: String, databaseReference: DatabaseReference) {
        val imageId = databaseReference.push().key // Generate a unique ID for each image
        imageId?.let {
            databaseReference.child(it).setValue(downloadUrl)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                        RLcommonToast("Image uploaded successfully!")
                    } else {
                        RLcommonToast("Failed to upload image URL to database.")
                    }
                }
        }
    }
    private fun RLuploadImageToFirebaseStorage(imageUri: Uri, text: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Once we have the image URL, save it with the text to the database

                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Image upload failed", e)
            }
    }
    private fun RLRevoolaUserSessionSummaryData(entry: RLWorkoutSessionSummaryModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }
    private fun RLRevoolaUserSessionDetailData(entry: RLWorkoutSessionDetailsModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "Failed to save entry", task.exception)
                    }
                }
        }
    }

    //FIREBASE USERDATA GET
    private fun RLFirebaseToFatchUserData() {
        //Firebase To Fetch UserData
        val databaseManager: RLDatabaseManagerRead = RLDatabaseManagerRead()
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()!!.uid
        val path ="/proposedstructure/revoolaUserSettings/$userId/basicData"
        databaseManager.RlreadData(path){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                wsWeight=userData.weightkg
                wsHeight=userData.height
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
            }
        }
    }

     fun zoneDiff(REVPer: Int) {
       when {
           REVPer <= 30 -> {
               //Zone 1

           }
           REVPer <= 50 -> {
               //Zone 2

           }
           REVPer <= 60 -> {
               //Zone 3

           }
           REVPer <= 70 -> {
               //Zone 4

           }
           REVPer <= 80 -> {
               //Zone 5

           }
           REVPer <= 90 -> {
               //Zone 6

           }
           REVPer <= 100 -> {
               //Zone 7

           }
       }
   }
}