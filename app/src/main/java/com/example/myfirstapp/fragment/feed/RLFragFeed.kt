package com.example.myfirstapp.fragment.feed

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLFeedListAdapter
import com.example.myfirstapp.fragment.feed.adapter.RLFeedListChallengesAdapter
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragFeedBinding
import com.example.myfirstapp.fragment.feed.adapter.RLFeedGroupNameAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.example.myfirstapp.model.RLGroupCardModel
import com.example.myfirstapp.model.RLSetGroupData
import com.example.myfirstapp.model.RLSetGroupRequest
import com.example.myfirstapp.model.RLSetgoaled_challenges
import com.example.myfirstapp.model.RLSetgoaled_challenges_request
import com.example.myfirstapp.model.RLSetoverview_thumb
import com.example.myfirstapp.model.RLSetoverview_thumbRequest
import com.example.myfirstapp.model.RLSetoverview_thumbRequest_you
import com.example.myfirstapp.model.RLSetoverview_thumb_you
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import java.time.ZonedDateTime

class RLFragFeed : RLBaseFragment() , RLItemClickListener {
    val TAG: String = RLFragFeed::class.java.simpleName
    lateinit var fragBinding: RlFragFeedBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    //val valueslist = arrayOf("Friends", "Groups","You","Challenges")
    val valueslist = arrayOf("FRIENDS", "GROUPS","YOU","CHALLENGES")
    var adapter : RLFeedListAdapter?=null
    private var clickyou:Boolean=false

    private var isLoading = false
    var  limit = 10
    var index=0
    var currentUser:String=""
    var GroupId:String="w2p8SQCvE3emjEEDo66f02eF6fG2_friends"
    var lastfragmentopen=""

