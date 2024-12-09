package com.revoola.fragment.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutCommentUserBinding

class RLFeedCommentListAdapter(val dataList: List<String>, val context: FragmentActivity?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedCommentListAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutCommentUserBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_comment_user, parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return 2  // dataList.size
    }
    inner class MyViewHolder(layoutBinding: RlLayoutCommentUserBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutCommentUserBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
           // val cardData= dataList[position]
        }
    }
}