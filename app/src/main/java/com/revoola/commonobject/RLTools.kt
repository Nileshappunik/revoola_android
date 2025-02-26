package com.revoola.commonobject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.revoola.R
import com.revoola.enumclass.RLYourWayName
import com.revoola.model.RLTextOverview
import com.revoola.model.RlMetric

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.roundToInt

import android.content.Context
import android.net.NetworkCapabilities
import com.revoola.model.EffortZoneFeedModel

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.revoola.utils.RLConstants
import java.time.temporal.TemporalAdjusters


object RLTools {

    fun View.RLadjustWidthToHeight() {
        this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = this@RLadjustWidthToHeight.height
                if (height > 0) {
                    this@RLadjustWidthToHeight.layoutParams.width = height
                    this@RLadjustWidthToHeight.requestLayout()
                    this@RLadjustWidthToHeight.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    fun RlLogDPrint(tag_log:String,message_log:String){
       Log.d(tag_log,message_log)
    }

    fun RlLogEPrint(tag_log:String,message_log:String){
       Log.e(tag_log,message_log)
    }

    fun RLChallengeIcon(challengeType:String):Int{
          when (challengeType.toLowerCase()){
            "steps"-> return R.drawable.fd_steps_green
            "effort"-> return R.drawable.ic_heart
            "calories"-> return R.drawable.fd_calories_green
            "distance"-> return R.drawable.ic_distance
            "climbed"-> return R.drawable.ic_climb
            "duration"-> return R.drawable.fd_active_time_green
            else -> return R.drawable.fd_steps_green
        }
    }
    fun RLChallengeTargetIcon(TargetType:String):Int{
          when (TargetType.toLowerCase()){
            "individualtarget"-> return R.drawable.ic_goal
            "sharedtarget"-> return R.drawable.goal_shared
            else -> return R.drawable.ic_goal
        }
    }
    fun RLChallengeTargetName(TargetType:String):String{
        when (TargetType.toLowerCase()){
            "individualtarget"-> return "individual"
            "sharedtarget"-> return "shared"
            else -> return "individual"
        }
    }

    fun RLChallengeForIcon(challengeForType:String):Int{
        when (challengeForType.toLowerCase()){
            "you"-> return R.drawable.ic_you
            "friends"-> return R.drawable.fr_friends_green
            "group"-> return R.drawable.ic_groups
            "groupvgroup"-> return R.drawable.ic_group_v_group
            else -> return R.drawable.ic_you
        }
    }
    fun RLCalendetIcon(calenderType:String):Int{
        when (calenderType.toLowerCase()){
            "daily"-> return R.drawable.calendar_daily
            "weekly"-> return R.drawable.calendar_weekly
            "monthly"-> return R.drawable.calendar_monthly
            "custom"-> return R.drawable.calendar_custom
            else -> return R.drawable.calendar_monthly
        }
    }


    fun RLMonthNameTogetFirstDate(inputDate: String): String {
        return try {
            // Define the input date format
            val inputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)

            // Define the output date format
            val outputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)

            // Parse the input date string to a Date object
            val date: Date = inputFormat.parse(inputDate)

            // Format the Date object to the desired output format
            outputFormat.format(date)
        } catch (e: Exception) {
            // If parsing fails, return the original string
            inputDate
        }
    }

