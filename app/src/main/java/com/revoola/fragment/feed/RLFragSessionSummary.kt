package com.revoola.fragment.feed

import android.os.Bundle
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
import com.google.android.gms.wearable.Wearable
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
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLSessionSummaryDataModel
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLWatchModel
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.revoola.watch.RLWearDataSync
import org.json.JSONArray
import org.json.JSONObject
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
    private fun RLuisetup() {
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
       // RLonBackPresAct(fragBinding.ivBack)
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLcloseScreen(isSessionComplete)
        }
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        RLfetchFirebaseData()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                RLcloseScreen(isSessionComplete)
            }
        })

        fragBinding.inlayTop.recyclerTitle.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(cardData.className.toString())
        fragBinding.inlayTop.ivDescription.setText(RLTools.RLconvertTimestampToDAte(cardData.timestamp.toLong()))
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }

        RLsummaryDataSet()
        RLClickToSetUI()
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
    private fun RLsummaryDataSet() {
        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE
        //var imagelink=RLTools.RLgetImage(classType)
        val imagelink= RLTools.RLFeedSetImage(cardData,currentUser,selectTag)

        /* if (!cardData.imageLinkSmall.isNullOrEmpty()){
             imagelink=cardData.imageLinkSmall
         }else if (!cardData.map_image.isNullOrEmpty()){
             imagelink=cardData.map_image
         }else{
             imagelink=RLTools.RLgetImage(classType)
         }*/
        Glide.with(requireContext()).load(imagelink).into(fragBinding.testImage)

        fragBinding.testImage.visibility=View.GONE
        fragBinding.viewPagerImage.visibility=View.VISIBLE
        fragBinding.intoTabLayout.visibility=View.VISIBLE
        fragBinding.intoTabLayout.setupWithViewPager(fragBinding.viewPagerImage)
        val imageListOriginal = listOf(imagelink, "CHART", RLTools.RLgetImage(classType))

        var imageList:MutableList<String> = mutableListOf()
        if (cardData.user_images.isEmpty()){
            imageList=imageListOriginal.toMutableList()
        }else{

            imageList = cardData.user_images.extractImageUrls().toMutableList()
            imageList.add("CHART")
            imageList.add(RLTools.RLGetLinkImage(cardData.classType?.toLowerCase().toString()))
        }

        //imageList = listOf(imagelink, "CHART", RLTools.RLFeedSetImage(cardData,currentUser,selectTag))

        RLTools.RLheightsetViewPager(fragBinding.viewPagerImage)
        val viewPagerAdapter = RLImagePagerAdapter(activity,imageList)
        fragBinding.viewPagerImage.adapter = viewPagerAdapter


        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze

        //Main Data List Set
        val rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )
        val danceHiitListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLdaytimeget(cardData.elevation.toInt())),
            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )
        val yogaListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLdaytimeget(cardData.elevation.toInt())),
            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )
        val pilatesListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )


        val rideListWithHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )
        val walkRunListWithHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )
        val workoutYogaPilatesListWithHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Effort to RLMetricData(cardData.totalREV.roundToInt().toString()),
            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )

        if (classType.toLowerCase().equals("ride")){
            RLgetWayName(RLYourWayName.Ride)
        }else if (classType.toLowerCase().equals("run")){
            RLgetWayName(RLYourWayName.Run)
        }else if (classType.toLowerCase().equals("walk")){
            RLgetWayName(RLYourWayName.Walk)
        }else if (classType.toLowerCase().equals("pilates")){
            RLgetWayName(RLYourWayName.Pilates)
        }else if (classType.toLowerCase().equals("warm")){
            RLgetWayName(RLYourWayName.Warm)
        }else if (classType.toLowerCase().equals("workout")){
            RLgetWayName(RLYourWayName.Workout)
        }else if (classType.toLowerCase().equals("dance")){
            RLgetWayName(RLYourWayName.Dance)
        }else if (classType.toLowerCase().equals("hiit")){
            RLgetWayName(RLYourWayName.Hiit)
        }else if (classType.toLowerCase().equals("yoga")){
            RLgetWayName(RLYourWayName.Yoga)
        }else{
            RLgetWayName(RLYourWayName.Yoga)
        }

        /*if (cardData.hrm==0){
            //WITHOUT HR
            if( classType.toLowerCase().equals("dance")||classType.toLowerCase().equals("hiit")||classType.toLowerCase().equals("run")||classType.toLowerCase().equals("walk")){
                RLSummryListSet(danceHiitListWithoutHr)
            }else if (classType.toLowerCase().equals("yoga")){
                RLSummryListSet(yogaListWithoutHr)
            }else if (classType.toLowerCase().equals("pilates")){
                RLSummryListSet(pilatesListWithoutHr)
            }else if (classType.toLowerCase().equals("ride")){
                RLSummryListSet(rideListWithoutHr)
            }else{
                RLSummryListSet(rideListWithoutHr)
            }

        }else{
            //WITH HR
            if( classType.toLowerCase().equals("walk")|| classType.toLowerCase().equals("run")){
                RLSummryListSet(walkRunListWithHr)
            }else if( classType.toLowerCase().equals("ride")){
                RLSummryListSet(rideListWithHr)
            } else if( classType.toLowerCase().equals("workout")||classType.toLowerCase().equals("yoga")||classType.toLowerCase().equals("pilates")){
                RLSummryListSet(workoutYogaPilatesListWithHr)
            }else{
                RLSummryListSet(rideListWithHr)
            }
        }*/

    }
    private fun RLClickToSetUI() {
        fragBinding.inlayTitle.layoutSummary.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            RLsummaryDataSet()
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
    private fun RLeffortDataSet(){
        var dataList: List<String> = listOf("Calm","Warm","Cardio","Fat Burn","Endurance","Power","Peak")
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.VISIBLE
       

        fragBinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        fragBinding.inlayChart.layEffortZone.txtNumber.setText(R.string.cardio)
        fragBinding.inlayChart.layEffortZone.txtNumber.setTextColor(resources.getColor(R.color.AppMainColor))

        fragBinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        fragBinding.inlayChart.layEffort.txtNumber.setText("57%")

        fragBinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        fragBinding.inlayChart.layEffortScore.txtNumber.setText("468")

        fragBinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        fragBinding.inlayChart.layMaxEffort.txtNumber.setText("84%")

        RLTools.RLheightsetdisplaywebview(fragBinding.inlayChart.webViewChart,activity)
        fragBinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        fragBinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.RLGetNewZoneChartHtml(), "text/html", "UTF-8", null)


        //Main list set
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleEffort.layoutManager = linearLayoutManager
        val adapterdata = RLFeedSessionEffortListAdapter(activity, dataList)
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
        }else{
            //With HR Sensor
            if( classType.toLowerCase().equals("run")){
                fragBinding.includeElevation.relativeCard.visibility=View.VISIBLE
                fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                fragBinding.includeSpeed.relativeCard.visibility=View.VISIBLE
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                RLanalysisEffortUISetup()
                RLanalysisPaceUISetup()
                RLanalysisSpeedUISetup()
                RLanalysisElevationUISetup()
            }else if(classType.toLowerCase().equals("walk")|| classType.toLowerCase().equals("yoga")||classType.toLowerCase().equals("pilates")||classType.toLowerCase().equals("workout")||classType.toLowerCase().equals("ride")){
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                fragBinding.includeElevation.relativeCard.visibility=View.GONE
                fragBinding.includePace.relativeCard.visibility=View.GONE
                fragBinding.includeSpeed.relativeCard.visibility=View.GONE
                fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                RLanalysisEffortUISetup()
            }
        }
    }
    private fun RLanalysisEffortUISetup(){
        RLTools.RlLogDPrint(TAG,"NU")
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData(cardData.totalREV.roundToInt().toString()),
            RLTypeOfMetrics.EffortZone to RLMetricData("CALM"),
            RLTypeOfMetrics.AvgEffort to RLMetricData(cardData.elevation.toString()+"%"),
            RLTypeOfMetrics.MaxEffort to RLMetricData(cardData.maxRevPercentage.roundToInt().toString()+"%"),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.hr.toString()),
            RLTypeOfMetrics.MaxHeartRate to RLMetricData(cardData.hr.toString())
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

        fragBinding.includeEffort.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.EFFORT)
        }
    }
    private fun RLanalysisPaceUISetup(){
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.completed to RLMetricData("0"),
            RLTypeOfMetrics.Averagepace to RLMetricData("0"),
            RLTypeOfMetrics.Slowtest to RLMetricData("0"),
            RLTypeOfMetrics.Fasttest to RLMetricData("0")
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

        fragBinding.includePace.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.PACE)
        }
    }
    private fun RLanalysisSpeedUISetup(){
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.Averagespeed to RLMetricData(cardData.average_speed.toString()),
            RLTypeOfMetrics.MaxSpeed to RLMetricData("0")
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

        fragBinding.includeSpeed.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.SPEED)
        }
    }
    private fun RLanalysisElevationUISetup(){
        var dataList: List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.Totalclimbed to RLMetricData(cardData.elevation.toString()),
            RLTypeOfMetrics.Minelevation to RLMetricData("0"),
            RLTypeOfMetrics.Maxelevation to RLMetricData("0")
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

        fragBinding.includeElevation.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.ELEVATION)
        }


    }
    private fun RLgetWayName(wayname: RLYourWayName) {
        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        var isHrConnected:Boolean
        var isClass:Boolean
        if (cardData.hrm==0) {
            //WITHOUT HR
            isHrConnected=false
        }else{
            isHrConnected=true
        }
        if (cardData.bmo==0){
            isClass=true
        }else{
            isClass=false
        }

       val  maxCadence=2
        var rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = mutableListOf()
         when (wayname) {
            RLYourWayName.Ride -> {
                if(isHrConnected){
                    if(isClass){
                        // cells = [ .Time, .Effort, .HR, .Cadence, .ActiveCalories, .Kudos, .Comments ,.Awards]
                       //done
                        rideListWithoutHr=  listOf(
                           // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                            RLTypeOfMetrics.Cadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                        )
                    }
                    else{
                       // cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                    // .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                        )
                    }
                }
                else if(maxCadence > 0){
                    if(isClass){
                      //  cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Cadence, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                           // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                        )
                    }
                    else{
                      //  cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        //  .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                       //done
                        rideListWithoutHr=  listOf(
                           // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                        )
                    }
                }
                else{
                    if(isClass){
                       // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                        )
                    }
                    else{
                       // cells = [ .Time, .EstimatedEffort, .Distance, .EstimatedCalories,
                        // .AvgMaxSpeed, .Elevation, .Speed, .Kudos, .Comments ,.Awards]
                      //done
                        rideListWithoutHr= listOf(
                            //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                    }
                }
            }
             RLYourWayName.Run -> {
             if(isHrConnected){
                // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed,
                 // .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                 //done
                 rideListWithoutHr=   listOf(
                    // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                     RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                     RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                     RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                     RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                     RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
             else{
                /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
             // .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                 //done
                 rideListWithoutHr=   listOf(
                     //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                     RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                     RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                     RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
            }
             RLYourWayName.Walk -> {
             if(isHrConnected){
                 // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                 //done
                 rideListWithoutHr=   listOf(
                     //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                     RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                     RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                     RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                     RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
             else{
                 /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                 //done
                 rideListWithoutHr=   listOf(
                     //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                     RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.elevation.toDouble())),
                     RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                     RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.average_speed.toDouble())),
                     RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
            }
             RLYourWayName.Pilates -> {
             if(isHrConnected){
                // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                 //done
                 rideListWithoutHr= listOf(
                     //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
             else{
                // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
               //done
                 rideListWithoutHr=listOf(
                     //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                     RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                     RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                     RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                     RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                     RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                     RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                 )
             }
            }
             RLYourWayName.Warm -> {
                 if(isHrConnected){
                     // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr= listOf(
                        // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
                 else{
                     // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr=listOf(
                        // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
             }
             RLYourWayName.Workout -> {
                 if(isHrConnected){
                     // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr= listOf(
                         //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
                 else{
                     // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr=listOf(
                        // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
             }
             RLYourWayName.Dance -> {
                 if(isHrConnected){
                     // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr= listOf(
                         //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
                 else{
                     // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr=listOf(
                         //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
             }
             RLYourWayName.Hiit -> {
                 if(isHrConnected){
                     // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr= listOf(
                         //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
                 else{
                     // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr=listOf(
                        // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
             }
             RLYourWayName.Yoga -> {
                 if(isHrConnected){
                     // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr= listOf(
                        // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
                 else{
                     // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                     //done
                     rideListWithoutHr=listOf(
                         //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                         RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                         RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                         RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
                         RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                         RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                         RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
                     )
                 }
             }
        }

        RLSummryListSet(rideListWithoutHr)
    }

    private fun String.extractImageUrls(): List<String> {
        return this.replace("\\", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    private fun RLSummryListSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }
    private fun RLheightsetdisplaywebview(webView: WebView) {
       RLTools.RLheightsetdisplaywebview(webView,activity)
    }
    private fun RLinjectDataIntoWebView(maptype:String) {
        // Inject JSON data into WebView's JavaScript context
        if (maptype.equals(RLConstants.EFFORT)) {
            val jsonArray: JSONArray = JSONArray()
            val time = arrayListOf(0, 7, 11, 16, 21, 26, 31, 36, 46, 61)
            val effort = arrayListOf(15.5, 20.7, 22.5, 23.3, 27.6, 28.5, 31.1, 32.8, 43.2, 53.5)
            for (i in 0 until 10) {
                val jsonObject: JSONObject = JSONObject()
                jsonObject.put("time", time[i])
                jsonObject.put("effort", effort[i])
                jsonArray.put(jsonObject)
            }
          fragBinding.includeEffort.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetEffortChartHtml(jsonArray.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.ELEVATION)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            fragBinding.includeElevation.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetElevationHtml( cumDistance.toString(),elevation.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.SPEED)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            val speed = listOf(1.1917761284398396, 13.271195141645485, 16.3731288772218, 25.100697267202097, 30.414623993580445, 23.07914084016237, 19.85637565717453, 22.02540041430026, 21.509108339338717, 22.04665020256577)
            fragBinding.includeSpeed.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetSpeedHtml( cumDistance.toString(),elevation.toString(),speed.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.PACE)) {
            val timeData = listOf(225, 165, 135, 132, 124, 131)
            fragBinding.includePace.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetPaceChartHtml( timeData.toString()), "text/html", "UTF-8", null)

        }
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }

    private fun RLfetchFirebaseData() {
        //User Data Fetch to Firebase
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userCardData =userData
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        // Firebase to fetch Session Summary data
        val path = RevoolaFirebasePath.sessionSummaryDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseCardData = gson.fromJson(jsonObject, RLSessionSummaryDataModel::class.java)
            } else {
               RLTools.RlLogEPrint(TAG,"Session Summary Empty Data")
            }
        }
    }

    fun getValueForTitle(title: String): String {
        if (userCardData!=null && fireBaseCardData != null){
            val isImperial = RLTools.RLGetIsImperial(userCardData!!.appUnit)
            return when (title) {
                "Total Time" -> RLTools.RLformatTime(fireBaseCardData!!.totalTime.toInt(),true)
                "Effort" -> fireBaseCardData!!.totalRev.toString()
                "Avg Heart Rate" -> if (!checkShowHeartRate()) fireBaseCardData!!.avgHr.toString() else "--"
                "Cadence" -> fireBaseCardData!!.avgCadence.toString()
                "Active Calories" -> (fireBaseCardData!!.totalPower ?: 0).toString()
                "Boosts" -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
                "Comments" -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
                "Awards" -> {
                    val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                    if (totalAwards != 0) totalAwards.toString() else "0"
                }
                "Steps" -> fireBaseCardData!!.totalSteps.toString()
                "Distance" -> if (!isImperial) fireBaseCardData!!.distance.toString() else (fireBaseCardData?.distance?:0 * 0.621371).toString()
                "Climbed" -> {
                    val elevation = if (!isImperial) {
                        if (fireBaseCardData!!.demsElevation == -1) "Pending" else (fireBaseCardData?.demsElevation?:-1).toString()
                    } else {
                        if (fireBaseCardData!!.demsElevation == -1) "Pending" else (fireBaseCardData?.demsElevation?:-1 * 3.28084).toString()
                    }
                    elevation.toString()
                }
                "Avg Pace" -> if (!isImperial) RLTools.RLformatTime((fireBaseCardData?.avgSpeedForOneKm?:0.0).toInt(),true) else  RLTools.RLformatTime((fireBaseCardData?.avgSpeedForOneMile?:0.0).toInt(),true)
                "Avg Speed" -> if (!isImperial) calculateAvgSpeed((fireBaseCardData?.totalTime?:0).toDouble(), fireBaseCardData?.distance?:0.0).toString() else (calculateAvgSpeed((fireBaseCardData?.totalTime?:0).toDouble(), fireBaseCardData?.distance?:0.0) * 0.62137119223733).toString()
                "Assumed Effort" -> fireBaseCardData!!.totalRev.toString()
                "Assumed Calories" -> (fireBaseCardData!!.burntCalories ?: 0).toString()
                else -> "0"
            }
        }else{
            return "0"
        }
    }

    private fun checkShowHeartRate(): Boolean {
        if (!userCardData?.currentGroup.isNullOrEmpty() && userCardData?.currentGroup!!.contains("schooltype1")) {
            return false
        }
        return true
    }


    // Helper function to calculate average speed (assuming it exists)
    private fun calculateAvgSpeed(totalTime: Double, distance: Double): Double {
        // Add your calculation logic here
        return distance / totalTime
    }
}