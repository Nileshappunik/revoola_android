package com.example.myfirstapp.fragment.start.challenges

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
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpChallengesForBinding
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragChallengesForNameBinding
import com.example.myfirstapp.databinding.RlFragChallengesForTypeBinding
import com.example.myfirstapp.utils.RLPrefManager

class RLFragChallengesForType : RLBaseFragment() {
    val TAG: String = RLFragChallengesForType::class.java.simpleName
    lateinit var fragBinding: RlFragChallengesForTypeBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengesForType()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragChallengesForTypeBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_type, container) as RlFragChallengesForTypeBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForType" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        val isGroup = requireArguments().getBoolean("IsGroup")
        fragBinding.inlayTop.ivBack.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            RLcloseFragment()
        }
        if (isGroup){
            fragBinding.inlayTop.ivTitle.setText("Group Challenge")
        }else{
            fragBinding.inlayTop.ivTitle.setText("Friends Challenge")
        }

        fragBinding.inlayTop.ivDescription.setText(R.string.individualorsharedtarget)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        fragBinding.layIndividualTarget.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)

        }

        fragBinding.laySharedTarget.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)
        }
    }

    private fun RLshowHelpDialog() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpSetyourgoalBinding = RlDialogHelpSetyourgoalBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogMainBinding.laySartdate.txtHeader.setText(R.string.pleaseenterstartdate)
        dialogMainBinding.laySartdate.txtHeaderDescription.setText(R.string.selecttosetthedatyouwantstart)
        dialogMainBinding.laySartdate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layEnddate.txtHeader.setText(R.string.pleaseenterenddate)
        dialogMainBinding.layEnddate.txtHeaderDescription.setText(R.string.selecttosetthedayuoyend)
        dialogMainBinding.layEnddate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialog.show()

    }


}