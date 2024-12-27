package com.revoola.fragment.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragFriendsBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.friends.adapter.RLFriendListAdapter
import com.revoola.utils.RLConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.commonobject.RLTools

class RLFragFriends : RLBaseFragment() {
    val TAG: String = RLFragFriends::class.java.simpleName
    lateinit var fragBinding: RlFragFriendsBinding
    private val binding by lazy {
        RlFragFriendsBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_friends, container) as RlFragFriendsBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragFriends" )
        RLFriendsList()
        return fragBinding.root
    }
    private fun RLuisetupNew(dataList: List<RLStartAllMenuModel>) {
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
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
                    RLTools.RlLogDPrint(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLuisetupNew(dataList)
                }catch (e:Exception){
                   RLTools.RlLogEPrint(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }

}