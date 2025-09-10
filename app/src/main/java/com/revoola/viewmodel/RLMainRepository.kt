package com.revoola.viewmodel
import com.revoola.api.RLNetworkService
import com.revoola.fragment.friends.model.EmailFilterApiResponse
import com.revoola.fragment.friends.model.RLEmailFilterRequestModel
import com.revoola.fragment.friends.model.RLFriendsInsertApiPayload
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
import com.revoola.model.RLTextOverview
import com.revoola.model.RLoverview_thumb_you
import com.revoola.model.RLrequest_goaled_challenges
import com.revoola.model.RLrequestgroup_dataset
import com.revoola.model.RLsearch_userrequest
import com.revoola.model.RLtrigger_inapp_referrer_goaled_challenges_Request
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RLMainRepository(private val apiService: RLNetworkService) {

    fun rl_getUserAggregatedData(request: List<RLGetUserAggregatedDataRequest>, callback: (Result<RLOverViewModel>) -> Unit) {
        apiService.rl_getUserAggregatedData(request).enqueue(object : Callback<RLOverViewModel> {
            override fun onResponse(call: Call<RLOverViewModel>, response: Response<RLOverViewModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLOverViewModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_getUserFeedCardData(request: List<RLSetoverview_thumbRequest>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.rl_getUserFeedCardData(request).enqueue(object : Callback<RLFeedModel> {
            override fun onResponse(call: Call<RLFeedModel>, response: Response<RLFeedModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_getUserFeedCardDatayou(request: List<RLSetoverview_thumbRequest_you>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.rl_getUserFeedCardDatayou(request).enqueue(object : Callback<RLFeedModel> {
            override fun onResponse(call: Call<RLFeedModel>, response: Response<RLFeedModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_joinBigChallengeFeed(request: List<RLtrigger_inapp_referrer_goaled_challenges_Request>, callback: (Result<String>) -> Unit) {
        apiService.rl_JoinBigChallengeFeed(request).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body() ?: "No response"
                    callback(Result.success(responseBody))
                } else {
                    callback(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }


    fun rl_groupMembers(request: List<RLSetGroupMemberRequest>, callback: (Result<RLGetGroupMemberModel>) -> Unit) {
        apiService.rl_groupMembers(request).enqueue(object : Callback<RLGetGroupMemberModel> {
            override fun onResponse(call: Call<RLGetGroupMemberModel>, response: Response<RLGetGroupMemberModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLGetGroupMemberModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_getGroupData(request: List<RLSetGroupRequest>, callback: (Result<RLGroupModel>) -> Unit) {
        apiService.rl_getGroupData(request).enqueue(object : Callback<RLGroupModel> {
            override fun onResponse(call: Call<RLGroupModel>, response: Response<RLGroupModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLGroupModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_getOverviewGraph(request: List<RLOverviewGraphDataRequest>, callback: (Result<RLOverviewGraphResponse>) -> Unit) {
        apiService.rl_getOverviewGraph(request).enqueue(object : Callback<RLOverviewGraphResponse> {
            override fun onResponse(call: Call<RLOverviewGraphResponse>, response: Response<RLOverviewGraphResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLOverviewGraphResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_goaled_challenges(request: List<RLSetgoaled_challenges_request>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        apiService.rl_goaled_challenges(request).enqueue(object : Callback<RLFeedChallengesModel> {
            override fun onResponse(call: Call<RLFeedChallengesModel>, response: Response<RLFeedChallengesModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedChallengesModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_goaled_challenges_view(request: List<RLrequest_goaled_challenges>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        apiService.rl_goaled_challenges_view(request).enqueue(object : Callback<RLFeedChallengesModel> {
            override fun onResponse(call: Call<RLFeedChallengesModel>, response: Response<RLFeedChallengesModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedChallengesModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_goaled_challenges_Single(request: List<RLSetgoaled_challenges_request_single>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        apiService.rl_goaled_challenges_Single(request).enqueue(object : Callback<RLFeedChallengesModel> {
            override fun onResponse(call: Call<RLFeedChallengesModel>, response: Response<RLFeedChallengesModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedChallengesModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_metricChartByDay(request: List<RLSetMetricChartByDay>, callback: (Result<RLFeedChallengesMapModel>) -> Unit) {
        apiService.rl_metricChartByDay(request).enqueue(object : Callback<RLFeedChallengesMapModel> {
            override fun onResponse(call: Call<RLFeedChallengesMapModel>, response: Response<RLFeedChallengesMapModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedChallengesMapModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_friendsYouFollow(request: List<RLSetsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        apiService.rl_friendsYouFollow(request).enqueue(object : Callback<RLYourFriendsModel> {
            override fun onResponse(call: Call<RLYourFriendsModel>, response: Response<RLYourFriendsModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLYourFriendsModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_search_user_Data_DeepLink(request: List<RLsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        apiService.rl_search_user_Data_DeepLink(request).enqueue(object : Callback<RLYourFriendsModel> {
            override fun onResponse(call: Call<RLYourFriendsModel>, response: Response<RLYourFriendsModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLYourFriendsModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_findOnRevoolaEmailFilter(request: List<RLEmailFilterRequestModel>, callback: (Result<EmailFilterApiResponse>) -> Unit) {
        apiService.rl_findOnRevoolaEmailFilter(request).enqueue(object : Callback<EmailFilterApiResponse> {
            override fun onResponse(call: Call<EmailFilterApiResponse>, response: Response<EmailFilterApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<EmailFilterApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_friendsFollowingYou(request: List<RLSetget_followersrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        apiService.rl_friendsFollowingYou(request).enqueue(object : Callback<RLYourFriendsModel> {
            override fun onResponse(call: Call<RLYourFriendsModel>, response: Response<RLYourFriendsModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLYourFriendsModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_friendsClickList(request: List<RLoverview_thumb_you>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.rl_friendsClickList(request).enqueue(object : Callback<RLFeedModel> {
            override fun onResponse(call: Call<RLFeedModel>, response: Response<RLFeedModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_yourGroupData(request: List<RLrequestgroup_dataset>, callback: (Result<RLYourGroupModel>) -> Unit) {
        apiService.rl_yourGroupData(request).enqueue(object : Callback<RLYourGroupModel> {
            override fun onResponse(call: Call<RLYourGroupModel>, response: Response<RLYourGroupModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLYourGroupModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_getOverviewThumbFromIdData(request: List<RLRequestDetail_dataset>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.rl_getOverviewThumbFromIdData(request).enqueue(object : Callback<RLFeedModel> {
            override fun onResponse(call: Call<RLFeedModel>, response: Response<RLFeedModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLFeedModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_getNotificationData(q:String, user:String, limit:Int, index:Int, callback: (Result<RLNotificationModel>) -> Unit) {
        apiService.rl_getNotificationData(q,user,limit,index).enqueue(object : Callback<RLNotificationModel> {
            override fun onResponse(call: Call<RLNotificationModel>, response: Response<RLNotificationModel>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLNotificationModel>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_getCommentsData(request: List<RLCommentGetApiPayload>, callback: (Result<RLCommentsApiResponse>) -> Unit) {
        apiService.rl_getCommentsData(request).enqueue(object : Callback<RLCommentsApiResponse> {
            override fun onResponse(call: Call<RLCommentsApiResponse>, response: Response<RLCommentsApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLCommentsApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_deleteCommentItem(request: List<RLCommentDeleteApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_deleteCommentItem(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_deleteReplyCommentItem(request: List<RLCommentReplyDeleteApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_deleteReplyCommentItem(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    //Innsert Api
    fun rl_insertYourWayData(request: List<RLYourWayApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertYourWayData(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_deleteFeedCardItem(request: List<RLDeleteFeedItemApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_deleteFeedCardItem(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    //Innsert Api
    fun rl_insertFriendsData(request: List<RLFriendsInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertFriendsData(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }


    fun rl_updateFriendsData(request: List<Map<String, Any>>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_updateFriendsData(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_insertClassSessionData(request:Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertClassSessionData(request,images).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }
    fun rl_insertYourWayOverviewData(request:Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertYourWayOverviewData(request,images).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_updateFeedItemCardData(request:Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_updateFeedItemCardData(request,images).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_insertGroupData(request:Map<String, @JvmSuppressWildcards RequestBody>, images: List<MultipartBody.Part>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertGroupData(request,images).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }


    fun rl_insertChallenges(request: List<RLChallengesApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertChallenges(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_insertCommentData(request: List<RLCommentInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertCommentData(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

    fun rl_insertReplyCommentData(request: List<RLCommentReplyInsertApiPayload>, callback: (Result<RLInsertCommonApiResponse>) -> Unit) {
        apiService.rl_insertReplyCommentData(request).enqueue(object : Callback<RLInsertCommonApiResponse> {
            override fun onResponse(call: Call<RLInsertCommonApiResponse>, response: Response<RLInsertCommonApiResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(response.body()!!))
                } else {
                    callback(Result.failure(Throwable(response.message().toString())))
                }
            }
            override fun onFailure(call: Call<RLInsertCommonApiResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })

    }

}