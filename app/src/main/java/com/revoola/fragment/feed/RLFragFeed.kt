package com.revoola.fragment.feed

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.revoola.R
import com.revoola.RLBaseFragment
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.databinding.RlFragFeedBinding
import com.revoola.fragment.feed.adapter.RLFeedGroupNameAdapter
import com.revoola.fragment.feed.adapter.RLFeedListAdapter
import com.revoola.fragment.feed.adapter.RLFeedListChallengesAdapter
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.friends.RLFragYourGroupDetails
import com.revoola.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.revoola.fragment.start.challenges.RLFragChalengesType
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.*
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager
import com.revoola.utils.loadSvg
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragFeed : RLBaseFragment(), RLItemClickListener {

    companion object {
        private val TAG = RLFragFeed::class.java.simpleName
        private const val PAGE_CHUNK = 100
    }

    /** Tabs for top selector */
    private enum class FeedTab(val label: String) {
        FRIENDS("FRIENDS"),
        GROUPS("GROUPS"),
        YOU("YOU"),
        CHALLENGES("CHALLENGES");

        companion object {
            fun fromLabel(label: String): FeedTab =
                values().firstOrNull { it.label == label } ?: FRIENDS
        }
    }

    private lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private lateinit var titleAdapter: RLOverviewSessionTitleListAdapter

    private var feedAdapter: RLFeedListAdapter? = null

    private val tabs = arrayOf(
        FeedTab.FRIENDS.label,
        FeedTab.GROUPS.label,
        FeedTab.YOU.label,
        FeedTab.CHALLENGES.label
    )

    private var currentTab: FeedTab = FeedTab.FRIENDS
    private var isYouTab = false

    private var isLoading = false
    private var limit = PAGE_CHUNK
    private var index = 0

    private var currentUser: String = ""
    private var groupId: String = "" // default: "<user>_friends"
    private var lastFragmentOpen = ""
    private var appUnit: String = "Metric"
    private var isSwitchOn: Boolean = false

    private val binding by lazy { RlFragFeedBinding.inflate(layoutInflater) }

    // --- Lifecycle ---

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Prefs & state
        lastFragmentOpen = RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_fragment, "")
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment, "RLFragFeed")

        currentUser = RLAuthManager().rl_getCurrentUser()?.uid?:""
        groupId = "${currentUser}_friends"

        // API / VM
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(
            requireActivity(),
            RLMainViewModelFactory(userRepository)
        )[RLMainViewModel::class.java]

        // Back pressed (keep custom hook if needed)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Custom back behavior if you want
                }
            }
        )
        setupUi()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        rl_bottomHideShowSet(true)
    }

    // --- UI Setup ---

    private fun setupUi() {
        rl_helpHideShowSet(true, binding.inlayTop.ivhelp, RLPrefManager.friends_help_content)

        binding.inlayNoData.noDataLayout.visibility = View.GONE
        binding.inlayTop.ivBack.visibility = View.GONE
        binding.inlayTop.ivTitle.text = getString(R.string.feedsmall)
        binding.inlayTop.ivDescription.text = ""
        binding.layoutSwitchFeed.visibility = View.GONE

        // Fetch user settings (appUnit)
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                appUnit = userData.appUnit
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        // Title (tabs)
        val titleLayoutManager =LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.inlayTop.recyclerTitle.layoutManager = titleLayoutManager

        currentTab = if (lastFragmentOpen == "RLFragChallengeSummary") {
            FeedTab.CHALLENGES
        } else {
            FeedTab.FRIENDS
        }
        titleAdapter = RLOverviewSessionTitleListAdapter(currentTab.label, this, tabs, activity)
        binding.inlayTop.recyclerTitle.adapter = titleAdapter

        // Initial content
        if (currentTab == FeedTab.CHALLENGES) {
            setupChallengesUi()
        } else {
            firstLoadForGroup(groupId)
        }

        // Endless scroll
        binding.rvItemFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return // only when scrolling down
                val lm = rv.layoutManager as? LinearLayoutManager ?: return
                val last = lm.findLastCompletelyVisibleItemPosition()
                val count = (feedAdapter?.itemCount ?: 0)
                if (!isLoading && count > 0 && last == count - 1) {
                    // Load more
                    if (isYouTab) {
                        callApiYou()
                    } else {
                        callApiGroup(groupId)
                    }
                }
            }
        })

        // FAB / filter icon behavior
        binding.inlayFilter.loadSvg(RLConstants.Friends_Fab_SVG)
        binding.inlayFilter.setOnClickListener {
            when (currentTab) {
                FeedTab.CHALLENGES -> (context as RLMainActivityRL)
                    .rl_loadFrag(RLFragChalengesType(), TAG, true, null, true)

                FeedTab.FRIENDS -> (context as RLMainActivityRL)
                    .rl_loadFrag(RLFragFindOnRevoola(), TAG, true, null, true)

                FeedTab.GROUPS -> (context as RLMainActivityRL)
                    .rl_loadFrag(RLFragYourGroup(), TAG, true, null, true)

                FeedTab.YOU -> {
                    // No filter action on YOU in original logic.
                }
            }
        }

        // Switch in YOU tab
        binding.switchFeed.setOnCheckedChangeListener { _, checked ->
            isSwitchOn = checked
            applyTab(currentTab) // re-apply current tab with switch value
        }

    }

    private fun resetListAdapterForFeed() {
        // Drop any previous adapter (including the challenges adapter)
        binding.rvItemFeed.adapter = null
        feedAdapter = null
        // Ensure a fresh feed adapter for FRIENDS / YOU
        ensureFeedAdapter()
    }

    private fun createFreshFeedAdapter(selectTag: String) {
        // Always a fresh LayoutManager + fresh Adapter
        binding.rvItemFeed.layoutManager = LinearLayoutManager(activity)
        feedAdapter = RLFeedListAdapter(
            requireActivity(),
            currentUser,
            selectTag,
            appUnit
        ) { clickedItem,isDeleteItem ->
            if (isDeleteItem) {
                deleteFeedCardItem(clickedItem)
            } else {
                joinChallenge(clickedItem)
            }
        }
        binding.rvItemFeed.adapter = feedAdapter
    }

    // --- Tab Handling ---

    private fun applyTab(tab: FeedTab) {
        binding.inlayNoData.noDataLayout.visibility = View.GONE
        currentTab = tab

        when (tab) {
            FeedTab.FRIENDS -> {
                binding.layoutSwitchFeed.visibility = View.GONE
                binding.inlayFilter.visibility = View.VISIBLE
                isYouTab = false
                setGroupTitle("GROUPS", updateAdapterTitle = false)
                showListOnly()
                // ✅ Always point FRIENDS to the default friends group
                groupId = "${currentUser}_friends"
                // 🔧 Key lines:
                resetPaging()
                resetListAdapterForFeed()
                createFreshFeedAdapter(FeedTab.FRIENDS.label)
                if (apiClientRetrofit.rl_isConnected()) callApiGroup(groupId) else rl_showDialogFullscreen()
            }

            FeedTab.GROUPS -> {
                binding.layoutSwitchFeed.visibility = View.GONE
                binding.inlayFilter.visibility = View.VISIBLE
                isYouTab = false
                setGroupTitle("123", updateAdapterTitle = true)
                showGroupRowAndList()
                // ✅ Reset state & build a fresh adapter tagged as GROUPS
                resetPaging()
                resetListAdapterForFeed()
                createFreshFeedAdapter(FeedTab.GROUPS.label)
                isSwitchOn = false
                if (apiClientRetrofit.rl_isConnected()) callApiYou() else rl_showDialogFullscreen()
                binding.relayGroupname.setOnClickListener { fetchGroupList() }
                fetchGroupList()
            }

            FeedTab.YOU -> {
                binding.layoutSwitchFeed.visibility = View.VISIBLE
                binding.inlayFilter.visibility = View.GONE
                isYouTab = true
                setGroupTitle("GROUPS", updateAdapterTitle = false)
                showListOnly()
                isSwitchOn =  binding.switchFeed.isChecked
                // ✅ Reset state & build a fresh adapter tagged as YOU
                resetPaging()
                resetListAdapterForFeed()
                createFreshFeedAdapter(FeedTab.YOU.label)
                if (apiClientRetrofit.rl_isConnected()) callApiYou() else rl_showDialogFullscreen()
            }

            FeedTab.CHALLENGES -> {
                binding.layoutSwitchFeed.visibility = View.GONE
                binding.inlayFilter.visibility = View.VISIBLE
                isYouTab = false
                setGroupTitle("GROUPS", updateAdapterTitle = false)

                // 🔧 Drop feed adapter so it can be recreated when returning
                binding.rvItemFeed.adapter = null
                feedAdapter = null

                setupChallengesUi()
            }
        }
    }

    private fun showListOnly() {
        binding.relayGroupname.visibility = View.GONE
        binding.relayListview.visibility = View.VISIBLE
    }

    private fun showGroupRowAndList() {
        binding.relayGroupname.visibility = View.VISIBLE
        binding.relayListview.visibility = View.VISIBLE
    }

    private fun setGroupTitle(selectionName: String, updateAdapterTitle: Boolean) {
        if (updateAdapterTitle) {
            titleAdapter.texttypeset = selectionName
        }
        tabs[1] = selectionName
        titleAdapter.notifyItemChanged(1, tabs)
        binding.txtUsername.text = selectionName
    }

    // --- First loads ---

    private fun firstLoadForGroup(groupId: String) {
        resetPaging()
        ensureFeedAdapter()
        if (apiClientRetrofit.rl_isConnected()) {
            callApiGroup(groupId)
        } else {
            rl_showDialogFullscreen()
        }
    }

    private fun ensureFeedAdapter() {
        if (binding.rvItemFeed.layoutManager == null) {
            binding.rvItemFeed.layoutManager = LinearLayoutManager(activity)
        }
        if (feedAdapter == null) {
            feedAdapter = RLFeedListAdapter(
                requireActivity(),
                currentUser,
                currentTab.label,
                appUnit
            ) { clickedItem,isDeleteItem ->
                if (isDeleteItem) {
                    deleteFeedCardItem(clickedItem)
                } else {
                    joinChallenge(clickedItem)
                }
            }
            binding.rvItemFeed.adapter = feedAdapter
        }
    }

    private fun resetPaging() {
        limit = PAGE_CHUNK
        index = 0
        isLoading = false
        // If you want to clear existing items on first load:
        feedAdapter?.clear()
    }

    // --- API: Group feed pagination ---

    private fun callApiGroup(groupId: String) {
        val adapter = feedAdapter ?: return
        isLoading = true
        adapter.rl_addLoadingFooter()

        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetoverview_thumbRequest(
                overview_thumb = RLSetoverview_thumb(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    groupid = groupId,
                    limit = limit,
                    index = index,
                    goal = "all",
                    current_user = currentUser,
                    isall = 0,
                    d = "mindAndBody"
                )
            )
        )

        RLTools.rl_logEPrint(TAG, "setdata ${Gson().toJson(request)}")

        viewModel.rl_getUserFeedCardData(request) { result ->
            adapter.rl_removeLoadingFooter()
            result.onSuccess { response ->
                try {
                    if (response.type == "success") {
                        RLTools.rl_logDPrint(TAG, "Success: ${Gson().toJson(response)}")
                        adapter.rl_addData(response.text, isSwitchOn)
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

    // --- API: YOU feed pagination ---

    private fun callApiYou() {
        val adapter = feedAdapter ?: return
        isLoading = true
        adapter.rl_addLoadingFooter()

        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val users = listOf(currentUser)

        val request = listOf(
            RLSetoverview_thumbRequest_you(
                overview_thumb = RLSetoverview_thumb_you(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    users = users,
                    limit = limit,
                    index = index,
                    goal = "all",
                    current_user = currentUser,
                    isall = 0,
                    d = "mindAndBody"
                )
            )
        )

        RLTools.rl_logDPrint(TAG, "setdatayou: ${Gson().toJson(request)}")

        viewModel.rl_getUserFeedCardDatayou(request) { result ->
            adapter.rl_removeLoadingFooter()
            result.onSuccess { response ->
                try {
                    if (response.type == "success") {
                        RLTools.rl_logDPrint(TAG, "Success: ${Gson().toJson(response)}")
                        adapter.rl_addData(response.text, isSwitchOn)
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

    // --- Groups: choose group dialog & API ---

    private fun fetchGroupList() {
        val request = listOf(
            RLSetGroupRequest(group_data = RLSetGroupData(userid = currentUser, limit = 100, index = 0))
        )
        RLTools.rl_logDPrint(TAG, "set Group Data: ${Gson().toJson(request)}")
        viewModel.rl_getGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type == "success") {
                        RLTools.rl_logDPrint(TAG, "Success: ${Gson().toJson(response)}")
                        openGroupPickerDialog(response.text)
                    } else {
                        RLTools.rl_logDPrint(TAG, "Fail: ${response.type}")
                    }
                } catch (e: Exception) {
                    RLTools.rl_logDPrint(TAG, "Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG, "Error: ${error.message}")
            }
        }
    }

    private fun openGroupPickerDialog(items: List<RLGroupCardModel>) {
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.rl_dailog_group_name)
            setCancelable(true)
            window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        }

        val list = dialog.findViewById<RecyclerView>(R.id.listItems)
        val btnClose = dialog.findViewById<TextView>(R.id.txtx_cancle)

        list.layoutManager = LinearLayoutManager(context)
        val dialogAdapter = RLFeedGroupNameAdapter(requireActivity(), false)
        list.adapter = dialogAdapter
        dialogAdapter.RLaddData(items)

        dialogAdapter.seOnClickListners(object : RLFeedGroupNameAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                binding.txtUsername.text = selectioncName
                dialog.dismiss()
                if (apiClientRetrofit.rl_isConnected()) {
                    groupId = selectionID
                    firstLoadForGroup(selectionID)
                    setGroupTitle(selectioncName, updateAdapterTitle = true)
                }
            }
        })

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    // --- Challenges ---

    private fun setupChallengesUi() {
        binding.relayGroupname.visibility = View.GONE
        binding.relayListview.visibility = View.VISIBLE
        isYouTab = false

        val lm = LinearLayoutManager(activity)
        binding.rvItemFeed.layoutManager = lm

        val chAdapter = RLFeedListChallengesAdapter(activity)
        binding.rvItemFeed.adapter = chAdapter
        callChallengesApi(chAdapter)
    }

    private fun callChallengesApi(adapter: RLFeedListChallengesAdapter) {
        isLoading = true
        val now = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetgoaled_challenges_request(
                goaled_challenges = RLSetgoaled_challenges(
                    id = currentUser,
                    type = "challenges_feed_thumbs",
                    today = now
                )
            )
        )

        RLTools.rl_logDPrint(TAG, "setdataChallenges: ${Gson().toJson(request)}")
        viewModel.rl_goaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type == "success") {
                        RLTools.rl_logLarge(TAG, "ChResponse: ${Gson().toJson(response.text)}")
                        if (response.text.data.isNullOrEmpty()) {
                            binding.inlayNoData.noDataLayout.visibility = View.VISIBLE
                        } else {
                            binding.inlayNoData.noDataLayout.visibility = View.GONE
                            adapter.rl_addData(response.text.data)
                        }
                    } else {
                        RLTools.rl_logDPrint(TAG, "Fail: ${response.type}")
                    }
                } catch (e: Exception) {
                    RLTools.rl_logDPrint(TAG, "Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG, "Error: ${error.message}")
            }
        }
    }

    private fun joinChallenge(card: RLTextOverview) {
        val req = listOf(
            RLtrigger_inapp_referrer_goaled_challenges_Request(
                trigger_inapp_referrer_goaled_challenges = RLtrigger_inapp_referrer_goaled_challenges(
                    userid = card.userid,
                    inapp_referrer = card.classType ?: "",
                    challengeid = card.classType ?: ""
                )
            )
        )
        RLTools.rl_logDPrint(TAG, "JoinBigChallengesRequest: $req")
        viewModel.rl_joinBigChallengeFeed(req) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG, "JoinBigChallenges Success: $response")
                    showJoinSuccessDialog()
                    applyTab(currentTab)
                } catch (e: Exception) {
                    RLTools.rl_logEPrint(TAG, "JoinBigChallenges Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logEPrint(TAG, "JoinBigChallenges Error: ${error.message}")
            }
        }
    }

    private fun deleteFeedCardItem(card: RLTextOverview) {
        val req = listOf(
            RLDeleteFeedItemApiPayload(
                classLeaderboard = RLDeleteFeedItemBoard(
                    userId = card.userid,
                    timestampLocal = card.timestamp_local,
                    className = card.className,
                    isDeleted = 1
                )
            )
        )
        RLTools.rl_logDPrint(TAG, "deleteFeedCardItemRequest: $req")
        viewModel.rl_deleteFeedCardItem(req) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG, "deleteFeedCardItem Success: $response")
                } catch (e: Exception) {
                    RLTools.rl_logEPrint(TAG, "deleteFeedCardItem Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logEPrint(TAG, "deleteFeedCardItem Error: ${error.message}")
            }
        }
    }

    private fun showJoinSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("You have successfully joined the challenge.")
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .create()
            .show()
    }

    // --- Callbacks (tab click) ---

    override fun onItemClick(position: Int) {
        binding.inlayNoData.noDataLayout.visibility = View.GONE
        val selected = FeedTab.fromLabel(tabs[position])
        applyTab(selected)
    }
}

