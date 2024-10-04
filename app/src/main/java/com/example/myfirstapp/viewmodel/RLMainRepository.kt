package com.example.myfirstapp.viewmodel
import com.example.myfirstapp.api.RLNetworkService
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
import org.json.JSONObject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RLMainRepository(private val apiService: RLNetworkService) {

    fun RLgetUserAggregatedData(request: List<RLGetUserAggregatedDataRequest>, callback: (Result<RLOverViewModel>) -> Unit) {
        apiService.RLgetUserAggregatedData(request).enqueue(object : Callback<RLOverViewModel> {
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

    fun RLgetUserFeedCardData(request: List<RLSetoverview_thumbRequest>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.RLgetUserFeedCardData(request).enqueue(object : Callback<RLFeedModel> {
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

    fun RLgetUserFeedCardDatayou(request: List<RLSetoverview_thumbRequest_you>, callback: (Result<RLFeedModel>) -> Unit) {
        apiService.RLgetUserFeedCardDatayou(request).enqueue(object : Callback<RLFeedModel> {
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

    fun RLGroupMembers(request: List<RLSetGroupMemberRequest>, callback: (Result<RLGetGroupMemberModel>) -> Unit) {
        apiService.RLGroupMembers(request).enqueue(object : Callback<RLGetGroupMemberModel> {
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

    fun RLgetGroupData(request: List<RLSetGroupRequest>, callback: (Result<RLGroupModel>) -> Unit) {
        apiService.RLgetGroupData(request).enqueue(object : Callback<RLGroupModel> {
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
    fun RLgetOverviewGraph(request: List<RLOverviewGraphDataRequest>, callback: (Result<RLOverviewGraphResponse>) -> Unit) {
        apiService.RLgetOverviewGraph(request).enqueue(object : Callback<RLOverviewGraphResponse> {
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

    fun RLgoaled_challenges(request: List<RLSetgoaled_challenges_request>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        apiService.RLgoaled_challenges(request).enqueue(object : Callback<RLFeedChallengesModel> {
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

    fun RLgoaled_challenges_Single(request: List<RLSetgoaled_challenges_request_single>, callback: (Result<RLFeedChallengesModel>) -> Unit) {
        apiService.RLgoaled_challenges_Single(request).enqueue(object : Callback<RLFeedChallengesModel> {
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
    fun RLMetricChartByDay(request: List<RLSetMetricChartByDay>, callback: (Result<RLFeedChallengesMapModel>) -> Unit) {
        apiService.RLMetricChartByDay(request).enqueue(object : Callback<RLFeedChallengesMapModel> {
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

    fun RLfriendsYouFollow(request: List<RLSetsearch_userrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        apiService.RLfriendsYouFollow(request).enqueue(object : Callback<RLYourFriendsModel> {
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

    fun RLfriendsFollowingYou(request: List<RLSetget_followersrequest>, callback: (Result<RLYourFriendsModel>) -> Unit) {
        apiService.RLfriendsFollowingYou(request).enqueue(object : Callback<RLYourFriendsModel> {
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

    fun RLyourGroupData(request: List<RLrequestgroup_dataset>, callback: (Result<RLYourGroupModel>) -> Unit) {
        apiService.RLyourGroupData(request).enqueue(object : Callback<RLYourGroupModel> {
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

    fun RLgetNotificationData(q:String, user:String, limit:Int, index:Int, callback: (Result<RLNotificationModel>) -> Unit) {
        apiService.RLgetNotificationData(q,user,limit,index).enqueue(object : Callback<RLNotificationModel> {
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

}