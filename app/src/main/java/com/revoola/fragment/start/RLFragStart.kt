package com.revoola.fragment.start

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlDialogHelpStartBinding
import com.revoola.databinding.RlFragStartBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.start.adapter.RLHelpListAdapter
import com.revoola.fragment.start.adapter.RLStartListAdapter
import com.revoola.utils.RLConstants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.activity.RLMainActivityRL
import com.revoola.commonobject.RLTools
import com.revoola.fragment.RLHealthConnectBottomSheet
import com.revoola.healthconnect.HealthConnectManager
import com.revoola.permission.RLHealthConnectManager
import com.revoola.utils.RLPrefManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit

class RLFragStart : RLBaseFragment() {
    val TAG: String = RLFragStart::class.java.simpleName
    lateinit var fragBinding: RlFragStartBinding

    private val binding by lazy {
        RlFragStartBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,savedInstanceState:Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_start, container) as RlFragStartBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        RLStartList()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Perform your custom logic here
                // For example, show a confirmation dialog or navigate back
                RLTools.rl_showAlertDialog(requireContext(),requireActivity())
            }
        })
        return fragBinding.root
    }

    private fun RLUiSetUP(dataList: List<RLStartAllMenuModel>) {
        RLfetchUserDetails()
        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.foryourmindandbody))
        fragBinding.inlayTop.smallLogo.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.visibility=View.GONE
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.thebestyoueveryday))

        fragBinding.rvStart.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                //Remove the listener to avoid multiple calls
                fragBinding.rvStart.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val height =  fragBinding.rvStart.height
                println("RelativeLayout total height: $height pixels")

                val linearLayoutMain = LinearLayoutManager(activity)
                fragBinding.rvStart.layoutManager = linearLayoutMain
                val adapter = RLStartListAdapter(activity,dataList,height)
                fragBinding.rvStart.adapter=adapter
            }
        })
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            //getHealth()
            RLshowHelpDialog()
        }

        rl_helpHideShowSet(true,fragBinding.inlayTop.ivhelp, RLPrefManager.start_help_content)
        (context as RLMainActivityRL).rl_checkAllPermission()

        lifecycleScope.launch {
            val  healthConnectManager = RLHealthConnectManager(requireContext())
            delay(1000) // Small delay to ensure permissions are updated
            if (!healthConnectManager.arePermissionsGranted()){
                val bottomSheet = RLHealthConnectBottomSheet()
                bottomSheet.show(parentFragmentManager, "RLHealthConnectBottomSheet")
            }
        }
    }

    private fun RLfetchUserDetails() {
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                val gson = Gson()
                val json = gson.toJson(userData)
                if (isAdded) {  // Check if fragment is attached
                    RLPrefManager.rl_setSomeStringValue(requireContext(), RLPrefManager.user_model_data, json)
                }
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }
    }

    private fun RLStartList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.rl_allMenuListRead(RLConstants.MAIN){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    RLTools.rl_logDPrint(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLUiSetUP(dataList)
                }catch (e:Exception){
                   RLTools.rl_logEPrint(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }
    private fun RLStartListNew() {
        val databaseReference = FirebaseDatabase.getInstance().getReference(RLConstants.MAIN)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    try {
                        val gson = Gson()
                        val jsonArray = gson.toJson(snapshot.value)
                        RLTools.rl_logDPrint(TAG, "Response:- $jsonArray")
                        val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                        val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                        RLUiSetUP(dataList)
                    } catch (e: Exception) {
                       RLTools.rl_logEPrint(TAG, "Catch:- ${e.message}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
               RLTools.rl_logEPrint(TAG, "Firebase Error: ${error.message}")
            }
        })
    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpStartBinding= RlDialogHelpStartBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        val linearLayoutMain = LinearLayoutManager(activity)
        dialogMainBinding.ivRecyclerview.layoutManager = linearLayoutMain

        val jsonString= RLPrefManager.rl_getSomeStringValue(activity, RLPrefManager.start_help_content,"")
        val gson = Gson()
        val StartHelpModel: RLStartHelpModel = gson.fromJson(jsonString, RLStartHelpModel::class.java)
        val adapter = RLHelpListAdapter(activity,StartHelpModel.data)
        dialogMainBinding.ivRecyclerview.adapter=adapter

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var healthConnectClient: HealthConnectClient

    private val permissionsLauncher = registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
        if (granted.containsAll(HealthConnectManager.PERMISSIONS)) {
            RLTools.rl_logDPrint(TAG, "Permissions granted.")
            readStepsData()
        } else {
            RLTools.rl_logEPrint(TAG, "Permissions denied.")
        }
    }

    private fun getHealth(){
        healthConnectManager = HealthConnectManager(requireContext())
        lifecycleScope.launch {
            healthConnectClient = healthConnectManager.getHealthConnectClient()
                ?: run {
                    Toast.makeText(requireContext(), "No provider found!", Toast.LENGTH_LONG).show()
                    return@launch
                }

            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (!granted.containsAll(HealthConnectManager.PERMISSIONS)) {
                permissionsLauncher.launch(HealthConnectManager.PERMISSIONS)
            } else {
                readStepsData()
            }
        }
    }

    private fun readStepsData() {
        fragBinding.tempText.setText("Start Step")
        lifecycleScope.launch {
            val stepsRecords = healthConnectManager.readStepsData(healthConnectClient, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now())
            for (record in stepsRecords) {
                val step = "Steps: ${record.count}, Start: ${record.startTime}, End: ${record.endTime}"
                fragBinding.tempText.setText(step)
               RLTools.rl_logDPrint(TAG, step)
            }
        }
    }

}