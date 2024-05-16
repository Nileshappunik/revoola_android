package com.example.myfirstapp.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.DialogHelpChallengesBinding
import com.example.myfirstapp.databinding.DialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.FragChooseYourSensorBinding
import com.example.myfirstapp.databinding.FragSensorProgressBinding
import com.example.myfirstapp.databinding.FragSessionCompleteBinding
import com.example.myfirstapp.databinding.FragSetYourGoalBinding

import com.example.myfirstapp.utils.PrefManager
import java.util.Calendar


class FragSessionComplete : BaseFragment(){
    val TAG: String = FragSessionComplete::class.java.simpleName
    lateinit var fragBinding: FragSessionCompleteBinding

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = FragSessionComplete()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        FragSessionCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_session_complete, container) as FragSessionCompleteBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragSessionComplete" )
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        //"Pilates","Ride","Run","Walk","Workout","Yoga" ic_pace ic_speeed
        val yourWayType = requireArguments().getString("YourWayType").toString().trim()
        fragBinding.switchCompat.setOnCheckedChangeListener { _, isChecked ->
            // Handle checked change
        }
        fragBinding.layPrivacy.setOnClickListener {
            val titletxt:String=fragBinding.tvsharetitle.text.toString()

            if (titletxt.equals("Friends")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightgreen))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyeveryone)
                fragBinding.tvsharetitle.setText(R.string.anyone)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.green))

            }else if (titletxt.equals("Anyone")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.redlight))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyprivate)
                fragBinding.tvsharetitle.setText(R.string.privatetx)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.reddark))

            }else if (titletxt.equals("Private")){
                fragBinding.layPrivacy.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.lightnavyblue))
                fragBinding.imgShareimage.setImageResource(R.drawable.ic_privacyfriends)
                fragBinding.tvsharetitle.setText(R.string.friendstx)
                fragBinding.tvsharetitle.setTextColor(resources.getColor(R.color.navyblue))
            }

        }


    }
}