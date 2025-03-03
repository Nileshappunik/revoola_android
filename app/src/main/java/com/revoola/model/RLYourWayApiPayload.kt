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

data class RLOverviewApiPayload(
    @SerializedName("userid") val userid: String,
    @SerializedName("className") val className: String,
    @SerializedName("classType") val classType: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("timestamp_local") val timestamp_local: String,
    @SerializedName("imageLinkSmall") val imageLinkSmall: String,
    @SerializedName("totalRev") val totalRev: Double,
    @SerializedName("totalTime") val totalTime: String,
    @SerializedName("burntCalories") val burntCalories: Double?,
    @SerializedName("totalRMM") val totalRMM: Int,
    @SerializedName("totalRMS") val totalRMS: Int,
    @SerializedName("maxRevPercentage") val maxRevPercentage: Double,
    @SerializedName("avgRevPercentage") val avgRevPercentage: Double,
    @SerializedName("zone1Seconds") val zone1Seconds: Int,
    @SerializedName("zone2Seconds") val zone2Seconds: Int,
    @SerializedName("zone3Seconds") val zone3Seconds: Int,
    @SerializedName("zone4Seconds") val zone4Seconds: Int,
    @SerializedName("zone5Seconds") val zone5Seconds: Int,
    @SerializedName("zone6Seconds") val zone6Seconds: Int,
    @SerializedName("zone7Seconds") val zone7Seconds: Int,
    @SerializedName("medals") val medals: String,
    @SerializedName("awards") val awards: String,
    @SerializedName("visibilityflagforthatsession") val visibilityflagforthatsession: Int,
    @SerializedName("share_map") val share_map: Int,
    @SerializedName("from_third_party_source") val from_third_party_source: Int,
    @SerializedName("bmo") val bmo: Int,
    @SerializedName("instructor") val instructor: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("rideTitle") val rideTitle: String,
    @SerializedName("mainTitle") val mainTitle: String,
    @SerializedName("originalClassDate") val originalClassDate: String,
    @SerializedName("videoKey") val videoKey: String,
    @SerializedName("goal") val goal: String,
    @SerializedName("medals_gold") val medals_gold: Int,
    @SerializedName("medals_silver") val medals_silver: Int,
    @SerializedName("medals_bronze") val medals_bronze: Int,
    @SerializedName("elevation") val elevation: Double,
    @SerializedName("power") val power: Int,
    @SerializedName("hr") val hr: Int,
    @SerializedName("steps") val steps: Int,
    @SerializedName("distance") val distance: Double,
    @SerializedName("userImage") val userImage: List<String>,
    @SerializedName("hrm") val hrm: Int,
    @SerializedName("class_level") val class_level: String,
    @SerializedName("average_speed") val average_speed: Double,
    @SerializedName("source") val source: String,
    @SerializedName("mapImage") val mapImage: String,
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

data class RLInsightlyMoEngageResponse(
    val response: List<RLInsightlyResponseItem>
)

data class RLInsightlyResponseItem(
    val success: String,
    val status: String
)


data class RLYourWayApiResponse(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String
)
