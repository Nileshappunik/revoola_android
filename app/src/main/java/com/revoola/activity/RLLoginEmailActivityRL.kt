package com.revoola.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.revoola.base.RLBaseActivity
import com.revoola.R
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlActivityLoginEmailBinding
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.gson.Gson

class RLLoginEmailActivityRL : RLBaseActivity() {
    val TAG: String = RLLoginEmailActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityLoginEmailBinding
    var emailID: String = ""
    var password: String = ""
    var sucDialog: Dialog? = null
    private lateinit var authManager: RLAuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_login_email) as RlActivityLoginEmailBinding
        RLUisetup()
    }
    private fun RLUisetup() {

        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
       activityBinding.toolbarLogin.tvTitle.setText(R.string.signuplogin)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                RLloginapicall()
            }
        })
        activityBinding.txtClickme.setOnClickListener(View.OnClickListener {
           startActivity(Intent(this, RLForgotPasswordActivityRL::class.java))
        })

        activityBinding.etemailid.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else if ( activityBinding.etPassword.text.toString().isNotEmpty()){
                    activityBinding.tvLogin.visibility=View.VISIBLE
                    activityBinding.tvLoginNoClick.visibility=View.GONE
                }else{
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        activityBinding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                   activityBinding.tvLogin.visibility=View.GONE
                   activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else if ( activityBinding.etemailid.text.toString().isNotEmpty()){
                    activityBinding.tvLogin.visibility=View.VISIBLE
                    activityBinding.tvLoginNoClick.visibility=View.GONE
                }else{
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RLvalidation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        password = activityBinding.etPassword.text.toString().trim()
        if (!RLTools.RLisEmailValid(emailID)) {
            RLshowDialog("Please Enter Valid Email and 6+ digit Password.")
            return false
        }else if (password.length<6){
            RLshowDialog("Please Enter 6+ digit Password.")
            return false
        }
        return true
    }

    fun RLloginapicall() {
        authManager = RLAuthManager()
        authManager.RlloginUser(emailID, password) { user, exception ->
            if (user != null) {
                try {
                    // Get provider data from the user object
                    val providerData = user.providerData
                    // Iterate through the list of provider data
                    val profile=providerData[0]
                    val uid = profile.uid
                    val userEmail = profile.email
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user,uid)
                    RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user_email,userEmail)
                    RLSetUsernameToFirebase(uid)

                } catch (e:Exception){
                   RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
                }
            }
            else {
                // Sign-in failed
                when (exception) {
                    is FirebaseAuthInvalidUserException -> {
                        // Handle case where user does not exist
                        RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.login_email,emailID)
                        RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.login_password,password)
                       RLTools.RlLogEPrint(TAG, "User does not exist: ${exception.message}")
                        startActivity(Intent(this, RLVerificationCodeActivityRL::class.java).putExtra("EmailId",emailID).putExtra("Password",password))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Handle case where password is incorrect
                        RLshowDialog("The password is invalid or the user does not have a password.")
                       RLTools.RlLogEPrint(TAG, "Invalid credentials: ${exception.message}")
                    }
                    else -> {
                        // Handle other exceptions
                        RLopentoast("Sign-in failed")
                       RLTools.RlLogEPrint(TAG, "Sign-in failed: ${exception!!.message}")
                    }
                }
            }
        }
    }
    private fun RLshowDialog( emaildid: String) {
        sucDialog = Dialog(activity)
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog!!.setContentView(R.layout.rl_dailog_login_error)
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvTitle: TextView = sucDialog!!.findViewById(R.id.tvTitle)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.tvYes)

        tvTitle.setText(emaildid)

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun RLSetUsernameToFirebase(userId:String){
        //Firebase To Fetch UserData
        RLDatabaseManagerRead().RlUserBasicDataRead(userId){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                if (userData.isBasicDataAdded){
                    startActivity(Intent(this, RLMainActivityRL::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",false))//.putExtra("EmailId",emailId).putExtra("Password",password))
                    finish()
                }

            }
        }

    }



}