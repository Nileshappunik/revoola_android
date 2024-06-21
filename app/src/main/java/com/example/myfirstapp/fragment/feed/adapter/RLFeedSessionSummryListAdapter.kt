package com.example.myfirstapp.fragment.feed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlCommonSessionSummaryCardBinding
import com.example.myfirstapp.enumclass.RLMetricData
import com.example.myfirstapp.enumclass.RLTypeOfMetrics


class RLFeedSessionSummryListAdapter(val context: FragmentActivity?, val  dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "OverviewSessionSummryListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlCommonSessionSummaryCardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_common_session_summary_card , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlCommonSessionSummaryCardBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlCommonSessionSummaryCardBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val (typeOfMetric, metricData) = dataList[position]

            layoutBinding.txtsessiontitle.setText(typeOfMetric.title)
            layoutBinding.imgsessionimage.setImageResource(typeOfMetric.image)
            layoutBinding.txtNumber.setText(metricData.value)
            if (typeOfMetric.title.equals("EFFORT ZONE")){
                layoutBinding.txtNumber.setTextColor(context!!.resources.getColor(R.color.AppZone1Color))
            }
            if (typeOfMetric.showright){
                layoutBinding.imgright.visibility=View.VISIBLE
            }else{
                layoutBinding.imgright.visibility=View.GONE
            }
            if (position % 2 == 0) {
                // Even positionget
                layoutBinding.viewEven.visibility=View.VISIBLE
                layoutBinding.viewOdd.visibility=View.GONE
            } else {
                // Odd position get
                layoutBinding.viewEven.visibility=View.GONE
                layoutBinding.viewOdd.visibility=View.VISIBLE
            }
        }
    }


}
