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
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourGroupDetails : RLBaseFragment() {
    val TAG: String = RLFragYourGroupDetails::class.java.simpleName
    lateinit var fragBinding: RlFragYourGroupDetailsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val binding by lazy {
        RlFragYourGroupDetailsBinding.inflate(layoutInflater)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragYourGroupDetails()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_group_details, container) as RlFragYourGroupDetailsBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragYourGroupDetails" )
        fragBinding.toolbar.tvTitle.setText(R.string.groupdetails)
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

        if (RLApiClientRetrofit.RLisConnected()) {
            RLGroupDataApiCall(groupID.toString())
        }
    }
    private fun RLGroupDataApiCall(groupID:String) {
        val request = listOf(RLSetGroupMemberRequest(group_data = RLSetGroupMemberData(groupid = groupID,current_userid = currentUser,limit = 100, index=0)))
        RLTools.RlLogDPrint(TAG,"group_data= "+request)

        viewModel.RLGroupMembers(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLresponsehandle(userdata: List<RLuserData>) {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata,true)
        fragBinding.recycleYourfriend.adapter = adapter


        fragBinding.edtFriendSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}