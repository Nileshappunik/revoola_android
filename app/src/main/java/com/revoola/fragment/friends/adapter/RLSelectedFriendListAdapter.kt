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
import com.revoola.databinding.RlLayoutSelectFriendBinding
import com.revoola.model.RLuserData

class RLSelectedFriendListAdapter(val context: FragmentActivity?, val friendList: List<RLuserData>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourFriendListAdapter"
    var bundle: Bundle = Bundle()
    var datalist:List<RLuserData> = friendList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutSelectFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_select_friend , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }
    override fun getItemCount(): Int {
       return datalist.size
    }

    inner class MyViewHolder( private val layoutBinding: RlLayoutSelectFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
       // private val layoutBinding: RlLayoutSelectFriendBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = datalist[position]
            Glide.with(context!!).load(cardData.avatar)
                .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                .into(layoutBinding.imgFriend)
            layoutBinding.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
        }
    }

}
