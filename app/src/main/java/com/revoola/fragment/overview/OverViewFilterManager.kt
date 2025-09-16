package com.revoola.fragment.overview

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFilterOverviewBinding
import com.revoola.fragment.overview.adapter.RLOverviewFilterListAdapter
import com.revoola.fragment.overview.adapter.RLOverviewFilterListMultipleSelectedAdapter
import com.revoola.utils.RLPrefManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class OverViewFilterManager(
    private val context: Context,
    private val onFilterSelected: (String?, String?, String, String, List<String>) -> Unit
) {

    private val filterListPeriod = listOf("This Month", "Last 3 Months", "Last 6 Months", "This Year","Custom Date Range")
    private val filterListSource = listOf("All Available", "Revoola Only")
    private val filterListType = listOf("All", "Walk", "Run","Ride","Workout","HIIT",  "Yoga", "Pilates", "Dance", "Other")


    // Load from temporary keys first (current session), then fall back to permanent keys
    private var toDate: String = RLPrefManager.rl_getSomeStringValue(context, "temp_toDate",
        RLPrefManager.rl_getSomeStringValue(context, "toDate", ""))
    private var fromDate: String = RLPrefManager.rl_getSomeStringValue(context, "temp_fromDate",
        RLPrefManager.rl_getSomeStringValue(context, "fromDate", ""))

    private var selectedPositionsPeriod: Int = RLPrefManager.rl_getSomeStringValue(context, "temp_selectedPositionsPeriod",
        RLPrefManager.rl_getSomeStringValue(context, "selectedPositionsPeriod","0")).toInt()
    private var selectionPeriod: String = RLPrefManager.rl_getSomeStringValue(context, "temp_selectionPeriod",
        RLPrefManager.rl_getSomeStringValue(context, "selectionPeriod", "This Month"))

    private var selectionSource: String = RLPrefManager.rl_getSomeStringValue(context, "temp_selectionSource",
        RLPrefManager.rl_getSomeStringValue(context, "selectionSource", "All Available"))
    private var selectionType: MutableList<String> = RLPrefManager.rl_getSomeStringListValue(context, "temp_selectionType",
        RLPrefManager.rl_getSomeStringListValue(context, "selectionType", mutableListOf("All")))

    // Use temporary keys for selected positions
    private var selectedPositionsSource: List<Int> = RLPrefManager.rl_getSomeIntListValue(context, "temp_selectedPositionsSource").ifEmpty {
        RLPrefManager.rl_getSomeIntListValue(context, "selectedPositionsSource")
    }

    private val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
    private val currentCalendar: Calendar = Calendar.getInstance()
    private var fromCalendar: Calendar = currentCalendar.clone() as Calendar
    private var toCalendar: Calendar = currentCalendar.clone() as Calendar

    private var isDefaultSwitchPeriod = false
    private var isDefaultSwitchSource = false

    // Method to initialize the filter dialog
    fun showFilterDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = RlFilterOverviewBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        if (selectionPeriod.equals("Custom Date Range")){
            if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                binding.txtThisMonth.text = "$fromDate - $toDate"
            }else {
                binding.txtThisMonth.text = selectionPeriod
                //binding.txtThisMonth.text = "$fromDateLocal - $toDateLocal"
            }
        }else{
            binding.txtThisMonth.text = selectionPeriod
        }

        // Set the default values for the filter text views
       // binding.txtThisMonth.text = if (selectionPeriod.equals("Custom Date Range")) "$fromDate - $toDate"  else selectionPeriod
        binding.txtAll.text = selectionSource

        // Setup period list adapter
        setupPeriodListAdapter(binding)

        // Setup source list adapter
        setupSourceListAdapter(binding)

        // Setup type list adapter
        setupTypeListAdapter(binding)

        // Set the click listener for the period text view
        binding.txtThisMonth.setOnClickListener {
            togglePeriodList(binding)
        }

        // Set the click listener for the source text view
        binding.txtAll.setOnClickListener {
            toggleActivityList(binding)
        }

        binding.recyclePeriod.setDefaultSwitchPeriod.setOnCheckedChangeListener { _, isChecked ->
            isDefaultSwitchPeriod = isChecked
        }

        binding.setDefaultSwitchSource.setOnCheckedChangeListener { _, isChecked ->
            isDefaultSwitchSource = isChecked
        }

        // Show results button click listener
        binding.btnShowResults.setOnClickListener {
            val selectedToDate = binding.recyclePeriod.toDateButton.text.toString()
            val selectedFromDate = binding.recyclePeriod.fromDateButton.text.toString()

            fromDate = selectedFromDate
            toDate = selectedToDate

            // Always save to temporary keys to maintain state across navigation
            RLPrefManager.rl_setSomeStringValue(context, "temp_toDate", selectedToDate)
            RLPrefManager.rl_setSomeStringValue(context, "temp_fromDate", selectedFromDate)
            RLPrefManager.rl_setSomeStringValue(context, "temp_selectionPeriod", selectionPeriod)
            RLPrefManager.rl_setSomeStringValue(context, "temp_selectedPositionsPeriod", selectedPositionsPeriod.toString())
            RLPrefManager.rl_setSomeStringListValue(context, "temp_selectionType", selectionType)
            RLPrefManager.rl_setSomeStringValue(context, "temp_selectionSource", selectionSource)
            RLPrefManager.rl_setSomeIntListValue(context, "temp_selectedPositionsSource", selectedPositionsSource)


            if (isDefaultSwitchPeriod){
                // Save the filter selections to SharedPreferences
                RLPrefManager.rl_setSomeStringValue(context, "toDate", selectedToDate)
                RLPrefManager.rl_setSomeStringValue(context, "fromDate", selectedFromDate)
                RLPrefManager.rl_setSomeStringValue(context, "selectionPeriod", selectionPeriod)
                RLPrefManager.rl_setSomeStringValue(context, "selectedPositionsPeriod", selectedPositionsPeriod.toString())
            }
            if (isDefaultSwitchSource){
                // Save the filter selections to SharedPreferences
                RLPrefManager.rl_setSomeStringListValue(context, "selectionType", selectionType)
                RLPrefManager.rl_setSomeStringValue(context, "selectionSource", selectionSource)
                RLPrefManager.rl_setSomeIntListValue(context, "selectedPositionsSource", selectedPositionsSource)
            }

            onFilterSelected(selectedFromDate, selectedToDate, selectionPeriod, selectionSource, selectionType)
            dialog.dismiss()
        }

        // Close button listener
        binding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun setupPeriodListAdapter(binding: RlFilterOverviewBinding) {
        // Handle the selection of buttons in the first row
        binding.recyclePeriod.thisMonth.setOnClickListener {
            handleButtonSelection(0,binding)
        }
       
        binding.recyclePeriod.last3Month.setOnClickListener {
            handleButtonSelection(1,binding)
        }

        // Handle the selection of buttons in the second row
        binding.recyclePeriod.last6Month.setOnClickListener {
            handleButtonSelection(2,binding)
        }

        binding.recyclePeriod.thisYear.setOnClickListener {
            handleButtonSelection(3,binding)
        }

        // Handle the visibility of the last item button (for single button)
        binding.recyclePeriod.customDateRange.setOnClickListener {
            handleButtonSelection(4,binding)
        }
        binding.recyclePeriod.fromDateButton.setOnClickListener {
            showFromDatePickerDialog(binding)
        }
        binding.recyclePeriod.toDateButton.setOnClickListener {
            showToDatePickerDialog(binding)
        }

        if ("Custom Date Range".equals(selectionPeriod, ignoreCase = true)) {
            if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                try {
                    fromCalendar.time = dateFormat.parse(fromDate) ?: currentCalendar.time
                    toCalendar.time = dateFormat.parse(toDate) ?: currentCalendar.time
                    binding.recyclePeriod.fromDateButton.text = fromDate
                    binding.recyclePeriod.toDateButton.text = toDate
                } catch (e: Exception) {
                    // If parsing fails (invalid date format), default to current date
                    fromCalendar.time = currentCalendar.time
                    toCalendar.time = currentCalendar.time
                    binding.recyclePeriod.fromDateButton.text = dateFormat.format(currentCalendar.time)
                    binding.recyclePeriod.toDateButton.text = dateFormat.format(currentCalendar.time)
                }
            } else {
                // If either fromDate or toDate is empty, default to current date
                fromCalendar.time = currentCalendar.time
                toCalendar.time = currentCalendar.time
                binding.recyclePeriod.fromDateButton.text = dateFormat.format(currentCalendar.time)
                binding.recyclePeriod.toDateButton.text = dateFormat.format(currentCalendar.time)
            }
        } else {
            binding.recyclePeriod.fromDateButton.text = dateFormat.format(currentCalendar.time)
            binding.recyclePeriod.toDateButton.text = dateFormat.format(currentCalendar.time)
            fromCalendar.time = currentCalendar.time
            toCalendar.time = currentCalendar.time
        }

