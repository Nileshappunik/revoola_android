package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import com.moengage.pushbase.internal.repository.VALUE
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.getOfferingsWith
import com.revoola.RLBaseProgress
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragCurrentSubscriptionBinding
import com.revoola.revenuecatrevoola.RelRevenueCatManager
import com.revenuecat.purchases.Package
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.enumclass.RLSubscribePageDesign
import com.revoola.model.RLRevoolaUsersSettingsModel
import java.util.Date

class RLFragCurrentSubScription : RLBaseFragment() {
    private val TAG: String = RLFragCurrentSubScription::class.java.simpleName
    private var currentUser: String = ""
    private var packagePrice: String = ""

    private val fragBinding by lazy {
        RlFragCurrentSubscriptionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragCurrentSubScription" )
        rl_uisetup()
        return fragBinding.root
    }
    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)
        currentUser = RLAuthManager().rl_getCurrentUser()?.uid?:""
        // Fetch user settings(appUnit)
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
                setupDesign(goToSubscription(userData))
                fragBinding.inlayPremiumUser.txtRevoolaDate.setText("since ${RLTools.rl_formatTimestamp(userData.joiningDate)}")
            } else {
                RLTools.rl_logEPrint(TAG, "Error fetching user data")
            }
        }

        fragBinding.relay1.txtRevoolaDes.visibility=View.VISIBLE
        fragBinding.relay1.txtRevoolaUser.visibility=View.GONE
        fragBinding.relay1.txtRevoolaDate.visibility=View.GONE

        fragBinding.relay2.txtRevoolaDes.visibility=View.VISIBLE
        fragBinding.relay2.txtRevoolaUser.visibility=View.GONE
        fragBinding.relay2.txtRevoolaDate.visibility=View.GONE

        fragBinding.relay3.txtRevoolaDes.visibility=View.VISIBLE
        fragBinding.relay3.txtRevoolaUser.visibility=View.GONE
        fragBinding.relay3.txtRevoolaDate.visibility=View.GONE

        fragBinding.relay4.txtRevoolaDes.visibility=View.VISIBLE
        fragBinding.relay4.txtRevoolaUser.visibility=View.GONE
        fragBinding.relay4.txtRevoolaDate.visibility=View.GONE

        fragBinding.relay5.txtRevoolaDes.visibility=View.VISIBLE
        fragBinding.relay5.txtRevoolaUser.visibility=View.GONE
        fragBinding.relay5.txtRevoolaDate.visibility=View.GONE

        fragBinding.relay1.txtRevoolaDes.setText(R.string.mindandbodyclasses)
        fragBinding.relay2.txtRevoolaDes.setText(R.string.trackyourprogress)
        fragBinding.relay3.txtRevoolaDes.setText(R.string.classandactivities)
        fragBinding.relay4.txtRevoolaDes.setText(R.string.personalisecalender)
        fragBinding.relay5.txtRevoolaDes.setText(R.string.challengesfriends)

        fragBinding.txtPrivacyPolicy.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isTermAndCondition",false)
            (context as RLMainActivityRL).rl_loadFrag(RLFragTermAndCondition().newInstance(bundle), TAG, true, null, false)
        }
        fragBinding.txtTermsOfUse.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isTermAndCondition",true)
            (context as RLMainActivityRL).rl_loadFrag(RLFragTermAndCondition().newInstance(bundle), TAG, true,null, false)
        }
        fragBinding.txtContinuewithfree.setOnClickListener {
            rl_closeFragment()
        }

