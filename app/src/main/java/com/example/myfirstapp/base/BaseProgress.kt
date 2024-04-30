package com.example.myfirstapp

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity



class BaseProgress : AppCompatActivity() {
    companion object {
        var dialog: Dialog? = null
        fun ShowProgressDialog(activity: Activity) {
            if (dialog != null) {
                dialog!!.dismiss()
            }
            try {
                dialog = Dialog(activity)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
                dialog!!.setContentView(R.layout.progress_style)
                dialog!!.setCancelable(false)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog!!.window!!.getAttributes())
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog!!.show()
                dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun ShowProgressDialogWithoutText(activity: Activity) {
            if (dialog != null) {
                dialog!!.dismiss()
            }
            try {
                dialog = Dialog(activity)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
                dialog!!.setContentView(R.layout.progress_style)
                dialog!!.setCancelable(false)
                val lp = WindowManager.LayoutParams()
                lp.copyFrom(dialog!!.window!!.getAttributes())
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                dialog!!.show()
                dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun hideProgressDialog() {
            if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
        }
    }

}
