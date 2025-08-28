package com.revoola.fragment.overview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlCommonFilterOverviewBinding


class RLOverviewFilterListMultipleSelectedAdapter(
    private val context: Context,
    private val dataList: List<String>,
    private val selectionType: MutableList<String>,
    private val onDataSelected: (List<String>) -> Unit // Changed to return list of selected items){}
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLOverviewFilterListMultipleSelectedAdapter"

    // Add this: Track selected items
    private val selectedItems  = selectionType//mutableSetOf<String>("All")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlCommonFilterOverviewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_common_filter_overview , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(val layoutBinding: RlCommonFilterOverviewBinding) : RecyclerView.ViewHolder(layoutBinding.root) {

        fun bindData(position: Int, itemVIew: View) {
            val title = dataList[position]
            //layoutBinding.layoutBox.visibility = View.GONE
           // layoutBinding.layoutBoxType.visibility = View.VISIBLE
            layoutBinding.boxButton.setText(title)

            // Add this: Update visual state based on selection
            val isSelected = selectedItems.contains(title)
            if (isSelected) {
                // Selected state - customize these colors as needed
                layoutBinding.boxButton.setBackgroundResource(R.drawable.rl_filter_border_green_overview) // or setBackgroundColor
                layoutBinding.boxButton.setTextColor(context.getColor(R.color.AppWhiteColor))
            } else {
                // Unselected state
                layoutBinding.boxButton.setBackgroundResource(R.drawable.rl_filter_border_overview) // or setBackgroundColor
                layoutBinding.boxButton.setTextColor(context.getColor(R.color.AppMainColor))
            }

            layoutBinding.boxButton.setOnClickListener {
                // Handle "All" selection logic
                if (title == "All") {
                    if (selectedItems.contains("All")) {
                        // If "All" is already selected, remove it
                        selectedItems.remove("All")
                    } else {
                        // If "All" is being selected, clear everything and add only "All"
                        selectedItems.clear()
                        selectedItems.add("All")
                        // Refresh entire list to update all items' visual state
                        notifyDataSetChanged()
                        onDataSelected(selectedItems.toList())
                        return@setOnClickListener
                    }
                } else {
                    // Handle other items selection
                    if (selectedItems.contains("All")) {
                        // If "All" was selected, remove it and add the clicked item
                        selectedItems.clear()
                        selectedItems.add(title)
                        // Refresh entire list to update "All" item's visual state
                        notifyDataSetChanged()
                        onDataSelected(selectedItems.toList())
                        return@setOnClickListener
                    } else if (selectedItems.contains(title)) {
                        // Remove the item if it's already selected
                        selectedItems.remove(title)
                    } else {
                        // Add the item to selection
                        selectedItems.add(title)
                    }
                }

                // Update only this item for regular toggle operations
                notifyItemChanged(position)

                // Return all selected items
                onDataSelected(selectedItems.toList())
            }
        }
    }
}



