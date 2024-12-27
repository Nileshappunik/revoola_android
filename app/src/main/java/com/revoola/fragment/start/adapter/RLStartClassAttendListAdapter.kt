package com.revoola.fragment.start.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlLayoutStartClassAttendListBinding
import com.revoola.firebaseModel.RLChallengeRiderBody
import com.revoola.interfaceall.RLItemClickListener
import kotlin.math.roundToInt

class RLStartClassAttendListAdapter( val dataList: MutableList<RLChallengeRiderBody>,val context: Context, private val RLItemClickListener: RLItemClickListener) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLStartClassAttendListAdapter"
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
        return dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutStartClassAttendListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding:RlLayoutStartClassAttendListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData:RLChallengeRiderBody= dataList[position]
            layoutBinding.txtSrno.setText((position+1).toString())

            layoutBinding.txtFriendName.setText(cardData.displayName)
            Glide.with(context).load(cardData.displayImage).into(layoutBinding.imgFriend)
            Glide.with(context).load(cardData.flagImage).into(layoutBinding.imgCountryFlag)

            val totalRev : Double =( cardData.totalRev?:0.00).toString().toDouble()
            val zoneColor=RLTools.RLGetZoneColor(RLTools.RLGetZoneNo(totalRev.roundToInt()))
            layoutBinding.txtCount.setTextColor(Color.parseColor(zoneColor))
            layoutBinding.txtCount.setText(cardData.totalRev.toString())

            layoutBinding.relayAttendlist.setOnClickListener {
                RLItemClickListener.onItemClick(position)
            }
        }
    }

}
