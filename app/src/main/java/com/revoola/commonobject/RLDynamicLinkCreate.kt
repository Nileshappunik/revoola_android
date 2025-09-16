package com.revoola.commonobject

import android.content.Context
import android.content.Intent
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.model.RLFulllVideoModel
import com.revoola.model.RLRevoolaUsersSettingsModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.BranchContentSchema
import io.branch.referral.util.LinkProperties

class RLDynamicLinkCreate private constructor() {

    companion object {
        private var instance: RLDynamicLinkCreate? = null

        fun shared(): RLDynamicLinkCreate {
            if (instance == null) {
                instance = RLDynamicLinkCreate()
            }
            return instance!!
        }
    }

    fun createVideoLink(
        context: Context,
        video: RLFulllVideoModel,
        isMind: Boolean,
        videoKey: String,
        userData: RLRevoolaUsersSettingsModel?,
        completion: (String?) -> Unit
    ) {
        val fullName = userData?.firstName +" "+ userData?.lastName
        val classType = if (isMind) 1 else 0
        val buo = BranchUniversalObject()
        val lp = LinkProperties()
            .addControlParameter("\$canonical_url", "https://www.revoola.com/")
            .addControlParameter("\$desktop_url", "https://www.revoola.com/")

        buo.setTitle("${video.rideTitle} by ${video.instructor}")
        buo.setContentDescription(video.rideDescription)
        buo.setContentImageUrl( video.imageLinkSquareV2)
        buo.contentMetadata.setContentSchema(BranchContentSchema.OTHER)

        val path =
            "https://www.revoola.com/classes-videos/?classType=$classType&classId=$videoKey&user=${RLAuthManager().rl_getCurrentUser()?.uid}&isUserInvite=true&displayName=${fullName}"

        lp.addControlParameter(
            "\$deeplink_path",
            "https://www.revoola.com/?classType=$classType&classId=$videoKey&user=${RLAuthManager().rl_getCurrentUser()?.uid}&isUserInvite=true"
        )
        lp.addControlParameter("\$desktop_url", path)
        lp.addControlParameter("\$ios_url", path)
        lp.addControlParameter("\$android_url", path)

        // Generate short URL
        buo.generateShortUrl(context, lp) { url, error ->
            if (error == null && url != null) {
                completion(url)
            } else {
                completion(null)
            }
        }
    }

    fun createChallengeLink(
        context: Context,
        challengeId: String? = null,
        trialDays: String? = null,
        userId: String? = null,
        pastChallengeId: String? = null,
        link: String? = null,
        userData: RLRevoolaUsersSettingsModel,
        completion: (String?) -> Unit
    ) {

        val fullName = userData.firstName +" "+ userData.lastName
        val buo = BranchUniversalObject()
        val lp = LinkProperties()
            .addControlParameter("\$canonical_url", "https://www.revoola.com/")
            .addControlParameter("\$desktop_url", "https://www.revoola.com/")

        if (challengeId != null && trialDays != null && userId != null) {
            buo.setTitle("Revoola Challenge")
            buo.contentMetadata.setContentSchema(BranchContentSchema.OTHER)
            var pathUrl =
                "https://www.revoola.com/?user=$userId&trialDays=$trialDays&isForExistingUser=true&inc_chall=$challengeId&isJoin=true"

            pastChallengeId?.let {
                pathUrl += "&originalChallengeId=$it"
            }

            lp.addControlParameter("\$deeplink_path", pathUrl)
        }

        if (link != null) {
            buo.setTitle("Revoola Challenge")
            buo.contentMetadata.setContentSchema(BranchContentSchema.OTHER)
            var pathUrl = link
            pastChallengeId?.let {
                pathUrl += "&originalChallengeId=$it&isJoin=true"
            }
            lp.addControlParameter("\$deeplink_path", pathUrl)
        } else {
            buo.setTitle(fullName)
            buo.setContentDescription("Invited to Revoola")
            buo.setContentImageUrl(userData.displayImage)
            buo.contentMetadata.setContentSchema(BranchContentSchema.OTHER)
            lp.addControlParameter(
                "\$deeplink_path",
                "https://www.revoola.com/?user=${RLAuthManager().rl_getCurrentUser()?.uid}"
            )
        }

        // Generate short URL
        buo.generateShortUrl(context, lp) { url, error ->
            if (error == null && url != null) {
                completion(url)
            } else {
                completion(null)
            }
        }
    }
}

object RELShareManager {
    fun shareAsText(context: Context, text: String) {
        val message = "You should try Revoola! It has over 200 professional instruction videos " +
                "and lets you monitor runs, rides and other activities. And what's even better: " +
                "you can create and participate in plenty of challenges as individual or in groups. " +
                "Click here, it's free to try: $text"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}
