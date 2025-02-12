package com.revoola.activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.revoola.R
import com.revoola.base.RLBaseActivity
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
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_sign_up_name) as RlActivitySignUpNameBinding
        RLUisetup()
    }
    private fun RLUisetup() {
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.toolbarLogin.tvTitle.setText(R.string.basicdetails)

        emailID= RLPrefManager.RLGetSomeStringValue(this, RLPrefManager.login_email,"")
        password=RLPrefManager.RLGetSomeStringValue(this,RLPrefManager.login_password,"")

        IsNewUser=intent.getBooleanExtra("IsNewUser",false)
      //  emailID= intent.getStringExtra("EmailId").toString()
      //  password= intent.getStringExtra("Password").toString()
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            val  userId=RLPrefManager.RLGetSomeStringValue(this, RLPrefManager.current_user,"")
            RlLoginSuccessful(userId)

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

    private fun RlLoginSuccessful(userId:String){
      RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user,userId)
       RLPrefManager.RLSetSomeStringValue(this,RLPrefManager.current_user_email,emailID)
       RLPrefManager.RLSetSomeBooleanValue(this,RLPrefManager.isGuestUser,false)
        startActivity(Intent(this, RLSignUpActivityRL::class.java)
            .putExtra("firstName",firstName)
            .putExtra("lastName",lastName)
            .putExtra("nickName",nickName))
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