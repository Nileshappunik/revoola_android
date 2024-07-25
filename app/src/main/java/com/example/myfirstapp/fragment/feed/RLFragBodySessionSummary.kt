package com.example.myfirstapp.fragment.feed

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragSessionSummaryBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfMetrics
import com.example.myfirstapp.enumclass.RLYourWayName
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionEffortListAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLImagePagerAdapter
import com.example.myfirstapp.model.RLTextOverview
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject

class RLFragBodySessionSummary : RLBaseFragment() {
    val TAG: String = RLFragBodySessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var classType=""

    private val binding by lazy {
        RlFragSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodySessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_summary, container) as RlFragSessionSummaryBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragBodySessionSummary" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
       // RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivBack.setOnClickListener {
            RLcloseFragment()
        }
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        fragBinding.ivTitle.setText(cardData.className.toString())
        fragBinding.ivTitleDate.setText(RLTools.RLconvertTimestampToDAte(cardData.timestamp.toLong()))
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLsummaryDataSet()
        RLClickToSetUI()
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
            RLTools.RLGetNewZoneChartHtml(), "text/html", "UTF-8", null)


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
            fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
            fragBinding.includeElevation.relativeCard.visibility=View.GONE
            fragBinding.includePace.relativeCard.visibility=View.GONE
            fragBinding.includeSpeed.relativeCard.visibility=View.GONE
            fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
            RLanalysisEffortUISetup()
        }
    }
    private fun RLanalysisEffortUISetup(){
        Log.d(TAG,"NU")
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData("0"),
            RLTypeOfMetrics.EffortZone to RLMetricData("CALM"),
            RLTypeOfMetrics.AvgEffort to RLMetricData(cardData.elevation.toString()),
            RLTypeOfMetrics.MaxEffort to RLMetricData("0"),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.hr.toString()),
            RLTypeOfMetrics.MaxHeartRate to RLMetricData("0"))

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
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList)
        fragBinding.includeEffort.recycleAnalysis.adapter = adapterdata

        fragBinding.includeEffort.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.EFFORT)
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
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                    }
                    else{
                        // cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        // .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                          //  RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
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
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
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
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
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
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                    }
                    else{
                        // cells = [ .Time, .EstimatedEffort, .Distance, .EstimatedCalories,
                        // .AvgMaxSpeed, .Elevation, .Speed, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr= listOf(
                           // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                            RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
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
                        RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                    // .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                        RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
            RLYourWayName.Walk -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                        RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
                        RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.Climbed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgPace to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AvgSpeed to RLMetricData(RLTools.RLformatCommas(cardData.distance.toDouble())),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
            RLYourWayName.Pilates -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                      //  RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
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
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
            RLYourWayName.Workout -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
            RLYourWayName.Dance -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
            RLYourWayName.Hiit -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
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
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                       // RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
                        RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
                        RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
                        RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
                }
            }
        }

        RLSummryListSet(rideListWithoutHr)
    }
    private fun RLsummaryDataSet() {
        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE
        var imagelink=RLTools.RLgetImage(classType)
        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            imagelink=cardData.imageLinkSmall
        }else if (!cardData.map_image.isNullOrEmpty()){
            imagelink=cardData.map_image
        }else{
            imagelink=RLTools.RLgetImage(classType)
        }
        Glide.with(requireContext()).load(imagelink).into(fragBinding.testImage)

        fragBinding.testImage.visibility=View.GONE
        fragBinding.viewPagerImage.visibility=View.VISIBLE
        fragBinding.intoTabLayout.visibility=View.VISIBLE
        fragBinding.intoTabLayout.setupWithViewPager(fragBinding.viewPagerImage)
        val imageList = listOf(imagelink, "CHART", RLTools.RLgetImage(classType))

        RLTools.RLheightsetViewPager( fragBinding.viewPagerImage)
        val viewPagerAdapter = RLImagePagerAdapter(activity, imageList)
        fragBinding.viewPagerImage.adapter = viewPagerAdapter

        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        val rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.AvgCadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()))
        val danceHiitYogaPilatesListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))


        val danceHiitYogaPilatesWithHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))
        val rideListWithHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(cardData.avgHr.toString()),
            RLTypeOfMetrics.Cadence to RLMetricData(RLTools.RLformatCommas(cardData.steps.toDouble())),
            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))

       /* if (cardData.hrm==0){
            //WITHOUT HR
            if( classType.toLowerCase().equals("dance")||classType.toLowerCase().equals("hiit")||classType.toLowerCase().equals("yoga")||classType.toLowerCase().equals("pilates")){
                RLSummryListSet(danceHiitYogaPilatesListWithoutHr)
            }else if( classType.toLowerCase().equals("ride")){
                RLSummryListSet(rideListWithoutHr)
            }else{
                RLSummryListSet(rideListWithoutHr)
            }
        }else {
            //WITH HR
            if( classType.toLowerCase().equals("dance")||classType.toLowerCase().equals("hiit")||classType.toLowerCase().equals("yoga")||classType.toLowerCase().equals("pilates")){
                RLSummryListSet(danceHiitYogaPilatesWithHr)
            }else if( classType.toLowerCase().equals("ride")){
                RLSummryListSet(rideListWithHr)
            }else{
                RLSummryListSet(rideListWithHr)
            }
        }*/

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

    }
    private fun RLSummryListSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity,dataList)
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
          fragBinding.includeEffort.webViewAnalysis.loadDataWithBaseURL(null, RLTools.RLgetEffortChartHtml(jsonArray.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.ELEVATION)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            fragBinding.includeElevation.webViewAnalysis.loadDataWithBaseURL(null, RLTools.RLgetElevationHtml( cumDistance.toString(),elevation.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.SPEED)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            val speed = listOf(1.1917761284398396, 13.271195141645485, 16.3731288772218, 25.100697267202097, 30.414623993580445, 23.07914084016237, 19.85637565717453, 22.02540041430026, 21.509108339338717, 22.04665020256577)
            fragBinding.includeSpeed.webViewAnalysis.loadDataWithBaseURL(null, RLTools.RLgetSpeedHtml( cumDistance.toString(),elevation.toString(),speed.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.PACE)) {
            val timeData = listOf(225, 165, 135, 132, 124, 131)
            fragBinding.includePace.webViewAnalysis.loadDataWithBaseURL(null, RLTools.RLgetPaceChartHtml( timeData.toString()), "text/html", "UTF-8", null)

        }
    }



    /*val imageList = listOf(
          "https://farm4.staticflickr.com/3224/3081748027_0ee3d59fea_z_d.jpg",
          "https://via.placeholder.com/300/09f/fff.png",
          "https://via.placeholder.com/150/0000FF/808080 ?RLText=PAKAINFO.com")

      RLTools.heightsetViewPager( fragBinding.viewPagerImage)
      val viewPagerAdapter = RLImagePagerAdapter(activity, imageList)
      fragBinding.viewPagerImage.adapter = viewPagerAdapter*/
}