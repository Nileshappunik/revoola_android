package com.revoola.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.net.ConnectivityManager
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.revoola.R
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.revoola.base.RLBaseActivity
import com.revoola.broadcast.RlNetworkChangeReceiver
import com.revoola.databinding.RlActivityMainBinding
import com.revoola.fragment.feed.RLFragFeed
import com.revoola.fragment.friends.RLFragFriends
import com.revoola.fragment.more.RLFragMore
import com.revoola.fragment.overview.RLFragOverviewSession
import com.revoola.fragment.start.RLFragStart
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.inapp.MoEInAppHelper
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.services.RELDynamicLinkManager
import com.revoola.commonobject.RLTools
import com.revoola.permission.RLHealthConnectManager
import com.revoola.permission.RLPermissionManager
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.coroutines.launch
import org.json.JSONObject

class RLMainActivityRL  : RLBaseActivity() {
    val TAG: String = RLMainActivityRL::class.java.simpleName
    var fragment: String? = null
    lateinit var activityMainBinding: RlActivityMainBinding
    var sucDialog: Dialog? = null
    private lateinit var networkChangeReceiver: RlNetworkChangeReceiver
    val  healthConnectManager = RLHealthConnectManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding =RLinflateBindLayout(this, R.layout.rl_activity_main) as RlActivityMainBinding
        RLshowbottombarcolorwhite()
        //First Fragment Open
         RLloadFrag(RLFragStart(), TAG, true, null, false)
        //Start Menu First Open
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.start)
        item.setChecked(true)
        //Navigation Item Click
        RLNavItemClick()
        // When Bottom Press Animation Stop
        RLDisableLongPressToast(activityMainBinding.bottomNav)
        // Internet Check And Reconnect
        networkChangeReceiver = RlNetworkChangeReceiver(activityMainBinding.container)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    fun RLCheckAllPermission(){
        //All Permission
        if (!RLPermissionManager.arePermissionsGranted(this)) {
            RLPermissionManager.requestPermissions(this,RLRequestPermissionsLauncher)
        }
    }

    private fun RLNavItemClick(){
        activityMainBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.overview -> {
                    activityMainBinding.bottomNav.setBackgroundResource(R.color.AppNEWBGColor)
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragOverviewSession(), TAG, false, null, false)
                    true
                }

                R.id.feed -> {
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragFeed(), TAG, false, null, false)
                    true
                }

                R.id.start -> {
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragStart(), TAG, false, null, false)
                    true
                }

                R.id.friends -> {
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragFriends(), TAG, false, null, false)
                    true
                }

                R.id.more -> {
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragMore(), TAG, false, null, false)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun RLDisableLongPressToast(bottomNavigationView: BottomNavigationView) {
        // Iterate over the BottomNavigationView items
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.feed), null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.overview), null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.start), null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.friends), null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.more), null)
        bottomNavigationView.findViewById<View>(R.id.feed).setOnLongClickListener {
            // add any code which you want to execute on long click
            true
        }
        bottomNavigationView.findViewById<View>(R.id.overview).setOnLongClickListener {
            // add any code which you want to execute on long click
            true
        }
        bottomNavigationView.findViewById<View>(R.id.start).setOnLongClickListener {
            // add any code which you want to execute on long click
            true
        }
        bottomNavigationView.findViewById<View>(R.id.friends).setOnLongClickListener {
            // add any code which you want to execute on long click
            true
        }
        bottomNavigationView.findViewById<View>(R.id.more).setOnLongClickListener {
            // add any code which you want to execute on long click
            true
        }
    }

    fun RLbottombarcolorDarkBlue() {
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.overview)
        item.setChecked(true)
        activityMainBinding.bottomNav.setBackgroundResource(R.color.AppNEWBGColor)
    }

    fun RLhidebottombarcolorwhite() {
        activityMainBinding.bottomNav.visibility = View.GONE
    }

    fun RLshowbottombarcolorwhite() {
        activityMainBinding.bottomNav.visibility = View.VISIBLE
        activityMainBinding.bottomNav.setBackgroundResource(R.color.AppWhiteColor)
    }

    fun RLSelectionbottombar(selectedID: Int) {
        activityMainBinding.bottomNav.selectedItemId = selectedID
    }

    fun RLloadFrag(fragment: Fragment?, tagName: String?, isBackStack: Boolean, fragmentName: String?, type: Boolean): Boolean {
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            this.fragment = fragmentName
            if (!type) {
                fragmentTransaction.replace(R.id.frame_container, fragment)
            } else {
                fragmentTransaction.add(R.id.frame_container, fragment)
            }
            if (isBackStack) {
                fragmentTransaction.addToBackStack(fragmentName)
            }
            if (!isFinishing) fragmentTransaction.commitAllowingStateLoss() else fragmentTransaction.commit()
            return true
        }
        return false
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent
        //intent?.putExtra("branch_force_new_session", true)
        Branch.sessionBuilder(this)
            .withCallback { referringParams, error ->
                if (error == null) {
                    if (referringParams != null) {
                        RLHandleBranchData(referringParams)
                    }
                } else {
                    RLTools.RlLogEPrint(TAG, "Branch Error onNewIntent: ${error.message}")
                }
            }
            .withData(intent?.data) // Pass the new intent data
            .init()
    }

    override fun onStart() {
        super.onStart()
        // Force a new Branch session by adding the extra
        intent.putExtra("branch_force_new_session", true)
        // Branch init
        Branch.sessionBuilder(this)
            .withCallback { referringParams: JSONObject?, error: BranchError? ->
                if (error == null) {
                    if (referringParams != null) {
                        RLHandleBranchData(referringParams)
                    }

                } else {
                    RLTools.RlLogEPrint(TAG, "Branch Error onStart: ${error.message}")
                }
            }.withData(intent.data).init()
        setupUsermoengage()
    }

    private fun setupUsermoengage() {
        MoEInAppHelper.getInstance().showInApp(applicationContext)
        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                val userAuth = RLAuthManager().RlgetCurrentUser()
                MoEAnalyticsHelper.setUniqueId(this, userAuth!!.uid)

                MoEAnalyticsHelper.setFirstName(this, userData.firstName)
                MoEAnalyticsHelper.setLastName(this, userData.lastName)
                MoEAnalyticsHelper.setUserName(this, userData.displayName)
                MoEAnalyticsHelper.setBirthDate(this, userData.dob)
                MoEAnalyticsHelper.setEmailId(this, userData.emailId)

            } else {
                RLTools.RlLogEPrint(TAG, "Error fetching user data")
            }
        }

    }

    private fun RLHandleBranchData(jsonObject: JSONObject) {
        RLTools.RlLogDPrint(TAG, "Branch params: $jsonObject")
        val deeplinkPath: String? = if (jsonObject.has("\$deeplink_path")) {
            jsonObject.optString("\$deeplink_path", null)
        } else {
            null
        }
        if (!deeplinkPath.isNullOrEmpty()) {
            RELDynamicLinkManager.getInstance()
                .RLCheckLink(deeplinkPath, this, this@RLMainActivityRL)

        }
    }

    val RLRequestPermissionHealthConnectLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            healthConnectManager.openHealthConnectPermissions()
        }
    }
    fun RLHealthAndAllPermission() {
        //Health permission
        // Check availability and installation
        if (!healthConnectManager.isHealthConnectAvailable()) {
            if (!healthConnectManager.isHealthConnectInstalled()) {
                // Health Connect app is not installed
                Toast.makeText(this, "Please install Health Connect from Play Store", Toast.LENGTH_LONG).show()
                // Optional: Open Play Store
                try {
                    val intent = Intent().apply {
                        action = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Could not open Play Store", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Health Connect is not available on this device", Toast.LENGTH_LONG).show()
            }
            return
        }

        // Check Health Connect permissions
        lifecycleScope.launch {
            val isGranted = healthConnectManager.arePermissionsGranted()
            RLTools.RlLogEPrint(TAG,"isGranted: $isGranted")
            if (!isGranted) {
                RLRequestPermissionHealthConnectLauncher.launch(healthConnectManager.requiredPermissions.toTypedArray())
            }
        }

    }


}