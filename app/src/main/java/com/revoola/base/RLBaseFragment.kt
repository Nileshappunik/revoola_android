package com.revoola

import android.app.AlertDialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLAssumedRev
import com.revoola.fragment.common.RLFragNoInternet
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.commonobject.RLTools.RLnextFinishAllActivity
import com.google.gson.Gson
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


open class RLBaseFragment : Fragment() {
    val TAG1: String = RLBaseFragment::class.java.simpleName


    open fun RLonBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.requireActivity().onBackPressed() }
    }

    open fun onBackPressedHandler() {
        // Call activity's onBackPressed
        requireActivity().onBackPressed() // This will trigger the default back behavior
    }

    open fun RLonDirectBackPresAct(o: ImageView) {
        super.requireActivity()!!.onBackPressed()
    }

    open fun RLonClickNoTask(o: LinearLayout) {
        o.setOnClickListener { v: View? -> }
    }

    open fun RLclickActiviy(o: View, cls: Class<*>?, finishAll: String) {
        if (finishAll == "NEXT") {
            o.setOnClickListener { RLnextActivity(cls) }
        } else if (finishAll == "ALL") {
            o.setOnClickListener { RLnextFinishAllActivity(requireActivity(), cls) }
        }
    }

    //TODO : Full Screen
    open fun RLsetStatusBarDark() {
        requireActivity()!!.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status RLText dark
        requireActivity()!!.window.statusBarColor =
            ContextCompat.getColor(requireActivity()!!, R.color.AppBlackColor)
    }

    open fun RLsetSystemBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity()!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.statusBarColor = requireActivity()!!.resources.getColor(color)
        }
    }
    fun RLgetCurrentDateTimeIsoFormatted(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime = ZonedDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            return currentDateTime.format(formatter)
        }else{
            return "0000-00-00'T'00:00:00"
        }

    }

    fun RLGetUserDetails(context: Context): RLRevoolaUsersSettingsModel? {
        val  json = RLPrefManager.RLgetSomeStringValue(activity, RLPrefManager.user_model_data,null)
        val gson = Gson()

        return if (json != null) {
            gson.fromJson(json, RLRevoolaUsersSettingsModel::class.java)
        } else {
            null
        }
    }

    open fun RLnextActivity(cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /*open fun RLopeDrawerBar(findId: View) {
        findId.setOnClickListener(View.OnClickListener {
            (activity as RLMainActivityRL).activityMainBinding.drawerLayout.openDrawer(Gravity.LEFT)
        })
    }*/

    //TODO : DataBind
    open fun RLinflateBindLayout(activity1: Class<FragmentActivity>?, inflater: LayoutInflater, layoutName: Int, container: ViewGroup?, ): Any? {
        return DataBindingUtil.inflate(inflater!!, layoutName, container, false)
    }

    val RLDIALOG_QUEST_CODE: Int = 205
    fun RLshowDialogFullscreen(): RLFragNoInternet {
        val dialog = RLFragNoInternet()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        dialog.show(ft, TAG1)

        return dialog
    }


   /* open fun RLsessionOut() {
        ShowProgressDialog(requireActivity())
        Toast.makeText(activity, "Session Expire", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            PrefManager.clear_all(activity)
            val intent = Intent(activity, RLLoginActivityRL::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().startActivity(intent)
            requireActivity().finish()
            RLBaseProgress.hideProgressDialog()
        }, 700)
    }*/

    open fun RLcommonToast(message:String){
        try{
            if (isAdded) {
                context?.let {
                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG1,"TOAST EXCEPTION:- ${e.message}")
        }

    }


    open fun RLcloseFragment() {
        // Close the fragment by popping it from the back stack
        parentFragmentManager.popBackStack()
    }

    open fun RLScreenSet(isLandScape:Boolean) {
        val uiModeManager =  activity?.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val currentModeType = uiModeManager.currentModeType

        if (currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            // The device is running in TV mode (Android TV)
            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
        } else {
            // The device is not running in TV mode
            if (isLandScape){
                activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
            }else{
                activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
        }

    }

    open fun RLBottomHideShowSet(isShow:Boolean) {
        if (isShow){
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()

        }else{
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        }
    }

    open fun RLHelpHideShowSet(isShow: Boolean, imageHelp: ImageView, startHelpContent: String) {
        val helpString= com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, startHelpContent,"" )

        if (helpString.isNotEmpty()){
            try {
                val gson = Gson()
                val StartHelpModel: RLStartHelpModel = gson.fromJson(helpString, RLStartHelpModel::class.java)
                if(StartHelpModel.visible){
                    imageHelp.visibility=View.VISIBLE
                }else{
                    imageHelp.visibility=View.GONE
                }
            }catch (e:Exception){
              e.printStackTrace()
            }
        }
        /*if (isShow){
            imageHelp.visibility=View.VISIBLE
        }else{
            imageHelp.visibility=View.GONE
        }*/
    }


    //Firebase To Fetch UserBasic Data
     fun RLFirebaseToFetchUserData(callback: (RLRevoolaUsersSettingsModel?) -> Unit) {
        val authManager = RLAuthManager()
        val userId = authManager.RlgetCurrentUser()?.uid ?: run {
            callback(null) // Return null if user is not logged in
            return
        }

        // Firebase to fetch user data
        RLDatabaseManagerRead().RlUserBasicDataRead(userId) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                callback(userData) // Return userData through the callback
            } else {
                callback(null) // Return null in case of an error
            }
        }
    }
    //Firebase To Fetch AssumedCalories Data
    fun RLfetchAssumedCalories(callback: (RLAssumedCalories?) -> Unit) {
        // Firebase to fetch user data
        val path ="/proposedstructure/codeSection/assumedCalories"
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLAssumedCalories::class.java)
                callback(userData) // Return userData through the callback
            } else {
                callback(null) // Return null in case of an error
            }
        }
    }
    //Firebase To Fetch AssumedRev Data
    fun RLfetchAssumedRev(callback: (RLAssumedRev?) -> Unit) {
        // Firebase to fetch user data
        val path ="/proposedstructure/codeSection/assumedRev"
        RLDatabaseManagerRead().RlreadData(path) { data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLAssumedRev::class.java)
                callback(userData) // Return userData through the callback
            } else {
                callback(null) // Return null in case of an error
            }
        }
    }
     fun RLCalculateAssumedCalories(hr: Double, weight: Double, age: Int, time: Double, assumedRev: Double, assumedCalories: RLAssumedCalories, gender:String): Double {
        val maxAssumedRev = (1000.0 / 3600.0) * time
        val normalizedAssumedRev = assumedRev / maxAssumedRev / 100

        val assumedCalory = if (gender.lowercase() == "male") {
            assumedCalories.Male
        } else {
            assumedCalories.Female
        } ?: assumedCalories.Male

        assumedCalory?.let {
            val first = it.assumedcaloriesY * hr
            val second = it.assumedcaloriesZ * weight
            val third = it.assumedcaloriesAge * age
            val fourth = it.assumedcaloriesX + first + second + third
            val fifth = fourth / it.assumedcaloriesAgeCo
            val sixth = fifth * normalizedAssumedRev
            val seventh = sixth * time
            val eighth = seventh * it.assumedcaloriesFactor
            return eighth
        }
        return 0.0
    }
     fun RLGenerateAssumedRev(classType:String, assumedRev: RLAssumedRev, distancevalue:Double, totalElevation:Double, totalTime:Double): Double {
        var assumedRevValue = 0.0
        val distance = distancevalue//
        val elevation =totalElevation  //
        val totalTravelled = elevation + distance

        when (classType.lowercase()) {
            "ride" -> {
                if (distance > 0.1) {
                    assumedRev?.rideout?.let { rideOutAssumedRev ->
                        val assumedRevClimbed = elevation *
                                rideOutAssumedRev.RevPerMeterClimbed *
                                (elevation / totalTravelled)
                        val assumedRevTravelled = distance *
                                rideOutAssumedRev.RevPerMeterTravelled *
                                (distance / totalTravelled)
                        assumedRevValue = assumedRevClimbed + assumedRevTravelled
                    }
                } else {
                    assumedRev?.ridein?.let { rideInAssumedRev ->
                        val assumedRevClimbed = elevation *
                                rideInAssumedRev.RevPerMeterClimbed *
                                (elevation / totalTravelled)
                        val assumedRevTravelled = distance *
                                rideInAssumedRev.RevPerMeterTravelled *
                                (distance / totalTravelled)
                        assumedRevValue = assumedRevClimbed + assumedRevTravelled
                    }
                }
            }
            "run" -> {
                assumedRev?.run?.let { runAssumedRev ->
                    val assumedRevClimbed = elevation *
                            runAssumedRev.RevPerMeterClimbed *
                            (elevation / totalTravelled)
                    val assumedRevTravelled = distance *
                            runAssumedRev.RevPerMeterTravelled *
                            (distance / totalTravelled)
                    assumedRevValue = assumedRevClimbed + assumedRevTravelled
                }
            }
            "walk" -> {
                assumedRev?.walk?.let { walkAssumedRev ->
                    val assumedRevClimbed = elevation *
                            walkAssumedRev.RevPerMeterClimbed *
                            (elevation / totalTravelled)
                    val assumedRevTravelled = distance *
                            walkAssumedRev.RevPerMeterTravelled *
                            (distance / totalTravelled)
                    assumedRevValue = assumedRevClimbed + assumedRevTravelled
                }
            }
            "pilates" -> {
                assumedRev?.pilates?.let { pilatesAssumedRev ->
                    assumedRevValue = (totalTime.toDouble() ?: 0.0) *
                            pilatesAssumedRev.RevPerSecond
                }
            }
            "yoga" -> {
                assumedRev?.yoga?.let { yogaAssumedRev ->
                    assumedRevValue = (totalTime.toDouble() ?: 0.0) *
                            yogaAssumedRev.RevPerSecond
                }
            }
            "workout" -> {
                assumedRev?.workout?.let { workoutAssumedRev ->
                    assumedRevValue = (totalTime.toDouble() ?: 0.0) *
                            workoutAssumedRev.RevPerSecond
                }
            }
        }
        return assumedRevValue
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

    fun  RLCommonAlert(message: String,contextt: Context){
        AlertDialog.Builder(contextt)
            .setTitle("Payload Details")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
