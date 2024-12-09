package com.revoola.fragment.start.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutHelpDialogListBinding
import com.revoola.fragment.start.RLStartHelpModelData
import com.revoola.utils.loadSvg

class RLHelpListAdapter(val context: FragmentActivity?,
    val dataList: List<RLStartHelpModelData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLHelpListAdapter"
    var bundle: Bundle = Bundle()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutHelpDialogListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_help_dialog_list , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return  dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutHelpDialogListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutHelpDialogListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            when (cardData.type){
                0->{
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.VISIBLE
                    layoutBinding.cardChallenge.visibility=View.GONE
                    layoutBinding.tvDescription.setText(cardData.text)
                }
                1->{
                    layoutBinding.tvTitle.visibility=View.VISIBLE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.cardChallenge.visibility=View.GONE
                    layoutBinding.tvTitle.setText(cardData.title)
                }
                2->{
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.cardChallenge.visibility=View.VISIBLE
                    layoutBinding.txtHeader.setText(cardData.title)
                    layoutBinding.tvDescriptionChallenge.setText(cardData.text)
                    layoutBinding.iconHelpChallenges.loadSvg(cardData.image)
                }
            }
    }
    }

}
