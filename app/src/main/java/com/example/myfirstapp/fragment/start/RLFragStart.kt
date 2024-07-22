package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragStartBinding
import com.example.myfirstapp.enumclass.RLStartType
import com.example.myfirstapp.fragment.start.body.RLFragBodyClasses
import com.example.myfirstapp.fragment.start.challenges.RLFragChalengesType
import com.example.myfirstapp.fragment.start.mind.RLFragMindClasses
import com.example.myfirstapp.fragment.start.yourway.RLFragYourWay
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg

class RLFragStart : RLBaseFragment() {
    val TAG: String = RLFragStart::class.java.simpleName
    lateinit var fragBinding: RlFragStartBinding

    private val binding by lazy {
        RlFragStartBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_start, container) as RlFragStartBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        /*val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvStart.layoutManager = linearLayoutManager
        val dataList1:List<Pair<String, String>> = listOf("Classes" to RLConstants.WALKIMAGE, "Your Way" to RLConstants.img_yourway_start, "Challenges" to RLConstants.img_challenge_start,"end" to RLConstants.WALKIMAGE)
        val adapter = RLStartListAdapter(activity,dataList1)*/


        val dataList:List<RLStartType> = listOf(RLStartType.MindClasses,RLStartType.BodyClasses,RLStartType.YourWay,RLStartType.Challenges)

        fragBinding.inlayMindclass.imgType.RLadjustWidthToHeight()
        fragBinding.inlayBodyclass.imgType.RLadjustWidthToHeight()
        fragBinding.inlayYourway.imgType.RLadjustWidthToHeight()
        fragBinding.inlayChallenges.imgType.RLadjustWidthToHeight()




        fragBinding.inlayMindclass.txtTypename.setText(dataList[0].title)
        fragBinding.inlayMindclass.txtDescription.setText(getString(dataList[0].description))
        Glide.with(requireContext()).load(dataList[0].image).into(fragBinding.inlayMindclass.imgType)
        fragBinding.inlayMindclass.imgTypeicon.loadSvg(dataList[0].icon_image)

        fragBinding.inlayBodyclass.txtTypename.setText(dataList[1].title)
        fragBinding.inlayBodyclass.txtDescription.setText(getString(dataList[1].description))
        Glide.with(requireContext()).load(dataList[1].image).into(fragBinding.inlayBodyclass.imgType)
        fragBinding.inlayBodyclass.imgTypeicon.loadSvg(dataList[1].icon_image)

        fragBinding.inlayYourway.txtTypename.setText(dataList[2].title)
        fragBinding.inlayYourway.txtDescription.setText(getString(dataList[2].description))
        Glide.with(requireContext()).load(dataList[2].image).into(fragBinding.inlayYourway.imgType)
        fragBinding.inlayYourway.imgTypeicon.loadSvg(dataList[2].icon_image)

        fragBinding.inlayChallenges.txtTypename.setText(dataList[3].title)
        fragBinding.inlayChallenges.txtDescription.setText(getString(dataList[3].description))
        Glide.with(requireContext()).load(dataList[3].image).into(fragBinding.inlayChallenges.imgType)
        fragBinding.inlayChallenges.imgTypeicon.loadSvg(dataList[3].icon_image)

        fragBinding.inlayMindclass.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses(), TAG, true, null, false)

        }
        fragBinding.inlayBodyclass.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragBodyClasses(), TAG, true, null, false)

        }
        fragBinding.inlayYourway.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
        }
        fragBinding.inlayChallenges.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, null, false)

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