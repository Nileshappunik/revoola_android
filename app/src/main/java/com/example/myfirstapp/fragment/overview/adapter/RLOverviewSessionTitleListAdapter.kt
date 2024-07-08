package com.example.myfirstapp.fragment.overview.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutOveriviewSessionTitleListBinding
import com.example.myfirstapp.interfaceall.RLItemClickListener


class RLOverviewSessionTitleListAdapter(texttypeset: String, private val RLItemClickListener: RLItemClickListener, valueslist: Array<String>, val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLOverviewSessionTitleListAdapter"
    var bundle: Bundle = Bundle()
    var titleList = valueslist
    var texttypeset=texttypeset



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutOveriviewSessionTitleListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_overiview_session_title_list, parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       return titleList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutOveriviewSessionTitleListBinding) : RecyclerView.ViewHolder(layoutBinding.root){
        private val layoutBinding: RlLayoutOveriviewSessionTitleListBinding = layoutBinding

        fun bindData(position: Int, itemVIew: View) {
            val itemres = titleList[position]
            layoutBinding.txtTitleSession.setText(itemres)
            //layoutBinding.viewSession.width=layoutBinding.txtTitleSession.width
            if (texttypeset.equals(itemres)){
                layoutBinding.txtTitleSession.setTextColor(context!!.resources.getColor(R.color.AppMainColor))
                layoutBinding.viewSession.setBackgroundResource(R.color.AppMainColor)
            }else{
                layoutBinding.txtTitleSession.setTextColor(context!!.resources.getColor(R.color.AppBlackColor))
                layoutBinding.viewSession.setBackgroundResource(R.color.AppWhiteColor)
            }
            layoutBinding.txtTitleSession.setOnClickListener {
                texttypeset=itemres
                RLItemClickListener.onItemClick(position)
               notifyDataSetChanged()

            }
        }

    }


}
