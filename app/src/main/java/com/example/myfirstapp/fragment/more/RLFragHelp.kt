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
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.fragment.more.adapter.RLHelpItemListAdapter
import com.example.myfirstapp.model.RLMoreGroupItemModel
import com.example.myfirstapp.utils.RLPrefManager

class RLFragHelp : RLBaseFragment() {
    val TAG: String = RLFragHelp::class.java.simpleName
    lateinit var fragBinding: RlFragHelpBinding

    
    private val binding by lazy {
        RlFragHelpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_help, container) as RlFragHelpBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragHelp")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)

        // AppointmentList
        val moreList = listOf(
            RLMoreGroupItemModel(R.drawable.ic_videocam_outline,resources.getString(R.string.aquickintroduction), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_account_g,resources.getString(R.string.getttingstarted),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_settings_g,resources.getString(R.string.faqs), emptyList()))

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerview.layoutManager = linearLayoutMain
        val adapter = RLHelpItemListAdapter(activity, moreList)
        fragBinding.recyclerview.adapter = adapter

    }

}