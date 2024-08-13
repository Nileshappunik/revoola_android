package com.example.myfirstapp.fragment.start.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutHelpDialogListBinding
import com.example.myfirstapp.databinding.RlLayoutStartMenuBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.fragment.friends.RLFragFindOnRevoola
import com.example.myfirstapp.fragment.friends.RLFragInviteFriends
import com.example.myfirstapp.fragment.friends.RLFragYourFriends
import com.example.myfirstapp.fragment.friends.RLFragYourGroup
import com.example.myfirstapp.fragment.start.RLStartHelpModelData
import com.example.myfirstapp.fragment.start.body.RLFragBodyClasses
import com.example.myfirstapp.fragment.start.challenges.RLFragChalengesType
import com.example.myfirstapp.fragment.start.classes.RLFragClasses
import com.example.myfirstapp.fragment.start.mind.RLFragMindClasses
import com.example.myfirstapp.fragment.start.yourway.RLFragYourWay
import com.example.myfirstapp.utils.loadSvg

class RLHelpListAdapter(val context: FragmentActivity?,
    val dataList: List<RLStartHelpModelData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLHelpListAdapter"
    var bundle: Bundle = Bundle()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutHelpDialogListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_help_dialog_list , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return  dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutHelpDialogListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutHelpDialogListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            when (cardData.type){
                1->{
                    layoutBinding.tvTitle.visibility=View.VISIBLE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.tvTitle.setText(cardData.title)
                }
                0->{
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.VISIBLE
                    layoutBinding.tvDescription.setText(cardData.text)
                }
            }
    }
    }

}
