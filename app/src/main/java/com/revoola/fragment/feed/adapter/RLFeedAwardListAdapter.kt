package com.revoola.fragment.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlItemAwardBinding
import com.revoola.databinding.RlLayoutCommentUserBinding
import com.revoola.firebaseModel.AwardModel
import com.revoola.model.CommentItem

class RLFeedAwardListAdapter(val dataList: List<AwardModel>, val context: FragmentActivity?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = RLFeedAwardListAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlItemAwardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_item_award, parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return  dataList.size
    }
    inner class MyViewHolder(val binding: RlItemAwardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData= dataList[position]
            binding.ivAwardDescription.setText(cardData.notes)
            binding.ivAwardSubDescription.setText(cardData.text)

        }
    }
}