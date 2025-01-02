package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragAccountBinding
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools

import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.models.Period
import com.revenuecat.purchases.models.Price
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.SubscriptionOption
import com.revenuecat.purchases.purchaseWith
import com.revoola.fragment.more.adapter.PaywallAdapter
import com.revoola.fragment.more.adapter.PaywallItem

class RLFragAccount : RLBaseFragment() {
    val TAG: String = RLFragAccount::class.java.simpleName
    lateinit var fragBinding: RlFragAccountBinding

    private val binding by lazy {
        RlFragAccountBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_account, container) as RlFragAccountBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragAccount" )

        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)

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

        // ScxRevenueCatSetUp()
    }

    private fun ScxRevenueCatSetUp() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
         fragBinding.paywallList.layoutManager = linearLayoutManager
        val adapter = PaywallAdapter(null, didChoosePaywallItem = { item: PaywallItem ->
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

        /*
        Load offerings when the paywall is displayed
         */
        fetchOfferings(adapter)
    }

    private fun fetchOfferings(adapter: PaywallAdapter) {
        Purchases.sharedInstance.getOfferingsWith { offerings: Offerings ->
            val gson = Gson()
            val offeringsJson = gson.toJson(offerings.current)
           RLTools.RlLogEPrint(TAG,"fetchOfferings:- ${offeringsJson}")
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
                    RLTools.RlLogEPrint(TAG,"purchaseProductError:- ${ error.message}")
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
                    RLTools.RlLogEPrint(TAG,"purchaseOptionError:- ${ error.message}")
                }
            },
            onSuccess = { _, _ ->
                activity?.finish()
            },
        )
    }

}