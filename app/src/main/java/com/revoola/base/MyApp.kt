package com.revoola.base

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoECoreHelper
import com.moengage.core.MoEngage
import com.moengage.core.config.FcmConfig
import io.branch.referral.Branch
import com.moengage.core.config.NotificationConfig
import com.moengage.core.config.PushKitConfig
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.geofence.MoEGeofenceHelper
import com.moengage.inapp.MoEInAppHelper
import com.moengage.pushbase.MoEPushHelper
import com.revoola.R
import com.revoola.moengage.callbacks.RLApplicationBackgroundListener
import com.revoola.moengage.callbacks.RLLogoutCompleteListener
import com.revoola.moengage.inapp.RLSelfHandledCallback
import com.revoola.moengage.inapp.RLClickActionCallback
import com.revoola.moengage.inapp.RLInAppLifecycleCallbacks
import com.revoola.moengage.push.RLCustomPushMessageListener
import com.revoola.moengage.push.RLGeofenceHitListener
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
        RLMoEngageInit()
    }

    private fun scxConfigureMoEngage() {
        // Notification Runtime Permission Check
        val isGranted= RLTools.ScxhasNotificationPermission(this)
        MoEPushHelper.getInstance().pushPermissionResponse(this, isGranted)
        if (isGranted){
            MoEPushHelper.getInstance().setUpNotificationChannels(this)
        }else{
            MoEPushHelper.getInstance().updatePushPermissionRequestCount(this, 555)
            MoEPushHelper.getInstance().requestPushPermission(this)
            MoEPushHelper.getInstance().navigateToSettings(this)
        }


        val moEngageuser = MoEngage.Builder(this,  getString(R.string.moengage_app_key), DataCenter.DATA_CENTER_1).build()
        MoEngage.initialiseDefaultInstance(moEngageuser)

         // Push Configure MoEngage
        val moEngage = MoEngage.Builder(this,  getString(R.string.moengage_app_key))
            .configureNotificationMetaData(NotificationConfig(R.drawable.ic_notifications, R.mipmap.ic_launcher))
            .build()
        // Initialize MoEngage
        MoEngage.initialiseDefaultInstance(moEngage)

    }

    private fun RLMoEngageInit(){
        val moEngage =
            MoEngage.Builder(this,getString(R.string.moengage_app_key), DataCenter.DATA_CENTER_1)
                .configureNotificationMetaData(
                    NotificationConfig(
                        smallIcon = R.drawable.ic_notifications,
                        largeIcon = R.drawable.ic_notifications,
                        notificationColor = R.color.AppMainColor,
                        isMultipleNotificationInDrawerEnabled = true,
                        isBuildingBackStackEnabled = true,
                        isLargeIconDisplayEnabled = true
                    )
                )
                .configureFcm(FcmConfig(true))
                .configurePushKit(PushKitConfig(true))
                .build()
        MoEngage.initialiseDefaultInstance(moEngage = moEngage)
        // register for application background listener
        MoECoreHelper.addAppBackgroundListener(RLApplicationBackgroundListener())
        // register for logout complete listener
        MoECoreHelper.addLogoutCompleteListener(RLLogoutCompleteListener())
        setupPushCallbacks()
        setupInAppCallbacks()

        // Register Geofence Hit Listener
        MoEGeofenceHelper.getInstance().addListener(RLGeofenceHitListener())

        // Enables geofence monitoring, required Only for Location-Triggered campaigns
        MoEGeofenceHelper.getInstance().startGeofenceMonitoring(this)

        // Notification Runtime Permission Check
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

    private fun setupPushCallbacks() {
        // callback for notification events and notification customisation point.
        MoEPushHelper.getInstance().registerMessageListener(RLCustomPushMessageListener())
        // Callback for Firebase Token
        MoEFireBaseHelper.getInstance().addTokenListener { token ->
            Log.e("setupPushCallbacks","fcm token: ${token.pushToken}")
            MoEFireBaseHelper.getInstance().passPushToken(applicationContext,token.pushToken)
        }

    }

    private fun setupInAppCallbacks() {
        // callback for in-app campaign click
        MoEInAppHelper.getInstance().setClickActionListener(RLClickActionCallback())
        // callback for in-app lifecycle - campaign shown/dismissed.
        MoEInAppHelper.getInstance().addInAppLifeCycleListener(RLInAppLifecycleCallbacks())
        // callback for self handled campaigns that are triggered based on events.
        MoEInAppHelper.getInstance().setSelfHandledListener(RLSelfHandledCallback())
    }
}


