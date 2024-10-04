package com.example.myfirstapp.api

import com.example.myfirstapp.model.RLFeedChallengesMapModel
import com.example.myfirstapp.model.RLFeedChallengesModel
import com.example.myfirstapp.model.RLFeedModel
import com.example.myfirstapp.model.RLGetGroupMemberModel
import com.example.myfirstapp.model.RLGetUserAggregatedDataRequest
import com.example.myfirstapp.model.RLGroupModel
import com.example.myfirstapp.model.RLNotificationModel
import com.example.myfirstapp.model.RLOverViewModel
import com.example.myfirstapp.model.RLOverviewGraphDataRequest
import com.example.myfirstapp.model.RLOverviewGraphResponse
import com.example.myfirstapp.model.RLSetGroupMemberRequest
import com.example.myfirstapp.model.RLSetGroupRequest
import com.example.myfirstapp.model.RLSetMetricChartByDay
import com.example.myfirstapp.model.RLSetget_followersrequest
import com.example.myfirstapp.model.RLSetgoaled_challenges_request
import com.example.myfirstapp.model.RLSetgoaled_challenges_request_single
import com.example.myfirstapp.model.RLSetoverview_thumbRequest
import com.example.myfirstapp.model.RLSetoverview_thumbRequest_you
import com.example.myfirstapp.model.RLSetsearch_userrequest
import com.example.myfirstapp.model.RLYourFriendsModel
import com.example.myfirstapp.model.RLYourGroupModel
import com.example.myfirstapp.model.RLrequestgroup_dataset
import com.example.myfirstapp.utils.RLConstants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface RLNetworkService {
    @POST(RLConstants.URLALL)
    fun RLgetUserAggregatedData(@Body request: List<RLGetUserAggregatedDataRequest>): Call<RLOverViewModel>

    @POST(RLConstants.URLALL)
    fun RLgetOverviewGraph(@Body request: List<RLOverviewGraphDataRequest>): Call<RLOverviewGraphResponse>

    @POST(RLConstants.URLALL)
    fun RLgetUserFeedCardData(@Body request: List<RLSetoverview_thumbRequest>): Call<RLFeedModel>

    @POST(RLConstants.URLALL)
    fun RLgetUserFeedCardDatayou(@Body request: List<RLSetoverview_thumbRequest_you>): Call<RLFeedModel>

    @POST(RLConstants.URLALL)
    fun RLgetGroupData(@Body request: List<RLSetGroupRequest>): Call<RLGroupModel>

    @POST(RLConstants.URLALL)
    fun RLgoaled_challenges(@Body request: List<RLSetgoaled_challenges_request>): Call<RLFeedChallengesModel>

    @POST(RLConstants.URLALL)
    fun RLGroupMembers(@Body request: List<RLSetGroupMemberRequest>): Call<RLGetGroupMemberModel>

    @POST(RLConstants.URLALL)
    fun RLgoaled_challenges_Single(@Body request: List<RLSetgoaled_challenges_request_single>): Call<RLFeedChallengesModel>

     @POST(RLConstants.URLALL)
    fun RLMetricChartByDay(@Body request: List<RLSetMetricChartByDay>): Call<RLFeedChallengesMapModel>

    @POST(RLConstants.URLALL)
    fun RLfriendsYouFollow(@Body request: List<RLSetsearch_userrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URLALL)
    fun RLfriendsFollowingYou(@Body request: List<RLSetget_followersrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URLALL)
    fun RLyourGroupData(@Body request: List<RLrequestgroup_dataset>): Call<RLYourGroupModel>

    @GET(RLConstants.URLALLV2)
    fun RLgetNotificationData(
        @Query("q") q: String,
        @Query("user") user: String,
        @Query("limit") limit: Int,
        @Query("index") index: Int): Call<RLNotificationModel>

}