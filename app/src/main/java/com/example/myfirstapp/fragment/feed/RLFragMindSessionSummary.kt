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
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragMindSessionSummaryBinding
import com.example.myfirstapp.databinding.RlFragSessionSummaryBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfMetrics
import com.example.myfirstapp.enumclass.RLYourWayName
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

class RLFragMindSessionSummary : RLBaseFragment() {
    val TAG: String = RLFragMindSessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragMindSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val binding by lazy {
        RlFragMindSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_session_summary, container) as RlFragMindSessionSummaryBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindSessionSummary" )
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
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        RLsummaryDataSet()
    }

    private fun RLsummaryDataSet() {
        //Image Set
        var classType=""
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(fragBinding.testImage)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(fragBinding.testImage)
        }else{
            Glide.with(requireContext()).load(RLTools.RLgetImage(classType)).into(fragBinding.testImage)
        }

        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        val dataListWithHR:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.MindfulMinutes to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.Relaxation to RLMetricData(RLTools.RLformatCommas(cardData.totalRMS.toDouble()).toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))

        val dataListWithoutHR:List<Pair<RLTypeOfMetrics, RLMetricData>> =listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString()))

        if (cardData.hrm==0) {
            //WITHOUT HR
            RLsummaryListDataSet(dataListWithoutHR)
        }else{
            //WITH HR
            RLsummaryListDataSet(dataListWithHR)
        }


    }

    private fun RLsummaryListDataSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity,dataList)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }


}