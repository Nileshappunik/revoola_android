package com.example.myfirstapp.fragment.feed.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlLayoutFeedListBinding
import com.example.myfirstapp.fragment.feed.RLFragMindSessionSummary
import com.example.myfirstapp.fragment.feed.RLFragSessionSummary
import com.example.myfirstapp.fragment.feed.RLFragTenChallengeSummary
import com.example.myfirstapp.model.RLTextOverview
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLTools
import kotlin.math.roundToInt

class RLFeedListAdapter(val context: FragmentActivity?, currentUser: String) :
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
                val cardData:RLTextOverview= dataList[position]
                RLcommonDataSet(cardData, layoutBinding,position+1)
                if (cardData.from_third_party_source == 0) {
                    if (cardData.bmo == 0) {
                        //BODY
                        RLbodyClassesBodySet(cardData, layoutBinding)
                    } else if (cardData.bmo == 1) {
                        //MIND
                        RLmindClassBodySet(cardData, layoutBinding)
                    } else if (cardData.bmo == 2) {
                        //OTHER
                        RLotherClassesBodySet(cardData, layoutBinding)
                    }
                }
                else if (cardData.from_third_party_source == 1) {
                    RLthirdPartyOneBodySet(cardData, layoutBinding)
                } else if (cardData.from_third_party_source == 2) {
                    RLthirdPartyTwoBodySet(cardData, layoutBinding)
                } else if (cardData.from_third_party_source > 10) {
                    RLthirdPartyTenBodySet(cardData, layoutBinding)
                }
            } catch (e: Exception) {
            Log.d(TAG, "exception= " + e.message)
                layoutBinding.temptext.setText("exception:- ${e.message.toString()}")
            }
        }
    }
    private fun RLcommonDataSet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding, position: Int){
        if (cardData.classType.isNullOrEmpty()){
            classType=""
        }else{
            classType = cardData.classType!!
        }
        RLTools.RLheightsetimageview(layoutBinding.imgNain)
        layoutBinding.layTime.viewCommon.visibility = View.GONE
        layoutBinding.layCalories.viewCommon.visibility = View.GONE
        layoutBinding.layAssumedeffort.viewCommon.visibility = View.GONE
        layoutBinding.laySteps.viewCommon.visibility = View.GONE

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
        val totlaaward = cardData.medals_gold + cardData.medals_silver + cardData.medals_bronze
        if (totlaaward > 0) {
            layoutBinding.txtAward.setText(totlaaward.toString())
        } else {
            layoutBinding.txtAward.setText("0")
        }

        if (currentUser.equals(cardData.userid)){
            layoutBinding.imgThreedot.visibility=View.VISIBLE
            layoutBinding.layoutAward.visibility=View.VISIBLE
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


        Glide.with(context!!).load(cardData.avatar)
            .placeholder(R.drawable.wellcome)
            .error(R.drawable.wellcome)
            .into(layoutBinding.imgUser)

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(context).load(cardData.imageLinkSmall).into(layoutBinding.imgNain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(context).load(cardData.map_image).into(layoutBinding.imgNain)
        }else{
            Glide.with(context).load(RLTools.RLgetImage(classType)).into(layoutBinding.imgNain)
        }
        layoutBinding.temptext.setText("pos:- ${position.toString()} , ctype:- $classType , third:- ${cardData.from_third_party_source.toString()} , bmo:- ${cardData.bmo.toString()}, HR:- ${cardData.hrm.toString()}")
        layoutBinding.cardChalengis.setOnClickListener {
            if (cardData.from_third_party_source == 0){
                if (cardData.bmo == 1) {
                    //MIND
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    (context as RLMainActivityRL).RLbottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindSessionSummary().newInstance(bundle), TAG, true, null, false)
                }else  if (cardData.bmo == 2){
                    val bundle = Bundle()
                    bundle.putSerializable(RLConstants.CardData, cardData)
                    (context as RLMainActivityRL).RLbottombarcolorwhite()
                    (context as RLMainActivityRL).RLloadFrag(RLFragSessionSummary().newInstance(bundle), TAG, true, null, false)

                }
            }else if(cardData.from_third_party_source > 10){
                //new design
                val bundle = Bundle()
                bundle.putSerializable(RLConstants.CardData, cardData)
                (context as RLMainActivityRL).RLbottombarcolorwhite()
                (context as RLMainActivityRL).RLloadFrag(RLFragTenChallengeSummary().newInstance(bundle), TAG, true, null, false)

            }
            else{
                RLshowAlertDialog()
            }

        }
    }
    private fun RLthirdPartyTenBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_calendar_today)
        layoutBinding.layTime.txtTime.setText(R.string.challengesfor)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.duration.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.GONE

        if (classType.toLowerCase().equals("challenge-effort")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targeteffort)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_heart)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType.toLowerCase().equals("challenge-steps")){

            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetsteps)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_steps_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }else if (classType!!.toLowerCase().equals("challenge-calories")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetcalories)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-distance")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetdistance)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-climbed")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targetclimbed)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_climb)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distance)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else if (classType!!.toLowerCase().equals("challenge-duration")){
            layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_goal)
            layoutBinding.layCalories.txtTime.setText(R.string.targettotalduration)
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.goal.toDouble()).toString())

            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_active_time_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.youachived)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()).toString())

        }

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_ranking)
        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE

        layoutBinding.laySteps.txtTime.setText(R.string.rank)
        layoutBinding.laySteps.txtTimeNumber.setText(cardData.hrm.toString()+ " of " +cardData.share_map.toString())
       layoutBinding.layBottom.visibility=View.VISIBLE
    }
    private fun RLthirdPartyTwoBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_steps_green)
        layoutBinding.layTime.txtTime.setText(R.string.step)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.steps.toDouble()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layCalories.txtTime.setText(R.string.calorie)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
        layoutBinding.laySteps.txtTime.setText(R.string.distancemiles)
        layoutBinding.laySteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        layoutBinding.laySteps.relativeCard.visibility=View.VISIBLE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE

        layoutBinding.layoutAward.visibility=View.GONE
        layoutBinding.layoutThumb.visibility=View.GONE
        layoutBinding.layoutComment.visibility=View.GONE
        layoutBinding.imgThreedot.visibility=View.GONE
        layoutBinding.blanckView1.visibility=View.VISIBLE

        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(context!!).load(cardData.imageLinkSmall).into(layoutBinding.imgNain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(context!!).load(cardData.map_image).into(layoutBinding.imgNain)
        }else{
            Glide.with(context!!).load(R.drawable.healthheart).into(layoutBinding.imgNain)
        }

    }
    private fun RLthirdPartyOneBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.revoolaeffort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(cardData.totalREV.roundToInt().toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        if (classType!!.toLowerCase().equals("ride")||classType!!.toLowerCase().equals("run")||classType!!.toLowerCase().equals("walk")){
            layoutBinding.laySteps.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.laySteps.txtTime.setText(R.string.distance)
            layoutBinding.laySteps.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance).toString())

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.activecalories)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }


        if (!cardData.imageLinkSmall.isNullOrEmpty()){
            Glide.with(context!!).load(cardData.imageLinkSmall).into(layoutBinding.imgNain)
        }else if (!cardData.map_image.isNullOrEmpty()){
            Glide.with(context!!).load(cardData.map_image).into(layoutBinding.imgNain)
        }else{
            Glide.with(context!!).load(RLConstants.img_feed_apple_fitness).into(layoutBinding.imgNain)
        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun RLotherClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(cardData.totalREV.roundToInt().toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        if (classType.toLowerCase().equals("ride")||classType!!.toLowerCase().equals("run")||classType!!.toLowerCase().equals("walk")){
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.ic_distance)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.distancemiles)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.distance.toDouble()))

        }else{
            layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
            layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
            layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()))

        }

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE
    }
    private fun RLbodyClassesBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding){
        layoutBinding.layTime.imgTime.setImageResource(R.drawable.fd_active_time_green)
        layoutBinding.layTime.txtTime.setText(R.string.time)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()).toString())
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_heart)
        layoutBinding.layCalories.txtTime.setText(R.string.effort)
        if (cardData.totalREV.roundToInt()>0){
            layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totalREV.toDouble()).toString())
        }else{
            layoutBinding.layCalories.txtTimeNumber.setText("0")
        }

        layoutBinding.layAssumedeffort.imgTime.setImageResource(R.drawable.fd_calories_green)
        layoutBinding.layAssumedeffort.txtTime.setText(R.string.calorie)
        layoutBinding.layAssumedeffort.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.burntCalories.toDouble()).toString())

        layoutBinding.laySteps.relativeCard.visibility=View.GONE
        layoutBinding.layAssumedeffort.relativeCard.visibility=View.VISIBLE


    }
    private fun RLmindClassBodySet(cardData: RLTextOverview, layoutBinding: RlLayoutFeedListBinding) {

        layoutBinding.layTime.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layTime.txtTime.setText(R.string.mindfulminutes)
        layoutBinding.layTime.txtTimeNumber.setText(RLTools.RLdaytimeget(cardData.totalTime.toInt()))
        layoutBinding.layTime.relativeCard.visibility=View.VISIBLE

        layoutBinding.layCalories.imgTime.setImageResource(R.drawable.ic_mind_read)
        layoutBinding.layCalories.txtTime.setText(R.string.relaxation)
        layoutBinding.layCalories.txtTimeNumber.setText(RLTools.RLformatCommas(cardData.totalRMS.toDouble()).toString())


        layoutBinding.layAssumedeffort.relativeCard.visibility=View.GONE
        layoutBinding.laySteps.relativeCard.visibility=View.GONE

    }
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
}
