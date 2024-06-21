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
import com.example.myfirstapp.fragment.start.RLFragChalengesType
import com.example.myfirstapp.fragment.start.RLFragClasses
import com.example.myfirstapp.fragment.start.RLFragYourWay

class RLStartListAdapter(val context: FragmentActivity?, valueslist: Array<String>, drawableArray: Array<Drawable?>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLStartListAdapter"
    var bundle: Bundle = Bundle()
    var feedList = valueslist
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
        return feedList.size
    }

    fun RLsetList(feedList: Array<String>) {
        this.feedList = feedList
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

            val itemres = feedList[position]
            layoutBinding.txtName.setText(itemres)
            layoutBinding.imgFull.setImageDrawable( drawableArray[position])
            layoutBinding.relayStart.setOnClickListener {
                if (itemres.equals("Challenges")){
                    (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, RLFragChalengesType::class.java.simpleName, false)

                }else if (itemres.equals("Your Way")){
                    (context as RLMainActivityRL).RLshowbottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, RLFragYourWay::class.java.simpleName, false)

                }else if (itemres.equals("Classes")){
                    (context as RLMainActivityRL).RLshowbottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragClasses(), TAG, true, RLFragClasses::class.java.simpleName, false)

                }

            }
        }
    }


}
