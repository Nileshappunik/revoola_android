package com.revoola.revenuecatrevoola

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

object UpdateFirebaseSubscription {

    /**
     * Mirrors your iOS 'updateAppleOnFirebase(price:)' but for Android.
     * Adjust field names to match your existing schema.
     */
    fun updateGoogleOnFirebase(price: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val nowSeconds = (System.currentTimeMillis() / 1000).toInt()
        val validDays = 30 // keep same rule as iOS or read from Remote Config

        val payload = mapOf(
            "timestamp" to nowSeconds,
            "validDays" to validDays,
            "isSubscriptionCheckRequired" to true,
            "isSubscriptionRequired" to true,
            "remark" to "android",
            "subscriptionName" to "Android-Premium"
        )

        // Matches the same ProposedStructure pathing you use on iOS Firebase code
        FirebaseDatabase.getInstance()
            .getReference("proposed_structure/revoola_user_settings/$uid/basic_data/current_subscription")
            .setValue(payload)
    }
}
