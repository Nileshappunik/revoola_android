package com.revoola.services

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import io.branch.referral.Branch
class RELDynamicLinkManager {
        private val dynamicLink = RELDynamicLink.shared()
        private var isRedirectPending: Boolean? = null

        fun shared(): RELDynamicLinkManager {
            return this
        }



        fun checkLink(link: String) {
            dynamicLink.link = link

            val comp = link.split("/")
            val _last = comp.lastOrNull()
            if (_last != null) {
                if (_last.contains("?")) {
                    val urlCode = _last.split("?")
                    val _lastUrlCode = urlCode.lastOrNull()
                    if (_lastUrlCode != null) {
                        val params = _lastUrlCode.split("&")
                        params.firstOrNull { it.contains("page=") }?.let {
                            dynamicLink.deepLinkPage = it.replace("page=", "")
                        }
                        params.firstOrNull { it.contains("id=") }?.let {
                            dynamicLink.id = it.replace("id=", "").toIntOrNull() ?: 0
                        }
                        params.firstOrNull { it.contains("user=") }?.let {
                            dynamicLink.referId = it.replace("user=", "")
                        }
                        params.firstOrNull { it.contains("company=") }?.let {
                            dynamicLink.company = it.replace("company=", "")
                        }
                        params.firstOrNull { it.contains("fromLink=") }?.let {
                            dynamicLink.fromLink = it.replace("fromLink=", "")
                        }
                        params.firstOrNull { it.contains("pl=") }?.let {
                            dynamicLink.plan = it.replace("pl=", "")
                        }
                        params.firstOrNull { it.contains("utp=") }?.let {
                            dynamicLink.inviteUserType = it.replace("utp=", "")
                        }
                        params.firstOrNull { it.contains("usm=") }?.let {
                            dynamicLink.inviteUserSubsModel = it.replace("usm=", "")
                        }
                        params.firstOrNull { it.contains("com=") }?.let {
                            dynamicLink.commisionFlag = it.replace("com=", "")
                        }
                        params.firstOrNull { it.contains("re=") }?.let {
                            dynamicLink.referrerTag = it.replace("re=", "")
                        }
                        params.firstOrNull { it.contains("last=") }?.let {
                            dynamicLink.lastDate = it.replace("last=", "")
                        }
                        params.firstOrNull { it.contains("classId=") }?.let {
                            dynamicLink.classId = it.replace("classId=", "")
                        }
                        params.firstOrNull { it.contains("challengeId=") }?.let {
                            dynamicLink.challengeId = it.replace("challengeId=", "")
                        }
                        params.firstOrNull { it.contains("classType=") }?.let {
                            dynamicLink.classType = it.replace("classType=", "")
                        }
                        params.firstOrNull { it.contains("metric=") }?.let {
                            dynamicLink.metric = it.replace("metric=", "")
                        }
                        params.firstOrNull { it.contains("trialDays=") }?.let {
                            dynamicLink.trialDays = it.replace("trialDays=", "")
                        }
                        params.firstOrNull { it.contains("isFullPremium=") }?.let {
                            dynamicLink.isFullPremium = it.replace("isFullPremium=", "")
                        }
                        params.firstOrNull { it.contains("branchLink=") }?.let {
                            dynamicLink.branchLink = it.replace("branchLink=", "")
                        }
                        params.firstOrNull { it.contains("pageType=") }?.let {
                            dynamicLink.pageType = it.replace("pageType=", "")
                        }
                        params.firstOrNull { it.contains("isOnlySubscription=") }?.let {
                            dynamicLink.isOnlySubscription = it.replace("isOnlySubscription=", "")
                        }
                        params.firstOrNull { it.contains("isUserInvite=") }?.let {
                            dynamicLink.isUserInvite = it.replace("isUserInvite=", "")
                        }
                        params.firstOrNull { it.contains("isForExistingUser=") }?.let {
                            dynamicLink.isForExistingUser = it.replace("isForExistingUser=", "")
                        }
                        params.firstOrNull { it.contains("inc_chall=") }?.let {
                            dynamicLink.isForChallenge = it.replace("inc_chall=", "")
                        }
                        params.firstOrNull { it.contains("isJoin=") }?.let {
                            dynamicLink.isJoin = it.replace("isJoin=", "")
                        }
                        params.firstOrNull { it.contains("originalChallengeId=") }?.let {
                            dynamicLink.originalChallengeId = it.replace("originalChallengeId=", "")
                        }
                    }
                } else {
                    if (_last.contains("user=")) {
                        dynamicLink.referId = _last.replace("user=", "")
                    }
                }
              //  redirectToPage()
            }
        }

