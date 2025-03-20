package com.revoola.fragment.overview

import android.app.Dialog
import android.os.Build

import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.overview.adapter.RLOverviewSessionListAdapter
import com.revoola.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.RlFilterOverviewBinding
import com.revoola.databinding.RlFragOverviewBinding
import com.revoola.databinding.RlFragOverviewSessionsBinding
import com.revoola.fragment.overview.adapter.RLAllDialogListAdapter
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.RLOverviewGraphDataRequest
import com.revoola.model.RLOverview_graphData
import com.revoola.model.RLSessionitemset
import com.revoola.model.RlOverviewGraphData
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.google.gson.Gson
import com.revoola.enumclass.RLValueOvName
import com.revoola.model.RLGetUserAggregatedData
import com.revoola.model.RLGetUserAggregatedDataRequest
import com.revoola.model.RLTextOverview
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.roundToInt

class RLFragOverviewSession : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragOverviewSession::class.java.simpleName
    lateinit var fragBinding: RlFragOverviewSessionsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var appUnit:String="Metric"
    val valueslist = arrayOf("OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED")
    var totaldisplayitem=9
    private lateinit var adapterdata: RLOverviewSessionListAdapter
    private lateinit var adapterTitle: RLOverviewSessionTitleListAdapter
    private  var cardDate: RlOverviewGraphData? = null
    private lateinit var gestureDetector: GestureDetector
    private var swipePosition:Int=16

    private val binding by lazy {
        RlFragOverviewBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //activity?.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Dark icons
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview_sessions, container) as RlFragOverviewSessionsBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragOverviewSession" )
        currentUser=  com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        // Initialize GestureDetector
        gestureDetector = GestureDetector(requireContext(), RlSwipeGestureListener())
        RLuisetup()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.RLshowAlertDialog(requireContext(),requireActivity())
            }
        })
        return fragBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private  fun  RLuisetup(){
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, com.revoola.utils.RLPrefManager.start_help_content)
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivBack.visibility=View.VISIBLE
        fragBinding.inlayTop.ivDescription.setText("")
        //do Title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
         adapterTitle = RLOverviewSessionTitleListAdapter("OVERVIEW",this,valueslist,activity)
        fragBinding.inlayTop.recyclerTitle.adapter = adapterTitle
        // click to show center 
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(fragBinding.inlayTop.recyclerTitle)
        // Initially move the first item to the center
        RLMoveToCenter(16)

        //DATA SET below
        val linearLayoutManagerMain = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = linearLayoutManagerMain
         adapterdata = RLOverviewSessionListAdapter(activity,false)
        fragBinding.recycleSession.adapter = adapterdata

        RLTools.RLheightsetdisplaywebview(fragBinding.webView,activity)
        RLTools.RLheightsetdisplayAll(fragBinding.relayOverviewName,activity)

        fragBinding.webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        //Firebase To Get Data
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                val authManager = RLAuthManager()
                val userId = authManager.RlgetCurrentUser()?.uid?:""
                currentUser = userId
                appUnit = userData.appUnit
                fragBinding.txtUsername.setText("Hi ${ userData.firstName},")
                RLapicallAggregatedData(userData.joiningDate?:0)
                if (isAdded){
                    Glide.with(requireContext()).load(userData.displayImage)
                        .placeholder(R.drawable.sample_user).error(R.drawable.sample_user).into(fragBinding.imgUser)
                }
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall("OVERVIEW")
        } else {
            RLshowDialogFullscreen()
        }
        fragBinding.inlayFilter.ivFilter.setOnClickListener {
           // RLallactivitydialogopen()
            RLFilterdialogopen()
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int) {
        swipePosition=position
        fragBinding.txtTotalsession.setText(valueslist[position])
        fragBinding.inlayTop.ivTitle.setText(valueslist[position])
        fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
        RLMoveToCenter(position)
        /*if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall(valueslist[position])
        } else {
            RLshowDialogFullscreen()
        }*/
        if (cardDate!=null){
            RLHandleApiResponse(cardDate!!,valueslist[position])
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun RLAPiCall(valueType:String) {

        val offset = Date().timezoneOffset
        val date = Date()
        val firstDay= Date(date.year, date.month, 1)
        val timestampFrom = ((if (firstDay!=null) firstDay.time else firstDay) / 1000 - offset * 60).toInt()
        val timestampTo = (date.time / 1000 - offset * 60).toInt()

        val request = listOf(
            RLOverviewGraphDataRequest(
                overview_graph = RLOverview_graphData(
                    user = currentUser,
                    classtype = "mindAndBody",
                    timestampfrom = timestampFrom.toInt(),
                    timestampto = timestampTo.toInt(),
                    fromthirdparty="n")
            )
        )
        val gson = Gson()
        val jsonRequest = gson.toJson(request)

        RLTools.RlLogDPrint(TAG,"request Overview:- $jsonRequest")

        viewModel.RLgetOverviewGraph(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        cardDate=response.text[0].overviewGraph[0]
                        val gson = Gson()
                        val jsonArray = gson.toJson(response)
                       RLTools.RlLogEPrint(TAG,"Overview_jsonDate:-  $jsonArray")
                        RLHandleApiResponse(response.text[0].overviewGraph[0],valueType)

                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun RLHandleApiResponse(carddate: RlOverviewGraphData, valueType:String) {
        val dataList= mutableListOf<RLSessionitemset>()
        var isTextColorSetWhite=false
        RLBottomHideShowSet(true)
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.visibility=View.VISIBLE
        fragBinding.inlayTop.ivDescription.visibility=View.VISIBLE
        fragBinding.inlayTop.logo.visibility=View.GONE

        fragBinding.relayOverviewName.visibility=View.GONE
        fragBinding.txtTotalsessionNumber.visibility=View.VISIBLE
        fragBinding.txtTotalsession.visibility=View.VISIBLE
        fragBinding.webView.visibility=View.VISIBLE
        fragBinding.relayMain.setBackgroundColor(resources.getColor(R.color.AppWhiteColor))


        when (valueType){
            "OVERVIEW"->{
                (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
                fragBinding.inlayTop.ivBack.visibility=View.GONE
                fragBinding.inlayTop.ivTitle.visibility=View.GONE
                fragBinding.inlayTop.ivDescription.visibility=View.GONE
                fragBinding.inlayTop.logo.visibility=View.VISIBLE

                fragBinding.relayOverviewName.visibility=View.VISIBLE
                fragBinding.txtTotalsessionNumber.visibility=View.GONE
                fragBinding.txtTotalsession.visibility=View.GONE
                fragBinding.webView.visibility=View.GONE
                fragBinding.relayMain.setBackgroundColor(resources.getColor(R.color.AppNEWBGColor))
                //Value Set
                val currentMonth= RLTools.RLgetCalculatedMonths()
                fragBinding.txtCurrentMonth.setText(currentMonth)
                isTextColorSetWhite=true
                
                totaldisplayitem=8
                for (i in 0 until  totaldisplayitem){

                    when (i){
                        0-> dataList.add(RLSessionitemset("EFFORT",
                            RLGetValueForTitle(RLValueOvName.Effort,carddate),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("RELAXATION",
                            RLGetValueForTitle(RLValueOvName.Relaxation,carddate),R.drawable.ic_mind_read))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLGetValueForTitle(RLValueOvName.TotalCalories,carddate),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLGetValueForTitle(RLValueOvName.ActiveCalories,carddate),R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.Distance,carddate),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",
                            RLGetValueForTitle(RLValueOvName.Steps,carddate),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.Climbed,carddate),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("AWARDS",
                            RLGetValueForTitle(RLValueOvName.Awards,carddate),R.drawable.ic_award))
                    }
                }
                fragBinding.relayOverviewName.visibility=View.VISIBLE
            }
            "SESSIONS" -> {
                RLwebviewurlload("sessions")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.Session,carddate))
                totaldisplayitem=10
                for (i in 0 until  totaldisplayitem){

                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",
                            RLGetValueForTitle(RLValueOvName.LongestSession,carddate),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",
                            RLGetValueForTitle(RLValueOvName.AvgSession,carddate),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("EFFORT",
                            RLGetValueForTitle(RLValueOvName.Effort,carddate),R.drawable.ic_heart))
                        3-> dataList.add(RLSessionitemset("RELAXATION",
                            RLGetValueForTitle(RLValueOvName.Relaxation,carddate),R.drawable.ic_mind_read))
                        4-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLGetValueForTitle(RLValueOvName.TotalCalories,carddate),R.drawable.fd_calories_green))
                        5-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLGetValueForTitle(RLValueOvName.ActiveCalories,carddate),R.drawable.fd_calories_green))
                        6-> dataList.add(RLSessionitemset("DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.Distance,carddate),R.drawable.ic_distance))
                        7-> dataList.add(RLSessionitemset("STEPS",
                            RLGetValueForTitle(RLValueOvName.Steps,carddate),R.drawable.fd_steps_green))
                        8-> dataList.add(RLSessionitemset("CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.Climbed,carddate),R.drawable.ic_climb))
                        9-> dataList.add(RLSessionitemset("AWARDS",
                            RLGetValueForTitle(RLValueOvName.Awards,carddate),R.drawable.ic_award))
                    }
                }

            }
            "CALORIES" -> {
                RLwebviewurlload("calories")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.TotalCalories,carddate))
                totaldisplayitem=6
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CALORIES",
                            RLGetValueForTitle(RLValueOvName.MaxCalories,carddate),R.drawable.fd_calories_green))
                        1-> dataList.add(RLSessionitemset("AVG CALORIES",
                            RLGetValueForTitle(RLValueOvName.AvgCalories,carddate),R.drawable.fd_calories_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY TOTAL",
                            RLGetValueForTitle(RLValueOvName.MaxDailyTotal,carddate),R.drawable.ic_max_calender_black))
                        3-> dataList.add(RLSessionitemset("MAX DAILY ACTIVE",
                            RLGetValueForTitle(RLValueOvName.MaxDailyActive,carddate),R.drawable.ic_max_calender_black))
                        4-> dataList.add(RLSessionitemset("AVG DAILY TOTAL",
                            RLGetValueForTitle(RLValueOvName.AvgDailyTotal,carddate),R.drawable.ic_avg_calender_black))
                        5-> dataList.add(RLSessionitemset("AVG DAILY ACTIVE",
                            RLGetValueForTitle(RLValueOvName.AvgDailyActive,carddate),R.drawable.ic_avg_calender_black))
                    }
                }
            }
            "RELAXATION" -> {
                RLwebviewurlload("relaxation")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.Relaxation,carddate))
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",
                            RLGetValueForTitle(RLValueOvName.LongestSession,carddate),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",
                            RLGetValueForTitle(RLValueOvName.AvgSession,carddate),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("MAX RELAXATION",
                            RLGetValueForTitle(RLValueOvName.MaxRelaxation,carddate),R.drawable.ic_mind_read))
                        3-> dataList.add(RLSessionitemset("AVG RELAXATION",
                            RLGetValueForTitle(RLValueOvName.AvgRelaxation,carddate),R.drawable.ic_mind_read))
                    }
                }
            }
            "EFFORT" -> {
                RLwebviewurlload("effort")
                totaldisplayitem=8
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.Effort,carddate))
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX EFFORT",
                            RLGetValueForTitle(RLValueOvName.MaxEffort,carddate),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("AVG EFFORT",
                            RLGetValueForTitle(RLValueOvName.AvgEffort,carddate),R.drawable.ic_heart))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",
                            RLGetValueForTitle(RLValueOvName.TotalCalories,carddate),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES",
                            RLGetValueForTitle(RLValueOvName.ActiveCalories,carddate),R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.Distance,carddate),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",
                            RLGetValueForTitle(RLValueOvName.Steps,carddate),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.Climbed,carddate),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("AWARDS",
                            RLGetValueForTitle(RLValueOvName.Awards,carddate),R.drawable.ic_award))
                    }
                }
            }
            "STEPS" -> {
                RLwebviewurlload("steps")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.Steps,carddate))
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX STEPS",
                            RLGetValueForTitle(RLValueOvName.MaxSteps,carddate),R.drawable.fd_steps_green))
                        1-> dataList.add(RLSessionitemset("AVG STEPS",
                            RLGetValueForTitle(RLValueOvName.AvgSteps,carddate),R.drawable.fd_steps_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY STEPS",
                            RLGetValueForTitle(RLValueOvName.MaxDailySteps,carddate),R.drawable.fd_steps_green))
                        3-> dataList.add(RLSessionitemset("AVG DAILY STEPS",
                            RLGetValueForTitle(RLValueOvName.AvgDailySteps,carddate),R.drawable.fd_steps_green))
                    }
                }
            }
            "DISTANCE" -> {
                RLwebviewurlload("distance")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.DistanceNormal,carddate))
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.MaxDistance,carddate),R.drawable.ic_distance))
                        1-> dataList.add(RLSessionitemset("AVG DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.AvgDistance,carddate),R.drawable.ic_distance))
                        2-> dataList.add(RLSessionitemset("CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.Climbed,carddate),R.drawable.ic_climb))
                        3-> dataList.add(RLSessionitemset("DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.Distance,carddate),R.drawable.ic_distance))
                    }
                }
            }
            "CLIMBED" ->{
                RLwebviewurlload("climbed")
                fragBinding.txtTotalsessionNumber.setText(RLGetValueForTitle(RLValueOvName.ClimbedNormal,carddate))
                totaldisplayitem = 4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.MaxClimbed,carddate),R.drawable.ic_climb))
                        1-> dataList.add(RLSessionitemset("AVG CLIMBED (${RLGetKmMiles("m","ft")})",
                            RLGetValueForTitle(RLValueOvName.AvgClimbed,carddate),R.drawable.ic_climb))
                        2-> dataList.add(RLSessionitemset("DISTANCE (${RLGetKmMiles("km","miles")})",
                            RLGetValueForTitle(RLValueOvName.Distance,carddate),R.drawable.ic_distance))
                        3-> dataList.add(RLSessionitemset("AWARDS",
                            RLGetValueForTitle(RLValueOvName.Awards,carddate),R.drawable.ic_award))
                    }
                }
            }
        }
        adapterdata.RLsetList(dataList,isTextColorSetWhite)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun RLwebviewurlload(type:String){

        // Set the timezone to GMT
        val zoneId = ZoneId.of("GMT")

        // Get the current date-time in GMT
        val now = ZonedDateTime.now(zoneId)

        // Get the first day of the previous month
        val firstDay = now.minusMonths(1).withDayOfMonth(1)

        // Get the start of the current month
        val endDay = now.withDayOfMonth(1)

        // Get the offset in minutes
        val offset = firstDay.offset.totalSeconds / 60

        // Get the Unix timestamp (in seconds)
        val timestampFrom = firstDay.toEpochSecond()
        val timestampTo = endDay.toEpochSecond()

        //val imageUrl="${RLConstants.BASE_URL}getResponse_v2.php?q=overviewGraphChartHTML&user=w2p8SQCvE3emjEEDo66f02eF6fG2&classtype=all&graphtimefrom=1711929600&graphtimeto=1714521600&timerange=this_month&gmtdiff=%2D0&type=$type"
        val imageUrl=RLConstants.BASE_URL+"_stuff/getCharts.php?q=overviewGraphChartHTMAll&user=$currentUser&classtype=all&graphtimefrom=$timestampFrom&graphtimeto=$timestampTo&timerange=this_month&gmtdiff=%2D0&type=$type&fromthirdparty=n&imperial=y"
       RLTools.RlLogEPrint(TAG,"$type CHART URL:- $imageUrl")
        fragBinding.webView.loadUrl(imageUrl)
    }
    private fun RLallactivitydialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_allactivity)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)


        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        val recycleDialog : RecyclerView = dialog.findViewById(R.id.recycle_dialog)
        val namelist = arrayOf(getString(R.string.mindbody),
            getString(R.string.allmind),
            getString(R.string.allbody),
            getString(R.string.selectbodyactivity),)
        val adapter = RLAllDialogListAdapter(requireContext(), namelist) { clickdata ->
            // Handle selection
            if (clickdata.equals(getString(R.string.selectbodyactivity))){
              dialog.dismiss()
                RLSelectBodyActivityDialogOpen()
            }
        }
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycleDialog.layoutManager = linearLayoutManager
        recycleDialog.adapter = adapter

        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    private fun RLSelectBodyActivityDialogOpen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_allactivity)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        val tvTitleDialog : TextView = dialog.findViewById(R.id.tvTitle_dialog)
        val recycleDialog : RecyclerView = dialog.findViewById(R.id.recycle_dialog)

        tvTitleDialog.setText(getString(R.string.selectbodyactivity))
        val namelist = arrayOf("DANCE", "HIIT","PILATES","RIDE","RUN","WALK","YOGA")

        val adapter = RLAllDialogListAdapter(requireContext(), namelist) { clickdata ->
            // Handle date selection
            //clickdata
        }
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycleDialog.layoutManager = linearLayoutManager
        recycleDialog.adapter = adapter

        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    private fun RLMoveToCenter(position: Int) {
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
    private inner class RlSwipeGestureListener : GestureDetector.SimpleOnGestureListener() {

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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSwipeRight() {
        // Transition to the next screen or perform an action
        // Example: You could load another fragment or activity

        println("Swiped right!")
        try {
            val position=swipePosition-1
            swipePosition=position
            adapterTitle.RLsetList(valueslist[position])
            fragBinding.txtTotalsession.setText(valueslist[position])
            fragBinding.inlayTop.ivTitle.setText(valueslist[position])
            fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
            RLMoveToCenter(position)
            if (cardDate!=null){
                RLHandleApiResponse(cardDate!!,valueslist[position])
            }
        }catch (e:Exception){
            println("Exception:- ${e.message}")
        }

    }
    // Handle swipe left gesture
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSwipeLeft() {
        // Transition to the previous screen or perform an action
        println("Swiped left!")
        try {
            val position=swipePosition+1
            swipePosition=position
            adapterTitle.RLsetList(valueslist[position])
            fragBinding.txtTotalsession.setText(valueslist[position])
            fragBinding.inlayTop.ivTitle.setText(valueslist[position])
            fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
            RLMoveToCenter(position)
            if (cardDate!=null){
                RLHandleApiResponse(cardDate!!,valueslist[position])
            }
        }catch (e:Exception){
            println("Exception:- ${e.message}")
        }

    }

    private fun RLFilterdialogopen() {
        var isExpande:Boolean=false
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setContentView(R.layout.rl_filter_overview)
        val binding = RlFilterOverviewBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
       binding.inlayThisMonth.BoxTextFirst.setText("THIS")
       binding.inlayThisMonth.BoxTextSecond.setText("MONTH")

        binding.inlayLast3Month.BoxTextFirst.setText("LAST 3")
        binding.inlayLast3Month.BoxTextSecond.setText("MONTHS")

        binding.inlayLast6Month.BoxTextFirst.setText("LAST 6")
        binding.inlayLast6Month.BoxTextSecond.setText("MONTHs")

        binding.inlayThisYear.BoxTextFirst.setText("THIS")
        binding.inlayThisYear.BoxTextSecond.setText("YEAR")


        binding.layoutPeriod.setOnClickListener{
            if (isExpande){
                isExpande=false
                binding.PERIODLAYOUT.visibility=View.VISIBLE
            }else{
                isExpande=true
                binding.PERIODLAYOUT.visibility=View.GONE
            }
        }

        binding.tvClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun RLapicallAggregatedData(joiningDate:Long) {
        val date = Calendar.getInstance()
        val firstDay = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val offset = TimeZone.getDefault().rawOffset / 1000
        val timestampFrom = (firstDay.timeInMillis / 1000) - offset
        val timestampTo = (date.timeInMillis / 1000) - offset

        val request = listOf(
            RLGetUserAggregatedDataRequest(
                getUserAggregatedData = RLGetUserAggregatedData(
                    userid = currentUser,
                    classtype = "all",
                    timestampfrom = timestampFrom,
                    timestampto = timestampTo)
            )
        )
        RLTools.RlLogDPrint(TAG,"request:- $request")
        viewModel.RLgetUserAggregatedData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        val session=response.text[0].aggregated[0].session
                        val displayMessage = RLTools.RLGetDisplayMessage(joiningDate = joiningDate,session)
                        fragBinding.txtGoodtoseeyou.setText(displayMessage)
                    }else {
                        RLcommonToast(response.type)
                    }
                }catch (e:Exception){
                    RLTools.RlLogDPrint(TAG,"exception= "+e.message)
                }
            }.onFailure { error ->
                // Handle failure
                RLTools.RlLogDPrint(TAG,"error= "+error.message)

            }
        }
    }

    private fun RLGetValueForTitle(title: String, cardOvData: RlOverviewGraphData): String {
        val isImperial = RLTools.RLGetIsImperial(appUnit)

        return when (title) {
            RLValueOvName.Effort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_REV))
            RLValueOvName.MaxEffort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_REV_per_session))
            RLValueOvName.AvgEffort  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_REV_per_session))

            RLValueOvName.Awards  -> {
                val totalAwards = cardOvData.medals_bronze + cardOvData.medals_silver + cardOvData.medals_gold
                if (totalAwards != 0) totalAwards.toString() else "0"
            }
            RLValueOvName.Steps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_steps))
            RLValueOvName.MaxSteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_steps_per_session))
            RLValueOvName.AvgSteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_steps_per_session))
            RLValueOvName.MaxDailySteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.max_daily_steps))
            RLValueOvName.AvgDailySteps  -> RLTools.RLformatCommasInt(convertToInt(cardOvData.avg_daily_steps))

            RLValueOvName.Distance  -> RLConvertDistance(isImperial,cardOvData)
            RLValueOvName.DistanceNormal  -> RLTools.RLformatCommas(convertToDouble(cardOvData.total_distance))
            RLValueOvName.MaxDistance  -> RLTools.RLformatCommas(convertToDouble(cardOvData.max_distance_per_session) )
            RLValueOvName.AvgDistance  -> RLTools.RLformatCommas(convertToDouble(cardOvData.avg_distance_per_session))

            RLValueOvName.Relaxation -> RLTools.RLminutesget(convertToInt(cardOvData.total_rmm))
            RLValueOvName.MaxRelaxation -> convertToInt(cardOvData.max_rmm_per_session).toString()
            RLValueOvName.AvgRelaxation -> convertToInt(cardOvData.avg_rmm_per_session).toString()
            RLValueOvName.TotalCalories -> convertToInt(cardOvData.total_calories).toString()
            RLValueOvName.ActiveCalories -> convertToInt(cardOvData.active_calories).toString()

            RLValueOvName.AvgClimbed -> convertToInt(cardOvData.avg_elevation_per_session).toString()
            RLValueOvName.MaxClimbed -> convertToInt(cardOvData.max_elevation_per_session).toString()
            RLValueOvName.ClimbedNormal -> RLTools.RLformatCommasInt(convertToInt(cardOvData.total_elevation))
            RLValueOvName.Climbed  -> {
                val demsElevation:Int = convertToInt(cardOvData.total_elevation?:-1)
                val elevation = if (!isImperial) {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                } else {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084).toInt())
                }
                elevation.toString()
            }
            RLValueOvName.Session -> convertToInt(cardOvData.session).toString()
            RLValueOvName.AvgSession -> RLTools.RLminutesget(convertToInt(cardOvData.avg_time_per_session))
            RLValueOvName.LongestSession -> RLTools.RLminutesget(convertToInt(cardOvData.max_time_per_session))
            RLValueOvName.MaxCalories -> convertToInt(cardOvData.max_total_calories_per_session).toString()
            RLValueOvName.AvgCalories -> convertToInt(cardOvData.avg_total_calories_per_session).toString()
            RLValueOvName.MaxDailyTotal -> convertToInt(cardOvData.max_daily_total_calories).toString()
            RLValueOvName.MaxDailyActive -> convertToInt(cardOvData.max_daily_active_calories).toString()
            RLValueOvName.AvgDailyTotal -> convertToInt(cardOvData.avg_daily_total_calories).toString()
            RLValueOvName.AvgDailyActive -> convertToInt(cardOvData.avg_daily_active_calories).toString()
            else -> "0"
        }

    }
    private fun RLConvertDistance(isImperial: Boolean, cardOvData: RlOverviewGraphData): String {
        val distance=convertToDouble(cardOvData.total_distance)
        if (!isImperial) {
            return RLTools.RLformatCommas(distance)
        } else{
            return  RLTools.RLformatCommas(distance * 0.621371)
        }
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
    private fun convertToDouble(value: Any): Double {
        return when (value) {
            is Double -> if (value.isFinite()) value else 0.0
            is Float -> if (value.isFinite()) value.toDouble() else 0.0
            is Int -> value.toDouble()
            is Long -> value.toDouble()
            is String -> value.toDoubleOrNull()?.takeIf { it.isFinite() } ?: 0.0
            else -> 0.0
        }
    }
    private fun RLGetKmMiles(km:String,miles:String):String{
        val isImperial = RLTools.RLGetIsImperial(appUnit)
        return if (isImperial) miles else km
    }
}