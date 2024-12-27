package com.revoola.fragment.start.challenges

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.databinding.RlFragSetYourGoalBinding
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.commonobject.RLTools
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.Locale

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
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_set_your_goal, container) as RlFragSetYourGoalBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragSetYourGoal" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, com.revoola.utils.RLPrefManager.start_help_content)
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLBottomHideShowSet(true)
            RLcloseFragment()
        }
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
            //fragBinding.txtHeader.setText(R.string.revoolaeffortscore)
            fragBinding.txtHeader.setText(R.string.effort)
        }else if (challengeType.equals("Calories")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)
           // fragBinding.txtHeader.setText(R.string.caloriessmallkcal)
            fragBinding.txtHeader.setText(R.string.calories)
        }else if (challengeType.equals("Distance")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_distance)
           // fragBinding.txtHeader.setText(R.string.distancesmallkm)
            fragBinding.txtHeader.setText(R.string.distance)
        }else if (challengeType.equals("Climbed")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_climb)
           // fragBinding.txtHeader.setText(R.string.climbedm)
            fragBinding.txtHeader.setText(R.string.climbed)
        }else if (challengeType.equals("Duration")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)
           // fragBinding.txtHeader.setText(R.string.durationh)
            fragBinding.txtHeader.setText(R.string.duration)
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
           RLnextFragmentOpen("Daily",challengeType)

        }
        fragBinding.relayWeekly.cardChalengesst.setOnClickListener {
            RLnextFragmentOpen("Weekly",challengeType)

        }
        fragBinding.relayMonthly.cardChalengesst.setOnClickListener {
            RLnextFragmentOpen("Monthly",challengeType)

        }
        fragBinding.relayCustom.cardChalengesst.setOnClickListener {
            RLnextFragmentOpen("Custom",challengeType)

        }
        RLAddCommaFormatting(fragBinding.edtStepCount)

    }

    private fun RLnextFragmentOpen(CalenderType:String,challengeType:String) {
        val textWithoutCommas =fragBinding.edtStepCount.text.toString()
        val stepCount = textWithoutCommas.replace(",", "")
        if (stepCount.isEmpty()){
            RLshowAlertDialog()
        }else if (stepCount.toDouble()>=1){
            val bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType)
            bundle.putString("CalenderType",CalenderType)
           (context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, true)
        } else{
            RLshowAlertDialog()
        }
    }

    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpStartBinding = RlDialogHelpStartBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val linearLayoutMain = LinearLayoutManager(activity)
        dialogMainBinding.ivRecyclerview.layoutManager = linearLayoutMain

        val jsonString= com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.challenge_selectTarget,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

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

    private fun RLAddCommaFormatting(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var currentText = ""

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentText) {
                    editText.removeTextChangedListener(this)

                    // Remove commas and reformat the number
                    val cleanString = s.toString().replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
                            val formatted = NumberFormat.getNumberInstance(Locale.getDefault()).format(parsed)

                            currentText = formatted
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        } catch (e: NumberFormatException) {
                            e.printStackTrace() // Handle the number formatting exception
                        }
                    }

                    editText.addTextChangedListener(this)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

}