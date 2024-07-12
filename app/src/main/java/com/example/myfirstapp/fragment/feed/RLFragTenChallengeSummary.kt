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
import com.example.myfirstapp.model.RLSetgoaled_challengesSingle
import com.example.myfirstapp.model.RLSetgoaled_challenges_request
import com.example.myfirstapp.model.RLSetgoaled_challenges_request_single
import com.example.myfirstapp.model.RLTextOverview
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

class RLFragTenChallengeSummary : RLBaseFragment() {
    val TAG: String = RLFragTenChallengeSummary::class.java.simpleName
    lateinit var fragBinding: RlFragChallengeSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val binding by lazy {
        RlFragChallengeSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragTenChallengeSummary()
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
        fragBinding.imgMyride.setImageResource(RLTools.RLgeticon(classType))
        

        fragBinding.layStepssofar.imgTime.setImageResource(R.drawable.ic_calender_daily)
        fragBinding.layStepssofar.txtTime.setText("CHALLENGE PERIOD")


        if (classType.toLowerCase().equals("challenge-effort")){
            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targeteffort)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_heart)
            fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType.toLowerCase().equals("challenge-steps")){

            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targetsteps)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_steps_green)
            fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType!!.toLowerCase().equals("challenge-calories")){
            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targetcalories)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_calories_green)
            fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-distance")){
            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targetdistance)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_distance)
            fragBinding.layDaysremaining.txtTime.setText(R.string.distance)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-climbed")){
            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targetclimbed)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_climb)
            fragBinding.layDaysremaining.txtTime.setText(R.string.distance)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-duration")){
            fragBinding.layTargetsteps.imgTime.setImageResource(R.drawable.ic_goal)
            fragBinding.layTargetsteps.txtTime.setText(R.string.targettotalduration)
            fragBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            fragBinding.layDaysremaining.imgTime.setImageResource(R.drawable.fd_active_time_green)
            fragBinding.layDaysremaining.txtTime.setText(R.string.youachived)
            fragBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }

        fragBinding.layRank.txtTime.setText(R.string.rank)
        fragBinding.layRank.imgTime.setImageResource(R.drawable.ic_ranking)
        fragBinding.layRank.txtTimeNumber.setText(cardData.hrm.toString()+ " of " +cardData.share_map.toString())


      /*  var stepsSoFar = if (cardData.actualtotal ?: 0 > 0) cardData.actualtotal ?: 0 else 0
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
*/
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

        RLRankingDataGetApi(cardData.mainTitle)
        
    }
    private fun RLRankingDataGetApi(challengeid:String){
        val jasonArray=JSONArray()
        val request = listOf(RLSetgoaled_challenges_request_single(goaled_challenges = RLSetgoaled_challengesSingle(
                    id = challengeid,type = "historic_challenges")))
        Log.d(TAG,"setgoaled_challenges_single= "+request)
        viewModel.RLgoaled_challenges_Single(request) { result ->
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
                            rank=response.text.data[i].length_of_challenge
                        }
                        fragBinding.layStepssofar.txtTimeNumber.setText("${rank.toString()} Days")
                        RLRankingMapSet(jasonArray)
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
 

}
