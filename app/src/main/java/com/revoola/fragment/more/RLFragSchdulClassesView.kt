package com.revoola.fragment.more

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.model.RLFulllVideoModel
import com.google.gson.Gson
import com.revoola.activity.RLMainActivityRL
import com.revoola.ble.RLExtraValueKey
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragSchduleClassesViewBinding
import com.revoola.fragment.more.schduleModel.ScheduleItem
import com.revoola.fragment.start.yourway.RLFragChooseYourSensor
import com.revoola.utils.RLPrefManager

class RLFragSchdulClassesView : RLBaseFragment() {
    val TAG: String = RLFragSchdulClassesView::class.java.simpleName

    private val fragBinding by lazy {
        RlFragSchduleClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSchdulClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSchdulClassesView" )
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
        val cardData:ScheduleItem = requireArguments().getParcelable<ScheduleItem>("selectedSchedule") as ScheduleItem
        fragBinding.txtUsernam.setText(cardData.organizer)
        Glide.with(requireContext()).load(cardData.organizerImage).into(fragBinding.imgUser)
        val date = RLTools.rl_convertTimestampToSchdualDAte(cardData.schedule.dateOfChallenge.toString().toLong())
        fragBinding.txtMisseddate.setText(date)
        fragBinding.txtMissed.setText(cardData.schedule.statusLbl)
        if (isAdded) fragBinding.txtMissed.setTextColor(requireContext().resources.getColor(RLTools.getColorForScheduleStatus(cardData.schedule.statusLbl)))

        rl_mindUiSetup(cardData.videoItem)
        val safeDateOfChallenge = safeString(cardData.schedule.dateOfChallenge.toString())
        RLTools.rl_logEPrint(TAG,"safeDateOfChallenge: $safeDateOfChallenge")
        if (isWithinLast10Minutes(safeDateOfChallenge)) {
            startReverseTimer(safeDateOfChallenge)
        }
        fragBinding.joinButton.setOnClickListener {
            //Join Button Click Set Here
            if (isWithinLast10Minutes(safeDateOfChallenge)) {
                val gson = Gson()
                val jsonObject = gson.toJson(cardData.videoItem)
                RLTools.rl_logLarge(TAG,"jsonObject: $jsonObject")
                if (cardData.schedule.isMindClass){
                    val bundle = Bundle()
                    bundle.putString(RLExtraValueKey.yourWayType,"all")
                    bundle.putBoolean(RLExtraValueKey.isBody,false)
                    bundle.putBoolean(RLExtraValueKey.isMind,true)
                    bundle.putBoolean(RLExtraValueKey.isYourWay,false)

                    bundle.putString(RLExtraValueKey.videoId,cardData.schedule.videoKey)
                    bundle.putString(RLExtraValueKey.videoData,jsonObject)
                    bundle.putString(RLExtraValueKey.audioVideoType,"video")

                    (context as RLMainActivityRL).rl_loadFrag(RLFragChooseYourSensor().newInstance(bundle), TAG, true, null, false)
                }else{
                    val bundle: Bundle = Bundle()
                    val ride = if (cardData.schedule.typeOfWorkout.toLowerCase().equals("ride")) true else false
                    bundle.putString(RLExtraValueKey.yourWayType,cardData.schedule.typeOfWorkout)
                    bundle.putBoolean(RLExtraValueKey.isBody,true)
                    bundle.putBoolean(RLExtraValueKey.isMind,false)
                    bundle.putBoolean(RLExtraValueKey.isYourWay,false)

                    bundle.putString(RLExtraValueKey.videoData,jsonObject)
                    bundle.putString(RLExtraValueKey.videoId,cardData.schedule.videoKey)
                    bundle.putBoolean(RLExtraValueKey.isRide,ride)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragChooseYourSensor().newInstance(bundle), TAG, true, null, false)
                }
            } else {
                if (isAdded)  showAlertTenMins(requireContext())
            }
        }
    }
    private fun rl_mindUiSetup(VideoData: RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtMinutes.setText(VideoData.duration+" CLASS")
        Glide.with(requireContext()).load(VideoData.imageLinkInstructor).into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkrectangleV2).into(fragBinding.imgMainBanner)

        fragBinding.txtWarmupMin.setText(safeString(VideoData.minwarmup)+" MIN")
        fragBinding.txtWorkoutMin.setText(safeString(VideoData.mincooldown)+" MIN")
        fragBinding.txtCooldownMin.setText(safeString(VideoData.mininstruction)+" MIN")

        fragBinding.txtEasy.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.imgEasy.setImageResource(R.drawable.ic_easy)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.imgEasy.setImageResource(R.drawable.ic_hard)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.imgEasy.setImageResource(R.drawable.ic_medium)
            fragBinding.txtEasy.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
        fragBinding.joinButton.visibility=View.VISIBLE

    }
    private fun safeString(value:String?):String{
        return if (value.isNullOrEmpty()) return "" else value
    }
    override fun onPause() {
        super.onPause()
        rl_bottomHideShowSet(true)
    }

    private fun isWithinLast10Minutes(dateOfChallenge: String): Boolean {
        return try {
            val challengeTimeMillis = dateOfChallenge.toLong() * 1000 // Convert seconds to milliseconds
            val currentTime = System.currentTimeMillis()
            RLTools.rl_logEPrint(TAG, "currentTime: $currentTime")
            RLTools.rl_logEPrint(TAG, "challengeTime: $challengeTimeMillis")

            val tenMinutesMillis = 10 * 60 * 1000 // 10 minutes in milliseconds
            val timeDifference = challengeTimeMillis - currentTime // Time remaining until challenge

            RLTools.rl_logEPrint(TAG, "Time remaining: ${timeDifference / 1000} seconds")
            RLTools.rl_logEPrint(TAG, "Is within 10 minutes: ${timeDifference <= tenMinutesMillis && timeDifference > 0}")

            // Check if challenge time is within next 10 minutes (10 minutes or less remaining)
            timeDifference <= tenMinutesMillis && timeDifference > 0
        } catch (e: Exception) {
            RLTools.rl_logEPrint(TAG, "Exception: ${e.localizedMessage}")
            false // Return false if parsing fails
        }
    }

    private fun showAlertTenMins(context: Context) {
        val sucDialog:Dialog = Dialog(context)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.iv_ok)
        val iv_title: TextView = sucDialog.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog.findViewById(R.id.iv_description)
        val view_v: View = sucDialog.findViewById(R.id.view_v)

        iv_title.visibility=View.GONE
        view_v.visibility=View.VISIBLE
        iv_description.setText("You can join only before 10 min of schedule time")
        iv_ok.setText("OK")
        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    private fun startReverseTimer(dateOfChallenge: String) {
        val challengeTimeMillis = dateOfChallenge.toLong() * 1000
        // Start countdown timer
        val timer =
            object : CountDownTimer(challengeTimeMillis - System.currentTimeMillis(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val minutes = millisUntilFinished / (60 * 1000)
                    val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                    fragBinding.txtMisseddate.text = String.format("%02d:%02d", minutes, seconds)
                    fragBinding.txtMissed.setText("Start in")
                    fragBinding.imgCalender.setImageResource(R.drawable.fd_active_time_green)
                    if (isAdded)  fragBinding.txtMissed.setTextColor(requireContext().resources.getColor(R.color.AppMainColor))
                }

                override fun onFinish() {
                    fragBinding.joinButton.visibility = View.GONE
                }
            }
        timer.start()
    }

}