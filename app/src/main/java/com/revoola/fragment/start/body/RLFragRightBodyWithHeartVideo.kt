package com.revoola.fragment.start.body

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragRightBodyHeartVideoBinding
import com.revoola.firebaseModel.RLLeaderBoardUser
import com.revoola.firebaseModel.RLChallengeRiderBody
import com.revoola.firebaseModel.RLRanking
import com.revoola.fragment.start.adapter.RLStartClassAttendListAdapter
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLPrefManager
import kotlin.math.roundToInt

class RLFragRightBodyWithHeartVideo : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragRightBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragRightBodyHeartVideoBinding
    var adapter : RLStartClassAttendListAdapter? = null
    var userBasicdata: RLRevoolaUsersSettingsModel? = null
    private var currentUserID=""

    private var leaderBoardUserList = mutableListOf<RLLeaderBoardUser>()
    private var leaderBoardRankObject = mutableMapOf<String, Int>()
    private var isRankObjectInitialized = true

    private val binding by lazy {
        RlFragRightBodyHeartVideoBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragRightBodyWithHeartVideo()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_right_body_heart_video, container) as RlFragRightBodyHeartVideoBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragRightBodyWithHeartVideo")
        currentUserID =  RLPrefManager.RLGetSomeStringValue(activity, RLPrefManager.current_user,"")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutMain
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userBasicdata=userData
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }
       // val VideoId=  requireArguments().getString("videoID","")
        val VideoId=  "20190611-0001-ride-floyd-robinson-intermediate-20"
        RLfetchLeaderBoardData(VideoId)
        fragBinding.inlayItemview.relayClick.setOnClickListener {
            fragBinding.inlayItemview.relayClick.visibility=View.GONE
            fragBinding.rightsideview.visibility=View.VISIBLE
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun RLfetchLeaderBoardData(videoId:String) {
        val sessionList = mutableListOf<RLRanking>()
        RLDatabaseManagerRead().RLClassLeaderBoardDataRead(videoId) { result, error ->
            if (error != null) {
                // Handle the error case
               RLTools.RlLogEPrint(TAG, "Error fetching leaderboard data: $error")
            } else if (result is Map<*, *>) {
                // Handle the successful result
                for ((key, value) in result) {
                    if (key is String && value is RLChallengeRiderBody) {

                        val objectleaterBoard= RLLeaderBoardUser(
                            key=key.toString(),
                            displayName=value.displayName,
                            displayImage=value.displayImage,
                            flagName=value.flagName,
                            flagImage=value.flagImage,
                            arrRevSecond=value.arrRevSecond,
                            arrRevPercentage=value.arrRevPercentage,
                            arrMaxRevPercentage=value.arrMaxRevPercentage,
                            arrAvgRevPercentage=value.arrAvgRevPercentage,
                            arrHr=value.arrHr,
                            location=value.location)
                        leaderBoardUserList.add(objectleaterBoard)

                      val rankindBoard=  RLRanking(
                            revSec = value.totalRev.toString().toDouble().roundToInt(),
                            displayName = value.displayName?:"",
                            displayImage =value.displayImage?:"",
                            flagName = value.flagName,
                            flagImage = value.flagImage?:"",
                            totalRev = value.totalRev.toString().toDouble().roundToInt(),
                            revPercentage = 0,
                            maxRevPercentage =0,
                            avgRevPercentage = 0,
                            hr =0,
                            userId = key,
                            status = 1,
                            location = value.location,
                            isGhost = false)
                        sessionList.add(rankindBoard)
                    }
                }
                adapter = RLStartClassAttendListAdapter(sessionList,requireContext(),this)
                 fragBinding.recyclerList.adapter = adapter
            }
        }
    }

    override fun onItemClick(position: Int) {
        //click adapter
        fragBinding.inlayItemview.relayClick.visibility=View.VISIBLE
        fragBinding.rightsideview.visibility=View.GONE
    }

    fun RLRankingByRevSec(sec: Int,revSec:Int,totalRev:Int,revPercentage:Int,maxRevPercentage:Int,avgRevPercentage:Int,hrNumber:Int) {
         var rankingList = mutableListOf<RLRanking>()
        if (leaderBoardUserList.isNotEmpty()) {
        val rankByIndex = leaderBoardUserList.map { user ->
            val isCurrentUser = user.key == currentUserID
            RLRanking(
                revSec = RLRevSecAt(user.arrRevSecond, sec),
                displayName = user.displayName,
                displayImage = user.displayImage,
                flagName = user.flagName,
                flagImage = user.flagImage,
                totalRev = RLSumAtN(listOf(user.arrRevSecond), sec),
                revPercentage = RLRevSecAt(user.arrRevPercentage, sec),
                maxRevPercentage = RLRevSecAt(user.arrMaxRevPercentage, sec),
                avgRevPercentage = RLRevSecAt(user.arrAvgRevPercentage, sec),
                hr = RLRevSecAtt(user.arrHr!!, sec),
                userId = if (isCurrentUser) "${currentUserID}_ghost" else user.key,
                status = 1,
                location = user.location,
                isGhost = isCurrentUser
            )
        }.toMutableList()

        rankByIndex.add(
            RLRanking(
                revSec = revSec,
                displayName = userBasicdata?.displayName ?: "",
                displayImage = userBasicdata?.displayImage ?: "",
                flagName = "",
                flagImage = userBasicdata?.flagImage ?: "",
                totalRev = totalRev,
                revPercentage = revPercentage,
                maxRevPercentage = maxRevPercentage,
                avgRevPercentage = avgRevPercentage,
                hr = hrNumber,
                userId = currentUserID,
                status = 1,
                location = "",
                isGhost = false
            )
        )

        rankByIndex.sortByDescending { it.totalRev }
        rankByIndex.forEachIndexed { index, ranking ->
            ranking.rank = index + 1
            val previousRank = leaderBoardRankObject[ranking.userId]
            ranking.status = when {
                previousRank == null -> 1
                previousRank == ranking.rank -> 0
                previousRank < ranking.rank -> 1
                else -> 2
            }
            if (isRankObjectInitialized) {
                leaderBoardRankObject[ranking.userId] = ranking.rank
            }
        }
        isRankObjectInitialized = false

        rankingList = rankByIndex
        if (adapter != null) {
            adapter!!.RLSetList(rankingList)
        }
    }
    }

    private fun RLRevSecAt(array: List<Any?>, n: Int): Int {
        return when {
            array != null && array.size - 1 > n -> array[n].toString().toDouble().roundToInt()
            else -> 0
        }
    }

    private fun RLRevSecAtt(array: List<Int?>, n: Int): Int {
        return when {
            array != null && array.size - 1 > n -> array[n].toString().toDouble().roundToInt()
            else -> 0
        }
    }

    private fun RLSumAtN(array: List<Any>, n: Int): Int {
        var sum = 0
        if (array.isNotEmpty()) { // Check if array is not empty
            val limit = if (n < array.size) n else array.size
            for (i in 0 until limit) {
                val element = array[i]
                val value = when (element) {
                    is Number -> element.toDouble() // If it's a number, convert to Double
                    is String -> element.toDoubleOrNull() ?: 0.0 // Try parsing as Double, default to 0.0 if invalid
                    else -> 0.0 // Default to 0.0 for unsupported types
                }
                sum += value.roundToInt() // Add rounded value to sum
            }
        }
        return sum
    }
}