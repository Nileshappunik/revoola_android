package com.revoola.fragment.more

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.gms.wearable.Wearable
import com.google.firebase.storage.FirebaseStorage
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class RLFragApplicationSetting : RLBaseFragment()  {
    val TAG: String = RLFragApplicationSetting::class.java.simpleName
    lateinit var fragBinding: RlFragApplicationSettingBinding
    private var userBasicDataCard: RLRevoolaUsersSettingsModel? =null

    private val binding by lazy {
        RlFragApplicationSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_application_setting, container) as RlFragApplicationSettingBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragSetting" )
        RlUiSetUp()
        return fragBinding.root
    }

    //All Design Setup Like Button Click And All
    private fun RlUiSetUp() {
        RLonBackPresAct(fragBinding.ivBack)
        //Firebase To Fetch UserData
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userBasicDataCard=userData
                if ( userData.appUnit.toLowerCase().equals("imperial")){
                    fragBinding.radioGroup.check(R.id.radioButtonimperial)
                }else{
                    fragBinding.radioGroup.check(R.id.radioButtonmetric)
                }

            } else {
               RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        fragBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId.equals(R.id.radioButtonimperial)) {
                RLBasicDataUpdateToFirebase("appUnit","Imperial")
            }else {
                RLBasicDataUpdateToFirebase("appUnit","Metric")
            }
        }

        fragBinding.layHelpAndVideoTutorial.txtusertitle.setText(R.string.helpvideotutorials)
        fragBinding.layHelpAndVideoTutorial.txtUsername.visibility=View.GONE
        fragBinding.layHelpAndVideoTutorial.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layHelpAndVideoTutorial.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).RLloadFrag(RLFragHelp(), TAG, true,null, false)
        }

        fragBinding.laySyncWatchData.txtusertitle.setText(R.string.syncwatchdata)
        fragBinding.laySyncWatchData.txtUsername.visibility=View.GONE
        fragBinding.laySyncWatchData.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.laySyncWatchData.relayUser.setOnClickListener {
            // Initialize WearDataSync with DataClient
            RLSyncWatchData()
        }


        fragBinding.layTermandcondition.txtusertitle.setText(R.string.termandcondition)
        fragBinding.layTermandcondition.txtUsername.visibility=View.GONE
        fragBinding.layTermandcondition.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layTermandcondition.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).RLloadFrag(RLFragTermAndCondition(), TAG, true,null, false)
        }

        fragBinding.layPrivacyPolicy.txtusertitle.setText(R.string.privacypolicy)
        fragBinding.layPrivacyPolicy.txtUsername.visibility=View.GONE
        fragBinding.layPrivacyPolicy.imgEdit.setImageResource(R.drawable.ic_chevron_right)
        fragBinding.layPrivacyPolicy.relayUser.setOnClickListener {
            //openwebview
            (context as RLMainActivityRL).RLloadFrag(RLFragTermAndCondition(), TAG, true, null, false)
        }

    }

    //Firebase One By One BasicData Update
    private fun RLBasicDataUpdateToFirebase(endPoint:String,data:Any,) {
        val firebasePath = RevoolaFirebasePath.basicDataPathWrite(endPoint)
        // Firebase to Update BasicData
        RLDatabaseManagerWrite().RlWriteBasicDataUpdate(firebasePath,data) { isSuccessful, error ->
           if (isSuccessful){
               RLTools.RlLogDPrint(TAG,"BasicData Update Successfully")
           }else{
               RLTools.RlLogEPrint(TAG, "Error Update BasicData: $error")
           }
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
                            RLBaseProgress.RLhideProgressDialog()
                            RLTools.RlLogDPrint(TAG, "WearDataSync Success: $userDataTransfer")
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






}