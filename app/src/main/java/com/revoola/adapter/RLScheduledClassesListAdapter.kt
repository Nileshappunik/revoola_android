package com.revoola.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlLayoutScheduledClassesListBinding
import com.revoola.fragment.more.RLFragSchdulClassesView
import com.revoola.fragment.more.schduleModel.ScheduleItem

class RLScheduledClassesListAdapter(val context: FragmentActivity?,var scheduledClassesList: List<ScheduleItem>) :
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

    fun updateList(newList: List<ScheduleItem>) {
        scheduledClassesList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
       return scheduledClassesList.size
    }

    inner class MyViewHolder(val layoutBinding: RlLayoutScheduledClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        fun bindData(position: Int, itemVIew: View) {
            val cardData:ScheduleItem = scheduledClassesList[position]
            val videoCardData = cardData.videoItem
            val scheduleCardData = cardData.schedule
            val organizerName = cardData.organizer
            val organizerImage = cardData.organizerImage

            layoutBinding.txtImageTitle.setText(videoCardData?.rideTitle)
            layoutBinding.txtWithName.setText(videoCardData?.instructor)
            layoutBinding.txtWatchtime.setText(videoCardData?.duration + " class")
            val date = RLTools.RLconvertTimestampToSchdualDAte(scheduleCardData?.dateOfChallenge?:0)
            layoutBinding.txtMisseddate.setText(date)
            layoutBinding.txtMissed.setText(cardData.schedule.statusLbl)
            layoutBinding.txtMissed.setTextColor(context!!.resources.getColor(RLTools.getColorForScheduleStatus(cardData.schedule.statusLbl)))


            Glide.with(context!!).load(videoCardData?.imageLinkrectangleV2).into(layoutBinding.imgBigFull)
            Glide.with(context!!).load(organizerImage).into(layoutBinding.imgUser)
            layoutBinding.txtUsernam.setText(organizerName)

            layoutBinding.txtWarmupMin.setText(safeString(videoCardData?.minwarmup)+" MIN")
            layoutBinding.txtWorkoutMin.setText(safeString(videoCardData?.mincooldown)+" MIN")
            layoutBinding.txtCooldownMin.setText(safeString(videoCardData?.mininstruction)+" MIN")

            layoutBinding.txtEasy.setText(videoCardData?.difficulty)
            if(videoCardData?.difficulty.equals("Beginner")){
                layoutBinding.imgEasy.setImageResource(R.drawable.ic_easy)
                layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppMainColor))
            }else if (videoCardData?.difficulty.equals("Advanced")){
                layoutBinding.imgEasy.setImageResource(R.drawable.ic_hard)
                layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppRedColor))
            }else{
                layoutBinding.imgEasy.setImageResource(R.drawable.ic_medium)
                layoutBinding.txtEasy.setTextColor(context.resources.getColor(R.color.AppOrangeColor))
            }

            itemVIew.setOnClickListener {
                itemVIew.setOnClickListener {
                    val bundle = Bundle().apply {
                        putParcelable("selectedSchedule",cardData)
                    }
                    (context as RLMainActivityRL).RLloadFrag(RLFragSchdulClassesView().newInstance(bundle), TAG, true, null, true)
                }
            }
        }
        private fun safeString(value:String?):String{
            return if (value.isNullOrEmpty()) return "" else value
        }
    }

}
