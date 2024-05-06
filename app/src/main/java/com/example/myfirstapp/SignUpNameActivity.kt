package com.example.myfirstapp


import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.activity.ForgotPasswordActivity
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityLoginEmailBinding
import com.example.myfirstapp.databinding.ActivitySignUpNameBinding
import com.example.myfirstapp.utils.Tools
import com.example.myfirstapp.viewmodel.MainRepository
import com.example.myfirstapp.viewmodel.MainViewModel
import com.example.myfirstapp.viewmodel.MainViewModelFactory

class SignUpNameActivity : BaseActivity(){

    val TAG: String = SignUpNameActivity::class.java.simpleName
    lateinit var activityBinding: ActivitySignUpNameBinding
    var firstName: String = ""
    var lastName: String = ""
    var nickName: String = ""
    private lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_sign_up_name) as ActivitySignUpNameBinding
        // Api call
        apiClientRetrofit = ApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = MainRepository(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(MainViewModel::class.java)
        Uisetup()
    }

    private fun Uisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signuplogin)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        onBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (validation()) {
                if (apiClientRetrofit.isConnected) {
                    startActivity(Intent(this, SignUpActivity::class.java))
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
        firstName = activityBinding.etfirstname.text.toString().trim()
        lastName = activityBinding.etlastname.text.toString().trim()
        nickName = activityBinding.etnickname.text.toString().trim()

        if (firstName.isEmpty()) {
            activityBinding.etfirstname.setError("First name is required.")
            activityBinding.etfirstname.requestFocus()
            return false
        }else if (lastName.isEmpty()) {
            activityBinding.etlastname.setError("Last name is required.")
            activityBinding.etlastname.requestFocus()
            return false
        }else if (nickName.isEmpty()) {
            activityBinding.etnickname.setError("Nick name is required.")
            activityBinding.etnickname.requestFocus()
            return false
        }
        return true
    }
}