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
                    //RlBodyHeartRateDataEntryToFirebase(classType,totalTime,heartRateList,VideoCardData,climbedList!!,speedList,distanceList,activeCaloriesList)
                }else  {
                    RLBodyFirebaseDataPrepaire("NO_SENSOR",VideoCardData)
                    //RlBodyNoSensorDataEntryToFirebase(classType,totalTime,heartRateList,VideoCardData,climbedList!!,speedList,distanceList,activeCaloriesList)
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

        when(sensorType){
            "HEART_SENSOR"->{
                 sessionUserSessionDetailData = hashMapOf(
                    "MaxHrUsedForCalculation" to RFMHR,
                    "MaxHrUsedForCalculation_Last" to RFMHR,
                    "RestingHrUsedForCalculation" to RestingHR,
                    "RestingHrUsedForCalculation_Last" to RestingHR,
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrBurntCalories" to listOf(0, 0, 0, 0),
                    "arrCadence" to listOf(0, 0, 0, 0),
                    "arrCumDistance" to listOf(0, 0, 0, 0),
                    "arrCumSpeed" to listOf(0, 0, 0, 0),
                    "arrDistance" to listOf(0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0),
                    "arrSpeed" to listOf(0, 0, 0, 0),
                    "arrHr" to listOf(0, 0, 0, 0),
                    "arrRevSecond" to listOf(0, 0, 0, 0),
                    "arrRevPercentage" to listOf(0, 0, 0, 0),
                    "avgRevPercentage" to 0,
                    "burntCalories" to 22.712962282347366,
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "distance" to 0,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "Ahmedabad",
                    "maxRevPercentage" to 0,
                    "minRevPercentage" to 0,
                    "remark" to "Android",
                    "revPercentage" to 0,
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to 35.87709318525651,
                    "totalTime" to totalTime,
                    "videoKey" to videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                    "zone1" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to totalTime,
                        "totalRev" to 0
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
                    "arrCadence" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrHr" to listOf(0, 0, 0, 0, 0),
                    "arrSpeed" to listOf(0, 0, 0, 0, 0),
                    "arrRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to listOf(0, 0, 0, 0, 0),
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0, 0),
                    "arrHr" to listOf(0, 0, 0, 0, 0),
                    "arrRevSecond" to listOf(0, 0, 0, 0, 0),
                    "arrRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to 35.87709318525651,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass = hashMapOf(
                    "arrAvgPower" to listOf(0, 0, 0, 0, 0),
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0, 0),
                     "arrHr" to listOf(0, 0, 0, 0, 0),
                     "arrRevSecond" to listOf(0, 0, 0, 0, 0),
                     "arrRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to 35.87709318525651,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
            }
            "NO_SENSOR"->{
                 sessionUserSessionDetailData = hashMapOf(
                    "MaxHrUsedForCalculation" to RFMHR,
                    "MaxHrUsedForCalculation_Last" to RFMHR,
                    "RestingHrUsedForCalculation" to RestingHR,
                    "RestingHrUsedForCalculation_Last" to RestingHR,
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrBurntCalories" to listOf(0, 0, 0, 0),
                    "arrCadence" to listOf(0, 0, 0, 0),
                    "arrCumDistance" to listOf(0, 0, 0, 0),
                    "arrCumSpeed" to listOf(0, 0, 0, 0),
                    "arrDistance" to listOf(0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0),
                    "arrSpeed" to listOf(0, 0, 0, 0),
                    "avgRevPercentage" to 0,
                    "burntCalories" to 22.712962282347366,
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "distance" to 0,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "maxRevPercentage" to 0,
                    "minRevPercentage" to 0,
                    "remark" to "Android",
                    "revPercentage" to 0,
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to 35.87709318525651,
                    "totalTime" to totalTime,
                    "videoKey" to videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                    "zone1" to hashMapOf(
                        "burntCalories" to 0,
                        "distance" to 0,
                        "remark" to "Android",
                        "seconds" to totalTime,
                        "totalRev" to 0
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
                    "arrCadence" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrSpeed" to listOf(0, 0, 0, 0, 0),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to listOf(0, 0, 0, 0, 0),
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0, 0),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to 35.87709318525651,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass = hashMapOf(
                    "arrAvgPower" to listOf(0, 0, 0, 0, 0),
                    "arrAvgRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrMaxRevPercentage" to listOf(0, 0, 0, 0, 0),
                    "arrPower" to listOf(0, 0, 0, 0, 0),
                    "arrPowerFromDevice" to listOf(0, 0, 0, 0, 0),
                    "classDate" to classDate,
                    "displayImage" to displayImage,
                    "displayName" to displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to 35.87709318525651,
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
            }
        }

        val sessionUserSessionSummaryData = hashMapOf(
            "avgBurntCalories" to 0,
            "avgCadence" to 0,
            "avgHr" to 0,
            "avgPower" to 0,
            "avgPowerFromDevice" to 0,
            "avgRevPercentage" to 0,
            "avgSpeed" to 0,
            "avgSpeedForOneKm" to 0,
            "avgSpeedForOneMile" to 0,
            "burntCalories" to 22.712962282347366,
            "classDate" to classDate,
            "classDescription" to videoCardData.rideDescription,
            "classImage" to "",
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to videoCardData.classType,
            "demsElevation" to 0,
            "distance" to 0,
            "imageLinkLarge" to videoCardData.imageLinkLarge,
            "imageLinkSmall" to videoCardData.imageLinkSmall,
            "isClass" to true,
            "isPowerDeviceConnected" to false,
            "location" to "",
            "maxBurntCalories" to 0,
            "maxCadence" to 0,
            "maxHr" to 0,
            "maxPower" to 0,
            "maxPowerFromDevice" to 0,
            "maxRevPercentage" to 0,
            "maxSpeed" to 0,
            "maxSpeedForOneKm" to 0,
            "maxSpeedForOneMile" to 0,
            "minHr" to 0,
            "minRevPercentage" to 0,
            "remark" to "Android",
            "revPercentage" to 0,
            "rms" to 0,
            "timestamp" to currentTimestamp,
            "totalElevation" to 0,
            "totalPower" to 0,
            "totalRev" to 35.87709318525651,
            "totalTime" to totalTime,
            "videoKey" to videoID,
            "visibilityflagforthatsession" to visibilityflagforthatsession,
            "zone1" to hashMapOf(
                "avgCadence" to 0,
                "avgHr" to 0,
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to 0,
                "burntCalories" to 0,
                "distance" to 0,
                "remark" to "Android",
                "seconds" to totalTime,
                "totalRev" to 0
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

        val sessionUserCompletedVideos = hashMapOf(videoID to true)

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
        databaseRefCompletedVideos.setValue(sessionUserCompletedVideos)
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