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
import com.revoola.model.RLRevoolaUsersSettingsModel
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
import com.google.gson.Gson
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
        RLScreenSet(false)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityBinding = RLinflateBindLayout(this, R.layout.rl_activity_login) as RlActivityLoginBinding
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
            RLgooglelogin()
        }
        activityBinding.txtFacebookaccount.setOnClickListener {
            if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken()?.isExpired!!.not()) {
                // User is logged in, perform logout
                LoginManager.getInstance().logOut()
            }else{
                RLfacebooklogin()
            }

        }

    }

    //FACEBOOK LOGIN START
    private fun RLfacebooklogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val accessToken = AccessToken.getCurrentAccessToken()
                if (accessToken != null && !accessToken.isExpired) {
                    // Handle successful login
                    RLhandleFacebookAccessToken(result.accessToken)
                }
            }

            override fun onCancel() {
               RLTools.RlLogEPrint(TAG,"FacebookLogin cancelled.")
            }

            override fun onError(error: FacebookException) {
               RLTools.RlLogEPrint(TAG,"FacebookLogin Error: ${error.message}")
            }
        })
    }
    private fun RLhandleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    RLupdateUI(user)
                    // Handle successful login
                } else {
                    // Handle unsuccessful login
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                    RLupdateUI(null)
                }
            }

    }
    //FACEBOOK LOGIN END
    //GOOGLE LOGIN START
    private fun RLgooglelogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            RLhandleSignInResultGoogle(task)
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun RLhandleSignInResultGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            // Signed in successfully, show authenticated UI.
            RLTools.RlLogDPrint(TAG, "signInResult= " + account.email)
            RLfirebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           RLTools.RlLogEPrint(TAG, "Google sign in failed Message:-  " + e.localizedMessage)
        }
    }
    private fun RLfirebaseAuthWithGoogle(account: GoogleSignInAccount) {
        try {
            RLTools.RlLogDPrint(TAG, "signInResult idToken:-  " + account.idToken)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        RLTools.RlLogDPrint(TAG, "signInWithCredential:success ID:- "+user)
                        RLupdateUI(user)
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                        RLupdateUI(null)
                    }
                }
        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"EXCEPTION:- "+e.message)
        }
    }
    private fun RLupdateUI(user: FirebaseUser?) {
        if (user != null) {
            RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user,user.uid)
            RLPrefManager.RLSetSomeStringValue(this, RLPrefManager.current_user_email,user.email)
            val userid:String= user.uid?:""
            val email:String=user.email?:""
            RLSetUsernameToFirebase(userid,email)
        } else {
            Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
        }
    }
//GOOGLE LOGIN END

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
                startActivity(Intent(this, RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",false))//.putExtra("EmailId",emailId).putExtra("Password",password))
                finish()
            }else {
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun RLSetUsernameToFirebase(userId:String,email:String){
        //Firebase To Fetch UserData
        RLDatabaseManagerRead().RlUserBasicDataRead(userId){ data, error ->
            if (data != null) {
                //val gson = Gson()
               // val jsonObject = gson.toJson(data)
              //  val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                val userData = RLTools.parseUserData(data)
                if (userData!!.isBasicDataAdded){
                    startActivity(Intent(this, RLMainActivityRL::class.java))
                    finish()
                }else{
                    RLRevoolaUserSettingFirebaseEntry(userId,email)
                }

            }
        }
    }

}