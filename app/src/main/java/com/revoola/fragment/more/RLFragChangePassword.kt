package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.databinding.RlFragChangePasswordBinding
import com.revoola.utils.RLPrefManager

class RLFragChangePassword : RLBaseFragment() {
    val TAG: String = RLFragChangePassword::class.java.simpleName
   // lateinit var fragBinding: RlFragChangePasswordBinding
    var passwordold: String = ""
    var passwordnew: String = ""

    
    private val fragBinding by lazy {
        RlFragChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //  fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_change_password, container) as RlFragChangePasswordBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChangePassword" )
        rl_onBackPresAct(fragBinding.ivBack)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {

        fragBinding.tvpassupdate.setOnClickListener {
            if (rl_validation()) {
                //api call
            }
        }
    }
    private fun rl_validation(): Boolean {
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