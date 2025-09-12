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
import com.revoola.databinding.RlLayoutYourFriendBinding
import com.revoola.enumclass.FriendsAPIStatusType
import com.revoola.enumclass.RLFriendsFollowType
import com.revoola.fragment.friends.RLFragFriendsItemClickList
import com.revoola.model.RLuserData

class RLYourFriendListAdapter(private val context: FragmentActivity?,
                              private val friendList: List<RLuserData>,
                              private val friendsFollowType: RLFriendsFollowType,
                              private val onItemClick: (RLuserData,FriendsAPIStatusType) -> Unit
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

            if (!cardData.avatar.isNullOrEmpty()) {
                Glide.with(context!!)
                    .load(cardData.avatar)
                    .placeholder(R.drawable.sample_user)
                    .error(R.drawable.sample_user)
                    .into(lb.imgFriend)
            } else {
                lb.imgFriend.setImageBitmap(RLTools.getInitialsBitmap(context!!,cardData.first_name+" "+cardData.last_name))
            }

            lb.txtFriendName.setText(cardData.first_name+" "+cardData.last_name)
            when (friendsFollowType){
                RLFriendsFollowType.FriendRequest->{
                    lb.txtFriendBlock.visibility= View.VISIBLE
                    lb.txtFriendUnfollow.visibility= View.VISIBLE
                    lb.txtFriendUnfollow.setText("Accept")
                    lb.txtFriendUnfollow.setBackgroundResource(R.drawable.round_border_green)
                    lb.txtFriendUnfollow.setTextColor(context!!.getColor(R.color.AppMainColor))
                    lb.txtFriendUnfollow.setOnClickListener {
                        onItemClick(cardData,FriendsAPIStatusType.Accepted)
                        dataList = dataList.filterIndexed { index, _ -> index != position }
                        notifyItemRemoved(position)
                    }
                    lb.txtFriendBlock.setOnClickListener {
                        onItemClick(cardData,FriendsAPIStatusType.Blocked)
                        dataList = dataList.filterIndexed { index, _ -> index != position }
                        notifyItemRemoved(position)
                    }
                }
                RLFriendsFollowType.YourGroup->{
                    lb.txtFriendUnfollow.visibility= View.GONE
                    lb.txtFriendBlock.visibility= View.GONE
                }
                RLFriendsFollowType.InviteGroupFriend->{
                    lb.txtFriendUnfollow.visibility= View.VISIBLE
                    lb.txtFriendBlock.visibility= View.GONE
                    lb.txtFriendUnfollow.setText("INVITE")
                    lb.txtFriendUnfollow.setBackgroundResource(R.drawable.round_border_green)
                    lb.txtFriendUnfollow.setTextColor(context!!.getColor(R.color.AppMainColor))
                    lb.txtFriendUnfollow.setOnClickListener {
                        onItemClick(cardData,FriendsAPIStatusType.Invite)
                    }
                }
                else -> {
                    lb.txtFriendUnfollow.visibility= View.VISIBLE
                    lb.txtFriendBlock.visibility= View.GONE
                    when(cardData.theiridstatus){
                        "2"->{
                            lb.txtFriendUnfollow.setText("Requested")
                            lb.txtFriendUnfollow.setBackgroundResource(R.drawable.square_border_black_20)
                            lb.txtFriendUnfollow.setTextColor(context!!.getColor(R.color.AppBlackColor))
                        }
                        "3"->{
                            lb.txtFriendUnfollow.setText("Unfollow")
                            lb.txtFriendUnfollow.setBackgroundResource(R.drawable.round_border_green)
                            lb.txtFriendUnfollow.setTextColor(context!!.getColor(R.color.AppMainColor))
                        }
                        else->{
                            lb.txtFriendUnfollow.setText("follow")
                            lb.txtFriendUnfollow.setBackgroundResource(R.drawable.round_border_green)
                            lb.txtFriendUnfollow.setTextColor(context!!.getColor(R.color.AppMainColor))
                        }
                    }
                    lb.txtFriendUnfollow.setOnClickListener {
                        if (friendsFollowType == RLFriendsFollowType.YouFollow){
                            onItemClick(cardData,FriendsAPIStatusType.Follow)
                            dataList = dataList.filterIndexed { index, _ -> index != position }
                            notifyItemRemoved(position)
                        }else{
                            when(cardData.theiridstatus){
                                "2"->{//Requested
                                    onItemClick(cardData,FriendsAPIStatusType.Follow)
                                    cardData.theiridstatus = "-1"
                                    notifyItemChanged(position)
                                }
                                "3"->{//Unfollow
                                    onItemClick(cardData,FriendsAPIStatusType.Follow)
                                    cardData.theiridstatus = "-1"
                                    notifyItemChanged(position)
                                }
                                else->{//follow
                                    onItemClick(cardData,FriendsAPIStatusType.Requested)
                                    cardData.theiridstatus = "2"
                                    notifyItemChanged(position)
                                }
                            }
                        }
                    }
                }
            }

            itemVIew.setOnClickListener {
                if (friendsFollowType == RLFriendsFollowType.YourGroup || friendsFollowType == RLFriendsFollowType.InviteGroupFriend){
                    return@setOnClickListener
                }
                val bundle = Bundle()
                bundle.putString("currentUser",cardData.theirid)
                (context as RLMainActivityRL).rl_loadFrag(RLFragFriendsItemClickList().newInstance(bundle), "RLFragYourFriends", true,null, false)
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