    fun RLStringDateToMonthYearFormate(dateString:String,isYear:Boolean):String {
        // Define the input date format
        val inputFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        if (isYear){
            // Define the output format
            val outputFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)
            // Parse the input date string to a Date object
            val date: Date? = inputFormat.parse(dateString)

            // Format the Date object to the required format
            val formattedDate = date?.let { outputFormat.format(it) }

            return formattedDate.toString()
        }else{
            // Define the output format
            val outputFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
            // Parse the input date string to a Date object
            val date: Date? = inputFormat.parse(dateString)

            // Format the Date object to the required format
            val formattedDate = date?.let { outputFormat.format(it) }

            return formattedDate.toString()
        }

    }

    fun RLStringDateToDateFormate(dateString:String):Date? {

        // Define the input date format
        val inputFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH)
        } else {
            TODO("VERSION.SDK_INT < N")
        }

        // Parse the date string to Date object
        val date: Date? = inputFormat.parse(dateString)

        return date
    }

    fun RLMonthNameTogetLastDate(dateString: String): String {
        // Define the input format
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("MMM, yyyy").withResolverStyle(java.time.format.ResolverStyle.STRICT).withLocale(java.util.Locale.ENGLISH)
        } else {
            TODO("VERSION.SDK_INT < O not supported")
        }

        // Parse the input string to a LocalDate
        val parsedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val normalizedDate = dateString.lowercase().replaceFirstChar { it.uppercase() }
            // Adjust the date format and handle the comma
            LocalDate.parse("$normalizedDate 01", DateTimeFormatter.ofPattern("MMM, yyyy dd").withLocale(java.util.Locale.ENGLISH))
        } else {
            TODO("VERSION.SDK_INT < O not supported")
        }

        // Get the last day of the month
        val lastDateOfMonth = parsedDate.with(TemporalAdjusters.lastDayOfMonth())

        // Convert LocalDate to legacy Date for formatting
        val legacyDate = Date.from(lastDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // Format the output in the desired format
        val outputFormatter = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.ENGLISH)
        return outputFormatter.format(legacyDate)
    }


    fun RLMonthNameTogetLastDateOld(dateString: String): String {
        // Define the input format
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("MMM, yyyy").withResolverStyle(java.time.format.ResolverStyle.STRICT).withLocale(java.util.Locale.ENGLISH)
        } else {
            TODO("VERSION.SDK_INT < O not supported")
        }

        // Parse the input string to a LocalDate
        val parsedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val normalizedDate = dateString.lowercase().replaceFirstChar { it.uppercase() }
            LocalDate.parse("$normalizedDate 01", DateTimeFormatter.ofPattern("MMM yyyy dd").withLocale(java.util.Locale.ENGLISH))
        } else {
            TODO("VERSION.SDK_INT < O not supported")
        }

        // Get the last day of the month
        val lastDateOfMonth = parsedDate.with(TemporalAdjusters.lastDayOfMonth())

        // Convert LocalDate to legacy Date for formatting
        val legacyDate = Date.from(lastDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // Format the output in the desired format
        val outputFormatter = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.ENGLISH)
        return outputFormatter.format(legacyDate)
    }

    fun RlconvertDateToTimestamp(dateString: String): String {
        val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone("GMT") // Ensure consistent parsing

        val date = dateFormat.parse(dateString) // Parse the date
        return (date?.time?.div(1000)).toString() // Convert to seconds (Unix timestamp)
    }

    fun RLConvertDate(inputDate: String): String {
        return try {
            // Define the input date format
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)

            // Define the output date format
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

            // Parse the input date string to a Date object
            val date: Date = inputFormat.parse(inputDate)

            // Format the Date object to the desired output format
            outputFormat.format(date)
        } catch (e: Exception) {
            // If parsing fails, return the original string
            inputDate
        }
    }

     fun RLshowAlertDialog(context: Context,activity: Activity) {
        val sucDialog: Dialog = Dialog(context)
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_dialog_subscribe)
        sucDialog.setCancelable(false)
        val iv_ok: TextView = sucDialog.findViewById(R.id.tvSubscribe)
        val iv_Cancle: TextView = sucDialog.findViewById(R.id.tvCancel)
        val tvMainMessage: TextView = sucDialog.findViewById(R.id.tvMainMessage)

        tvMainMessage.setText("After clicking OK, the app will close.")
        iv_ok.setText("OK")
        iv_Cancle.setText("CANCEL")

        iv_ok.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
           activity.finish()
        })
        iv_Cancle.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
    }

     fun ScxhasNotificationPermission(context: Context): Boolean {
         return  ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun RLCalculateCircularGraph(rev: Int): String {
        when {
            rev <= 30 -> {
                return "#F177A0"
            }
            rev <= 50 -> {
                return "#FFCF2F"
            }
            rev <= 60 -> {
                return "#2CAE2C"
            }
            rev <= 70 -> {
                return "#0099DA"
            }
            rev <= 80 -> {
                return "#FE6902"
            }
            rev <= 90 -> {
                return "#9900CC"
            }
            rev <= 100 -> {
                return "#ED4541"
            }
            else -> {
                return "#000000"
            }
        }
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun RLCalculateAge(dateString: String): Int {
        // Define the date format (day/month/year)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the string into a Date object
        val birthDate: Date = dateFormat.parse(dateString) ?: return 0

        // Get the current date
        val currentDate = Calendar.getInstance()

        // Create a Calendar object for the birth date
        val birthCalendar = Calendar.getInstance()
        birthCalendar.time = birthDate

        // Calculate age
        var age = currentDate.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        // Check if the birthday hasn't occurred yet this year
        if (currentDate.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun RLsetSystemBarColor(act: Activity, @ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = act.resources.getColor(color)
        }
    }

    fun RLheightsetimageview(testImage:ImageView) {


        // Ensure the layout has been completed before getting the width
        testImage.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent multiple calls
                testImage.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the width of the ImageView
                val width = testImage.width

                // Calculate height as 75% of width
                val height = (width * 0.76).toInt()

                // Set the calculated height to the ImageView
                val layoutParams = testImage.layoutParams
                layoutParams.height = height
                testImage.layoutParams = layoutParams
            }
        })
        val layoutParamsImage: ViewGroup.LayoutParams = testImage.layoutParams
        val width = testImage.width
        val height = (width * 0.76).toInt()
        layoutParamsImage.height = height
        testImage.layoutParams =layoutParamsImage
    }

    fun RLheightsetRelative(testImage:RelativeLayout) {

        // Ensure the layout has been completed before getting the width
        testImage.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent multiple calls
                testImage.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the width of the ImageView
                val width = testImage.width

                // Calculate height as 75% of width
                val height = (width * 0.76).toInt()

                // Set the calculated height to the ImageView
                val layoutParams = testImage.layoutParams
                layoutParams.height = height
                testImage.layoutParams = layoutParams
            }
        })
        val layoutParamsImage: ViewGroup.LayoutParams = testImage.layoutParams
        val width = testImage.width
        val height = (width * 0.76).toInt()
        layoutParamsImage.height = height
        testImage.layoutParams =layoutParamsImage
    }

    fun RLChallengesTypeGet(typename:String): String {
        var ChallengeName=""
        when(typename.toLowerCase()){
            "challenge-effort"->{
                ChallengeName="effort"
            }
            "challenge-custom-effort"->{
                ChallengeName="effort"
            }
            "challenge-daily-effort"->{
                ChallengeName="effort"
            }
            "challenge-weekly-effort"->{
                ChallengeName="effort"
            }
            "challenge-monthly-effort"->{
                ChallengeName="effort"
            }

            "challenge-steps"->{
                ChallengeName="steps"
            }
            "challenge-custom-steps"->{
                ChallengeName="steps"
            }
            "challenge-daily-steps"->{
                ChallengeName="steps"
            }
            "challenge-weekly-steps"->{
                ChallengeName="steps"
            }
            "challenge-monthly-steps"->{
                ChallengeName="steps"
            }

            "challenge-calories"->{
                ChallengeName="calories"
            }
            "challenge-custom-calories"->{
                ChallengeName="calories"
            }
            "challenge-daily-calories"->{
                ChallengeName="calories"
            }
            "challenge-weekly-calories"->{
                ChallengeName="calories"
            }
            "challenge-monthly-calories"->{
                ChallengeName="calories"
            }

            "challenge-distance"->{
                ChallengeName="distance"
            }
            "challenge-custom-distance"->{
                ChallengeName="distance"
            }
            "challenge-daily-distance"->{
                ChallengeName="distance"
            }
            "challenge-weekly-distance"->{
                ChallengeName="distance"
            }
            "challenge-monthly-distance"->{
                ChallengeName="distance"
            }

            "challenge-climbed"->{
                ChallengeName="climbed"
            }
            "challenge-custom-climbed"->{
                ChallengeName="climbed"
            }
            "challenge-daily-climbed"->{
                ChallengeName="climbed"
            }
            "challenge-weekly-climbed"->{
                ChallengeName="climbed"
            }
            "challenge-monthly-climbed"->{
                ChallengeName="climbed"
            }

            "challenge-duration"->{
                ChallengeName="duration"
            }
            "challenge-custom-duration"->{
                ChallengeName="duration"
            }
            "challenge-daily-duration"->{
                ChallengeName="duration"
            }
            "challenge-weekly-duration"->{
                ChallengeName="duration"
            }
            "challenge-monthly-duration"->{
                ChallengeName="duration"
            }
        }
        return ChallengeName
    }

    fun RLgeticon(typename:String): Int {
       if( typename.toLowerCase().equals("walk")){
           return R.drawable.ic_walk
       }else if( typename.toLowerCase().equals("calories")){
           return R.drawable.fd_calories_green
       }else if( typename.toLowerCase().equals("steps")){
           return R.drawable.fd_steps_green
       }else if( typename.toLowerCase().equals("dance")){
           return R.drawable.ic_dance
       }else if( typename.toLowerCase().equals("run")){
           return R.drawable.ic_run
       }else if (typename.toLowerCase().equals("challenge-effort")){
           return R.drawable.ic_heart
       }else if (typename.toLowerCase().equals("challenge-custom-effort")){
           return R.drawable.ic_heart
       }else if (typename.toLowerCase().equals("challenge-daily-effort")){
           return R.drawable.ic_heart
       }else if (typename.toLowerCase().equals("challenge-weekly-effort")){
           return R.drawable.ic_heart
       }else if (typename.toLowerCase().equals("challenge-monthly-effort")){
           return R.drawable.ic_heart
       }else if (typename.toLowerCase().equals("challenge-steps")){
           return R.drawable.fd_steps_green
       }else if (typename.toLowerCase().equals("challenge-custom-steps")){
           return R.drawable.fd_steps_green
       }else if (typename.toLowerCase().equals("challenge-daily-steps")){
           return R.drawable.fd_steps_green
       }else if (typename.toLowerCase().equals("challenge-weekly-steps")){
           return R.drawable.fd_steps_green
       }else if (typename.toLowerCase().equals("challenge-monthly-steps")){
           return R.drawable.fd_steps_green
       }else if (typename.toLowerCase().equals("challenge-calories")){
           return R.drawable.fd_calories_green
       }else if (typename.toLowerCase().equals("challenge-custom-calories")){
           return R.drawable.fd_calories_green
       }else if (typename.toLowerCase().equals("challenge-daily-calories")){
           return R.drawable.fd_calories_green
       }else if (typename.toLowerCase().equals("challenge-weekly-calories")){
           return R.drawable.fd_calories_green
       }else if (typename.toLowerCase().equals("challenge-monthly-calories")){
           return R.drawable.fd_calories_green
       }else if (typename.toLowerCase().equals("challenge-distance")){
           return R.drawable.ic_distance
       }else if (typename.toLowerCase().equals("challenge-custom-distance")){
           return R.drawable.ic_distance
       }else if (typename.toLowerCase().equals("challenge-daily-distance")){
           return R.drawable.ic_distance
       }else if (typename.toLowerCase().equals("challenge-weekly-distance")){
           return R.drawable.ic_distance
       }else if (typename.toLowerCase().equals("challenge-monthly-distance")){
           return R.drawable.ic_distance
       }else if (typename.toLowerCase().equals("challenge-climbed")){
           return R.drawable.ic_climb
       }else if (typename.toLowerCase().equals("challenge-custom-climbed")){
           return R.drawable.ic_climb
       }else if (typename.toLowerCase().equals("challenge-daily-climbed")){
           return R.drawable.ic_climb
       }else if (typename.toLowerCase().equals("challenge-weekly-climbed")){
           return R.drawable.ic_climb
       }else if (typename.toLowerCase().equals("challenge-monthly-climbed")){
           return R.drawable.ic_climb
       }else if (typename.toLowerCase().equals("challenge-duration")){
           return R.drawable.fd_active_time_green
       }else if (typename.toLowerCase().equals("challenge-custom-duration")){
           return R.drawable.fd_active_time_green
       }else if (typename.toLowerCase().equals("challenge-daily-duration")){
           return R.drawable.fd_active_time_green
       }else if (typename.toLowerCase().equals("challenge-weekly-duration")){
           return R.drawable.fd_active_time_green
       }else if (typename.toLowerCase().equals("challenge-monthly-duration")){
           return R.drawable.fd_active_time_green
       }

       else if( typename.toLowerCase().equals("yoga")){
           return R.drawable.ic_yoga
       }else if( typename.toLowerCase().equals("pilates")){
           return R.drawable.ic_pilates
       }else if( typename.toLowerCase().equals("ride")){
           return R.drawable.fd_ride_green
       }else if( typename.toLowerCase().equals("energise")){
           return R.drawable.ic_power
       }else if( typename.toLowerCase().equals("workout")){
           return R.drawable.ic_workout
       }else{
           return R.drawable.ic_dance
       }
    }

    fun RLgetImage1(typename:String): Int {
        if (typename.isNullOrEmpty()){
            return R.drawable.walk
        }
        if( typename.toLowerCase().equals("walk")){
            return R.drawable.walk
        }else if( typename.toLowerCase().equals("run")){
            return R.drawable.run
        }else if( typename.toLowerCase().equals("yoga")){
            return R.drawable.yoga
        }else if( typename.toLowerCase().equals("pilates")){
            return R.drawable.pilates
        }else if( typename.toLowerCase().equals("ride")){
            return R.drawable.ride
        }else if( typename.toLowerCase().equals("workout")){
            return R.drawable.workout
        }else if (typename.toLowerCase().equals("challenge-effort")){
            return R.drawable.challenges_start
        }else if (typename.toLowerCase().equals("challenge-steps")){
            return R.drawable.challenges_start
        }else if (typename.toLowerCase().equals("challenge-calories")){
            return R.drawable.challenges_start
        }else if (typename.toLowerCase().equals("challenge-distance")){
            return R.drawable.challenges_start
        }else if (typename.toLowerCase().equals("challenge-climbed")){
            return R.drawable.challenges_start
        }else if (typename.toLowerCase().equals("challenge-duration")){
            return R.drawable.challenges_start
        }else{
            return R.drawable.walk
        }
    }

    fun RLFeedSetImage(datas: RLTextOverview, currentUserId:String, selectTag:String): String {
        var returnValue = ""
        if (datas != null) {
            when (datas.from_third_party_source) {
                1 -> {
                    val key = datas.className?.toLowerCase() ?: ""
                    returnValue = when {
                        key.contains("fitbit") -> "https://video.revoola.com/v2/images/v3_app_fitbit.png"
                        key.contains("strava") -> "https://video.revoola.com/v2/images/v3_app_strava.png"
                        key.contains("garmin") || key.contains("connect") -> "https://video.revoola.com/v2/images/v3_app_connect.png"
                        key.contains("oura") -> "https://video.revoola.com/v2/images/v3_app_oura.png"
                        key.contains("health") || key.contains("watch") -> "https://video.revoola.com/v2/images/v3_app_apple.png"
                        key.contains("whoop") -> "https://video.revoola.com/v2/images/v3_app_whoop.png"
                        key.contains("bend") -> "https://video.revoola.com/v2/images/v3_app_bend.png"
                        key.contains("coros") -> "https://video.revoola.com/v2/images/v3_app_coros.png"
                        else -> "https://video.revoola.com/v2/images/v3_app_gfit.png"
                    }
                }
                2 -> {
                    if (datas.source == "ios") {
                        returnValue = "https://video.revoola.com/v2/images/v3_app_applehealth.png"
                    } else {
                        returnValue = "https://video.revoola.com/v2/images/_app_healthconnect.png"
                    }
                }
                else -> if (datas.from_third_party_source > 10) {
                    returnValue = "https://video.revoola.com/v2/start/challenges_start.jpg"
                } else {
                    val userImages = datas.user_images?.split(",") ?: emptyList()
                    if (userImages.isNotEmpty()&& !userImages[0].isNullOrEmpty()) {
                        returnValue =  userImages[0]
                    }
                    else if (!datas.imageLinkSmall.isNullOrEmpty()) {
                        returnValue = datas.imageLinkSmall
                    }
                    else if (!datas.map_image.isNullOrEmpty() &&
                        (selectTag.toLowerCase() == "you" ||
                                datas.userid == currentUserId ||
                                (selectTag.toLowerCase() == "friends" && datas.share_map == 1))) {
                        returnValue =   datas.map_image
                    }
                    else {
                        when (datas.classType?.toLowerCase()) {
                            RLYourWayName.Workout.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_workout.png"
                            RLYourWayName.Pilates.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_pilates.png"
                            RLYourWayName.Ride.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_ride.png"
                            RLYourWayName.Run.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_run.png"
                            RLYourWayName.Walk.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_walk.png"
                            RLYourWayName.Yoga.toString().toLowerCase() -> returnValue = "https://video.revoola.com/v2/images/iphone8landscape_yoga.png"
                        }
                    }
                }
            }
        }

        return returnValue
    }

    fun RlVerifyFeedZoneName(REVPer: Double): EffortZoneFeedModel {
        val roundedREVPer = REVPer.roundToInt()

        return when {
            roundedREVPer < 30 -> EffortZoneFeedModel(
                efforZoneText = "CALM",
                efforZoneBgrClr = "#FEF2F6",
                efforZoneTxtClr = "#F177A0",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/nonactive.svg",
                cloriesImage = "/assets/icon/svg/calories-calm.svg",
                timeImage = "/assets/icon/svg/time/time-calm.svg"
            )
            roundedREVPer < 50 -> EffortZoneFeedModel(
                efforZoneText = "WARM",
                efforZoneBgrClr = "#FFFBEB",
                efforZoneTxtClr = "#FFCF2F",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/warm.svg",
                cloriesImage = "/assets/icon/svg/calories-warm.svg",
                timeImage = "/assets/icon/svg/time/time-warm.svg"
            )
            roundedREVPer < 60 -> EffortZoneFeedModel(
                efforZoneText = "CARDIO",
                efforZoneBgrClr = "#ECF8ED",
                efforZoneTxtClr = "#39B54A",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/cardio.svg",
                cloriesImage = "/assets/icon/svg/calories-cardio.svg",
                timeImage = "/assets/icon/svg/time/time-cardio.svg"
            )
            roundedREVPer < 70 -> EffortZoneFeedModel(
                efforZoneText = "FAT BURN",
                efforZoneBgrClr = "#E6F5FC",
                efforZoneTxtClr = "#0099DA",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/fatburn.svg",
                cloriesImage = "/assets/icon/svg/calories-fatburn.svg",
                timeImage = "/assets/icon/svg/time/time-fatburn.svg"
            )
            roundedREVPer < 80 -> EffortZoneFeedModel(
                efforZoneText = "ENDURANCE",
                efforZoneBgrClr = "#FFF4ED",
                efforZoneTxtClr = "#FE6902",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/endurance.svg",
                cloriesImage = "/assets/icon/svg/calories-endurance.svg",
                timeImage = "/assets/icon/svg/time/time-endurance.svg"
            )
            roundedREVPer < 90 -> EffortZoneFeedModel(
                efforZoneText = "POWER",
                efforZoneBgrClr = "#F8EDFB",
                efforZoneTxtClr = "#9900CC",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/power.svg",
                cloriesImage = "/assets/icon/svg/calories-power.svg",
                timeImage = "/assets/icon/svg/time/time-power.svg"
            )
            roundedREVPer < 100 -> EffortZoneFeedModel(
                efforZoneText = "PEEK",
                efforZoneBgrClr = "#FEEDEC",
                efforZoneTxtClr = "#ED4541",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/peak.svg",
                cloriesImage = "/assets/icon/svg/calories-peak.svg",
                timeImage = "/assets/icon/svg/time/time-peak.svg"
            )
            else -> EffortZoneFeedModel("", "", "", "", "", "")
        }
    }

    fun RLzoneDiff(REVPer: Int):Int {
        when {
            REVPer <= 30 -> {
                //Zone 1
                return 1
            }
            REVPer <= 50 -> {
                //Zone 2
                return 2
            }
            REVPer <= 60 -> {
                //Zone 3
                return 3
            }
            REVPer <= 70 -> {
                //Zone 4
                return 4
            }
            REVPer <= 80 -> {
                //Zone 5
                return 5
            }
            REVPer <= 90 -> {
                //Zone 6
                return 6
            }
            REVPer <= 100 -> {
                //Zone 7
                return 7
            }
        }
        return 1
    }

    fun RLGetZoneNo(num: Int): String {
        return when {
            num < 30 -> "zone1"
            num < 50 -> "zone2"
            num < 60 -> "zone3"
            num < 70 -> "zone4"
            num < 80 -> "zone5"
            num < 90 -> "zone6"
            num < 100 -> "zone7"
            else -> "zone7"
        }
    }

    fun RLGetZoneColor(zone: String): String {
        return when (zone) {
            "zone1" -> "#F177A0"
            "zone2" -> "#FFCF2F"
            "zone3" -> "#39B54A"
            "zone4" -> "#0099DA"
            "zone5" -> "#FE6902"
            "zone6" -> "#9900CC"
            "zone7" -> "#ED4541"
            else -> "#000000" // Default color for undefined zones
        }
    }

    fun RLgetImage(typename:String): String {
        if (typename.isNullOrEmpty()){
            return RLConstants.WALK_IMAGE
        }
        if( typename.toLowerCase().equals("walk")){
            return RLConstants.WALK_IMAGE
        }else if( typename.toLowerCase().equals("run")){
            return RLConstants.RUN_IMAGE
        }else if( typename.toLowerCase().equals("yoga")){
            return RLConstants.YOGA_IMAGE
        }else if( typename.toLowerCase().equals("pilates")){
            return RLConstants.PILATES_IMAGE
        }else if( typename.toLowerCase().equals("ride")){
            return RLConstants.RIDE_IMAGE
        }else if( typename.toLowerCase().equals("workout")){
            return RLConstants.WORKOUT_IMAGE
        }else if (typename.toLowerCase().equals("challenge-effort")){
            return RLConstants.Img_Challenge_Start
        }else if (typename.toLowerCase().equals("challenge-steps")){
            return RLConstants.Img_Challenge_Start
        }else if (typename.toLowerCase().equals("challenge-calories")){
            return RLConstants.Img_Challenge_Start
        }else if (typename.toLowerCase().equals("challenge-distance")){
            return RLConstants.Img_Challenge_Start
        }else if (typename.toLowerCase().equals("challenge-climbed")){
            return RLConstants.Img_Challenge_Start
        }else if (typename.toLowerCase().equals("challenge-duration")){
            return RLConstants.Img_Challenge_Start
        }else{
            return RLConstants.WALK_IMAGE
        }
    }

    fun RLheightsetdisplaywebview(imageView: WebView, context: FragmentActivity?) {
        // Ensure the layout has been completed before getting the width
        // Get the screen width
        val displayMetrics = DisplayMetrics()
        context!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calculate the desired height (75% of screen width)
        val desiredHeight = (screenWidth * 0.76).toInt()

        // Set the ImageView height
        val layoutParams = imageView.layoutParams
        layoutParams.height = desiredHeight
        imageView.layoutParams = layoutParams

    }

    fun RLheightsetdisplayAll(relativeLayout: RelativeLayout, context: FragmentActivity?) {
        // Ensure the layout has been completed before getting the width
        // Get the screen width
        val displayMetrics = DisplayMetrics()
        context!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calculate the desired height (75% of screen width)
        val desiredHeight = (screenWidth * 0.76).toInt()

        // Set the ImageView height
        val layoutParams = relativeLayout.layoutParams
        layoutParams.height = desiredHeight
        relativeLayout.layoutParams = layoutParams

    }

    fun RLheightsetstartimage(relativeLayout: RelativeLayout, context: FragmentActivity?) {
        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                relativeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the current width of the RelativeLayout
                val width = relativeLayout.width
                //val newHeight= (width *0.99).toInt()

                val displayMetrics = DisplayMetrics()
                context!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                val screenWidth = displayMetrics.widthPixels

                val contentHeight:Int = (screenWidth - 50)
                val cardHeight:Int= contentHeight / 2

                val newHeight= cardHeight

                // Set the new height
                val layoutParams = relativeLayout.layoutParams
                layoutParams.height = newHeight
                relativeLayout.layoutParams = layoutParams
            }
        })


    }

    fun RLheightsetViewPager(relativeLayout: ViewPager) {
        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                relativeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Get the current width of the RelativeLayout
                val width = relativeLayout.width

                // Calculate the new height (75% of the width)
                val newHeight = (width * 0.75).toInt()

                // Set the new height
                val layoutParams = relativeLayout.layoutParams
                layoutParams.height = newHeight
                relativeLayout.layoutParams = layoutParams
            }
        })


    }

    fun RLroundnumbert(number:Double):String{
        val roundedNumber = BigDecimal(number).setScale(2, BigDecimal.ROUND_HALF_EVEN).toDouble()
        return roundedNumber.toString()
    }

    fun RLdaytimeget(totalSeconds:Int):String {
        val secondsInADay = 86400
        val secondsInAnHour = 3600
        val secondsInAMinute = 60

        val days = totalSeconds / secondsInADay
        val remainingSecondsAfterDays = totalSeconds % secondsInADay

        val hours = remainingSecondsAfterDays / secondsInAnHour
        val remainingSecondsAfterHours = remainingSecondsAfterDays % secondsInAnHour
        val formattedHours = String.format("%02d", hours)

        val minutes = remainingSecondsAfterHours / secondsInAMinute
        val remainingSeconds = remainingSecondsAfterHours % secondsInAMinute
        val formattedMinutes = String.format("%02d", minutes)

        if (days>=1){
            return (days.toString()+" DAYS")
        }else{
            return (formattedHours.toString()+"h "+formattedMinutes.toString()+"m")
        }
    }

    fun RLminutesget(totalSeconds:Int):String {
        val secondsInAMinute = 60
        val minutes = totalSeconds / secondsInAMinute
        val formattedMinutes = String.format("%02d", minutes)
        return (formattedMinutes.toString())
    }

    fun RLnestedScrollTo(nested: NestedScrollView, targetView: View) {
        nested.post { nested.scrollTo(500, targetView.bottom) }
    }

    fun RLcopyCode(activity: Activity, mReffralCode: String?) {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Referral Code", mReffralCode)
        clipboardManager.setPrimaryClip(clipData)
        //        Toast.makeText(activity, "Code Coppied", Toast.LENGTH_SHORT).show();
    }

    fun RLclearFlag(context: Activity) {
        context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun RLsetFlag(activity: Activity) {
        //diable RLuser intraction on background
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    //for network state
    fun RLisNetworkAvailable(activity: Activity): Boolean {
        val connectivity = activity
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity == null) {
            return false
        } else {
            val info = connectivity.allNetworkInfo
            if (info != null) {
                for (i in info.indices) {
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun RlconvertSecondsToDays(seconds: Long): Long {
        val secondsInADay = 86400  // 24 * 60 * 60
        return seconds / secondsInADay
    }

    fun RLcapitalize(name: String): String {
        val charArray = name.toLowerCase().toCharArray()
        for (i in 0 until name.length) {
            if (i == 0 && charArray[i] != ' ' || charArray[i] != ' ' && charArray[i - 1] == ' ') {
                if (charArray[i] >= 'a' && charArray[i] <= 'z') {
                    charArray[i] = (charArray[i] - 'a' + 'A'.toInt()).toChar()
                }
            }
        }
        return String(charArray)
    }

    fun RLdialogForNetwork(activity: Activity) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage("There is no Internet Connection.. Please Connect to The Internet and Try Again")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "Goto Setting"
        ) { arg0, arg1 -> //activity.finish();
            activity.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun RLdialogBox(activity: Activity, msg: String?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { arg0, arg1 -> RLstartApp(activity) }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun RLdpToPx(c: Context, dp: Int): Int {
        val r = c.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }

    fun RLdialogBoxWithoutExit(activity: Activity?, msg: String?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { arg0, arg1 -> alertDialogBuilder.create().cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun RLstartApp(activity: Activity) {
        val i = activity.baseContext.packageManager
            .getLaunchIntentForPackage(activity.baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(i)
    }

    fun RLsecureAppMthd(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun RLsecureAppMthd(activity: FragmentActivity?) {
        if (activity != null) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    fun RLhideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun RLhideKeyBoard(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    fun RLtransparentStatusBar(activity: Activity) {
        //status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun RLvisitPage(activity: Activity, urlString: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            activity.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            RlLogDPrint("pp", "no browser found" + ex.message)
        }
    }

    fun RLconvertDateTimeToTimestamp(dateString:String): Long? {

        // Define the date format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the date string to a Date object
        val date = dateFormat.parse(dateString)

        // Get the time in milliseconds
        val timeInMillis = date?.time

        return timeInMillis
    }

    fun RLconvertTimestampToDateTime(timestamp: Long): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm")
                val dateTime: LocalDateTime =LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                val dateall=dateTime.format(formatter)
                return dateall
            }else{
                return "0"
            }
        } catch (ex: ActivityNotFoundException) {
            return "0"
        }
    }

    fun RLformatTime(secs: Int, isSec: Boolean): String {
        val secNum = secs
        val hours = secNum / 3600
        val minutes = (secNum / 60) % 60
        val seconds = secNum % 60

        var time = ""
        if (hours > 0) {
            time += "${hours}h "
        }
        if (minutes > 0) {
            time += if (minutes < 10) {
                "0${minutes}m"
            } else {
                "${minutes}m"
            }
        }
        if (hours == 0 && seconds > 0 && isSec) {
            time += if (seconds < 10) {
                " 0${seconds}s"
            } else {
                " ${seconds}s"
            }
        }

        if (time.isEmpty()) {
            time = "00min"
        }

        return time
    }

    fun RLconvertTimestampToDAte(timestamp: Long): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd '|' HH:mm")
                //val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
                val dateTime: LocalDateTime =LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                val dateall=dateTime.format(formatter)//+" | "+dateTime.format(formatter2)
                return dateall
            }else{
                return "0"
            }
        } catch (ex: ActivityNotFoundException) {
            return "0"
        }
    }

    fun RLgetMetric(thirdPartySource: Int,cardData: RLTextOverview): RlMetric {
        return when (thirdPartySource) {
            in listOf(20, 26, 32, 38) -> RlMetric(
                R.drawable.ic_heart,
                title = "effort",
                value = cardData.totalREV.toInt(),
                uom = "",
                isTime = false
            )
            in listOf(21, 27, 33, 39) -> RlMetric(
                R.drawable.fd_steps_green,
                title = "steps",
                value = cardData.steps,
                uom = "Steps",
                isTime = false
            )
            in listOf(22, 28, 34, 40) -> RlMetric(
                R.drawable.fd_calories_green,
                title = "calories",
                value = cardData.burntCalories.toInt(),
                uom = "",
                isTime = false
            )
            in listOf(23, 29, 35, 41) -> RlMetric(
                R.drawable.fd_active_time_green,
                title = "duration",
                value = cardData.totalTime.toInt(),
                uom = "",
                isTime = true
            )
            in listOf(24, 30, 36, 42) -> RlMetric(
                R.drawable.ic_climb,
                title = "climbed",
                value = cardData.elevation,
                uom = " (m)",
                isTime = false
            )
            in listOf(25, 31, 37, 43) -> RlMetric(
                R.drawable.ic_distance,
                title = "distance",
                value = cardData.distance.toInt(),
                uom = " (km)",
                isTime = false
            )
            else -> RlMetric(
                R.drawable.fd_steps_green,
                title = "steps",
                value = cardData.steps,
                uom = "Steps",
                isTime = false
            )
        }
    }

    fun RlgetDifferenceInDays(timestampInMillis: Long,totalDays:String): String {
        // Convert timestamp to LocalDate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val givenDate = Instant.ofEpochMilli(timestampInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            // Get the current date
            val currentDate = LocalDate.now()

            val different=ChronoUnit.DAYS.between(givenDate, currentDate)

            val secondsInADay = 86400
            val differentInADay = different/secondsInADay
           RlLogEPrint("TAG","different:- $different  ,differentInADay:- $differentInADay")
            val final=totalDays.toInt() - differentInADay.toDouble().toInt()
            // Calculate the difference in days between the current date and the given date
            return final.toString()
        }else{
            return "0"
        }
    }

    fun RlgetDifferenceInDays1(timestampInMillis: Long,totalDays:String): String {
        // Convert timestamp to LocalDate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val givenDate = Instant.ofEpochMilli(timestampInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            // Get the current date
            val currentDate = LocalDate.now()

            val different=ChronoUnit.DAYS.between(givenDate, currentDate)

            val secondsInADay = 86400
            val differentInADay = different/secondsInADay
           RlLogEPrint("TAG","different:- $different  ,differentInADay:- $differentInADay")
            val final=totalDays.toInt() - differentInADay.toDouble().toInt()
            // Calculate the difference in days between the current date and the given date
            return final.toString()
        }else{
            return "0"
        }
    }

    fun RLvisitSite(activity: Context, url: String, user_id: String?) {
        val shop_url = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                url.replace(
                    "{USERID}",
                    user_id!!
                )
            )
        )
        activity.startActivity(shop_url)
    }

    fun RLcopyClipBoard(activity: Activity, str: String?) {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("coupon", str)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun RLgenerateGradient(color: IntArray?): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color)
    }

    fun RLshareOnWhatsapp(activity: Activity, url: String?) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "RLText/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, url)
        try {
            activity.startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
//            RLTools.RlLogDPrint("Activity", "Whatsapp have not been installed.");
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.whatsapp")
                )
            )
        }
    }

    fun RLconvertData(inputDate: String?): String? {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output = SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa")
        var date: Date? = null
        var outputString: String? = null
        try {
            date = input.parse(inputDate)
            outputString = output.format(date)
            return outputString
        } catch (pe: ParseException) {
            pe.printStackTrace()
        }
        return null
    }

    fun RLopenPlaystore(activity: Activity, url: String?) {
        val shareOnPlaystore = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(shareOnPlaystore)
    }

    fun RLisConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }

    fun RLisConnectingToInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
