package com.revoola.fragment.guest

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
import com.revoola.databinding.RlItemQuestionBinding
import com.revoola.enumclass.RLDateType
import com.revoola.fragment.start.challenges.model.RLDateInfoModel
import com.revoola.fragment.start.challenges.model.RLMonthInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RLQuestionListAdapter(
    private val context: Context,
    private val questionList: List<RLQuestionModel>,
    private val onDateSelected: (RLQuestionModel) -> Unit) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLQuestionListAdapter"
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlItemQuestionBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_item_question , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    inner class MyViewHolder(layoutBinding: RlItemQuestionBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlItemQuestionBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData=questionList[position]
            if (selectedPosition==position){
                layoutBinding.ivButton.setImageResource(R.drawable.ic_checkboxchecked)
            }else{
                layoutBinding.ivButton.setImageResource(R.drawable.ic_un_checkbox)
            }

            if (position+1==questionList.size){
                layoutBinding.dividerView.visibility=View.GONE
            }else{
                layoutBinding.dividerView.visibility=View.VISIBLE
            }

            layoutBinding.ivDemo.setImageResource(cardData.image)
            layoutBinding.tvDescription.setText(cardData.Description)
            layoutBinding.ivButton.setOnClickListener {
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(position)
                    onDateSelected(cardData)
                }
            }
        }

    }

}


