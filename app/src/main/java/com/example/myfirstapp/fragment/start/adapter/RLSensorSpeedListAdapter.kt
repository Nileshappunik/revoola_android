package com.example.myfirstapp.fragment.start.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlCommonSensorListBinding
import com.example.myfirstapp.fragment.start.yourway.RLFragEditYourSensor
import com.example.myfirstapp.interfaceall.RLItemClickListenerAdapter
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.JsonParser

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
                val lastConnectDeviceAddress = RLPrefManager.RLgetSomeStringValue(context, RLPrefManager.last_device_connect, "")
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
                        RLPrefManager.RLsetSomeStringValue(context, RLPrefManager.last_device_connect, "no")
                        RLPrefManager.RLsetSomeStringValue(context, RLPrefManager.last_device_connect_type, "")
                        itemClickListener.onItemClick(cardData.deviceType,cardData.deviceAddress,false)
                    }else{
                        RLPrefManager.RLsetSomeStringValue(context, RLPrefManager.last_device_connect, cardData.deviceAddress)
                        RLPrefManager.RLsetSomeStringValue(context, RLPrefManager.last_device_connect_type,RLConstants.SPEEDSENSOR)
                        itemClickListener.onItemClick(cardData.deviceType,cardData.deviceAddress,true)
                    }
                    notifyDataSetChanged()
                }

                // when we Change name then display name Change
                val changeDeviceName = RLPrefManager.RLgetSomeStringValue(context, RLPrefManager.change_device_name, "")
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
            Log.e(TAG, "exception= " + e.message)
            }
        }
    }


}
