package com.example.myfirstapp.model

data class RLOverviewGraphResponse(var type:String, val text: List<RLOverviewGraphResponseData>)
data class RLOverviewGraphResponseData(val overviewGraph: List<RlOverviewGraphData>)
data class RlOverviewGraphData(
    val session: Int,
    val totaltime: Int,
    val max_time_per_session: Int,
    val avg_time_per_session: Int,
    val max_daily_totaltime: Int,
    val avg_daily_totaltime: Int,
    val total_calories: Int,
    val max_total_calories_per_session: Int,
    val avg_total_calories_per_session: Int,
    val max_daily_total_calories: Int,
    val avg_daily_total_calories: Int,
    val active_calories: Int,
    val max_active_calories_per_session: Int,
    val avg_active_calories_per_session: Int,
    val max_daily_active_calories: Int,
    val avg_daily_active_calories: Int,
    val total_REV: Int,
    val max_REV_per_session: Int,
    val avg_REV_per_session: Int,
    val max_daily_totalREV: Int,
    val avg_daily_totalREV: Int,
    val total_steps: Int,
    val max_steps_per_session: Int,
    val avg_steps_per_session: Int,
    val max_daily_steps: Int,
    val avg_daily_steps: Int,



    val total_distance: String,
    val max_distance_per_session: String,
    val avg_distance_per_session: String,
    val max_daily_distance: String,
    val avg_daily_distance: String,
    val avg_daily_totalrms: String,
    val avg_daily_totalrmm: String,
    val max_daily_elevation: String,
    val avg_daily_elevation: String,



    val total_elevation: Int,
    val max_elevation_per_session: Int,
    val avg_elevation_per_session: Int,
    val total_rmm: Int,
    val max_rmm_per_session: Int,
    val avg_rmm_per_session: Int,
    val total_rms: Int,
    val max_rms_per_session: Int,
    val avg_rms_per_session: Int,
    val awards: Int,
    val medals: Int,
    val medals_gold: Int,
    val medals_silver: Int,
    val medals_bronze: Int,
    val countSessionBody: Int,
    val countSessionMind: Int)








data class RLSessionitemset(val name:String, val number:String, val imageset:Int)