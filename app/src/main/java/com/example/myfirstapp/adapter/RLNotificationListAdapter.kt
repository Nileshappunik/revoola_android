package com.example.myfirstapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutNotificationListBinding
import com.example.myfirstapp.model.RLNotificationDataModel
import com.example.myfirstapp.utils.RLTools
import org.json.JSONObject

class RLNotificationListAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"
    private var isLoadingAdded = false
    var groupList = mutableListOf<RLNotificationDataModel>()
    companion object {
        private const val ITEM_TYPE_DATA = 0
        private const val ITEM_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_DATA) {
            val layoutbinding: RlLayoutNotificationListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_notification_list , parent, false)
            return MyViewHolder(layoutbinding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rl_item_loading_layout, parent, false)
            LoadingViewHolder(view)
        }
    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val layoutbinding: LayoutNotificationListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_notification_list , parent, false)
//        return MyViewHolder(layoutbinding)
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_DATA) {
            if (holder is RLNotificationListAdapter.MyViewHolder) {
                holder.bindData(position, holder.itemView)
            }
        }
    }

//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is RLNotificationListAdapter.MyViewHolder) {
//            holder.bindData(position, holder.itemView)
//        }
//
//    }

    override fun getItemViewType(position: Int): Int {
        return if (position == groupList.size - 1 && isLoadingAdded) ITEM_TYPE_LOADING else ITEM_TYPE_DATA
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun RLaddLoadingFooter() {
        isLoadingAdded = true
        notifyItemInserted(groupList.size)
    }

    fun RLremoveLoadingFooter() {
        isLoadingAdded = false
        notifyItemRemoved(groupList.size)
    }

    override fun getItemCount(): Int {
       return groupList.size
    }

    fun RLsetList(newData: List<RLNotificationDataModel>) {
        val startPosition = groupList.size
        groupList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }


    inner class MyViewHolder(layoutBinding: RlLayoutNotificationListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutNotificationListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val carddata = groupList[position]
           val time:String= RLTools.RLconvertTimestampToDateTime(carddata.timestamp.toLong())
            layoutBinding.txtNotificationTime.setText(time)
            try {
                val jsonObject: JSONObject = JSONObject(carddata.notification_data)
                val simpleJson= jsonObject.getJSONObject("simple")
                val dataJson= simpleJson.getJSONObject("data")
                val content:String= simpleJson.getString("content")
                val userName:String= dataJson.getString("userName")
                val userImage:String= dataJson.getString("userImage")
                layoutBinding.txtUserName.setText(userName)
                layoutBinding.txtNotification.setText(content)
                Glide.with(context!!).load(userImage)
                    .placeholder(R.drawable.wellcome)
                    .error(R.drawable.wellcome)
                    .into(layoutBinding.imgNotification)

            }catch (e:Exception){
                Log.d(TAG,"EXCEPTION= "+e.message)
            }

        }
    }


}
