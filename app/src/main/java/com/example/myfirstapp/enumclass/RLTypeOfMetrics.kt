package com.example.myfirstapp.enumclass

import com.example.myfirstapp.R

enum class RLTypeOfMetrics (val title: String, val image: Int, val showright:Boolean) {
    Time("TIME", R.drawable.fd_active_time_green,false),
    TotalTime("TOTAL TIME", R.drawable.fd_active_time_green,false),
    Effort("EFFORT", R.drawable.ic_heart,false),
    AssumedEffort("ASSUMED EFFORT", R.drawable.ic_heart,false),
    Steps("STEPS", R.drawable.fd_steps_green,false),
    Calories("CALORIES", R.drawable.fd_calories_green,false),
    AvgHeartRate("AVG HEART RATE(bpm)", R.drawable.ic_heartrate,false),
    Distance("DISTANCE(MILE)", R.drawable.ic_distance,false),
    Climbed("CLIMBED(FEET)", R.drawable.ic_climb,false),
    AvgPace("AVG PACE(per mile)", R.drawable.fd_active_time_green,false),
    AvgSpeed("AVG SPEED(miles/h)", R.drawable.ic_speeed,false),
    Boosts("BOOSTS", R.drawable.fd_thumbs_gray,true),
    Comments("COMMENTS", R.drawable.fd_comment_gray,true),
    Awards("AWARDS", R.drawable.ic_award,true),
    EffortScore("EFFORT SCORE", R.drawable.ic_heart,false),
    EffortZone("EFFORT ZONE", R.drawable.ic_heart,false),
    AvgEffort("AVG EFFORT", R.drawable.ic_heart,false),
    MaxEffort("MAX EFFORT", R.drawable.ic_heart,false),
    MaxHeartRate("MAX HEART RATE(bpm)", R.drawable.ic_heartrate,false),
    Averagespeed("AVERAGE SPEED(MILE/H)", R.drawable.ic_speeed,false),
    MaxSpeed("Max SPEED(MILE/H)", R.drawable.ic_speeed,false),
    completed("COMPLETED(KM'S)", R.drawable.ic_distance,false),
    Averagepace("AVERAGE PACE", R.drawable.ic_pace,false),
    Slowtest("SLOWTEST", R.drawable.ic_pace,false),
    Fasttest("FastTEST", R.drawable.ic_pace,false),
    Totalclimbed("TOTAL CLIMBED(FEET)", R.drawable.ic_climb,false),
    Minelevation("MIN ELEVATION(FEET)", R.drawable.ic_climb,false),
    Maxelevation("MAX ELEVATION(FEET)", R.drawable.ic_climb,false),
    ActiveCalories("ACTIVE CALORIES", R.drawable.fd_calories_green,false),
    AssumedCalories("ASSUMED CALORIES", R.drawable.fd_calories_green,false),
    MindfulMinutes("MINDFUL MINUTES", R.drawable.ic_mind_read,false),
    Relaxation("RELAXATION", R.drawable.ic_mind_read,false),
    Cadence("CADENCE(rpm)", R.drawable.ic_cadence,false),
    AvgCadence("AVG CADENCE(rpm)", R.drawable.ic_cadence,false)
}


data class RLMetricData(var value: String)

//val metricValues: MutableMap<RLTypeOfMetrics, RLMetricData> = mutableMapOf(
//    RLTypeOfMetrics.Time to RLMetricData("00m 00s"),
//    RLTypeOfMetrics.Effort to RLMetricData("0"),
//    RLTypeOfMetrics.Steps to RLMetricData("0"))

