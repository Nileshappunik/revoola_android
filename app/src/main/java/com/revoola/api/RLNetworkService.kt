package com.revoola.api


import com.revoola.model.RLChallengePayload
import com.revoola.model.RLChallengesApiPayload
import com.revoola.model.RLFeedChallengesMapModel
import com.revoola.model.RLFeedChallengesModel
import com.revoola.model.RLFeedModel
import com.revoola.model.RLGetGroupMemberModel
import com.revoola.model.RLGetUserAggregatedDataRequest
import com.revoola.model.RLGroupModel
import com.revoola.model.RLInsightlyApiPayload
import com.revoola.model.RLInsightlyMoEngageResponse
import com.revoola.model.RLInsightlyMoengageApiPayload
import com.revoola.model.RLNotificationModel
import com.revoola.model.RLOverViewModel
import com.revoola.model.RLOverviewGraphDataRequest
import com.revoola.model.RLOverviewGraphResponse
import com.revoola.model.RLRequestDetail_dataset
import com.revoola.model.RLSetGroupMemberRequest
import com.revoola.model.RLSetGroupRequest
import com.revoola.model.RLSetMetricChartByDay
import com.revoola.model.RLSetget_followersrequest
import com.revoola.model.RLSetgoaled_challenges_request
import com.revoola.model.RLSetgoaled_challenges_request_single
import com.revoola.model.RLSetoverview_thumbRequest
import com.revoola.model.RLSetoverview_thumbRequest_you
import com.revoola.model.RLSetsearch_userrequest
import com.revoola.model.RLTextOverview
import com.revoola.model.RLYourFriendsModel
import com.revoola.model.RLYourGroupModel
import com.revoola.model.RLYourWayApiPayload
import com.revoola.model.RLYourWayApiResponse
import com.revoola.model.RLrequest_goaled_challenges
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsearch_userrequest
import com.revoola.model.RLtrigger_inapp_referrer_goaled_challenges_Request
import com.revoola.utils.RLConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RLNetworkService {
    @POST(RLConstants.URL_V3)
    fun RLgetUserAggregatedData(@Body request: List<RLGetUserAggregatedDataRequest>): Call<RLOverViewModel>

    @POST(RLConstants.URL_V3)
    fun RLgetOverviewGraph(@Body request: List<RLOverviewGraphDataRequest>): Call<RLOverviewGraphResponse>

    @POST(RLConstants.URL_V3)
    fun RLgetUserFeedCardData(@Body request: List<RLSetoverview_thumbRequest>): Call<RLFeedModel>

    @POST(RLConstants.URL_V3)
    fun RLgetUserFeedCardDatayou(@Body request: List<RLSetoverview_thumbRequest_you>): Call<RLFeedModel>

   @POST(RLConstants.URL_V3)
    fun RLJoinBigChallengeFeed(@Body request: List<RLtrigger_inapp_referrer_goaled_challenges_Request>): Call<String>

    @POST(RLConstants.URL_V3)
    fun RLgetGroupData(@Body request: List<RLSetGroupRequest>): Call<RLGroupModel>

    @POST(RLConstants.URL_V3)
    fun RLgoaled_challenges(@Body request: List<RLSetgoaled_challenges_request>): Call<RLFeedChallengesModel>

    @POST(RLConstants.URL_V3)
    fun RLgoaled_challenges_view(@Body request: List<RLrequest_goaled_challenges>): Call<RLFeedChallengesModel>

    @POST(RLConstants.URL_V3)
    fun RLGroupMembers(@Body request: List<RLSetGroupMemberRequest>): Call<RLGetGroupMemberModel>

    @POST(RLConstants.URL_V3)
    fun RLgoaled_challenges_Single(@Body request: List<RLSetgoaled_challenges_request_single>): Call<RLFeedChallengesModel>

     @POST(RLConstants.URL_V3)
    fun RLMetricChartByDay(@Body request: List<RLSetMetricChartByDay>): Call<RLFeedChallengesMapModel>

    @POST(RLConstants.URL_V3)
    fun RLfriendsYouFollow(@Body request: List<RLSetsearch_userrequest>): Call<RLYourFriendsModel>


    @POST(RLConstants.URL_V3)
    fun RLsearch_user_Data_DeepLink(@Body request: List<RLsearch_userrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URL_V3)
    fun RLfriendsFollowingYou(@Body request: List<RLSetget_followersrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URL_V3)
    fun RLyourGroupData(@Body request: List<RLrequestgroup_dataset>): Call<RLYourGroupModel>

    @POST(RLConstants.URL_V3)
    fun RLgetOverviewThumbFromIdData(@Body request: List<RLRequestDetail_dataset>): Call<RLFeedModel>

    @GET(RLConstants.URL_V2)
    fun RLgetNotificationData(
        @Query("q") q: String,
        @Query("user") user: String,
        @Query("limit") limit: Int,
        @Query("index") index: Int): Call<RLNotificationModel>

    @GET(RLConstants.URL_V2)
    fun RLgetCommentsData(
        @Query("q") q: String,
        @Query("overviewid") overviewid: String,
        @Query("limit") limit: Int,
        @Query("index") index: Int): Call<RLFeedModel>

    //Insert Api
    @POST(RLConstants.insertJSONApi)
    fun RLInsertYourWayData(@Body request: List<RLYourWayApiPayload>): Call<RLYourWayApiResponse>

    @Multipart
    @POST(RLConstants.mpfIfCWxBL_insert)
    fun RLInsertYourWayOverviewData( @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>): Call<RLYourWayApiResponse>

     @POST(RLConstants.insertJSONApi)
    fun RLInsertChallenges(@Body request: List<RLChallengesApiPayload>): Call<RLYourWayApiResponse>


}