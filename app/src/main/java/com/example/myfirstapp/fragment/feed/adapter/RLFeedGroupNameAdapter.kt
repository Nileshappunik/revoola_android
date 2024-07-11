package com.example.myfirstapp.fragment.feed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutGroupFeedListBinding
import com.example.myfirstapp.databinding.RlLayoutGroupListBinding
import com.example.myfirstapp.model.RLGroupCardModel


class RLFeedGroupNameAdapter (val context: Activity, val radioenable:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedGroupNameAdapter"
    var clickListner: ClickListner? = null
    private val dataList =mutableListOf<RLGroupCardModel>()
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutGroupFeedListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_group_feed_list, parent, false)
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

    fun RLaddData(newData: List<RLGroupCardModel>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    inner class MyViewHolder(layoutBinding: RlLayoutGroupFeedListBinding) :
        RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutGroupFeedListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata= dataList[position]
            layoutBinding.tvGroupName.setText(carddata.group_name)

            itemVIew.setOnClickListener(View.OnClickListener {
                  clickListner!!.onSelectClick(carddata.group_name, carddata.group_id)
            })
        }

    }


}