package com.example.myfirstapp.fragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.adapter.StartListAdapter
import com.example.myfirstapp.adapter.YourWayListAdapter
import com.example.myfirstapp.databinding.FragStartBinding
import com.example.myfirstapp.databinding.FragYoueWayBinding
import com.example.myfirstapp.utils.PrefManager


class FragYourWay : BaseFragment() {
    val TAG: String = FragYourWay::class.java.simpleName
    lateinit var fragBinding: FragYoueWayBinding

    private val binding by lazy {
        FragYoueWayBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_youe_way, container) as FragYoueWayBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragYourWay" )
        uisetup()
        return fragBinding.root
    }
    private fun uisetup() {
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        onBackPresAct(fragBinding.toolbar.ivBack)
        val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvYourway.layoutManager = linearLayoutManager
        val valueslist = arrayOf("Pilates","Ride","Run","Walk","Workout","Yoga")
        // Create an array of drawables
        val drawableArray = arrayOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.pilates),
            ContextCompat.getDrawable(requireContext(), R.drawable.ride),
            ContextCompat.getDrawable(requireContext(), R.drawable.run),
            ContextCompat.getDrawable(requireContext(), R.drawable.walk),
            ContextCompat.getDrawable(requireContext(), R.drawable.workout),
            ContextCompat.getDrawable(requireContext(), R.drawable.yoga))
        val adapter = YourWayListAdapter(activity,valueslist,drawableArray)

       // val data: List<String> =ArrayList<String>()
       // adapter.setList(valueslist)
        fragBinding.rvYourway.adapter = adapter
    }
}