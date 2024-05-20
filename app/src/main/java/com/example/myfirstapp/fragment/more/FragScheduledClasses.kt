package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragScheduledClassesBinding
import com.example.myfirstapp.utils.PrefManager

class FragScheduledClasses : BaseFragment() {
    val TAG: String = FragScheduledClasses::class.java.simpleName
    lateinit var fragBinding: FragScheduledClassesBinding

    
    private val binding by lazy {
        FragScheduledClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_scheduled_classes, container) as FragScheduledClassesBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragScheduledClasses" )
        fragBinding.toolbar.tvTitle.setText(R.string.schduledclasses)
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

    }

}