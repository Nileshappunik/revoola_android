package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragFeedCardAwardViewBinding
import com.revoola.fragment.feed.adapter.RLFeedAwardListAdapter
import com.revoola.fragment.feed.adapter.RLFeedCommentListAdapter
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager


class RLFragFeedCardAwardView : RLBaseFragment(){
    val TAG: String = RLFragFeedCardAwardView::class.java.simpleName

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFeedCardAwardView()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragFeedCardAwardViewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeedCardAwardView" )
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.ivBack.setOnClickListener {
            rl_closeFragment()
        }
        val cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        RLDatabaseManagerRead().rl_getAwardList(cardData.awards) { awardsList, error ->
            if (error != null) {
                RLTools.rl_logEPrint(TAG,"❌ get Award Error: ${error.message}")
            } else {
                RLTools.rl_logDPrint(TAG,"✅ get Award Success: ${awardsList?.size}")
                if (awardsList!=null && awardsList.size>0){
                    val linearLayoutManager = LinearLayoutManager(activity)
                    fragBinding.rvCommentListThumb.layoutManager = linearLayoutManager
                    val adapter = RLFeedAwardListAdapter(awardsList, activity)
                    fragBinding.rvCommentListThumb.adapter = adapter
                }
            }
        }
    }
}