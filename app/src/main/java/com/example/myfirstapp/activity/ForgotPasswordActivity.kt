package com.example.myfirstapp.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityForgotPasswordBinding
import com.example.myfirstapp.utils.Tools
import com.example.myfirstapp.viewmodel.MainRepository
import com.example.myfirstapp.viewmodel.MainViewModel
import com.example.myfirstapp.viewmodel.MainViewModelFactory

class ForgotPasswordActivity : BaseActivity() {
    val TAG: String = ForgotPasswordActivity::class.java.simpleName
    lateinit var activityBinding: ActivityForgotPasswordBinding
    var emailID: String = ""
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_forgot_password) as ActivityForgotPasswordBinding
        // Api call
        apiClientRetrofit = ApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = MainRepository(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(MainViewModel::class.java)
        Uisetup()

    }
    private fun Uisetup() {
        activityBinding.toolbar.tvTitle.setText(R.string.app_name)
        activityBinding.toolbar.ivBack.visibility= View.VISIBLE
        onBackPresAct(activityBinding.toolbar.ivBack)
        activityBinding.tvnext.setOnClickListener(View.OnClickListener {
            if (validation()) {
                if (apiClientRetrofit.isConnected) {
                    //login Api
                    // loginapicall()
                } else {
                    //showDialogFullscreen()
                }
            }
        })

    }

    private fun opentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }

    private fun validation(): Boolean {
        emailID = activityBinding.etemailid.text.toString().trim()
        if (emailID.isEmpty()) {
            activityBinding.etemailid.setError("Please Enter a EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }else if (!Tools.isEmailValid(emailID)) {
            activityBinding.etemailid.setError("Please Enter a valid EmailId")
            activityBinding.etemailid.requestFocus()
            return false
        }
        return true
    }
}