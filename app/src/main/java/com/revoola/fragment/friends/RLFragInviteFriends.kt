package com.revoola.fragment.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.*
import com.revoola.utils.RLPrefManager

class RLFragInviteFriends : RLBaseFragment() {
    val TAG: String = RLFragInviteFriends::class.java.simpleName


    private val fragBinding by lazy {
        RlFragInviteFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragInviteFriends" )
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        rl_onBackPresAct(fragBinding.toolbar.ivBack)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
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