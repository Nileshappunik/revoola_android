package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragChallengeSummaryBinding
import com.revoola.model.RLFeedChallengesModelData
import com.revoola.model.RLSetMetricChartByDay
import com.revoola.model.RLSetMetricChartByDayData
import com.revoola.model.RLSetgoaled_challenges
import com.revoola.model.RLSetgoaled_challenges_request
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
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
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenge_summary, container) as RlFragChallengeSummaryBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengeSummary" )
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
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivTitle.setText(R.string.challengesummery)


        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLFeedChallengesModelData

        RLTools.RlLogDPrint(TAG,"ChSummeryCard: ${Gson().toJson(cardData)}")

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

        fragBinding.layTargetsteps.imgTime.setImageResource(RLTools.RLgeticon(cardData.metric))
        fragBinding.layTargetsteps.txtTime.setText("ACHIEVED SO FAR")
        fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.actualtotal.toDouble()))

        fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_goal)
        fragBinding.layDaysremaining.txtTime.setText(cardData.targettype.uppercase()+" TARGET")
        fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totaltarget.toDouble()))

        fragBinding.layRank.imgTime.setImageResource(R.drawable.ic_ranking)
        fragBinding.layRank.txtTime.setText("RANK")
        fragBinding.layRank.txtTimeNumber.setText("${cardData.ranking_by_challenge.toString()} OF ${cardData.participants.toString()}")


        val stepsSoFar = if (cardData.actualtotal.toInt() ?: 0 > 0) cardData.actualtotal ?: 0 else 0
        val targetSteps = if (cardData.totaltarget ?: 0 > 0) cardData.totaltarget ?: 0 else 0

        val remainingDays = cardData.days_remaining ?: 0
        val timeGone = if (remainingDays >0) remainingDays else 0
        val totalTime = if (cardData?.totaldays ?: 0 > 0) cardData?.totaldays ?: 0 else 0

        // green and Blue vertical line chart
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
            RLAllHTMLChart.RLgetChallengeChartHtml(stepsSoFar.toInt(),targetSteps,timeGone,totalTime,RLTools.RLGetMetricsName(cardData.metric)), "text/html", "UTF-8", null)

        //user name Image and persentage chart  Ranking Chart
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
        val htmltext=RLAllHTMLChart.RLgetRankingChartHtml(jasonArray,currentUser)
        fragBinding.webViewRankingChart.loadDataWithBaseURL(null,htmltext, "text/html", "UTF-8", null)

        //Step Chart
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
        val htmlText=RLAllHTMLChart.RLgetIndividualStepsChartHtml(jasonArray)
        fragBinding.webViewStepChart.loadDataWithBaseURL(null,htmlText, "text/html", "UTF-8", null)

        //both Api call Chart and Ranking
        RLRankingDataGetApi(cardData.challengeid)
        RLStepDataGetApi(cardData.challengeid,cardData.userid)
        //Last Chart Name
        fragBinding.txtDailystep.setText("MY DAILY ${RLTools.RLGetMetricsName(cardData.metric)}")

    }


    private fun RLRankingDataGetApi(challengeid:String){
        val scenario = cardData.scenario
        val metric = if (cardData.metric == "climbed") "elevation" else cardData.metric

       val  scenarioType = when (scenario) {
            "group_group" -> "group_group_$metric"
            "group" -> "group_$metric"
            "user" -> "users_$metric"
            else -> ""
        }

        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(RLSetgoaled_challenges_request(
            goaled_challenges = RLSetgoaled_challenges(
                    id = challengeid,
                    type = scenarioType,//"users_steps",
                    today = currentTimestamp)))

        RLTools.RlLogDPrint(TAG,"setgoaled_challenges= "+request)

        viewModel.RLgoaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        var rank=0
                        val jsonArray = JSONArray().apply {
                            response.text.data.map { data ->
                                JSONObject().apply {
                                    val intValue = data.percentage_of_goal_display.toDouble().roundToInt()
                                    put("value", intValue)
                                    put("name", data.username)
                                    put("number2", data.totalmetric)
                                    put("image", data.avatar)
                                    put("userid", data.userid)
                                    // Update rank if current user
                                    if(currentUser.equals(data.userid)){
                                        rank=data.ranking_by_challenge
                                    }
                                }
                            }.forEach { put(it) }
                        }
                        RLTools.RlLogDPrint(TAG,"RankResponse: $jsonArray")
                        RLRankingMapSet(jsonArray)
                        fragBinding.layRank.txtTimeNumber.setText("${rank.toString()} OF ${jsonArray.length()}")
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }

    }
    private fun RLStepDataGetApi(challengeid:String,userid:String){
        val request = listOf(
            RLSetMetricChartByDay(metric_chart_by_day = RLSetMetricChartByDayData(
                userid = userid,challengeid =challengeid )
            )
        )
        RLTools.RlLogDPrint(TAG,"SetMetricChartByDay= "+request)
        viewModel.RLMetricChartByDay(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        val jsonArray = JSONArray().apply {
                            response.text.data.map { data ->
                                JSONObject().apply {
                                    put("value", data.metric)
                                    put("date", data.datefield)

                                }
                            }.forEach { put(it) }
                        }
                        RLStepMapSet(jsonArray)

                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLRankingMapSet(jasonArray: JSONArray){
        if (jasonArray.length()>0){
            fragBinding.webViewRankingChart.visibility=View.VISIBLE
            val htmltext=RLAllHTMLChart.RLgetRankingChartHtml(jasonArray,currentUser)
            fragBinding.webViewRankingChart.loadDataWithBaseURL(null,
                htmltext, "text/html", "UTF-8", null)
        }else{
            fragBinding.webViewRankingChart.visibility=View.GONE
        }

    }
    private fun RLStepMapSet(jasonArray: JSONArray) {
        val htmlText=RLAllHTMLChart.RLgetIndividualStepsChartHtml(jasonArray)
        fragBinding.webViewStepChart.loadDataWithBaseURL(null,htmlText, "text/html", "UTF-8", null)

    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }


}
