package com.revoola.fragment.start.challenges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlItemCalendarDateBinding
import com.revoola.enumclass.RLDateType
import com.revoola.fragment.start.challenges.model.RLMonthInfoModel

class RLMonthlyCalenderListAdapter(
    private val context: Context,
    private val dates: List<com.revoola.fragment.start.challenges.model.RLMonthInfoModel>,
    private val onDateSelected: (String) -> Unit
    ) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLCalenderAdapter"
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlItemCalendarDateBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_item_calendar_date , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return dates.size
    }

    inner class MyViewHolder(layoutBinding: RlItemCalendarDateBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlItemCalendarDateBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val dateType = dates[position].dateType
            layoutBinding.dateText.visibility=View.GONE
            layoutBinding.monthText.visibility=View.VISIBLE
            if (dateType.equals(RLDateType.BLANK)){
                //blanck
            }else if (dateType.equals(RLDateType.OLD)){
                val month = dates[position].date
                layoutBinding.monthText.text = month.toString()
                layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
                layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
            }else if (dateType.equals(RLDateType.CURRENT)){
                val month = dates[position].date
                layoutBinding.monthText.text = month.toString()
                layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                layoutBinding.layDate.setOnClickListener {
                    if (selectedPosition != position) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = position
                        notifyItemChanged(position)
                        onDateSelected(month)
                    }
                }
            }
            else{
                val month = dates[position].date
                layoutBinding.monthText.text = month.toString()
                layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                if (selectedPosition == position) {
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                }else {
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                }
                layoutBinding.layDate.setOnClickListener {
                    if (selectedPosition != position) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = position
                        notifyItemChanged(position)
                        onDateSelected(month)
                    }
                }
            }
        }
    }

}
