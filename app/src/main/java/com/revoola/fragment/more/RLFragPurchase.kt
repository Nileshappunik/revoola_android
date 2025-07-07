package com.revoola.fragment.more

import android.app.Dialog
import android.content.res.ColorStateList
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
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databinding.*
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLPrefManager

class RLFragPurchase : RLBaseFragment() {
    val TAG: String = RLFragPurchase::class.java.simpleName
    lateinit var fragBinding: RlFragPurchaseBinding

    
    private val binding by lazy {
        RlFragPurchaseBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_purchase, container) as RlFragPurchaseBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragPurchase")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)

        if (RLPrefManager.RLGetGuestUser(requireContext())){
            fragBinding.layTryPremiumForFree.relayUser.visibility=View.VISIBLE
        }else{
            fragBinding.layTryPremiumForFree.relayUser.visibility=View.GONE
        }

        fragBinding.layCurrentSubscription.txtusertitle.setText(R.string.current_subscription)
        fragBinding.layCurrentSubscription.txtUsername.visibility=View.GONE
        fragBinding.layCurrentSubscription.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layCurrentSubscription.relayUser.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragCurrentSubScription(), TAG, true, null, false)
        }

        fragBinding.layTryPremiumForFree.txtusertitle.setText(R.string.trypremiumforfree)
        fragBinding.layTryPremiumForFree.txtUsername.visibility=View.GONE
        fragBinding.layTryPremiumForFree.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layTryPremiumForFree.relayUser.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragAccount(), TAG, true, null, false)
        }
        fragBinding.layRestoreYourPurchase.txtusertitle.setText(R.string.restorepurchase)
        fragBinding.layRestoreYourPurchase.txtUsername.visibility=View.GONE
        fragBinding.layRestoreYourPurchase.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layRestoreYourPurchase.relayUser.setOnClickListener {
            RLshowDialogAlert(getString(R.string.youhavesuccessfullyrestored))
        }

        fragBinding.layViewTransaction.txtusertitle.setText(R.string.viewtransation)
        fragBinding.layViewTransaction.txtUsername.visibility=View.GONE
        fragBinding.layViewTransaction.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layViewTransaction.relayUser.setOnClickListener {
         //click ViewTransaction
        }

    }

    private fun RLshowDialogAlert( message: String) {
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