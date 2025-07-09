package com.revoola.fragment.more.schduleModel

import android.os.Parcelable
import com.revoola.model.RLFulllVideoModel
import kotlinx.parcelize.Parcelize

data class ChallengerInfo(
    val userId: String,
    val isDecline: Boolean = false,
    val status: Boolean = false,
    val totalTime: Int = 0,
    val totalRev: Double = 0.0,
    val rank: Int = 0,
    val isDeline: Boolean = false,
    val challengeStatus: Int? = null
)

@Parcelize
data class ScheduleItem(
    val schedule: RLScheduleData,
    val videoItem: RLFulllVideoModel,
    val organizer: String,
    val organizerImage: String
) : Parcelable

@Parcelize
data class RLScheduleData(
    val isClass: Boolean = false,
    val typeOfGoal: String = "",
    val goal: String = "",
    val videoKey: String = "",
    val groupId: String = "",
    val dateOfChallenge: Long,
    val remark: String = "",
    val isMindClass: Boolean,
    val groupName: String = "",
    val createdBy: String = "",
    val typeOfWorkout: String = "",
    val challengeName: String = "",
    val challenger: Map<String, RLChallengerData> = emptyMap(),
    val location: String = "",
    val challengeStatus: Int = 0,
    val isGroup: Boolean = false,
    val typeOfChallenge: Int = 0,
    val timestamp: Long = 0,
    val statusLbl: String = ""
) : Parcelable

@Parcelize
data class RLChallengerData(
    val totalTime: Int = 0,
    val totalRev: Double = 0.0,
    val rank: Int = 0,
    val isDeline: Boolean = false,
    val status: Boolean = false
) : Parcelable
