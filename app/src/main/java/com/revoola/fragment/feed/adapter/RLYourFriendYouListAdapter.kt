package com.revoola.fragment.feed.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutYourFriendYouBinding


class RLYourFriendYouListAdapter(val context: FragmentActivity?, val Friendlay:Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourFriendListAdapter"
    var bundle: Bundle = Bundle()
    var friendList: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutYourFriendYouBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_friend_you , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }

    override fun getItemCount(): Int {
       //return feedList!!.size
        return 5

    }


    fun RLsetList(friendList: List<String>?) {
        this.friendList = friendList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(val layoutBinding: RlLayoutYourFriendYouBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        //private val layoutBinding: RlLayoutYourFriendYouBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            //val itemres = feedList!![position]
            if (Friendlay){
                layoutBinding.txtFriendDate.visibility=View.GONE
            }else{
                layoutBinding.txtFriendDate.visibility=View.VISIBLE
            }

        }
    }


}
