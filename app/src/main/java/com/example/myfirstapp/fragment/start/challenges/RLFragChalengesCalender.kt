package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlDialogHelpStartBinding
import com.example.myfirstapp.databinding.RlFragChalengesCalenderBinding
import com.example.myfirstapp.enumclass.RLDateType
import com.example.myfirstapp.fragment.start.RLStartHelpModel
import com.example.myfirstapp.fragment.start.adapter.RLHelpListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLCalenderListAdapter
import com.example.myfirstapp.fragment.start.challenges.adapter.RLMonthlyCalenderListAdapter
import com.example.myfirstapp.fragment.start.challenges.model.RLDateInfoModel
import com.example.myfirstapp.fragment.start.challenges.model.RLMonthInfoModel
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RLFragChalengesCalender : RLBaseFragment() {
    val TAG: String = RLFragChalengesCalender::class.java.simpleName
    lateinit var fragBinding: RlFragChalengesCalenderBinding
    private lateinit var calendarTo: Calendar
    private lateinit var calendarFrom: Calendar
    private var fromDate:String =""
    private var toDate:String =""
    private var currentToYear:Int =0
    private  var currentFromYear:Int =0
    private var dayBefore:String ="test"
    private var currentDateFrom = Calendar.getInstance().time

    private val binding by lazy {
        RlFragChalengesCalenderBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChalengesCalender()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_calender, container) as RlFragChalengesCalenderBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesCalender" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup(){
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        RLHelpHideShowSet(true, fragBinding.inlayTop.ivhelp, RLPrefManager.start_help_content)
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        val calenderType = requireArguments().getString("CalenderType").toString().trim()
        if (challengeType.equals("Steps")){
            fragBinding.inlayTop.ivTitle.setText(R.string.stepchallenge)
        } else if (challengeType.equals("Effort")){
            fragBinding.inlayTop.ivTitle.setText(R.string.effortchallenge)
        }else if (challengeType.equals("Calories")){
            fragBinding.inlayTop.ivTitle.setText(R.string.calorieschallenge)
        }else if (challengeType.equals("Distance")){
            fragBinding.inlayTop.ivTitle.setText(R.string.distancechallenge)
        }else if (challengeType.equals("Climbed")){
            fragBinding.inlayTop.ivTitle.setText(R.string.climbedchallenge)
        }else if (challengeType.equals("Duration")){
            fragBinding.inlayTop.ivTitle.setText(R.string.durationchallenge)
        }
        calendarFrom = Calendar.getInstance()
        calendarTo = Calendar.getInstance()
        if (calenderType.equals("Daily")){
            RLDailyCalenderShow()
            fragBinding.inlayTop.ivDescription.setText(R.string.dailychallenge)
        } else if (calenderType.equals("Weekly")){
            RLWeeklyCalenderShow()
            fragBinding.inlayTop.ivDescription.setText(R.string.weeklychallenge)
        }else if (calenderType.equals("Monthly")){
            RLMonthlyCalenderShow()
            fragBinding.inlayTop.ivDescription.setText(R.string.monthlychallenge)
        }else if (calenderType.equals("Custom")){
            RLDailyCalenderShow()
            fragBinding.inlayTop.ivDescription.setText(R.string.customchallenge)
        }

        fragBinding.btnNext.setOnClickListener {
           if (fromDate.isNotEmpty() && toDate.isNotEmpty()){
               val bundle: Bundle = Bundle()
               bundle.putString("ChallengeType",challengeType)
               bundle.putString("CalenderType",calenderType)
               (context as RLMainActivityRL).RLloadFrag(RLFragChallengesFor().newInstance(bundle), TAG, true,null, false)
           }else{
               RLshowAlertDialog("Please select Valid details")
           }
        }

    }
    private fun RLDailyCalenderShow(){

        RLsetupCalendarTO()
        RLsetupCalendarFrom()
        fragBinding.previousMonthButton.setOnClickListener {
            calendarTo.add(Calendar.MONTH, -1)
            RLsetupCalendarTO()
        }

        fragBinding.nextMonthButton.setOnClickListener {
            calendarTo.add(Calendar.MONTH, 1)
            RLsetupCalendarTO()
        }
        fragBinding.previousMonthButtonFrom.setOnClickListener {
            calendarFrom.add(Calendar.MONTH, -1)
            RLsetupCalendarFrom()
        }

        fragBinding.nextMonthButtonFrom.setOnClickListener {
            calendarFrom.add(Calendar.MONTH, 1)
            RLsetupCalendarFrom()
        }
    }
    private fun RLWeeklyCalenderShow(){
        RLsetupCalendarWeeklyFrom()
       // RLsetupCalendarWeeklyTO()
        RLsetupCalendarFirstTimeWeeklyTO()
        fragBinding.previousMonthButtonFrom.setOnClickListener {
            calendarFrom.add(Calendar.MONTH, -1)
            RLsetupCalendarWeeklyFrom()
        }
        fragBinding.nextMonthButtonFrom.setOnClickListener {
            calendarFrom.add(Calendar.MONTH, 1)
            RLsetupCalendarWeeklyFrom()
        }
        fragBinding.previousMonthButton.setOnClickListener {
            calendarTo.add(Calendar.MONTH, -1)
            RLsetupCalendarWeeklyTO()
        }

        fragBinding.nextMonthButton.setOnClickListener {
            calendarTo.add(Calendar.MONTH, 1)
            RLsetupCalendarWeeklyTO()
        }
    }
    private fun RLMonthlyCalenderShow(){
         currentToYear=calendarTo.get(Calendar.YEAR)
         currentFromYear=calendarFrom.get(Calendar.YEAR)
        RLsetupCalendarMonthlyFrom(currentFromYear)
        RLsetupCalendarMonthlyTO(currentToYear)
        fragBinding.inlayWeek.visibility=View.GONE
        fragBinding.inlayWeekFrom.visibility=View.GONE
        fragBinding.previousMonthButtonFrom.setOnClickListener {
            currentFromYear=currentFromYear-1
            RLsetupCalendarMonthlyFrom(currentFromYear)
        }
        fragBinding.nextMonthButtonFrom.setOnClickListener {
            currentFromYear=currentFromYear+1
            RLsetupCalendarMonthlyFrom(currentFromYear)
        }
        fragBinding.previousMonthButton.setOnClickListener {
            currentToYear= currentToYear-1
            RLsetupCalendarMonthlyTO(currentToYear)
        }

        fragBinding.nextMonthButton.setOnClickListener {
            currentToYear= currentToYear+1
            RLsetupCalendarMonthlyTO(currentToYear)
        }
    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpStartBinding = RlDialogHelpStartBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val linearLayoutMain = LinearLayoutManager(activity)
        dialogMainBinding.ivRecyclerview.layoutManager = linearLayoutMain

        val jsonString= RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.challenge_selectTarget,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun RLsetupCalendarTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarTo)

        val adapter = RLCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            toDate=date.toString()
            if (fromDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    private fun RLsetupCalendarWeeklyTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonthWeekly(calendarTo)

        val adapter = RLCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            toDate=date.toString()
            if (fromDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    private fun RLsetupCalendarFirstTimeWeeklyTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarTo)

        val adapter = RLCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            /*toDate=date.toString()
            if (fromDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }*/
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    private fun RLsetupCalendarFrom() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarFrom)

        val adapter = RLCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            dayBefore = date?.let { getDayBefore(it) } ?: "null"
            fromDate=date.toString()
             currentDateFrom = date
            RLsetupCalendarTO()
            if (toDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextViewFrom,calendarFrom)
    }
    private fun RLsetupCalendarWeeklyFrom() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarFrom)

        val adapter = RLCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            dayBefore = date?.let { getDayBefore(it) } ?: "null"
            currentDateFrom = date
            RLsetupCalendarWeeklyTO()
            fromDate=date.toString()
            if (toDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextViewFrom,calendarFrom)
    }
    private fun RLsetupCalendarMonthlyFrom(currentYearFrom: Int) {
        // Generate dates for the current month
        val dates = RlGenerateYearlyCalendar(currentYearFrom)

        val adapter = RLMonthlyCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            fromDate=date.toString()
            if (toDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 4)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        fragBinding.monthYearTextViewFrom.setText(currentYearFrom.toString())
    }
    private fun RLsetupCalendarMonthlyTO(currentYearFrom: Int) {
        // Generate dates for the current month
        val dates = RlGenerateYearlyCalendar(currentYearFrom)

        val adapter = RLMonthlyCalenderListAdapter(requireContext(), dates) { date ->
            // Handle date selection
            println("Selected date: $date")
            toDate=date.toString()
            if (fromDate.isNotEmpty()){
                fragBinding.btnNext.setBackgroundResource(R.drawable.round_green_thirty)
                fragBinding.btnNext.setTextColor(resources.getColor(R.color.AppWhiteColor))
            }
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 4)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        fragBinding.monthYearTextView.setText(currentYearFrom.toString())
    }
    private fun RLupdateMonthYearTextView(monthYearTextView: TextView, calendar: Calendar) {
        val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())
        val monthYear = dateFormat.format(calendar.time)
        monthYearTextView.text = monthYear
    }
    private fun RLgenerateDatesForMonth(calendar: Calendar): List<RLDateInfoModel> {
        // Generate days for the calendar
        val days = mutableListOf<RLDateInfoModel>()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val lastDayOfMonth = calendar.time

        val dateIterator = Calendar.getInstance()
        dateIterator.time = firstDayOfMonth
        val date = dateIterator.time
        if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Log.d(TAG,"MONDAY")
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            Log.d(TAG,"TUESDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            Log.d(TAG,"WEDNESDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            Log.d(TAG,"THURSDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            Log.d(TAG,"FRIDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            Log.d(TAG,"SATURDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            Log.d(TAG,"SUNDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }
        var currentDate = Calendar.getInstance().time
       // var currentDate = currentDateFrom

        while (dateIterator.time.before(lastDayOfMonth) || dateIterator.time.equals(lastDayOfMonth)) {
          val  dateType = if (RLisSameDay(dateIterator.time, currentDate)){
                RLDateType.CURRENT
            }else{
              currentDate = currentDateFrom
                when {
                    dateIterator.time.before(currentDate) -> RLDateType.OLD
                    dateIterator.time.after(currentDate) -> RLDateType.NEW
                    else -> RLDateType.CURRENT
                }
            }
            days.add(RLDateInfoModel(dateIterator.time, dateType))
            dateIterator.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
    private fun RLgenerateDatesForMonthWeekly(calendar: Calendar): List<RLDateInfoModel> {
        // Generate days for the calendar
        val days = mutableListOf<RLDateInfoModel>()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.time
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val lastDayOfMonth = calendar.time

        val dateIterator = Calendar.getInstance()

        dateIterator.time = firstDayOfMonth
        val date = dateIterator.time
        if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Log.d(TAG,"MONDAY")
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            Log.d(TAG,"TUESDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            Log.d(TAG,"WEDNESDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            Log.d(TAG,"THURSDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            Log.d(TAG,"FRIDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            Log.d(TAG,"SATURDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }else if (dateIterator.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            Log.d(TAG,"SUNDAY")
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
            days.add(RLDateInfoModel(date, RLDateType.BLANK))
        }
        // val currentDate = Calendar.getInstance().time
        val currentDate =currentDateFrom
        while (dateIterator.time.before(lastDayOfMonth) || dateIterator.time.equals(lastDayOfMonth)) {
            var  dateType = if (RLisSameDay(dateIterator.time, currentDate)){
                RLDateType.CURRENT
            }else{
                when {
                    dateIterator.time.before(currentDate) -> RLDateType.OLD
                    dateIterator.time.after(currentDate) -> RLDateType.NEW
                    else -> RLDateType.CURRENT
                }
            }
            if (dateType.equals(RLDateType.NEW)||dateType.equals(RLDateType.CURRENT)){
                val dayofweek=dateIterator.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                if (dayBefore.toUpperCase().equals(dayofweek.toUpperCase())){
                    dateType=RLDateType.NEW
                }else{
                    dateType=RLDateType.OLD
                }
            }
            days.add(RLDateInfoModel(dateIterator.time, dateType))
            dateIterator.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
    private fun RlGenerateYearlyCalendar(year: Int): List<RLMonthInfoModel> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.YEAR, year)
        val months = mutableListOf<RLMonthInfoModel>()

        // Get the names of the months
        val monthNames = DateFormatSymbols().shortMonths

        for (month in Calendar.JANUARY until Calendar.DECEMBER+1) {
            val monthType = when {
                year < currentYear -> RLDateType.OLD
                year > currentYear -> RLDateType.NEW
                month < currentMonth -> RLDateType.OLD
                month > currentMonth -> RLDateType.NEW
                else -> RLDateType.CURRENT
            }
            months.add(RLMonthInfoModel(monthNames[month].toUpperCase(), monthType))
        }

        return months
    }
    private fun getDayBefore(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Unknown"
    }
    private fun RLisSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    private fun RLshowAlertDialog(message:String) {
        val sucDialog:Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText(message)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

}