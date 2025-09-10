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
import com.revoola.databinding.RlLayoutYourGroupBinding
import com.revoola.enumclass.FriendsAPIStatusType
import com.revoola.fragment.friends.model.RLFindOnRevoolaInviteItem

class RLContactsAdapter(val context: FragmentActivity, val contactsList: List<RLFindOnRevoolaInviteItem>,
                        private val onSelected: (RLFindOnRevoolaInviteItem,FriendsAPIStatusType) -> Unit) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    var bundle: Bundle = Bundle()
    var dataList: List<RLFindOnRevoolaInviteItem> = contactsList

    private val FOLLOW_CONTACT = 1
    private val INVITE_CONTACT = 2

    override fun getItemViewType(position: Int): Int {
        return when (contactsList[position]) {
            is RLFindOnRevoolaInviteItem.RLFollow -> FOLLOW_CONTACT
            is RLFindOnRevoolaInviteItem.RLInvite -> INVITE_CONTACT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutYourGroupBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_your_group , parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = contactsList[position]
        (holder as MyViewHolder).bindData(position, item)
    }

    // MyViewHolder implementation
    inner class MyViewHolder(private val layoutBinding: RlLayoutYourGroupBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        fun bindData(position: Int, item: RLFindOnRevoolaInviteItem) {
            // Check the type of the data and bind accordingly
            layoutBinding.txtGroupMember.visibility = View.VISIBLE
            layoutBinding.txtGroupNoofmembers.visibility = View.GONE
            when (item) {
                is RLFindOnRevoolaInviteItem.RLFollow -> {
                    val followUserData = item.user
                    // Binding data for UserInvite (EmailFilterUserInvite)
                    layoutBinding.txtGroupName.text = "${followUserData.firstName} ${followUserData.lastName}"
                    //layoutBinding.txtGroupMember.setText("Follow")
                    Glide.with(context!!).load(followUserData.avatar)
                        .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                        .into(layoutBinding.imgGroup)

                    when(followUserData.theirIdStatus?:""){
                        "2"->{
                            layoutBinding.txtGroupMember.setText("Requested")
                            layoutBinding.txtGroupMember.setBackgroundResource(R.drawable.square_border_black_20)
                            layoutBinding.txtGroupMember.setTextColor(context.getColor(R.color.AppBlackColor))
                        }
                        else->{
                            layoutBinding.txtGroupMember.setText("Follow")
                            layoutBinding.txtGroupMember.setBackgroundResource(R.drawable.round_border_green)
                            layoutBinding.txtGroupMember.setTextColor(context.getColor(R.color.AppMainColor))
                        }
                    }
                    layoutBinding.txtGroupMember.setOnClickListener {
                        when(followUserData.theirIdStatus?:""){
                            "2"->{//Requested
                                onSelected(item,FriendsAPIStatusType.Invite)
                                followUserData.theirIdStatus = "0"
                                notifyItemChanged(position)
                            }
                            else->{//follow
                                onSelected(item,FriendsAPIStatusType.Requested)
                                followUserData.theirIdStatus = "2"
                                notifyItemChanged(position)
                            }
                        }
                    }
                    
                }
                is RLFindOnRevoolaInviteItem.RLInvite  -> {
                    val invitContact = item.contact
                    // Binding data for ContactInvite (RLContactModel)
                    layoutBinding.txtGroupName.text = invitContact.name
                    layoutBinding.txtGroupMember.setText("Invite")
                    when(invitContact.theirIdStatus?:""){
                        "2"->{
                            layoutBinding.txtGroupMember.setText("Invited")
                            layoutBinding.txtGroupMember.setBackgroundResource(R.drawable.square_border_black_20)
                            layoutBinding.txtGroupMember.setTextColor(context.getColor(R.color.AppBlackColor))
                        }
                        else->{
                            layoutBinding.txtGroupMember.setText("Invite")
                            layoutBinding.txtGroupMember.setBackgroundResource(R.drawable.round_border_green)
                            layoutBinding.txtGroupMember.setTextColor(context.getColor(R.color.AppMainColor))
                        }
                    }
                    layoutBinding.txtGroupMember.setOnClickListener {
                        when(invitContact.theirIdStatus?:""){
                            "0"->{//Requested
                                onSelected(item,FriendsAPIStatusType.Invited)
                                invitContact.theirIdStatus = "2"
                                notifyItemChanged(position)
                            }
                            else->{//follow
                               //nothing
                            }
                        }

                    }
                }

            }

        }
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    fun rl_filter(query: String) {
        dataList = if (query.isEmpty()) {
            contactsList // Show all items if query is empty
        } else {
            // Filter based on the query
            contactsList.filter { item ->
                when (item) {
                    is RLFindOnRevoolaInviteItem.RLFollow -> {
                        // Check if `username` or `email` matches query
                        item.user.username.contains(query, ignoreCase = true) || item.user.email.contains(query, ignoreCase = true)
                    }
                    is RLFindOnRevoolaInviteItem.RLInvite -> {
                        // Check if `name` or `phoneNumber` matches query
                        item.contact.name.contains(query, ignoreCase = true) || item.contact.phoneNumber.contains(query, ignoreCase = true)
                    }
                    else -> false
                }
            }
        }
        notifyDataSetChanged() // Notify the adapter to refresh the list
    }

}
