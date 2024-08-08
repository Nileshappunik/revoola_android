package com.example.myfirstapp.fragment.start.yourway

import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.fragment.overview.RLFragOverviewSession

import com.example.myfirstapp.utils.RLPrefManager


class RLFragSessionComplete : RLBaseFragment(){
    val TAG: String = RLFragSessionComplete::class.java.simpleName
    lateinit var fragBinding: RlFragSessionCompleteBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionComplete()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        RlFragSessionCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_complete, container) as RlFragSessionCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionComplete" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga" ic_pace ic_speeed
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titletxt:String=fragBinding.tvsharetitle.text.toString()

            if (titletxt.equals("Friends")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyEveryOneBGColor))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvsharetitle.setText(R.string.anyone)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.AppPrivacyEveryOneColor))

            }else if (titletxt.equals("Anyone")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyPrivateBGColor))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvsharetitle.setText(R.string.privatetx)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.AppPrivacyPrivateColor))

            }else if (titletxt.equals("Private")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.AppPrivacyFriendsBGColor))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvsharetitle.setText(R.string.friendstx)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.AppPrivacyFriendsColor))
            }

        }

        fragBinding.imgCancle.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }
        fragBinding.tvsave.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }


    }
}