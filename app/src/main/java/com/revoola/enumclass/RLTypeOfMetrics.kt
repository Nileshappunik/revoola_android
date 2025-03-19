package com.revoola.enumclass

import com.revoola.R

enum class RLTypeOfMetrics (val title: String, val image: Int, val showright:Boolean) {
    Time("TIME", R.drawable.fd_active_time_green,false),
    TotalTime("TOTAL TIME", R.drawable.fd_active_time_green,false),
    Effort("EFFORT", R.drawable.ic_heart,false),
    AssumedEffort("ASSUMED EFFORT", R.drawable.ic_heart,false),
    Steps("STEPS", R.drawable.fd_steps_green,false),
    Calories("CALORIES", R.drawable.fd_calories_green,false),
    AvgHeartRate("AVG HEART RATE(bpm)", R.drawable.ic_heartrate,false),
    Distance("DISTANCE(mile)", R.drawable.ic_distance,false),
    DistanceKm("DISTANCE(km)", R.drawable.ic_distance,false),
    Climbed("CLIMBED(feet)", R.drawable.ic_climb,false),
    ClimbedM("CLIMBED(m)", R.drawable.ic_climb,false),
    AvgPace("AVG PACE(per mile)", R.drawable.ic_pace,false),
    AvgPaceKm("AVG PACE(per km)", R.drawable.ic_pace,false),
    AvgSpeed("AVG SPEED(mph)", R.drawable.ic_speeed,false),
    AvgSpeedKm("AVG SPEED(km/h)", R.drawable.ic_speeed,false),
    Boosts("BOOSTS", R.drawable.fd_thumbs_gray,true),
    Comments("COMMENTS", R.drawable.fd_comment_gray,true),
    Awards("AWARDS", R.drawable.ic_award,true),
    EffortScore("EFFORT SCORE", R.drawable.ic_heart,false),
    EffortZone("EFFORT ZONE", R.drawable.ic_heart,false),
    AvgEffort("AVG EFFORT", R.drawable.ic_heart,false),
    MaxEffort("MAX EFFORT", R.drawable.ic_heart,false),
    MaxHeartRate("MAX HEART RATE(bpm)", R.drawable.ic_heartrate,false),
    Averagespeed("AVERAGE SPEED(mph)", R.drawable.ic_speeed,false),
    AveragespeedKm("AVERAGE SPEED(km/h)", R.drawable.ic_speeed,false),
    MaxSpeed("Max SPEED(mph)", R.drawable.ic_speeed,false),
    MaxSpeedKM("Max SPEED(km/h)", R.drawable.ic_speeed,false),
    completed("COMPLETED(miles)", R.drawable.ic_distance,false),
    completedKm("COMPLETED(KM'S)", R.drawable.ic_distance,false),
    Averagepace("AVERAGE PACE", R.drawable.ic_pace,false),
    Slowtest("SLOWTEST", R.drawable.ic_pace,false),
    Fasttest("FastTEST", R.drawable.ic_pace,false),
    Totalclimbed("TOTAL CLIMBED(feet)", R.drawable.ic_climb,false),
    TotalclimbedM("TOTAL CLIMBED(m)", R.drawable.ic_climb,false),
    Totalelevation("TOTAL ELEVATION(feet)", R.drawable.ic_climb,false),
    TotalelevationM("TOTAL ELEVATION(m)", R.drawable.ic_climb,false),
    Minelevation("MIN ELEVATION(feet)", R.drawable.ic_climb,false),
    MinelevationM("MIN ELEVATION(m)", R.drawable.ic_climb,false),
    Maxelevation("MAX ELEVATION(feet)", R.drawable.ic_climb,false),
    MaxelevationM("MAX ELEVATION(m)", R.drawable.ic_climb,false),
    ActiveCalories("ACTIVE CALORIES", R.drawable.fd_calories_green,false),
    AssumedCalories("ASSUMED CALORIES", R.drawable.fd_calories_green,false),
    MindfulMinutes("MINDFUL MINUTES", R.drawable.ic_mind_read,false),
    AssumeRelaxation("ASSUMED RELAXATION", R.drawable.ic_mind_read,false),
    Cadence("CADENCE(rpm)", R.drawable.ic_cadence,false),
    AvgCadence("AVG CADENCE(rpm)", R.drawable.ic_cadence,false)
}

data class RLMetricData(var value: String)



//val metricValues: MutableMap<RLTypeOfMetrics, RLMetricData> = mutableMapOf(
//    RLTypeOfMetrics.Time to RLMetricData("00m 00s"),
//    RLTypeOfMetrics.Effort to RLMetricData("0"),
//    RLTypeOfMetrics.Steps to RLMetricData("0"))

