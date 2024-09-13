package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpStartBinding
import com.example.myfirstapp.databinding.RlFragChallengesForTypeBinding
import com.example.myfirstapp.fragment.start.RLStartHelpModel
import com.example.myfirstapp.fragment.start.adapter.RLHelpListAdapter
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson

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
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_type, container) as RlFragChallengesForTypeBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForType" )

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.challenge_selectTarget)
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        val isGroup = requireArguments().getBoolean("IsGroup")
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLBottomHideShowSet(true)
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
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)

        }

        fragBinding.laySharedTarget.setOnClickListener {
            val bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)
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

        val jsonString= RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.challenge_selectTarget,"")
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