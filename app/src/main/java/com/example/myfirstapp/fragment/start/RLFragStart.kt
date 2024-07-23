package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.ViewTreeObserver
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragStartBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.enumclass.RLStartType
import com.example.myfirstapp.fragment.start.body.RLFragBodyClasses
import com.example.myfirstapp.fragment.start.challenges.RLFragChalengesType
import com.example.myfirstapp.fragment.start.mind.RLFragMindClasses
import com.example.myfirstapp.fragment.start.yourway.RLFragYourWay
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        //RLuisetup(dataList)
        RLStartList()
        return fragBinding.root
    }
    private fun RLuisetup(dataList: List<RLStartAllMenuModel>) {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()

        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.foryourmindandbody))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.whatdoyouwanttoday))

        /*val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvStart.layoutManager = linearLayoutManager
        val dataList1:List<Pair<String, String>> = listOf("Classes" to RLConstants.WALKIMAGE, "Your Way" to RLConstants.img_yourway_start, "Challenges" to RLConstants.img_challenge_start,"end" to RLConstants.WALKIMAGE)
        val adapter = RLStartListAdapter(activity,dataList1)*/


        //val dataList:List<RLStartType> = listOf(RLStartType.MindClasses,RLStartType.BodyClasses,RLStartType.YourWay,RLStartType.Challenges)

        fragBinding.inlayMindclass.imgType.RLadjustWidthToHeight()
        fragBinding.inlayBodyclass.imgType.RLadjustWidthToHeight()
        fragBinding.inlayYourway.imgType.RLadjustWidthToHeight()
        fragBinding.inlayChallenges.imgType.RLadjustWidthToHeight()




        fragBinding.inlayMindclass.txtTypename.setText(dataList[0].title)
        fragBinding.inlayMindclass.txtDescription.setText(dataList[0].description)
        Glide.with(requireContext()).load(dataList[0].img).into(fragBinding.inlayMindclass.imgType)
        fragBinding.inlayMindclass.imgTypeicon.loadSvg(dataList[0].type)

        fragBinding.inlayBodyclass.txtTypename.setText(dataList[1].title)
        fragBinding.inlayBodyclass.txtDescription.setText(dataList[1].description)
        Glide.with(requireContext()).load(dataList[1].img).into(fragBinding.inlayBodyclass.imgType)
        fragBinding.inlayBodyclass.imgTypeicon.loadSvg(dataList[1].type)

        fragBinding.inlayYourway.txtTypename.setText(dataList[2].title)
        fragBinding.inlayYourway.txtDescription.setText(dataList[2].description)
        Glide.with(requireContext()).load(dataList[2].img).into(fragBinding.inlayYourway.imgType)
        fragBinding.inlayYourway.imgTypeicon.loadSvg(dataList[2].type)

        fragBinding.inlayChallenges.txtTypename.setText(dataList[3].title)
        fragBinding.inlayChallenges.txtDescription.setText(dataList[3].description)
        Glide.with(requireContext()).load(dataList[3].img).into(fragBinding.inlayChallenges.imgType)
        fragBinding.inlayChallenges.imgTypeicon.loadSvg(dataList[3].type)

        fragBinding.inlayMindclass.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses(), TAG, true, null, true)

        }
        fragBinding.inlayBodyclass.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragBodyClasses(), TAG, true, null, true)

        }
        fragBinding.inlayYourway.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, true)
        }
        fragBinding.inlayChallenges.relayStartNew.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, null, true)

        }
    }

    private fun RLStartList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLALLMENULISTRead(RLConstants.MAIN){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    Log.d(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLuisetup(dataList)
                }catch (e:Exception){
                    Log.e(TAG,"Catch:- ${e.message}")
                }
            }
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