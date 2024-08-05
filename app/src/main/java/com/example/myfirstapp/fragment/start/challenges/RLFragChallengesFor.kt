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
import com.example.myfirstapp.databinding.RlDialogFriendChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpChallengesForBinding
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragChallengesForBinding
import com.example.myfirstapp.databinding.RlFragSetYourGoalBinding
import com.example.myfirstapp.fragment.start.challenges.model.RLFragChallengesForName
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools

class RLFragChallengesFor : RLBaseFragment() {
    val TAG: String = RLFragChallengesFor::class.java.simpleName
    lateinit var fragBinding: RlFragChallengesForBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengesFor()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragChallengesForBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for, container) as RlFragChallengesForBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesFor" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.inlayTop.ivBack.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            RLcloseFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(R.string.challengesfor)
        fragBinding.inlayTop.ivDescription.setText(R.string.challengeforyouorwithothers)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        RLUIBottom()
    }
    private fun RLUIBottom() {

        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()

        RLTools.RLheightsetstartimage(fragBinding.relayYou.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayFriends.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroup.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroupVGroup.cardChalengesst,requireActivity())

        fragBinding.relayYou.imgType.setImageResource(R.drawable.you)
        fragBinding.relayYou.txtTypeTitle.setText(R.string.you)

        fragBinding.relayFriends.imgType.setImageResource(R.drawable.ic_friends)
        fragBinding.relayFriends.txtTypeTitle.setText(R.string.friends)

        fragBinding.relayGroup.imgType.setImageResource(R.drawable.ic_groups)
        fragBinding.relayGroup.txtTypeTitle.setText(R.string.group)

        fragBinding.relayGroupVGroup.imgType.visibility=View.GONE
        fragBinding.relayGroupVGroup.imgTypeFull.visibility=View.VISIBLE
        fragBinding.relayGroupVGroup.imgTypeFull.setImageResource(R.drawable.ic_group_v_group)
        fragBinding.relayGroupVGroup.txtTypeTitle.setText(R.string.groupvgroup)

        fragBinding.relayYou.cardChalengesst.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)

        }
        fragBinding.relayFriends.cardChalengesst.setOnClickListener {
            RLshowFriend()
        }
        fragBinding.relayGroup.cardChalengesst.setOnClickListener {

        }
        fragBinding.relayGroupVGroup.cardChalengesst.setOnClickListener {

        }

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



        dialogMainBinding.layYou.txtHeader.setText(getString(R.string.you)+":")
        dialogMainBinding.layYou.txtHeaderDescription.setText("SET YOURSELF A PERSONAL CHALLENGE.")
        dialogMainBinding.layYou.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)

        dialogMainBinding.layFriends.txtHeader.setText(getString(R.string.friends)+":")
        val friendDescription="SET A CHALLENGE FOR YOU AND YOUR FRIENDS. WHERE EACH OF YOU ARE CHALLENGED TO ACHIEVE THE SAME GOAL."
        dialogMainBinding.layFriends.txtHeaderDescription.setText(friendDescription)
        dialogMainBinding.layFriends.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layGroups.txtHeader.setText(getString(R.string.groups)+":")
        val groupDescription="JUST LIKE A FRIENDS CHALLENGE YOU CAN CHALLENGE ALL THE MEMBERS OF ANY OF THE GROUPS THAT YOU ARE A MEMBER OF, TO ACHIEVE THE SAME GOAL."
        dialogMainBinding.layGroups.txtHeaderDescription.setText(groupDescription)
        dialogMainBinding.layGroups.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layGroupsVGroups.txtHeader.setText(getString(R.string.groupvgroup)+":")
        val groupvDescription="GROUP V GROUP CHALLENGES ARE WHERE A GROUP IS COLLECTIVELY TRYING TO BEAT THE OTHER GROUP TO ACHIEVE THE GOAL. FOR INSTANCE, CAN GROUP AACHIEVE 100,000 STEPS BEFORE GROUP B?"
        dialogMainBinding.layGroupsVGroups.txtHeaderDescription.setText(groupvDescription)
        dialogMainBinding.layGroupsVGroups.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)


        dialog.show()

    }

    private fun RLshowFriend() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogFriendChallengesBinding =
            RlDialogFriendChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        //dialogMainBinding.layYou.txtHeader.setText(getString(R.string.you)+":")


        dialog.show()

    }

}