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
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlLayoutScheduledClassesListBinding
import com.revoola.fragment.more.ScheduleItem
import com.revoola.fragment.start.yourway.RLFragChooseYourSensor
import com.revoola.model.RLFulllVideoModel
import com.revoola.utils.RLConstants

class RLScheduledClassesListAdapter(val context: FragmentActivity?,val scheduledClassesList: List<ScheduleItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLYourGroupListAdapter"

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
            val cardData = scheduledClassesList[position]
            val schedule = cardData.schedule
            val videoId = cardData.schedule.get("videoKey").toString()
            val createdBy = cardData.schedule.get("createdBy").toString()
            RLDatabaseManagerRead().RLRevoolaVideosMindRead(videoId){ data, error ->
                if (data != null) {
                    val jsonObject = Gson().toJson(data)
                    RLTools.RlLogDPrint(TAG,"videoData: ${jsonObject}")
                    val videoCardData = Gson().fromJson(jsonObject, RLFulllVideoModel::class.java)
                    layoutBinding.txtImageTitle.setText(videoCardData.rideTitle)
                    layoutBinding.txtWithName.setText(videoCardData.instructor)
                    layoutBinding.txtWatchtime.setText(videoCardData.duration + " class")
                    val date = RLTools.RLconvertTimestampToSchdualDAte(schedule.get("dateOfChallenge").toString().toLong())
                    layoutBinding.txtMisseddate.setText(date)
                    Glide.with(context!!).load(videoCardData.imageLinkSquareV2).into(layoutBinding.imgBigFull)
                    RLDatabaseManagerRead().RlUserBasicDataRead(createdBy) { data, error ->
                        if (data!=null) {
                            val userData = RLTools.parseUserData(data)
                            if(userData!=null){
                                layoutBinding.txtUsernam.setText(userData.displayName)
                            }
                        }
                    }
                    layoutBinding.txtWarmupMin.setText(videoCardData.minwarmup+" MIN")
                    layoutBinding.txtWarmupMin.setText(videoCardData.mincooldown+" MIN")
                    layoutBinding.txtWarmupMin.setText(videoCardData.mininstruction+" MIN")

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
                }
            }
        }
    }
}
