package com.revoola.fragment.overview

import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.overview.adapter.RLOverviewSessionListAdapter
import com.revoola.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.RlFragOverviewSessionsBinding
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.RLOverviewGraphDataRequest
import com.revoola.model.RLOverview_graphData
import com.revoola.model.RLSessionitemset
import com.revoola.model.RlOverviewGraphData
import com.revoola.commonobject.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.google.gson.Gson
import com.revoola.aisetup.AiViewModel
import com.revoola.aisetup.MonthlyStats
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.commonobject.RLYourWayCalvulation.convertToInt
import com.revoola.enumclass.RLValueOvName
import com.revoola.model.RLGetUserAggregatedData
import com.revoola.model.RLGetUserAggregatedDataRequest
import com.revoola.utils.RLPrefManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RLFragOverviewSession : RLBaseFragment(), RLItemClickListener {
    private val TAG: String = RLFragOverviewSession::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var currentUser:String=""
    private var appUnit:String="Metric"

    private var fromThirdParty = "y"
    private var imperial = "y"
    private var toDate = ""
    private var fromDate = ""
    private var selectionPeriod = "This Month"
    private var selectionType = listOf("All")
    private var selectionSource = "All Available"

    private val titleValueList = arrayOf("OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED")

   private var totalDisplayItem=9
    private var swipePosition:Int=16
    private lateinit var gestureDetector: GestureDetector
    private val timezone = TimeZone.getDefault().rawOffset / 1000
    private lateinit var adapterData: RLOverviewSessionListAdapter
    private lateinit var adapterTitle: RLOverviewSessionTitleListAdapter
    private  var cardDate: RlOverviewGraphData? = null
    private val fragBinding by lazy {
        RlFragOverviewSessionsBinding.inflate(layoutInflater)
    }

    private lateinit var filterManager: OverViewFilterManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //activity?.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Dark icons
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview_sessions, container) as RlFragOverviewSessionsBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragOverviewSession" )
        currentUser= RLAuthManager().rl_getCurrentUser()?.uid?:""
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        // Initialize GestureDetector
        gestureDetector = GestureDetector(requireContext(), rl_swipeGestureListener())
        //Filter Dialog Use
        toDate = RLPrefManager.rl_getSomeStringValue(requireContext()  , "toDate", "")
        fromDate = RLPrefManager.rl_getSomeStringValue(requireContext()  , "fromDate", "")
        selectionPeriod =  RLPrefManager.rl_getSomeStringValue(requireContext()  , "selectionPeriod", "This Month")
        selectionType = RLPrefManager.rl_getSomeStringListValue(requireContext()  , "selectionType", listOf("All"))
        selectionSource =  RLPrefManager.rl_getSomeStringValue(requireContext()  , "selectionSource", "All Available")
        filterManager = OverViewFilterManager(requireContext()) { fromDate, toDate, selectionPeriod, selectionSource, selectionType ->
            // Handle the selected filters here
            updateFilters(fromDate, toDate, selectionPeriod, selectionSource, selectionType)
        }
        rl_uisetup()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.rl_showAlertDialog(requireContext(),requireActivity())
            }
        })
        return fragBinding.root
    }
    override fun onItemClick(position: Int) {
        swipePosition=position
        fragBinding.txtTotalsession.setText(titleValueList[position])
        fragBinding.inlayTop.ivTitle.setText(titleValueList[position])
        fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
        rl_moveToCenter(position)
        if (cardDate!=null){
            rl_handleApiResponse(cardDate!!,titleValueList[position],true)
        }
    }
    private  fun  rl_uisetup(){
        rl_helpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.start_help_content)
        rl_onBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivBack.visibility=View.VISIBLE
        fragBinding.inlayTop.ivDescription.setText("")
        //do Title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
         adapterTitle = RLOverviewSessionTitleListAdapter("OVERVIEW",this,titleValueList,activity)
        fragBinding.inlayTop.recyclerTitle.adapter = adapterTitle
        // click to show center
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(fragBinding.inlayTop.recyclerTitle)
        // Initially move the first item to the center
        rl_moveToCenter(16)

        //DATA SET below
        val linearLayoutManagerMain = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = linearLayoutManagerMain
         adapterData = RLOverviewSessionListAdapter(activity,false)
        fragBinding.recycleSession.adapter = adapterData


        fragBinding.webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        //Firebase To Get Data
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                val authManager = RLAuthManager()
                val userId = authManager.rl_getCurrentUser()?.uid?:""
                currentUser = userId
                appUnit = userData.appUnit
                if (userData.appUnit.toString().toLowerCase().equals("imperial")){
                    imperial = "y"
                }else{
                    imperial = "n"
                }
                fragBinding.txtUsername.setText("Hi ${ userData.firstName},")

                if (isAdded){
                    Glide.with(requireContext()).load(userData.displayImage)
                        .placeholder(R.drawable.sample_user).error(R.drawable.sample_user).into(fragBinding.imgUser)
                }
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        if (apiClientRetrofit.rl_isConnected()) {
            //Detail Api
            val selectedPeriod = filterManager.getSelectionPeriod(selectionPeriod)
            if (selectedPeriod.equals("CUSTOM_DATE_RANGE")){
                fragBinding.txtCurrentMonth.setText(filterManager.formatToMonthYear(fromDate) +" - " +filterManager.formatToMonthYear(toDate))
            }else{
                fragBinding.txtCurrentMonth.setText(selectionPeriod)
            }

            fromThirdParty =  if (selectionSource.equals("All Available")) "y" else "n"

            val classType=selectionType.joinToString(",") { it.lowercase() }

            val result = filterManager.getDateNewRangeForPeriod(selectedPeriod,fromDate,toDate)

            val dateFrom = if (selectedPeriod.equals( "CUSTOM_DATE_RANGE")) {
                result["comparisonFromTimestamp"] as Long + timezone
            }else {
                result["fromTimestamp"] as Long + timezone
            }

            val dateTo = result["toTimestamp"] as Long + timezone

            val request = listOf(
                RLOverviewGraphDataRequest(
                    overview_graph = RLOverview_graphData(
                        user = currentUser,
                        classtype = classType,
                        timestampfrom = dateFrom.toInt(),
                        timestampto = dateTo.toInt(),
                        fromthirdparty=fromThirdParty)
                )
            )
            rl_apiCall(titleValueList[swipePosition],request,false)
        } else {
            rl_showDialogFullscreen()
        }

        fragBinding.inlayFilter.ivFilter.setOnClickListener {
            if (::filterManager.isInitialized) {
                filterManager.showFilterDialog()
            }
        }

        // Set the touch listener to the root view (or any full-screen view)
        fragBinding.webView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
        fragBinding.recycleSession.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }
    private fun updateFilters(fromSelectDate: String?, toSelectDate: String?,
                              selectionFPeriod: String, selectionFSource: String, selectionFType: List<String>) {

        toDate = toSelectDate?:""
        fromDate = fromSelectDate?:""
        selectionPeriod = selectionFPeriod
        selectionSource = selectionFSource
        selectionType = selectionFType

        fromThirdParty =  if (selectionFSource.equals("All Available")) "y" else "n"

        val classType=selectionFType.joinToString(",") { it.lowercase() }
        val selectedPeriod = filterManager.getSelectionPeriod(selectionFPeriod)
        val result = filterManager.getDateNewRangeForPeriod(selectedPeriod,fromSelectDate?:"",toSelectDate?:"")

        if (selectedPeriod.equals("CUSTOM_DATE_RANGE")){
            fragBinding.txtCurrentMonth.setText(filterManager.formatToMonthYear(fromSelectDate) +" - " +filterManager.formatToMonthYear(toSelectDate))
        }else{
            fragBinding.txtCurrentMonth.setText(selectionFPeriod)
        }

        val dateFrom = if (selectedPeriod.equals( "CUSTOM_DATE_RANGE")) {
            result["comparisonFromTimestamp"] as Long + timezone
        }else {
            result["fromTimestamp"] as Long + timezone
        }

        val dateTo = result["toTimestamp"] as Long + timezone

        val request = listOf(
            RLOverviewGraphDataRequest(
                overview_graph = RLOverview_graphData(
                    user = currentUser,
                    classtype = classType,
                    timestampfrom = dateFrom.toInt(),
                    timestampto = dateTo.toInt(),
                    fromthirdparty=fromThirdParty)
            )
        )
        if (apiClientRetrofit.rl_isConnected()) {
            //Detail Api
            rl_apiCall(titleValueList[swipePosition],request,true)
        } else {
            rl_showDialogFullscreen()
        }
    }
    private fun rl_apiCall(valueType:String,requestFilter: List<RLOverviewGraphDataRequest>,isFilter: Boolean) {
        RLTools.rl_logDPrint(TAG,"request Overview:- ${Gson().toJson(requestFilter)}")
        viewModel.rl_getOverviewGraph(requestFilter) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        cardDate=response.text[0].overviewGraph[0]
                        val jsonArray = Gson().toJson(response)
                        RLTools.rl_logDPrint(TAG,"Overview_response:-  $jsonArray")
                        rl_handleApiResponse(response.text[0].overviewGraph[0],valueType,isFilter)

                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun rl_handleApiResponse(carddate: RlOverviewGraphData, valueType:String,isFilter: Boolean) {
        val dataList= mutableListOf<RLSessionitemset>()
        var isTextColorSetWhite=false
        rl_bottomHideShowSet(true)
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.visibility=View.VISIBLE
        fragBinding.inlayTop.ivDescription.visibility=View.VISIBLE
        fragBinding.inlayTop.logo.visibility=View.GONE

        fragBinding.relayOverviewName.visibility=View.GONE
        fragBinding.txtTotalsessionNumber.visibility=View.VISIBLE
        fragBinding.txtTotalsession.visibility=View.VISIBLE
        fragBinding.webView.visibility=View.VISIBLE
        fragBinding.relayWeb.visibility=View.VISIBLE
        fragBinding.relayMain.setBackgroundColor(resources.getColor(R.color.AppWhiteColor))

        when (valueType){
            "OVERVIEW"->{
                (context as RLMainActivityRL).rl_bottombarcolorDarkBlue()
                fragBinding.inlayTop.ivBack.visibility=View.GONE
                fragBinding.inlayTop.ivTitle.visibility=View.GONE
                fragBinding.inlayTop.ivDescription.visibility=View.GONE
                fragBinding.inlayTop.logo.visibility=View.VISIBLE

                fragBinding.relayOverviewName.visibility=View.VISIBLE
                fragBinding.txtTotalsessionNumber.visibility=View.GONE
                fragBinding.txtTotalsession.visibility=View.GONE
                fragBinding.webView.visibility=View.GONE
                fragBinding.relayWeb.visibility=View.GONE
                fragBinding.relayMain.setBackgroundColor(resources.getColor(R.color.AppNEWBGColor))
                isTextColorSetWhite=true

                totalDisplayItem=8
                for (i in 0 until  totalDisplayItem){

                    when (i){
                        0-> dataList.add(RLSessionitemset("EFFORT",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Effort,carddate,appUnit),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("RELAXATION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Relaxation,carddate,appUnit),R.drawable.ic_mind_read))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.TotalCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.ActiveCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Distance,carddate,appUnit),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Steps,carddate,appUnit),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Climbed,carddate,appUnit),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("AWARDS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Awards,carddate,appUnit),R.drawable.ic_award))
                    }
                }
                fragBinding.relayOverviewName.visibility=View.VISIBLE
            }
            "SESSIONS" -> {
                rl_webviewurlload("sessions")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Session,carddate,appUnit))
                totalDisplayItem=10
                for (i in 0 until  totalDisplayItem){

                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.LongestSession,carddate,appUnit),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgSession,carddate,appUnit),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("EFFORT",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Effort,carddate,appUnit),R.drawable.ic_heart))
                        3-> dataList.add(RLSessionitemset("RELAXATION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Relaxation,carddate,appUnit),R.drawable.ic_mind_read))
                        4-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.TotalCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        5-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.ActiveCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        6-> dataList.add(RLSessionitemset("DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Distance,carddate,appUnit),R.drawable.ic_distance))
                        7-> dataList.add(RLSessionitemset("STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Steps,carddate,appUnit),R.drawable.fd_steps_green))
                        8-> dataList.add(RLSessionitemset("CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Climbed,carddate,appUnit),R.drawable.ic_climb))
                        9-> dataList.add(RLSessionitemset("AWARDS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Awards,carddate,appUnit),R.drawable.ic_award))
                    }
                }

            }
            "CALORIES" -> {
                rl_webviewurlload("calories")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.TotalCalories,carddate,appUnit))
                totalDisplayItem=6
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        1-> dataList.add(RLSessionitemset("AVG CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY TOTAL",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxDailyTotal,carddate,appUnit),R.drawable.ic_max_calender_black))
                        3-> dataList.add(RLSessionitemset("MAX DAILY ACTIVE",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxDailyActive,carddate,appUnit),R.drawable.ic_max_calender_black))
                        4-> dataList.add(RLSessionitemset("AVG DAILY TOTAL",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgDailyTotal,carddate,appUnit),R.drawable.ic_avg_calender_black))
                        5-> dataList.add(RLSessionitemset("AVG DAILY ACTIVE",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgDailyActive,carddate,appUnit),R.drawable.ic_avg_calender_black))
                    }
                }
            }
            "RELAXATION" -> {
                rl_webviewurlload("relaxation")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Relaxation,carddate,appUnit))
                totalDisplayItem=4
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.LongestSession,carddate,appUnit),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgSession,carddate,appUnit),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("MAX RELAXATION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxRelaxation,carddate,appUnit),R.drawable.ic_mind_read))
                        3-> dataList.add(RLSessionitemset("AVG RELAXATION",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgRelaxation,carddate,appUnit),R.drawable.ic_mind_read))
                    }
                }
            }
            "EFFORT" -> {
                rl_webviewurlload("effort")
                totalDisplayItem=8
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Effort,carddate,appUnit))
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX EFFORT",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxEffort,carddate,appUnit),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("AVG EFFORT",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgEffort,carddate,appUnit),R.drawable.ic_heart))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.TotalCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.ActiveCalories,carddate,appUnit),R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Distance,carddate,appUnit),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Steps,carddate,appUnit),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Climbed,carddate,appUnit),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("AWARDS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Awards,carddate,appUnit),R.drawable.ic_award))
                    }
                }
            }
            "STEPS" -> {
                rl_webviewurlload("steps")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Steps,carddate,appUnit))
                totalDisplayItem=4
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxSteps,carddate,appUnit),R.drawable.fd_steps_green))
                        1-> dataList.add(RLSessionitemset("AVG STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgSteps,carddate,appUnit),R.drawable.fd_steps_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxDailySteps,carddate,appUnit),R.drawable.fd_steps_green))
                        3-> dataList.add(RLSessionitemset("AVG DAILY STEPS",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgDailySteps,carddate,appUnit),R.drawable.fd_steps_green))
                    }
                }
            }
            "DISTANCE" -> {
                rl_webviewurlload("distance")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.DistanceNormal,carddate,appUnit))
                totalDisplayItem=4
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxDistance,carddate,appUnit),R.drawable.ic_distance))
                        1-> dataList.add(RLSessionitemset("AVG DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgDistance,carddate,appUnit),R.drawable.ic_distance))
                        2-> dataList.add(RLSessionitemset("CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Climbed,carddate,appUnit),R.drawable.ic_climb))
                        3-> dataList.add(RLSessionitemset("DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Distance,carddate,appUnit),R.drawable.ic_distance))
                    }
                }
            }
            "CLIMBED" ->{
                rl_webviewurlload("climbed")
                fragBinding.txtTotalsessionNumber.setText(RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.ClimbedNormal,carddate,appUnit))
                totalDisplayItem = 4
                for (i in 0 until  totalDisplayItem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.MaxClimbed,carddate,appUnit),R.drawable.ic_climb))
                        1-> dataList.add(RLSessionitemset("AVG CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.AvgClimbed,carddate,appUnit),R.drawable.ic_climb))
                        2-> dataList.add(RLSessionitemset("DISTANCE (${RLYourWayCalvulation.rl_getKmMiles("km","miles",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Distance,carddate,appUnit),R.drawable.ic_distance))
                        3-> dataList.add(RLSessionitemset("CLIMBED (${RLYourWayCalvulation.rl_getKmMiles("m","ft",appUnit)})",
                            RLYourWayCalvulation.rl_getValueForTitle(RLValueOvName.Climbed,carddate,appUnit),R.drawable.ic_climb))
                    }
                }
            }
        }
        adapterData.RLsetList(dataList,isTextColorSetWhite)

        if (!isFilter){
            val viewModelAI: AiViewModel = ViewModelProvider(this).get(AiViewModel::class.java)
            val stats = MonthlyStats(sessions = convertToInt(carddate.session),
                steps = convertToInt(carddate.total_steps),
                calories = convertToInt(carddate.total_calories))
            // Call load method to start data processing
            viewModelAI.loadMotivationMessage(stats)
            // Observe the state to get the result and print messages
            lifecycleScope.launchWhenStarted {
                viewModelAI.state.collect { state ->
                    // Print messages to Logcat when state changes
                    fragBinding.txtGoodtoseeyou.setText(state.motivation)
                    // RLTools.rl_logDPrint(TAG, "Motivation: ${state.motivation}")
                    // Optionally, handle errors
                    state.error?.let {
                        RLTools.rl_logEPrint(TAG, "Error: $it")
                    }
                }
            }
        }
    }
    private fun rl_webviewurlload(type:String){
        val selectedPeriod = filterManager.getSelectionPeriod(selectionPeriod)
        val result = filterManager.getDateNewRangeForPeriod(selectedPeriod,fromDate,toDate)
        val dateFrom = result["comparisonFromTimestamp"] as Long + timezone
        val dateTo = result["fromTimestamp"] as Long + timezone
        val timeRef = result["timeRef"] as String
        val queryType = if (selectedPeriod.equals( "CUSTOM_DATE_RANGE")) "overviewGraphChartHTMCT" else "overviewGraphChartHTMAll"
        val classTypeString=selectionType.joinToString(",") { it.lowercase() }

        val _timezone = if (timezone > 0) "%2B${timezone}" else "%2D${kotlin.math.abs(timezone)}"

        val params = "?q=$queryType" +
                "&user=$currentUser" +
                "&classtype=$classTypeString" +
                "&graphtimefrom=$dateFrom" +
                "&graphtimeto=$dateTo" +
                "&timerange=$timeRef" +
                "&gmtdiff=$_timezone" +
                "&type=${type.lowercase()}" +
                "&fromthirdparty=$fromThirdParty" +
                "&imperial=$imperial"

        val chart = "https://video.revoola.com/_stuff/getCharts.php$params"
        RLTools.rl_logDPrint(TAG,"chart:- $chart")
        fragBinding.webView.loadUrl(chart)

    }

    //Swipe to move Code
    private fun rl_moveToCenter(position: Int) {
        val layoutManager = fragBinding.inlayTop.recyclerTitle.layoutManager as LinearLayoutManager

        fragBinding.inlayTop.recyclerTitle.post {
            // Scroll to the desired position first
            layoutManager.scrollToPositionWithOffset(position, fragBinding.inlayTop.recyclerTitle.width / 2)
            fragBinding.inlayTop.recyclerTitle.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fragBinding.inlayTop.recyclerTitle.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val view = layoutManager.findViewByPosition(position)
                        if (view != null) {
                            val viewLeft = view.left
                            val viewWidth = view.width

                            val scrollDistance = viewLeft - (fragBinding.inlayTop.recyclerTitle.width / 2 - viewWidth / 2)
                            fragBinding.inlayTop.recyclerTitle.smoothScrollBy(scrollDistance, 0)
                        }
                    }
                })
        }
    }
    // Custom gesture listener to detect swipe gestures
    private inner class rl_swipeGestureListener : GestureDetector.SimpleOnGestureListener() {

        private val SWIPE_THRESHOLD = 100 // Minimum distance to detect swipe
        private val SWIPE_VELOCITY_THRESHOLD = 100 // Minimum velocity to detect swipe

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2.x.minus(e1!!.x) ?: 0.0f
            val diffY = e2.y.minus(e1.y) ?: 0.0f

            if (Math.abs(diffX) > Math.abs(diffY)) {
                // Detect horizontal swipe
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right
                        onSwipeRight()
                    } else {
                        // Swipe left
                        onSwipeLeft()
                    }
                    return true
                }
            }
            return false
        }

        /*  @RequiresApi(Build.VERSION_CODES.O)
          override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
              val diffX = e2.x.minus(e1.x) ?: 0.0f
              val diffY = e2.y.minus(e1.y) ?: 0.0f

              if (Math.abs(diffX) > Math.abs(diffY)) {
                  // Detect horizontal swipe
                  if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                      if (diffX > 0) {
                          // Swipe right
                          onSwipeRight()
                      } else {
                          // Swipe left
                          onSwipeLeft()
                      }
                      return true
                  }
              }
              return false
          }*/

    }
    // Handle swipe right gesture
    private fun onSwipeRight() {
        // Transition to the next screen or perform an action
        // Example: You could load another fragment or activity

        println("Swiped right!")
        try {
            val position=swipePosition-1
            swipePosition=position
            adapterTitle.rl_setList(titleValueList[position])
            fragBinding.txtTotalsession.setText(titleValueList[position])
            fragBinding.inlayTop.ivTitle.setText(titleValueList[position])
            fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
            rl_moveToCenter(position)
            if (cardDate!=null){
                rl_handleApiResponse(cardDate!!,titleValueList[position],true)
            }
        }catch (e:Exception){
            println("Exception:- ${e.message}")
        }

    }
    // Handle swipe left gesture
    private fun onSwipeLeft() {
        // Transition to the previous screen or perform an action
        println("Swiped left!")
        try {
            val position=swipePosition+1
            swipePosition=position
            adapterTitle.rl_setList(titleValueList[position])
            fragBinding.txtTotalsession.setText(titleValueList[position])
            fragBinding.inlayTop.ivTitle.setText(titleValueList[position])
            fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
            rl_moveToCenter(position)
            if (cardDate!=null){
                rl_handleApiResponse(cardDate!!,titleValueList[position],true)
            }
        }catch (e:Exception){
            println("Exception:- ${e.message}")
        }

    }

}

