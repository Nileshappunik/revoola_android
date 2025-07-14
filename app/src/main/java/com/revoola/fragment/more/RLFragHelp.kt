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
import com.revoola.fragment.more.adapter.RLHelpItemListAdapter
import com.revoola.model.RLMoreGroupItemModel
import com.revoola.utils.RLPrefManager

class RLFragHelp : RLBaseFragment() {
    val TAG: String = RLFragHelp::class.java.simpleName
  //  lateinit var fragBinding: RlFragHelpBinding

    
    private val fragBinding by lazy {
        RlFragHelpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_help, container) as RlFragHelpBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragHelp")
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)

        // AppointmentList
        val moreList = listOf(
            RLMoreGroupItemModel(R.drawable.ic_videocam_outline,resources.getString(R.string.aquickintroduction), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_account_g,resources.getString(R.string.getttingstarted),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_settings_g,resources.getString(R.string.faqs), emptyList())
        )

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerview.layoutManager = linearLayoutMain
        val adapter = RLHelpItemListAdapter(activity, moreList)
        fragBinding.recyclerview.adapter = adapter

    }

}