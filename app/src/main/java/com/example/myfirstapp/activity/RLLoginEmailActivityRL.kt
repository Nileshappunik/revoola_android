package com.example.myfirstapp.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivityLoginEmailBinding
import com.example.myfirstapp.model.RLUserModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class RLLoginEmailActivityRL : RLBaseActivity() {

    val TAG: String = RLLoginEmailActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityLoginEmailBinding
    var emailID: String = ""
    var password: String = ""
    private lateinit var viewModel: RLMainViewModel
    var sucDialog: Dialog? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_login_email) as RlActivityLoginEmailBinding
        // Api call code
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(this, RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLUisetup()
    }
    private fun RLUisetup() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        activityBinding.toolbarLogin.tvTitle.setText(R.string.signuplogin)
        activityBinding.toolbarLogin.ivBack.visibility=View.VISIBLE
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                if (RLApiClientRetrofit.RLisConnected()) {
                    //login Api
                    RLshowDialog(emailID)
                } else {
                    //showDialogFullscreen()
                }
            }
        })
        activityBinding.txtClickme.setOnClickListener(View.OnClickListener {
           startActivity(Intent(this, RLForgotPasswordActivityRL::class.java))
        })
    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RLvalidation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        password = activityBinding.etPassword.text.toString().trim()

        if (emailID.isEmpty()) {
            activityBinding.etemailid.setError("Please Enter a EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }else if (!RLTools.RLisEmailValid(emailID)) {
                activityBinding.etemailid.setError("Please Enter a valid EmailId")
                activityBinding.etemailid.requestFocus()
                return false
        } else if (password.isEmpty()) {
            activityBinding.etPassword.setError("Please Enter a Password")
            activityBinding.etPassword.requestFocus()
            return false
        }
        return true
    }

    //Realtime Database
    fun RLloginapicall() {
        // Sign in with email and password

        auth.signInWithEmailAndPassword(emailID, password)
            .addOnCompleteListener { task ->
                try {
                    if (task.isSuccessful) {
                        // Sign-in successful
                        val user = auth.currentUser
                        if (user != null) {
                            // Get provider data from the user object
                            val providerData = user.providerData
                            // Iterate through the list of provider data
                            val profile=providerData[0]
                            val uid = profile.uid
                            val userEmail = profile.email
                            RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,uid)
                            RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user_email,userEmail)
                            startActivity(Intent(this, RLMainActivityRL::class.java))
                            finish()
                        }
                    }else {
                        // Sign-in failed INVALID_LOGIN_CREDENTIALS
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            println("User does not exist.")
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            println("Invalid credentials.")
                        } catch (e: Exception) {
                            println("Error: ${e.message}")
                        }
                    }
                }catch (e:Exception){
                    Log.e(TAG,"Exception:- "+e.message)
                }
            }
    }

    private fun RLshowDialog( emaildid: String) {
        sucDialog = Dialog(activity)
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog!!.setContentView(R.layout.rl_layout_dailog_login)
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog!!.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.tvYes)
        val tvemailid: TextView = sucDialog!!.findViewById(R.id.tvemailid)
        tvemailid.setText(emaildid)

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
            RLloginapicall()
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
}