//                RLTools.RlLogDPrint(TAG, "Internet Active: " + activeNetwork.getTypeName());
                return true
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
//                RLTools.RlLogDPrint(TAG, "Internet Active: " + activeNetwork.getTypeName());
                return true
            }
        }
        return false
    }

    fun RLrupeeTOPaise(rupee: String): Int {
        val split = rupee.split("\\.".toRegex()).toTypedArray()
        val item1 = split[0].toInt()
        val item2 = split[1].toInt()
        return item1 * 100 + item2
    }

    fun RLroundOffAmount(amount: Double?): Double {
        val decimalFormat = DecimalFormat(".##")
        decimalFormat.roundingMode = RoundingMode.CEILING
        return java.lang.Double.valueOf(decimalFormat.format(amount))
    }

    fun RLnextActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
    }

    fun RLnextFinishAllActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
        activity.finish()
    }


    //for Device Id
    fun RLgetDeviceId(activity: Activity): String {
        return Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
    }
    fun RLisEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    fun RLvalidateVehicleNumber(email: String): Boolean {
        val emailRegex = Regex( "[a-zA-z]{2}+[0-9]{2}+[a-zA-z]{2}+[0-9]{4}")
        return emailRegex.matches(email)
    }

    @SuppressLint("SimpleDateFormat")
    fun RLgetCalculatedMonths(): String? {
        val c: Calendar = GregorianCalendar()
        val sdfr = SimpleDateFormat("MMM yyyy")
        return sdfr.format(c.time).toString()
    }

     fun RLformatCommas(number: Double): String {
         val decimalPlaces=2
      /*  val numberFormat = NumberFormat.getNumberInstance(Locale.US)
         val integerPart = number.roundToInt()
         val formattedInteger = numberFormat.format(integerPart)
        return formattedInteger*/

         val formatter = NumberFormat.getInstance(Locale.getDefault())
         formatter.maximumFractionDigits = 2
         formatter.minimumFractionDigits = 0
         return formatter.format(number?:0)
    }

    fun RLnumberToUUID(number: Int): UUID {
        val baseUUID = "0000%04x-0000-1000-8000-00805f9b34fb"
        return UUID.fromString(String.format(baseUUID, number))
    }


}
