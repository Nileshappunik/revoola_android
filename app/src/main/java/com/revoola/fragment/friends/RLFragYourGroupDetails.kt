package com.revoola.fragment.friends

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.fragment.friends.adapter.RLYourFriendListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.*
import com.revoola.model.RLSetGroupMemberData
import com.revoola.model.RLSetGroupMemberRequest
import com.revoola.model.RLuserData
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.enumclass.RLFriendsFollowType
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourGroupDetails : RLBaseFragment() {
    companion object{
        private val TAG: String = RLFragYourGroupDetails::class.java.simpleName
    }
    private lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

    private val fragBinding by lazy {
        RlFragYourGroupDetailsBinding.inflate(layoutInflater)
    }
    private val currentUser by lazy {
        RLAuthManager().rl_getCurrentUser()?.uid?:""
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragYourGroupDetails()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourGroupDetails" )
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        fragBinding.toolbar.tvTitle.setText(R.string.groupdetails)
        rl_onBackPresAct(fragBinding.toolbar.ivBack)

        val groupID=requireArguments().getString("GroupID")
        val groupName=requireArguments().getString("GroupName")
        val groupAvatar=requireArguments().getString("GroupAvatar")
        val groupMember=requireArguments().getString("GroupMember")
        val is_admin=requireArguments().getInt("is_admin")

        when(is_admin){
            1->{
                fragBinding.txtGroupName.setText("$groupName (ADMIN)")
            }
            0->{
                fragBinding.txtGroupName.setText(groupName)
            }
        }

        fragBinding.txtGroupMembers.setText("NUMBER OF NUMBERS $groupMember")

        if (!groupAvatar.isNullOrEmpty()) {
            Glide.with(requireContext()).load(groupAvatar)
                .placeholder(R.drawable.sample_user)
                .error(R.drawable.sample_user)
                .into(fragBinding.imageGroup)
        }
        else {
            fragBinding.imageGroup.setImageBitmap(RLTools.getInitialsBitmap(requireContext(),groupName?: "Group"))
        }
        if (apiClientRetrofit.rl_isConnected()) {
            rl_groupDataApiCall(groupID.toString())
        }

    }
    private fun rl_groupDataApiCall(groupID:String) {
        val request = listOf(RLSetGroupMemberRequest(group_data = RLSetGroupMemberData(groupid = groupID,current_userid = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"groupData request: ${Gson().toJson(request)}")

        viewModel.rl_groupMembers(request) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG,"response groupData: ${Gson().toJson(response)}")
                    if (response.type.equals("success")){
                        rl_responsehandle(response.text)
                        rlclickEvent(groupID,response.text)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch groupData: ${e.message}")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error groupData:${error.message} ")
            }
        }
    }

    private fun rlclickEvent(groupID: String, userData: List<RLuserData>) {
        val groupMember: List<String> = userData
            .map { it.userid }
            .filter { it.isNotBlank() }
        fragBinding.txtRevoolaAll.setOnClickListener {
            deleteDataForGroup(groupID,groupMember)
        }
    }

    private fun rl_responsehandle(userdata: List<RLuserData>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata,
            RLFriendsFollowType.YourGroup, onItemClick = { userData, FriendsAPIStatusType ->
            RLTools.rl_logDPrint(TAG,"Unfollow Click:- ${Gson().toJson(userData)}")
        })
        fragBinding.recycleYourfriend.adapter = adapter
        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun deleteDataForGroup(groupId: String, members: List<String>) {
        val groupIdMap = mapOf("userid" to members, "groupid" to groupId)
        val params = listOf(mapOf("delete" to groupIdMap))
        RLTools.rl_logDPrint(TAG,"request deleteGroup: ${Gson().toJson(params)}")
        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                rl_conformationalertDialog()
                try {
                    RLTools.rl_logDPrint(TAG,"response deleteGroup: ${Gson().toJson(response)}")
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch deleteGroup: ${e.message}")
                }
            }.onFailure { error ->
                rl_conformationalertDialog()
                RLTools.rl_logDPrint(TAG,"Error deleteGroup:${error.localizedMessage} ")
            }
        }
    }

    private fun rl_conformationalertDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Group deleted successfully")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                rl_closeFragment()
            }
            .show()
    }
}