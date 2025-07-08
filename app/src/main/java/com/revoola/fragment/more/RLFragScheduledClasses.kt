package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.adapter.RLScheduledClassesListAdapter
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databinding.RlFragScheduledClassesBinding
import com.revoola.model.RLFulllVideoModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RLFragScheduledClasses : RLBaseFragment() {
    val TAG: String = RLFragScheduledClasses::class.java.simpleName
    lateinit var fragBinding: RlFragScheduledClassesBinding
    // Collection to store all scheduled request data
    var currentUserId = ""

    private val binding by lazy {
        RlFragScheduledClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_scheduled_classes, container) as RlFragScheduledClassesBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragScheduledClasses" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        currentUserId = RLAuthManager().RlgetCurrentUser()?.uid ?:""
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvSchdualclasses.layoutManager = linearLayoutManager
      //  val adapterScheduledClasses = RLScheduledClassesListAdapter(activity,emptyList())
     //   fragBinding.rvSchdualclasses.adapter = adapterScheduledClasses
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

    // Main sorting function equivalent to JavaScript getSortScheduleList()
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
                    isDeline = challengerData["isDeline"] as? Boolean ?: false
                )

                sampleArr.add(challengerInfo)

                // Check if this is the current user
                if (userId == currentUserId) {
                    selfUserChallenge = challengerInfo
                }
            }

            // Determine status label
            selfUserChallenge?.let { userChallenge ->
                val statusLabel = when {
                    userChallenge.isDecline -> "Declined"
                    userChallenge.status -> {
                        if (sampleArr.isNotEmpty() && sampleArr[0].userId != currentUserId) {
                            "Lost"
                        } else {
                            "Win"
                        }
                    }
                    else -> "Scheduled For"
                }
                mutableSchedule["statusLbl"] = statusLabel
            }

            // Process based on class type
            val isClass = schedule["isClass"] as? Boolean ?: false
            val isMindClass = schedule["isMindClass"] as? Boolean ?: false

            if (isClass) {
                if (isMindClass) {
                    getMindData(mutableSchedule, scheduleKeyList)
                } else {
                    getBodyData(mutableSchedule, scheduleKeyList)
                }
            } else {
                scheduleKeyList.add(
                    ScheduleItem(
                        schedule = mutableSchedule,
                        videoObj = emptyMap()
                    )
                )
            }
        }
        if (scheduleKeyList.isNotEmpty()){
            updateListSetToList(scheduleKeyList)
        }else{
            noScheduleDataShow("No schedule Data")
        }

    }

    private fun noScheduleDataShow(value :String) {
        fragBinding.rvSchdualclasses.visibility=View.GONE
        fragBinding.txtNoData.visibility=View.VISIBLE
        RLBaseProgress.RLhideProgressDialog()
        RLTools.RlLogEPrint(TAG,"Error:- $value")
    }

    private fun updateListSetToList(scheduleKeyList: MutableList<ScheduleItem>) {
        val scheduledList = mutableListOf<ScheduleMediaItem>()
        var completedRequests = 0
        val totalRequests = scheduleKeyList.size
        scheduleKeyList.forEach { cardData->
            val videoId = cardData.schedule.get("videoKey").toString()
            RLTools.RlLogDPrint(TAG,"videoId: $videoId")
            val createdBy = cardData.schedule.get("createdBy").toString()
            val isMindClass = cardData.schedule.get("isMindClass").toString()
            if (isMindClass.equals("false")){
                RLDatabaseManagerRead().RLRevoolaVideosRead(videoId) { data, error ->
                    if (data!=null){
                        val videoCardData = Gson().fromJson(Gson().toJson(data), RLFulllVideoModel::class.java)
                        RLTools.RlLogDPrint(TAG,"videoCardData: $videoCardData")
                        RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { data, error ->
                            if (data!=null) {
                                val userData = RLTools.parseUserData(data)
                                if(userData!=null){
                                    val organizerName = userData.displayName
                                    val organizerImage = userData.displayImage
                                    RLTools.RlLogDPrint(TAG,"organizerName: $organizerName")
                                    RLTools.RlLogDPrint(TAG,"videoCardData: $videoCardData")
                                    // Add all three items to the array
                                    scheduledList.add(ScheduleMediaItem.Combined(cardData, videoCardData, organizerName,organizerImage))
                                }
                            }else{
                                RLBaseProgress.RLhideProgressDialog()
                                RLTools.RlLogEPrint(TAG,"Error Fetch User Data: ${error?.message}")
                            }
                            completedRequests++
                            if (completedRequests == totalRequests) {
                                RLTools.RlLogDPrint(TAG,"scheduledList: ${scheduledList.size}")
                                completedRequests = 0
                                val adapterScheduledClasses = RLScheduledClassesListAdapter(activity,scheduledList)
                                fragBinding.rvSchdualclasses.adapter = adapterScheduledClasses
                                fragBinding.rvSchdualclasses.visibility=View.VISIBLE
                                fragBinding.txtNoData.visibility=View.GONE
                                RLBaseProgress.RLhideProgressDialog()
                            }
                        }

                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Video Data: ${error?.message}")
                        RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Video Data: ${Gson().toJson(cardData)}")
                    }
                }
            }else{
                RLDatabaseManagerRead().RLRevoolaVideosMindRead(videoId){ data, error ->
                    if (data!=null){
                        val videoCardData = Gson().fromJson(Gson().toJson(data), RLFulllVideoModel::class.java)
                        RLTools.RlLogDPrint(TAG,"videoCardData: $videoCardData")
                        RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { data, error ->
                            if (data!=null) {
                                val userData = RLTools.parseUserData(data)
                                if(userData!=null){
                                    val organizerName = userData.displayName
                                    val organizerImage = userData.displayImage
                                    RLTools.RlLogDPrint(TAG,"organizerName: $organizerName")
                                    RLTools.RlLogDPrint(TAG,"videoCardData: $videoCardData")
                                    // Add all three items to the array
                                    scheduledList.add(ScheduleMediaItem.Combined(cardData, videoCardData, organizerName,organizerImage))
                                }
                            }else{
                                RLBaseProgress.RLhideProgressDialog()
                                RLTools.RlLogEPrint(TAG,"Error Fetch User Data: ${error?.message}")
                            }
                            completedRequests++
                            if (completedRequests == totalRequests) {
                                RLTools.RlLogDPrint(TAG,"scheduledList: ${scheduledList.size}")
                                completedRequests = 0
                                val adapterScheduledClasses = RLScheduledClassesListAdapter(activity,scheduledList)
                                fragBinding.rvSchdualclasses.adapter = adapterScheduledClasses
                                fragBinding.rvSchdualclasses.visibility=View.VISIBLE
                                fragBinding.txtNoData.visibility=View.GONE
                                RLBaseProgress.RLhideProgressDialog()
                            }
                        }

                    }else{
                        RLBaseProgress.RLhideProgressDialog()
                        RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Video Data: ${error?.message}")
                        RLTools.RlLogEPrint(TAG,"Error Fetch Scheduled Video Data: ${Gson().toJson(cardData)}")
                    }

                }
            }

        }
    }

    // Function to handle mind class data
    private fun getMindData(schedule: MutableMap<String, Any?>, scheduleKeyList: MutableList<ScheduleItem>) {
        // Implement your mind data logic here
        // This would be equivalent to your JavaScript getMindData(x) function

        val videoKey = schedule["videoKey"] as? String ?: ""

        // Example mind data processing
        val mindVideoObj = mapOf(
            "type" to "mind",
            "videoKey" to videoKey,
            "isMindClass" to true
            // Add other mind-specific properties
        )

        scheduleKeyList.add(
            ScheduleItem(
                schedule = schedule,
                videoObj = mindVideoObj
            )
        )

        RLTools.RlLogDPrint(TAG, "Processed Mind Class: ${schedule["challengeName"]}")
    }

    // Function to handle body class data
    private fun getBodyData(schedule: MutableMap<String, Any?>, scheduleKeyList: MutableList<ScheduleItem>) {
        // Implement your body data logic here
        // This would be equivalent to your JavaScript getBodyData(x) function

        val videoKey = schedule["videoKey"] as? String ?: ""

        // Example body data processing
        val bodyVideoObj = mapOf(
            "type" to "body",
            "videoKey" to videoKey,
            "isMindClass" to false
            // Add other body-specific properties
        )

        scheduleKeyList.add(
            ScheduleItem(
                schedule = schedule,
                videoObj = bodyVideoObj
            )
        )

        RLTools.RlLogDPrint(TAG, "Processed Body Class: ${schedule["challengeName"]}")
    }

}
// Data class for better type safety (optional but recommended)
data class ScheduleItem(
    val schedule: MutableMap<String, Any?>,
    val videoObj: Map<String, Any?> = emptyMap()
)


sealed class ScheduleMediaItem() {
    data class Combined(val scheduleItem: ScheduleItem, val videoItem: RLFulllVideoModel, val organizer: String,val organizerImage:String) : ScheduleMediaItem()
}

data class ChallengerInfo(
    val userId: String,
    val isDecline: Boolean = false,
    val status: Boolean = false,
    val totalTime: Int = 0,
    val totalRev: Double = 0.0,
    val rank: Int = 0,
    val isDeline: Boolean = false
)