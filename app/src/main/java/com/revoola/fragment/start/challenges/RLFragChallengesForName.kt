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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.databinding.RlFragChallengesForNameBinding
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.utils.RLPrefManager
import com.google.gson.Gson
import com.revoola.activity.RLMainActivityRL

class RLFragChallengesForName : RLBaseFragment() {
    val TAG: String = RLFragChallengesForName::class.java.simpleName
    lateinit var fragBinding: RlFragChallengesForNameBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengesForName()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragChallengesForNameBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_name, container) as RlFragChallengesForNameBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForName" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.challenge_selectName)
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLcloseFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(challengeType+" Challenge")
        fragBinding.inlayTop.ivDescription.setText(R.string.giveyourchallengename)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        RLUIBottom(challengeType.toString().trim())
    }
    private fun RLUIBottom(challengeType:String) {
        fragBinding.txtHeader.setText("")
        if (challengeType.equals("Steps")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)
        }else if (challengeType.equals("Effort")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_heart)
        }else if (challengeType.equals("Calories")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)
        }else if (challengeType.equals("Distance")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_distance)
        }else if (challengeType.equals("Climbed")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_climb)
        }else if (challengeType.equals("Duration")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)
        }

        fragBinding.edtStepCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called to notify you that somewhere within s, the text is about to be changed
                // Use this method to take any action before the text is actually changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called to notify you that somewhere within s, the text has been changed
                // Use this method to take any action as the text is being changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called to notify you that somewhere within s, the text has been changed
                // Use this method to take any action after the text has been changed
                fragBinding.txtHeader.setText(s.toString())
            }
        })

        fragBinding.btnNext.setOnClickListener{
            val bundle: Bundle = Bundle()
            bundle.putString("challengeType",challengeType)
            (context as RLMainActivityRL).RLloadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
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

        val jsonString= com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.challenge_selectName,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}