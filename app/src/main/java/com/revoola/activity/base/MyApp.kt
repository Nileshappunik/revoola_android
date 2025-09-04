package com.revoola.activity.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.inappmessaging.model.MessageType
import com.revoola.BuildConfig
import com.moengage.core.DataCenter
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
import com.revoola.commonobject.RLTools
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databasefirebase.RlFirebaseWorker
import com.revoola.utils.RLConstants

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Enable Firebase Database persistence
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)

        //BackGround Firebase WatchData
        val request = OneTimeWorkRequestBuilder<RlFirebaseWorker>().build()
        WorkManager.getInstance(this).enqueue(request)

        // Branch logging for debugging
        Branch.enableLogging()

        // Initialize Branch SDK
        Branch.getAutoInstance(this)

        FirebaseInAppMessaging.getInstance().addClickListener { inAppMessage, _ ->
            // Handle the message
            if (inAppMessage.messageType == MessageType.MODAL) {
                RLTools.rl_logEPrint("FirebaseMessage","Firebase Message:- $inAppMessage")
            }
        }

        // Initialize RevenueCat using Purchases.Builder
        val revenueCatApiKey = BuildConfig.REVENUECAT_ANDROID_API_KEY
        // Otherwise, you can logIn later.
        Purchases.configure(
            PurchasesConfiguration.Builder(this, revenueCatApiKey)
                .appUserID(RLAuthManager().rl_getCurrentUser()?.uid) // nullable ok
                .build()
        )
       // val configurationRevenueCat = PurchasesConfiguration.Builder(this, BuildConfig.REVENUECAT_ANDROID_API_KEY).build()
        // Initialize RevenueCat with your API key
      //  Purchases.configure(configurationRevenueCat) // Replace with your RevenueCat API key



        // Initialize MoEngage SDK
        rl_initializeMoEngage()

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                MoEInAppHelper.getInstance().showInApp(activity)
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

    }

    private fun rl_initializeMoEngage() {
        // Check notification permissions once
        val isGranted = RLTools.rl_hasNotificationPermission(this)
        MoEPushHelper.getInstance().pushPermissionResponse(this, isGranted)

        if (isGranted) {
            MoEPushHelper.getInstance().setUpNotificationChannels(this)
        } else {
            MoEPushHelper.getInstance().updatePushPermissionRequestCount(this, 555)
            MoEPushHelper.getInstance().requestPushPermission(this)
            MoEPushHelper.getInstance().navigateToSettings(this)
        }

        // Initialize MoEngage SDK with complete configuration
        val moEngage = MoEngage.Builder(this, getString(R.string.moengage_app_key), DataCenter.DATA_CENTER_1)
            .configureNotificationMetaData(
                NotificationConfig(
                    smallIcon = R.drawable.ic_notifications,
                    largeIcon = R.drawable.ic_notifications,
                    notificationColor = R.color.AppMainColor,
                    isMultipleNotificationInDrawerEnabled = true,
                    isBuildingBackStackEnabled = true,
                    isLargeIconDisplayEnabled = true))
            .configureFcm(FcmConfig(true))
            .configurePushKit(PushKitConfig(true))
            .build()

        // Initialize MoEngage ONCE
        MoEngage.initialiseDefaultInstance(moEngage)

        // Set up all listeners
        MoECoreHelper.addAppBackgroundListener(RLApplicationBackgroundListener())
        MoECoreHelper.addLogoutCompleteListener(RLLogoutCompleteListener())
        setupPushCallbacks()
        setupInAppCallbacks()

        // Register Geofence Hit Listener
        MoEGeofenceHelper.getInstance().addListener(RLGeofenceHitListener())
        MoEGeofenceHelper.getInstance().startGeofenceMonitoring(this)
    }
    private fun setupPushCallbacks() {
        //Callback for notification events and notification customisation point
        MoEPushHelper.getInstance().registerMessageListener(RLCustomPushMessageListener())
        //Callback for Firebase Token
        MoEFireBaseHelper.getInstance().addTokenListener { token ->
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
        //Display InApp
        MoEInAppHelper.getInstance().showInApp(this)
        //Display Nudges
        MoEInAppHelper.getInstance().showNudge(this)

        //Reset Context
        // MoEInAppHelper.getInstance().resetInAppContext()
        //Handling Configuration change
        // MoEInAppHelper.getInstance().onConfigurationChanged()

    }

}


