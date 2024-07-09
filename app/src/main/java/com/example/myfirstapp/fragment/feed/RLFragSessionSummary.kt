package com.example.myfirstapp.fragment.feed

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragSessionSummaryBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfMetrics
import com.example.myfirstapp.fragment.feed.adapter.RLFeedGroupNameAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionEffortListAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSimpleAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLYourFriendYouListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.model.RLGroupCardModel
import com.example.myfirstapp.model.RLSetGroupData
import com.example.myfirstapp.model.RLSetGroupRequest
import com.example.myfirstapp.model.RLTextOverview
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject

class RLFragSessionSummary : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragSessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    val valueslist = arrayOf("SUMMARY", "ANALYSIS","EFFORT","RANKING")

    private val binding by lazy {
        RlFragSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_summary, container) as RlFragSessionSummaryBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionSummary" )
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
        RLonBackPresAct(fragBinding.ivBack)
        //Title list set
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.recycleSessionTitle.layoutManager = linearLayoutManager
        val adaptertitle = RLOverviewSessionTitleListAdapter("SUMMARY",this,valueslist,activity)
        fragBinding.recycleSessionTitle.adapter = adaptertitle
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        fragBinding.ivTitle.setText(cardData.className.toString())
        fragBinding.ivTitleDate.setText(RLTools.RLconvertTimestampToDateTime(cardData.timestamp.toLong()))
        RLsummaryDataSet()
    }
    private fun RLrankingDataSet(){
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE
        fragBinding.relayRanking.visibility=View.VISIBLE
        fragBinding.txtAlltime.setOnClickListener {
            RLallTimeDialogOpen()
        }
        fragBinding.txtWalk.setOnClickListener {
             RLwalkDialogOpen()
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleRanking.layoutManager = linearLayoutManager
        val adapter = RLYourFriendYouListAdapter(activity,false)
        fragBinding.recycleRanking.adapter = adapter

        fragBinding.txtYou.setOnClickListener {
            fragBinding.txtYou.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.viewYou.setBackgroundResource(R.color.AppMainColor)

            fragBinding.txtFriend.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewFriend.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.txtGroup.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewGroup.setBackgroundResource(R.color.AppWhiteColor)

            val linearLayoutManager = LinearLayoutManager(activity)
            fragBinding.recycleRanking.layoutManager = linearLayoutManager
            val adapter = RLYourFriendYouListAdapter(activity,false)
            fragBinding.recycleRanking.adapter = adapter
        }
        fragBinding.txtFriend.setOnClickListener {
            fragBinding.txtFriend.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.viewFriend.setBackgroundResource(R.color.AppMainColor)

            fragBinding.txtGroup.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewGroup.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewYou.setBackgroundResource(R.color.AppWhiteColor)

            val linearLayoutManager = LinearLayoutManager(activity)
            fragBinding.recycleRanking.layoutManager = linearLayoutManager
            val adapter = RLYourFriendYouListAdapter(activity,true)
            fragBinding.recycleRanking.adapter = adapter
        }
        fragBinding.txtGroup.setOnClickListener {
            fragBinding.txtGroup.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.viewGroup.setBackgroundResource(R.color.AppMainColor)

            fragBinding.txtFriend.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewFriend.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.viewYou.setBackgroundResource(R.color.AppWhiteColor)
            RLgroupAPiCall()
        }
    }
    private fun RLeffortDataSet(){
        var dataList: List<String> = listOf("Calm","Warm","Cardio","Fat Burn","Endurance","Power","Peak")
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.VISIBLE
        fragBinding.relayRanking.visibility=View.GONE
        RLTools.RLheightsetdisplaywebview(fragBinding.webViewEffort,activity)
        fragBinding.webViewEffort.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.webViewEffort.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        fragBinding.webViewEffort.loadUrl("file:///android_asset/chart-android-effort.html")

        //Main list set
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleEffort.layoutManager = linearLayoutManager
        val adapterdata = RLFeedSessionEffortListAdapter(activity, dataList)
        fragBinding.recycleEffort.adapter = adapterdata
    }
    private fun RLanalysisDataSet(){
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.VISIBLE
        fragBinding.relayEffort.visibility=View.GONE
        fragBinding.relayRanking.visibility=View.GONE

        RLanalysisEffortUISetup()
        RLanalysisPaceUISetup()
        RLanalysisSpeedUISetup()
        RLanalysisElevationUISetup()


    }
    private fun RLanalysisEffortUISetup(){
        Log.d(TAG,"NU")
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData("0"),
            RLTypeOfMetrics.EffortZone to RLMetricData("Calm"),
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
    private fun RLanalysisPaceUISetup(){
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.completed to RLMetricData("0"),
            RLTypeOfMetrics.Averagepace to RLMetricData("0"),
            RLTypeOfMetrics.Slowtest to RLMetricData("0"),
            RLTypeOfMetrics.Fasttest to RLMetricData("0"))

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
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList)
        fragBinding.includePace.recycleAnalysis.adapter = adapterdata

        fragBinding.includePace.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.PACE)
        }
    }
    private fun RLanalysisSpeedUISetup(){
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.Averagespeed to RLMetricData(cardData.average_speed.toString()),
            RLTypeOfMetrics.MaxSpeed to RLMetricData("0"))

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
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList)
        fragBinding.includeSpeed.recycleAnalysis.adapter = adapterdata

        fragBinding.includeSpeed.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.SPEED)
        }
    }
    private fun RLanalysisElevationUISetup(){
        var dataList: List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.Totalclimbed to RLMetricData(cardData.elevation.toString()),
            RLTypeOfMetrics.Minelevation to RLMetricData("0"),
            RLTypeOfMetrics.Maxelevation to RLMetricData("0"))

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
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList)
        fragBinding.includeElevation.recycleAnalysis.adapter = adapterdata

        fragBinding.includeElevation.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.ELEVATION)
        }


    }
    private fun RLsummaryDataSet() {
        var totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.Time to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.Effort to RLMetricData(RLTools.RLformatNumberWithCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.Steps to RLMetricData(RLTools.RLformatNumberWithCommas(cardData.steps.toDouble())),
            RLTypeOfMetrics.Calories to RLMetricData(RLTools.RLformatNumberWithCommas(cardData.burntCalories.toDouble())),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData("0"),
            RLTypeOfMetrics.Distance to RLMetricData(RLTools.RLformatNumberWithCommas(cardData.distance.toDouble())),
            RLTypeOfMetrics.Climbed to RLMetricData(cardData.elevation.toString()),
            RLTypeOfMetrics.AvgPace to RLMetricData("0"),
            RLTypeOfMetrics.AvgSpeed to RLMetricData(cardData.average_speed.toString()),
            RLTypeOfMetrics.Boosts to RLMetricData("0"),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))

        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE
        fragBinding.relayRanking.visibility=View.GONE
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
    override fun onItemClick(position: Int) {
        if (valueslist[position].equals("SUMMARY")){
            RLsummaryDataSet()
        }else if (valueslist[position].equals("ANALYSIS")){
            RLanalysisDataSet()
        }else if (valueslist[position].equals("EFFORT")){
            RLeffortDataSet()
        }else if (valueslist[position].equals("RANKING")){
            RLrankingDataSet()
        }
    }
    fun RLwalkDialogOpen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_group_name)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val recyclerSelectAssign =  dialog.findViewById(R.id.listItems) as RecyclerView
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        btClear.setText(R.string.cancelsmall)
        var dataList: List<String> = listOf(
            "All","HIIT","Ride","Walk","Run","Yoga","Pilates","Dance","Warm","Workout")
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val  simpleAdapter = RLFeedSimpleAdapter(dataList,requireActivity(),fragBinding.txtWalk.text.toString())
        recyclerSelectAssign.adapter = simpleAdapter
        simpleAdapter.seOnClickListners(object : RLFeedSimpleAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                fragBinding.txtWalk.setText(selectioncName)
                dialog.dismiss()
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    fun RLallTimeDialogOpen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_group_name)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val recyclerSelectAssign =  dialog.findViewById(R.id.listItems) as RecyclerView
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        btClear.setText(R.string.cancelsmall)
        var dataList: List<String> = listOf("Last 4 Weeks","Last 12 Weeks","All Time")
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val  simpleAdapter = RLFeedSimpleAdapter(dataList,requireActivity(),fragBinding.txtAlltime.text.toString())
        recyclerSelectAssign.adapter = simpleAdapter
        simpleAdapter.seOnClickListners(object : RLFeedSimpleAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                fragBinding.txtAlltime.setText(selectioncName)
                dialog.dismiss()
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    private fun RLgroupAPiCall() {
        val request = listOf(RLSetGroupRequest(group_data = RLSetGroupData(userid = currentUser, limit = 100, index=0)))
        Log.d(TAG,"setGroupdata= "+request)
        viewModel.RLgetGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        //groupnamelistdialogopen(response.RLText)
                        RLshowCustomAlertDialog(response.text)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLshowCustomAlertDialog(newData: List<RLGroupCardModel>) {
        var selectionGroupcName=""
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.rl_alertdailog_group_name, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.selectgroup)
        builder.setView(dialogLayout)
        val recyclerSelectAssign =  dialogLayout.findViewById(R.id.listItems) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val  dialogAdapter = RLFeedGroupNameAdapter(requireActivity(),true)
        recyclerSelectAssign.adapter = dialogAdapter
        dialogAdapter.RLaddData(newData)
        dialogAdapter.seOnClickListners(object :RLFeedGroupNameAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                selectionGroupcName=selectioncName
            }
        })
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            // Handle input RLText
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancelsmall) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
        // Change the color of the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.CYAN)
        // Change the color of the negative button if needed
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.CYAN)
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