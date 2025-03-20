package com.revoola.fragment.feed

import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.utils.RLPrefManager
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragSessionSummaryBinding
import com.revoola.enumclass.RLMetricData
import com.revoola.enumclass.RLTypeOfMetrics
import com.revoola.enumclass.RLYourWayName
import com.revoola.fragment.feed.adapter.RLFeedSessionEffortListAdapter
import com.revoola.fragment.feed.adapter.RLImagePagerAdapter
import com.revoola.model.RLTextOverview
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.enumclass.RLValueName
import com.revoola.firebaseModel.RLSessionSummaryDataModel
import com.revoola.firebaseModel.RLSessionDetailDataModel
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLZoneChartData
import com.revoola.model.RLZoneChartScoreData
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.math.roundToInt

class RLFragSessionSummary : RLBaseFragment() {
    val TAG: String = RLFragSessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var currentUser:String=""
    private var classType=""
    private var selectTag=""
    private var fireBaseCardData: RLSessionSummaryDataModel?=null
    private var fireBaseDetailCardData: RLSessionDetailDataModel?=null
    private var userCardData: RLRevoolaUsersSettingsModel?=null

    private val binding by lazy {
        RlFragSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_summary, container) as RlFragSessionSummaryBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionSummary" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }

    //Start Ui
    private fun RLuisetup() {
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
       // RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivBack.setOnClickListener {
            RLcloseScreen(isSessionComplete)
        }
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        RLTools.RLLogLarge(TAG,"cardData: ${Gson().toJson(cardData)}")
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLfetchFirebaseData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                RLcloseScreen(isSessionComplete)
            }
        })
        fragBinding.ivTitle.setText(cardData.className.toString())
        fragBinding.ivDescription.setText(RLTools.RLconvertTimestampToDAte(cardData.timestamp.toLong()))

    }

    //Firebase Fetch User Data and Session Summery Data
    private fun RLfetchFirebaseData() {
        //User Data Fetch to Firebase
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userCardData =userData
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }
        // Firebase to fetch Session Summary Graph Data
        val graphPath = RevoolaFirebasePath.sessionDetailDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().RlreadData(graphPath) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseDetailCardData = gson.fromJson(jsonObject, RLSessionDetailDataModel::class.java)
                RLTools.RLLogLarge(TAG,"Session Summary Detail Data: $jsonObject")
            } else {
                RLTools.RlLogEPrint(TAG,"Session Summary Graph Empty Data")
            }
        }
        // Firebase to fetch Session Summary Data
        val path = RevoolaFirebasePath.sessionSummaryDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseCardData = gson.fromJson(jsonObject, RLSessionSummaryDataModel::class.java)
                RLTools.RLLogLarge(TAG,"Session Summary Data: $jsonObject")
                RLSummaryUiSet()
                RLClickToSetUI()
            } else {
                RLSummaryUiSet()
                RLClickToSetUI()
                RLTools.RlLogEPrint(TAG,"Session Summary Empty Data")
            }
        }


    }

    //Summery Ui SetUp
    private fun RLSummaryUiSet() {
        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE

        val imagelink= RLTools.RLFeedSetImage(cardData,currentUser,selectTag)
        if (isAdded) Glide.with(requireContext()).load(imagelink).into(fragBinding.testImage)


        var imageList:MutableList<String> = mutableListOf()
        val imageListOriginal = listOf(imagelink, RLTools.RLgetImage(classType))
        fragBinding.testImage.visibility=View.GONE
        fragBinding.viewPagerImage.visibility=View.VISIBLE
        fragBinding.intoTabLayout.visibility=View.VISIBLE
        fragBinding.intoTabLayout.setupWithViewPager(fragBinding.viewPagerImage)

        val zoneDataList = listOf(
            RLZoneChartData("Zone1", fireBaseCardData?.zone1?.seconds?:0, "rgb(241, 119, 160)"),
            RLZoneChartData("Zone2", fireBaseCardData?.zone2?.seconds?:0, "rgb(255, 207, 47)"),
            RLZoneChartData("Zone3", fireBaseCardData?.zone3?.seconds?:0, "rgb(44, 174, 44)"),
            RLZoneChartData("Zone4", fireBaseCardData?.zone4?.seconds?:0, "rgb(0, 153, 218)"),
            RLZoneChartData("Zone5", fireBaseCardData?.zone5?.seconds?:0, "rgb(254, 105, 02)"),
            RLZoneChartData("Zone6", fireBaseCardData?.zone6?.seconds?:0, "rgb(153, 0, 204)"),
            RLZoneChartData("Zone7", fireBaseCardData?.zone7?.seconds?:0, "rgb(237, 69, 65)")
        )

        val ZoneTextData= RLTools.RlVerifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
        val effort =RLGetValueForTitle(RLValueName.AvgEffort)
        val effortScore:String =RLGetValueForTitle(RLValueName.Effort)
        val maxEffort = RLGetValueForTitle(RLValueName.MaxEffort)


        when (cardData.bmo){
             0->{ //Body Image Set
                 if (cardData.user_images.isEmpty()){
                     imageList=imageListOriginal.toMutableList()
                 }else{
                     imageList = cardData.user_images.extractImageUrls().toMutableList()
                     imageList.add(RLTools.RLGetLinkImage(cardData.classType?.toLowerCase().toString()))
                 }
             }
            2->{ //Your Way Image Set
                if (cardData.map_image.isEmpty() && cardData.user_images.isNotEmpty()){
                    imageList = cardData.user_images.extractImageUrls().toMutableList()
                    imageList.add(RLTools.RLGetLinkImage(cardData.classType?.toLowerCase().toString()))
                }else if (cardData.map_image.isNotEmpty() && cardData.user_images.isNotEmpty()){
                    imageList = cardData.user_images.extractImageUrls().toMutableList()
                    imageList.add(cardData.map_image)
                    imageList.add(RLTools.RLGetLinkImage(cardData.classType?.toLowerCase().toString()))
                }else{
                    imageList=imageListOriginal.toMutableList()
                }
            }
        }

        if (cardData.hrm==1){
            imageList.add("CHART")
        }

        RLTools.RLheightsetViewPager(fragBinding.viewPagerImage)
        val viewPagerAdapter = RLImagePagerAdapter(activity,imageList,zoneDataList,ZoneTextData,effort,effortScore,maxEffort)
        fragBinding.viewPagerImage.adapter = viewPagerAdapter

        when (classType.toLowerCase()){
            "run"->  RLSummaryNameToUi(RLYourWayName.Run)
            "walk"->  RLSummaryNameToUi(RLYourWayName.Walk)
            "workout"->  RLSummaryNameToUi(RLYourWayName.Workout)
            "ride"->  RLSummaryNameToUi(RLYourWayName.Ride)
            "pilates"->  RLSummaryNameToUi(RLYourWayName.Pilates)
            "warm"->  RLSummaryNameToUi(RLYourWayName.Warm)
            "dance"->  RLSummaryNameToUi(RLYourWayName.Dance)
            "hiit"->  RLSummaryNameToUi(RLYourWayName.Hiit)
            "yoga"->  RLSummaryNameToUi(RLYourWayName.Yoga)
            else -> RLSummaryNameToUi(RLYourWayName.Yoga)
        }
    }
    private fun RLSummaryNameToUi(wayname: RLYourWayName) {
        val isHrConnected = cardData.hrm != 0 // hrm=0 HeartRate Not Connect && hrm!=0 HeartRate Connected
        val isClass = cardData.bmo == 0 //bmo= 0 Your Way && bmo!=0 Class
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val  maxCadence:Int = if (fireBaseCardData?.maxCadence == null) 0 else convertToInt(fireBaseCardData?.maxCadence?:0)
        val Distance = if (isImperial) RLTypeOfMetrics.Distance else  RLTypeOfMetrics.DistanceKm
        val AvgPace = if (isImperial) RLTypeOfMetrics.AvgPace else  RLTypeOfMetrics.AvgPaceKm
        val AvgSpeed = if (isImperial) RLTypeOfMetrics.AvgSpeed else  RLTypeOfMetrics.AvgSpeedKm
        val Climbed = if (isImperial) RLTypeOfMetrics.Climbed else  RLTypeOfMetrics.ClimbedM

        RLTools.RlLogEPrint(TAG,"avgSpeedCaluate:  ${RLGetValueForTitle(RLValueName.AvgSpeed)}")

        var rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = mutableListOf()
        when (wayname) {
            RLYourWayName.Ride -> {
                if(isHrConnected){
                    if(isClass){
                        // cells = [ .Time, .Effort, .HR, .Cadence, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Cadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        // .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else if(maxCadence > 0){
                    if(isClass){
                        //  cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Cadence, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        //  cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        //  .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else{
                    if(isClass){
                        // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .EstimatedEffort, .Distance, .EstimatedCalories,
                        // .AvgMaxSpeed, .Elevation, .Speed, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr= listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
            }
            RLYourWayName.Run -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed,
                    // .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                    // .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Walk -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Pilates -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Warm -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Workout -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Dance -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Hiit -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards)),
                    )
                }
            }
            RLYourWayName.Yoga -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards)),
                    )
                }
            }
        }

        RLSummryListSet(rideListWithoutHr)
    }
    private fun RLSummryListSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }
    private fun RLGetValueForTitle(title: String): String {
        if (userCardData!=null && fireBaseCardData != null){
            val isImperial = RLTools.RLGetIsImperial(userCardData!!.appUnit)
            val avgSpeedForOneKm = RLTools.RLformatTime(convertToInt(fireBaseCardData?.avgSpeedForOneKm?:0),true)
            val avgSpeedForOneMile =RLTools.RLformatTime(convertToInt(fireBaseCardData?.avgSpeedForOneMile?:0),true)
            val avgHeartRate = RLTools.RLformatCommasInt(fireBaseCardData?.avgHr?:0.0)
            val maxHeartRate = RLTools.RLformatCommasInt(fireBaseCardData?.maxHr?:0)


            return when (title) {
                RLValueName.TotalTime -> RLTools.RLformatTime(fireBaseCardData!!.totalTime.toInt(),true)
                RLValueName.Effort  -> RLTools.RLformatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AvgHeartRate  -> if (!checkShowHeartRate()) avgHeartRate else avgHeartRate
                RLValueName.ActiveCalories  -> RLTools.RLformatCommasInt(convertToInt(cardData.power ?: 0))
                RLValueName.Boosts  -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
                RLValueName.Comments  -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
                RLValueName.Awards  -> {
                    val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                    if (totalAwards != 0) totalAwards.toString() else "0"
                }
                RLValueName.Steps  -> RLTools.RLformatCommasInt(fireBaseCardData!!.totalSteps?:0)
                RLValueName.Distance  -> if (!isImperial) RLTools.RLformatCommas(cardData.distance?:0.0) else RLTools.RLformatCommas(cardData.distance * 0.621371)
                RLValueName.Climbed  -> {
                    val demsElevation:Int = convertToInt(fireBaseCardData!!.demsElevation?:-1)
                    val elevation = if (!isImperial) {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                    } else {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084))
                    }
                    elevation.toString()
                }
                RLValueName.AvgPace  -> if (!isImperial) avgSpeedForOneKm else  avgSpeedForOneMile
                RLValueName.AvgSpeed  ->   RLCalculateAvgSpeed(isImperial)
                RLValueName.MaxSpeed  ->    RLCalculateMaxSpeed(isImperial)
                RLValueName.AssumedEffort  -> RLTools.RLformatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AssumedCalories  -> RLTools.RLformatCommasInt(fireBaseCardData!!.burntCalories ?: 0.0)
                RLValueName.AvgCadence  ->  RLTools.RLformatCommasInt(fireBaseCardData!!.avgCadence?:0.0)
                RLValueName.MaxHeartRate  ->  maxHeartRate
                RLValueName.AvgEffort -> convertToInt(cardData.avgRevPercentage).toString()+"%"
                RLValueName.MaxEffort -> convertToInt(cardData.maxRevPercentage).toString()+"%"

                RLValueName.MinElevation -> RLCalculateMaxAndMinElevation(isImperial,false)
                RLValueName.MaxElevation -> RLCalculateMaxAndMinElevation(isImperial,true)

                RLValueName.Completed -> if (isImperial) fireBaseDetailCardData?.speedForOneMile?.size.toString() else fireBaseDetailCardData?.speedForOneKm?.size.toString()
                RLValueName.AvaragePace -> RLCalculateAvaragePace(isImperial)
                RLValueName.Slowtest -> RLCalculateSlowtestAndFasttestPace(isImperial,false)
                RLValueName.Fasttest -> RLCalculateSlowtestAndFasttestPace(isImperial,true)

                else -> "0"
            }
        }
        else{
            return when (title) {
                RLValueName.TotalTime -> RLTools.RLformatTime(cardData.totalTime.toInt(),true)
                RLValueName.Effort  -> RLTools.RLformatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AssumedEffort  -> RLTools.RLformatCommasInt(cardData.totalREV.roundToInt())
                else -> "0"
            }
            return "0"
        }

    }

    private fun RLCalculateAvaragePace(isImperial: Boolean):String{
        if (isImperial){
           val time =  fireBaseDetailCardData?.speedForOneMile?.average()?:0.0
           return RLTools.RLformatTimeNoMS(time.toInt(),true)
        } else {
            val time =  fireBaseDetailCardData?.speedForOneKm?.average()?:0.0
            return   RLTools.RLformatTimeNoMS(time.toInt(),true)
        }

    }

    private fun RLCalculateMaxAndMinElevation(isImperial: Boolean,isMax:Boolean):String{
        if (isImperial){
            if (isMax){
                val elevation =  (fireBaseDetailCardData?.arrElevation?.max()?:0.0) * 3.28084
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.RLformatCommasInt(elevation.toInt())
                }
            }else{
                val elevation =  (fireBaseDetailCardData?.arrElevation?.min()?:0.0) * 3.28084
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.RLformatCommasInt(elevation.toInt())
                }
            }

        }else{
            if (isMax){
                val elevation =  (fireBaseDetailCardData?.arrElevation?.max()?:0.0)
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.RLformatCommasInt(elevation.toInt())
                }
            }else{
                val elevation =  (fireBaseDetailCardData?.arrElevation?.min()?:0.0)
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.RLformatCommasInt(elevation.toInt())
                }
            }
        }
    }

    private fun RLCalculateSlowtestAndFasttestPace(isImperial: Boolean,isFastest:Boolean):String{
        if (isImperial){
            if (isFastest){
                val time =  fireBaseDetailCardData?.speedForOneMile?.min()?:0.0
                return RLTools.RLformatTimeNoMS(time.toInt(),true)
            }else{
                val time =  fireBaseDetailCardData?.speedForOneMile?.max()?:0.0
                return RLTools.RLformatTimeNoMS(time.toInt(),true)
            }

        } else {
            if (isFastest){
                val time =  fireBaseDetailCardData?.speedForOneKm?.min()?:0.0
                return RLTools.RLformatTimeNoMS(time.toInt(),true)
            }else{
                val time =  fireBaseDetailCardData?.speedForOneKm?.max()?:0.0
                return RLTools.RLformatTimeNoMS(time.toInt(),true)
            }
        }
    }


    private  fun RLCalculateMaxSpeed(isImperial: Boolean): String {
        val maxSpeed:Double = fireBaseDetailCardData?.arrSpeed?.takeIf { it.isNotEmpty() }?.maxOrNull() ?: 0.0

        return if (maxSpeed.isNaN() || maxSpeed.isInfinite()) {
            "0"
        } else if (isImperial) {
            String.format("%.2f", maxSpeed / 1.609) // Convert to miles per hour if imperial
        }else{
            String.format("%.2f", maxSpeed)  // Return speed in km/h
        }
    }

    private  fun RLCalculateAvgSpeed(isImperial: Boolean): String {
        val totalTime = fireBaseCardData?.totalTime?:0
        val  distance = fireBaseCardData?.distance?:0.0

        val tempTime = totalTime / 3600 // Convert time to hours
        val tempAvgSpeed = distance / tempTime // Speed in km/h
        RLTools.RlLogEPrint(TAG,"AVGSPD:- $tempAvgSpeed")
        // Ensure valid output
        return if (tempAvgSpeed.isNaN() || tempAvgSpeed.isInfinite()) {
            "0"
        } else if (isImperial) {
            String.format("%.2f", tempAvgSpeed / 1.609) // Convert to miles per hour if imperial
        }else{
            String.format("%.2f", tempAvgSpeed)
        }
    }

    //Click Wise Ui SetUp
    private fun RLClickToSetUI() {
        fragBinding.inlayTitle.layoutSummary.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            RLSummaryUiSet()
        }
        fragBinding.inlayTitle.layoutAnalysis.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            RLanalysisDataSet()
        }
        fragBinding.inlayTitle.layoutEffort.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppMainColor)
            RLeffortDataSet()
        }
    }

    //Analysis Ui SetUp
    private fun RLanalysisDataSet(){
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.VISIBLE
        fragBinding.relayEffort.visibility=View.GONE
        if (cardData.hrm==0){
            //Without HR Sensor
            fragBinding.includeEffort.relativeCard.visibility=View.GONE
            fragBinding.includeElevation.relativeCard.visibility=View.GONE
            fragBinding.includePace.relativeCard.visibility=View.GONE
            fragBinding.includeSpeed.relativeCard.visibility=View.GONE
            fragBinding.txtWithouthrmessageAnalysis.visibility=View.VISIBLE
            if( classType.toLowerCase() in listOf("ride", "run", "walk")){
                val isSpeedGraph = fireBaseDetailCardData?.arrSpeed?.any { convertToInt(it) > 0 } == true
                if (isSpeedGraph){
                    fragBinding.includeSpeed.relativeCard.visibility=View.VISIBLE
                    fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                    RLanalysisSpeedUISetup()
                }
                val isPaceGraph = fireBaseDetailCardData?.speedForOneKm?.any { convertToInt(it) > 0 } == true
                if (isPaceGraph){
                    fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                    fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                    RLanalysisPaceUISetup()
                }
                if (!fireBaseDetailCardData?.arrElevation.isNullOrEmpty()){
                    val elevation = if (fireBaseCardData?.demsElevation == 0.0 && fireBaseCardData?.totalElevation != 0.0) {
                        fireBaseCardData?.totalElevation
                    } else {
                        fireBaseCardData?.demsElevation
                    } ?: 0.0

                    if (convertToInt(elevation) > 5 || elevation == -1.0) {
                        fragBinding.includeElevation.relativeCard.visibility=View.VISIBLE
                        RLanalysisElevationUISetup()
                    }
                }
            }
        }
        else{
            //With HR Sensor
            fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
            fragBinding.includeEffort.relativeCard.visibility=View.GONE
            fragBinding.includeElevation.relativeCard.visibility=View.GONE
            fragBinding.includePace.relativeCard.visibility=View.GONE
            fragBinding.includeSpeed.relativeCard.visibility=View.GONE

            if(cardData.bmo ==2 ){
                // Your Way Map
                if( classType.toLowerCase() in listOf("ride", "run", "walk") && !fireBaseDetailCardData?.arrElevation.isNullOrEmpty()){
                    val elevation = if (fireBaseCardData?.demsElevation == 0.0 && fireBaseCardData?.totalElevation != 0.0) {
                        fireBaseCardData?.totalElevation
                    } else {
                        fireBaseCardData?.demsElevation
                    } ?: 0.0

                    if (convertToInt(elevation) > 5 || elevation == -1.0) {
                        fragBinding.includeElevation.relativeCard.visibility=View.VISIBLE
                        RLanalysisElevationUISetup()
                    }
                }
            }

            val isEffortGraph = fireBaseDetailCardData?.arrRevPercentage?.any { convertToInt(it) > 0 } == true
            if (isEffortGraph){
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                RLanalysisEffortUISetup()
            }

            if( classType.toLowerCase() in listOf("ride", "run", "walk")){
                val isSpeedGraph = fireBaseDetailCardData?.arrSpeed?.any { convertToInt(it) > 0 } == true
                if (isSpeedGraph){
                    fragBinding.includeSpeed.relativeCard.visibility=View.VISIBLE
                    RLanalysisSpeedUISetup()
                }
                val isPaceGraph = fireBaseDetailCardData?.speedForOneKm?.any { convertToInt(it) > 0 } == true
                if (isPaceGraph){
                    fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                    RLanalysisPaceUISetup()
                }
            }


        }
    }
    //Effort Chart
    private fun RLanalysisEffortUISetup(){
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
            RLTypeOfMetrics.EffortZone to RLMetricData("CALM"),
            RLTypeOfMetrics.AvgEffort to RLMetricData(RLGetValueForTitle(RLValueName.AvgEffort)),
            RLTypeOfMetrics.MaxEffort to RLMetricData(RLGetValueForTitle(RLValueName.MaxEffort)),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
            RLTypeOfMetrics.MaxHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.MaxHeartRate))
        )

        RLheightsetdisplaywebview(fragBinding.includeEffort.webViewAnalysis)
        fragBinding.includeEffort.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeEffort.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeEffort.webViewAnalysis.loadUrl("file:///android_asset/chart-android-effort.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeEffort.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeEffort.recycleAnalysis.adapter = adapterdata

        RLinjectDataIntoWebView(RLConstants.EFFORT)

    }
    //Pace Chart
    private fun RLanalysisPaceUISetup(){
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val completed = if (isImperial) RLTypeOfMetrics.completed else  RLTypeOfMetrics.completedKm

        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            completed to RLMetricData(RLGetValueForTitle(RLValueName.Completed)),
            RLTypeOfMetrics.Averagepace to RLMetricData(RLGetValueForTitle(RLValueName.AvaragePace)),
            RLTypeOfMetrics.Slowtest to RLMetricData(RLGetValueForTitle(RLValueName.Slowtest)),
            RLTypeOfMetrics.Fasttest to RLMetricData(RLGetValueForTitle(RLValueName.Fasttest))
        )

        fragBinding.includePace.imgTitleAnalysis.setImageResource(R.drawable.ic_pace)
        fragBinding.includePace.txtTitleAnalysis.setText("PACE")
        fragBinding.includePace.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includePace.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includePace.webViewAnalysis.loadUrl("file:///android_asset/chart-android-pace.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includePace.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includePace.recycleAnalysis.adapter = adapterdata

        RLinjectDataIntoWebView(RLConstants.PACE)
    }
    //Speed Chart
    private fun RLanalysisSpeedUISetup(){
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val Averagespeed = if (isImperial) RLTypeOfMetrics.Averagespeed else  RLTypeOfMetrics.AveragespeedKm
        val MaxSpeed = if (isImperial) RLTypeOfMetrics.MaxSpeed else  RLTypeOfMetrics.MaxSpeedKM

        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Averagespeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
            MaxSpeed to RLMetricData(RLGetValueForTitle(RLValueName.MaxSpeed))
        )

        fragBinding.includeSpeed.imgTitleAnalysis.setImageResource(R.drawable.ic_speeed)
        fragBinding.includeSpeed.txtTitleAnalysis.setText("Speed")

        RLheightsetdisplaywebview(fragBinding.includeSpeed.webViewAnalysis)
        fragBinding.includeSpeed.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeSpeed.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeSpeed.webViewAnalysis.loadUrl("file:///android_asset/chart-android-speed.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeSpeed.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeSpeed.recycleAnalysis.adapter = adapterdata

        RLinjectDataIntoWebView(RLConstants.SPEED)
    }
    //Elevation Chart
    private fun RLanalysisElevationUISetup(){
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val Totalclimbed = if (isImperial) RLTypeOfMetrics.Totalelevation else  RLTypeOfMetrics.TotalelevationM
        val Minelevation = if (isImperial) RLTypeOfMetrics.Minelevation else  RLTypeOfMetrics.MinelevationM
        val Maxelevation = if (isImperial) RLTypeOfMetrics.Maxelevation else  RLTypeOfMetrics.MaxelevationM

        val  dataList: List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Totalclimbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
            Minelevation to RLMetricData(RLGetValueForTitle(RLValueName.MinElevation)),
            Maxelevation to RLMetricData(RLGetValueForTitle(RLValueName.MaxElevation))
        )

        fragBinding.includeElevation.imgTitleAnalysis.setImageResource(R.drawable.ic_climb)
        fragBinding.includeElevation.txtTitleAnalysis.setText("Elevation")

        RLheightsetdisplaywebview(fragBinding.includeElevation.webViewAnalysis)
        fragBinding.includeElevation.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeElevation.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeElevation.webViewAnalysis.loadUrl("file:///android_asset/chart-android-elevation.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeElevation.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeElevation.recycleAnalysis.adapter = adapterdata

        RLinjectDataIntoWebView(RLConstants.ELEVATION)

    }
    //Insert Data All Chart
    private fun RLinjectDataIntoWebView(mapType:String) {
        val arrCumDistance = fireBaseDetailCardData?.arrCumDistance!!
        val arrElevation = fireBaseDetailCardData?.arrElevation!!
        val arrSpeed = fireBaseDetailCardData?.arrSpeed!!
        var  speedForPace = fireBaseDetailCardData?.speedForOneKm!!

        val isImperial = RLTools.RLGetIsImperial(userCardData!!.appUnit)
        if (isImperial){
            speedForPace=  fireBaseDetailCardData?.speedForOneMile!!
        }

        // Inject JSON data into WebView's JavaScript context
        if (mapType.equals(RLConstants.EFFORT)) {
            val revData = fireBaseDetailCardData?.arrRevPercentage
            val jsonArray: JSONArray = JSONArray()
            revData?.forEachIndexed { index, value ->
                if (value != null) {
                    val jsonObject: JSONObject = JSONObject()
                    jsonObject.put("time", index)
                    jsonObject.put("effort", value)
                    jsonArray.put(jsonObject)
                }
            }


            fragBinding.includeEffort.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetEffortChartHtml(jsonArray.toString()), "text/html", "UTF-8", null)
        }else if(mapType.equals(RLConstants.ELEVATION)) {
            val cumDistance = arrCumDistance
            val elevation =  arrElevation
            var metersLabel = "METERS"  // Dynamic Speed Label
            var  kmsLabel = "KM's"  // Dynamic Distance Label
            if (isImperial){
                metersLabel="FEET"
                kmsLabel="Miles's"
            }

            fragBinding.includeElevation.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetElevationHtml( cumDistance.toString(),elevation.toString(),metersLabel,kmsLabel), "text/html", "UTF-8", null)
        }else if(mapType.equals(RLConstants.SPEED)) {
            val speed= arrSpeed
            val cumDistance= arrCumDistance
            val elevation= arrElevation
            var kmhLabel = "KMH"  // Dynamic Speed Label
            var  kmsLabel = "KM's"  // Dynamic Distance Label
            if (isImperial){
                kmhLabel="MPH"
                kmsLabel="Miles's"
            }

            fragBinding.includeSpeed.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetSpeedHtml( cumDistance.toString(),elevation.toString(),speed.toString(),kmhLabel,kmsLabel), "text/html", "UTF-8", null)
        }else if(mapType.equals(RLConstants.PACE)) {
            val timeData =speedForPace



            fragBinding.includePace.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetPaceChartHtml( timeData.toString()), "text/html", "UTF-8", null)

        }
    }


    //Effort Ui SetUp
    private fun RLeffortDataSet(){
        val dataList: List<RLZoneChartScoreData> = listOf(
            RLZoneChartScoreData("Calm", fireBaseCardData?.zone1?.seconds?:0, fireBaseCardData?.zone1?.totalRev?:0.0),
            RLZoneChartScoreData("Warm", fireBaseCardData?.zone2?.seconds?:0, fireBaseCardData?.zone2?.totalRev?:0.0),
            RLZoneChartScoreData("Cardio", fireBaseCardData?.zone3?.seconds?:0, fireBaseCardData?.zone3?.totalRev?:0.0),
            RLZoneChartScoreData("Fat Burn", fireBaseCardData?.zone4?.seconds?:0, fireBaseCardData?.zone4?.totalRev?:0.0),
            RLZoneChartScoreData("Endurance", fireBaseCardData?.zone5?.seconds?:0, fireBaseCardData?.zone5?.totalRev?:0.0),
            RLZoneChartScoreData("Power", fireBaseCardData?.zone6?.seconds?:0, fireBaseCardData?.zone6?.totalRev?:0.0),
            RLZoneChartScoreData("Peek", fireBaseCardData?.zone7?.seconds?:0, fireBaseCardData?.zone7?.totalRev?:0.0)
        )
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.VISIBLE

        val ZoneTextData= RLTools.RlVerifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)

        fragBinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        fragBinding.inlayChart.layEffortZone.txtNumber.setText(ZoneTextData.efforZoneText)
        fragBinding.inlayChart.layEffortZone.txtNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        fragBinding.inlayChart.layAll.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        fragBinding.inlayChart.relayChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        fragBinding.inlayChart.webViewChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        val efforZoneBgrClr =  ZoneTextData.efforZoneBgrClr

        val effort =RLGetValueForTitle(RLValueName.AvgEffort)
        val effortScore:String =RLGetValueForTitle(RLValueName.Effort)
        val maxEffort = RLGetValueForTitle(RLValueName.MaxEffort)


        fragBinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        fragBinding.inlayChart.layEffort.txtNumber.setText(effort)

        fragBinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        fragBinding.inlayChart.layEffortScore.txtNumber.setText(effortScore)

        fragBinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        fragBinding.inlayChart.layMaxEffort.txtNumber.setText(maxEffort)

        RLTools.RLheightsetdisplaywebview(fragBinding.inlayChart.webViewChart,activity)
        fragBinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        val zoneDataList = listOf(
            RLZoneChartData("Zone1", fireBaseCardData?.zone1?.seconds?:0, "rgb(241, 119, 160)"),
            RLZoneChartData("Zone2", fireBaseCardData?.zone2?.seconds?:0, "rgb(255, 207, 47)"),
            RLZoneChartData("Zone3", fireBaseCardData?.zone3?.seconds?:0, "rgb(44, 174, 44)"),
            RLZoneChartData("Zone4", fireBaseCardData?.zone4?.seconds?:0, "rgb(0, 153, 218)"),
            RLZoneChartData("Zone5", fireBaseCardData?.zone5?.seconds?:0, "rgb(254, 105, 02)"),
            RLZoneChartData("Zone6", fireBaseCardData?.zone6?.seconds?:0, "rgb(153, 0, 204)"),
            RLZoneChartData("Zone7", fireBaseCardData?.zone7?.seconds?:0, "rgb(237, 69, 65)")
        )


        fragBinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.RLGetNewZoneChartHtml(zoneDataList,efforZoneBgrClr), "text/html", "UTF-8", null)

        //Main list set
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleEffort.layoutManager = linearLayoutManager
        val adapterdata = RLFeedSessionEffortListAdapter(activity, dataList,fireBaseCardData?.totalTime?:0)
        fragBinding.recycleEffort.adapter = adapterdata

        if (cardData.hrm==0){
            //Without HR Sensor
            fragBinding.inlayChart.layAll.visibility=View.GONE
            fragBinding.layEffortTit.visibility=View.GONE
            fragBinding.recycleEffort.visibility=View.GONE
            fragBinding.txtWithouthrmessageEffort.visibility=View.VISIBLE
        }else{
            //With HR Sensor
            fragBinding.inlayChart.layAll.visibility=View.VISIBLE
            fragBinding.layEffortTit.visibility=View.VISIBLE
            fragBinding.recycleEffort.visibility=View.VISIBLE
            fragBinding.txtWithouthrmessageEffort.visibility=View.GONE
        }
    }

    //Common All UI Setup
    private fun RLheightsetdisplaywebview(webView: WebView) {
       RLTools.RLheightsetdisplaywebview(webView,activity)
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }
    private fun RLcloseScreen(isSessionComplete:Boolean){
        if (isSessionComplete){
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }else{
            RLcloseFragment()
        }
    }

    //Calculation All UI Setup
    private fun String.extractImageUrls(): List<String> {
        return this.replace("\\", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    private fun convertToInt(value: Any): Int {
        return when (value) {
            is Double -> value.roundToInt()
            is Float -> value.roundToInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
            else -> 0 // Default fallback for unsupported types
        }
    }
    private fun checkShowHeartRate(): Boolean {
        if (!userCardData?.currentGroup.isNullOrEmpty() && userCardData?.currentGroup!!.contains("schooltype1")) {
            return false
        }
        return true
    }

}