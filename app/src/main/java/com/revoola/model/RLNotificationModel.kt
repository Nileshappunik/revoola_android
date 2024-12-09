package com.revoola.model

data class RLNotificationModel (var type:String, var text:List<RLNotificationDataModel>)
data class RLNotificationDataModel(val id:Int, val userid:String, val notification_data:String, val isRead:Int, val timestamp:String)