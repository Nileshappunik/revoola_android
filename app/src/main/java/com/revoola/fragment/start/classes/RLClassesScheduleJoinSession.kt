package com.revoola.fragment.start.classes

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
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragClassesScheduleSessionBinding
import com.revoola.fragment.start.adapter.RLMindBodyClassForGroupListAdapter
import com.revoola.fragment.start.challenges.adapter.RLChallengeForFriendListAdapter
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsetgroup_data
import com.revoola.model.RLuserData
import com.revoola.model.RLyourGroupDataModel
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory


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
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLClassesScheduleJoinSession" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
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
            val data=  requireArguments().getString("videoCardData","")
            val audioVideoType=  requireArguments().getString("audioVideoType","")
            val bundle = Bundle()
            bundle.putString("videoCardData",data)
            bundle.putString("audioVideoType",audioVideoType)
            bundle.putString("Message",message)
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, false,null, false)

        }
    }
}