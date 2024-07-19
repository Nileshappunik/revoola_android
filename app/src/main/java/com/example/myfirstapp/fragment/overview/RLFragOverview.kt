package com.example.myfirstapp.fragment.overview

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.api.RLApiClientRet
import com.example.myfirstapp.databinding.RlFragOverviewBinding
import com.example.myfirstapp.fragment.more.RLFragNotification
import com.example.myfirstapp.model.RLGetUserAggregatedData
import com.example.myfirstapp.model.RLGetUserAggregatedDataRequest
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools
import com.example.myfirstapp.viewmodel.RLMainRepository
import com.example.myfirstapp.viewmodel.RLMainViewModel
import com.example.myfirstapp.viewmodel.RLMainViewModelFactory

class RLFragOverview : RLBaseFragment() {
    val TAG: String = RLFragOverview::class.java.simpleName
    lateinit var fragBinding: RlFragOverviewBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 101
        const val REQUEST_ENABLE_BLUETOOTH = 102
    }
    private val binding by lazy {
        RlFragOverviewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview, container) as RlFragOverviewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragOverview" )
        RLcheckBluetoothenabled()
        RLcheckBluetoothPermissions()
        // Api call
        RLApiClientRetrofit = RLApiClientRet(activity)
        val apiService = RLApiClientRetrofit.RLNetworkService
        val userRepository = RLMainRepository(apiService)
        viewModel = ViewModelProvider(requireActivity(),
            RLMainViewModelFactory(
                userRepository
            )
        ).get(RLMainViewModel::class.java)

        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        val currentmonth=RLTools.RLgetCalculatedMonths()
        fragBinding.txtMonth.setText(currentmonth)
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
        fragBinding.inlaySession.cardOverview.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, true, RLFragOverviewSession::class.java.simpleName, false)
        }
        fragBinding.ivNotification.setOnClickListener {
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, RLFragNotification::class.java.simpleName, false)
        }


        fragBinding.inlaySession.cardOverview.setCardBackgroundColor(resources.getColor(R.color.AppOverviewCardColor))
        fragBinding.inlaySession.txtTitleLeft.setText(R.string.session)
        fragBinding.inlaySession.txtTitleRight.setText(R.string.activetime)
        fragBinding.inlaySession.txtNumberLeft.setText("0")
        fragBinding.inlaySession.txtNumberRight.setText("0")

        fragBinding.inlayTotalcalories.cardOverview.setCardBackgroundColor(resources.getColor(R.color.AppOverviewCardColor))
        fragBinding.inlayTotalcalories.txtTitleLeft.setText(R.string.totalcalories)
        fragBinding.inlayTotalcalories.txtTitleRight.setText(R.string.activecalories)
        fragBinding.inlayTotalcalories.txtNumberLeft.setText("0")
        fragBinding.inlayTotalcalories.txtNumberRight.setText("0")

        fragBinding.inlayDistance.cardOverview.setCardBackgroundColor(resources.getColor(R.color.AppOverviewCardColor))
        fragBinding.inlayDistance.txtTitleLeft.setText(R.string.distancekm)
        fragBinding.inlayDistance.txtTitleRight.setText(R.string.steps)
        fragBinding.inlayDistance.txtNumberLeft.setText("0")
        fragBinding.inlayDistance.txtNumberRight.setText("0")

        if (RLApiClientRetrofit.RLisConnected()) {
            //LeadDetail Api
            RLapicall()
        } else {
            RLshowDialogFullscreen()
        }
    }


    private fun RLapicall() {
        val request = listOf(RLGetUserAggregatedDataRequest(
                RLGetUserAggregatedData = RLGetUserAggregatedData(
                    userid = "w2p8SQCvE3emjEEDo66f02eF6fG2",
                    classtype = "all",
                    timestampfrom = 1714521600,
                    timestampto = 1716469710)))
        viewModel.RLgetUserAggregatedData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){

                        val effort=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].power.toDouble())
                        val relaxation=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].rmm.toDouble())

                        val session=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].session.toDouble())
                        val activetime=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].hr.toDouble())

                        val totalcalories=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].calorie.toDouble())
                        val activecalories=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].totalTime.toDouble())

                        val distance=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].distance.toDouble())
                        val steps=RLTools.RLformatCommas(response.RLText[0].RLAggregated[0].steps.toDouble())

                        fragBinding.inlayEffort.txtNumberLeft.setText(response.RLText[0].RLAggregated[0].power.toString())
                        fragBinding.inlayEffort.txtNumberRight.setText(response.RLText[0].RLAggregated[0].rmm.toString())

                        fragBinding.inlaySession.txtNumberLeft.setText(response.RLText[0].RLAggregated[0].session.toString())
                        fragBinding.inlaySession.txtNumberRight.setText(response.RLText[0].RLAggregated[0].hr.toString())

                        fragBinding.inlayTotalcalories.txtNumberLeft.setText(totalcalories.toString())
                        fragBinding.inlayTotalcalories.txtNumberRight.setText(activecalories.toString())

                        fragBinding.inlayDistance.txtNumberLeft.setText(distance.toString())
                        fragBinding.inlayDistance.txtNumberRight.setText(steps.toString())

                    }else {
                        RLcommonToast(response.type)
                    }
                }catch (e:Exception){
                    Log.d(TAG,"exception= "+e.message)
                }
            }.onFailure { error ->
                // Handle failure
                Log.d(TAG,"error= "+error.message)
                RLcommonToast(RLConstants.SERVER_PROBLEM)
            }
        }
    }

    private fun RLcheckBluetoothenabled(){
        // Check if Bluetooth is enabled
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(requireContext(), "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            // Request to enable Bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            // Bluetooth is already enabled, proceed with Bluetooth operations
            RLstartBluetoothOperations(true)
        }
    }
    private fun RLcheckBluetoothPermissions() {
        // List of required permissions for Bluetooth operations
        val permissions = listOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT)

        // Check which permissions are not granted
        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isNotEmpty()) {
            // Request the required permissions
            ActivityCompat.requestPermissions(requireActivity(), permissionsNeeded.toTypedArray(), PERMISSIONS_REQUEST_CODE)
        } else {
            // Permissions are already granted
            RLstartBluetoothOperations(false)
        }
    }
    private fun RLstartBluetoothOperations(enabled:Boolean) {
        if (enabled){
            // Bluetooth is enabled; you can start Bluetooth operations here
            //Toast.makeText(requireContext(), "Bluetooth is enabled", Toast.LENGTH_SHORT).show()
        }else{
            // Bluetooth operations can be performed here since permissions are granted
           // Toast.makeText(requireContext(), "Bluetooth permissions granted", Toast.LENGTH_SHORT).show()
            // Implement Bluetooth functionality
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth was enabled
                RLstartBluetoothOperations(true)
            } else {
                // Bluetooth was not enabled
                Toast.makeText(requireContext(), "Bluetooth needs to be enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // All permissions were granted
                    RLstartBluetoothOperations(false)
                } else {
                    // Some permissions were denied
                    Toast.makeText(requireContext(), "Bluetooth permissions are required", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }




}