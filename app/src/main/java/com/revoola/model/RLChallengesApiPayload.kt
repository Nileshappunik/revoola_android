package com.revoola.model

import com.google.gson.annotations.SerializedName

data class RLChallengesApiPayload(
    @SerializedName("goaled_challenges_new") val goaledChallengesNew: GoaledChallengesNew
)

data class GoaledChallengesNew(
    @SerializedName("challengeadmin") val challengeAdmin: String,
    @SerializedName("groupid_userid") val groupIdUserId: List<String>,
    @SerializedName("groupongroup") val groupOnGroup: String,
    @SerializedName("group") val group: String,
    @SerializedName("metric") val metric: String,
    @SerializedName("creationdate") val creationDate: String,
    @SerializedName("startdate") val startDate: String,
    @SerializedName("enddate") val endDate: String,
    @SerializedName("goalvalue") val goalValue: String,
    @SerializedName("max") val max: String,
    @SerializedName("challenge_name") val challengeName: String,
    @SerializedName("challenger") val challenger: String,
    @SerializedName("challenger_avatar") val challengerAvatar: String,
    @SerializedName("scenario") val scenario: Int,
    @SerializedName("dwmstart") val dwmStart: String,
    @SerializedName("dwmend") val dwmEnd: String,
    @SerializedName("targettype") val targetType: String
)
