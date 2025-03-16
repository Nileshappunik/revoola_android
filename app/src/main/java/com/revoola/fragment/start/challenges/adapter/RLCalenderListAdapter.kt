package com.revoola.fragment.start.challenges.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlItemCalendarDateBinding
import com.revoola.enumclass.RLDateType
import com.revoola.fragment.start.challenges.model.RLDateInfoModel
import com.revoola.fragment.start.challenges.model.RLMonthInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RLCalenderListAdapter(
    private val context: Context,
    private val dates: List<RLDateInfoModel>,
    private var selectionDate:Date?,
    private val onDateSelected: (Date) -> Unit
    ) :RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
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
            val cardData=dates[position]
            if (selectionDate!=null && RlBothDateCheck(selectionDate!!,cardData.date)){
                selectedPosition=position
            }
            when(cardData.dateType){
                RLDateType.BLANK-> RLTools.RlLogDPrint(TAG,"BLANK:- ${cardData.date}")
                RLDateType.OLD-> {
                    layoutBinding.dateText.text = (cardData.date.date).toString()
                    if (RlISCurrentDateCheck(cardData.date)){
                        //When Current Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                    }else{
                        //When Old Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                }
                RLDateType.CURRENT-> {
                    layoutBinding.dateText.text = (cardData.date.date).toString()
                    if (RlISCurrentDateCheck(cardData.date)){
                        //When Current Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                        if (selectedPosition == position) {
                            //When Selection new Date
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                        }else {
                            //When No Selection new Date
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                        }
                    }else{
                        //When Current New Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                        if (selectedPosition == position) {
                            //When Selection new Date
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                        }else {
                            //When No Selection new Date
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                        }
                    }
                    layoutBinding.layDate.setOnClickListener {
                        RLClickHandle(cardData)
                    }
                }
                RLDateType.NEW-> {
                    layoutBinding.dateText.text = (cardData.date.date).toString()
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    if (selectedPosition == position) {
                        // //When Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                    }else {
                        // //When No Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                    layoutBinding.layDate.setOnClickListener {
                        RLClickHandle(cardData)
                    }
                }
                else-> {
                    layoutBinding.dateText.text = (cardData.date.date).toString()
                    layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    if (selectedPosition == position) {
                        // //When Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                    }else {
                        // //When No Selection new Date
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                    layoutBinding.layDate.setOnClickListener {
                        RLClickHandle(cardData)
                    }
                }
            }
        }

        private fun RLClickHandle(cardData: RLDateInfoModel){
            if (selectedPosition != position) {
                notifyItemChanged(selectedPosition)
                selectionDate=cardData.date
                selectedPosition = position
                notifyItemChanged(position)
                onDateSelected(cardData.date)
            }
        }

        private fun RlISCurrentDateCheck(date: Date):Boolean{
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

        private fun RlBothDateCheck(date: Date,srDate:Date):Boolean{
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate=dateFormat.format(date)
            val serverDate=dateFormat.format(srDate)
            if (currentDate.equals(serverDate)){
                return true
            }else{
                return false
            }
        }

    }

}

//OLD CODE
/*
if (cardData.dateType.equals(RLDateType.BLANK)){
    //blanck
}
else if (cardData.dateType.equals(RLDateType.OLD)){

    val date = cardData.date
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

}
else if (cardData.dateType.equals(RLDateType.CURRENT)){
    val date = cardData.date
    val day = date.date
    layoutBinding.dateText.text = day.toString()

    layoutBinding.layDate.setOnClickListener {
        RLClickHandle(cardData)
    }
    if (RlISCurrentDateCheck(date)){
        //When Current Date
        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
        // layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
        if (selectedPosition == position) {
            // //When Selection new Date
            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
        }else {
            // //When No Selection new Date
            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
        }
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
    val date = cardData.date
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
        RLClickHandle(cardData)
    }
}*/
