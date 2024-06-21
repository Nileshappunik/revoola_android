package com.example.myfirstapp.model

data class RLYourFriendsModel(val type:String, val text:RLuser)

data class RLuser(val user:List<RLuserData>)
data class RLuserData(val first_name:String, val last_name:String, val userid:String, val username:String,
                      val avatar:String, val myid:String, val myidstatus:String, val theirid:String, val theiridstatus:String, var isSelected: Boolean = false)


data class RLSetsearch_userrequest(var search_user: RLSetsearch_user)
data class RLSetsearch_user(var get_friends: String, var limit: Int, var index:Int)
data class RLSetget_followersrequest(var search_user: RLSetget_followers)
data class RLSetget_followers(var get_followers: String, var limit: Int, var index:Int)
