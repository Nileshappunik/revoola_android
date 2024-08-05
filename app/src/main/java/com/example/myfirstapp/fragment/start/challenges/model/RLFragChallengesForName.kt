package com.example.myfirstapp.fragment.start.challenges.model

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpChallengesForBinding
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragChallengesForBinding
import com.example.myfirstapp.databinding.RlFragChallengesForNameBinding
import com.example.myfirstapp.databinding.RlFragSetYourGoalBinding
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools

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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_name, container) as RlFragChallengesForNameBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForName" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        fragBinding.inlayTop.ivBack.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
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

    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpChallengesForBinding =
            RlDialogHelpChallengesForBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }


        dialogMainBinding.layYou.txtHeader.setText(R.string.nameyourchallenge)
        val description="it's important to give you challenge name this is what everyone will see when they are invited to join the challenge and will be how they can identify your challenge in their feed."
        dialogMainBinding.layYou.txtHeaderDescription.setText(description)
        dialogMainBinding.layYou.imgHelpChallenges.setImageResource(R.drawable.ic_challenge_flag)

        dialogMainBinding.layFriends.cardChallenge.visibility=View.GONE
        dialogMainBinding.layGroups.cardChallenge.visibility=View.GONE
        dialogMainBinding.layGroupsVGroups.cardChallenge.visibility=View.GONE

        dialog.show()

    }


}