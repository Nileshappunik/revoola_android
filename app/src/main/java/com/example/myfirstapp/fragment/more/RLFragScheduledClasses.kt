package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.RLScheduledClassesListAdapter
import com.example.myfirstapp.databinding.RlFragScheduledClassesBinding
import com.example.myfirstapp.utils.RLPrefManager

class RLFragScheduledClasses : RLBaseFragment() {
    val TAG: String = RLFragScheduledClasses::class.java.simpleName
    lateinit var fragBinding: RlFragScheduledClassesBinding

    
    private val binding by lazy {
        RlFragScheduledClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_scheduled_classes, container) as RlFragScheduledClassesBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragScheduledClasses" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvSchdualclasses.layoutManager = linearLayoutManager
        val adapterScheduledClasses = RLScheduledClassesListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.rvSchdualclasses.adapter = adapterScheduledClasses
    }

}