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
import com.example.myfirstapp.databinding.RlLayoutYourGroupBinding
import com.example.myfirstapp.model.RLyourGroupDataModel

class RLYourGroupListAdapter(val context: FragmentActivity?, val groupList: List<RLyourGroupDataModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var dataList: List<RLyourGroupDataModel> = groupList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutYourGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_group , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       return dataList.size
    }


    inner class MyViewHolder(layoutBinding: RlLayoutYourGroupBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourGroupBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata = dataList[position]
            layoutBinding.txtGroupName.setText(carddata.group_name)
            layoutBinding.txtGroupNoofmembers.setText("Number of Members: " +carddata.number_of_members.toString())
            Glide.with(context!!).load(carddata.group_avatar)
                .placeholder(R.drawable.wellcome)
                .error(R.drawable.wellcome)
                .into(layoutBinding.imgGroup)
        }
    }

    fun RLfilter(query: String) {
        dataList = if (query.isEmpty()) {
            groupList
        } else {
            groupList.filter {it.group_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

}
