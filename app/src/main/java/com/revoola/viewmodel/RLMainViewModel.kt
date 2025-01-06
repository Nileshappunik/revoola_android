package com.revoola.viewmodel
import androidx.lifecycle.ViewModel
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
import com.revoola.model.RLTextOverview
import com.revoola.model.RLYourFriendsModel
import com.revoola.model.RLYourGroupModel
import com.revoola.model.RLrequest_goaled_challenges
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsearch_userrequest
import com.revoola.model.RLtrigger_inapp_referrer_goaled_challenges_Request

class RLMainViewModel(val mainRepository: RLMainRepository): ViewModel() {
    fun RLgetUserAggregatedData(request: List<RLGetUserAggregatedDataRequest>, callback: (Result<RLOverViewModel>) -> Unit) {
        mainRepository.RLgetUserAggregatedData(request, callback)
    }

    fun RLgetUserFeedCardData(request: List<RLSetoverview_thumbRequest>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetUserFeedCardData(request, callback)
    }
    fun RLgetUserFeedCardDatayou(request: List<RLSetoverview_thumbRequest_you>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetUserFeedCardDatayou(request, callback)
    }
    fun RLJoinBigChallengeFeed(request: List<RLtrigger_inapp_referrer_goaled_challenges_Request>, callback: (Result<String>) -> Unit) {
        mainRepository.RLJoinBigChallengeFeed(request, callback)
    }


    fun RLgetGroupData(request: List<RLSetGroupRequest>, callback: (Result<RLGroupModel>) -> Unit) {
        mainRepository.RLgetGroupData(request, callback)
    }

    fun RLGroupMembers(request: List<RLSetGroupMemberRequest>, callback: (Result<RLGetGroupMemberModel>) -> Unit) {
        mainRepository.RLGroupMembers(request, callback)
    }
    fun RLgetOverviewGraph(request: List<RLOverviewGraphDataRequest>, callback: (Result<RLOverviewGraphResponse>) -> Unit) {
        mainRepository.RLgetOverviewGraph(request, callback)
    }

    fun RLgoaled_challenges(request: List<RLSetgoaled_challenges_request>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.RLgoaled_challenges(request, callback)
    }

    fun RLgoaled_challenges_view(request: List<RLrequest_goaled_challenges>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.RLgoaled_challenges_view(request, callback)
    }

    fun RLgoaled_challenges_Single(request: List<RLSetgoaled_challenges_request_single>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.RLgoaled_challenges_Single(request, callback)
    }

    fun RLMetricChartByDay(request: List<RLSetMetricChartByDay>, callback: (Result<RLFeedChallengesMapModel>) -> Unit) {
        mainRepository.RLMetricChartByDay(request, callback)
    }

    fun RLfriendsYouFollow(request: List<RLSetsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.RLfriendsYouFollow(request, callback)
    }

    fun RLsearch_user_Data_DeepLink(request: List<RLsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.RLsearch_user_Data_DeepLink(request, callback)
    }

    fun RLfriendsFollowingYou(request: List<RLSetget_followersrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.RLfriendsFollowingYou(request, callback)
    }

    fun RLyourGroupData(request: List<RLrequestgroup_dataset>, callback: (Result<RLYourGroupModel>) -> Unit) {
        mainRepository.RLyourGroupData(request, callback)
    }

    fun RLgetOverviewThumbFromIdData(request: List<RLRequestDetail_dataset>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetOverviewThumbFromIdData(request, callback)
    }

    fun RLgetNotificationData(q:String, user:String, limit:Int, index:Int, callback: (Result<RLNotificationModel>) -> Unit) {
        mainRepository.RLgetNotificationData(q,user,limit,index, callback)
    }

    fun RLgetCommentsData(q:String, overviewid:String, limit:Int, index:Int, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetCommentsData(q,overviewid,limit,index, callback)
    }


}