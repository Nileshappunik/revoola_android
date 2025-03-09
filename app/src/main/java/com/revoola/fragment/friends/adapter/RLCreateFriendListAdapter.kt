package com.revoola.fragment.friends.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.databinding.RlLayoutYourFriendBinding
import com.revoola.model.RLUserDataParcelable
import com.revoola.model.RLuserData

class RLCreateFriendListAdapter(val context: FragmentActivity?, var datalist: List<RLUserDataParcelable>,
                                private val onSelected: (RLUserDataParcelable) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLCreateFriendListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutYourFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_friend , parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }
    override fun getItemCount(): Int {
       return datalist.size
    }

    inner class MyViewHolder(val layoutBinding: RlLayoutYourFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData = datalist[position]
            Glide.with(context!!).load(cardData.avatar)
                .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                .into(layoutBinding.imgFriend)
            layoutBinding.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
            layoutBinding.txtFriendUnfollow.setText("DELETE")
            layoutBinding.txtFriendUnfollow.setOnClickListener {
                onSelected(cardData)
                datalist -= listOf(cardData)
                notifyItemRemoved(position)
            }
        }
    }



}