//        if (selectionPeriod.equals("Custom Date Range")){
//            binding.recyclePeriod.fromDateButton.setText(fromDate)
//            binding.recyclePeriod.toDateButton.setText(toDate)
//        }else{
//            binding.recyclePeriod.fromDateButton.setText(dateFormat.format(currentCalendar.time))
//            binding.recyclePeriod.toDateButton.setText(dateFormat.format(currentCalendar.time))
//        }


        // Update button visibility and selection state
        updateButtons(binding)
    }

    private fun handleButtonSelection(position: Int,binding: RlFilterOverviewBinding) {
        selectionPeriod = filterListPeriod.get(position)
        selectedPositionsPeriod = position
        updateButtons(binding)

    }

    private fun updateButtons(binding: RlFilterOverviewBinding) {
        // Update button colors based on selection
        when (selectedPositionsPeriod) {
            0 -> {
                binding.recyclePeriod.thisMonth.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                binding.recyclePeriod.thisMonth.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                binding.recyclePeriod.last3Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last3Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last6Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last6Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.thisYear.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisYear.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.customDateRange.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.customDateRange.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.layoutCustomDate.visibility= View.GONE
            }
            1 -> {
                binding.recyclePeriod.thisMonth.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisMonth.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last3Month.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                binding.recyclePeriod.last3Month.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                binding.recyclePeriod.last6Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last6Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.thisYear.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisYear.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.customDateRange.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.customDateRange.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.layoutCustomDate.visibility= View.GONE
            }
            2 -> {
                binding.recyclePeriod.thisMonth.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisMonth.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last3Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last3Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last6Month.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                binding.recyclePeriod.last6Month.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                binding.recyclePeriod.thisYear.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisYear.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.customDateRange.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.customDateRange.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.layoutCustomDate.visibility= View.GONE
            }
            3 -> {
                binding.recyclePeriod.thisMonth.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisMonth.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last3Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last3Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last6Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last6Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.thisYear.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                binding.recyclePeriod.thisYear.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                binding.recyclePeriod.customDateRange.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.customDateRange.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.layoutCustomDate.visibility= View.GONE
            }
            4 -> {
                binding.recyclePeriod.customDateRange.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                binding.recyclePeriod.customDateRange.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                binding.recyclePeriod.thisMonth.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisMonth.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last3Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last3Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.last6Month.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.last6Month.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.thisYear.setBackgroundResource(R.drawable.rl_filter_border_overview)
                binding.recyclePeriod.thisYear.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                binding.recyclePeriod.layoutCustomDate.visibility= View.VISIBLE
            }
        }
    }

    private fun setupSourceListAdapter(binding: RlFilterOverviewBinding) {
        val adapterSource = RLOverviewFilterListAdapter(
            context, toDate, fromDate, filterListSource, selectedPositionsSource.toMutableList(),
            { selectionData, selectionType -> handleSourceSelection(selectionData) },
            { selectedPositions ->
                selectedPositionsSource = selectedPositions // Update the selected positions for source
                RLPrefManager.rl_setSomeIntListValue(context, "selectedPositionsSource", selectedPositionsSource) // Save to SharedPreferences
            }
        )
        val layoutManagerSource = GridLayoutManager(context, 2)
        binding.recycleSource.layoutManager = layoutManagerSource
        binding.recycleSource.adapter = adapterSource
    }

    private fun setupTypeListAdapter(binding: RlFilterOverviewBinding) {
        val adapterType = RLOverviewFilterListMultipleSelectedAdapter(
            context, filterListType, selectionType
        ) { selectedItems -> handleTypeSelection(selectedItems) }
        val layoutManagerType = GridLayoutManager(context, 2)
        binding.recycleType.layoutManager = layoutManagerType
        binding.recycleType.adapter = adapterType
    }

    private fun togglePeriodList(binding: RlFilterOverviewBinding) {
        binding.recyclePeriod.periodLayout.visibility = if (binding.recyclePeriod.periodLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
       binding.activityLayout.visibility = View.GONE
       binding.sourceSwitchLayout.visibility = View.GONE
    }

    private fun toggleActivityList(binding: RlFilterOverviewBinding) {
        binding.activityLayout.visibility = if (binding.activityLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.sourceSwitchLayout.visibility = if (binding.sourceSwitchLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.recyclePeriod.periodLayout.visibility = View.GONE
    }

    private fun handleSourceSelection(selectionData: String) {
        // Handle source selection
        selectionSource = selectionData
    }

    private fun handleTypeSelection(selectedItems: List<String>) {
        // Handle type selection (multiple selection)
        selectionType = selectedItems.toMutableList()
    }

    private fun showFromDatePickerDialog(binding: RlFilterOverviewBinding) {
        val year = fromCalendar.get(Calendar.YEAR)
        val month = fromCalendar.get(Calendar.MONTH)
        val day = fromCalendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                fromCalendar.set(selectedYear, selectedMonth, selectedDay)
                binding.recyclePeriod.fromDateButton.setText(dateFormat.format(fromCalendar.time))

                // Auto set 'to' date to the selected 'from' date
                toCalendar.time = fromCalendar.time
                binding.recyclePeriod.toDateButton.setText(dateFormat.format(toCalendar.time))
            },
            year,
            month,
            day
        )

        // Allow only dates up to current date (no future dates)
        dialog.datePicker.maxDate = currentCalendar.timeInMillis
        dialog.show()
    }

    private fun showToDatePickerDialog(binding: RlFilterOverviewBinding) {
        val year = toCalendar.get(Calendar.YEAR)
        val month = toCalendar.get(Calendar.MONTH)
        val day = toCalendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                toCalendar.set(selectedYear, selectedMonth, selectedDay)
                binding.recyclePeriod.toDateButton.setText(dateFormat.format(toCalendar.time))
            },
            year,
            month,
            day
        )

        // Allow dates between 'from' date and current date
        dialog.datePicker.minDate = fromCalendar.timeInMillis
        dialog.datePicker.maxDate = currentCalendar.timeInMillis
        dialog.show()
    }

    //Date get
    fun getDateNewRangeForPeriod(selectedPeriod: String,fromDate: String,toDate: String): Map<String, Any> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        fun startOfDay(date: Date): Date {
            calendar.time = date
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }
        return when (selectedPeriod) {
            "THIS_MONTH" -> {
                val startOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH).let {
                    calendar.set(Calendar.DAY_OF_MONTH, it)
                    startOfDay(calendar.time)
                }
                val endOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH).let {
                    calendar.set(Calendar.DAY_OF_MONTH, it)
                    startOfDay(calendar.time)
                }
                val previousMonth = calendar.apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time

                val startOfPreviousMonth = startOfDay(previousMonth)

                mapOf(
                    "fromTimestamp" to startOfMonth.time / 1000,
                    "toTimestamp" to endOfMonth.time / 1000,
                    "comparisonFromTimestamp" to startOfPreviousMonth.time / 1000,
                    "timeRef" to "this_month"
                )
            }
            "LAST_3_MONTHS" -> {
                val startOfCurrentMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time
                val endOfCurrentMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }.time
                val startOf3MonthsAgo = calendar.apply {
                    add(Calendar.MONTH, -2)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time
                val startOfComparison6MonthsAgo = calendar.apply {
                    add(Calendar.MONTH, -5)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time

                mapOf(
                    "fromTimestamp" to startOf3MonthsAgo.time / 1000,
                    "toTimestamp" to endOfCurrentMonth.time / 1000,
                    "comparisonFromTimestamp" to startOfComparison6MonthsAgo.time / 1000,
                    "timeRef" to "3_months"
                )
            }
            "LAST_6_MONTHS" -> {
                val startOfCurrentMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time
                val endOfCurrentMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }.time
                val startOf6MonthsAgo = calendar.apply {
                    add(Calendar.MONTH, -5)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time
                val startOfComparison12MonthsAgo = calendar.apply {
                    add(Calendar.MONTH, -11)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }.time

                mapOf(
                    "fromTimestamp" to startOf6MonthsAgo.time / 1000,
                    "toTimestamp" to endOfCurrentMonth.time / 1000,
                    "comparisonFromTimestamp" to startOfComparison12MonthsAgo.time / 1000,
                    "timeRef" to "6_months"
                )
            }
            "THIS_YEAR" -> {
                val currentYear = calendar.get(Calendar.YEAR)
                val startOfYear = calendar.apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, Calendar.JANUARY)
                    set(Calendar.DAY_OF_MONTH, 1)
                }.time
                val endOfYear = calendar.apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, Calendar.DECEMBER)
                    set(Calendar.DAY_OF_MONTH, 31)
                }.time
                val startOfLastYear = calendar.apply {
                    set(Calendar.YEAR, currentYear - 1)
                    set(Calendar.MONTH, Calendar.JANUARY)
                    set(Calendar.DAY_OF_MONTH, 1)
                }.time

                mapOf(
                    "fromTimestamp" to startOfYear.time / 1000,
                    "toTimestamp" to endOfYear.time / 1000,
                    "comparisonFromTimestamp" to startOfLastYear.time / 1000,
                    "timeRef" to "this_year"
                )
            }
            "CUSTOM_DATE_RANGE" -> {
                val fromTimestamp = convertDate(fromDate)?.let { startOfDay(it).time / 1000 } ?: startOfDay(now).time / 1000
                val toTimestamp = convertDate(toDate)?.let { startOfDay(it).time / 1000 } ?: startOfDay(now).time / 1000

                mapOf(
                    "fromTimestamp" to fromTimestamp,
                    "toTimestamp" to toTimestamp,
                    "comparisonFromTimestamp" to fromTimestamp,
                    "timeRef" to "custom"
                )
            }
            else -> {
                mapOf(
                    "fromTimestamp" to 0,
                    "toTimestamp" to 0,
                    "comparisonFromTimestamp" to 0,
                    "timeRef" to "this_year"
                )
            }
        }
    }
    private fun convertDate(dateString: String): Date? {
        val format = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    fun formatToMonthYear(dateString: String?): String {
        return try {
            if (dateString.isNullOrEmpty()){
                "-"
            }else{
                // Define the format of the input date string
                val inputFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

                // Parse the input string into a Date object
                val date: Date = inputFormat.parse(dateString) ?: return ""

                // Define the output format (MMM-yyyy)
                val outputFormat = SimpleDateFormat("MMM-yy", Locale.getDefault())

                // Format the date to the desired "MMM-yyyy" format
                outputFormat.format(date)
            }
        }catch (e: Exception){
            "-"
        }

    }
    fun getSelectionPeriod(period: String): String{
        return when (period) {
            "This Month" ->  "THIS_MONTH"
            "Last 3 Months" -> "LAST_3_MONTHS"
            "Last 6 Months" ->"LAST_6_MONTHS"
            "This Year" ->"THIS_YEAR"
            else -> "CUSTOM_DATE_RANGE"
        }
    }
}




