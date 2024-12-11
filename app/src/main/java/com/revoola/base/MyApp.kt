package com.revoola.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.moengage.core.DataCenter
import com.moengage.core.MoECoreHelper
import com.moengage.core.MoEngage
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.config.FcmConfig
import io.branch.referral.Branch
import com.moengage.core.config.NotificationConfig
import com.moengage.core.config.LogConfig
import com.moengage.pushbase.MoEPushHelper
import com.revoola.R
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.utils.RLTools

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firebase Database persistence
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)

        // Branch logging for debugging
        Branch.enableLogging()

        // Initialize Branch SDK
        Branch.getAutoInstance(this)
        // Configure MoEngage
        scxConfigureMoEngage()
    }

    private fun scxConfigureMoEngage() {
         // Configure MoEngage
        val moEngage = MoEngage.Builder(this,  getString(R.string.moengage_app_key))
            .configureNotificationMetaData(NotificationConfig(R.drawable.ic_notifications, R.mipmap.ic_launcher))
            .configureFcm(FcmConfig(false))
            .build()
        // Initialize MoEngage
        MoEngage.initialiseDefaultInstance(moEngage)

        val moEngageuser = MoEngage.Builder(this,  getString(R.string.moengage_app_key), DataCenter.DATA_CENTER_1)
            .build()//replace X with your data center number
        MoEngage.initialiseDefaultInstance(moEngageuser)

        //Notification Runtime Permission Check
        val isGranted= RLTools.ScxhasNotificationPermission(this)
        MoEPushHelper.getInstance().pushPermissionResponse(this, isGranted)
        if (isGranted){
            MoEPushHelper.getInstance().setUpNotificationChannels(this)
        }else{
            MoEPushHelper.getInstance().updatePushPermissionRequestCount(this, 555)
            MoEPushHelper.getInstance().requestPushPermission(this)
            MoEPushHelper.getInstance().navigateToSettings(this)
        }

    }

}


