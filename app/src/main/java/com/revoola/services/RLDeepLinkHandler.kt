package com.revoola.services

import android.content.Context
import android.os.Bundle
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.feed.RLFragFeed
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragFriends
import com.revoola.fragment.friends.RLFragInviteFriends
import com.revoola.fragment.friends.RLFragYourFriends
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.more.RLFragChangePassword
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.RLFragStart
import com.revoola.fragment.start.body.RLFragBodyClasses
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.fragment.start.yourway.RLFragYourWay

class RLDeepLinkHandler {
    val TAG: String = RLDeepLinkHandler::class.java.simpleName
    var userCode: String = "androidPlayStore"
    var page: String = ""
    var referrerTag: String = ""
    var commissionFlag: String = ""
    var plan: String = ""
    var inviteUserType: String = ""
    var inviteUserSubsModel: String = ""
    var company: String = ""
    var fromLink: String = ""
    var lastDate: Long = 0L
    var trialDays: String = "14"
    var referrerLink: String? = null
    var id: Int = 0
    var classType: Int = -1
    var classId: Int = -1
    var dayCount: Int? = null
    var isValidLink: Boolean = false
    var isFullPremium: Boolean = false
    var branchLink: String? = null
    var metric: String = "Effort"
    var pageType: String = ""
    var isOnlySubscription: String = ""
    var isForExistingUser: String = ""
    var isForChallenge: String = ""
    var isJoin: String = ""
    private var isFresh: Boolean = true

    fun onReceiveBranchIoLink(linkData: String,context:Context) {
        val deepLink = linkData.substringAfter("?")
        println("=======deepLink==== $deepLink")

        for (linkObject in deepLink.split("&")) {
            println("=======deepLink====2 $linkObject")

            isValidLink = true
            when {
                linkObject.contains("user=") -> {
                    userCode = linkObject.replace("user=", "").replace(Regex("[^\\w\\s]"), "")
                }

                linkObject.contains("company=") -> {
                    company = linkObject.replace("company=", "")
                }

                linkObject.contains("fromLink=") -> {
                    fromLink = linkObject.replace("fromLink=", "")
                }

                linkObject.contains("pl=") -> {
                    plan = linkObject.replace("pl=", "")
                }

                linkObject.contains("utp=") -> {
                    inviteUserType = linkObject.replace("utp=", "")
                }

                linkObject.contains("usm=") -> {
                    inviteUserSubsModel = linkObject.replace("usm=", "")
                }

                linkObject.contains("com=") -> {
                    commissionFlag = linkObject.replace("com=", "")
                }

                linkObject.contains("re=") -> {
                    referrerTag = linkObject.replace("re=", "")
                }

                linkObject.contains("last=") -> {
                    lastDate = linkObject.replace("last=", "").toLongOrNull() ?: 0L
                    val currentDate = System.currentTimeMillis()
                    val targetDate = lastDate * 1000
                    val timeDifference = targetDate - currentDate
                    dayCount = (timeDifference / (1000 * 60 * 60 * 24)).toInt()
                }

                linkObject.contains("trialDays=") -> {
                    trialDays = linkObject.replace("trialDays=", "")
                }

                linkObject.contains("page=") -> {
                    page = linkObject.replace("page=", "")
                }

                linkObject.contains("id=") -> {
                    id = linkObject.replace("id=", "").toIntOrNull() ?: 0
                }

                linkObject.contains("classType=") -> {
                    classType = linkObject.replace("classType=", "").toIntOrNull() ?: -1
                }

                linkObject.contains("classId=") -> {
                    classId = linkObject.replace("classId=", "").toIntOrNull() ?: -1
                }

                linkObject.contains("isFullPremium=") -> {
                    isFullPremium = linkObject.replace("isFullPremium=", "").toBoolean()
                }

                linkObject.contains("branchLink=") -> {
                    branchLink = linkObject.replace("branchLink=", "")
                }

                linkObject.contains("metric=") -> {
                    metric = linkObject.replace("metric=", "")
                }

                linkObject.contains("challengeId=") -> {
                    val challengeId = linkObject.replace("challengeId=", "")
                    val myChallengeData = mapOf(
                        "challengeid" to challengeId,
                        "isFromPush" to true
                    )
                    println("=========myChallengeData====== $myChallengeData")
                    navigateToChallengesOverview(myChallengeData)
                }

                linkObject.contains("pageType=") -> {
                    pageType = linkObject.replace("pageType=", "")
                }

                linkObject.contains("isOnlySubscription=") -> {
                    isOnlySubscription = linkObject.replace("isOnlySubscription=", "")
                }

                linkObject.contains("isForExistingUser=") -> {
                    isForExistingUser = linkObject.replace("isForExistingUser=", "")
                }

                linkObject.contains("inc_chall=") -> {
                    isForChallenge = linkObject.replace("inc_chall=", "")
                }

                linkObject.contains("isJoin=") -> {
                    isJoin = linkObject.replace("isJoin=", "")
                }
            }
        }
        navigateToScreen(context)
    }

