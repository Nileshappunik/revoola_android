package com.example.myfirstapp.fragment.start.classes

import android.app.Dialog
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
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlDialogFriendChallengesBinding
import com.example.myfirstapp.databinding.RlFragClassesScheduleSessionBinding
import com.example.myfirstapp.databinding.RlFragYourGroupBinding
import com.example.myfirstapp.fragment.friends.adapter.RLSelectedFriendListAdapter
import com.example.myfirstapp.fragment.friends.adapter.RLYourFriendSelectListAdapter
import com.example.myfirstapp.fragment.start.adapter.RLMindBodyClassForGroupListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengeForFriendListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengeForGroupListAdapter
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


class RLClassesScheduleJoinSession : RLBaseFragment() {
    val TAG: String = RLClassesScheduleJoinSession::class.java.simpleName
    lateinit var fragBinding: RlFragClassesScheduleSessionBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""


    private val binding by lazy {
        RlFragClassesScheduleSessionBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLClassesScheduleJoinSession()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes_schedule_session, container) as RlFragClassesScheduleSessionBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLClassesScheduleJoinSession" )
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
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(R.string.selectfriendsgroups)
        fragBinding.inlayTop.ivDescription.setText("")

        RLButtonClickEvent(false,"")
        RLfriendsApiCall()
        fragBinding.btnFriend.setOnClickListener {
            fragBinding.btnFriend.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.btnFriend.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.btnGroup.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.btnGroup.background = null
            RLButtonClickEvent(false,"")
            RLfriendsApiCall()

        }
        fragBinding.btnGroup.setOnClickListener {
            fragBinding.btnFriend.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.btnFriend.background = null
            fragBinding.btnGroup.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.btnGroup.setBackgroundResource(R.drawable.full_round_green)
            RLButtonClickEvent(false,"")
            RLgroupApiCall()
        }
    }

    private fun RLfriendsApiCall() {
        val request = listOf(
            RLSetsearch_userrequest(
                search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)
            )
        )
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
        fragBinding.recyclerList.layoutManager = linearLayoutManager

        val adapter = RLChallengeForFriendListAdapter(activity, userdata) { cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectUserdata += listOf(cardData)
            }else{
                selectUserdata -= listOf(cardData)
            }
            if (selectUserdata.size>0){
                RLButtonClickEvent(true,selectUserdata.size.toString())
            }else{
                RLButtonClickEvent(false,"")
            }
        }
        fragBinding.recyclerList.adapter = adapter

        fragBinding.edtSearch.addTextChangedListener(object : TextWatcher {
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
        var  selectGroupId:String=""
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutManager

        val adaptergroup = RLMindBodyClassForGroupListAdapter(activity,groupdata){ cardData,isSelected ->
            // Handle selection
            if (isSelected){
                selectGroupId=cardData.group_id
            }else{
                selectGroupId=""
            }
            if (selectGroupId.isEmpty()){
                RLButtonClickEvent(false,"")
            }else{
                RLButtonClickEvent(true,cardData.group_name)
            }
        }

        fragBinding.recyclerList.adapter = adaptergroup

        fragBinding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adaptergroup.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }
    private fun RLButtonClickEvent(isClickVisible:Boolean,message:String){
        if (isClickVisible){
            fragBinding.btnInviteClick.visibility=View.VISIBLE
            fragBinding.btnInvite.visibility=View.GONE
        }else{
            fragBinding.btnInviteClick.visibility=View.GONE
            fragBinding.btnInvite.visibility=View.VISIBLE
        }
        fragBinding.btnInviteClick.setOnClickListener {
            val data=  requireArguments().getString("VIDEODATA","")
            val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            bundle.putString("Message",message)
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, false,null, false)

        }
    }
}