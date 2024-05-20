package com.example.myfirstapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.myfirstapp.fragment.common.FragNoInternet
import com.example.myfirstapp.utils.Tools.nextFinishAllActivity


open class BaseFragment : Fragment() {
    val TAG1: String = BaseFragment::class.java.simpleName
    private val STORAGE_PERMISSION_REQUEST_CODE = 100


    open fun onBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.requireActivity().onBackPressed() }
    }

    open fun onDirectBackPresAct(o: ImageView) {
        super.requireActivity()!!.onBackPressed()
    }

    open fun onClickNoTask(o: LinearLayout) {
        o.setOnClickListener { v: View? -> }
    }

    open fun clickActiviy(o: View, cls: Class<*>?, finishAll: String) {
        if (finishAll == "NEXT") {
            o.setOnClickListener { nextActivity(cls) }
        } else if (finishAll == "ALL") {
            o.setOnClickListener { nextFinishAllActivity(requireActivity(), cls) }
        }
    }

    //TODO : Full Screen
    open fun setStatusBarDark() {
        requireActivity()!!.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status text dark
        requireActivity()!!.window.statusBarColor =
            ContextCompat.getColor(requireActivity()!!, R.color.black)
    }

    open fun setSystemBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity()!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.statusBarColor = requireActivity()!!.resources.getColor(color)
        }
    }


    open fun nextActivity(cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /*open fun opeDrawerBar(findId: View) {
        findId.setOnClickListener(View.OnClickListener {
            (activity as MainActivity).activityMainBinding.drawerLayout.openDrawer(Gravity.LEFT)
        })
    }*/

    //TODO : DataBind
    open fun inflateBindLayout(activity1: Class<FragmentActivity>?,inflater: LayoutInflater, layoutName: Int, container: ViewGroup?, ): Any? {
        return DataBindingUtil.inflate(inflater!!, layoutName, container, false)
    }

    val DIALOG_QUEST_CODE: Int = 205
    fun showDialogFullscreen(): FragNoInternet {
        val dialog = FragNoInternet()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        dialog.show(ft, TAG1)

        return dialog
    }

   /* open fun sessionOut() {
        ShowProgressDialog(requireActivity())
        Toast.makeText(activity, "Session Expire", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            PrefManager.clear_all(activity)
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().startActivity(intent)
            requireActivity().finish()
            BaseProgress.hideProgressDialog()
        }, 700)
    }*/

    open fun commonToast(message:String){
        Toast.makeText(activity,message, Toast.LENGTH_SHORT).show()
    }



    open fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    open fun requestStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
    }

    open fun closeFragment() {
        // Close the fragment by popping it from the back stack
        parentFragmentManager.popBackStack()
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, you can proceed with your code
            } else {
                // Permission is denied
                // You may want to show a message or handle the case where the user denies the permission
                //Toast.makeText(activity,"Permission is denied",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
