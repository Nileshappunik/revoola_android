package com.example.myfirstapp.fragment.overview

import android.app.Dialog
import android.content.pm.ActivityInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionListAdapter
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragOverviewBinding
import com.example.myfirstapp.databinding.RlFragOverviewSessionsBinding
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.model.RLOverviewGraphResponseDataCard
import com.example.myfirstapp.model.RLSessionitemset
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory


class RLFragOverviewSession : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragOverviewSession::class.java.simpleName
    lateinit var fragBinding: RlFragOverviewSessionsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    val valueslist = arrayOf("SESSIONS","EFFORT","RELAXATION","CALORIES","STEPS","DISTANCE","CLIMBED" )
    var totaldisplayitem=9
    private lateinit var adapterdata: RLOverviewSessionListAdapter
    private val binding by lazy {
        RlFragOverviewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //activity?.window!!.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Dark icons
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview_sessions, container) as RlFragOverviewSessionsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragOverviewSession" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),
            RLMainViewModelFactory(
                userRepository
            )
        ).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }
    fun  RLuisetup(){
        RLonBackPresAct(fragBinding.ivBack)
        //do
       val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.recycleSessionTitle.layoutManager = linearLayoutManager
        val adaptertitle = RLOverviewSessionTitleListAdapter("SESSIONS",this,valueslist,activity)
        fragBinding.recycleSessionTitle.adapter = adaptertitle
        //DATA SET
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
         adapterdata = RLOverviewSessionListAdapter(activity)
        fragBinding.recycleSession.adapter = adapterdata

        RLTools.RLheightsetdisplaywebview(fragBinding.webView,activity)
        fragBinding.webView.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall("SESSIONS")
        } else {
            RLshowDialogFullscreen()
        }
    }
    fun RLthismonthdialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_thismonth)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)

        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    fun RLallactivitydialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_allactivity)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)

        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    override fun onItemClick(position: Int) {
        fragBinding.txtTotalsession.setText(valueslist[position])
        fragBinding.ivTitle.setText(valueslist[position])
        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLAPiCall(valueslist[position])
        } else {
            RLshowDialogFullscreen()
        }
    }
    private fun RLAPiCall(valuetype:String) {
        viewModel.RLgetOverviewGraph("overviewGraph",currentUser,1714521600,1716812869,"all") { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLhendleApiResponse(response.text[0].overviewGraph[0],valuetype)
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
    private fun RLhendleApiResponse(carddate: RLOverviewGraphResponseDataCard, valuetype:String) {
        val datalist= mutableListOf<RLSessionitemset>()
        if (valuetype.equals("SESSIONS")){
            RLwebviewurlload("sessions")

            fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatNumberWithCommas(carddate.session.toDouble()))
            totaldisplayitem=10
            val awards=carddate.medals_gold+carddate.medals_silver+carddate.medals_bronze
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("LONGEST SESSION",RLTools.RLminutesget(carddate.max_time.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                    1-> datalist.add(RLSessionitemset("AVG SESSION",RLTools.RLminutesget(carddate.avgtime.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                    2-> datalist.add(RLSessionitemset("EFFORT",RLTools.RLformatNumberWithCommas(carddate.totalREV.toDouble()),R.drawable.ic_heart))
                    3-> datalist.add(RLSessionitemset("RELAXATION",carddate.totalrms.toString(),R.drawable.ic_mind_read))
                    4-> datalist.add(RLSessionitemset("TOTAL CALORIES",RLTools.RLformatNumberWithCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                    5-> datalist.add(RLSessionitemset("ACTIVE CALORIES","0",R.drawable.fd_calories_green))
                    6-> datalist.add(RLSessionitemset("DISTANCE(miles)",carddate.distance.toString(),R.drawable.ic_distance))
                    7-> datalist.add(RLSessionitemset("STEPS",RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    8-> datalist.add(RLSessionitemset("CLIMBED(feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                    9-> datalist.add(RLSessionitemset("AWARDS",awards.toString(),R.drawable.ic_award))
                }
            }

        }
        else if (valuetype.equals("CALORIES")){
            RLwebviewurlload("calories")
            fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatNumberWithCommas(carddate.burntcalories.toDouble()))
            totaldisplayitem=6
            val DAILYAVGTOTALCALORIES = if (carddate.countSessionBody != 0) {
                carddate.burntcalories / carddate.countSessionBody
            } else {
                0 // or some default value if countSessionBody is zero
            }
         // val   DAILYAVGTOTALCALORIES=carddate.burntcalories/carddate.countSessionBody
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("MAX CALORIES",RLTools.RLformatNumberWithCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                    1-> datalist.add(RLSessionitemset("AVG CALORIES","0",R.drawable.fd_calories_green))
                    2-> datalist.add(RLSessionitemset("MAX DAILY TOTAL",RLTools.RLformatNumberWithCommas(carddate.maxcalories.toDouble()),R.drawable.ic_calendar_today))
                    3-> datalist.add(RLSessionitemset("MAX DAILY ACTIVE","0",R.drawable.ic_calendar_today))
                    4-> datalist.add(RLSessionitemset("AVG DAILY TOTAL",RLTools.RLformatNumberWithCommas(DAILYAVGTOTALCALORIES.toDouble()),R.drawable.ic_calendar_today))
                    5-> datalist.add(RLSessionitemset("AVG DAILY ACTIVE","0",R.drawable.ic_calendar_today))
                }
            }

        }
        else if (valuetype.equals("RELAXATION")){
            RLwebviewurlload("relaxation")
            fragBinding.txtTotalsessionNumber.setText(RLTools.RLminutesget(carddate.totalrmm.toInt())+"m".toString())
            totaldisplayitem=4
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("LONGEST SESSION",RLTools.RLminutesget(carddate.max_time.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                    1-> datalist.add(RLSessionitemset("AVG SESSION",RLTools.RLminutesget(carddate.avgtime.toInt())+"m".toString(),R.drawable.fd_active_time_green))
                    2-> datalist.add(RLSessionitemset("MAX RELAXATION","0",R.drawable.ic_mind_read))
                    3-> datalist.add(RLSessionitemset("AVG RELAXATION","0",R.drawable.ic_mind_read))
                }
            }
        }
        else if (valuetype.equals("EFFORT")){
            RLwebviewurlload("effort")
            totaldisplayitem=8
            //val avarageeffort=carddate.totalREV/carddate.countSessionBody
                val avarageeffort = if (carddate.countSessionBody != 0) {
                    carddate.totalREV / carddate.countSessionBody
                } else {
                    0 // or some default value if countSessionBody is zero
                }
            fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatNumberWithCommas(carddate.totalREV.toDouble()))
            val awards=carddate.medals_gold+carddate.medals_silver+carddate.medals_bronze
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("MAX EFFORT",carddate.maxREV.toString(),R.drawable.ic_heart))
                    1-> datalist.add(RLSessionitemset("AVG EFFORT",avarageeffort.toString(),R.drawable.ic_heart))
                    2-> datalist.add(RLSessionitemset("TOTAL CALORIES",RLTools.RLformatNumberWithCommas(carddate.burntcalories.toDouble()),R.drawable.fd_calories_green))
                    3-> datalist.add(RLSessionitemset("ACTIVE CALORIES","0",R.drawable.fd_calories_green))
                    4-> datalist.add(RLSessionitemset("DISTANCE(miles)",carddate.distance.toString(),R.drawable.ic_distance))
                    5-> datalist.add(RLSessionitemset("STEPS",RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    6-> datalist.add(RLSessionitemset("CLIMBED(feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                    7-> datalist.add(RLSessionitemset("AWARDS",awards.toString(),R.drawable.ic_award))
                }
            }
        }
        else if (valuetype.equals("STEPS")){
            RLwebviewurlload("steps")
            fragBinding.txtTotalsessionNumber.setText(RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()))
            totaldisplayitem=4
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("MAX STEPS",RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    1-> datalist.add(RLSessionitemset("AVG STEPS",RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    2-> datalist.add(RLSessionitemset("MAX DAILY STEPS",RLTools.RLformatNumberWithCommas(carddate.steps.toDouble()),R.drawable.fd_steps_green))
                    3-> datalist.add(RLSessionitemset("AVG DAILY STEPS",RLTools.RLformatNumberWithCommas(carddate.avgsteps.toDouble()),R.drawable.fd_steps_green))
                }
            }
        }
        else if (valuetype.equals("DISTANCE")){
            RLwebviewurlload("distance")
            fragBinding.txtTotalsessionNumber.setText(carddate.maxdistance.toString())
            totaldisplayitem=4
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("MAX DISTANCE(miles)",carddate.maxdistance.toString(),R.drawable.ic_distance))
                    1-> datalist.add(RLSessionitemset("AVG DISTANCE(miles)",carddate.avgdistance.toString(),R.drawable.ic_distance))
                    2-> datalist.add(RLSessionitemset("CLIMBED(feet)",carddate.elevation.toString(),R.drawable.ic_climb))
                    3-> datalist.add(RLSessionitemset("AVG EFFORT PER",RLTools.RLminutesget(carddate.totalrmm.toInt()).toString(),R.drawable.ic_heart))
                }
            }
        }
        else if (valuetype.equals("CLIMBED")){
            RLwebviewurlload("climbed")
            fragBinding.txtTotalsessionNumber.setText(carddate.maxelevation.toString())
            totaldisplayitem=4
            for (i in 0 until  totaldisplayitem){
                when (i){
                    0-> datalist.add(RLSessionitemset("MAX CLIMBED(ft)",carddate.elevation.toString(),R.drawable.ic_climb))
                    1-> datalist.add(RLSessionitemset("AVG CLIMBED(ft)",carddate.avgelevation.toString(),R.drawable.ic_climb))
                    2-> datalist.add(RLSessionitemset("DISTANCE(miles)",carddate.maxdistance.toString(),R.drawable.ic_distance))
                    3-> datalist.add(RLSessionitemset("AVG EFFORT PER",carddate.elevation.toString(),R.drawable.ic_heart))
                }
            }
        }
        adapterdata.RLsetList(datalist)
    }
    fun RLwebviewurlload(type:String){
        val imageUrl=RLConstants.BASE_URL+"getResponse_v2.php?q=overviewGraphChartHTML&RLuser=w2p8SQCvE3emjEEDo66f02eF6fG2&classtype=all&graphtimefrom=1711929600&graphtimeto=1714521600&timerange=this_month&gmtdiff=%2D0&type="+type
        fragBinding.webView.loadUrl(imageUrl)
    }
}