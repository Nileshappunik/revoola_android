package com.revoola.fragment.start.classes

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragSessionCompleteBinding
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.adapter.RLSelectedImagesAdapter
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.fragment.feed.RLFragMindSessionSummary
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.fragment.start.yourway.RLSessionDataTransferModelNew
import com.revoola.model.RLClassLeaderboard
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageBodyApiPayload
import com.revoola.model.RLTextOverview
import com.revoola.model.RLYourWayApiPayload
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RLFragClassWorkoutComplete : RLBaseFragment(){
    val TAG: String = RLFragClassWorkoutComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding
    //Api call use
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

    private val REQUEST_CODE_CHOOSE_IMAGE = 500
    private lateinit var cardData: RLSessionDataTransferModelNew

    private var imgUriList = mutableListOf<Uri>()
    private var currentUser =""
    private var visibilityflagforthatsession:Int =0

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragClassWorkoutComplete()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the Parcelable object from the Bundle
        arguments?.let {
            cardData = it.getParcelable("cardData")!! // Use !! only if you're sure it's not null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragClassWorkoutComplete" )
        currentUser= RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
            }
        })
        RLUiSetUp()
        return fragBinding.root
    }
    private fun RLUiSetUp() {
        val VideoCardData = Gson().fromJson(cardData.VIDEODATA, RLFulllVideoModel::class.java)
        fragBinding.edtSessionName.setText(VideoCardData.rideTitle)
        fragBinding.txtMainTitle.setText("ACTIVITY COMPLETE!")
        //ShareMap Hide
        fragBinding.txtShareMap.visibility=View.GONE
        fragBinding.switchCompat.visibility=View.GONE

        visibilityflagforthatsession=cardData.visibilityflagforthatsession
        when(visibilityflagforthatsession){
            0->{//EveryOne
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvShareTitle.setText(R.string.everyone)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
            }
            1->{//Private
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvShareTitle.setText(R.string.privatetx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))
            }
            2->{//Friends
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvShareTitle.setText(R.string.friendstx)
                fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
            }
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
                }
                "PRIVATE"->{
                    visibilityflagforthatsession=1
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyeveryone)
                    fragBinding.tvShareTitle.setText(R.string.everyone)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))
                }
                "EVERYONE"->{
                    visibilityflagforthatsession=2
                    fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                    fragBinding.imgShareImage.setImageResource(R.drawable.ic_privacyfriends)
                    fragBinding.tvShareTitle.setText(R.string.friendstx)
                    fragBinding.tvShareTitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
                }
            }

        }

        fragBinding.imgCancle.setOnClickListener {
            rl_bottomHideShowSet(true)
            (context as RLMainActivityRL).rl_bottombarcolorDarkBlue()
            (context as RLMainActivityRL).rl_loadFrag(RLFragOverviewSession(), TAG, false,null, false)
        }
        fragBinding.inlayButton.commonButton.setText(R.string.save)
        fragBinding.inlayButton.commonButton.setOnClickListener {
            if (isAdded){RLBaseProgress.rl_showProgressDialog(requireActivity())}
            if ((cardData.classType).equals(RLConstants.BODY)){
                if ((cardData.SENSOR).equals(RLConstants.HEART_SENSOR)){
                    cardData.hrm=1
                    RLBodyFirebaseDataPrepaire("HEART_SENSOR",VideoCardData)
                }else  {
                    cardData.hrm=0
                    RLBodyFirebaseDataPrepaire("NO_SENSOR",VideoCardData)
                }
            }else{
                if ((cardData.SENSOR).equals(RLConstants.HEART_SENSOR)){
                    cardData.hrm=1
                    RLMindFirebaseDataPrepaire("HEART_SENSOR",VideoCardData)
                }else  {
                    cardData.hrm=0
                    RLMindFirebaseDataPrepaire("NO_SENSOR",VideoCardData)
                }

            }

        }
        fragBinding.txtAddPhoto.setOnClickListener {
            RLchooseFromGallery()
        }
    }

    //ALL BODY DATA TO FIREBASE ENTRY
    private fun RLBodyFirebaseDataPrepaire(sensorType:String,videoCardData: RLFulllVideoModel){
        var sessionUserSessionDetailData= hashMapOf<String, Any?>()
        var sessionUserSessionSummaryGraphData= hashMapOf<String, Any>()
        var sessionGhostForClassBestForClass= hashMapOf<String, Any>()
        var sessionGhostForClassLastForClass= hashMapOf<String, Any>()
        val classDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())//"20241128105036"
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()

        when(sensorType){
            "HEART_SENSOR"->{
                  sessionUserSessionDetailData = hashMapOf<String, Any?>(
                    "MaxHrUsedForCalculation" to cardData.RFMHR,
                    "MaxHrUsedForCalculation_Last" to cardData.RFMHR,
                    "RestingHrUsedForCalculation" to cardData.RestingHR,
                    "RestingHrUsedForCalculation_Last" to cardData.RestingHR,
                    "arrAvgRevPercentage" to  (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrBurntCalories" to (cardData.arrBurntCalories?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCadence" to (cardData.arrCadence?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumDistance" to (cardData.arrCumDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumSpeed" to (cardData.arrCumSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrDistance" to (cardData.arrDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to  (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to  (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (cardData.arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrHr" to  (cardData.arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrRevSecond" to (cardData.arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (cardData.arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "avgRevPercentage" to safeNumber(cardData.avgRevPercentage),
                    "burntCalories" to safeNumber(cardData.burntCalories),
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to cardData.displayImage,
                    "displayName" to cardData.displayName,
                    "distance" to safeNumber(cardData.distance),
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "Ahmedabad",
                    "maxRevPercentage" to safeNumber(cardData.maxRevPercentage),
                    "minRevPercentage" to safeNumber(cardData.minRevPercentage),
                    "remark" to "Android",
                    "revPercentage" to safeNumber(cardData.revPercentage),
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to safeNumber(cardData.totalRev),
                    "totalTime" to cardData.totalTime,
                    "videoKey" to cardData.videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                     RevoolaKeys.Zone1 to cardData.zoneDataDetail[RevoolaKeys.Zone1],
                     RevoolaKeys.Zone2 to cardData.zoneDataDetail[RevoolaKeys.Zone2],
                     RevoolaKeys.Zone3 to cardData.zoneDataDetail[RevoolaKeys.Zone3],
                     RevoolaKeys.Zone4 to cardData.zoneDataDetail[RevoolaKeys.Zone4],
                     RevoolaKeys.Zone5 to cardData.zoneDataDetail[RevoolaKeys.Zone5],
                     RevoolaKeys.Zone6 to cardData.zoneDataDetail[RevoolaKeys.Zone6],
                     RevoolaKeys.Zone7 to cardData.zoneDataDetail[RevoolaKeys.Zone7]
                )
                 sessionUserSessionSummaryGraphData = hashMapOf(
                    "arrCadence" to (cardData.arrCadence.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrHr" to (cardData.arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (cardData.arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (cardData.arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrAvgRevPercentage" to (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrHr" to (cardData.arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrRevSecond" to (cardData.arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrRevPercentage" to (cardData.arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "classDate" to classDate,
                    "displayImage" to cardData.displayImage,
                    "displayName" to cardData.displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to safeNumber(cardData.totalRev),
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass =  hashMapOf(
                     "arrAvgPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrAvgRevPercentage" to (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrPowerFromDevice" to (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrHr" to (cardData.arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrRevSecond" to (cardData.arrRevSecond?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrRevPercentage" to (cardData.arrRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "classDate" to classDate,
                     "displayImage" to cardData.displayImage,
                     "displayName" to cardData.displayName,
                     "flagImage" to "flag-of-United-Kingdom.png",
                     "flagName" to "United Kingdom",
                     "isPowerDeviceConnected" to false,
                     "location" to "",
                     "remark" to "Android",
                     "totalRev" to safeNumber(cardData.totalRev),
                     "visibilityflagforthatsession" to visibilityflagforthatsession
                 )
            }
            "NO_SENSOR"->{
                sessionUserSessionDetailData = hashMapOf(
                    "MaxHrUsedForCalculation" to cardData.RFMHR,
                    "MaxHrUsedForCalculation_Last" to cardData.RFMHR,
                    "RestingHrUsedForCalculation" to cardData.RestingHR,
                    "RestingHrUsedForCalculation_Last" to cardData.RestingHR,
                    "arrAvgRevPercentage" to (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrBurntCalories" to (cardData.arrBurntCalories?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCadence" to (cardData.arrCadence?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumDistance" to (cardData.arrCumDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrCumSpeed" to (cardData.arrCumSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrDistance" to (cardData.arrDistance?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (cardData.arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "avgRevPercentage" to safeNumber(cardData.avgRevPercentage),
                    "burntCalories" to safeNumber(cardData.burntCalories),
                    "classDate" to classDate,
                    "classDescription" to videoCardData.rideDescription,
                    "classImage" to "",
                    "className" to fragBinding.edtSessionName.text.toString(),
                    "classNote" to fragBinding.edtAddNotes.text.toString(),
                    "classType" to videoCardData.classType,
                    "demsElevation" to 0,
                    "displayImage" to cardData.displayImage,
                    "displayName" to cardData.displayName,
                    "distance" to safeNumber(cardData.distance),
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "imageLinkLarge" to videoCardData.imageLinkLarge,
                    "imageLinkSmall" to videoCardData.imageLinkSmall,
                    "isClass" to true,
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "maxRevPercentage" to safeNumber(cardData.maxRevPercentage),
                    "minRevPercentage" to safeNumber(cardData.minRevPercentage),
                    "remark" to "Android",
                    "revPercentage" to safeNumber(cardData.revPercentage),
                    "rms" to 0,
                    "timestamp" to currentTimestamp,
                    "totalElevation" to 0,
                    "totalPower" to 0,
                    "totalRev" to safeNumber(cardData.totalRev),
                    "totalTime" to cardData.totalTime,
                    "videoKey" to cardData.videoID,
                    "visibilityflagforthatsession" to visibilityflagforthatsession,
                    "zone1" to hashMapOf(
                        "burntCalories" to safeNumber(cardData.burntCalories),
                        "distance" to safeNumber(cardData.distance),
                        "remark" to "Android",
                        "seconds" to cardData.totalTime,
                        "totalRev" to safeNumber(cardData.totalRev)
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
                    "arrCadence" to (cardData.arrCadence.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrSpeed" to (cardData.arrSpeed?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "remark" to "Android"
                )
                 sessionGhostForClassBestForClass = hashMapOf(
                    "arrAvgPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrAvgRevPercentage" to (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                    "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "arrPowerFromDevice" to (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                    "classDate" to classDate,
                    "displayImage" to cardData.displayImage,
                    "displayName" to cardData.displayName,
                    "flagImage" to "flag-of-United-Kingdom.png",
                    "flagName" to "United Kingdom",
                    "isPowerDeviceConnected" to false,
                    "location" to "",
                    "remark" to "Android",
                    "totalRev" to safeNumber(cardData.totalRev),
                    "visibilityflagforthatsession" to visibilityflagforthatsession
                )
                 sessionGhostForClassLastForClass =  hashMapOf(
                     "arrAvgPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrAvgRevPercentage" to (cardData.arrAvgRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrMaxRevPercentage" to (cardData.arrMaxRevPercentage?.takeIf { it.isNotEmpty() } ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0)),
                     "arrPower" to (cardData.arrPower?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "arrPowerFromDevice" to (cardData.arrPowerFromDevice?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
                     "classDate" to classDate,
                     "displayImage" to cardData.displayImage,
                     "displayName" to cardData.displayName,
                     "flagImage" to "flag-of-United-Kingdom.png",
                     "flagName" to "United Kingdom",
                     "isPowerDeviceConnected" to false,
                     "location" to "",
                     "remark" to "Android",
                     "totalRev" to safeNumber(cardData.totalRev),
                     "visibilityflagforthatsession" to visibilityflagforthatsession
                 )
            }
        }

        //revoola_UserSessionSummaryData  Prepaire
        val sessionUserSessionSummaryData = hashMapOf(
            "avgBurntCalories" to safeNumber(cardData.avgBurntCalories),
            "avgCadence" to safeNumber(cardData.avgCadence),
            "avgHr" to safeIntNumber(cardData.avgHr),
            "avgPower" to 0,
            "avgPowerFromDevice" to 0,
            "avgRevPercentage" to safeNumber(cardData.avgRevPercentage),
            "avgSpeed" to safeNumber(cardData.avgSpeed),
            "avgSpeedForOneKm" to safeNumber(cardData.avgSpeedForOneKm),
            "avgSpeedForOneMile" to safeNumber(cardData.avgSpeedForOneMile),
            "burntCalories" to safeNumber(cardData.burntCalories),
            "classDate" to classDate,
            "classDescription" to videoCardData.rideDescription,
            "classImage" to "",
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to videoCardData.classType,
            "demsElevation" to 0,
            "distance" to safeNumber(cardData.distance),
            "imageLinkLarge" to videoCardData.imageLinkLarge,
            "imageLinkSmall" to videoCardData.imageLinkSmall,
            "isClass" to true,
            "isPowerDeviceConnected" to false,
            "location" to "",
            "maxBurntCalories" to safeIntNumber(cardData.maxBurntCalories),
            "maxCadence" to safeIntNumber(cardData.maxCadence),
            "maxHr" to safeIntNumber(cardData.maxHeartRate),
            "maxPower" to 0,
            "maxPowerFromDevice" to 0,
            "maxRevPercentage" to safeNumber(cardData.maxRevPercentage),
            "maxSpeed" to safeIntNumber(cardData.maxSpeed),
            "maxSpeedForOneKm" to safeNumber(cardData.maxSpeedForOneKm),
            "maxSpeedForOneMile" to safeNumber(cardData.maxSpeedForOneMile),
            "minHr" to safeIntNumber(cardData.minHeartRate),
            "minRevPercentage" to safeNumber(cardData.minRevPercentage),
            "remark" to "Android",
            "revPercentage" to safeNumber(cardData.revPercentage),
            "rms" to 0,
            "timestamp" to currentTimestamp,
            "totalElevation" to 0,
            "totalPower" to 0,
            "totalRev" to safeNumber(cardData.totalRev),
            "totalTime" to cardData.totalTime,
            "videoKey" to cardData.videoID,
            "visibilityflagforthatsession" to visibilityflagforthatsession,
            "zone1" to hashMapOf(
                "avgCadence" to safeNumber(cardData.avgCadence),
                "avgHr" to safeIntNumber(cardData.avgHr),
                "avgPower" to 0,
                "avgPowerFromDevice" to 0,
                "avgSpeed" to safeNumber(cardData.avgSpeed),
                "burntCalories" to safeNumber(cardData.burntCalories),
                "distance" to safeNumber(cardData.distance),
                "remark" to "Android",
                "seconds" to cardData.totalTime,
                "totalRev" to safeNumber(cardData.totalRev)
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

        val sessionUserCompletedVideos = mapOf(cardData.videoID to true)

        //Entry GhostData lastForClass
        val databaseRefGhostLast = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostLastForClassDataPath(currentUser))
        databaseRefGhostLast.child(cardData.videoID).setValue(sessionGhostForClassLastForClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "revoola_GhostData LastForClass Entry saved successfully!")

                } else {
                   RLTools.rl_logEPrint(TAG, "revoola_GhostData LastForClass Entry Failed to save:- ${task.exception}")
                }
            }

        //Entry GhostData bestForClass
        val databaseRefGhostBest = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.ghostBestForClassDataPath(currentUser))
        databaseRefGhostBest.child(cardData.videoID).setValue(sessionGhostForClassBestForClass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "revoola_GhostData BestForClass Entry saved successfully!")

                } else {
                   RLTools.rl_logEPrint(TAG, "revoola_GhostData BestForClass Entry Failed to save :- ${task.exception}")
                }
            }

        //revoola_UserSessionSummaryData
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.summaryDataPath(currentUser))
        val entryIdSummery =currentTimestamp
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(sessionUserSessionSummaryData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(TAG, "revoola_UserSessionSummaryData Entry saved successfully!")

                    } else {
                       RLTools.rl_logEPrint(TAG, "revoola_UserSessionSummaryData Entry Failed to save :- ${task.exception}")
                    }
                }
        }

        //revoola_UserSessionSummaryGraphData
        val databaseRefGraph = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.graphDataPath(currentUser))
        val entryIdGraph = currentTimestamp
        entryIdGraph.let {
            databaseRefGraph.child(it).setValue(sessionUserSessionSummaryGraphData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(TAG, "revoola_UserSessionSummaryGraphData Entry saved successfully!")
                    } else {
                       RLTools.rl_logEPrint(TAG, "revoola_UserSessionSummaryGraphData Entry Failed to save :- ${task.exception}")
                    }
                }
        }

        //revoolaUserCompletedVideos
        val databaseRefCompletedVideos = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.userCompletedVideosDataPath(currentUser))
        databaseRefCompletedVideos.updateChildren(sessionUserCompletedVideos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "revoola_UserCompletedVideos Entry saved successfully!")
                } else {
                   RLTools.rl_logEPrint(TAG, "revoola_UserCompletedVideos Entry Failed to save:- ${task.exception}")
                }
            }

        //revoola_UserSessionDetailData
         val databaseRef = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.detailDataPath(currentUser))
        val entryId =currentTimestamp
        entryId.let {
            databaseRef.child(it).setValue(sessionUserSessionDetailData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(TAG, "revoola_UserSessionDetailData Entry saved successfully!")
                        RLInsertApiCall(currentTimestamp,0,videoCardData)
                    } else {
                       RLTools.rl_logEPrint(TAG, "revoola_UserSessionDetailData Entry Failed to save:- ${task.exception}")
                    }
                }
        }

    }

    //ALL MiND DATA TO FIREBASE ENTRY
    private fun RLMindFirebaseDataPrepaire(sensorType:String,videoCardData: RLFulllVideoModel){
        val classDate = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
        val sessionUserSessionDetailData = hashMapOf(
            "MaxHrUsedForCalculation" to cardData.RFMHR,
            "MaxHrUsedForCalculation_Last" to cardData.RFMHR,
            "RestingHrUsedForCalculation" to cardData.RestingHR,
            "RestingHrUsedForCalculation_Last" to cardData.RestingHR,
            "arrHr" to (cardData.arrHr?.takeIf { it.isNotEmpty() } ?: listOf(0, 0, 0, 0, 0)),
            "classDate" to classDate,
            "classDescription" to videoCardData.rideDescription,
            "classImage" to "",
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to videoCardData.classType,
            "displayImage" to cardData.displayImage,
            "displayName" to cardData.displayName,
            "duration" to videoCardData.duration,
            "flagImage" to "flag-of-United-Kingdom.png",
            "flagName" to "United Kingdom",
            "imageLinkLarge" to videoCardData.imageLinkLarge,
            "imageLinkSmall" to videoCardData.imageLinkSmall,
            "instructor" to videoCardData.instructor,
            "isClass" to true,
            "isMindClass" to true,
            "location" to "",
            "mainTitle" to videoCardData.classType,
            "originalClassDate" to videoCardData.originalClassDate,
            "remark" to "Android",
            "rideTitle" to videoCardData.rideTitle,
            "rms" to safeNumber(cardData.rms),
            "timestamp" to currentTimestamp,
            "totalTime" to cardData.totalTime,
            "videoKey" to cardData.videoID,
            "visibilityflagforthatsession" to visibilityflagforthatsession
        )

        val sessionUserSessionSummaryData = hashMapOf(
            "avgHr" to cardData.avgHr,
            "classDate" to classDate,
            "classDescription" to videoCardData.rideDescription,
            "classImage" to "",
            "className" to fragBinding.edtSessionName.text.toString(),
            "classNote" to fragBinding.edtAddNotes.text.toString(),
            "classType" to videoCardData.classType,
            "duration" to videoCardData.duration,
            "imageLinkLarge" to videoCardData.imageLinkLarge,
            "imageLinkSmall" to videoCardData.imageLinkSmall,
            "instructor" to videoCardData.instructor,
            "isClass" to true,
            "isMindClass" to true,
            "location" to "",
            "mainTitle" to videoCardData.classType,
            "maxHr" to cardData.maxHeartRate,
            "minHr" to cardData.minHeartRate,
            "originalClassDate" to videoCardData.originalClassDate,
            "remark" to "Android",
            "rideTitle" to videoCardData.rideTitle,
            "rms" to safeNumber(cardData.rms),
            "timestamp" to currentTimestamp,
            "totalTime" to cardData.totalTime,
            "videoKey" to cardData.videoID,
            "visibilityflagforthatsession" to visibilityflagforthatsession
        )

        val sessionUserCompletedVideos = mapOf(cardData.videoID to true)

        //revoola User SessionSummary Data
        val databaseRefSummery = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.summaryDataPath(currentUser))
        val entryIdSummery = currentTimestamp
        entryIdSummery.let {
            databaseRefSummery.child(it).setValue(sessionUserSessionSummaryData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(TAG, "revoola_UserSessionSummaryData Entry saved successfully!")

                    } else {
                       RLTools.rl_logEPrint(TAG, "revoola_UserSessionSummaryData Entry Failed to save:- ${task.exception}")
                    }
                }
        }

        //revoola User Completed Videos
        val databaseRefCompletedVideos = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.userCompletedVideosDataPath(currentUser))
        databaseRefCompletedVideos.updateChildren(sessionUserCompletedVideos)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    RLTools.rl_logDPrint(TAG, "revoola_UserCompletedVideos Entry saved successfully!")
                } else {
                   RLTools.rl_logEPrint(TAG, "revoola_UserCompletedVideos Entry Failed to save:- ${task.exception}")
                }
            }

        //revoola User Session Detail Data
        val databaseRef = FirebaseDatabase.getInstance().getReference(RevoolaFirebasePath.detailDataPath(currentUser))
        val entryId = currentTimestamp
        entryId.let {
            databaseRef.child(it).setValue(sessionUserSessionDetailData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        RLTools.rl_logDPrint(TAG, "revoola_UserSessionDetailData Entry saved successfully!")
                        RLInsertApiCall(currentTimestamp,1,videoCardData)
                    } else {
                       RLTools.rl_logEPrint(TAG, "revoola_UserSessionDetailData Entry Failed to save:- ${task.exception}")
                    }
                }
        }

    }

    //Insert Api
    private fun createPayload( currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel): String {
        val classLeaderboard = RLClassLeaderboard(
            userId = currentUser,
            classId = cardData.videoID,
            timestamp = currentTimestamp,
            timestampLocal = currentTimestamp,
            totalRev = safeNumber(cardData.totalRev),
            visibilityFlagForThatSession = visibilityflagforthatsession,
            discipline = videoCardData.classType,
            duration = cardData.totalTime,
            calories = safeNumber(cardData.burntCalories),
            bmo = bmo,
            rmm = safeIntNumber((if (cardData.totalTime.isNullOrEmpty()) "0" else cardData.totalTime).toInt()),
            rms = 0,
            source = "android",
            goal = "all")

        val apiPayload = listOf(RLYourWayApiPayload(classLeaderboard))

        // Convert to JSON String
        return Gson().toJson(apiPayload)
    }
    private fun RLInsertApiCall(currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel) {
        if (RLApiClientRetrofit.rl_isConnected()) {
            val jsonPayload = createPayload(currentTimestamp,bmo,videoCardData)
            val request = Gson().fromJson(jsonPayload, Array<RLYourWayApiPayload>::class.java).toList()

            RLTools.rl_logDPrint(TAG,"MindBody Insert Request: $request")
            //Insert Api Call
            viewModel.rl_insertYourWayData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLInsertOverviewApiCall(currentTimestamp,bmo,videoCardData)
                            RLTools.rl_logDPrint(TAG, "MindBody Insert Success: ${response.text}")
                        } else {
                            RLBaseProgress.rl_hideProgressDialog()
                            RLTools.rl_logEPrint(TAG, "MindBody Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logEPrint(TAG, "MindBody Insert Catch: ${e.localizedMessage}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logEPrint(TAG, "MindBody Insert Error: ${error.localizedMessage}" )
                }
            }
        }

    }

    // Convert text to RequestBody and OverView Api Call
    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    private fun createOverviewPayloadNew(currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
       // hrm -> 1 (is hr sensor is connected), 0 (if not connected)
        // Add text fields as form data
        requestBodyMap["data[myOverviewThumbnails][userid]"] = createRequestBody(currentUser)
        requestBodyMap["data[myOverviewThumbnails][className]"] = createRequestBody(videoCardData.rideTitle)
        requestBodyMap["data[myOverviewThumbnails][classType]"] = createRequestBody(videoCardData.classType)
        requestBodyMap["data[myOverviewThumbnails][timestamp]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][timestamp_local]"] = createRequestBody(currentTimestamp)
        requestBodyMap["data[myOverviewThumbnails][totalREV]"] = createRequestBody(safeNumber(cardData.totalRev).toString())
        requestBodyMap["data[myOverviewThumbnails][totalTime]"] = createRequestBody(cardData.totalTime.toString())

        val burntCalories = safeNumber(cardData.burntCalories) ?: 0
        requestBodyMap["data[myOverviewThumbnails][burntCalories]"] = createRequestBody(burntCalories.toString())

        requestBodyMap["data[myOverviewThumbnails][totalRMM]"] = createRequestBody(cardData.totalTime)
        requestBodyMap["data[myOverviewThumbnails][totalRMS]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][maxRevPercentage]"] = createRequestBody(safeNumber(cardData.maxRevPercentage).toString())
        requestBodyMap["data[myOverviewThumbnails][avgRevPercentage]"] = createRequestBody(safeNumber(cardData.avgRevPercentage).toString())

        requestBodyMap["data[myOverviewThumbnails][zone1Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone1]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone2Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone2]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone3Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone3]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone4Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone4]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone5Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone5]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone6Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone6]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][zone7Seconds]"] = createRequestBody(safeIntNumber(cardData.zoneDataDetail[RevoolaKeys.Zone7]?.seconds).toString())
        requestBodyMap["data[myOverviewThumbnails][medals]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][awards]"] = createRequestBody("0")


        requestBodyMap["data[myOverviewThumbnails][visibilityflagforthatsession]"] = createRequestBody(visibilityflagforthatsession.toString())
        requestBodyMap["data[myOverviewThumbnails][bmo]"] = createRequestBody(bmo.toString())
        requestBodyMap["data[myOverviewThumbnails][instructor]"] = createRequestBody(videoCardData.instructor)
        requestBodyMap["data[myOverviewThumbnails][duration]"] = createRequestBody(videoCardData.duration)
        requestBodyMap["data[myOverviewThumbnails][rideTitle]"] = createRequestBody(videoCardData.rideTitle)
        requestBodyMap["data[myOverviewThumbnails][mainTitle]"] = createRequestBody(videoCardData.classType)


        requestBodyMap["data[myOverviewThumbnails][originalClassDate]"] = createRequestBody(videoCardData.originalClassDate)
        requestBodyMap["data[myOverviewThumbnails][videoKey]"] = createRequestBody(cardData.videoID)


        requestBodyMap["data[myOverviewThumbnails][goal]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][medals_gold]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_silver]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][medals_bronze]"] = createRequestBody("0")

        requestBodyMap["data[myOverviewThumbnails][elevation]"] = createRequestBody(safeNumber(cardData.totalElevation).toString())
        requestBodyMap["data[myOverviewThumbnails][power]"] = createRequestBody("0")
        requestBodyMap["data[myOverviewThumbnails][hr]"] = createRequestBody(safeIntNumber(cardData.avgHr).toString())
        requestBodyMap["data[myOverviewThumbnails][steps]"] = createRequestBody(safeIntNumber(cardData.totalSteps).toString())
        requestBodyMap["data[myOverviewThumbnails][distance]"] = createRequestBody(safeNumber(cardData.distance).toString())
        requestBodyMap["data[myOverviewThumbnails][hrm]"] = createRequestBody(safeIntNumber(cardData.hrm).toString())
        requestBodyMap["data[myOverviewThumbnails][class_level]"] = createRequestBody("")
        requestBodyMap["data[myOverviewThumbnails][average_speed]"] = createRequestBody(safeNumber(cardData.avgSpeed).toString())
        requestBodyMap["data[myOverviewThumbnails][imageLinkSmall]"] = createRequestBody(videoCardData.imageLinkrectangleV2)
        requestBodyMap["data[myOverviewThumbnails][share_map]"] = createRequestBody("")

        requestBodyMap["data[myOverviewThumbnails][source]"] = createRequestBody("android")
        requestBodyMap["data[myOverviewThumbnails][from_third_party_source]"] = createRequestBody("0")

        return requestBodyMap
    }
    private fun RLInsertOverviewApiCall(currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel) {
        if (RLApiClientRetrofit.rl_isConnected()) {
            val dataMap  = createOverviewPayloadNew(currentTimestamp,bmo,videoCardData)
            val images = getUserImages()
            RLTools.rl_logDPrint(TAG,"Overview Insert Request: $dataMap")
            //Insert Api Call
            viewModel.rl_insertClassSessionData(dataMap,images) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.rl_logDPrint(TAG, "Overview Insert Success: ${response.text}")
                            RLupdateUserInsightlyMoengageApiCall(currentTimestamp,bmo,videoCardData)
                        } else {
                            RLBaseProgress.rl_hideProgressDialog()
                            RLTools.rl_logEPrint(TAG, "Overview Insert Fail: ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.rl_hideProgressDialog()
                        e.printStackTrace()
                        RLTools.rl_logEPrint(TAG, "Overview Insert Catch: ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logEPrint(TAG, "Overview Insert Error: ${error.localizedMessage}" )
                }
            }
        }

    }
    private fun getUserImages(): List<MultipartBody.Part> {
        val imageParts = mutableListOf<MultipartBody.Part>()
        imgUriList.forEachIndexed { index, uri ->
            val imageFile = RLTools.rl_getFileFromUri(requireContext(), uri)
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("userImage[]", imageFile.name, requestFile)
            imageParts.add(imagePart)
        }
        return imageParts
    }
    //Mo engage Api Call
    private fun RLupdateUserInsightlyMoengageApiCall(currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestApi = RLInsightlyMoengageBodyApiPayload(
                    email=cardData.emailId,
                    uid= currentUser,
                    device_type= "Android",
                    Is_basic_data_added= cardData.isBasicDataAdded,
                    mind_activity= RLTools.rl_getCurrentISO8601())

                RLTools.rl_logDPrint(TAG, "Moengage requestApi: $requestApi")

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
                RLTools.rl_logDPrint(TAG, "Moengage Response: $responseBody")
                val apiResponse = Gson().fromJson(responseBody, RLInsightlyMoEngageResponse::class.java)
                // If UI update needed, switch to Main Thread
                CoroutineScope(Dispatchers.Main).launch {
                    if (apiResponse.response.isNotEmpty() && apiResponse.response[0].success == "true") {
                        // Show success message in UI
                        // Handle UI updates if required (e.g., Toast message)
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logDPrint(TAG, "Moengage Insert Success: ${apiResponse.response[0].status}")
                        RLAllProcessDone(currentTimestamp,bmo,videoCardData)

                    }else{
                        RLBaseProgress.rl_hideProgressDialog()
                        RLTools.rl_logEPrint(TAG, "Moengage Error: ${apiResponse.response[0].status}")
                    }

                }

            } catch (e: Exception) {
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logEPrint(TAG, "Moengage Exception: ${e.localizedMessage}")
            }
        }
    }
    //When All Api and Firebase Process Done
    private fun RLAllProcessDone(currentTimestamp: String,bmo:Int,videoCardData: RLFulllVideoModel){
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
            className =videoCardData.rideTitle,
            classType =cardData.classType,
            timestamp =currentTimestamp,
            short_timestamp= currentTimestamp,
            timestamp_local=currentTimestamp,
            imageLinkSmall =videoCardData.imageLinkrectangleV2,
            totalREV =safeNumber(cardData.totalRev),
            totalTime= cardData.totalTime,
            burntCalories= safeNumber(cardData.burntCalories).toString(),
            totalRMM= cardData.totalTime,
            totalRMS ="0",
            maxRevPercentage= safeNumber(cardData.maxRevPercentage),
            avgRevPercentage= safeNumber(cardData.avgRevPercentage).toString(),
            zone1Seconds= cardData.zoneDataDetail[RevoolaKeys.Zone1]?.seconds.toString(),
            zone2Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone2]?.seconds.toString(),
            zone3Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone3]?.seconds.toString(),
            zone4Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone4]?.seconds.toString(),
            zone5Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone5]?.seconds.toString(),
            zone6Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone6]?.seconds.toString(),
            zone7Seconds =cardData.zoneDataDetail[RevoolaKeys.Zone7]?.seconds.toString(),
            medals ="0",
            medals_gold =0,
            medals_silver =0,
            medals_bronze =0,
            awards ="0",
            visibilityFlagForThatSession =visibilityflagforthatsession,
            bmo =bmo,
            instructor =videoCardData.instructor,
            duration =videoCardData.duration,
            rideTitle =videoCardData.rideTitle,
            mainTitle =videoCardData.rideTitle,
            originalClassDate =videoCardData.originalClassDate,
            videoKey =cardData.videoID,
            goal ="",
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

        if (bmo.toInt()==0) {
            //Body
            val bundle = Bundle()
            bundle.putSerializable(RLConstants.CardData, modelData)
            bundle.putString(RLConstants.FeedSelectTag, "FRIENDS")
            bundle.putBoolean("isSessionComplete", true)
           (context as RLMainActivityRL).rl_loadFrag(RLFragSessionSummary().newInstance(bundle), TAG, false, null, true)

        }else{
            //Mind
            val bundle = Bundle()
            bundle.putSerializable(RLConstants.CardData, modelData)
            bundle.putString(RLConstants.FeedSelectTag, "FRIENDS")
            bundle.putBoolean("isSessionComplete", true)
            (context as RLMainActivityRL).rl_loadFrag(RLFragMindSessionSummary().newInstance(bundle), TAG, false, null, true)

        }
    }

    //Double Safe Number
    private fun safeNumber(value: Double?): Double {
        return if (value == null || value.isNaN() || value.isInfinite()) 0.0 else value
    }
    //Int Safe Number
    private fun safeIntNumber(value: Int?): Int {
        return if (value == null || value < 0) 0 else value
    }
    //Below All Code ImagePicker
    private fun RLHandleSelectedImageList(imgUriList:MutableList<Uri>){
        if (imgUriList.size > 0) {
            RLimageListVisible(true)
        } else {
            RLimageListVisible(false)
        }
        fragBinding.rvSelectedImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val selectedImagesAdapter = RLSelectedImagesAdapter(imgUriList) { uri ->
            imgUriList.remove(uri)
            if (imgUriList.size > 0) {
                RLimageListVisible(true)
            } else {
                RLimageListVisible(false)
            }
        }
        fragBinding.rvSelectedImages.adapter = selectedImagesAdapter
    }
    private fun RLchooseFromGallery() {
        try {
            Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(5)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())  // Requires implementation
                .forResult(REQUEST_CODE_CHOOSE_IMAGE)
        }catch (e:Exception){
            RLTools.rl_logEPrint(TAG,"Exception: ${e.localizedMessage}")
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
    // Handle in onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            val uriList = Matisse.obtainResult(data)
            // Handle the selected images here
            for (uri in uriList) {
                imgUriList.add(uri)
            }
            RLHandleSelectedImageList(imgUriList)
        }
    }
}