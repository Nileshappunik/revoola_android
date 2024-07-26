package com.example.myfirstapp.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivityVerificationCodeBinding
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

class RLVerificationCodeActivityRL : RLBaseActivity()  {
    val TAG: String = RLVerificationCodeActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityVerificationCodeBinding
    private lateinit var viewModel: RLMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_verification_code) as RlActivityVerificationCodeBinding
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(this, RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLUisetup()
    }

    private fun RLUisetup() {
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.verificationcode)

        val emailId= intent.getStringExtra("EmailId")
        val password= intent.getStringExtra("Password")

        activityBinding.pincustom.pinDigit1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    activityBinding.pincustom.pinDigit2.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        activityBinding.pincustom.pinDigit2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    activityBinding.pincustom.pinDigit3.requestFocus()
                } else if (s?.length == 0) {
                    activityBinding.pincustom.pinDigit1.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        activityBinding.pincustom.pinDigit3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    activityBinding.pincustom.pinDigit4.requestFocus()
                } else if (s?.length == 0) {
                    activityBinding.pincustom.pinDigit2.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        activityBinding.pincustom.pinDigit4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    activityBinding.pincustom.pinDigit3.requestFocus()
                }else if (s?.length == 1) {
                    val pinDigit1 = activityBinding.pincustom.pinDigit1.text.toString()
                    val pinDigit2 = activityBinding.pincustom.pinDigit2.text.toString()
                    val pinDigit3 = activityBinding.pincustom.pinDigit3.text.toString()
                    val pinDigit4 = activityBinding.pincustom.pinDigit4.text.toString()
                    val pin:String = pinDigit1 + pinDigit2 + pinDigit3 + pinDigit4
                    if (pin.equals("1234")){
                        startActivity(Intent(this@RLVerificationCodeActivityRL, RLSignUpNameActivityRL::class.java).putExtra("EmailId",emailId).putExtra("Password",password))
                        finish()
                    }else{
                        Toast.makeText(this@RLVerificationCodeActivityRL, "Wrong Code", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        activityBinding.txtClickme.setOnClickListener(View.OnClickListener {
            //clickme resend code
        })
    }
}