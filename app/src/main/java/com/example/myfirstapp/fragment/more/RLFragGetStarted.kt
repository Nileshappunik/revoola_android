package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.RLPrefManager

class RLFragGetStarted : RLBaseFragment() {
    val TAG: String = RLFragGetStarted::class.java.simpleName
    lateinit var fragBinding: RlFragGetStartedBinding

    
    private val binding by lazy {
        RlFragGetStartedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_get_started, container) as RlFragGetStartedBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragGetStarted")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        
        RLonBackPresAct(fragBinding.ivBack)

        fragBinding.relayApplewatch.txtAccount.setText(R.string.connectionapplewatch)
        fragBinding.relayApplewatch.imgAccount.setImageResource(R.drawable.ic_help)

        fragBinding.relayRatesensor.txtAccount.setText(R.string.heartratesensor)
        fragBinding.relayRatesensor.imgAccount.setImageResource(R.drawable.ic_help)

        fragBinding.relaySpeedsensor.txtAccount.setText(R.string.connectingaspeedsensor)
        fragBinding.relaySpeedsensor.imgAccount.setImageResource(R.drawable.ic_help)

        fragBinding.relayYourensor.txtAccount.setText(R.string.nameyoursensor)
        fragBinding.relayYourensor.imgAccount.setImageResource(R.drawable.ic_help)

        fragBinding.relayTourofrevoola.txtAccount.setText(R.string.aquicktourofrevoola)
        fragBinding.relayTourofrevoola.imgAccount.setImageResource(R.drawable.ic_help)

        fragBinding.relayCantfindmysensor.txtAccount.setText(R.string.icantfindmysensor)
        fragBinding.relayCantfindmysensor.imgAccount.setImageResource(R.drawable.ic_help)
        fragBinding.relayCantfindmysensor.viewimgtxt.visibility=View.GONE

    }

}