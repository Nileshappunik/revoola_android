package com.example.myfirstapp.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragFriendsBinding
import com.example.myfirstapp.databinding.FragOverviewBinding
import com.example.myfirstapp.databinding.FragStartBinding
import com.example.myfirstapp.utils.PrefManager


class FragFriends : BaseFragment() {
    val TAG: String = FragFriends::class.java.simpleName
    lateinit var fragBinding: FragFriendsBinding

    private val binding by lazy {
        FragFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_friends, container) as FragFriendsBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragFriends" )
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        fragBinding.layYourFriend.imgSeasrch.setImageResource(R.drawable.fr_friends_green)
        fragBinding.layYourFriend.txtName.setText(R.string.yourfriends)

        fragBinding.layYourGroup.imgSeasrch.setImageResource(R.drawable.fr_groups_green)
        fragBinding.layYourGroup.txtName.setText(R.string.yourgroup)
    }
}