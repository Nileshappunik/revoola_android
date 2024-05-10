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
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.adapter.FeedListAdapter
import com.example.myfirstapp.adapter.YourFriendListAdapter
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools

class FragFindOnRevoola : BaseFragment() {
    val TAG: String = FragFindOnRevoola::class.java.simpleName
    lateinit var fragBinding: FragFingOnRevoolaBinding

    private val binding by lazy {
        FragFingOnRevoolaBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_fing_on_revoola, container) as FragFingOnRevoolaBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.searchfriends)
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as MainActivity).bottombarcolorwhite()
            (context as MainActivity).loadFrag(FragInviteFriends(), TAG, true, FragInviteFriends::class.java.simpleName, false)
        }
    }

}