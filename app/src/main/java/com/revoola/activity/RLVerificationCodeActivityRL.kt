package com.revoola.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
//import com.revoola.BuildConfig
import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.activity.base.RLBaseActivity
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.databinding.RlActivityVerificationCodeBinding
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory

class RLVerificationCodeActivityRL : RLBaseActivity()  {
    val TAG: String = RLVerificationCodeActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityVerificationCodeBinding
    private lateinit var viewModel: RLMainViewModel
    lateinit var  databaseManager: RLDatabaseManagerWrite
    private var emailID=""
    private var password=""

    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_verification_code) as RlActivityVerificationCodeBinding
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.networkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(this,
            RLMainViewModelFactory(
                userRepository
            )
        ).get(RLMainViewModel::class.java)
        RLUisetup()
    }

    private fun RLUisetup() {
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.verificationcode)
        emailID= intent.getStringExtra("EmailId").toString()
        password= intent.getStringExtra("Password").toString()

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
                        RlupdateUserDetails()
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
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RlupdateUserDetails() {
        val authManager: RLAuthManager = RLAuthManager()
        databaseManager = RLDatabaseManagerWrite()
        authManager.RLRegisterUser(emailID, password) { user, error ->
            if (user != null) {
                val userId = user.uid
                RLLiveUserEmailWrite(userId)
            }else {
                RLopentoast("Registration failed: ${error?.message}")
            }
        }
    }
    private fun RLLiveUserEmailWrite(userId: String) {
        databaseManager.RLLIVEUSERSEMAILWrite(userId,emailID) { success, error ->
            if (success) {
                RevoolaUserEmailWrite(userId)
            } else {
                RLopentoast("User write operation failed: ${error?.message}")
            }
        }
    }
    private fun RevoolaUserEmailWrite(userId: String) {
        databaseManager.REVOOLAUSEREMAILSWrite(userId,emailID) { success, error ->
            if (success) {
                RLRevoolaUserSettingFirebaseEntry(userId,emailID)
            } else {
                RLopentoast("User write operation failed: ${error?.message}")
            }
        }
    }

    ///FIREBASE Revoola User Setting USer Blanck Entry
    private fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String) {
        val versionName: String = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        val firebaseManager = RLFirebaseManager()
        firebaseManager.RLRevoolaUserSettingFirebaseEntry(userId,emailId, versionName) { success ->
            if (success) {
                RlLoginSuccessful(userId)
            }else {
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun RlLoginSuccessful(userId:String){
       RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user,userId)
        startActivity(Intent(this@RLVerificationCodeActivityRL, RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",true))
        finish()
    }
}