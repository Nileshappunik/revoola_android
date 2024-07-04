package com.example.myfirstapp.fragment.start.classes

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragClassesScheduleSessionBinding
import com.example.myfirstapp.utils.RLPrefManager


class RLClassesScheduleJoinSession : RLBaseFragment() {
    val TAG: String = RLClassesScheduleJoinSession::class.java.simpleName
    lateinit var fragBinding: RlFragClassesScheduleSessionBinding
    private val binding by lazy {
        RlFragClassesScheduleSessionBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLClassesScheduleJoinSession()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes_schedule_session, container) as RlFragClassesScheduleSessionBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLClassesScheduleJoinSession" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
    }
}