package com.example.myfirstapp.fragment.start.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutMindClassesListBinding
import com.example.myfirstapp.fragment.start.RLFragMindClassesView
import com.example.myfirstapp.utils.RLConstants


class RLMindClassListAdapter(val context: FragmentActivity?, val classtype:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLMindClassListAdapter"
    var bundle: Bundle = Bundle()
    var feedList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutMindClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_mind_classes_list , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 15

    }


    fun RLsetList(feedList: List<String>?) {
        this.feedList = feedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(layoutBinding: RlLayoutMindClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutMindClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]

            itemVIew.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(RLConstants.CLASSTYPE, classtype)
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesView().newInstance(bundle), TAG, true, RLFragMindClassesView::class.java.simpleName, false)
            }
        }
    }


}
