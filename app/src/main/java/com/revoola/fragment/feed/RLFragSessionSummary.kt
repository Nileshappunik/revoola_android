package com.revoola.fragment.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.utils.RLPrefManager
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragSessionSummaryBinding
import com.revoola.enumclass.RLMetricData
import com.revoola.enumclass.RLTypeOfMetrics
import com.revoola.enumclass.RLYourWayName
import com.revoola.fragment.feed.adapter.RLFeedSessionEffortListAdapter
import com.revoola.fragment.feed.adapter.RLImagePagerAdapter
import com.revoola.model.RLTextOverview
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.enumclass.RLValueName
import com.revoola.firebaseModel.RLSessionSummaryDataModel
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class RLFragSessionSummary : RLBaseFragment() {
    val TAG: String = RLFragSessionSummary::class.java.simpleName
    lateinit var fragBinding: RlFragSessionSummaryBinding
    lateinit var cardData: RLTextOverview
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    private var currentUser:String=""
    private var classType=""
    private var selectTag=""
    private var fireBaseCardData: RLSessionSummaryDataModel?=null
    private var userCardData: RLRevoolaUsersSettingsModel?=null

    private val binding by lazy {
        RlFragSessionSummaryBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSessionSummary()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_session_summary, container) as RlFragSessionSummaryBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSessionSummary" )
        currentUser=  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user, "")
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLuisetup()
        return fragBinding.root
    }

    //Start Ui
    private fun RLuisetup() {
        val isSessionComplete = requireArguments().getBoolean("isSessionComplete")
       // RLonBackPresAct(fragBinding.ivBack)
        fragBinding.inlayTop.ivBack.setOnClickListener {
            RLcloseScreen(isSessionComplete)
        }
        // Data Get TO List
        cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
        selectTag = requireArguments().getString(RLConstants.FeedSelectTag) as String
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLfetchFirebaseData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing or show a message
                RLcloseScreen(isSessionComplete)
            }
        })
        fragBinding.inlayTop.recyclerTitle.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.GONE
        fragBinding.inlayTop.ivTitle.setText(cardData.className.toString())
        fragBinding.inlayTop.ivDescription.setText(RLTools.RLconvertTimestampToDAte(cardData.timestamp.toLong()))

    }

    //Firebase Fetch User Data and Session Summery Data
    private fun RLfetchFirebaseData() {
        //User Data Fetch to Firebase
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userCardData =userData
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        // Firebase to fetch Session Summary data
        val path = RevoolaFirebasePath.sessionSummaryDataPathRead(cardData.userid,cardData.timestamp)
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                fireBaseCardData = gson.fromJson(jsonObject, RLSessionSummaryDataModel::class.java)
                RLSummaryUiSet()
                RLClickToSetUI()
            } else {
                RLSummaryUiSet()
                RLClickToSetUI()
                RLTools.RlLogEPrint(TAG,"Session Summary Empty Data")
            }
        }

    }

    //Summery Ui SetUp
    private fun RLSummaryUiSet() {
        fragBinding.relaySummary.visibility=View.VISIBLE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.GONE

        val imagelink= RLTools.RLFeedSetImage(cardData,currentUser,selectTag)
        Glide.with(requireContext()).load(imagelink).into(fragBinding.testImage)

        fragBinding.testImage.visibility=View.GONE
        fragBinding.viewPagerImage.visibility=View.VISIBLE
        fragBinding.intoTabLayout.visibility=View.VISIBLE
        fragBinding.intoTabLayout.setupWithViewPager(fragBinding.viewPagerImage)
        val imageListOriginal = listOf(imagelink, "CHART", RLTools.RLgetImage(classType))

        var imageList:MutableList<String> = mutableListOf()
        if (cardData.user_images.isEmpty()){
            imageList=imageListOriginal.toMutableList()
        }else{

            imageList = cardData.user_images.extractImageUrls().toMutableList()
            imageList.add("CHART")
            imageList.add(RLTools.RLGetLinkImage(cardData.classType?.toLowerCase().toString()))
        }
        RLTools.RLheightsetViewPager(fragBinding.viewPagerImage)
        val viewPagerAdapter = RLImagePagerAdapter(activity,imageList)
        fragBinding.viewPagerImage.adapter = viewPagerAdapter

        when (classType.toLowerCase()){
            "run"->  RLSummaryNameToUi(RLYourWayName.Run)
            "walk"->  RLSummaryNameToUi(RLYourWayName.Walk)
            "workout"->  RLSummaryNameToUi(RLYourWayName.Workout)
            "ride"->  RLSummaryNameToUi(RLYourWayName.Ride)
            "pilates"->  RLSummaryNameToUi(RLYourWayName.Pilates)
            "warm"->  RLSummaryNameToUi(RLYourWayName.Warm)
            "dance"->  RLSummaryNameToUi(RLYourWayName.Dance)
            "hiit"->  RLSummaryNameToUi(RLYourWayName.Hiit)
            "yoga"->  RLSummaryNameToUi(RLYourWayName.Yoga)
            else -> RLSummaryNameToUi(RLYourWayName.Yoga)
        }
    }
    private fun RLSummaryNameToUi(wayname: RLYourWayName) {
        val isHrConnected = cardData.hrm != 0 // hrm=0 HeartRate Not Connect && hrm!=0 HeartRate Connected
        val isClass = cardData.bmo == 0 //bmo= 0 Your Way && bmo!=0 Class
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val  maxCadence:Int = if (fireBaseCardData?.maxCadence == null) 0 else convertToInt(fireBaseCardData?.maxCadence?:0)
        val Distance = if (isImperial) RLTypeOfMetrics.Distance else  RLTypeOfMetrics.DistanceKm
        val AvgPace = if (isImperial) RLTypeOfMetrics.AvgPace else  RLTypeOfMetrics.AvgPaceKm
        val AvgSpeed = if (isImperial) RLTypeOfMetrics.AvgSpeed else  RLTypeOfMetrics.AvgSpeedKm
        val Climbed = if (isImperial) RLTypeOfMetrics.Climbed else  RLTypeOfMetrics.ClimbedM


        var rideListWithoutHr:List<Pair<RLTypeOfMetrics, RLMetricData>> = mutableListOf()
        when (wayname) {
            RLYourWayName.Ride -> {
                if(isHrConnected){
                    if(isClass){
                        // cells = [ .Time, .Effort, .HR, .Cadence, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Cadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        // .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                            RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                            RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else if(maxCadence > 0){
                    if(isClass){
                        //  cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Cadence, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        //  cells = [ .Time, .Cadence, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                        //  .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AvgCadence to RLMetricData(RLGetValueForTitle(RLValueName.AvgCadence)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
                else{
                    if(isClass){
                        // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr=  listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                    else{
                        // cells = [ .Time, .EstimatedEffort, .Distance, .EstimatedCalories,
                        // .AvgMaxSpeed, .Elevation, .Speed, .Kudos, .Comments ,.Awards]
                        //done
                        rideListWithoutHr= listOf(
                            RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                            RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                            Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                            RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                            AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                            Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                            AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                            RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                            RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                            RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                        )
                    }
                }
            }
            RLYourWayName.Run -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed,
                    // .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed,
                    // .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Walk -> {
                if(isHrConnected){
                    // cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .HR, .Effort, .ActiveCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    /// cells = [ .Time, .Steps, .Distance, .Elevation, .AvgMaxSpeed, .Speed, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=   listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Steps to RLMetricData(RLGetValueForTitle(RLValueName.Steps)),
                        Distance to RLMetricData(RLGetValueForTitle(RLValueName.Distance)),
                        Climbed to RLMetricData(RLGetValueForTitle(RLValueName.Climbed)),
                        AvgPace to RLMetricData(RLGetValueForTitle(RLValueName.AvgPace)),
                        AvgSpeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Pilates -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Warm -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Workout -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Dance -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        //RLTypeOfMetrics.TotalTime to RLMetricData(RLTools.RLdaytimeget(cardData.totalTime.toInt())),
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
            }
            RLYourWayName.Hiit -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards)),
                    )
                }
            }
            RLYourWayName.Yoga -> {
                if(isHrConnected){
                    // cells = [ .Time, .Effort, .ActiveCalories, .HR, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr= listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.Effort to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
                        RLTypeOfMetrics.ActiveCalories to RLMetricData(RLGetValueForTitle(RLValueName.ActiveCalories)),
                        RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards))
                    )
                }
                else{
                    // cells = [ .Time, .EstimatedEffort, .EstimatedCalories, .Kudos, .Comments ,.Awards]
                    //done
                    rideListWithoutHr=listOf(
                        RLTypeOfMetrics.TotalTime to RLMetricData(RLGetValueForTitle(RLValueName.TotalTime)),
                        RLTypeOfMetrics.AssumedEffort to RLMetricData(RLGetValueForTitle(RLValueName.AssumedEffort)),
                        RLTypeOfMetrics.AssumedCalories to RLMetricData(RLGetValueForTitle(RLValueName.AssumedCalories)),
                        RLTypeOfMetrics.Boosts to RLMetricData(RLGetValueForTitle(RLValueName.Boosts)),
                        RLTypeOfMetrics.Comments to RLMetricData(RLGetValueForTitle(RLValueName.Comments)),
                        RLTypeOfMetrics.Awards to RLMetricData(RLGetValueForTitle(RLValueName.Awards)),
                    )
                }
            }
        }

        RLSummryListSet(rideListWithoutHr)
    }
    private fun RLSummryListSet(dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) {
        //Main Data List Set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.recycleSession.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.recycleSession.adapter = adapterdata
        RLTools.RLheightsetimageview( fragBinding.testImage)
    }
    private fun RLGetValueForTitle(title: String): String {
        if (userCardData!=null && fireBaseCardData != null){
            val isImperial = RLTools.RLGetIsImperial(userCardData!!.appUnit)
            val avgSpeedForOneKm = RLTools.RLformatTime(convertToInt(fireBaseCardData?.avgSpeedForOneKm?:0),true)
            val avgSpeedForOneMile =RLTools.RLformatTime(convertToInt(fireBaseCardData?.avgSpeedForOneMile?:0),true)
            val avgHeartRate = RLTools.RLformatCommasInt(fireBaseCardData?.avgHr?:0.0)
            val maxHeartRate = RLTools.RLformatCommasInt(fireBaseCardData?.maxHr?:0)
            return when (title) {
                RLValueName.TotalTime -> RLTools.RLformatTime(fireBaseCardData!!.totalTime.toInt(),true)
                RLValueName.Effort  -> RLTools.RLformatCommasInt(fireBaseCardData!!.totalRev?:0.0)
                RLValueName.AvgHeartRate  -> if (!checkShowHeartRate()) avgHeartRate else avgHeartRate
                RLValueName.ActiveCalories  -> RLTools.RLformatCommasInt(convertToInt(fireBaseCardData!!.totalPower ?: 0))
                RLValueName.Boosts  -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
                RLValueName.Comments  -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
                RLValueName.Awards  -> {
                    val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                    if (totalAwards != 0) totalAwards.toString() else "0"
                }
                RLValueName.Steps  -> RLTools.RLformatCommasInt(fireBaseCardData!!.totalSteps?:0)
                RLValueName.Distance  -> if (!isImperial) RLTools.RLformatCommas(fireBaseCardData!!.distance?:0.0) else RLTools.RLformatCommas(fireBaseCardData?.distance?:0 * 0.621371)
                RLValueName.Climbed  -> {
                    val demsElevation:Int = convertToInt(fireBaseCardData!!.demsElevation?:-1)
                    val elevation = if (!isImperial) {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                    } else {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084))
                    }
                    elevation.toString()
                }
                RLValueName.AvgPace  -> if (!isImperial) avgSpeedForOneKm else  avgSpeedForOneMile
                RLValueName.AvgSpeed  ->    RLTools.RLformatCommas(cardData.average_speed?:0.0)
                RLValueName.MaxSpeed  ->    RLTools.RLformatCommas(fireBaseCardData?.maxSpeed?:0.0)
                RLValueName.AssumedEffort  -> RLTools.RLformatCommasInt(fireBaseCardData!!.totalRev?:0.0)
                RLValueName.AssumedCalories  -> RLTools.RLformatCommasInt(fireBaseCardData!!.burntCalories ?: 0.0)
                RLValueName.AvgCadence  ->  RLTools.RLformatCommasInt(fireBaseCardData!!.avgCadence?:0.0)
                RLValueName.MaxHeartRate  ->  maxHeartRate
                RLValueName.AvgEffort -> convertToInt(cardData.avgRevPercentage).toString()+"%"
                RLValueName.MaxEffort -> convertToInt(cardData.maxRevPercentage).toString()+"%"

                else -> "0"
            }
        }
        else{
            return "0"
        }
    }

    //Click Wise Ui SetUp
    private fun RLClickToSetUI() {
        fragBinding.inlayTitle.layoutSummary.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            RLSummaryUiSet()
        }
        fragBinding.inlayTitle.layoutAnalysis.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppMainColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppWhiteColor)
            RLanalysisDataSet()
        }
        fragBinding.inlayTitle.layoutEffort.setOnClickListener {
            fragBinding.inlayTitle.txtSummary.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewSummary.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtAnalysis.setTextColor(resources.getColor(R.color.AppBlackColor))
            fragBinding.inlayTitle.viewAnalysis.setBackgroundResource(R.color.AppWhiteColor)

            fragBinding.inlayTitle.txtEffoert.setTextColor(resources.getColor(R.color.AppMainColor))
            fragBinding.inlayTitle.viewEffort.setBackgroundResource(R.color.AppMainColor)
            RLeffortDataSet()
        }
    }

    //Analysis Ui SetUp
    private fun RLanalysisDataSet(){
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.VISIBLE
        fragBinding.relayEffort.visibility=View.GONE

        if (cardData.hrm==0){
            //Without HR Sensor
            fragBinding.includeEffort.relativeCard.visibility=View.GONE
            fragBinding.includeElevation.relativeCard.visibility=View.GONE
            fragBinding.includePace.relativeCard.visibility=View.GONE
            fragBinding.includeSpeed.relativeCard.visibility=View.GONE
            fragBinding.txtWithouthrmessageAnalysis.visibility=View.VISIBLE
        }else{
            //With HR Sensor
            if( classType.toLowerCase().equals("run")){
                fragBinding.includeElevation.relativeCard.visibility=View.VISIBLE
                fragBinding.includePace.relativeCard.visibility=View.VISIBLE
                fragBinding.includeSpeed.relativeCard.visibility=View.VISIBLE
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                RLanalysisEffortUISetup()
                RLanalysisPaceUISetup()
                RLanalysisSpeedUISetup()
                RLanalysisElevationUISetup()
            }else if(classType.toLowerCase().equals("walk")|| classType.toLowerCase().equals("yoga")||classType.toLowerCase().equals("pilates")||classType.toLowerCase().equals("workout")||classType.toLowerCase().equals("ride")){
                fragBinding.includeEffort.relativeCard.visibility=View.VISIBLE
                fragBinding.includeElevation.relativeCard.visibility=View.GONE
                fragBinding.includePace.relativeCard.visibility=View.GONE
                fragBinding.includeSpeed.relativeCard.visibility=View.GONE
                fragBinding.txtWithouthrmessageAnalysis.visibility=View.GONE
                RLanalysisEffortUISetup()
            }
        }
    }
    private fun RLanalysisEffortUISetup(){
        RLTools.RlLogDPrint(TAG,"NU")
        val dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.EffortScore to RLMetricData(RLGetValueForTitle(RLValueName.Effort)),
            RLTypeOfMetrics.EffortZone to RLMetricData("CALM"),
            RLTypeOfMetrics.AvgEffort to RLMetricData(RLGetValueForTitle(RLValueName.AvgEffort)),
            RLTypeOfMetrics.MaxEffort to RLMetricData(RLGetValueForTitle(RLValueName.MaxEffort)),
            RLTypeOfMetrics.AvgHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.AvgHeartRate)),
            RLTypeOfMetrics.MaxHeartRate to RLMetricData(RLGetValueForTitle(RLValueName.MaxHeartRate))
        )

        RLheightsetdisplaywebview(fragBinding.includeEffort.webViewAnalysis)
        fragBinding.includeEffort.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeEffort.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeEffort.webViewAnalysis.loadUrl("file:///android_asset/chart-android-effort.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeEffort.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeEffort.recycleAnalysis.adapter = adapterdata

        fragBinding.includeEffort.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.EFFORT)
        }
    }
    private fun RLanalysisPaceUISetup(){
        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            RLTypeOfMetrics.completed to RLMetricData("0"),
            RLTypeOfMetrics.Averagepace to RLMetricData("0"),
            RLTypeOfMetrics.Slowtest to RLMetricData("0"),
            RLTypeOfMetrics.Fasttest to RLMetricData("0")
        )

        fragBinding.includePace.imgTitleAnalysis.setImageResource(R.drawable.ic_pace)
        fragBinding.includePace.txtTitleAnalysis.setText("PACE")
        fragBinding.includePace.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includePace.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includePace.webViewAnalysis.loadUrl("file:///android_asset/chart-android-pace.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includePace.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includePace.recycleAnalysis.adapter = adapterdata

        fragBinding.includePace.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.PACE)
        }
    }
    private fun RLanalysisSpeedUISetup(){
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val Averagespeed = if (isImperial) RLTypeOfMetrics.Averagespeed else  RLTypeOfMetrics.AveragespeedKm
        val MaxSpeed = if (isImperial) RLTypeOfMetrics.MaxSpeed else  RLTypeOfMetrics.MaxSpeedKM

        var dataList:List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Averagespeed to RLMetricData(RLGetValueForTitle(RLValueName.AvgSpeed)),
            MaxSpeed to RLMetricData(RLGetValueForTitle(RLValueName.MaxSpeed))
        )

        fragBinding.includeSpeed.imgTitleAnalysis.setImageResource(R.drawable.ic_speeed)
        fragBinding.includeSpeed.txtTitleAnalysis.setText("Speed")

        RLheightsetdisplaywebview(fragBinding.includeSpeed.webViewAnalysis)
        fragBinding.includeSpeed.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeSpeed.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeSpeed.webViewAnalysis.loadUrl("file:///android_asset/chart-android-speed.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeSpeed.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeSpeed.recycleAnalysis.adapter = adapterdata

        fragBinding.includeSpeed.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.SPEED)
        }
    }
    private fun RLanalysisElevationUISetup(){
        val isImperial = RLTools.RLGetIsImperial(userCardData?.appUnit?:"Metric")
        val Totalclimbed = if (isImperial) RLTypeOfMetrics.Totalclimbed else  RLTypeOfMetrics.TotalclimbedM
        val Minelevation = if (isImperial) RLTypeOfMetrics.Minelevation else  RLTypeOfMetrics.MinelevationM
        val Maxelevation = if (isImperial) RLTypeOfMetrics.Maxelevation else  RLTypeOfMetrics.MaxelevationM

        var dataList: List<Pair<RLTypeOfMetrics, RLMetricData>> = listOf(
            Totalclimbed to RLMetricData(cardData.elevation.toString()),
            Minelevation to RLMetricData("0"),
            Maxelevation to RLMetricData("0")
        )

        fragBinding.includeElevation.imgTitleAnalysis.setImageResource(R.drawable.ic_climb)
        fragBinding.includeElevation.txtTitleAnalysis.setText("Elevation")

        RLheightsetdisplaywebview(fragBinding.includeElevation.webViewAnalysis)
        fragBinding.includeElevation.webViewAnalysis.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.includeElevation.webViewAnalysis.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        fragBinding.includeElevation.webViewAnalysis.loadUrl("file:///android_asset/chart-android-elevation.html")

        //Main list set
        val glinearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.includeElevation.recycleAnalysis.layoutManager = glinearLayoutManager
        val adapterdata = RLFeedSessionSummryListAdapter(activity, dataList, cardData)
        fragBinding.includeElevation.recycleAnalysis.adapter = adapterdata

        fragBinding.includeElevation.txtTitleAnalysis.setOnClickListener {
            RLinjectDataIntoWebView(RLConstants.ELEVATION)
        }


    }
    private fun RLinjectDataIntoWebView(maptype:String) {
        // Inject JSON data into WebView's JavaScript context
        if (maptype.equals(RLConstants.EFFORT)) {
            val jsonArray: JSONArray = JSONArray()
            val time = arrayListOf(0, 7, 11, 16, 21, 26, 31, 36, 46, 61)
            val effort = arrayListOf(15.5, 20.7, 22.5, 23.3, 27.6, 28.5, 31.1, 32.8, 43.2, 53.5)
            for (i in 0 until 10) {
                val jsonObject: JSONObject = JSONObject()
                jsonObject.put("time", time[i])
                jsonObject.put("effort", effort[i])
                jsonArray.put(jsonObject)
            }
            fragBinding.includeEffort.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetEffortChartHtml(jsonArray.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.ELEVATION)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            fragBinding.includeElevation.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetElevationHtml( cumDistance.toString(),elevation.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.SPEED)) {
            val cumDistance = listOf(0.005296782793065954, 0.17643476423982488, 0.32406591625405773, 0.4900312010503477, 0.6886913564745826, 0.8554970494682735, 0.9942858127871057, 1.1225646445024045, 1.2667188760567833, 1.4243537519381972)
            val elevation = listOf(62.0, 68.2, 77.6, 74.8, 69.1, 68.3, 71.2, 71.2, 70.9, 68.7)
            val speed = listOf(1.1917761284398396, 13.271195141645485, 16.3731288772218, 25.100697267202097, 30.414623993580445, 23.07914084016237, 19.85637565717453, 22.02540041430026, 21.509108339338717, 22.04665020256577)
            fragBinding.includeSpeed.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetSpeedHtml( cumDistance.toString(),elevation.toString(),speed.toString()), "text/html", "UTF-8", null)
        }else if(maptype.equals(RLConstants.PACE)) {
            val timeData = listOf(225, 165, 135, 132, 124, 131)
            fragBinding.includePace.webViewAnalysis.loadDataWithBaseURL(null, RLAllHTMLChart.RLgetPaceChartHtml( timeData.toString()), "text/html", "UTF-8", null)

        }
    }

    //Effort Ui SetUp
    private fun RLeffortDataSet(){
        val dataList: List<String> = listOf("Calm","Warm","Cardio","Fat Burn","Endurance","Power","Peak")
        fragBinding.relaySummary.visibility=View.GONE
        fragBinding.relayAnalysis.visibility=View.GONE
        fragBinding.relayEffort.visibility=View.VISIBLE
       

        fragBinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        fragBinding.inlayChart.layEffortZone.txtNumber.setText(R.string.cardio)
        fragBinding.inlayChart.layEffortZone.txtNumber.setTextColor(resources.getColor(R.color.AppMainColor))

        fragBinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        fragBinding.inlayChart.layEffort.txtNumber.setText("57%")

        fragBinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        fragBinding.inlayChart.layEffortScore.txtNumber.setText("468")

        fragBinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        fragBinding.inlayChart.layMaxEffort.txtNumber.setText("84%")

        RLTools.RLheightsetdisplaywebview(fragBinding.inlayChart.webViewChart,activity)
        fragBinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = fragBinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        fragBinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.RLGetNewZoneChartHtml(), "text/html", "UTF-8", null)


        //Main list set
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.recycleEffort.layoutManager = linearLayoutManager
        val adapterdata = RLFeedSessionEffortListAdapter(activity, dataList)
        fragBinding.recycleEffort.adapter = adapterdata

        if (cardData.hrm==0){
            //Without HR Sensor
            fragBinding.inlayChart.layAll.visibility=View.GONE
            fragBinding.layEffortTit.visibility=View.GONE
            fragBinding.recycleEffort.visibility=View.GONE
            fragBinding.txtWithouthrmessageEffort.visibility=View.VISIBLE
        }else{
            //With HR Sensor
            fragBinding.inlayChart.layAll.visibility=View.VISIBLE
            fragBinding.layEffortTit.visibility=View.VISIBLE
            fragBinding.recycleEffort.visibility=View.VISIBLE
            fragBinding.txtWithouthrmessageEffort.visibility=View.GONE
        }
    }

    //Common All UI Setup
    private fun RLheightsetdisplaywebview(webView: WebView) {
       RLTools.RLheightsetdisplaywebview(webView,activity)
    }
    override fun onPause() {
        super.onPause()
        RLBottomHideShowSet(true)
    }
    private fun RLcloseScreen(isSessionComplete:Boolean){
        if (isSessionComplete){
            RLBottomHideShowSet(true)
            (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
        }else{
            RLcloseFragment()
        }
    }

    //Calculation All UI Setup
    private fun String.extractImageUrls(): List<String> {
        return this.replace("\\", "")
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    private fun convertToInt(value: Any): Int {
        return when (value) {
            is Double -> value.roundToInt()
            is Float -> value.roundToInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
            else -> 0 // Default fallback for unsupported types
        }
    }
    private fun checkShowHeartRate(): Boolean {
        if (!userCardData?.currentGroup.isNullOrEmpty() && userCardData?.currentGroup!!.contains("schooltype1")) {
            return false
        }
        return true
    }

}