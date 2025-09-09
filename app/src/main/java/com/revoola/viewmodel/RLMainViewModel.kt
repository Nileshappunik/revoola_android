package com.revoola.viewmodel

import androidx.lifecycle.ViewModel
import com.revoola.fragment.friends.model.EmailFilterApiResponse
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFriendsInsertApiPayload
import com.revoola.fragment.friends.model.RLFriendsUpdateApiPayload
import com.revoola.model.RLChallengesApiPayload
import com.revoola.model.RLCommentDeleteApiPayload
import com.revoola.model.RLCommentGetApiPayload
import com.revoola.model.RLCommentInsertApiPayload
import com.revoola.model.RLCommentReplyDeleteApiPayload
import com.revoola.model.RLCommentReplyInsertApiPayload
import com.revoola.model.RLCommentsApiResponse
import com.revoola.model.RLDeleteFeedItemApiPayload
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
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RLMainViewModel(val mainRepository: RLMainRepository): ViewModel() {
    fun rl_getUserAggregatedData(request: List<RLGetUserAggregatedDataRequest>, callback: (Result<RLOverViewModel>) -> Unit) {
        mainRepository.rl_getUserAggregatedData(request, callback)
    }

    fun rl_getUserFeedCardData(request: List<RLSetoverview_thumbRequest>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.rl_getUserFeedCardData(request, callback)
    }
    fun rl_getUserFeedCardDatayou(request: List<RLSetoverview_thumbRequest_you>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.rl_getUserFeedCardDatayou(request, callback)
    }
    fun rl_joinBigChallengeFeed(request: List<RLtrigger_inapp_referrer_goaled_challenges_Request>, callback: (Result<String>) -> Unit) {
        mainRepository.rl_joinBigChallengeFeed(request, callback)
    }


    fun rl_getGroupData(request: List<RLSetGroupRequest>, callback: (Result<RLGroupModel>) -> Unit) {
        mainRepository.rl_getGroupData(request, callback)
    }

    fun rl_groupMembers(request: List<RLSetGroupMemberRequest>, callback: (Result<RLGetGroupMemberModel>) -> Unit) {
        mainRepository.rl_groupMembers(request, callback)
    }
    fun rl_getOverviewGraph(request: List<RLOverviewGraphDataRequest>, callback: (Result<RLOverviewGraphResponse>) -> Unit) {
        mainRepository.rl_getOverviewGraph(request, callback)
    }

    fun rl_goaled_challenges(request: List<RLSetgoaled_challenges_request>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.rl_goaled_challenges(request, callback)
    }

    fun rl_goaled_challenges_view(request: List<RLrequest_goaled_challenges>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.rl_goaled_challenges_view(request, callback)
    }

    fun rl_goaled_challenges_Single(request: List<RLSetgoaled_challenges_request_single>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.rl_goaled_challenges_Single(request, callback)
    }

    fun rl_metricChartByDay(request: List<RLSetMetricChartByDay>, callback: (Result<RLFeedChallengesMapModel>) -> Unit) {
        mainRepository.rl_metricChartByDay(request, callback)
    }

    fun rl_friendsYouFollow(request: List<RLSetsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.rl_friendsYouFollow(request, callback)
    }

    fun rl_search_user_Data_DeepLink(request: List<RLsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.rl_search_user_Data_DeepLink(request, callback)
    }
    fun rl_findOnRevoolaEmailFilter(request: List<RLEmailFilterRequestModel>, callback: (Result<EmailFilterApiResponse>) -> Unit) {
        mainRepository.rl_findOnRevoolaEmailFilter(request, callback)
    }

    fun rl_friendsFollowingYou(request: List<RLSetget_followersrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.rl_friendsFollowingYou(request, callback)
    }

    fun rl_yourGroupData(request: List<RLrequestgroup_dataset>, callback: (Result<RLYourGroupModel>) -> Unit) {
        mainRepository.rl_yourGroupData(request, callback)
    }

    fun rl_getOverviewThumbFromIdData(request: List<RLRequestDetail_dataset>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.rl_getOverviewThumbFromIdData(request, callback)
    }

    fun rl_getNotificationData(q:String, user:String, limit:Int, index:Int, callback: (Result<RLNotificationModel>) -> Unit) {
        mainRepository.rl_getNotificationData(q,user,limit,index, callback)
    }

    fun rl_getCommentsData(request: List<RLCommentGetApiPayload>,callback: (Result<RLCommentsApiResponse>) -> Unit) {
        mainRepository.rl_getCommentsData(request, callback)
    }

    fun rl_deleteCommentItem(request: List<RLCommentDeleteApiPayload>,callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_deleteCommentItem(request, callback)
    }

    fun rl_deleteReplyCommentItem(request: List<RLCommentReplyDeleteApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_deleteReplyCommentItem(request, callback)
    }

    //Insert Api
    fun rl_insertYourWayData(request: List<RLYourWayApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertYourWayData(request, callback)
    }

    fun rl_deleteFeedCardItem(request: List<RLDeleteFeedItemApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_deleteFeedCardItem(request, callback)
    }
    fun rl_insertFriendsData(request: List<RLFriendsInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertFriendsData(request, callback)
    }
    fun rl_updateFriendsData(request: List<Map<String, Any>>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_updateFriendsData(request, callback)
    }
    fun rl_insertClassSessionData(request: Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertClassSessionData(request,images, callback)
    }

    fun rl_insertYourWayOverviewData(request:  Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertYourWayOverviewData(request,images, callback)
    }


    fun rl_updateFeedItemCardData(request:  Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_updateFeedItemCardData(request,images, callback)
    }
    fun rl_insertGroupData(request:  Map<String, @JvmSuppressWildcards RequestBody>, images:List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertGroupData(request,images, callback)
    }

    fun rl_insertChallenges(request: List<RLChallengesApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertChallenges(request, callback)
    }


    fun rl_insertCommentData(request: List<RLCommentInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertCommentData(request, callback)
    }

    fun rl_insertReplyCommentData(request: List<RLCommentReplyInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        mainRepository.rl_insertReplyCommentData(request, callback)
    }

}