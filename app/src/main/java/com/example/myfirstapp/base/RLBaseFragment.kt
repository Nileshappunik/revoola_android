package com.example.myfirstapp

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.fragment.common.RLFragNoInternet
import com.example.myfirstapp.fragment.start.RLStartHelpModel
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.RLTools.RLnextFinishAllActivity
import com.google.gson.Gson


open class RLBaseFragment : Fragment() {
    val TAG1: String = RLBaseFragment::class.java.simpleName


    open fun RLonBackPresAct(o: ImageView) {
        o.setOnClickListener { v: View? -> super.requireActivity().onBackPressed() }
    }

    open fun RLonDirectBackPresAct(o: ImageView) {
        super.requireActivity()!!.onBackPressed()
    }

    open fun RLonClickNoTask(o: LinearLayout) {
        o.setOnClickListener { v: View? -> }
    }

    open fun RLclickActiviy(o: View, cls: Class<*>?, finishAll: String) {
        if (finishAll == "NEXT") {
            o.setOnClickListener { RLnextActivity(cls) }
        } else if (finishAll == "ALL") {
            o.setOnClickListener { RLnextFinishAllActivity(requireActivity(), cls) }
        }
    }

    //TODO : Full Screen
    open fun RLsetStatusBarDark() {
        requireActivity()!!.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//  set status RLText dark
        requireActivity()!!.window.statusBarColor =
            ContextCompat.getColor(requireActivity()!!, R.color.AppBlackColor)
    }

    open fun RLsetSystemBarColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity()!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.statusBarColor = requireActivity()!!.resources.getColor(color)
        }
    }


    open fun RLnextActivity(cls: Class<*>?) {
        val intent = Intent(activity, cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    /*open fun RLopeDrawerBar(findId: View) {
        findId.setOnClickListener(View.OnClickListener {
            (activity as RLMainActivityRL).activityMainBinding.drawerLayout.openDrawer(Gravity.LEFT)
        })
    }*/

    //TODO : DataBind
    open fun RLinflateBindLayout(activity1: Class<FragmentActivity>?, inflater: LayoutInflater, layoutName: Int, container: ViewGroup?, ): Any? {
        return DataBindingUtil.inflate(inflater!!, layoutName, container, false)
    }

    val RLDIALOG_QUEST_CODE: Int = 205
    fun RLshowDialogFullscreen(): RLFragNoInternet {
        val dialog = RLFragNoInternet()
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        dialog.show(ft, TAG1)

        return dialog
    }


   /* open fun RLsessionOut() {
        ShowProgressDialog(requireActivity())
        Toast.makeText(activity, "Session Expire", Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({
            PrefManager.clear_all(activity)
            val intent = Intent(activity, RLLoginActivityRL::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().startActivity(intent)
            requireActivity().finish()
            RLBaseProgress.hideProgressDialog()
        }, 700)
    }*/

    open fun RLcommonToast(message:String){
        try{
            if (isAdded) {
                context?.let {
                    Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Log.e(TAG1,"TOAST EXCEPTION:- ${e.message}")
        }

    }


    open fun RLcloseFragment() {
        // Close the fragment by popping it from the back stack
        parentFragmentManager.popBackStack()
    }

    open fun RLScreenSet(isLandScape:Boolean) {
        val uiModeManager =  activity?.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val currentModeType = uiModeManager.currentModeType

        if (currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            // The device is running in TV mode (Android TV)
            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
        } else {
            // The device is not running in TV mode
            if (isLandScape){
                activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE)
            }else{
                activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
        }

    }

    open fun RLBottomHideShowSet(isShow:Boolean) {
        if (isShow){
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()

        }else{
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        }
    }

    open fun RLHelpHideShowSet(isShow: Boolean, imageHelp: ImageView, startHelpContent: String) {
        val helpString= RLPrefManager.RLgetSomeStringValue(activity, startHelpContent,"" )

        if (helpString.isNotEmpty()){
            try {
                val gson = Gson()
                val StartHelpModel: RLStartHelpModel = gson.fromJson(helpString, RLStartHelpModel::class.java)
                if(StartHelpModel.visible){
                    imageHelp.visibility=View.VISIBLE
                }else{
                    imageHelp.visibility=View.GONE
                }
            }catch (e:Exception){
              e.printStackTrace()
            }
        }
        /*if (isShow){
            imageHelp.visibility=View.VISIBLE
        }else{
            imageHelp.visibility=View.GONE
        }*/
    }

}
