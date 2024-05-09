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


class FeedListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "FeedListAdapter"
    var bundle: Bundle = Bundle()
    var feedList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutFeedListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_feed_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FeedListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 5

    }


    fun setList(feedList: List<String>?) {
        this.feedList = feedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutFeedListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutFeedListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layCalories.txtTime.setText(R.string.calorie)
            layoutBinding.layCalories.txtTimeNumber.setText("0")

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_heart_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.assumedeffort)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText("0")

            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.fd_steps_green)
            layoutBinding.laySteps.txtTime.setText(R.string.step)
            layoutBinding.laySteps.txtTimeNumber.setText("0")

        }
    }


}
