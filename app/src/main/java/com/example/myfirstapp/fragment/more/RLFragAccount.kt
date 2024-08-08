package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlFragAccountBinding
import com.example.myfirstapp.utils.RLPrefManager

class RLFragAccount : RLBaseFragment() {
    val TAG: String = RLFragAccount::class.java.simpleName
    lateinit var fragBinding: RlFragAccountBinding

    
    private val binding by lazy {
        RlFragAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account, container) as RlFragAccountBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccount" )

        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.relaySinceuser.txtRevoolaUser.visibility=View.VISIBLE
        fragBinding.relaySinceuser.txtRevoolaDate.visibility=View.VISIBLE
        fragBinding.relaySinceuser.txtRevoolaDes.visibility=View.GONE

        fragBinding.relay1.txtRevoolaDes.setText(R.string.mindandbodyclasses)
        fragBinding.relay2.txtRevoolaDes.setText(R.string.trackyourprogress)
        fragBinding.relay3.txtRevoolaDes.setText(R.string.classandactivities)
        fragBinding.relay4.txtRevoolaDes.setText(R.string.personalisecalender)
        fragBinding.relay5.txtRevoolaDes.setText(R.string.challengesfriends)

    }

}