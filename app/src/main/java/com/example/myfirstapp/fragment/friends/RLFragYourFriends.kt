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
import com.example.myfirstapp.fragment.friends.adapter.RLYourFriendListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.*
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

class RLFragYourFriends : RLBaseFragment() {
    val TAG: String = RLFragYourFriends::class.java.simpleName
    lateinit var fragBinding: RlFragYourFriendsBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""

    private val binding by lazy {
        RlFragYourFriendsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_your_friends, container) as RlFragYourFriendsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourFriends" )
        fragBinding.toolbar.tvTitle.setText(R.string.yourfriends)
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

        fragBinding.txtFriendYoufollow.setOnClickListener {
            fragBinding.txtFriendYoufollow.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.txtFriendFollowingyou.background=null
            if (RLApiClientRetrofit.RLisConnected()) {
                RLyouFollowApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }

        fragBinding.txtFriendFollowingyou.setOnClickListener {
            fragBinding.txtFriendFollowingyou.setBackgroundResource(R.drawable.full_round_green)
            fragBinding.txtFriendYoufollow.background=null
            fragBinding.txtFriendFollowingyou.setTextColor(resources.getColor(R.color.AppWhiteColor))
            fragBinding.txtFriendYoufollow.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            if (RLApiClientRetrofit.RLisConnected()) {
                RLfollowingYouApiCall()
            } else {
                RLshowDialogFullscreen()
            }
        }

        fragBinding.txtInviteyourfriend.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, RLFragInviteFriends::class.java.simpleName, false)
        }

        if (RLApiClientRetrofit.RLisConnected()) {
            RLyouFollowApiCall()
        } else {
            RLshowDialogFullscreen()
        }
    }
    private fun RLyouFollowApiCall() {
        val request = listOf(RLSetsearch_userrequest(search_user = RLSetsearch_user(get_friends = currentUser,limit = 100, index=0)))
        Log.d(TAG,"setyouFollowdata= "+request)

        viewModel.RLfriendsYouFollow(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text.user,true)
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
    private fun RLfollowingYouApiCall() {
        val request = listOf(RLSetget_followersrequest(
            search_user = RLSetget_followers(get_followers = currentUser,limit = 100, index=0)))
        Log.d(TAG,"setfollowingYoudata= "+request)

        viewModel.RLfriendsFollowingYou(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLresponsehandle(response.text.user,false)
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
    private fun RLresponsehandle(userdata: List<RLuserData>, youFollow:Boolean) {
        if (youFollow){
            fragBinding.txtFriendFollowcount.setText(userdata.size.toString()+" "+getString(R.string.friendsyoufollow))
        }else{
            fragBinding.txtFriendFollowcount.setText(userdata.size.toString()+" "+getString(R.string.friendsfollowingyou))
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleYourfriend.layoutManager = linearLayoutManager
        val adapter = RLYourFriendListAdapter(activity,userdata)
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