package com.example.myfirstapp.utils

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
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.example.myfirstapp.R

import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


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
           return R.drawable.ic_run
       }else if (typename.toLowerCase().equals("challenge-steps")){
           return R.drawable.ic_walk
       }else if (typename.toLowerCase().equals("challenge-calories")){
           return R.drawable.ic_run
       }else if (typename.toLowerCase().equals("challenge-distance")){
           return R.drawable.ic_run
       }else if (typename.toLowerCase().equals("challenge-climbed")){
           return R.drawable.ic_run
       }else if (typename.toLowerCase().equals("challenge-duration")){
           return R.drawable.ic_run
       }else if( typename.toLowerCase().equals("yoga")){
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

    fun RLgetImage(typename:String): String {
        if (typename.isNullOrEmpty()){
            return  RLConstants.WALKIMAGE
        }
        if( typename.toLowerCase().equals("walk")){
            return  RLConstants.WALKIMAGE
        }else if( typename.toLowerCase().equals("run")){
            return RLConstants.RUNIMAGE
        }else if( typename.toLowerCase().equals("yoga")){
            return RLConstants.YOGAIMAGE
        }else if( typename.toLowerCase().equals("pilates")){
            return RLConstants.PILATESIMAGE
        }else if( typename.toLowerCase().equals("ride")){
            return RLConstants.RIDEIMAGE
        }else if( typename.toLowerCase().equals("workout")){
            return RLConstants.WORKOUTIMAGE
        }else if (typename.toLowerCase().equals("challenge-effort")){
            return RLConstants.img_challenge_start
        }else if (typename.toLowerCase().equals("challenge-steps")){
            return RLConstants.img_challenge_start
        }else if (typename.toLowerCase().equals("challenge-calories")){
            return RLConstants.img_challenge_start
        }else if (typename.toLowerCase().equals("challenge-distance")){
            return RLConstants.img_challenge_start
        }else if (typename.toLowerCase().equals("challenge-climbed")){
            return RLConstants.img_challenge_start
        }else if (typename.toLowerCase().equals("challenge-duration")){
            return RLConstants.img_challenge_start
        }else{
            return  RLConstants.WALKIMAGE
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

    fun RLheightsetdisplayview(imageView: RelativeLayout, context: FragmentActivity?) {
        // Ensure the layout has been completed before getting the width
        // Get the screen width
        val displayMetrics = DisplayMetrics()
        context!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calculate the desired height (75% of screen width)
        val desiredHeight = (screenWidth * 0.75).toInt()

        // Set the ImageView height
        val layoutParams = imageView.layoutParams
        layoutParams.height = desiredHeight
        imageView.layoutParams = layoutParams

    }
    fun RLheightsetdisplayimgview(imageView: ImageView, context: FragmentActivity?) {
        // Ensure the layout has been completed before getting the width
        // Get the screen width
        val displayMetrics = DisplayMetrics()
        context!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calculate the desired height (75% of screen width)
        val desiredHeight = (screenWidth * 0.75).toInt()

        // Set the ImageView height
        val layoutParams = imageView.layoutParams
        layoutParams.height = desiredHeight
        imageView.layoutParams = layoutParams

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
            activity.window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
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
            Log.d("pp", "no browser found" + ex.message)
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
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
                val dateTime: LocalDateTime =LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                val dateall=dateTime.format(formatter)+" at "+dateTime.format(formatter2)
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
                val formatter = DateTimeFormatter.ofPattern("EEE, MMM dd")
                val formatter2 = DateTimeFormatter.ofPattern("HH:mm")
                val dateTime: LocalDateTime =LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                val dateall=dateTime.format(formatter)+" | "+dateTime.format(formatter2)
                return dateall
            }else{
                return "0"
            }
        } catch (ex: ActivityNotFoundException) {
            return "0"
        }
    }

    fun RLconvertTimestampToDate(timestamp: Long): String {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dateTime: LocalDateTime =LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                return dateTime.format(formatter)
            }else{
                return "0"
            }
        } catch (ex: ActivityNotFoundException) {
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
//            Log.d("Activity", "Whatsapp have not been installed.");
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
//                Log.d(TAG, "Internet Active: " + activeNetwork.getTypeName());
                return true
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
//                Log.d(TAG, "Internet Active: " + activeNetwork.getTypeName());
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

    fun RLshowSnackBar(activity: Activity, msg: String?, bgColor: Int, textColor: Int) {
        val parent_view = activity.findViewById<View>(android.R.id.content)
        val mSnackBar = Snackbar.make(parent_view, msg!!, Snackbar.LENGTH_SHORT)
        val view = mSnackBar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        view.setBackgroundColor(bgColor)
        val mainTextView =
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        mainTextView.setTextColor(textColor)
        // view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.slide_in))
        mSnackBar.show()
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
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
         val integerPart = number.roundToInt()
         val formattedInteger = numberFormat.format(integerPart)
        return formattedInteger
    }

    fun RLnumberToUUID(number: Int): UUID {
        val baseUUID = "0000%04x-0000-1000-8000-00805f9b34fb"
        return UUID.fromString(String.format(baseUUID, number))
    }

    fun RLgetEffortChartHtml(jsondata: String) : String {
        val webdata:String="""
            <!DOCTYPE html>
        <meta charset="utf-8">
        <title>Line Chart</title>
        <style> /* set the CSS */

                body { font: 30px Arial;background-color: #FFFFFFFF;}

                path {
                            stroke: steelblue;
                            stroke-width: 2;
                            fill: none;
                        }

                        .axis path,
                        .axis line {
                            fill: none;
                            stroke: grey;
                            stroke-width: 1;
                            shape-rendering: crispEdges;
                        }
                .axis RLText{
                  fill: grey;
                  color: grey;
                }
                        .line {
                            fill: none;
                            stroke: url(#line-gradient);
                            stroke-width: 3px;
                        }

                    .dot {
                            fill: url(#line-gradient);
                        }

                        </style>
                <body>
                <div id="lineChart"></div>


                <!-- load the d3.js library -->
                <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js"></script>

                <script>

                // Set the dimensions of the canvas / graph
                var margin = {top: 100, right: 20, bottom: 100, left: 50},
                    width = window.innerWidth - margin.left - margin.right,
                    height = window.innerHeight - margin.top - margin.bottom;

                // Set the ranges
                var x = d3.scale.linear().range([0, width]);
                var y = d3.scale.linear().range([height, 0]);

                // Define the axes
                var xAxis = d3.svg.axis().scale(x)
                    .orient("bottom").outerTickSize(0).innerTickSize(-height, 0, 0).ticks(6);
                var yAxis = d3.svg.axis().scale(y)
                    .orient("left").ticks(5).outerTickSize(0).innerTickSize(-width, 0, 0).ticks(10);

                // Define the line
                var valueline = d3.svg.line().interpolate("linear")
                    .x(function(d) { return x(d.time); })
                    .y(function(d) { return y(d.effort); });
                    
                // Adds the svg canvas
                var svg = d3.select("#lineChart")
                    .append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                        .attr("transform",
                              "translate(" + margin.left + "," + margin.top + ")");



                    var data = JSON.parse('$jsondata');
                    data.forEach(function(d) {
                            d.time = +d.time/60;
                            d.effort = +d.effort;
                        });
                
                        // Scale the range of the data
                        x.domain([d3.min(data, function(d) { return +d.time; }), d3.max(data, function(d) { return +d.time; })]);
                        y.domain([0, 100]);
                
                        svg.append("linearGradient")
                            .attr("id", "line-gradient")
                            .attr("gradientUnits", "userSpaceOnUse")
                            .attr("x1", 0).attr("y1", y(0))
                            .attr("x2", 0).attr("y2", y(100))
                        .selectAll("stop")
                            .data([
                                {offset: "0%", color: "rgb(241, 119, 160)"},
                                {offset: "29.99%", color: "rgb(241, 119, 160)"},
                                {offset: "30%", color: "rgb(255, 207, 47)"},
                                {offset: "49.99%", color: "rgb(255, 207, 47)"},
                                {offset: "50%", color: "rgb(44, 174, 44)"},
                                {offset: "59.99%", color: "rgb(44, 174, 44)"},
                                {offset: "60%", color: "rgb(0, 153, 218)"},
                                {offset: "69.99%", color: "rgb(0, 153, 218)"},
                                {offset: "70%", color: "rgb(254, 105, 02)"},
                                {offset: "79.99%", color: "rgb(254, 105, 02)"},
                                {offset: "80%", color: "rgb(153, 0, 204)"},
                                {offset: "89.99%", color: "rgb(153, 0, 204)"},
                                {offset: "90%", color: "rgb(237, 69, 65)"},
                                {offset: "99.99%", color: "rgb(237, 69, 65)"}
                            ])
                        .enter().append("stop")
                            .attr("offset", function(d) { return d.offset; })
                            .attr("stop-color", function(d) { return d.color; });
                
                        // Add the valueline path.
                        svg.append("path")
                            .attr("class", "line")
                            .attr("d", valueline(data));
                
                        // Add the X Axis
                        var xx = svg.append("g")
                            .attr("class", "x axis")
                            .attr("transform", "translate(0," + height + ")")
                            .call(xAxis);
                
                        // Add the Y Axis
                        var yx = svg.append("g")
                            .attr("class", "y axis")
                            .call(yAxis);
                        
                            xx.selectAll("line").remove();
                            
                            yx.selectAll("line").style("opacity", 0.6);
                            yx.selectAll("RLText").attr("x", -5);
                
                            var last = svg.selectAll(".y .tick RLText")[0].length;
                
                            svg.selectAll(".y .tick RLText")[0].forEach(function(d, i){
                            if (i==last-1){
                                return;
                            }
                            if(i%2!=0)
                                d3.select(d).style("display", "none")
                            });
                
                            svg.append("RLText").attr("x", (width/2)-100).attr("y", height+80).style("font-size", 40).style("fill", "grey").RLText("MINUTES");
                            svg.append("RLText").attr("x", 0).attr("y", -20).style("font-size", 40).style("fill", "grey").RLText("EFFORT%");

    </script>
    </body>
</html>           
""".trimIndent()
        return webdata
    }
    fun RLgetElevationHtml(arrCumDistance:String, arrElevation:String): String{
        return """
            <!DOCTYPE html>
            <head>
                <meta charset="utf-8">
                <title>Area Chart</title>
                <style>
                    line{
                        stroke-opacity: 0.5;
                        stroke: grey;
                    }
                    .xaxis .tick line{
                        stroke-opacity: 0;
                        stroke: grey;
                    }
                    path.domain{
                        stroke: grey;
                        stroke-width: 1.5;
                    }
                    .yaxis RLText{
                        font-size: 30px;
                        fill: grey;
                    }
                    .xaxis RLText{
                        font-size: 30px;
                        fill: grey;
                    }
                    table {
              width: 100%;
            }
                </style>
                <!-- Load d3.js -->
                <script src="https://d3js.org/d3.v4.js"></script>
            </head>
            <body>
                <!-- Create a div where the graph will take place -->
                <div id="my_dataviz"></div>

                <script>

                    // set the dimensions and margins of the graph
                    var margin = {top: 70, right: 30, bottom: 80, left: 50},
                        width = window.innerWidth - margin.left - margin.right,
                        height = window.innerHeight - margin.top - margin.bottom;
                    
                    // append the svg object to the body of the page
                    var svg = d3.select("#my_dataviz")
                      .append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .style("overflow", "visible")
                        .append("g")
                        .attr("transform",
                              "translate(" + margin.left + "," + margin.top + ")");
                    var arrCumDistance=JSON.parse('$arrCumDistance');
                   
                    var arrElevation=JSON.parse('$arrElevation');
                   
                    var iterations = 0;
                    if(arrElevation.length<arrCumDistance.length||arrElevation.length==arrCumDistance.length){
                        iterations = arrElevation.length;
                    }
                    else{
                        iterations = arrCumDistance.length;
                    }
                    var data = [];
                    for(var i = 0; i < iterations; i++){
                        var obj = {};
                        obj.arrCumDistance = +arrCumDistance[i];
                        obj.arrElevation = +arrElevation[i];
                        data.push(obj);
                    }
                    
                        // Add X axis
                        var x = d3.scaleLinear()
                        .domain(d3.extent(data, function(d) { return +d.arrCumDistance; }))
                          .range([ 0, width ]);
                        svg.append("g")
                        .attr("class", "xaxis")
                          .attr("transform", "translate(0," + height + ")")
                          .call(d3.axisBottom(x).ticks(8).tickSizeOuter([0]))
                        svg.append("RLText")
                          .attr("x", width/2)
                          .attr("y", height+58)
                          .attr("font-size", 30)
                        .style("font-family", "omnes-pro, sans-serif")
                          .RLText("KM's")
                    
                        let minimum = d3.min(data, function (d) { return +d.arrElevation; });
                        let maximum = d3.max(data, function (d) { return +d.arrElevation; });
                        let difference = maximum - minimum;
                        let onepercent = difference/100;
                        let fifteenpercent = onepercent * 15;
                        let upperLimit = minimum - fifteenpercent;
                        let lowerLimit = maximum + fifteenpercent;
                        
                        // Add Y axis
                        var y = d3.scaleLinear()
                          .domain([upperLimit, lowerLimit])
                          .range([ height, 0 ]);
                        svg.append("g")
                        .attr("class", "yaxis")
                          .call(d3.axisLeft(y).ticks(9).tickSizeInner([-width]).tickSizeOuter([0]));
                        svg.append("RLText")
                            .attr("y", -5)

                            .attr("x", -31)
                            .attr("font-size", 30)
            .style("font-family", "omnes-pro, sans-serif")
                            .RLText("METERS");
                        // Add the area
                        svg.append("path")
                          .datum(data)
                          .attr("fill", "lightgreen")
                          .attr("fill-opacity", 0.5)
                          .attr("stroke", "#69b3a2")
                          .attr("stroke-width", 0.5)
                          .attr("d", d3.area()
                            .x(function(d) { return x(d.arrCumDistance) })
                            .y0(y(d3.min(data, function(d) { return +d.arrElevation - fifteenpercent ; })))
                            .y1(function(d) { return y(d.arrElevation) })
                            )
                    
                    </script>
            </body>
            </html>
        """.trimIndent()
    }
    fun RLgetSpeedHtml(arrCumDistance:String, arrElevation:String, arrCumSpeed:String): String {
        return """
                    <!DOCTYPE html>
                    <head>
                      <meta charset="utf-8">
                      <title>Multi Area Chart</title>
                      <script src="https://d3js.org/d3.v4.min.js"></script>
                      <style>
                        body { margin:0;position:fixed;top:0;right:0;bottom:0;left:0; }
                      </style>
                    </head>

                    <body>
                      <script>
                       var arrCumDistance= JSON.parse('$arrCumDistance');
                       var arrElevation=JSON.parse('$arrElevation');
                       var arrCumSpeed=JSON.parse('$arrCumSpeed');
                       
                        var iterations = 0;
                            console.log(arrCumDistance.length)
                            console.log(arrElevation.length)
                                        if(arrElevation.length<arrCumDistance.length||arrElevation.length==arrCumDistance.length){
                                            iterations = arrElevation.length;
                                        }
                                        else{
                                            iterations = arrCumDistance.length;
                                        }
                                        var data = [];
                                        for(var i = 0; i < iterations; i++){
                                            var obj = {};
                                            obj.arrCumDistance = +arrCumDistance[i];
                                            obj.arrCumSpeed = +arrCumSpeed[i];
                                            obj.arrElevation = +arrElevation[i];
                                            data.push(obj);
                                        }
                                        data.columns = d3.keys(data[0]);
                                        console.log(data)

                                const margin = { left: 55, right: 0, top: 35, bottom: 75 };
                                const width = window.innerWidth-50;
                                const height = window.innerHeight - margin.top - margin.bottom;
                                var svg = d3.select('body').append('svg').attr('width', width).attr('height', height).style("overflow", "visible")

                                const xScale = d3.scaleLinear().range([ margin.left, width - margin.right, ]).domain(d3.extent(data, stock => stock.arrCumDistance));
                                let minimum = d3.min(data, function (d) { return +d.arrElevation; });
                                  let maximum = d3.max(data, function (d) { return +d.arrElevation; });
                                  let difference = maximum - minimum;
                                  let onepercent = difference / 100;
                                  let fifteenpercent = onepercent * 15;
                                  let upperLimit = minimum - fifteenpercent;
                                  let lowerLimit = maximum + fifteenpercent;
                                  let heightBottom = height - margin.bottom;

                                const y1Scale = d3.scaleLinear().range([ height - margin.bottom, margin.top, ]).domain([ upperLimit, lowerLimit]);
                                const y2Scale = d3.scaleLinear().range([ height - margin.bottom, margin.top, ]).domain([ d3.min(data, stock => stock.arrCumSpeed), d3.max(data, stock => stock.arrCumSpeed)]);
                                const xAxis = svg.append('g').attr("class", "axis").style("font-size", 34).attr('transform', `translate(0,`+ heightBottom +`)`).call(d3.axisBottom(xScale).ticks(7).tickSizeOuter([0]).tickSizeInner([0]).tickPadding(10));
                                const elivationArea = d3.area().x (d => xScale(d.arrCumDistance)).y0(d => height - margin.bottom).y1(d => y1Scale(d.arrElevation));
                                const speedArea = d3.area().x (d => xScale(d.arrCumDistance)).y0(d => height - margin.bottom).y1(d => y2Scale(d.arrCumSpeed)).curve(d3.curveBasis);
                                const speedLine = d3.line().x(d => xScale(d.arrCumDistance)).y(d => y2Scale(d.arrCumSpeed));
                                  
                                svg.append('path').data([data]).attr('d', elivationArea).attr('fill', 'rgb(228, 235, 230)').attr('opacity', 0.6);
                                    //svg.append('path').attr('d', speedLine(data)).attr('stroke', 'rgba(0, 188, 212, 0.47)').attr('stroke-width', 3).attr('fill', 'none');
                                    svg.append('path').data([data]).attr('d', speedArea).attr('fill', 'rgba(0, 188, 212, 0.47)').attr('opacity', 0.6);
                                    svg.append("RLText").attr("x", width/2).attr("y", height - 5 ).style("font-size", 34).style("font-family", "omnes-pro, sans-serif").RLText("KM's");
                                    
                                const y1Axis = svg.append('g').attr("class", "axis").style("font-size", 34).attr('transform', `translate(`+ margin.left +`, 0)`).call(d3.axisLeft(y2Scale).ticks(5).tickSizeOuter([0]).tickSizeInner([0]).tickPadding(10)).append("RLText")
                                  .attr("x", 40).attr("y", 1).attr("dy", "0.71em").attr("fill", "#000").style("font-size", 34).style("font-family", "omnes-pro, sans-serif").RLText("KMH");
                                
                              </script>
                            </body>

                        """.trimIndent()
    }
    fun RLgetPaceChartHtml(jsondata: String) : String{

        return """
           <!DOCTYPE html>
                <html>
                            <head>
                                <script src="https://d3js.org/d3.v4.min.js"></script>
                                <style>body {
                                        margin: 0
                                    }
                                    .domain {
                                        display: none
                                    }

                                    .tick line {
                                        stroke: #c0c0bb
                                    }

                                    .tick RLText {
                                        fill: #8e8883;
                                        font-size: 32pt;
                                        font-family: sans-serif
                                    }
                                    .axis-label {
                                        fill: #635f5d;
                                        font-size: 32pt;
                                        font-family: sans-serif
                                    }
                                    </style>
                            </head>
                            <body>
                                <div id="chart"></div>
                            <script>
                            
                    

                //Here is the time in seconds
                  var timeData = JSON.parse('$jsondata');
               
                var arrCumDistance = [];

                                timeData.forEach((d,i)=>{
                                  arrCumDistance.push(i);
                                })

                                var distanceData = arrCumDistance;
                                        distanceData.sort(function(a, b){return a - b});
                                        var barData = [];
                                        var total = 0;
                                        distanceData.forEach(function(d){
                                            var obj = {}
                                            obj.arrCumDistance = Math.trunc(d);
                                            barData.push(obj)
                                        })
                                        var buckets = [...new Set(barData.map(d => d.arrCumDistance))];
                                        
                                        
                                        var finalBarData = [];
                                        buckets.forEach(function(b, i){
                                            var fdata = barData.filter(function(d){ return d.arrCumDistance == b;})
                                            var obj = {};
                                            obj.arrCumDistance = +b+1;
                                            obj.time = timeData[i];
                                            finalBarData.push(obj)
                                        })

                                      var compkilometers = [];
                                      for(i=0;i<finalBarData.length-1;i++){
                                        compkilometers.push(finalBarData[i])
                                      }
                                      
                                      var mintimeseconds = d3.min(compkilometers, function(d){ return d.time;})
                                      const noOfBars = buckets.length;
                                      const xValue = d => d.time;;
                                      const yValue = d => +d.arrCumDistance;
                                      const margin = { left: 50, right: 140, top: 35, bottom: 75 };
                                      const barHeight = 60;
                                      const width = innerWidth;
                                      const height = barHeight*noOfBars+200;
                                      const innerWidthA = width - margin.left - margin.right;
                                      const innerHeight = height - margin.top - margin.bottom;
                                      const svg = d3.select("#chart").append("svg").attr("height", height).attr("width", width);

                                   
                                   const g = svg.append("g")
                                  .attr("transform", `translate(`+ margin.left +`,`+  margin.top +`)`);
                                  const xAxisG = g.append("g")
                                      .attr("transform", `translate(0,` + innerHeight + `)`);
                                       
                                      const yAxisG = g.append("g");

                                      const xScale = d3.scaleLinear();
                                      const yScale = d3.scaleBand()
                                        .paddingInner(0.3)
                                        .paddingOuter(0);

                                      const xTicks = 10;
                                      const xAxis = d3.axisBottom()
                                        .scale(xScale)
                                        .ticks(xTicks)
                                        .tickPadding(5)
                                        .tickSize(-innerHeight);

                                      const yAxis = d3.axisLeft()
                                        .scale(yScale)
                                        .tickPadding(5)
                                        .tickSize(-innerWidthA);

                                        var data = finalBarData;
                                      console.log(data);
                                        yScale
                                          .domain(data.map(yValue).reverse())
                                          .range([innerHeight, 0]);

                                        xScale
                                          .domain([0, d3.max(data, xValue)])
                                          .range([0, innerWidthA])
                                          .nice(xTicks);

                                        var bars = g.selectAll("rect").data(data)
                                          .enter().append("rect")
                                            .attr("x", 0)
                                            .attr("y", d => yScale(yValue(d)))
                                            .attr("width", d => xScale(xValue(d)))
                                            .attr("height", barHeight)
                                            .attr("rx", 12)
                                            .attr("fill", "#39B54A");
                                        
                                        xAxisG.call(xAxis);

                                        yAxisG.call(yAxis);
                                        yAxisG.selectAll(".tick line").remove();
                                        xAxisG.selectAll(".tick RLText").remove();

                                        function toTimeString(seconds) {
                                            return getReadableTime(seconds);
                                        }
                                        var timeAxisData = finalBarData;
                                        timeAxisData.forEach(function(t){
                                            t.time = toTimeString(t.time);
                                        })
                                        
                                        g.selectAll("label").data(timeAxisData)
                                          .enter().append("RLText")
                                            .attr("class", "label")
                                            .attr("y", d => yScale(yValue(d))+40)
                                            .attr("x", +innerWidthA+15)
                                            .attr("font-size", 32)
                                            .attr("font-family", 'sans-serif')
                                            .attr("fill", "#8E8883")
                                            .RLText(function (d) {
                                                return d.time;
                                            });
                                      var totaltimeofcompkilo = 0;
                                      for(i=0;i<buckets.length-1;i++){
                                        var filterdata = arrCumDistance.filter(function(d){ return Math.trunc(d)==i; });
                                        totaltimeofcompkilo = totaltimeofcompkilo + filterdata.length;
                                      }
                                      
                                      var avgtimeseconds = totaltimeofcompkilo/(compkilometers.length);
                                      var mintime = toTimeString(mintimeseconds);
                                      var avgtime = toTimeString(avgtimeseconds);
                                      
                                      
                                         function getReadableTime(sec) {
                                            var hrs = Math.floor(sec / 3600);
                                            var min = Math.floor((sec - (hrs * 3600)) / 60);
                                            var seconds = sec - (hrs * 3600) - (min * 60);
                                            seconds = Math.round(seconds * 100) / 100
                                           
                                           var result = "";
                                            if(hrs > 0){
                                                result += (hrs < 10 ? hrs : hrs) + ":";
                                            }
                                            result += (min < 10 ? "0" + min : min);
                                            result += ":" + (seconds < 10 ? "0" + seconds : seconds);
                                            return result;
                                         }
                                      </script>
                                </body>
                                </html>  
        """.trimIndent()
    }
    fun RLgetChallengeChartHtml (steps_so_far:Int,target_steps:Int,time_gone:Int,total_time:Int) : String{
        return """
         <!DOCTYPE html>
         <html>
         <head>
             <meta charset="utf-8">
             <title>Progress</title>
             <style>
                 body { margin:0; position:fixed; top:0; right:0; bottom:0; left:0; }
                 .yaxis path { stroke: none; }
                 .yaxis line { stroke: none; }
                 text { fill: #aaa; font-size: 40px; font-family: sans-serif; }
                 .yaxis text { fill: #aaa; font-size: 50px; font-family: sans-serif; }
             </style>
             <!-- Load d3.js -->
             <script src="https://d3js.org/d3.v4.js"></script>
         </head>
         <body>
         <script>
             let steps_so_far = '$steps_so_far'; //3364 number of steps taken
             let target_steps = '$target_steps'; // 3333 total number of steps
             let time_gone =' $time_gone'; //0 number of days passed away
             let total_time = '$total_time'; //1total number of days for completing all steps
             
             if(steps_so_far > target_steps) {
                 steps_so_far = target_steps;
             }
             if(time_gone > total_time || time_gone === 0.0) {
                 time_gone = total_time;
             } else {
                 time_gone = (total_time - time_gone);
             }
             
             const margin = { left: 35, right: 0, top: 35, bottom: 35 };
             const chart_width = window.innerWidth;
             const chart_height = window.innerHeight;
             const width = (chart_width / 100) * 20; // Width of the progress bar
             const height = chart_height - margin.top - margin.bottom; // Height of the progress bar

             // Create an SVG container
             const svg = d3.select('body').append('svg')
                 .attr("width", chart_width)
                 .attr("height", chart_height);

             let x_mid = chart_width / 2 - (width / 2);
             let shift_percentage = 45; // Increased percentage for more separation
             let step_x = x_mid - ((x_mid / 100) * shift_percentage);
             let time_x = x_mid + ((x_mid / 100) * shift_percentage);

             var y = d3.scaleLinear().range([height, 0]);
             var yAxis = d3.axisRight(y).ticks(3).tickValues([25, 50, 75]).tickFormat(d => d + "%");
             y.domain([0, 100]);

             svg.append("g").attr("transform", "translate(" + (x_mid + 60) + "," + 50 + ")")
                 .attr("class", "yaxis").call(yAxis);

             // Adjust text positions
             svg.append("text")
                 .attr("x", step_x + width / 2)
                 .attr("y", 40)
                 .attr("text-anchor", "middle")
                 .text("STEPS");

             svg.append("text")
                 .attr("x", time_x + width / 2)
                 .attr("y", 40)
                 .attr("text-anchor", "middle")
                 .text("TIME");

             const progressBar = svg.append("g").attr("transform", "translate(" + step_x + "," + 50 + ")");
             progressBar.append("rect")
                 .attr("width", width)
                 .attr("height", height)
                 .attr("rx", 12)
                 .attr("fill", "#ccc");

             progressBar.append("rect")
                 .data([steps_so_far / target_steps])
                 .attr("width", width)
                 .attr("height", 0)
                 .attr("y", height)
                 .attr("rx", 12)
                 .attr("fill", "#4CAF50")
                 .transition()
                 .duration(1000)
                 .attr("height", d => height * d)
                 .attr("y", d => height * (1 - d));

             const progressBar1 = svg.append("g").attr("transform", "translate(" + time_x + "," + 50 + ")");
             progressBar1.append("rect")
                 .attr("width", width)
                 .attr("height", height)
                 .attr("rx", 12)
                 .attr("fill", "#ccc");

             progressBar1.append("rect")
                 .data([time_gone / total_time])
                 .attr("width", width)
                 .attr("height", 0)
                 .attr("y", height)
                 .attr("rx", 12)
                 .attr("fill", "#118def")
                 .transition()
                 .duration(1000)
                 .attr("height", d => height * d)
                 .attr("y", d => height * (1 - d));
         </script>
         </body>
         </html>

        """.trimIndent()
    }
    fun RLgetChallengeSessionChartHtml (steps_so_far:Int,target_steps:Int) : String{
        return """
       
       <!DOCTYPE html>
       <html>
       <head>
           <meta charset="utf-8">
           <title>Progress</title>
           <style>
               body {
                   margin: 0;
                   position: fixed;
                   top: 0;
                   right: 0;
                   bottom: 0;
                   left: 0;
               }
               text {
                   fill: #aaa;
                   font-size: 40px;
                   font-family: sans-serif;
               }
           </style>
           <!-- Load d3.js -->
           <script src="https://d3js.org/d3.v4.js"></script>
       </head>
       <body>
           <script>
               let steps_so_far = '$steps_so_far'; // \(achieved);  // Number of steps taken
               let target_steps ='$target_steps';  // \(target);  // Total number of steps target

              const margin = { left: 35, right: 35, top: 50, bottom: 35 };
                      const chart_width = window.innerWidth - margin.left - margin.right;
                      const chart_height = window.innerHeight - margin.top - margin.bottom;
                      const bar_width = 250;  // Width of each progress bar
                      const gap = 100;  // Gap between bars

                      // Create an SVG container
                      const svg = d3.select('body').append('svg')
                          .attr("width", window.innerWidth)
                          .attr("height", window.innerHeight);

                      // Calculate bar positions
                      let x_firstBar = (window.innerWidth / 2) - bar_width - (gap / 2);
                      let x_secondBar = (window.innerWidth / 2) + (gap / 2);

                      // Scale for the bars
                      var y = d3.scaleLinear()
                          .range([chart_height, 0])
                          .domain([0, Math.max(steps_so_far, target_steps)]);

                      // First Bar: Steps Achieved
                      const achievedBar = svg.append("g")
                          .attr("transform", `translate(`+ x_firstBar +`, `+margin.top+`)`);
                      achievedBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", chart_height)
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#ccc");  // Background color

                      achievedBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", y(0) - y(steps_so_far))
                          .attr("y", y(steps_so_far))
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#4CAF50");  // Fill color

                      achievedBar.append("text")
                          .attr("x", bar_width / 2)
                          .attr("y", -20)
                          .attr("text-anchor", "middle")
                          .text("ACHIEVED");

                      // Second Bar: Target Steps
                      const targetBar = svg.append("g")
                          .attr("transform", `translate(` + x_secondBar + `, `+ margin.top +`)`);
                      targetBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", chart_height)
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#ccc");  // Background color

                      targetBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", y(0) - y(target_steps))
                          .attr("y", y(target_steps))
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#118def");  // Fill color

                      targetBar.append("text")
                          .attr("x", bar_width / 2)
                          .attr("y", -20)
                          .attr("text-anchor", "middle")
                          .text("TARGET");
                  </script>
              </body>
              </html>
        """.trimIndent()
    }
    fun RLgetRankingChartHtml(jsondata: JSONArray,userid:String): String{
        return """     
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>Bar</title>
                <style>
                    body {
                        margin: 0;
                        top: 0;
                        right: 0;
                        bottom: 0;
                        left: 0;
                    }
                    .image {
                        stroke: white;
                        border-width: 1px;
                        border-radius: 50%;
                        overflow: hidden;
                    }
                    .x-axis text{
                        fill: none;
                        font-family: "Omnes";
                    }
                    .x-axis path{
                        stroke: none;
                    }

                    .x-axis line{
                        stroke: white;
                    }
                </style>
                <!-- Load d3.js -->
                <script src="https://d3js.org/d3.v7.js"></script>
            </head>

            <body>
                <script>
                    

          
         
                
               // Define your data (sample data)
                       var data = $jsondata;
                       
                       
            		var bar_color = "#ebf7ed";
                    var percentage_label_color = "#4a4c4f";
                    var user_name_label_color = "#4a4c4f"; // Change user_name_label_color to white
                    var name_label_color = "#4a4c4f";
                    var index_label_color = "black";
                    var name_font_size = "45px";
                    var percentage_font_size = "45px";
                    var index_font_size = "50px";
                    var font_family = "Omnes, sans-serif";
                    var barHeight = 65;
                    var user_name ='$userid';
                    var font_vertical_pos = 13;

              
                   	 // Sort data by value in descending order
                   data.sort((a, b) => b.value - a.value);




                    // Find the index of the user's bar
                    const userIndex = data.findIndex(d => d.userid === user_name);

                    // Determine which bars to display
                    let displayData=data;
                    //if (userIndex <= userIndex) {
                      //  displayData = data;
            			
            //			.slice(0, userIndex);
                    //} else {
                      //  displayData = data.slice(0, 14).concat(data[userIndex]);
                    //}
            		// Now, sort displayData in ascending order based on the value
            //displayData.sort((a, b) => a.value - b.value);
            	
                    const svgWidth = (window.innerWidth / 100) * 90;
                    //const svgHeight = data.length * 90;
            		const svgHeight = data.length > 1 ? data.length * 90 : 180; // Adjust overall SVG height if only one bar
                    const margin = {top: 20, right: 80, bottom: 40, left: 120};
                    const width = svgWidth - margin.left - margin.right;
                    const height = svgHeight - margin.top - margin.bottom;

                    const svg = d3.select("body")
                            .append("svg")
                            .attr("width", svgWidth)
                            .attr("height", svgHeight)
                            .style("overflow", "visible")
                            .append("g")
                            .attr("transform", `translate(`+ margin.left +`,`+ margin.top +`)`);
            		

                    
                    // Set the ranges
                 //   const x = d3.scaleLinear().range([0, width]);
                 //   const y = d3.scaleBand().range([height, 0]).padding(0.1);
            			
            				        // Create scales
                    const xScale = d3.scaleLinear()
                            .domain([0, d3.max(data, d => d.value)])
                            .range([0, width]);

                    const yScale = d3.scaleBand()
                            .domain(displayData.map(d => d.name))
                            .range([0, height])
                            .padding(0.25);

                    // Add axes if needed
                    const xAxis = d3.axisBottom(xScale).tickSize(-height);
                    svg.append("g")
                            .attr("class", "x-axis")
                            .attr("transform", `translate(0, `+ height +`)`)
                            .call(xAxis);

                    const yAxis = d3.axisLeft(yScale);
                    svg.append("g")
                            .attr("class", "y-axis")
                            .style("display", "none")
                            .call(yAxis);

                    // Create bars
                    svg.selectAll(".gap")
                            .data(displayData)
                            .enter()
                            .append("rect")
                            .attr("class", "gap")
                            .attr("x", 10)
                            .attr("y", d => yScale(d.name))
                            .attr("width", d => xScale(d.value))
                            .attr("height", yScale.bandwidth())
                            .attr("rx", 12)
                            //.style("fill", d => d.userid.startsWith(user_name) ? "#39b54a" : bar_color); // Dark blue color for user_name, bar_color for others
            				 .style("fill", (d, i) => i === userIndex ? "#39b54a" : bar_color); // Use index to determine fill color
                    svg
                            .append("defs")
                            .selectAll("pattern")
                            .data(displayData)
                            .enter()
                            .append("pattern")
                            .attr("id", (d, i) => "image-pattern-" + i)
                            .attr("width", 1) // Adjust the width of the pattern as needed
                            .attr("height", 1) // Adjust the height of the pattern as needed
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("patternContentUnits", "objectBoundingBox")
                            .append("image")
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("width", 1)
                            .attr("height", 1)
                            .attr("xlink:href", (d) => d.image)
                            .attr("preserveAspectRatio", "xMinYMin slice");

                    svg.selectAll(".circle")
                            .data(displayData)
                            .enter()
                            .append("circle")
                            .attr("class", "circle")
                            .attr("cx", -margin.left + 60)
                            .attr("cy", d => yScale(d.name) + yScale.bandwidth() / 2 + 2)
                            .attr("r", yScale.bandwidth() / 2) //
                            .style("fill", (d, i) => `url(#image-pattern-`+ i+`)`);
            				
            				
            				
            				// This code goes after the section where you create the bars.

            if (userIndex > userIndex) {
                // Calculate the y-position of the 14th bar. Since bars are drawn using yScale,
                // you can use yScale to find the y-position of the 14th item's name.
                // We add half of the bandwidth to position the line below the 14th bar.
                const yPositionOf14thBar = yScale(displayData[13].name) + yScale.bandwidth();

                // Draw a horizontal line at the y-position of the 14th bar
                svg.append("line")
                    .style("stroke", "black") // Color of the line
                    .style("stroke-width", 4) // Thickness of the line
                     .style("stroke-dasharray", "10,10") // Make the line dashed: 10 pixels dash, 10 pixels gap
            		.attr("x1", 0) // Starting x-position of the line
                    .attr("y1", yPositionOf14thBar+25) // Starting y-position of the line (same as ending y-position)
                    .attr("x2", width) // Ending x-position of the line
                    .attr("y2", yPositionOf14thBar+25); // Ending y-position of the line
            }

            				
            				
            				
            // metric values at right hand end of bars
               svg.selectAll(".label3")
                            .data(displayData)
                            .enter()
                            .append("text")
                            .attr("class", "label")
                            .attr("x", d => width - 10) // Adjust the position to right-align the text
                            .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4) // Center the text vertically
                            .style("fill", (d) => {
                                if (d.userid.startsWith(user_name)) {
                                    return user_name_label_color;
                                } else {
                                    return name_label_color;
                                }
                            })
                            .style("font-size", name_font_size)
                            .style("font-family", font_family)
                            .style("text-anchor", "end") // Right-align the text
                            .text(d => d.number2);

            // right hand side % labels

               svg.selectAll(".label4")
                            .data(displayData)
                            .enter()
                            .append("text")
                            .attr("class", "label")
                            .attr("x", width + 150) // Adjust the position to right-align the text
                            .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4)
                            .style("fill", percentage_label_color)
                            .style("font-size", percentage_font_size)
                            .style("font-family", font_family)
                            .style("text-anchor", "end") // Right-align the text
                            .text(d => d.value + "%");

            // Find the index of the user in the data array
            var userRowIndex = (data.findIndex(d => d.userid === user_name)+1);
            	


            // labels in bars
            // Find the first index where the value is 0 to adjust ranking shown for users with value 0
            const firstZeroValueIndex = data.findIndex(d => d.value === 0) + 1; // Adding 1 for 1-based indexing


            // Determine the lowest ranking for users with a value of 0 among the bars
            let ranksWithZeroValue = data
              .map((d, i) => ({ value: d.value, rank: i + 1 }))
              .filter(d => d.value === 0 && d.rank <= userRowIndex)
              .map(d => d.rank);

            let lowestRankForZeroValue = Math.min(...ranksWithZeroValue, data.length + 1); // Use a fallback if no 0 values


            // labels in bars
            svg.selectAll(".label1")
                .data(displayData)
                .enter()
                .append("text")
                .attr("class", "label")
                .attr("x", d => 15)  // Position the label slightly right of the y-axis
                .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4)  // Vertically center the text in the bar
                .style("fill", (d, i) => (i === userIndex && d.value === 0) ? "#39b54a" : name_label_color)  // Change font color to blue if bar length is zero and it's the userIndex
                .style("font-size", name_font_size)
                .style("font-family", font_family)
                .text(d => {
                    // Determine the ranking directly based on displayData for simplicity and clarity
                    let ranking = displayData.findIndex(x => x.userid === d.userid) + 1;
                    return ``+ ranking +` - `+  d.name.split(" - ")[1]+``;  // Display ranking and name
                });
			   
                </script>
            </body>
            </html>
        """.trimIndent()
    }
    fun RLgetIndividualStepsChartHtml(jsondata: JSONArray): String{
        return """     
           <!DOCTYPE html>
           <html>
           <head>
             <script src="https://d3js.org/d3.v4.min.js"></script>
             <style>
               body {
                 font: 25px sans-serif;
               }
               svg {
                 font-family: Sans-Serif, Arial;
                 padding-bottom: 135px; /* Add padding-bottom to create space for x-axis labels and values */
               }
               .axis path,
               .axis line {
                 stroke: black;
               }
               .bar {
                 fill: #39B54A; /* Green color for the bars */
               }
               .tick line {
                 stroke-opacity: 0.99;
               }
             </style>
           </head>
           <body>
           <div id="chart"></div>
           <script>
               var array = [

                 {
                   name: "Challenge Metric",
                   values: $jsondata
                 }
               
           ];

           var challengeData = array.find(entry => entry.name === "Challenge Metric");
           createGraph(challengeData);

           function createGraph(data) {
             var margin = { top: 20, right: 20, bottom: 85, left: 100 },
                 barWidth = 56,
                 width = data.values.length * barWidth + margin.left + margin.right, // Adjust the overall width based on the number of bars
                 height = 600,
                 gapWidth = barWidth / 2; // Adjust gapWidth to ensure the first tick starts under the middle of the first bar

             /* Format Data */
             var parseDate = d3.timeParse("%d/%m/%Y");
             data.values.forEach(function(d) {
               d.date = parseDate(d.date);
               d.value = +d.value;
             });

             /* Scales */
             var xScale = d3.scaleBand()
                            .rangeRound([margin.left, width - margin.right])
                            .paddingInner(0.1)
                            .domain(data.values.map(d => d.date));

             var yScale = d3.scaleLinear()
                            .domain([0, d3.max(data.values, d => d.value)])
                            .range([height - margin.bottom, margin.top]);

             var svg = d3.select("#chart").append("svg")
                         .attr("width", width)
                         .attr("height", height);

             /* Add Axes */
             var xAxis = d3.axisBottom(xScale).tickFormat(d3.timeFormat("%d %b")),
                 yAxis = d3.axisLeft(yScale).ticks(10);
                var heightminusmarginbottom=height - margin.bottom
             svg.append("g")
                .attr("transform", `translate(0,`+heightminusmarginbottom+`)`)
                .call(xAxis)
                .selectAll("text")
                  .style("text-anchor", "end")
                  .attr("dx", "-.8em")
                  .attr("dy", ".15em")
           	   .style("font-size", 18)
                  .attr("transform", "rotate(-65)");

             svg.append("g")
                .attr("transform", `translate(`+ margin.left +`,0)`)
                .call(yAxis)
           	   .style("font-size", 18);

            /* Draw Bars with Rounded Tops Only */
           svg.selectAll(".bar")
              .data(data.values)
              .enter().append("path")
                .attr("d", function(d) {
                  const x = xScale(d.date) + gapWidth - 28; // Adjust the starting x position
                  const y = yScale(d.value);
                  const barHeight = height - margin.bottom - yScale(d.value);
                  const barWidth = xScale.bandwidth();
                   const ybarheight=y + barHeight
                    const barheightminusten= barHeight-10
                    const barwidthminustwenty= barWidth-20
                  
                  // Move to the bottom left, draw line up to the start of top left curve,
                  // arc for the top left corner, line across the top, arc for the top right corner,
                  // then line down the right side and close the path.
               
                 return `M`+x+`,`+ybarheight+` ` + // Move to bottom left
                    `v-`+ barheightminusten +` ` +   // Line up to start of top left curve
                    `q0,-10 10,-10 ` +         // Top left corner curve
                    `h`+barwidthminustwenty+` ` +     // Line across the top
                    `q10,0 10,10 ` +           // Top right corner curve
                    `v`+ barheightminusten +` ` +    // Line down the right side
                    `h-`+barWidth+`z`;          // Close path
                    })
                    .attr("fill", "#ebf7ed");


               /* Adjust label positions if you have them, assuming here how you might add them */
           	svg.selectAll(".bar-label").data(data.values).enter().append("text")
                .attr("class", "bar-label")
                .attr("x", d => xScale(d.date) + 26) // Adjusted for 40px offset
                .attr("y", d => yScale(d.value) - 5) // Example position above the bar
                .attr("text-anchor", "middle")
           	 .text(d => d3.format(",")(d.value)) // Format the value with commas
              	 .style("font-size", 18)
                .style("fill", "black");
         
           }
           </script>
           </body>
           </html>

        """.trimIndent()

    }
    fun RLGetNewZoneChartHtml(): String{
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Stacked Effort Zone Bar Chart</title>
                <script src="https://d3js.org/d3.v6.min.js"></script>
                <style>
                    body { font: 12px Arial; background-color: #FFFFFF; }
                    .axis { font-size:40px }
                    .axis path, .axis line { fill: none; stroke: black; shape-rendering: crispEdges; }
                </style>
            </head>
            <body>
            <div id="chart"></div>
            <script>
            document.addEventListener("DOMContentLoaded", function() {
                var data = [
                    { zone: "Zone1", value: 1829, color: "rgb(241, 119, 160)" },
                    { zone: "Zone2", value: 2345, color: "rgb(255, 207, 47)" },
                    { zone: "Zone3", value: 1230, color: "rgb(44, 174, 44)" },
                    { zone: "Zone4", value: 2310, color: "rgb(0, 153, 218)" },
                    { zone: "Zone5", value: 560, color: "rgb(254, 105, 02)" },
                    { zone: "Zone6", value: 680, color: "rgb(153, 0, 204)" },
                    { zone: "Zone7", value: 120, color: "rgb(237, 69, 65)" }
                ];

                var margin = { top: 20, right: 20, bottom: 40, left: 30 }, // Reduced left margin
                width = window.innerWidth - margin.left - margin.right,
                height = window.innerHeight - margin.top - margin.bottom;

                var svg = d3.select("#chart").append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    var g = svg.append("g")
                    .attr("transform", "translate(" + (width/2 - 120.0) + "," + margin.top + ")");

                var barWidth = 240; // Set the bar width to 120 pixels

                var y = d3.scaleLinear()
                    .range([height, 0])
                    .domain([0, d3.sum(data, function(d) { return d.value; }) / 60]); // Convert total seconds to minutes

                var x = d3.scaleBand()
                    .range([0, barWidth]) // Set the range for the single bar width
                    .padding(0.1);

                var runningTotal = 0;
                var bar = g.selectAll(".bar")
                    .data(data)
                    .enter().append("rect")
                    .attr("class", "bar")
                    .attr("x", 10) // Set a smaller x value to reduce the gap
                    .attr("width", barWidth)
                    .attr("y", function(d) { var yVal = y(runningTotal + d.value / 60); runningTotal += d.value / 60; return yVal; })
                    .attr("height", function(d) { return height - y(d.value / 60); })        
                    .attr("fill", function(d) { return d.color; });
                    

                    // Add foreignObject
                 const foreignObject = g.append("foreignObject")
                     .attr("width", barWidth+13)
                     .attr("height", height)
                     .style("zIndex", 1)
                     .attr("x", 4)
                     .attr("y", -6);

                 // Add div inside foreignObject
                 const foreignDiv = foreignObject.append("xhtml:div")
                     .style("width", "94%")
                     .style("height", "100%")
                     .style("fill", "none")
                     .style("zIndex", 1)
                     .style("border", "10px solid white")
                     .style("border-top-right-radius", "20px")
                     .style("border-top-left-radius", "20px");

                // Add the Y Axis with exactly 5 ticks
                var yAxis = d3.axisLeft(y).ticks(5).tickFormat(function(d) {
                     return d === 0 ? "Mins" : d; // Replace the 0 with 'Mins'
                });
                g.append("g")
                    .attr("class", "axis")
                    .call(yAxis);
            });
            </script>
            </body>
            </html>

        """.trimIndent()
    }

    fun RLGetNewZoneChartHtml1(jsonArray: JSONArray): String{
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Stacked Effort Zone Bar Chart</title>
                <script src="https://d3js.org/d3.v6.min.js"></script>
                <style>
                    body { font: 12px Arial; background-color: #FFFFFF; }
                    .axis { font-size:40px }
                    .axis path, .axis line { fill: none; stroke: black; shape-rendering: crispEdges; }
                </style>
            </head>
            <body>
            <div id="chart"></div>
            <script>
            document.addEventListener("DOMContentLoaded", function() {
                var data = $jsonArray

                var margin = { top: 20, right: 20, bottom: 40, left: 30 }, // Reduced left margin
                width = window.innerWidth - margin.left - margin.right,
                height = window.innerHeight - margin.top - margin.bottom;

                var svg = d3.select("#chart").append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    var g = svg.append("g")
                    .attr("transform", "translate(" + (width/2 - 120.0) + "," + margin.top + ")");

                var barWidth = 240; // Set the bar width to 120 pixels

                var y = d3.scaleLinear()
                    .range([height, 0])
                    .domain([0, d3.sum(data, function(d) { return d.value; }) / 60]); // Convert total seconds to minutes

                var x = d3.scaleBand()
                    .range([0, barWidth]) // Set the range for the single bar width
                    .padding(0.1);

                var runningTotal = 0;
                var bar = g.selectAll(".bar")
                    .data(data)
                    .enter().append("rect")
                    .attr("class", "bar")
                    .attr("x", 10) // Set a smaller x value to reduce the gap
                    .attr("width", barWidth)
                    .attr("y", function(d) { var yVal = y(runningTotal + d.value / 60); runningTotal += d.value / 60; return yVal; })
                    .attr("height", function(d) { return height - y(d.value / 60); })        
                    .attr("fill", function(d) { return d.color; });
                    

                    // Add foreignObject
                 const foreignObject = g.append("foreignObject")
                     .attr("width", barWidth+13)
                     .attr("height", height)
                     .style("zIndex", 1)
                     .attr("x", 4)
                     .attr("y", -6);

                 // Add div inside foreignObject
                 const foreignDiv = foreignObject.append("xhtml:div")
                     .style("width", "94%")
                     .style("height", "100%")
                     .style("fill", "none")
                     .style("zIndex", 1)
                     .style("border", "10px solid white")
                     .style("border-top-right-radius", "20px")
                     .style("border-top-left-radius", "20px");

                // Add the Y Axis with exactly 5 ticks
                var yAxis = d3.axisLeft(y).ticks(5).tickFormat(function(d) {
                     return d === 0 ? "Mins" : d; // Replace the 0 with 'Mins'
                });
                g.append("g")
                    .attr("class", "axis")
                    .call(yAxis);
            });
            </script>
            </body>
            </html>

        """.trimIndent()
    }
}
