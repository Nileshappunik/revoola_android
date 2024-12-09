package com.revoola.api

import com.revoola.model.RLFeedChallengesMapModel
import com.revoola.model.RLFeedChallengesModel
import com.revoola.model.RLFeedModel
import com.revoola.model.RLGetGroupMemberModel
import com.revoola.model.RLGetUserAggregatedDataRequest
import com.revoola.model.RLGroupModel
import com.revoola.model.RLNotificationModel
import com.revoola.model.RLOverViewModel
import com.revoola.model.RLOverviewGraphDataRequest
import com.revoola.model.RLOverviewGraphResponse
import com.revoola.model.RLSetGroupMemberRequest
import com.revoola.model.RLSetGroupRequest
import com.revoola.model.RLSetMetricChartByDay
import com.revoola.model.RLSetget_followersrequest
import com.revoola.model.RLSetgoaled_challenges_request
import com.revoola.model.RLSetgoaled_challenges_request_single
import com.revoola.model.RLSetoverview_thumbRequest
import com.revoola.model.RLSetoverview_thumbRequest_you
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLYourFriendsModel
import com.revoola.model.RLYourGroupModel
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.utils.RLConstants
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