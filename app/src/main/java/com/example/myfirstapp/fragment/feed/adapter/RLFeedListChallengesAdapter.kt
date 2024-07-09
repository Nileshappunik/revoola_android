package com.example.myfirstapp.fragment.feed.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutChallengesListBinding
import com.example.myfirstapp.fragment.feed.RLFragChallengeSummary
import com.example.myfirstapp.fragment.feed.RLFragSessionSummary
import com.example.myfirstapp.model.RLFeedChallengesModelData
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTools

class RLFeedListChallengesAdapter(val context: FragmentActivity?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedListChallengesAdapter"
    var bundle: Bundle = Bundle()
    private var isLoadingAdded = false
    private val dataList = mutableListOf<RLFeedChallengesModelData>()
    companion object {
        private const val ITEM_TYPE_DATA = 0
        private const val ITEM_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_DATA) {
            val layoutbinding: RlLayoutChallengesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_challenges_list , parent, false)
            return MyViewHolder(layoutbinding)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.rl_item_loading_layout, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_DATA) {
            if (holder is MyViewHolder) {
                holder.bindData(position, holder.itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == dataList.size - 1 && isLoadingAdded) ITEM_TYPE_LOADING else ITEM_TYPE_DATA
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun RLaddLoadingFooter() {
        isLoadingAdded = true
        notifyItemInserted(dataList.size)
    }

    fun RLremoveLoadingFooter() {
        isLoadingAdded = false
        notifyItemRemoved(dataList.size)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    fun RLaddData(newData: List<RLFeedChallengesModelData>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    inner class MyViewHolder(layoutBinding: RlLayoutChallengesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutChallengesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            try {
                val cardData = dataList[position]

                //RLTools.heightsetrelativelayout(layoutBinding.relayChart)

                layoutBinding.layStepssofar.viewCommon.visibility = View.GONE
                layoutBinding.layTargetsteps.viewCommon.visibility = View.GONE
                layoutBinding.layDaysremaining.viewCommon.visibility = View.GONE

                layoutBinding.txtUsername.setText(cardData.username.toString())
                layoutBinding.txtMyride.setText(cardData.challenge_name.toString())
                layoutBinding.txtUserdatetime.setText(RLTools.RLconvertTimestampToDateTime(cardData.startdate.toLong()))
                layoutBinding.imgMyride.setImageResource(RLTools.RLgeticon(cardData.metric))
                Glide.with(context!!).load(cardData.avatar)
                    .placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                    .into(layoutBinding.imgUser)

                Glide.with(context).load(cardData.adminavatar)
                    .placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                    .into(layoutBinding.imgOrganizerUser)

                layoutBinding.txtOrganizerName.setText(cardData.adminfullname.toString())

                layoutBinding.layStepssofar.imgTime.setImageResource(R.drawable.ic_calender_daily)
                layoutBinding.layStepssofar.txtTime.setText("CHALLENGE PERIOD")
                if (cardData.days_remaining>0){
                    val daysremain=cardData.totaldays-cardData.days_remaining
                    layoutBinding.layStepssofar.txtTimeNumber.setText(daysremain.toString()+" of "+cardData.totaldays.toString()+" Days")
                }else{
                    layoutBinding.layStepssofar.txtTimeNumber.setText(cardData.totaldays.toString()+" of "+cardData.totaldays.toString()+" Days")
                }
                layoutBinding.layTargetsteps.imgTime.setImageResource(R.drawable.fd_steps_green)
                layoutBinding.layTargetsteps.txtTime.setText("ACHIEVED SO FAR")
                layoutBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatNumberWithCommas(cardData.actualtotal.toDouble()))

                layoutBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_goal)
                layoutBinding.layDaysremaining.txtTime.setText(cardData.targettype.uppercase()+" TARGET")
                layoutBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatNumberWithCommas(cardData.totaltarget.toDouble()))

                layoutBinding.cardChalengis.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    (context as RLMainActivityRL).RLbottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragChallengeSummary().newInstance(bundle), TAG, true, null, false)
                }

                var stepsSoFar = if (cardData.actualtotal ?: 0 > 0) cardData.actualtotal ?: 0 else 0
                var targetSteps = if (cardData.totaltarget ?: 0 > 0) cardData.totaltarget ?: 0 else 0

                var remainingDays = cardData.days_remaining ?: 0
                var timeGone = if (remainingDays >0) remainingDays else 0
                var totalTime = if (cardData?.totaldays ?: 0 > 0) cardData?.totaldays ?: 0 else 0



                val webSettings: WebSettings = layoutBinding.webViewChart.settings
                webSettings.javaScriptEnabled = true
                webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
                webSettings.domStorageEnabled = true
                webSettings.useWideViewPort = true
                webSettings.loadWithOverviewMode = true
                layoutBinding.webViewChart.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
                layoutBinding.webViewChart.isHorizontalScrollBarEnabled = false
                layoutBinding.webViewChart.isVerticalScrollBarEnabled = false
                layoutBinding.webViewChart.webViewClient = WebViewClient()
                layoutBinding.webViewChart.loadDataWithBaseURL(null,
                    RLTools.RLgetChallengeChartHtml(stepsSoFar,targetSteps,timeGone,totalTime), "text/html", "UTF-8", null)
            } catch (e: Exception) {
            Log.d(TAG, "exceptionAdaptermsg= " + e.message)
            }


        }
    }
}
