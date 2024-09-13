package com.example.myfirstapp.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.goodiebag.pinview.Pinview


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
        activityBinding.toolbarLogin.tvTitle.visibility=View.GONE
        activityBinding.toolbarLogin.ivlogoapp.visibility=View.VISIBLE
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        val emailId= intent.getStringExtra("EmailId")
        val password= intent.getStringExtra("Password")
        println("GETEMAIL:- $emailId")
        var pin = Pinview(this)
        pin = activityBinding.pinview
        pin.requestPinEntryFocus()
        pin.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                if (pinview!!.value.equals("1234")){
                    startActivity(Intent(this@RLVerificationCodeActivityRL, RLSignUpNameActivityRL::class.java).putExtra("EmailId",emailId).putExtra("Password",password))
                    finish()
                }else{
                    Toast.makeText(this@RLVerificationCodeActivityRL, "Wrong Code", Toast.LENGTH_SHORT).show()
                }
            }
        })

        pin.apply {
            setTextColor(resources.getColor(R.color.AppMainColor))
        }
       /* // pinView Customize
        pin.apply {
            setCursorShape(R.drawable.example_cursor)
            //setCursorColor(Color.BLUE);
            setTextSize(12)
            setTextColor(Color.BLACK)
            showCursor(true)
        }*/
        activityBinding.txtClickme.setOnClickListener(View.OnClickListener {
           //clickme resend code
        })
    }
}