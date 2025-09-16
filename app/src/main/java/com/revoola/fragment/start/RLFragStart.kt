package com.revoola.fragment.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragStartBinding
import com.revoola.enumclass.RLStartAllMenuModel
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
    private val TAG: String = RLFragStart::class.java.simpleName
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var healthConnectClient: HealthConnectClient

    private val fragBinding by lazy {
        RlFragStartBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?,savedInstanceState:Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.foryourmindandbody))
        fragBinding.inlayTop.smallLogo.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.visibility=View.GONE
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.thebestyoueveryday))
        RLTools.RLhideShowHelpDialog(requireContext(), "start", fragBinding.inlayTop.ivhelp)
        
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