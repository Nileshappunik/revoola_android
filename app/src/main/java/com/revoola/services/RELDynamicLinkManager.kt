package com.revoola.services

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.fragment.feed.RLFragChallengeSummary
import com.revoola.fragment.feed.RLFragFeed
import com.revoola.fragment.feed.RLFragFeedCardLikeCommentView
import com.revoola.fragment.feed.RLFragMindSessionSummary
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragFriends
import com.revoola.fragment.friends.RLFragYourFriends
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.more.RLFragChangePassword
import com.revoola.fragment.more.RLFragNotification
import com.revoola.fragment.start.RLFragStart
import com.revoola.fragment.start.body.RLFragBodyClasses
import com.revoola.fragment.start.body.RLFragBodyClassesView
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.fragment.start.mind.RLFragMindClassesView
import com.revoola.fragment.start.yourway.RLFragYourWay
import com.revoola.model.RLRequestDetail_dataset
import com.revoola.model.RLTextOverview
import com.revoola.model.RLoverview_thumb_from_id
import com.revoola.model.RLrequest_challenges_feed_thumbs
import com.revoola.model.RLrequest_goaled_challenges
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RevoolaKeys
import com.revoola.enumclass.RLFriendsFollowType
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RELDynamicLinkManager private   constructor() {
    private val dynamicLink: RELDynamicLink = RELDynamicLink.getInstance()
    val TAG = "RELDynamicLinkManager"
    lateinit var context: Context
    lateinit var apiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel

    fun rl_checkLink(link: String, context_t: Context, rlMainActivityRL: RLMainActivityRL) {
        context = context_t
         dynamicLink.link = link

        // Split the link based on "/"
        val comp = link.split("/")
        val lastSegment = comp.lastOrNull()

        if (lastSegment != null) {
            if (lastSegment.contains("?")) {
                // Extract query parameters from the link
                val urlCode = lastSegment.split("?")
                val params = urlCode.getOrNull(1)?.split("&") ?: return

                params.forEach { param ->
                    RLTools.rl_logDPrint(TAG,"param All :- $param")
                    when {
                        param.contains("page=") -> dynamicLink.deepLinkPage = param.replace("page=", "")
                        param.contains("id=") -> dynamicLink.id = param.replace("id=", "").toInt()
                        param.contains("user=") -> dynamicLink.referId = param.replace("user=", "")
                        param.contains("company=") -> dynamicLink.company = param.replace("company=", "")
                        param.contains("fromLink=") -> dynamicLink.fromLink = param.replace("fromLink=", "")
                        param.contains("pl=") -> dynamicLink.plan = param.replace("pl=", "")
                        param.contains("utp=") -> dynamicLink.inviteUserType = param.replace("utp=", "")
                        param.contains("usm=") -> dynamicLink.inviteUserSubsModel = param.replace("usm=", "")
                        param.contains("com=") -> dynamicLink.commisionFlag = param.replace("com=", "")
                        param.contains("re=") -> dynamicLink.referrerTag = param.replace("re=", "")
                        param.contains("last=") -> dynamicLink.lastDate = param.replace("last=", "")
                        param.contains("classId=") -> dynamicLink.classId = param.replace("classId=", "")
                        param.contains("challengeId=") -> dynamicLink.challengeId = param.replace("challengeId=", "")
                        param.contains("classType=") -> dynamicLink.classType = param.replace("classType=", "")
                        param.contains("metric=") -> dynamicLink.metric = param.replace("metric=", "")
                        param.contains("trialDays=") -> dynamicLink.trialDays = param.replace("trialDays=", "")
                        param.contains("isFullPremium=") -> dynamicLink.isFullPremium = param.replace("isFullPremium=", "")
                        param.contains("branchLink=") -> dynamicLink.branchLink = param.replace("branchLink=", "")
                        param.contains("pageType=") -> dynamicLink.pageType = param.replace("pageType=", "")
                        param.contains("isOnlySubscription=") -> dynamicLink.isOnlySubscription = param.replace("isOnlySubscription=", "")
                        param.contains("isUserInvite=") -> dynamicLink.isUserInvite = param.replace("isUserInvite=", "")
                        param.contains("isForExistingUser=") -> dynamicLink.isForExistingUser = param.replace("isForExistingUser=", "")
                        param.contains("inc_chall=") -> dynamicLink.isForChallenge = param.replace("inc_chall=", "")
                        param.contains("isJoin=") -> dynamicLink.isJoin = param.replace("isJoin=", "")
                        param.contains("originalChallengeId=") -> dynamicLink.originalChallengeId = param.replace("originalChallengeId=", "")
                    }
                }
            } else {
                // If there's no query part, check if it contains "user="
                if (lastSegment.contains("user=")) {
                    dynamicLink.referId = lastSegment.replace("user=", "")
                }
            }
        }
        val deepLinkPage = rl_getDeepLinkPage()
        RLTools.rl_logDPrint(TAG,"deepLinkPage:- $deepLinkPage")
        if (!deepLinkPage.isNullOrEmpty()){
            rl_redirectToPage(deepLinkPage,rlMainActivityRL)
        }

    }

    private fun rl_getDeepLinkPage() : String {
        return dynamicLink.deepLinkPage.toLowerCase()
    }
    private fun rl_getLink() :RELDynamicLink {
        return dynamicLink
    }

    private fun rl_redirectToPage(deepLinkPage: String, rlMainActivityRL: RLMainActivityRL) {
        // Api call
        apiClientRetrofit = RLApiClientRet(context)
        val apiService = apiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(rlMainActivityRL, RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)



        if(deepLinkPage.equals("m") || deepLinkPage.equals("b") || deepLinkPage.equals("o")) {
            val databaseManager = RLDatabaseManagerWrite()
            val currentUser=  RLPrefManager.rl_getSomeStringValue(context, RLPrefManager.current_user, "")
            databaseManager.revoola_Deep_Link_Write(currentUser)
            when (deepLinkPage) {
                "m" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).rl_loadFrag(RLFragMindClasses(), TAG, true, null, false)
                }
                "b" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).rl_loadFrag(RLFragBodyClasses(), TAG, true, null, false)
                }
                "o" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).rl_loadFrag(RLFragYourWay(), TAG, true, null, false)
                }
            }
        }
        else if (deepLinkPage.equals("leaderboard")){
            //REVLeaderboardListVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""

        }
        else if (deepLinkPage.equals("feed")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).rl_selectionbottombar(R.id.feed)
            (context as RLMainActivityRL).rl_showbottombarcolorwhite()
            (context as RLMainActivityRL).rl_loadFrag(RLFragFeed(), TAG, false,null, false)
        }
        else if (deepLinkPage.equals("fri")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).rl_selectionbottombar(R.id.friends)
            (context as RLMainActivityRL).rl_showbottombarcolorwhite()
            (context as RLMainActivityRL).rl_loadFrag(RLFragFriends(), TAG, false, null, false)
        }
        else if (deepLinkPage.equals("start")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).rl_selectionbottombar(R.id.start)
            (context as RLMainActivityRL).rl_showbottombarcolorwhite()
            (context as RLMainActivityRL).rl_loadFrag(RLFragStart(), TAG, false, null, false)
        }
        else if(deepLinkPage.equals("invite")){
            //REVSendInviteVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""
        }
        else if(deepLinkPage.equals( "convert-guest") || deepLinkPage.equals("convert-guest2")){
            //TODO RELConnectGuestVC (guest valu page ave ama more ma try valu avse)
            val isAuthTrial = dynamicLink.deepLinkPage == "convert-guest2"
            dynamicLink.deepLinkPage = ""
        }
        else if(deepLinkPage.equals("reset")){
            //REVPasswordChangeVC
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).rl_loadFrag(RLFragChangePassword(), TAG, true, null, false)
        }
        else if (deepLinkPage.equals("metric") && dynamicLink.metric.isNotEmpty()) {
            //RELMatricDetailVC (SKIP NOW)
            //val metric = RELMetricTitle(rawValue: dynamicLink.metric.capitalized)
            dynamicLink.deepLinkPage = ""
            dynamicLink.metric = ""
        }
        else if (deepLinkPage.equals("share-card") && dynamicLink.id != 0) {
            //TODO RELSingleFeedVC (fEED-FRIEND-LIST ONLY ONE CARD SHOW) TESTING PENDING
            dynamicLink.deepLinkPage = ""
            rl_apiCallCardDetail(dynamicLink.id,false,"share-card")
        }
        else if (deepLinkPage.equals("card-detail") && dynamicLink.id != 0) {
            //TODO API CALL RELResultContainerVC:- FEED-FRIEND-LIST ITEM VIEW (YOUWAY,BODY) (FIRST API CALL AFTER REDIRECPAGE) TESTING PENDING
            //TODO API CALL RELNEWMindResultPageVC:- FEED-FRIEND-LIST ITEM VIEW (MIND) (FIRST API CALL AFTER REDIRECPAGE) TESTING PENDING
            dynamicLink.deepLinkPage = ""
            rl_apiCallCardDetail(dynamicLink.id,false,"")

        }
        else if (dynamicLink.classId.isNotEmpty()) {
            val classId = dynamicLink.classId
            dynamicLink.classId = ""
            if (dynamicLink.classType == "0"){
                //RELBodyClassDetailsVC
                val bundle = Bundle()
                bundle.putString("VIDEODATA",classId)
                 bundle.putBoolean("Ride",false)
                (context as RLMainActivityRL).rl_loadFrag(RLFragBodyClassesView().newInstance(bundle), TAG, true, null, true)

            }
            else{
                //RELMindClassDetailsVC
                val bundle = Bundle()
                bundle.putString("AUDIOVIDEOTYPE", "Video")
                bundle.putString("VIDEODATA",classId)
                (context as RLMainActivityRL).rl_loadFrag(RLFragMindClassesView().newInstance(bundle), TAG, true, null, true)

            }
        }
        else if (dynamicLink.challengeId.isNotEmpty()) {
            //TODO RELGoaledChallengeResultVC (FEED-CHALLENGES-LIST ITEM VIEW) TESTING PENDING
            val challengeId = dynamicLink.challengeId
            dynamicLink.challengeId = ""
            rl_challengeDataGetApi(challengeId.toInt())
        }
        else if (deepLinkPage.equals("kudos")){
            //TODO RELKudosVC (FEED kUDO PAGE) TESTING PENDING
            dynamicLink.deepLinkPage = ""
            var passstring="Thumb"
            rl_apiCallCardDetail(dynamicLink.id,true,passstring)
        }
        else if (deepLinkPage.equals("comments") ){
            //TODO REVResultCommentsVC (FEED COMMENT PAGE) TESTING PENDING
            dynamicLink.deepLinkPage = ""
            var passstring="Comment"
            rl_apiCallCardDetail(dynamicLink.id,true,passstring)
        }
        else if (deepLinkPage.equals("challenge")) {
            //REVMyChallengesVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""
        }
        else if (deepLinkPage.equals("schedule")) {
            //REVMyChallengesVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""
        }
        else if (deepLinkPage.equals("fri-request") ){
            //TODO REVFriendsRequestListVC (FRIEND-POPOPEN-YES-REDIRECFRIENDPAGE) TESTING PENDING
            dynamicLink.deepLinkPage = ""
            val bundle =Bundle ()
            bundle.putString(RevoolaKeys.FriendsFollowType, RLFriendsFollowType.FriendRequest.name)
            (context as RLMainActivityRL).rl_loadFrag(RLFragYourFriends(), TAG, true,null, false)
        }
        else if (deepLinkPage.equals("fri-detail")){
            if (RELDynamicLinkManager.getInstance().rl_getLink().pageType.equals("find") ){
                //REVSearchFriendsVC
                (context as RLMainActivityRL).rl_loadFrag(RLFragFindOnRevoola(), TAG, true, null, false)
            }
            else if (RELDynamicLinkManager.getInstance().rl_getLink().pageType.equals("friends")) {
                //REVYourFriendsVC
                val bundle =Bundle ()
                bundle.putBoolean("reDirecDeepLinkPage",false)
                (context as RLMainActivityRL).rl_loadFrag(RLFragYourFriends(), TAG, true,null, false)
            }
            else if (RELDynamicLinkManager.getInstance().rl_getLink().pageType.equals("groups")) {
                //REVYourGroupsVC
                (context as RLMainActivityRL).rl_loadFrag(RLFragYourGroup(), TAG, true, null, false)
            }
        }
        else if (deepLinkPage.equals("notification-list") ){
            //RELNotificationVC
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).rl_loadFrag(RLFragNotification(), TAG, true, null, false)
        }
    }

    companion object {
        @Volatile
        private var instance: RELDynamicLinkManager? = null

        fun getInstance(): RELDynamicLinkManager {
            return instance ?: synchronized(this) {
                instance ?: RELDynamicLinkManager().also { instance = it }
            }
        }
    }

    private fun rl_apiCallCardDetail(id:Int, commentPageOpen:Boolean, passstring:String) {
         if (apiClientRetrofit.rl_isConnected()) {
            val request = listOf(RLRequestDetail_dataset(overview_thumb_from_id = RLoverview_thumb_from_id(id =id)))
            RLTools.rl_logDPrint(TAG,"setgroupdata= "+request)
            //Detail Api
            viewModel.rl_getOverviewThumbFromIdData(request) { result ->
                result.onSuccess { response ->
                    try {
                        if (response.type.equals("success")) {
                            RLTools.rl_logDPrint(TAG, "Success= " + response.type)
                            if (response.text.size > 0) {
                                val cardData = response.text[0]
                                if (commentPageOpen){
                                    val bundle = Bundle()
                                    bundle.putSerializable(RLConstants.CardData, cardData)
                                    bundle.putString(RLConstants.TYPE, passstring)
                                    (context as RLMainActivityRL).rl_loadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
                                }else{
                                    if (passstring.equals("share-card")){
                                      //Open Challenge Card And Share This Card ScreenShot like FeedList Adapter
                                    }else{
                                        rl_clickEventProcess(cardData)
                                    }

                                }

                            }
                        } else {
                            RLTools.rl_logDPrint(TAG, "Fail= " + response.type)

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        RLTools.rl_logDPrint(TAG, "Catch= " + e.message)

                    }
                }.onFailure { error ->
                    RLTools.rl_logDPrint(TAG, "Error= " + error.message)
                }
            }
        }

    }

    private fun rl_clickEventProcess(cardData: RLTextOverview) {
        var selectTag="FRIENDS"
        if (cardData.from_third_party_source == 0){
            when (cardData.bmo){
                0->{
                    //BODY
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    bundle.putString(RLConstants.FeedSelectTag, selectTag)
                    bundle.putBoolean("isSessionComplete", false)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragSessionSummary().newInstance(bundle), TAG, true, null, true)
                   // (context as RLMainActivityRL).RLloadFrag(RLFragBodySessionSummary().newInstance(bundle), TAG, true, null, true)
                }
                1->{
                    //MIND
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    bundle.putString(RLConstants.FeedSelectTag, selectTag)
                    bundle.putBoolean("isSessionComplete", false)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragMindSessionSummary().newInstance(bundle), TAG, true, null, true)
                }
                2->{
                    //OTHER
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    bundle.putString(RLConstants.FeedSelectTag, selectTag)
                    bundle.putBoolean("isSessionComplete", false)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragSessionSummary().newInstance(bundle), TAG, true, null, true)
                }

            }
        }
    }

    private fun rl_challengeDataGetApi(challengeid:Int){
        val currentUser=  RLPrefManager.rl_getSomeStringValue(context, RLPrefManager.current_user, "")
        val currentTimestamp = (System.currentTimeMillis() / 1000).toString()
        //var id:Int, var type:String, var today:String,var challengeid:Int
        val request = listOf(
            RLrequest_goaled_challenges(goaled_challenges = RLrequest_challenges_feed_thumbs(
                id = currentUser,type = "challenges_feed_thumbs", today = currentTimestamp.toString(),challengeid)
            )
        )
        RLTools.rl_logDPrint(TAG,"setgoaled_challenges= "+request)
        viewModel.rl_goaled_challenges_view(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){
                        RLTools.rl_logDPrint(TAG,"Success= "+response.type)
                        if (response.text.data.size>0){
                            val bundle = Bundle()
                            bundle.putSerializable(RLConstants.CardData, response.text.data[0])
                            (context as RLMainActivityRL).rl_loadFrag(RLFragChallengeSummary().newInstance(bundle), TAG, true, null, false)
                        }

                    }else {
                        RLTools.rl_logDPrint(TAG,"Fail= "+response.type)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    RLTools.rl_logDPrint(TAG,"Catch= "+e.message)
                }
            }.onFailure { error ->
                RLTools.rl_logDPrint(TAG,"Error= "+error.message)
            }
        }

    }

}


