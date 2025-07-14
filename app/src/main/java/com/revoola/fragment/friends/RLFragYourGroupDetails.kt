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
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.fragment.friends.adapter.RLYourFriendListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.*
import com.revoola.model.RLSetGroupMemberData
import com.revoola.model.RLSetGroupMemberRequest
import com.revoola.model.RLuserData
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourGroupDetails : RLBaseFragment() {
    val TAG: String = RLFragYourGroupDetails::class.java.simpleName
    //lateinit var fragBinding: RlFragYourGroupDetailsBinding
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val fragBinding by lazy {
        RlFragYourGroupDetailsBinding.inflate(layoutInflater)
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
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_group_details, container) as RlFragYourGroupDetailsBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourGroupDetails" )
        fragBinding.toolbar.tvTitle.setText(R.string.groupdetails)
        rl_onBackPresAct(fragBinding.toolbar.ivBack)
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        val groupID=requireArguments().getString("GroupID")
        val groupName=requireArguments().getString("GroupName")
        val groupAvatar=requireArguments().getString("GroupAvatar")
        val groupMember=requireArguments().getString("GroupMember")

        fragBinding.txtGroupName.setText("$groupName (ADMIN)")
        fragBinding.txtGroupMembers.setText("NUMBER OF NUMBERS $groupMember")
        Glide.with(requireContext()).load(groupAvatar)
            .placeholder(R.drawable.sample_user)
            .error(R.drawable.sample_user)
            .into(fragBinding.imageGroup)

        if (apiClientRetrofit.rl_isConnected()) {
            rl_groupDataApiCall(groupID.toString())
        }
    }
    private fun rl_groupDataApiCall(groupID:String) {
        val request = listOf(RLSetGroupMemberRequest(group_data = RLSetGroupMemberData(groupid = groupID,current_userid = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"group_data= "+request)

        viewModel.rl_groupMembers(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandle(response.text)
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun rl_responsehandle(userdata: List<RLuserData>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata,true)
        fragBinding.recycleYourfriend.adapter = adapter


        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}