package com.revoola

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.revoola.activity.RLMainActivityRL
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.firebaseModel.RLAssumedCalories
import com.revoola.firebaseModel.RLAssumedRev
import com.revoola.fragment.common.RLFragNoInternet
import com.revoola.fragment.start.RLStartHelpModel
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.google.gson.Gson
import com.moengage.core.MoECoreHelper
import com.revoola.activity.RLSplashActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.utils.RLPrefManager
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


open class RLBaseFragment : Fragment() {
    val TAG1: String = RLBaseFragment::class.java.simpleName


    open fun rl_onBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.requireActivity().onBackPressed() }
    }

    fun rl_getCurrentDateTimeIsoFormatted(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDateTime = ZonedDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            return currentDateTime.format(formatter)
        }else{
            return "0000-00-00'T'00:00:00"
        }

    }

    fun rl_getUserDetails(context: Context): RLRevoolaUsersSettingsModel? {
        val  json = RLPrefManager.rl_getSomeStringValue(requireContext(), RLPrefManager.user_model_data,null)
        val gson = Gson()

        return if (json != null) {
            gson.fromJson(json, RLRevoolaUsersSettingsModel::class.java)
        } else {
            null
        }
    }


    open fun rl_signOut() {
        Firebase.auth.signOut()
        MoECoreHelper.logoutUser(requireContext())
        // RLPrefManager.RLsetSomeStringValue(requireContext(), RLPrefManager.current_user,"")
        RLPrefManager.rl_clear_all(requireContext())
        val intent = Intent(requireContext(), RLSplashActivityRL::class.java)
        startActivity(intent)
        activity?.finish()
    }

    //TODO : DataBind
    open fun rl_inflateBindLayout(activity1: Class<FragmentActivity>?, inflater: LayoutInflater, layoutName: Int, container: ViewGroup?, ): Any? {
        return DataBindingUtil.inflate(inflater!!, layoutName, container, false)
    }

    fun rl_showDialogFullscreen(): RLFragNoInternet {
        val dialog = RLFragNoInternet()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        dialog.show(ft, TAG1)

        return dialog
    }

    open fun rl_commonToast(message:String){
        try{
            if (isAdded) {
                context?.let {
                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
           RLTools.rl_logEPrint(TAG1,"TOAST EXCEPTION:- ${e.message}")
        }

    }

    open fun rl_closeFragment() {
        // Close the fragment by popping it from the back stack
        parentFragmentManager.popBackStack()
    }

    open fun rl_screenSet(isLandScape:Boolean) {
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

    open fun rl_bottomHideShowSet(isShow:Boolean) {
        if (isShow){
            (context as RLMainActivityRL).rl_showbottombarcolorwhite()

        }else{
            (context as RLMainActivityRL).rl_hidebottombarcolorwhite()
        }
    }

    open fun rl_helpHideShowSet(isShow: Boolean, imageHelp: ImageView, startHelpContent: String) {
        val helpString= com.revoola.utils.RLPrefManager.rl_getSomeStringValue(activity, startHelpContent,"" )

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
     fun rl_firebaseToFetchUserData(callback: (RLRevoolaUsersSettingsModel?) -> Unit) {
        val authManager = RLAuthManager()
        val userId = authManager.rl_getCurrentUser()?.uid ?: run {
            callback(null) // Return null if user is not logged in
            return
        }

        // Firebase to fetch user data
        RLDatabaseManagerRead().rl_userBasicDataRead(userId) { data, error ->
            if (data != null) {
               // val gson = Gson()
               // val jsonObject = gson.toJson(data)
               // val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                val userData = RLTools.parseUserData(data)
                callback(userData) // Return userData through the callback
            } else {
                callback(null) // Return null in case of an error
            }
        }

    }
    //Firebase To Fetch AssumedCalories Data
    fun rl_fetchAssumedCalories(callback: (RLAssumedCalories?) -> Unit) {
        // Firebase to fetch user data

        val path = RevoolaFirebasePath.assumedCaloriesDataPath()
        RLDatabaseManagerRead().rl_readData(path) { data, error ->
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
    fun rl_fetchAssumedRev(callback: (RLAssumedRev?) -> Unit) {
        // Firebase to fetch user data
        val path = RevoolaFirebasePath.assumedRevDataPath()
        RLDatabaseManagerRead().rl_readData(path) { data, error ->
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
     fun rl_calculateAssumedCalories(hr: Double, weight: Double, age: Int, time: Double, assumedRev: Double, assumedCalories: RLAssumedCalories, gender:String): Double {
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
     fun rl_generateAssumedRev(classType:String, assumedRev: RLAssumedRev, distancevalue:Double, totalElevation:Double, totalTime:Double): Double {
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


}
