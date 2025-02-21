package com.revoola.fragment.more

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.wearable.Wearable
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragMoreBinding
import com.revoola.fragment.more.adapter.RlMoreExpandableListAdapter
import com.revoola.model.RLMoreGroupItemModel
import com.revoola.utils.RLConstants
import com.revoola.RLBaseProgress
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.model.RLWatchModel
import com.revoola.utils.RLPrefManager
import com.revoola.watch.RLWearDataSync

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

        val editAccountChildDataList = mutableListOf("CURRENT SUBSCRIPTION","CHANGE YOUR APP SETTINGS", "CHANGE YOUR PASSWORD", "RESTORE YOUR PURCHASES","REQUEST TO DELETE YOUR DATA")

        if (RLPrefManager.RLGetGuestUser(requireContext())){
            editAccountChildDataList.add("TRY PREMIUM FOR FREE")
        }
        // Prepare the Data
        val groupDataList = listOf(RLMoreGroupItemModel(R.drawable.ic_help_g,resources.getString(R.string.helpvideotutorials), emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_sensors_g,resources.getString(R.string.syncwatchdata),emptyList()),
            RLMoreGroupItemModel(R.drawable.ic_settings_g,resources.getString(R.string.edit_your_account_data),editAccountChildDataList),
            RLMoreGroupItemModel(R.drawable.ic_sign_out_g,resources.getString(R.string.signout), emptyList()))

        // Set up the Adapter
        val adapter = RlMoreExpandableListAdapter(requireContext(), groupDataList)
        fragBinding.expandableListView.setAdapter(adapter)
        fragBinding.expandableListView.expandGroup(2)
        // Optionally: Set listeners for group and child clicks
        fragBinding.expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            // Handle group click if needed
            when(groupDataList[groupPosition].title){
                resources.getString(R.string.helpvideotutorials)->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragHelp(), TAG, true, null, false)
                }
                resources.getString(R.string.syncwatchdata)->{
                    // Initialize WearDataSync with DataClient
                    RLSyncWatchData()

                }
                resources.getString(R.string.signout)->{
                    RLshowDialog(RLConstants.LOGOUT_D,getString(R.string.exit_app))
                }
            }
            false
        }

        fragBinding.expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            // Handle child click if needed
            when(groupDataList[groupPosition].childItems[childPosition].toString())
            {
                "CURRENT SUBSCRIPTION"->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragCurrentSubScription(), TAG, true, null, false)
                }
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
                "TRY PREMIUM FOR FREE"->{
                    (context as RLMainActivityRL).RLloadFrag(RLFragAccount(), TAG, true, null, false)
                }
            }
            false
        }
    }

    private fun RLSyncWatchData() {
        if (isAdded){
            RLBaseProgress.RLShowProgressDialog(requireActivity())
        }
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                val uid= RLAuthManager().RlgetCurrentUser()?.uid?:""
                val weight=userData.weightkg?:"60"
                val height =userData.height?:"167"
                val dob= userData.dob
                val gender=userData.gender
                val RFMHR=userData.RFMHR
                val restingHr=userData.restingHr
                val userDataTransfer = RLWatchModel(uid, weight, height, dob, gender, RFMHR, restingHr)
                if (isAdded){
                    //val  wearDataSync = WearDataSync(Wearable.getDataClient(requireContext()))
                    val wearDataSync = RLWearDataSync(Wearable.getDataClient(requireContext()),Wearable.getNodeClient(requireContext()))
                    wearDataSync.sendUserDataToWatch(userDataTransfer){ isSuccess, message ->
                        if (isSuccess) {
                            RLBaseProgress.RLhideProgressDialog()
                           RLTools.RlLogDPrint(TAG, "WearDataSync Success: $message")
                            // Handle success (e.g., update UI)
                        } else {
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogEPrint(TAG, "WearDataSync Error: $message")
                            // Handle failure (e.g., show error message to the user)
                        }
                    }
                }else{
                    RLBaseProgress.RLhideProgressDialog()
                }

            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
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
               /* Firebase.auth.signOut()
                 MoECoreHelper.logoutUser(requireContext())
              // RLPrefManager.RLsetSomeStringValue(requireContext(), RLPrefManager.current_user,"")
               RLPrefManager.RLClear_all(requireContext())
                val intent = Intent(requireContext(), RLSplashActivityRL::class.java)
                startActivity(intent)
                activity?.finish()*/
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

    private fun RLshowBasicAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.youhavesuccessfullyrestored))
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
}