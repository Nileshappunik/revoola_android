package com.example.myfirstapp.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.FragOverviewBinding
import com.example.myfirstapp.databinding.FragStartBinding
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools
import java.util.*

class FragOverview : BaseFragment() {
    val TAG: String = FragOverview::class.java.simpleName
    lateinit var fragBinding: FragOverviewBinding

    private val binding by lazy {
        FragOverviewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_overview, container) as FragOverviewBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragOverview" )
        val currentmonth=Tools.getCalculatedMonths()
        fragBinding.txtMonth.setText(currentmonth)
        fragBinding.relayOverview.setOnClickListener {
            //(context as MainActivity).bottombarcolorwhite()
            //(context as MainActivity).loadFrag(FragSessions(), TAG, true, FragSessions::class.java.simpleName, false)
        }
        return fragBinding.root
    }
}