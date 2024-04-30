package com.example.myfirstapp.base

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.myfirstapp.api.ApiClientRet


open class BaseActivity: AppCompatActivity() {

    lateinit var activity: Activity
    lateinit var apiClientRetrofit: ApiClientRet


    fun setAct(activity: Activity) {
        this.activity = activity
    }

   open fun onBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.onBackPressed() }
    }

    open fun onClickNoTask(o: LinearLayout) {
        o.setOnClickListener { v: View? -> }
    }


    //TODO : Full Screen
    open fun fullScreen() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    //TODO : Full Screen
    open fun setStatusBarDark() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
    }

    //TODO : DataBind
    open fun inflateBindLayout(activity1: Activity, layoutName: Int): Any? {
        activity = activity1
        return DataBindingUtil.setContentView(activity1, layoutName)
    }

    open fun clickActiviy(o: View, cls: Class<*>?, finishAll: String) {
        if (finishAll == "NEXT") {
            o.setOnClickListener { nextActivity(cls) }
        } else if (finishAll == "ALL") {
            o.setOnClickListener { nextFinishAllActivity(activity, cls) }
        }
    }

    open fun nextActivity(cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    open fun nextFinishAllActivity(activity: Activity, cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}

