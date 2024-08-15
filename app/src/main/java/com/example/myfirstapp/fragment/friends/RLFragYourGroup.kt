package com.example.myfirstapp.fragment.friends

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.friends.adapter.RLYourGroupListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.fragment.friends.adapter.RLSelectedFriendListAdapter
import com.example.myfirstapp.fragment.friends.adapter.RLYourFriendSelectListAdapter
import com.example.myfirstapp.model.RLSetsearch_user
import com.example.myfirstapp.model.RLSetsearch_userrequest
import com.example.myfirstapp.model.RLrequestgroup_dataset
import com.example.myfirstapp.model.RLsetgroup_data
import com.example.myfirstapp.model.RLuserData
import com.example.myfirstapp.model.RLyourGroupDataModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

class RLFragYourGroup : RLBaseFragment() {
    val TAG: String = RLFragYourGroup::class.java.simpleName
    lateinit var fragBinding: RlFragYourGroupBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    var isGroup:Boolean=true

    
    private val binding by lazy {
        RlFragYourGroupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_group, container) as RlFragYourGroupBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourGroup" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourgroup)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

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
                (context as RLMainActivityRL).RLbottombarcolorwhite()
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
                search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        Log.d(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandlefriendsApi(response.text.user)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandlefriendsApi(userdata: List<RLuserData>) {
        var selectUserdata: List<RLuserData> = mutableListOf()
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
        val request = listOf(RLrequestgroup_dataset(
                group_data = RLsetgroup_data(userid = currentUser,limit = 100, index=0)))
        Log.d(TAG,"setgroupdata= "+request)

        viewModel.RLyourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandleGroupsApi(response.text)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
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



}