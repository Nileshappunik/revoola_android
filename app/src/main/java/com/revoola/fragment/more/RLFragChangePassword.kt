package com.revoola.fragment.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.moengage.core.MoECoreHelper
import com.revoola.RLBaseFragment
import com.revoola.RLBaseProgress
import com.revoola.activity.RLSplashActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragChangePasswordBinding
import com.revoola.utils.RLPrefManager

class RLFragChangePassword : RLBaseFragment() {
    val TAG: String = RLFragChangePassword::class.java.simpleName
    var passwordold: String = ""
    var passwordnew: String = ""

    
    private val fragBinding by lazy {
        RlFragChangePasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChangePassword" )
        rl_onBackPresAct(fragBinding.ivBack)
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.tvpassupdate.setOnClickListener {
            if (isAdded) RLBaseProgress.rl_showProgressDialog(requireActivity())
            if (rl_validation()) {
                //api call
                val user = FirebaseAuth.getInstance().currentUser
                val emailId = user?.email?:""

                changeUserPassword(
                    currentEmail = emailId,
                    currentPassword = passwordold,
                    newPassword = passwordnew,
                    onSuccess = {
                        RLBaseProgress.rl_hideProgressDialog()
                        if (isAdded) Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show()
                        rl_signOut()
                    },
                    onFailure = { exception ->
                        RLBaseProgress.rl_hideProgressDialog()
                        if (isAdded)  Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    }
                )

            }
        }
    }
    private fun rl_validation(): Boolean {
         passwordold = fragBinding.etcurrentPassword.text.toString().trim()
         passwordnew = fragBinding.etnewPassword.text.toString().trim()
        val passwordnewcon = fragBinding.etnewconformPassword.text.toString().trim()
         if (passwordold.isEmpty()) {
             RLBaseProgress.rl_hideProgressDialog()
            fragBinding.etcurrentPassword.setError("Please Enter a Current Password")
            fragBinding.etcurrentPassword.requestFocus()
            return false
        }else if (passwordnew.isEmpty()){
             RLBaseProgress.rl_hideProgressDialog()
             fragBinding.etnewPassword.setError("Please Enter a New Password")
             fragBinding.etnewPassword.requestFocus()
             return false
        }else if (!passwordnew.equals(passwordnewcon)){
             RLBaseProgress.rl_hideProgressDialog()
             fragBinding.etnewPassword.setError("New password and New confirm password do not match.")
             fragBinding.etnewPassword.requestFocus()
             return false
         }
        return true
    }
    private fun changeUserPassword(currentEmail: String,currentPassword: String,newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && !currentEmail.isBlank() && !currentPassword.isBlank()) {
            // Re-authenticate user
            val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)

            user.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Update password
                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(updateTask.exception ?: Exception("Password update failed"))
                        }
                    }
                } else {
                    onFailure(authTask.exception ?: Exception("Reauthentication failed"))
                }
            }
        } else {
            onFailure(Exception("Invalid user or credentials"))
        }
    }
}