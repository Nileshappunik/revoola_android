package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.PrefManager

class FragHelp : BaseFragment() {
    val TAG: String = FragHelp::class.java.simpleName
    lateinit var fragBinding: FragHelpBinding

    
    private val binding by lazy {
        FragHelpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_help, container) as FragHelpBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragHelp" )
        fragBinding.toolbar.tvTitle.setText(R.string.help)
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

    }

}