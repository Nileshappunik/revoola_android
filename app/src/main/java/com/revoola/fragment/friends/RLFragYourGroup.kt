package com.revoola.fragment.friends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.friends.adapter.RLYourGroupListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.*
import com.revoola.fragment.friends.adapter.RLSelectedFriendListAdapter
import com.revoola.fragment.friends.adapter.RLYourFriendSelectListAdapter
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsetgroup_data
import com.revoola.model.RLuserData
import com.revoola.model.RLyourGroupDataModel
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.fragment.start.yourway.RLSessionDataTransferModel
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class RLFragYourGroup : RLBaseFragment() {
    val TAG: String = RLFragYourGroup::class.java.simpleName
    lateinit var fragBinding: RlFragYourGroupBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var isGroup:Boolean=true
    private var selectUserdata: List<RLuserData> = mutableListOf()
    
    private val binding by lazy {
        RlFragYourGroupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_group, container) as RlFragYourGroupBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragYourGroup" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourgroup)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        currentUser=  com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),
            RLMainViewModelFactory(
                userRepository
            )
        ).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLgroupApiCall()
        fragBinding.txtMyGroup.setOnClickListener {
            fragBinding.txtMyGroup.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtMyGroup.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.txtCreateGroup.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.txtCreateGroup.background=null
            fragBinding.txtInviteyourfriend.setText(R.string.invite)
            fragBinding.txtInviteyourfriend.visibility=View.GONE
            fragBinding.demoInvite.visibility=View.GONE
            fragBinding.tvCreate.visibility=View.GONE
            fragBinding.toolbar.tvTitle.setText(R.string.yourgroup)
            isGroup=true
            RLgroupApiCall()
        }

        fragBinding.txtCreateGroup.setOnClickListener {
            fragBinding.txtCreateGroup.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtCreateGroup.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.txtMyGroup.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.txtMyGroup.background=null
            fragBinding.txtInviteyourfriend.setText(R.string.creategroup)
            fragBinding.txtInviteyourfriend.visibility=View.GONE
            fragBinding.demoInvite.visibility=View.GONE
            fragBinding.tvCreate.visibility=View.VISIBLE
            fragBinding.toolbar.tvTitle.setText(R.string.addmembers)
            isGroup=false
            RLfriendsApiCall()
        }

        fragBinding.rvSelectedFriend.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        fragBinding.txtInviteyourfriend.setOnClickListener {
            if (isGroup){
                (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
            }
        }
        fragBinding.tvCreateClick.setOnClickListener {
           //Friend CREATE IMPLEMENT

        }
    }
    private fun RLfriendsApiCall() {
        val request = listOf(
            RLSetsearch_userrequest(
                search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)
            )
        )
        RLTools.RlLogDPrint(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandlefriendsApi(response.text.user)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandlefriendsApi(userdata: List<RLuserData>) {
         selectUserdata = emptyList()
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourgroup.layoutManager = linearLayoutManager

        val adapter = RLYourFriendSelectListAdapter(activity,userdata,fragBinding.tvCreate,fragBinding.tvCreateClick,fragBinding.layInviteCommon) { cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectUserdata += listOf(cardData)
            }else{
                selectUserdata -= listOf(cardData)
            }
            val adapter = RLSelectedFriendListAdapter(activity,selectUserdata)
            fragBinding.rvSelectedFriend.adapter = adapter
        }
        fragBinding.recycleYourgroup.adapter = adapter

        fragBinding.edtGroupSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun RLgroupApiCall() {
        val request = listOf(
            RLrequestgroup_dataset(
                group_data = RLsetgroup_data(userid = currentUser,limit = 100, index=0)
            )
        )
        RLTools.RlLogDPrint(TAG,"setgroupdata= "+request)

        viewModel.RLyourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandleGroupsApi(response.text)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandleGroupsApi(groupdata: List<RLyourGroupDataModel>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourgroup.layoutManager = linearLayoutManager
        val adaptergroup = RLYourGroupListAdapter(activity,groupdata)
        fragBinding.recycleYourgroup.adapter = adaptergroup

        fragBinding.edtGroupSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptergroup.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun createGroupPayload(groupName: String): Map<String, RequestBody> {
        val requestBodyMap = mutableMapOf<String, RequestBody>()
        // Add text fields as form data
        requestBodyMap["data[create_group][group_name]"] = createRequestBody(groupName)
        requestBodyMap["data[create_group][group_id]"] = createRequestBody(currentUser+generateUniqueKey())
        requestBodyMap["data[create_group][child_user][$currentUser]"] = createRequestBody("1")

        // Add selected friends
        for (friend in selectUserdata) {
            if (friend.userid != currentUser) {
                requestBodyMap["data[create_group][child_user][${friend.userid}]"] = createRequestBody("0")
            }
        }
        return requestBodyMap
    }
    fun generateUniqueKey(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32)
    }



}