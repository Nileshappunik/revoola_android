package com.revoola.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutScheduledClassesListBinding

class RLScheduledClassesListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var scheduledClassesList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutScheduledClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_scheduled_classes_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return scheduledClassesList!!.size
        return 20

    }


    fun RLsetList(scheduledClassesList: List<String>?) {
        this.scheduledClassesList = scheduledClassesList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: RlLayoutScheduledClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutScheduledClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = scheduledClassesList!![position]


        }
    }


}
