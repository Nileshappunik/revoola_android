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
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager

class RLFragGetStarted : RLBaseFragment() {
    val TAG: String = RLFragGetStarted::class.java.simpleName
    //lateinit var fragBinding: RlFragGetStartedBinding

    
    private val fragBinding by lazy {
        RlFragGetStartedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
       // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_get_started, container) as RlFragGetStartedBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragGetStarted")
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
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
        val adapter = RLGetStartedItemListAdapter(activity, dataList) {selectedItem ->
            // Handle date selection
            when(selectedItem){
                getString(R.string.heartratesensor)->{
                    rl_openClickNextView(RLConstants.Connecting_HearRate_Help_Video)
                }
                getString(R.string.connectingaspeedsensor)->{
                    rl_openClickNextView(RLConstants.Connecting_Speed_Help_Video)
                }
                getString(R.string.aquicktourofrevoola)->{
                    rl_openClickNextView(RLConstants.A_Quick_Tour_of_Revoola_Help_Video)
                }
                getString(R.string.connectionapplewatch)->{
                    rl_openClickNextView(RLConstants.Connecting_Apple_Watch_Help_Video)
                }
                getString(R.string.icantfindmysensor)->{
                    rl_openClickNextView(RLConstants.Troubleshooting_Cant_Find_My_Sensor_Help_Video)
                }
                getString(R.string.nameyoursensor)->{
                    rl_openClickNextView(RLConstants.Name_You_Sensor_Help_Video)
                }
            }
        }
        fragBinding.recyclerview.adapter = adapter
    }

    private fun rl_openClickNextView(HelpType: String) {
        val videoDialog = RLFragGetStartedVideoPlay(HelpType)
        videoDialog.show(parentFragmentManager, "RLFragGetStartedVideoPlay")
    }

}