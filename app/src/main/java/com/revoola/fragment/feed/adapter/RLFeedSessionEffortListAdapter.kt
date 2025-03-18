package com.revoola.fragment.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlCardFeedEffortBinding
import com.revoola.model.RLZoneChartScoreData


class RLFeedSessionEffortListAdapter(private val context: FragmentActivity?,
                                     private val dataList: List<RLZoneChartScoreData>,
                                     private val totalTime:Int ) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedSessionEffortListAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlCardFeedEffortBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_card_feed_effort, parent, false)
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

    inner class MyViewHolder(layoutBinding: RlCardFeedEffortBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlCardFeedEffortBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            val scoreNumber = String.format("%.0f", cardData.totalRev) // Removed the percentage symbol
            val timezoneNumber = toHHMMSS(cardData.seconds) // Convert seconds to HH:MM:SS
            val timeinZone = String.format("%d%%", (cardData.seconds.toDouble() / totalTime * 100).toInt()) // Ensure percentage calculation


            layoutBinding.txtZoneTitle.setText(cardData.zoneName)
            layoutBinding.txtScoreNumber.setText(scoreNumber)
            layoutBinding.txtTimezoneNumber.setText(timezoneNumber)
            layoutBinding.txtInzoneNumber.setText(timeinZone)
        }
        private fun toHHMMSS(totalSeconds: Int): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }


}
