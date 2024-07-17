package com.example.myfirstapp.fragment.start.challenges.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import java.util.*

class RLCalendarAdapter(
    private val context: Context,
    private val dates: List<Date>,
    private val calendar: Calendar,
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<RLCalendarAdapter.CalendarViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rl_item_calendar_date, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val date = dates[position]
        val day = date.date
        holder.dateText.text = day.toString()

        val today = Calendar.getInstance()
        if (date.month == calendar.get(Calendar.MONTH) && date.year == calendar.get(Calendar.YEAR)) {
            if (selectedPosition == position) {
                holder.dateText.setBackgroundResource(R.drawable.bg_selected_date)
            } else {
                holder.dateText.setBackgroundResource(R.drawable.bg_unselected_date)
            }

            holder.itemView.setOnClickListener {
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(position)
                    onDateSelected(date)
                }
            }
        } else {
            holder.dateText.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = dates.size

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }
}
