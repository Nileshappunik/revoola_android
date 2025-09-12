package com.revoola.fragment.friends

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.databinding.RlFriendInviteGroupDialogBinding
import com.revoola.enumclass.RLFriendsFollowType
import com.revoola.fragment.friends.adapter.RLYourFriendListAdapter
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLuserData
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import java.util.ArrayList


class RLFriendInviteGroupDialog() : DialogFragment() {
    private val TAG: String = RLFriendInviteGroupDialog::class.java.simpleName
    private lateinit var fragBinding: RlFriendInviteGroupDialogBinding
    private lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var arrInvited: MutableList<String> = mutableListOf()
    private var memberIds: MutableList<String> = mutableListOf()

    companion object {
        private const val ARG_GROUP_ID = "GroupID"
        private const val ARG_GroupName = "groupName"
        private const val ARG_USER_LIST = "UserList"
        // New instance method to pass data
        fun newInstance(bundle: Bundle?): RLFriendInviteGroupDialog {
            val fragment = RLFriendInviteGroupDialog()
            fragment.arguments = bundle
            return fragment
        }
    }
    private val currentUser by lazy {
        RLAuthManager().rl_getCurrentUser()?.uid?:""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        fragBinding = RlFriendInviteGroupDialogBinding.inflate(inflater, container, false)
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        setupUI()
        return fragBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    private fun setupUI() {
        val userIdList =requireArguments().getStringArrayList(ARG_USER_LIST)
        if (!userIdList.isNullOrEmpty()){
            arrInvited.addAll(userIdList)
            memberIds.addAll(userIdList)
        }
        rl_friendsApiCall()
    }
    private fun rl_friendsApiCall() {
        val request = listOf(
            RLSetsearch_userrequest(
                search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)
            ))
        RLTools.rl_logDPrint(TAG,"request FriendsData: ${Gson().toJson(request)}")

        viewModel.rl_friendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG,"Success FriendsData: ${Gson().toJson(response)}")
                    if (response.type.equals("success")){
                        val filteredUserData = response.text.user.filterNot { it.userid in memberIds }
                        rl_responsehandlefriendsApi(filteredUserData)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch FriendsData: ${e.message} ")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error FriendsData: ${error.message} " )
            }
        }
    }
    private fun rl_responsehandlefriendsApi(filteredUserData: List<RLuserData>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,filteredUserData,
            RLFriendsFollowType.InviteGroupFriend, onItemClick = { userCardData, FriendsAPIStatusType ->
                RLTools.rl_logDPrint(TAG,"Unfollow Click:- ${Gson().toJson(userCardData)}")
                inviteToJoinClicked(userCardData)
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
    private  fun inviteToJoinClicked(selectedUser:RLuserData) {
        val groupID =requireArguments().getString(ARG_GROUP_ID)
        val groupName =requireArguments().getString(ARG_GroupName)
        selectedUser.let { _selectedUser ->
            if (_selectedUser.userid !in arrInvited) {
                arrInvited.add(_selectedUser.userid)
                if (_selectedUser.userid !in memberIds) {
                   memberIds.add(_selectedUser.userid)
                    val members = mutableMapOf<String, Boolean>()
                    for (member in memberIds) {
                        members[member] = true
                    }
                    RLDatabaseManagerWrite().rl_update_All_Data(RevoolaFirebasePath.groupMemberPathWrite(groupID.toString()), members)
                }

                val groupO = mapOf(
                    "join" to System.currentTimeMillis() / 1000,
                    "status" to 0,
                    "remark" to "android",
                    "name" to groupName
                )
                RLDatabaseManagerWrite().rl_update_All_Data(RevoolaFirebasePath.groupPathWrite(_selectedUser.userid,groupID.toString()), groupO)
                saveDataForGroup(groupID.toString(), listOf(_selectedUser.userid))
            }else {
                rl_conformationalertDialog()
            }
        }
    }
    private fun saveDataForGroup(groupId: String, members: List<String>) {
        val _members = mutableListOf<Map<String, Any>>()
        for (member in members) {
            val user = mapOf(
                "userid" to member,
                "is_admin" to 0
            )
            _members.add(user)
        }
        val groupIdMap = mapOf(
            "users" to _members,
            "is_friends_group" to false,
            "group_id" to groupId
        )
        val params = listOf(mapOf("group_users" to groupIdMap))

        RLTools.rl_logDPrint(TAG,"request followAll: ${Gson().toJson(params)}")
        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG,"response followAll: ${Gson().toJson(response)}")
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch followAll: ${e.message}")
                }
                //rl_friendsApiCall()
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG,"Error followAll:${error.localizedMessage} ")
              //  rl_friendsApiCall()
            }
        }
    }
    private fun rl_conformationalertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Alert!")
            .setMessage("Already Invited.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}