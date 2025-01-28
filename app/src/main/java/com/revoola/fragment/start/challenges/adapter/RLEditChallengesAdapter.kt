package com.revoola.fragment.start.challenges.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.ItemEditChallengesBinding
import com.revoola.databinding.RlLayoutStartMenuBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.start.challenges.RLFragChallengesFor
import com.revoola.fragment.start.challenges.model.RLEditChallenge
import com.revoola.utils.loadSvg
import java.util.Date

class RLEditChallengesAdapter(val context: FragmentActivity?,
                              val  dataList: List<RLEditChallenge>, private val onEditChallengeSelected: (String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLEditChallengesAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: ItemEditChallengesBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_edit_challenges , parent, false)
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

    inner class MyViewHolder(layoutBinding: ItemEditChallengesBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: ItemEditChallengesBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
           layoutBinding.textTitle.setText(cardData.title)
            layoutBinding.icIcon.setImageResource(cardData.icon)
            if (cardData.subTitle.isNullOrEmpty()){
                layoutBinding.textSubTitle.visibility=View.GONE
            }else{
                layoutBinding.textSubTitle.visibility=View.VISIBLE
                layoutBinding.textSubTitle.setText(cardData.subTitle)
            }

            if (cardData.isTagetEditable){
                layoutBinding.icEditIcon.visibility=View.VISIBLE
            }else{
                layoutBinding.icEditIcon.visibility=View.GONE
            }

            layoutBinding.icEditIcon.setOnClickListener {
                onEditChallengeSelected(cardData.fragmentName)
            }

        }

    }

}
