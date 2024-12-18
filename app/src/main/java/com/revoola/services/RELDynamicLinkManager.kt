package com.revoola.services

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.feed.RLFragChallengeSummary
import com.revoola.fragment.feed.RLFragFeed
import com.revoola.fragment.feed.RLFragFeedCardLikeCommentView
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragFriends
import com.revoola.fragment.friends.RLFragInviteFriends
import com.revoola.fragment.friends.RLFragYourFriends
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.more.RLFragChangePassword
import com.revoola.fragment.more.RLFragNotification
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.RLFragStart
import com.revoola.fragment.start.body.RLFragBodyClasses
import com.revoola.fragment.start.body.RLFragBodyClassesView
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.fragment.start.mind.RLFragMindClassesView
import com.revoola.fragment.start.yourway.RLFragYourWay
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTools

class RELDynamicLinkManager private   constructor() {
    private val dynamicLink: RELDynamicLink = RELDynamicLink.getInstance()
    val TAG = "RELDynamicLinkManager"
    lateinit var context: Context

    fun RLCheckLink(link: String,context_t: Context) {
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
                    RLTools.RlLogDPrint(TAG,"param All :- $param")
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
        val deepLinkPage = RlGetDeepLinkPage()
        RLTools.RlLogDPrint(TAG,"deepLinkPage:- $deepLinkPage")
        if (!deepLinkPage.isNullOrEmpty()){
            RlRedirectToPage(deepLinkPage)
        }

    }

    private fun RlGetDeepLinkPage() : String {
        return dynamicLink.deepLinkPage.toLowerCase()
    }
    private fun RlGetLink() :RELDynamicLink {
        return dynamicLink
    }

