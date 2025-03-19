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
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragMindSessionSummary : RLBaseFragment() {
    val TAG: String = RLFragMindSessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragMindSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var  selectTag:String=""

    private val binding by lazy {
        RlFragMindSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_session_summary, container) as RlFragMindSessionSummaryBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragMindSessionSummary" )
        currentUser=  com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
        fragBinding.ivBack.setOnClickListener {
            RLcloseScreen(isSessionComplete)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                RLcloseScreen(isSessionComplete)
            }
        })

        fragBinding.ivTitle.setText(getString(R.string.sessionsummerys))

        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        RLTools.RLLogLarge(TAG,"Card Data: ${Gson().toJson(cardData)}")
        RLsummaryDataSet()
    }

    private fun RLcloseScreen(isSessionComplete:Boolean){
        if (isSessionComplete){
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }else{
            RLcloseFragment()
        }
    }

    private fun RLsummaryDataSet() {
        //Image Set
        Glide.with(requireContext()).load(RLTools.RLFeedSetImage(cardData,currentUser,selectTag)).into(fragBinding.testImage)

        val totalAward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        val dataListWithoutHR:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.MindfulMinutes to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.AssumeRelaxation to RLMetricData(RLTools.RLformatCommas(cardData.totalRMS.toDouble()).toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totalAward.toString())
        )

        val dataListWithHR:List<Pair<RLTypeOfMetrics, RLMetricData>> =listOf(
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.burntCalories.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totalAward.toString())
        )

        if (cardData.hrm==0) {
            //WITHOUT HR
            RLsummaryListDataSet(dataListWithoutHR)
        }else{
            //WITH HR
            //RLsummaryListDataSet(dataListWithHR)
            RLsummaryListDataSet(dataListWithoutHR)

        }
    }
    private fun RLsummaryListDataSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }

}