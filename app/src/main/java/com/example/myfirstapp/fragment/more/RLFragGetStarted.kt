package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.fragment.more.adapter.RLGetStartedItemListAdapter
import com.example.myfirstapp.fragment.more.adapter.RLHelpItemListAdapter
import com.example.myfirstapp.fragment.more.adapter.RlMoreExpandableListAdapter
import com.example.myfirstapp.model.RLMoreGroupItemModel
import com.example.myfirstapp.utils.RLPrefManager

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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragGetStarted")
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
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.nameyoursensor), emptyList()))

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerview.layoutManager = linearLayoutMain
        val adapter = RLGetStartedItemListAdapter(activity, dataList)
        fragBinding.recyclerview.adapter = adapter
    }

}