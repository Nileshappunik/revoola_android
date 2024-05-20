package com.example.myfirstapp.fragment.friends

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.adapter.YourFriendListAdapter
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.PrefManager

class FragYourFriends : BaseFragment() {
    val TAG: String = FragYourFriends::class.java.simpleName
    lateinit var fragBinding: FragYourFriendsBinding

    
    private val binding by lazy {
        FragYourFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_your_friends, container) as FragYourFriendsBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourfriends)
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = YourFriendListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.recycleYourfriend.adapter = adapter

        fragBinding.txtFriendYoufollow.setOnClickListener {
            fragBinding.txtFriendYoufollow.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.white))
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.txtFriendFollowingyou.background=null
        }

        fragBinding.txtFriendFollowingyou.setOnClickListener {
            fragBinding.txtFriendFollowingyou.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtFriendYoufollow.background=null
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.white))
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.Gray))

        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as MainActivity).bottombarcolorwhite()
            (context as MainActivity).loadFrag(FragInviteFriends(), TAG, true, FragInviteFriends::class.java.simpleName, false)
        }
    }

}