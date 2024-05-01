package com.example.myfirstapp.activity

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.myfirstapp.R
import com.example.myfirstapp.base.BaseActivity
import com.example.myfirstapp.databinding.ActivityMainBinding
import com.example.myfirstapp.fragment.FragMore
import com.example.myfirstapp.utils.Constants
import com.example.myfirstapp.utils.PrefManager

class MainActivity  : BaseActivity() {
    val TAG: String = MainActivity::class.java.simpleName
    var fragment: String? = null
    lateinit var activityMainBinding: ActivityMainBinding
    var sucDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        activityMainBinding = inflateBindLayout(this, R.layout.activity_main) as ActivityMainBinding
        //loadFrag(FragStart(), TAG, true, FragStart::class.java.simpleName, false)
        val item: MenuItem = activityMainBinding.bottomNav.getMenu().findItem(R.id.start)
        item.setChecked(true)
        activityMainBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.overview -> {
                    activityMainBinding.bottomNav.setBackgroundResource(R.color.DarkBlue)
                   // loadFrag(FragOverview(), TAG, true, FragOverview::class.java.simpleName, false)
                    true
                }
                R.id.feed -> {
                    bottombarcolorwhite()
                   // loadFrag(FragFeed(), TAG, true, FragFeed::class.java.simpleName, false)
                    true
                }
                R.id.start -> {
                    bottombarcolorwhite()
                  //  loadFrag(FragStart(), TAG, true, FragStart::class.java.simpleName, false)
                    true
                }
                R.id.friends -> {
                    bottombarcolorwhite()
                   // loadFrag(FragFriends(), TAG, true, FragFriends::class.java.simpleName, false)
                    true
                }
                R.id.more -> {
                    bottombarcolorwhite()
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

    fun bottombarcolorwhite(){
        activityMainBinding.bottomNav.setBackgroundResource(R.color.white)
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