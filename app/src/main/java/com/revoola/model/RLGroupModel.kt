package com.revoola.model

data class RLGroupModel(var type:String, var text:List<RLGroupCardModel>)

data class RLGroupCardModel(var group_avatar:String, var group_name:String, var group_id:String, var is_admin:Int, var number_of_members:Int)

data class RLSetGroupRequest(var group_data: RLSetGroupData)
data class RLSetGroupData(var userid: String, var limit: Int, var index:Int)

data class RLSetGroupMemberRequest(var group_data: RLSetGroupMemberData)
data class RLSetGroupMemberData(var groupid: String,val current_userid:String, var limit: Int, var index:Int)

data class RLGetGroupMemberModel(var type:String, var text:List<RLuserData>)