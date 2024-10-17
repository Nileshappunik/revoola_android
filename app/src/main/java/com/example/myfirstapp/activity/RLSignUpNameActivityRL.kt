package com.example.myfirstapp.activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.myfirstapp.R
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databasefirebase.RLAuthManager
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerWrite
import com.example.myfirstapp.databinding.RlActivitySignUpNameBinding
import com.example.myfirstapp.utils.RLPrefManager

class RLSignUpNameActivityRL : RLBaseActivity(){
    val TAG: String = RLSignUpNameActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySignUpNameBinding
    lateinit var  databaseManager: RLDatabaseManagerWrite
    var firstName: String = ""
    var lastName: String = ""
    var nickName: String = ""
    var emailID: String = ""
    var password: String = ""
    var IsNewUser: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_sign_up_name) as RlActivitySignUpNameBinding
        RLUisetup()
    }
    private fun RLUisetup() {
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.basicdetails)

        emailID= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.login_email,"")
        password= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.login_password,"")

        IsNewUser=intent.getBooleanExtra("IsNewUser",false)
      //  emailID= intent.getStringExtra("EmailId").toString()
      //  password= intent.getStringExtra("Password").toString()
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (IsNewUser){
                RlupdateUserDetails()
            }else{
                val  userId= RLPrefManager.RLgetSomeStringValue(this, RLPrefManager.current_user,"")
                RlLoginSuccessful(userId)
            }

        })

        activityBinding.etfirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else{
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        activityBinding.etlastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else{
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        activityBinding.etnickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else{
                    RLvalidation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }
    fun RlupdateUserDetails() {
        val authManager: RLAuthManager =RLAuthManager()
        databaseManager =RLDatabaseManagerWrite()
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
                RevoolaUserForSearchWrite(userId)
            } else {
                RLopentoast("User write operation failed: ${error?.message}")
            }
        }
    }
    private fun RevoolaUserForSearchWrite(userId: String) {
        val revoolaUserForSearchMap = hashMapOf(
            "displayImage" to "",
            "emailId" to emailID,
            "firstName" to firstName,
            "lastName" to lastName,
            "name" to nickName,
            "remark" to "Android",
            "userId" to userId)
        databaseManager.REVOOLAUSERFORSEARCHWrite(userId,revoolaUserForSearchMap) { success, error ->
            if (success) {
                RlLoginSuccessful(userId)
            } else {
                RLopentoast("User write operation failed: ${error?.message}")
            }
        }
    }
    private fun RlLoginSuccessful(userId:String){
        RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,userId)
        RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user_email,emailID)
        startActivity(Intent(this, RLSignUpActivityRL::class.java))
        finish()
    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RLvalidation(): Boolean {
        firstName = activityBinding.etfirstname.text.toString().trim()
        lastName = activityBinding.etlastname.text.toString().trim()
        nickName = activityBinding.etnickname.text.toString().trim()
        if (firstName.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (lastName.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }else if (nickName.isEmpty()) {
            activityBinding.tvLogin.visibility=View.GONE
            activityBinding.tvLoginNoClick.visibility=View.VISIBLE
            return false
        }
        activityBinding.tvLogin.visibility=View.VISIBLE
        activityBinding.tvLoginNoClick.visibility=View.GONE
        return true
    }
}