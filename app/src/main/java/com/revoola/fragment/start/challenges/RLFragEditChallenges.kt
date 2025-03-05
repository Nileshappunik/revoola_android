package com.revoola.fragment.start.challenges

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.commonobject.RLTools
import com.google.gson.Gson
import com.revoola.RLBaseProgress
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragEditChallengesBinding
import com.revoola.fragment.start.RLFragStart
import com.revoola.fragment.start.challenges.adapter.RLEditChallengesAdapter
import com.revoola.fragment.start.challenges.model.RLEditChallenge
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData
import com.revoola.model.RLChallengePayload
import com.revoola.model.RLChallengesApiPayload
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragEditChallenges : RLBaseFragment() {
    val TAG: String = RLFragEditChallenges::class.java.simpleName
    lateinit var fragBinding: RlFragEditChallengesBinding
    var CurrentUserID:String=""
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

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
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragEditChallenges" )
        CurrentUserID=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLshowAlertDialog("Do you want to discard the changes?")
            }
        })
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.ivhelp, RLPrefManager.start_help_content)
        fragBinding.ivBack.setOnClickListener {
            RLBottomHideShowSet(true)
            RLshowAlertDialog("Do you want to discard the changes?")
        }

        fragBinding.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData
        var isTagetEditable:Boolean=false
        if (cardData.challengeForType.toLowerCase().equals("you")){
            isTagetEditable=false
        }else{
            isTagetEditable=true
        }

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerviewEdit.layoutManager = linearLayoutMain
        val  dataList: MutableList<RLEditChallenge> = mutableListOf(
            RLEditChallenge(R.drawable.ic_award,cardData.ChallengeGivenName,"","RLFragChallengesForName",true),
            RLEditChallenge(RLTools.RLChallengeIcon(cardData.ChallengeType),"${cardData.stepCount} ${cardData.ChallengeType}","","RLFragSetYourGoal",true),
            RLEditChallenge(RLTools.RLChallengeTargetIcon(cardData.TargetType),cardData.TargetType,"","RLFragChallengesForType",isTagetEditable),
            RLEditChallenge(RLTools.RLChallengeForIcon(cardData.challengeForType),cardData.challengeForType,"","RLFragChallengesFor",true),
            RLEditChallenge(RLTools.RLCalendetIcon(cardData.CalenderType),cardData.CalenderType,cardData.selectedDate,"RLFragSetYourGoal",true)
        )

        val adapter = RLEditChallengesAdapter(requireActivity(), dataList) { challengeName ->
            // Handle date selection
            when(challengeName){
                "RLFragChallengesForName"->{
                    val bundle: Bundle = Bundle()
                    cardData.isEditClass=true
                    bundle.putSerializable("cardData",cardData)
                    (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true,null, false)
                }
                "RLFragSetYourGoal"->{
                    val bundle: Bundle = Bundle()
                    cardData.isEditClass=true
                    bundle.putSerializable("cardData",cardData)
                    (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true,null, false)
                }
                "RLFragChallengesForType"->{
                    val bundle: Bundle = Bundle()
                    cardData.isEditClass=true
                    bundle.putSerializable("cardData",cardData)
                    (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForType().newInstance(bundle), TAG, true,null, false)

                }
                "RLFragChallengesFor"->{
                    val bundle: Bundle = Bundle()
                    cardData.isEditClass=true
                    bundle.putSerializable("cardData",cardData)
                    (context as RLMainActivityRL).RLloadFrag(RLFragChallengesFor().newInstance(bundle), TAG, true,null, true)
                }
            }
        }
        fragBinding.recyclerviewEdit.adapter=adapter

        fragBinding.btnDone.setOnClickListener {
            if (isAdded){
                RLBaseProgress.RLShowProgressDialog(requireActivity())
            }
            val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
            val userData= RLGetUserDetails(requireContext())
            val challengePayload=RLChallengePayload()

            when (cardData.challengeForType.toLowerCase()){
                "you"-> {
                    challengePayload.groupongroup = "false"
                    challengePayload.group = "false"
                }
                "friends"-> {
                    challengePayload.groupongroup = "false"
                    challengePayload.group = "false"
                }
                "group"-> {
                    when (cardData.TargetType.toLowerCase()){
                        "individualtarget"->  challengePayload.groupongroup = "false"
                        "sharedtarget"->  challengePayload.groupongroup = "true"
                    }
                    challengePayload.group = "true"
                }
                "groupvgroup"-> {
                    challengePayload.groupongroup = "true"
                    challengePayload.group = "true"
                }
            }
            challengePayload.groupid_userid = cardData.selectGroupList
            challengePayload.metric =cardData.ChallengeType
            challengePayload.creationdate = currentTimestamp
            challengePayload.startdate = RLTools.RlconvertDateToTimestamp(cardData.fromDate)
            challengePayload.enddate = RLTools.RlconvertDateToTimestamp(cardData.toDate)
            challengePayload.goalvalue = cardData.stepCount
            challengePayload.max = "false"
            challengePayload.challenger = "${userData?.firstName ?: ""} ${userData?.lastName ?: ""}"
            challengePayload.displayImage = userData?.displayImage?:""
            challengePayload.typeOfSelect =cardData.challengeForType // chekc friend ,you group ,and
            challengePayload.targetType = RLTools.RLChallengeTargetName(cardData.TargetType)
            challengePayload.challenge_name = cardData.ChallengeGivenName
            challengePayload.day_type = cardData.CalenderType
            challengePayload.isChallengeEdit= cardData.isEditClass
            val payLoad = createGoaledChallenges(cardData,challengePayload)
            if (payLoad!=null){
                RLTools.RlLogDPrint(TAG,"Payload: ${Gson().toJson(payLoad)}")
                val cardRequestData = Gson().fromJson(Gson().toJson(payLoad), Array<RLChallengesApiPayload>::class.java).toList()
                RLInsertApiCall(cardRequestData)
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

        val jsonString= RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.challenge_selectTarget,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun RLshowAlertDialog(message:String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_subscribe)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.tvSubscribe)
        val iv_Cancle: TextView = sucDialog.findViewById(R.id.tvCancel)
        val tvMainMessage: TextView = sucDialog.findViewById(R.id.tvMainMessage)


        tvMainMessage.setText(message)
        iv_ok.setText("OK")
        iv_Cancle.setText("CANCEL")

        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
            (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, false,null, false)
        })
        iv_Cancle.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    private fun createGoaledChallenges(cardData:RLEditChallengeAllData,challengePayload: RLChallengePayload): Any? {
        val body: List<Map<String, Any>>?
        val groupid_userid =if (challengePayload.groupid_userid.isEmpty()) listOf<String>(CurrentUserID) else challengePayload.groupid_userid
       var scenario = 1
        when (cardData.CalenderType.toLowerCase()) {
            "daily" -> scenario = 1
            "weekly" -> scenario = 2
            "monthly" -> scenario = 3
            "custom" -> scenario = 0
        }
        if (cardData.CalenderType.toLowerCase().equals("custom")){
            body = listOf(mapOf(
                "goaled_challenges_new" to mapOf(
                    "challengeadmin" to CurrentUserID,
                    "groupid_userid" to groupid_userid,
                    "groupongroup" to challengePayload.groupongroup,
                    "group" to challengePayload.group,
                    "metric" to challengePayload.metric,
                    "creationdate" to challengePayload.creationdate,
                    "startdate" to challengePayload.startdate,
                    "enddate" to challengePayload.enddate,
                    "goalvalue" to challengePayload.goalvalue,
                    "max" to challengePayload.max,
                    "challenge_name" to challengePayload.challenge_name,
                    "challenger" to challengePayload.challenger,
                    "challenger_avatar" to challengePayload.displayImage,
                    "scenario" to scenario,
                    "targettype" to challengePayload.targetType
                )
            ))
        }else{
            body = listOf(mapOf(
                "goaled_challenges_new" to mapOf(
                    "challengeadmin" to CurrentUserID,
                    "groupid_userid" to groupid_userid,
                    "groupongroup" to challengePayload.groupongroup,
                    "group" to challengePayload.group,
                    "metric" to challengePayload.metric,
                    "creationdate" to challengePayload.creationdate,
                    "startdate" to challengePayload.startdate,
                    "enddate" to (challengePayload.enddate),
                    "goalvalue" to challengePayload.goalvalue,
                    "max" to challengePayload.max,
                    "challenge_name" to challengePayload.challenge_name,
                    "challenger" to challengePayload.challenger,
                    "challenger_avatar" to challengePayload.displayImage,
                    "scenario" to scenario,
                    "dwmstart" to challengePayload.startdate,
                    "dwmend" to challengePayload.enddate,
                    "targettype" to challengePayload.targetType
                )
            ))
        }
        if (body == null) {
            return null
        }
        return body
    }


    private fun RLInsertApiCall(request: List<RLChallengesApiPayload>) {
        if (RLApiClientRetrofit.RLisConnected()) {
            RLTools.RlLogDPrint(TAG,"Challenges Insert Request: $request")
            //Insert Api Call
            viewModel.RLInsertChallenges(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogDPrint(TAG, "Challenges Insert Success= ${response.text}")
                            RLBaseProgress.RLhideProgressDialog()
                            (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, false,null, false)
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "Challenges Insert Fail= ${response.text}")
                        }
                    } catch (e: Exception) {
                        RLBaseProgress.RLhideProgressDialog()
                        e.printStackTrace()
                        RLTools.RlLogEPrint(TAG, "Challenges Insert Catch= ${e.message}" )

                    }
                }.onFailure { error ->
                    RLBaseProgress.RLhideProgressDialog()
                    RLTools.RlLogEPrint(TAG, "Challenges Insert Error= ${error.message}" )
                }
            }
        }

    }


}