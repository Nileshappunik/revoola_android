package com.revoola.fragment.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.friends.adapter.RLYourFriendListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.api.RLNetworkService
import com.revoola.databinding.*
import com.revoola.model.RLSetget_followers
import com.revoola.model.RLSetget_followersrequest
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLsearch_user_request
import com.revoola.model.RLsearch_userrequest
import com.revoola.model.RLuserData
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.enumclass.FriendsAPIStatusType
import com.revoola.fragment.friends.model.RLFriendsUpdateApiPayload
import com.revoola.fragment.friends.model.RLUpdateContactData
import com.revoola.fragment.friends.model.RLUsersContactsMk2Update
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RLFragYourFriends : RLBaseFragment() {
    private val TAG: String = RLFragYourFriends::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragYourFriends()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragYourFriendsBinding.inflate(layoutInflater)
    }
    private val currentUser by lazy {
        RLAuthManager().rl_getCurrentUser()?.uid?:""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourfriends)
        rl_onBackPresAct(fragBinding.toolbar.ivBack)
       // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        fragBinding.txtFriendYoufollow.setOnClickListener {
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (apiClientRetrofit.rl_isConnected()) {
                rl_youFollowApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }

        fragBinding.txtFriendFollowingyou.setOnClickListener {
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (apiClientRetrofit.rl_isConnected()) {
                rl_followingYouApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }


        if (apiClientRetrofit.rl_isConnected()) {
            rl_youFollowApiCall()
            rl_getFriendRequestApiCall()
        } else {
            rl_showDialogFullscreen()
        }

//        val reDirecDeepLinkPage=requireArguments().getBoolean("reDirecDeepLinkPage")
//        if (reDirecDeepLinkPage){
//            if (apiClientRetrofit.rl_isConnected()) {
//                rl_getFriendRequestApiCall()
//            } else {
//                rl_showDialogFullscreen()
//            }
//        }
//        else{
//            if (apiClientRetrofit.rl_isConnected()) {
//                rl_youFollowApiCall()
//            } else {
//                rl_showDialogFullscreen()
//            }
//        }
    }
    private fun rl_youFollowApiCall() {
        val request = listOf(RLSetsearch_userrequest(
            search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"Fried Request:- ${Gson().toJson(request)}")

        viewModel.rl_friendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                       rl_responsehandle(response.text.user,true)
                    }
                    RLTools.rl_logDPrint(TAG,"Friend Response:- ${Gson().toJson(response)}")
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Friend Catch:- ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG,"Friend Error:- ${error.message}")
            }
        }
    }
    private fun rl_followingYouApiCall() {
        val request = listOf(RLSetget_followersrequest(
            search_user = RLSetget_followers(get_followers = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"get followers Request:- ${Gson().toJson(request)}")

        viewModel.rl_friendsFollowingYou(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        rl_responsehandle(response.text.user,false)
                    }
                    RLTools.rl_logDPrint(TAG,"get followers Success:- ${Gson().toJson(response)}")
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"get followers Catch:- ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG,"get followers Error:- ${error.message}")
            }
        }
    }
    private fun rl_responsehandle(userdata: List<RLuserData>, youFollow:Boolean) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata,false, onItemClick = { userData ->
            RLTools.rl_logDPrint(TAG,"Unfollow Click:- ${Gson().toJson(userData)}")
            //updateStatusForUser(userData)
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
    private fun rl_getFriendRequestApiCall() {
        val request = listOf(RLsearch_userrequest(
            search_user = RLsearch_user_request(myid = currentUser,contact_status=2,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"get Friend Request:- ${Gson().toJson(request)}")

        viewModel.rl_search_user_Data_DeepLink(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        rl_responsehandle(response.text.user,true)
                    }
                    RLTools.rl_logDPrint(TAG,"get Friend Request Response:- ${Gson().toJson(response)}")
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"get Friend Request Catch:- ${e.message}")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"get Friend Request Error:- ${error.message}")
            }
        }
    }

