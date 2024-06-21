package com.example.myfirstapp.model

data class RLOverviewGraphResponse(var type:String, val text: List<RLOverviewGraphResponseData>)
data class RLOverviewGraphResponseData(val overviewGraph: List<RLOverviewGraphResponseDataCard>)
data class RLOverviewGraphResponseDataCard(val session: Int, val max_time:String, val avgtime:Int,
                                           val totaltime:Int, val burntcalories:Int, val maxcalories:Int, val totalREV:Int, val maxREV:Int,
                                           val totalrmm:Int, val maxrmm:Int, val avgrmm:Int, val totalrms:Int, val maxrms:Int, val steps:Int,
                                           val maxsteps:Int, val avgsteps:Int, val distance:Int, val maxdistance:Int, val avgdistance:String,
                                           val awards:Int, val medals:Int, val medals_gold:Int, val medals_silver:Int, val medals_bronze:Int,
                                           val elevation:Int, val maxelevation:Int, val avgelevation:String, val countSessionBody:Int, val countSessionMind:Int)

data class RLSessionitemset(val name:String, val number:String, val imageset:Int)