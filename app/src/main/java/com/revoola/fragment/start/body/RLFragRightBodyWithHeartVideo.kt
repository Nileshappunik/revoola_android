package com.revoola.fragment.start.body

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
import com.revoola.firebaseModel.RLChallengeRiderBody
import com.revoola.fragment.start.adapter.RLStartClassAttendListAdapter
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLPrefManager
import kotlin.math.roundToInt

class RLFragRightBodyWithHeartVideo : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragRightBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragRightBodyHeartVideoBinding
    var userBasicdata: RLRevoolaUsersSettingsModel? = null
    private  var rankList = mutableListOf<RLChallengeRiderBody>()
    private var currentUserID=""

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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragRightBodyWithHeartVideo")
        currentUserID =  RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.current_user,"")
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

    private fun RLfetchLeaderBoardData(videoId:String) {
        val sessionList = mutableListOf<RLChallengeRiderBody>()
        RLDatabaseManagerRead().RLClassLeaderBoardDataRead(videoId) { result, error ->
            if (error != null) {
                // Handle the error case
               RLTools.RlLogEPrint(TAG, "Error fetching leaderboard data: $error")
            } else if (result is Map<*, *>) {
                // Handle the successful result
                for ((key, value) in result) {
                    if (key is String && value is RLChallengeRiderBody) {
                        sessionList.add(value)
                        rankList.add(value)
                    }
                }
                 val adapter = RLStartClassAttendListAdapter(sessionList,requireContext(),this)
                 fragBinding.recyclerList.adapter = adapter
            }
        }
    }

    fun RLUpdateSecond(
        sec: Int,
        heartRateNumber: Int,
        REVSec: Double,
        totalRev: Double,
        REVPer: Double,
        maxRevPercentage: Double,
        avgRevPercentage: Double
    ){
        rankingByRevSec(sec,heartRateNumber,REVSec,totalRev,REVPer,maxRevPercentage,avgRevPercentage)
    }

    override fun onItemClick(position: Int) {
        //click adapter
        fragBinding.inlayItemview.relayClick.visibility=View.VISIBLE
        fragBinding.rightsideview.visibility=View.GONE
    }

    fun rankingByRevSec(sec: Int,
                        heartRateNumber: Int,
                        REVSec: Double,
                        totalRev: Double,
                        REVPer: Double,
                        maxRevPercentage: Double,
                        avgRevPercentage: Double): List<Map<String, Any>>
    {

        val rankbyindex = mutableListOf<Map<String, Any>>()
        /*for (list in leaderBoardUserList) {
            val obj = mutableMapOf<String, Any>(
                "revSec" to revSecAt(list.arrRevSecond, sec),
                "displayName" to list.displayName,
                "displayImage" to list.displayImage,
                "flagName" to list.flagName,
                "flagImage" to list.flagImage,
                "totalRev" to sumAtN(list.arrRevSecond, sec),
                "revPercentage" to revSecAt(list.arrRevPercentage, sec),
                "maxRevPercentage" to revSecAt(list.arrMaxRevPercentage, sec),
                "avgRevPercentage" to revSecAt(list.arrAvgRevPercentage, sec),
                "hr" to revSecAt(list.arrHr, sec),
                "userId" to if (list.key == currentUserID)
                    "${currentUserID}_ghost"
                else list.key,
                "status" to 1,
                "location" to list.location,
                "isGhost" to true
            )

            rankbyindex.add(obj)
        }*/

        val obj = mutableMapOf<String, Any>(
            "revSec" to REVSec,
            "displayName" to (userBasicdata?.displayName?:""),
            "displayImage" to (userBasicdata?.displayImage?:""),
            "flagName" to "",
            "flagImage" to (userBasicdata?.flagImage?:""),
            "location" to "",
            "totalRev" to totalRev,
            "revPercentage" to REVPer,
            "maxRevPercentage" to maxRevPercentage,
            "avgRevPercentage" to avgRevPercentage,
            "userId" to currentUserID,
            "status" to 1,
            "isGhost" to false
        )
        rankbyindex.add(obj)

        val sortedRankByIndex = rankbyindex
            .sortedByDescending { it["totalRev"].toString().toDouble().roundToInt()}
            .mapIndexed { index, value ->
                value.toMutableMap().apply {
                    put("rank", index + 1)
                }
            }

        /*for (x in sortedRankByIndex) {
            leaderBoardRankObject[x["userId"]]?.let { previousRank ->
                x["status"] = when {
                    previousRank == x["rank"] -> 0 // same
                    previousRank < x["rank"] -> 1  // down
                    else -> 2                      // up
                }
            }
        }

        if (isRObj) {
            isRObj = false
            for (x in sortedRankByIndex) {
                leaderBoardRankObject[x["userId"]] = x["rank"] as Int
            }
        }

        rankingList = sortedRankByIndex*/
        println("--------rankingByRevSec-------01 $sortedRankByIndex")

        return sortedRankByIndex
    }

    private fun revSecAt(array: List<Number>?, n: Int): Number {
        return when {
            array == null -> 0
            array.size - 1 > n -> array[n]
            else -> 0
        }
    }

    private fun sumAtN(array: List<Number>?, n: Int): Number {
        var sum = 0.0
        if (array != null) {
            val limit = minOf(if (array.size - 1 > n) n else array.size, array.size)
            for (i in 0 until limit) {
                sum += array[i].toDouble()
            }
        }
        return sum
    }


}