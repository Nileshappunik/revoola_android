package com.example.myfirstapp.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.FeedListAdapter
import com.example.myfirstapp.databinding.FragFeedBinding
import com.example.myfirstapp.utils.PrefManager


class FragFeed : BaseFragment() {
    val TAG: String = FragFeed::class.java.simpleName
    lateinit var fragBinding: FragFeedBinding

    private val binding by lazy {
        FragFeedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_feed, container) as FragFeedBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragFeed" )
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemfeed.layoutManager = linearLayoutManager
        val adapter = FeedListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.rvItemfeed.adapter = adapter
        
        fragBinding.layFriends.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewFriends.visibility=View.VISIBLE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE

        }
        fragBinding.layYou.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewYou.visibility=View.VISIBLE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE
        }
        fragBinding.layGroups.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewGroups.visibility=View.VISIBLE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE
        }
        fragBinding.layChallenges.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewChallenges.visibility=View.VISIBLE
        }
    }
}