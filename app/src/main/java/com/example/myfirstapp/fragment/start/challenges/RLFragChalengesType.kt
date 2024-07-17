package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlFragChalengesTypeBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfChallenges
import com.example.myfirstapp.enumclass.RLTypeOfMetrics
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengesListAdapter
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
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
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
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
    }

    fun RLshowHelpDialog() {
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

}