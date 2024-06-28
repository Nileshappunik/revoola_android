package com.example.myfirstapp.fragment.start.challenges

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlDialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.RlFragSetYourGoalBinding

import com.example.myfirstapp.utils.RLPrefManager
import java.util.Calendar


class RLFragSetYourGoal : RLBaseFragment(),DatePickerDialog.OnDateSetListener {
    val TAG: String = RLFragSetYourGoal::class.java.simpleName
    lateinit var fragBinding: RlFragSetYourGoalBinding
    var datetype:String="START"
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var myDay: Int = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var Month: String = ""
    var Day: String = ""
    var DateTime: String = ""
    var startdateselect:Boolean=false


    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragSetYourGoal()
        fragment.arguments = bundle
        return fragment
    }

    private val binding by lazy {
        RlFragSetYourGoalBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_set_your_goal, container) as RlFragSetYourGoalBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSetYourGoal" )

        fragBinding.toolbar.tvTitle.visibility=View.GONE
        fragBinding.toolbar.ivlogoapp.visibility=View.VISIBLE
        fragBinding.toolbar.ivlogoapp.setImageResource(R.drawable.ic_challenge_flag)

        fragBinding.toolbar.ivNotification.visibility=View.VISIBLE
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_circle)
        fragBinding.toolbar.ivNotification.setOnClickListener {
            RLshowHelpDialog()
        }
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        val challengeType = requireArguments().getString("ChallengeType").toString().trim()
        if (challengeType.equals("Steps")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_steps_green)
            fragBinding.txtHeader.setText(R.string.stepsmall)
        }else if (challengeType.equals("Effort")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_heart_blanck)
            fragBinding.txtHeader.setText(R.string.revoolaeffortscore)
        }else if (challengeType.equals("Calories")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)
            fragBinding.txtHeader.setText(R.string.caloriessmallkcal)
        }else if (challengeType.equals("Distance")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_distance)
            fragBinding.txtHeader.setText(R.string.distancesmallkm)
        }else if (challengeType.equals("Climbed")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.ic_climb)
            fragBinding.txtHeader.setText(R.string.climbedm)
        }else if (challengeType.equals("Duration")){
            fragBinding.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)
            fragBinding.txtHeader.setText(R.string.durationh)
        }

        fragBinding.txtEnterStart.setOnClickListener {
            datetype="START"
            RLdialogStartDatePicker()
        }

        fragBinding.txtEnterEnd.setOnClickListener {
            if (startdateselect){
                datetype ="END"
                RLdialogEndDatePicker()
            }
        }
    }

    fun RLshowHelpDialog() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpSetyourgoalBinding=RlDialogHelpSetyourgoalBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)


        val window: Window = dialog.getWindow()!!
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity=Gravity.RIGHT or Gravity.TOP
        window.attributes = lp



        dialogMainBinding.laySartdate.txtHeader.setText(R.string.pleaseenterstartdate)
        dialogMainBinding.laySartdate.txtHeaderDescription.setText(R.string.selecttosetthedatyouwantstart)
        dialogMainBinding.laySartdate.imgHelpChallenges.setImageResource(R.drawable.ic_calendar_today)

        dialogMainBinding.layEnddate.txtHeader.setText(R.string.pleaseenterenddate)
        dialogMainBinding.layEnddate.txtHeaderDescription.setText(R.string.selecttosetthedayuoyend)
        dialogMainBinding.layEnddate.imgHelpChallenges.setImageResource(R.drawable.ic_calendar_today)

        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }

    //DATE SELECT
    private fun RLdialogStartDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(requireActivity(),this, day, month, year)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        myYear = year
        myMonth = month + 1
        Log.d("myMonth", "" + myMonth)
        myDay = day
        Log.d("myDay", "" + myDay)

        Month = if (myMonth < 10) {
            "0$myMonth"
        } else {
            myMonth.toString()
        }
        Day = if (myDay < 10) {
            "0$myDay"
        } else {
            myDay.toString()
        }
        DateTime = "" + Day + "-" + Month + "-" + myYear

        if (datetype.equals("END")){
            fragBinding.txtEnterEnd.setText(DateTime).toString()
            fragBinding.txtEnterEnd.setTextColor(resources.getColor(R.color.AppBlackColor))
        }else{
            fragBinding.txtEnterStart.setText(DateTime).toString()
            fragBinding.txtEnterStart.setTextColor(resources.getColor(R.color.AppBlackColor))
            startdateselect=true
        }

    }

    private fun RLdialogEndDatePicker() {
        val desiredCalendar = Calendar.getInstance()
        desiredCalendar.set(Calendar.YEAR, myYear) // Set the year
        desiredCalendar.set(Calendar.MONTH, myMonth-1) // Set the month (zero-based index, so May is 4)
        desiredCalendar.set(Calendar.DAY_OF_MONTH, myDay) // Set the day
        val datePickerDialog = DatePickerDialog(requireActivity(),this, day, month, year)
        datePickerDialog.datePicker.minDate = desiredCalendar.timeInMillis
        datePickerDialog.show()
    }

}