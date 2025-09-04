package com.revoola.revenuecatrevoola

// RelRevenueCatManager.kt
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.revenuecat.purchases.*
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.GetStoreProductsCallback
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.models.GoogleStoreProduct
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.models.SubscriptionOption
import com.revoola.commonobject.RLTools
import com.revoola.fragment.more.RLFragCurrentSubScription


object RelRevenueCatManager {
    private val TAG: String = RLFragCurrentSubScription::class.java.simpleName
    /** Call once user is signed in to mirror iOS logIn(logged in with globalUid). */
    fun logInIfNeeded(appUserId: String, onDone: (() -> Unit)? = null) {
        Purchases.sharedInstance.logInWith(
            appUserId,
            onError = { _ /* PurchasesError */ -> onDone?.invoke() },
            onSuccess = { _: CustomerInfo, _: Boolean -> onDone?.invoke() }
        )
    }

    fun logOut(onDone: (() -> Unit)? = null) {
        Purchases.sharedInstance.logOutWith(
            onError = { _ /* PurchasesError */ -> onDone?.invoke() },
            onSuccess = { _: CustomerInfo -> onDone?.invoke() }
        )
    }

    /** Promo code button on Android: open Play Store redeem screen. */
    fun openPromoCodeRedeem(context: Context) {
        val redeemUri = Uri.parse("https://play.google.com/redeem")
        val intent = Intent(Intent.ACTION_VIEW, redeemUri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // Fallback to Play Store web
            context.startActivity(Intent(Intent.ACTION_VIEW, redeemUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    /**
     * Subscribe button: decide productId like iOS and purchase via RevenueCat.
     * If you use Offerings, you can fetch and pick the matching `StoreProduct` by id.
     */
    // -------------------------
    // buySubscriptionInAppPurchase(_ price, promoCode)
    // -------------------------

    fun subscribe(activity: Activity, onSuccess: () -> Unit,
        onError: (Throwable) -> Unit   // <— accept Throwable so we can pass either RC errors or our own
    ) {
        val productId = RelSubscriptionConfig.pickProductId(RelAccountManagerAndroid.referId())

        Purchases.sharedInstance.getProducts(
            productIds = listOf(productId),
            type = ProductType.SUBS,  // keep SUBS explicit for Play Billing subs
            callback = object : GetStoreProductsCallback {
                override fun onReceived(products: List<StoreProduct>) {
                    val product = products.firstOrNull()
                    if (product == null) {
                        onError(IllegalStateException("Product $productId not found"))
                        return
                    }
                    
                    // Form A (no upgrade info):
                    Purchases.sharedInstance.purchaseProduct(
                        activity,
                        product,
                        object : PurchaseCallback {
                            override fun onCompleted(
                                storeTransaction: StoreTransaction,
                                customerInfo: CustomerInfo
                            ) {
                                // Use the ID we requested to map price
                                val price = RelSubscriptionConfig.priceFor(productId)
                                RelAccountManagerAndroid.onSubscribed(price)
                                onSuccess()
                            }

                            override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                onError(error)
                            }
                        }
                    )

                }

                override fun onError(error: PurchasesError) {
                    onError(error)
                }
            }
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





}


