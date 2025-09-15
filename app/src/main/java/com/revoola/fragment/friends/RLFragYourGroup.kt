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
import com.google.gson.Gson
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
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.fragment.friends.model.RLCreateGroupModel
import com.revoola.model.RLUserDataParcelable
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourGroup : RLBaseFragment() {
    companion object{
        private val TAG: String = RLFragYourGroup::class.java.simpleName
    }
    private lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var isGroup:Boolean=true
    private var selectUserdata: List<RLuserData> = mutableListOf()
    
    private val fragBinding by lazy {
        RlFragYourGroupBinding.inflate(layoutInflater)
    }
    private val currentUser by lazy {
        RLAuthManager().rl_getCurrentUser()?.uid?:""
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity,RLPrefManager.current_fragment,"RLFragYourGroup" )
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        fragBinding.toolbar.tvTitle.setText(R.string.yourgroup)
        fragBinding.toolbar.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            rl_bottomHideShowSet(true)
        }
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_info)
        RLTools.RLhideShowHelpDialog(requireContext(), "your_groups",  fragBinding.toolbar.ivNotification)

        rl_groupApiCall()
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
            rl_groupApiCall()
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
            rl_friendsApiCall()
        }

        fragBinding.rvSelectedFriend.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        fragBinding.tvCreateClick.setOnClickListener {
           //Friend CREATE IMPLEMENTATION
            val bundle=Bundle()
            val cardData=RLCreateGroupModel()
            val selectFriendList: List<RLUserDataParcelable> = selectUserdata.map {
                RLUserDataParcelable(
                    first_name = it.first_name,
                    last_name = it.last_name,
                    userid = it.userid,
                    username = it.username,
                    avatar = it.avatar,
                    myid = it.myid,
                    myidstatus = it.myidstatus,
                    theirid = it.theirid,
                    theiridstatus = it.theiridstatus,
                    isSelected = it.isSelected
                )
            }
            cardData.selectFriendList=selectFriendList
            bundle.putParcelable("cardData",cardData)
            (context as RLMainActivityRL).rl_loadFrag(RLFragCreateGroup().newInstance(bundle), TAG, true,null, true)
        }
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
                         rl_responsehandlefriendsApi(response.text.user)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch FriendsData: ${e.message} ")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error FriendsData: ${error.message} " )
            }
        }
    }
    private fun rl_responsehandlefriendsApi(userdata: List<RLuserData>) {
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
                adapter.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun rl_groupApiCall() {
        val request = listOf(
            RLrequestgroup_dataset(
                group_data = RLsetgroup_data(userid = currentUser,limit = 100, index=0)
            )
        )
        RLTools.rl_logDPrint(TAG,"request groupData: ${Gson().toJson(request)}")
        viewModel.rl_yourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG,"success groupData: ${Gson().toJson(response)}")
                    if (response.type.equals("success")){
                        rl_responsehandleGroupsApi(response.text)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch groupData: ${e.message}")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error groupData: ${error.message}")
            }
        }
    }
    private fun rl_responsehandleGroupsApi(groupData: List<RLyourGroupDataModel>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourgroup.layoutManager = linearLayoutManager
        val adapterGroup = RLYourGroupListAdapter(activity,groupData)
        fragBinding.recycleYourgroup.adapter = adapterGroup

        fragBinding.edtGroupSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterGroup.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}