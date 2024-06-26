package com.example.myfirstapp.fragment.start

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
import com.example.myfirstapp.databinding.RlClassWorkoutCompleteBinding
import com.example.myfirstapp.databinding.RlFragSessionCompleteBinding
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants

import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson


class RLFragClassWorkoutComplete : RLBaseFragment(){
    val TAG: String = RLFragClassWorkoutComplete::class.java.simpleName
    lateinit var fragBinding: RlClassWorkoutCompleteBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragClassWorkoutComplete()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        RlClassWorkoutCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_class_workout_complete, container) as RlClassWorkoutCompleteBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragClassWorkoutComplete" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
       val classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.edtname.setText(VideoCardData.rideTitle)

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
            (context as RLMainActivityRL).RLloadFrag(RLFragOverview(), TAG, false, RLFragOverview::class.java.simpleName, false)
        }
        fragBinding.tvsave.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverview(), TAG, false, RLFragOverview::class.java.simpleName, false)
        }


    }
}