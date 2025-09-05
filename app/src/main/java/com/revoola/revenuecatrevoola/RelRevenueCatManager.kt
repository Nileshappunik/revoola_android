package com.revoola.revenuecatrevoola

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.revenuecat.purchases.*
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.models.StoreTransaction

object RelRevenueCatManager {

    fun logInIfNeeded(appUserId: String, onDone: (() -> Unit)? = null) {
        Purchases.sharedInstance.logInWith(
            appUserId,
            onError = { _ -> onDone?.invoke() },
            onSuccess = { _: CustomerInfo, _: Boolean -> onDone?.invoke() }
        )
    }

    fun logOut(onDone: (() -> Unit)? = null) {
        Purchases.sharedInstance.logOutWith(
            onError = { _ -> onDone?.invoke() },
            onSuccess = { _: CustomerInfo -> onDone?.invoke() }
        )
    }

    fun restore(onDone: (Boolean) -> Unit) {
        Purchases.sharedInstance.restorePurchases(
            object : ReceiveCustomerInfoCallback {
                override fun onReceived(info: CustomerInfo) {
                    val hasActive = info.entitlements.active.isNotEmpty()
                    onDone(hasActive)
                }

                override fun onError(error: PurchasesError) {
                    onDone(false)
                }
            }
        )
    }

    fun checkIsSubscribed(onDone: (Boolean) -> Unit) {
        Purchases.sharedInstance.getCustomerInfo(
            CacheFetchPolicy.CACHED_OR_FETCHED,
            object : ReceiveCustomerInfoCallback {
                override fun onReceived(info: CustomerInfo) {
                    val hasActive = info.entitlements.active.isNotEmpty()
                    onDone(hasActive)
                }

                override fun onError(error: PurchasesError) {
                    onDone(false)
                }
            }
        )
    }

    fun subscribe(activity: Activity,packageToPurchase: Package,onSuccess: (StoreTransaction, CustomerInfo) -> Unit,
        onError: (Throwable) -> Unit) {
        // You can show the default RevenueCat dialog using the `Purchases` SDK's purchase flow
        Purchases.sharedInstance.purchasePackage(activity, packageToPurchase, object : PurchaseCallback {
                override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                    onSuccess(storeTransaction, customerInfo)
                }
                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    onError(Throwable(error.message))
                }
            })
    }

    fun updateGoogleOnFirebase() {
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

        FirebaseDatabase.getInstance()
            .getReference("proposed_structure/revoola_user_settings/$uid/basic_data/current_subscription")
            .setValue(payload)
    }

    fun getSubscriptionName(packageType: String): String? {
        return when (packageType) {
            "MONTHLY" -> "month"
            "ANNUAL" -> "year"
            else -> "month"
        }
    }
}


