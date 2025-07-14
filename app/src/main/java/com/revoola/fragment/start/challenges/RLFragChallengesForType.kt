package com.revoola.fragment.start.challenges

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.databinding.RlFragChallengesForTypeBinding
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.utils.RLPrefManager
import com.google.gson.Gson
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData

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
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_type, container) as RlFragChallengesForTypeBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForType" )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom action here
                // For example, show a confirmation dialog or navigate
                rl_bottomHideShowSet(true)
                rl_closeFragment()
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        rl_helpHideShowSet(true, fragBinding.inlayTop.ivhelp, com.revoola.utils.RLPrefManager.challenge_selectTarget)
        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData
        fragBinding.inlayTop.ivBack.setOnClickListener {
            rl_bottomHideShowSet(true)
            rl_closeFragment()
        }
        if (cardData.IsGroup){
            fragBinding.inlayTop.ivTitle.setText("Group Challenge")
        }else{
            fragBinding.inlayTop.ivTitle.setText("Friends Challenge")
        }

        fragBinding.inlayTop.ivDescription.setText(R.string.individualorsharedtarget)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        fragBinding.layIndividualTarget.setOnClickListener {
            cardData.TargetType="IndividualTarget"
            val bundle: Bundle = Bundle()
            bundle.putSerializable("cardData",cardData)
            if (cardData.isEditClass){
                (context as RLMainActivityRL).rl_loadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
            }else{
                (context as RLMainActivityRL).rl_loadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true,null, false)
            }
        }

        fragBinding.laySharedTarget.setOnClickListener {
            cardData.TargetType="SharedTarget"
            val bundle: Bundle = Bundle()
            bundle.putSerializable("cardData",cardData)
            if (cardData.isEditClass){
                (context as RLMainActivityRL).rl_loadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
            }else{
                (context as RLMainActivityRL).rl_loadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true,null, false)
            }

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

        val jsonString= RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.challenge_selectTarget,"")
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