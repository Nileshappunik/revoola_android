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
import com.example.myfirstapp.databinding.LayoutStartListBinding

class StartListAdapter(
    val context: FragmentActivity?,
    valueslist: Array<String>,
    drawableArray: Array<Drawable?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "StartListAdapter"
    var bundle: Bundle = Bundle()
    var feedList = valueslist
    var drawableArray = drawableArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: LayoutStartListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_start_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StartListAdapter.MyViewHolder) {
            holder.bindData(position, holder.itemView)

        }

    }

    override fun getItemCount(): Int {
        return feedList.size


    }


    fun setList(feedList: Array<String>) {
        this.feedList = feedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: LayoutStartListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: LayoutStartListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val itemres = feedList[position]
            layoutBinding.txtName.setText(itemres)
            layoutBinding.imgFull.setImageDrawable( drawableArray[position])

        }
    }


}
