package com.revoola.model

import com.google.gson.annotations.SerializedName

data class RLYourWayApiPayload(
    @SerializedName("classleaderboard") val classLeaderboard: RLClassLeaderboard,
    //@SerializedName("username_v2") val usernameV2: RLUsernameV2
)


data class RLClassLeaderboard(
    @SerializedName("userid") val userId: String,
    @SerializedName("classid") val classId: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("timestamp_local") val timestampLocal: String,
    @SerializedName("totalrev") val totalRev: Double,
    @SerializedName("visibilityflagforthatsession") val visibilityFlagForThatSession: Int,
    @SerializedName("discipline") val discipline: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("calories") val calories: Double?,
    @SerializedName("bmo") val bmo: Int,
    @SerializedName("rmm") val rmm: Int,
    @SerializedName("rms") val rms: Int,
    @SerializedName("source") val source: String,
    @SerializedName("goal") val goal: String
)

data class RLUsernameV2(
    @SerializedName("userid") val userId: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("username") val username: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("current_group") val currentGroup: String
)


data class RLInsightlyApiPayload(
    @SerializedName("email") val email: String,
    @SerializedName("device_type") val device_type: String,
    @SerializedName("your_way") val your_way: String,
    @SerializedName("insightlyId") val insightlyId: Int
)

// Third Insert Data Model
data class RLInsightlyMoengageApiPayload(
    @SerializedName("email") val email: String,
    @SerializedName("uid") val uid: String,
    @SerializedName("device_type") val device_type: String,
    @SerializedName("Is_basic_data_added") val Is_basic_data_added: Boolean,
    @SerializedName("your_way") val your_way: String
)

data class RLInsightlyMoengageBodyApiPayload(
    @SerializedName("email") val email: String,
    @SerializedName("uid") val uid: String,
    @SerializedName("device_type") val device_type: String,
    @SerializedName("Is_basic_data_added") val Is_basic_data_added: Boolean,
    @SerializedName("mind_activity") val mind_activity: String
)

data class RLInsightlyMoEngageResponse(
    val response: List<RLInsightlyResponseItem>
)

data class RLInsightlyResponseItem(
    val success: String,
    val status: String
)


data class RLInsertCommonApiResponse(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)
