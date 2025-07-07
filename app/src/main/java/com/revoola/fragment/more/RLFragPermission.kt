package com.revoola.fragment.more

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.commonobject.RLTools
import com.revoola.databasefirebase.RLDatabaseManagerWrite
import com.revoola.databasefirebase.RevoolaFirebasePath
import com.revoola.databinding.*
import com.revoola.model.RLRevoolaUsersSettingsModel
import com.revoola.utils.RLPrefManager

class RLFragPermission : RLBaseFragment() {
    val TAG: String = RLFragPermission::class.java.simpleName
    private var userBasicDataCard: RLRevoolaUsersSettingsModel? = null
    private val fragBinding by lazy {
        RlFragPermissionBinding.inflate(layoutInflater)
    }

    // Permission request codes
    private val LOCATION_PERMISSION_REQUEST = 1001
    private val CAMERA_PERMISSION_REQUEST = 1002
    private val GALLERY_PERMISSION_REQUEST = 1003
    private val NOTIFICATION_PERMISSION_REQUEST = 1004

    // Required permissions
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA)

    private val galleryPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment, "RLFragPermission")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        //Firebase To Fetch UserData
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                userBasicDataCard = userData
                if (isAdded){
                    when (userData.visibilityflagforthatsession) {
                        0 -> {//EveryOne
                            RLactivitySet(resources.getColor(R.color.AppPrivacyEveryOneBGColor),
                                R.drawable.ic_privacyeveryone,
                                R.string.everyone,
                                resources.getColor(R.color.AppPrivacyEveryOneColor))
                        }
                        1 -> {//Private
                            RLactivitySet(resources.getColor(R.color.AppPrivacyPrivateBGColor),
                                R.drawable.ic_privacyprivate,
                                R.string.privatetx,
                                resources.getColor(R.color.AppPrivacyPrivateColor))
                        }

                        2 -> {//Friends
                            RLactivitySet(resources.getColor(R.color.AppPrivacyFriendsBGColor),
                                R.drawable.ic_privacyfriends,
                                R.string.friendstx,
                                resources.getColor(R.color.AppPrivacyFriendsColor))
                        }
                    }
                }
            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

        fragBinding.layActivities.apply {
            txtusertitle.setText(R.string.activities)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            layPrivacy.visibility = View.GONE
            layPrivacy.setOnClickListener {
                val titletxt: String = tvsharetitle.text.toString()
                if (titletxt.uppercase().equals("PRIVATE")) {
                    RLBasicDataUpdateToFirebase("visibilityflagforthatsession", 0)
                    RLactivitySet(resources.getColor(R.color.AppPrivacyEveryOneBGColor),
                        R.drawable.ic_privacyeveryone,
                        R.string.everyone,
                        resources.getColor(R.color.AppPrivacyEveryOneColor))

                } else if (titletxt.uppercase().equals("FRIENDS")) {
                    RLBasicDataUpdateToFirebase("visibilityflagforthatsession", 1)
                    RLactivitySet(resources.getColor(R.color.AppPrivacyPrivateBGColor),
                        R.drawable.ic_privacyprivate,
                        R.string.privatetx,
                        resources.getColor(R.color.AppPrivacyPrivateColor))

                } else if (titletxt.uppercase().equals("EVERYONE")) {
                    RLactivitySet(resources.getColor(R.color.AppPrivacyFriendsBGColor),
                        R.drawable.ic_privacyfriends,
                        R.string.friendstx,
                        resources.getColor(R.color.AppPrivacyFriendsColor))
                }
            }
        }

        setupPermissionSwitches()
        updateSwitchStates()
        
    }

    private fun RLactivitySet(bgColor:Int,icon:Int,title:Int,titleColor:Int){
        fragBinding.layActivities.apply {
            layPrivacy.visibility = View.VISIBLE
            layPrivacy.backgroundTintList = ColorStateList.valueOf(bgColor)
            imgShareimage.setImageResource(icon)
            tvsharetitle.setText(title)
            tvsharetitle.setTextColor(titleColor)
        }
    }

    //Firebase One By One BasicData Update
    private fun RLBasicDataUpdateToFirebase(endPoint: String, data: Any, ) {
        val firebasePath = RevoolaFirebasePath.basicDataPathWrite(endPoint)
        // Firebase to Update BasicData
        RLDatabaseManagerWrite().RlWriteBasicDataUpdate(firebasePath, data) { isSuccessful, error ->
            if (isSuccessful) {
                RLTools.RlLogDPrint(TAG, "BasicData Update Successfully")
            } else {
                RLTools.RlLogEPrint(TAG, "Error Update BasicData: $error")
            }
        }
    }

    private fun setupPermissionSwitches() {
        // Location permission switch
        fragBinding.layLocation.apply {
            txtusertitle.setText(R.string.location)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            switchSetting.visibility = View.VISIBLE

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestLocationPermission()
                } else {
                    // Can't programmatically revoke permissions, guide user to settings
                    showPermissionSettingsDialog("Location")
                    // Reset switch state
                    switchSetting.isChecked = true
                }
            }
        }

        // Camera permission switch
        fragBinding.layCamera.apply {
            txtusertitle.setText(R.string.camera)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            switchSetting.visibility = View.VISIBLE

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestCameraPermission()
                } else {
                    showPermissionSettingsDialog("Camera")
                    switchSetting.isChecked = true
                }
            }
        }

        // Gallery permission switch
        fragBinding.layGallery.apply {
            txtusertitle.setText(R.string.gallery)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            switchSetting.visibility = View.VISIBLE

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestGalleryPermission()
                } else {
                    showPermissionSettingsDialog("Gallery")
                    switchSetting.isChecked = true
                }
            }
        }

        // Notification permission switch (Android 13+)
        fragBinding.layNotification.apply {
            txtusertitle.setText(R.string.notifications)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            switchSetting.visibility = View.VISIBLE

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestNotificationPermission()
                } else {
                    showPermissionSettingsDialog("Notification")
                    switchSetting.isChecked = true
                }
            }
        }

        // Steps permission (if you're using activity recognition)
        fragBinding.layStep.apply {
            txtusertitle.setText(R.string.steps)
            txtUsername.visibility = View.GONE
            imgEdit.visibility = View.GONE
            switchSetting.visibility = View.VISIBLE

            switchSetting.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestActivityRecognitionPermission()
                } else {
                    showPermissionSettingsDialog("Activity Recognition")
                    switchSetting.isChecked = true
                }
            }
        }
    }

    private fun updateSwitchStates() {
        // Update switch states based on current permissions
//        fragBinding.layLocation.switchSetting.isChecked = hasLocationPermission()
//        fragBinding.layCamera.switchSetting.isChecked = hasCameraPermission()
//        fragBinding.layGallery.switchSetting.isChecked = hasGalleryPermission()
//        fragBinding.layNotification.switchSetting.isChecked = hasNotificationPermission()
//        fragBinding.layStep.switchSetting.isChecked = hasActivityRecognitionPermission()


        // Update switch states based on current permissions without triggering listeners
        fragBinding.layLocation.switchSetting.apply {
            setOnCheckedChangeListener(null)
            isChecked = hasLocationPermission()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestLocationPermission()
                } else {
                    showPermissionSettingsDialog("Location")
                    this.isChecked = true
                }
            }
        }

        fragBinding.layCamera.switchSetting.apply {
            setOnCheckedChangeListener(null)
            isChecked = hasCameraPermission()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestCameraPermission()
                }else {
                    showPermissionSettingsDialog("Camera")
                    this.isChecked = true
                }
            }
        }

        fragBinding.layGallery.switchSetting.apply {
            setOnCheckedChangeListener(null)
            isChecked = hasGalleryPermission()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestGalleryPermission()
                } else {
                    showPermissionSettingsDialog("Gallery")
                    this.isChecked = true
                }
            }
        }

        fragBinding.layNotification.switchSetting.apply {
            setOnCheckedChangeListener(null)
            isChecked = hasNotificationPermission()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestNotificationPermission()
                } else {
                    showPermissionSettingsDialog("Notification")
                    this.isChecked = true
                }
            }
        }

        fragBinding.layStep.switchSetting.apply {
            setOnCheckedChangeListener(null)
            isChecked = hasActivityRecognitionPermission()
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    requestActivityRecognitionPermission()
                } else {
                    showPermissionSettingsDialog("Activity Recognition")
                    this.isChecked = true
                }
            }
        }
    }

    // Permission check methods
    private fun hasLocationPermission(): Boolean {
        return locationPermissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasGalleryPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notifications are allowed by default on older versions
        }
    }

    private fun hasActivityRecognitionPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not required on older versions
        }
    }

    // Permission request methods
    private fun requestLocationPermission() {
        try {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionRationaleDialog("Location", "This app needs location access to provide location-based features.") {
                    requestPermissions(locationPermissions, LOCATION_PERMISSION_REQUEST)
                }
            } else {
                requestPermissions(locationPermissions, LOCATION_PERMISSION_REQUEST)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun requestCameraPermission() {
        try {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showPermissionRationaleDialog("Camera", "This app needs camera access to take photos.") {
                    requestPermissions(cameraPermissions, CAMERA_PERMISSION_REQUEST)
                }
            } else {
                requestPermissions(cameraPermissions, CAMERA_PERMISSION_REQUEST)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun requestGalleryPermission() {
        try {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                galleryPermissions
            }

            requestPermissions(permissions, GALLERY_PERMISSION_REQUEST)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun requestActivityRecognitionPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), NOTIFICATION_PERMISSION_REQUEST)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    // Handle permission results
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                fragBinding.layLocation.switchSetting.isChecked = granted
                if (!granted) {
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            CAMERA_PERMISSION_REQUEST -> {
                val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                fragBinding.layCamera.switchSetting.isChecked = granted
                if (!granted) {
                    Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            GALLERY_PERMISSION_REQUEST -> {
                val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                fragBinding.layGallery.switchSetting.isChecked = granted
                if (!granted) {
                    Toast.makeText(requireContext(), "Gallery permission denied", Toast.LENGTH_SHORT).show()
                }
            }

            NOTIFICATION_PERMISSION_REQUEST -> {
                val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                fragBinding.layNotification.switchSetting.isChecked = granted
                if (!granted) {
                    Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Helper methods
    private fun showPermissionRationaleDialog(permissionName: String, message: String, onPositive: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Grant") { _, _ -> onPositive() }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                updateSwitchStates() // Reset switch state
            }
            .show()
    }

    private fun showPermissionSettingsDialog(permissionName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Settings")
            .setMessage("To disable $permissionName permission, please go to Settings > Apps > Your App > Permissions and disable it manually.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onStart() {
        super.onStart()
        // Update switch states when fragment becomes visible
        updateSwitchStates()
    }
    override fun onResume() {
        super.onResume()
        // Update switch states when returning from settings
        updateSwitchStates()
    }

}