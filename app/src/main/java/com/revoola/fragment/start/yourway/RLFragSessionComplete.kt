package com.revoola.fragment.start.yourway

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlFragSessionCompleteBinding
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.adapter.RLSelectedImagesAdapter
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLUsernameV2
import com.revoola.model.RLYourWayApiPayload
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import gun0912.tedimagepicker.builder.TedImagePicker
import java.io.Serializable
import kotlin.math.roundToInt

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""
    private var displayImage =""
    private var displayName =""
    private var visibilityflagforthatsession:Int =0
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

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
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        if (currentUser.isEmpty()){
            currentUser=RLAuthManager().RlgetCurrentUser()?.uid?:""
        }
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val cardData = requireArguments().getSerializable("cardData") as RLSessionDataTransferModel
        fragBinding.edtSessionName.setText("${cardData.yourWayType} Session")
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
            if(isAdded){
                RLBaseProgress.RLShowProgressDialog(requireActivity())
            }
            RLMakeSensorData(cardData)
        }
    }
    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }

    private fun RLMakeSensorData(cardData: RLSessionDataTransferModel) {

        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        val deviceRecordedDataMap = hashMapOf(
            RevoolaKeys.distance to cardData.distance,
            RevoolaKeys.elevation to cardData.totalElevation)

        val elevationDataMap = hashMapOf(
            RevoolaKeys.data to cardData.arrDataLocation,
            RevoolaKeys.status to true)

        val gpxDataMap = hashMapOf(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android")

        val gpx_TDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.elevation to cardData.totalElevation,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android")

        val gpx_T_ServerDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.generatedDisntace to cardData.distance,
            RevoolaKeys.generatedDisntace_T to 0,
            RevoolaKeys.generatedElevation to -1,
            RevoolaKeys.generatedElevation_T to 0,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android",
            RevoolaKeys.statusForElevationUpdate to true)

        val gpx_T_Server_NDataMap = hashMapOf<String,Any>(
            RevoolaKeys.classDate to currentTimestamp,
            RevoolaKeys.generatedDisntace_T to 0,
            RevoolaKeys.generatedElevation_T to 0,
            RevoolaKeys.gpxString to cardData.gpxStringBuilder,
            RevoolaKeys.remark to "android")

        val locationDataMap = hashMapOf(
            RevoolaKeys.className to fragBinding.edtSessionName.text.toString(),
            RevoolaKeys.classNote to fragBinding.edtAddNotes.text.toString(),
            RevoolaKeys.classType to cardData.yourWayType,
            RevoolaKeys.elevationDic to cardData.arrElevation,//ARRAY
            RevoolaKeys.locationDic to cardData.arrLocationDetails,//ARRAY
            RevoolaKeys.speedDic to cardData.arrSpeed,//ARRAY
            RevoolaKeys.totalRev to cardData.totalRev)

        val ghostDataMap = hashMapOf(
            RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RFMHR,
            RevoolaKeys.arrAvgCadence to cardData.arrAvgCadence,
            RevoolaKeys.arrAvgHr to cardData.arrAvgHr,
            RevoolaKeys.arrAvgPower to  mutableListOf(0),
            RevoolaKeys.arrAvgRevPercentage to cardData.arrAvgRevPercentage,
            RevoolaKeys.arrHr to cardData.arrHr,
            RevoolaKeys.arrMaxCadence to cardData.arrMaxCadence,
            RevoolaKeys.arrMaxHr to cardData.arrMaxHr,
            RevoolaKeys.arrMaxPower to mutableListOf(0),
            RevoolaKeys.arrMaxRevPercentage to cardData.arrMaxRevPercentage,
            RevoolaKeys.arrPower to mutableListOf(0),
            RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
            RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage,
            RevoolaKeys.arrRevSecond to cardData.arrRevSecond,
            RevoolaKeys.classDate to currentTimestamp.toLong(),
            RevoolaKeys.displayImage to displayImage,
            RevoolaKeys.displayName to displayName,
            RevoolaKeys.flagImage to  "flag-of-United-Kingdom.png",
            RevoolaKeys.flagName to "United Kingdom",
            RevoolaKeys.isPowerDeviceConnected to  false,
            RevoolaKeys.location to "",
            RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
            RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
            RevoolaKeys.restingHrUsedForCalculation to cardData.RFMHR,
            RevoolaKeys.timestamp to currentTimestamp.toLong(),
            RevoolaKeys.totalRev to cardData.totalRev,
            RevoolaKeys.totalTime to cardData.totalTime.toInt(),
            RevoolaKeys.visibilityflagforthatsession to visibilityflagforthatsession
        )

        val summaryDataMap = hashMapOf(
            RevoolaKeys.arrBurntCalories to safeNumber(cardData.arrBurntCalories.average()),
            RevoolaKeys.arrCadence to safeNumber(cardData.arrCadence.average()),
            RevoolaKeys.arrHr to safeNumber(cardData.arrHr.average()),
            RevoolaKeys.avgPower to  0,
            RevoolaKeys.avgPowerFromDevice to  0,
            RevoolaKeys.avgRevPercentage to safeNumber(cardData.arrRevPercentage.average()),
            RevoolaKeys.avgSpeed to safeNumber(cardData.arrSpeed.average()),
            RevoolaKeys.avgSpeedForOneKm to  cardData.avgSpeedForOneKm,
            RevoolaKeys.avgSpeedForOneMile to  cardData.avgSpeedForOneMile,
            RevoolaKeys.burntCalories to  cardData.burntCalories,
            RevoolaKeys.classDate to  currentTimestamp,
            RevoolaKeys.classDescription to  "",
            RevoolaKeys.classImage to  "",
            RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
            RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
            RevoolaKeys.classType to  cardData.yourWayType,
            RevoolaKeys.demsElevation to  -1,
            RevoolaKeys.distance to  cardData.distance,
            RevoolaKeys.goal to  "",
            RevoolaKeys.isClass to  false,
            RevoolaKeys.isPowerDeviceConnected to  false,
            RevoolaKeys.maxBurntCalories to  cardData.maxBurntCalories,
            RevoolaKeys.maxCadence to  cardData.maxCadence,
            RevoolaKeys.maxHr to  cardData.maxHeartRate,
            RevoolaKeys.maxPower to  0,
            RevoolaKeys.maxPowerFromDevice to  0,
            RevoolaKeys.maxRevPercentage to  cardData.maxRevPercentage,
            RevoolaKeys.maxSpeed to  cardData.maxSpeed,
            RevoolaKeys.maxSpeedForOneKm to  cardData.maxSpeedForOneKm,
            RevoolaKeys.maxSpeedForOneMile to  cardData.maxSpeedForOneMile,
            RevoolaKeys.minHr to  cardData.minHeartRate,
            RevoolaKeys.minRevPercentage to  cardData.minRevPercentage,
            RevoolaKeys.remark to  "android",
            RevoolaKeys.revPercentage to  cardData.revPercentage,
            RevoolaKeys.timestamp to  currentTimestamp.toLong(),
            RevoolaKeys.totalElevation to  cardData.totalElevation,
            RevoolaKeys.totalPower to  0,
            RevoolaKeys.totalRev to  cardData.totalRev,
            RevoolaKeys.totalSteps to  cardData.totalSteps,
            RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
            RevoolaKeys.typeOfGoal to  cardData.yourWayType,
            RevoolaKeys.visibilityflagforthatsession to  visibilityflagforthatsession,


            RevoolaKeys.Zone1 to cardData.zoneDataSummery[RevoolaKeys.Zone1],
            RevoolaKeys.Zone2 to cardData.zoneDataSummery[RevoolaKeys.Zone2],
            RevoolaKeys.Zone3 to cardData.zoneDataSummery[RevoolaKeys.Zone3],
            RevoolaKeys.Zone4 to cardData.zoneDataSummery[RevoolaKeys.Zone4],
            RevoolaKeys.Zone5 to cardData.zoneDataSummery[RevoolaKeys.Zone5],
            RevoolaKeys.Zone6 to cardData.zoneDataSummery[RevoolaKeys.Zone6],
            RevoolaKeys.Zone7 to cardData.zoneDataSummery[RevoolaKeys.Zone7]
        )

        val graphDataMapHeart = hashMapOf(
            RevoolaKeys.arrCadence to cardData.arrCadence,
            RevoolaKeys.arrPower to mutableListOf(0.0),
            RevoolaKeys.arrHr to cardData.arrHr,
            RevoolaKeys.arrSpeed to cardData.arrSpeed,
            RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage,
            RevoolaKeys.remark to "android",
        )

        val graphDataMapSpeedAndNoSensor = hashMapOf(
            RevoolaKeys.arrCadence to cardData.arrCadence,
            RevoolaKeys.arrPower to mutableListOf(0.0),
            RevoolaKeys.arrSpeed to cardData.arrSpeed,
            RevoolaKeys.remark to "android",
        )

        when (cardData.SENSOR){
            RLConstants.HEART_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),
                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumDistance to cardData.arrCumDistance,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,
                    RevoolaKeys.arrCumSpeed to cardData.arrCumSpeed,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrHRRecordedSecond to  cardData.arrHRRecordedSecond,
                    RevoolaKeys.arrHr to  cardData.arrHr,
                    RevoolaKeys.arrRevPercentage to cardData.arrRevPercentage ,
                    RevoolaKeys.arrRevSecond to cardData.arrRevSecond,
                    RevoolaKeys.arrSpeed to  cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,
                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to -1,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to "",
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(deviceRecordedDataMap,elevationDataMap,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,
                    gpx_T_Server_NDataMap,locationDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapHeart,cardData,currentTimestamp)
            }
            RLConstants.SPEED_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),

                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrPower to mutableListOf(0),
                    RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
                    RevoolaKeys.arrSpeed to cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,

                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to -1,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to "",
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(deviceRecordedDataMap,elevationDataMap,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,
                    gpx_T_Server_NDataMap,locationDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapSpeedAndNoSensor,cardData,currentTimestamp)

            }
            RLConstants.NO_SENSOR->{
                val detailsDataMap = hashMapOf(
                    RevoolaKeys.maxHrUsedForCalculation to cardData.RFMHR,
                    RevoolaKeys.maxHrUsedForCalculation_Last to cardData.RFMHR,
                    RevoolaKeys.restingHrUsedForCalculation to cardData.RestingHR.toInt(),
                    RevoolaKeys.restingHrUsedForCalculation_Last to cardData.RestingHR.toInt(),

                    RevoolaKeys.arrBurntCalories to  cardData.arrBurntCalories,
                    RevoolaKeys.arrCadence to  cardData.arrCadence,
                    RevoolaKeys.arrCumDistance to  cardData.arrCumDistance,
                    RevoolaKeys.arrCumSpeed to  cardData.arrCumSpeed,
                    RevoolaKeys.arrDistance to  cardData.arrDistance,
                    RevoolaKeys.arrElevation to cardData.arrElevation,
                    RevoolaKeys.arrPower to mutableListOf(0),
                    RevoolaKeys.arrPowerFromDevice to mutableListOf(0),
                    RevoolaKeys.arrSpeed to cardData.arrSpeed,
                    RevoolaKeys.speedForOneKm to  cardData.speedForOneKm,
                    RevoolaKeys.speedForOneMile to  cardData.speedForOneMile,
                    RevoolaKeys.arrCumElevation to cardData.arrCumElevation,

                    RevoolaKeys.avgRevPercentage to cardData.avgRevPercentage,
                    RevoolaKeys.burntCalories to cardData.burntCalories,
                    RevoolaKeys.classDate to currentTimestamp,
                    RevoolaKeys.classDescription to "",
                    RevoolaKeys.classImage to "",
                    RevoolaKeys.className to  fragBinding.edtSessionName.text.toString(),
                    RevoolaKeys.classNote to  fragBinding.edtAddNotes.text.toString(),
                    RevoolaKeys.classType to  cardData.yourWayType,
                    RevoolaKeys.demsElevation to -1,
                    RevoolaKeys.distance to cardData.distance,
                    RevoolaKeys.goal to "",
                    RevoolaKeys.isClass to false,
                    RevoolaKeys.isPowerDeviceConnected to false,
                    RevoolaKeys.mapGeneratedUrl to "",
                    RevoolaKeys.maxRevPercentage to cardData.maxRevPercentage,
                    RevoolaKeys.minRevPercentage to cardData.minRevPercentage,
                    RevoolaKeys.remark to "android",
                    RevoolaKeys.revPercentage to cardData.revPercentage,
                    RevoolaKeys.timestamp to currentTimestamp.toInt(),
                    RevoolaKeys.totalElevation to cardData.totalElevation,
                    RevoolaKeys.totalPower to 0,
                    RevoolaKeys.totalRev to cardData.totalRev,
                    RevoolaKeys.totalSteps to cardData.totalSteps,
                    RevoolaKeys.totalTime to  cardData.totalTime.toInt(),
                    RevoolaKeys.typeOfGoal to cardData.yourWayType,

                    RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                    RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                    RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                    RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                    RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                    RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                    RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                RLFirebaseEntry(deviceRecordedDataMap,elevationDataMap,gpxDataMap,gpx_TDataMap,gpx_T_ServerDataMap,
                    gpx_T_Server_NDataMap,locationDataMap,ghostDataMap,summaryDataMap,detailsDataMap,graphDataMapSpeedAndNoSensor,cardData,currentTimestamp)

            }
        }
    }

    private fun RLFirebaseEntry(
        deviceRecordedDataMap: HashMap<String, Double>,
        elevationDataMap: HashMap<String, Any>,
        gpxDataMap: HashMap<String, String>,
        gpx_TDataMap: HashMap<String, Any>,
        gpx_T_ServerDataMap: HashMap<String, Any>,
        gpx_T_Server_NDataMap: HashMap<String, Any>,
        locationDataMap: HashMap<String, Any>,
        ghostDataMap: HashMap<String, Any>,
        summaryDataMap: HashMap<String, Serializable?>,
        detailsDataMap: HashMap<String, Any?>,
        graphDataMap: HashMap<String, Any>,
        cardData: RLSessionDataTransferModel,
        currentTimestamp: String
    ) {
        //Firebase  Entry
        val databaseManager = RLDatabaseManagerWrite()


        /* databaseManager.RlWriteData(RevoolaFirebasePath.connectivityDataPath(currentUser),connectivityDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting connectivity Entry")
            }else {
               RLTools.RlLogEPrint(TAG,"Error dataForTesting connectivity Entry:- $error")
            }
        }*/

        databaseManager.RlWriteData(RevoolaFirebasePath.deviceRecordedDataPath(currentUser),deviceRecordedDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful deviceRecordedData Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error deviceRecordedData Entry:- $error")
            }
        }
        databaseManager.RlWriteData(RevoolaFirebasePath.elevationDataPath(currentUser),elevationDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful elevation Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error elevation Entry:- $error")
            }
        }
        databaseManager.RlWriteData(RevoolaFirebasePath.gpxDataPath(currentUser),gpxDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx Entry:- $error")
            }
        }

        databaseManager.RlWriteData(RevoolaFirebasePath.gpx_TDataPath(currentUser),gpx_TDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T Entry:- $error")
            }
        }

        databaseManager.RlWriteData(RevoolaFirebasePath.gpx_T_ServerDataPath(currentUser),gpx_T_ServerDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server Entry:- $error")
            }
        }

        databaseManager.RlWriteData(RevoolaFirebasePath.gpx_T_Server_NDataPath(currentUser),gpx_T_Server_NDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful dataForTesting gpx_T_Server_N Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error dataForTesting gpx_T_Server_N Entry:- $error")
            }
        }
        databaseManager.RlWriteData(RevoolaFirebasePath.locationDataPath(currentUser),locationDataMap) { success, error ->
            if (success) {
                RLTools.RlLogDPrint(TAG,"Successful  location Entry")
            }else {
                RLTools.RlLogEPrint(TAG,"Error  location Entry:- $error")
            }
        }

        val justRide_=cardData.yourWayType+"_justRide_"

        //Entry GhostData lastForClass Walk_justRide_
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostLastForClassDataPath(currentUser))
        databaseRefGhostLast.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData LastForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData LastForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostBestForClassDataPath(currentUser))
        databaseRefGhostBest.child(justRide_).setValue(ghostDataMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.RlLogDPrint("FirebaseDatabase", "Entry  GhostData bestForClass saved successfully!")

                } else {
                    RLTools.RlLogEPrint("FirebaseDatabase", "Failed  GhostData bestForClass to save entry :- ${ task.exception}")
                }
            }

        //Entry Summery
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.summaryDataPath(currentUser))
        val entryIdSummery = (System.currentTimeMillis() / 1000).toString()
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(summaryDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryData")

                    } else {
                        RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryData :- ${ task.exception}")
                    }
                }
        }

        //entry Graph Data
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference( RevoolaFirebasePath.graphDataPath(currentUser))
        val entryIdGraph = (System.currentTimeMillis() / 1000).toString()
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(graphDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully! revoolaUserSessionSummaryGraphData")
                    } else {
                        RLTools.RlLogEPrint("FirebaseDatabase", "Failed to save entry revoolaUserSessionSummaryGraphData :- ${ task.exception}")
                    }
                }
        }

        //entry session detail data
        val databaseRef = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.detailDataPath(currentUser))
        val entryId = (System.currentTimeMillis() / 1000).toString()
        entryId.let {
            databaseRef.child(it).setValue(detailsDataMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.RlLogDPrint("FirebaseDatabase", "Entry saved successfully revoolaUserSessionDetailData!")
                        RLInsertApiCall(cardData,currentTimestamp)
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

    private fun RLInsertApiCall(cardData: RLSessionDataTransferModel, currentTimestamp: String) {
        if (RLApiClientRetrofit.RLisConnected()) {
            val jsonPayload = createPayload(cardData,currentTimestamp)
            val request = Gson().fromJson(jsonPayload, Array<RLYourWayApiPayload>::class.java).toList()

            RLTools.RlLogDPrint(TAG,"YourWay Insert Request: $request")
            //Insert Api Call
            viewModel.RLInsertYourWayData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogDPrint(TAG, "YourWay Insert Success= " + response.type)
                            RLBottomHideShowSet(true)
                            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)

                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogDPrint(TAG, "YourWay Insert Fail= " + response.type)
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogDPrint(TAG, "YourWay Insert Catch= " + e.message)

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogDPrint(TAG, "YourWay Insert Error= " + error.message)
                }
            }
        }

    }

    private fun createPayload(cardData: RLSessionDataTransferModel, currentTimestamp: String): String {
        val classLeaderboard = RLClassLeaderboard(
            userId = currentUser,
            classId = "",
            timestamp = currentTimestamp,
            timestampLocal = currentTimestamp,
            totalRev = cardData.totalRev,
            visibilityFlagForThatSession = visibilityflagforthatsession,
            discipline = cardData.yourWayType,
            duration = cardData.totalTime,
            calories = cardData.burntCalories,
            bmo = 2,
            rmm = 0,
            rms = 0,
            source = "android",
            goal = "all")

        val usernameV2 = RLUsernameV2(
            userId = currentUser,
            avatar = cardData.userModel!!.displayImage,
            username = cardData.userModel!!.displayName,
            accessToken = "",
            firstName = cardData.userModel!!.firstName,
            lastName = cardData.userModel!!.lastName,
            email = cardData.userModel!!.emailId,
            currentGroup = cardData.userModel?.currentGroup?:"premium")

      //  val apiPayload = listOf(RLYourWayApiPayload(classLeaderboard,usernameV2))
        val apiPayload = listOf(RLYourWayApiPayload(classLeaderboard))

        // Convert to JSON String
        return Gson().toJson(apiPayload)
    }

}


/*if (imgUriList.size>0){
               RLuploadImagesToFirebase(imgUriList)
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
   }*/