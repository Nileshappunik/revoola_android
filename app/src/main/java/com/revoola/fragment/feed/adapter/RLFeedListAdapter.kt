package com.revoola.fragment.feed.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.branchManagerIo.RLBranchManager
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlLayoutFeedListBinding
import com.revoola.enumclass.RLValueName
import com.revoola.fragment.feed.RLFragMindSessionSummary
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.fragment.feed.RLFragTenChallengeSummary
import com.revoola.model.RLTextOverview
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import java.util.Locale
import kotlin.math.roundToInt

class RLFeedListAdapter(
    val activity: FragmentActivity,
    private val currentUserId: String,
    private val selectTag: String,
    private val appUnit: String,
    private val onItemClicked: (RLTextOverview) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_TYPE_DATA = 0
        private const val ITEM_TYPE_LOADING = 1
        private const val TAG = "RLFeedListAdapter"
    }

    private val dataList = mutableListOf<RLTextOverview>()
    private var isLoadingAdded: Boolean = false

    // ---------- Public API ----------
    fun clear() {
        isLoadingAdded = false
        dataList.clear()
        notifyDataSetChanged()
    }

    fun setItems(items: List<RLTextOverview>) {
        isLoadingAdded = false
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * Adds data; if isSwitchOn=false, filters out items with from_third_party_source == 2
     */
    fun rl_addData(newData: List<RLTextOverview>, isSwitchOn: Boolean) {
        val filtered = if (isSwitchOn) newData else newData.filter { it.from_third_party_source != 2 }
        val start = dataList.size
        dataList.addAll(filtered)
        notifyItemRangeInserted(start, filtered.size)
    }

    fun rl_addLoadingFooter() {
        if (isLoadingAdded) return
        isLoadingAdded = true
        // We render an extra row for footer; notify insertion at the new "footer" position.
        notifyItemInserted(dataList.size)
    }

    fun rl_removeLoadingFooter() {
        if (!isLoadingAdded) return
        isLoadingAdded = false
        // Footer lives at index == dataList.size
        notifyItemRemoved(dataList.size)
    }

    // ---------- RecyclerView.Adapter ----------
    override fun getItemCount(): Int = dataList.size + if (isLoadingAdded) 1 else 0

    override fun getItemViewType(position: Int): Int {
        return if (isLoadingAdded && position == dataList.size) ITEM_TYPE_LOADING else ITEM_TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_DATA) {
            val binding: RlLayoutFeedListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.rl_layout_feed_list,
                parent,
                false
            )
            DataViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.rl_item_loading_layout, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) holder.bind(position)
    }

    // ---------- ViewHolders ----------
    private class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private inner class DataViewHolder(
        private val binding: RlLayoutFeedListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            try {
                val card = dataList[position]
                RLTools.rl_logLarge(TAG, "cardData: ${Gson().toJson(card)}")

                commonHeader(card, binding, position + 1)

                val isImperial = RLTools.rl_getIsImperial(appUnit)
                when (card.from_third_party_source) {
                    0 -> { // Revoola native
                        when (card.bmo) {
                            0 -> bodyCard(card, binding, isImperial) // BODY
                            1 -> mindCard(card, binding)            // MIND
                            2 -> yourWayCard(card, binding, isImperial) // OTHER / Your Way
                        }
                    }
                    1 -> thirdPartyGoogleFit(card, binding, isImperial)
                    2 -> thirdPartyAppleHealth(card, binding, isImperial)
                    101 -> {
                        // Big Challenges Join Session
                        binding.mainLayoutFeed.visibility = View.GONE
                        binding.bigChallengesLayout.visibility = View.VISIBLE
                        bigChallengeJoinCard(card, binding)
                    }
                    else -> {
                        // >10 => Challenge design
                        challengeDesignCard(card, binding, position + 1, isImperial)
                    }
                }
            } catch (e: Exception) {
                RLTools.rl_logDPrint(TAG, "exception= ${e.message}")
                val temp = "pos: ${position}, third: ${dataList[position].from_third_party_source}, bmo: ${dataList[position].bmo}, HR: ${dataList[position].hrm}"
                binding.temptext.text = "exception: ${e.message} :: $temp"
            }
        }

        // ---------- Common top section ----------
        private fun commonHeader(card: RLTextOverview, b: RlLayoutFeedListBinding, pos: Int) {
            val classType = card.classType.orEmpty()

            // Hide metric chips by default
            b.layTime.viewCommon.visibility = View.GONE
            b.layCalories.viewCommon.visibility = View.GONE
            b.layAssumedeffort.viewCommon.visibility = View.GONE
            b.laySteps.viewCommon.visibility = View.GONE

            b.txtOrganizer.visibility = View.GONE
            b.txtOrganizerName.visibility = View.GONE
            b.imgOrganizerUser.visibility = View.GONE
            b.relayChart.visibility = View.GONE
            b.imgMain.visibility = View.VISIBLE
            b.imageChart.visibility = View.VISIBLE

            b.layAssumedeffort.txtTimeNumber.setTextColor(
                ContextCompat.getColor(activity, R.color.AppBlackColor)
            )

            // counts
            b.txtComment.text = if (card.total_comments > 0) card.total_comments.toString() else ""
            if (card.total_kudos > 0) {
                b.txtThum.text = card.total_kudos.toString()
                b.imgThum.setImageResource(R.drawable.fd_thumbs_gray)
            } else {
                b.txtThum.text = ""
                b.imgThum.setImageResource(R.drawable.ic_thumbs_g)
            }
            val totalAward = card.medals_gold + card.medals_silver + card.medals_bronze
            if (totalAward > 0) {
                b.txtAward.text = totalAward.toString()
                b.imgAward.setImageResource(R.drawable.ic_award)
            } else {
                b.txtAward.text = "0"
                b.imgAward.setImageResource(R.drawable.ic_award_g)
            }

            // visibility by user
            if (currentUserId == card.userid) {
                b.imgThreedot.visibility = View.VISIBLE
                b.layoutAward.visibility = View.VISIBLE
                b.imgThum.visibility = View.VISIBLE
                b.layoutShare.visibility = View.VISIBLE
                b.layoutThumb.visibility = View.VISIBLE
                b.layoutComment.visibility = View.VISIBLE
                b.blanckView.visibility = View.GONE
                b.blanckView1.visibility = View.GONE
            } else {
                b.imgThreedot.visibility = View.GONE
                b.layoutAward.visibility = View.GONE
                b.layoutShare.visibility = View.GONE
                b.blanckView.visibility = View.VISIBLE
                b.layoutThumb.visibility = View.VISIBLE
                b.layoutComment.visibility = View.VISIBLE
                b.blanckView1.visibility = View.GONE
            }

            b.txtUsername.text = card.username.orEmpty()
            b.txtMyride.text = card.className.orEmpty()
            b.imgMyride.setImageResource(RLTools.rl_geticon(classType))
            b.txtUserdatetime.text = RLTools.rl_convertTimestampToDateTime(card.timestamp.toLong())

            Glide.with(activity)
                .load(card.avatar)
                .placeholder(R.drawable.sample_user)
                .error(R.drawable.sample_user)
                .into(b.imgUser)

            val imageLinkMain = RLTools.rl_feedSetImage(card, currentUserId, selectTag)
            Glide.with(activity).load(imageLinkMain).into(b.imgMain)

            b.temptext.text =
                "pos: $pos , ctype: $classType , third: ${card.from_third_party_source} , bmo: ${card.bmo}, HR: ${card.hrm}"

            b.cardChalengis.setOnClickListener {
                when {
                    card.from_third_party_source == 0 -> {
                        val bundle = Bundle().apply {
                            putSerializable(RLConstants.CardData, card)
                            putString(RLConstants.FeedSelectTag, selectTag)
                            putBoolean("isSessionComplete", false)
                        }
                        val act = activity as RLMainActivityRL
                        when (card.bmo) {
                            0 -> act.rl_loadFrag(
                                RLFragSessionSummary().newInstance(bundle),
                                TAG, true, null, true
                            )
                            1 -> act.rl_loadFrag(
                                RLFragMindSessionSummary().newInstance(bundle),
                                TAG, true, null, true
                            )
                            2 -> act.rl_loadFrag(
                                RLFragSessionSummary().newInstance(bundle),
                                TAG, true, null, true
                            )
                        }
                    }
                    card.from_third_party_source == 101 -> {
                        // Big Challenge: handled via Join button click; no-op here
                    }
                    card.from_third_party_source > 10 -> {
                        val bundle = Bundle().apply {
                            putSerializable(RLConstants.CardData, card)
                        }
                        (activity as RLMainActivityRL).rl_loadFrag(
                            RLFragTenChallengeSummary().newInstance(bundle),
                            TAG, true, null, true
                        )
                    }
                    else -> showSimpleAlert()
                }
            }

            b.imgThreedot.setOnClickListener { showEditDeleteDialog(card) }

            b.imgShare.setOnClickListener {
                val bitmap = RLBranchManager(activity).rl_captureSpecificView(b.cardChalengis)
                val imgUri = RLBranchManager(activity).rl_bitmapToUri(bitmap)
                RLTools.rl_logEPrint(TAG, "capture image Uri: $imgUri")
                if (imgUri != null) RLBranchManager(activity).rl_shareImage(imgUri)
            }
        }

        // ---------- Card bodies ----------
        private fun challengeDesignCard(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding,
            pos: Int,
            isImperial: Boolean
        ) {
            b.txtOrganizer.visibility = View.VISIBLE
            b.txtOrganizerName.visibility = View.VISIBLE
            b.imgOrganizerUser.visibility = View.VISIBLE
            b.relayChart.visibility = View.VISIBLE
            b.imgMain.visibility = View.GONE
            b.imageChart.visibility = View.GONE

            b.txtOrganizerName.text = card.instructor.orEmpty()

            Glide.with(activity)
                .load(card.videoKey)
                .placeholder(R.drawable.sample_user)
                .error(R.drawable.sample_user)
                .into(b.imgOrganizerUser)

            val ws: WebSettings = b.webViewChart.settings
            ws.javaScriptEnabled = true
            ws.cacheMode = WebSettings.LOAD_NO_CACHE
            ws.domStorageEnabled = true
            ws.useWideViewPort = true
            ws.loadWithOverviewMode = true
            b.webViewChart.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
            b.webViewChart.isHorizontalScrollBarEnabled = false
            b.webViewChart.isVerticalScrollBarEnabled = false
            b.webViewChart.webViewClient = WebViewClient()

            val goal = toInt(card.goal)
            val duration = toInt(card.duration)
            val metric = RLTools.rl_getMetric(card.from_third_party_source, card)
            val stepsSoFar = (metric.value ?: 0).coerceAtLeast(0)
            val targetSteps = goal.coerceAtLeast(0)

            val remainingDays = (((System.currentTimeMillis() / 1000) - card.timestamp.toLong()) / 86400.0).roundToInt()
                .coerceAtLeast(0)
            val totalDays = (duration / 86400).toInt()
            val htmlText = RLAllHTMLChart.rl_getChallengeChartHtml(
                stepsSoFar,
                targetSteps,
                remainingDays,
                totalDays,
                RLTools.rl_getMetricsName(RLTools.rl_getClassTypeValue(card.classType))
            )
            b.webViewChart.loadDataWithBaseURL(null, htmlText, "text/html", "UTF-8", null)

            b.layTime.imgTime.setImageResource(R.drawable.ic_calender_daily)
            b.layTime.txtTime.setText(R.string.challenge_period)

            val daysRemain = (totalDays - remainingDays).coerceAtLeast(0)
            b.layTime.txtTimeNumber.text = "${(totalDays - daysRemain) + daysRemain} of $totalDays Days" // matches original behavior

            when (RLTools.rl_challengesTypeGet(card.classType.orEmpty().lowercase(Locale.ROOT))) {
                "effort" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
                    b.layCalories.txtTime.setText(R.string.youachived)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Effort, card)
                }
                "steps" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.fd_steps_green)
                    b.layCalories.txtTime.setText(R.string.youachived)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Steps, card)
                }
                "calories" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
                    b.layCalories.txtTime.setText(R.string.youachived)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)
                }
                "distance" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.ic_distance)
                    b.layCalories.txtTime.setText(R.string.distance)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Distance, card)
                }
                "climbed" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.ic_climb)
                    b.layCalories.txtTime.setText(R.string.distance)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Distance, card)
                }
                "duration" -> {
                    b.layCalories.imgTime.setImageResource(R.drawable.fd_active_time_green)
                    b.layCalories.txtTime.setText(R.string.youachived)
                    b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Steps, card)
                }
            }

            if (card.originalClassDate == "shared") {
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_goal)
                b.layAssumedeffort.txtTime.setText(R.string.sharedtargetcaps)
                b.layAssumedeffort.txtTimeNumber.text = valueFor(RLValueName.Goal, card)
            } else {
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_goal)
                b.layAssumedeffort.txtTime.setText(R.string.individualtargetcaps)
                b.layAssumedeffort.txtTimeNumber.text = valueFor(RLValueName.Goal, card)
            }

            b.laySteps.txtTime.setText(R.string.currenrrank)
            b.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
            b.laySteps.txtTimeNumber.text = valueFor(RLValueName.Rank, card)

            b.laySteps.relativeCard.visibility = View.VISIBLE
            b.layAssumedeffort.relativeCard.visibility = View.VISIBLE
            b.layBottom.visibility = View.VISIBLE

            b.blanckView1.visibility = View.VISIBLE
            b.layoutShare.visibility = View.VISIBLE
            b.layoutThumb.visibility = View.GONE
            b.layoutComment.visibility = View.GONE
            b.blanckView.visibility = View.GONE
            b.layoutAward.visibility = View.GONE
        }

        private fun thirdPartyAppleHealth(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding,
            isImperial: Boolean
        ) {
            b.layTime.imgTime.setImageResource(R.drawable.fd_steps_green)
            b.layTime.txtTime.setText(R.string.step)
            b.layTime.txtTimeNumber.text = valueFor(RLValueName.Steps, card)
            b.layTime.relativeCard.visibility = View.VISIBLE

            b.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
            b.layCalories.txtTime.setText(R.string.calorie)
            b.layCalories.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)

            b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            b.layAssumedeffort.txtTime.setText(
                if (isImperial) R.string.distancemiles else R.string.distancekm
            )
            b.layAssumedeffort.txtTimeNumber.text = valueFor(RLValueName.Distance, card)

            b.laySteps.imgTime.setImageResource(R.drawable.ic_dance)
            b.laySteps.txtTime.setText(R.string.stadinghour)
            b.laySteps.txtTimeNumber.text = "--"

            b.laySteps.relativeCard.visibility = View.VISIBLE
            b.layAssumedeffort.relativeCard.visibility = View.VISIBLE

            if (card.userid == currentUserId) {
                b.layoutAward.visibility = View.VISIBLE
                b.layoutThumb.visibility = View.VISIBLE
                b.layoutComment.visibility = View.VISIBLE
                b.imgThreedot.visibility = View.GONE
            } else {
                b.layoutAward.visibility = View.GONE
                b.layoutThumb.visibility = View.GONE
                b.layoutComment.visibility = View.GONE
                b.imgThreedot.visibility = View.GONE
                b.blanckView1.visibility = View.VISIBLE
            }
        }

        private fun thirdPartyGoogleFit(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding,
            isImperial: Boolean
        ) {
            b.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
            b.layTime.txtTime.setText(R.string.time)
            b.layTime.txtTimeNumber.text = valueFor(RLValueName.TotalTime, card)
            b.layTime.relativeCard.visibility = View.VISIBLE

            b.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
            b.layCalories.txtTime.setText(R.string.revoolaeffort)
            b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Effort, card)

            val classTypeLc = card.classType.orEmpty().lowercase(Locale.ROOT)
            if (classTypeLc == "ride" || classTypeLc == "run" || classTypeLc == "walk") {
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
                b.layAssumedeffort.txtTime.setText(R.string.calorie)
                b.layAssumedeffort.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)

                b.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
                b.laySteps.txtTime.setText(if (isImperial) R.string.distancemiles else R.string.distancekm)
                b.laySteps.txtTimeNumber.text = valueFor(RLValueName.Distance, card)
            } else {
                val zone = RLTools.rl_verifyFeedZoneName((card.avgRevPercentage.toDouble() ?: 0.0))
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
                b.layAssumedeffort.txtTime.setText(R.string.effortzone)
                b.layAssumedeffort.txtTimeNumber.text = zone.efforZoneText
                b.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(zone.efforZoneTxtClr))

                b.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
                b.laySteps.txtTime.setText(R.string.calorie)
                b.laySteps.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)
            }

            b.laySteps.relativeCard.visibility = View.VISIBLE
            b.layAssumedeffort.relativeCard.visibility = View.VISIBLE
            b.imgThreedot.visibility = View.GONE
        }

        private fun yourWayCard(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding,
            isImperial: Boolean
        ) {
            b.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
            b.layTime.txtTime.setText(R.string.time)
            b.layTime.txtTimeNumber.text = valueFor(RLValueName.TotalTime, card)
            b.layTime.relativeCard.visibility = View.VISIBLE

            b.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
            b.layCalories.txtTime.setText(R.string.effort)
            b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Effort, card)

            val classTypeLc = card.classType.orEmpty().lowercase(Locale.ROOT)
            if (classTypeLc == "ride" || classTypeLc == "run" || classTypeLc == "walk") {
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
                b.layAssumedeffort.txtTime.setText(R.string.calorie)
                b.layAssumedeffort.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)

                b.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
                b.laySteps.txtTime.setText(if (isImperial) R.string.distancemiles else R.string.distancekm)
                b.laySteps.txtTimeNumber.text = valueFor(RLValueName.Distance, card)
            } else {
                val zone = RLTools.rl_verifyFeedZoneName((card.avgRevPercentage.toDouble() ?: 0.0))
                b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
                b.layAssumedeffort.txtTime.setText(R.string.effortzone)
                b.layAssumedeffort.txtTimeNumber.text = zone.efforZoneText
                b.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(zone.efforZoneTxtClr))

                b.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
                b.laySteps.txtTime.setText(R.string.calorie)
                b.laySteps.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)
            }

            b.laySteps.relativeCard.visibility = View.VISIBLE
            b.layAssumedeffort.relativeCard.visibility = View.VISIBLE
        }

        private fun bodyCard(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding,
            isImperial: Boolean
        ) {
            b.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
            b.layTime.txtTime.setText(R.string.time)
            b.layTime.txtTimeNumber.text = valueFor(RLValueName.TotalTime, card)
            b.layTime.relativeCard.visibility = View.VISIBLE

            b.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
            b.layCalories.txtTime.setText(R.string.effort)
            b.layCalories.txtTimeNumber.text = valueFor(RLValueName.Effort, card)

            val zone = RLTools.rl_verifyFeedZoneName((card.avgRevPercentage.toDouble() ?: 0.0))
            b.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            b.layAssumedeffort.txtTime.setText(R.string.effortzone)
            b.layAssumedeffort.txtTimeNumber.text = zone.efforZoneText
            b.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(zone.efforZoneTxtClr))

            b.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
            b.laySteps.txtTime.setText(R.string.calorie)
            b.laySteps.txtTimeNumber.text = valueFor(RLValueName.ActiveCalories, card)

            b.laySteps.relativeCard.visibility = View.VISIBLE
            b.layAssumedeffort.relativeCard.visibility = View.VISIBLE
        }

        private fun mindCard(
            card: RLTextOverview,
            b: RlLayoutFeedListBinding
        ) {
            b.layTime.imgTime.setImageResource(R.drawable.ic_mind_read)
            b.layTime.txtTime.setText(R.string.mindfulminutes)
            b.layTime.txtTimeNumber.text = valueFor(RLValueName.TotalTime, card)
            b.layTime.relativeCard.visibility = View.VISIBLE

            b.layCalories.imgTime.setImageResource(R.drawable.ic_mind_read)
            b.layCalories.txtTime.setText(R.string.assumedrelaxation)
            b.layCalories.txtTimeNumber.text = valueFor(RLValueName.AssumedRelaxation, card)

            b.layAssumedeffort.relativeCard.visibility = View.GONE
            b.laySteps.relativeCard.visibility = View.GONE
        }

        // Big challenge (join)
        @SuppressLint("NewApi")
        private fun bigChallengeJoinCard(card: RLTextOverview, b: RlLayoutFeedListBinding) {
            Glide.with(activity).load(card.imageLinkSmall).into(b.imgMainBigChallenges)
            b.bigChallengesTitle.text = card.mainTitle.orEmpty()
            b.bigChallengesDescription.text = Html.fromHtml(
                card.className.orEmpty(),
                Html.FROM_HTML_MODE_LEGACY
            )
            b.ButtonJoinChallenge.setOnClickListener { onItemClicked(card) }
        }

        // ---------- Dialogs ----------
        private fun showSimpleAlert() {
            val dlg = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.rl_alertdialog_custom_layout)
                setCancelable(false)
                window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
            }
            val ok: TextView = dlg.findViewById(R.id.iv_ok)
            ok.setOnClickListener { dlg.dismiss() }
            dlg.show()
        }

        private fun showEditDeleteDialog(cardData: RLTextOverview) {
            val dialog = Dialog(activity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.rl_dailog_edit_delete_feedcard)
                setCancelable(true)
                window?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                window?.setBackgroundDrawableResource(R.color.transparent_dialog)
            }

            dialog.findViewById<TextView>(R.id.txt_edit)?.setOnClickListener { dialog.dismiss() }
            dialog.findViewById<TextView>(R.id.txt_delete)?.setOnClickListener { dialog.dismiss() }
            dialog.findViewById<TextView>(R.id.btn_cancle)?.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }

        // ---------- Value helpers ----------
        private fun valueFor(title: String, card: RLTextOverview): String {
            val isImperial = RLTools.rl_getIsImperial(appUnit)
            return when (title) {
                RLValueName.TotalTime -> RLTools.rl_daytimeget(toInt(card.totalTime))
                RLValueName.Effort -> RLTools.rl_formatCommasInt(toInt(card.totalREV))
                RLValueName.Boosts -> (card.total_kudos.takeIf { it != 0 } ?: 0).toString()
                RLValueName.Comments -> (card.total_comments.takeIf { it != 0 } ?: 0).toString()
                RLValueName.Awards -> {
                    val total = (card.medals_bronze + card.medals_silver + card.medals_gold)
                    (total.takeIf { it != 0 } ?: 0).toString()
                }
                RLValueName.Steps -> RLTools.rl_formatCommasInt(toInt(card.steps))
                RLValueName.Distance -> {
                    val km = (card.distance ?: 0.0)
                    val v = if (!isImperial) km else km * 0.621371
                    RLTools.rl_formatCommas(v)
                }
                RLValueName.Climbed -> {
                    val raw = toInt(card.elevation ?: -1)
                    if (raw < 0) {
                        "Pending"
                    } else {
                        val v = if (!isImperial) raw else (raw * 3.28084)
                        RLTools.rl_formatCommasInt(v.toDouble())
                    }
                }
                RLValueName.AvgEffort -> "${toInt(card.avgRevPercentage)}%"
                RLValueName.MaxEffort -> "${toInt(card.maxRevPercentage)}%"
                RLValueName.ActiveCalories -> toInt(card.burntCalories).toString()
                RLValueName.AssumedRelaxation -> RLTools.rl_formatCommasInt(toInt(card.totalRMS))
                RLValueName.Rank -> "${card.hrm} of ${card.share_map}"
                RLValueName.Goal -> RLTools.rl_formatCommasInt(toInt(card.goal))
                else -> "0"
            }
        }

        private fun toInt(value: Any?): Int {
            val r = when (value) {
                is Double -> value.roundToInt()
                is Float -> value.roundToInt()
                is Int -> value
                is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
                else -> 0
            }
            return r.coerceAtLeast(0)
        }
    }
}
