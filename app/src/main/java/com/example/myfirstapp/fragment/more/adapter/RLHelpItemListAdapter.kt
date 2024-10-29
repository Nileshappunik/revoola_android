package com.example.myfirstapp.fragment.more.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlExpandableListItemMoreBinding
import com.example.myfirstapp.fragment.more.RLFragGetStarted
import com.example.myfirstapp.model.RLMoreGroupItemModel

class RLHelpItemListAdapter(
    val context: FragmentActivity?,
    val dataList: List<RLMoreGroupItemModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLHelpItemListAdapter"
    var bundle: Bundle = Bundle()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlExpandableListItemMoreBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_expandable_list_item_more , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlExpandableListItemMoreBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlExpandableListItemMoreBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtAccount.setText(cardData.title)
            layoutBinding.imgAccount.setImageResource(cardData.icon)
            layoutBinding.layMoreClick.setBackgroundColor(context!!.resources.getColor(R.color.AppWhiteColor))
            layoutBinding.layMoreClick.setOnClickListener {
                when(cardData.title){
                    context.resources.getString(R.string.aquickintroduction)->{

                    }
                    context.resources.getString(R.string.getttingstarted)->{
                        (context as RLMainActivityRL).RLloadFrag(RLFragGetStarted(), TAG, true, RLFragGetStarted::class.java.simpleName, false)
                    }
                    context.resources.getString(R.string.faqs)->{

                    }
                }
            }

        }
    }

}
