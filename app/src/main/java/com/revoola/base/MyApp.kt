package com.revoola.base

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
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


