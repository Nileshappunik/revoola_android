package com.example.myfirstapp.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.LayoutStartListBinding
import com.example.myfirstapp.fragment.start.FragChooseYourSensor

class YourWayListAdapter(val context: FragmentActivity?, valueslist: Array<String>, drawableArray: Array<Drawable?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "YourWayListAdapter"
    var bundle: Bundle = Bundle()
    var yourwayList = valueslist
    var drawableArray = drawableArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutStartListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_start_list , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is YourWayListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return yourwayList.size
    }
    fun setList(yourwayList: Array<String>) {
        this.yourwayList = yourwayList
        notifyDataSetChanged()
    }
    inner class MyViewHolder(layoutBinding: LayoutStartListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutStartListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val itemres = yourwayList[position]
            layoutBinding.txtYourwayName.visibility=View.VISIBLE
            layoutBinding.txtName.visibility=View.GONE
            layoutBinding.txtYourwayName.setText(itemres)
            layoutBinding.imgFull.setImageDrawable( drawableArray[position])
            //"Pilates","Ride","Run","Walk","Workout","Yoga"
            layoutBinding.relayStart.setOnClickListener {
                var bundle: Bundle = Bundle()
                bundle.putString("YourWayType",itemres)
                (context as MainActivity).hidebottombarcolorwhite()
                (context as MainActivity).loadFrag(FragChooseYourSensor().newInstance(bundle), TAG, true, FragChooseYourSensor::class.java.simpleName, false)
            }
        }
    }


}
