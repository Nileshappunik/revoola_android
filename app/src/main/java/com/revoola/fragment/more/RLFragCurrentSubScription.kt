package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.revenuecat.purchases.CustomerInfo
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.utils.RLPrefManager
import com.revoola.commonobject.RLTools
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.logInWith
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.models.SubscriptionOption
import com.revenuecat.purchases.purchaseWith
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.RlFragCurrentSubscriptionBinding
import com.revoola.fragment.more.adapter.PaywallItem
import com.revoola.fragment.more.adapter.RLPaywallAdapter

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

        Purchases.sharedInstance.logInWith(
            appUserID = RLAuthManager().rl_getCurrentUser()?.uid ?: ""
        ) { customerInfo: CustomerInfo, created: Boolean ->
            RLTools.rl_logEPrint(TAG, "✅ Login successful!")
            RLTools.rl_logEPrint(TAG, "Customer Info: $customerInfo")
            RLTools.rl_logEPrint(TAG, "New customer created: $created")
            RLTools.rl_logEPrint(TAG, "Active subscriptions: ${customerInfo.activeSubscriptions}")
        }

        //Load offerings when the paywall is displayed
        fetchOfferings(adapter)
    }

    private fun fetchOfferings(adapter: RLPaywallAdapter) {
        Purchases.sharedInstance.getOfferingsWith { offerings: Offerings ->
            val gson = Gson()
            val offeringsJson = gson.toJson(offerings.current)
            val list = parseOffering(offeringsJson)
            RLTools.rl_logLarge(TAG,"Offering:  ${gson.toJson(list)}")
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
   private fun parseOffering(jsonResponse: String): List<SubscriptionOffer> {
        return try {
            val gson = Gson()
            val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)

            val availablePackages = jsonObject.getAsJsonArray("availablePackages")
            val offers = mutableListOf<SubscriptionOffer>()

            availablePackages?.forEach { packageElement ->
                val packageObj = packageElement.asJsonObject
                val product = packageObj.getAsJsonObject("product")
                val subscriptionOptions = product.getAsJsonArray("subscriptionOptions")

                subscriptionOptions?.forEach { optionElement ->
                    val option = optionElement.asJsonObject
                    val offerId = option.get("offerId")?.asString ?: "regular"
                    val pricingPhases = option.getAsJsonArray("pricingPhases")

                    val phases = mutableListOf<PricingPhase>()
                    pricingPhases?.forEach { phaseElement ->
                        val phase = phaseElement.asJsonObject
                        phases.add(
                            PricingPhase(
                                formattedPrice = phase.getAsJsonObject("price").get("formatted").asString,
                                billingPeriod = phase.getAsJsonObject("billingPeriod").get("iso8601").asString,
                                billingCycleCount = phase.get("billingCycleCount").asInt,
                                recurrenceMode = phase.get("recurrenceMode").asString
                            )
                        )
                    }

                    val tags = option.getAsJsonArray("tags")?.map { it.asString } ?: emptyList()

                    offers.add(
                        SubscriptionOffer(
                            offerId = offerId,
                            title = "Revoola Subscription",
                            description = "Enterprise subscription",
                            phases = phases,
                            tags = tags
                        )
                    )
                }
            }

            // Sort offers by savings (best deals first)
            offers.sortedByDescending { offer ->
                when {
                    offer.offerId.contains("90-per-6-months") -> 90
                    offer.offerId.contains("85-per-6-months") -> 85
                    offer.offerId.contains("50-per-12-months") -> 50
                    offer.offerId.contains("50-per-6-months") -> 50
                    offer.offerId.contains("25-per-12-months") -> 25
                    else -> 0
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

}

//Data Classes for parsing the subscription response
data class SubscriptionOffer(
    val offerId: String,
    val title: String,
    val description: String,
    val phases: List<PricingPhase>,
    val tags: List<String> = emptyList()
) {
    val isPromotional: Boolean get() = phases.size > 1
    val finalPrice: String get() = phases.lastOrNull()?.formattedPrice ?: ""
    val introductoryPrice: String? get() = if (isPromotional) phases.firstOrNull()?.formattedPrice else null
    val introductoryDuration: String? get() = if (isPromotional) phases.firstOrNull()?.durationText else null

    val displayTitle: String get() = when {
        offerId.contains("90-per-6-months") -> "90% OFF - 6 Months"
        offerId.contains("85-per-6-months") -> "85% OFF - 6 Months"
        offerId.contains("50-per-12-months") -> "50% OFF - 12 Months"
        offerId.contains("50-per-6-months") -> "50% OFF - 6 Months"
        offerId.contains("25-per-12-months") -> "25% OFF - 12 Months"
        else -> "Regular Plan"
    }

    val savings: String? get() = when {
        offerId.contains("90-per-6-months") -> "Save 90%"
        offerId.contains("85-per-6-months") -> "Save 85%"
        offerId.contains("50-per-12-months") -> "Save 50%"
        offerId.contains("50-per-6-months") -> "Save 50%"
        offerId.contains("25-per-12-months") -> "Save 25%"
        else -> null
    }
}

data class PricingPhase(
    val formattedPrice: String,
    val billingPeriod: String,
    val billingCycleCount: Int,
    val recurrenceMode: String
) {
    val durationText: String get() = when {
        billingCycleCount == 0 -> "Ongoing"
        billingCycleCount == 1 -> "1 month"
        else -> "$billingCycleCount months"
    }

    val isIntroductory: Boolean get() = recurrenceMode == "FINITE_RECURRING"
}
