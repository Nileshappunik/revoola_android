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
import com.example.myfirstapp.databinding.LayoutYourGroupBinding

class YourGroupListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "YourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var groupList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutYourGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_your_group , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is YourGroupListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 5

    }


    fun setList(groupList: List<String>?) {
        this.groupList = groupList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutYourGroupBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutYourGroupBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]


        }
    }


}
