package com.example.myfirstapp.fragment.start.challenges.adapter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutYourFriendBinding
import com.example.myfirstapp.databinding.RlLayoutYourGroupBinding
import com.example.myfirstapp.model.RLuserData
import com.example.myfirstapp.model.RLyourGroupDataModel

class RLChallengeForGroupListAdapter(
    val context: FragmentActivity?,
    private var groupList: List<RLyourGroupDataModel>,
    private val onSelected: (RLyourGroupDataModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLChallengeForFriendListAdapter"
    var bundle: Bundle = Bundle()
    var dataList:List<RLyourGroupDataModel> = groupList.toList()
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutYourGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_group , parent, false)
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

    inner class MyViewHolder(layoutBinding: RlLayoutYourGroupBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutYourGroupBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtGroupName.setText(cardData.group_name)
            layoutBinding.txtGroupNoofmembers.setText("Number of Members: ${cardData.number_of_members.toString()}")
            Glide.with(context!!).load(cardData.group_avatar)
                .placeholder(R.drawable.sample_user)
                .error(R.drawable.sample_user)
                .into(layoutBinding.imgGroup)

            layoutBinding.checkboxFriend.visibility=View.VISIBLE
            layoutBinding.txtGroupMember.visibility=View.GONE
            layoutBinding.checkboxFriend.setOnCheckedChangeListener(null)
            layoutBinding.checkboxFriend.isChecked=cardData.isSelected
            layoutBinding.checkboxFriend.setOnCheckedChangeListener { buttonView, isChecked ->
                try {
                    cardData.isSelected=isChecked
                    onSelected(cardData)
                    val index = groupList.indexOfFirst { it.group_id == cardData.group_id }
                    if (index >= 0) {
                        groupList[index].isSelected = isChecked
                    }
                    // Notify item changed instead of whole dataset
                    notifyItemChanged(adapterPosition)
                }catch (e:Exception){
                    Log.e(TAG,"EXCEPTION:- ${e.message}")
                }
            }
        }
    }
    fun RLfilter(query: String) {
        dataList = if (query.isEmpty()) {
            groupList.toList()
        } else {
            groupList.filter { it.group_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }


}
