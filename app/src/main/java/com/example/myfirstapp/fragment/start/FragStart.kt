package com.example.myfirstapp.fragment.start

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
import com.example.myfirstapp.databinding.FragStartBinding
import com.example.myfirstapp.utils.PrefManager


class FragStart : BaseFragment() {
    val TAG: String = FragStart::class.java.simpleName
    lateinit var fragBinding: FragStartBinding

    private val binding by lazy {
        FragStartBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_start, container) as FragStartBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragStart" )
        uisetup()
        return fragBinding.root
    }
    private fun uisetup() {
        val linearLayoutManager = GridLayoutManager(activity, 2)
        fragBinding.rvStart.layoutManager = linearLayoutManager
        val valueslist = arrayOf("Classes", "Your Way", "Challenges")
        // Create an array of drawables
        val drawableArray = arrayOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.classes),
            ContextCompat.getDrawable(requireContext(), R.drawable.yourway),
            ContextCompat.getDrawable(requireContext(), R.drawable.wellcome))
        val adapter = StartListAdapter(activity,valueslist,drawableArray)

       // val data: List<String> =ArrayList<String>()
       // adapter.setList(valueslist)
        fragBinding.rvStart.adapter = adapter
    }
}