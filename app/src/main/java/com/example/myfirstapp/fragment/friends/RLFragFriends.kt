package com.example.myfirstapp.fragment.friends

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragFriendsBinding
import com.example.myfirstapp.utils.RLPrefManager


class RLFragFriends : RLBaseFragment() {
    val TAG: String = RLFragFriends::class.java.simpleName
    lateinit var fragBinding: RlFragFriendsBinding

    private val binding by lazy {
        RlFragFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_friends, container) as RlFragFriendsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFriends" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {

        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        fragBinding.layYourFriend.imgSeasrch.setImageResource(R.drawable.fr_friends_green)
        fragBinding.layYourFriend.txtName.setText(R.string.yourfriends)

        fragBinding.layYourGroup.imgSeasrch.setImageResource(R.drawable.fr_groups_green)
        fragBinding.layYourGroup.txtName.setText(R.string.yourgroup)

        fragBinding.layYourFriend.cardImagetext.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourFriends(), TAG, true, RLFragYourFriends::class.java.simpleName, false)
        }

        fragBinding.layFindonrevolla.cardImagetext.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, RLFragFindOnRevoola::class.java.simpleName, false)
        }

        fragBinding.layYourGroup.cardImagetext.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, RLFragYourGroup::class.java.simpleName, false)
        }

        fragBinding.txtInvitefriend.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }
    }
}