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
import androidx.activity.OnBackPressedCallback
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
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for_name, container) as RlFragChallengesForNameBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesForName" )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom action here
                // For example, show a confirmation dialog or navigate
                RLcloseFragment()
            }
        })
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.challenge_selectName)

        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData

        if (cardData.isEditClass){
            fragBinding.edtStepCount.setText(cardData.ChallengeGivenName)
            fragBinding.txtHeader.setText(cardData.ChallengeGivenName)
            RLTools.RlLogEPrint(TAG,"cardData: ${cardData.selectedDate}")

        }

        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLcloseFragment()
        }
        fragBinding.inlayTop.ivTitle.setText(cardData.ChallengeType+" Challenge")
        fragBinding.inlayTop.ivDescription.setText(R.string.giveyourchallengename)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
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

        val jsonString= RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.challenge_selectName,"")
        val gson = Gson()
       // RLTools.RlLogEPrint(TAG,"jsonString: ${gson.toJson(jsonString)}")
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}