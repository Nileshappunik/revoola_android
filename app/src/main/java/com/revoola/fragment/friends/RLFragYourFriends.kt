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
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragYourFriends : RLBaseFragment() {
    val TAG: String = RLFragYourFriends::class.java.simpleName
    //lateinit var fragBinding: RlFragYourFriendsBinding
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragYourFriends()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragYourFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_friends, container) as RlFragYourFriendsBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourfriends)
        rl_onBackPresAct(fragBinding.toolbar.ivBack)
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {

        fragBinding.txtFriendYoufollow.setOnClickListener {
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (apiClientRetrofit.rl_isConnected()) {
                rl_youFollowApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }

        fragBinding.txtFriendFollowingyou.setOnClickListener {
            fragBinding.viewlablelright.setBackgroundResource(R.color.AppMainColor)
            fragBinding.viewlableleft.setBackgroundResource(R.color.AppWhiteColor)
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppBlackColor))
            if (apiClientRetrofit.rl_isConnected()) {
                rl_followingYouApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }

        val reDirecDeepLinkPage=requireArguments().getBoolean("reDirecDeepLinkPage")
        if (reDirecDeepLinkPage){
            if (apiClientRetrofit.rl_isConnected()) {
                rl_searchFollowApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }else{
            if (apiClientRetrofit.rl_isConnected()) {
                rl_youFollowApiCall()
            } else {
                rl_showDialogFullscreen()
            }
        }


    }
    private fun rl_youFollowApiCall() {
        val request = listOf(RLSetsearch_userrequest(search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"setyouFollowdata= "+request)

        viewModel.rl_friendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandle(response.text.user,true)
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

    private fun rl_searchFollowApiCall() {
        //var myid: String,var contact_status:Int, var limit: Int, var index:Int
        val request = listOf(RLsearch_userrequest(search_user = RLsearch_user_request(myid = currentUser,contact_status=2,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"setSearchFollowdata= "+request)

        viewModel.rl_search_user_Data_DeepLink(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandle(response.text.user,true)
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

    private fun rl_followingYouApiCall() {
        val request = listOf(RLSetget_followersrequest(search_user = RLSetget_followers(get_followers = currentUser,limit = 100, index=0)))
        RLTools.rl_logDPrint(TAG,"setfollowingYoudata= "+request)

        viewModel.rl_friendsFollowingYou(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        rl_responsehandle(response.text.user,false)
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
    private fun rl_responsehandle(userdata: List<RLuserData>, youFollow:Boolean) {
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
                adapter.rl_filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}