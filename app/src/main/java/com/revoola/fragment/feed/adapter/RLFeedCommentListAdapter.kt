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
import com.revoola.databinding.RlLayoutCommentUserBinding
import com.revoola.model.CommentItem

class RLFeedCommentListAdapter(val dataList: List<CommentItem>, val context: FragmentActivity?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = RLFeedCommentListAdapter::class.java.simpleName
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
        return  dataList.size
    }
    inner class MyViewHolder(val binding: RlLayoutCommentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData= dataList[position]
            Glide.with(context!!).load(cardData.avatar).into(binding.imgUser)
            binding.txtUsername.setText(cardData.username)
            binding.txtCommet.setText(cardData.comment)
            binding.txtDate.setText(RLTools.rl_formatTimestamp(cardData.timestamp))
        }
    }
}