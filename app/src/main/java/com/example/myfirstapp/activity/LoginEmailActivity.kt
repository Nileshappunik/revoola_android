package com.example.myfirstapp.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityLoginEmailBinding
import com.example.myfirstapp.utils.Tools
import com.example.myfirstapp.viewmodel.MainRepository
import com.example.myfirstapp.viewmodel.MainViewModel
import com.example.myfirstapp.viewmodel.MainViewModelFactory

class LoginEmailActivity : BaseActivity() {

    val TAG: String = LoginEmailActivity::class.java.simpleName
    lateinit var activityBinding: ActivityLoginEmailBinding
    var emailID: String = ""
    var password: String = ""
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_login_email) as ActivityLoginEmailBinding
        // Api call
        apiClientRetrofit = ApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = MainRepository(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(MainViewModel::class.java)
        Uisetup()
    }

    private fun Uisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signuplogin)
        activityBinding.toolbarLogin.ivBack.visibility=View.VISIBLE
        onBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
           /* if (validation()) {
                if (apiClientRetrofit.isConnected) {
                    //login Api
                   // loginapicall()
                    startActivity(Intent(this,MainActivity::class.java))
                } else {
                    //showDialogFullscreen()
                }
            }*/
            startActivity(Intent(this,MainActivity::class.java))
        })
        activityBinding.txtClickme.setOnClickListener(View.OnClickListener {
           startActivity(Intent(this, ForgotPasswordActivity::class.java))
        })
    }

    private fun opentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }

    private fun validation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        password = activityBinding.etPassword.text.toString().trim()

        if (emailID.isEmpty()) {
            activityBinding.etemailid.setError("Please Enter a EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }else if (!Tools.isEmailValid(emailID)) {
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
}