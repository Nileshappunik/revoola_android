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
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragChallengeSummaryBinding
import com.example.myfirstapp.model.RLFeedChallengesModelData
import com.example.myfirstapp.model.RLSetMetricChartByDay
import com.example.myfirstapp.model.RLSetMetricChartByDayData
import com.example.myfirstapp.model.RLSetgoaled_challenges
import com.example.myfirstapp.model.RLSetgoaled_challenges_request
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

class RLFragChallengeSummary : RLBaseFragment() {
    val TAG: String = RLFragChallengeSummary::class.java.simpleName
    lateinit var fragBinding: RlFragChallengeSummaryBinding
    lateinit var cardData: RLFeedChallengesModelData
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val binding by lazy {
        RlFragChallengeSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengeSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenge_summary, container) as RlFragChallengeSummaryBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengeSummary" )
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
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLFeedChallengesModelData

        fragBinding.imgMyride.setImageResource(RLTools.RLgeticon(cardData.metric))
        fragBinding.txtMyride.setText(cardData.challenge_name.toUpperCase().toString())

        fragBinding.layStepssofar.imgTime.setImageResource(R.drawable.ic_calender_daily)
        fragBinding.layStepssofar.txtTime.setText("CHALLENGE PERIOD")
        if (cardData.days_remaining>0){
            val daysremain=cardData.totaldays-cardData.days_remaining
            fragBinding.layStepssofar.txtTimeNumber.setText(daysremain.toString()+" of "+cardData.totaldays.toString()+" Days")
        }else{
            fragBinding.layStepssofar.txtTimeNumber.setText(cardData.totaldays.toString()+" of "+cardData.totaldays.toString()+" Days")
        }

        fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.fd_steps_green)
        fragBinding.layTargetsteps.txtTime.setText("ACHIEVED SO FAR")
        fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.actualtotal.toDouble()))

        fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_goal)
        fragBinding.layDaysremaining.txtTime.setText(cardData.targettype.uppercase()+" TARGET")
        fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totaltarget.toDouble()))

        fragBinding.layRank.imgTime.setImageResource(R.drawable.ic_ranking)
        fragBinding.layRank.txtTime.setText("RANK")


        var stepsSoFar = if (cardData.actualtotal ?: 0 > 0) cardData.actualtotal ?: 0 else 0
        var targetSteps = if (cardData.totaltarget ?: 0 > 0) cardData.totaltarget ?: 0 else 0

        var remainingDays = cardData.days_remaining ?: 0
        var timeGone = if (remainingDays >0) remainingDays else 0
        var totalTime = if (cardData?.totaldays ?: 0 > 0) cardData?.totaldays ?: 0 else 0

        val webSettings: WebSettings = fragBinding.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.webViewChart.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
        fragBinding.webViewChart.isHorizontalScrollBarEnabled = false
        fragBinding.webViewChart.isVerticalScrollBarEnabled = false
        fragBinding.webViewChart.webViewClient = WebViewClient()
        fragBinding.webViewChart.loadDataWithBaseURL(null,
            RLTools.RLgetChallengeChartHtml(stepsSoFar,targetSteps,timeGone,totalTime), "text/html", "UTF-8", null)

        val webRankingSettings: WebSettings = fragBinding.webViewRankingChart.settings
        webRankingSettings.javaScriptEnabled = true
        webRankingSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webRankingSettings.domStorageEnabled = true
        webRankingSettings.useWideViewPort = true
        webRankingSettings.loadWithOverviewMode = true
        fragBinding.webViewRankingChart.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
        fragBinding.webViewRankingChart.isHorizontalScrollBarEnabled = false
        fragBinding.webViewRankingChart.isVerticalScrollBarEnabled = false
        fragBinding.webViewRankingChart.webViewClient = WebViewClient()
        val jasonArray=JSONArray()
        val htmltext=RLTools.RLgetRankingChartHtml(jasonArray,currentUser)
        fragBinding.webViewRankingChart.loadDataWithBaseURL(null,htmltext, "text/html", "UTF-8", null)

        RLRankingDataGetApi(cardData.challengeid)
        RLStepDataGetApi(cardData.challengeid,cardData.userid)
    }
    private fun RLRankingDataGetApi(challengeid:String){
        val jasonArray=JSONArray()
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(RLSetgoaled_challenges_request(goaled_challenges = RLSetgoaled_challenges(
                    id = challengeid,type = "users_steps", today = currentTimestamp)))
        Log.d(TAG,"setgoaled_challenges= "+request)
        viewModel.RLgoaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        var rank=0
                        for ( i in 0 until response.text.data.size){
                            val jsonObject=JSONObject()
                            val intValue = response.text.data[i].percentage_of_goal_display.toDouble().roundToInt()
                            jsonObject.put("value",intValue)
                            jsonObject.put("name",response.text.data[i].username)
                            jsonObject.put("number2",response.text.data[i].totalmetric)
                            jsonObject.put("image",response.text.data[i].avatar)
                            jsonObject.put("userid",response.text.data[i].userid)
                            jasonArray.put(jsonObject)
                            if(currentUser.equals(response.text.data[i].userid)){
                                rank=response.text.data[i].ranking_by_challenge
                            }
                        }
                        RLRankingMapSet(jasonArray)
                        fragBinding.layRank.txtTimeNumber.setText("${rank.toString()} OF ${jasonArray.length()}")
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }

    }

    private fun RLStepDataGetApi(challengeid:String,userid:String){
        val jasonArray=JSONArray()
        val request = listOf(RLSetMetricChartByDay(metric_chart_by_day = RLSetMetricChartByDayData(
                userid = userid,challengeid =challengeid )))
        Log.d(TAG,"SetMetricChartByDay= "+request)
        viewModel.RLMetricChartByDay(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        for ( i in 0 until response.text.data.size){
                            val jsonObject=JSONObject()
                            val outputFormatter = SimpleDateFormat("dd/MM/yyyy")
                            val inputFormatter = SimpleDateFormat("yyyy-MM-dd")
                            val date: Date = inputFormatter.parse(response.text.data[i].datefield)
                            val formattedDate: String = outputFormatter.format(date)

                            jsonObject.put("date",formattedDate)
                            jsonObject.put("value",response.text.data[i].metric)
                            jasonArray.put(jsonObject)
                        }
                        RLStepMapSet(jasonArray)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }

    private fun RLRankingMapSet(jasonArray: JSONArray){
        Log.e(TAG,"RLRankingMapSet")

        val htmltext=RLTools.RLgetRankingChartHtml(jasonArray,currentUser)
        Log.d(TAG,"MAp:- $htmltext")
        fragBinding.webViewRankingChart.loadDataWithBaseURL(null,
            htmltext, "text/html", "UTF-8", null)

    }
    private fun RLStepMapSet(jasonArray: JSONArray) {
        val webSettingsSteps: WebSettings = fragBinding.webViewStepChart.settings
        webSettingsSteps.javaScriptEnabled = true
        webSettingsSteps.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettingsSteps.domStorageEnabled = true
        webSettingsSteps.useWideViewPort = true
        webSettingsSteps.loadWithOverviewMode = true
        fragBinding.webViewStepChart.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
        fragBinding.webViewStepChart.isHorizontalScrollBarEnabled = false
        fragBinding.webViewStepChart.isVerticalScrollBarEnabled = false
        fragBinding.webViewStepChart.webViewClient = WebViewClient()
        val htmltext=RLTools.RLgetIndividualStepsChartHtml(jasonArray)
        fragBinding.webViewStepChart.loadDataWithBaseURL(null,
            htmltext, "text/html", "UTF-8", null)

    }

}
