package com.revoola.fragment.start.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragChallengesForTypeBinding
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
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
        RLTools.RLhideShowHelpDialog(
            requireContext(),
            "challenge_selectTargetType",
            fragBinding.inlayTop.ivhelp
        )
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




}