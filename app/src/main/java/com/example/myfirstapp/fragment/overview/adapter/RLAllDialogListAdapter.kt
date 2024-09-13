package com.example.myfirstapp.fragment.overview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutAllDialogListBinding

class RLAllDialogListAdapter(
    private val context: Context,
    private val data: Array<String>,
    private val onDataSelected: (String) -> Unit
    ) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLAllDialogListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutAllDialogListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_all_dialog_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutAllDialogListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutAllDialogListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val datastring = data[position]
            layoutBinding.tvmindbody.setText(datastring)
            layoutBinding.layoutDialogAll.setOnClickListener {
                onDataSelected(datastring)
            }

        }
    }

}
