package com.revoola.fragment.start.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlCommonSensorListBinding
import com.revoola.fragment.start.yourway.RLFragEditYourSensor
import com.revoola.interfaceall.RLItemClickListenerAdapter
import com.revoola.utils.RLConstants
import com.google.gson.JsonParser
import com.revoola.commonobject.RLTools

class RLSensorSpeedListAdapter(val context: FragmentActivity?, private val itemClickListener: RLItemClickListenerAdapter) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLSensorSpeedListAdapter"
     val dataList = mutableListOf<RLBleListModel>()
     var connectedDeviceAddress:String=""
    var connectedDeviceType:String=""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlCommonSensorListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_common_sensor_list , parent, false)
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

    fun RLaddData(newData:List<RLBleListModel>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    inner class MyViewHolder(layoutBinding: RlCommonSensorListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding:RlCommonSensorListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            try {
                val lastConnectDeviceAddress = com.revoola.utils.RLPrefManager.RLgetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect, "")
                val cardData:RLBleListModel= dataList[position]
                layoutBinding.img1.setImageResource(R.drawable.ic_speeed)

                if (cardData.deviceAddress.equals(lastConnectDeviceAddress)){
                    connectedDeviceAddress=cardData.deviceAddress
                    connectedDeviceType=cardData.deviceType
                    layoutBinding.imgDone.setImageResource(R.drawable.ic_checkboxchecked)
                    layoutBinding.imgEdit.visibility=View.VISIBLE
                }else{
                    layoutBinding.imgDone.setImageResource(R.drawable.ic_checkbox_empty)
                    layoutBinding.imgEdit.visibility=View.GONE
                }
                layoutBinding.imgEdit.setOnClickListener {
                    var bundle: Bundle = Bundle()
                    bundle.putString("deviceType",cardData.deviceType)
                    bundle.putString("deviceAddress",cardData.deviceAddress)
                    bundle.putString("devicename",cardData.devicename)
                    (context as RLMainActivityRL).RLloadFrag(RLFragEditYourSensor().newInstance(bundle), TAG, true, null, false)
                }
                layoutBinding.imgDone.setOnClickListener {
                    if (cardData.deviceAddress.equals(lastConnectDeviceAddress)){
                        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect, "no")
                        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect_type, "")
                        itemClickListener.onItemClick(cardData.deviceType,cardData.deviceAddress,false)
                    }else{
                        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect, cardData.deviceAddress)
                        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(context, com.revoola.utils.RLPrefManager.last_device_connect_type,RLConstants.SPEEDSENSOR)
                        itemClickListener.onItemClick(cardData.deviceType,cardData.deviceAddress,true)
                    }
                    notifyDataSetChanged()
                }

                // when we Change name then display name Change
                val changeDeviceName = com.revoola.utils.RLPrefManager.RLgetSomeStringValue(context, com.revoola.utils.RLPrefManager.change_device_name, "")
                if (changeDeviceName.isNullOrEmpty()){
                    layoutBinding.txtSensorName.setText(cardData.devicename)
                }else{
                    changeDeviceName.let {
                        val jsonobject= JsonParser.parseString(it).asJsonObject
                        val jsonDeviceName:String= jsonobject.get(RLConstants.DEVICENAME).asString
                        val jsonDeviceAddress:String= jsonobject.get(RLConstants.DEVICEADDRESS).asString
                        if (jsonDeviceAddress.equals(cardData.deviceAddress)){
                            layoutBinding.txtSensorName.setText(jsonDeviceName)
                        }else{
                            layoutBinding.txtSensorName.setText(cardData.devicename)
                        }
                    }
                }

            } catch (e: Exception) {
           RLTools.RlLogEPrint(TAG, "exception= " + e.message)
            }
        }
    }


}
