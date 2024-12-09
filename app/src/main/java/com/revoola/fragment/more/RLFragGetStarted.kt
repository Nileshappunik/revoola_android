package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.*
import com.revoola.fragment.more.adapter.RLGetStartedItemListAdapter
import com.revoola.model.RLMoreGroupItemModel

class RLFragGetStarted : RLBaseFragment() {
    val TAG: String = RLFragGetStarted::class.java.simpleName
    lateinit var fragBinding: RlFragGetStartedBinding

    
    private val binding by lazy {
        RlFragGetStartedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_get_started, container) as RlFragGetStartedBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragGetStarted")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        //List
        val dataList = listOf(
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.heartratesensor), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.connectingaspeedsensor),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.aquicktourofrevoola),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.connectionapplewatch),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.icantfindmysensor),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.nameyoursensor), emptyList())
        )

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerview.layoutManager = linearLayoutMain
        val adapter = RLGetStartedItemListAdapter(activity, dataList)
        fragBinding.recyclerview.adapter = adapter
    }

}