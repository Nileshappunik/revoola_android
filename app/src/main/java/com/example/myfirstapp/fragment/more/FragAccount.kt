package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragAccountBinding
import com.example.myfirstapp.utils.PrefManager

class FragAccount : BaseFragment() {
    val TAG: String = FragAccount::class.java.simpleName
    lateinit var fragBinding: FragAccountBinding

    
    private val binding by lazy {
        FragAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_account, container) as FragAccountBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragAccount" )
        fragBinding.toolbar.tvTitle.setText(R.string.account)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        onBackPresAct(fragBinding.toolbar.ivBack)
    }

}