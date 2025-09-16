package com.revoola.fragment.start.challenges

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragChalengesCalenderBinding
import com.revoola.enumclass.RLDateType
import com.revoola.fragment.start.challenges.adapter.RLCalenderListAdapter
import com.revoola.fragment.start.challenges.adapter.RLMonthlyCalenderListAdapter
import com.revoola.fragment.start.challenges.model.RLDateInfoModel
import com.revoola.commonobject.RLTools
import com.revoola.fragment.start.challenges.model.RLEditChallengeAllData
import com.revoola.fragment.start.challenges.model.RLMonthInfoModel
import com.revoola.utils.RLPrefManager
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

    var selectedFromMonthName:String = ""
    var selectedFromYear:String=""
    private var currentToYear:Int =0
    private  var currentFromYear:Int =0
    private var dayBefore:String ="test"

    private var selectionAllReadyDateFrom:Date? = null
    private var selectionAllReadyDateTo:Date? = null
    private var selectionAllReadyMontFrom:String = ""
    private var selectionAllReadyMonthTo:String = ""

    val currentYearTemp  = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonthTemp = Calendar.getInstance().get(Calendar.MONTH)

    private var selectionCurrentMonthFrom:String = DateFormatSymbols().shortMonths[currentMonthTemp].toUpperCase(Locale.ENGLISH)//currentMonthTemp.toString()
    private var selectionCurrentYearFrom:String = currentYearTemp.toString()

    private var currentDateFrom = Calendar.getInstance().time
    private var currentDateTO = Calendar.getInstance().time

    private val binding by lazy {
        RlFragChalengesCalenderBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragChalengesCalender()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_calender, container) as RlFragChalengesCalenderBinding
       RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesCalender" )
        RLuisetup()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom action here
                // For example, show a confirmation dialog or navigate
                rl_closeFragment()
            }
        })
        return fragBinding.root
    }
    private fun RLuisetup(){
        fragBinding.inlayTop.ivBack.setOnClickListener {
            rl_closeFragment()
        }
        RLTools.RLhideShowHelpDialog(requireContext(), "challenge_selectTime",  fragBinding.inlayTop.ivhelp)

        val cardData = requireArguments().getSerializable("cardData") as RLEditChallengeAllData

        when(cardData.ChallengeType){
            "Steps"->{fragBinding.inlayTop.ivTitle.setText(R.string.stepchallenge)}
            "Effort"->{fragBinding.inlayTop.ivTitle.setText(R.string.effortchallenge)}
            "Calories"->{fragBinding.inlayTop.ivTitle.setText(R.string.calorieschallenge)}
            "Distance"->{fragBinding.inlayTop.ivTitle.setText(R.string.distancechallenge)}
            "Climbed"->{fragBinding.inlayTop.ivTitle.setText(R.string.climbedchallenge)}
            "Duration"->{fragBinding.inlayTop.ivTitle.setText(R.string.durationchallenge)}
        }
        when(cardData.CalenderType){
            "Daily"->{
                RLEditDateSeletionSet(cardData,false,cardData.CalenderType)
                fragBinding.inlayTop.ivDescription.setText(R.string.dailychallenge)}
            "Weekly"->{
                RLEditDateSeletionSet(cardData,false,cardData.CalenderType)
                fragBinding.inlayTop.ivDescription.setText(R.string.weeklychallenge)}
            "Monthly"->{
                RLEditDateSeletionSet(cardData,true,cardData.CalenderType)
                fragBinding.inlayTop.ivDescription.setText(R.string.monthlychallenge)}
            "Custom"->{
                RLEditDateSeletionSet(cardData,false,cardData.CalenderType)
                fragBinding.inlayTop.ivDescription.setText(R.string.customchallenge)}
        }
        fragBinding.btnNextVisible.setOnClickListener {
           if (fromDate.isNotEmpty() && toDate.isNotEmpty()){
               val todate=RLTools.rl_convertDate(toDate)
               val fromdate=RLTools.rl_convertDate(fromDate)
               val joinDate = "STARTS: $fromdate ENDS: $todate"

               cardData.selectedDate=joinDate
               cardData.toDate=toDate
               cardData.fromDate=fromDate

               val bundle: Bundle = Bundle()
               bundle.putSerializable("cardData",cardData)
               if (cardData.isEditClass){
                   (context as RLMainActivityRL).rl_loadFrag(RLFragEditChallenges().newInstance(bundle), TAG, true,null, false)
               }else {
                   (context as RLMainActivityRL).rl_loadFrag(RLFragChallengesForName().newInstance(bundle), TAG, true, null, false)
               }
           }else{
               RLshowAlertDialog("Please select Valid details")
           }
        }

    }

    //this function work all ready selection date handle
    private fun RLEditDateSeletionSet(cardData:RLEditChallengeAllData,isMonth:Boolean,CalenderType:String) {
        calendarFrom = Calendar.getInstance()
        calendarTo = Calendar.getInstance()
        if (cardData.toDate.isNotEmpty() && cardData.fromDate.isNotEmpty()){
            if (isMonth){
                selectionCurrentYearFrom = RLTools.rl_stringDateToMonthYearFormate(cardData.fromDate,true)
                selectionCurrentMonthFrom = RLTools.rl_stringDateToMonthYearFormate(cardData.fromDate,false).toUpperCase()

                selectionAllReadyMontFrom = "$selectionCurrentMonthFrom, $selectionCurrentYearFrom"
                selectedFromMonthName = selectionCurrentMonthFrom
                selectedFromYear = selectionCurrentYearFrom
                fromDate=cardData.fromDate
                toDate=cardData.toDate
                selectionAllReadyMonthTo = "${RLTools.rl_stringDateToMonthYearFormate(cardData.toDate,false).toUpperCase()}, ${RLTools.rl_stringDateToMonthYearFormate(cardData.toDate,true)}"

                RLCalenderShow(CalenderType,true)
            }else {
                selectionAllReadyDateFrom = RLTools.rl_stringDateToDateFormate(cardData.fromDate)
                if (!CalenderType.equals("Weekly")){
                   /* selectionAllReadyDateTo = RLTools.RLStringDateToDateFormate(cardData.toDate)
                    toDate = selectionAllReadyDateTo.toString()
                    currentDateFrom= RLTools.RLStringDateToDateFormate(cardData.fromDate)!!
                    currentDateTO = RLTools.RLStringDateToDateFormate(cardData.fromDate)!!*/


                    val date=RLTools.rl_stringDateToDateFormate(cardData.fromDate)
                    selectionAllReadyDateFrom=date
                    selectionAllReadyDateTo=null
                    dayBefore = date?.let { getDayBefore(it) } ?: "null"
                    currentDateTO = date
                    currentDateFrom=date
                    fromDate=date.toString()
                    toDate = RLTools.rl_stringDateToDateFormate(cardData.fromDate).toString()

                }else{
                    selectionAllReadyDateTo=null
                    currentDateTO = selectionAllReadyDateFrom
                }
                dayBefore = selectionAllReadyDateFrom?.let { getDayBefore(it) } ?: "null"
                fromDate = selectionAllReadyDateFrom.toString()
                RLCalenderShow(CalenderType,true)
            }

        }else{
            RLCalenderShow(CalenderType,false)
        }

    }
    //this function work calender name wise calender Show
    private fun RLCalenderShow(CalenderType:String,isEdit:Boolean){
        when(CalenderType){
            "Daily"->{
                RLDailyCalenderShow()
            }
            "Weekly"->{
                RLWeeklyCalenderShow(isEdit)
            }
            "Monthly"->{
                RLMonthlyCalenderShow()
            }
            "Custom"->{
                RLDailyCalenderShow()
            }
        }
    }
    //Daily and Custom Calender Setup
    private fun RLDailyCalenderShow(){
        RLsetupCalendarFrom()
        RLsetupCalendarTO()
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
    //Weekly Calender Setup
    private fun RLWeeklyCalenderShow(isEdit:Boolean){
        RLsetupCalendarWeeklyFrom()
        if (isEdit){
            RLsetupCalendarWeeklyTO()
        }else{
            RLsetupCalendarFirstTimeWeeklyTO()
        }

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
    //Monthly Name Calender Setup
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

    //From Calender Setup
    //Daily
    private fun RLsetupCalendarFrom() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarFrom,false)

        val adapter = RLCalenderListAdapter(requireContext(), dates,selectionAllReadyDateFrom) { date ->
            // Handle date selection
            println("Selected date: $date")
            selectionAllReadyDateFrom=date
            selectionAllReadyDateTo=null
            dayBefore = date?.let { getDayBefore(it) } ?: "null"
            fromDate=date.toString()
            currentDateTO = date
            toDate=""
            RLsetupCalendarTO()
            RLNextButtonSShowHide(toDate)
        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextViewFrom,calendarFrom)
    }
    //Week
    private fun RLsetupCalendarWeeklyFrom() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarFrom,false)
        val adapter = RLCalenderListAdapter(requireContext(), dates,selectionAllReadyDateFrom) { date ->
            // Handle date selection
            println("Selected date: $date")
            selectionAllReadyDateFrom=date
            selectionAllReadyDateTo=null
            dayBefore = date?.let { getDayBefore(it) } ?: "null"
            currentDateTO = date
            currentDateFrom=date
            RLsetupCalendarWeeklyTO()
            fromDate=date.toString()
            toDate=""
            RLNextButtonSShowHide(toDate)
        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextViewFrom,calendarFrom)
    }
    //Month
    private fun RLsetupCalendarMonthlyFrom(currentYearFrom: Int) {
        // Generate dates for the current month
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val dates = RlGenerateYearlyCalendar(currentYearFrom,currentYear,currentMonth)

        val adapter = RLMonthlyCalenderListAdapter(requireContext(),dates,selectionAllReadyMontFrom) { monthData ->
            // Handle date selection
            selectionAllReadyMontFrom = monthData.monthNameWithYear
            selectionAllReadyMonthTo =""
            selectedFromMonthName = monthData.date
            selectedFromYear = monthData.monthNameWithYear.split(", ")[1].toString()
            toDate=""
            selectionCurrentMonthFrom= monthData.date
            selectionCurrentYearFrom=monthData.monthNameWithYear.split(", ")[1].toString()
            RLsetupCalendarMonthlyTO(currentToYear)
            fromDate=RLTools.rl_monthNameTogetFirstDate("01 ${monthData.monthNameWithYear}")
            println("Selected fromDate: $fromDate")
            RLNextButtonSShowHide(toDate)


        }
        fragBinding.calendarRecyclerViewFrom.layoutManager = GridLayoutManager(requireContext(), 4)
        fragBinding.calendarRecyclerViewFrom.adapter = adapter
        fragBinding.monthYearTextViewFrom.setText(currentYearFrom.toString())
    }

    //TO Calender Setup
    //Daily
    private fun RLsetupCalendarTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarTo,true)

        val adapter = RLCalenderListAdapter(requireContext(), dates,selectionAllReadyDateTo) { date ->
            // Handle date selection
            println("Selected date: $date")
            toDate=date.toString()
            selectionAllReadyDateTo=date
            RLNextButtonSShowHide(fromDate)
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    //Week
    private fun RLsetupCalendarFirstTimeWeeklyTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForMonth(calendarTo,true)

        val adapter = RLCalenderListAdapter(requireContext(), dates,selectionAllReadyDateTo) { date ->
            // Handle date selection
            println("Selected date: $date")
            selectionAllReadyDateTo=date
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    private fun RLsetupCalendarWeeklyTO() {
        // Generate dates for the current month
        val dates = RLgenerateDatesForWeekly(calendarTo)
        val adapter = RLCalenderListAdapter(requireContext(), dates,selectionAllReadyDateTo) { date ->
            // Handle date selection
            println("Selected date: $date")
            selectionAllReadyDateTo=date
            toDate=date.toString()
            RLNextButtonSShowHide(fromDate)
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 7)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        RLupdateMonthYearTextView(fragBinding.monthYearTextView, calendarTo)
    }
    //Month
    private fun RLsetupCalendarMonthlyTO(currentYearFrom: Int) {
        // Generate dates for the current month
        val  currentMonth = mapMonthToIndex(selectionCurrentMonthFrom)
        val currentYear = selectionCurrentYearFrom.toInt()
        val MonthNameList:List<RLMonthInfoModel> = RlGenerateYearlyCalendar(currentYearFrom,currentYear,currentMonth)
        val adapter = RLMonthlyCalenderListAdapter(requireContext(), MonthNameList,selectionAllReadyMonthTo) { monthData ->
            // Handle date selection
            selectionAllReadyMonthTo = monthData.monthNameWithYear
            toDate=RLTools.rl_monthNameTogetLastDate("${monthData.monthNameWithYear}")
            RLNextButtonSShowHide(fromDate)
        }
        fragBinding.calendarRecyclerViewTo.layoutManager = GridLayoutManager(requireContext(), 4)
        fragBinding.calendarRecyclerViewTo.adapter = adapter
        fragBinding.monthYearTextView.setText(currentYearFrom.toString())
    }

    //Daily Date Get
    private fun RLgenerateDatesForMonth(calendar: Calendar,isCalenderTo:Boolean): List<RLDateInfoModel> {
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
        when(dateIterator.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY->{
                RLTools.rl_logDPrint(TAG,"MONDAY")}
            Calendar.TUESDAY->{
                RLTools.rl_logDPrint(TAG,"TUESDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.WEDNESDAY->{ RLTools.rl_logDPrint(TAG,"WEDNESDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.THURSDAY->{  RLTools.rl_logDPrint(TAG,"THURSDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.FRIDAY->{  RLTools.rl_logDPrint(TAG,"FRIDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.SATURDAY->{ RLTools.rl_logDPrint(TAG,"SATURDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.SUNDAY->{ RLTools.rl_logDPrint(TAG,"SUNDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}

        }

        var currentDate = Calendar.getInstance().time
        // var currentDate = currentDateFrom

        while (dateIterator.time.before(lastDayOfMonth) || dateIterator.time.equals(lastDayOfMonth)) {
            val  dateType = if (RLisSameDay(dateIterator.time, currentDate)){
                RLDateType.CURRENT
            }else{
                if (isCalenderTo){
                    currentDate = currentDateTO
                }/*else{
                    currentDate = currentDateFrom
                }*/

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
    //Weekly Date Get
    private fun RLgenerateDatesForWeekly(calendar: Calendar): List<RLDateInfoModel> {
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
        when(dateIterator.get(Calendar.DAY_OF_WEEK)){
            Calendar.MONDAY->{
                RLTools.rl_logDPrint(TAG,"MONDAY")}
            Calendar.TUESDAY->{
                RLTools.rl_logDPrint(TAG,"TUESDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.WEDNESDAY->{ RLTools.rl_logDPrint(TAG,"WEDNESDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.THURSDAY->{  RLTools.rl_logDPrint(TAG,"THURSDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.FRIDAY->{  RLTools.rl_logDPrint(TAG,"FRIDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.SATURDAY->{ RLTools.rl_logDPrint(TAG,"SATURDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}
            Calendar.SUNDAY->{ RLTools.rl_logDPrint(TAG,"SUNDAY")
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))
                days.add(RLDateInfoModel(date, RLDateType.BLANK))}

        }

        // val currentDate = Calendar.getInstance().time
       // val currentDate =currentDateFrom
        val currentDate =currentDateTO
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
                    dateType= RLDateType.NEW
                }else{
                    dateType= RLDateType.OLD
                }
            }
            days.add(RLDateInfoModel(dateIterator.time, dateType))
            dateIterator.add(Calendar.DAY_OF_MONTH, 1)
        }
        return days
    }
    //MonthName Get
    private fun RlGenerateYearlyCalendar(year: Int,currentYear:Int,currentMonth:Int): List<RLMonthInfoModel> {

        val calendar = Calendar.getInstance()
       // val currentYear = calendar.get(Calendar.YEAR)
        //val currentMonth = calendar.get(Calendar.MONTH)
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
            months.add(
                RLMonthInfoModel(
                    monthNames[month].toUpperCase(),
                    monthType,"${monthNames[month].uppercase()}, $year"
                )
            )
        }

        return months
    }

    //This Function Work  if Current Day Sunday And This Function Return Saturday
    private fun getDayBefore(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: "Unknown"
    }
    //Both Date Compaire And Check Both Date Same Or Not
    private fun RLisSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
    //Alert Dialog Show when From and  to Date Unselect
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

    //button next visibleinvisible
    private fun RLNextButtonSShowHide(toDateget: String){
        if (toDateget.isNotEmpty()){
            fragBinding.btnNextVisible.visibility=View.VISIBLE
            fragBinding.btnNextInvisible.visibility=View.GONE
        }else{
            fragBinding.btnNextVisible.visibility=View.GONE
            fragBinding.btnNextInvisible.visibility=View.VISIBLE
        }
    }
    //MonthName to get MonthNumber
    private fun mapMonthToIndex(month: String): Int {
        val months = DateFormatSymbols().shortMonths.map { it.uppercase() }
        return months.indexOf(month.uppercase())
    }
    //Selection to set YearName
    private fun RLupdateMonthYearTextView(monthYearTextView: TextView, calendar: Calendar) {
        val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.getDefault())
        val monthYear = dateFormat.format(calendar.time)
        monthYearTextView.text = monthYear
    }

}