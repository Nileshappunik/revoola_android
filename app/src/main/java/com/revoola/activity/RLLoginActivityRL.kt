package com.revoola.activity

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.widget.Toast
//import com.revoola.BuildConfig
import com.revoola.activity.base.RLBaseActivity
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlActivityLoginBinding
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.utils.RLPrefManager

class RLLoginActivityRL : RLBaseActivity() {
    val TAG: String = RLLoginActivityRL::class.java.simpleName
    lateinit var activityBinding: RlActivityLoginBinding

    private lateinit var callbackManager: CallbackManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val RC_SIGN_IN = 9001
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        rl_screenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = rl_inflateBindLayout(this, R.layout.rl_activity_login) as RlActivityLoginBinding
       //googlelogin
        FirebaseApp.initializeApp(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()

        //faceboobk login
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
                  RLTools.RlLogEPrint("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))

               } else {
                  RLTools.RlLogEPrint("KeyHash:", "error")
               }
            }
        } catch (e: PackageManager.NameNotFoundException) {
           RLTools.RlLogEPrint("KeyHash:", "error1= "+e.message)
        } catch (e: NoSuchAlgorithmException) {
           RLTools.RlLogEPrint("KeyHash:", "error2= "+e.message)
        }*/

        activityBinding.txtEmailaccount.setOnClickListener {
            startActivity(Intent(this, RLLoginEmailActivityRL::class.java))
        }
        activityBinding.txtGoogleaccount.setOnClickListener {
            rl_googlelogin()
        }
        activityBinding.txtFacebookaccount.setOnClickListener {
            if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken()?.isExpired!!.not()) {
                // User is logged in, perform logout
                LoginManager.getInstance().logOut()
            }else{
                rl_facebooklogin()
            }

        }

    }

    //FACEBOOK LOGIN START
    private fun rl_facebooklogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    // Handle successful login
                    rl_handleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
               RLTools.rl_logEPrint(TAG,"FacebookLogin cancelled.")
            }

            override fun onError(error: FacebookException) {
               RLTools.rl_logEPrint(TAG,"FacebookLogin Error: ${error.message}")
            }
        })
    }
    private fun rl_handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    rl_updateUI(user)
                    // Handle successful login
                } else {
                    // Handle unsuccessful login
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                    rl_updateUI(null)
                }
            }

    }
    //FACEBOOK LOGIN END
    //GOOGLE LOGIN START
    private fun rl_googlelogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            rl_handleSignInResultGoogle(task)
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun rl_handleSignInResultGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            // Signed in successfully, show authenticated UI.
            RLTools.rl_logDPrint(TAG, "signInResult= " + account.email)
            rl_firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           RLTools.rl_logEPrint(TAG, "Google sign in failed Message:-  " + e.localizedMessage)
        }
    }
    private fun rl_firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        try {
            RLTools.rl_logDPrint(TAG, "signInResult idToken:-  " + account.idToken)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        RLTools.rl_logDPrint(TAG, "signInWithCredential:success ID:- "+user)
                        rl_updateUI(user)
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                        rl_updateUI(null)
                    }
                }
        }catch (e:Exception){
           RLTools.rl_logEPrint(TAG,"EXCEPTION:- "+e.message)
        }
    }
    private fun rl_updateUI(user: FirebaseUser?) {
        if (user != null) {
            RLPrefManager.rl_setSomeStringValue(this, RLPrefManager.current_user,user.uid)
            RLPrefManager.rl_setSomeStringValue(this, RLPrefManager.current_user_email,user.email)
            val userid:String= user.uid?:""
            val email:String=user.email?:""
            rl_setUsernameToFirebase(userid,email)
        } else {
            Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
        }
    }
//GOOGLE LOGIN END

    ///FIREBASE Revoola User Setting USer Blanck Entry
    private fun rl_revoolaUserSettingFirebaseEntry(userId:String, emailId:String) {
        val versionName: String = try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        val firebaseManager = RLFirebaseManager()
        firebaseManager.revoola_User_Setting_Firebase_Entry(userId,emailId, versionName) { success ->
            if (success) {
                startActivity(Intent(this, RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",false))//.putExtra("EmailId",emailId).putExtra("Password",password))
                finish()
            }else {
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun rl_setUsernameToFirebase(userId:String, email:String){
        //Firebase To Fetch UserData
        RLDatabaseManagerRead().rl_userBasicDataRead(userId){ data, error ->
            if (data != null) {
                //val gson = Gson()
               // val jsonObject = gson.toJson(data)
              //  val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                val userData = RLTools.parseUserData(data)
                if (userData!!.isBasicDataAdded){
                    startActivity(Intent(this, RLMainActivityRL::class.java))
                    finish()
                }else{
                    rl_revoolaUserSettingFirebaseEntry(userId,email)
                }

            }
        }
    }

}