package com.revoola.base

import android.R
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.model.RLRevoolaUsersSettingsModel
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject


open class  RLBaseActivity: AppCompatActivity() {
    val TAG1: String = RLBaseActivity::class.java.simpleName

    lateinit var activity: Activity
    lateinit var RLApiClientRetrofit: RLApiClientRet

   open fun RLonBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.onBackPressed() }
    }

    // DataBind
    open fun RLinflateBindLayout(activity1: Activity, layoutName: Int): Any? {
        activity = activity1
        return DataBindingUtil.setContentView(activity1, layoutName)
    }

    open fun RLScreenSet(isLandScape:Boolean) {
        val uiModeManager =  getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val currentModeType = uiModeManager.currentModeType

        if (currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            // The device is running in TV mode (Android TV)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
        } else {
            // The device is not running in TV mode
            if (isLandScape){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }

        }

    }

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


    // Register All permission request launcher at the class level
    val RLRequestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }
            /*if (deniedPermissions.isEmpty()) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Some permissions denied: $deniedPermissions", Toast.LENGTH_LONG).show()
            }*/
        }


}

