package com.example.myfirstapp.fragment.more

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragMoreBinding
import com.example.myfirstapp.fragment.more.adapter.RlMoreExpandableListAdapter
import com.example.myfirstapp.model.RLMoreGroupItemModel
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
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_more, container) as RlFragMoreBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMore" )
        RLsetupuiList()
        return fragBinding.root
    }

    private fun RLsetupuiList(){
        fragBinding.cardNotification.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, null, false)
        }
        fragBinding.cardSchdualedclasses.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragScheduledClasses(), TAG, true,null, false)
        }

        // Prepare the data
        val groupList = listOf(
            RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.helpvideotutorials), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_sensors_g,resources.getString(R.string.syncwatchdata),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_settings_g,resources.getString(R.string.edit_your_account_data), listOf("CHANGE YOUR APP SETTINGS", "CHANGE YOUR PASSWORD", "RESTORE YOUR PURCHASES","REQUEST TO DELETE YOUR DATA")),
            RLMoreGroupItemModel(R.drawable.ic_sign_out_g,resources.getString(R.string.signout), emptyList()))

        // Set up the adapter
        val adapter = RlMoreExpandableListAdapter(requireContext(), groupList)
        fragBinding.expandableListView.setAdapter(adapter)

        // Optionally: Set listeners for group and child clicks
        fragBinding.expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            // Handle group click if needed
            when(groupList[groupPosition].title){
                resources.getString(R.string.helpvideotutorials)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragHelp(), TAG, true, null, false)
                }
                resources.getString(R.string.syncwatchdata)->{
                   // (context as RLMainActivityRL).RLloadFrag(RLFragAccount(), TAG, true, null, false)
                }
                resources.getString(R.string.signout)->{
                    RLshowDialog(RLConstants.LOGOUT_D,getString(R.string.exit_app))
                }
            }
            false
        }

        fragBinding.expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            // Handle child click if needed
            Log.e(TAG,"CHILDNAME:- ${groupList[groupPosition].childItems[childPosition].toString()}")
            when(groupList[groupPosition].childItems[childPosition].toString())
            {
                "CHANGE YOUR APP SETTINGS"->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragSetting(), TAG, true, null, false)
                }
                "RESTORE YOUR PURCHASES"->{
                    RLshowDialogAlert(getString(R.string.youhavesuccessfullyrestored))
                }
                "CHANGE YOUR PASSWORD"->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragChangePassword(), TAG, true, null, false)
                }
                "REQUEST TO DELETE YOUR DATA"->{
                    RLshowDialog(RLConstants.EXIT,getString(R.string.areyousurewanttodeletedata))
                }
            }
            false
        }
    }


    private fun RLshowBasicAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.youhavesuccessfullyrestored))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
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
                Firebase.auth.signOut()
                RLPrefManager.RLsetSomeStringValue(requireContext(), RLPrefManager.current_user,"")
                activity?.finish()
            }
        })
        sucDialog.show()
        sucDialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
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