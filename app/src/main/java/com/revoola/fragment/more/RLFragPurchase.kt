package com.revoola.fragment.more

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.*
import com.revoola.utils.RLPrefManager

class RLFragPurchase : RLBaseFragment() {
    private val TAG: String = RLFragPurchase::class.java.simpleName

    private val fragBinding by lazy {
        RlFragPurchaseBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragPurchase")
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
        if (RLPrefManager.rl_getGuestUser(requireContext())){
            fragBinding.layTryPremiumForFree.relayUser.visibility=View.VISIBLE
        }else{
            fragBinding.layTryPremiumForFree.relayUser.visibility=View.GONE
        }
        fragBinding.layCurrentSubscription.txtusertitle.setText(R.string.current_subscription)
        fragBinding.layCurrentSubscription.txtUsername.visibility=View.GONE
        fragBinding.layCurrentSubscription.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layCurrentSubscription.relayUser.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragCurrentSubScription(), TAG, true, null, false)
        }
        fragBinding.layTryPremiumForFree.txtusertitle.setText(R.string.trypremiumforfree)
        fragBinding.layTryPremiumForFree.txtUsername.visibility=View.GONE
        fragBinding.layTryPremiumForFree.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layTryPremiumForFree.relayUser.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragAccount(), TAG, true, null, false)
        }
        fragBinding.layRestoreYourPurchase.txtusertitle.setText(R.string.restorepurchase)
        fragBinding.layRestoreYourPurchase.txtUsername.visibility=View.GONE
        fragBinding.layRestoreYourPurchase.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layRestoreYourPurchase.relayUser.setOnClickListener {
            rl_showDialogAlert(getString(R.string.youhavesuccessfullyrestored))
        }
        fragBinding.layViewTransaction.txtusertitle.setText(R.string.viewtransation)
        fragBinding.layViewTransaction.txtUsername.visibility=View.GONE
        fragBinding.layViewTransaction.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layViewTransaction.relayUser.setOnClickListener {
        //click ViewTransaction
        }
    }

    private fun rl_showDialogAlert(message: String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_layout_dailog_alert)
        sucDialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvSubTitle: TextView = sucDialog.findViewById(R.id.tvSubTitle)
        val tvOk: TextView = sucDialog.findViewById(R.id.tvOk)
        tvSubTitle.setText(message)
        tvOk.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }


}