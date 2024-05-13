package com.example.myfirstapp.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.LayoutGroupListBinding

class FeedGroupNameAdapter (val context: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "FeedGroupNameAdapter"
    var clickListner: ClickListner? = null
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: LayoutGroupListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_group_list, parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is FeedGroupNameAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {

        return 50
    }

    interface ClickListner {
        fun onSelectClick(name: String, id: String)
    }

    fun seOnClickListners(clickListner: ClickListner) {
        this.clickListner = clickListner
    }

    var groupnamelistList: List<String>? = null
   
    fun setListPData(groupnamelistList: List<String>?) {
        this.groupnamelistList = groupnamelistList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutGroupListBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutGroupListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {

            //layoutBinding.tvDemo.text = productfilterList!![position].file_no
            itemVIew.setOnClickListener(View.OnClickListener {
                  clickListner!!.onSelectClick("123", "15")
            })
        }
    }


}