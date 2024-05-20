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
import com.example.myfirstapp.adapter.YourGroupListAdapter
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.utils.PrefManager

class FragYourGroup : BaseFragment() {
    val TAG: String = FragYourGroup::class.java.simpleName
    lateinit var fragBinding: FragYourGroupBinding

    
    private val binding by lazy {
        FragYourGroupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_your_group, container) as FragYourGroupBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragYourGroup" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourgroup)
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourgroup.layoutManager = linearLayoutManager
        val adaptergroup = YourGroupListAdapter(activity)
        val adapterfriend = YourFriendListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.recycleYourgroup.adapter = adaptergroup

        fragBinding.txtMyGroup.setOnClickListener {
            fragBinding.txtMyGroup.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtMyGroup.setTextColor(resources.getColor(R.color.white))
            fragBinding.txtCreateGroup.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.txtCreateGroup.background=null
            fragBinding.recycleYourgroup.adapter = adaptergroup
        }

        fragBinding.txtCreateGroup.setOnClickListener {
            fragBinding.txtCreateGroup.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtCreateGroup.setTextColor(resources.getColor(R.color.white))
            fragBinding.txtMyGroup.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.txtMyGroup.background=null
            fragBinding.recycleYourgroup.adapter = adapterfriend
        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as MainActivity).bottombarcolorwhite()
            (context as MainActivity).loadFrag(FragInviteFriends(), TAG, true, FragInviteFriends::class.java.simpleName, false)
        }
    }

}