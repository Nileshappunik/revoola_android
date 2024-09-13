package com.example.myfirstapp.fragment.start.challenges.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlItemCalendarDateBinding
import com.example.myfirstapp.enumclass.RLDateType
import com.example.myfirstapp.fragment.start.challenges.model.RLDateInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RLCalenderListAdapter(
    private val context: Context,
    private val dates: List<RLDateInfoModel>,
    private val onDateSelected: (Date) -> Unit
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
            if (dateType.equals(RLDateType.BLANK)){
                //blanck
            }else if (dateType.equals(RLDateType.OLD)){

                val date = dates[position].date
                val day = date.date
                layoutBinding.dateText.text = day.toString()
               // layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
              //  layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                if (RlISCurrentDateCheck(date)){
                    //When Current Date
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                }else{
                    //When Old Date
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                }

            }else if (dateType.equals(RLDateType.CURRENT)){
                val date = dates[position].date
                val day = date.date
                layoutBinding.dateText.text = day.toString()
               // layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
               // layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                layoutBinding.layDate.setOnClickListener {
                    if (selectedPosition != position) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = position
                        notifyItemChanged(position)
                        onDateSelected(date)
                    }
                }
                if (RlISCurrentDateCheck(date)){
                    //When Current Date
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                }else{
                    //When New  Date
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    if (selectedPosition == position) {
                        // //When Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                    }else {
                        // //When No Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                }
            }
            else{
                val date = dates[position].date
                val day = date.date
                layoutBinding.dateText.text = day.toString()
                layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))

                if (selectedPosition == position) {
                    // //When Selection new Date
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                }else {
                    // //When No Selection new Date
                    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                }

                layoutBinding.layDate.setOnClickListener {
                    if (selectedPosition != position) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = position
                        notifyItemChanged(position)
                        onDateSelected(date)
                    }
                }
            }
        }

        fun RlISCurrentDateCheck(date: Date):Boolean{
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            val currentDate=dateFormat.format(calendar.time)
            val serverDate=dateFormat.format(date)
            if (currentDate.equals(serverDate)){
                return true
            }else{
                return false
            }
        }
    }

}
