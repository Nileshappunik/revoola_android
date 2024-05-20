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
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.adapter.StartListAdapter
import com.example.myfirstapp.databinding.FragClassesBinding
import com.example.myfirstapp.databinding.FragStartBinding
import com.example.myfirstapp.utils.PrefManager


class FragClasses : BaseFragment() {
    val TAG: String = FragClasses::class.java.simpleName
    lateinit var fragBinding: FragClassesBinding

    private val binding by lazy {
        FragClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_classes, container) as FragClassesBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragClasses" )
        uisetup()
        return fragBinding.root
    }
    private fun uisetup() {
        fragBinding.toolbar.tvTitle.visibility=View.GONE
        onBackPresAct(fragBinding.toolbar.ivBack)

        fragBinding.layYourmind.imgClass.visibility=View.VISIBLE
        fragBinding.layYourmind.imgClass.setImageResource(R.drawable.ic_mind_read)
        fragBinding.layYourmind.imgFull.setImageResource(R.drawable.ride)
        fragBinding.layYourmind.txtName.visibility=View.GONE
        fragBinding.layYourmind.txtClassName.setText(R.string.foryourmind)
        fragBinding.layYourmind.txtClassName.visibility=View.VISIBLE

        fragBinding.layYourbody.imgClass.visibility=View.VISIBLE
        fragBinding.layYourbody.imgClass.setImageResource(R.drawable.ic_heart)
        fragBinding.layYourmind.imgFull.setImageResource(R.drawable.pilates)
        fragBinding.layYourbody.txtName.visibility=View.GONE
        fragBinding.layYourbody.txtClassName.setText(R.string.foryourbody)
        fragBinding.layYourbody.txtClassName.visibility=View.VISIBLE

        fragBinding.layYourmind.imgFull.setOnClickListener {
            (context as MainActivity).showbottombarcolorwhite()
            (context as MainActivity).bottombarcolorwhite()
            (context as MainActivity).loadFrag(FragMindClasses(), TAG, true, FragMindClasses::class.java.simpleName, false)

        }

    }
}