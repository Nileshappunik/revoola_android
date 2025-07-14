package com.revoola.fragment.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
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
import com.revoola.utils.RLPrefManager

class RLFragFriends : RLBaseFragment() {
    val TAG: String = RLFragFriends::class.java.simpleName

    private val fragBinding by lazy {
        RlFragFriendsBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFriends" )
        rl_friendsList()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.rl_showAlertDialog(requireContext(),requireActivity())
            }
        })
        return fragBinding.root
    }
    private fun rl_uisetupNew(dataList: List<RLStartAllMenuModel>) {
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
    private fun rl_friendsList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.rl_allMenuListRead(RLConstants.FRIENDS){ data, error ->
            if (data != null) {
                try{
                    val gson = Gson()
                    val jsonArray =  Gson().toJson(data)
                    RLTools.rl_logDPrint(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    rl_uisetupNew(dataList)
                }catch (e:Exception){
                   RLTools.rl_logEPrint(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }

}