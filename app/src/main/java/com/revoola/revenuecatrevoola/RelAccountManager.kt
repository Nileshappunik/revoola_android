package com.revoola.revenuecatrevoola

import com.revoola.databasefirebase.RLAuthManager

// RelAccountManager.kt
object RelAccountManagerAndroid {
    // Use your actual user singleton / Firebase auth to provide this
    fun userIdOrNull(): String? = RLAuthManager().rl_getCurrentUser()?.uid // e.g., FirebaseAuth.getInstance().currentUser?.uid


    @Volatile
    private var currentReferId: String = ""

    // Call this once when you parse your Firebase Dynamic Link / Remote Config.
    fun setReferId(value: String?) {
        currentReferId = value?.trim().orEmpty()
    }

    // This should reflect the same "referUser/referId" you use on iOS dynamic link.
    fun referId(): String = currentReferId // e.g., from dynamic link / remote config

    // Price chosen earlier for analytics / updating Firebase
    fun onSubscribed(price: String) {
        // Hook: call your API / Firebase the same way as iOS `updateAppleOnFirebase(price:)`
        UpdateFirebaseSubscription.updateGoogleOnFirebase(price)
    }


}

// RelSubscriptionConfig.kt
object RelSubscriptionConfig {
    // Mirror of your iOS product IDs
    const val PROD_299  = "com.revoola.appSubscription299"
    const val PROD_499  = "com.revoola.appSubscription499"
    const val PROD_699  = "com.revoola.appSubscription699"
    const val PROD_999  = "com.revoola.appSubscription"
    const val PROD_2999 = "com.revoola.appSubscription2999"

    // These lists should be filled from Remote Config the same way you do on iOS
    // (RELSubscriptionManager.fetchOrganisations…).
    val orgs699   = mutableSetOf<String>()   // iOS: organisations (999 in old naming) → 6.99 here per your current mapping
    val orgs499   = mutableSetOf<String>()
    val orgs299   = mutableSetOf<String>()
    val orgs2999  = mutableSetOf<String>()

    // iOS has a special-case referId == "EDenRed" → 6.99
    private val specialEdenRed = "EDenRed".lowercase()

    fun pickProductId(referIdRaw: String): String {
        val rid = referIdRaw.lowercase()
        return when {
            rid == specialEdenRed -> PROD_699
            rid in orgs699        -> PROD_699
            rid in orgs499        -> PROD_499
            rid in orgs299        -> PROD_299
            rid in orgs2999       -> PROD_2999
            else                  -> PROD_699 // default like iOS
        }
    }

    fun priceFor(productId: String): String = when (productId) {
        PROD_299  -> "2.99"
        PROD_499  -> "4.99"
        PROD_699  -> "6.99"
        PROD_2999 -> "29.99"
        else      -> "6.99"
    }
}
