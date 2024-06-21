package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.RLNotificationListAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragNotificationBinding
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

class RLFragNotification : RLBaseFragment() {
    val TAG: String = RLFragNotification::class.java.simpleName
    lateinit var fragBinding: RlFragNotificationBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var adapter : RLNotificationListAdapter?=null
    private var isLoading = false
    var  limit = 10
    var index=0
    var currentUser:String=""
    
    private val binding by lazy {
        RlFragNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_notification, container) as RlFragNotificationBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragNotification" )
        currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
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
        RLonBackPresAct(fragBinding.ivBack)
        if (RLApiClientRetrofit.RLisConnected()) {
            limit = 10
            index=0
            isLoading = false
            val linearLayoutManager = LinearLayoutManager(activity)
            fragBinding.rvNotification.layoutManager = linearLayoutManager
             adapter = RLNotificationListAdapter(activity)
            fragBinding.rvNotification.adapter = adapter
            //Detail Api
            RLAPiCall()
        } else {
            RLshowDialogFullscreen()
        }

        // Add scroll listener for pagination
        fragBinding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == adapter!!.itemCount - 1) {
                        RLAPiCall()
                    }
                }catch (e:Exception){
                    Log.d(TAG,"Catch="+e.message)
                }

            }
        })
    }
    private fun RLAPiCall() {
        isLoading = true
        adapter!!.RLaddLoadingFooter()
        viewModel.RLgetNotificationData("getNotifications",currentUser,limit,index) { result ->
            result.onSuccess { response ->
                adapter!!.RLremoveLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        adapter!!.RLsetList(response.text)
                        isLoading = false
                        index=index+10
                        limit=limit+10
                    }else {
                        isLoading = true
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    isLoading = true
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                adapter!!.RLremoveLoadingFooter()
                isLoading = true
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }


}