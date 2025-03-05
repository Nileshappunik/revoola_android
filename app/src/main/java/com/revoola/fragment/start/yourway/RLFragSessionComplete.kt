package com.revoola.fragment.start.yourway

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLInsightlyApiPayload
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageApiPayload
import com.revoola.model.RLTextOverview
import com.revoola.model.RLUsernameV2
import com.revoola.model.RLYourWayApiPayload
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.Serializable
import java.util.Base64

class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    var imgUriList = mutableListOf<Uri>()
    var currentUser =""
    private var displayImage =""
    private var displayName =""
    private var visibilityflagforthatsession:Int =0
    private val MAX_IMAGE_SELECTION = 5
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
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val cardData = requireArguments().getSerializable("cardData") as RLSessionDataTransferModel
        fragBinding.edtSessionName.setText("${cardData.yourWayType} Session")
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
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
            //openImagePicker()
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
            val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
            //RLInsertOverviewApiCall(cardData,currentTimestamp)
            RLMakeSensorData(cardData)
        }
    }
    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }

    private fun safeIntNumber(value: Int?): Int {
        return if (value == null || value < 0 ) 0 else value
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

        val apiPayload = listOf(RLYourWayApiPayload(classLeaderboard))

        // Convert to JSON String
        return Gson().toJson(apiPayload)
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
                            RLInsertOverviewApiCall(cardData,currentTimestamp)
                            RLTools.RlLogDPrint(TAG, "YourWay Insert Success: ${response.text}")
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "YourWay Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "YourWay Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogEPrint(TAG, "YourWay Insert Error: ${error.localizedMessage}" )
                }
            }
        }

    }


    private fun createOverviewPayloadNew(cardData: RLSessionDataTransferModel, currentTimestamp: String): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()

        // Add text fields as form data
        requestBodyMap["data[myOverviewThumbnails][userid]"] = createRequestBody(currentUser)
        requestBodyMap["data[myOverviewThumbnails][className]"] = createRequestBody("${cardData.yourWayType} Session")
        requestBodyMap["data[myOverviewThumbnails][classType]"] = createRequestBody(cardData.yourWayType)
        requestBodyMap["data[myOverviewThumbnails][timestamp]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][timestamp_local]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][totalREV]"] = createRequestBody(safeNumber(cardData.totalRev).toString())
        requestBodyMap["data[myOverviewThumbnails][totalTime]"] = createRequestBody(cardData.totalTime.toString())

        val burntCalories = safeNumber(cardData.burntCalories) ?: 0
        requestBodyMap["data[myOverviewThumbnails][burntCalories]"] = createRequestBody(burntCalories.toString())

        requestBodyMap["data[myOverviewThumbnails][totalRMM]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][totalRMS]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][maxRevPercentage]"] = createRequestBody(safeNumber(cardData.maxRevPercentage).toString())
        requestBodyMap["data[myOverviewThumbnails][avgRevPercentage]"] = createRequestBody(safeNumber(cardData.avgRevPercentage).toString())

        requestBodyMap["data[myOverviewThumbnails][visibilityflagforthatsession]"] = createRequestBody(visibilityflagforthatsession.toString())
        requestBodyMap["data[myOverviewThumbnails][bmo]"] = createRequestBody("2")
        requestBodyMap["data[myOverviewThumbnails][instructor]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][duration]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][rideTitle]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][mainTitle]"] = createRequestBody(cardData.yourWayType)
        requestBodyMap["data[myOverviewThumbnails][goal]"] = createRequestBody("all")
        requestBodyMap["data[myOverviewThumbnails][medals_gold]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_silver]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_bronze]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][source]"] = createRequestBody("android")
        requestBodyMap["data[myOverviewThumbnails][from_third_party_source]"] = createRequestBody("0")

        // Handle map image
        val mapImage = ""// myOverviewThumbnails["mapImage"] as? String
        if (!mapImage.isNullOrEmpty()) {
            val mapImageRequestBody = base64ToRequestBody(mapImage)
            requestBodyMap["mapImage"] = mapImageRequestBody
        }

        return requestBodyMap
    }
    // Convert text to RequestBody
    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    // Convert Base64 image to RequestBody
    private fun base64ToRequestBody(base64String: String): RequestBody {
        val decodedBytes = Base64.getDecoder().decode(base64String.split(",")[1])
        return decodedBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
    }
    private fun RLInsertOverviewApiCall(cardData: RLSessionDataTransferModel, currentTimestamp: String) {
        if (RLApiClientRetrofit.RLisConnected()) {
            val dataMap  = createOverviewPayloadNew(cardData,currentTimestamp)
            val images=getUserImages()
            RLTools.RlLogDPrint(TAG,"Overview Insert Request: $dataMap")
            //Insert Api Call
            viewModel.RLInsertYourWayOverviewData(dataMap,images) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.RlLogDPrint(TAG, "Overview Insert Success: ${response.text}")
                            RLupdateUserInsightlyMoengageApiCall(cardData)
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "Overview Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "Overview Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogEPrint(TAG, "Overview Insert Error: ${error.message}" )
                }
            }
        }

    }

    private fun RLupdateUserInsightlyMoengageApiCall(cardData: RLSessionDataTransferModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestApi = RLInsightlyMoengageApiPayload(
                    email=cardData.emailId,
                    uid= currentUser,
                    device_type= "Android",
                    Is_basic_data_added= cardData.isBasicDataAdded,
                    your_way= RLTools.RLgetCurrentISO8601())

                RLTools.RlLogDPrint(TAG, "Insightly Moengage requestApi: $requestApi")

                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val body = Gson().toJson(requestApi).toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(RLConstants.UPDATE_MOENAGE_USER)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Log response on background thread
                RLTools.RlLogDPrint(TAG, "Insightly Moengage Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogDPrint(TAG, "Insightly Moengage Insert Success: ${response}")
                        RLAllProcessDone(cardData)
                       // RLBottomHideShowSet(true)
                       // (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                       // (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)

                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogEPrint(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                    }

                }

            } catch (e: Exception) {
                RLBaseProgress.RLhideProgressDialog()
                RLTools.RlLogEPrint(TAG, "Insightly Moengage Error: ${e.localizedMessage}")
            }
        }
    }
    private fun getUserImages(): List<MultipartBody.Part> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        imgUriList.forEachIndexed { index, uri ->
            val imageFile = RLTools.RLGetFileFromUri(requireContext(), uri)
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("userImage[]", imageFile.name, requestFile)
            imageParts.add(imagePart)
        }
        return imageParts
    }

    private fun RLAllProcessDone(cardData: RLSessionDataTransferModel){
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
        // Convert to a single comma-separated string
        var imgString: String=""
        if (imgUriList.isNotEmpty()){
            imgString = imgUriList.joinToString(separator = ",") { it.toString() }
        }

        val  modelData= RLTextOverview(
            avatar =cardData.displayImage,
            username =cardData.displayName,
            first_name =cardData.displayName,
            ID =  0,
            userid =  currentUser,
            className ="${cardData.classType} Session",
            classType =cardData.classType,
            timestamp =currentTimestamp,
            short_timestamp= currentTimestamp,
            timestamp_local=currentTimestamp,
            imageLinkSmall ="",
            totalREV =safeNumber(cardData.totalRev),
            totalTime= cardData.totalTime,
            burntCalories= safeNumber(cardData.burntCalories).toString(),
            totalRMM= "0",
            totalRMS ="0",
            maxRevPercentage= safeNumber(cardData.maxRevPercentage),
            avgRevPercentage= safeNumber(cardData.avgRevPercentage).toString(),
            zone1Seconds= "0",
            zone2Seconds ="0",
            zone3Seconds ="0",
            zone4Seconds ="0",
            zone5Seconds ="0",
            zone6Seconds ="0",
            zone7Seconds ="0",
            medals ="0",
            medals_gold =0,
            medals_silver =0,
            medals_bronze =0,
            awards ="0",
            visibilityFlagForThatSession =visibilityflagforthatsession,
            bmo =2,
            instructor ="",
            duration ="",
            rideTitle ="",
            mainTitle ="",
            originalClassDate ="",
            videoKey ="",
            goal ="all",
            avatarKudos ="",
            avatar_comments = "",
            total_kudos =0,
            total_comments =0,
            elevation =safeNumber(cardData.totalElevation).toInt(),
            power =0,
            hr =safeIntNumber(cardData.avgHr),
            steps =safeIntNumber(cardData.totalSteps),
            distance= safeNumber(cardData.distance),
            hrm= if (cardData.SENSOR.equals(RLConstants.HEART_SENSOR)) 1 else 0,
            class_level ="",
            average_speed= safeNumber(cardData.avgSpeed),
            map_image ="",
            user_images =imgString,
            isDeleted =0,
            dems = "",
            spike_steps = "",
            spike_timestamp = "",
            share_map =0,
            from_third_party_source =0,
            map_url = "",
            dom =0,
            rhr =0,
            mhr =0,
            avgHr= safeIntNumber(cardData.avgHr),
            notes = fragBinding.edtAddNotes.text.toString(),
            source ="",
            isKudos= 0)

        val bundle = Bundle()
        bundle.putSerializable(RLConstants.CardData, modelData)
        bundle.putString(RLConstants.FeedSelectTag, "FRIENDS")
        bundle.putBoolean("isSessionComplete", true)
        (context as RLMainActivityRL).RLloadFrag(RLFragSessionSummary().newInstance(bundle), TAG, false, null, true)
    }

    private fun RLchooseFromGallery() {
        try {
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
        }catch (e:Exception){
            RLTools.RlLogEPrint(TAG,"Exception: ${e.localizedMessage}")
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

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Images"))
    }
    // Launcher for image selection
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle multiple image selection
            val clipData = result.data?.clipData
            val singleImage = result.data?.data
            when {
                // Multiple images selected
                clipData != null -> {
                    for (i in 0 until minOf(clipData.itemCount, MAX_IMAGE_SELECTION - imgUriList.size)) {
                        val imageUri = clipData.getItemAt(i).uri
                        if (!imgUriList.contains(imageUri)) {
                            imgUriList.add(imageUri)
                        }
                    }
                }
                // Single image selected
                singleImage != null -> {
                    if (!imgUriList.contains(singleImage) && imgUriList.size < MAX_IMAGE_SELECTION) {
                        imgUriList.add(singleImage)
                    }
                }
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
}
