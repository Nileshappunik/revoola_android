package com.example.myfirstapp.fragment.friends.adapter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutYourFriendBinding
import com.example.myfirstapp.model.RLuserData

class RLYourFriendSelectListAdapter(val context: FragmentActivity?, val friendList: List<RLuserData>, val txtInviteyourfriend: TextView) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourFriendSelectListAdapter"
    var bundle: Bundle = Bundle()
    var datalist:List<RLuserData> = friendList
    var totalselect:Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutYourFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_friend , parent, false)
        return MyViewHolder(layoutbinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }
    override fun getItemCount(): Int {
       return datalist.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutYourFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourFriendBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = datalist[position]
            Glide.with(context!!).load(cardData.avatar)
                .placeholder(R.drawable.sample_user)
                .error(R.drawable.sample_user)
                .into(layoutBinding.imgFriend)
            layoutBinding.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
            layoutBinding.txtFriendUnfollow.visibility=View.GONE
            layoutBinding.checkboxFriend.visibility=View.VISIBLE
            layoutBinding.checkboxFriend.isChecked=cardData.isSelected

            layoutBinding.checkboxFriend.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    if (isChecked){
                        txtInviteyourfriend.visibility=View.VISIBLE
                        totalselect= totalselect+1
                    }else{
                        totalselect= totalselect-1
                        if (totalselect==0){
                            txtInviteyourfriend.visibility=View.GONE
                        }
                    }
                    cardData.isSelected=isChecked
                    notifyItemChanged(position)
                }catch (e:Exception){
                    Log.e(TAG,"EXCEPTION:- ${e.message}")
                }
            }
        }
    }
    fun RLfilter(query: String) {
        datalist = if (query.isEmpty()) {
            friendList
        } else {
            friendList.filter { it.first_name.contains(query, ignoreCase = true) || it.last_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


}
