package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragChangePasswordBinding

class RLFragChangePassword : RLBaseFragment() {
    val TAG: String = RLFragChangePassword::class.java.simpleName
    lateinit var fragBinding: RlFragChangePasswordBinding
    var passwordold: String = ""
    var passwordnew: String = ""

    
    private val binding by lazy {
        RlFragChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_change_password, container) as RlFragChangePasswordBinding

        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragChangePassword" )
        RLonBackPresAct(fragBinding.ivBack)
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {

        fragBinding.tvpassupdate.setOnClickListener {
            if (RLvalidation()) {
                //api call
            }
        }
    }
    private fun RLvalidation(): Boolean {
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