package com.revoola.commonobject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.revoola.R
import com.revoola.enumclass.RLYourWayName
import com.revoola.model.RLTextOverview
import com.revoola.model.RlMetric

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

import android.content.Context
import android.net.NetworkCapabilities
import com.revoola.model.EffortZoneFeedModel

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.revoola.model.BooleanDeserializer
import com.revoola.model.DoubleDeserializer
import com.revoola.model.FloatDeserializer
import com.revoola.model.IntDeserializer
import com.revoola.model.LongDeserializer
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLZoneChartData
import com.revoola.model.StringDeserializer
import com.revoola.utils.RLConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.temporal.TemporalAdjusters


object RLTools {

    fun getInitialsBitmap( context: Context,name: String): Bitmap {
        val textColor = ContextCompat.getColor(context, R.color.AppMainColor)
        val bgColor = ContextCompat.getColor(context, R.color.AppTextLightGrayColor)
        val size = 200

        val initials = name.split(" ")
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it[0].uppercase() }

        val paint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = size / 2f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) // 👈 Bold text
        }

        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(bgColor)

        val x = size / 2f
        val y = size / 2f - (paint.descent() + paint.ascent()) / 2
        canvas.drawText(initials, x, y, paint)

        return bmp
    }

    fun getColorForScheduleStatus(statusText: String): Int {
        return when (statusText.lowercase()) {
            "scheduled for" -> R.color.AppMainColor
            "completed"     -> R.color.AppScheduleCompletedColor
            "declined"      -> R.color.AppScheduleDeclinedColor
            "missed"        -> R.color.AppScheduleMissedColor
            "lost"          -> R.color.AppScheduleLoseColor
            "won", "win"    -> R.color.AppScheduleWinColor
            else            -> R.color.AppMainColor
        }
    }

    // Custom Gson Builder with all deserializers
    fun createCustomGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .registerTypeAdapter(Long::class.java, LongDeserializer())
            .registerTypeAdapter(Int::class.java, IntDeserializer())
            .registerTypeAdapter(String::class.java, StringDeserializer())
            .registerTypeAdapter(Boolean::class.java, BooleanDeserializer())
            .registerTypeAdapter(Double::class.java, DoubleDeserializer())
            .registerTypeAdapter(Float::class.java, FloatDeserializer())
            .create()
    }

    // Usage Example
    fun parseUserData(data: Any): RLRevoolaUsersSettingsModel? {
        return try {
            val gson = createCustomGson()
            val jsonObject = gson.toJson(data)
            gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun rl_logDPrint(tag_log:String, message_log:String){
        Log.d(tag_log,message_log)
    }

    fun rl_logEPrint(tag_log:String, message_log:String){
        Log.e(tag_log,message_log)
    }

    fun rl_challengeIcon(challengeType:String):Int{
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
    fun rl_challengeTargetIcon(TargetType:String):Int{
        when (TargetType.toLowerCase()){
            "individualtarget"-> return R.drawable.ic_goal
            "sharedtarget"-> return R.drawable.goal_shared
            else -> return R.drawable.ic_goal
        }
    }
    fun rl_challengeTargetName(TargetType:String):String{
        when (TargetType.toLowerCase()){
            "individualtarget"-> return "individual"
            "sharedtarget"-> return "shared"
            else -> return "individual"
        }
    }

    fun rl_challengeForIcon(challengeForType:String):Int{
        when (challengeForType.toLowerCase()){
            "you"-> return R.drawable.ic_you
            "friends"-> return R.drawable.fr_friends_green
            "group"-> return R.drawable.ic_groups
            "groupvgroup"-> return R.drawable.ic_group_v_group
            else -> return R.drawable.ic_you
        }
    }
    fun rl_calendetIcon(calenderType:String):Int{
        when (calenderType.toLowerCase()){
            "daily"-> return R.drawable.calendar_daily
            "weekly"-> return R.drawable.calendar_weekly
            "monthly"-> return R.drawable.calendar_monthly
            "custom"-> return R.drawable.calendar_custom
            else -> return R.drawable.calendar_monthly
        }
    }

    fun rl_monthNameTogetFirstDate(inputDate: String): String {
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

    fun rl_stringDateToMonthYearFormate(dateString:String, isYear:Boolean):String {
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

    fun rl_stringDateToDateFormate(dateString:String):Date? {
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

    fun rl_monthNameTogetLastDate(dateString: String): String {
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

    fun rl_convertDate(inputDate: String): String {
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

    fun rl_showAlertDialog(context: Context, activity: Activity) {
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

    fun rl_hasNotificationPermission(context: Context): Boolean {
        return  ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun rl_calculateCircularGraph(rev: Int): String {
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
                return "#ED4541"
            }
        }
    }
    fun rl_getState(rev: Int): Int {
        return when {
            rev <= 30 -> 0
            rev <= 50 -> 1
            rev <= 60 -> 2
            rev <= 70 -> 3
            rev <= 80 -> 4
            rev <= 90 -> 5
            rev <= 100 -> 6
            else -> 7
        }
    }
    fun rl_logLarge(tag: String, message: String) {
        val maxLogSize = 4000
        var start = 0

        while (start < message.length) {
            val end = minOf(start + maxLogSize, message.length)
            Log.d(tag, message.substring(start, end))
            start = end
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

    fun rl_calculateAge(dateString: String): Int {
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

    fun rl_challengesTypeGet(typename:String): String {
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
    fun rl_getMetricsName(metric:String?): String {
        val metric = metric ?: return "STEPS"
        return if (metric.equals("elevation", ignoreCase = true)) {
            "CLIMBED"
        } else {
            metric.uppercase()
        }
    }
    fun rl_getClassTypeValue(classType: String?): String {
        return when {
            classType?.contains("steps", ignoreCase = true) == true -> "steps"
            classType?.contains("effort", ignoreCase = true) == true -> "effort"
            else -> "steps"
        }
    }
    fun rl_geticon(typename:String): Int {
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
        }else if( typename.toLowerCase().equals("effort")){
            return R.drawable.ic_heart
        }else if( typename.toLowerCase().equals("elevation")){
            return R.drawable.ic_climb
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
        } else if( typename.toLowerCase().equals("warm")){
            return R.drawable.ic_warmup
        } else if( typename.toLowerCase().equals("hiit")){
            return R.drawable.ic_hiit
        }else if( typename.toLowerCase().equals("pilates")){
            return R.drawable.ic_pilates
        }else if( typename.toLowerCase().equals("ride")){
            return R.drawable.fd_ride_green
        }else if( typename.toLowerCase().equals("energise")){
            return R.drawable.ic_power
        }else if( typename.toLowerCase().equals("workout")){
            return R.drawable.ic_workout
        }else if( typename.toLowerCase().equals("mindful movement")){
            return R.drawable.ic_move
        }else if( typename.toLowerCase().equals("relax")){
            return R.drawable.ic_relax
        }else if( typename.toLowerCase().equals("sleep")){
            return R.drawable.ic_speeed
        }else if( typename.toLowerCase().equals("focus")){
            return R.drawable.ic_focus
        }else if( typename.toLowerCase().equals("energise")){
            return R.drawable.ic_power
        }else if( typename.toLowerCase().equals("happiness")){
            return R.drawable.ic_sun
        }else{
            return R.drawable.ic_dance
        }
    }

    fun rl_getImage1(typename:String): Int {
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

    fun rl_feedSetImage(datas: RLTextOverview, currentUserId:String, selectTag:String): String {
        if (datas != null) {
            when (datas.from_third_party_source) {
                1 -> {
                    val key = datas.className?.toLowerCase() ?: ""
                    return when {
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
                    if (datas.source.toLowerCase().equals("ios")) {
                        return "https://video.revoola.com/v2/images/v3_app_applehealth.png"
                    } else {
                        return "https://video.revoola.com/v2/images/_app_healthconnect.png"
                    }
                }
                else ->{
                    if (datas.from_third_party_source > 10) {
                        return "https://video.revoola.com/v2/start/challenges_start.jpg"
                    }else {
                        val userImages = datas.user_images?.split(",") ?: emptyList()
                        if (userImages.isNotEmpty()&& !userImages[0].isNullOrEmpty()) {
                            return userImages[0]
                        }
                        else if (!datas.imageLinkSmall.isNullOrEmpty()) {
                            return datas.imageLinkSmall
                        }
                        else if (!datas.map_url.isNullOrEmpty() && (selectTag.toLowerCase() == "you" ||datas.userid == currentUserId || (selectTag.toLowerCase() == "friends" && datas.share_map == 1))) {
                            var secureMapUrl = datas.map_url
                            if (secureMapUrl.startsWith("http://")) {
                                secureMapUrl = secureMapUrl.replaceFirst("http://", "https://")
                            }
                            return "$secureMapUrl&key=AIzaSyBUc1JOJWSpJJtGIge4xc1LBcTT_m3w1FU"
                        }
                        else if (!datas.map_image.isNullOrEmpty() && (selectTag.toLowerCase() == "you" ||datas.userid == currentUserId || (selectTag.toLowerCase() == "friends" && datas.share_map == 1))) {
                            return  datas.map_image
                        }
                        else {
                            return  when (datas.classType?.toLowerCase()) {
                                RLYourWayName.Workout.toString().toLowerCase() -> "https://video.revoola.com/v2/images/iphone8landscape_workout.png"
                                RLYourWayName.Pilates.toString().toLowerCase() ->  "https://video.revoola.com/v2/images/iphone8landscape_pilates.png"
                                RLYourWayName.Ride.toString().toLowerCase() ->  "https://video.revoola.com/v2/images/iphone8landscape_ride.png"
                                RLYourWayName.Run.toString().toLowerCase() -> "https://video.revoola.com/v2/images/iphone8landscape_run.png"
                                RLYourWayName.Walk.toString().toLowerCase() ->  "https://video.revoola.com/v2/images/iphone8landscape_walk.png"
                                RLYourWayName.Yoga.toString().toLowerCase() ->  "https://video.revoola.com/v2/images/iphone8landscape_yoga.png"
                                else -> "https://video.revoola.com/v2/images/iphone8landscape_walk.png"
                            }
                        }
                    }
                }
            }
        }else{
            return ""
        }

    }

    fun rl_getLinkImage(classType:String):String{
        when (classType) {
            RLYourWayName.Workout.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_workout.png"
            RLYourWayName.Pilates.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_pilates.png"
            RLYourWayName.Ride.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_ride.png"
            RLYourWayName.Run.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_run.png"
            RLYourWayName.Walk.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_walk.png"
            RLYourWayName.Yoga.toString().toLowerCase() -> return  "https://video.revoola.com/v2/images/iphone8landscape_yoga.png"
            else -> return "https://video.revoola.com/v2/images/iphone8landscape_workout.png"
        }
    }

    fun rl_verifyFeedZoneName(REVPer: Double): EffortZoneFeedModel {
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
            else ->  EffortZoneFeedModel(
                efforZoneText = "PEEK",
                efforZoneBgrClr = "#FEEDEC",
                efforZoneTxtClr = "#ED4541",
                effortImage = "https://video.revoola.com/v2/icons/05metrics/zonehearts/peak.svg",
                cloriesImage = "/assets/icon/svg/calories-peak.svg",
                timeImage = "/assets/icon/svg/time/time-peak.svg"
            )
        }
    }

    fun rl_zoneDiff(REVPer: Int):Int {
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

    fun rl_getZoneNo(num: Int): String {
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

    fun rl_getZoneColor(zone: String): String {
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

    fun rl_getImage(typename:String): String {
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

    fun rl_heightsetdisplayAll(relativeLayout: RelativeLayout, context: FragmentActivity?) {
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

    fun rl_heightsetstartimage(relativeLayout: RelativeLayout, context: FragmentActivity?) {
        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                relativeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
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

    private fun isValidValueInt(value: Int?): Boolean {
        return if (value == null) false else true
    }

    fun rl_getIsImperial(appUnit:String): Boolean {
        return when (appUnit) {
            "Imperial" -> true
            "Metric" -> false
            else -> false
        }
    }

    fun rl_zoneDataToJson(zoneData: List<RLZoneChartData>): JSONArray {
        val jsonArray = JSONArray()
        for (zone in zoneData) {
            val jsonObject = JSONObject()
            jsonObject.put("zone", zone.zone)
            jsonObject.put("value", zone.seconds)
            jsonObject.put("color", zone.color)
            jsonArray.put(jsonObject)
        }
        return jsonArray
    }

    fun rl_toTimeLabel(secs: Int?): String {
        if (!isValidValueInt(secs)) return "0"

        val secNum = secs ?: 0
        val hours = secNum / 3600
        val minutes = (secNum / 60) % 60
        val seconds = secNum % 60

        var time = ""

        if (hours > 0) {
            time += "${hours}h "
        }
        if (minutes > 0) {
            time += "${minutes}m "
        }
        if (hours == 0 && seconds > 0) {
            time += "${seconds}s"
        }
        if (time.isEmpty()) {
            time = "00s"
        }

        return time.trim() // Removes any trailing spaces
    }

    fun rl_getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp_img", ".jpg", context.cacheDir)
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }
    fun rl_formatValue(title: String?, value: Any?): String {
        if (title != null) {
            val lowercaseTitle = title.lowercase()

            return when {
                lowercaseTitle.contains("time") ||
                        lowercaseTitle.contains("pace") ||
                        lowercaseTitle.contains("minute") -> value.toString()

                lowercaseTitle.contains("session") -> rl_toTimeLabel(value as Int?)

                lowercaseTitle.contains("speed") ||
                        lowercaseTitle.contains("distance") -> {
                    val parsedValue = value.toString().toDoubleOrNull()
                    if (parsedValue == null || !parsedValue.isFinite()) "0"
                    else addComma("%.2f".format(parsedValue))
                }

                else -> {
                    val parsedValue = value.toString().toDoubleOrNull()
                    if (parsedValue == null || !parsedValue.isFinite()) "0"
                    else addComma("%.0f".format(parsedValue))
                }
            }
        }
        val parsedValue = value.toString().toDoubleOrNull()
        return if (parsedValue == null || !parsedValue.isFinite()) "0"
        else addComma("%.0f".format(parsedValue))
    }

    fun addComma(nStr: Any?): String {
        return try {
            val number = nStr.toString().toDoubleOrNull() ?: return "0"
            val formatter: NumberFormat = DecimalFormat("#,###.##")
            formatter.format(number)
        } catch (e: Exception) {
            "0"
        }
    }
    fun rl_getCurrentISO8601(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC") // Ensure UTC time zone
        return sdf.format(Date()) // Get current date & time
    }

    fun rl_formatMinutesToTimeLabel(minutes: Int): String {
        return if (minutes >= 60) {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) {
                String.format("%dh %02dm", hours, remainingMinutes) // Adds leading zero if < 10
            } else {
                String.format("%dh", hours)
            }
        } else {
            String.format("%02dm", minutes)
        }
    }

    fun rl_getDisplayMessage(joiningDate:Long, sessionCount:Int): String {
        val date = Calendar.getInstance()
        val y = date.get(Calendar.YEAR)
        val m = date.get(Calendar.MONTH)

        val firstDay = Calendar.getInstance().apply {
            set(y, m, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = firstDay.timeInMillis / 1000

        return if (joiningDate < today) {
            if (sessionCount > 0) "Keep up the good work. You are smashing it"
            else "Good to see you back. Let's get going"
        } else {
            if (sessionCount > 0) "Good to see you back. Let's get going!"
            else "Good to see you here. Let's get going!"
        }
    }

    fun rl_daytimeget(totalSeconds:Int):String {
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

    fun rl_minutesget(totalSeconds:Int):String {
        val secondsInAMinute = 60
        val minutes = totalSeconds / secondsInAMinute
        val formattedMinutes = String.format("%02d", minutes)
        val timeminutes = rl_formatMinutesToTimeLabel(formattedMinutes.toInt())
        return timeminutes
    }

    fun rl_convertTimestampToDateTime(timestamp: Long): String {
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

    fun rl_formatTime(secs: Int, isSec: Boolean): String {
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
            if (isSec){
                time = "00s"
            }else{
                time = "00min"
            }

        }

        return time
    }

    fun rl_formatTimeNoMS(secs: Int, isSec: Boolean): String {
        val secNum = secs
        val hours = secNum / 3600
        val minutes = (secNum / 60) % 60
        val seconds = secNum % 60

        var time = ""

        // If hours are greater than 0, add hours to the time string
        if (hours > 0) {
            time += "${hours}:"
        }

        // Add minutes to the time string, ensuring a leading zero if the minutes are less than 10
        if (minutes > 0 || hours > 0) {
            time += if (minutes < 10) {
                "0${minutes}:"
            } else {
                "${minutes}:"
            }
        }

        // If seconds should be shown (isSec), always display seconds even if hours > 0
        if (isSec) {
            time += if (seconds < 10) {
                "0${seconds}"
            } else {
                "${seconds}"
            }
        }

        // If no time components were added (for 0 seconds), set the time to 0
        if (time.isEmpty()) {
            time = "0"
        }

        return time
    }
    fun rl_formatTimestamp(timestamp: Long): String {
        // timestamp is in seconds, so convert to milliseconds
        val date = Date(timestamp * 1000)

        // Format: 01 AUG 2024 (dd MMM yyyy in uppercase)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(date).uppercase(Locale.ENGLISH)
    }

    fun rl_convertTimestampToDAte(timestamp: Long): String {
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

    fun rl_convertTimestampToSchdualDAte(timestamp: Long): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")
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

    fun rl_convertDateToTimestamp(dateString: String, isStartDate: Boolean): String {
        return try {
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            val date = dateFormat.parse(dateString)

            val calendar = Calendar.getInstance()
            calendar.time = date!!

            if (isStartDate) {
                // Set to start of day: 00:00:00.000
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            } else {
                // Set to end of day: 23:59:59.999
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
            }

            // Return Unix timestamp in seconds
            (calendar.timeInMillis / 1000).toString()

        } catch (e: Exception) {
            e.printStackTrace()
            "0"
        }
    }


    fun rl_getMetric(thirdPartySource: Int, cardData: RLTextOverview): RlMetric {
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

    fun rl_isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    @SuppressLint("SimpleDateFormat")
    fun rl_getCalculatedMonths(): String? {
        val c: Calendar = GregorianCalendar()
        val sdfr = SimpleDateFormat("MMM yyyy")
        return sdfr.format(c.time).toString()
    }

    fun rl_formatCommasInt(number: Double): String {
        if (number<0){
            return "0"
        }else{
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            val integerPart = number.roundToInt()
            val formattedInteger = numberFormat.format(integerPart)
            return formattedInteger
        }
    }
    fun rl_formatCommasInt(number: Int): String {
        if (number<0){
            return "0"
        }else{
            val formatter = DecimalFormat("##,##,##,##0") // Keeps up to 2 decimals without rounding
            return formatter.format(number)

//            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
//            val integerPart = number
//            val formattedInteger = numberFormat.format(integerPart)
//            return formattedInteger
        }
    }

    fun rl_formatCommas(number: Double): String {
        if (number>0){
           // val formatter = DecimalFormat("#,##0.##") // Keeps up to 2 decimals without rounding
            val formatter = DecimalFormat("##,##,##,##0.##") // Keeps up to 2 decimals without rounding
            return formatter.format(number)
        }else{
            return "0"
        }

    }

}
