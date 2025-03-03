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
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlDialogFriendChallengesBinding
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.databinding.RlFragChallengesForBinding
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.fragment.start.challenges.adapter.RLChallengeForFriendListAdapter
import com.revoola.fragment.start.challenges.adapter.RLChallengeForGroupListAdapter
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsetgroup_data
import com.revoola.model.RLuserData
import com.revoola.model.RLyourGroupDataModel
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import com.google.gson.Gson
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData
import com.revoola.utils.RLPrefManager

class RLFragChallengesFor : RLBaseFragment() {
    val TAG: String = RLFragChallengesFor::class.java.simpleName
    lateinit var fragBinding: RlFragChallengesForBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var selectDataID: List<String> = mutableListOf()

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChallengesFor()
        fragment.arguments = bundle
        return fragment
    }
    private val binding by lazy {
        RlFragChallengesForBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_challenges_for, container) as RlFragChallengesForBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChallengesFor" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom action here
                // For example, show a confirmation dialog or navigate
                RLcloseFragment()
            }
        })
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.challenge_selectFor)
        fragBinding.inlayTop.ivBack.setOnClickListener {
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
        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData
        RLTools.RLheightsetstartimage(fragBinding.relayYou.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayFriends.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroup.cardChalengesst,requireActivity())
        RLTools.RLheightsetstartimage(fragBinding.relayGroupVGroup.cardChalengesst,requireActivity())

        selectDataID = cardData.selectGroupList

        fragBinding.relayYou.imgTypeFull.setImageResource(R.drawable.you)
        fragBinding.relayYou.txtTypeTitle.setText(R.string.you)
        fragBinding.relayYou.imgType.visibility=View.GONE
        fragBinding.relayYou.imgTypeFull.visibility=View.VISIBLE

        fragBinding.relayFriends.imgTypeFull.setImageResource(R.drawable.ic_friends)
        fragBinding.relayFriends.txtTypeTitle.setText(R.string.friends)
        fragBinding.relayFriends.imgType.visibility=View.GONE
        fragBinding.relayFriends.imgTypeFull.visibility=View.VISIBLE

        fragBinding.relayGroup.imgTypeFull.setImageResource(R.drawable.ic_groups)
        fragBinding.relayGroup.txtTypeTitle.setText(R.string.group)
        fragBinding.relayGroup.imgType.visibility=View.GONE
        fragBinding.relayGroup.imgTypeFull.visibility=View.VISIBLE

        fragBinding.relayGroupVGroup.imgType.visibility=View.GONE
        fragBinding.relayGroupVGroup.imgTypeFull.visibility=View.VISIBLE
        fragBinding.relayGroupVGroup.imgTypeFull.setImageResource(R.drawable.ic_group_v_group)
        fragBinding.relayGroupVGroup.txtTypeTitle.setText(R.string.groupvgroup)

        fragBinding.relayYou.cardChalengesst.setOnClickListener {
            cardData.challengeForType="You"
            cardData.TargetType="IndividualTarget"

            val bundle: Bundle = Bundle()
            bundle.putSerializable("cardData",cardData)

            if (cardData.isEditClass){
                (context as RLMainActivityRL).RLloadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
            }else{
                (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true,null, false)
            }

        }
        fragBinding.relayFriends.cardChalengesst.setOnClickListener {
            RLshowFriend()
        }
        fragBinding.relayGroup.cardChalengesst.setOnClickListener {
            RLshowGroup(false)
        }
        fragBinding.relayGroupVGroup.cardChalengesst.setOnClickListener {
            RLshowGroup(true)
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

        val jsonString= com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.challenge_selectFor,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun RLshowGroup(isGroupVGroup:Boolean) {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogFriendChallengesBinding =
            RlDialogFriendChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvTitle.setText("Select Groups")
        dialogMainBinding.tvClose.setOnClickListener {
           dialog.dismiss()
        }

        RLgroupApiCall(dialogMainBinding,isGroupVGroup,dialog)
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

        RLfriendsApiCall(dialogMainBinding,dialog)
        //dialogMainBinding.layYou.txtHeader.setText(getString(R.string.you)+":")
        dialog.show()

    }
    private fun RLNextFragmentOpen(isGroup:Boolean,challengeForType:String,selectUserdata: List<RLuserData>,selectGroupdata: List<RLyourGroupDataModel> ){
        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData
        cardData.challengeForType=challengeForType
        cardData.IsGroup=isGroup
        if (isGroup){
            val selectGroupIDList: List<String> = selectGroupdata.map { it.group_id }
            cardData.selectGroupList= selectGroupIDList
        }else{
            val selectUserIDList: List<String> = selectUserdata.map { it.userid }
            cardData.selectGroupList = selectUserIDList
        }
        val bundle: Bundle = Bundle()
        bundle.putSerializable("cardData",cardData)
        (context as RLMainActivityRL).RLloadFrag(RLFragChallengesForType().newInstance(bundle), TAG, true,null, false)

    }
    private fun RLshowAlertDialog(message:String) {
        val sucDialog:Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText(message)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    //All Api Call
    private fun RLgroupApiCall(dialogMainBinding: RlDialogFriendChallengesBinding,isGroupVGroup:Boolean, dialog: Dialog ) {

        val request = listOf(RLrequestgroup_dataset(group_data = RLsetgroup_data(userid = currentUser,limit = 100, index=0)))
        RLTools.RlLogDPrint(TAG,"setgroupdata= "+request)

        viewModel.RLyourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandleGroupsApi(response.text,dialogMainBinding,isGroupVGroup,dialog)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception) {
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }private fun RLfriendsApiCall(dialogMainBinding: RlDialogFriendChallengesBinding,dialog: Dialog) {
        val request = listOf(RLSetsearch_userrequest(search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        RLTools.RlLogDPrint(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandlefriendsApi(response.text.user,dialogMainBinding,dialog)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandlefriendsApi(userdata: List<RLuserData>, dialogMainBinding: RlDialogFriendChallengesBinding, dialog: Dialog) {
        var selectUserdata: List<RLuserData> = mutableListOf()

        val linearLayoutManager = LinearLayoutManager(activity)
        dialogMainBinding.recyclerFriend.layoutManager = linearLayoutManager

        userdata.forEach { user ->
            if (selectDataID.contains(user.userid)) {
                user.isSelected = true
                selectUserdata += listOf(user)
            }else{
                user.isSelected = false
            }
        }

        val adapter = RLChallengeForFriendListAdapter(activity, userdata) { cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectUserdata += listOf(cardData)
            }else{
                selectUserdata -= listOf(cardData)
            }

        }
        dialogMainBinding.recyclerFriend.adapter = adapter
        dialogMainBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        dialogMainBinding.tvSelect.setOnClickListener {
            if (selectUserdata.size>0){
                RLNextFragmentOpen(false,"Friends",selectUserdata, emptyList())
                dialog.dismiss()
            }else{
                RLshowAlertDialog("Please Select friends")
                dialog.dismiss()
            }

        }

    }
    private fun RLresponsehandleGroupsApi(groupdata: List<RLyourGroupDataModel>, dialogMainBinding: RlDialogFriendChallengesBinding, isGroupVGroup:Boolean, dialog: Dialog ) {
        var selectGroupdata: List<RLyourGroupDataModel> = mutableListOf()
        val linearLayoutManager = LinearLayoutManager(activity)
        dialogMainBinding.recyclerFriend.layoutManager = linearLayoutManager

        groupdata.forEach { user ->
            if (selectDataID.contains(user.group_id)) {
                user.isSelected = true
                selectGroupdata += listOf(user)
            }else{
                user.isSelected = false
            }
        }
        val adaptergroup = RLChallengeForGroupListAdapter(activity,groupdata){ cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectGroupdata += listOf(cardData)
            }else{
                selectGroupdata -= listOf(cardData)
            }

        }
        dialogMainBinding.recyclerFriend.adapter = adaptergroup
        dialogMainBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptergroup.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        dialogMainBinding.tvSelect.setOnClickListener {
            if (isGroupVGroup){
                if (selectGroupdata.size>1){
                    RLNextFragmentOpen(true,"GroupVGroup", emptyList(),selectGroupdata)
                    dialog.dismiss()
                }else{
                    dialog.dismiss()
                    RLshowAlertDialog("Please Select Multi Groups")
                }
            }else  if ( selectGroupdata.size>0){
                RLNextFragmentOpen(true,"Group",emptyList(),selectGroupdata)
                dialog.dismiss()
            }else{
                dialog.dismiss()
                RLshowAlertDialog("Please Select One Group")
            }

        }
    }

}