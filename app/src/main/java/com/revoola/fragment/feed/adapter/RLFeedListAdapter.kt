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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.branchManagerIo.RLBranchManager
import com.revoola.databinding.RlLayoutFeedListBinding
import com.revoola.fragment.feed.RLFragMindSessionSummary
import com.revoola.fragment.feed.RLFragSessionSummary
import com.revoola.fragment.feed.RLFragTenChallengeSummary
import com.revoola.model.RLTextOverview
import com.revoola.services.RLAllHTMLChart
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.enumclass.RLValueName
import kotlin.math.roundToInt

class RLFeedListAdapter(val context: FragmentActivity?,currentUser: String,
                        val selectTag:String,val appUnit:String,private val onItemClicked: (RLTextOverview) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLFeedListAdapter"
    private var isLoadingAdded = false
    private val dataList = mutableListOf<RLTextOverview>()
    val currentUser = currentUser
    var classType=""
    companion object {
        private const val ITEM_TYPE_DATA = 0
        private const val ITEM_TYPE_LOADING = 1
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_DATA) {
            val layoutBinding: RlLayoutFeedListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_feed_list , parent, false)
            return MyViewHolder(layoutBinding)
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
    fun RLaddData(newData: List<RLTextOverview>) {
        val startPosition = dataList.size
        dataList.addAll(newData)
        notifyItemRangeInserted(startPosition, newData.size)
    }
    inner class MyViewHolder(layoutBinding: RlLayoutFeedListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutFeedListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            try {
                val cardData: RLTextOverview = dataList[position]
                RLcommonDataSet(cardData, layoutBinding,position+1)
                layoutBinding.mainLayoutFeed.visibility=View.VISIBLE
                layoutBinding.bigChallengesLayout.visibility=View.GONE
                val isImperial = RLTools.RLGetIsImperial(appUnit)
                when(cardData.from_third_party_source){
                    0->{
                        when(cardData.bmo){
                            0->{
                                //BODY
                                RLbodyClassesBodySet(cardData, layoutBinding,isImperial)
                            }
                            1->{
                                //MIND
                                RLmindClassBodySet(cardData, layoutBinding,isImperial)
                            }
                            2->{
                                //OTHER Your Way
                                RLotherClassesBodySet(cardData, layoutBinding,isImperial)
                            }
                        }
                    }
                    1->{ // 3rd party card
                        RLthirdPartyOneBodySet(cardData, layoutBinding,isImperial)
                    }
                    2->{ // Metric Card
                        RLthirdPartyTwoBodySet(cardData, layoutBinding,isImperial)
                    }
                    101->{
                        //Big Challenges Join Session
                        layoutBinding.mainLayoutFeed.visibility=View.GONE
                        layoutBinding.bigChallengesLayout.visibility=View.VISIBLE
                        RLBigChallengesJoinBodySet(cardData, layoutBinding)
                    }
                     else -> {// >10 Challenges
                        RLthirdPartyTenBodySet(cardData, layoutBinding,position+1,isImperial)

                    }
                }
            }
            catch (e: Exception) {
            RLTools.RlLogDPrint(TAG, "exception= " + e.message)
                val temptext="pos:- ${position.toString()} , ctype:- $classType , third:- ${dataList[position].from_third_party_source.toString()} , bmo:- ${dataList[position].bmo.toString()}, HR:- ${dataList[position].hrm.toString()}"
                layoutBinding.temptext.setText("exception:- ${e.message.toString()} :- $temptext")
            }
        }
    }

    //All Card Common Value Like UserImage Title Click Event
    private fun RLcommonDataSet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, position: Int){
        RLTools.RLheightsetimageview(layoutBinding.imgMain)

        RLTools.RLheightsetRelative(layoutBinding.relayChart)
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }

        layoutBinding.layTime.viewCommon.visibility = View.GONE
        layoutBinding.layCalories.viewCommon.visibility = View.GONE
        layoutBinding.layAssumedeffort.viewCommon.visibility = View.GONE
        layoutBinding.laySteps.viewCommon.visibility = View.GONE

        layoutBinding.txtOrganizer.visibility=View.GONE
        layoutBinding.txtOrganizerName.visibility=View.GONE
        layoutBinding.imgOrganizerUser.visibility=View.GONE
        layoutBinding.relayChart.visibility=View.GONE
        layoutBinding.imgMain.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.txtTimeNumber.setTextColor(context!!.resources.getColor(R.color.AppBlackColor))

        if (cardData.total_comments > 0) {
            layoutBinding.txtComment.setText(cardData.total_comments.toString())
        } else {
            layoutBinding.txtComment.setText("")
        }

        if (cardData.total_kudos > 0) {
            layoutBinding.txtThum.setText(cardData.total_kudos.toString())
            layoutBinding.imgThum.setImageResource(R.drawable.fd_thumbs_gray)
        } else {
            layoutBinding.txtThum.setText("")
            layoutBinding.imgThum.setImageResource(R.drawable.ic_thumbs_g)
        }

        val totalAward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        if (totalAward > 0) {
            layoutBinding.txtAward.setText(totalAward.toString())
            layoutBinding.imgAward.setImageResource(R.drawable.ic_award)
        } else {
            layoutBinding.txtAward.setText("0")
            layoutBinding.imgAward.setImageResource(R.drawable.ic_award_g)
        }

        if (currentUser.equals(cardData.userid)){
            layoutBinding.imgThreedot.visibility=View.VISIBLE
            layoutBinding.layoutAward.visibility=View.VISIBLE
            layoutBinding.imgThum.visibility=View.VISIBLE
            layoutBinding.layoutShare.visibility=View.VISIBLE
            layoutBinding.layoutThumb.visibility=View.VISIBLE
            layoutBinding.layoutComment.visibility=View.VISIBLE
            layoutBinding.blanckView.visibility=View.GONE
            layoutBinding.blanckView1.visibility=View.GONE
        }else{
            layoutBinding.imgThreedot.visibility=View.GONE
            layoutBinding.layoutAward.visibility=View.GONE
            layoutBinding.layoutShare.visibility=View.GONE
            layoutBinding.blanckView.visibility=View.VISIBLE
            layoutBinding.layoutThumb.visibility=View.VISIBLE
            layoutBinding.layoutComment.visibility=View.VISIBLE
            layoutBinding.blanckView1.visibility=View.GONE
        }

        layoutBinding.txtUsername.setText(cardData.username.toString())
        layoutBinding.txtMyride.setText(cardData.className.toString())

        layoutBinding.imgMyride.setImageResource(RLTools.RLgeticon(classType))
        layoutBinding.txtUserdatetime.setText(RLTools.RLconvertTimestampToDateTime(cardData.timestamp.toLong()))

        Glide.with(context).load(cardData.avatar).placeholder(R.drawable.sample_user).error(R.drawable.sample_user).into(layoutBinding.imgUser)

        val imageLinkMain=RLTools.RLFeedSetImage(cardData,currentUser,selectTag)
        Glide.with(context).load(imageLinkMain).into(layoutBinding.imgMain)

        layoutBinding.temptext.setText("pos:- ${position.toString()} , ctype:- $classType , third:- ${cardData.from_third_party_source.toString()} , bmo:- ${cardData.bmo.toString()}, HR:- ${cardData.hrm.toString()}")

        layoutBinding.cardChalengis.setOnClickListener{
            if (cardData.from_third_party_source == 0){
                when (cardData.bmo){
                    0->{
                        //BODY
                        val bundle = Bundle()
                        bundle.putSerializable(RLConstants.CardData, cardData)
                        bundle.putString(RLConstants.FeedSelectTag, selectTag)
                        bundle.putBoolean("isSessionComplete", false)
                        //(context as RLMainActivityRL).RLloadFrag(RLFragBodySessionSummary().newInstance(bundle), TAG, true, null, true)
                        (context as RLMainActivityRL).RLloadFrag(RLFragSessionSummary().newInstance(bundle), TAG, true, null, true)
                    }
                    1->{
                        //MIND
                        val bundle = Bundle()
                        bundle.putSerializable(RLConstants.CardData, cardData)
                        bundle.putString(RLConstants.FeedSelectTag, selectTag)
                        bundle.putBoolean("isSessionComplete", false)
                        (context as RLMainActivityRL).RLloadFrag(RLFragMindSessionSummary().newInstance(bundle), TAG, true, null, true)
                    }
                    2->{
                        //OTHER
                        val bundle = Bundle()
                        bundle.putSerializable(RLConstants.CardData, cardData)
                        bundle.putString(RLConstants.FeedSelectTag, selectTag)
                        bundle.putBoolean("isSessionComplete", false)
                        (context as RLMainActivityRL).RLloadFrag(RLFragSessionSummary().newInstance(bundle), TAG, true, null, true)
                    }

                }
            }
            else if(cardData.from_third_party_source == 101){
                //BIG Challenge
            }
            else if(cardData.from_third_party_source > 10){
                //Challenge design
                val bundle = Bundle()
                bundle.putSerializable(RLConstants.CardData, cardData)
                (context as RLMainActivityRL).RLloadFrag(RLFragTenChallengeSummary().newInstance(bundle), TAG, true, null, true)
            }
            else{
                RLshowAlertDialog()
            }
        }
        layoutBinding.imgThreedot.setOnClickListener {
            RLshowEditDeleteDialog(cardData)
        }

        layoutBinding.imgShare.setOnClickListener {
           // val bitmap = RLBranchManager(context!!).RLCaptureScreen(context!!)
            val bitmap = RLBranchManager(context!!).RLCaptureSpecificView(layoutBinding.cardChalengis)
           // val pathUri =RLBranchManager(context!!).RLSaveBitmapToInternalStorage(bitmap,"Capture")
            val imgUri = RLBranchManager(context!!).RLBitmapToUri(bitmap)

           RLTools.RlLogEPrint(TAG,"catch image  Uri:- $imgUri")
            if (imgUri != null) {
                RLBranchManager(context!!).RLShareImage(imgUri)
            }else{
               RLTools.RlLogEPrint(TAG,"Test Uri:- $imgUri")
            }

        }

        /*layoutBinding.imgComment.setOnClickListener {
            var passstring="Comment"
            if (passstring.isNotEmpty()){
                val bundle = Bundle()
                bundle.putSerializable(RLConstants.CardData, cardData)
                bundle.putString(RLConstants.TYPE, passstring)
                (context as RLMainActivityRL).RLloadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
            }
        }
        layoutBinding.imgThum.setOnClickListener {
            var passstring="Thumb"
            if (passstring.isNotEmpty()){
                val bundle = Bundle()
                bundle.putSerializable(RLConstants.CardData, cardData)
                bundle.putString(RLConstants.TYPE, passstring)
                (context as RLMainActivityRL).RLloadFrag(RLFragFeedCardLikeCommentView().newInstance(bundle), TAG, true, null, false)
            }
        }*/
    }
    //When ThirdParty >10 Challenges Card once Check
    private fun RLthirdPartyTenBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, pos: Int, isImperial: Boolean){
        layoutBinding.txtOrganizer.visibility=View.VISIBLE
        layoutBinding.txtOrganizerName.visibility=View.VISIBLE
        layoutBinding.imgOrganizerUser.visibility=View.VISIBLE
        layoutBinding.relayChart.visibility=View.VISIBLE
        layoutBinding.imgMain.visibility=View.GONE

        layoutBinding.txtOrganizerName.setText(cardData.instructor.toString())

        Glide.with(context!!).load(cardData.videoKey)
            .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(layoutBinding.imgOrganizerUser)


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

        val goal = convertToInt(cardData.goal)
        val duration = convertToInt(cardData.duration)
        val metric =  RLTools.RLgetMetric(cardData.from_third_party_source,cardData)
        val stepsSoFar = if (metric.value ?: 0 > 0) metric.value ?: 0 else 0
        val targetSteps = if (goal ?: 0 > 0) goal ?: 0 else 0


        val remainingDays = Math.round((System.currentTimeMillis() / 1000 - cardData.timestamp.toLong()) / 86400.0).toInt()?: 0
        val timeGone = if (remainingDays >0) remainingDays else 0

        val totalDays = (duration / 86400).toInt()
        val totalTime = if (totalDays ?: 0 > 0) totalDays ?: 0 else 0

        val htmlText= RLAllHTMLChart.RLgetChallengeChartHtml(stepsSoFar,targetSteps,timeGone,totalTime)

        layoutBinding.webViewChart.loadDataWithBaseURL(null,
            htmlText, "text/html", "UTF-8", null)

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_calender_daily)
        layoutBinding.layTime.txtTime.setText("CHALLENGE PERIOD")

        if (remainingDays.toInt() > 0){
            val daysRemain=totalDays.toInt()-remainingDays.toInt()
            if (daysRemain>0){
                layoutBinding.layTime.txtTimeNumber.setText("${daysRemain.toString()} of ${totalDays.toString()} Days")
            }else{
                layoutBinding.layTime.txtTimeNumber.setText("${totalDays.toString()} of ${totalDays.toString()} Days")
            }

        }else{
            layoutBinding.layTime.txtTimeNumber.setText("${totalDays.toString()} of ${totalDays.toString()} Days")
        }

        when(RLTools.RLChallengesTypeGet(classType.toLowerCase())){
            "effort"->{
                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
                layoutBinding.layCalories.txtTime.setText(R.string.youachived)
                layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Effort,cardData))

            }
            "steps"->{

                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_steps_green)
                layoutBinding.layCalories.txtTime.setText(R.string.youachived)
                layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Steps,cardData))

            }
            "calories"->{
                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
                layoutBinding.layCalories.txtTime.setText(R.string.youachived)
                layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

            }
            "distance"->{
                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_distance)
                layoutBinding.layCalories.txtTime.setText(R.string.distance)
                layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

            }
            "climbed"->{
                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_climb)
                layoutBinding.layCalories.txtTime.setText(R.string.distance)
                layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

            }
            "duration"->{
                layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_active_time_green)
                layoutBinding.layCalories.txtTime.setText(R.string.youachived)
                layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

            }
        }


        if (cardData.originalClassDate.equals("shared")){
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.sharedtargetcaps)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommasInt(convertToInt(cardData.goal)))

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.individualtargetcaps)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommasInt(convertToInt(cardData.goal)))

        }

        layoutBinding.laySteps.txtTime.setText(R.string.currenrrank)
        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
        layoutBinding.laySteps.txtTimeNumber.setText("${cardData.hrm.toString()} of ${cardData.share_map.toString()}")

        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

        layoutBinding.layBottom.visibility=View.VISIBLE

        layoutBinding.blanckView1.visibility=View.VISIBLE
        layoutBinding.layoutShare.visibility=View.VISIBLE
        layoutBinding.layoutThumb.visibility=View.GONE
        layoutBinding.layoutComment.visibility=View.GONE
        layoutBinding.blanckView.visibility=View.GONE
        layoutBinding.layoutAward.visibility=View.GONE

    }
    //when ThirdParty 2 and AppleIos Card done
    private fun RLthirdPartyTwoBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, isImperial: Boolean){

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_steps_green)
        layoutBinding.layTime.txtTime.setText(R.string.step)
        layoutBinding.layTime.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Steps,cardData))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layCalories.txtTime.setText(R.string.calorie)
        layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

        layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
        if (isImperial) {
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distancemiles)
        }else{
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distancekm)
        }
        layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_dance)
        layoutBinding.laySteps.txtTime.setText(R.string.stadinghour)
        layoutBinding.laySteps.txtTimeNumber.setText("--")


        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

        if (cardData.userid.equals(currentUser)){
            layoutBinding.layoutAward.visibility=View.VISIBLE
            layoutBinding.layoutThumb.visibility=View.VISIBLE
            layoutBinding.layoutComment.visibility=View.VISIBLE
            layoutBinding.imgThreedot.visibility=View.GONE
        }else{
            layoutBinding.layoutAward.visibility=View.GONE
            layoutBinding.layoutThumb.visibility=View.GONE
            layoutBinding.layoutComment.visibility=View.GONE
            layoutBinding.imgThreedot.visibility=View.GONE
            layoutBinding.blanckView1.visibility=View.VISIBLE
        }

       /* layoutBinding.layoutAward.visibility=View.GONE
        layoutBinding.layoutThumb.visibility=View.GONE
        layoutBinding.layoutComment.visibility=View.GONE
        layoutBinding.imgThreedot.visibility=View.GONE
        layoutBinding.blanckView1.visibility=View.VISIBLE*/

    }
    //when ThirdParty 1 and GoogleFit Card done
    private fun RLthirdPartyOneBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, isImperial: Boolean){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.TotalTime,cardData))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.revoolaeffort)
        layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Effort,cardData))


        if (classType.toLowerCase().equals("ride")||classType!!.toLowerCase().equals("run")||classType!!.toLowerCase().equals("walk")){

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
            if (isImperial){
                layoutBinding.laySteps.txtTime.setText(R.string.distancemiles)
            }else{
                layoutBinding.laySteps.txtTime.setText(R.string.distancekm)
            }

            layoutBinding.laySteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))


        }else{
            val ZoneTextData= RLTools.RlVerifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.effortzone)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(ZoneTextData.efforZoneText)
            layoutBinding.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))


            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.laySteps.txtTime.setText(R.string.calorie)
            layoutBinding.laySteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

        }

        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.imgThreedot.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    //when ThirdParty 0 and Bmo 2 YourWay Card done
    private fun RLotherClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, isImperial: Boolean){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.TotalTime,cardData))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Effort,cardData))


        if (classType.toLowerCase().equals("ride")||classType.toLowerCase().equals("run")||classType.toLowerCase().equals("walk")){

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))


            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
            if (isImperial){
                layoutBinding.laySteps.txtTime.setText(R.string.distancemiles)
            }else{
                layoutBinding.laySteps.txtTime.setText(R.string.distancekm)
            }

            layoutBinding.laySteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Distance,cardData))

        }else{
            val ZoneTextData= RLTools.RlVerifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.effortzone)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(ZoneTextData.efforZoneText)
            layoutBinding.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))

            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.laySteps.txtTime.setText(R.string.calorie)
            layoutBinding.laySteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))

        }

        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    //when ThirdParty 0 and Bmo 0 Body Card done
    private fun RLbodyClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, isImperial: Boolean){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.TotalTime,cardData))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.Effort,cardData))

        val ZoneTextData= RLTools.RlVerifyFeedZoneName(cardData.avgRevPercentage.toDouble()?:0.0)
        layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layAssumedeffort.txtTime.setText(R.string.effortzone)
        layoutBinding.layAssumedeffort.txtTimeNumber.setText(ZoneTextData.efforZoneText)
        layoutBinding.layAssumedeffort.txtTimeNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.laySteps.txtTime.setText(R.string.calorie)
        layoutBinding.laySteps.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.ActiveCalories,cardData))


        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE


    }
    //when ThirdParty 0 and Bmo  1 Mind Card done
    private fun RLmindClassBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, isImperial: Boolean) {

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layTime.txtTime.setText(R.string.mindfulminutes)
        layoutBinding.layTime.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.TotalTime,cardData))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layCalories.txtTime.setText(R.string.assumedrelaxation)
        layoutBinding.layCalories.txtTimeNumber.setText(RLGetValueForTitle(RLValueName.AssumedRelaxation,cardData))


        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE
        layoutBinding.laySteps.relativeCard.visibility=View.GONE

    }

    //When ThirdParty  101 then Big Challenges Card Show
    @SuppressLint("NewApi")
    private fun RLBigChallengesJoinBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        Glide.with(context!!).load(cardData.imageLinkSmall).into(layoutBinding.imgMainBigChallenges)
        layoutBinding.bigChallengesTitle.setText(cardData.mainTitle)
        layoutBinding.bigChallengesDescription.text = Html.fromHtml(cardData.className, Html.FROM_HTML_MODE_LEGACY)
        layoutBinding.ButtonJoinChallenge.setOnClickListener {
            onItemClicked(cardData)
        }
    }

    //Alert AND Delete Dialog
    private fun RLshowAlertDialog() {
        val sucDialog: Dialog = Dialog(context!!)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }
    private fun RLshowEditDeleteDialog(cardData: RLTextOverview) {
        val  dialog: Dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.rl_dailog_edit_delete_feedcard)
        dialog.setCancelable(true)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT)

        val tvEdit : TextView =  dialog.findViewById(R.id.txt_edit)
        val tvDelete : TextView =  dialog.findViewById(R.id.txt_delete)
        val btnClose : TextView = dialog.findViewById(R.id.btn_cancle)

        tvEdit.setOnClickListener {
            dialog.dismiss()
        }
        tvDelete.setOnClickListener {
            dialog.dismiss()
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun RLGetValueForTitle(title: String,cardData:RLTextOverview): String {
            val isImperial = RLTools.RLGetIsImperial(appUnit)

            return when (title) {
                RLValueName.TotalTime -> RLTools.RLdaytimeget(convertToInt(cardData.totalTime))
                RLValueName.Effort  -> RLTools.RLformatCommasInt(convertToInt(cardData.totalREV))
                RLValueName.Boosts  -> if (cardData.total_kudos != 0) cardData.total_kudos.toString() else "0"
                RLValueName.Comments  -> if (cardData.total_comments != 0) cardData.total_comments.toString() else "0"
                RLValueName.Awards  -> {
                    val totalAwards = cardData.medals_bronze + cardData.medals_silver + cardData.medals_gold
                    if (totalAwards != 0) totalAwards.toString() else "0"
                }
                RLValueName.Steps  -> RLTools.RLformatCommasInt(convertToInt(cardData.steps))
                RLValueName.Distance  -> if (!isImperial) RLTools.RLformatCommas(cardData.distance?:0.0) else RLTools.RLformatCommas(cardData.distance * 0.621371)
                RLValueName.Climbed  -> {
                    val demsElevation:Int = convertToInt(cardData.elevation?:-1)
                    val elevation = if (!isImperial) {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt(demsElevation)
                    } else {
                        if (demsElevation == -1) "Pending" else RLTools.RLformatCommasInt((demsElevation * 3.28084))
                    }
                    elevation.toString()
                }
                RLValueName.AvgEffort -> convertToInt(cardData.avgRevPercentage).toString()+"%"
                RLValueName.MaxEffort -> convertToInt(cardData.maxRevPercentage).toString()+"%"
                RLValueName.ActiveCalories -> convertToInt(cardData.burntCalories).toString()
                RLValueName.AssumedRelaxation -> RLTools.RLformatCommasInt(convertToInt(cardData.totalRMS))
                else -> "0"
            }

    }
    private fun convertToInt(value: Any): Int {
        return when (value) {
            is Double -> value.roundToInt()
            is Float -> value.roundToInt()
            is Int -> value
            is String -> value.toDoubleOrNull()?.roundToInt() ?: 0
            else -> 0 // Default fallback for unsupported types
        }
    }

}
