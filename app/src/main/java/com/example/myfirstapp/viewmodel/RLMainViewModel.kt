package com.example.myfirstapp.viewmodel
import androidx.lifecycle.ViewModel
import com.example.myfirstapp.model.RLFeedChallengesMapModel
import com.example.myfirstapp.model.RLFeedChallengesModel
import com.example.myfirstapp.model.RLFeedModel
import com.example.myfirstapp.model.RLGetUserAggregatedDataRequest
import com.example.myfirstapp.model.RLGroupModel
import com.example.myfirstapp.model.RLNotificationModel
import com.example.myfirstapp.model.RLOverViewModel
import com.example.myfirstapp.model.RLOverviewGraphResponse
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

class RLMainViewModel(val mainRepository:RLMainRepository): ViewModel() {
    fun RLgetUserAggregatedData(request: List<RLGetUserAggregatedDataRequest>, callback: (Result<RLOverViewModel>) -> Unit) {
        mainRepository.RLgetUserAggregatedData(request, callback)
    }

    fun RLgetUserFeedCardData(request: List<RLSetoverview_thumbRequest>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetUserFeedCardData(request, callback)
    }
    fun RLgetUserFeedCardDatayou(request: List<RLSetoverview_thumbRequest_you>, callback: (Result<RLFeedModel>) -> Unit) {
        mainRepository.RLgetUserFeedCardDatayou(request, callback)
    }

    fun RLgetGroupData(request: List<RLSetGroupRequest>, callback: (Result<RLGroupModel>) -> Unit) {
        mainRepository.RLgetGroupData(request, callback)
    }
    fun RLgetOverviewGraph(q:String, user:String, timestampfrom:Long, timestampto:Long, classtype:String, callback: (Result<RLOverviewGraphResponse>) -> Unit) {
        mainRepository.RLgetOverviewGraph(q,user,timestampfrom,timestampto,classtype, callback)
    }

    fun RLgoaled_challenges(request: List<RLSetgoaled_challenges_request>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        mainRepository.RLgoaled_challenges(request, callback)
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

    fun RLfriendsFollowingYou(request: List<RLSetget_followersrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        mainRepository.RLfriendsFollowingYou(request, callback)
    }

    fun RLyourGroupData(request: List<RLrequestgroup_dataset>, callback: (Result<RLYourGroupModel>) -> Unit) {
        mainRepository.RLyourGroupData(request, callback)
    }

    fun RLgetNotificationData(q:String, user:String, limit:Int, index:Int, callback: (Result<RLNotificationModel>) -> Unit) {
        mainRepository.RLgetNotificationData(q,user,limit,index, callback)
    }

}