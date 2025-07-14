package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.adapter.RLNotificationListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragNotificationBinding
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragNotification : RLBaseFragment() {
    val TAG: String = RLFragNotification::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
     var adapter : RLNotificationListAdapter?=null
    private var isLoading = false
    var  limit = 10
    var index=0
    var currentUser:String=""
    
    private val fragBinding by lazy {
        RlFragNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragNotification" )
        currentUser= RLAuthManager().rl_getCurrentUser()?.uid ?:""
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
        if (apiClientRetrofit.rl_isConnected()) {
            limit = 10
            index=0
            isLoading = false
            val linearLayoutManager = LinearLayoutManager(activity)
            fragBinding.rvNotification.layoutManager = linearLayoutManager
             adapter = RLNotificationListAdapter(activity)
            fragBinding.rvNotification.adapter = adapter
            //Detail Api
            rl_notificationAPiCall()
        } else {
            rl_showDialogFullscreen()
        }

        // Add scroll listener for pagination
        fragBinding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == adapter!!.itemCount - 1) {
                        rl_notificationAPiCall()
                    }
                }catch (e:Exception){
                    RLTools.rl_logDPrint(TAG,"Catch="+e.message)
                }

            }
        })
    }
    private fun rl_notificationAPiCall() {
        isLoading = true
        adapter?.rl_addLoadingFooter()
        viewModel.rl_getNotificationData("getNotifications",currentUser,limit,index) { result ->
            result.onSuccess { response ->
                adapter?.rl_removeLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logLarge(TAG,"Notification Object: ${Gson().toJson(response)}")
                        adapter?.rl_setList(response.text)
                        isLoading = false
                        index=index+10
                        limit=limit+10
                    }else {
                        isLoading = true
                        RLTools.rl_logDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    isLoading = true
                    RLTools.rl_logDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                adapter?.rl_removeLoadingFooter()
                isLoading = true
                RLTools.rl_logDPrint(TAG,"Error= "+error.message)
            }
        }
    }


}