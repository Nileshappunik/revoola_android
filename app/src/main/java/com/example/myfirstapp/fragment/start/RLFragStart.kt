package com.example.myfirstapp.fragment.start

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
import com.example.myfirstapp.fragment.start.adapter.RLStartListAdapter
import com.example.myfirstapp.databinding.RlFragStartBinding
import com.example.myfirstapp.utils.RLPrefManager


class RLFragStart : RLBaseFragment() {
    val TAG: String = RLFragStart::class.java.simpleName
    lateinit var fragBinding: RlFragStartBinding

    private val binding by lazy {
        RlFragStartBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_start, container) as RlFragStartBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvStart.layoutManager = linearLayoutManager
        val valueslist = arrayOf("Classes", "Your Way", "Challenges")
        // Create an array of drawables
        val drawableArray = arrayOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.classes),
            ContextCompat.getDrawable(requireContext(), R.drawable.yourway),
            ContextCompat.getDrawable(requireContext(), R.drawable.challenges_start))
        val adapter = RLStartListAdapter(activity,valueslist,drawableArray)

       // val data: List<String> =ArrayList<String>()
       // adapter.setList(valueslist)
        fragBinding.rvStart.adapter = adapter
    }
}