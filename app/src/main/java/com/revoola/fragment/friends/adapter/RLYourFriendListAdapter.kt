package com.revoola.fragment.friends.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.databinding.RlLayoutYourFriendBinding
import com.revoola.model.RLuserData

class RLYourFriendListAdapter(val context: FragmentActivity?,
                              val friendList: List<RLuserData>,
                              val isFollowHide:Boolean,
                              val onItemClick: (RLuserData) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var bundle: Bundle = Bundle()
    var dataList:List<RLuserData> = friendList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutYourFriendBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_friend , parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }

    }
    override fun getItemCount(): Int {
       return dataList.size
    }
    inner class MyViewHolder(private  val lb: RlLayoutYourFriendBinding) : RecyclerView.ViewHolder(lb.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            Glide.with(context!!).load(cardData.avatar)
                .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                .into(lb.imgFriend)
            lb.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
            when(cardData.theiridstatus){
                "2"->{
                    lb.txtFriendUnfollow.setText("Requested")
                    lb.txtFriendUnfollow.setBackgroundResource(R.drawable.square_border_black_20)
                    lb.txtFriendUnfollow.setTextColor(context.getColor(R.color.AppBlackColor))
                }
                else->{
                    lb.txtFriendUnfollow.setText("Unfollow")
                    lb.txtFriendUnfollow.setBackgroundResource(R.drawable.round_border_green)
                    lb.txtFriendUnfollow.setTextColor(context.getColor(R.color.AppMainColor))
                }
            }
            if (isFollowHide){
                lb.txtFriendUnfollow.visibility=View.GONE
            }else{
                lb.txtFriendUnfollow.visibility=View.VISIBLE
            }
            lb.txtFriendUnfollow.setOnClickListener {
                onItemClick(cardData)
            }

        }
    }
    fun rl_filter(query: String) {
        dataList = if (query.isEmpty()) {
            friendList
        } else {
            friendList.filter { it.first_name.contains(query, ignoreCase = true) || it.last_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


}
