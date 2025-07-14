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
import com.revoola.fragment.friends.model.RLFindOnRevoolaInviteItem

class RLContactsAdapter(val context: FragmentActivity?, val contactsList: List<RLFindOnRevoolaInviteItem>,
                        private val onSelected: (RLFindOnRevoolaInviteItem) -> Unit) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                    layoutBinding.txtGroupMember.setText("Follow")
                    Glide.with(context!!).load(followUserData.avatar)
                        .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                        .into(layoutBinding.imgGroup)
                }
                is RLFindOnRevoolaInviteItem.RLInvite  -> {
                    val invitContact = item.contact
                    // Binding data for ContactInvite (RLContactModel)
                    layoutBinding.txtGroupName.text = invitContact.name
                    layoutBinding.txtGroupMember.setText("Invite")
                }

            }
            layoutBinding.txtGroupMember.setOnClickListener {
                onSelected(item)
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
