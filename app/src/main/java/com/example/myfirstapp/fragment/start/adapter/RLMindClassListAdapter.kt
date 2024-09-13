package com.example.myfirstapp.fragment.start.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutMindClassesListBinding
import com.example.myfirstapp.fragment.start.RLFragMindClassesView
import com.example.myfirstapp.model.RLVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTools


class RLMindClassListAdapter(private val dataList: List<RLVideoModel>,val context: FragmentActivity?, val classtype:String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLMindClassListAdapter"

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
       return dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutMindClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutMindClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            try{
                // Convert timestamp to minutes
                val minutes =  RLTools.RLconvertTimestampToMinutes(cardData.timestamp)
                layoutBinding.txtClasstime.setText(minutes+"mins")
                layoutBinding.txtVideoaudio.setText(cardData.classtype)
                layoutBinding.txtUsername.setText(cardData.instructor)
            }catch (e:Exception){
                Log.e(TAG,"Exception:- ${e.message}")
            }
            itemVIew.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(RLConstants.CLASSTYPE, classtype)
                (context as RLMainActivityRL).RLloadFrag(RLFragMindClassesView().newInstance(bundle), TAG, true, RLFragMindClassesView::class.java.simpleName, false)
            }
        }
    }


}
