package com.example.myfirstapp.base

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.branch.referral.Branch

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


    }
}


