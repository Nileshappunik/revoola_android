package com.revoola.fragment.feed

import android.graphics.Color
import android.os.Bundle
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
import com.revoola.RLBaseProgress
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
import com.revoola.fragment.feed.adapter.OnImageClickListener
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLZoneChartData
import com.revoola.model.RLZoneChartScoreData
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class RLFragSessionSummary : RLBaseFragment() , OnImageClickListener {
    val TAG: String = RLFragSessionSummary::class.java.simpleName
    lateinit var cardData: RLTextOverview
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var currentUser:String=""
    private var classType=""
    private var selectTag=""
    private var fireBaseCardData: RLSessionSummaryDataModel?=null
    private var fireBaseDetailCardData: RLSessionDetailDataModel?=null
    private var userCardData: RLRevoolaUsersSettingsModel?=null
    private var mapObject:String =""

    private val fragBinding by lazy {
        RlFragSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionSummary" )
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }

    //Start Ui
    private fun rl_uisetup() {
      //  if (isAdded) RLBaseProgress.rl_showProgressDialog(requireActivity())
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
       // RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivBack.setOnClickListener {
            rl_closeScreen(isSessionComplete)
        }
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        RLTools.rl_logLarge(TAG,"cardData: ${Gson().toJson(cardData)}")
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        rl_fetchFirebaseData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                rl_closeScreen(isSessionComplete)
            }
        })
        fragBinding.ivTitle.setText(cardData.className ?: "")
        fragBinding.ivDescription.setText(RLTools.rl_convertTimestampToDAte(cardData.timestamp.toLong()))

    }

    //Firebase Fetch User Data and Session Summery Data
    private fun rl_fetchFirebaseData() {
        //User Data Fetch to Firebase
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                userCardData =userData
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }
        // Firebase to fetch Session Summary Graph Data
        val graphPath = RevoolaFirebasePath.sessionDetailDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().rl_readData(graphPath) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseDetailCardData = gson.fromJson(jsonObject, RLSessionDetailDataModel::class.java)
                RLTools.rl_logLarge(TAG,"Session Summary Detail Data: $jsonObject")
            } else {
                RLTools.rl_logEPrint(TAG,"Session Summary Graph Empty Data")
            }
        }
        // Firebase to fetch Session Summary Data
        val path = RevoolaFirebasePath.sessionSummaryDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().rl_readData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseCardData = gson.fromJson(jsonObject, RLSessionSummaryDataModel::class.java)
                RLTools.rl_logLarge(TAG,"Session Summary Data: $jsonObject")
                rl_summaryUiSet()
                rl_clickToSetUI()
            } else {
                rl_summaryUiSet()
                rl_clickToSetUI()
                RLTools.rl_logEPrint(TAG,"Session Summary Empty Data")
            }
        }

        val mapPath = RevoolaFirebasePath.dataForTestingPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().rl_d2DataBaseReadData(mapPath) { data, error ->
            if (data != null) {
                  mapObject =  Gson().toJson(data)
                RLTools.rl_logEPrint(TAG,"dataForTesting Data: $mapObject")
            } else {
                RLTools.rl_logEPrint(TAG,"dataForTesting Error: $error")
            }
        }
    }

    //Summery Ui SetUp
    private fun rl_summaryUiSetOld() {
        // Add null safety checks at the beginning
        if (cardData == null) {
            RLTools.rl_logEPrint(TAG, "cardData is null")
            return
        }
        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE

        val imagelink= RLTools.rl_feedSetImage(cardData,currentUser,selectTag)
        if (isAdded) Glide.with(requireContext()).load(imagelink).into(fragBinding.testImage)

        var imageList:MutableList<String> = mutableListOf()
        val imageListOriginal = listOf(imagelink, RLTools.rl_getImage(classType))
        RLBaseProgress.rl_hideProgressDialog()
        fragBinding.testImage.visibility=View.GONE
        fragBinding.viewPagerImage.visibility=View.VISIBLE
        fragBinding.constantPager.visibility=View.VISIBLE
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

        val ZoneTextData= RLTools.rl_verifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
        val effort =rl_getValueForTitle(RLValueName.AvgEffort)
        val effortScore:String =rl_getValueForTitle(RLValueName.Effort)
        val maxEffort = rl_getValueForTitle(RLValueName.MaxEffort)


        when (cardData.bmo){
             0->{ //Body Image Set
                 if (cardData.user_images.isEmpty()){
                     imageList=imageListOriginal.toMutableList()
                 }else{
                     imageList = cardData.user_images.extractImageUrls().toMutableList()
                     imageList.add(RLTools.rl_getLinkImage(cardData.classType?:"".toLowerCase().toString()))
                 }
             }
            2->{ //Your Way Image Set
                if (cardData.map_image.isNullOrEmpty() && cardData.user_images.isNotEmpty()){
                    imageList = cardData.user_images.extractImageUrls().toMutableList()
                    imageList.add(RLTools.rl_getLinkImage(cardData.classType?:"".toLowerCase().toString()))
                }else if (cardData.map_image.isNotEmpty() && cardData.user_images.isNotEmpty()){
                    imageList = cardData.user_images.extractImageUrls().toMutableList()
                    imageList.add(cardData.map_image)
                    imageList.add(RLTools.rl_getLinkImage(cardData.classType?:"".toLowerCase().toString()))
                }else{
                    imageList=imageListOriginal.toMutableList()
                }
            }
        }

        if (cardData.hrm==1){
            imageList.add("CHART")
        }


        val viewPagerAdapter = RLImagePagerAdapter(activity,imageList,zoneDataList,ZoneTextData,effort,effortScore,maxEffort,this)
        fragBinding.viewPagerImage.adapter = viewPagerAdapter

        when (classType.toLowerCase()){
            "run"->  rl_summaryNameToUi(RLYourWayName.Run)
            "walk"->  rl_summaryNameToUi(RLYourWayName.Walk)
            "workout"->  rl_summaryNameToUi(RLYourWayName.Workout)
            "ride"->  rl_summaryNameToUi(RLYourWayName.Ride)
            "pilates"->  rl_summaryNameToUi(RLYourWayName.Pilates)
            "warm"->  rl_summaryNameToUi(RLYourWayName.Warm)
            "dance"->  rl_summaryNameToUi(RLYourWayName.Dance)
            "hiit"->  rl_summaryNameToUi(RLYourWayName.Hiit)
            "yoga"->  rl_summaryNameToUi(RLYourWayName.Yoga)
            else -> rl_summaryNameToUi(RLYourWayName.Yoga)
        }
    }

    private fun rl_summaryUiSet() {
        // 0) Basic guards
        val localCard = cardData
        if (localCard == null) {
            RLTools.rl_logEPrint(TAG, "cardData is null")
            return
        }
        if (!isAdded) return

        fragBinding.relaySummary.visibility = View.VISIBLE
        fragBinding.relayAnalysis.visibility = View.GONE
        fragBinding.relayEffort.visibility = View.GONE

        // 1) Safe image sources
        val imageLink = RLTools.rl_feedSetImage(localCard, currentUser, selectTag).orEmpty()
        Glide.with(requireContext()).load(imageLink).into(fragBinding.testImage)


        val classTypeLower = classType?.lowercase() ?: ""
        val imageListOriginal = listOf(imageLink)

        RLBaseProgress.rl_hideProgressDialog()
        fragBinding.testImage.visibility = View.GONE
        fragBinding.viewPagerImage.visibility = View.VISIBLE
        fragBinding.constantPager.visibility = View.VISIBLE
        fragBinding.intoTabLayout.visibility = View.VISIBLE
        fragBinding.intoTabLayout.setupWithViewPager(fragBinding.viewPagerImage)

        // 2) Zone data (already safe with Elvis to 0)
        val zoneDataList = listOf(
            RLZoneChartData("Zone1", fireBaseCardData?.zone1?.seconds ?: 0, "rgb(241, 119, 160)"),
            RLZoneChartData("Zone2", fireBaseCardData?.zone2?.seconds ?: 0, "rgb(255, 207, 47)"),
            RLZoneChartData("Zone3", fireBaseCardData?.zone3?.seconds ?: 0, "rgb(44, 174, 44)"),
            RLZoneChartData("Zone4", fireBaseCardData?.zone4?.seconds ?: 0, "rgb(0, 153, 218)"),
            RLZoneChartData("Zone5", fireBaseCardData?.zone5?.seconds ?: 0, "rgb(254, 105, 02)"),
            RLZoneChartData("Zone6", fireBaseCardData?.zone6?.seconds ?: 0, "rgb(153, 0, 204)"),
            RLZoneChartData("Zone7", fireBaseCardData?.zone7?.seconds ?: 0, "rgb(237, 69, 65)")
        )

        val avgRevPct = localCard.avgRevPercentage?.toString()?.toDoubleOrNull() ?: 0.0
        val zoneTextData = RLTools.rl_verifyFeedZoneName(avgRevPct)
        val effort = rl_getValueForTitle(RLValueName.AvgEffort)
        val effortScore: String = rl_getValueForTitle(RLValueName.Effort)
        val maxEffort = rl_getValueForTitle(RLValueName.MaxEffort)

        // 3) Build image list with null-safe checks
        val userImages = localCard.user_images?.extractImageUrls().orEmpty()
        val mapImage = localCard.map_image // platform type? treat as nullable
        val rlImage = RLTools.rl_getImage(classTypeLower)
        var imageList: MutableList<String> = mutableListOf()

        when (localCard.bmo) {
            0 -> { // Body
                imageList = if (userImages.isEmpty()) {
                    imageListOriginal.toMutableList()
                } else {
                    (userImages + rlImage).toMutableList()
                }
            }

            2 -> { // Your Way
                imageList = when {
                    // map_image empty/null + user images present
                    mapImage.isNullOrEmpty() && userImages.isNotEmpty() -> {
                        (userImages + rlImage).toMutableList()
                    }
                    // map_image present + user images present
                    !mapImage.isNullOrEmpty() && userImages.isNotEmpty() -> {
                        (userImages + mapImage).toMutableList()
                    }
                    else -> imageListOriginal.toMutableList()
                }
            }

            else -> {
                imageList = imageListOriginal.toMutableList()
            }
        }


        // --- Insert CHART in right position ---
        if (localCard.hrm == 1) {
            if (!mapImage.isNullOrEmpty()) {
                // Map exists → insert CHART at index 1 (second position)
                imageList.add(1, "CHART")
                // RLTools image should be last
                if (!imageList.contains(rlImage)) {
                    imageList.add(rlImage)
                }
            } else {
                // No map → CHART goes first
                imageList.add(0, "CHART")
            }
        }

        val viewPagerAdapter = RLImagePagerAdapter(
            activity,
            imageList,
            zoneDataList,
            zoneTextData,
            effort,
            effortScore,
            maxEffort,
            this
        )
        fragBinding.viewPagerImage.adapter = viewPagerAdapter

        // 4) Safe class type mapping
        when (classTypeLower) {
            "run" -> rl_summaryNameToUi(RLYourWayName.Run)
            "walk" -> rl_summaryNameToUi(RLYourWayName.Walk)
            "workout" -> rl_summaryNameToUi(RLYourWayName.Workout)
            "ride" -> rl_summaryNameToUi(RLYourWayName.Ride)
            "pilates" -> rl_summaryNameToUi(RLYourWayName.Pilates)
            "warm" -> rl_summaryNameToUi(RLYourWayName.Warm)
            "dance" -> rl_summaryNameToUi(RLYourWayName.Dance)
            "hiit" -> rl_summaryNameToUi(RLYourWayName.Hiit)
            "yoga" -> rl_summaryNameToUi(RLYourWayName.Yoga)
            else -> rl_summaryNameToUi(RLYourWayName.Yoga)
        }
    }

    private fun rl_summaryNameToUi(wayname: RLYourWayName) {
        val isHrConnected = cardData.hrm != 0 // hrm=0 HeartRate Not Connect && hrm!=0 HeartRate Connected
        val isClass = cardData.bmo == 0 //bmo= 0 Your Way && bmo!=0 Class
        val isImperial = RLTools.rl_getIsImperial(userCardData?.appUnit?:"Metric")
        val  maxCadence:Int = if (fireBaseCardData?.maxCadence == null) 0 else convertToInt(fireBaseCardData?.maxCadence?:0)
        val Distance = if (isImperial) RLTypeOfMetrics.Distance else  RLTypeOfMetrics.DistanceKm
        val AvgPace = if (isImperial) RLTypeOfMetrics.AvgPace else  RLTypeOfMetrics.AvgPaceKm
        val AvgSpeed = if (isImperial) RLTypeOfMetrics.AvgSpeed else  RLTypeOfMetrics.AvgSpeedKm
        val Climbed = if (isImperial) RLTypeOfMetrics.Climbed else  RLTypeOfMetrics.ClimbedM

        RLTools.rl_logEPrint(TAG,"avgSpeedCaluate:  ${rl_getValueForTitle(RLValueName.AvgSpeed)}")

        var rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = mutableListOf()
        when (wayname) {
            RLYourWayName.Ride -> {
                if(isHrConnected){
                    if(isClass){
                        // cells = [ .Time, .Effort, .HR, .Cadence, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Cadence to RLMetricData(rl_getValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        // .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(rl_getValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else if(maxCadence > 0){
                    if(isClass){
                        //  cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Cadence, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(rl_getValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        //  cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        //  .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(rl_getValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else{
                    if(isClass){
                        // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .EstimatedEffort, .Distance, .EstimatedCalories,
                        // .AvgMaxSpeed, .Elevation, .Speed, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr= listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                            Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                            AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                            Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                            AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
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
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(rl_getValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                    // .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(rl_getValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Walk -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(rl_getValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(rl_getValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(rl_getValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(rl_getValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Pilates -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Warm -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Workout -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Dance -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Hiit -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards)),
                    )
                }
            }
            RLYourWayName.Yoga -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(rl_getValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(rl_getValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(rl_getValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(rl_getValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(rl_getValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(rl_getValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(rl_getValueForTitle(RLValueName.Awards)),
                    )
                }
            }
        }

        rl_summryListSet(rideListWithoutHr)
    }
    private fun rl_summryListSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
       // RLTools.rl_heightsetimageview( fragBinding.testImage)
    }
    private fun rl_getValueForTitle(title: String): String {
        if (userCardData!=null && fireBaseCardData != null){
            val isImperial = RLTools.rl_getIsImperial(userCardData!!.appUnit)
            val avgSpeedForOneKm = RLTools.rl_formatTime(convertToInt(fireBaseCardData?.avgSpeedForOneKm?:0),true)
            val avgSpeedForOneMile =RLTools.rl_formatTime(convertToInt(fireBaseCardData?.avgSpeedForOneMile?:0),true)
            val avgHeartRate = RLTools.rl_formatCommasInt(fireBaseCardData?.avgHr?:0.0)
            val maxHeartRate = RLTools.rl_formatCommasInt(fireBaseCardData?.maxHr?:0)

            return when (title) {
                RLValueName.TotalTime -> RLTools.rl_formatTime(fireBaseCardData!!.totalTime.toInt(),true)
                RLValueName.Effort  -> RLTools.rl_formatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AvgHeartRate  -> if (!checkShowHeartRate()) avgHeartRate else avgHeartRate
                RLValueName.ActiveCalories  -> RLTools.rl_formatCommasInt(convertToInt(cardData.power ?: 0))
                RLValueName.Boosts  -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
                RLValueName.Comments  -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
                RLValueName.Awards  -> {
                    val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                    if (totalAwards != 0) totalAwards.toString() else "0"
                }
                RLValueName.Steps  -> RLTools.rl_formatCommasInt(fireBaseCardData!!.totalSteps?:0)
                RLValueName.Distance  -> if (!isImperial) RLTools.rl_formatCommas(cardData.distance?:0.0) else RLTools.rl_formatCommas(cardData.distance * 0.621371)
                RLValueName.Climbed  -> {
                    val demsElevation:Int = convertToInt(fireBaseCardData!!.demsElevation?:-1)
                    val elevation = if (!isImperial) {
                        if (demsElevation == -1) "Pending" else RLTools.rl_formatCommasInt(demsElevation)
                    } else {
                        if (demsElevation == -1) "Pending" else RLTools.rl_formatCommasInt((demsElevation * 3.28084))
                    }
                    elevation.toString()
                }
                RLValueName.AvgPace  -> if (!isImperial) avgSpeedForOneKm else  avgSpeedForOneMile
                RLValueName.AvgSpeed  ->   rl_calculateAvgSpeed(isImperial)
                RLValueName.MaxSpeed  ->    rl_calculateMaxSpeed(isImperial)
                RLValueName.AssumedEffort  -> RLTools.rl_formatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AssumedCalories  -> RLTools.rl_formatCommasInt(fireBaseCardData!!.burntCalories ?: 0.0)
                RLValueName.AvgCadence  ->  RLTools.rl_formatCommasInt(fireBaseCardData!!.avgCadence?:0.0)
                RLValueName.MaxHeartRate  ->  maxHeartRate
                RLValueName.AvgEffort -> convertToInt(cardData.avgRevPercentage).toString()+"%"
                RLValueName.MaxEffort -> convertToInt(cardData.maxRevPercentage).toString()+"%"

                RLValueName.MinElevation -> rl_calculateMaxAndMinElevation(isImperial,false)
                RLValueName.MaxElevation -> rl_calculateMaxAndMinElevation(isImperial,true)

                RLValueName.Completed -> if (isImperial) fireBaseDetailCardData?.speedForOneMile?.size.toString() else fireBaseDetailCardData?.speedForOneKm?.size.toString()
                RLValueName.AvaragePace -> rl_calculateAvaragePace(isImperial)
                RLValueName.Slowtest -> rl_calculateSlowtestAndFasttestPace(isImperial,false)
                RLValueName.Fasttest -> rl_calculateSlowtestAndFasttestPace(isImperial,true)

                else -> "0"
            }
        }
        else{
            return when (title) {
                RLValueName.TotalTime -> RLTools.rl_formatTime(cardData.totalTime.toInt(),true)
                RLValueName.Effort  -> RLTools.rl_formatCommasInt(cardData.totalREV.roundToInt())
                RLValueName.AssumedEffort  -> RLTools.rl_formatCommasInt(cardData.totalREV.roundToInt())
                else -> "0"
            }
            return "0"
        }

    }

    private fun rl_calculateAvaragePace(isImperial: Boolean):String{
        if (isImperial){
           val time =  fireBaseDetailCardData?.speedForOneMile?.average()?:0.0
           return RLTools.rl_formatTimeNoMS(time.toInt(),true)
        } else {
            val time =  fireBaseDetailCardData?.speedForOneKm?.average()?:0.0
            return   RLTools.rl_formatTimeNoMS(time.toInt(),true)
        }

    }

    private fun rl_calculateMaxAndMinElevation(isImperial: Boolean, isMax:Boolean):String{
        if (isImperial){
            if (isMax){
                val elevation =  (fireBaseDetailCardData?.arrElevation?.max()?:0.0) * 3.28084
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.rl_formatCommasInt(elevation.toInt())
                }
            }else{
                val elevation =  (fireBaseDetailCardData?.arrElevation?.min()?:0.0) * 3.28084
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.rl_formatCommasInt(elevation.toInt())
                }
            }

        }else{
            if (isMax){
                val elevation =  (fireBaseDetailCardData?.arrElevation?.max()?:0.0)
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.rl_formatCommasInt(elevation.toInt())
                }
            }else{
                val elevation =  (fireBaseDetailCardData?.arrElevation?.min()?:0.0)
                return if (elevation.isNaN() || elevation.isInfinite()) {
                    "0"
                }else{
                    RLTools.rl_formatCommasInt(elevation.toInt())
                }
            }
        }
    }

    private fun rl_calculateSlowtestAndFasttestPace(isImperial: Boolean, isFastest:Boolean):String{
        if (isImperial){
            if (isFastest){
                val time =  fireBaseDetailCardData?.speedForOneMile?.min()?:0.0
                return RLTools.rl_formatTimeNoMS(time.toInt(),true)
            }else{
                val time =  fireBaseDetailCardData?.speedForOneMile?.max()?:0.0
                return RLTools.rl_formatTimeNoMS(time.toInt(),true)
            }

        } else {
            if (isFastest){
                val time =  fireBaseDetailCardData?.speedForOneKm?.min()?:0.0
                return RLTools.rl_formatTimeNoMS(time.toInt(),true)
            }else{
                val time =  fireBaseDetailCardData?.speedForOneKm?.max()?:0.0
                return RLTools.rl_formatTimeNoMS(time.toInt(),true)
            }
        }
    }


    private  fun rl_calculateMaxSpeed(isImperial: Boolean): String {
        val maxSpeed:Double = fireBaseDetailCardData?.arrSpeed?.takeIf { it.isNotEmpty() }?.maxOrNull() ?: 0.0

        return if (maxSpeed.isNaN() || maxSpeed.isInfinite()) {
            "0"
        } else if (isImperial) {
            String.format("%.2f", maxSpeed / 1.609) // Convert to miles per hour if imperial
        }else{
            String.format("%.2f", maxSpeed)  // Return speed in km/h
        }
    }

    private  fun rl_calculateAvgSpeed(isImperial: Boolean): String {
        val totalTime = fireBaseCardData?.totalTime?:0
        val  distance = fireBaseCardData?.distance?:0.0

        val tempTime = totalTime / 3600 // Convert time to hours
        val tempAvgSpeed = distance / tempTime // Speed in km/h
        RLTools.rl_logEPrint(TAG,"AVGSPD:- $tempAvgSpeed")
        // Ensure valid output
        return if (tempAvgSpeed.isNaN() || tempAvgSpeed.isInfinite()) {
           // "0"
            val speedavg= fireBaseCardData?.avgSpeed?:0.0
            String.format("%.2f", speedavg / 1.609) // Convert to miles per hour if imperial
        } else if (isImperial) {
            String.format("%.2f", tempAvgSpeed / 1.609) // Convert to miles per hour if imperial
        }else{
            String.format("%.2f", tempAvgSpeed)
        }
    }

    //Click Wise Ui SetUp
    private fun rl_clickToSetUI() {
        fragBinding.inlayTitle.layoutSummary.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            rl_summaryUiSet()
        }
        fragBinding.inlayTitle.layoutAnalysis.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            rl_analysisDataSet()
        }
        fragBinding.inlayTitle.layoutEffort.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppMainColor)
            rl_effortDataSet()
        }
    }

    //Analysis Ui SetUp
    private fun rl_analysisDataSet(){
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
                    rl_analysisSpeedUISetup()
                }
                val isPaceGraph = fireBaseDetailCardData?.speedForOneKm?.any { convertToInt(it) > 0 } == true
                if (isPaceGraph){
                    fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                    fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                    rl_analysisPaceUISetup()
                }
                if (!fireBaseDetailCardData?.arrElevation.isNullOrEmpty()){
                    val elevation = if (fireBaseCardData?.demsElevation == 0.0 && fireBaseCardData?.totalElevation != 0.0) {
                        fireBaseCardData?.totalElevation
                    } else {
                        fireBaseCardData?.demsElevation
                    } ?: 0.0

                    if (convertToInt(elevation) > 5 || elevation == -1.0) {
                        fragBinding.includeElevation.relativeCard.visibility=View.VISIBLE
                        rl_analysisElevationUISetup()
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
                        rl_analysisElevationUISetup()
                    }
                }
            }

            val isEffortGraph = fireBaseDetailCardData?.arrRevPercentage?.any { convertToInt(it) > 0 } == true
            if (isEffortGraph){
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                rl_analysisEffortUISetup()
            }

            if( classType.toLowerCase() in listOf("ride", "run", "walk")){
                val isSpeedGraph = fireBaseDetailCardData?.arrSpeed?.any { convertToInt(it) > 0 } == true
                if (isSpeedGraph){
                    fragBinding.includeSpeed.relativeCard.visibility=View.VISIBLE
                    rl_analysisSpeedUISetup()
                }
                val isPaceGraph = fireBaseDetailCardData?.speedForOneKm?.any { convertToInt(it) > 0 } == true
                if (isPaceGraph){
                    fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                    rl_analysisPaceUISetup()
                }
            }


        }
    }
    //Effort Chart
    private fun rl_analysisEffortUISetup(){
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData(rl_getValueForTitle(RLValueName.Effort)),
            RLTypeOfMetrics.EffortZone to RLMetricData("CALM"),
            RLTypeOfMetrics.AvgEffort to RLMetricData(rl_getValueForTitle(RLValueName.AvgEffort)),
            RLTypeOfMetrics.MaxEffort to RLMetricData(rl_getValueForTitle(RLValueName.MaxEffort)),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.AvgHeartRate)),
            RLTypeOfMetrics.MaxHeartRate to RLMetricData(rl_getValueForTitle(RLValueName.MaxHeartRate))
        )

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

        rl_injectDataIntoWebView(RLConstants.EFFORT)

    }
    //Pace Chart
    private fun rl_analysisPaceUISetup(){
        val isImperial = RLTools.rl_getIsImperial(userCardData?.appUnit?:"Metric")
        val completed = if (isImperial) RLTypeOfMetrics.completed else  RLTypeOfMetrics.completedKm

        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            completed to RLMetricData(rl_getValueForTitle(RLValueName.Completed)),
            RLTypeOfMetrics.Averagepace to RLMetricData(rl_getValueForTitle(RLValueName.AvaragePace)),
            RLTypeOfMetrics.Slowtest to RLMetricData(rl_getValueForTitle(RLValueName.Slowtest)),
            RLTypeOfMetrics.Fasttest to RLMetricData(rl_getValueForTitle(RLValueName.Fasttest))
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

        rl_injectDataIntoWebView(RLConstants.PACE)
    }
    //Speed Chart
    private fun rl_analysisSpeedUISetup(){
        val isImperial = RLTools.rl_getIsImperial(userCardData?.appUnit?:"Metric")
        val Averagespeed = if (isImperial) RLTypeOfMetrics.Averagespeed else  RLTypeOfMetrics.AveragespeedKm
        val MaxSpeed = if (isImperial) RLTypeOfMetrics.MaxSpeed else  RLTypeOfMetrics.MaxSpeedKM

        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Averagespeed to RLMetricData(rl_getValueForTitle(RLValueName.AvgSpeed)),
            MaxSpeed to RLMetricData(rl_getValueForTitle(RLValueName.MaxSpeed))
        )

        fragBinding.includeSpeed.imgTitleAnalysis.setImageResource(R.drawable.ic_speeed)
        fragBinding.includeSpeed.txtTitleAnalysis.setText("Speed")

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

        rl_injectDataIntoWebView(RLConstants.SPEED)
    }
    //Elevation Chart
    private fun rl_analysisElevationUISetup(){
        val isImperial = RLTools.rl_getIsImperial(userCardData?.appUnit?:"Metric")
        val Totalclimbed = if (isImperial) RLTypeOfMetrics.Totalelevation else  RLTypeOfMetrics.TotalelevationM
        val Minelevation = if (isImperial) RLTypeOfMetrics.Minelevation else  RLTypeOfMetrics.MinelevationM
        val Maxelevation = if (isImperial) RLTypeOfMetrics.Maxelevation else  RLTypeOfMetrics.MaxelevationM

        val  dataList: List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Totalclimbed to RLMetricData(rl_getValueForTitle(RLValueName.Climbed)),
            Minelevation to RLMetricData(rl_getValueForTitle(RLValueName.MinElevation)),
            Maxelevation to RLMetricData(rl_getValueForTitle(RLValueName.MaxElevation))
        )

        fragBinding.includeElevation.imgTitleAnalysis.setImageResource(R.drawable.ic_climb)
        fragBinding.includeElevation.txtTitleAnalysis.setText("Elevation")

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

        rl_injectDataIntoWebView(RLConstants.ELEVATION)

    }
    //Insert Data All Chart
    private fun rl_injectDataIntoWebView(mapType:String) {
        val arrCumDistance = fireBaseDetailCardData?.arrCumDistance!!
        val arrElevation = fireBaseDetailCardData?.arrElevation!!
        val arrSpeed = fireBaseDetailCardData?.arrSpeed!!
        var  speedForPace = fireBaseDetailCardData?.speedForOneKm!!

        val isImperial = RLTools.rl_getIsImperial(userCardData!!.appUnit)
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


            fragBinding.includeEffort.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.rl_getEffortChartHtml(jsonArray.toString()), "text/html", "UTF-8", null)
        }else if(mapType.equals(RLConstants.ELEVATION)) {
            val cumDistance = arrCumDistance
            val elevation =  arrElevation
            var metersLabel = "METERS"  // Dynamic Speed Label
            var  kmsLabel = "KM's"  // Dynamic Distance Label
            if (isImperial){
                metersLabel="FEET"
                kmsLabel="Miles's"
            }

            fragBinding.includeElevation.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.rl_getElevationHtml( cumDistance.toString(),elevation.toString(),metersLabel,kmsLabel), "text/html", "UTF-8", null)
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

            fragBinding.includeSpeed.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.rl_getSpeedHtml( cumDistance.toString(),elevation.toString(),speed.toString(),kmhLabel,kmsLabel), "text/html", "UTF-8", null)
        }else if(mapType.equals(RLConstants.PACE)) {
            val timeData =speedForPace



            fragBinding.includePace.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.rl_getPaceChartHtml( timeData.toString()), "text/html", "UTF-8", null)

        }
    }

    //Effort Ui SetUp
    private fun rl_effortDataSet(){
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

        val ZoneTextData= RLTools.rl_verifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)

        fragBinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        fragBinding.inlayChart.layEffortZone.txtNumber.setText(ZoneTextData.efforZoneText)
        fragBinding.inlayChart.layEffortZone.txtNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        fragBinding.inlayChart.layAll.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        fragBinding.inlayChart.relayChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        fragBinding.inlayChart.webViewChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        val efforZoneBgrClr =  ZoneTextData.efforZoneBgrClr

        val effort =rl_getValueForTitle(RLValueName.AvgEffort)
        val effortScore:String =rl_getValueForTitle(RLValueName.Effort)
        val maxEffort = rl_getValueForTitle(RLValueName.MaxEffort)


        fragBinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        fragBinding.inlayChart.layEffort.txtNumber.setText(effort)

        fragBinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        fragBinding.inlayChart.layEffortScore.txtNumber.setText(effortScore)

        fragBinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        fragBinding.inlayChart.layMaxEffort.txtNumber.setText(maxEffort)

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
            RLAllHTMLChart.rl_getNewZoneChartHtml(zoneDataList,efforZoneBgrClr), "text/html", "UTF-8", null)

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

    override fun onPause() {
        super.onPause()
        rl_bottomHideShowSet(true)
    }
    private fun rl_closeScreen(isSessionComplete:Boolean){
        if (isSessionComplete){
            rl_bottomHideShowSet(true)
            (context as RLMainActivityRL).rl_bottombarcolorDarkBlue()
            (context as RLMainActivityRL).rl_loadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }else{
            rl_closeFragment()
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

    override fun onImageClick(position: Int, imageUrl: String) {
        // Example: open full-screen image
        if (position == 0 && (cardData.map_image.isNotEmpty() || !cardData.map_url.isNullOrEmpty())) {
            openFullGoogleMap(position)
        }

    }

    private fun openFullGoogleMap(position:Int){
        val bundle: Bundle = Bundle()
        bundle.putString("jsonDataString",mapObject)
        (context as RLMainActivityRL).rl_loadFrag(RLFragMapView().newInstance(bundle), TAG, true,null, true)
    }

}
