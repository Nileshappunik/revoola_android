package com.revoola.fragment.feed.adapter

import android.os.Bundle
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
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlLayoutFeedListBinding
import com.revoola.fragment.feed.RLFragChallengeSummary
import com.revoola.model.RLFeedChallengesModelData
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools

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
           // val layoutbinding: RlLayoutChallengesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_challenges_list , parent, false)
            val layoutbinding: RlLayoutFeedListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_feed_list , parent, false)
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

    fun rl_addLoadingFooter() {
        isLoadingAdded = true
        notifyItemInserted(dataList.size)
    }

    fun rl_removeLoadingFooter() {
        isLoadingAdded = false
        notifyItemRemoved(dataList.size)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    fun rl_addData(newData: List<RLFeedChallengesModelData>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }

    inner class MyViewHolder(val layoutBinding: RlLayoutFeedListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
       // private val layoutBinding: RlLayoutFeedListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            try {
                val cardData = dataList[position]
                layoutBinding.bigChallengesLayout.visibility=View.GONE
                layoutBinding.mainLayoutFeed.visibility=View.VISIBLE
               // RLTools.rl_heightsetRelative(layoutBinding.relayChart)
                layoutBinding.txtOrganizer.visibility=View.VISIBLE
                layoutBinding.txtOrganizerName.visibility=View.VISIBLE
                layoutBinding.imgOrganizerUser.visibility=View.VISIBLE
                layoutBinding.relayChart.visibility=View.VISIBLE
                layoutBinding.imgMain.visibility=View.GONE
                layoutBinding.imgThreedot.visibility=View.GONE
                layoutBinding.blanckView1.visibility=View.VISIBLE
                layoutBinding.layoutShare.visibility=View.VISIBLE
                layoutBinding.layoutThumb.visibility=View.GONE
                layoutBinding.layoutComment.visibility=View.GONE
                layoutBinding.blanckView.visibility=View.GONE
                layoutBinding.layoutAward.visibility=View.GONE
                layoutBinding.layTime.viewCommon.visibility = View.GONE
                layoutBinding.layCalories.viewCommon.visibility = View.GONE
                layoutBinding.layAssumedeffort.viewCommon.visibility = View.GONE
                layoutBinding.laySteps.viewCommon.visibility = View.GONE
                if (position == dataList.size-1){
                    layoutBinding.viewBottom.visibility = View.GONE
                }else{
                    layoutBinding.viewBottom.visibility = View.VISIBLE
                }

                layoutBinding.txtUsername.setText(cardData.username.toString())
                layoutBinding.txtMyride.setText(cardData.challenge_name.toString())
                layoutBinding.txtUserdatetime.setText(RLTools.rl_convertTimestampToDateTime(cardData.startdate.toLong()))
                layoutBinding.imgMyride.setImageResource(RLTools.rl_geticon(cardData.metric))
                Glide.with(context!!).load(cardData.avatar)
                    .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                    .into(layoutBinding.imgUser)

                Glide.with(context).load(cardData.adminavatar)
                    .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                    .into(layoutBinding.imgOrganizerUser)

                layoutBinding.txtOrganizerName.setText(cardData.adminfullname.toString())

                layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_calender_daily)
                layoutBinding.layTime.txtTime.setText("CHALLENGE PERIOD")
                if (cardData.days_remaining>0){
                    val daysremain=cardData.totaldays-cardData.days_remaining
                    layoutBinding.layTime.txtTimeNumber.setText(daysremain.toString()+" of "+cardData.totaldays.toString()+" Days")
                }else{
                    layoutBinding.layTime.txtTimeNumber.setText(cardData.totaldays.toString()+" of "+cardData.totaldays.toString()+" Days")
                }
                layoutBinding.layCalories.imgTime.setImageResource(RLTools.rl_geticon(cardData.metric))
                layoutBinding.layCalories.txtTime.setText("ACHIEVED SO FAR")
                layoutBinding.layCalories.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.actualtotal.toDouble()))

                layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_goal)
                layoutBinding.layAssumedeffort.txtTime.setText(cardData.targettype.uppercase()+" TARGET")
                layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.rl_formatCommas(cardData.totaltarget.toDouble()))

                layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
                layoutBinding.laySteps.txtTime.setText(R.string.currenrrank)
                layoutBinding.laySteps.txtTimeNumber.setText("${cardData.ranking_by_challenge.toString()} OF ${cardData.participants.toString()}")

                layoutBinding.cardChalengis.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragChallengeSummary().newInstance(bundle), TAG, true, null, false)
                }

                val stepsSoFar = if (cardData.actualtotal.toInt() ?: 0 > 0) cardData.actualtotal ?: 0 else 0
                val targetSteps = if (cardData.totaltarget ?: 0 > 0) cardData.totaltarget ?: 0 else 0

                val remainingDays = cardData.days_remaining ?: 0
                val timeGone = if (remainingDays >0) remainingDays else 0
                val totalTime = if (cardData?.totaldays ?: 0 > 0) cardData?.totaldays ?: 0 else 0

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
                    RLAllHTMLChart.rl_getChallengeChartHtml(stepsSoFar.toInt(),targetSteps,timeGone,totalTime,RLTools.rl_getMetricsName(cardData.metric)), "text/html", "UTF-8", null)

                layoutBinding.imgShare.setOnClickListener {
                 // RLBranchManager(context!!).RLGenerateBranchLink(cardData.challenge_name, cardData.adminfullname,cardData.avatar)
                  //RLBranchManager(context!!).generateBranchLink()

                }
            } catch (e: Exception) {
                RLTools.rl_logDPrint(TAG, "exceptionAdaptermsg= " + e.message)
            }


        }
    }

   /* inner class MyViewHolder(layoutBinding: RlLayoutChallengesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
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
                    .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                    .into(layoutBinding.imgUser)

                Glide.with(context).load(cardData.adminavatar)
                    .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
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
                layoutBinding.layTargetsteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.actualtotal.toDouble()))

                layoutBinding.layDaysremaining.imgTime.setImageResource(R.drawable.ic_goal)
                layoutBinding.layDaysremaining.txtTime.setText(cardData.targettype.uppercase()+" TARGET")
                layoutBinding.layDaysremaining.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totaltarget.toDouble()))

                layoutBinding.cardChalengis.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
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
            RLTools.RlLogDPrint(TAG, "exceptionAdaptermsg= " + e.message)
            }
        }
    }*/
}
