package com.revoola.fragment.more.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlListItemGetStarttedBinding
import com.revoola.model.RLMoreGroupItemModel

class RLGetStartedItemListAdapter(
    val context: FragmentActivity?,
    val dataList: List<RLMoreGroupItemModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLHelpItemListAdapter"
    var bundle: Bundle = Bundle()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlListItemGetStarttedBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_list_item_get_startted , parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }


    override fun getItemCount(): Int {
        return  dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlListItemGetStarttedBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlListItemGetStarttedBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtAccount.setText(cardData.title)
            layoutBinding.imgAccount.setImageResource(cardData.icon)
            layoutBinding.layMoreClick.setBackgroundColor(context!!.resources.getColor(R.color.AppWhiteColor))
            layoutBinding.layMoreClick.setOnClickListener {
                when(cardData.title){
                    context.resources.getString(R.string.heartratesensor)->{

                    }
                    context.resources.getString(R.string.connectingaspeedsensor)->{

                    }
                    context.resources.getString(R.string.aquicktourofrevoola)->{

                    }
                    context.resources.getString(R.string.connectionapplewatch)->{

                    }
                    context.resources.getString(R.string.icantfindmysensor)->{

                    }
                    context.resources.getString(R.string.nameyoursensor)->{

                    }
                }
            }

        }
    }

}
