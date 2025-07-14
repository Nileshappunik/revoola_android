package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragMindSessionSummaryBinding
import com.revoola.enumclass.RLMetricData
import com.revoola.enumclass.RLTypeOfMetrics
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragMindSessionSummary : RLBaseFragment() {
    val TAG: String = RLFragMindSessionSummary::class.java.simpleName
   // lateinit var fragBinding: RlFragMindSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var  selectTag:String=""

    private val fragBinding by lazy {
        RlFragMindSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
       // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_session_summary, container) as RlFragMindSessionSummaryBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindSessionSummary" )
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
        fragBinding.ivBack.setOnClickListener {
            rl_closeScreen(isSessionComplete)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                rl_closeScreen(isSessionComplete)
            }
        })

        fragBinding.ivTitle.setText(getString(R.string.sessionsummerys))

        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        RLTools.rl_logLarge(TAG,"Card Data: ${Gson().toJson(cardData)}")
        rl_summaryDataSet()
    }

    private fun rl_closeScreen(isSessionComplete:Boolean){
        if (isSessionComplete){
            rl_bottomHideShowSet(true)
            (context as RLMainActivityRL).rl_bottombarcolorDarkBlue()
            (context as RLMainActivityRL).rl_loadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }else{
            rl_closeFragment()
        }
    }

    private fun rl_summaryDataSet() {
        //Image Set
        Glide.with(requireContext()).load(RLTools.rl_feedSetImage(cardData,currentUser,selectTag)).into(fragBinding.testImage)

        val totalAward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        val dataListWithoutHR:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.MindfulMinutes to RLMetricData(RLTools.rl_formatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.AssumeRelaxation to RLMetricData(RLTools.rl_formatCommas(cardData.totalRMS.toDouble()).toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totalAward.toString())
        )

        val dataListWithHR:List<Pair<RLTypeOfMetrics, RLMetricData>> =listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.rl_formatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.rl_formatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.rl_formatCommas(cardData.burntCalories.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totalAward.toString())
        )

        if (cardData.hrm==0) {
            //WITHOUT HR
            rl_summaryListDataSet(dataListWithoutHR)
        }else{
            //WITH HR
            //RLsummaryListDataSet(dataListWithHR)
            rl_summaryListDataSet(dataListWithoutHR)

        }
    }
    private fun rl_summaryListDataSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.rl_heightsetimageview( fragBinding.testImage)
    }
    override fun onPause() {
        super.onPause()
        rl_bottomHideShowSet(true)
    }

}