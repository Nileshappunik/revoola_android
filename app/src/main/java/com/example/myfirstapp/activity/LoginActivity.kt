package com.example.myfirstapp.activity

import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString

import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityLoginBinding
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class LoginActivity : BaseActivity() {
    val TAG: String = LoginActivity::class.java.simpleName
lateinit var activityBinding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private  val RC_SIGN_IN = 123
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = inflateBindLayout(this, R.layout.activity_login) as ActivityLoginBinding
       //googlelogin
        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //faceboobk ligin
        callbackManager = CallbackManager.Factory.create()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)


       /* try {
            val info = packageManager.getPackageInfo(
                "com.example.myfirstapp",
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())

               val st= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                   Log.e("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))

               } else {
                   Log.e("KeyHash:", "error")
               }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("KeyHash:", "error1= "+e.message)
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash:", "error2= "+e.message)
        }*/


        logoutFromFacebook()

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

    private fun logoutFromFacebook() {
        FirebaseAuth.getInstance().signOut()
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
    }

    private fun facebooklogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    opentoast("Login successful!")
                    // Handle successful login
                    handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
                opentoast("Login cancelled.")
            }

            override fun onError(error: FacebookException) {
                Log.e("FacebookLogin", "Error: ${error.message}")
                opentoast("Login error: ${error.message}")

            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val request = GraphRequest.newMeRequest(token, object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                    try {
                        // Save user email to variable
                        Log.d(TAG, "signInResult= " +"object= "+ obj)
                     /*  val email = obj!!.getString("email")
                        val firstName = obj.getString("first_name")
                        val  lastName = obj.getString("last_name")
                        Log.d(TAG, "signInResult= " + "1) Facebook email received $email and name $firstName $lastName")*/
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    }
                    catch (e: JSONException) {
                        opentoast("Facebook Authentication Failed.")
                    }
                }
            })

        val parameters = Bundle()
        parameters.putString("fields", "email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()

    }

    private fun opentoast(messageprint: String) {
        Toast.makeText(this,messageprint, Toast.LENGTH_SHORT).show()
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
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "signInResult= " + account.email)
            opentoast("Login Successful")
            startActivity(Intent(this,MainActivity::class.java))

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }


}