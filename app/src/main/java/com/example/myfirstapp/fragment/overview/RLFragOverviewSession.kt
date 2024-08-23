package com.example.myfirstapp.fragment.overview

import android.app.Dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionListAdapter
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragOverviewBinding
import com.example.myfirstapp.databinding.RlFragOverviewSessionsBinding
import com.example.myfirstapp.fragment.overview.adapter.RLAllDialogListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.model.RLOverviewGraphResponseDataCard
import com.example.myfirstapp.model.RLSessionitemset
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import java.util.Calendar
import java.util.TimeZone

class RLFragOverviewSession : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragOverviewSession::class.java.simpleName
    lateinit var fragBinding: RlFragOverviewSessionsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
   // val valueslist = arrayOf("OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED" )
    val valueslist = arrayOf("OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED",
       "OVERVIEW","SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED")
    var totaldisplayitem=9
    private lateinit var adapterdata: RLOverviewSessionListAdapter
    private  var cardDate: RLOverviewGraphResponseDataCard? = null
    private val binding by lazy {
        RlFragOverviewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //activity?.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Dark icons
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview_sessions, container) as RlFragOverviewSessionsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragOverviewSession" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }

    private  fun  RLuisetup(){
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.start_help_content)
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivBack.visibility=View.VISIBLE
        fragBinding.inlayTop.ivDescription.setText("")
        //do Title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
        val adapterTitle = RLOverviewSessionTitleListAdapter("OVERVIEW",this,valueslist,activity)
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

        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall("OVERVIEW")
        } else {
            RLshowDialogFullscreen()
        }
        fragBinding.inlayFilter.ivFilter.setOnClickListener {
            RLallactivitydialogopen()
        }
    }
    override fun onItemClick(position: Int) {
        fragBinding.txtTotalsession.setText(valueslist[position])
        fragBinding.inlayTop.ivTitle.setText(valueslist[position])
        fragBinding.inlayTop.ivDescription.setText("THIS MONTH")
        RLMoveToCenter(position)
       /* if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall(valueslist[position])
        } else {
            RLshowDialogFullscreen()
        }*/
        if (cardDate!=null){
            RLHandleApiResponse(cardDate!!,valueslist[position])
        }
    }
    private fun RLAPiCall(valuetype:String) {
        val date = Calendar.getInstance()
        val firstDay = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val offset = TimeZone.getDefault().rawOffset / 1000
        val timestampFrom = (firstDay.timeInMillis / 1000) - offset
        val timestampTo = (date.timeInMillis / 1000) - offset

        viewModel.RLgetOverviewGraph("overviewGraph",currentUser,timestampFrom,timestampTo,"all") { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        cardDate=response.text[0].overviewGraph[0]
                        RLHandleApiResponse(response.text[0].overviewGraph[0],valuetype)

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
    private fun RLHandleApiResponse(carddate: RLOverviewGraphResponseDataCard, valueType:String) {
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
                val currentMonth=RLTools.RLgetCalculatedMonths()
                fragBinding.txtCurrentMonth.setText(currentMonth)
                isTextColorSetWhite=true
                totaldisplayitem=8
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("EFFORT",RLTools.RLformatCommas(carddate.totalREV.toDouble()),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("RELAXATION",carddate.totalrms.toString(),R.drawable.ic_mind_read))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",RLTools.RLformatCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES","0",R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (miles)",RLTools.RLformatCommas(carddate.distance.toDouble()),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    }
                }
            }
            "SESSIONS" -> {
                RLwebviewurlload("sessions")

                fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatCommas(carddate.session.toDouble()))
                totaldisplayitem=10
                val awards=carddate.medals_gold+carddate.medals_silver+carddate.medals_bronze
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",RLTools.RLminutesget(carddate.max_time.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",RLTools.RLminutesget(carddate.avgtime.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("EFFORT",RLTools.RLformatCommas(carddate.totalREV.toDouble()),R.drawable.ic_heart))
                        3-> dataList.add(RLSessionitemset("RELAXATION",carddate.totalrms.toString(),R.drawable.ic_mind_read))
                        4-> dataList.add(RLSessionitemset("TOTAL CALORIES",RLTools.RLformatCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                        5-> dataList.add(RLSessionitemset("ACTIVE CALORIES","0",R.drawable.fd_calories_green))
                        6-> dataList.add(RLSessionitemset("DISTANCE (miles)",RLTools.RLformatCommas(carddate.distance.toDouble()),R.drawable.ic_distance))
                        7-> dataList.add(RLSessionitemset("STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        8-> dataList.add(RLSessionitemset("CLIMBED (feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                        9-> dataList.add(RLSessionitemset("AWARDS",awards.toString(),R.drawable.ic_award))
                    }
                }

            }
            "CALORIES" -> {
                RLwebviewurlload("calories")
                fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatCommas(carddate.burntcalories.toDouble()))
                totaldisplayitem=6
                val DAILYAVGTOTALCALORIES = if (carddate.countSessionBody != 0) {
                    carddate.burntcalories / carddate.countSessionBody
                } else {
                    0 // or some default value if countSessionBody is zero
                }
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CALORIES",RLTools.RLformatCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                        1-> dataList.add(RLSessionitemset("AVG CALORIES","0",R.drawable.fd_calories_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY TOTAL",RLTools.RLformatCommas(carddate.maxcalories.toDouble()),R.drawable.ic_max_calender_black))
                        3-> dataList.add(RLSessionitemset("MAX DAILY ACTIVE","0",R.drawable.ic_max_calender_black))
                        4-> dataList.add(RLSessionitemset("AVG DAILY TOTAL",RLTools.RLformatCommas(DAILYAVGTOTALCALORIES.toDouble()),R.drawable.ic_avg_calender_black))
                        5-> dataList.add(RLSessionitemset("AVG DAILY ACTIVE","0",R.drawable.ic_avg_calender_black))
                    }
                }
            }
            "RELAXATION" -> {
                RLwebviewurlload("relaxation")
                fragBinding.txtTotalsessionNumber.setText(RLTools.RLminutesget(carddate.totalrmm.toInt())+"m".toString())
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("LONGEST SESSION",RLTools.RLminutesget(carddate.max_time.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                        1-> dataList.add(RLSessionitemset("AVG SESSION",RLTools.RLminutesget(carddate.avgtime.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                        2-> dataList.add(RLSessionitemset("MAX RELAXATION","0",R.drawable.ic_mind_read))
                        3-> dataList.add(RLSessionitemset("AVG RELAXATION","0",R.drawable.ic_mind_read))
                    }
                }
            }
            "EFFORT" -> {
                RLwebviewurlload("effort")
                totaldisplayitem=8
                val averageEffort = if (carddate.countSessionBody != 0) {
                    carddate.totalREV / carddate.countSessionBody
                } else {
                    0 // or some default value if countSessionBody is zero
                }
                fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatCommas(carddate.totalREV.toDouble()))
                val awards=carddate.medals_gold+carddate.medals_silver+carddate.medals_bronze
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX EFFORT",carddate.maxREV.toString(),R.drawable.ic_heart))
                        1-> dataList.add(RLSessionitemset("AVG EFFORT",averageEffort.toString(),R.drawable.ic_heart))
                        2-> dataList.add(RLSessionitemset("TOTAL CALORIES",RLTools.RLformatCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                        3-> dataList.add(RLSessionitemset("ACTIVE CALORIES","0",R.drawable.fd_calories_green))
                        4-> dataList.add(RLSessionitemset("DISTANCE (miles)",RLTools.RLformatCommas(carddate.distance.toDouble()),R.drawable.ic_distance))
                        5-> dataList.add(RLSessionitemset("STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        6-> dataList.add(RLSessionitemset("CLIMBED (feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                        7-> dataList.add(RLSessionitemset("AWARDS",awards.toString(),R.drawable.ic_award))
                    }
                }
            }
            "STEPS" -> {
                RLwebviewurlload("steps")
                fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatCommas(carddate.steps.toDouble()))
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        1-> dataList.add(RLSessionitemset("AVG STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        2-> dataList.add(RLSessionitemset("MAX DAILY STEPS",RLTools.RLformatCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                        3-> dataList.add(RLSessionitemset("AVG DAILY STEPS",RLTools.RLformatCommas(carddate.avgsteps.toDouble()),R.drawable.fd_steps_green))
                    }
                }
            }
            "DISTANCE" -> {
                RLwebviewurlload("distance")
                fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatCommas(carddate.maxdistance.toDouble()))
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX DISTANCE (miles)",RLTools.RLformatCommas(carddate.maxdistance.toDouble()),R.drawable.ic_distance))
                        1-> dataList.add(RLSessionitemset("AVG DISTANCE (miles)",RLTools.RLformatCommas(carddate.avgdistance.toDouble()),R.drawable.ic_distance))
                        2-> dataList.add(RLSessionitemset("CLIMBED (feet)",RLTools.RLformatCommas(carddate.elevation.toDouble()),R.drawable.ic_climb))
                        3-> dataList.add(RLSessionitemset("AVG EFFORT PER (miles)",RLTools.RLminutesget(carddate.totalrmm.toInt()).toString(),R.drawable.ic_heart))
                    }
                }
            }
            "CLIMBED" ->{
                RLwebviewurlload("climbed")
                fragBinding.txtTotalsessionNumber.setText(carddate.maxelevation.toString())
                totaldisplayitem=4
                for (i in 0 until  totaldisplayitem){
                    when (i){
                        0-> dataList.add(RLSessionitemset("MAX CLIMBED (ft)",carddate.elevation.toString(),R.drawable.ic_climb))
                        1-> dataList.add(RLSessionitemset("AVG CLIMBED (ft)",carddate.avgelevation.toString(),R.drawable.ic_climb))
                        2-> dataList.add(RLSessionitemset("DISTANCE (miles)",RLTools.RLformatCommas(carddate.maxdistance.toDouble()),R.drawable.ic_distance))
                        3-> dataList.add(RLSessionitemset("AVG EFFORT PER (ft)",carddate.elevation.toString(),R.drawable.ic_heart))
                    }
                }
            }
        }
        adapterdata.RLsetList(dataList,isTextColorSetWhite)
    }
    fun RLwebviewurlload(type:String){
        val imageUrl=RLConstants.BASE_URL+"getResponse_v2.php?q=overviewGraphChartHTML&RLuser=w2p8SQCvE3emjEEDo66f02eF6fG2&classtype=all&graphtimefrom=1711929600&graphtimeto=1714521600&timerange=this_month&gmtdiff=%2D0&type="+type
        fragBinding.webView.loadUrl(imageUrl)
    }
    fun RLallactivitydialogopen() {
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
    fun RLSelectBodyActivityDialogOpen() {
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

}