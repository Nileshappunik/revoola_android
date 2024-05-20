package com.example.myfirstapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.LayoutFeedListBinding
import com.example.myfirstapp.databinding.LayoutMindClassesListBinding
import com.example.myfirstapp.fragment.feed.FragSessionSummary


class MindClassListAdapter(val context: FragmentActivity?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "MindClassListAdapter"
    var bundle: Bundle = Bundle()
    var feedList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutMindClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_mind_classes_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MindClassListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 15

    }


    fun setList(feedList: List<String>?) {
        this.feedList = feedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutMindClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutMindClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]


        }
    }


}
