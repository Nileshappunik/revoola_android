package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.adapter.RLScheduledClassesListAdapter
import com.revoola.databinding.RlFragScheduledClassesBinding

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
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragScheduledClasses" )
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