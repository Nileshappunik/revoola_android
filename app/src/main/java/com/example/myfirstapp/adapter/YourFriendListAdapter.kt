package com.example.myfirstapp.adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.LayoutFeedListBinding
import com.example.myfirstapp.databinding.LayoutYourFriendBinding


class YourFriendListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "YourFriendListAdapter"
    var bundle: Bundle = Bundle()
    var friendList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutYourFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_your_friend , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is YourFriendListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 5

    }


    fun setList(friendList: List<String>?) {
        this.friendList = friendList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutYourFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutYourFriendBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]


        }
    }


}
