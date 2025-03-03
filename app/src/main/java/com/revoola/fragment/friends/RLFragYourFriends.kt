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
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.friends.adapter.RLYourFriendListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.*
import com.revoola.model.RLSetget_followers
import com.revoola.model.RLSetget_followersrequest
import com.revoola.model.RLSetsearch_user
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLsearch_user_request
import com.revoola.model.RLsearch_userrequest
import com.revoola.model.RLuserData
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourFriends : RLBaseFragment() {
    val TAG: String = RLFragYourFriends::class.java.simpleName
    lateinit var fragBinding: RlFragYourFriendsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragYourFriends()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        RlFragYourFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_friends, container) as RlFragYourFriendsBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourfriends)
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

        fragBinding.txtFriendYoufollow.setOnClickListener {
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (RLApiClientRetrofit.RLisConnected()) {
                RLyouFollowApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }

        fragBinding.txtFriendFollowingyou.setOnClickListener {
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (RLApiClientRetrofit.RLisConnected()) {
                RLfollowingYouApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }

        val reDirecDeepLinkPage=requireArguments().getBoolean("reDirecDeepLinkPage")
        if (reDirecDeepLinkPage){
            if (RLApiClientRetrofit.RLisConnected()) {
                RLSearchFollowApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }else{
            if (RLApiClientRetrofit.RLisConnected()) {
                RLyouFollowApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }


    }
    private fun RLyouFollowApiCall() {
        val request = listOf(RLSetsearch_userrequest(search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        RLTools.RlLogDPrint(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text.user,true)
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

    private fun RLSearchFollowApiCall() {
        //var myid: String,var contact_status:Int, var limit: Int, var index:Int
        val request = listOf(RLsearch_userrequest(search_user = RLsearch_user_request(myid = currentUser,contact_status=2,limit = 100, index=0)))
        RLTools.RlLogDPrint(TAG,"setSearchFollowdata= "+request)

        viewModel.RLsearch_user_Data_DeepLink(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text.user,true)
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

    private fun RLfollowingYouApiCall() {
        val request = listOf(
            RLSetget_followersrequest(
            search_user = RLSetget_followers(get_followers = currentUser,limit = 100, index=0)
            )
        )
        RLTools.RlLogDPrint(TAG,"setfollowingYoudata= "+request)

        viewModel.RLfriendsFollowingYou(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text.user,false)
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
    private fun RLresponsehandle(userdata: List<RLuserData>, youFollow:Boolean) {
        if (youFollow){
           // fragBinding.txtFriendFollowcount.setText(userdata.size.toString()+" "+getString(R.string.friendsyoufollow))
        }else{
           // fragBinding.txtFriendFollowcount.setText(userdata.size.toString()+" "+getString(R.string.friendsfollowingyou))
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata,false)
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