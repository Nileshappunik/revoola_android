package com.example.myfirstapp.fragment.start.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutStartClassAttendListBinding

class RLStartClassAttendListAdapter(val context: FragmentActivity?) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
       return 7
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
        }
    }


}
