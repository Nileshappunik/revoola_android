package com.revoola.fragment.feed.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlCommonSessionSummaryCardBinding
import com.revoola.enumclass.RLMetricData
import com.revoola.enumclass.RLTypeOfMetrics
import com.revoola.fragment.feed.RLFragFeedCardLikeCommentView
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools


class RLFeedSessionSummryListAdapter(
    val context: FragmentActivity?,
    val dataList: List<Pair<RLTypeOfMetrics, RLMetricData>>,
    val cardData: RLTextOverview
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedSessionSummryListAdapter"

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

    inner class MyViewHolder(val layoutBinding: RlCommonSessionSummaryCardBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
       // private val layoutBinding: RlCommonSessionSummaryCardBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val (typeOfMetric, metricData) = dataList[position]

            layoutBinding.txtsessiontitle.setText(typeOfMetric.title)
            layoutBinding.imgsessionimage.setImageResource(typeOfMetric.image)
            layoutBinding.txtNumber.setText(metricData.value)
            if (typeOfMetric.title.equals("EFFORT ZONE")){
                val ZoneTextData= RLTools.rl_verifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
                layoutBinding.txtNumber.setText(ZoneTextData.efforZoneText)
                layoutBinding.txtNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
            }
            if (typeOfMetric.showright){
                layoutBinding.imgright.visibility=View.VISIBLE
            }else{
                layoutBinding.imgright.visibility=View.GONE
            }
            layoutBinding.imgright.setOnClickListener {
                var passstring=""
                if (typeOfMetric.title.equals("COMMENTS")){
                    passstring="Comment"
                } else if (typeOfMetric.title.equals("BOOSTS")){
                    passstring="Thumb"
                }
                if (passstring.isNotEmpty()){
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    bundle.putString(RLConstants.TYPE, passstring)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
                }
            }
            if (position % 2 == 0) {
                //Even position get
                layoutBinding.viewEven.visibility=View.VISIBLE
                layoutBinding.viewOdd.visibility=View.GONE
            } else {
                //Odd position get
                layoutBinding.viewEven.visibility=View.GONE
                layoutBinding.viewOdd.visibility=View.VISIBLE
            }
        }
    }

}
