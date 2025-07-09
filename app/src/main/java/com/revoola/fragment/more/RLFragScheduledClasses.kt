package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.RLBaseProgress
import com.revoola.adapter.RLScheduledClassesListAdapter
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databinding.RlFragScheduledClassesBinding
import com.revoola.enumclass.RLChallengeStatusType
import com.revoola.fragment.more.schduleModel.ChallengerInfo
import com.revoola.fragment.more.schduleModel.RLScheduleData
import com.revoola.fragment.more.schduleModel.ScheduleItem
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RLFragScheduledClasses : RLBaseFragment() {
    val TAG: String = RLFragScheduledClasses::class.java.simpleName
    private lateinit var adapterScheduledClasses: RLScheduledClassesListAdapter
    var currentUserId = ""

    private val fragBinding by lazy {
        RlFragScheduledClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragScheduledClasses" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        currentUserId = RLAuthManager().RlgetCurrentUser()?.uid ?:""
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvSchdualclasses.layoutManager = linearLayoutManager
        adapterScheduledClasses = RLScheduledClassesListAdapter(activity,emptyList())
        fragBinding.rvSchdualclasses.adapter = adapterScheduledClasses
        RLBaseProgress.RLShowProgressDialog(requireActivity())
        GlobalScope.launch {
            RLFirebaseManager().databaseRead.RlreadData(RevoolaFirebasePath.ScheduledPathRead()){  data, error ->
                if (data!=null){
                    // Extract keys into a list
                    val keysList = mutableListOf<String>()
                    when (data) {
                        is Map<*, *> -> {
                            // If data is a Map, extract all keys
                            data.keys.forEach { key ->
                                key?.toString()?.let { keyString ->
                                    keysList.add(keyString)
                                }
                            }
                        }
                        else -> {
                            RLTools.RlLogEPrint(TAG, "Data is not in expected Map format")
                        }
                    }
                    val scheduledRequestDataList = mutableListOf<Map<String, Any?>>()
                    var completedRequests = 0
                    val totalRequests = keysList.size
                    keysList.forEach { key->
                        RLFirebaseManager().databaseRead.RlreadData(RevoolaFirebasePath.ScheduledRequestPathRead(key)){  data, error ->
                            if (data!=null){
                                // Simple usage example
                                when (data) {
                                    is Map<*, *> -> {
                                        @Suppress("UNCHECKED_CAST")
                                        scheduledRequestDataList.add(data as Map<String, Any?>)
                                    }
                                    else -> {
                                        RLTools.RlLogDPrint(TAG, "Request data for key $key is not in expected Map format")
                                    }
                                }
                            }else{
                                RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Request Data: ${error?.message}")
                            }
                            completedRequests++
                            if (completedRequests == totalRequests) {
                                completedRequests = 0
                                getSortScheduleList(scheduledRequestDataList)
                            }
                        }

                    }
                }else{
                    noScheduleDataShow("No schedule Data")
                    RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Key Data: ${error?.message}")
                }
            }
        }

    }

    private fun getSortScheduleList(scheduleList: List<Map<String, Any?>>) {
        val now = System.currentTimeMillis()
        // Filter future dates (equivalent to x.dateOfChallenge * 1000 >= now)
        val filteredList = scheduleList.filter { schedule ->
            val dateOfChallenge = when (val date = schedule["dateOfChallenge"]) {
                is Number -> date.toLong() * 1000
                is String -> date.toLongOrNull()?.times(1000) ?: 0L
                else -> 0L
            }
            dateOfChallenge >= now
        }

        // Sort by dateOfChallenge (equivalent to a.dateOfChallenge - b.dateOfChallenge)
        val sortedList = filteredList.sortedBy { schedule ->
            when (val date = schedule["dateOfChallenge"]) {
                is Number -> date.toLong()
                is String -> date.toLongOrNull() ?: 0L
                else -> 0L
            }
        }

        val scheduleKeyList = mutableListOf<ScheduleItem>()
        // Process each schedule item
        sortedList.forEach { schedule ->
            val mutableSchedule = schedule.toMutableMap()
            val sampleArr = mutableListOf<ChallengerInfo>()
            var selfUserChallenge: ChallengerInfo? = null

            // Process challengers (equivalent to Object.keys(x.challenger).forEach)
            val challengerMap = schedule["challenger"] as? Map<*, *>
            challengerMap?.forEach { (key, value) ->
                val userId = key.toString()
                val challengerData = value as? Map<*, *> ?: emptyMap<String, Any?>()

                val challengerInfo = ChallengerInfo(
                    userId = userId,
                    isDecline = challengerData["isDecline"] as? Boolean ?: false,
                    status = challengerData["status"] as? Boolean ?: false,
                    totalTime = when (val time = challengerData["totalTime"]) {
                        is Number -> time.toInt()
                        else -> 0
                    },
                    totalRev = when (val rev = challengerData["totalRev"]) {
                        is Number -> rev.toDouble()
                        else -> 0.0
                    },
                    rank = when (val rank = challengerData["rank"]) {
                        is Number -> rank.toInt()
                        else -> 0
                    },
                    isDeline = challengerData["isDeline"] as? Boolean ?: false,
                    challengeStatus = challengerData["challengeStatus"] as? Int
                )

                sampleArr.add(challengerInfo)

                // Check if this is the current user
                if (userId == currentUserId) {
                    selfUserChallenge = challengerInfo
                }
            }

            // Determine status label
            selfUserChallenge?.let { userChallenge ->
                val statusLabel = if (userChallenge.challengeStatus != null) {
                    // Handle challengeStatus-based logic
                    when (userChallenge.challengeStatus) {
                        RLChallengeStatusType.Pending.value -> {
                            val lastDateTimestamp = when (val timestamp = schedule["dateOfChallenge"]) {
                                is Number -> timestamp.toLong()
                                is String -> timestamp.toLongOrNull() ?: 0L
                                else -> 0L
                            }
                            if (lastDateTimestamp < System.currentTimeMillis() / 1000) {
                                "Missed"
                            } else {
                                "Scheduled For"
                            }
                        }
                        RLChallengeStatusType.Accepted.value -> "Scheduled For"
                        RLChallengeStatusType.Completed.value -> "Completed"
                        RLChallengeStatusType.Declined.value -> "Declined"
                        RLChallengeStatusType.Missed.value -> "Missed"
                        RLChallengeStatusType.OwnerWinner.value -> "Won"
                        RLChallengeStatusType.OtherWinner.value -> "Lost"
                        RLChallengeStatusType.Ignored.value -> "Declined"
                        else -> "Scheduled For"
                    }
                } else {
                    //Handle legacy status logic
                    when {
                        userChallenge.isDecline -> "Declined"
                        userChallenge.status -> {
                            val lastDateTimestamp = when (val timestamp = schedule["dateOfChallenge"]) {
                                is Number -> timestamp.toLong()
                                is String -> timestamp.toLongOrNull() ?: 0L
                                else -> 0L
                            }

                            if (lastDateTimestamp < System.currentTimeMillis() / 1000) {
                                if (userChallenge.rank == 0) {
                                    "Missed"
                                } else {
                                    //Sort participants by totalRev (descending), then by totalTime (ascending)
                                    val sortedParticipants = sampleArr.sortedWith(compareBy<ChallengerInfo> { -it.totalRev }.thenBy { it.totalTime })

                                    if (sortedParticipants.isNotEmpty() && sortedParticipants[0].userId != currentUserId) {
                                        "Lost"
                                    } else {
                                        "Won"
                                    }
                                }
                            } else {
                                val challengeOwner = schedule["createdBy"] as? String
                                if (challengeOwner == currentUserId) {
                                    val otherParticipant = sampleArr.find { participant ->
                                        participant.userId != currentUserId
                                    }

                                    if (otherParticipant != null && !otherParticipant.isDecline) {
                                        "Scheduled For"
                                    } else {
                                        "Declined"
                                    }
                                } else {
                                    "Scheduled For"
                                }
                            }
                        }
                        else -> {
                            val lastDateTimestamp = when (val timestamp = schedule["dateOfChallenge"]) {
                                is Number -> timestamp.toLong()
                                is String -> timestamp.toLongOrNull() ?: 0L
                                else -> 0L
                            }

                            if (lastDateTimestamp < System.currentTimeMillis() / 1000) {
                                "Missed"
                            } else {
                                "Scheduled For"
                            }
                        }
                    }
                }

                mutableSchedule["statusLbl"] = statusLabel
            }

            //Process based on class type
            val isClass = schedule["isClass"] as? Boolean ?: false
            val isMindClass = schedule["isMindClass"] as? Boolean ?: false

            if (isClass) {
                if (isMindClass) {
                    getMindData(mutableSchedule) { item ->
                        if (item != null) {
                            scheduleKeyList.add(item)
                            updateListSetToList(scheduleKeyList)
                        }
                    }
                } else {
                    getBodyData(mutableSchedule) { item ->
                        if (item != null) {
                            scheduleKeyList.add(item)
                            updateListSetToList(scheduleKeyList)
                        }
                    }
                }
            }
        }
    }

    private fun getMindData(schedule: MutableMap<String, Any?>, onScheduleItemReady: (ScheduleItem?) -> Unit) {
        val videoKey = schedule["videoKey"] as? String ?: ""
        val createdBy = schedule["createdBy"] as? String ?: ""

        val scheduleJson = Gson().toJson(schedule)
        val scheduleData = Gson().fromJson(scheduleJson, RLScheduleData::class.java)

        RLDatabaseManagerRead().RLRevoolaVideosMindRead(videoKey) { data, error ->
            if (data != null) {
                val videoCardData = Gson().fromJson(Gson().toJson(data), RLFulllVideoModel::class.java)

                RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { userDataRaw, _ ->
                    if (userDataRaw != null) {
                        val userData = RLTools.parseUserData(userDataRaw)
                        if (userData != null) {
                            val item = ScheduleItem(
                                schedule = scheduleData,
                                videoItem = videoCardData,
                                organizer = userData.displayName,
                                organizerImage = userData.displayImage
                            )
                            onScheduleItemReady(item)  // return the item
                        } else {
                            onScheduleItemReady(null)
                        }
                    } else {
                        onScheduleItemReady(null)
                    }
                }
            } else {
                onScheduleItemReady(null)
            }
        }
    }
    private fun getBodyData(schedule: MutableMap<String, Any?>, onScheduleItemReady: (ScheduleItem?) -> Unit) {
        val videoKey = schedule["videoKey"] as? String ?: ""
        val createdBy = schedule["createdBy"] as? String ?: ""
        val scheduleJson = Gson().toJson(schedule)
        val scheduleData = Gson().fromJson(scheduleJson, RLScheduleData::class.java)
        RLDatabaseManagerRead().RLRevoolaVideosRead(videoKey) { data, error ->
            if (data != null) {
                val videoCardData = Gson().fromJson(Gson().toJson(data), RLFulllVideoModel::class.java)

                RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { userDataRaw, _ ->
                    if (userDataRaw != null) {
                        val userData = RLTools.parseUserData(userDataRaw)
                        if (userData != null) {
                            val item = ScheduleItem(
                                schedule = scheduleData,
                                videoItem = videoCardData,
                                organizer = userData.displayName,
                                organizerImage = userData.displayImage
                            )
                            onScheduleItemReady(item)  // return the item
                        } else {
                            onScheduleItemReady(null)
                        }
                    } else {
                        onScheduleItemReady(null)
                    }
                }
            } else {
                onScheduleItemReady(null)
            }
        }
    }

    private fun updateListSetToList(scheduleKeyList: MutableList<ScheduleItem>) {
        adapterScheduledClasses.updateList(scheduleKeyList)
        fragBinding.rvSchdualclasses.visibility=View.VISIBLE
        fragBinding.txtNoData.visibility=View.GONE
        RLBaseProgress.RLhideProgressDialog()
    }
    private fun noScheduleDataShow(value :String) {
        fragBinding.rvSchdualclasses.visibility=View.GONE
        fragBinding.txtNoData.visibility=View.VISIBLE
        RLBaseProgress.RLhideProgressDialog()
        RLTools.RlLogEPrint(TAG,"Error:- $value")
    }
}
