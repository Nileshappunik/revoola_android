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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.revoola.fragment.feed.adapter.RLFeedListAdapter
import com.revoola.fragment.feed.adapter.RLFeedListChallengesAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragFeedBinding
import com.revoola.fragment.feed.adapter.RLFeedGroupNameAdapter
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.start.challenges.RLFragChalengesType
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.RLGroupCardModel
import com.revoola.model.RLSetGroupData
import com.revoola.model.RLSetGroupRequest
import com.revoola.model.RLSetgoaled_challenges
import com.revoola.model.RLSetgoaled_challenges_request
import com.revoola.model.RLSetoverview_thumb
import com.revoola.model.RLSetoverview_thumbRequest
import com.revoola.model.RLSetoverview_thumbRequest_you
import com.revoola.model.RLSetoverview_thumb_you
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.model.RLTextOverview
import com.revoola.model.RLtrigger_inapp_referrer_goaled_challenges
import com.revoola.model.RLtrigger_inapp_referrer_goaled_challenges_Request
import com.revoola.utils.RLPrefManager
import com.revoola.utils.loadSvg
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLFragFeed : RLBaseFragment() , RLItemClickListener {
    private val TAG: String = RLFragFeed::class.java.simpleName
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private lateinit var   adaptertitle: RLOverviewSessionTitleListAdapter
    private val valueslist = arrayOf("FRIENDS","GROUPS","YOU","CHALLENGES")
    private var adapter : RLFeedListAdapter?=null
    private var clickyou:Boolean=false
    private var currentState:String="FRIENDS"

    private var isLoading = false
    private var  limit = 100
    private var index=0
    private var currentUser:String=""
    private var groupId:String=""
    private var lastfragmentopen=""
    private var appUnit:String="Metric"
    private var isSwitchOn: Boolean = false
    private val fragBinding by lazy {
        RlFragFeedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        lastfragmentopen= RLPrefManager.rl_getSomeStringValue(activity,RLPrefManager.current_fragment,"" )
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeed" )
        currentUser=  RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.current_user, "")
        groupId= currentUser+"_friends"
        // Api call
        apiClientRetrofit = RLApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                // RLTools.RLshowAlertDialog(requireContext(),requireActivity())
            }
        })
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.feedsmall))
        fragBinding.inlayTop.ivDescription.setText("")
        fragBinding.inlayTop.switchFeed.visibility= View.VISIBLE
        fragBinding.inlayTop.switchFeed.setOnCheckedChangeListener { _, isChecked ->
            isSwitchOn = isChecked
            rl_topListItemClickUISetup(currentState)
        }

        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                appUnit =userData.appUnit
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        if (lastfragmentopen.equals("RLFragChallengeSummary")){
            //CHALLENGES view back event get
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
            adaptertitle = RLOverviewSessionTitleListAdapter("CHALLENGES",this,valueslist,activity)
            fragBinding.inlayTop.recyclerTitle.adapter = adaptertitle
            rl_challengesUISet()
        }
        else{
            rl_firsttimeApiCall(groupId)
            //do title
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
            adaptertitle = RLOverviewSessionTitleListAdapter("FRIENDS",this,valueslist,activity)
            fragBinding.inlayTop.recyclerTitle.adapter = adaptertitle
        }
        // Add scroll listener for pagination
        fragBinding.rvItemFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                try {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    if (!isLoading && layoutManager.findLastCompletelyVisibleItemPosition() == adapter!!.itemCount - 1) {
                        if (clickyou){
                            rl_apicallYou()
                        }else{
                            rl_apicall(groupId)
                        }
                    }
                }catch (e:Exception){
                    RLTools.rl_logDPrint(TAG,"Catch: ${e.message}")
                }
            }
        })

        /*  fragBinding.imgPlus.setOnClickListener{
            (context as RLMainActivityRL.RLloadFrag(RLFragChalengesType(), TAG, true, null, false)
        }*/
        fragBinding.inlayFilter.loadSvg(RLConstants.Friends_Fab_SVG)
        fragBinding.inlayFilter.setOnClickListener {
            when(currentState){
                "CHALLENGES"->{(context as RLMainActivityRL).rl_loadFrag(RLFragChalengesType(), TAG, true, null, true)}
                "FRIENDS"->{(context as RLMainActivityRL).rl_loadFrag(RLFragFindOnRevoola(), TAG, true, null, true)}
                "GROUPS"->{(context as RLMainActivityRL).rl_loadFrag(RLFragYourGroup(), TAG, true, null, true)}
            }
        }
    }
    private fun rl_apicall(groupId:String) {
        isLoading = true
      adapter!!.rl_addLoadingFooter()
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetoverview_thumbRequest(
                overview_thumb = RLSetoverview_thumb(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    groupid = groupId,
                    limit = limit,
                    index=index,
                    goal="all",
                    current_user = currentUser,
                    isall = 0,
                    d = "mindAndBody")
            )
        )

       RLTools.rl_logEPrint(TAG,"setdata ${Gson().toJson(request)}")

        viewModel.rl_getUserFeedCardData(request) { result ->
            result.onSuccess { response ->
                 adapter!!.rl_removeLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success: ${Gson().toJson(response)}")
                        adapter!!.rl_addData(response.text,isSwitchOn)
                        isLoading = false
                        index=index+100
                        limit=limit+100
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail: ${response.type}")
                        //commonToast(response.type)
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch: ${e.message}")
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.rl_removeLoadingFooter()
                isLoading = true

                RLTools.rl_logDPrint(TAG,"Error: ${error.message}")
            }
        }
    }
    private fun rl_apicallYou() {
        isLoading = true
        adapter!!.rl_addLoadingFooter()
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val user= listOf<String>(currentUser)

        val request = listOf(
            RLSetoverview_thumbRequest_you(
                overview_thumb = RLSetoverview_thumb_you(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    users = user,
                    limit = limit,
                    index=index,
                    goal="all",
                    current_user = currentUser,
                    isall = 0,
                    d = "mindAndBody")
            )
        )

        RLTools.rl_logDPrint(TAG,"setdatayou: ${Gson().toJson(request)}")

        viewModel.rl_getUserFeedCardDatayou(request) { result ->
            result.onSuccess { response ->
                adapter!!.rl_removeLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success: ${Gson().toJson(response)}")
                        //main list
                        adapter!!.rl_addData(response.text,isSwitchOn)
                        isLoading = false
                        index=index+100
                        limit=limit+100
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail: ${response.type}")
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch: ${e.message}")
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.rl_removeLoadingFooter()
                isLoading = true

                RLTools.rl_logDPrint(TAG,"Error: ${error.message}")
            }
        }
    }
    private fun rl_groupAPiCall() {
        val request = listOf(
            RLSetGroupRequest(
                group_data = RLSetGroupData(userid = currentUser, limit = 100, index=0)
            )
        )
        RLTools.rl_logDPrint(TAG,"set Group Data: ${Gson().toJson(request)}")
        viewModel.rl_getGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success: ${Gson().toJson(response)}")
                        rl_groupnamelistdialogopen(response.text)
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail: ${response.type}")
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch: ${e.message}")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error: ${error.message}")
            }
        }
    }
    private fun rl_groupnamelistdialogopen(newData: List<RLGroupCardModel>) {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_group_name)
        dialog.setCancelable(true)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        val recyclerSelectAssign = dialog.findViewById(R.id.listItems) as RecyclerView
        val btClear: TextView = dialog.findViewById(R.id.txtx_cancle)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val dialogAdapter = RLFeedGroupNameAdapter(requireActivity(), false)
        recyclerSelectAssign.adapter = dialogAdapter
        dialogAdapter.RLaddData(newData)
        dialogAdapter.seOnClickListners(object : RLFeedGroupNameAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                fragBinding.txtUsername.setText(selectioncName)
                dialog.dismiss()
                if (apiClientRetrofit.rl_isConnected()) {
                    //Detail Api
                    groupId = selectionID
                    rl_firsttimeApiCall(selectionID)
                    rl_GroupNameSetTitle(selectioncName, true)
                }
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    private fun rl_GroupNameSetTitle(selectioncName: String, b: Boolean) {
        if (b){
            adaptertitle.texttypeset=selectioncName
        }
        valueslist.set(1,selectioncName)
        adaptertitle.notifyItemChanged(1,valueslist)
    }
    private fun rl_firsttimeApiCall(groupid:String){
          limit = 100
         index=0
        isLoading = false
        //main list
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemFeed.layoutManager = linearLayoutManager
        adapter = RLFeedListAdapter(activity,currentUser,currentState,appUnit){ clickedItem ->
            rl_joinChallengesApiCall(clickedItem)
        }
        fragBinding.rvItemFeed.adapter = adapter
        if (apiClientRetrofit.rl_isConnected()) {
            //Detail Api
            rl_apicall(groupid)
        } else {
            rl_showDialogFullscreen()
        }
    }
    private fun rl_apicallChallenges(adapterch: RLFeedListChallengesAdapter) {
        isLoading=true
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetgoaled_challenges_request(
                goaled_challenges = RLSetgoaled_challenges(
                    id = currentUser,
                    type = "challenges_feed_thumbs",
                    today = currentTimestamp)))
        RLTools.rl_logDPrint(TAG,"setdataChallenges: ${Gson().toJson(request)}")
        viewModel.rl_goaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success: ${response.type}")
                        RLTools.rl_logDPrint(TAG,"ChResponse: ${Gson().toJson(response.text)}")
                        if (response.text.data.isNullOrEmpty()){
                            fragBinding.inlayNoData.noDataLayout.visibility=View.VISIBLE
                        }else{
                            fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
                            adapterch.rl_addData(response.text.data)
                        }
                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail: ${response.type}")
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch: ${e.message}")
                }
            }.onFailure { error ->

                RLTools.rl_logDPrint(TAG,"Error: ${error.message}")
            }
        }
    }
    private fun rl_challengesUISet(){
        fragBinding.relayGroupname.visibility=View.GONE
        fragBinding.relayListview.visibility=View.VISIBLE
        clickyou=false
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemFeed.layoutManager = linearLayoutManager
        val adapterch = RLFeedListChallengesAdapter(activity)
        fragBinding.rvItemFeed.adapter = adapterch
        rl_apicallChallenges(adapterch)
    }
    private fun rl_joinChallengesApiCall(cardData: RLTextOverview){
        val request = listOf(RLtrigger_inapp_referrer_goaled_challenges_Request(
            trigger_inapp_referrer_goaled_challenges = RLtrigger_inapp_referrer_goaled_challenges(
                userid = cardData.userid,inapp_referrer = cardData.classType!!,challengeid = cardData.classType!!)))

        RLTools.rl_logDPrint(TAG,"JoinBigChallengesRequest: $request")
        viewModel.rl_joinBigChallengeFeed(request) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.rl_logDPrint(TAG,"JoinBigChallenges Success: ${response}")
                    rl_showSuccessDialog()
                    rl_topListItemClickUISetup(currentState)
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logEPrint(TAG,"JoinBigChallenges Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.rl_logEPrint(TAG,"JoinBigChallenges Error: ${error.message}")
            }
        }
    }
    private fun rl_showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("You have successfully joined the challenge.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog when "OK" is clicked
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun rl_topListItemClickUISetup(currentState_: String) {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        when(currentState_){
            "FRIENDS"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="FRIENDS"
                fragBinding.relayGroupname.visibility=View.GONE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=false
                rl_GroupNameSetTitle("GROUPS",false)
                rl_firsttimeApiCall(groupId)
            }
            "GROUPS"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="GROUPS"
                fragBinding.relayGroupname.visibility=View.VISIBLE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=false
                rl_GroupNameSetTitle("123",true)
                fragBinding.relayGroupname.setOnClickListener {
                    rl_groupAPiCall()
                }
                rl_groupAPiCall()
            }
            "YOU"-> {
                fragBinding.inlayFilter.visibility=View.GONE
                currentState="YOU"
                fragBinding.relayGroupname.visibility=View.GONE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=true
                rl_GroupNameSetTitle("GROUPS",false)
                if (apiClientRetrofit.rl_isConnected()) {
                    //Detail Api
                    limit = 100
                    index=0
                    isLoading = false
                    val linearLayoutManager = LinearLayoutManager(activity)
                    fragBinding.rvItemFeed.layoutManager = linearLayoutManager
                    adapter = RLFeedListAdapter(activity,currentUser,currentState,appUnit){ clickedItem ->
                        rl_joinChallengesApiCall(clickedItem)
                    }
                    fragBinding.rvItemFeed.adapter = adapter
                    rl_apicallYou()
                } else {
                    rl_showDialogFullscreen()
                }
            }
            "CHALLENGES"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="CHALLENGES"
                rl_GroupNameSetTitle("GROUPS",false)
                rl_challengesUISet()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        rl_bottomHideShowSet(true)
    }

    override fun onItemClick(position: Int) {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        currentState = valueslist[position]
        rl_topListItemClickUISetup(currentState)
//        when(valueslist[position]){
//            "FRIENDS"-> {
//                currentState="FRIENDS"
//                rl_topListItemClickUISetup(currentState)
//            }
//            "GROUPS"-> {
//                currentState="GROUPS"
//                rl_topListItemClickUISetup(currentState)
//            }
//            "YOU"-> {
//                currentState="YOU"
//                rl_topListItemClickUISetup(currentState)
//            }
//            "CHALLENGES"-> {
//                currentState="CHALLENGES"
//                rl_topListItemClickUISetup(currentState)
//            }
//        }
    }
}