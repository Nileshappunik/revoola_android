package com.revoola.fragment.start.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.fragment.start.yourway.RLFragChooseYourSensor
import com.revoola.ble.RLExtraValueKey
import com.revoola.databinding.RlLayoutStartMenuBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.utils.loadSvg

class RLYourWayListAdapter(val context: FragmentActivity?,
                           val  dataList: List<RLStartAllMenuModel>,
                           val heightTotal: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourWayListAdapter"
    var bundle: Bundle = Bundle()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutStartMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_menu , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlLayoutStartMenuBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutStartMenuBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtTypename.setText(cardData.title)
            layoutBinding.txtDescription.setText(cardData.description)
            Glide.with(context!!).load(cardData.img).into(layoutBinding.imgType)
            layoutBinding.imgTypeicon.loadSvg(cardData.type)
            
            //RelativeLayout Height set
            val layoutParams: ViewGroup.LayoutParams = layoutBinding.relayStartNew.layoutParams
            layoutParams.height =  heightTotal/4
            layoutBinding.relayStartNew.layoutParams =layoutParams

            //Image Height Width set
            val layoutParamsImage: ViewGroup.LayoutParams = layoutBinding.imgType.layoutParams
            layoutParamsImage.height =  heightTotal/5
            layoutParamsImage.width =  heightTotal/5
            layoutBinding.imgType.layoutParams =layoutParamsImage

            layoutBinding.relayStartNew.setOnClickListener {
                if (cardData.title.toLowerCase().equals("walk")){
                    RLNextViewOpen("Walk")
                }else if (cardData.title.toLowerCase().equals("run")){
                    RLNextViewOpen("Run")
                }else if (cardData.title.toLowerCase().equals("ride")){
                    RLNextViewOpen("Ride")
                }else if (cardData.title.toLowerCase().equals("workout")){
                    RLNextViewOpen("Workout")
                }
            }
        }
        fun RLNextViewOpen(name:String){
            val bundle: Bundle = Bundle()
            bundle.putString(RLExtraValueKey.yourWayType,name)
            bundle.putBoolean(RLExtraValueKey.isBody,false)
            bundle.putBoolean(RLExtraValueKey.isMind,false)
            bundle.putBoolean(RLExtraValueKey.isYourWay,true)
            (context as RLMainActivityRL).rl_loadFrag(RLFragChooseYourSensor().newInstance(bundle), TAG, true, null, false)

        }
    }


}
