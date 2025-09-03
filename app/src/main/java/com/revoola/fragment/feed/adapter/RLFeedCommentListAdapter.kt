package com.revoola.fragment.feed.adapter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlLayoutCommentUserBinding
import com.revoola.fragment.start.yourway.RLFragSessionComplete
import com.revoola.model.CommentItem
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants

class RLFeedCommentListAdapter(private val dataList: MutableList<CommentItem>,
                               private val context: FragmentActivity?,
                               private val onItemClick :(item: CommentItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = RLFeedCommentListAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutCommentUserBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_comment_user, parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }
    inner class MyViewHolder(val binding: RlLayoutCommentUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData= dataList[position]
            Glide.with(context!!).load(cardData.avatar).into(binding.imgUser)
            binding.txtUsername.setText(cardData.username)
            binding.txtCommet.setText(cardData.comment)
            binding.txtDate.setText(RLTools.rl_formatTimestamp(cardData.timestamp))
            binding.imgThreedot.setOnClickListener {
             //   showEditDeleteDialog(cardData,position)
            }
        }

        private fun showEditDeleteDialog(cardData: CommentItem,pos: Int) {
            val dialog = Dialog(context!!).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.rl_dailog_edit_delete_feedcard)
                setCancelable(true)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                window?.setBackgroundDrawableResource(R.color.transparent_dialog)
            }

            dialog.findViewById<TextView>(R.id.txt_edit)?.apply {
                setText("Reply")
                setOnClickListener {
                    dialog.dismiss()
                    // handle reply here
                }
            }
            dialog.findViewById<TextView>(R.id.txt_delete)?.setOnClickListener { dialog.dismiss()
                onItemClick(cardData)
                // remove from list
                dataList.removeAt(pos)
                // notify adapter
                notifyItemRemoved(pos)
            }
            dialog.findViewById<TextView>(R.id.btn_cancle)?.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
}