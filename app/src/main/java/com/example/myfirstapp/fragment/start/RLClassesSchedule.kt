package com.example.myfirstapp.fragment.start

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragClassesScheduleBinding
import com.example.myfirstapp.fragment.start.adapter.RLStartListAdapter
import com.example.myfirstapp.databinding.RlFragStartBinding
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RLClassesSchedule : RLBaseFragment() {
    val TAG: String = RLClassesSchedule::class.java.simpleName
    lateinit var fragBinding: RlFragClassesScheduleBinding
    private var selectedCalendar = Calendar.getInstance()
    private val binding by lazy {
        RlFragClassesScheduleBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLClassesSchedule()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes_schedule, container) as RlFragClassesScheduleBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLClassesSchedule" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        fragBinding.txtSelectDatatime.setOnClickListener {
            RLShowDatePickerDialog()
        }
        fragBinding.btnScheduleclass.setOnClickListener {
            RLCheckIfFuture()
        }
        val data=  requireArguments().getString("VIDEODATA","")
        val classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.txtTitle.setText(VideoCardData.rideTitle)
        fragBinding.txtNamewith.setText(VideoCardData.instructor)
        fragBinding.txtMinutes.setText(VideoCardData.duration)
        if (classtype.equals(RLConstants.MIND)){
            val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
            fragBinding.txtVideo.setText(audioVideoType)
            if (audioVideoType.equals("Video")){
                fragBinding.imgVideo.setImageResource(R.drawable.ic_video)
            }else{
                fragBinding.imgVideo.setImageResource(R.drawable.ic_audio)
            }
        }else{
            fragBinding.txtVideo.setText(VideoCardData.difficulty)
            if(VideoCardData.difficulty.equals("Beginner")){
                fragBinding.imgVideo.setImageResource(R.drawable.ic_easy)
                fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
            }else if (VideoCardData.difficulty.equals("Advanced")){
                fragBinding.imgVideo.setImageResource(R.drawable.ic_hard)
                fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
            }else{
                fragBinding.imgVideo.setImageResource(R.drawable.ic_medium)
                fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
            }
        }
    }
    private fun RLShowDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
            val selectdate="$selectedDay/${selectedMonth + 1}/$selectedYear"
            RLShowTimePickerDialog(selectdate)
        }, year, month, day)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }
    private fun RLShowTimePickerDialog(selectdate:String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedCalendar.set(Calendar.MINUTE, selectedMinute)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = timeFormat.format(selectedCalendar.time)
            //fragBinding.txtSelectDatatime.text = "$selectdate  $selectedHour:$selectedMinute"
            fragBinding.txtSelectDatatime.text = "$selectdate  $formattedTime"
            val color = ContextCompat.getColor(requireContext(), R.color.AppMainColor)
            ViewCompat.setBackgroundTintList(fragBinding.btnScheduleclass, ColorStateList.valueOf(color))
        }, hour, minute, true)
        timePickerDialog.show()
    }
    private fun RLCheckIfFuture() {
        val currentCalendar = Calendar.getInstance()
        val isFuture = selectedCalendar.timeInMillis > currentCalendar.timeInMillis
        if (isFuture){
            //Future Time
            (context as RLMainActivityRL).RLloadFrag(RLClassesScheduleJoinSession(), TAG, true,null, false)
        }else{
            RLcommonToast("Please Select Future Time")
        }
    }
}