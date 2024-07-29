package com.example.myfirstapp.fragment.more

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragMoreBinding
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RLFragMore : RLBaseFragment() {
    val TAG: String = RLFragMore::class.java.simpleName
    lateinit var fragBinding: RlFragMoreBinding

    private val binding by lazy {
        RlFragMoreBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_more, container) as RlFragMoreBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMore" )
        RLsetupui()
        return fragBinding.root
    }

    private fun RLsetupui() {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()

        //fragBinding.layAccount.layMoreClick.visibility=View.GONE
        fragBinding.layAccount.txtAccount.setText(R.string.account)
        fragBinding.layAccount.imgAccount.setImageResource(R.drawable.ic_account_g)

        fragBinding.laySetting.txtAccount.setText(R.string.settings)
        fragBinding.laySetting.imgAccount.setImageResource(R.drawable.ic_settings_g)

        fragBinding.layChangepassword.txtAccount.setText(R.string.changepassword)
        fragBinding.layChangepassword.imgAccount.setImageResource(R.drawable.ic_envelope_g)

        fragBinding.layHelp.txtAccount.setText(R.string.help)
        fragBinding.layHelp.imgAccount.setImageResource(R.drawable.ic_help_g)

        fragBinding.laySyncwatchdara.txtAccount.setText(R.string.syncwatchdata)
        fragBinding.laySyncwatchdara.imgAccount.setImageResource(R.drawable.ic_sensors_g)

        fragBinding.layRestorepurchase.txtAccount.setText(R.string.restorepurchase)
        fragBinding.layRestorepurchase.imgAccount.setImageResource(R.drawable.ic_help_g)

        fragBinding.layRequesttodeletedata.txtAccount.setText(R.string.requesttodeleteyourdata)
        fragBinding.layRequesttodeletedata.imgAccount.setImageResource(R.drawable.ic_help_g)

        fragBinding.laySignout.txtAccount.setText(R.string.signout)
        fragBinding.laySignout.imgAccount.setImageResource(R.drawable.ic_sign_out_g)

        fragBinding.cardNotification.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, RLFragNotification::class.java.simpleName, false)
        }
        fragBinding.cardSchdualedclasses.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragScheduledClasses(), TAG, true, RLFragScheduledClasses::class.java.simpleName, false)
        }

        fragBinding.layChangepassword.layMoreClick.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragChangePassword(), TAG, true, RLFragChangePassword::class.java.simpleName, false)
        }
        fragBinding.layAccount.layMoreClick.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragAccount(), TAG, true, RLFragAccount::class.java.simpleName, false)
        }

        fragBinding.layHelp.layMoreClick.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragHelp(), TAG, true, RLFragHelp::class.java.simpleName, false)
        }

        fragBinding.laySetting.layMoreClick.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragSetting(), TAG, true, RLFragSetting::class.java.simpleName, false)
        }
        fragBinding.layRequesttodeletedata.layMoreClick.setOnClickListener {
            RLshowDialog(RLConstants.EXIT,RLConstants.SCHEDULE)
        }
        fragBinding.laySignout.layMoreClick.setOnClickListener {
            RLshowDialog(RLConstants.LOGOUT_D,RLConstants.SCHEDULE)
        }
        /*fragBinding.layConnecttohealthconnect.layMoreClick.setOnClickListener {
            RLshowBasicAlertDialog()
        }*/
    }

    private fun RLshowBasicAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.youalreadyconnecthealth))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss() // Dismisses the dialog when the button is clicked
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun RLshowDialog(type: String, schedule: String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (schedule == RLConstants.SCHEDULE) {
            sucDialog.setContentView(R.layout.rl_layout_dailog)
        }
        sucDialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        tvNo.setText(R.string.cancel)
        tvYes.setText(R.string.confirm)

        if(type.equals(RLConstants.EXIT)) {
            val tvSubTitle: TextView = sucDialog.findViewById(R.id.tvSubTitle)
            val tvTitle: TextView = sucDialog.findViewById(R.id.tvTitle)
            tvTitle.text =""
            tvSubTitle.text = resources.getString(R.string.areyousurewanttodeletedata)
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
            Firebase.auth.signOut()
            RLPrefManager.RLsetSomeStringValue(requireContext(), RLPrefManager.current_user,"")
            activity?.finish()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
}