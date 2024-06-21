package com.example.myfirstapp.fragment.start

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlFragChalengesTypeBinding
import com.example.myfirstapp.utils.RLPrefManager


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

        fragBinding.toolbar.tvTitle.visibility=View.GONE
        fragBinding.toolbar.ivlogoapp.visibility=View.VISIBLE
        fragBinding.toolbar.ivlogoapp.setImageResource(R.drawable.ic_challenge_flag)

        fragBinding.toolbar.ivNotification.visibility=View.VISIBLE
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_circle)
        fragBinding.toolbar.ivNotification.setOnClickListener {
            RLshowHelpDialog()
        }
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.toolbar.ivBack)
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

        fragBinding.relayEffort.imgType.setImageResource(R.drawable.ic_heart_blanck)
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
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpChallengesBinding = RlDialogHelpChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)


        val window: Window = dialog.getWindow()!!
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity=Gravity.RIGHT or Gravity.TOP
        window.attributes = lp

        dialogMainBinding.layStep.txtHeader.setText(R.string.stepdot)

        dialogMainBinding.layEffort.txtHeader.setText(R.string.effortdot)
        dialogMainBinding.layEffort.txtHeaderDescription.setText(R.string.revoolauniqueeffort)
        dialogMainBinding.layEffort.imgHelpChallenges.setImageResource(R.drawable.ic_heart_blanck)

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
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }

}