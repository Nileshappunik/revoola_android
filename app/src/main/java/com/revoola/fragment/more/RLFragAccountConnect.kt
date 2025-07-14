package com.revoola.fragment.more

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revoola.databinding.RlFragAccountConnectBinding

class RLFragAccountConnect : RLBaseFragment() {
    val TAG: String = RLFragAccountConnect::class.java.simpleName
    //lateinit var fragBinding: RlFragAccountConnectBinding
    var emailID: String = ""
    var password: String = ""
    var sucDialog: Dialog? = null

    private val fragBinding by lazy {
        RlFragAccountConnectBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account_connect, container) as RlFragAccountConnectBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccountConnect" )
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
        fragBinding.tvConnect.setOnClickListener(View.OnClickListener {
            if (rl_validation()) {
                if (isAdded){
                    RLBaseProgress.rl_showProgressDialog(requireActivity())
                }
                rl_loginConvertGuestUser(emailID, password)
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

    private fun rl_validation(): Boolean {
        emailID = fragBinding.ivEmailId.text.toString().trim()
        password = fragBinding.ivPassword.text.toString().trim()
        if (!RLTools.rl_isEmailValid(emailID)) {
            rl_showDialog("Please Enter Valid Email and 6+ digit Password.")
            return false
        }else if (password.length<6){
            rl_showDialog("Please Enter 6+ digit Password.")
            return false
        }
        return true
    }

    private fun rl_showDialog(emaildid: String) {
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

    private fun rl_loginConvertGuestUser(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        val user = FirebaseAuth.getInstance().currentUser
        user?.linkWithCredential(credential)
            ?.addOnCompleteListener { task ->
                RLBaseProgress.rl_hideProgressDialog()
                if (task.isSuccessful) {
                   rl_signOut()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        rl_showDialog("Email already in use!")
                    }else{
                        rl_showDialog("Email linking failed: ${task.exception?.message}")
                    }

                }
            }
    }
}