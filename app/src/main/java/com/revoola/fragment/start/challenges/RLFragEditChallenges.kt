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
import com.revoola.databinding.RlFragEditChallengesBinding
import com.revoola.fragment.start.challenges.adapter.RLCalenderListAdapter
import com.revoola.fragment.start.challenges.adapter.RLEditChallengesAdapter
import com.revoola.fragment.start.challenges.model.RLEditChallenge
import com.revoola.utils.RLPrefManager
import java.text.NumberFormat
import java.util.Locale

class RLFragEditChallenges : RLBaseFragment() {
    val TAG: String = RLFragEditChallenges::class.java.simpleName
    lateinit var fragBinding: RlFragEditChallengesBinding
   // RLEditChallengesAdapter
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragEditChallenges()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        RlFragEditChallengesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_edit_challenges, container) as RlFragEditChallengesBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragEditChallenges" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.start_help_content)
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLBottomHideShowSet(true)
            RLcloseFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(R.string.challenge_summary)
        fragBinding.inlayTop.ivDescription.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerviewEdit.layoutManager = linearLayoutMain
        val  dataList: MutableList<RLEditChallenge> = mutableListOf(
            RLEditChallenge(R.drawable.ic_award,"Tets",""),
            RLEditChallenge(R.drawable.fd_steps_green,"22 Steps",""),
            RLEditChallenge(R.drawable.ic_goal,"INDIVIDUAL",""),
            RLEditChallenge(R.drawable.ic_person,"YOU",""),
            RLEditChallenge(R.drawable.calendar_monthly,"Daily Challenges","strt:31/05/2025 Ends:2/06/2025")
        )

        val adapter = RLEditChallengesAdapter(requireActivity(), dataList) { challengeName ->
            // Handle date selection
            println("Selected date: $challengeName")
        }
        fragBinding.recyclerviewEdit.adapter=adapter

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

}