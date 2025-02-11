package com.revoola.fragment.more

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragAccountBinding
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revoola.activity.RLLoginEmailActivityRL
import com.revoola.activity.RLMainActivityRL
import com.revoola.activity.RLSignUpNameActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.fragment.more.adapter.PaywallItem
import com.revoola.fragment.more.adapter.RLPaywallAdapter
import com.revoola.model.RLRevoolaUsersSettingsModel

class RLFragAccount : RLBaseFragment() {
    val TAG: String = RLFragAccount::class.java.simpleName
    lateinit var fragBinding: RlFragAccountBinding

    private lateinit var callbackManager: CallbackManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val binding by lazy {
        RlFragAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account, container) as RlFragAccountBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccount" )
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        //googlelogin
        FirebaseApp.initializeApp(requireContext())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        auth = FirebaseAuth.getInstance()

        //faceboobk login
        callbackManager = CallbackManager.Factory.create()
        FacebookSdk.sdkInitialize(requireContext())
       // AppEventsLogger.activateApp(requireActivity())

        fragBinding.txtEmailaccount.setOnClickListener {
            startActivity(Intent(requireActivity(), RLLoginEmailActivityRL::class.java))
            requireActivity().finish()
        }
        fragBinding.txtGoogleaccount.setOnClickListener {
            RLgooglelogin()
        }
        fragBinding.txtFacebookaccount.setOnClickListener {
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
        val user = FirebaseAuth.getInstance().currentUser
        user?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   RLTools.RlLogDPrint(TAG, "Facebook account linked successfully!")
                    val user = auth.currentUser
                    RLupdateUI(user)
                } else {
                    RLTools.RlLogEPrint(TAG, "Facebook linking failed: ${task.exception?.message}")
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(requireContext(), "Facebook account already in use!", Toast.LENGTH_SHORT).show()
                    }
                    RLupdateUI(null)
                }
            }

    }
    //FACEBOOK LOGIN END
    //GOOGLE LOGIN START
    private fun RLgooglelogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            RLhandleSignInResultGoogle(task)
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun RLhandleSignInResultGoogleOLd(completedTask: Task<GoogleSignInAccount>) {
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

    private fun RLhandleSignInResultGoogle(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            RLTools.RlLogDPrint(TAG, "Google Sign In successful: ${account.email}")
            RLfirebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in cancelled"
                GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error occurred"
                GoogleSignInStatusCodes.INVALID_ACCOUNT -> "Invalid account"
                GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> "Sign in required"
                else -> "Google sign in failed: ${e.statusCode}"
            }
            RLTools.RlLogEPrint(TAG, errorMessage)
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun RLfirebaseAuthWithGoogle(account: GoogleSignInAccount) {
            try {
                RLTools.RlLogDPrint(TAG, "signInResult idToken:-  ${account.idToken}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val user = FirebaseAuth.getInstance().currentUser
                user?.linkWithCredential(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RLTools.RlLogDPrint(TAG, "Google account linked successfully!")
                            RLupdateUI(auth.currentUser)
                        } else {
                            RLTools.RlLogEPrint(TAG, "Google linking failed: ${task.exception}")
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(requireContext(), "Google account already in use!", Toast.LENGTH_SHORT).show()
                            }
                            RLupdateUI(null)
                        }
                    }
            } catch (e: Exception) {
                RLTools.RlLogEPrint(TAG, "EXCEPTION: ${e.message}")
            }
        }

    private fun RLupdateUI(user: FirebaseUser?) {
        if (user != null) {
            RLPrefManager.RLSetSomeStringValue(requireContext(), RLPrefManager.current_user,user.uid)
            RLPrefManager.RLSetSomeStringValue(requireContext(), RLPrefManager.current_user_email,user.email)
            val userid:String= user.uid?:""
            val email:String=user.email?:""
            RLSetUsernameToFirebase(userid,email)
        } else {
            Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_LONG).show()
        }
    }
   //GOOGLE LOGIN END

    ///FIREBASE Revoola User Setting USer Blanck Entry
    private fun RLRevoolaUserSettingFirebaseEntry(userId:String,emailId:String) {
        val versionName: String = try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionName ?: "0"
        } catch (e: Exception) {
            "0"
        }
        val firebaseManager = RLFirebaseManager()
        firebaseManager.RLRevoolaUserSettingFirebaseEntry(userId,emailId, versionName) { success ->
            if (success) {
                startActivity(Intent(requireActivity(), RLSignUpNameActivityRL::class.java).putExtra("IsNewUser",false))
                requireActivity().finish()
            }else {
                Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun RLSetUsernameToFirebase(userId:String,email:String){
        //Firebase To Fetch UserData
        RLDatabaseManagerRead().RlUserBasicDataRead(userId){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val userData = gson.fromJson(jsonObject, RLRevoolaUsersSettingsModel::class.java)
                if (userData.isBasicDataAdded){
                  RLSignOut()
                }else{
                    RLRevoolaUserSettingFirebaseEntry(userId,email)
                }

            }
        }
    }



}