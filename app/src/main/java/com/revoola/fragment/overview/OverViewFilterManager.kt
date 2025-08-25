package com.revoola.fragment.overview

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.GridLayoutManager
import com.revoola.R
import com.revoola.databinding.RlFilterOverviewBinding
import com.revoola.fragment.overview.adapter.RLOverviewFilterListAdapter
import com.revoola.fragment.overview.adapter.RLOverviewFilterListMultipleSelectedAdapter
import com.revoola.utils.RLPrefManager
class OverViewFilterManager(
    private val context: Context,
    private val onFilterSelected: (String?, String?, String, String, List<String>) -> Unit
) {
    private val filterListPeriod = listOf("This Month", "Last 3 Months", "Last 6 Months", "This Year","From","To")
    private val filterListSource = listOf("All Available", "Revoola Only")
    private val filterListType = listOf("All", "Walk", "Run","Ride","Workout","HIIT",  "Yoga", "Pilates", "Dance", "Other")


    private var toDate: String = RLPrefManager.rl_getSomeStringValue(context, "toDate", "")
    private var fromDate: String = RLPrefManager.rl_getSomeStringValue(context, "fromDate", "")
    private var selectionPeriod: String = "This Month"
    private var selectionSource: String = RLPrefManager.rl_getSomeStringValue(context, "selectionSource", "All Available")
    private var selectionType: MutableList<String> = RLPrefManager.rl_getSomeStringListValue(context, "selectionType", mutableListOf())

    // Use RLPrefManager to retrieve selected positions from SharedPreferences
    private var selectedPositionsSource: List<Int> = RLPrefManager.rl_getSomeIntListValue(context, "selectedPositionsSource")
    private var selectedPositionsPeriod: List<Int> = RLPrefManager.rl_getSomeIntListValue(context, "selectedPositionsPeriod")

    // Method to initialize the filter dialog
    fun showFilterDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = RlFilterOverviewBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Set the default values for the filter text views
        binding.txtThisMonth.text = if (toDate.isEmpty() && fromDate.isEmpty()) selectionPeriod else "$fromDate - $toDate"
        binding.txtAll.text = if (selectionType.size == 1) selectionType[0] else "MULTI"

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

        // Show results button click listener
        binding.btnShowResults.setOnClickListener {
            // Save the filter selections to SharedPreferences
            RLPrefManager.rl_setSomeStringValue(context, "toDate", toDate)
            RLPrefManager.rl_setSomeStringValue(context, "fromDate", fromDate)
            RLPrefManager.rl_setSomeStringValue(context, "selectionSource", selectionSource)
            RLPrefManager.rl_setSomeStringListValue(context, "selectionType", selectionType)

            // Save selected positions
            RLPrefManager.rl_setSomeIntListValue(context, "selectedPositionsSource", selectedPositionsSource)
            RLPrefManager.rl_setSomeIntListValue(context, "selectedPositionsPeriod", selectedPositionsPeriod)

            onFilterSelected(fromDate, toDate, selectionPeriod, selectionSource, selectionType)
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
        val adapterPeriod = RLOverviewFilterListAdapter(
            context, toDate, fromDate, filterListPeriod, selectedPositionsPeriod.toMutableList(),
            { selectionData, selectionType -> handlePeriodSelection(selectionData) },
            { selectedPositions ->
                selectedPositionsPeriod = selectedPositions // Update the selected positions for period
                RLPrefManager.rl_setSomeIntListValue(context, "selectedPositionsPeriod", selectedPositionsPeriod) // Save to SharedPreferences
            }
        )
        val layoutManagerPeriod = GridLayoutManager(context, 2)
        binding.recyclePeriod.layoutManager = layoutManagerPeriod
        binding.recyclePeriod.adapter = adapterPeriod
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
        binding.recyclePeriod.visibility = if (binding.recyclePeriod.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.activityLayout.visibility = View.GONE
    }

    private fun toggleActivityList(binding: RlFilterOverviewBinding) {
        binding.activityLayout.visibility = if (binding.activityLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        binding.recyclePeriod.visibility = View.GONE
    }

    private fun handlePeriodSelection(selectionData: String) {
        // Handle period selection (from date or to date)
        when (selectionData.toLowerCase()) {
            "from" -> {
                // Set from date logic
                fromDate = selectionData
                selectionPeriod = "This Month"
            }
            "to" -> {
                // Set to date logic
                toDate = selectionData
                selectionPeriod = "This Month"
            }
            else -> {
                fromDate = ""
                toDate = ""
                selectionPeriod = selectionData
            }
        }
    }

    private fun handleSourceSelection(selectionData: String) {
        // Handle source selection
        selectionSource = selectionData
    }

    private fun handleTypeSelection(selectedItems: List<String>) {
        // Handle type selection (multiple selection)
        selectionType = selectedItems.toMutableList()
    }

    // Additional helper methods for validation, resetting filters, etc.
}




