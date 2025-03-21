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
import com.revoola.R
import com.revoola.RLBaseFragment
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragChallengeSummaryBinding
import com.revoola.model.RLFeedChallengesModelData
import com.revoola.model.RLSetgoaled_challengesSingle
import com.revoola.model.RLSetgoaled_challenges_request_single
import com.revoola.model.RLTextOverview
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.enumclass.RLValueName
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class RLFragTenChallengeSummary : RLBaseFragment() {
    val TAG: String = RLFragTenChallengeSummary::class.java.simpleName
    lateinit var fragBinding: RlFragChallengeSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var appUnit:String="Metric"

    private val binding by lazy {
        RlFragChallengeSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragTenChallengeSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenge_summary, container) as RlFragChallengeSummaryBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragChallengeSummary" )
        currentUser=  com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),
            RLMainViewModelFactory(
                userRepository
            )
        ).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivTitle.setText(R.string.challengesummery)
        RLDatabaseManagerRead().RlAppUnitRead { data, error ->
            if (data != null) {
             appUnit = data.toString()
            }
        }

        var classType=""
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        fragBinding.txtDailystep.visibility=View.GONE
        fragBinding.webViewStepChart.visibility=View.GONE
        fragBinding.txtMyride.setText(cardData.className.toString())

        fragBinding.layStepssofar.imgTime.setImageResource(R.drawable.ic_calender_daily)
        fragBinding.layStepssofar.txtTime.setText("CHALLENGE PERIOD")

        when(RLTools.RLChallengesTypeGet(classType.toLowerCase())){
            "effort"->{
                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targeteffort)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.ic_heart)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_heart)
                fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Effort,cardData))

            }
            "steps"->{

                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targetsteps)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.fd_steps_green)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_steps_green)
                fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Steps,cardData))

            }
            "calories"->{
                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targetcalories)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.fd_calories_green)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_calories_green)
                fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

            }
            "distance"->{
                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targetdistance)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.ic_distance)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_distance)
                fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

            }
            "climbed"->{
                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targetclimbed)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.ic_climb)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_climb)
                fragBinding.layDaysremaining.txtTime.setText(R.string.distance)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

            }
            "duration"->{
                fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
                fragBinding.layTargetsteps.txtTime.setText(R.string.targettotalduration)
                fragBinding.layTargetsteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Goal,cardData))

                fragBinding.imgMyride.setImageResource(R.drawable.fd_active_time_green)

                fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_active_time_green)
                fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
                fragBinding.layDaysremaining.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Steps,cardData))

            }
        }

        fragBinding.layRank.txtTime.setText(R.string.rank)
        fragBinding.layRank.imgTime.setImageResource(R.drawable.ic_ranking)
        fragBinding.layRank.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Rank,cardData))

      /*var stepsSoFar = if (cardData.actualtotal ?: 0 > 0) cardData.actualtotal ?: 0 else 0
        var targetSteps = if (cardData.totaltarget ?: 0 > 0) cardData.totaltarget ?: 0 else 0

        var remainingDays = cardData.days_remaining ?: 0
        var timeGone = if (remainingDays >0) remainingDays else 0
        var totalTime = if (cardData?.totaldays ?: 0 > 0) cardData?.totaldays ?: 0 else 0 */

        val stepsSoFar = 3364
        val targetSteps = 3333
        val timeGone = 0
        val totalTime = 1

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
            RLAllHTMLChart.RLgetChallengeChartHtml(stepsSoFar,targetSteps,timeGone,totalTime,RLTools.RLGetMetricsName(RLTools.RLGetClassTypeValue(cardData.classType))), "text/html", "UTF-8", null)

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
        RLRankingDataGetApi(cardData.mainTitle)

    }

    private fun RLGetValueForTitle(title: String,cardData:RLTextOverview): String {
        val isImperial = RLTools.RLGetIsImperial(appUnit)

        return when (title) {
            RLValueName.TotalTime -> RLTools.RLdaytimeget(convertToInt(cardData.totalTime))
            RLValueName.Effort  -> RLTools.RLformatCommasInt(convertToInt(cardData.totalREV))
            RLValueName.Boosts  -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
            RLValueName.Comments  -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
            RLValueName.Awards  -> {
                val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                if (totalAwards != 0) totalAwards.toString() else "0"
            }
            RLValueName.Steps  -> RLTools.RLformatCommasInt(convertToInt(cardData.steps))
            RLValueName.Distance  -> if (!isImperial) RLTools.RLformatCommas(cardData.distance?:0.0) else RLTools.RLformatCommas(cardData.distance * 0.621371)
            RLValueName.Climbed  -> {
                val demsElevation:Int = convertToInt(cardData.elevation?:-1)
                val elevation = if (!isImperial) {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                } else {
                    if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084))
                }
                elevation.toString()
            }
            RLValueName.AvgEffort -> convertToInt(cardData.avgRevPercentage).toString()+"%"
            RLValueName.MaxEffort -> convertToInt(cardData.maxRevPercentage).toString()+"%"
            RLValueName.ActiveCalories -> convertToInt(cardData.burntCalories).toString()
            RLValueName.AssumedRelaxation -> RLTools.RLformatCommasInt(convertToInt(cardData.totalRMS))
            RLValueName.Rank ->cardData.hrm.toString()+ " of " +cardData.share_map.toString()
            RLValueName.Goal ->RLTools.RLformatCommasInt(convertToInt(cardData.goal))
            else -> "0"
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
    private fun RLRankingDataGetApi(challengeid:String){
        val jasonArray=JSONArray()
        val request = listOf(
            RLSetgoaled_challenges_request_single(goaled_challenges = RLSetgoaled_challengesSingle(
                    id = challengeid,type = "historic_challenges")
            )
        )
        RLTools.RlLogDPrint(TAG,"setgoaled_challenges_single= "+request)
        viewModel.RLgoaled_challenges_Single(request) { result ->
            result.onSuccess { response ->
                try {
                    var selfUserData=""
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        var rank=0
                        for ( i in 0 until response.text.data.size){
                            val jsonObject=JSONObject()
                            val intValue = response.text.data[i].percentage_of_goal_display.toDouble().roundToInt()
                            jsonObject.put("value",intValue)
                            jsonObject.put("name","${i + 1} - ${response.text.data[i].username}")
                            jsonObject.put("number2",response.text.data[i].totalmetric)
                            jsonObject.put("image",response.text.data[i].avatar)
                            jsonObject.put("userid",response.text.data[i].userid)
                            jasonArray.put(jsonObject)
                           // RLTools.RlLogDPrint(TAG,"jsonObject= $jsonObject")
                            rank=response.text.data[i].length_of_challenge.toString().toDouble().roundToInt()
                            if(response.text.data[i].userid == cardData.userid) {
                                selfUserData = cardData.userid
                            }
                        }
                        if (response.text.data.size>0){
                            val myChallengeData=response.text.data[0]
                            RLStepTimeMapSet(myChallengeData)
                        }
                        fragBinding.layStepssofar.txtTimeNumber.setText("${rank.toString()} Days")
                        RLRankingMapSet(jasonArray,selfUserData)
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
    private fun RLRankingMapSet(jasonArray: JSONArray,selfUserData:String){
        val htmltext=RLAllHTMLChart.RLgetRankingChartHtml(jasonArray,selfUserData)
       // RLTools.RlLogDPrint(TAG,"htmltext:-   $htmltext ")
        fragBinding.webViewRankingChart.loadDataWithBaseURL(null,
            htmltext, "text/html", "UTF-8", null)
    }
    private fun RLStepTimeMapSet(myChallengeData: RLFeedChallengesModelData) {
        val stepsSoFar:Int = if (myChallengeData.totalmetric != null && myChallengeData.totalmetric > 0) myChallengeData.totalmetric else 0
        val targetSteps = if (myChallengeData.goalvalue != null && myChallengeData.goalvalue > 0) myChallengeData.goalvalue else 0

       RLTools.RlLogEPrint(TAG,"stepsSoFar:- $stepsSoFar ")
       RLTools.RlLogEPrint(TAG,"targetSteps:- $targetSteps ")

        val htmltext=RLAllHTMLChart.RLgetChallengeSessionChartHtml(stepsSoFar,targetSteps)
        // RLTools.RlLogDPrint(TAG,"htmltext:-   $htmltext ")
        fragBinding.webViewChart.loadDataWithBaseURL(null,
            htmltext , "text/html", "UTF-8", null)

    }

    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }

}