class RELDynamicLink private constructor() {
    var referId: String = "PlayStore"
    var deepLinkPage: String = ""
    var company: String = ""
    var fromLink: String = ""
    var plan: String = ""
    var commisionFlag: String = ""
    var inviteUserType: String = "NormalUser"
    var inviteUserSubsModel: String = "1" // 0 = Apple, 1 = NonApple
    var referrerTag: String = "Android"
    var lastDate: String = ""
    var classId: String = ""
    var challengeId: String = ""
    var classType: String = "0"
    var pageType: String = ""
    var link: String = ""
    var originalLink: String = ""
    var trialDays: String = "14"
    var id: Int = 0
    var metric: String = ""
    var isFullPremium: String = ""
    var branchLink: String = ""
    var isOnlySubscription: String = ""
    var isUserInvite: String = ""
    var isForExistingUser: String = ""
    var isForChallenge: String = ""
    var isJoin: String = ""
    var originalChallengeId: String = ""

    companion object {
        @Volatile
        private var sharedInstance: RELDynamicLink? = null

        fun getInstance(): RELDynamicLink {
            return sharedInstance ?: synchronized(this) {
                sharedInstance ?: RELDynamicLink().also { sharedInstance = it }
            }
        }
    }
}

/*
RELMatricDetailVC:- SKIP //DONE
RELSingleFeedVC:- fEED-FRIEND-LIST ONLY ONE CARD SHOW
RELResultContainerVC:- FEED-FRIEND-LIST ITEM VIEW (YOUWAY,BODY) (FIRST API CALL AFTER REDIRECPAGE)
RELNEWMindResultPageVC:- FEED-FRIEND-LIST ITEM VIEW (MIND) (FIRST API CALL AFTER REDIRECPAGE)
RELGoaledChallengeResultVC:-FEED-CHALLENGES-LIST ITEM VIEW  // DONE
RELKudosVC:- FEED kUDO PAGE   // DONE
REVResultCommentsVC:- FEED COMMENT PAGE  // DONE
REVMyChallengesVC:- SKIP
REVFriendsRequestListVC:- FRIEND-POPOPEN-YES-REDIRECFRIENDPAGE */
