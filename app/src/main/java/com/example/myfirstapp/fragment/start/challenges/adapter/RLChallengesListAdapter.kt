package com.example.myfirstapp.fragment.start.challenges.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlCommonChallengesTypeCardBinding
import com.example.myfirstapp.enumclass.RLTypeOfChallenges

class RLChallengesListAdapter
    (val context: FragmentActivity?, val  dataList: List<RLTypeOfChallenges>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLChallengesListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlCommonChallengesTypeCardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_common_challenges_type_card , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlCommonChallengesTypeCardBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlCommonChallengesTypeCardBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata = dataList[position]
            layoutBinding.txtTypeTitle.setText(carddata.title)
            layoutBinding.imgType.setImageResource(carddata.image)

           /* if (position % 2 == 0) {
                // Even positionget

            } else {
                // Odd position get

            }*/
        }
    }


}
