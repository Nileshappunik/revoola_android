package com.example.myfirstapp.model

data class RLVideoModel (
    val difficulty: String,
    val duration: String,
    val currentVideoGroup: String,
    val instructor: String,
    val classtype: String,
    val currentVideoGroups: String,
    val key: String,
    val timestamp: Long
)

data class RlVideoData(val videos: Map<String, RLVideoModel>)