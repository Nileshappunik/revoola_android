package com.example.myfirstapp.model

import java.io.Serializable

data class RLFeedModel(var type: String, var text: List<RLTextOverview>)

data class RLTextOverview(val avatar: String,
                          val username: String,
                          val first_name: String,
                          val ID: Int,
                          val userid: String,
                          val className: String,
                          var classType: String?,
                          val timestamp: String,
                          val short_timestamp: String,
                          val timestamp_local: String,
                          val imageLinkSmall: String,
                          val totalREV: Double,
                          val totalTime: String,
                          val burntCalories: String,
                          val totalRMM: String,
                          val totalRMS: String,
                          val maxRevPercentage: String,
                          val avgRevPercentage: String,
                          val zone1Seconds: String,
                          val zone2Seconds: String,
                          val zone3Seconds: String,
                          val zone4Seconds: String,
                          val zone5Seconds: String,
                          val zone6Seconds: String,
                          val zone7Seconds: String,
                          val medals: String,
                          val medals_gold: Int,
                          val medals_silver: Int,
                          val medals_bronze: Int,
                          val awards: String,
                          val visibilityFlagForThatSession: Int,
                          val bmo: Int,
                          val instructor: String,
                          val duration: String,
                          val rideTitle: String,
                          val mainTitle: String,
                          val originalClassDate: String,
                          val videoKey: String,
                          val goal: String,
                          val avatarKudos: String,
                          val avatar_comments: String?,
                          val total_kudos: Int,
                          val total_comments: Int,
                          val elevation: Int,
                          val power: Int,
                          val hr: Int,
                          val steps: Int,
                          val distance: Double,
                          val hrm: Int,
                          val class_level: String,
                          val average_speed: Double,
                          val map_image: String,
                          val user_images: String,
                          val isDeleted: Int,
                          val dems: String?,
                          val spike_steps: String?,
                          val spike_timestamp: String?,
                          val share_map: Int,
                          val from_third_party_source: Int,
                          val map_url: String?,
                          val dom: Int,
                          val rhr: Int?,
                          val mhr: Int?,
                          val avgHr: Int?,
                          val notes: String?,
                          val source: String,
                          val isKudos: Int ) : Serializable

data class RLSetoverview_thumbRequest(var overview_thumb: RLSetoverview_thumb)
data class RLSetoverview_thumb(var timestampfrom: Int,
                               var timestampto: String, var groupid: String, var limit: Int,
                               var index:Int, var goal:String,
                               var current_user:String, var isall:Int, var d:String)

data class RLSetoverview_thumbRequest_you(var overview_thumb: RLSetoverview_thumb_you)
data class RLSetoverview_thumb_you(var timestampfrom: Int,
                                   var timestampto: String, var users:List<String>, var limit: Int,
                                   var index:Int, var goal:String,
                                   var current_user:String, var isall:Int, var d:String)

data class RLSetgoaled_challenges_request(var goaled_challenges:RLSetgoaled_challenges)

data class RLSetgoaled_challenges(var id:String, var type:String, var today:String)

data class RLFeedChallengesModel(var type: String, var text:RLFeedChallengesModelListData )
data class RLFeedChallengesModelListData(var data:List<RLFeedChallengesModelData>)
data class RLFeedChallengesModelData(var actualtotal:Int, var metric:String, var challenge_name:String,
                                     var startdate:String, var enddate:String, var totaldays:Int, var totaltarget:Double, var userid:String,
                                     var username:String, var avatar:String, var first_name:String, var last_name:String, var full_name:String,
                                     var challengeadmin:String, var adminusername:String, var adminavatar:String, var adminfirstname:String,
                                     var adminlastname:String, var adminfullname:String, var number_of_members:String, var challengeid:String,
                                     var scenario:String, var percentage_of_goal:Double, var length_of_challenge:Double, var days_remaining:Double,
                                     var percentage_of_time_elapsed:Double,val targettype:String)


