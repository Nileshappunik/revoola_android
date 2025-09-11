package com.revoola.fragment.friends.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlLayoutYourGroupBinding
import com.revoola.fragment.friends.RLFragYourGroupDetails
import com.revoola.model.RLyourGroupDataModel

class RLYourGroupListAdapter(private val context: FragmentActivity?,private val groupList: List<RLyourGroupDataModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var dataList: List<RLyourGroupDataModel> = groupList

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


    inner class MyViewHolder( private val lB: RlLayoutYourGroupBinding) : RecyclerView.ViewHolder(lB.root) {
        fun bindData(position: Int, itemVIew: View) {
            val carddata = dataList[position]
            lB.txtGroupName.setText(carddata.group_name)
            lB.txtGroupNoofmembers.setText("Number of Members: ${carddata.number_of_members.toString()}")
            if (!carddata.group_avatar.isNullOrEmpty()) {
                Glide.with(context!!)
                    .load(carddata.group_avatar)
                    .placeholder(R.drawable.sample_user)
                    .error(R.drawable.sample_user)
                    .into(lB.imgGroup)
            } else {
                lB.imgGroup.setImageBitmap(RLTools.getInitialsBitmap(context!!,carddata.group_name?: "Group"))
            }
            when(carddata.is_admin){
                1->{
                    lB.txtGroupDelete.visibility=View.GONE
                }
                0->{
                    lB.txtGroupDelete.visibility=View.VISIBLE
                    lB.txtGroupDelete.setText("LEAVE")
                    lB.txtGroupDelete.setBackgroundResource(R.drawable.square_border_black_20)
                    lB.txtGroupDelete.setTextColor(context!!.getColor(R.color.AppBlackColor))
                }
            }
            lB.relativeGroupCard.setOnClickListener {
                val bundle: Bundle = Bundle()
                bundle.putString("GroupID", carddata.group_id)
                bundle.putString("GroupName", carddata.group_name)
                bundle.putString("GroupAvatar", carddata.group_avatar)
                bundle.putString("GroupMember", carddata.number_of_members.toString())
                bundle.putInt("is_admin", carddata.is_admin)
                (context as RLMainActivityRL).rl_loadFrag(RLFragYourGroupDetails().newInstance(bundle), TAG, true, null, true)
            }
       }
    }

    fun rl_filter(query: String) {
        dataList = if (query.isEmpty()) {
            groupList
        } else {
            groupList.filter {it.group_name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

}
