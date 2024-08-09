package com.example.myfirstapp.fragment.start

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import android.view.ViewTreeObserver
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlDialogHelpStartBinding
import com.example.myfirstapp.databinding.RlFragStartBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.fragment.start.adapter.RLStartListAdapter
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RLFragStart : RLBaseFragment() {
    val TAG: String = RLFragStart::class.java.simpleName
    lateinit var fragBinding: RlFragStartBinding

    private val binding by lazy {
        RlFragStartBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_start, container) as RlFragStartBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragStart" )
        RLStartList()
        return fragBinding.root
    }
    private fun RLUiSetUP(dataList: List<RLStartAllMenuModel>) {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()

        fragBinding.inlayTop.ivBack.visibility=View.GONE
        fragBinding.inlayTop.ivhelp.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.foryourmindandbody))
        fragBinding.inlayTop.smallLogo.visibility=View.VISIBLE
        fragBinding.inlayTop.ivTitle.visibility=View.GONE
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.thebestyoueveryday))

        fragBinding.rvStart.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                fragBinding.rvStart.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val height =  fragBinding.rvStart.height
                println("RelativeLayout total height: $height pixels")

                val linearLayoutMain = LinearLayoutManager(activity)
                fragBinding.rvStart.layoutManager = linearLayoutMain
                val adapter = RLStartListAdapter(activity,dataList,height)
                fragBinding.rvStart.adapter=adapter

            }
        })

        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
    }
    private fun RLStartList() {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLALLMENULISTRead(RLConstants.MAIN){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    Log.d(TAG,"Response:- $jsonArray")
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    RLUiSetUP(dataList)
                }catch (e:Exception){
                    Log.e(TAG,"Catch:- ${e.message}")
                }
            }
        }
    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpStartBinding= RlDialogHelpStartBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}