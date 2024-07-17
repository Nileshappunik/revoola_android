package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragChalengesCalenderBinding
import com.example.myfirstapp.databinding.RlFragChalengesTypeBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfChallenges
import com.example.myfirstapp.enumclass.RLTypeOfMetrics
import com.example.myfirstapp.fragment.feed.adapter.RLFeedSessionSummryListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLCalendarAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengesListAdapter
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class RLFragChalengesCalender : RLBaseFragment() {
    val TAG: String = RLFragChalengesCalender::class.java.simpleName
    lateinit var fragBinding: RlFragChalengesCalenderBinding
    private lateinit var calendar: Calendar

    private val binding by lazy {
        RlFragChalengesCalenderBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChalengesCalender()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_calender, container) as RlFragChalengesCalenderBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesCalender" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup(){
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        if (challengeType.equals("Steps")){
            fragBinding.tvTitle.setText(R.string.stepchallenge)
        } else if (challengeType.equals("Effort")){
            fragBinding.tvTitle.setText(R.string.effortchallenge)
        }else if (challengeType.equals("Calories")){
            fragBinding.tvTitle.setText(R.string.calorieschallenge)
        }else if (challengeType.equals("Distance")){
            fragBinding.tvTitle.setText(R.string.distancechallenge)
        }else if (challengeType.equals("Climbed")){
            fragBinding.tvTitle.setText(R.string.climbedchallenge)
        }else if (challengeType.equals("Duration")){
            fragBinding.tvTitle.setText(R.string.durationchallenge)
        }

        if (calenderType.equals("Daily")){
            fragBinding.tvpassupdate.setText(R.string.dailychallenge)
        } else if (calenderType.equals("Weekly")){
            fragBinding.tvpassupdate.setText(R.string.weeklychallenge)
        }else if (calenderType.equals("Monthly")){
            fragBinding.tvpassupdate.setText(R.string.monthlychallenge)
        }else if (calenderType.equals("Custom")){
            fragBinding.tvpassupdate.setText(R.string.customchallenge)
        }
        fragBinding.btnNext.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            bundle.putString("CalenderType",calenderType )
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            //(context as RLMainActivityRL).RLloadFrag(RLFragChalengesCalender().newInstance(bundle), TAG, true,null, false)
        }
        calendar = Calendar.getInstance()
        setupCalendar()
        fragBinding.previousMonthButton.setOnClickListener {
           RLcommonToast("previousMonthButton")
            calendar.add(Calendar.MONTH, -1)
            setupCalendar()
        }

        fragBinding.nextMonthButton.setOnClickListener {
            RLcommonToast("nextMonthButton")
             calendar.add(Calendar.MONTH, 1)
            setupCalendar()
        }
    }
    fun RLshowHelpDialog() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpSetyourgoalBinding = RlDialogHelpSetyourgoalBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.hide()
        }

        dialogMainBinding.laySartdate.txtHeader.setText(R.string.pleaseenterstartdate)
        dialogMainBinding.laySartdate.txtHeaderDescription.setText(R.string.selecttosetthedatyouwantstart)
        dialogMainBinding.laySartdate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialogMainBinding.layEnddate.txtHeader.setText(R.string.pleaseenterenddate)
        dialogMainBinding.layEnddate.txtHeaderDescription.setText(R.string.selecttosetthedayuoyend)
        dialogMainBinding.layEnddate.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialog.show()

    }

    private fun setupCalendar() {
        // Generate dates for the current month
        val dates = generateDatesForMonth(calendar)

        val adapter = RLCalendarAdapter(requireContext(), dates, calendar) { date ->
            // Handle date selection
            println("Selected date: $date")
        }
        fragBinding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerView.adapter = adapter
        updateMonthYearTextView()
    }
    private fun updateMonthYearTextView() {
        val dateFormat = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
        val monthYear = dateFormat.format(calendar.time)
        fragBinding.monthYearTextView.text = monthYear
    }
    private fun generateDatesForMonth(calendar: Calendar): List<Date> {
        val dates = mutableListOf<Date>()
        val currentMonth = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        val daysBefore = if (firstDayOfMonth == Calendar.SUNDAY) 6 else firstDayOfMonth - Calendar.MONDAY

        // Add days from the previous month to fill the first week
        calendar.add(Calendar.DAY_OF_MONTH, -daysBefore)
        while (calendar.get(Calendar.MONTH) != currentMonth) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Add days of the current month
        while (calendar.get(Calendar.MONTH) == currentMonth) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Add days from the next month to fill the remaining weeks
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Reset calendar to the first day of the current month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        return dates
    }

}