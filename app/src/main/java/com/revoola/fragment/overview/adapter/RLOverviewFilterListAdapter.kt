package com.revoola.fragment.overview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlCommonFilterOverviewBinding


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
            layoutBinding.boxButton.setText(title)

            // Check if this item is selected
            val isSelected = selectedPositions.contains(position)

            // Set the background color based on selection
            if (isSelected) {
                layoutBinding.boxButton.setTextColor(ContextCompat.getColor(context, R.color.AppWhiteColor))
                layoutBinding.boxButton.setBackgroundResource(R.drawable.rl_filter_border_green_overview)
            } else {
                layoutBinding.boxButton.setTextColor(ContextCompat.getColor(context, R.color.AppMainColor))
                layoutBinding.boxButton.setBackgroundResource(R.drawable.rl_filter_border_overview)
            }

            layoutBinding.boxButton.setOnClickListener {
                handleNonDateSelection(title, position)
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
    }
}