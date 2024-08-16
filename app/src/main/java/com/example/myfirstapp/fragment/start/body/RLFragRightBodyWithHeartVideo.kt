package com.example.myfirstapp.fragment.start.body

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlFragRightBodyHeartVideoBinding
import com.example.myfirstapp.fragment.start.adapter.RLStartClassAttendListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.utils.RLPrefManager


class RLFragRightBodyWithHeartVideo : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragRightBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragRightBodyHeartVideoBinding

    private val binding by lazy {
        RlFragRightBodyHeartVideoBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragRightBodyWithHeartVideo()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_right_body_heart_video, container) as RlFragRightBodyHeartVideoBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragRightBodyWithHeartVideo" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutMain
        val adapter = RLStartClassAttendListAdapter(requireContext(),this)
        fragBinding.recyclerList.adapter = adapter

        fragBinding.inlayItemview.relayClick.setOnClickListener {
            fragBinding.inlayItemview.relayClick.visibility=View.GONE
            fragBinding.rightsideview.visibility=View.VISIBLE
        }
    }

    override fun onItemClick(position: Int) {
        //click adapter
        fragBinding.inlayItemview.relayClick.visibility=View.VISIBLE
        fragBinding.rightsideview.visibility=View.GONE
    }

}