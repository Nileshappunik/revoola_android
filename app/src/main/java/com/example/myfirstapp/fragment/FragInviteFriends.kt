package com.example.myfirstapp.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools

class FragInviteFriends : BaseFragment() {
    val TAG: String = FragInviteFriends::class.java.simpleName
    lateinit var fragBinding: FragInviteFriendsBinding

    
    private val binding by lazy {
        FragInviteFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_invite_friends, container) as FragInviteFriendsBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragInviteFriends" )
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
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