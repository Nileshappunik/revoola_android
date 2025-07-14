package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.wearable.Wearable
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.RLBaseProgress
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.*
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.model.RLWatchModel
import com.revoola.utils.RLPrefManager
import com.revoola.watch.RLWearDataSync

class RLFragApplicationSetting : RLBaseFragment()  {
    val TAG: String = RLFragApplicationSetting::class.java.simpleName
   // lateinit var fragBinding: RlFragApplicationSettingBinding
    private var userBasicDataCard: RLRevoolaUsersSettingsModel? =null

    private val fragBinding by lazy {
        RlFragApplicationSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_application_setting, container) as RlFragApplicationSettingBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSetting" )
        rl_uiSetUp()
        return fragBinding.root
    }

    //All Design Setup Like Button Click And All
    private fun rl_uiSetUp() {
        rl_onBackPresAct(fragBinding.ivBack)
        //Firebase To Fetch UserData
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                userBasicDataCard=userData
                if ( userData.appUnit.toLowerCase().equals("imperial")){
                    fragBinding.radioGroup.check(R.id.radioButtonimperial)
                }else{
                    fragBinding.radioGroup.check(R.id.radioButtonmetric)
                }

            } else {
               RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        fragBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonimperial)) {
                rl_basicDataUpdateToFirebase("appUnit","Imperial")
            }else {
                rl_basicDataUpdateToFirebase("appUnit","Metric")
            }
        }

        fragBinding.layHelpAndVideoTutorial.txtusertitle.setText(R.string.helpvideotutorials)
        fragBinding.layHelpAndVideoTutorial.txtUsername.visibility=View.GONE
        fragBinding.layHelpAndVideoTutorial.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layHelpAndVideoTutorial.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).rl_loadFrag(RLFragHelp(), TAG, true,null, false)
        }

        fragBinding.laySyncWatchData.txtusertitle.setText(R.string.syncwatchdata)
        fragBinding.laySyncWatchData.txtUsername.visibility=View.GONE
        fragBinding.laySyncWatchData.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.laySyncWatchData.relayUser.setOnClickListener {
            // Initialize WearDataSync with DataClient
            rl_syncWatchData()
        }


        fragBinding.layTermandcondition.txtusertitle.setText(R.string.termandcondition)
        fragBinding.layTermandcondition.txtUsername.visibility=View.GONE
        fragBinding.layTermandcondition.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layTermandcondition.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).rl_loadFrag(RLFragTermAndCondition(), TAG, true,null, false)
        }

        fragBinding.layPrivacyPolicy.txtusertitle.setText(R.string.privacypolicy)
        fragBinding.layPrivacyPolicy.txtUsername.visibility=View.GONE
        fragBinding.layPrivacyPolicy.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layPrivacyPolicy.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).rl_loadFrag(RLFragTermAndCondition(), TAG, true, null, false)
        }

    }

    //Firebase One By One BasicData Update
    private fun rl_basicDataUpdateToFirebase(endPoint:String, data:Any,) {
        val firebasePath = RevoolaFirebasePath.basicDataPathWrite(endPoint)
        // Firebase to Update BasicData
        RLDatabaseManagerWrite().rl_write_Basic_Data_Update(firebasePath,data) { isSuccessful, error ->
           if (isSuccessful){
               RLTools.rl_logDPrint(TAG,"BasicData Update Successfully")
           }else{
               RLTools.rl_logEPrint(TAG, "Error Update BasicData: $error")
           }
        }
    }

    private fun rl_syncWatchData() {
        if (isAdded){
            RLBaseProgress.rl_showProgressDialog(requireActivity())
        }
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                val uid= RLAuthManager().rl_getCurrentUser()?.uid?:""
                val weight=userData.weightkg?:"60"
                val height =userData.height?:"167"
                val dob= userData.dob
                val gender=userData.gender
                val RFMHR=userData.RFMHR
                val restingHr=userData.restingHr
                val emailId=userData.emailId
                val isBasicDataAdded=userData.isBasicDataAdded
                val userDataTransfer = RLWatchModel(uid, weight, height, dob, gender, RFMHR, restingHr,emailId,isBasicDataAdded)
                if (isAdded){
                    //val  wearDataSync = WearDataSync(Wearable.getDataClient(requireContext()))
                    val wearDataSync = RLWearDataSync(
                        Wearable.getDataClient(requireContext()),
                        Wearable.getNodeClient(requireContext()))
                    wearDataSync.sendUserDataToWatch(userDataTransfer){ isSuccess, message ->
                        if (isSuccess) {
                            RLBaseProgress.rl_hideProgressDialog()
                            RLTools.rl_logDPrint(TAG, "WearDataSync Success: $userDataTransfer")
                            // Handle success (e.g., update UI)
                        } else {
                            RLBaseProgress.rl_hideProgressDialog()
                            RLTools.rl_logEPrint(TAG, "WearDataSync Error: $message")
                            // Handle failure (e.g., show error message to the user)
                        }
                    }
                }else{
                    RLBaseProgress.rl_hideProgressDialog()
                }

            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

    }

}