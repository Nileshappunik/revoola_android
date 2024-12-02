package com.example.myfirstapp.model

import android.os.Parcelable



data class RLVideoModel (
    val difficulty: String,
    val duration: String,
    val currentVideoGroup: String,
    val instructor: String,
    val imageLinkInstructor: String,
    val imageLinkrectangleV2: String,
    val rideTitle: String,
    val classtype: String,
    val currentVideoGroups: String,
    val key: String,
    val timestamp: Long)


data class RLFulllVideoModel (
    val assumedREV: String,
    val assumedRMS: String,
    val classType: String,
    val cumulativeRiders: String,
    val currentVideoGroup: String,
    val difficulty: String,
    val duration: String,
    val imageLinkInstructor: String,
    val imageLinkLarge: String,
    val imageLinkSmall: String,
    val imageLinkSquareV2: String,
    val imageLinkfeedV2: String,
    val imageLinkrectangleV2: String,
    val instructor: String,
    val instructorClasses: String,
    val keywords: String,
    val mincooldown: String,
    val mininstruction: String,
    val minwarmup: String,
    val originalClassDate: String,
    val rating: String,
    val rideDescription: String,
    val rideTitle: String,
    val streamingUrl: String,
    val streamingUrlIpad: String,
    val streamingUrlIphonex: String,
    val style: String,
    val timestamp: String,
    val type: String,
    val videoLinkiPad: String,
    val videoLinkiPhone: String,
    val videoLinkiPhonex: String, )