    private fun RlRedirectToPage(deepLinkPage:String) {
        if(deepLinkPage.equals("m") || deepLinkPage.equals("b") || deepLinkPage.equals("o")) {
            /*  RELFirebase.shared().getMainDatabase()
                                .child(RevoolaKeys.ProposedStructure)
                                .child(RevoolaKeys.RevoolaUserSettings)
                                .child(RELAccountManager.shared().getUser().globalUid)
                                .child(RevoolaKeys.BasicData)
                                .child("link")
                                .setValue("")*/

            when (deepLinkPage) {
                "m" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses(), TAG, true, null, false)
                }
                "b" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClasses(), TAG, true, null, false)
                }
                "o" -> {
                    dynamicLink.deepLinkPage = ""
                    (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
                }
            }
        }
        else if (deepLinkPage.equals("leaderboard")){
            //REVLeaderboardListVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""

        }
        else if (deepLinkPage.equals("feed")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).RLSelectionbottombar(R.id.feed)
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragFeed(), TAG, false,null, false)
        }
        else if (deepLinkPage.equals("fri")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).RLSelectionbottombar(R.id.friends)
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragFriends(), TAG, false, null, false)
        }
        else if (deepLinkPage.equals("start")){
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).RLSelectionbottombar(R.id.start)
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, false, null, false)
        }
        else if(deepLinkPage.equals("invite")){
            //REVSendInviteVC (SKIP NOW)
            dynamicLink.deepLinkPage = ""
        }
        else if(deepLinkPage.equals( "convert-guest") || deepLinkPage.equals("convert-guest2")){
            //RELConnectGuestVC (guest valu page ave ama more ma try valu avse
            val isAuthTrial = dynamicLink.deepLinkPage == "convert-guest2"
            dynamicLink.deepLinkPage = ""
        }
        else if(deepLinkPage.equals("reset")){
            //REVPasswordChangeVC
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).RLloadFrag(RLFragChangePassword(), TAG, true, null, false)
        }
        else if (deepLinkPage.equals("metric") && dynamicLink.metric.isNotEmpty()) {
            //RELMatricDetailVC (SKIP NOW)
            //val metric = RELMetricTitle(rawValue: dynamicLink.metric.capitalized)
            dynamicLink.deepLinkPage = ""
            dynamicLink.metric = ""
        }
        else if (deepLinkPage.equals("share-card") && dynamicLink.id != 0) {
            //RELSingleFeedVC
            dynamicLink.deepLinkPage = ""
        }
        else if (deepLinkPage.equals("card-detail") && dynamicLink.id != 0) {
            dynamicLink.deepLinkPage = ""
            /*RELWebRequest.shared().getOverviewDataV3(dynamicLink.id) {
                (result, error) in
                DispatchQueue.main.async {
                    topVc.hideLoader()
                    if val _result = result, _result.fromThirdPartySource == 0 {
                    when (_result.resultFor) {
                            .Body &&.Open -> {
                                val controller = RELResultContainerVC(nibName: "RELResultContainerVC", bundle: nil)
                                controller.isResults = false
                                controller.resultListData = _result
                                navigateManager(topVc: topVc, controller: controller)
                            }
                            .Mind-> {
                            val controller = RELNEWMindResultPageVC(nibName: "RELNEWMindResultPageVC", bundle: nil)
                            controller.isResults = false
                            controller.resultListData = _result
                            navigateManager(topVc: topVc, controller: controller)
                            }
                    }
                } }
            }*/
        }
        else if (dynamicLink.classId.isNotEmpty()) {
            val classId = dynamicLink.classId
            dynamicLink.classId = ""
            if (dynamicLink.classType == "0"){
                //RELBodyClassDetailsVC
                val bundle = Bundle()
                //bundle.putString("VIDEODATA",cardData.key)
                // bundle.putBoolean("Ride",ride)
                (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesView().newInstance(bundle), TAG, true, null, true)

            }
            else{
                //RELMindClassDetailsVC
                val bundle = Bundle()
                //bundle.putString("AUDIOVIDEOTYPE", cardData.classtype)
                //bundle.putString("VIDEODATA",cardData.key)
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesView().newInstance(bundle), TAG, true, null, true)

            }
        }
        else if (dynamicLink.challengeId.isNotEmpty()) {
            //RELGoaledChallengeResultVC
            val challengeId = dynamicLink.challengeId
            dynamicLink.challengeId = ""
            val bundle = Bundle()
           // bundle.putSerializable(RLConstants.CardData, cardData)
            (context as RLMainActivityRL).RLloadFrag(RLFragChallengeSummary().newInstance(bundle), TAG, true, null, false)
        }
        else if (deepLinkPage.equals("kudos")){
            //RELKudosVC
            dynamicLink.deepLinkPage = ""
            var passstring="Thumb"
            val bundle = Bundle()
            //bundle.putSerializable(RLConstants.CardData, cardData)
            bundle.putString(RLConstants.TYPE, passstring)
            (context as RLMainActivityRL).RLloadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
        }
        else if (deepLinkPage.equals("comments") ){
            //REVResultCommentsVC
            dynamicLink.deepLinkPage = ""
            var passstring="Comment"
            val bundle = Bundle()
            //bundle.putSerializable(RLConstants.CardData, cardData)
            bundle.putString(RLConstants.TYPE, passstring)
            (context as RLMainActivityRL).RLloadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
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
            //REVFriendsRequestListVC
            dynamicLink.deepLinkPage = ""
        }
        else if (deepLinkPage.equals("fri-detail")){
            if (RELDynamicLinkManager.getInstance().RlGetLink().pageType.equals("find") ){
                //REVSearchFriendsVC
                (context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, null, false)
            }
            else if (RELDynamicLinkManager.getInstance().RlGetLink().pageType.equals("friends")) {
                //REVYourFriendsVC
                (context as RLMainActivityRL).RLloadFrag(RLFragYourFriends(), TAG, true,null, false)
            }
            else if (RELDynamicLinkManager.getInstance().RlGetLink().pageType.equals("groups")) {
                //REVYourGroupsVC
                (context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, null, false)
            }
        }
        else if (deepLinkPage.equals("notification-list") ){
            //RELNotificationVC
            dynamicLink.deepLinkPage = ""
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, null, false)
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
