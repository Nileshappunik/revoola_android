package com.revoola.activity

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.revoola.R
import com.revoola.base.RLBaseActivity
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.RlActivityForgotPasswordBinding
import com.revoola.commonobject.RLTools

class RLForgotPasswordActivityRL : RLBaseActivity() {
    val TAG: String = RLForgotPasswordActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityForgotPasswordBinding
    var emailID: String = ""
    var sucDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_forgot_password) as RlActivityForgotPasswordBinding
        RLUisetup()

    }
    private fun RLUisetup() {
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.resetpassword)
        activityBinding.tvnext.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                RLSendPasswordResetEmail()
            }
        })

    }
    private fun RLSendPasswordResetEmail() {
        // Initialize Firebase Auth
        val  authManager = RLAuthManager()
        authManager.RLForgotPasswordUser(emailID) { data, error ->
            if (!data.isNullOrEmpty()) {
                RLopentoast(data)
                finish()
            } else {
                RLopentoast("Registration failed: ${error?.message}")
            }
        }
    }

    private fun RLvalidation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        if (emailID.isEmpty()) {
            RLshowDialog("Please enter valid Email")
            return false
        }else if (!RLTools.RLisEmailValid(emailID)) {
            RLshowDialog("Please enter valid Email")

            return false
        }
        return true
    }

    private fun RLshowDialog( emaildid: String) {
        sucDialog = Dialog(activity)
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog!!.setContentView(R.layout.rl_alertdialog_custom_layout)
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvTitle: TextView = sucDialog!!.findViewById(R.id.iv_title)
        val iv_description: TextView = sucDialog!!.findViewById(R.id.iv_description)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.iv_ok)
        tvTitle.setText("")
        iv_description.setText(emaildid)

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
}