    private val binding by lazy {
        RlFragFeedBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_feed, container) as RlFragFeedBinding
         lastfragmentopen=RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_fragment,"" )
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeed" )
         currentUser=  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(), RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        if (lastfragmentopen.equals("RLFragChallengeSummary")){
            //CHALLENGES view back event get
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            fragBinding.recycleSessionTitle.layoutManager = linearLayoutManager
            val adaptertitle = RLOverviewSessionTitleListAdapter("CHALLENGES",this,valueslist,activity)
            fragBinding.recycleSessionTitle.adapter = adaptertitle
            RLChallengesUISet()
        }else{
            RLfirsttimeApiCall(GroupId)
            //do title
            val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            fragBinding.recycleSessionTitle.layoutManager = linearLayoutManager
            val adaptertitle = RLOverviewSessionTitleListAdapter("FRIENDS",this,valueslist,activity)
            fragBinding.recycleSessionTitle.adapter = adaptertitle
        }
        // Add scroll listener for pagination
        fragBinding.rvItemfeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    Log.d(TAG,"Catch="+e.message)
                }
            }
        })
    }
    private fun RLapicall(groupid:String) {
        isLoading = true
      adapter!!.RLaddLoadingFooter()
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        val request = listOf(
            RLSetoverview_thumbRequest(
                overview_thumb = RLSetoverview_thumb(
                    timestampfrom = 0,
                    timestampto = currentTimestamp,
                    groupid = groupid,
                    limit = limit,
                    index=index,
                    goal="all",
                    current_user = currentUser,
                    isall = 0,
                    d = "mindAndBody")))

        Log.d(TAG,"setdata= "+request)

        viewModel.RLgetUserFeedCardData(request) { result ->
            result.onSuccess { response ->
                 adapter!!.RLremoveLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        adapter!!.RLaddData(response.text)
                        isLoading = false
                        index=index+10
                        limit=limit+10
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                        //commonToast(response.type)
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.RLremoveLoadingFooter()
                isLoading = true
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
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
                    d = "mindAndBody")))

        Log.d(TAG,"setdatayou= "+request)

        viewModel.RLgetUserFeedCardDatayou(request) { result ->
            result.onSuccess { response ->
                adapter!!.RLremoveLoadingFooter()
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        //main list
                        adapter!!.RLaddData(response.text)
                        isLoading = false
                        index=index+10
                        limit=limit+10
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                        isLoading = true
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                    isLoading = true
                }
            }.onFailure { error ->
                adapter!!.RLremoveLoadingFooter()
                isLoading = true
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    private fun RLgroupAPiCall() {
        val request = listOf(RLSetGroupRequest(
                group_data = RLSetGroupData(
                    userid = currentUser,
                    limit = 100,
                    index=0)))
        Log.d(TAG,"setGroupdata= "+request)

        viewModel.RLgetGroupData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)
                        RLgroupnamelistdialogopen(response.text)
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
    fun RLgroupnamelistdialogopen(newData: List<RLGroupCardModel>) {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_group_name)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val recyclerSelectAssign =  dialog.findViewById(R.id.listItems) as RecyclerView
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val  dialogAdapter = RLFeedGroupNameAdapter(requireActivity(),false)
        recyclerSelectAssign.adapter = dialogAdapter
        dialogAdapter.RLaddData(newData)
        dialogAdapter.seOnClickListners(object : RLFeedGroupNameAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                fragBinding.txtUsername.setText(selectioncName)
                dialog.dismiss()
                if (RLApiClientRetrofit.RLisConnected()) {
                    //Detail Api
                    GroupId=selectionID
                     RLfirsttimeApiCall(selectionID)
                }
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
    fun RLfirsttimeApiCall(groupid:String){
          limit = 10
         index=0
        isLoading = false
        //main list
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemfeed.layoutManager = linearLayoutManager
        adapter = RLFeedListAdapter(activity,currentUser)
        fragBinding.rvItemfeed.adapter = adapter
        if (RLApiClientRetrofit.RLisConnected()) {
            //Detail Api
            RLapicall( groupid)
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
        Log.d(TAG,"setdataChallenges= "+request)
        viewModel.RLgoaled_challenges(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        Log.d(TAG,"Success= "+response.type)

                        adapterch.RLaddData(response.text.data)
                    }else {
                        Log.d(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.d(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLcommonToast(RLConstants.SERVER_PROBLEM)
                Log.d(TAG,"Error= "+error.message)
            }
        }
    }
    override fun onItemClick(position: Int) {
        if (valueslist[position].equals("FRIENDS")) {
            fragBinding.relayGroupname.visibility=View.GONE
            fragBinding.relayListview.visibility=View.VISIBLE
            clickyou=false
            RLfirsttimeApiCall(GroupId)
        } else if (valueslist[position].equals("GROUPS")) {
            fragBinding.relayGroupname.visibility=View.VISIBLE
            fragBinding.relayListview.visibility=View.VISIBLE
            clickyou=false
            fragBinding.relayGroupname.setOnClickListener {
                RLgroupAPiCall()
            }
            RLgroupAPiCall()
        }else if (valueslist[position].equals("YOU")) {
            fragBinding.relayGroupname.visibility=View.GONE
            fragBinding.relayListview.visibility=View.VISIBLE
            clickyou=true
            if (RLApiClientRetrofit.RLisConnected()) {
                //Detail Api
                limit = 10
                index=0
                isLoading = false
                val linearLayoutManager = LinearLayoutManager(activity)
                fragBinding.rvItemfeed.layoutManager = linearLayoutManager
                adapter = RLFeedListAdapter(activity,currentUser)
                fragBinding.rvItemfeed.adapter = adapter
                RLapicallYou()
            } else {
                RLshowDialogFullscreen()
            }
        }else if (valueslist[position].equals("CHALLENGES")) {
            RLChallengesUISet()
        }

    }
    private fun RLChallengesUISet(){
        fragBinding.relayGroupname.visibility=View.GONE
        fragBinding.relayListview.visibility=View.VISIBLE
        clickyou=false
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemfeed.layoutManager = linearLayoutManager
        val adapterch = RLFeedListChallengesAdapter(activity)
        fragBinding.rvItemfeed.adapter = adapterch
        RLapicallChallenges(adapterch)
    }
}