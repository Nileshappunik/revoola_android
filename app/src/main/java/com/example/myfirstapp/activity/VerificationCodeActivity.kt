package com.example.myfirstapp.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityVerificationCodeBinding
import com.example.myfirstapp.viewmodel.MainRepository
import com.example.myfirstapp.viewmodel.MainViewModel
import com.example.myfirstapp.viewmodel.MainViewModelFactory
import com.goodiebag.pinview.Pinview

class VerificationCodeActivity : BaseActivity()  {
    val TAG: String = VerificationCodeActivity::class.java.simpleName
    lateinit var activityBinding: ActivityVerificationCodeBinding
    var emailID: String = ""
    var password: String = ""
    private lateinit var viewModel: MainViewModel
    var sucDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_verification_code) as ActivityVerificationCodeBinding
        // Api call
        apiClientRetrofit = ApiClientRet(activity)
        val apiService = apiClientRetrofit.networkService
        val userRepository = MainRepository(apiService)
        viewModel = ViewModelProvider(this, MainViewModelFactory(userRepository)).get(MainViewModel::class.java)
        Uisetup()
    }

    private fun Uisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.app_name)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        onBackPresAct(activityBinding.toolbarLogin.ivBack)
        var pin = Pinview(this)
        pin = activityBinding.pinview
        pin.setPinViewEventListener(object : Pinview.PinViewEventListener {
            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                //Toast.makeText(this@VerificationCodeActivity, pinview!!.value, Toast.LENGTH_SHORT).show()
               startActivity(Intent(this@VerificationCodeActivity, SignUpNameActivity::class.java))
            }
        })
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