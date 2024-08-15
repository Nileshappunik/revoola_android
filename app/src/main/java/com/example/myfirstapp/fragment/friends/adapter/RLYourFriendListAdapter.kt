package com.example.myfirstapp.fragment.friends.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutYourFriendBinding
import com.example.myfirstapp.model.RLuserData

class RLYourFriendListAdapter(val context: FragmentActivity?, val friendList: List<RLuserData>,val isFollowHide:Boolean) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourFriendListAdapter"
    var bundle: Bundle = Bundle()
    var datalist:List<RLuserData> = friendList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutYourFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_friend , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlLayoutYourFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourFriendBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = datalist[position]
            Glide.with(context!!).load(cardData.avatar)
                .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                .into(layoutBinding.imgFriend)
            layoutBinding.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)

            if (isFollowHide){
                layoutBinding.txtFriendUnfollow.visibility=View.GONE
            }else{
                layoutBinding.txtFriendUnfollow.visibility=View.VISIBLE
            }
        }
    }
    fun RLfilter(query: String) {
        datalist = if (query.isEmpty()) {
            friendList
        } else {
            friendList.filter { it.first_name.contains(query, ignoreCase = true) || it.last_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


}
