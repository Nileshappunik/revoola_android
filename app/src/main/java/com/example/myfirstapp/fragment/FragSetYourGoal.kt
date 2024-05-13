package com.example.myfirstapp.fragment

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.DialogHelpChallengesBinding
import com.example.myfirstapp.databinding.DialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.FragSetYourGoalBinding

import com.example.myfirstapp.utils.PrefManager


class FragSetYourGoal : BaseFragment() {
    val TAG: String = FragSetYourGoal::class.java.simpleName
    lateinit var fragBinding: FragSetYourGoalBinding

    private val binding by lazy {
        FragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_set_your_goal, container) as FragSetYourGoalBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragSetYourGoal" )

        fragBinding.toolbar.tvTitle.visibility=View.GONE
        fragBinding.toolbar.ivlogoapp.visibility=View.VISIBLE
        fragBinding.toolbar.ivlogoapp.setImageResource(R.drawable.ic_challenge_flag)

        fragBinding.toolbar.ivNotification.visibility=View.VISIBLE
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_circle)
        fragBinding.toolbar.ivNotification.setOnClickListener {
            showHelpDialog()
        }
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {
        onBackPresAct(fragBinding.toolbar.ivBack)
    }

    fun showHelpDialog() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: DialogHelpSetyourgoalBinding=DialogHelpSetyourgoalBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)


        val window: Window = dialog.getWindow()!!
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity=Gravity.RIGHT or Gravity.TOP
        window.attributes = lp



        dialogMainBinding.laySartdate.txtHeader.setText(R.string.pleaseenterstartdate)
        dialogMainBinding.laySartdate.txtHeaderDescription.setText(R.string.selecttosetthedatyouwantstart)
        dialogMainBinding.laySartdate.imgHelpChallenges.setImageResource(R.drawable.ic_calendar_today)

        dialogMainBinding.layEnddate.txtHeader.setText(R.string.pleaseenterenddate)
        dialogMainBinding.layEnddate.txtHeaderDescription.setText(R.string.selecttosetthedayuoyend)
        dialogMainBinding.layEnddate.imgHelpChallenges.setImageResource(R.drawable.ic_calendar_today)

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }

}