package com.example.myfirstapp.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivityForgotPasswordBinding
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class RLForgotPasswordActivityRL : RLBaseActivity() {
    val TAG: String = RLForgotPasswordActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityForgotPasswordBinding
    var emailID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_forgot_password) as RlActivityForgotPasswordBinding
        RLUisetup()

    }
    private fun RLUisetup() {
        activityBinding.toolbar.tvTitle.visibility=View.GONE
        activityBinding.toolbar.ivlogoapp.visibility=View.VISIBLE
        activityBinding.toolbar.ivBack.visibility= View.VISIBLE
        RLonBackPresAct(activityBinding.toolbar.ivBack)
        activityBinding.tvnext.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                RLSendPasswordResetEmail()
            }
        })

    }
    private fun RLSendPasswordResetEmail() {
        // Initialize Firebase Auth
        val  auth = FirebaseAuth.getInstance()
        auth.sendPasswordResetEmail(emailID)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun RLvalidation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        if (emailID.isEmpty()) {
            activityBinding.etemailid.setError("Please Enter a EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }else if (!RLTools.RLisEmailValid(emailID)) {
            activityBinding.etemailid.setError("Please Enter a valid EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }
        return true
    }
}