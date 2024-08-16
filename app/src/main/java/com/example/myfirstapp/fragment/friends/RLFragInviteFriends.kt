package com.example.myfirstapp.fragment.friends

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

class RLFragInviteFriends : RLBaseFragment() {
    val TAG: String = RLFragInviteFriends::class.java.simpleName
    lateinit var fragBinding: RlFragInviteFriendsBinding

    
    private val binding by lazy {
        RlFragInviteFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_invite_friends, container) as RlFragInviteFriendsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragInviteFriends" )
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        fragBinding.layInviteTwitter.txtInviteCommon.setText(R.string.sendontwitter)
        fragBinding.layInviteTwitter.imgInviteCommon.setImageResource(R.drawable.ic_send_twitter)

        fragBinding.layInviteWhatsapp.txtInviteCommon.setText(R.string.sendontwhatsapp)
        fragBinding.layInviteWhatsapp.imgInviteCommon.setImageResource(R.drawable.ic_send_whatsapp)

        fragBinding.layInviteSms.txtInviteCommon.setText(R.string.sendsms)
        fragBinding.layInviteSms.imgInviteCommon.setImageResource(R.drawable.ic_send_sms)

        fragBinding.layInviteEmail.txtInviteCommon.setText(R.string.sendemail)
        fragBinding.layInviteEmail.imgInviteCommon.setImageResource(R.drawable.ic_send_email)
    }

}