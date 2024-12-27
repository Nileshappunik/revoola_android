package com.revoola.fragment.start.body

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlFragRightBodyHeartVideoBinding
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLChallengeRiderBody
import com.revoola.fragment.start.adapter.RLStartClassAttendListAdapter
import com.revoola.interfaceall.RLItemClickListener
import com.revoola.utils.RLPrefManager


class RLFragRightBodyWithHeartVideo : RLBaseFragment(), RLItemClickListener {
    val TAG: String = RLFragRightBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragRightBodyHeartVideoBinding

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
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragRightBodyWithHeartVideo" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.recyclerList.layoutManager = linearLayoutMain
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
                    }
                }
                 val adapter = RLStartClassAttendListAdapter(sessionList,requireContext(),this)
                 fragBinding.recyclerList.adapter = adapter
            }
        }
    }

    override fun onItemClick(position: Int) {
        //click adapter
        fragBinding.inlayItemview.relayClick.visibility=View.VISIBLE
        fragBinding.rightsideview.visibility=View.GONE
    }

}