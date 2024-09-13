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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.friends.adapter.RLYourFriendListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.*
import com.example.myfirstapp.fragment.start.yourway.RLFragEditYourSensor
import com.example.myfirstapp.model.RLSetGroupMemberData
import com.example.myfirstapp.model.RLSetGroupMemberRequest
import com.example.myfirstapp.model.RLSetget_followers
import com.example.myfirstapp.model.RLSetget_followersrequest
import com.example.myfirstapp.model.RLSetsearch_user
import com.example.myfirstapp.model.RLSetsearch_userrequest
import com.example.myfirstapp.model.RLuserData
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourGroupDetails" )
        fragBinding.toolbar.tvTitle.setText(R.string.groupdetails)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
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
        Log.d(TAG,"group_data= "+request)

        viewModel.RLGroupMembers(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text)
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