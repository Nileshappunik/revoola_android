package com.revoola.fragment.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.*
import com.revoola.firebaseModel.RLGetStartedHelpVideo
import com.revoola.fragment.more.adapter.RLGetStartedItemListAdapter
import com.revoola.model.RLMoreGroupItemModel
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLConstants

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
        val adapter = RLGetStartedItemListAdapter(activity, dataList) {selectedItem ->
            // Handle date selection
            when(selectedItem){
                getString(R.string.heartratesensor)->{
                    RLOpenClickNextView(RLConstants.Connecting_HearRate_Help_Video)
                }
                getString(R.string.connectingaspeedsensor)->{
                    RLOpenClickNextView(RLConstants.Connecting_Speed_Help_Video)
                }
                getString(R.string.aquicktourofrevoola)->{
                    RLOpenClickNextView(RLConstants.A_Quick_Tour_of_Revoola_Help_Video)
                }
                getString(R.string.connectionapplewatch)->{
                    RLOpenClickNextView(RLConstants.Connecting_Apple_Watch_Help_Video)
                }
                getString(R.string.icantfindmysensor)->{
                    RLOpenClickNextView(RLConstants.Troubleshooting_Cant_Find_My_Sensor_Help_Video)
                }
                getString(R.string.nameyoursensor)->{
                    RLOpenClickNextView(RLConstants.Name_You_Sensor_Help_Video)
                }
            }
        }
        fragBinding.recyclerview.adapter = adapter
    }

    private fun RLOpenClickNextView(HelpType: String) {
        val videoDialog = RLFragGetStartedVideoPlay(HelpType)
        videoDialog.show(parentFragmentManager, "RLFragGetStartedVideoPlay")
    }

}