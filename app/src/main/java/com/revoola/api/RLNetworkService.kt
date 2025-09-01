package com.revoola.api


import com.revoola.fragment.friends.model.EmailFilterApiResponse
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFriendsInsertApiPayload
import com.revoola.model.RLChallengesApiPayload
import com.revoola.model.RLCommentGetApiPayload
import com.revoola.model.RLCommentInsertApiPayload
import com.revoola.model.RLCommentsApiResponse
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
import com.revoola.model.RLYourFriendsModel
import com.revoola.model.RLYourGroupModel
import com.revoola.model.RLYourWayApiPayload
import com.revoola.model.RLInsertCommonApiResponse
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
    fun rl_getUserAggregatedData(@Body request: List<RLGetUserAggregatedDataRequest>): Call<RLOverViewModel>

    @POST(RLConstants.URL_V3)
    fun rl_getOverviewGraph(@Body request: List<RLOverviewGraphDataRequest>): Call<RLOverviewGraphResponse>

    @POST(RLConstants.URL_V3)
    fun rl_getUserFeedCardData(@Body request: List<RLSetoverview_thumbRequest>): Call<RLFeedModel>

    @POST(RLConstants.URL_V3)
    fun rl_getUserFeedCardDatayou(@Body request: List<RLSetoverview_thumbRequest_you>): Call<RLFeedModel>

   @POST(RLConstants.URL_V3)
    fun rl_JoinBigChallengeFeed(@Body request: List<RLtrigger_inapp_referrer_goaled_challenges_Request>): Call<String>

    @POST(RLConstants.URL_V3)
    fun rl_getGroupData(@Body request: List<RLSetGroupRequest>): Call<RLGroupModel>

    @POST(RLConstants.URL_V3)
    fun rl_goaled_challenges(@Body request: List<RLSetgoaled_challenges_request>): Call<RLFeedChallengesModel>

    @POST(RLConstants.URL_V3)
    fun rl_goaled_challenges_view(@Body request: List<RLrequest_goaled_challenges>): Call<RLFeedChallengesModel>

    @POST(RLConstants.URL_V3)
    fun rl_groupMembers(@Body request: List<RLSetGroupMemberRequest>): Call<RLGetGroupMemberModel>

    @POST(RLConstants.URL_V3)
    fun rl_goaled_challenges_Single(@Body request: List<RLSetgoaled_challenges_request_single>): Call<RLFeedChallengesModel>

     @POST(RLConstants.URL_V3)
    fun rl_metricChartByDay(@Body request: List<RLSetMetricChartByDay>): Call<RLFeedChallengesMapModel>

    @POST(RLConstants.URL_V3)
    fun rl_friendsYouFollow(@Body request: List<RLSetsearch_userrequest>): Call<RLYourFriendsModel>


    @POST(RLConstants.URL_V3)
    fun rl_search_user_Data_DeepLink(@Body request: List<RLsearch_userrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URL_V3)
    fun rl_findOnRevoolaEmailFilter(@Body request: List<RLEmailFilterRequestModel>): Call<EmailFilterApiResponse>

    @POST(RLConstants.URL_V3)
    fun rl_friendsFollowingYou(@Body request: List<RLSetget_followersrequest>): Call<RLYourFriendsModel>

    @POST(RLConstants.URL_V3)
    fun rl_yourGroupData(@Body request: List<RLrequestgroup_dataset>): Call<RLYourGroupModel>

    @POST(RLConstants.URL_V3)
    fun rl_getOverviewThumbFromIdData(@Body request: List<RLRequestDetail_dataset>): Call<RLFeedModel>

    @GET(RLConstants.URL_V2)
    fun rl_getNotificationData(
        @Query("q") q: String,
        @Query("user") user: String,
        @Query("limit") limit: Int,
        @Query("index") index: Int): Call<RLNotificationModel>

    @POST(RLConstants.URL_V3)
    fun rl_getCommentsData(@Body request: List<RLCommentGetApiPayload>): Call<RLCommentsApiResponse>

    //Insert Api
    @POST(RLConstants.insertJSONApi)
    fun rl_insertYourWayData(@Body request: List<RLYourWayApiPayload>): Call<RLInsertCommonApiResponse>

    @Multipart
    @POST(RLConstants.mpfIfCWxBL_insert)
    fun rl_insertClassSessionData(@PartMap data: Map<String, @JvmSuppressWildcards RequestBody>, @Part images: List<MultipartBody.Part>): Call<RLInsertCommonApiResponse>

     @Multipart
    @POST(RLConstants.mpfIfCWxBL_insert)
    fun rl_insertYourWayOverviewData(@PartMap data: Map<String, @JvmSuppressWildcards RequestBody>, @Part images: List<MultipartBody.Part>): Call<RLInsertCommonApiResponse>

     @Multipart
    @POST(RLConstants.insertGroup)
    fun rl_insertGroupData(@PartMap data: Map<String, @JvmSuppressWildcards RequestBody>, @Part images: List<MultipartBody.Part>): Call<RLInsertCommonApiResponse>

     @POST(RLConstants.insertJSONApi)
    fun rl_insertChallenges(@Body request: List<RLChallengesApiPayload>): Call<RLInsertCommonApiResponse>

    //Friends Insert
    @POST(RLConstants.insertJSONApi)
    fun rl_insertFriendsData(@Body request: List<RLFriendsInsertApiPayload>): Call<RLInsertCommonApiResponse>

    //Comment Insert
    @POST(RLConstants.CommentUrl)
    fun rl_insertCommentData(@Body request: List<RLCommentInsertApiPayload>): Call<RLInsertCommonApiResponse>

}