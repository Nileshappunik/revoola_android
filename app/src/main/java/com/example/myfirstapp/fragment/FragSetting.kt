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

class FragSetting : BaseFragment() {
    val TAG: String = FragSetting::class.java.simpleName
    lateinit var fragBinding: FragSettingBinding

    
    private val binding by lazy {
        FragSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_setting, container) as FragSettingBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragSetting" )
        fragBinding.toolbar.tvTitle.setText(R.string.settings)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

    }

}