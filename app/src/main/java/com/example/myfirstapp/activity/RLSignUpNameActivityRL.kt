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
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RLSignUpNameActivityRL : RLBaseActivity(){

    val TAG: String = RLSignUpNameActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivitySignUpNameBinding
    var firstName: String = ""
    var lastName: String = ""
    var nickName: String = ""
    var emailID: String = ""
    var password: String = ""
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
        emailID= intent.getStringExtra("EmailId").toString()
         password= intent.getStringExtra("Password").toString()
        activityBinding.toolbarLogin.tvTitle.setText(R.string.signup)
        activityBinding.toolbarLogin.ivBack.visibility= View.VISIBLE
        RLonBackPresAct(activityBinding.toolbarLogin.ivBack)
        activityBinding.tvLogin.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                RlupdateUserDetails()
            }
        })

    }

    fun RlupdateUserDetails() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
         // Sign up with email and password
        auth.createUserWithEmailAndPassword(emailID,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User creation successful
                    val user = auth.currentUser
                    val userId = user?.uid
                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "nickname" to nickName,
                        "email" to emailID)
                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user,userId)
                                RLPrefManager.RLsetSomeStringValue(this, RLPrefManager.current_user_email,emailID)
                                startActivity(Intent(this, RLSignUpActivityRL::class.java).putExtra("EmailId",emailID).putExtra("Password",password))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                RLopentoast( "Error saving user information: ${e.message}")
                            }
                    }
                } else {
                    // User creation failed
                    RLopentoast("User creation failed: ${task.exception?.message}")
                }
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