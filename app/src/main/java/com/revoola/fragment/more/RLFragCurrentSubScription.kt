package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
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

class RLFragCurrentSubScription : RLBaseFragment() {
    private val TAG: String = RLFragCurrentSubScription::class.java.simpleName

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

        // Fetch user settings (appUnit)
        rl_firebaseToFetchUserData { userData ->
            if (userData != null) {
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

        // check all ready subscribed or not
        RelRevenueCatManager.checkIsSubscribed{
            if(it){
               // fragBinding.txtContinuewithfree.visibility=View.VISIBLE
            }else{
             //   fragBinding.txtContinuewithfree.visibility=View.GONE
            }
        }
        getAvailableSubscriptions()
    }

    fun getAvailableSubscriptions() {
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
        val packagePrice = packageToPurchase.product.price.formatted
        val subscriptionName = RelRevenueCatManager.getSubscriptionName(packageToPurchase.packageType.name)
        val descriptionString="By tapping subscribe/upgrade your payment will be charged to your iTunes account, and your account will be charged each $subscriptionName at the price of $packagePrice until you cancel it in settings in the iTunes Store at least 24 hours prior to the end of the current period."
        fragBinding.txtDollarsign.setText(packagePrice)
        fragBinding.btnSubscribe.setText("$packagePrice ${ getString(R.string.permonthcost) }")
        fragBinding.txtBytappingsubscribeyourpayment.setText(descriptionString)

        fragBinding.btnSubscribe.setOnClickListener {
            if(isAdded) RLBaseProgress.rl_showProgressDialog(requireActivity())
            RelRevenueCatManager.subscribe(
                activity = requireActivity(),
                packageToPurchase= packageToPurchase,
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


}