    private fun getPlanRes() {
        // Add your logic for getting the plan response
    }

    private fun updateReferUser(data: String) {
        // Add your logic for updating the referred user
    }

    private fun navigateToChallengesOverview(myChallengeData: Map<String, Any>) {
        // Add your navigation logic to the challenges overview
    }
    private fun navigateToScreen(context:Context) {
        when (page) {
            "share-card", "card-detail" -> {
                val isShareCard = page == "share-card"
                val bundle = Bundle().apply {
                    putInt("id", id)
                    putBoolean("isShareCard", isShareCard)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true,null, false)

            }
            "feed" -> (context as RLMainActivityRL).RLloadFrag(RLFragFeed(), TAG, true, null, false)
            "start" -> (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, true, null, false)
            "fri" -> (context as RLMainActivityRL).RLloadFrag(RLFragFriends(), TAG, true, null, false)
            "invite" -> (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, null, false)
            "m" -> {
                val bundle = Bundle().apply {
                    putString("classesType", "Mind")
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses(), TAG, true, null, false)

            }
            "b" -> {
                val bundle = Bundle().apply {
                    putString("classesType", "Body")
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragBodyClasses(), TAG, true, null, false)
            }
            "o" -> (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
            "leaderboard" -> (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, true, null, false)
            "convert-guest", "convert-guest2" -> {
                val isShowTrial = page == "convert-guest"
                val bundle = Bundle().apply {
                    putString("emailId", "guestuser@example.com")
                    putBoolean("isLinkAccount", true)
                    putBoolean("isNotificationLinkAccount", true)
                    putBoolean("isShowLoginOption", true)
                    putBoolean("isShowTrial", isShowTrial)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
            }
            "kudos" -> {
                val bundle = Bundle().apply {
                    putInt("overviewID", id)
                    putBoolean("isFromNotification", true)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
            }
            "comments" -> {
                val bundle = Bundle().apply {
                    putInt("overviewID", id)
                    putBoolean("isFromNotification", true)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, true, null, false)
            }
            "reset" -> (context as RLMainActivityRL).RLloadFrag(RLFragChangePassword(), TAG, true, null, false)
            "metric" -> {
                val bundle = Bundle().apply {
                    putString("activityName", metric)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)
            }
            "fri-detail" -> {
                when (pageType) {
                    "find" -> (context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, null, false)
                    "friends" -> (context as RLMainActivityRL).RLloadFrag(RLFragFriends(), TAG, true, null, false)
                    "groups" -> {
                        val bundle = Bundle().apply {
                            putString("grpList", "")
                        }
                        (context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, null, false)
                    }
                }
            }
            "fri-request" -> {
                val bundle = Bundle().apply {
                    putSerializable("requestList", arrayListOf<String>())
                    putBoolean("fetchRequest", true)
                }
                (context as RLMainActivityRL).RLloadFrag(RLFragYourFriends(), TAG, true, null, false)
            }
            else -> {
               /* if (classType == 0) {
                    lifecycleScope.launch {
                        try {
                            val response = getVideoDetails(classId)
                            val bundle = Bundle().apply {
                                putSerializable("data", mapOf(
                                    "contentDetails" to response,
                                    "classesType" to "Body",
                                    "isDataFromDeepLink" to true
                                ))
                            }
                            navController.navigate(R.id.classContentDetailsFragment, bundle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                else if (classType == 1) {
                    lifecycleScope.launch {
                        try {
                            val response = getVideoDetails(classId)
                            val bundle = Bundle().apply {
                                putSerializable("data", mapOf(
                                    "contentDetails" to response,
                                    "classesType" to "Mind",
                                    "isDataFromDeepLink" to true
                                ))
                            }
                            navController.navigate(R.id.classContentDetailsFragment, bundle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }*/
            }
        }
    }

    private suspend fun getVideoDetails(classId: Int): Any {
        // Mocked video service response for illustration. Replace with actual API call logic.
        return mapOf("contentDetails" to "Details for classId: $classId")
    }
}
