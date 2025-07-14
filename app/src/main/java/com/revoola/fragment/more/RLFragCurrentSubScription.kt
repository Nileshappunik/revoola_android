package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.SubscriptionOption
import com.revenuecat.purchases.purchaseWith
import com.revoola.databinding.RlFragCurrentSubscriptionBinding
import com.revoola.fragment.more.adapter.PaywallItem
import com.revoola.fragment.more.adapter.RLPaywallAdapter

class RLFragCurrentSubScription : RLBaseFragment() {
    val TAG: String = RLFragCurrentSubScription::class.java.simpleName
    //lateinit var fragBinding: RlFragCurrentSubscriptionBinding

    private val fragBinding by lazy {
        RlFragCurrentSubscriptionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_current_subscription, container) as RlFragCurrentSubscriptionBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragCurrentSubScription" )

        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)

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

        rl_revenueCatSetUp()
    }

    private fun rl_revenueCatSetUp() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        fragBinding.paywallList.layoutManager = linearLayoutManager
        val adapter = RLPaywallAdapter(null,didChoosePaywallItem = { item: PaywallItem ->
            when (item) {
                is PaywallItem.Product -> {
                    purchaseProduct(item.storeProduct)
                }
                is PaywallItem.Option -> {
                    purchaseOption(item.subscriptionOption)
                }
                is PaywallItem.Title -> {
                    // Do nothing
                }
            }
        })

        fragBinding.paywallList.adapter = adapter
        //Load offerings when the paywall is displayed
        fetchOfferings(adapter)
    }

    private fun fetchOfferings(adapter: RLPaywallAdapter) {
        Purchases.sharedInstance.getOfferingsWith { offerings: Offerings ->
            val gson = Gson()
            val offeringsJson = gson.toJson(offerings.current)
            RLTools.rl_logEPrint(TAG,offeringsJson)
            adapter.offering = offerings.current
            adapter.notifyDataSetChanged()
        }
    }

    private fun purchaseProduct(item: StoreProduct) {
        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(requireActivity(), item).build(),
            onError = { error, userCancelled ->
                if (!userCancelled) {
                    //buildError(context, error.message)
                    RLTools.rl_logEPrint(TAG,"purchaseProductError:- ${ error.message}")
                }
            },
            onSuccess = { _, _ ->
                activity?.finish()
            },
        )
    }

    private fun purchaseOption(item: SubscriptionOption) {
        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(requireActivity(), item).build(),
            onError = { error, userCancelled ->
                if (!userCancelled) {
                   // buildError(context, error.message)
                    RLTools.rl_logEPrint(TAG,"purchaseOptionError:- ${ error.message}")
                }
            },
            onSuccess = { _, _ ->
                activity?.finish()
            },
        )
    }

}