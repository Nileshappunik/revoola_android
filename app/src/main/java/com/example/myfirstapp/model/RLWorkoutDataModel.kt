package com.example.myfirstapp.model

import java.io.Serializable

data class RLWorkoutDataModel (
    var workoutSessionDetails: RLWorkoutSessionDetailsModel,
    var workoutSessionSummary: RLWorkoutSessionSummaryModel,
    val getSessionTime:String,
    val bestLastObj:RLBestLastObj,
    val backend_Body2:RLBackendBody2,
    val backend_Body1:RLBackendBody1
) : Serializable

data class RLWorkoutModel (
    var name: String = "",
    var activity: String = "",
    var IODoor: String = "",
    var type: String = "",
    var icon: String = "",
    var goalType: String = "",
    var hour: String = "",
    var min: String = "",
    var time: String = "",
    var sensor: MutableList<Any> = mutableListOf(),
    var classType: String = "",
    var filter: String = ""
) : Serializable

data class RLBackendBody2(
    val myOverviewThumbnails: List<RLMyOverviewThumbnail>
)

data class RLMyOverviewThumbnail(
    val userid: String,
    val className: String,
    val classType: String,
    val timestamp: Long,
    val timestamp_local: Long,
    val imageLinkSmall: String,
    val totalRev: Double,
    val totalTime: Int,
    val burntCalories: Double,
    val totalRMM: Int,
    val totalRMS: Int,
    val maxRevPercentage: Int,
    val avgRevPercentage: Int,
    val zone1Seconds: Int,
    val zone2Seconds: Int,
    val zone3Seconds: Int,
    val zone4Seconds: Int,
    val zone5Seconds: Int,
    val zone6Seconds: Int,
    val zone7Seconds: Int,
    val medals: String,
    val awards: String,
    val visibilityflagforthatsession: Int,
    val share_map: Int,
    val from_third_party_source: Int,
    val bmo: Int,
    val instructor: String,
    val duration: String,
    val rideTitle: String,
    val mainTitle: String,
    val originalClassDate: String,
    val videoKey: String,
    val goal: String,
    val medals_gold: Int,
    val medals_silver: Int,
    val medals_bronze: Int,
    val elevation: Int,
    val power: Int,
    val hr: Int,
    val steps: Int,
    val distance: Double,
    val userImage: List<String>, // Assuming it's a list of images
    val hrm: Int,
    val class_level: String,
    val average_speed: Double
)

data class RLBackendBody1(
    val classleaderboard: List<RLClassLeaderboard>
)

data class RLClassLeaderboard(
    val userid: String,
    val classid: String,
    val timestamp: Long,
    val timestamp_local: Long,
    val totalrev: Double,
    val visibilityflagforthatsession: Int,
    val discipline: String,
    val duration: Int,
    val calories: Double,
    val bmo: Int,
    val rmm: Int,
    val rms: Int,
    val source: String,
    val goal: String
)

data class RLBestLastObj(
    val arrAvgCadence: List<Int>,
    val arrAvgHr: List<Int>,
    val arrAvgPower: List<Int>,
    val arrAvgRevPercentage: List<Int>,
    val arrHr: List<Int>,
    val arrMaxCadence: List<Int>,
    val arrMaxHr: List<Int>,
    val arrMaxPower: List<Int>,
    val arrMaxRevPercentage: List<Int>,
    val arrPower: List<Int>,
    val arrPowerFromDevice: List<Int>,
    val arrRevPercentage: List<Int>,
    val arrRevSecond: List<Int>,
    val classDate: Long,
    val displayImage: String,
    val displayName: String,
    val flagImage: String,
    val flagName: String,
    val isPowerDeviceConnected: Boolean,
    val location: String,
    val timestamp: Long,
    val totalRev: Double,
    val totalTime: Int,
    val visibilityflagforthatsession: Int,
    val restingHrUsedForCalculation: Int,
    val maxHrUsedForCalculation: Int,
    val RestingHrUsedForCalculation_Last: Int,
    val maxHrUsedForCalculation_Last: Int
)

