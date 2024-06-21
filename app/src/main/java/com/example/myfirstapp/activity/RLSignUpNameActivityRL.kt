package com.example.myfirstapp.activity


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.R
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivitySignUpNameBinding
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RLSignUpNameActivityRL : RLBaseActivity(){

    val TAG: String = RLSignUpNameActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySignUpNameBinding
    var firstName: String = ""
    var lastName: String = ""
    var nickName: String = ""
    private lateinit var viewModel: RLMainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_sign_up_name) as RlActivitySignUpNameBinding
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(this, RLMainViewModelFactory(userRepository)).get(RLMainViewModel::class.java)
        RLUisetup()
    }

    private fun RLUisetup() {
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signup)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
              //  RlupdateUserDetails(firstName,lastName,nickName)
            }
        })

    }

    fun RlupdateUserDetails(firstname: String?, lastname: String, nickname: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            // Update display name and photo URL
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(firstname)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, RLSignUpActivityRL::class.java))
                        finish()
                        println("User profile updated.")
                    } else {
                        println("Error updating profile: ${task.exception?.message}")
                    }
                }


        } else {
            println("No user is signed in.")
        }
    }
    private fun RLopentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
    }
    private fun RLvalidation(): Boolean {
        firstName = activityBinding.etfirstname.text.toString().trim()
        lastName = activityBinding.etlastname.text.toString().trim()
        nickName = activityBinding.etnickname.text.toString().trim()

        if (firstName.isEmpty()) {
            activityBinding.etfirstname.setError("First name is required.")
            activityBinding.etfirstname.requestFocus()
            return false
        }else if (lastName.isEmpty()) {
            activityBinding.etlastname.setError("Last name is required.")
            activityBinding.etlastname.requestFocus()
            return false
        }else if (nickName.isEmpty()) {
            activityBinding.etnickname.setError("Nick name is required.")
            activityBinding.etnickname.requestFocus()
            return false
        }
        return true
    }
}