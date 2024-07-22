package com.example.myfirstapp.fragment.friends

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragFriendsBinding
import com.example.myfirstapp.enumclass.RLStartType
import com.example.myfirstapp.fragment.start.yourway.RLFragChooseYourSensor
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg


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
        RLuisetupNew()
        return fragBinding.root
    }

    private fun  RLuisetupNew(){
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.friends))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.manageyourrevoolacommunity))

        val dataList:List<RLStartType> = listOf(
            RLStartType.FindOnRevoola,
            RLStartType.YourFriend,
            RLStartType.YourGroup,
            RLStartType.InviteToJoin)

        fragBinding.inlayFindonRevoola.imgType.RLadjustWidthToHeight()
        fragBinding.inlayYourFriend.imgType.RLadjustWidthToHeight()
        fragBinding.inlayYourGroup.imgType.RLadjustWidthToHeight()
        fragBinding.inlayInvitetoJoin.imgType.RLadjustWidthToHeight()

        fragBinding.inlayFindonRevoola.txtTypename.setText(dataList[0].title)
        fragBinding.inlayFindonRevoola.txtDescription.setText(getString(dataList[0].description))
        Glide.with(requireContext()).load(dataList[0].image).into(fragBinding.inlayFindonRevoola.imgType)
        fragBinding.inlayFindonRevoola.imgTypeicon.loadSvg(dataList[0].icon_image)

        fragBinding.inlayYourFriend.txtTypename.setText(dataList[1].title)
        fragBinding.inlayYourFriend.txtDescription.setText(getString(dataList[1].description))
        Glide.with(requireContext()).load(dataList[1].image).into(fragBinding.inlayYourFriend.imgType)
        fragBinding.inlayYourFriend.imgTypeicon.loadSvg(dataList[1].icon_image)

        fragBinding.inlayYourGroup.txtTypename.setText(dataList[2].title)
        fragBinding.inlayYourGroup.txtDescription.setText(getString(dataList[2].description))
        Glide.with(requireContext()).load(dataList[2].image).into(fragBinding.inlayYourGroup.imgType)
        fragBinding.inlayYourGroup.imgTypeicon.loadSvg(dataList[2].icon_image)

        fragBinding.inlayInvitetoJoin.txtTypename.setText(dataList[3].title)
        fragBinding.inlayInvitetoJoin.txtDescription.setText(getString(dataList[3].description))
        Glide.with(requireContext()).load(dataList[3].image).into(fragBinding.inlayInvitetoJoin.imgType)
        fragBinding.inlayInvitetoJoin.imgTypeicon.loadSvg(dataList[3].icon_image)

        fragBinding.inlayFindonRevoola.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, null, false)
        }
        fragBinding.inlayYourFriend.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourFriends(), TAG, true,null, false)
        }
        fragBinding.inlayYourGroup.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, null, false)
        }
        fragBinding.inlayInvitetoJoin.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, null, false)
        }
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

    fun View.RLadjustWidthToHeight() {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = this@RLadjustWidthToHeight.height
                if (height > 0) {
                    this@RLadjustWidthToHeight.layoutParams.width = height
                    this@RLadjustWidthToHeight.requestLayout()
                    this@RLadjustWidthToHeight.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }
}