package com.example.myfirstapp.activity

import android.Manifest
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.fragment.feed.FragFeed
import com.example.myfirstapp.fragment.friends.FragFriends
import com.example.myfirstapp.fragment.more.FragMore
import com.example.myfirstapp.fragment.overview.FragOverview
import com.example.myfirstapp.fragment.start.FragStart

class MainActivity  : BaseActivity() {
    val TAG: String = MainActivity::class.java.simpleName
    var fragment: String? = null
    lateinit var activityMainBinding: ActivityMainBinding
    var sucDialog: Dialog? = null

    companion object {
        const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding = inflateBindLayout(this, R.layout.activity_main) as ActivityMainBinding
        loadFrag(FragStart(), TAG, true, FragStart::class.java.simpleName, false)
        bottombarcolorwhite()
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.start)
        item.setChecked(true)
        chepermissionphysicalActivity()
        activityMainBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.overview -> {
                    activityMainBinding.bottomNav.setBackgroundResource(R.color.DarkBlue)
                    showbottombarcolorwhite()
                    loadFrag(FragOverview(), TAG, true, FragOverview::class.java.simpleName, false)
                    true
                }
                R.id.feed -> {
                    bottombarcolorwhite()
                    showbottombarcolorwhite()
                    loadFrag(FragFeed(), TAG, true, FragFeed::class.java.simpleName, false)
                    true
                }
                R.id.start -> {
                    bottombarcolorwhite()
                    showbottombarcolorwhite()
                    loadFrag(FragStart(), TAG, true, FragStart::class.java.simpleName, false)
                    true
                }
                R.id.friends -> {
                    bottombarcolorwhite()
                    showbottombarcolorwhite()
                    loadFrag(FragFriends(), TAG, true, FragFriends::class.java.simpleName, false)
                    true
                }
                R.id.more -> {
                    bottombarcolorwhite()
                    showbottombarcolorwhite()
                    loadFrag(FragMore(), TAG, true, FragMore::class.java.simpleName, false)
                    true
                }
                else -> {
                   // loadFrag(FragStart(), TAG, true, FragStart::class.java.simpleName, false)
                    false
                }
            }
        }
    }
    fun chepermissionphysicalActivity(){
        // Check if the permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission is already granted
            onActivityRecognitionPermissionGranted()
        }
    }
    // Handle the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                onActivityRecognitionPermissionGranted()
            } else {
                // Permission denied
                onActivityRecognitionPermissionDenied()
            }
        }
    }
    private fun onActivityRecognitionPermissionGranted() {
        //Toast.makeText(this, "Activity Recognition Permission Granted", Toast.LENGTH_SHORT).show()
        // Start using physical activity recognition features
    }
    private fun onActivityRecognitionPermissionDenied() {
        Toast.makeText(this, "Activity Recognition Permission Denied", Toast.LENGTH_SHORT).show()
        // Handle the denial appropriately, such as notifying the user about limited functionality
    }
    fun bottombarcolorwhite(){
        activityMainBinding.bottomNav.setBackgroundResource(R.color.white)
    }

    fun hidebottombarcolorwhite(){
        activityMainBinding.bottomNav.visibility=View.GONE
    }

    fun showbottombarcolorwhite(){
        activityMainBinding.bottomNav.visibility=View.VISIBLE
    }

    fun loadFrag(fragment: Fragment?, tag: String?, isbackStack: Boolean, fragmentName: String?, type: Boolean): Boolean {
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
            if (isbackStack) {
                fragmentTransaction.addToBackStack(fragmentName)
            }
            if (!isFinishing) fragmentTransaction.commitAllowingStateLoss() else fragmentTransaction.commit()
            return true
        }
        return false
    }
/*
    override fun onBackPressed() {
        val fragmentclose:String=  PrefManager.getSomeStringValue(activity, PrefManager.current_fragment, "")
        if(fragment!!.contains(FragStart::class.java.simpleName)){
            Log.d(TAG, "" + fragment)
            showDialog(Constants.EXIT, Constants.SCHEDULE)
        }else{
            if (fragmentclose.equals(FragStart::class.java.simpleName)){
                showDialog(Constants.EXIT,Constants.SCHEDULE)
            }else{
                super.onBackPressed()
            }
        }
    }

    private fun showDialog(type: String, schedule: String) {
        sucDialog = Dialog(activity)
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (schedule == Constants.SCHEDULE) {
            sucDialog!!.setContentView(R.layout.layout_dailog)
        }
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog!!.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.tvYes)

        if(type.equals(Constants.EXIT)) {
            val tvSubTitle: TextView = sucDialog!!.findViewById(R.id.tvSubTitle)
            val tvTitle: TextView = sucDialog!!.findViewById(R.id.tvTitle)

            tvTitle.text = resources.getString(R.string.exit)
            tvSubTitle.text = resources.getString(R.string.exit_app1)
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            if (type.equals(Constants.LOGOUT_D)) {
                sucDialog!!.dismiss()
                // logoutapicall()
            }else if (type.equals(Constants.EXIT)){
                sucDialog!!.dismiss()
                finishAffinity()
            }
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }*/
}