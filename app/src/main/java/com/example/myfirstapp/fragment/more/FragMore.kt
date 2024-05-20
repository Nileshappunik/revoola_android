package com.example.myfirstapp.fragment.more

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.databinding.FragMoreBinding
import com.example.myfirstapp.utils.Constants
import com.example.myfirstapp.utils.PrefManager

class FragMore : BaseFragment() {
    val TAG: String = FragMore::class.java.simpleName
    lateinit var fragBinding: FragMoreBinding

    private val binding by lazy {
        FragMoreBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_more, container) as FragMoreBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragMore" )
        setupui()
        return fragBinding.root
    }

    private fun setupui() {
        fragBinding.laySetting.imgAccount.setImageResource(R.drawable.ic_settings)
        fragBinding.laySetting.txtAccount.setText(R.string.settings)

        fragBinding.layChangepassword.imgAccount.setImageResource(R.drawable.ic_change_password)
        fragBinding.layChangepassword.txtAccount.setText(R.string.changepassword)

        fragBinding.layHelp.imgAccount.setImageResource(R.drawable.ic_help)
        fragBinding.layHelp.txtAccount.setText(R.string.help)

        fragBinding.layConnecttohealthconnect.imgAccount.setImageResource(R.drawable.ic_person)
        fragBinding.layConnecttohealthconnect.txtAccount.setText(R.string.connecttohealthconnect)

        fragBinding.layRequesttodeletedata.imgAccount.setImageResource(R.drawable.ic_help)
        fragBinding.layRequesttodeletedata.txtAccount.setText(R.string.requesttodeleteyourdata)

        fragBinding.layLinktoaccount.imgAccount.setImageResource(R.drawable.ic_person)
        fragBinding.layLinktoaccount.txtAccount.setText(R.string.linktoaccount)

        fragBinding.laySignout.imgAccount.setImageResource(R.drawable.ic_signout)
        fragBinding.laySignout.txtAccount.setText(R.string.signout)

        fragBinding.cardNotification.setOnClickListener {
            (context as MainActivity).loadFrag(FragNotification(), TAG, true, FragNotification::class.java.simpleName, false)
        }
        fragBinding.cardSchdualedclasses.setOnClickListener {
            (context as MainActivity).loadFrag(FragScheduledClasses(), TAG, true, FragScheduledClasses::class.java.simpleName, false)
        }

        fragBinding.layChangepassword.layMoreClick.setOnClickListener {
            (context as MainActivity).loadFrag(FragChangePassword(), TAG, true, FragChangePassword::class.java.simpleName, false)
        }
        fragBinding.layAccount.layMoreClick.setOnClickListener {
            (context as MainActivity).loadFrag(FragAccount(), TAG, true, FragAccount::class.java.simpleName, false)
        }

        fragBinding.layHelp.layMoreClick.setOnClickListener {
            (context as MainActivity).loadFrag(FragHelp(), TAG, true, FragHelp::class.java.simpleName, false)
        }

        fragBinding.laySetting.layMoreClick.setOnClickListener {
            (context as MainActivity).loadFrag(FragSetting(), TAG, true, FragSetting::class.java.simpleName, false)
        }
        fragBinding.layRequesttodeletedata.layMoreClick.setOnClickListener {
            showDialog(Constants.EXIT,Constants.SCHEDULE)
        }
        fragBinding.laySignout.layMoreClick.setOnClickListener {
            showDialog(Constants.LOGOUT_D,Constants.SCHEDULE)
        }
        fragBinding.layConnecttohealthconnect.layMoreClick.setOnClickListener {
            showBasicAlertDialog()
        }
    }

    private fun showBasicAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.youalreadyconnecthealth))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss() // Dismisses the dialog when the button is clicked
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showDialog(type: String, schedule: String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (schedule == Constants.SCHEDULE) {
            sucDialog.setContentView(R.layout.layout_dailog)
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

        if(type.equals(Constants.EXIT)) {
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
            activity?.finish()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
}