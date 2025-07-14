package com.revoola.activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.revoola.R
import com.revoola.activity.base.RLBaseActivity
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databinding.RlActivitySignUpNameBinding
import com.revoola.utils.RLPrefManager

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
        rl_screenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = rl_inflateBindLayout(this, R.layout.rl_activity_sign_up_name) as RlActivitySignUpNameBinding
        rl_uisetup()
    }
    private fun rl_uisetup() {
        rl_onBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.basicdetails)

        emailID= RLPrefManager.rl_getSomeStringValue(this, RLPrefManager.login_email,"")
        password=RLPrefManager.rl_getSomeStringValue(this,RLPrefManager.login_password,"")

        IsNewUser=intent.getBooleanExtra("IsNewUser",false)
      //  emailID= intent.getStringExtra("EmailId").toString()
      //  password= intent.getStringExtra("Password").toString()
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            val  userId=RLPrefManager.rl_getSomeStringValue(this, RLPrefManager.current_user,"")
            rl_loginSuccessful(userId)

        })

        activityBinding.etfirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    activityBinding.tvLogin.visibility=View.GONE
                    activityBinding.tvLoginNoClick.visibility=View.VISIBLE
                }else{
                    rl_validation()
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
                    rl_validation()
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
                    rl_validation()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }

    private fun rl_loginSuccessful(userId:String){
      RLPrefManager.rl_setSomeStringValue(this, RLPrefManager.current_user,userId)
       RLPrefManager.rl_setSomeStringValue(this,RLPrefManager.current_user_email,emailID)
       RLPrefManager.rl_setSomeBooleanValue(this,RLPrefManager.isGuestUser,false)
        startActivity(Intent(this, RLSignUpActivityRL::class.java)
            .putExtra("firstName",firstName)
            .putExtra("lastName",lastName)
            .putExtra("nickName",nickName))
        finish()
    }

    private fun rl_validation(): Boolean {
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