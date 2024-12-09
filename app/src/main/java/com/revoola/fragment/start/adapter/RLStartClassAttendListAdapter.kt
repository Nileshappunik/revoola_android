package com.revoola.fragment.start.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutStartClassAttendListBinding
import com.revoola.interfaceall.RLItemClickListener

class RLStartClassAttendListAdapter(val context: Context, private val RLItemClickListener: RLItemClickListener) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLStartClassAttendListAdapter"
     val dataList = mutableListOf<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutStartClassAttendListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_class_attend_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
      // return dataList.size
       return 30
    }

    fun RLaddData(newData:List<String>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    inner class MyViewHolder(layoutBinding: RlLayoutStartClassAttendListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding:RlLayoutStartClassAttendListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val cardData:RLBleListModel= dataList[position]
            layoutBinding.txtSrno.setText((position+1).toString())
            layoutBinding.relayAttendlist.setOnClickListener {
                RLItemClickListener.onItemClick(position)
            }
        }
    }


}
