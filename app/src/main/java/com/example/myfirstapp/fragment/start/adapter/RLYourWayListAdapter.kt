package com.example.myfirstapp.fragment.start.adapter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutStartListBinding
import com.example.myfirstapp.fragment.start.RLFragChooseYourSensor

class RLYourWayListAdapter(val context: FragmentActivity?, valueslist: Array<String>, drawableArray: Array<Drawable?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourWayListAdapter"
    var bundle: Bundle = Bundle()
    var yourwayList = valueslist
    var drawableArray = drawableArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutStartListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_list , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return yourwayList.size
    }
    fun RLsetList(yourwayList: Array<String>) {
        this.yourwayList = yourwayList
        notifyDataSetChanged()
    }
    inner class MyViewHolder(layoutBinding: RlLayoutStartListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutStartListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {

            itemVIew.post{
                val width = itemVIew.width
                val newHeight = width * 2
                // Set the new height to the itemView
                val layoutParams =itemVIew.layoutParams
                layoutParams.height = newHeight
                itemVIew.layoutParams = layoutParams
            }

            val itemres = yourwayList[position]

            layoutBinding.txtYourwayName.visibility=View.VISIBLE
            layoutBinding.txtName.visibility=View.GONE
            layoutBinding.txtYourwayName.setText(itemres)
            layoutBinding.imgFull.setImageDrawable( drawableArray[position])

            //"Pilates","Ride","Run","Walk","Workout","Yoga"
            layoutBinding.relayStart.setOnClickListener {
                var bundle: Bundle = Bundle()
                bundle.putString("YourWayType",itemres)
                (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragChooseYourSensor().newInstance(bundle), TAG, true, RLFragChooseYourSensor::class.java.simpleName, false)
            }
        }
    }


}
