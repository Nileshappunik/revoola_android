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
import com.example.myfirstapp.databinding.FragChangePasswordBinding
import com.example.myfirstapp.databinding.FragNotificationBinding
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools

class FragNotification : BaseFragment() {
    val TAG: String = FragNotification::class.java.simpleName
    lateinit var fragBinding: FragNotificationBinding

    
    private val binding by lazy {
        FragNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_notification, container) as FragNotificationBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragNotification" )
        fragBinding.toolbar.tvTitle.setText(R.string.notifications)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

    }

}