//------------------------------------------------------------------------------
    private fun updateStatusForUser(user: RLuserData, status: FriendsAPIStatusType) {
    val params = mutableListOf<Map<String, Any>>()

    val myUser = mapOf(
        "myidstatus" to if (status == FriendsAPIStatusType.Accepted) status.value else user.myidstatus,
        "contact_userid" to user.userid,
        "contact_status" to if (status == FriendsAPIStatusType.Accepted) user.theiridstatus else status.value
    )

    val otherUser = mapOf(
        "myidstatus" to if (status == FriendsAPIStatusType.Accepted) user.theiridstatus else status.value,
        "contact_userid" to RLAuthManager().rl_getCurrentUser()?.uid,
        "contact_status" to if (status == FriendsAPIStatusType.Accepted) status.value else user.myidstatus
    )

    val mySearchUser = mapOf(
        "myid" to RLAuthManager().rl_getCurrentUser()?.uid,
        "contact_data" to listOf(myUser)
    )

    val otherSearchUser = mapOf(
        "myid" to user.userid,
        "contact_data" to listOf(otherUser)
    )

    params.add(mapOf("users_contacts_mk2" to mySearchUser))
    params.add(mapOf("users_contacts_mk2" to otherSearchUser))

    RLTools.rl_logDPrint(TAG,"Insert Friends Request: ${Gson().toJson(params)}")
    viewModel.rl_updateFriendsData(params) { result ->
        result.onSuccess { response ->
            RLBaseProgress.rl_hideProgressDialog()
            try {
                RLTools.rl_logDPrint(TAG,"Insert Friends Success: ${Gson().toJson(response) }")
            }catch (e:Exception){
                e.printStackTrace()
                RLTools.rl_logDPrint(TAG,"Insert Friends Catch: ${e.message}")
            }
        }.onFailure { error ->
            RLBaseProgress.rl_hideProgressDialog()
            RLTools.rl_logDPrint(TAG,"Insert Friends Error: ${error.message}")
        }
    }

    when (status) {
        FriendsAPIStatusType.Follow, FriendsAPIStatusType.Invite, FriendsAPIStatusType.Blocked -> {
            deleteDataForFriends(listOf(user.userid))
        }
        FriendsAPIStatusType.Invited, FriendsAPIStatusType.Requested -> {
            RELMoengageManager.sendRequest(user.userid)
        }
        FriendsAPIStatusType.Accepted -> {
            saveDataForFriends(listOf(user.userid))
            RELMoengageManager.acceptRequest(user.userid)
        }
    }
}

    private fun acceptAndFollowBack(user: RLuserData) {
        val params = mutableListOf<Map<String, Any>>()
        val myUser = mapOf(
            "myidstatus" to "3",
            "contact_userid" to user.userid,
            "contact_status" to "3"
        )

        val otherUser = mapOf(
            "myidstatus" to "3",
            "contact_userid" to  RLAuthManager().rl_getCurrentUser()?.uid,
            "contact_status" to "3"
        )

        val mySearchUser = mapOf(
            "myid" to  RLAuthManager().rl_getCurrentUser()?.uid,
            "contact_data" to listOf(myUser)
        )

        val otherSearchUser = mapOf(
            "myid" to user.userid,
            "contact_data" to listOf(otherUser)
        )

        params.add(mapOf("users_contacts_mk2" to mySearchUser))
        params.add(mapOf("users_contacts_mk2" to otherSearchUser))

        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                RLBaseProgress.rl_hideProgressDialog()
                try {
                    RLTools.rl_logDPrint(TAG,"Update Friends Success: ${Gson().toJson(response) }")
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Update Friends Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logDPrint(TAG,"Update Friends Error: ${error.message}")
            }
        }
        RELMoengageManager.shared().acceptRequest(user.uid)
    }

    private fun saveDataForFriends(members: List<String>) {
        val currentUserId = RLAuthManager().rl_getCurrentUser()?.uid?:""

        for (member in members) {
            val user = mapOf(
                "userid" to currentUserId,
                "is_admin" to 0
            )
            val groupId = mapOf(
                "users" to listOf(user),
                "is_friends_group" to true,
                "group_id" to "${member}_friends"
            )
            val params = listOf(mapOf("group_users" to groupId))

            RLTools.rl_logDPrint(TAG,"save Data Friends Request: ${Gson().toJson(params)}")
            viewModel.rl_updateFriendsData(params) { result ->
                result.onSuccess { response ->
                    RLBaseProgress.rl_hideProgressDialog()
                    try {
                        RLTools.rl_logDPrint(TAG,"save Data  Friends Success: ${Gson().toJson(response) }")
                    }catch (e:Exception){
                        e.printStackTrace()
                        RLTools.rl_logDPrint(TAG,"save Data  Friends Catch: ${e.message}")
                    }
                }.onFailure { error ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logDPrint(TAG,"save Data  Friends Error: ${error.message}")
                }
            }
        }
    }
    private fun deleteDataForFriends(members: List<String>) {
        val currentUserId = RLAuthManager().rl_getCurrentUser()?.uid?:""
        val groupId = mapOf(
            "userid" to members,
            "groupid" to "${currentUserId}_friends"
        )
        val params = listOf(mapOf("delete" to groupId))
        RLTools.rl_logDPrint(TAG,"delete Data Friends Request: ${Gson().toJson(params)}")
        viewModel.rl_updateFriendsData(params) { result ->
            result.onSuccess { response ->
                RLBaseProgress.rl_hideProgressDialog()
                try {
                    RLTools.rl_logDPrint(TAG,"delete Data  Friends Success: ${Gson().toJson(response) }")
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"delete Data  Friends Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLBaseProgress.rl_hideProgressDialog()
                RLTools.rl_logDPrint(TAG,"delete Data  Friends Error: ${error.message}")
            }
        }
    }


}