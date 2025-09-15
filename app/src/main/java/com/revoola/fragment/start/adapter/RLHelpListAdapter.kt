package com.revoola.fragment.start.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.revoola.R
import com.revoola.databinding.RlLayoutHelpDialogListBinding
import com.revoola.fragment.start.RLStartHelpModelData
import com.revoola.utils.loadSvg

class RLHelpListAdapter(val context: Context,
    val dataList: List<RLStartHelpModelData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLHelpListAdapter"
    var bundle: Bundle = Bundle()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutHelpDialogListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_help_dialog_list , parent, false)
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

    inner class MyViewHolder( private val layoutBinding: RlLayoutHelpDialogListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            when (cardData.type){
                0->{
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.VISIBLE
                    layoutBinding.cardChallenge.visibility=View.GONE
                    setHelpText(layoutBinding.tvDescription, cardData.text)
                }
                1->{
                    layoutBinding.tvTitle.visibility=View.VISIBLE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.cardChallenge.visibility=View.GONE
                    layoutBinding.tvTitle.setText(cardData.title)
                }
                2->{
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.cardChallenge.visibility=View.VISIBLE
                    layoutBinding.txtHeader.setText(cardData.title)
                    setHelpText(layoutBinding.tvDescriptionChallenge, cardData.text)
                    layoutBinding.iconHelpChallenges.loadSvg(cardData.image)
                }
                else -> {
                    layoutBinding.tvTitle.visibility=View.GONE
                    layoutBinding.tvDescription.visibility=View.GONE
                    layoutBinding.cardChallenge.visibility=View.VISIBLE
                    layoutBinding.txtHeader.setText(cardData.title)
                    setHelpText(layoutBinding.tvDescriptionChallenge, cardData.text)
                    layoutBinding.iconHelpChallenges.loadSvg(cardData.image)

                }
            }

        }

        private fun setHelpText(tv: TextView, rawText: String) {
            // Parse HTML tags in your text (e.g. <a href="...">click here</a>)
            val spanned = HtmlCompat.fromHtml(rawText, HtmlCompat.FROM_HTML_MODE_LEGACY)

            tv.text = spanned
            tv.movementMethod = LinkMovementMethod.getInstance() // enable clicking
            tv.linksClickable = true
        }
    }

}
