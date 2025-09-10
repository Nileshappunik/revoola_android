package com.revoola.fragment.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragAccountBinding
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revoola.activity.RLMainActivityRL

class RLFragAccount : RLBaseFragment() {
    val TAG: String = RLFragAccount::class.java.simpleName

    private lateinit var callbackManager: CallbackManager

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private val fragBinding by lazy {
        RlFragAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account, container) as RlFragAccountBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccount" )
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
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
            (context as RLMainActivityRL).rl_loadFrag(RLFragAccountConnect(), TAG, true, null, false)
        }
        fragBinding.txtGoogleaccount.setOnClickListener {
            rl_googlelogin()
        }
        fragBinding.txtFacebookaccount.setOnClickListener {
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
        val user = FirebaseAuth.getInstance().currentUser
        user?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                   RLTools.rl_logDPrint(TAG, "Facebook account linked successfully!")
                    val user = auth.currentUser
                    rl_updateUI(user)
                } else {
                    RLTools.rl_logEPrint(TAG, "Facebook linking failed: ${task.exception?.message}")
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(requireContext(), "Facebook account already in use!", Toast.LENGTH_SHORT).show()
                    }
                    rl_updateUI(null)
                }
            }

    }
    //FACEBOOK LOGIN END
    //GOOGLE LOGIN START
    private fun rl_googlelogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
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
                RLTools.rl_logDPrint(TAG, "signInResult idToken:-  ${account.idToken}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val user = FirebaseAuth.getInstance().currentUser
                user?.linkWithCredential(credential)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            RLTools.rl_logDPrint(TAG, "Google account linked successfully!")
                            rl_updateUI(auth.currentUser)
                        } else {
                            RLTools.rl_logEPrint(TAG, "Google linking failed: ${task.exception}")
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(requireContext(), "Google account already in use!", Toast.LENGTH_SHORT).show()
                            }
                            rl_updateUI(null)
                        }
                    }
            } catch (e: Exception) {
                RLTools.rl_logEPrint(TAG, "EXCEPTION: ${e.message}")
            }
        }

    private fun rl_updateUI(user: FirebaseUser?) {
        if (user != null) {
            rl_signOut()
        } else {
            Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_LONG).show()
        }
    }

}