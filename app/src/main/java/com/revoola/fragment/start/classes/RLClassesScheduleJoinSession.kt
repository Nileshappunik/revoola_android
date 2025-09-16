package com.revoola.fragment.start.classes

import android.os.Bundle
import android.os.Parcelable
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
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory


class RLClassesScheduleJoinSession : RLBaseFragment() {
    private val TAG: String = RLClassesScheduleJoinSession::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var currentUser:String=""
    private var  selectGroupId:String=""
    private var selectUserData: List<RLuserData> = mutableListOf()
    private val fragBinding by lazy {
        RlFragClassesScheduleSessionBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLClassesScheduleJoinSession()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes_schedule_session, container) as RlFragClassesScheduleSessionBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLClassesScheduleJoinSession" )
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(R.string.selectfriendsgroups)
        fragBinding.inlayTop.ivDescription.setText("")

        rl_buttonClickEvent(false,"",null,null)
        rl_friendsApiCall()
        fragBinding.btnFriend.setOnClickListener {
            fragBinding.btnFriend.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.btnFriend.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.btnGroup.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.btnGroup.background = null
            rl_buttonClickEvent(false,"",null,null)
            rl_friendsApiCall()

        }
        fragBinding.btnGroup.setOnClickListener {
            fragBinding.btnFriend.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.btnFriend.background = null
            fragBinding.btnGroup.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.btnGroup.setBackgroundResource(R.drawable.full_round_green)
            rl_buttonClickEvent(false,"",null,null)
            rl_groupApiCall()
        }
    }

    private fun rl_friendsApiCall() {
        val request = listOf(
            RLSetsearch_userrequest(
                search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)
            )
        )
        RLTools.rl_logDPrint(TAG,"setyouFollowdata= "+request)

        viewModel.rl_friendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandlefriendsApi(response.text.user)
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
    private fun rl_responsehandlefriendsApi(userdata: List<RLuserData>) {
       // var selectUserData: List<RLuserData> = mutableListOf()
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutManager
        val adapterFriends = RLChallengeForFriendListAdapter(activity, userdata) { cardData ->
            // Handle selection
            if (cardData.isSelected){
                selectUserData += listOf(cardData)
            }else{
                selectUserData -= listOf(cardData)
            }
            if (selectUserData.size>0){
                rl_buttonClickEvent(true,selectUserData.size.toString(),selectGroupId,selectUserData)
            }else{
                rl_buttonClickEvent(false,"",null,null)
            }
        }
        fragBinding.recyclerList.adapter = adapterFriends
        fragBinding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterFriends.RLfilter(s.toString())
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
        RLTools.rl_logDPrint(TAG,"setgroupdata= "+request)

        viewModel.rl_yourGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandleGroupsApi(response.text)
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
    private fun rl_responsehandleGroupsApi(groupdata: List<RLyourGroupDataModel>) {
       // var  selectGroupId:String=""
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutManager
        val adapterGroup = RLMindBodyClassForGroupListAdapter(activity,groupdata){ cardData,isSelected ->
            // Handle selection
            if (isSelected){
                selectGroupId=cardData.group_id
            }else{
                selectGroupId=""
            }
            if (selectGroupId.isEmpty()){
                rl_buttonClickEvent(false,"",null,null)
            }else{
                rl_buttonClickEvent(true,cardData.group_name,cardData.group_id,selectUserData)
            }
        }

        fragBinding.recyclerList.adapter = adapterGroup
        fragBinding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapterGroup.RLfilter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
    private fun rl_buttonClickEvent(isClickVisible:Boolean, message:String,groupId:String?,selectUserData: List<RLuserData>?){
        if (isClickVisible){
            fragBinding.btnInviteClick.visibility=View.VISIBLE
            fragBinding.btnInvite.visibility=View.GONE
        }else{
            fragBinding.btnInviteClick.visibility=View.GONE
            fragBinding.btnInvite.visibility=View.VISIBLE
        }
        fragBinding.btnInviteClick.setOnClickListener {
            val data=  requireArguments().getString("videoCardData","")
            val selectDate=  requireArguments().getString("selectDate","")
            val isMindClass=  requireArguments().getBoolean("isMindClass",false)
            val videoKey=  requireArguments().getString("videoKey","")
            val audioVideoType=  requireArguments().getString("audioVideoType","")
            val bundle = Bundle()
            bundle.putString("videoCardData",data)
            bundle.putString("audioVideoType",audioVideoType)
            bundle.putString("Message",message)
            bundle.putString("groupId",groupId)
            bundle.putString("videoKey",videoKey)
            bundle.putString("selectDate",selectDate)
            bundle.putBoolean("isMindClass",isMindClass)
            bundle.putParcelableArrayList("selectUserData", ArrayList(selectUserData ?: emptyList()))
            (context as RLMainActivityRL).rl_loadFrag(RLClassesSchedule().newInstance(bundle), TAG, false,null, false)

        }
    }
}