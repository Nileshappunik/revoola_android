package com.revoola.fragment.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.*
import com.revoola.fragment.feed.adapter.RLFeedListAdapter
import com.revoola.model.RLoverview_thumb_data_you
import com.revoola.model.RLoverview_thumb_you
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragFriendsItemClickList : RLBaseFragment() {
    companion object {
        private val TAG = RLFragFriendsItemClickList::class.java.simpleName
        private const val PAGE_CHUNK = 100
    }
    
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var feedAdapter: RLFeedListAdapter? = null
    private var appUnit: String = "Metric"
    private var isLoading = false
    private var limit = PAGE_CHUNK
    private var index = 0
    private var currentUser = ""

    private val fragBinding by lazy {
        RlFragFriendsItemClickListBinding.inflate(layoutInflater)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFriendsItemClickList()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFriendsItemClickList" )
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        currentUser = requireArguments().getString("currentUser","")
        fragBinding.ivBack.setOnClickListener { rl_closeFragment()}
        // Fetch user settings (appUnit)
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                appUnit = userData.appUnit
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }
        createFreshFeedAdapter()
    }

    private fun createFreshFeedAdapter() {
        // Always a fresh LayoutManager + fresh Adapter
        fragBinding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = RLFeedListAdapter(requireActivity(),
            RLAuthManager().rl_getCurrentUser()?.uid?:"","FRIENDS",appUnit) { clickedItem, isDeleteItem -> }
        fragBinding.listRecyclerView.adapter = feedAdapter
        if (apiClientRetrofit.rl_isConnected()) callApiYou() else rl_showDialogFullscreen()
        fragBinding.listRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return // only when scrolling down
                val lm = rv.layoutManager as? LinearLayoutManager ?: return
                val last = lm.findLastCompletelyVisibleItemPosition()
                val count = (feedAdapter?.itemCount ?: 0)
                if (!isLoading && count > 0 && last == count - 1) {
                    // Load more
                    callApiYou()
                }
            }
        })
    }

    // --- API: YOU feed pagination ---

    private fun callApiYou() {
        val adapter = feedAdapter ?: return
        isLoading = true
        adapter.rl_addLoadingFooter()

        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val users = listOf(currentUser)

        val request = listOf(
            RLoverview_thumb_you(
                overview_thumb = RLoverview_thumb_data_you(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    users = users,
                    limit = limit,
                    index = index,
                    goal = "all",
                    current_user = currentUser,
                    isall = 0,
                    metriccaardsvisible = false,
                    from_third_party_source = 1
                )
            )
        )

        RLTools.rl_logDPrint(TAG, "setdatayou: ${Gson().toJson(request)}")

        viewModel.rl_friendsClickList(request) { result ->
            adapter.rl_removeLoadingFooter()
            result.onSuccess { response ->
                try {
                    if (response.type == "success") {
                        RLTools.rl_logDPrint(TAG, "Success: ${Gson().toJson(response)}")
                        adapter.rl_addData(response.text, false)
                        isLoading = false
                        // Preserve original progressive growth behavior:
                        index += PAGE_CHUNK
                        limit += PAGE_CHUNK
                    } else {
                        RLTools.rl_logDPrint(TAG, "Fail: ${response.type}")
                        isLoading = true
                    }
                } catch (e: Exception) {
                    RLTools.rl_logDPrint(TAG, "Catch: ${e.message}")
                    isLoading = true
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG, "Error: ${error.message}")
                isLoading = true
            }
        }
    }

}