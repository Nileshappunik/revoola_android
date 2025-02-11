package com.revoola.fragment.start.classes

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragClassesScheduleBinding
import com.revoola.fragment.start.RLFragStart
import com.revoola.model.RLFulllVideoModel
import com.google.gson.Gson
import com.revoola.commonobject.RLTools
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RLClassesSchedule : RLBaseFragment() {
    val TAG: String = RLClassesSchedule::class.java.simpleName
    lateinit var fragBinding: RlFragClassesScheduleBinding
    private var selectedCalendar = Calendar.getInstance()
    private val PERMISSIONS_REQUEST_WRITE_CALENDAR = 100
    var calenderEventDescription=""
    private val binding by lazy {
        RlFragClassesScheduleBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLClassesSchedule()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes_schedule, container) as RlFragClassesScheduleBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLClassesSchedule" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.txtSelectDatatime.setOnClickListener {
            RLShowDatePickerDialog()
        }

        val data=  requireArguments().getString("VIDEODATA","")
        val selectedFriend =  requireArguments().getString("Message","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.txtTitle.setText(VideoCardData.rideTitle)
        fragBinding.txtNamewith.setText(VideoCardData.instructor)
        fragBinding.txtMinutes.setText(VideoCardData.duration)
        calenderEventDescription="${VideoCardData.instructor}'s ${VideoCardData.duration}"
        Glide.with(requireContext()).load(VideoCardData.imageLinkSquareV2)
            // .placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(fragBinding.imgMainBanner)

        if (selectedFriend.isEmpty()){
            fragBinding.txtTotalFriend.visibility=View.GONE
        }else{
            fragBinding.txtTotalFriend.visibility=View.VISIBLE
            fragBinding.txtTotalFriend.setText("$selectedFriend FRIENDS WILL BE INVITED")
            fragBinding.txtVideoTitle.setText("CONFIRM AND ADD TO CALENDER?")
            fragBinding.txtSelectDatatime.setText("")
            fragBinding.btnScheduleclass.setText("CONFIRM")
        }

        fragBinding.btnScheduleclass.setOnClickListener {
           if (fragBinding.btnScheduleclass.text.equals("CONFIRM")){
               RLshowSDialog("Would you like to add to your calendar?")
           }else{
               RLCheckIfFuture(data,"")
           }
        }

       /* if (classtype.equals(RLConstants.MIND)){
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
        }*/

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
            com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.selected_schedule_date,"$selectdate  $formattedTime" )
            //fragBinding.txtSelectDatatime.text = "$selectdate  $selectedHour:$selectedMinute"
            fragBinding.txtSelectDatatime.text = "$selectdate  $formattedTime"
            val color = ContextCompat.getColor(requireContext(), R.color.AppMainColor)
            ViewCompat.setBackgroundTintList(fragBinding.btnScheduleclass, ColorStateList.valueOf(color))
        }, hour, minute, true)
        timePickerDialog.show()
    }
    private fun RLCheckIfFuture(data: String, audioVideoType: String) {
        val currentCalendar = Calendar.getInstance()
        val isFuture = selectedCalendar.timeInMillis > currentCalendar.timeInMillis
        if (isFuture){
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            bundle.putString("SELECTEDDATE",fragBinding.txtSelectDatatime.text.toString())
            //Future Time
            (context as RLMainActivityRL).RLloadFrag(RLClassesScheduleJoinSession().newInstance(bundle), TAG, false,null, false)
        }else{
            RLcommonToast("Please Select Future Time")
        }
    }

    private fun RLshowSDialog(message:String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_subscribe)
        sucDialog.setCancelable(true)
        val tvNo: TextView = sucDialog.findViewById(R.id.tvCancel)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvSubscribe)
        val tvMainMessage: TextView = sucDialog.findViewById(R.id.tvMainMessage)
        tvMainMessage.setText(message)
        tvNo.setText("No")
        tvYes.setText("Yes")
        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
            (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, false, null, false)
        })
        tvYes.setOnClickListener(View.OnClickListener {
            RLCheckCalendarPermission()
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

    private fun RLCheckCalendarPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                PERMISSIONS_REQUEST_WRITE_CALENDAR
            )
        } else {
            RLAddEventToCalendar()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_CALENDAR -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RLAddEventToCalendar()
                } else {
                    // Permission denied
                }
            }
        }
    }
    private fun RLAddEventToCalendar() {
        try {
            val dateString = com.revoola.utils.RLPrefManager.RLGetSomeStringValue(activity, com.revoola.utils.RLPrefManager.selected_schedule_date,"" )

            val calendar = RLParseDateString(dateString.toString())
            val startMillis = calendar.timeInMillis
            val endMillis = startMillis + 60 * 60 * 1000 // 1-hour event

            // Use Google Calendar ID if available
            val calendarID = RLGetWritableCalendarId() ?:RLGetPrimaryCalendarId() // Get the primary calendar ID
            val values = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, calendarID)
                put(CalendarContract.Events.TITLE, "Revoola Schedule Reminder")
                put(CalendarContract.Events.DESCRIPTION, "Your schedule $calenderEventDescription is scheduled today")
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }

            val uri = requireActivity().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            uri?.let {
                val eventId = it.lastPathSegment?.toLongOrNull()

                // Add reminder
                if (eventId != null) {
                    RLAddReminderToEvent(eventId)
                }
            }

        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"EXCEPTION DATE:- ${e.message}")
        }
    }
    private fun RLParseDateString(dateString: String): Calendar {
        val dateFormat = SimpleDateFormat("dd/M/yyyy hh:mm a", Locale.getDefault())
        val date = dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")
        return Calendar.getInstance().apply { time = date }
    }
    private fun RLAddReminderToEvent(eventId: Long) {
        val values = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, 10) // Reminder 10 minutes before
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        //Log.e(TAG, "Event added successfully with ID: $eventId")
        requireActivity().contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values)
        (context as RLMainActivityRL).RLloadFrag(RLFragStart(), TAG, false, null, false)
    }

    private fun RLGetPrimaryCalendarId(): Long {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        val cursor = requireActivity().contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                do {
                    val calendarId = it.getLong(idIndex)
                    val displayName = it.getString(nameIndex)
                   //RLTools.RlLogEPrint(TAG, "Calendar ID: $calendarId, Name: $displayName")
                    // You can choose the desired calendar based on the display name or just return the first one.
                    return calendarId
                } while (it.moveToNext())
            }
        }
        throw IllegalStateException("No calendar found.")
    }

    private fun RLGetWritableCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
        )

        val cursor = requireActivity().contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accessLevelIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL)

                do {
                    val calendarId = it.getLong(idIndex)
                    val calendarName = it.getString(nameIndex)
                    val accessLevel = it.getInt(accessLevelIndex)

                    // Check if the calendar is writable
                    if (accessLevel >= CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) {
                       //RLTools.RlLogEPrint(TAG, "Writable Calendar ID: $calendarId, Name: $calendarName")
                        return calendarId
                    }
                } while (it.moveToNext())
            }
        }
        // Return null if no writable calendar was found
        return null
    }


}