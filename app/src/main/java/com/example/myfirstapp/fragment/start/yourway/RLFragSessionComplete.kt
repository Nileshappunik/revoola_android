package com.example.myfirstapp.fragment.start.yourway

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.enumclass.RLYourWayArrayType
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.model.RLHeartRateSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLNoSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.model.RLSpeedSensorWorkoutSessionDetailsModel
import com.example.myfirstapp.model.RLWorkoutSessionSummaryModel
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

        val entryWorkoutSessionDetails = RLHeartRateSensorWorkoutSessionDetailsModel()

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
            }
            REVPer <= 50 -> {
                //Zone 2
                entryWorkoutSessionDetails.zone2.remark= "android"
                entryWorkoutSessionDetails.zone2.distance=distance
                entryWorkoutSessionDetails.zone2.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone2.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone2.totalRev=totalRev
            }
            REVPer <= 60 -> {
                //Zone 3
                entryWorkoutSessionDetails.zone3.remark= "android"
                entryWorkoutSessionDetails.zone3.distance=distance
                entryWorkoutSessionDetails.zone3.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone3.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone3.totalRev=totalRev
            }
            REVPer <= 70 -> {
                //Zone 4
                entryWorkoutSessionDetails.zone4.remark= "android"
                entryWorkoutSessionDetails.zone4.distance=distance
                entryWorkoutSessionDetails.zone4.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone4.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone4.totalRev=totalRev
            }
            REVPer <= 80 -> {
                //Zone 5
                entryWorkoutSessionDetails.zone5.remark= "android"
                entryWorkoutSessionDetails.zone5.distance=distance
                entryWorkoutSessionDetails.zone5.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone5.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone5.totalRev=totalRev
            }
            REVPer <= 90 -> {
                //Zone 6
                entryWorkoutSessionDetails.zone6.remark= "android"
                entryWorkoutSessionDetails.zone6.distance=distance
                entryWorkoutSessionDetails.zone6.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone6.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone6.totalRev=totalRev
            }
            REVPer <= 100 -> {
                //Zone 7
                entryWorkoutSessionDetails.zone7.remark= "android"
                entryWorkoutSessionDetails.zone7.distance=distance
                entryWorkoutSessionDetails.zone7.burntCalories=burntCalories
                entryWorkoutSessionDetails.zone7.seconds=totalTime.toInt()?:0
                entryWorkoutSessionDetails.zone7.totalRev=totalRev
            }
        }

        RLHeartRateSensorUserSessionDetailData(entryWorkoutSessionDetails)
    }
    private fun RlNoSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {

        val burntCalories=requireArguments().getDouble("burntCalories")
        val totalElevation=requireArguments().getDouble("totalElevation")
        val totalSteps=requireArguments().getInt("totalSteps")
        val distance=requireArguments().getDouble("distance")

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

        RLNoSensorUserSessionDetailData(entryWorkoutSessionDetails)

        //val entryWorkoutSessionSummary = RLWorkoutSessionSummaryModel()
       // RLWorkoutSessionSummaryData(entryWorkoutSessionSummary)
    }
    private fun RlSpeedSensorDataEntryToFirebase(yourWayType: String, totalTime: String) {

        val burntCalories=requireArguments().getDouble("burntCalories")
        val totalElevation=requireArguments().getDouble("totalElevation")
        val totalSteps=requireArguments().getInt("totalSteps")
        val distance=requireArguments().getDouble("distance")

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

        RLSpeedSensorUserSessionDetailData(entryWorkoutSessionDetails)


    }
    //Entry to Firebase
    private fun RLNoSensorUserSessionDetailData(entry: RLNoSensorWorkoutSessionDetailsModel) {
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
    private fun RLHeartRateSensorUserSessionDetailData(entry: RLHeartRateSensorWorkoutSessionDetailsModel) {
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
    private fun RLSpeedSensorUserSessionDetailData(entry: RLSpeedSensorWorkoutSessionDetailsModel) {
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
    private fun RLWorkoutSessionSummaryData(entry: RLWorkoutSessionSummaryModel){
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