//        RelRevenueCatManager.logInIfNeeded(currentUser) { customerInfo, error ->
//            if (error != null) {
//                RLTools.rl_logDPrint(TAG, "❌ RevenueCat login failed: ${error.message}")
//            } else if (customerInfo != null) {
//                val entitlementId = "Monthly" // 👈 your entitlement identifier in RevenueCat
//
//                val entitlementInfo = customerInfo.entitlements[entitlementId]
//                rl_SubscribeUiSetup(entitlementInfo?.isActive?:false)
//            }
//        }

        getAvailableSubscriptions()
    }
    private fun getAvailableSubscriptions() {
        Purchases.sharedInstance.getOfferingsWith {offerings: Offerings ->
            if (offerings != null) {
                // Handle the available offerings
                val currentOffering = offerings.current
                if (currentOffering != null) {
                    val availablePackages = currentOffering.availablePackages
                    val packageToPurchase: Package = availablePackages.first()
                    handleAvailableSubscriptions(packageToPurchase)
                }
            }
        }
    }
    private fun handleAvailableSubscriptions(packageToPurchase: Package) {
         packagePrice = packageToPurchase.product.price.formatted
        val subscriptionName = RelRevenueCatManager.getSubscriptionName(packageToPurchase.packageType.name)
        val descriptionString="By tapping subscribe/upgrade your payment will be charged to your PlayStore account, and your account will be charged each $subscriptionName at the price of $packagePrice until you cancel it in settings in the PlayStore at least 24 hours prior to the end of the current period."
        fragBinding.txtDollarsign.setText(packagePrice)
        fragBinding.txtBytappingsubscribeyourpayment.setText(descriptionString)

        fragBinding.btnSubscribe.setOnClickListener {
            if(isAdded) RLBaseProgress.rl_showProgressDialog(requireActivity())
            RelRevenueCatManager.subscribe(
                activity = requireActivity(),
                packageToPurchase= packageToPurchase,
                onSuccess = {storeTransaction,customerInfo ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logDPrint(TAG,"Subscribed successfully!")
                    setupDesign(RLSubscribePageDesign.PremiumAccount)
                },
                onError = {
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logEPrint(TAG,"Subscribed Error :- ${it.message}")
                }
            )
        }

        fragBinding.txtHavepromocode.setOnClickListener {
            if(isAdded) RLBaseProgress.rl_showProgressDialog(requireActivity())
            RelRevenueCatManager.subscribe(
                activity = requireActivity(),
                packageToPurchase= packageToPurchase  ,
                onSuccess = {storeTransaction,customerInfo ->
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logDPrint(TAG,"Subscribed successfully!")
                },
                onError = {
                    RLBaseProgress.rl_hideProgressDialog()
                    RLTools.rl_logEPrint(TAG,"Subscribed Error :- ${it.message}")
                }
            )
        }
    }
    private fun goToSubscription(user: RLRevoolaUsersSettingsModel): RLSubscribePageDesign {
        val subscription = user.currentSubscription
        val subscriptionName = subscription.subscriptionName.lowercase()
        // Suppose `join` is in seconds (like Swift's timeIntervalSince1970)
        val joinDate = Date(subscription.timestamp * 1000) // Java Date takes milliseconds
        val now = Date()
        // Calculate difference in days
        val diffInMillis = now.time - joinDate.time
        val spentDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        val validDays = subscription.validDays
        var isPaidActive = false
        if(spentDays > validDays){
            isPaidActive = false
        }else{
            isPaidActive = true
        }
        return  when {
            subscriptionName == "freemium" -> RLSubscribePageDesign.FreeAccount

            isPaidActive &&
                    subscriptionName != "trial-premium" &&
                    subscriptionName != "freemium" -> RLSubscribePageDesign.PremiumAccount

            subscriptionName == "premium" -> RLSubscribePageDesign.PremiumAccount

            isPaidActive && subscriptionName == "trial-premium" -> RLSubscribePageDesign.OnTrial

            !isPaidActive &&
                    (user.currentGroup.contains("all", ignoreCase = true) ||
                            user.currentGroup.contains("Instructor", ignoreCase = true) ||
                            user.currentGroup.contains("premium", ignoreCase = true)) &&
                    subscription.subscriptionName.isEmpty() -> RLSubscribePageDesign.PremiumAccount

            else -> RLSubscribePageDesign.FreeAccount
        }
    }
    private fun setupDesign(pageDesign:RLSubscribePageDesign){
        when(pageDesign){
            RLSubscribePageDesign.FreeAccount -> {
                // Setup for Free Account
                fragBinding.txtFreeonemonth.setText(R.string.freeonemonthtrial)
                fragBinding.relay5.txtRevoolaDes.setText(R.string.challengesfriends)
                fragBinding.subscribe.visibility= View.VISIBLE
                fragBinding.txtFreeonemonth.visibility= View.VISIBLE
                fragBinding.txtOr.visibility= View.VISIBLE
                fragBinding.txtContinuewithfree.visibility= View.VISIBLE
                fragBinding.txtTryfreeonemonth.setText(R.string.tryfreeonemonth)
                fragBinding.btnSubscribe.setText("$packagePrice ${ getString(R.string.permonthcost) }")
                fragBinding.inlayPremiumUser.txtRevoolaUser.setText(R.string.freerevoolauser)
                fragBinding.btnSubscribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.AppMainColor)
                fragBinding.txtDollarsign.setTextColor(ContextCompat.getColor(requireContext(), R.color.AppMainColor))
            }
            RLSubscribePageDesign.PremiumAccount -> {
                // Setup for Premium Account
                fragBinding.subscribe.visibility= View.GONE
                fragBinding.inlayPremiumUser.txtRevoolaUser.setText(R.string.premium_user)
            }
            RLSubscribePageDesign.OnTrial -> {
                // Setup for On Trial
                fragBinding.relay5.txtRevoolaDes.setText(R.string.challengesfriends2)
                fragBinding.subscribe.visibility= View.VISIBLE
                fragBinding.txtFreeonemonth.setText("")
                fragBinding.txtOr.visibility= View.GONE
                fragBinding.txtContinuewithfree.visibility= View.GONE
                fragBinding.txtTryfreeonemonth.setText(R.string.per_month_cancel_at_any_time)
                fragBinding.btnSubscribe.setText(R.string.subscribe)
                fragBinding.inlayPremiumUser.txtRevoolaUser.setText(R.string.trial_user)
                fragBinding.btnSubscribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.AppZone4Color)
                fragBinding.txtDollarsign.setTextColor(ContextCompat.getColor(requireContext(), R.color.AppZone4Color))
            }
            else -> {
                // Default setup
                fragBinding.relay5.txtRevoolaDes.setText(R.string.challengesfriends)
                fragBinding.txtFreeonemonth.setText(R.string.freeonemonthtrial)
                fragBinding.subscribe.visibility= View.VISIBLE
                fragBinding.txtFreeonemonth.visibility= View.VISIBLE
                fragBinding.txtOr.visibility= View.VISIBLE
                fragBinding.txtContinuewithfree.visibility= View.VISIBLE
                fragBinding.txtTryfreeonemonth.setText(R.string.tryfreeonemonth)
                fragBinding.btnSubscribe.setText("$packagePrice ${ getString(R.string.permonthcost) }")
                fragBinding.inlayPremiumUser.txtRevoolaUser.setText(R.string.freerevoolauser)
                fragBinding.btnSubscribe.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.AppMainColor)
                fragBinding.txtDollarsign.setTextColor(ContextCompat.getColor(requireContext(), R.color.AppMainColor))
            }
        }
    }
}