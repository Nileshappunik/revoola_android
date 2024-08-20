package com.example.myfirstapp.fragment.friends

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

class RLFragFindOnRevoola : RLBaseFragment() {
    val TAG: String = RLFragFindOnRevoola::class.java.simpleName
    lateinit var fragBinding: RlFragFingOnRevoolaBinding

    private val binding by lazy {
        RlFragFingOnRevoolaBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_fing_on_revoola, container) as RlFragFingOnRevoolaBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFindOnRevoola" )
        fragBinding.toolbar.tvTitle.setText(R.string.searchfriends)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }
    }

}