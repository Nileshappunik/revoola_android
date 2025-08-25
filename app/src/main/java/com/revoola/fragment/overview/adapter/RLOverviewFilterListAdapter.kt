package com.revoola.fragment.overview.adapter

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlCommonFilterOverviewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RLOverviewFilterListAdapter(
    private val context: Context,private var toDate: String,private var fromDate: String,
    private val dataList: List<String>, private val dataPosition: MutableList<Int>,
    private val onDataSelected: (String, String?) -> Unit,
    private val onPositionsSelected: (MutableList<Int>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Track selected positions (can be multiple for date items)
    private val selectedPositions = dataPosition //mutableSetOf<Int>(0)

    // Store selected dates
    private val selectedDates = mutableMapOf<String, String>()

    // Date formatter for display
    private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlCommonFilterOverviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_common_filter_overview, parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(val layoutBinding: RlCommonFilterOverviewBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {

        fun bindData(position: Int, itemView: View) {
            val title = dataList[position]
            layoutBinding.BoxTextFirst.setText(title)
            layoutBinding.layoutBox.visibility = View.VISIBLE
            layoutBinding.layoutBoxType.visibility = View.GONE

            // Check if this item is selected
            val isSelected = selectedPositions.contains(position)

            // Set the background color based on selection
            if (isSelected) {
                layoutBinding.BoxTextFirst.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                layoutBinding.BoxTextSecond.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                layoutBinding.BoxText.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                layoutBinding.layoutBox.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
                layoutBinding.layoutBoxType.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
            } else {
                layoutBinding.BoxTextFirst.setTextColor(ContextCompat.getColor(context, R.color.AppBlackColor))
                layoutBinding.BoxTextSecond.setTextColor(ContextCompat.getColor(context, R.color.AppTextGrayColor))
                layoutBinding.BoxText.setTextColor(ContextCompat.getColor(context, R.color.AppBlackColor))
                layoutBinding.layoutBox.setBackgroundResource(R.drawable.rl_filter_border_overview)
                layoutBinding.layoutBoxType.setBackgroundResource(R.drawable.rl_filter_border_overview)
            }

            // Handle date field visibility and display
            when (title.toLowerCase()) {
                "from"-> {
                    layoutBinding.BoxTextSecond.visibility = View.VISIBLE
                    // Display selected date if available
                    val selectedDate = selectedDates[title.toLowerCase()]
                    if (selectedDate != null) {
                        layoutBinding.BoxTextSecond.setText(selectedDate)
                    }else {
                        if (fromDate.isNotEmpty()){
                            layoutBinding.BoxTextSecond.setText(fromDate)
                        }else{
                            layoutBinding.BoxTextSecond.setText("DD/MM/YY")
                        }
                    }
                }
                "to" -> {
                    layoutBinding.BoxTextSecond.visibility = View.VISIBLE
                    // Display selected date if available
                    val selectedDate = selectedDates[title.toLowerCase()]
                    if (selectedDate != null) {
                        layoutBinding.BoxTextSecond.setText(selectedDate)
                    }else {
                        if (toDate.isNotEmpty()){
                            layoutBinding.BoxTextSecond.setText(toDate)
                        }else{
                            layoutBinding.BoxTextSecond.setText("DD/MM/YY")
                        }

                    }
                }
                else -> {
                    layoutBinding.BoxTextSecond.visibility = View.GONE
                }
            }

            layoutBinding.layoutBox.setOnClickListener {
                when (title.toLowerCase()) {
                    "from", "to" -> {
                        showDatePicker(title, position)
                    }
                    else -> {
                        toDate = "DD/MM/YY"
                        fromDate = "DD/MM/YY"
                        handleNonDateSelection(title, position)
                    }
                }
            }
        }

        private fun handleNonDateSelection(title: String, position: Int) {
            // Clear current selections and select only this item
            selectedPositions.clear()
            selectedDates.clear()
            selectedPositions.add(position)

            // Notify about selection
            onDataSelected(title, null)
            onPositionsSelected(selectedPositions.toMutableList())
            notifyDataSetChanged()
        }

        private fun showDatePicker(title: String, position: Int) {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)

                    // Validate date selection
                    if (isValidDateSelection(title, selectedCalendar)) {
                        val formattedDate = dateFormatter.format(selectedCalendar.time)

                        // Clear other period selections (like "Last 3 months") when date is selected
                        clearNonDateSelections()

                        // Store the selected date
                        selectedDates[title.toLowerCase()] = formattedDate

                        // Add this position to selected positions
                        selectedPositions.add(position)

                        // Update the display
                        layoutBinding.BoxTextSecond.setText(formattedDate)

                        // Notify about date selection
                        onDataSelected(title, formattedDate)
                        onPositionsSelected(selectedPositions.toMutableList())
                        // Notify only the specific item to avoid flickering
                        notifyItemChanged(position)

                        // Also update other date items if they exist
                        val otherDateType = if (title.toLowerCase() == "from") "to" else "from"
                        val otherPosition = dataList.indexOfFirst { it.toLowerCase() == otherDateType }
                        if (otherPosition != -1) {
                            notifyItemChanged(otherPosition)
                        }
                    } else {
                        // Show error message for invalid date selection
                        showDateValidationError(title)
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Set date constraints
            setDatePickerConstraints(datePickerDialog, title)
            datePickerDialog.show()
        }

        private fun isValidDateSelection(title: String, selectedDate: Calendar): Boolean {
            val currentDate = Calendar.getInstance()

            when (title.toLowerCase()) {
                "from" -> {
                    // From date cannot be in the future
                    if (selectedDate.after(currentDate)) {
                        return false
                    }

                    // If "to" date is already selected, "from" date must be before or equal to "to" date
                    selectedDates["to"]?.let { toDateString ->
                        val toDate = Calendar.getInstance()
                        toDate.time = dateFormatter.parse(toDateString) ?: return false
                        if (selectedDate.after(toDate)) {
                            return false
                        }
                    }
                }
                "to" -> {
                    // To date cannot be in the future
                    if (selectedDate.after(currentDate)) {
                        return false
                    }

                    // If "from" date is already selected, "to" date must be after or equal to "from" date
                    selectedDates["from"]?.let { fromDateString ->
                        val fromDate = Calendar.getInstance()
                        fromDate.time = dateFormatter.parse(fromDateString) ?: return false
                        if (selectedDate.before(fromDate)) {
                            return false
                        }
                    }
                }
            }

            return true
        }

        private fun setDatePickerConstraints(datePickerDialog: DatePickerDialog, title: String) {
            val currentDate = Calendar.getInstance()

            // Set maximum date to current date (no future dates allowed)
            datePickerDialog.datePicker.maxDate = currentDate.timeInMillis

            when (title.toLowerCase()) {
                "from" -> {
                    // If "to" date is selected, set max date to "to" date
                    selectedDates["to"]?.let { toDateString ->
                        val toDate = Calendar.getInstance()
                        toDate.time = dateFormatter.parse(toDateString) ?: return@let
                        datePickerDialog.datePicker.maxDate = toDate.timeInMillis
                    }
                }
                "to" -> {
                    // If "from" date is selected, set min date to "from" date
                    selectedDates["from"]?.let { fromDateString ->
                        val fromDate = Calendar.getInstance()
                        fromDate.time = dateFormatter.parse(fromDateString) ?: return@let
                        datePickerDialog.datePicker.minDate = fromDate.timeInMillis
                    }
                }
            }
        }

        private fun showDateValidationError(title: String) {
            val errorMessage = when (title.toLowerCase()) {
                "from" -> "From date cannot be after the To date or in the future"
                "to" -> "To date cannot be before the From date or in the future"
                else -> "Invalid date selection"
            }

            android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_SHORT).show()
        }

        private fun clearNonDateSelections() {
            // Remove selections for non-date items (like "Last 3 months", "This Year", etc.)
            val dateItems = listOf("from", "to")
            val positionsToRemove = mutableSetOf<Int>()

            selectedPositions.forEach { position ->
                val item = dataList[position].toLowerCase()
                if (!dateItems.contains(item)) {
                    positionsToRemove.add(position)
                }
            }

            selectedPositions.removeAll(positionsToRemove)

            // Notify changes for the cleared items
            positionsToRemove.forEach { position ->
                notifyItemChanged(position)
            }
        }
    }
}