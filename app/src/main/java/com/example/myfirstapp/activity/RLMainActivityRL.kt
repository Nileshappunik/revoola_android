package com.example.myfirstapp.activity

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.graphics.drawable.Drawable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myfirstapp.R
import com.example.myfirstapp.base.RLBaseActivity
import com.example.myfirstapp.databinding.RlActivityMainBinding
import com.example.myfirstapp.fragment.feed.RLFragFeed
import com.example.myfirstapp.fragment.friends.RLFragFriends
import com.example.myfirstapp.fragment.more.RLFragMore
import com.example.myfirstapp.fragment.overview.RLFragOverview
import com.example.myfirstapp.fragment.start.RLFragStart

class RLMainActivityRL  : RLBaseActivity() {
    val TAG: String = RLMainActivityRL::class.java.simpleName
    var fragment: String? = null
    lateinit var activityMainBinding: RlActivityMainBinding
    var sucDialog: Dialog? = null

    companion object {
        const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding = RLinflateBindLayout(this, R.layout.rl_activity_main) as RlActivityMainBinding
        RLloadFrag(RLFragStart(), TAG, true, RLFragStart::class.java.simpleName, false)
        RLbottombarcolorwhite()
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.start)
        item.setChecked(true)
        RLchepermissionphysicalActivity()

        activityMainBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.overview -> {
                    activityMainBinding.bottomNav.setBackgroundResource(R.color.AppNEWBGColor)
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragOverview(), TAG, true, RLFragOverview::class.java.simpleName, false)
                    true
                }
                R.id.feed -> {
                    RLbottombarcolorwhite()
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragFeed(), TAG, true, RLFragFeed::class.java.simpleName, false)
                    true
                }
                R.id.start -> {
                    RLbottombarcolorwhite()
                    RLshowbottombarcolorwhite()
                   RLloadFrag(RLFragStart(), TAG, true, RLFragStart::class.java.simpleName, false)

                    true
                }
                R.id.friends -> {
                    RLbottombarcolorwhite()
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragFriends(), TAG, true, RLFragFriends::class.java.simpleName, false)
                    true
                }
                R.id.more -> {
                    RLbottombarcolorwhite()
                    RLshowbottombarcolorwhite()
                    RLloadFrag(RLFragMore(), TAG, true, RLFragMore::class.java.simpleName, false)
                    true
                }
                else -> {
                   // loadFrag(RLFragStart(), TAG, true, RLFragStart::class.java.simpleName, false)
                    false
                }
            }
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
    fun RLbottombarcolorwhite(){
        activityMainBinding.bottomNav.setBackgroundResource(R.color.AppWhiteColor)
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
    }

    fun RLloadFrag(fragment: Fragment?, tagname: String?, isbackStack: Boolean, fragmentName: String?, type: Boolean): Boolean {
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
    }

    private fun showDialog(type: String, schedule: String) {
        sucDialog = Dialog(activity)
        sucDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (schedule == RLConstants.SCHEDULE) {
            sucDialog!!.setContentView(R.layout.rl_layout_dailog)
        }
        sucDialog!!.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(sucDialog!!.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val tvNo: TextView = sucDialog!!.findViewById(R.id.tvNo)
        val tvYes: TextView = sucDialog!!.findViewById(R.id.tvYes)

        if(type.equals(RLConstants.EXIT)) {
            val tvSubTitle: TextView = sucDialog!!.findViewById(R.id.tvSubTitle)
            val tvTitle: TextView = sucDialog!!.findViewById(R.id.tvTitle)

            tvTitle.RLText = resources.getString(R.string.exit)
            tvSubTitle.RLText = resources.getString(R.string.exit_app1)
        }

        tvNo.setOnClickListener(View.OnClickListener {
            sucDialog!!.dismiss()
        })

        tvYes.setOnClickListener(View.OnClickListener {
            if (type.equals(RLConstants.LOGOUT_D)) {
                sucDialog!!.dismiss()
                // logoutapicall()
            }else if (type.equals(RLConstants.EXIT)){
                sucDialog!!.dismiss()
                finishAffinity()
            }
        })
        sucDialog!!.show()
        sucDialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }*/
}