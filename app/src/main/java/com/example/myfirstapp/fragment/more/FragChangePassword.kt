package com.example.myfirstapp.fragment.more

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragChangePasswordBinding
import com.example.myfirstapp.utils.PrefManager

class FragChangePassword : BaseFragment() {
    val TAG: String = FragChangePassword::class.java.simpleName
    lateinit var fragBinding: FragChangePasswordBinding
    var passwordold: String = ""
    var passwordnew: String = ""

    
    private val binding by lazy {
        FragChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_change_password, container) as FragChangePasswordBinding

        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragChangePassword" )
        fragBinding.toolbar.tvTitle.setText(R.string.changepassword)
        fragBinding.toolbar.ivBack.visibility
        onBackPresAct(fragBinding.toolbar.ivBack)
        uisetup()
        return fragBinding.root
    }

    private fun uisetup() {

        fragBinding.tvpassupdate.setOnClickListener {
            if (validation()) {
                //api call
            }
        }
    }
    private fun validation(): Boolean {
         passwordold = fragBinding.etcurrentPassword.text.toString().trim()
         passwordnew = fragBinding.etnewPassword.text.toString().trim()
        val passwordnewcon = fragBinding.etnewconformPassword.text.toString().trim()

         if (passwordold.isEmpty()) {
            fragBinding.etcurrentPassword.setError("Please Enter a Current Password")
            fragBinding.etcurrentPassword.requestFocus()
            return false
        }else if (passwordnew.isEmpty()){
             fragBinding.etnewPassword.setError("Please Enter a New Password")
             fragBinding.etnewPassword.requestFocus()
             return false
        }else if (passwordnew.equals(passwordnewcon)){
             fragBinding.etnewPassword.setError("New password and New confirm password do not match.")
             fragBinding.etnewPassword.requestFocus()
             return false
         }
        return true
    }
}