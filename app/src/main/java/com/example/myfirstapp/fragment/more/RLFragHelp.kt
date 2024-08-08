package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.RLPrefManager

class RLFragHelp : RLBaseFragment() {
    val TAG: String = RLFragHelp::class.java.simpleName
    lateinit var fragBinding: RlFragHelpBinding

    
    private val binding by lazy {
        RlFragHelpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_help, container) as RlFragHelpBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragHelp")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {

        RLonBackPresAct(fragBinding.ivBack)

        fragBinding.relayGettingStarted.txtAccount.setText(R.string.getttingstarted)
        fragBinding.relayGettingStarted.imgAccount.setImageResource(R.drawable.ic_account_g)
        fragBinding.relayGettingStarted.viewimgtxt.visibility=View.GONE

        fragBinding.relayFaqus.txtAccount.setText(R.string.faqs)
        fragBinding.relayFaqus.imgAccount.setImageResource(R.drawable.ic_settings_g)
        fragBinding.relayFaqus.viewimgtxt.visibility=View.GONE

        fragBinding.relayLogs.imgAccount.setImageResource(R.drawable.ic_help)
        fragBinding.relayLogs.txtAccount.setText(R.string.logs)
        fragBinding.relayLogs.viewimgtxt.visibility=View.GONE
        fragBinding.relayLogs.layMoreClick.visibility=View.GONE

        fragBinding.relayGettingStarted.layMoreClick.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragGetStarted(), TAG, true, RLFragGetStarted::class.java.simpleName, false)
        }
    }

}