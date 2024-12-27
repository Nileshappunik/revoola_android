package com.revoola.fragment.start.yourway

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlFragSessionCompleteBinding
import com.revoola.enumclass.RLYourWayArrayType
import com.revoola.firebaseModel.RLAllWaySessionSummaryGraphDataHRModel
import com.revoola.firebaseModel.RLAllWaySessionSummaryGraphDataModel
import com.revoola.firebaseModel.RLElevationPoint
import com.revoola.firebaseModel.RLGhostDataModel
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.adapter.RLSelectedImagesAdapter
import com.revoola.firebaseModel.RLHeartRateSensorWorkoutSessionDetailsModel
import com.revoola.firebaseModel.RLLocationDetails
import com.revoola.firebaseModel.RLWorkoutSessionDetailsModel
import com.revoola.firebaseModel.RLNoSensorWorkoutSessionDetailsModel
import com.revoola.firebaseModel.RLSpeedSensorWorkoutSessionDetailsModel
import com.revoola.firebaseModel.RLWorkoutSessionSummaryModel
import com.revoola.utils.RLConstants

import com.revoola.commonobject.RLTools
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    private var displayImage =""
    private var displayName =""
    private var yourWayType ="Walk"
    private var visibilityflagforthatsession:Int =0

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
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragSessionComplete" )
        currentUser=  com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga"
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                wsWeight=userData.weightkg?:"60"
                wsHeight=userData.height?:"167"
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                displayImage =userData.displayImage
                displayName =userData.displayName
            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

         yourWayType = requireArguments().getString("YourWayType").toString().trim()
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
                    visibilityflagforthatsession=0
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                    fragBinding.tvShareTitle.setText(R.string.privatetx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
                    RLShareMapHide(false)
                }
                "EVERYONE"->{
                    visibilityflagforthatsession=2
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                    fragBinding.tvShareTitle.setText(R.string.friendstx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                    RLShareMapHide(true)
                }
                "PRIVATE"->{
                    visibilityflagforthatsession=1
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
       val gpxString=requireArguments().getString("gpxStringBuilder")?:""
        val avgRevPercentage=requireArguments().getDouble("avgRevPercentage")?:0.0
        val burntCalories=requireArguments().getDouble("burntCalories")?:0.0
        var distance=requireArguments().getDouble("distance")?:0.0
        val maxRevPercentage=requireArguments().getDouble("maxRevPercentage")?:0.0
        val minRevPercentage=requireArguments().getDouble("minRevPercentage")?:0.0
        val revPercentage=requireArguments().getDouble("revPercentage")?:0.0
        val totalElevation=requireArguments().getDouble("totalElevation")?:0.0
        val totalRev=requireArguments().getDouble("totalRev")?:0.0
        val totalSteps=requireArguments().getInt("totalSteps")?:0
        val maxSpeed=requireArguments().getInt("maxSpeed")?:0
        val maxHeartrate=requireArguments().getInt("maxHeartrate")?:0
        val maxCadence=requireArguments().getInt("maxCadence")?:0
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")?:0
        val minHeartrate=requireArguments().getInt("minHeartrate")?:0

        val maxSpeedForOneKm=requireArguments().getDouble("maxSpeedForOneKm")?:0.0
        val maxSpeedForOneMile=requireArguments().getDouble("maxSpeedForOneMile")?:0.0
        val avgSpeedForOneKm=requireArguments().getDouble("avgSpeedForOneKm")?:0.0
        val avgSpeedForOneMile=requireArguments().getDouble("avgSpeedForOneMile")?:0.0

        val retrievedConnectionArray = requireArguments().getBooleanArray("arrConnection")
        val arrConnection: MutableList<Boolean> = retrievedConnectionArray?.toMutableList() ?: mutableListOf()


        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())

        val speedForOneMileList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneMile.toString())
        val speedForOneKmList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneKm.toString())
        val speedForOneKm: MutableList<Double> = speedForOneKmList?.toMutableList() ?: mutableListOf(0.0)
        val speedForOneMile: MutableList<Double> = speedForOneMileList?.toMutableList() ?: mutableListOf(0.0)

        val arrRevPercentageList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevPercentage.toString())
        val arrRevSecondList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevSecond.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumDistance.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())
        val arrCumSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumSpeed.toString())
        val arrMaxRevPercentageList=arguments?.getDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString())
        val arrAvgCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrAvgCadence.toString())

        val arrHRRecordedSecond: ArrayList<Int>? = requireArguments().getIntegerArrayList(
            RLYourWayArrayType.arrHRRecordedSecond.toString())
        val arrHr: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrHr.toString())
        val arrAvgHr: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrAvgHr.toString())
        val arrAvgRevPercentage: ArrayList<Int>? = requireArguments().getIntegerArrayList(
            RLYourWayArrayType.arrAvgRevPercentage.toString())
        val arrMaxCadence: ArrayList<Int>? = requireArguments().getIntegerArrayList(
            RLYourWayArrayType.arrMaxCadence.toString())
        val arrMaxHr: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrMaxHr.toString())


        var arrDataLocation =  requireArguments()?.getParcelableArrayList<RLElevationPoint>(
            RLYourWayArrayType.arrDataLocation.toString())
        val arrLocationDetails =  requireArguments()?.getParcelableArrayList<RLLocationDetails>(
            RLYourWayArrayType.arrLocationDetails.toString())

        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf(0.0)
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf(0.0)
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf(0.0)
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf(0.0)
        val arrRevPercentage: MutableList<Double> = arrRevPercentageList?.toMutableList() ?: mutableListOf(0.0)
        val arrRevSecond: MutableList<Double> = arrRevSecondList?.toMutableList() ?: mutableListOf(0.0)
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf(0.0)
        val arrCumDistance: MutableList<Double> = arrCumDistanceList?.toMutableList() ?: mutableListOf(0.0)
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf(0.0)
        val arrCumSpeed: MutableList<Double> = arrCumSpeedList?.toMutableList() ?: mutableListOf(0.0)
        val arrAvgCadence: MutableList<Double> = arrAvgCadenceList?.toMutableList() ?: mutableListOf(0.0)
        val arrMaxRevPercentage: MutableList<Double> = arrMaxRevPercentageList?.toMutableList() ?: mutableListOf(0.0)

        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLHeartRateSensorWorkoutSessionDetailsModel() //Session Details Data
        val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()//summery Data Entry


        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories= arrBurntCalories?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrCadence= arrCadence?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrCumDistance=arrCumDistance?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrCumElevation=arrCumElevation?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrCumSpeed=arrCumSpeed?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrDistance= arrDistance?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrElevation=arrElevation?: mutableListOf(0.0)
        entryWorkoutSessionDetails.arrHRRecordedSecond= arrHRRecordedSecond?: mutableListOf(0)
        entryWorkoutSessionDetails.arrHr= arrHr?: mutableListOf(0)
        entryWorkoutSessionDetails.arrRevPercentage=arrRevPercentage?: mutableListOf(0.0) //ready value
        entryWorkoutSessionDetails.arrRevSecond=arrRevSecond?: mutableListOf(0.0) //ready value
        entryWorkoutSessionDetails.arrSpeed= arrSpeed?: mutableListOf(0.0)

        entryWorkoutSessionDetails.speedForOneKm= speedForOneKm?: mutableListOf(0.0)
        entryWorkoutSessionDetails.speedForOneMile= speedForOneMile?: mutableListOf(0.0)


        //Normal Entry Value
        entryWorkoutSessionDetails.MaxHrUsedForCalculation=RFMHR?:0
        entryWorkoutSessionDetails.MaxHrUsedForCalculation_Last=RFMHR?:0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation=RestingHR.toInt()?:0
        entryWorkoutSessionDetails.RestingHrUsedForCalculation_Last=RestingHR.toInt()?:0
        entryWorkoutSessionDetails.avgRevPercentage=avgRevPercentage?:0.0
        entryWorkoutSessionDetails.burntCalories=burntCalories?:0.0
        entryWorkoutSessionDetails.classDate=currentTimestamp?:"0"
        entryWorkoutSessionDetails.classDescription=""
        entryWorkoutSessionDetails.classImage=""
        entryWorkoutSessionDetails.className= fragBinding.edtSessionName.text.toString()?:""
        entryWorkoutSessionDetails.classNote= fragBinding.edtAddNotes.text.toString()?:""
        entryWorkoutSessionDetails.classType= yourWayType?:""
        entryWorkoutSessionDetails.demsElevation=-1
        entryWorkoutSessionDetails.distance=distance?:0.0
        entryWorkoutSessionDetails.goal=""
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage=maxRevPercentage?:0.0
        entryWorkoutSessionDetails.minRevPercentage=minRevPercentage?:0.0
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage=revPercentage?:0.0
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalElevation=totalElevation?:0.0
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=totalRev?:0.0
        entryWorkoutSessionDetails.totalSteps=totalSteps?:0
        entryWorkoutSessionDetails.totalTime= totalTime.toInt()?:0
        entryWorkoutSessionDetails.typeOfGoal=yourWayType?:""
        //Zone Entry Value
        val REVPer=revPercentage.roundToInt()
        when (RLzoneDiff(REVPer)){
            1 -> {
                //Zone 1
                entryWorkoutSessionDetails.zone1.remark= "android"
                entryWorkoutSessionDetails.zone1.distance=distance?:0.0
                entryWorkoutSessionDetails.zone1.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone1.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone1.totalRev=totalRev?:0.0

                //Zone 1 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone1.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone1.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone1.distance=distance?:0.0
                entryWorkoutSessionSummary.zone1.remark="android"
                entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone1.totalRev=totalRev?:0.0
            }
            2 -> {
                //Zone 2
                entryWorkoutSessionDetails.zone2.remark= "android"
                entryWorkoutSessionDetails.zone2.distance=distance?:0.0
                entryWorkoutSessionDetails.zone2.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone2.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone2.totalRev=totalRev?:0.0

                //Zone 2 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone2.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone2.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone2.avgSpeed=arrSpeed.average()
                entryWorkoutSessionSummary.zone2.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone2.distance=distance?:0.0
                entryWorkoutSessionSummary.zone2.remark="android"
                entryWorkoutSessionSummary.zone2.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone2.totalRev=totalRev?:0.0
            }
            3 -> {
                //Zone 3
                entryWorkoutSessionDetails.zone3.remark= "android"
                entryWorkoutSessionDetails.zone3.distance=distance?:0.0
                entryWorkoutSessionDetails.zone3.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone3.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone3.totalRev=totalRev?:0.0

                //Zone 3 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone3.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone3.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone3.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone3.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone3.distance=distance?:0.0
                entryWorkoutSessionSummary.zone3.remark="android"
                entryWorkoutSessionSummary.zone3.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone3.totalRev=totalRev?:0.0
            }
            4 -> {
                //Zone 4
                entryWorkoutSessionDetails.zone4.remark= "android"
                entryWorkoutSessionDetails.zone4.distance=distance?:0.0
                entryWorkoutSessionDetails.zone4.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone4.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone4.totalRev=totalRev?:0.0

                //Zone 4 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone4.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone4.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone4.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone4.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone4.distance=distance?:0.0
                entryWorkoutSessionSummary.zone4.remark="android"
                entryWorkoutSessionSummary.zone4.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone4.totalRev=totalRev?:0.0
            }
            5 -> {
                //Zone 5
                entryWorkoutSessionDetails.zone5.remark= "android"
                entryWorkoutSessionDetails.zone5.distance=distance?:0.0
                entryWorkoutSessionDetails.zone5.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone5.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone5.totalRev=totalRev?:0.0

                //Zone 5 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone5.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone5.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone5.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone5.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone5.distance=distance?:0.0
                entryWorkoutSessionSummary.zone5.remark="android"
                entryWorkoutSessionSummary.zone5.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone5.totalRev=totalRev?:0.0
            }
            6 -> {
                //Zone 6
                entryWorkoutSessionDetails.zone6.remark= "android"
                entryWorkoutSessionDetails.zone6.distance=distance?:0.0
                entryWorkoutSessionDetails.zone6.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone6.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone6.totalRev=totalRev?:0.0

                //Zone 6 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone6.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone6.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone6.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone6.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone6.distance=distance?:0.0
                entryWorkoutSessionSummary.zone6.remark="android"
                entryWorkoutSessionSummary.zone6.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone6.totalRev=totalRev?:0.0
            }
            7 -> {
                //Zone 7
                entryWorkoutSessionDetails.zone7.remark= "android"
                entryWorkoutSessionDetails.zone7.distance=distance?:0.0
                entryWorkoutSessionDetails.zone7.burntCalories=burntCalories?:0.0
                entryWorkoutSessionDetails.zone7.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone7.totalRev=totalRev?:0.0

                //Zone 7 WorkoutSessionSummary Data Entery
                entryWorkoutSessionSummary.zone7.avgCadence= arrCadence.average()?:0.0
                entryWorkoutSessionSummary.zone7.avgHr= arrHr?.average()?.roundToInt()?:0
                entryWorkoutSessionSummary.zone7.avgSpeed=arrSpeed.average()?:0.0
                entryWorkoutSessionSummary.zone7.burntCalories=burntCalories?:0.0
                entryWorkoutSessionSummary.zone7.distance=distance?:0.0
                entryWorkoutSessionSummary.zone7.remark="android"
                entryWorkoutSessionSummary.zone7.seconds=totalTime.toInt()?:0
                entryWorkoutSessionSummary.zone7.totalRev=totalRev?:0.0
            }
        }

        //AllWaySessionSummaryGraphData Entry
        val entrySessionSummaryGraphData = RLAllWaySessionSummaryGraphDataHRModel()
        entrySessionSummaryGraphData.arrHr=arrHr?: mutableListOf(0)
        entrySessionSummaryGraphData.arrCadence=arrCadence?: mutableListOf(0.0)
        entrySessionSummaryGraphData.arrSpeed=arrSpeed?: mutableListOf(0.0)
        entrySessionSummaryGraphData.arrRevPercentage=arrRevPercentage?: mutableListOf(0.0)

        //WorkoutSessionSummary Data Entery

        entryWorkoutSessionSummary.avgBurntCalories=arrBurntCalories.average()?:0.0
        entryWorkoutSessionSummary.avgCadence=arrCadence.average()?:0.0
        entryWorkoutSessionSummary.avgHr= arrHr?.average()?.roundToInt()?:0
        entryWorkoutSessionSummary.avgRevPercentage= arrRevPercentage.average()?:0.0
        entryWorkoutSessionSummary.avgSpeed=arrSpeed.average()?:0.0
        entryWorkoutSessionSummary.avgSpeedForOneKm=avgSpeedForOneKm
        entryWorkoutSessionSummary.avgSpeedForOneMile=avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories?:0.0
        entryWorkoutSessionSummary.classDate=currentTimestamp?:"0"
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()?:""
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()?:""
        entryWorkoutSessionSummary.classType= yourWayType?:""
        entryWorkoutSessionSummary.distance= distance?:0.0
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories?:0
        entryWorkoutSessionSummary.maxCadence=maxCadence?:0
        entryWorkoutSessionSummary.maxHr=maxHeartrate?:0
        entryWorkoutSessionSummary.maxRevPercentage=maxRevPercentage?:0.0
        entryWorkoutSessionSummary.maxSpeed=maxSpeed?:0
        entryWorkoutSessionSummary.maxSpeedForOneKm=maxSpeedForOneKm
        entryWorkoutSessionSummary.maxSpeedForOneMile=maxSpeedForOneMile
        entryWorkoutSessionSummary.minHr=minHeartrate?:0
        entryWorkoutSessionSummary.minRevPercentage=minRevPercentage?:0.0
        entryWorkoutSessionSummary.revPercentage=revPercentage?:0.0
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()?:0
        entryWorkoutSessionSummary.totalElevation=totalElevation?:0.0
        entryWorkoutSessionSummary.totalRev=totalRev?:0.0
        entryWorkoutSessionSummary.totalSteps=totalSteps?:0
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType?:""
        entryWorkoutSessionSummary.visibilityflagforthatsession=visibilityflagforthatsession?:0

        if (distance.isNaN() ){
            distance=0.0
        }



        //dataForTesting Entry
        var arrDataLocation1 =arrDataLocation
        var arrElevation1 =arrElevation?: mutableListOf(0)
        var arrLocationDetails1 =arrLocationDetails
        var arrSpeed1 =arrSpeed?: mutableListOf(0)

        if (arrDataLocation1.isNullOrEmpty()){
            val dataclass= RLElevationPoint(0.00,0.00,0.00)
            arrDataLocation1?.add(dataclass)
        }
        if (arrLocationDetails1.isNullOrEmpty()){
            val dataclass= RLLocationDetails(0.00,0.00,0.00,0.00,0.00,0.00)
            arrLocationDetails1?.add(dataclass)
        }

        /*val connectivityDataMap = hashMapOf(
            "cadence" to arrCadence,
            "connection" to arrConnection,
            "HR" to arrHr,
            "Power" to emptyList<Int>(),
            "Remark" to "android",
            "Speed" to arrSpeed)*/

        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)


        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation1,//ARRAY
            "status" to true)

        val gpxDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_TDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "elevation" to totalElevation,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_T_ServerDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace" to distance,
            "generatedDisntace_T" to 0,
            "generatedElevation" to -1,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android",
            "statusForElevationUpdate" to true)

        val gpx_T_Server_NDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace_T" to 0,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android")

        val locationDataMap = hashMapOf(
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to yourWayType,
            "elevationDic" to arrElevation1,//ARRAY
            "locationDic" to arrLocationDetails1,//ARRAY
            "speedDic" to arrSpeed1,//ARRAY
            "totalRev" to totalRev)

        //GhostData Entry
        val ghostDataEntry= RLGhostDataModel()
        ghostDataEntry.RestingHrUsedForCalculation_Last=RFMHR
        ghostDataEntry.arrAvgCadence=arrAvgCadence?: mutableListOf(0.0)
        ghostDataEntry.arrAvgHr=arrAvgHr?: mutableListOf(0)
        ghostDataEntry.arrAvgRevPercentage=arrAvgRevPercentage?: mutableListOf(0)
        ghostDataEntry.arrHr=arrHr?: mutableListOf(0)
        ghostDataEntry.arrMaxCadence=arrMaxCadence?: mutableListOf(0)
        ghostDataEntry.arrMaxHr=arrMaxHr?: mutableListOf(0)
        ghostDataEntry.arrMaxRevPercentage=arrMaxRevPercentage?: mutableListOf(0.0)
        ghostDataEntry.arrRevPercentage=arrRevPercentage?: mutableListOf(0.0)
        ghostDataEntry.arrRevSecond=arrRevSecond?: mutableListOf(0.0)
        ghostDataEntry.classDate=currentTimestamp.toLong()
        ghostDataEntry.displayImage=displayImage
        ghostDataEntry.displayName=displayName
        ghostDataEntry.location= ""
        ghostDataEntry.maxHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.maxHrUsedForCalculation_Last=RFMHR?:0
        ghostDataEntry.restingHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.timestamp=currentTimestamp.toLong()
        ghostDataEntry.totalRev=totalRev
        ghostDataEntry.totalTime=totalTime.toInt()?:0
        ghostDataEntry.visibilityflagforthatsession=visibilityflagforthatsession

        //val json=Gson()
        val json = GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .create()

        logLargeArray(TAG,"entry:- ",json.toJson(entryWorkoutSessionDetails))
        logLargeArray(TAG,"entryGraph:-",json.toJson(entrySessionSummaryGraphData))
        logLargeArray(TAG,"entrysummery:-",json.toJson(entryWorkoutSessionSummary))
        logLargeArray(TAG,"entryGhost:-",json.toJson(ghostDataEntry))
       RLTools.RlLogEPrint(TAG,"deviceRecordedData:- $deviceRecordedDataMap")
       RLTools.RlLogEPrint(TAG,"elevationData:- $elevationDataMap")
       RLTools.RlLogEPrint(TAG,"locationData:- $locationDataMap")


        RLHeartRateSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap,ghostDataEntry,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,gpx_T_Server_NDataMap)

    }
    private fun RlNoSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {
        val gpxString=requireArguments().getString("gpxStringBuilder")?:""
        val burntCalories=requireArguments().getDouble("burntCalories")?:0.0
        val totalRev=requireArguments().getDouble("totalRev")?:0.0
        val totalElevation=requireArguments().getDouble("totalElevation")?:0.0
        val totalSteps=requireArguments().getInt("totalSteps")?:0
        var distance=requireArguments().getDouble("distance")?:0.0
        val maxSpeed=requireArguments().getInt("maxSpeed")?:0
        val maxCadence=requireArguments().getInt("maxCadence")?:0
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")?:0

        val maxSpeedForOneKm=requireArguments().getDouble("maxSpeedForOneKm")?:0.0
        val maxSpeedForOneMile=requireArguments().getDouble("maxSpeedForOneMile")?:0.0
        val avgSpeedForOneKm=requireArguments().getDouble("avgSpeedForOneKm")?:0.0
        val avgSpeedForOneMile=requireArguments().getDouble("avgSpeedForOneMile")?:0.0

        val retrievedConnectionArray = requireArguments().getBooleanArray("arrConnection")
        val arrConnection: MutableList<Boolean> = retrievedConnectionArray?.toMutableList() ?: mutableListOf()

        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumDistance.toString())
        val arrCumSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumSpeed.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())

        val speedForOneMileList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneMile.toString())
        val speedForOneKmList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneKm.toString())
        val speedForOneKm: MutableList<Double> = speedForOneKmList?.toMutableList() ?: mutableListOf(0.0)
        val speedForOneMile: MutableList<Double> = speedForOneMileList?.toMutableList() ?: mutableListOf(0.0)

        val arrMaxCadence: ArrayList<Int>? = requireArguments().getIntegerArrayList(
            RLYourWayArrayType.arrMaxCadence.toString())
        val arrAvgCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrAvgCadence.toString())
        val arrAvgCadence: MutableList<Double> = arrAvgCadenceList?.toMutableList() ?: mutableListOf()

        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf()
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf()
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf()
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf()
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf()
        val arrCumDistance: MutableList<Double> = arrCumDistanceList?.toMutableList() ?: mutableListOf()
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf()
        val arrCumSpeed: MutableList<Double> = arrCumSpeedList?.toMutableList() ?: mutableListOf()

         val arrDataLocation = arguments?.getParcelableArrayList<RLElevationPoint>(
             RLYourWayArrayType.arrDataLocation.toString())
         val arrLocationDetails = arguments?.getParcelableArrayList<RLLocationDetails>(
             RLYourWayArrayType.arrLocationDetails.toString())

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
        entryWorkoutSessionDetails.speedForOneKm= speedForOneKm?: mutableListOf(0.0)
        entryWorkoutSessionDetails.speedForOneMile= speedForOneMile?: mutableListOf(0.0)


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
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage= 0 //calculation of REV persentage
        entryWorkoutSessionDetails.minRevPercentage= 0 //calculation of REV persentage
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage= 0 //last value of arrRevPercentage
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionDetails.totalElevation= totalElevation
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev= totalRev // sum of revpersentage
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
        entryWorkoutSessionSummary.avgSpeedForOneKm=avgSpeedForOneKm
        entryWorkoutSessionSummary.avgSpeedForOneMile=avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories
        entryWorkoutSessionSummary.classDate=currentTimestamp
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.distance= distance
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories
        entryWorkoutSessionSummary.maxCadence=maxCadence
        entryWorkoutSessionSummary.maxSpeed=maxSpeed
        entryWorkoutSessionSummary.maxSpeedForOneKm=maxSpeedForOneKm
        entryWorkoutSessionSummary.maxSpeedForOneMile=maxSpeedForOneMile
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionSummary.totalElevation=totalElevation
        entryWorkoutSessionSummary.totalSteps=totalSteps
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType
        entryWorkoutSessionSummary.totalRev=totalRev
        entryWorkoutSessionSummary.visibilityflagforthatsession=visibilityflagforthatsession


        //Zone 1 WorkoutSessionSummary Data Entery
        entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()
        entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()
        entryWorkoutSessionSummary.zone1.burntCalories=burntCalories
        entryWorkoutSessionSummary.zone1.distance=distance
        entryWorkoutSessionSummary.zone1.remark="android"
        entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0

        if (distance.isNaN() ){
            distance=0.0
        }

        //dataForTesting Entry
        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)

        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation,//ARRAY
            "status" to true)
        val gpxDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_TDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "elevation" to totalElevation,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_T_ServerDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace" to distance,
            "generatedDisntace_T" to 0,
            "generatedElevation" to -1,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android",
            "statusForElevationUpdate" to true)

        val gpx_T_Server_NDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace_T" to 0,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android")

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
        ghostDataEntry.RestingHrUsedForCalculation_Last=RFMHR
        ghostDataEntry.arrAvgCadence=arrAvgCadence
        ghostDataEntry.arrMaxCadence=arrMaxCadence!!
        ghostDataEntry.classDate=currentTimestamp.toLong()
        ghostDataEntry.displayImage=displayImage
        ghostDataEntry.displayName=displayName
        ghostDataEntry.location= ""
        ghostDataEntry.maxHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.maxHrUsedForCalculation_Last=RFMHR?:0
        ghostDataEntry.restingHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.timestamp=currentTimestamp.toLong()
        ghostDataEntry.totalTime=totalTime.toInt()?:0
        ghostDataEntry.totalRev=totalRev?:0.0
        ghostDataEntry.visibilityflagforthatsession=visibilityflagforthatsession


        RLNoSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap,ghostDataEntry,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,gpx_T_Server_NDataMap)
    }
    private fun RlSpeedSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {
        val gpxString=requireArguments().getString("gpxStringBuilder")?:""
        val burntCalories=requireArguments().getDouble("burntCalories")?:0.0
        val totalRev=requireArguments().getDouble("totalRev")?:0.0
        val totalElevation=requireArguments().getDouble("totalElevation")?:0.0
        val totalSteps=requireArguments().getInt("totalSteps")?:0
        var distance=requireArguments().getDouble("distance")?:0.0
        val maxSpeed=requireArguments().getInt("maxSpeed")?:0
        val maxCadence=requireArguments().getInt("maxCadence")?:0
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")?:0

        val maxSpeedForOneKm=requireArguments().getDouble("maxSpeedForOneKm")?:0.0
        val maxSpeedForOneMile=requireArguments().getDouble("maxSpeedForOneMile")?:0.0
        val avgSpeedForOneKm=requireArguments().getDouble("avgSpeedForOneKm")?:0.0
        val avgSpeedForOneMile=requireArguments().getDouble("avgSpeedForOneMile")?:0.0

        val retrievedConnectionArray = requireArguments().getBooleanArray("arrConnection")
        val arrConnection: MutableList<Boolean> = retrievedConnectionArray?.toMutableList() ?: mutableListOf()

        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrElevation.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumElevationList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumElevation.toString())

        val speedForOneMileList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneMile.toString())
        val speedForOneKmList=arguments?.getDoubleArray(RLYourWayArrayType.speedForOneKm.toString())
        val speedForOneKm: MutableList<Double> = speedForOneKmList?.toMutableList() ?: mutableListOf(0.0)
        val speedForOneMile: MutableList<Double> = speedForOneMileList?.toMutableList() ?: mutableListOf(0.0)

        val arrMaxCadence: ArrayList<Int>? = requireArguments().getIntegerArrayList(
            RLYourWayArrayType.arrMaxCadence.toString())
        val arrAvgCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrAvgCadence.toString())
        val arrAvgCadence: MutableList<Double> = arrAvgCadenceList?.toMutableList() ?: mutableListOf()


        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf()
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf()
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf()
        val arrElevation: MutableList<Double> = arrElevationList?.toMutableList() ?: mutableListOf()
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf()
        val arrCumElevation: MutableList<Double> = arrCumElevationList?.toMutableList() ?: mutableListOf()

        val arrDataLocation = arguments?.getParcelableArrayList<RLElevationPoint>(RLYourWayArrayType.arrDataLocation.toString())
        val arrLocationDetails = arguments?.getParcelableArrayList<RLLocationDetails>(
            RLYourWayArrayType.arrLocationDetails.toString())


        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val entryWorkoutSessionDetails = RLSpeedSensorWorkoutSessionDetailsModel()

        //Array Entry Value
        entryWorkoutSessionDetails.arrBurntCalories= arrBurntCalories
        entryWorkoutSessionDetails.arrCadence=arrCadence
        entryWorkoutSessionDetails.arrDistance= arrDistance
        entryWorkoutSessionDetails.arrElevation=arrElevation
        entryWorkoutSessionDetails.arrSpeed= arrSpeed
        entryWorkoutSessionDetails.arrCumElevation=arrCumElevation
        entryWorkoutSessionDetails.speedForOneKm= speedForOneKm?: mutableListOf(0.0)
        entryWorkoutSessionDetails.speedForOneMile= speedForOneMile?: mutableListOf(0.0)

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
        entryWorkoutSessionDetails.mapGeneratedUrl=""
        entryWorkoutSessionDetails.maxRevPercentage=0
        entryWorkoutSessionDetails.minRevPercentage=0
        entryWorkoutSessionDetails.remark="android"
        entryWorkoutSessionDetails.revPercentage=0
        entryWorkoutSessionDetails.timestamp=currentTimestamp.toInt()
        entryWorkoutSessionDetails.totalElevation=totalElevation
        entryWorkoutSessionDetails.totalPower=0
        entryWorkoutSessionDetails.totalRev=totalRev
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
        entryWorkoutSessionSummary.avgSpeedForOneKm=avgSpeedForOneKm
        entryWorkoutSessionSummary.avgSpeedForOneMile=avgSpeedForOneMile
        entryWorkoutSessionSummary.burntCalories=burntCalories
        entryWorkoutSessionSummary.classDate=currentTimestamp
        entryWorkoutSessionSummary.className= fragBinding.edtSessionName.text.toString()
        entryWorkoutSessionSummary.classNote= fragBinding.edtAddNotes.text.toString()
        entryWorkoutSessionSummary.classType= yourWayType
        entryWorkoutSessionSummary.distance= distance
        entryWorkoutSessionSummary.maxBurntCalories=maxBurntCalories
        entryWorkoutSessionSummary.maxCadence=maxCadence
        entryWorkoutSessionSummary.maxSpeed=maxSpeed
        entryWorkoutSessionSummary.maxSpeedForOneKm=maxSpeedForOneKm
        entryWorkoutSessionSummary.maxSpeedForOneMile=maxSpeedForOneMile
        entryWorkoutSessionSummary.timestamp=currentTimestamp.toLong()
        entryWorkoutSessionSummary.totalElevation=totalElevation
        entryWorkoutSessionSummary.totalSteps=totalSteps
        entryWorkoutSessionSummary.totalTime=totalTime.toInt()?:0
        entryWorkoutSessionSummary.typeOfGoal=yourWayType
        entryWorkoutSessionSummary.totalRev=totalRev
        entryWorkoutSessionSummary.visibilityflagforthatsession=visibilityflagforthatsession

        //Zone 1 WorkoutSessionSummary Data Entery
        entryWorkoutSessionSummary.zone1.avgCadence= arrCadence.average()
        entryWorkoutSessionSummary.zone1.avgSpeed=arrSpeed.average()
        entryWorkoutSessionSummary.zone1.burntCalories=burntCalories
        entryWorkoutSessionSummary.zone1.distance=distance
        entryWorkoutSessionSummary.zone1.remark="android"
        entryWorkoutSessionSummary.zone1.seconds=totalTime.toInt()?:0

        if (distance.isNaN() ){
            distance=0.0
        }

        //dataForTesting Entry
        val deviceRecordedDataMap = hashMapOf(
            "distance" to distance,
            "elevation" to totalElevation)

        val elevationDataMap = hashMapOf(
            "data" to arrDataLocation,//ARRAY
            "status" to true)
        val gpxDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_TDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "elevation" to totalElevation,
            "gpxString" to gpxString,
            "remark" to "android")

        val gpx_T_ServerDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace" to distance,
            "generatedDisntace_T" to 0,
            "generatedElevation" to -1,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android",
            "statusForElevationUpdate" to true)

        val gpx_T_Server_NDataMap = hashMapOf(
            "classDate" to currentTimestamp,
            "generatedDisntace_T" to 0,
            "generatedElevation_T" to 0,
            "gpxString" to gpxString,
            "remark" to "android")

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
        ghostDataEntry.RestingHrUsedForCalculation_Last=RFMHR
        ghostDataEntry.arrAvgCadence=arrAvgCadence
        ghostDataEntry.arrMaxCadence=arrMaxCadence!!
        ghostDataEntry.classDate=currentTimestamp.toLong()
        ghostDataEntry.displayImage=displayImage
        ghostDataEntry.displayName=displayName
        ghostDataEntry.location= ""
        ghostDataEntry.maxHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.maxHrUsedForCalculation_Last=RFMHR?:0
        ghostDataEntry.restingHrUsedForCalculation=RFMHR?:0
        ghostDataEntry.timestamp=currentTimestamp.toLong()
        ghostDataEntry.totalTime=totalTime.toInt()?:0
        ghostDataEntry.totalRev=totalRev?:0.0
        ghostDataEntry.visibilityflagforthatsession=visibilityflagforthatsession

        RLSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails,entrySessionSummaryGraphData,entryWorkoutSessionSummary,
            deviceRecordedDataMap,elevationDataMap,locationDataMap,ghostDataEntry,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,gpx_T_Server_NDataMap)

    }

    fun logLargeArray(tag: String,message:String, jsonString: String) {
        val maxLogSize = 1000  // Android's limit for log entry length
        var start = 0
        while (start < jsonString.length) {
            val end = minOf(start + maxLogSize, jsonString.length)
           RLTools.RlLogEPrint(tag, "$message  ${jsonString.substring(start, end)}")
            start = end
        }
    }
    //Entry to Firebase
    private fun RLNoSensorUserSessionDetailData(entry: RLNoSensorWorkoutSessionDetailsModel, entryGraph: RLAllWaySessionSummaryGraphDataModel, entrysummery: RLWorkoutSessionSummaryModel,
                                                deviceRecordedData: Any, elevationData: Any, locationData:Any, entryGhost: RLGhostDataModel,
                                                gpxDataMap:Any, gpx_TDataMap:Any, gpx_T_ServerDataMap:Any, gpx_T_Server_NDataMap:Any) {

        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"
        val gpxDataPath="/proposedstructure/dataForTesting/$currentUser/gpx"
        val gpx_TDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T"
        val gpx_T_ServerDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server"
        val gpx_T_Server_NDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server_N"
        val connectivityDataPath="/proposedstructure/dataForTesting/$currentUser/connectivity"

        /* databaseManager.RlWriteData(connectivityDataPath,connectivityDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting connectivity Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting connectivity Entry:- $error")
            }
        }*/

        databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful deviceRecordedData Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error deviceRecordedData Entry:- $error")
            }
        }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful elevation Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(gpxDataPath,gpxDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_TDataPath,gpx_TDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_ServerDataPath,gpx_T_ServerDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_Server_NDataPath,gpx_T_Server_NDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server_N Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server_N Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful  location Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error  location Entry:- $error")
            }
        }

        val justRide_=yourWayType+"_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/lastForClass")
        databaseRefGhostLast.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData LastForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry GhostData bestForClass Walk_justRide_
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/bestForClass")
        databaseRefGhostBest.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData bestForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryData")

                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
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
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryGraphData")
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                    }
                }
        }


        //Entry Session Detail Data
        // val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val databaseRef = FirebaseDatabase.getInstance().getReference("/${RLConstants.PROPOSEDSTRUCTURE}/${RLConstants.REVOOLAUSERSESSIONDETAILDATA}/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
       RLTools.RlLogEPrint(TAG,"print when call sessionDetail api ${Gson().toJson(entry)}")
        entryId.let {
            databaseRef.child(it).setValue(entry.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionDetailData!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionDetailData :- ${ task.exception}")
                    }
                }
        }
    }
    private fun RLHeartRateSensorUserSessionDetailData(entry: RLHeartRateSensorWorkoutSessionDetailsModel, entryGraph: RLAllWaySessionSummaryGraphDataHRModel, entrysummery: RLWorkoutSessionSummaryModel,
                                                       deviceRecordedData:Any, elevationData:Any, locationData:Any, entryGhost: RLGhostDataModel,
                                                       gpxDataMap:Any, gpx_TDataMap:Any, gpx_T_ServerDataMap:Any, gpx_T_Server_NDataMap:Any) {

        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"
        val gpxDataPath="/proposedstructure/dataForTesting/$currentUser/gpx"
        val gpx_TDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T"
        val gpx_T_ServerDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server"
        val gpx_T_Server_NDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server_N"
        val connectivityDataPath="/proposedstructure/dataForTesting/$currentUser/connectivity"

        /* databaseManager.RlWriteData(connectivityDataPath,connectivityDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting connectivity Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting connectivity Entry:- $error")
            }
        }*/

        databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful deviceRecordedData Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error deviceRecordedData Entry:- $error")
            }
        }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful elevation Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(gpxDataPath,gpxDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_TDataPath,gpx_TDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_ServerDataPath,gpx_T_ServerDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_Server_NDataPath,gpx_T_Server_NDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server_N Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server_N Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful  location Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error  location Entry:- $error")
            }
        }

        val justRide_=yourWayType+"_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/lastForClass")
        databaseRefGhostLast.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData LastForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/bestForClass")
        databaseRefGhostBest.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData bestForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryData")

                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
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
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryGraphData")
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                    }
                }
        }

        //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionDetailData!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionDetailData :- ${ task.exception}")
                    }
                }
        }


    }
    private fun RLSpeedSensorUserSessionDetailData(entry: RLSpeedSensorWorkoutSessionDetailsModel, entryGraph: RLAllWaySessionSummaryGraphDataModel, entrysummery: RLWorkoutSessionSummaryModel,
                                                   deviceRecordedData:Any, elevationData:Any, locationData:Any, entryGhost: RLGhostDataModel,
                                                   gpxDataMap:Any, gpx_TDataMap:Any, gpx_T_ServerDataMap:Any, gpx_T_Server_NDataMap:Any) {

        //dataForTesting Entry
        val databaseManager = RLDatabaseManagerWrite()
        val deviceRecordedDataPath="/proposedstructure/dataForTesting/$currentUser/deviceRecordedData"
        val elevationDataPath="/proposedstructure/dataForTesting/$currentUser/elevation"
        val locationDataPath="/proposedstructure/dataForTesting/$currentUser/location"
        val gpxDataPath="/proposedstructure/dataForTesting/$currentUser/gpx"
        val gpx_TDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T"
        val gpx_T_ServerDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server"
        val gpx_T_Server_NDataPath="/proposedstructure/dataForTesting/$currentUser/gpx_T_Server_N"
        val connectivityDataPath="/proposedstructure/dataForTesting/$currentUser/connectivity"

        /* databaseManager.RlWriteData(connectivityDataPath,connectivityDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting connectivity Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting connectivity Entry:- $error")
            }
        }*/

        databaseManager.RlWriteData(deviceRecordedDataPath,deviceRecordedData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful deviceRecordedData Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error deviceRecordedData Entry:- $error")
            }
        }
        databaseManager.RlWriteData(elevationDataPath,elevationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful elevation Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(gpxDataPath,gpxDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_TDataPath,gpx_TDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_ServerDataPath,gpx_T_ServerDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server Entry:- $error")
            }
        }

        databaseManager.RlWriteData(gpx_T_Server_NDataPath,gpx_T_Server_NDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server_N Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server_N Entry:- $error")
            }
        }
        databaseManager.RlWriteData(locationDataPath,locationData) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful  location Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error  location Entry:- $error")
            }
        }
        val justRide_=yourWayType+"_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/lastForClass")
        databaseRefGhostLast.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData LastForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/bestForClass")
        databaseRefGhostBest.child(justRide_).setValue(entryGhost.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData bestForClass saved successfully!")

                } else {
                   RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(entrysummery.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionSummaryData!")

                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
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
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionSummaryGraphData!")
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                    }
                }
        }

       //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry.toMap())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionDetailData!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionDetailData :- ${ task.exception}")
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
               RLTools.RlLogEPrint("FirebaseStorage", "Image upload failed:- $e")
            }
    }
    private fun RLRevoolaUserSessionSummaryData(entry: RLWorkoutSessionSummaryModel) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(entry)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry :- ${ task.exception}")
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
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                       RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry :- ${ task.exception}")
                    }
                }
        }
    }

    //revoolaUserSessionDetailData Model to Map
    fun RLNoSensorWorkoutSessionDetailsModel.toMap(): Map<String, Any?> {
        return mapOf(
            "MaxHrUsedForCalculation" to MaxHrUsedForCalculation,
            "MaxHrUsedForCalculation_Last" to MaxHrUsedForCalculation_Last,
            "RestingHrUsedForCalculation" to RestingHrUsedForCalculation,
            "RestingHrUsedForCalculation_Last" to RestingHrUsedForCalculation_Last,
            "arrBurntCalories" to arrBurntCalories,
            "arrCadence" to arrCadence,
            "arrCumDistance" to arrCumDistance,
            "arrCumSpeed" to arrCumSpeed,
            "arrDistance" to arrDistance,
            "arrElevation" to arrElevation,
            "arrPower" to arrPower,
            "arrPowerFromDevice" to arrPowerFromDevice,
            "arrSpeed" to arrSpeed,
            "speedForOneKm" to speedForOneKm,
            "speedForOneMile" to speedForOneMile,
            "arrCumElevation" to arrCumElevation,
            "avgRevPercentage" to avgRevPercentage,
            "burntCalories" to burntCalories,
            "classDate" to classDate,
            "classDescription" to classDescription,
            "classImage" to classImage,
            "className" to className,
            "classNote" to classNote,
            "classType" to classType,
            "demsElevation" to demsElevation,
            "distance" to distance,
            "goal" to goal,
            "isClass" to isClass,
            "isPowerDeviceConnected" to isPowerDeviceConnected,
            "mapGeneratedUrl" to mapGeneratedUrl,
            "maxRevPercentage" to maxRevPercentage,
            "minRevPercentage" to minRevPercentage,
            "remark" to remark,
            "revPercentage" to revPercentage,
            "timestamp" to timestamp,
            "totalElevation" to totalElevation,
            "totalPower" to totalPower,
            "totalRev" to totalRev,
            "totalSteps" to totalSteps,
            "totalTime" to totalTime,
            "typeOfGoal" to typeOfGoal,
            "zone1" to zone1.toMap(),
            "zone2" to zone2.toMap(),
            "zone3" to zone3.toMap(),
            "zone4" to zone4.toMap(),
            "zone5" to zone5.toMap(),
            "zone6" to zone6.toMap(),
            "zone7" to zone7.toMap()
        )
    }
    fun RLNoSensorWorkoutSessionDetailsModel.ZoneNew.toMap(): Map<String, Any?> {
        return mapOf(
            "burntCalories" to burntCalories,
            "distance" to distance,
            "remark" to remark,
            "seconds" to seconds,
            "totalRev" to totalRev
        )
    }

    //revoolaUserSessionSummaryData  Model to Map
    fun RLWorkoutSessionSummaryModel.toMap(): Map<String, Any?> {
        return mapOf(
            "avgBurntCalories" to avgBurntCalories,
            "avgCadence" to avgCadence,
            "avgHr" to avgHr,
            "avgPower" to avgPower,
            "avgPowerFromDevice" to avgPowerFromDevice,
            "avgRevPercentage" to avgRevPercentage,
            "avgSpeed" to avgSpeed,
            "avgSpeedForOneKm" to avgSpeedForOneKm,
            "avgSpeedForOneMile" to avgSpeedForOneMile,
            "burntCalories" to burntCalories,
            "classDate" to classDate,
            "classDescription" to classDescription,
            "classImage" to classImage,
            "className" to className,
            "classNote" to classNote,
            "classType" to classType,
            "demsElevation" to demsElevation,
            "distance" to distance,
            "goal" to goal,
            "isClass" to isClass,
            "isPowerDeviceConnected" to isPowerDeviceConnected,
            "maxBurntCalories" to maxBurntCalories,
            "maxCadence" to maxCadence,
            "maxHr" to maxHr,
            "maxPower" to maxPower,
            "maxPowerFromDevice" to maxPowerFromDevice,
            "maxRevPercentage" to maxRevPercentage,
            "maxSpeed" to maxSpeed,
            "maxSpeedForOneKm" to maxSpeedForOneKm,
            "maxSpeedForOneMile" to maxSpeedForOneMile,
            "minHr" to minHr,
            "minRevPercentage" to minRevPercentage,
            "remark" to remark,
            "revPercentage" to revPercentage,
            "timestamp" to timestamp,
            "totalElevation" to totalElevation,
            "totalPower" to totalPower,
            "totalRev" to totalRev,
            "totalSteps" to totalSteps,
            "totalTime" to totalTime,
            "typeOfGoal" to typeOfGoal,
            "visibilityflagforthatsession" to visibilityflagforthatsession,
            "zone1" to zone1.toMap(),
            "zone2" to zone2.toMap(),
            "zone3" to zone3.toMap(),
            "zone4" to zone4.toMap(),
            "zone5" to zone5.toMap(),
            "zone6" to zone6.toMap(),
            "zone7" to zone7.toMap()
        )
    }
    fun RLWorkoutSessionSummaryModel.Zone.toMap(): Map<String, Any?> {
        return mapOf(
            "avgCadence" to avgCadence,
            "avgHr" to avgHr,
            "avgPower" to avgPower,
            "avgPowerFromDevice" to avgPowerFromDevice,
            "avgSpeed" to avgSpeed,
            "burntCalories" to burntCalories,
            "distance" to distance,
            "remark" to remark,
            "seconds" to seconds,
            "totalRev" to totalRev
        )
    }

    //ghostForClass / bestForClass and lastForClass Model to Map
    fun RLGhostDataModel.toMap(): Map<String, Any?> {
        return mapOf(
            "RestingHrUsedForCalculation_Last" to RestingHrUsedForCalculation_Last,
            "arrAvgCadence" to arrAvgCadence,
            "arrAvgHr" to arrAvgHr,
            "arrAvgPower" to arrAvgPower,
            "arrAvgRevPercentage" to arrAvgRevPercentage,
            "arrHr" to arrHr,
            "arrMaxCadence" to arrMaxCadence,
            "arrMaxHr" to arrMaxHr,
            "arrMaxPower" to arrMaxPower,
            "arrMaxRevPercentage" to arrMaxRevPercentage,
            "arrPower" to arrPower,
            "arrPowerFromDevice" to arrPowerFromDevice,
            "arrRevPercentage" to arrRevPercentage,
            "arrRevSecond" to arrRevSecond,
            "classDate" to classDate,
            "displayImage" to displayImage,
            "displayName" to displayName,
            "flagImage" to flagImage,
            "flagName" to flagName,
            "isPowerDeviceConnected" to isPowerDeviceConnected,
            "location" to location,
            "maxHrUsedForCalculation" to maxHrUsedForCalculation,
            "maxHrUsedForCalculation_Last" to maxHrUsedForCalculation_Last,
            "restingHrUsedForCalculation" to restingHrUsedForCalculation,
            "timestamp" to timestamp,
            "totalRev" to totalRev,
            "totalTime" to totalTime,
            "visibilityflagforthatsession" to visibilityflagforthatsession
        )
    }

    //revoolaUserSessionDetailData Model to Map
    fun RLHeartRateSensorWorkoutSessionDetailsModel.toMap(): Map<String, Any?> {
        return mapOf(
            "MaxHrUsedForCalculation" to MaxHrUsedForCalculation,
            "MaxHrUsedForCalculation_Last" to MaxHrUsedForCalculation_Last,
            "RestingHrUsedForCalculation" to RestingHrUsedForCalculation,
            "RestingHrUsedForCalculation_Last" to RestingHrUsedForCalculation_Last,
            "arrBurntCalories" to arrBurntCalories,
            "arrCadence" to arrCadence,
            "arrCumDistance" to arrCumDistance,
            "arrCumElevation" to arrCumElevation,
            "arrCumSpeed" to arrCumSpeed,
            "arrDistance" to arrDistance,
            "arrElevation" to arrElevation,
            "arrHRRecordedSecond" to arrHRRecordedSecond,
            "arrHr" to arrHr,
            "arrPower" to arrPower,
            "arrPowerFromDevice" to arrPowerFromDevice,
            "arrRevPercentage" to arrRevPercentage,
            "arrRevSecond" to arrRevSecond,
            "arrSpeed" to arrSpeed,
            "speedForOneKm" to speedForOneKm,
            "speedForOneMile" to speedForOneMile,
            "avgRevPercentage" to avgRevPercentage,
            "burntCalories" to burntCalories,
            "classDate" to classDate,
            "classDescription" to classDescription,
            "classImage" to classImage,
            "className" to className,
            "classNote" to classNote,
            "classType" to classType,
            "demsElevation" to demsElevation,
            "distance" to distance,
            "goal" to goal,
            "isClass" to isClass,
            "isPowerDeviceConnected" to isPowerDeviceConnected,
            "mapGeneratedUrl" to mapGeneratedUrl,
            "maxRevPercentage" to maxRevPercentage,
            "minRevPercentage" to minRevPercentage,
            "remark" to remark,
            "revPercentage" to revPercentage,
            "timestamp" to timestamp,
            "totalElevation" to totalElevation,
            "totalPower" to totalPower,
            "totalRev" to totalRev,
            "totalSteps" to totalSteps,
            "totalTime" to totalTime,
            "typeOfGoal" to typeOfGoal,
            "zone1" to zone1.toMap(),
            "zone2" to zone2.toMap(),
            "zone3" to zone3.toMap(),
            "zone4" to zone4.toMap(),
            "zone5" to zone5.toMap(),
            "zone6" to zone6.toMap(),
            "zone7" to zone7.toMap()
        )
    }
    fun RLHeartRateSensorWorkoutSessionDetailsModel.ZoneNew.toMap(): Map<String, Any?> {
        return mapOf(
            "burntCalories" to burntCalories,
            "distance" to distance,
            "remark" to remark,
            "seconds" to seconds,
            "totalRev" to totalRev
        )
    }

    //revoolaUserSessionDetailData Model to Map
    fun RLSpeedSensorWorkoutSessionDetailsModel.toMap(): Map<String, Any?> {
        return mapOf(
            "MaxHrUsedForCalculation" to MaxHrUsedForCalculation,
            "MaxHrUsedForCalculation_Last" to MaxHrUsedForCalculation_Last,
            "RestingHrUsedForCalculation" to RestingHrUsedForCalculation,
            "RestingHrUsedForCalculation_Last" to RestingHrUsedForCalculation_Last,
            "arrBurntCalories" to arrBurntCalories,
            "arrCadence" to arrCadence,
            "arrCumElevation" to arrCumElevation,
            "arrDistance" to arrDistance,
            "arrElevation" to arrElevation,
            "arrPower" to arrPower,
            "arrPowerFromDevice" to arrPowerFromDevice,
            "arrSpeed" to arrSpeed,
            "speedForOneKm" to speedForOneKm,
            "speedForOneMile" to speedForOneMile,
            "avgRevPercentage" to avgRevPercentage,
            "burntCalories" to burntCalories,
            "classDate" to classDate,
            "classDescription" to classDescription,
            "classImage" to classImage,
            "className" to className,
            "classNote" to classNote,
            "classType" to classType,
            "demsElevation" to demsElevation,
            "distance" to distance,
            "goal" to goal,
            "isClass" to isClass,
            "isPowerDeviceConnected" to isPowerDeviceConnected,
            "mapGeneratedUrl" to mapGeneratedUrl,
            "maxRevPercentage" to maxRevPercentage,
            "minRevPercentage" to minRevPercentage,
            "remark" to remark,
            "revPercentage" to revPercentage,
            "timestamp" to timestamp,
            "totalElevation" to totalElevation,
            "totalPower" to totalPower,
            "totalRev" to totalRev,
            "totalSteps" to totalSteps,
            "totalTime" to totalTime,
            "typeOfGoal" to typeOfGoal,
            "zone1" to zone1.toMap(),
            "zone2" to zone2.toMap(),
            "zone3" to zone3.toMap(),
            "zone4" to zone4.toMap(),
            "zone5" to zone5.toMap(),
            "zone6" to zone6.toMap(),
            "zone7" to zone7.toMap()
        )
    }
    fun RLSpeedSensorWorkoutSessionDetailsModel.ZoneNew.toMap(): Map<String, Any?> {
        return mapOf(
            "burntCalories" to burntCalories,
            "distance" to distance,
            "remark" to remark,
            "seconds" to seconds,
            "totalRev" to totalRev
        )
    }

}