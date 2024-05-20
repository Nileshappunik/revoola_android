package com.example.myfirstapp.fragment.overview

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.FragOverviewBinding
import com.example.myfirstapp.utils.PrefManager
import com.example.myfirstapp.utils.Tools

class FragOverview : BaseFragment() {
    val TAG: String = FragOverview::class.java.simpleName
    lateinit var fragBinding: FragOverviewBinding
    companion object {
        const val PERMISSIONS_REQUEST_CODE = 101
        const val REQUEST_ENABLE_BLUETOOTH = 102
    }
    private val binding by lazy {
        FragOverviewBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_overview, container) as FragOverviewBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragOverview" )
        val currentmonth=Tools.getCalculatedMonths()
        fragBinding.txtMonth.setText(currentmonth)
        fragBinding.relayOverview.setOnClickListener {
            //(context as MainActivity).bottombarcolorwhite()
            //(context as MainActivity).loadFrag(FragSessions(), TAG, true, FragSessions::class.java.simpleName, false)
        }
        checkBluetoothenabled()
        checkBluetoothPermissions()
        return fragBinding.root
    }
    private fun checkBluetoothenabled(){
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
            startBluetoothOperations(true)
        }
    }
    private fun checkBluetoothPermissions() {
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
            startBluetoothOperations(false)
        }
    }
    private fun startBluetoothOperations(enabled:Boolean) {
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
                startBluetoothOperations(true)
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
                    startBluetoothOperations(false)
                } else {
                    // Some permissions were denied
                    Toast.makeText(requireContext(), "Bluetooth permissions are required", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}