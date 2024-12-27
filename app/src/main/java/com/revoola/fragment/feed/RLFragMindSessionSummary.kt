package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragMindSessionSummaryBinding
import com.revoola.enumclass.RLMetricData
import com.revoola.enumclass.RLTypeOfMetrics
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
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
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragMindSessionSummary" )
        currentUser=  com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
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
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.sessionsummerys))
        fragBinding.inlayTop.ivDescription.setText("")


        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String

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

        Glide.with(requireContext()).load(RLTools.RLFeedSetImage(cardData,currentUser,selectTag)).into(fragBinding.testImage)
       /* if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.imageLinkSmall).into(fragBinding.testImage)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(requireContext()).load(cardData.map_image).into(fragBinding.testImage)
        }else{
            Glide.with(requireContext()).load(RLTools.RLgetImage(classType)).into(fragBinding.testImage)

        }*/

        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        //Main Data List Set
        val dataListWithHR:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.MindfulMinutes to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.Relaxation to RLMetricData(RLTools.RLformatCommas(cardData.totalRMS.toDouble()).toString()),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )

        val dataListWithoutHR:List<Pair<RLTypeOfMetrics, RLMetricData>> =listOf(
            //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
            RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLformatTime(cardData.totalTime.toInt(),true)),
            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLTools.RLformatCommas(cardData.totalREV.toDouble())),
            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLTools.RLformatCommas(cardData.power.toDouble())),
            RLTypeOfMetrics.Boosts to RLMetricData(cardData.total_kudos.toString()),
            RLTypeOfMetrics.Comments to RLMetricData(cardData.total_comments.toString()),
            RLTypeOfMetrics.Awards to RLMetricData(totlaaward.toString())
        )

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
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }

}