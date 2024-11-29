package com.example.myfirstapp.fragment.start.classes

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
import com.example.myfirstapp.firebaseModel.RLElevationPoint
import com.example.myfirstapp.firebaseModel.RLLocationDetails
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession
import com.example.myfirstapp.fragment.start.adapter.RLSelectedImagesAdapter
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.model.RLRevoolaUsersSettingsModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import gun0912.tedimagepicker.builder.TedImagePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RLFragClassWorkoutComplete : RLBaseFragment(){
    val TAG: String = RLFragClassWorkoutComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    private var imgUriList = mutableListOf<Uri>()
    private var currentUser =""
    private var wsWeight="60"
    private var wsHeight="167"
    private var wsAge=25
    private var gender="Male"
    private var RFMHR=191
    private var RestingHR="50"
    private var displayImage =""
    private var displayName =""
    private var visibilityflagforthatsession:Int =0

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragClassWorkoutComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragClassWorkoutComplete" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLFirebaseToFatchUserData()
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.edtSessionName.setText(VideoCardData.rideTitle)
        fragBinding.txtMainTitle.setText("ACTIVITY COMPLETE!")

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

        fragBinding.imgCancle.setOnClickListener {
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false,null, false)
        }
        fragBinding.inlayButton.commonButton.setText(R.string.save)
        fragBinding.inlayButton.commonButton.setOnClickListener {
            val classType=  requireArguments().getString(RLConstants.CLASSTYPE,"")
            val sensorType=  requireArguments().getString(RLConstants.HEARTSENSOR,"")
            val totalTime=  requireArguments().getString("totalTime","0")
            val heartRateList: ArrayList<Int>? = requireArguments().getIntegerArrayList("heartRateList")
            if (classType.equals(RLConstants.BODY)){

                val speedArray = arguments?.getDoubleArray("speedList")
                val distanceArray = arguments?.getDoubleArray("distanceList")
                val activeCaloriesArray = arguments?.getDoubleArray("activeCaloriesList")

                val climbedList: ArrayList<Int>? = requireArguments().getIntegerArrayList("climbedList")
                val speedList: MutableList<Double> = speedArray?.toMutableList() ?: mutableListOf()
                val distanceList: MutableList<Double> = distanceArray?.toMutableList() ?: mutableListOf()
                val activeCaloriesList: MutableList<Double> = activeCaloriesArray?.toMutableList() ?: mutableListOf()

                if (sensorType.equals(RLConstants.HEARTSENSOR)){
                    RLBodyFirebaseDataPrepaire("HEART_SENSOR",VideoCardData)
                }else  {
                    RLBodyFirebaseDataPrepaire("NO_SENSOR",VideoCardData)
                }
            }else{
               // RlMindNoSensorDataEntryToFirebase(classType,totalTime,VideoCardData,heartRateList)
            }

        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
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
                    imageListVisible(true)
                }else{
                    imageListVisible(false)
                }
                fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
                    imgUriList.remove(uri)
                    if (imgUriList.size>0){
                        imageListVisible(true)
                    }else{
                        imageListVisible(false)
                    }
                }
                fragBinding.rvSelectedImages.adapter = selectedImagesAdapter

            }
    }
    private fun imageListVisible(isVisible: Boolean){
        if (isVisible){
            fragBinding.rvSelectedImages.visibility=View.VISIBLE
            fragBinding.txtAddPhoto.visibility=View.GONE
        }else{
            fragBinding.rvSelectedImages.visibility=View.GONE
            fragBinding.txtAddPhoto.visibility=View.VISIBLE
        }
    }

    //ALL BODY DATA TO FIREABSE ENTRY
    private fun RLBodyFirebaseDataPrepaire(sensorType:String,videoCardData:RLFulllVideoModel){
        var sessionUserSessionDetailData= hashMapOf<String, Any>()
        var sessionUserSessionSummaryGraphData= hashMapOf<String, Any>()
        var sessionGhostForClassBestForClass= hashMapOf<String, Any>()
        var sessionGhostForClassLastForClass= hashMapOf<String, Any>()
        val classDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())//"20241128105036"
        val totalTime=  requireArguments().getString("totalTime","0")
        val videoID=  requireArguments().getString("videoID","")
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val avgRevPercentage=requireArguments().getDouble("avgRevPercentage")?:0.0
        val burntCalories=requireArguments().getDouble("burntCalories")?:0.0
        var distance=requireArguments().getDouble("distance")?:0.0
        val maxRevPercentage=requireArguments().getDouble("maxRevPercentage")?:0.0
        val minRevPercentage=requireArguments().getDouble("minRevPercentage")?:0.0
        val revPercentage=requireArguments().getDouble("revPercentage")?:0.0
        val totalRev=requireArguments().getDouble("totalRev")?:0.0
        val maxSpeed=requireArguments().getInt("maxSpeed")?:0
        val maxHr=requireArguments().getInt("maxHeartrate")?:0
        val maxCadence=requireArguments().getInt("maxCadence")?:0
        val maxBurntCalories=requireArguments().getInt("maxBurntCalories")?:0
        val minHr=requireArguments().getInt("minHeartrate")?:0
        val avgBurntCalories=requireArguments().getDouble("avgBurntCalories")?:0.0
        val avgCadence=requireArguments().getDouble("avgCadence")?:0.0
        val avgHr=requireArguments().getDouble("avgHr")?:0.0
        val avgSpeed=requireArguments().getDouble("avgSpeed")?:0.0
        val maxSpeedForOneKm=requireArguments().getDouble("maxSpeedForOneKm")?:0.0
        val maxSpeedForOneMile=requireArguments().getDouble("maxSpeedForOneMile")?:0.0
        val avgSpeedForOneKm=requireArguments().getDouble("avgSpeedForOneKm")?:0.0
        val avgSpeedForOneMile=requireArguments().getDouble("avgSpeedForOneMile")?:0.0

        val arrHr: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrHr.toString())
        val arrPower: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrPower.toString())
        val arrPowerFromDevice: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrPowerFromDevice.toString())
        val arrAvgRevPercentage: ArrayList<Int>? = requireArguments().getIntegerArrayList(RLYourWayArrayType.arrAvgRevPercentage.toString())

        val arrBurntCaloriesList=arguments?.getDoubleArray(RLYourWayArrayType.arrBurntCalories.toString())
        val arrCadenceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCadence.toString())
        val arrDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrDistance.toString())
        val arrRevPercentageList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevPercentage.toString())
        val arrRevSecondList=arguments?.getDoubleArray(RLYourWayArrayType.arrRevSecond.toString())
        val arrSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrSpeed.toString())
        val arrCumDistanceList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumDistance.toString())
        val arrCumSpeedList=arguments?.getDoubleArray(RLYourWayArrayType.arrCumSpeed.toString())
        val arrMaxRevPercentageList=arguments?.getDoubleArray(RLYourWayArrayType.arrMaxRevPercentage.toString())


        val arrBurntCalories: MutableList<Double> = arrBurntCaloriesList?.toMutableList() ?: mutableListOf(0.0)
        val arrCadence: MutableList<Double> = arrCadenceList?.toMutableList() ?: mutableListOf(0.0)
        val arrDistance: MutableList<Double> = arrDistanceList?.toMutableList() ?: mutableListOf(0.0)
        val arrRevPercentage: MutableList<Double> = arrRevPercentageList?.toMutableList() ?: mutableListOf(0.0)
        val arrRevSecond: MutableList<Double> = arrRevSecondList?.toMutableList() ?: mutableListOf(0.0)
        val arrSpeed: MutableList<Double> = arrSpeedList?.toMutableList() ?: mutableListOf(0.0)
        val arrCumDistance: MutableList<Double> = arrCumDistanceList?.toMutableList() ?: mutableListOf(0.0)
        val arrCumSpeed: MutableList<Double> = arrCumSpeedList?.toMutableList() ?: mutableListOf(0.0)
        val arrMaxRevPercentage: MutableList<Double> = arrMaxRevPercentageList?.toMutableList() ?: mutableListOf(0.0)

        if (distance.isNaN() ){
            distance=0.0
        }
        when(sensorType){
            "HEART_SENSOR"->{
                 sessionUserSessionDetailData = hashMapOf(
                    "MaxHrUsedForCalculation" to RFMHR,
                    "MaxHrUsedForCalculation_Last" to RFMHR,
                    "RestingHrUsedForCalculation" to RestingHR,
                    "RestingHrUsedForCalculation_Last" to RestingHR,
                    "arrAvgRevPercentage" to  (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrBurntCalories" to (arrBurntCalories?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCadence" to (arrCadence?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumDistance" to (arrCumDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumSpeed" to (arrCumSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrDistance" to (arrDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to  (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to  (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrHr" to  (arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrRevSecond" to (arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "avgRevPercentage" to avgRevPercentage,
                    "burntCalories" to burntCalories,
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "distance" to distance,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "Ahmedabad",
                    "maxRevPercentage" to maxRevPercentage,
                    "minRevPercentage" to minRevPercentage,
                    "remark" to "Android",
                    "revPercentage" to revPercentage,
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to totalRev,
                    "totalTime" to totalTime,
                    "videoKey" to videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                    "zone1" to hashMapOf(
                        "burntCalories" to burntCalories,
                        "distance" to distance,
                        "remark" to "Android",
                        "seconds" to totalTime,
                        "totalRev" to totalRev
                    ),
                    "zone2" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone3" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone4" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone5" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone6" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone7" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    )
                )
                 sessionUserSessionSummaryGraphData = hashMapOf(
                    "arrCadence" to (arrCadence.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrHr" to (arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrAvgRevPercentage" to (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrHr" to (arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrRevSecond" to (arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to totalRev,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass =  hashMapOf(
                     "arrAvgPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrAvgRevPercentage" to (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrPowerFromDevice" to (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrHr" to (arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrRevSecond" to (arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrRevPercentage" to (arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "classDate" to classDate,
                     "displayImage" to displayImage,
                     "displayName" to displayName,
                     "flagImage" to "flag-of-United-Kingdom.png",
                     "flagName" to "United Kingdom",
                     "isPowerDeviceConnected" to false,
                     "location" to "",
                     "remark" to "Android",
                     "totalRev" to totalRev,
                     "visibilityflagforthatsession" to visibilityflagforthatsession
                 )
            }
            "NO_SENSOR"->{
                 sessionUserSessionDetailData = hashMapOf(
                    "MaxHrUsedForCalculation" to RFMHR,
                    "MaxHrUsedForCalculation_Last" to RFMHR,
                    "RestingHrUsedForCalculation" to RestingHR,
                    "RestingHrUsedForCalculation_Last" to RestingHR,
                    "arrAvgRevPercentage" to (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrBurntCalories" to (arrBurntCalories?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCadence" to (arrCadence?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumDistance" to (arrCumDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumSpeed" to (arrCumSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrDistance" to (arrDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "avgRevPercentage" to avgRevPercentage,
                    "burntCalories" to burntCalories,
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "distance" to distance,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "maxRevPercentage" to maxRevPercentage,
                    "minRevPercentage" to minRevPercentage,
                    "remark" to "Android",
                    "revPercentage" to revPercentage,
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to totalRev,
                    "totalTime" to totalTime,
                    "videoKey" to videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                    "zone1" to hashMapOf(
                        "burntCalories" to burntCalories,
                        "distance" to distance,
                        "remark" to "Android",
                        "seconds" to totalTime,
                        "totalRev" to totalRev
                    ),
                    "zone2" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone3" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone4" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone5" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone6" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    ),
                    "zone7" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to 0,
                        "totalRev" to 0
                    )
                )
                 sessionUserSessionSummaryGraphData = hashMapOf(
                    "arrCadence" to (arrCadence.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrAvgRevPercentage" to (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to totalRev,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass =  hashMapOf(
                     "arrAvgPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrAvgRevPercentage" to (arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrMaxRevPercentage" to (arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrPower" to (arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrPowerFromDevice" to (arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "classDate" to classDate,
                     "displayImage" to displayImage,
                     "displayName" to displayName,
                     "flagImage" to "flag-of-United-Kingdom.png",
                     "flagName" to "United Kingdom",
                     "isPowerDeviceConnected" to false,
                     "location" to "",
                     "remark" to "Android",
                     "totalRev" to totalRev,
                     "visibilityflagforthatsession" to visibilityflagforthatsession
                 )
            }
        }

        //revoola_UserSessionSummaryData  Prepaire
        val sessionUserSessionSummaryData = hashMapOf(
            "avgBurntCalories" to avgBurntCalories,
            "avgCadence" to avgCadence,
            "avgHr" to avgHr,
            "avgPower" to 0,
            "avgPowerFromDevice" to 0,
            "avgRevPercentage" to avgRevPercentage,
            "avgSpeed" to avgSpeed,
            "avgSpeedForOneKm" to avgSpeedForOneKm,
            "avgSpeedForOneMile" to avgSpeedForOneMile,
            "burntCalories" to burntCalories,
            "classDate" to classDate,
            "classDescription" to videoCardData.rideDescription,
            "classImage" to "",
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to videoCardData.classType,
            "demsElevation" to 0,
            "distance" to distance,
            "imageLinkLarge" to videoCardData.imageLinkLarge,
            "imageLinkSmall" to videoCardData.imageLinkSmall,
            "isClass" to true,
            "isPowerDeviceConnected" to false,
            "location" to "",
            "maxBurntCalories" to maxBurntCalories,
            "maxCadence" to maxCadence,
            "maxHr" to maxHr,
            "maxPower" to 0,
            "maxPowerFromDevice" to 0,
            "maxRevPercentage" to maxRevPercentage,
            "maxSpeed" to maxSpeed,
            "maxSpeedForOneKm" to maxSpeedForOneKm,
            "maxSpeedForOneMile" to maxSpeedForOneMile,
            "minHr" to minHr,
            "minRevPercentage" to minRevPercentage,
            "remark" to "Android",
            "revPercentage" to revPercentage,
            "rms" to 0,
            "timestamp" to currentTimestamp,
            "totalElevation" to 0,
            "totalPower" to 0,
            "totalRev" to totalRev,
            "totalTime" to totalTime,
            "videoKey" to videoID,
            "visibilityflagforthatsession" to visibilityflagforthatsession,
            "zone1" to hashMapOf(
                "avgCadence" to avgCadence,
                "avgHr" to avgHr,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to avgSpeed,
                "burntCalories" to burntCalories,
                "distance" to distance,
                "remark" to "Android",
                "seconds" to totalTime,
                "totalRev" to totalRev
            ),
            "zone2" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            ),
            "zone3" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            ),
            "zone4" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            ),
            "zone5" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            ),
            "zone6" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            ),
            "zone7" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to 0,
                "totalRev" to 0
            )
        )

        val sessionUserCompletedVideos = mapOf(videoID to true)

        //Entry GhostData lastForClass
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/lastForClass")
        databaseRefGhostLast.child(videoID).setValue(sessionGhostForClassLastForClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDatabase", "revoola_GhostData LastForClass Entry saved successfully!")

                } else {
                    Log.e("FirebaseDatabase", "revoola_GhostData LastForClass Entry Failed to save", task.exception)
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/ghostForClass/bestForClass")
        databaseRefGhostBest.child(videoID).setValue(sessionGhostForClassBestForClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDatabase", "revoola_GhostData BestForClass Entry saved successfully!")

                } else {
                    Log.e("FirebaseDatabase", "revoola_GhostData BestForClass Entry Failed to save", task.exception)
                }
            }

        //revoola_UserSessionSummaryData
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryData/$currentUser")
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(sessionUserSessionSummaryData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "revoola_UserSessionSummaryData Entry saved successfully!")

                    } else {
                        Log.e("FirebaseDatabase", "revoola_UserSessionSummaryData Entry Failed to save", task.exception)
                    }
                }
        }

        //revoola_UserSessionSummaryGraphData
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionSummaryGraphData/$currentUser")
        val entryIdGraph = (System.currentTimeMillis() / 1000).toString()
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(sessionUserSessionSummaryGraphData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "revoola_UserSessionSummaryGraphData Entry saved successfully!")
                    } else {
                        Log.e("FirebaseDatabase", "revoola_UserSessionSummaryGraphData Entry Failed to save", task.exception)
                    }
                }
        }

        //revoolaUserCompletedVideos
        val databaseRefCompletedVideos = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSettings/$currentUser/revoolaUserCompletedVideos")
        databaseRefCompletedVideos.updateChildren(sessionUserCompletedVideos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseDatabase", "revoola_UserCompletedVideos Entry saved successfully!")
                } else {
                    Log.e("FirebaseDatabase", "revoola_UserCompletedVideos Entry Failed to save", task.exception)
                }
            }

        //revoola_UserSessionDetailData
         val databaseRef = FirebaseDatabase.getInstance().getReference("/proposedstructure/revoolaUserSessionDetailData/$currentUser")
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(sessionUserSessionDetailData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "revoola_UserSessionDetailData Entry saved successfully!")
                        RLBottomHideShowSet(true)
                        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                        (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    } else {
                        Log.e("FirebaseDatabase", "revoola_UserSessionDetailData Entry Failed to save", task.exception)
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
        currentUser=userId
        val path ="/proposedstructure/revoolaUserSettings/$userId/basicData"
        databaseManager.RlreadData(path){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                wsWeight=userData.weightkg?:"60"
                wsHeight=userData.height?:"167"
                wsAge= RLTools.RLCalculateAge(userData.dob)
                gender=userData.gender
                RFMHR=userData.RFMHR
                RestingHR=userData.restingHr
                displayImage =userData.displayImage
                displayName =userData.displayName
            }
        }
    }




}