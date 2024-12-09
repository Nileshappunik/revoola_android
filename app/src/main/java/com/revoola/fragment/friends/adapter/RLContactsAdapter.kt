package com.revoola.fragment.friends.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutYourGroupBinding
import com.revoola.model.RLContactModel

class RLContactsAdapter(val context: FragmentActivity?, val contactsList: List<RLContactModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var dataList: List<RLContactModel> = contactsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutYourGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_group , parent, false)
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


    inner class MyViewHolder(layoutBinding: RlLayoutYourGroupBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourGroupBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtGroupName.setText(cardData.name)
            layoutBinding.txtGroupNoofmembers.setText(cardData.phoneNumber)
            layoutBinding.txtGroupMember.visibility=View.GONE
       }
    }

    fun RLfilter(query: String) {
        dataList = if (query.isEmpty()) {
            contactsList
        } else {
            contactsList.filter {it.name.contains(query, ignoreCase = true) ||it.phoneNumber.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

}
