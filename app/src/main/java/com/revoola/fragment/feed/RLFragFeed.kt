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
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    val TAG: String = RLFragFeed::class.java.simpleName
    lateinit var fragBinding: RlFragFeedBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private lateinit var   adaptertitle: RLOverviewSessionTitleListAdapter
    //val valuesList = arrayOf("Friends", "Groups","You","Challenges")
    private val valueslist = arrayOf("FRIENDS","GROUPS","YOU","CHALLENGES")
    private var adapter : RLFeedListAdapter?=null
    private var clickyou:Boolean=false
    private var currentState:String="FRIENDS"

    private var isLoading = false
    private var  limit = 100
    private var index=0
    private var currentUser:String=""
    private var GroupId:String="w2p8SQCvE3emjEEDo66f02eF6fG2_friends"
    private var lastfragmentopen=""

    private val binding by lazy {
        RlFragFeedBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_feed, container) as RlFragFeedBinding
         lastfragmentopen= RLPrefManager.RLGetSomeStringValue(activity,RLPrefManager.current_fragment,"" )
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeed" )
         currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        /*requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.RLshowAlertDialog(requireContext(),requireActivity())
            }
        })*/
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.feedsmall))
        fragBinding.inlayTop.ivDescription.setText("")

        if (lastfragmentopen.equals("RLFragChallengeSummary")){
            //CHALLENGES view back event get
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            fragBinding.inlayTop.recyclerTitle.layoutManager = linearLayoutManager
             adaptertitle = RLOverviewSessionTitleListAdapter("CHALLENGES",this,valueslist,activity)
            fragBinding.inlayTop.recyclerTitle.adapter = adaptertitle
            RLChallengesUISet()
        }
        else{
            RLfirsttimeApiCall(GroupId)
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
                            RLapicallYou()
                        }else{
                            RLapicall(GroupId)
                        }
                    }
                }catch (e:Exception){
                    RLTools.RlLogDPrint(TAG,"Catch="+e.message)
                }
            }
        })

        /*  fragBinding.imgPlus.setOnClickListener{
            (context as RLMainActivityRL.RLloadFrag(RLFragChalengesType(), TAG, true, null, false)
        }*/
        fragBinding.inlayFilter.loadSvg(RLConstants.Friends_Fab_SVG)
        fragBinding.inlayFilter.setOnClickListener {
           when(currentState){
               "CHALLENGES"->{(context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, null, true)}
               "FRIENDS"->{(context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, null, true)}
               "GROUPS"->{(context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, null, true)}
           }
        }
    }
    private fun RLapicall(groupId:String) {
        isLoading = true
      adapter!!.RLaddLoadingFooter()
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

       RLTools.RlLogEPrint(TAG,"setdata= $request")

        viewModel.RLgetUserFeedCardData(request) { result ->
            result.onSuccess { response ->
                 adapter!!.RLremoveLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                       /* val gson = Gson()
                        val jsonArray = gson.toJson(response.text)
                       RLTools.RlLogEPrint(TAG,"Feed_jsonDate:-  $jsonArray")*/
                        adapter!!.RLaddData(response.text)
                        isLoading = false
                        index=index+100
                        limit=limit+100
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                        //commonToast(response.type)
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.RLremoveLoadingFooter()
                isLoading = true
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLapicallYou() {
        isLoading = true
        adapter!!.RLaddLoadingFooter()
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

        RLTools.RlLogDPrint(TAG,"setdatayou= "+request)

        viewModel.RLgetUserFeedCardDatayou(request) { result ->
            result.onSuccess { response ->
                adapter!!.RLremoveLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        //main list
                        adapter!!.RLaddData(response.text)
                        isLoading = false
                        index=index+100
                        limit=limit+100
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.RLremoveLoadingFooter()
                isLoading = true
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLgroupAPiCall() {
        val request = listOf(
            RLSetGroupRequest(
                group_data = RLSetGroupData(userid = currentUser, limit = 100, index=0)
            )
        )
        RLTools.RlLogDPrint(TAG,"setGroupdata= "+request)

        viewModel.RLgetGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        RLgroupnamelistdialogopen(response.text)
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){ e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    fun RLgroupnamelistdialogopen(newData: List<RLGroupCardModel>) {
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
                if (RLApiClientRetrofit.RLisConnected()) {
                    //Detail Api
                    GroupId = selectionID
                    RLfirsttimeApiCall(selectionID)
                    RlGroupNameSetTitle(selectioncName, true)
                }
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    private fun RlGroupNameSetTitle(selectioncName: String, b: Boolean) {
        if (b){
            adaptertitle.texttypeset=selectioncName
        }
        valueslist.set(1,selectioncName)
        adaptertitle.notifyItemChanged(1,valueslist)
    }
    fun RLfirsttimeApiCall(groupid:String){
          limit = 100
         index=0
        isLoading = false
        //main list
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemFeed.layoutManager = linearLayoutManager
        adapter = RLFeedListAdapter(activity,currentUser,currentState){ clickedItem ->
            RlJoinChallengesApiCall(clickedItem)
        }
        fragBinding.rvItemFeed.adapter = adapter
        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLapicall(groupid)
        } else {
            RLshowDialogFullscreen()
        }
    }
    private fun RLapicallChallenges(adapterch: RLFeedListChallengesAdapter) {
        isLoading=true
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetgoaled_challenges_request(
                goaled_challenges = RLSetgoaled_challenges(
                    id = currentUser,
                    type = "challenges_feed_thumbs",
                    today = currentTimestamp)))
        RLTools.RlLogDPrint(TAG,"setdataChallenges= "+request)
        viewModel.RLgoaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.RlLogDPrint(TAG,"Success= "+response.type)
                        /*val gson = Gson()
                        val jsonArray = gson.toJson(response.text)
                       RLTools.RlLogEPrint(TAG,"Success= $jsonArray")*/
                        if (response.text.data.isNullOrEmpty()){
                            fragBinding.inlayNoData.noDataLayout.visibility=View.VISIBLE
                        }else{
                            fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
                            adapterch.RLaddData(response.text.data)
                        }
                    }else {
                        RLTools.RlLogDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                RLTools.RlLogDPrint(TAG,"Error= "+error.message)
            }
        }
    }
    override fun onItemClick(position: Int) {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        when(valueslist[position]){
            "FRIENDS"-> {
                //fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="FRIENDS"
                //fragBinding.relayGroupname.visibility=View.GONE
                //fragBinding.relayListview.visibility=View.VISIBLE
               // clickyou=false
               // RlGroupNameSetTitle("GROUPS",false)
               // RLfirsttimeApiCall(GroupId)
                RLTopListItemClickUISetup(currentState)
            }
            "GROUPS"-> {
                currentState="GROUPS"
                RLTopListItemClickUISetup(currentState)
                /*fragBinding.inlayFilter.visibility=View.VISIBLE
                fragBinding.relayGroupname.visibility=View.VISIBLE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=false
                RlGroupNameSetTitle("123",true)
                fragBinding.relayGroupname.setOnClickListener {
                    RLgroupAPiCall()
                }
                RLgroupAPiCall()*/

            }
            "YOU"-> {
                currentState="YOU"
                RLTopListItemClickUISetup(currentState)
                /*fragBinding.inlayFilter.visibility=View.GONE

                fragBinding.relayGroupname.visibility=View.GONE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=true
                RlGroupNameSetTitle("GROUPS",false)
                if (RLApiClientRetrofit.RLisConnected()) {
                    //Detail Api
                    limit = 100
                    index=0
                    isLoading = false
                    val linearLayoutManager = LinearLayoutManager(activity)
                    fragBinding.rvItemFeed.layoutManager = linearLayoutManager
                    adapter = RLFeedListAdapter(activity,currentUser,currentState,viewModel)
                    fragBinding.rvItemFeed.adapter = adapter
                    RLapicallYou()
                } else {
                    RLshowDialogFullscreen()
                }*/
            }
            "CHALLENGES"-> {
                currentState="CHALLENGES"
                RLTopListItemClickUISetup(currentState)
                /*fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="CHALLENGES"
                RlGroupNameSetTitle("GROUPS",false)
                RLChallengesUISet()*/
            }
        }
    }
    private fun RLChallengesUISet(){
        fragBinding.relayGroupname.visibility=View.GONE
        fragBinding.relayListview.visibility=View.VISIBLE
        clickyou=false
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemFeed.layoutManager = linearLayoutManager
        val adapterch = RLFeedListChallengesAdapter(activity)
        fragBinding.rvItemFeed.adapter = adapterch
        RLapicallChallenges(adapterch)
    }
    override fun onResume() {
        super.onResume()
        RLBottomHideShowSet(true)
    }
    private fun RlJoinChallengesApiCall(cardData: RLTextOverview){
        val request = listOf(RLtrigger_inapp_referrer_goaled_challenges_Request(
            trigger_inapp_referrer_goaled_challenges = RLtrigger_inapp_referrer_goaled_challenges(
                userid = cardData.userid,inapp_referrer = cardData.classType!!,challengeid = cardData.classType!!)))

        RLTools.RlLogDPrint(TAG,"JoinBigChallengesRequest: $request")
        viewModel.RLJoinBigChallengeFeed(request) { result ->
            result.onSuccess { response ->
                try {
                    RLTools.RlLogDPrint(TAG,"JoinBigChallenges Success: ${response}")
                    RLShowSuccessDialog()
                    RLTopListItemClickUISetup(currentState)
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.RlLogEPrint(TAG,"JoinBigChallenges Catch: ${e.message}")
                }
            }.onFailure { error ->
                RLTools.RlLogEPrint(TAG,"JoinBigChallenges Error: ${error.message}")
            }
        }
    }

    private fun RLShowSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("You have successfully joined the challenge.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog when "OK" is clicked
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun RLTopListItemClickUISetup(currentState_: String) {
        fragBinding.inlayNoData.noDataLayout.visibility=View.GONE
        when(currentState_){
            "FRIENDS"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="FRIENDS"
                fragBinding.relayGroupname.visibility=View.GONE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=false
                RlGroupNameSetTitle("GROUPS",false)
                RLfirsttimeApiCall(GroupId)
            }
            "GROUPS"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="GROUPS"
                fragBinding.relayGroupname.visibility=View.VISIBLE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=false
                RlGroupNameSetTitle("123",true)
                fragBinding.relayGroupname.setOnClickListener {
                    RLgroupAPiCall()
                }
                RLgroupAPiCall()
            }
            "YOU"-> {
                fragBinding.inlayFilter.visibility=View.GONE
                currentState="YOU"
                fragBinding.relayGroupname.visibility=View.GONE
                fragBinding.relayListview.visibility=View.VISIBLE
                clickyou=true
                RlGroupNameSetTitle("GROUPS",false)
                if (RLApiClientRetrofit.RLisConnected()) {
                    //Detail Api
                    limit = 100
                    index=0
                    isLoading = false
                    val linearLayoutManager = LinearLayoutManager(activity)
                    fragBinding.rvItemFeed.layoutManager = linearLayoutManager
                    adapter = RLFeedListAdapter(activity,currentUser,currentState){ clickedItem ->
                        RlJoinChallengesApiCall(clickedItem)
                    }
                    fragBinding.rvItemFeed.adapter = adapter
                    RLapicallYou()
                } else {
                    RLshowDialogFullscreen()
                }
            }
            "CHALLENGES"-> {
                fragBinding.inlayFilter.visibility=View.VISIBLE
                currentState="CHALLENGES"
                RlGroupNameSetTitle("GROUPS",false)
                RLChallengesUISet()
            }
        }
    }

}