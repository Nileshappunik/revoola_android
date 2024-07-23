package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlFragChalengesTypeBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.enumclass.RLTypeOfChallenges
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengesListAdapter
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.utils.loadSvg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.roundToInt

class RLFragChalengesType : RLBaseFragment() {
    val TAG: String = RLFragChalengesType::class.java.simpleName
    lateinit var fragBinding: RlFragChalengesTypeBinding

    private val binding by lazy {
        RlFragChalengesTypeBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_type, container) as RlFragChalengesTypeBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesType" )
        //RLuisetup()
        //RLuisetupNew(dataList)
        RLChallengesList()
        return fragBinding.root
    }
    private fun RLuisetupNew(dataList: List<RLStartAllMenuModel>) {
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
       // val dataList:List<RLStartType> = listOf(RLStartType.Steps, RLStartType.Effort , RLStartType.Calories , RLStartType.Distance , RLStartType.Climbed, RLStartType.Duration)

        fragBinding.inlaySteps.imgType.RLadjustWidthToHeight()
        fragBinding.inlayEffort.imgType.RLadjustWidthToHeight()
        fragBinding.inlayCalories.imgType.RLadjustWidthToHeight()
        fragBinding.inlayDistance.imgType.RLadjustWidthToHeight()
        fragBinding.inlayClimbed.imgType.RLadjustWidthToHeight()
        fragBinding.inlayDuration.imgType.RLadjustWidthToHeight()

        fragBinding.inlaySteps.txtTypename.setText(dataList[0].title)
        fragBinding.inlaySteps.txtDescription.setText(dataList[0].description)
        Glide.with(requireContext()).load(dataList[0].img).into(fragBinding.inlaySteps.imgType)
        fragBinding.inlaySteps.imgTypeicon.loadSvg(dataList[0].type)

        fragBinding.inlayEffort.txtTypename.setText(dataList[1].title)
        fragBinding.inlayEffort.txtDescription.setText(dataList[1].description)
        Glide.with(requireContext()).load(dataList[1].img).into(fragBinding.inlayEffort.imgType)
        fragBinding.inlayEffort.imgTypeicon.loadSvg(dataList[1].type)

        fragBinding.inlayCalories.txtTypename.setText(dataList[2].title)
        fragBinding.inlayCalories.txtDescription.setText(dataList[2].description)
        Glide.with(requireContext()).load(dataList[2].img).into(fragBinding.inlayCalories.imgType)
        fragBinding.inlayCalories.imgTypeicon.loadSvg(dataList[2].type)

        fragBinding.inlayDistance.txtTypename.setText(dataList[3].title)
        fragBinding.inlayDistance.txtDescription.setText(dataList[3].description)
        Glide.with(requireContext()).load(dataList[3].img).into(fragBinding.inlayDistance.imgType)
        fragBinding.inlayDistance.imgTypeicon.loadSvg(dataList[3].type)

        fragBinding.inlayClimbed.txtTypename.setText(dataList[4].title)
        fragBinding.inlayClimbed.txtDescription.setText(dataList[4].description)
        Glide.with(requireContext()).load(dataList[4].img).into(fragBinding.inlayClimbed.imgType)
        fragBinding.inlayClimbed.imgTypeicon.loadSvg(dataList[4].type)

        fragBinding.inlayDuration.txtTypename.setText(dataList[5].title)
        fragBinding.inlayDuration.txtDescription.setText(dataList[5].description)
        Glide.with(requireContext()).load(dataList[5].img).into(fragBinding.inlayDuration.imgType)
        fragBinding.inlayDuration.imgTypeicon.loadSvg(dataList[5].type)

        fragBinding.inlayTop.ivTitle.setText(getString(R.string.challengessmall))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.typeofchallenge))
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }

        fragBinding.inlaySteps.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Steps" )
        }

        fragBinding.inlayEffort.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Effort" )
        }

        fragBinding.inlayCalories.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Calories" )
        }

        fragBinding.inlayDistance.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Distance" )
        }
        fragBinding.inlayClimbed.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Climbed" )
        }
        fragBinding.inlayDuration.relayStartNew.setOnClickListener {
            RLNextViewOpen( "Duration" )
        }
    }
    private fun RLChallengesList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLALLMENULISTRead(RLConstants.CHALLENGES){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    Log.d(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLuisetupNew(dataList)
                }catch (e:Exception){
                    Log.e(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpChallengesBinding =
            RlDialogHelpChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.hide()
        }

        dialogMainBinding.layStep.txtHeader.setText(R.string.stepdot)

        dialogMainBinding.layEffort.txtHeader.setText(R.string.effortdot)
        dialogMainBinding.layEffort.txtHeaderDescription.setText(R.string.revoolauniqueeffort)
        dialogMainBinding.layEffort.imgHelpChallenges.setImageResource(R.drawable.ic_heart)

        dialogMainBinding.layCalories.txtHeader.setText(R.string.caloriesdot)
        dialogMainBinding.layCalories.txtHeaderDescription.setText(R.string.asimplecountcallery)
        dialogMainBinding.layCalories.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)

        dialogMainBinding.layDistance.txtHeader.setText(R.string.distancedot)
        dialogMainBinding.layDistance.txtHeaderDescription.setText(R.string.measureinkmormiles)
        dialogMainBinding.layDistance.imgHelpChallenges.setImageResource(R.drawable.ic_distance)

        dialogMainBinding.layClimbed.txtHeader.setText(R.string.climbeddot)
        dialogMainBinding.layClimbed.txtHeaderDescription.setText(R.string.measureinmeterorfeet)
        dialogMainBinding.layClimbed.imgHelpChallenges.setImageResource(R.drawable.ic_climb)

        dialogMainBinding.layDuration.txtHeader.setText(R.string.durationdot)
        dialogMainBinding.layDuration.txtHeaderDescription.setText(R.string.measureindaysandhours)
        dialogMainBinding.layDuration.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialog.show()

    }
    fun View.RLadjustWidthToHeight() {
        val widthInDp = 150
        val widthInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        val heightInDp = 80
        val heightInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightInDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = this@RLadjustWidthToHeight.height
                val heightNew = this@RLadjustWidthToHeight.height*1.8
                if (height > 0) {
                    this@RLadjustWidthToHeight.layoutParams.width =heightNew.roundToInt() // widthInPx height
                    this@RLadjustWidthToHeight.requestLayout()
                    this@RLadjustWidthToHeight.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

            }
        })
    }
    private fun RLNextViewOpen(challengeType:String){
        var bundle: Bundle = Bundle()
        bundle.putString("ChallengeType",challengeType )
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, null, true)
    }
   /* private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val dataList:List<RLTypeOfChallenges> = listOf(
            RLTypeOfChallenges.Steps,
            RLTypeOfChallenges.Effort ,
            RLTypeOfChallenges.Calories ,
            RLTypeOfChallenges.Distance ,
            RLTypeOfChallenges.Climbed,
            RLTypeOfChallenges.Duration)

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleChallenges.layoutManager = glinearLayoutManager
        val adapterdata = RLChallengesListAdapter(activity, dataList)
        fragBinding.recycleChallenges.adapter = adapterdata

        fragBinding.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        RLTools.RLheightsetstartimage(fragBinding.relaySteps.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayEffort.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayCalories.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayDistance.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayClimbed.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayDuration.cardChalengesst,requireActivity())

        fragBinding.relaySteps.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Steps" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }

        fragBinding.relayEffort.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Effort" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }

        fragBinding.relayCalories.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Calories" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }

        fragBinding.relayDistance.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Distance" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }
        fragBinding.relayClimbed.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Climbed" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }
        fragBinding.relayDuration.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType", "Duration" )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, RLFragSetYourGoal::class.java.simpleName, false)
        }

        fragBinding.relayEffort.imgType.setImageResource(R.drawable.ic_heart)
        fragBinding.relayEffort.txtTypeTitle.setText(R.string.effortsmall)

        fragBinding.relayCalories.imgType.setImageResource(R.drawable.fd_calories_green)
        fragBinding.relayCalories.txtTypeTitle.setText(R.string.caloriessmall)

        fragBinding.relayDistance.imgType.setImageResource(R.drawable.ic_distance)
        fragBinding.relayDistance.txtTypeTitle.setText(R.string.distancesmall)

        fragBinding.relayClimbed.imgType.setImageResource(R.drawable.ic_climb)
        fragBinding.relayClimbed.txtTypeTitle.setText(R.string.climbed)

        fragBinding.relayDuration.imgType.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.relayDuration.txtTypeTitle.setText(R.string.duration)
    }*/
}