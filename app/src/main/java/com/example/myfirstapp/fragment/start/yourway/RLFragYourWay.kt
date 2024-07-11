package com.example.myfirstapp.fragment.start.yourway

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.start.adapter.RLYourWayListAdapter
import com.example.myfirstapp.databinding.RlFragYoueWayBinding
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager


class RLFragYourWay : RLBaseFragment() {
    val TAG: String = RLFragYourWay::class.java.simpleName
    lateinit var fragBinding: RlFragYoueWayBinding

    private val binding by lazy {
        RlFragYoueWayBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_youe_way, container) as RlFragYoueWayBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourWay" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        RLonBackPresAct(fragBinding.ivBack)
        val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvYourway.layoutManager = linearLayoutManager

        val dataList:List<Pair<String, String>> = listOf("Pilates" to RLConstants.PILATESIMAGE,
            "Ride" to RLConstants.RIDEIMAGE,
            "Run" to RLConstants.RUNIMAGE,
            "Walk" to RLConstants.WALKIMAGE,
            "Workout" to RLConstants.WORKOUTIMAGE,
            "Yoga" to RLConstants.YOGAIMAGE)


        val valueslist = arrayOf("Pilates","Ride","Run","Walk","Workout","Yoga")
        // Create an array of drawables
        val drawableArray = arrayOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.pilates),
            ContextCompat.getDrawable(requireContext(), R.drawable.ride),
            ContextCompat.getDrawable(requireContext(), R.drawable.run),
            ContextCompat.getDrawable(requireContext(), R.drawable.walk),
            ContextCompat.getDrawable(requireContext(), R.drawable.workout),
            ContextCompat.getDrawable(requireContext(), R.drawable.yoga))
        val adapter = RLYourWayListAdapter(activity,dataList)

       // val data: List<String> =ArrayList<String>()
       // adapter.setList(valueslist)
        fragBinding.rvYourway.adapter = adapter
    }
}