package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragSetYourGoalBinding
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools

class RLFragSetYourGoal : RLBaseFragment() {
    val TAG: String = RLFragSetYourGoal::class.java.simpleName
    lateinit var fragBinding: RlFragSetYourGoalBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSetYourGoal()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_set_your_goal, container) as RlFragSetYourGoalBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSetYourGoal" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivTitle.setText(R.string.setyourtarget)
        fragBinding.inlayTop.ivDescription.setText(R.string.setyourtargetandselecttimeperiod)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        RLUIBottom()
    }
    private fun RLUIBottom() {

        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        if (challengeType.equals("Steps")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)
            fragBinding.txtHeader.setText(R.string.stepsmall)
        }else if (challengeType.equals("Effort")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_heart)
            fragBinding.txtHeader.setText(R.string.revoolaeffortscore)
        }else if (challengeType.equals("Calories")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)
            fragBinding.txtHeader.setText(R.string.caloriessmallkcal)
        }else if (challengeType.equals("Distance")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_distance)
            fragBinding.txtHeader.setText(R.string.distancesmallkm)
        }else if (challengeType.equals("Climbed")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_climb)
            fragBinding.txtHeader.setText(R.string.climbedm)
        }else if (challengeType.equals("Duration")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)
            fragBinding.txtHeader.setText(R.string.durationh)
        }

        RLTools.RLheightsetstartimage(fragBinding.relayDaily.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayWeekly.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayMonthly.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayCustom.cardChalengesst,requireActivity())

        fragBinding.relayDaily.imgType.setImageResource(R.drawable.calendar_daily)
        fragBinding.relayDaily.txtTypeTitle.setText(R.string.daily)

        fragBinding.relayWeekly.imgType.setImageResource(R.drawable.calendar_weekly)
        fragBinding.relayWeekly.txtTypeTitle.setText(R.string.weekly)

        fragBinding.relayMonthly.imgType.setImageResource(R.drawable.calendar_monthly)
        fragBinding.relayMonthly.txtTypeTitle.setText(R.string.monthly)

        fragBinding.relayCustom.imgType.setImageResource(R.drawable.calendar_custom)
        fragBinding.relayCustom.txtTypeTitle.setText(R.string.custom)


        fragBinding.relayDaily.cardChalengesst.setOnClickListener {
            val stepcount=fragBinding.edtStepCount.text.toString()
            if (stepcount.isNullOrEmpty()){
                RLshowAlertDialog()
            }else{
                var bundle: Bundle = Bundle()
                bundle.putString("ChallengeType",challengeType )
                bundle.putString("CalenderType","Daily" )
                (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, true)
            }
        }
        fragBinding.relayWeekly.cardChalengesst.setOnClickListener {
            val stepcount=fragBinding.edtStepCount.text.toString()
            if (stepcount.isNullOrEmpty()){
                RLshowAlertDialog()
            }else{
                var bundle: Bundle = Bundle()
                bundle.putString("ChallengeType",challengeType )
                bundle.putString("CalenderType","Weekly" )
                (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, true)
            }
        }
        fragBinding.relayMonthly.cardChalengesst.setOnClickListener {
            val stepcount=fragBinding.edtStepCount.text.toString()
            if (stepcount.isNullOrEmpty()){
                RLshowAlertDialog()
            }else{
                var bundle: Bundle = Bundle()
                bundle.putString("ChallengeType",challengeType )
                bundle.putString("CalenderType","Monthly" )
                (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, true)
            }
        }
        fragBinding.relayCustom.cardChalengesst.setOnClickListener {
            val stepcount=fragBinding.edtStepCount.text.toString()
            if (stepcount.isNullOrEmpty()){
                RLshowAlertDialog()
            }else{
                var bundle: Bundle = Bundle()
                bundle.putString("ChallengeType",challengeType )
                bundle.putString("CalenderType","Custom" )
                (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, true)
            }
        }

    }
    fun RLshowHelpDialog() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpSetyourgoalBinding=RlDialogHelpSetyourgoalBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.hide()
        }

        dialogMainBinding.laySartdate.txtHeader.setText(R.string.pleaseenterstartdate)
        dialogMainBinding.laySartdate.txtHeaderDescription.setText(R.string.selecttosetthedatyouwantstart)
        dialogMainBinding.laySartdate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layEnddate.txtHeader.setText(R.string.pleaseenterenddate)
        dialogMainBinding.layEnddate.txtHeaderDescription.setText(R.string.selecttosetthedayuoyend)
        dialogMainBinding.layEnddate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialog.show()

    }
    private fun RLshowAlertDialog() {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText(R.string.pleasesetyourtarget)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
}