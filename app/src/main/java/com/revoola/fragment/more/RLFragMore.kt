package com.revoola.fragment.more

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragMoreBinding
import com.revoola.fragment.more.adapter.RlMoreExpandableListAdapter
import com.revoola.model.RLMoreGroupItemModel
import com.revoola.utils.RLConstants
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager

class RLFragMore : RLBaseFragment() {
    val TAG: String = RLFragMore::class.java.simpleName

    private val fragBinding by lazy {
        RlFragMoreBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMore" )
        RLsetupuiList()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.RLshowAlertDialog(requireContext(),requireActivity())
            }
        })
        return fragBinding.root
    }

    private fun RLsetupuiList(){
        fragBinding.cardNotification.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, null, false)
        }
        fragBinding.cardSchdualedclasses.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragScheduledClasses(), TAG, true,null, false)
        }
        val groupDataList = listOf(RLMoreGroupItemModel(R.drawable.ic_account_g,resources.getString(R.string.profile), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_settings_g,resources.getString(R.string.application_setting),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_permission,resources.getString(R.string.permission),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_purchase,resources.getString(R.string.purchase),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_sign_out_g,resources.getString(R.string.signout), emptyList()))
        // Set up the Adapter
        val adapter = RlMoreExpandableListAdapter(requireContext(), groupDataList)
        fragBinding.expandableListView.setAdapter(adapter)
        fragBinding.expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            // Handle group click if needed
            when(groupDataList[groupPosition].title){
                resources.getString(R.string.profile)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragProfile(), TAG, true, null, false)
                }
                resources.getString(R.string.application_setting)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragApplicationSetting(), TAG, true, null, false)
                }
                resources.getString(R.string.permission)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragPermission(), TAG, true, null, false)
                }
                resources.getString(R.string.purchase)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragPurchase(), TAG, true, null, false)
                }
                resources.getString(R.string.signout)->{
                    RLshowDialog(RLConstants.LOGOUT_D,getString(R.string.exit_app))
                }
            }
            false
        }

    }

    private fun RLshowDialog(type: String, message: String) {
        val sucDialog: Dialog = Dialog(requireContext())
        sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        sucDialog.setContentView(R.layout.rl_layout_dailog)
        sucDialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog.findViewById(R.id.tvYes)
        val tvTitle: TextView = sucDialog.findViewById(R.id.tvTitle)
        val tvSubTitle: TextView = sucDialog.findViewById(R.id.tvSubTitle)
        tvSubTitle.setText(message)

        tvNo.setOnClickListener(View.OnClickListener {
           sucDialog.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            sucDialog.dismiss()
            if (type.equals(RLConstants.LOGOUT_D)){
                RLSignOut()
            }
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }

}