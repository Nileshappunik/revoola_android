package com.revoola.fragment.more

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.databinding.RlFragAccountBinding
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revoola.activity.RLLoginEmailActivityRL
import com.revoola.activity.RLMainActivityRL
import com.revoola.activity.RLSignUpNameActivityRL
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databasefirebase.RLFirebaseManager
import com.revoola.databinding.RlFragAccountConnectBinding
import com.revoola.fragment.more.adapter.PaywallItem
import com.revoola.fragment.more.adapter.RLPaywallAdapter
import com.revoola.model.RLRevoolaUsersSettingsModel

class RLFragAccountConnect : RLBaseFragment() {
    val TAG: String = RLFragAccountConnect::class.java.simpleName
    lateinit var fragBinding: RlFragAccountConnectBinding
    var emailID: String = ""
    var password: String = ""
    var sucDialog: Dialog? = null

    private val binding by lazy {
        RlFragAccountConnectBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account_connect, container) as RlFragAccountConnectBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccountConnect" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.tvConnect.setOnClickListener(View.OnClickListener {
            if (RLvalidation()) {
                if (isAdded){
                    RLBaseProgress.RLShowProgressDialog(requireActivity())
                }
                RLLoginConvertGuestUser(emailID, password)
            }
        })

        fragBinding.ivEmailId.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    fragBinding.tvConnect.visibility=View.GONE
                    fragBinding.tvConnectNoClick.visibility=View.VISIBLE
                }else if ( fragBinding.ivPassword.text.toString().isNotEmpty()){
                    fragBinding.tvConnect.visibility=View.VISIBLE
                    fragBinding.tvConnectNoClick.visibility=View.GONE
                }else{
                    fragBinding.tvConnect.visibility=View.GONE
                    fragBinding.tvConnectNoClick.visibility=View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        fragBinding.ivPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length ==0) {
                    fragBinding.tvConnect.visibility=View.GONE
                    fragBinding.tvConnectNoClick.visibility=View.VISIBLE
                }else if ( fragBinding.ivEmailId.text.toString().isNotEmpty()){
                    fragBinding.tvConnect.visibility=View.VISIBLE
                    fragBinding.tvConnectNoClick.visibility=View.GONE
                }else{
                    fragBinding.tvConnect.visibility=View.GONE
                    fragBinding.tvConnectNoClick.visibility=View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun RLvalidation(): Boolean {
        emailID = fragBinding.ivEmailId.text.toString().trim()
        password = fragBinding.ivPassword.text.toString().trim()
        if (!RLTools.RLisEmailValid(emailID)) {
            RLshowDialog("Please Enter Valid Email and 6+ digit Password.")
            return false
        }else if (password.length<6){
            RLshowDialog("Please Enter 6+ digit Password.")
            return false
        }
        return true
    }

    private fun RLshowDialog( emaildid: String) {
        sucDialog = Dialog(requireContext())
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog!!.setContentView(R.layout.rl_dailog_login_error)
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvTitle: TextView = sucDialog!!.findViewById(R.id.tvTitle)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.tvYes)

        tvTitle.setText(emaildid)

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

    private fun RLLoginConvertGuestUser(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        val user = FirebaseAuth.getInstance().currentUser
        user?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                RLBaseProgress.RLhideProgressDialog()
                if (task.isSuccessful) {
                   RLSignOut()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        RLshowDialog("Email already in use!")
                    }else{
                        RLshowDialog("Email linking failed: ${task.exception?.message}")
                    }

                }
            }
    }
}