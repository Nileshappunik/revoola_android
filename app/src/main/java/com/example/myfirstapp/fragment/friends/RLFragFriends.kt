package com.example.myfirstapp.fragment.friends

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragFriendsBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.fragment.friends.adapter.RLFriendListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class RLFragFriends : RLBaseFragment() {
    val TAG: String = RLFragFriends::class.java.simpleName
    lateinit var fragBinding: RlFragFriendsBinding
    private val binding by lazy {
        RlFragFriendsBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_friends, container) as RlFragFriendsBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFriends" )
        RLFriendsList()
        return fragBinding.root
    }
    private fun  RLuisetupNew(dataList: List<RLStartAllMenuModel>) {
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.friends))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.manageyourrevoolacommunity))

        fragBinding.rvFriend.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                fragBinding.rvFriend.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val height =  fragBinding.rvFriend.height
                println("RelativeLayout total height: $height pixels")

                val linearLayoutMain = LinearLayoutManager(activity)
                fragBinding.rvFriend.layoutManager = linearLayoutMain
                val adapter = RLFriendListAdapter(activity,dataList,height)
                fragBinding.rvFriend.adapter=adapter
            }
        })

    }
    private fun RLFriendsList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLALLMENULISTRead(RLConstants.FRIENDS){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    Log.d(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLuisetupNew(dataList)
                }catch (e:Exception){
                    Log.e(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }

}