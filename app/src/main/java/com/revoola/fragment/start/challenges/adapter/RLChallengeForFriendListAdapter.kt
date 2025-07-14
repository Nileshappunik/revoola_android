package com.revoola.fragment.start.challenges.adapter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.revoola.commonobject.RLTools

class RLChallengeForFriendListAdapter(
    val context: FragmentActivity?,
    private var friendList: List<RLuserData>,
    private val onSelected: (RLuserData) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLChallengeForFriendListAdapter"
    var bundle: Bundle = Bundle()
    var dataList:List<RLuserData> = friendList.toList()
    private val handler = Handler(Looper.getMainLooper())
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
       return dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutYourFriendBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourFriendBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            Glide.with(context!!).load(cardData.avatar).placeholder(R.drawable.sample_user).error(R.drawable.sample_user).into(layoutBinding.imgFriend)
            layoutBinding.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
            layoutBinding.txtFriendUnfollow.visibility=View.GONE
            layoutBinding.checkboxFriend.visibility=View.VISIBLE
            layoutBinding.checkboxFriend.setOnCheckedChangeListener(null)
            layoutBinding.checkboxFriend.isChecked=cardData.isSelected
            layoutBinding.viewSelectFriend.visibility=View.VISIBLE
            layoutBinding.viewFriend.visibility=View.GONE

            val layoutParamsImage: ViewGroup.LayoutParams = layoutBinding.relayFriends.layoutParams
            layoutParamsImage.height =  120
            layoutBinding.relayFriends.layoutParams =layoutParamsImage

            layoutBinding.checkboxFriend.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    cardData.isSelected=isChecked
                    onSelected(cardData)
                    val index = friendList.indexOfFirst { it.userid == cardData.userid }
                    if (index >= 0) {
                        friendList[index].isSelected = isChecked
                    }
                    // Notify item changed instead of whole dataset
                    notifyItemChanged(adapterPosition)
                }catch (e:Exception){
                   RLTools.rl_logEPrint(TAG,"EXCEPTION:- ${e.message}")
                }
            }
        }
    }
    fun RLfilter(query: String) {
        dataList = if (query.isEmpty()) {
            friendList.toList()
        } else {
            friendList.filter { it.first_name.contains(query, ignoreCase = true) || it.last_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


}
