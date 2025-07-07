package com.revoola.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLTools
import com.revoola.commonobject.RLYourWayCalvulation
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlLayoutScheduledClassesListBinding
import com.revoola.fragment.more.RLFragSchdulClassesView
import com.revoola.fragment.more.ScheduleItem
import com.revoola.fragment.more.ScheduleMediaItem
import com.revoola.fragment.start.body.RLFragBodyClassesView
import com.revoola.fragment.start.mind.RLFragMindClassesView
import com.revoola.fragment.start.yourway.RLFragChooseYourSensor
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants

class RLScheduledClassesListAdapter(val context: FragmentActivity?,val scheduledClassesList: List<ScheduleMediaItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLScheduledClassesListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutScheduledClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_scheduled_classes_list , parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
       return scheduledClassesList.size
    }

    inner class MyViewHolder(val layoutBinding: RlLayoutScheduledClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData:ScheduleMediaItem = scheduledClassesList[position]

            when (cardData) {
                is ScheduleMediaItem.Combined -> {
                    val videoCardData = cardData.videoItem
                    val scheduleCardData = cardData.scheduleItem
                    val organizerName = cardData.organizer
                    val organizerImage = cardData.organizerImage

                    layoutBinding.txtImageTitle.setText(videoCardData.rideTitle)
                    layoutBinding.txtWithName.setText(videoCardData.instructor)
                    layoutBinding.txtWatchtime.setText(videoCardData.duration + " class")
                    val date = RLTools.RLconvertTimestampToSchdualDAte(scheduleCardData.schedule.get("dateOfChallenge").toString().toLong())
                    layoutBinding.txtMisseddate.setText(date)
                    Glide.with(context!!).load(videoCardData.imageLinkrectangleV2).into(layoutBinding.imgBigFull)
                    Glide.with(context!!).load(organizerImage).into(layoutBinding.imgUser)
                    layoutBinding.txtUsernam.setText(organizerName)

                    layoutBinding.txtWarmupMin.setText(safeString(videoCardData.minwarmup)+" MIN")
                    layoutBinding.txtWorkoutMin.setText(safeString(videoCardData.mincooldown)+" MIN")
                    layoutBinding.txtCooldownMin.setText(safeString(videoCardData.mininstruction)+" MIN")

                    layoutBinding.txtEasy.setText(videoCardData.difficulty)
                    if(videoCardData.difficulty.equals("Beginner")){
                        layoutBinding.imgEasy.setImageResource(R.drawable.ic_easy)
                        layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppMainColor))
                    }else if (videoCardData.difficulty.equals("Advanced")){
                        layoutBinding.imgEasy.setImageResource(R.drawable.ic_hard)
                        layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppRedColor))
                    }else{
                        layoutBinding.imgEasy.setImageResource(R.drawable.ic_medium)
                        layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppOrangeColor))
                    }

                    itemVIew.setOnClickListener {
                        var ride = false
                        val videoId = scheduleCardData.schedule.get("videoKey").toString()
                        val createdBy = scheduleCardData.schedule.get("createdBy").toString()
                        val dateOfChallenge = scheduleCardData.schedule.get("dateOfChallenge").toString()
                        if (videoCardData.classType.toLowerCase().equals("ride")) ride = true else ride = false
                        itemVIew.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("videoID",videoId)
                            bundle.putString("createdBy",createdBy)
                            bundle.putString("dateOfChallenge",dateOfChallenge)
                            (context as RLMainActivityRL).RLloadFrag(RLFragSchdulClassesView().newInstance(bundle), TAG, true, null, true)
                        }
                    }
                }
            }
        }

        private fun safeString(value:String?):String{
            return if (value.isNullOrEmpty()) return "" else value
        }
    }
}
