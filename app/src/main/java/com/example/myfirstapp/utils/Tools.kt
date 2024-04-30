package com.example.myfirstapp.utils

//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.bumptech.glide.request.RequestOptions
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
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Tools {
    fun setSystemBarColor(act: Activity, @ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = act.resources.getColor(color)
        }
    }

    fun nestedScrollTo(nested: NestedScrollView, targetView: View) {
        nested.post { nested.scrollTo(500, targetView.bottom) }
    }

    /*fun displayImageOriginal(ctx: Context, img: ImageView, url: String?) {
        try {
            val requestOptions: RequestOptions = RequestOptions()
                    .placeholder(R.drawable.progress_img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            //                    .error(drawable);
            Glide.with(ctx).load(url)
                    .apply(requestOptions)
                    .into(img)
        } catch (e: Exception) {
        }
    }*/

    fun copyCode(activity: Activity, mReffralCode: String?) {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Referral Code", mReffralCode)
        clipboardManager.setPrimaryClip(clipData)
        //        Toast.makeText(activity, "Code Coppied", Toast.LENGTH_SHORT).show();
    }



    fun clearFlag(context: Activity) {
        context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun setFlag(activity: Activity) {
        //diable user intraction on background
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    //for network state
    fun isNetworkAvailable(activity: Activity): Boolean {
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
    fun capitalize(name: String): String {
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

    fun dialogForNetwork(activity: Activity) {
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

    fun dialogBox(activity: Activity, msg: String?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { arg0, arg1 -> startApp(activity) }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }




    fun dpToPx(c: Context, dp: Int): Int {
        val r = c.resources
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                r.displayMetrics
            )
        )
    }

    fun dialogBoxWithoutExit(activity: Activity?, msg: String?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(
            "Ok"
        ) { arg0, arg1 -> alertDialogBuilder.create().cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun startApp(activity: Activity) {
        val i = activity.baseContext.packageManager
            .getLaunchIntentForPackage(activity.baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(i)
    }

    fun secureAppMthd(activity: Activity) {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun secureAppMthd(activity: FragmentActivity?) {
        if (activity != null) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyBoard(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    //    public static void showAlertDialog(Activity activity, String title, String msg){
    //        Alerter.create(activity)
    //                .setTitle(title)
    //                .setText(msg)
    //                .setIcon(R.mipmap.ic_launcher)
    //                .setBackgroundColorRes(R.color.colorPrimary)
    //                .show();
    //    }
    fun transparentStatusBar(activity: Activity) {
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

    fun visitPage(activity: Activity, urlString: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")
        try {
            activity.startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Log.d("pp", "no browser found" + ex.message)
        }
    }

    fun visitSite(activity: Context, url: String, user_id: String?) {
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

    fun copyClipBoard(activity: Activity, str: String?) {
        val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("coupon", str)
        clipboardManager.setPrimaryClip(clipData)
    }

    //    public static final Context getContext(){
    //        return HelperApplication.getInstance();
    //    }
    fun generateGradient(color: IntArray?): GradientDrawable {
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color)
    }

    //    new int[] {getContext().getResources().getColor(R.color.colorPrimary),
    //            getContext().getResources().getColor(R.color.colorPrimaryDup)}
    fun shareOnWhatsapp(activity: Activity, url: String?) {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
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

    fun convertData(inputDate: String?): String? {
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

    fun openPlaystore(activity: Activity, url: String?) {
        val shareOnPlaystore = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(shareOnPlaystore)
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }

    fun isConnectingToInternet(context: Context): Boolean {
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



    fun rupeeTOPaise(rupee: String): Int {
        val split = rupee.split("\\.".toRegex()).toTypedArray()
        val item1 = split[0].toInt()
        val item2 = split[1].toInt()
        return item1 * 100 + item2
    }

    fun roundOffAmount(amount: Double?): Double {
        val decimalFormat = DecimalFormat(".##")
        decimalFormat.roundingMode = RoundingMode.CEILING
        return java.lang.Double.valueOf(decimalFormat.format(amount))
    }

    fun nextActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
    }

    fun nextFinishAllActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        activity.startActivity(intent)
        activity.finish()
    }

    fun showSnackBar(activity: Activity, msg: String?, bgColor: Int, textColor: Int) {
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
    fun getDeviceId(activity: Activity): String {
        return Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
    }
    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    fun validateVehicleNumber(email: String): Boolean {
        val emailRegex = Regex( "[a-zA-z]{2}+[0-9]{2}+[a-zA-z]{2}+[0-9]{4}")
        return emailRegex.matches(email)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCalculatedMonths(): String? {
        val c: Calendar = GregorianCalendar()
        val sdfr = SimpleDateFormat("MMM yyyy")
        return sdfr.format(c.time).toString()
    }

}
