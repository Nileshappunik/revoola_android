package com.example.myfirstapp.fragment.feed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutGroupListBinding

class RLFeedSimpleAdapter(val dataList: List<String>, val context: Activity, val selectname:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedSimpleAdapter"
    var clickListner: ClickListner? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutGroupListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_group_list, parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    interface ClickListner {
        fun onSelectClick(name: String, id: String)
    }
    fun seOnClickListners(clickListner: ClickListner) {
        this.clickListner = clickListner
    }

    inner class MyViewHolder(layoutBinding: RlLayoutGroupListBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutGroupListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata= dataList[position]
            if (selectname.equals(carddata)){
                layoutBinding.tvDemoBold.setText(carddata)
                layoutBinding.tvDemoBold.visibility=View.VISIBLE
                layoutBinding.tvDemo.visibility=View.GONE
            }else{
                layoutBinding.tvDemo.setText(carddata)
                layoutBinding.tvDemoBold.visibility=View.GONE
                layoutBinding.tvDemo.visibility=View.VISIBLE
            }


            itemVIew.setOnClickListener(View.OnClickListener {
                  clickListner!!.onSelectClick(carddata, carddata)
            })
        }
    }


}