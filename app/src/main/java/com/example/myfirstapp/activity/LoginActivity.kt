package com.example.myfirstapp.activity

import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import java.util.*

class LoginActivity : BaseActivity() {
    val TAG: String = LoginActivity::class.java.simpleName
lateinit var activityBinding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private  val RC_SIGN_IN = 123
    private  val FB_SIGN_IN = 124
    //private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_login) as ActivityLoginBinding
       //googlelogin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //faceboobk ligin
        //FacebookSdk.sdkInitialize(applicationContext)
        //AppEventsLogger.activateApp(this)
       // callbackManager = CallbackManager.Factory.create()


        activityBinding.txtEmailaccount.setOnClickListener {
            startActivity(Intent(this,LoginEmailActivity::class.java))
        }
        activityBinding.txtGoogleaccount.setOnClickListener {
           googlelogin()
        }
        activityBinding.txtFacebookaccount.setOnClickListener {
           facebooklogin()
        }

    }

    private fun facebooklogin() {
        /*LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))*/

       /* LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    Toast.makeText(this@MainActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Handle successful login
                }
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login cancelled.", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Log.e("FacebookLogin", "Error: ${error.message}")
                Toast.makeText(this@MainActivity, "Login error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })*/
    }
    private fun googlelogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }else if (requestCode == FB_SIGN_IN) {
            //callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "signInResult= " + account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }


}