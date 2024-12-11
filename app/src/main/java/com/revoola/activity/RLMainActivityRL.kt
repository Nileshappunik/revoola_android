package com.revoola.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.widget.TooltipCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.revoola.R
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
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.services.RLDeepLinkHandler
import io.branch.referral.Branch
import io.branch.referral.BranchError
import org.json.JSONObject

class RLMainActivityRL  : RLBaseActivity() {
    val TAG: String = RLMainActivityRL::class.java.simpleName
    var fragment: String? = null
    lateinit var activityMainBinding: RlActivityMainBinding
    var sucDialog: Dialog? = null
    private lateinit var networkChangeReceiver: RlNetworkChangeReceiver

    companion object {
        const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding = RLinflateBindLayout(this, R.layout.rl_activity_main) as RlActivityMainBinding
        RLshowbottombarcolorwhite()
        RLloadFrag(RLFragStart(), TAG, true, null, false)
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.start)
        item.setChecked(true)
        RLchepermissionphysicalActivity()

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
                   // loadFrag(RLFragStart(), TAG, true, RLFragStart::class.java.simpleName, false)
                    false
                }
            }
        }
        RLDisableLongPressToast(activityMainBinding.bottomNav)

        // Internet Check And Reconnect
        networkChangeReceiver = RlNetworkChangeReceiver(activityMainBinding.container)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

    private fun RLDisableLongPressToast(bottomNavigationView: BottomNavigationView) {
        // Iterate over the BottomNavigationView items
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.feed),null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.overview),null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.start),null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.friends),null)
        TooltipCompat.setTooltipText(bottomNavigationView.findViewById<View>(R.id.more),null)
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

    private fun RLresizeDrawable(drawableId: Int, size: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(this, drawableId)
        drawable?.setBounds(0, 0, size, size)
        val bitmap = drawable?.toBitmap(size, size)
        return BitmapDrawable(resources, bitmap)
    }

    fun RLchepermissionphysicalActivity(){
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission is already granted
            RLonActivityRecognitionPermissionGranted()
        }
    }
    // Handle the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                RLonActivityRecognitionPermissionGranted()
            } else {
                // Permission denied
                RLonActivityRecognitionPermissionDenied()
            }
        }
    }
    private fun RLonActivityRecognitionPermissionGranted() {
        //Toast.makeText(this, "Activity Recognition Permission Granted", Toast.LENGTH_SHORT).show()
        // Start using physical activity recognition features
    }
    private fun RLonActivityRecognitionPermissionDenied() {
        Toast.makeText(this, "Activity Recognition Permission Denied", Toast.LENGTH_SHORT).show()
        // Handle the denial appropriately, such as notifying the RLuser about limited functionality
    }

    fun RLbottombarcolorDarkBlue(){
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.overview)
        item.setChecked(true)
        activityMainBinding.bottomNav.setBackgroundResource(R.color.AppNEWBGColor)
    }

    fun RLhidebottombarcolorwhite(){
        activityMainBinding.bottomNav.visibility=View.GONE
    }
    fun RLshowbottombarcolorwhite(){
        activityMainBinding.bottomNav.visibility=View.VISIBLE
        activityMainBinding.bottomNav.setBackgroundResource(R.color.AppWhiteColor)
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

    override fun onBackPressed() {
        super.onBackPressed()
        //RLshowbottombarcolorwhite()
    }
    override fun onDestroy() {
        super.onDestroy()
      //  unregisterReceiver(networkChangeReceiver) // Unregister receiver to avoid leaks
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent
        //intent?.putExtra("branch_force_new_session", true)
        Branch.sessionBuilder(this)
            .withCallback { referringParams, error ->
                if (error == null) {
                    RLHandleBranchData(referringParams)
                } else {
                    Log.e(TAG,"Branch Error: ${error.message}")
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
        Branch.sessionBuilder(this).withCallback { referringParams: JSONObject?, error: BranchError? ->
            if (error == null) {
                // Process the deep link data (if available)
                RLHandleBranchData(referringParams)
            } else {
                Log.e(TAG,"Branch Error: ${error.message}")
            }
        }.withData(intent.data).init()
        setupUsermoengage()
    }

    private fun setupUsermoengage(){

        RLFirebaseToFetchUserData { userData ->
            if (userData != null) {
                val userAuth = RLAuthManager().RlgetCurrentUser()
                MoEAnalyticsHelper.setUniqueId(this, userAuth!!.uid)

                MoEAnalyticsHelper.setFirstName(this,userData.firstName)
                MoEAnalyticsHelper.setLastName(this,userData.lastName)
                MoEAnalyticsHelper.setUserName(this,userData.displayName)
                MoEAnalyticsHelper.setBirthDate(this,userData.dob)
                MoEAnalyticsHelper.setEmailId(this,userData.emailId)

            } else {
                Log.e(TAG, "Error fetching user data")
            }
        }



    }

    private fun RLHandleBranchData(deepLinkData: JSONObject?) {
        RLDeepLinkHandler().onReceiveBranchIoLink(deepLinkData.toString(), this)
        Log.d(TAG,"Branch params: $deepLinkData")
       /* deepLinkData?.let {
            // Handle the data (e.g., navigate to a specific screen)
            Log.d(TAG,"Branch params: $it")

        }*/
    }

    /*
        override fun onBackPressed() {
            val fragmentclose:String=  PrefManager.getSomeStringValue(activity, PrefManager.current_fragment, "")
            if(fragment!!.contains(RLFragStart::class.java.simpleName)){
                Log.d(TAG, "" + fragment)
                showDialog(RLConstants.EXIT, RLConstants.SCHEDULE)
            }else{
                if (fragmentclose.equals(RLFragStart::class.java.simpleName)){
                    showDialog(RLConstants.EXIT,RLConstants.SCHEDULE)
                }else{
                    super.onBackPressed()
                }
            }
        }*/

}