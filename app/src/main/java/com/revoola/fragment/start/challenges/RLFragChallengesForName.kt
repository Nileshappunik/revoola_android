package com.revoola.fragment.start.challenges

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragChallengesForNameBinding
import com.revoola.utils.RLPrefManager
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData

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
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_name, container) as RlFragChallengesForNameBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForName" )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom action here
                // For example, show a confirmation dialog or navigate
                rl_closeFragment()
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {

        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData

        if (cardData.isEditClass){
            fragBinding.edtStepCount.setText(cardData.ChallengeGivenName)
            fragBinding.txtHeader.setText(cardData.ChallengeGivenName)
            RLTools.rl_logEPrint(TAG,"cardData: ${cardData.selectedDate}")

        }

        fragBinding.inlayTop.ivBack.setOnClickListener {
            rl_closeFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(cardData.ChallengeType+" Challenge")
        fragBinding.inlayTop.ivDescription.setText(R.string.giveyourchallengename)
        RLTools.RLhideShowHelpDialog(requireContext(), "challenge_selectName",  fragBinding.inlayTop.ivhelp)
        RLUIBottom(cardData)
    }
    private fun RLUIBottom(cardData:RLEditChallengeAllData) {
        fragBinding.txtHeader.setText("")

        when(cardData.ChallengeType){
            "Steps"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)
            }
            "Effort"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_heart)
            }
            "Calories"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)
            }
            "Distance"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_distance)
            }
            "Climbed"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_climb)
            }
            "Duration"->{
                fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)
            }
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
            val ChallengeGivenName=  fragBinding.edtStepCount.text.toString()
            cardData.ChallengeGivenName=ChallengeGivenName
            val bundle: Bundle = Bundle()
            bundle.putSerializable("cardData",cardData)
            (context as RLMainActivityRL).rl_loadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
        }

    }

}