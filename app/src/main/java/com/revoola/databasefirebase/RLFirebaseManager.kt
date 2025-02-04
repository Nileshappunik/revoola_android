package com.revoola.databasefirebase

import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.revoola.activity.RLMainActivityRL
import com.revoola.utils.RLPrefManager

class RLFirebaseManager {
    val databaseManager = RLDatabaseManagerWrite()

     fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String,versionName:String,callback: (Boolean) -> Unit) {
        val currentTimestamp  = (System.currentTimeMillis() / 1000).toString()
        val currentSubscriptionMap = hashMapOf(
            "validDaysMonth" to 0,
            "inviteUserSubsModel" to "0",
            "referrerTag" to "Android",
            "permissionLevelAfterTrial" to "Free",
            "isTrialTaken" to true,
            "isSubscriptionRequired" to true,
            "commisionFlag" to "",
            "discountPeriodMonth" to 0,
            "remark" to "Android",
            "familyPrice" to 0,
            "onGoingPriceType" to "none",
            "onGoingPrice" to 0,
            "discountedPriceType" to "none",
            "discountedPrice" to 0,
            "isSubscriptionCheckRequired" to true,
            "subscriptionName" to "Trial-Premium",
            "inviteUserType" to "NormalUser",
            "plan" to "",
            "timestamp" to currentTimestamp,
            "validDays" to 14)

        val revoolaUserSettingsMap = hashMapOf(
            "FCMToken" to "",
            "RFMHR" to 196,
            "TMHR" to 196,
            "AMHR" to 196,
            "emailId" to emailId,
            "appUnit" to "Imperial",
            "currentGroup" to "freemium",
            "displayImage" to "none",
            "displayName" to "Guest",
            "dob" to "00/00/0000",
            "firstName" to "Guest",
            "flagImage" to "flag-of-United-Kingdom.png",
            "flagName" to "United Kingdom",
            "heartRate" to 0,
            "height" to "167",
            "heightUnit" to "FeetInch",
            "isBasicDataAdded" to false,
            "joiningDate" to currentTimestamp,//first time user create then date
            "lastHRChange" to 0,
            " lastHRChange90" to 0,
            " lastHRUsed" to 0,
            "lastLogin" to currentTimestamp,
            "lastName" to "",
            "gender" to "none",
            "lastVersion" to versionName,//current app version
            "leaderBoardImage" to "none",
            "currentSubscription" to currentSubscriptionMap,
            "location" to "United Kingdom",
            "numberOfGhost" to "1",
            "power" to 0,
            "referUser" to "AndroidPlayStore",
            "referalCode" to "",
            "remark" to "Android",
            "restingHr" to "60",
            "totalRev" to 0,
            "visibilityflagforthatsession" to 0,
            "weightUnit" to "Metric",
            "weightkg" to "77")
        databaseManager.REVOOLAUSERSETTINGSWrite(userId,revoolaUserSettingsMap) { success, error ->
            callback(success)

        }
    }


}