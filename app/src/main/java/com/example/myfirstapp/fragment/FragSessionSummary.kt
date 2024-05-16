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
import com.example.myfirstapp.databinding.FragAccountBinding
import com.example.myfirstapp.databinding.FragChangePasswordBinding
import com.example.myfirstapp.databinding.FragNotificationBinding
import com.example.myfirstapp.databinding.FragScheduledClassesBinding
import com.example.myfirstapp.databinding.FragSessionSummaryBinding
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools

class FragSessionSummary : BaseFragment() {
    val TAG: String = FragSessionSummary::class.java.simpleName
    lateinit var fragBinding: FragSessionSummaryBinding

    
    private val binding by lazy {
        FragSessionSummaryBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_session_summary, container) as FragSessionSummaryBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragSessionSummary" )
        fragBinding.toolbar.tvTitle.setText(R.string.sessionsummery)
        fragBinding.toolbar.tvTitle.setTextColor(resources.getColor(R.color.black))
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        onBackPresAct(fragBinding.toolbar.ivBack)

        fragBinding.relayAssumedRelaxation.txtMindTitle.setText("Assumed Relaxation")
        fragBinding.relayAssumedRelaxation.txtTimeSecond.setText("1")

        fragBinding.relayComments.txtTitle.setText("Comments")
        fragBinding.relayAwards.txtTitle.setText("Awards")
    }

}