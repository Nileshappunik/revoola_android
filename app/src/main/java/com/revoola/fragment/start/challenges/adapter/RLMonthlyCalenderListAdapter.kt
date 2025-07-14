package com.revoola.fragment.start.challenges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlItemCalendarDateBinding
import com.revoola.enumclass.RLDateType
import com.revoola.fragment.start.challenges.model.RLMonthInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RLMonthlyCalenderListAdapter(
    private val context: Context,
    private val dates: List<RLMonthInfoModel>,private var selectionAllReadyMonth:String,
    private val onDateSelected: (RLMonthInfoModel) -> Unit
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
            val cardData=dates[position]
            layoutBinding.dateText.visibility=View.GONE
            layoutBinding.monthText.visibility=View.VISIBLE

            if (!selectionAllReadyMonth.isNullOrEmpty() && selectionAllReadyMonth.equals(cardData.monthNameWithYear)){
                selectedPosition=position
            }

            when(cardData.dateType){
                RLDateType.BLANK-> RLTools.rl_logDPrint(TAG,"BLANK:- ${cardData.date}")
                RLDateType.OLD-> {
                    layoutBinding.monthText.text = cardData.date.toString()
                    if (RlISCurrentDateCheck(cardData.monthNameWithYear)){
                        //When Current Date
                        layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                    }else{
                        //When Old Date
                        layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                }
                RLDateType.CURRENT-> {
                    layoutBinding.monthText.text = cardData.date.toString()
                    if (RlISCurrentDateCheck(cardData.monthNameWithYear)){
                        //When Current Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
                    }else{
                        //When Current New Date
                        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
                        if (selectedPosition == position) {
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                        }else {
                            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                        }
                        layoutBinding.layDate.setOnClickListener {
                            RLClickHandle(cardData)
                        }
                    }
                }
                RLDateType.NEW-> {
                    layoutBinding.monthText.text = cardData.date.toString()
                    layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    if (selectedPosition == position) {
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                    }else {
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                    layoutBinding.layDate.setOnClickListener {
                        RLClickHandle(cardData)
                    }
                }
                else-> {
                    layoutBinding.monthText.text = cardData.date.toString()
                    layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
                    if (selectedPosition == position) {
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
                    }else {
                        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
                    }
                    layoutBinding.layDate.setOnClickListener {
                        RLClickHandle(cardData)
                    }
                }
            }
        }

        private fun RLClickHandle(cardData:RLMonthInfoModel){
            if (selectedPosition != position) {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                selectionAllReadyMonth=cardData.monthNameWithYear
                notifyItemChanged(position)
                onDateSelected(cardData)
            }
        }
        private fun RlISCurrentDateCheck(input: String): Boolean {
            val dateFormat = SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)
            val inputDate = Calendar.getInstance().apply { time = dateFormat.parse(input)!! }

            val currentDate = Calendar.getInstance()

            return inputDate.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                    inputDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)
        }

    }

}

//Old Code
/*
if (dateType.equals(RLDateType.BLANK)){
    //blanck
}
else if (dateType.equals(RLDateType.OLD)){
    val month = cardData.date
    layoutBinding.monthText.text = month.toString()
    layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)

    if (RlISCurrentDateCheck(cardData.monthNameWithYear)){
        //When Current Date
        layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)
    }else{
        //When Old Date
        layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
    }


}
else if (dateType.equals(RLDateType.CURRENT)){
    val month = cardData.date
    layoutBinding.monthText.text = month.toString()
    layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
    layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)

    if (RlISCurrentDateCheck(cardData.monthNameWithYear)){
        //When Current Date
        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_current_date)

    }else{
        //When Old Date
        layoutBinding.dateText.setTextColor(context.resources.getColor(R.color.AppTextLightGrayColor))
        // layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
        if (selectedPosition == position) {
            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
        }else {
            layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
        }

        layoutBinding.layDate.setOnClickListener {
            RLClickHandle(cardData)
        }
    }


}
else{
    val month = cardData.date
    layoutBinding.monthText.text = month.toString()
    layoutBinding.monthText.setTextColor(context.resources.getColor(R.color.AppBlackColor))
    if (selectedPosition == position) {
        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_selected_date)
    }else {
        layoutBinding.layDate.setBackgroundResource(R.drawable.bg_unselected_date)
    }
    layoutBinding.layDate.setOnClickListener {
        RLClickHandle(cardData)
    }
}
*/
