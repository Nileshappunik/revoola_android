package com.revoola.base

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.moengage.core.DataCenter
import com.moengage.core.MoEngage
import io.branch.referral.Branch
import com.moengage.core.config.NotificationConfig
import com.moengage.core.config.LogConfig
import com.revoola.R

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
        val moEngage = MoEngage.Builder(this, getString(R.string.moengage_app_key))
            .configureNotificationMetaData(NotificationConfig(smallIcon = R.drawable.ic_notifications, largeIcon = R.mipmap.ic_launcher)).build()
        // Initialize MoEngage
        MoEngage.initialise(moEngage)

    }
}


