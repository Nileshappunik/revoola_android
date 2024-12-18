package com.revoola.fragment.overview

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlFragOverviewBinding
import com.revoola.fragment.more.RLFragNotification
import com.revoola.model.RLGetUserAggregatedData
import com.revoola.model.RLGetUserAggregatedDataRequest
import com.revoola.utils.RLConstants
import com.revoola.utils.RLTools
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory
import java.util.Calendar
import java.util.TimeZone

class RLFragOverview : RLBaseFragment() {
    val TAG: String = RLFragOverview::class.java.simpleName
    lateinit var fragBinding: RlFragOverviewBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    private lateinit var viewModel: RLMainViewModel
    var currentUser:String=""
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 101
        const val REQUEST_ENABLE_BLUETOOTH = 102
    }
    private val binding by lazy {
        RlFragOverviewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_overview, container) as RlFragOverviewBinding
        currentUser=  com.revoola.utils.RLPrefManager.RLgetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_user, "")
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragOverview" )
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
        (context as RLMainActivityRL).RLbottombarcolorDarkBlue()
        fragBinding.inlaySession.cardOverview.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragOverviewSession(), TAG, true,null, false)
        }
        fragBinding.ivNotification.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLFragNotification(), TAG, true, null, false)
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
        val date = Calendar.getInstance()
        val firstDay = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val offset = TimeZone.getDefault().rawOffset / 1000
        val timestampFrom = (firstDay.timeInMillis / 1000) - offset
        val timestampTo = (date.timeInMillis / 1000) - offset

        val request = listOf(
            RLGetUserAggregatedDataRequest(
            getUserAggregatedData = RLGetUserAggregatedData(
                    userid = currentUser,
                    classtype = "all",
                    timestampfrom = timestampFrom,
                    timestampto = timestampTo)
            )
        )
        RLTools.RlLogDPrint(TAG,"request:- $request")
        viewModel.RLgetUserAggregatedData(request) { result ->
            result.onSuccess { response ->
                try {
                    if (response.type.equals("success")){

                        val effort=RLTools.RLformatCommas(response.text[0].aggregated[0].rev.toDouble())
                        val relaxation=RLTools.RLformatTime(response.text[0].aggregated[0].rmm, false)

                        val session=response.text[0].aggregated[0].session.toString()
                        val activetime=RLTools.RLformatTime(response.text[0].aggregated[0].totalTime,false)

                        val totalcalories=RLTools.RLformatCommas(response.text[0].aggregated[0].calorie.toDouble())
                        val activecalories=RLTools.RLformatCommas(response.text[0].aggregated[0].power.toDouble())

                        val distance=RLTools.RLformatCommas(response.text[0].aggregated[0].distance.toDouble())
                        val steps=RLTools.RLformatCommas(response.text[0].aggregated[0].steps.toDouble())

                        fragBinding.inlayEffort.txtNumberLeft.setText(effort)
                        fragBinding.inlayEffort.txtNumberRight.setText(relaxation)

                        fragBinding.inlaySession.txtNumberLeft.setText(session)
                        fragBinding.inlaySession.txtNumberRight.setText(activetime)

                        fragBinding.inlayTotalcalories.txtNumberLeft.setText(totalcalories.toString())
                        fragBinding.inlayTotalcalories.txtNumberRight.setText(activecalories.toString())

                        fragBinding.inlayDistance.txtNumberLeft.setText(distance.toString())
                        fragBinding.inlayDistance.txtNumberRight.setText(steps.toString())

                    }else {
                        RLcommonToast(response.type)
                    }
                }catch (e:Exception){
                    RLTools.RlLogDPrint(TAG,"exception= "+e.message)
                }
            }.onFailure { error ->
                // Handle failure
                RLTools.RlLogDPrint(TAG,"error= "+error.message)
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