package com.example.myfirstapp.fragment.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlCardFeedEffortBinding


class RLFeedSessionEffortListAdapter(val context: FragmentActivity?, val dataList: List<String>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            val carddata = dataList[position]
            layoutBinding.txtZoneTitle.setText(carddata)
        }
    }


}
