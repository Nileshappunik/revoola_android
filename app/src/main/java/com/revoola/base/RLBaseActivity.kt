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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject


open class  RLBaseActivity: AppCompatActivity() {
    val TAG1: String = RLBaseActivity::class.java.simpleName

    lateinit var activity: Activity
    lateinit var RLApiClientRetrofit: RLApiClientRet


    fun RLsetAct(activity: Activity) {
        this.activity = activity
    }

   open fun RLonBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.onBackPressed() }
    }

    open fun RLonClickNoTask(o: LinearLayout) {
        o.setOnClickListener { v: View? -> }
    }


    //TODO : Full Screen
    open fun RLfullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    //TODO : Full Screen
    open fun RLsetStatusBarDark() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status RLText dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    //TODO : DataBind
    open fun RLinflateBindLayout(activity1: Activity, layoutName: Int): Any? {
        activity = activity1
        return DataBindingUtil.setContentView(activity1, layoutName)
    }

    open fun RLclickActiviy(o: View, cls: Class<*>?, finishAll: String) {
        if (finishAll == "NEXT") {
            o.setOnClickListener { RLnextActivity(cls) }
        } else if (finishAll == "ALL") {
            o.setOnClickListener { RLnextFinishAllActivity(activity, cls) }
        }
    }

    open fun RLnextActivity(cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    open fun RLnextFinishAllActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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






}

