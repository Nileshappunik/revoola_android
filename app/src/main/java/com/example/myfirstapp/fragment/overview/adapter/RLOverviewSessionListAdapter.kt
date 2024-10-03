package com.example.myfirstapp.fragment.overview.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutOveriviewSessionListBinding
import com.example.myfirstapp.model.RLSessionitemset

class RLOverviewSessionListAdapter(val context: FragmentActivity?, val textColorSetWhite:Boolean) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLOverviewSessionListAdapter"
    var bundle: Bundle = Bundle()
    var sessionList= mutableListOf<RLSessionitemset>()
    var istextColorSetWhite= textColorSetWhite

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutOveriviewSessionListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_overiview_session_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       return sessionList.size


    }

    fun RLsetList(newData: List<RLSessionitemset>,textColorSetWhite:Boolean) {
        sessionList.clear()
        sessionList.addAll(newData)
        istextColorSetWhite=textColorSetWhite
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: RlLayoutOveriviewSessionListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutOveriviewSessionListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata = sessionList[position]
            layoutBinding.txtsessiontitle.setText(carddata.name)
            layoutBinding.txtNumber.setText(carddata.number)
            layoutBinding.imgsessionimage.setImageResource(carddata.imageset)

            if(istextColorSetWhite){
                layoutBinding.txtsessiontitle.setTextColor(context!!.resources.getColor(R.color.AppWhiteColor))
                layoutBinding.txtNumber.setTextColor(context!!.resources.getColor(R.color.AppWhiteColor))
            }else{
                layoutBinding.txtsessiontitle.setTextColor(context!!.resources.getColor(R.color.AppBlackColor))
                layoutBinding.txtNumber.setTextColor(context!!.resources.getColor(R.color.AppBlackColor))
            }

            if (position % 2 == 0) {
                // Even positionGet
                layoutBinding.viewEven.visibility=View.VISIBLE
                layoutBinding.viewOdd.visibility=View.GONE
            } else {
                // Odd positionGet
                layoutBinding.viewEven.visibility=View.GONE
                layoutBinding.viewOdd.visibility=View.VISIBLE
            }


        }
    }


}