        fun getLink(): RELDynamicLink {
            return dynamicLink
        }

        fun getReferrer(isGuest: Boolean): String {
            return if (isGuest && getLink().referId == "iOSappstore") {
                "peak"
            } else {
                getLink().referId
            }
        }

        fun setRedirect(value: Boolean) {
            isRedirectPending = value
        }

        fun updatePage(page: String) {
            dynamicLink.deepLinkPage = page
        }

        fun getDeepLinkPage(): String {
            return dynamicLink.deepLinkPage.lowercase()
        }

        fun updateChallengeId(id: String) {
            dynamicLink.challengeId = id
        }

        fun updateClassId(page: String) {
            dynamicLink.classId = page
        }


 /* fun redirectToPage() {
    val topVc = supportFragmentManager.findFragmentById(R.id.topViewController)
    if (topVc == null || topVc is REVLaunchScreenVC) {
        isRedirectPending = true
        return
    }

    val notification = RELDynamicLinkManager().shared().getNotification()
    if (notification != null) {

        RELDynamicLinkManager.shared().removeNotification()
    }

    val deepLinkPage = getDeepLinkPage()
    when (deepLinkPage) {
        "m", "b", "o" -> {
            dynamicLink.deepLinkPage = ""
            FirebaseDatabase.getInstance().reference
                .child(RevoolaKeys.ProposedStructure)
                .child(RevoolaKeys.RevoolaUserSettings)
                .child(RELAccountManager.shared().getUser().globalUid)
                .child(RevoolaKeys.BasicData)
                .child("link")
                .setValue("")

            val controller = when (deepLinkPage) {
                "m" -> RELBodyClassesNewListVC().apply { isForBody = false }
                "b" -> RELBodyClassesNewListVC().apply { isForBody = true }
                "o" -> RELYourWayCategoryListVC()
                else -> return
            }

            runOnUiThread {
                navigateManager(topVc, controller)
            }
        }
        "leaderboard" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                navigateManager(topVc, REVLeaderboardListVC())
            }
        }
        "feed" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                topVc.tabBarController?.selectedIndex = 1
            }
        }
        "fri" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                topVc.tabBarController?.selectedIndex = 3
            }
        }
        "start" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                topVc.tabBarController?.selectedIndex = 2
            }
        }
        "invite" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                navigateManager(topVc, REVSendInviteVC())
            }
        }
        "convert-guest", "convert-guest2" -> {
            val isAuthTrial = deepLinkPage == "convert-guest2"
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                navigateManager(topVc, RELConnectGuestVC().apply {
                    isFromNotification = true
                    isAutoTrial = isAuthTrial
                })
            }
        }
        "reset" -> {
            dynamicLink.deepLinkPage = ""
            runOnUiThread {
                navigateManager(topVc, REVPasswordChangeVC())
            }
        }
        "metric" -> {
            if crossinline dynamicLink.metric.isNotEmpty()) {
                val metric = RELMetricTitle.valueOf(dynamicLink.metric.capitalize())
                dynamicLink.deepLinkPage = ""
                dynamicLink.metric = ""
                runOnUiThread {
                    navigateManager(topVc, RELMatricDetailVC().apply {
                        selected = metric ?: RELMetricTitle.Steps
                    })
                }
            }
        }
        "share-card" -> {
            if (dynamicLink.id != 0) {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, RELSingleFeedVC().apply {
                        id = dynamicLink.id
                    })
                }
            }
        }
        "card-detail" -> {
            if (dynamicLink.id != 0) {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    topVc.showLoader(message = "")
                }
                RELWebRequest.shared().getOverviewDataV3(dynamicLink.id) { result, error ->
                    runOnUiThread {
                        topVc.hideLoader()
                        result?.let {
                            if (it.fromThirdPartySource == 0) {
                                val controller = when (it.resultFor) {
                                    ResultFor.Body, ResultFor.Open -> RELResultContainerVC().apply {
                                        isResults = false
                                        resultListData = it
                                    }
                                    ResultFor.Mind -> RELNEWMindResultPageVC().apply {
                                        isResults = false
                                        resultListData = it
                                    }
                                    else -> return@let
                                }
                                navigateManager(topVc, controller)
                            }
                        }
                    }
                }
            }
        }
        else -> {
            if (dynamicLink.classId.isNotEmpty()) {
                val classId = dynamicLink.classId
                dynamicLink.classId = ""
                val controller = if (dynamicLink.classType == "0") {
                    RELBodyClassDetailsVC().apply {
                        this.classId = classId
                    }
                } else {
                    RELMindClassDetailsVC().apply {
                        this.classId = classId
                    }
                }
                runOnUiThread {
                    navigateManager(topVc, controller, isHideNav = true)
                }
            } else if (dynamicLink.challengeId.isNotEmpty()) {
                val challengeId = dynamicLink.challengeId
                dynamicLink.challengeId = ""
                runOnUiThread {
                    navigateManager(topVc, RELGoaledChallengeResultVC().apply {
                        goaledChallengeId = challengeId
                    })
                }
            } else if (deepLinkPage == "kudos") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, RELKudosVC().apply {
                        isFetchResults = true
                        selectedResult = REVResultList().apply {
                            overviewId = dynamicLink.id
                        }
                    })
                }
            } else if (deepLinkPage == "comments") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, REVResultCommentsVC().apply {
                        isFetchResults = true
                        selectedResult = REVResultList().apply {
                            overviewId = dynamicLink.id
                        }
                        selectedDisplay = DisplayType.Comments
                        currentUserId = RELAccountManager.shared().getUser().globalUid
                    }, isHideNav = true)
                }
            } else if (deepLinkPage == "challenge") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, REVMyChallengesVC().apply {
                        isSchedule = false
                    })
                }
            } else if (deepLinkPage == "schedule") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, REVMyChallengesVC().apply {
                        isSchedule = true
                    })
                }
            } else if (deepLinkPage == "fri-request") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, REVFriendsRequestListVC())
                }
            } else if (deepLinkPage == "fri-detail") {
                when (RELDynamicLinkManager.shared().getLink().pageType) {
                    "find" -> {
                        runOnUiThread {
                            navigateManager(topVc, REVSearchFriendsVC())
                        }
                    }
                    "friends" -> {
                        runOnUiThread {
                            navigateManager(topVc, REVYourFriendsVC())
                        }
                    }
                    "groups" -> {
                        runOnUiThread {
                            navigateManager(topVc, REVYourGroupsVC())
                        }
                    }
                }
            } else if (deepLinkPage == "notification-list") {
                dynamicLink.deepLinkPage = ""
                runOnUiThread {
                    navigateManager(topVc, RELNotificationVC())
                }
            }
        }
    }
}

 private fun navigateManager(topVc: AppCompatActivity, controller: AppiskipActivitycommunication, isHideNav: Boolean = false) {
    val nav = topVc.supportFragmentManager.findFragmentById(R.id.navigationController) as? NavController
    if (nav != null) {
        controller.hidesBottomBarWhenPushed = true
        nav.navigate(controller, bundleOf())
    } else {
        topVc.supportFragmentManager.beginTransaction()
            .replace(R.id.container, controller)
            .addToBackStack(null)
            .commit()
    }
}
*/
fun pendingRedirect(): Boolean {
    return isRedirectPending == true
}

fun removeRedirect() {
    isRedirectPending = null
}

class RELDynamicLink private constructor() {
    var referId = "iOSappstore"
    var deepLinkPage = ""
    var company = ""
    var fromLink = ""
    var plan = ""
    var commisionFlag = ""
    var inviteUserType = "NormalUser"
    var inviteUserSubsModel = "0"    //0 = Apple, 1 = NonApple
    var referrerTag = "iOS"
    var lastDate = ""
    var classId = ""
    var challengeId = ""
    var classType = "0"
    var pageType = ""
    var link = ""
    var originalLink = ""
    var trialDays = "14"
    var id = 0
    var metric = ""
    var isFullPremium = ""
    var branchLink = ""
    var isOnlySubscription = ""
    var isUserInvite = ""
    var isForExistingUser = ""
    var isForChallenge = ""
    var isJoin = ""
    var originalChallengeId = ""

    companion object {
        private val sharedInstance = RELDynamicLink()

        fun shared(): RELDynamicLink {
            return sharedInstance
        }
    }
}


}