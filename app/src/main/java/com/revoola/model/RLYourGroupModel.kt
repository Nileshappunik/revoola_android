package com.revoola.model

data class RLYourGroupModel(val type:String, val text:List<RLyourGroupDataModel>)
data class RLyourGroupDataModel(val group_avatar:String, val group_name:String, val group_id:String, val is_admin:Int, val number_of_members:Int, var isSelected: Boolean = false)

data class RLrequestgroup_dataset(val group_data: RLsetgroup_data)
data class RLsetgroup_data(val userid:String, val limit:Int, val index:Int)